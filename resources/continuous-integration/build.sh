#!/bin/bash

set -x
set -o errexit

cd working_copy

# clean up because we're using update rather than a clean checkout
svn status | grep ^? | awk '{print $2}' | xargs --no-run-if-empty rm
cd application/src/main/resources/org/mifos/config/localizedResources
svn revert --recursive .

# generate localized strings from .po files
./create_props.sh

# back to "working_copy"
cd $OLDPWD

mvn clean install -Pcontinuous-integration,tomcat6x,download-container
