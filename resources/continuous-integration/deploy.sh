#!/bin/sh
set -x
set -o errexit
lastStableWAR=$HOME/hudson-home/jobs/mifos-gazelle-trunk/lastStable/archive/trunk/application/target/mifos-webapp.war

$HOME/mifos-v1.3.x-deploy/control.sh stop
cp $lastStableWAR $HOME/mifos-v1.3.x-deploy/tomcat6/webapps/mifos.war
$HOME/mifos-v1.3.x-deploy/control.sh start
