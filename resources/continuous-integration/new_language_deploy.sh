#!/bin/bash
set -o errexit

# Create new deployment root for a Mifos Hudson build job

deployNickname=$1
if [ -z "$deployNickname" ]
then
    echo "ERROR: Must provide a deployment nickname like 'trunk' or 'v1.3.x' to proceed"
    echo "Usage: $0 NICKNAME"
    exit 1
fi

deployRoot=$HOME/deploys/mifos-$deployNickname-deploy
if [ -e $deployRoot ]
then
    echo ERROR: $deployRoot already exists.
    exit 1
fi

ciResources=$HOME/hudson-home/jobs/trunk/workspace/working_copy/resources/continuous-integration

set -x
mkdir $deployRoot
cd $deployRoot
tar -xzf $HOME/arc/apache-tomcat-6.*.tar.gz
ln -s apache-tomcat-* tomcat6
ln -s $ciResources/$deployNickname-deploy/mifos_conf
cd tomcat6/conf
ln -fs $ciResources/$deployNickname-deploy/tomcat/server.xml
ln -fs $ciResources/$deployNickname-deploy/tomcat/context.xml
cd ../lib
ln -s $ciResources/$deployNickname-deploy/tomcat/c3p0.properties
