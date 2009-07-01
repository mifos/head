#!/bin/bash

set -x
set -o errexit

# used by redeploy_war-pom.xml and rename_latest_war.sh
export WAR_ARCHIVE=/var/www/gazelle/war/trunk

cd trunk

# clean up because we're using update rather than a clean checkout
svn status | grep ^? | awk '{print $2}' | xargs --no-run-if-empty rm
cd mifos/src/main/resources/org/mifos/config/localizedResources
svn revert --recursive .

# generate localized strings from .po files
./create_props.sh

# back to "trunk"
cd $OLDPWD

mvn clean install -Pcontinuous-integration,tomcat6x,download-container

# copy the WAR to where the test server can automatically fetch it
mvn -f resources/continuous-integration/redeploy_war-pom.xml dependency:copy
resources/continuous-integration/rename_latest_war.sh
