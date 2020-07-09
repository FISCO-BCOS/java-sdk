#!/bin/bash

set -e
bash gradlew verifyGoogleJavaFormat
bash gradlew build

