#!/bin/bash

set -e
# check code format
bash gradlew verifyGoogleJavaFormat
# build
bash gradlew build -x integrationTest

# check integration-test
## start up FISCO BCOS nodes.
curl -LO https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/master/tools/build_chain.sh && chmod u+x build_chain.sh
./build_chain.sh -l 127.0.0.1:4
./nodes/127.0.0.1/fisco-bcos -v
./nodes/127.0.0.1/start_all.sh
# ./build_chain.sh -l 127.0.0.1:4 -o nodes

## prepare resources for integration test
mkdir -p src/integration-test/resources/
cp -r nodes/127.0.0.1/sdk/* src/integration-test/resources/
cp src/main/resources/config-example.yaml src/integration-test/resources/config-example.yaml
cp src/test/resources/log4j.properties src/integration-test/resources/

## run integration test
bash gradlew integrationTest

## clean
bash nodes/127.0.0.1/stop_all.sh
bash nodes/127.0.0.1/stop_all.sh
bash nodes/127.0.0.1/stop_all.sh
rm -rf nodes