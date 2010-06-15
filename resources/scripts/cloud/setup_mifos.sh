#!/bin/bash

. lib.sh
. params.sh

if ! mifos_instance_root_exists
then
	echo -n setting up Mifos instance root...
	create_mifos_instance_root
	echo Done.
fi

if ! tomcat_configured
then
	echo -n setting up Tomcat for this Mifos instance...
	configure_tomcat
	echo Done.
fi

if ! init_script_setup
then
	echo -n setting up init script for this Mifos instance...
	setup_init_script
	echo Done.
fi

# logger configuration (write logs to a unique place)
if ! logging_configured
then
	echo -n setting up Mifos application log configuration...
	configure_logging
	echo Done.
fi

# create database instance
## in mysql
### comment: password exists in ~/.my.cnf
### test: mysql -h RDS_HOSTNAME -u mifosroot <<< 'use mifostest'
### create: mysqladmin -h RDS_HOSTNAME -u mifosroot create mifostest

# set up db connection parameters
## local.properties

# set up apache frontend
## add ProxyPass* lines to /etc/apache2/conf.d/mifosapps.conf
## restart apache
if ! apache_frontend_setup
then
	echo -n setting up Apache reverse proxy...
	setup_apache_frontend
	echo Done.
fi

# lastly, test connect to Mifos (or remind user to do so)
# for example: https://cloudtest.mifos.org/lightmf
