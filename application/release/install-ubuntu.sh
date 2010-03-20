#!/bin/bash

# Copyright (c) 2005-2009 Grameen Foundation USA
# All rights reserved.
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#     http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
# implied. See the License for the specific language governing
# permissions and limitations under the License.
# 
# See also http://www.apache.org/licenses/LICENSE-2.0.html for an
# explanation of the license and how it is applied.

#### Install script settings

# defaults from http://www.mifos.org/developers/wiki/MifosUbuntuInstall

# Full path of Mifos web application archive to deploy
INSTALL_WAR=mifos.war

# Root of Tomcat installation. If Mifos is already deployed under this
# directory, it will be deleted.
CATALINA_HOME=$HOME/tomcat6

# Directory with Mifos configuration files. If $HOME/.mifos is not used (where
# $HOME is the user's home which Mifos runs as) the MIFOS_CONF environment
# variable must be set and exported prior to starting Mifos. See
# http://www.mifos.org/developers/wiki/MifosConfigurationLocations for more
# information. Backups will be made of existing configuration files.
MIFOS_CONF=$HOME/.mifos

# MySQL administrative user for creating Mifos database
MYSQL_ADMIN_USERNAME=root
MYSQL_ADMIN_PASSWORD=

# MySQL connection settings used by Mifos web application
MYSQL_MIFOS_USERNAME=mifos
MYSQL_MIFOS_PASSWORD=mifos
MYSQL_MIFOS_DATABASE_NAME=mifos
MYSQL_HOST=localhost
MYSQL_PORT=3306

# Where Mifos connects from. Used for setting database permissions.
MYSQL_CLIENT_HOST=localhost

####

echo Mifos Ubuntu Install Script

#### set up logging

logfile=`mktemp --tmpdir mifos_ubuntu_install_log.XXXXXX`
echo Details will be logged to $logfile
echo

#### check for dependencies

which_result=`which java`
echo -n "Looking for Java ... " | tee -a $logfile
if [ -n "$which_result" ] && [ -x "$which_result" ]; then
    echo found. | tee -a $logfile
    java -version 2>> $logfile
else
    echo ERROR: Java not found. | tee -a $logfile
    exit 1
fi

echo -n "Looking for local MySQL server ... " | tee -a $logfile
dpkg -p mysql-server > /dev/null
dpkg_result=$?
if [ $dpkg_result -eq 0 ]; then
    echo found. | tee -a $logfile
    mysqld --version >> $logfile
else
    echo WARNING: local MySQL Server not found. | tee -a $logfile
    echo Proceeding assuming a remote server will be used. | tee -a $logfile
fi

echo -n "Looking for Tomcat ... " | tee -a $logfile
version_result=-1
if [ -e "$CATALINA_HOME/bin/catalina.sh" ]; then
    echo found. | tee -a $logfile
else
    echo ERROR: Tomcat not found. | tee -a $logfile
    exit 1
fi

# A convenience: make all Tomcat scripts executable. As of 6.0.20, scripts in
# bin/ in the release zip are not executable.
chmod +x $CATALINA_HOME/bin/*.sh 2>&1 | tee -a $logfile

$CATALINA_HOME/bin/version.sh >> $logfile
version_result=$?
if [ $version_result -ne 0 ]; then
    echo ERROR: could not determine Tomcat version. | tee -a $logfile
    exit 1
fi

# use TCP since I think that's what the MySQL client library will use
echo -n "Trying to connect to MySQL server as administrator ... " | tee -a $logfile
mysql_result=`mysql --host=$MYSQL_HOST --port=$MYSQL_PORT --user=$MYSQL_ADMIN_USERNAME --password="$MYSQL_ADMIN_PASSWORD" --protocol=TCP <<< 'QUIT' 2>&1`

if [ -z "$mysql_result" ]; then
    echo success. | tee -a $logfile
else
    # assume any output indicates a connection error occurred
    echo $mysql_result | tee -a $logfile
    exit 1
fi

#### Initialize database

echo -n "Initializing Mifos database ..." | tee -a $logfile

echo >> $logfile
echo "Creating database ... " >> $logfile
mysql_result=`mysqladmin --host=$MYSQL_HOST --port=$MYSQL_PORT --user=$MYSQL_ADMIN_USERNAME --password="$MYSQL_ADMIN_PASSWORD" create "$MYSQL_MIFOS_DATABASE_NAME" 2>&1`

if [ -n "$mysql_result" ]; then
    echo Creating database failed. $mysql_result | tee -a $logfile
    exit 1
fi

echo -n .
echo "Granting permissions ... " >> $logfile
mysql_result=`mysql --host=$MYSQL_HOST --port=$MYSQL_PORT --user=$MYSQL_ADMIN_USERNAME --password="$MYSQL_ADMIN_PASSWORD" <<< "GRANT ALL ON $MYSQL_MIFOS_DATABASE_NAME.* TO '$MYSQL_MIFOS_USERNAME'@'$MYSQL_CLIENT_HOST' IDENTIFIED BY '$MYSQL_MIFOS_PASSWORD'" 2>&1`

if [ -n "$mysql_result" ]; then
    echo Granting permissions failed. $mysql_result | tee -a $logfile
    exit 1
fi

echo -n .
echo "Flushing privileges ... " >> $logfile
mysql_result=`mysqladmin --host=$MYSQL_HOST --port=$MYSQL_PORT --user=$MYSQL_ADMIN_USERNAME --password="$MYSQL_ADMIN_PASSWORD" flush-privileges 2>&1`

if [ -n "$mysql_result" ]; then
    echo Flushing privileges failed. $mysql_result | tee -a $logfile
    exit 1
fi

echo -n .
echo "Running sql/latest-schema.sql ... " >> $logfile
mysql_result=`mysql --host=$MYSQL_HOST --port=$MYSQL_PORT --user=$MYSQL_MIFOS_USERNAME --password="$MYSQL_MIFOS_PASSWORD" --database=$MYSQL_MIFOS_DATABASE_NAME < sql/latest-schema.sql 2>&1`

if [ -n "$mysql_result" ]; then
    echo Creating schema failed. $mysql_result | tee -a $logfile
    exit 1
fi

echo -n .
echo "Running sql/latest-data.sql ... " >> $logfile
mysql_result=`mysql --host=$MYSQL_HOST --port=$MYSQL_PORT --user=$MYSQL_MIFOS_USERNAME --password="$MYSQL_MIFOS_PASSWORD" --database=$MYSQL_MIFOS_DATABASE_NAME < sql/latest-data.sql 2>&1`

if [ -n "$mysql_result" ]; then
    echo Loading initial data failed. $mysql_result | tee -a $logfile
    exit 1
fi

echo -n ". "
echo "Running sql/init_mifos_password.sql ... " >> $logfile
mysql_result=`mysql --host=$MYSQL_HOST --port=$MYSQL_PORT --user=$MYSQL_MIFOS_USERNAME --password="$MYSQL_MIFOS_PASSWORD" --database=$MYSQL_MIFOS_DATABASE_NAME < sql/init_mifos_password.sql 2>&1`

if [ -n "$mysql_result" ]; then
    echo Initializing password failed. $mysql_result | tee -a $logfile
    exit 1
fi

echo success. | tee -a $logfile

#### Install Mifos web application

echo -n "Attempting to deploy $INSTALL_WAR ... " | tee -a $logfile

echo Stopping Tomcat >> $logfile
$CATALINA_HOME/bin/shutdown.sh > /dev/null 2>&1

rm_result=`rm -rf $CATALINA_HOME/webapps/mifos 2>&1`
if [ $? -ne 0 ]; then
    echo Clearing exploded war dir failed. $rm_result | tee -a $logfile
    exit 1
fi

cp_result=`cp $INSTALL_WAR $CATALINA_HOME/webapps 2>&1`
if [ $? -eq 0 ]; then
    echo success. | tee -a $logfile
else
    echo Deploy failed. $cp_result | tee -a $logfile
    exit 1
fi

#### Create custom local configuration file

echo -n "Configuring database connection ... " | tee -a $logfile

echo Creating $MIFOS_CONF, if it doesn''t already exist >> $logfile
mkdir_result=`mkdir -p $MIFOS_CONF 2>&1`
if [ $? -ne 0 ]; then
    echo Could not create $MIFOS_CONF. $mkdir_result | tee -a $logfile
    exit 1
fi

if [ -e $MIFOS_CONF/local.properties ]; then
    backup=`mktemp $MIFOS_CONF/local.properties.bak.XXXXX`
    echo Backing up $MIFOS_CONF/local.properties as $backup >> $logfile
    mv_result=`mv $MIFOS_CONF/local.properties $backup 2>&1`
    if [ $? -ne 0 ]; then
        echo Could not backup existing local.properties. $mv_result | tee -a $logfile
        exit 1
    fi
fi

echo "main.database=$MYSQL_MIFOS_DATABASE_NAME"     >> $MIFOS_CONF/local.properties
echo "main.database.host=$MYSQL_HOST"               >> $MIFOS_CONF/local.properties
echo "main.database.port=$MYSQL_PORT"               >> $MIFOS_CONF/local.properties
echo "main.database.user=$MYSQL_MIFOS_USERNAME"     >> $MIFOS_CONF/local.properties
echo "main.database.password=$MYSQL_MIFOS_PASSWORD" >> $MIFOS_CONF/local.properties

echo done. | tee -a $logfile

#### Tidy up, exit

echo | tee -a $logfile
echo See http://www.mifos.org/knowledge/support/deploying-mifos/configuration/guide | tee -a $logfile
echo for more information on configuring Mifos. If this Mifos instance will be used | tee -a $logfile
echo for production, DO NOT START MIFOS BEFORE READING THE CONFIGURATION GUIDE. | tee -a $logfile
echo | tee -a $logfile
echo Edit $MIFOS_CONF/local.properties to change database connection settings. | tee -a $logfile
echo Run $CATALINA_HOME/bin/startup.sh to start Mifos. | tee -a $logfile
echo | tee -a $logfile
echo DONE. Details logged in $logfile
