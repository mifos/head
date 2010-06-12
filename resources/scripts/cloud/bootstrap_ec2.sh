#!/bin/bash
set -o errexit

. lib.sh

if ! mifos_ssh_pubkeys_installed
then
    echo -n installing public ssh keys for Mifos admins...
    install_mifos_ssh_pubkeys
    echo Done.
fi

if ! firewall_enabled
then
    echo -n setting up firewall...
    enable_firewall
    echo Done.
fi

if ! remote_monitoring_enabled
then
    echo -n setting up remote monitoring...
    enable_remote_monitoring
    echo Done.
fi

if ! backup_pubkey_installed
then
    echo -n installing public ssh keys for automated backups...
    install_backup_pubkey
    echo Done.
fi

if ! prerequisite_packages_installed
then
    echo -n installing prerequisite packages...
    install_prerequisite_packages
    echo Done.
fi

echo Bootstrap complete. The following must be manually completed:
echo "* add this machine's IP to system monitor (see http://confluence.mifos.org/display/Main/System+Monitoring )"
echo "* install server SSL cert and key (see http://www.mifos.org/developers/wiki/CertsAndSSL )"
echo "* disable instance termination (see http://alestic.com/2010/01/ec2-instance-locking )"
echo "* enable Tomcat/OS backups for this machine (see http://confluence.mifos.org/display/Main/BackupPC )"

echo

echo You may now run setup_mifos.sh after completing the manual steps above.
