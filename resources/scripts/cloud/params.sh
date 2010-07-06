#!/bin/bash

# maintain this file before spinning up a new Mifos instance

instance_nickname=kmbi
mifos_instance_root=/opt/mifos-$instance_nickname
database_name=mifostest_$instance_nickname
database_user=mifosroot
database_pwd=neo8Yknc3Tb

# increment this by two
tomcat_server_port=8007

# increment this by two
tomcat_connector_port=8008

RDS_HOSTNAME=mifoscloudtest.cz2a1vveusgo.us-east-1.rds.amazonaws.com
