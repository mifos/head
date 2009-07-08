#!/bin/sh
set -x
set -o errexit

location=$1

controlScript=$HOME/continuousIntegrationResources/deploy/$location/tomcat/control.sh
lastStableWAR=$HOME/hudson-home/jobs/mifos-gazelle-$location/lastStable/archive/trunk/application/target/mifos-webapp.war
targetWARlocation=$HOME/mifos-$location-deploy/tomcat6/webapps/mifos.war

$controlScript stop
rm -f $HOME/mifos-$location-deploy/tomcat6/logs/*
cp $lastStableWAR $targetWARlocation
$controlScript start
