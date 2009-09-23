#!/bin/sh
set -x
set -o errexit

# JOB_NAME environment variable must be set. We count on Hudson for this.

controlScript=$WORKSPACE/working_copy/resources/continuous-integration/deploy/tomcat/control.sh
lastStableWAR=$WORKSPACE/working_copy/application/target/mifos-webapp.war
deployRoot=$HOME/mifos-$JOB_NAME-deploy
targetWARlocation=$deployRoot/tomcat6/webapps/mifos.war

$controlScript stop
rm -f $deployRoot/tomcat6/logs/*
rm -f $deployRoot/tomcat6/webapps/mifos
cp $lastStableWAR $targetWARlocation
$controlScript start
