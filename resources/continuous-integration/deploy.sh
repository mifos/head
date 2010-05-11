#!/bin/sh
set -x
set -o errexit

# JOB_NAME environment variable must be set. We count on Hudson for this.

controlScript=$WORKSPACE/resources/continuous-integration/deploy/tomcat/control.sh
lastStableWAR=$WORKSPACE/application/target/mifos-webapp.war
deployRoot=$HOME/deploys/mifos-$JOB_NAME-deploy
targetWARlocation=$deployRoot/tomcat6/webapps/mifos.war

$controlScript stop
rm -f $deployRoot/tomcat6/logs/*
rm -rf $deployRoot/tomcat6/webapps/mifos
rm -rf $deployRoot/tomcat6/work
cp $lastStableWAR $targetWARlocation
$controlScript start
