#!/bin/bash

set -e
LOG_INFO() {
    local content=${1}
    echo -e "\033[32m ${content}\033[0m"
}

download_tassl()
{
local OPENSSL_CMD=${HOME}/.fisco/tassl-1.1.1b
if [ -f "${OPENSSL_CMD}" ];then
    return
fi
local package_name="tassl-1.1.1b-linux-x86_64"
if [ "$(uname)" == "Darwin" ];then
    package_name="tassl-1.1.1b-macOS-x86_64"
fi
curl -LO "https://github.com/FISCO-BCOS/LargeFiles/raw/master/tools/${package_name}.tar.gz" && tar -zxvf "${package_name}.tar.gz" && mv "${package_name}" tassl-1.1.1b && mkdir -p ~/.fisco && mv tassl-1.1.1b ~/.fisco/
}

get_sed_cmd()
{
  local sed_cmd="sed -i"
  if [ "$(uname)" == "Darwin" ];then
        sed_cmd="sed -i .bkp"
  fi
  echo "$sed_cmd"
}

get_latest_version()
{
  # The GitHub /releases/latest redirect is authoritative for a release that
  # actually has downloadable binary assets (the newest git tag may not have
  # a published release yet). Fall back to the gitee tag list if unreachable.
  local tag
  tag=$(curl -fsSI "https://github.com/FISCO-BCOS/FISCO-BCOS/releases/latest" | grep -i "^location:" | grep -oe "v[0-9]*\.[0-9]*\.[0-9]*" | head -n 1)
  if [ -z "${tag}" ]; then
    tag=$(curl -sS "https://gitee.com/api/v5/repos/FISCO-BCOS/FISCO-BCOS/tags" | grep -oe "\"name\":\"v[2-9]*\.[0-9]*\.[0-9]*\"" | cut -d \" -f 4 | sort -V | tail -n 1)
  fi
  echo "${tag}"
}

download_build_chain()
{
  local tag="${1}"
  LOG_INFO "--- download build_chain.sh: ${tag} ---"
  # older releases attach build_chain.sh as a release asset; newer releases
  # only keep it in the source tree, so fall back to the raw file at that tag
  curl -fsSL -o "build_chain-${tag}.sh" "https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/${tag}/build_chain.sh" \
    || curl -fsSL -o "build_chain-${tag}.sh" "https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/${tag}/tools/BcosAirBuilder/build_chain.sh"
  chmod u+x "build_chain-${tag}.sh"
}

download_binary()
{
  local tag="${1}"
  LOG_INFO "--- download fisco-bcos binary: ${tag} ---"
  local package_name="fisco-bcos-linux-x86_64.tar.gz"
  if [ "$(uname)" == "Darwin" ];then
      package_name="fisco-bcos-macOS-x86_64.tar.gz"
  fi
  curl -fsSLO "https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/${tag}/${package_name}"
  # keep the binary named exactly 'fisco-bcos' (the generated start.sh expects
  # that name), one sub-directory per version
  mkdir -p "bins/${tag}"
  tar -zxf "${package_name}" -C "bins/${tag}" && rm -f "${package_name}"
  "./bins/${tag}/fisco-bcos" -v
}

# build one 4-node air chain with a certificate-free (ssl disabled) rpc
# endpoint, then start it
build_chain_one()
{
  local tag="${1}"
  local outdir="${2}"
  local ports="${3}"     # "p2p_start_port,rpc_start_port"
  local extra="${4}"     # e.g. "-s" for a sm-crypto chain
  LOG_INFO "--- build chain: version=${tag} outdir=${outdir} ports=${ports} ${extra} ---"
  bash "build_chain-${tag}.sh" -l 127.0.0.1:4 -e "./bins/${tag}/fisco-bcos" -o "${outdir}" -p "${ports}" ${extra}
  # turn off ssl on the rpc endpoint; the config key differs between versions:
  #   <= 3.7.x ships a commented ';disable_ssl=true' in [rpc]
  #   >= 3.16.x ships an explicit 'enable_ssl=true' in [rpc]
  # ([p2p] uses a distinct 'enable_ssl_verify' key, so the sed cannot touch it)
  local sed_cmd=$(get_sed_cmd)
  local cfg
  for cfg in "${outdir}"/127.0.0.1/node*/config.ini; do
    ${sed_cmd} "s/;disable_ssl=true/disable_ssl=true/" "${cfg}"
    ${sed_cmd} "s/enable_ssl=true/enable_ssl=false/" "${cfg}"
  done
  cat "${outdir}/127.0.0.1/node0/config.genesis"
  bash "${outdir}/127.0.0.1/start_all.sh"
}

wait_rpc_ready()
{
  local port="${1}"
  local i
  for i in $(seq 1 30); do
    if curl -s -m 3 --noproxy "*" -o /dev/null "http://127.0.0.1:${port}"; then
      LOG_INFO "--- rpc 127.0.0.1:${port} is ready ---"
      return 0
    fi
    sleep 2
  done
  echo "rpc 127.0.0.1:${port} not ready after 60s"
  return 1
}

# render the sdk configs for one round: no certificates (ssl disabled),
# pointed at the given chain's rpc ports
prepare_sdk_config()
{
  local rpc_port="${1}"
  local use_sm="${2}"   # "true" / "false"
  local sed_cmd=$(get_sed_cmd)

  mkdir -p src/integration-test/resources/ conf
  cp src/test/resources/clog.ini conf/
  cp src/test/resources/config-example.toml src/test/resources/config.toml
  cp src/test/resources/config-example.toml src/integration-test/resources/config.toml
  cp src/test/resources/log4j2.properties src/integration-test/resources/

  rm -rf src/integration-test/resources/abi src/integration-test/resources/bin
  if [ "${use_sm}" == "true" ];then
    cp -r src/test/resources/gm/abi src/integration-test/resources/abi
    cp -r src/test/resources/gm/bin src/integration-test/resources/bin
  else
    cp -r src/test/resources/ecdsa/abi src/integration-test/resources/abi
    cp -r src/test/resources/ecdsa/bin src/integration-test/resources/bin
  fi

  ${sed_cmd} "s/enableSsl = \"true\"/enableSsl = \"false\"/" ./src/integration-test/resources/config.toml
  ${sed_cmd} "s/useSMCrypto = \"false\"/useSMCrypto = \"${use_sm}\"/" ./src/integration-test/resources/config.toml
  ${sed_cmd} "s/127.0.0.1:20201/127.0.0.1:${rpc_port}/g" ./src/integration-test/resources/config.toml

  # amop test configs: restore from a pristine template each round, then point
  # them at this round's chain; certPath is replaced by enableSsl=false since
  # no certificates are needed
  local f
  for f in config-publisher-for-test.toml config-subscriber-for-test.toml; do
    local p="src/integration-test/resources/amop/${f}"
    if [ ! -f "${p}.tpl" ];then
      cp "${p}" "${p}.tpl"
    fi
    cp "${p}.tpl" "${p}"
    ${sed_cmd} "s/certPath = \"conf\"/enableSsl = \"false\"/" "${p}"
    ${sed_cmd} "s/useSMCrypto = \"false\"/useSMCrypto = \"${use_sm}\"/" "${p}"
    ${sed_cmd} "s/127.0.0.1:20200/127.0.0.1:${rpc_port}/g" "${p}"
    ${sed_cmd} "s/127.0.0.1:20201/127.0.0.1:$((rpc_port + 1))/g" "${p}"
  done
}

# informational only: a burst of PBFT view-change timeouts right after start
# is normal; a persistently growing count means the chain never reached
# consensus and the round against it will fail
report_chain_health()
{
  local outdir="${1}"
  local cnt
  cnt=$(grep -h "After onTimeout" "${outdir}"/127.0.0.1/node0/log/*.log 2>/dev/null | wc -l | tr -d ' ')
  LOG_INFO "--- ${outdir}: node0 consensus view-change timeouts so far: ${cnt:-0} ---"
}

run_integration_round()
{
  local name="${1}"
  local rpc_port="${2}"
  local use_sm="${3}"
  LOG_INFO "------ integration round: ${name} (rpc ${rpc_port}, sm=${use_sm}) ------"
  wait_rpc_ready "${rpc_port}"
  prepare_sdk_config "${rpc_port}" "${use_sm}"
  bash gradlew clean integrationTest --info
  # if hs_err log exist, print it
  (cat hs_err_pid*.log) || true
}

LOG_INFO "------ check java version ---------"
java -version

pwd
ls -la
export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8
download_tassl

if [ ! -f "get_account.sh" ];then
  curl -LO https://raw.githubusercontent.com/FISCO-BCOS/console/master/tools/get_account.sh
fi
if [ ! -f "get_gm_account.sh" ];then
  curl -LO https://raw.githubusercontent.com/FISCO-BCOS/console/master/tools/get_gm_account.sh
fi

PINNED_VERSION="v3.7.3"
LATEST_VERSION=$(get_latest_version)
LOG_INFO "------ node versions: ${PINNED_VERSION} (pinned) + ${LATEST_VERSION} (latest) ---------"

download_build_chain "${PINNED_VERSION}"
download_binary "${PINNED_VERSION}"
if [ "${LATEST_VERSION}" != "${PINNED_VERSION}" ];then
  download_build_chain "${LATEST_VERSION}"
  download_binary "${LATEST_VERSION}"
fi

# three chains, started together on disjoint ports, all with certificate-free rpc
build_chain_one "${PINNED_VERSION}" "nodes_pinned"    "30300,20200"
build_chain_one "${LATEST_VERSION}" "nodes_latest"    "30310,20210"
build_chain_one "${LATEST_VERSION}" "nodes_latest_sm" "30320,20220" "-s"

wait_rpc_ready 20200
wait_rpc_ready 20210
wait_rpc_ready 20220

run_integration_round "ecdsa @ ${PINNED_VERSION}" 20200 "false"
report_chain_health "nodes_latest"
run_integration_round "ecdsa @ ${LATEST_VERSION}" 20210 "false"
report_chain_health "nodes_latest_sm"
run_integration_round "sm @ ${LATEST_VERSION}"    20220 "true"

# best-effort teardown
bash nodes_pinned/127.0.0.1/stop_all.sh || true
bash nodes_latest/127.0.0.1/stop_all.sh || true
bash nodes_latest_sm/127.0.0.1/stop_all.sh || true
