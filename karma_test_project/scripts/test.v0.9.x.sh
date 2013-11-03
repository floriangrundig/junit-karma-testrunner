#!/bin/bash

BASE_DIR=`dirname $0`

export PATH=$PATH:/usr/local/bin/
echo "Using Path: $PATH"
echo ""
echo "Starting Karma Server (http://karma-runner.github.io)"
echo "-------------------------------------------------------------------"

karma_test_project/node_modules/karma/bin/karma start $*
#karma start $BASE_DIR/../config/karma.conf.v0.9.x.js $*
