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

download_build_chain()
{
  local tag="${1}"
  if [ -z "${tag}" ]; then
    tag=$(curl -sS "https://gitee.com/api/v5/repos/FISCO-BCOS/FISCO-BCOS/tags" | grep -oe "\"name\":\"v[2-9]*\.[0-9]*\.[0-9]*\"" | cut -d \" -f 4 | sort -V | tail -n 1)
  fi
  LOG_INFO "--- current tag: $tag"
  curl -LO "https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/${tag}/build_chain.sh" && chmod u+x build_chain.sh
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
  cp -r src/test/resources/amop conf/amop
  cp -r src/test/resources/amop src/integration-test/resources/amop
  rm -rf src/integration-test/resources/abi
  rm -rf src/integration-test/resources/bin
  cp -r src/test/resources/ecdsa/abi src/integration-test/resources/abi
  cp -r src/test/resources/ecdsa/bin src/integration-test/resources/bin
  mkdir -p sdk-amop/src/test/resources
  cp -r src/test/resources/ sdk-amop/src/test/resources

  sed_cmd=$(get_sed_cmd)

  local node_type="${1}"
  if [ "${node_type}" == "sm" ];then
    rm -rf src/integration-test/resources/abi
    rm -rf src/integration-test/resources/bin
    cp -r src/test/resources/gm/abi src/integration-test/resources/abi
    cp -r src/test/resources/gm/bin src/integration-test/resources/bin
    ${sed_cmd} 's/useSMCrypto = "false"/useSMCrypto = "true"/g' src/integration-test/resources/config.toml
  fi
}

build_node()
{
  local node_type="${1}"
  if [ "${node_type}" == "sm" ];then
      bash build_chain.sh -l 127.0.0.1:4 -s
  else
      bash build_chain.sh -l 127.0.0.1:4
  fi
  ./nodes/127.0.0.1/fisco-bcos -v
  bash nodes/127.0.0.1/start_all.sh
}

clean_node()
{
  bash nodes/127.0.0.1/stop_all.sh
  rm -rf nodes
}

 # check integration-test for non-gm node
check_standard_node()
{
  build_node
  prepare_environment
  ## run integration test
  bash gradlew clean integrationTest --info
  ## clean
  clean_node
}

check_sm_node()
{
  build_node "sm"
  prepare_environment "sm"
  ## run integration test
  bash gradlew clean integrationTest --info
  ## clean
  clean_node
}

ls -la
export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8
LOG_INFO "------ download_build_chain---------"
# set tag v3.0.0-rc1, update when new rc, remove when release final
download_build_chain "v3.0.0-rc1"
LOG_INFO "------ check_standard_node---------"
check_standard_node
LOG_INFO "------ check_sm_node---------"
check_sm_node
LOG_INFO "------ check_basic---------"
check_basic
#LOG_INFO "------ check_log---------"
#cat log/* |grep -i error
#cat log/* |grep -i warn
