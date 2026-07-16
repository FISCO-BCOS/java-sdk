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
    if curl -s -m 3 --noproxy "*" -H "Content-Type: application/json" \
         -d '{"jsonrpc":"2.0","method":"getBlockNumber","params":["group0",""],"id":1}' \
         "http://127.0.0.1:${port}" | grep -q "jsonrpc"; then
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
  # single peer on purpose: the round death we chased with a "failover" second peer was
  # never a single-node RPC wedge — it was one poison transaction (non-hex `to`) halting
  # the whole chain on 3.16.x, now fixed test-side. A second peer does not help a
  # whole-chain stall and it makes BcosSDKTest.testClient flaky: getBlockByNumber and
  # getBlockByHash can then land on different nodes whose node-local block fields
  # (importTime, signatureList order) differ, breaking its block-equality assertion
  ${sed_cmd} "s/peers=\[.*\]/peers=[\"127.0.0.1:${rpc_port}\"]/" ./src/integration-test/resources/config.toml

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

rpc_call()
{
  local port="${1}"
  local method="${2}"
  curl -s -m 5 --noproxy "*" -H "Content-Type: application/json" \
    -d "{\"jsonrpc\":\"2.0\",\"method\":\"${method}\",\"params\":[\"group0\",\"\"],\"id\":1}" \
    "http://127.0.0.1:${port}" 2>/dev/null
}

# only runs after a failed round: a "-4008 receipt timeout" alone cannot be
# attributed — this answers, per node, whether blocks still advance, whether
# txs are stuck in the txpool, what the committee looks like (a phantom
# bogus-id entry would show up in the sealer/observer lists), and what the
# node logs themselves report
dump_chain_diagnostics()
{
  local rpc_port="${1}"
  local outdir="${2}"
  local i port node
  LOG_INFO "--- diagnostics for ${outdir} (rpc base ${rpc_port}) ---"
  for i in 0 1 2 3; do
    port=$((rpc_port + i))
    echo "[node${i} :${port}] blockNumber(t0): $(rpc_call "${port}" getBlockNumber)"
    echo "[node${i} :${port}] pendingTxSize:   $(rpc_call "${port}" getPendingTxSize)"
  done
  sleep 5
  for i in 0 1 2 3; do
    port=$((rpc_port + i))
    echo "[node${i} :${port}] blockNumber(t+5s): $(rpc_call "${port}" getBlockNumber)"
  done
  echo "[node0] sealerList:      $(rpc_call "${rpc_port}" getSealerList | head -c 2000)"
  echo "[node0] observerList:    $(rpc_call "${rpc_port}" getObserverList | head -c 1000)"
  echo "[node0] consensusStatus: $(rpc_call "${rpc_port}" getConsensusStatus | head -c 3000)"
  for node in node0 node1 node2 node3; do
    echo "--- ${outdir}/${node}: last warning/error log lines ---"
    grep -hE "^(warning|error)\|" "${outdir}/127.0.0.1/${node}/log/"*.log 2>/dev/null | tail -20 || true
  done
}

# run one round; never aborts the script — a failed round is recorded in
# FAILED_ROUNDS so the remaining rounds still run and the job fails at the end
run_integration_round()
{
  local name="${1}"
  local rpc_port="${2}"
  local use_sm="${3}"
  local outdir="${4}"
  LOG_INFO "------ integration round: ${name} (rpc ${rpc_port}, sm=${use_sm}) ------"
  report_chain_health "${outdir}"
  local round_status=0
  if wait_rpc_ready "${rpc_port}"; then
    prepare_sdk_config "${rpc_port}" "${use_sm}"
    bash gradlew clean integrationTest --info || round_status=1
    # if hs_err log exist, print it
    (cat hs_err_pid*.log) || true
  else
    round_status=1
  fi
  report_chain_health "${outdir}"
  if [ "${round_status}" -ne 0 ]; then
    dump_chain_diagnostics "${rpc_port}" "${outdir}" || true
  fi
  # stop this chain as soon as its round is done: later rounds do not touch it,
  # and the runner (especially macOS, where the x86_64 nodes run under Rosetta)
  # cannot sustain all 12 nodes plus the JVM for the whole job — chain2 stalled
  # with execution timeouts mid-round when all three chains were kept running
  bash "${outdir}/127.0.0.1/stop_all.sh" || true
  if [ "${round_status}" -ne 0 ]; then
    FAILED_ROUNDS="${FAILED_ROUNDS} [${name}]"
  fi
  return 0
}

# map a round id -> (version, outdir, ports, rpc_port, sm, extra) and run it:
# download that one version, build its single 4-node cert-free chain, then run
# one integrationTest pass against it. Splitting the rounds this way lets CI run
# them as independent parallel jobs (one 4-node chain per runner) instead of one
# runner carrying all three chains (12 nodes) through three sequential rounds.
build_and_run_round()
{
  local round="${1}"
  local tag outdir ports rpc_port sm extra name
  case "${round}" in
    pinned-ecdsa) tag="${PINNED_VERSION}";     outdir="nodes_pinned";    ports="30300,20200"; rpc_port=20200; sm="false"; extra="";   ;;
    latest-ecdsa) tag="$(get_latest_version)"; outdir="nodes_latest";    ports="30310,20210"; rpc_port=20210; sm="false"; extra="";   ;;
    latest-sm)    tag="$(get_latest_version)"; outdir="nodes_latest_sm"; ports="30320,20220"; rpc_port=20220; sm="true";  extra="-s"; ;;
    *) echo "unknown round '${round}' (want: pinned-ecdsa | latest-ecdsa | latest-sm)"; exit 2 ;;
  esac
  if ! echo "${tag}" | grep -qE "^v[0-9]+\.[0-9]+\.[0-9]+$"; then
    echo "failed to resolve node version for round '${round}', got: '${tag}'"
    exit 1
  fi
  if [ "${sm}" = "true" ]; then name="sm @ ${tag}"; else name="ecdsa @ ${tag}"; fi
  download_build_chain "${tag}"
  download_binary "${tag}"
  build_chain_one "${tag}" "${outdir}" "${ports}" "${extra}"
  run_integration_round "${name}" "${rpc_port}" "${sm}" "${outdir}"
}

# round selector: a single round id (CI runs one per job, in parallel) or "all"
# (local / fallback: run all three sequentially in this one invocation)
ROUND="${1:-all}"

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
FAILED_ROUNDS=""

case "${ROUND}" in
  all)
    LOG_INFO "------ running all rounds sequentially (local/fallback mode) ------"
    build_and_run_round pinned-ecdsa
    build_and_run_round latest-ecdsa
    build_and_run_round latest-sm
    ;;
  pinned-ecdsa|latest-ecdsa|latest-sm)
    build_and_run_round "${ROUND}"
    ;;
  *)
    echo "usage: $(basename "$0") [all|pinned-ecdsa|latest-ecdsa|latest-sm]"
    exit 2
    ;;
esac

if [ -n "${FAILED_ROUNDS}" ]; then
  echo "integration rounds failed:${FAILED_ROUNDS}"
  exit 1
fi
LOG_INFO "------ all integration rounds passed ---------"
