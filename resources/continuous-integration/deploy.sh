#!/bin/bash
set -ex
# JOB_NAME environment variable must be set. We count on Hudson for this.

controlScript=$WORKSPACE/resources/continuous-integration/deploy/tomcat/control.sh
lastStableWAR=$WORKSPACE/../lastStable/org.mifos\$mifos-webapp/archive/org.mifos/mifos-webapp/*/*.war
deployRoot=$HOME/deploys/mifos-$JOB_NAME-deploy
targetWARlocation=$deployRoot/tomcat6/webapps/mifos.war
dbProperties=$WORKSPACE/db/target/release/db/mifos-db.properties
expandScript=$WORKSPACE/db/target/release/db/bin/expand_db.sh
contractScript=$WORKSPACE/db/target/release/db/bin/contract_db.sh

function cUrl {
    # If TEST_SERVER_PORT is set by Hudson, we can test if the deployed test server
    # is online. Using parameterized builds the the preferred means for setting
    # TEST_SERVER_PORT.
    if [[ -n "$TEST_SERVER_PORT" ]]; then
        can_hit_test_server=1
        while [[ $can_hit_test_server -ne 0 ]]
        do
            # The purpose of this sleep is twofold:
            # 1) on the first iteration, give tomcat a second to start up.
            # 2) thereafter, pause between loop iterations so we don't exhaust
            # resources on birch if some unforseen problem occurs.
            sleep 1
            set +e # or a failure would stop the script prematurely
            curl --fail --location http://ci.mifos.org:$TEST_SERVER_PORT/mifos/
            can_hit_test_server=$?
        done
    fi
}

function updateDbProperties {
    sed 's/@USERNAME@/$USERNAME/g;
         s/@PASSWORD@/$PASSWORD/g;
         s/@SCHEMA_NAME@/$SCHEMA_NAME/g;
         s/@DB_HOST@/$DB_HOST/g;
         s/@DB_PORT@/$DB_PORT/g' $dbProperties > $dbProperties.tmp
    mv $dbProperties.tmp $dbProperties
}

function deployMifos {
    rm -f $deployRoot/tomcat6/logs/*
    rm -rf $deployRoot/tomcat6/webapps/mifos
    rm -rf $deployRoot/tomcat6/work
    cp $lastStableWAR $targetWARlocation
}

function startTomcat {
    $controlScript start
}

function stopTomcat {
    $controlScript stop
}

function doExpansion {
    $expandScript
}

function doContraction {
    $contractScript
}

# Test the previous version of application against new db
stopTomcat
updateDbProperties
doExpansion
startTomcat
cUrl

# Test new version of application against new db
stopTomcat
deploy
startTomcat
cUrl

# Test new version of application after contraction
stopTomcat
updateDbProperties
doContraction
startTomcat
cUrl