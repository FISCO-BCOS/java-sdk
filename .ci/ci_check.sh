#!/bin/bash

set -e
LOG_INFO() {
    local content=${1}
    echo -e "\033[32m ${content}\033[0m"
}
check_basic()
{
# check code format
# bash gradlew verifyGoogleJavaFormat
# build
bash gradlew build --info
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

download_build_chain()
{
  local tag="${1}"
  if [ -z "${tag}" ]; then
    tag=$(curl -sS "https://gitee.com/api/v5/repos/FISCO-BCOS/FISCO-BCOS/tags" | grep -oe "\"name\":\"v[2-9]*\.[0-9]*\.[0-9]*\"" | cut -d \" -f 4 | sort -V | tail -n 1)
  fi
  LOG_INFO "--- current tag: $tag"
  curl -LO "https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/${tag}/build_chain.sh" && chmod u+x build_chain.sh
}

download_binary()
{
  local tag="${1}"
  LOG_INFO "--- current tag: $tag"
  local package_name="fisco-bcos-linux-x86_64.tar.gz"
  if [ "$(uname)" == "Darwin" ];then
      package_name="fisco-bcos-macOS-x86_64.tar.gz"
  fi
  curl -LO "https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/${tag}/${package_name}" && tar -zxvf "${package_name}"
}

get_sed_cmd()
{
  local sed_cmd="sed -i"
  if [ "$(uname)" == "Darwin" ];then
        sed_cmd="sed -i .bkp"
  fi
  echo "$sed_cmd"
}

prepare_environment()
{
  ## prepare resources for integration test
  mkdir -p src/integration-test/resources/
  mkdir -p conf
  cp -r nodes/127.0.0.1/sdk/* conf
  cp src/test/resources/config-example.toml src/integration-test/resources/config.toml
  cp src/test/resources/clog.ini conf/
  cp src/test/resources/config-example.toml src/test/resources/config.toml
  cp src/test/resources/log4j2.properties src/integration-test/resources/
  rm -rf src/integration-test/resources/abi
  rm -rf src/integration-test/resources/bin
  cp -r src/test/resources/ecdsa/abi src/integration-test/resources/abi
  cp -r src/test/resources/ecdsa/bin src/integration-test/resources/bin

  sed_cmd=$(get_sed_cmd)
  local node_type="${1}"
  local use_sm="false"
  local not_use_sm="true"
  if [ "${node_type}" == "sm" ];then
    use_sm="true"
    not_use_sm="false"
    rm -rf src/integration-test/resources/abi
    rm -rf src/integration-test/resources/bin
    cp -r src/test/resources/gm/abi src/integration-test/resources/abi
    cp -r src/test/resources/gm/bin src/integration-test/resources/bin
  fi
  use_sm_str="useSMCrypto = \"${use_sm}\""
  ${sed_cmd} "s/useSMCrypto = \"${not_use_sm}\"/${use_sm_str}/g" ./src/integration-test/resources/config.toml
  ${sed_cmd} "s/useSMCrypto = \"${not_use_sm}\"/${use_sm_str}/g" ./src/integration-test/resources/amop/config-subscriber-for-test.toml
  ${sed_cmd} "s/useSMCrypto = \"${not_use_sm}\"/${use_sm_str}/g" ./src/integration-test/resources/amop/config-publisher-for-test.toml
}

prepare_wasm_environment()
{
  ## prepare resources for integration test
  mkdir -p src/integration-wasm-test/resources/
  mkdir -p conf
  cp -r nodes/127.0.0.1/sdk/* conf
  cp src/test/resources/config-example.toml src/integration-wasm-test/resources/config.toml
  cp src/test/resources/clog.ini conf/
  cp src/test/resources/config-example.toml src/test/resources/config.toml
  cp src/test/resources/log4j2.properties src/integration-wasm-test/resources/
}

build_node()
{
  local node_type="${1}"
  local sed_cmd=$(get_sed_cmd)
  if [ ! -f "get_account.sh" ];then
    curl -LO https://raw.githubusercontent.com/FISCO-BCOS/console/master/tools/get_account.sh
  fi
  if [ ! -f "get_gm_account.sh" ];then
    curl -LO https://raw.githubusercontent.com/FISCO-BCOS/console/master/tools/get_gm_account.sh
  fi
  if [ "${node_type}" == "wasm" ];then
      bash build_chain.sh -l 127.0.0.1:4 -e ./fisco-bcos -w ${2}
  else
      bash build_chain.sh -l 127.0.0.1:4 -e ./fisco-bcos ${2}
  fi
  ./nodes/127.0.0.1/fisco-bcos -v
  cat nodes/127.0.0.1/node0/config.genesis
  bash nodes/127.0.0.1/start_all.sh
}

clean_node()
{
  bash nodes/127.0.0.1/stop_all.sh
  rm -rf nodes
  if [ "${1}" == "true" ]; then
    rm -rf ./fisco-bcos*
  fi
}

 # check integration-test for non-gm node
check_standard_node()
{
  rm -rf build dist
  build_node "normal" "${3}"
  prepare_environment "${2}"
  ## run integration test
  bash gradlew clean integrationTest --info
  # if $? is not 0, then exit
  if [ ${?} -ne 0 ]; then
    cat log/*.log
  fi
  ## clean
  clean_node "${1}"
}

check_wasm_node()
{
  rm -rf build dist
  build_node "wasm" "${3}"
  prepare_wasm_environment
  ## run integration test
  bash gradlew clean integrationWasmTest --info
  ## clean
  clean_node "${1}"
}
LOG_INFO "------ check java version ---------"
java -version

pwd
ls -la
export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8
download_tassl
LOG_INFO "------ download_binary: v3.0.0---------"
download_build_chain "v3.0.0"
download_binary "v3.0.0"
LOG_INFO "------ check_standard_node---------"
check_standard_node
rm -rf ./bin

LOG_INFO "------ download_binary: v3.1.0---------"
download_build_chain "v3.1.0"
download_binary "v3.1.0"
LOG_INFO "------ check_standard_node---------"
check_standard_node "true" "normal" "-A"
rm -rf ./bin

LOG_INFO "------ download_binary: v3.2.3---------"
download_build_chain "v3.2.3"
download_binary "v3.2.3"
LOG_INFO "------ check_standard_node---------"
check_standard_node "false" "normal" "-A"
LOG_INFO "------ check_sm_node---------"
check_standard_node "true" "sm" "-s -A"
rm -rf ./bin

LOG_INFO "------ download_build_chain: v3.3.0---------"
download_binary "v3.3.0"
download_build_chain "v3.3.0"
LOG_INFO "------ check_standard_node---------"
check_standard_node "true"
rm -rf ./bin

LOG_INFO "------ download_build_chain: v3.4.0---------"
download_binary "v3.4.0"
download_build_chain "v3.4.0"
LOG_INFO "------ check_standard_node---------"
check_standard_node "true"
rm -rf ./bin

LOG_INFO "------ download_build_chain: v3.5.0---------"
download_binary "v3.5.0"
download_build_chain "v3.5.0"
LOG_INFO "------ check_wasm_node---------"
check_wasm_node "false"
LOG_INFO "------ check_standard_node---------"
check_standard_node "false" "normal"
LOG_INFO "------ check_standard_node---------"
check_standard_node "true" "sm" "-s"
rm -rf ./bin
