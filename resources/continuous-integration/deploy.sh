#!/bin/bash
set -ex
# JOB_NAME environment variable must be set. We count on Hudson for this.

controlScript=$WORKSPACE/resources/continuous-integration/deploy/jetty7x/control.sh
lastStableWAR=$WORKSPACE/../lastStable/org.mifos\$mifos-war/archive/org.mifos/mifos-war/*/*.war
deployRoot=$HOME/deploys/mifos-$JOB_NAME-deploy
targetWARlocation=$deployRoot/jetty7x/webapps/mifos.war
dbProperties=$WORKSPACE/db/target/release/db/mifos-db.properties
dbPropertiesTemplate=$WORKSPACE/db/target/release/db/mifos-db-template.properties
expandScript=$WORKSPACE/db/target/release/db/bin/expand_db.sh
contractScript=$WORKSPACE/db/target/release/db/bin/contract_db.sh
undoExpandScript=$WORKSPACE/db/target/release/db/bin/undo_expand_db.sh
undoContractScript=$WORKSPACE/db/target/release/db/bin/undo_contract_db.sh
liquibaseScript=$WORKSPACE/db/target/release/db/bin/liquibase.sh

chmod +x $controlScript
chmod +x $expandScript
chmod +x $contractScript
chmod +x $liquibaseScript

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
    sed "s/@USERNAME@/$USERNAME/g;
         s/@PASSWORD@/$PASSWORD/g;
         s/@SCHEMA_NAME@/$SCHEMA_NAME/g;
         s/@DB_HOST@/$DB_HOST/g;
         s/@DB_PORT@/$DB_PORT/g" $dbPropertiesTemplate > $dbProperties
}

function deployMifos {
    rm -f $deployRoot/jetty7x/logs/*
    rm -rf $deployRoot/jetty7x/webapps/mifos
    cp $lastStableWAR $targetWARlocation
}

function startJetty {
    $controlScript start
}

function stopJetty {
    $controlScript stop
}

function doExpansion {
    $expandScript
}

function doContraction {
    $contractScript
}


function doRollbackExpansion {
    $undoExpandScript 1970-07-07T00:00:00
}

function doRollbackContraction {
    $undoContractScript 1970-07-07T00:00:00
}

# Test the previous version of application against new db
stopJetty
updateDbProperties
doRollbackContraction
doRollbackExpansion
doExpansion
startJetty
cUrl

# Test new version of application against new db
stopJetty
deployMifos
startJetty
cUrl

# Test new version of application after contraction
stopJetty
updateDbProperties
doContraction
startJetty
cUrl
