#!/bin/bash

set -e
# check code format
bash gradlew verifyGoogleJavaFormat
bash gradlew build
