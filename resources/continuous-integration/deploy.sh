#!/bin/sh
set -ex

# JOB_NAME environment variable must be set. We count on Hudson for this.

controlScript=$WORKSPACE/resources/continuous-integration/deploy/tomcat/control.sh
lastStableWAR=$WORKSPACE/../lastStable/org.mifos\$mifos-webapp/archive/org.mifos/mifos-webapp/*/*.war
deployRoot=$HOME/deploys/mifos-$JOB_NAME-deploy
targetWARlocation=$deployRoot/tomcat6/webapps/mifos.war

$controlScript stop
rm -f $deployRoot/tomcat6/logs/*
rm -rf $deployRoot/tomcat6/webapps/mifos
rm -rf $deployRoot/tomcat6/work
cp $lastStableWAR $targetWARlocation
$controlScript start

# If TEST_SERVER_PORT is set by Hudson, we can test if the deployed test server
# is online. Using parameterized builds the the preferred means for setting
# TEST_SERVER_PORT.
if [ -n "$TEST_SERVER_PORT" ]; then
    can_hit_test_server=1
    while [ $can_hit_test_server -ne 0 ]
    do
        # The purpose of this sleep is twofold:
        # 1) on the first iteration, give tomcat a second to start up.
        # 2) thereafter, pause between loop iterations so we don't exhaust
        # resources on birch if some unforseen problem occurs.
        sleep 1
        set +e # or a failure would stop the script prematurely
        curl --fail http://ci.mifos.org:$TEST_SERVER_PORT/mifos/
        can_hit_test_server=$?
    done
fi
