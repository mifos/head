#!/bin/bash
set -o errexit

SSH_PUB_KEYRING="~/.ssh/authorized_keys"
REMOTE_MONITORING_SERVER_IP="75.149.167.17"
DOWNLOAD_DIR=/opt/software
UNPRIVILEGED_USER=ubuntu
UNPRIVILEGED_GROUP=ubuntu
APACHE_CONF=/etc/apache2/conf.d/mifosapps.conf

mifos_ssh_pubkeys_installed() {
    if grep -q 'automated provisioning software' ~/.ssh/authorized_keys
    then
        return 0
    else
        return 1
    fi
}

install_mifos_ssh_pubkeys() {
    # go ahead and clobber existing .bak file
    cp $SSH_PUB_KEYRING $SSH_PUB_KEYRING.bak
    cat >> $SSH_PUB_KEYRING <<EndOfPublicKeys
ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEA5c57pEsxJNA5oQ26vRXixZdkFLSW40uFmJHaEAx9/PJxQGN+Gu6Z/iHAImDqDASCDHe9LihbjVusnBW4VVhCYGwGgtSpKXI2xBUBbD9hgmB0E8Km7fAjZ+HK6iXC2auxgGKvlHMIks/m0/n7lK0/1Dsi0poABkQT0UQwCYWV4irI03WQC99v26BveoyUnFjYXfyMBm6RA+5c8/U4UftXwpS4wWpE3fTV01JJdUvUOWPrswgaw3lXvBudw1qnarnY+/20kv4LDjEL5OPcPY/1L75X6ZrMVXVfpZnYV0slMnW9bOrJXF3Mf/Cc6qAGS0cMi2U9NRlnG/fqcEer1rFIVw== Key for Adam Monsen <amonsen@grameenfoundation.org> added by automated provisioning software
ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAQEAlN3C30nwy6f6aGBGaaUhfoGz2MGi3DIcqHviwAozaS/mlAGW2ab8v2ZUd22Na5SZ41WCEant2ybbH1tiY+SHeVCybd2UEW1N6PNEFGvDawwtm67JsOWlf+ut9rqyfbE6C2cHjTcjWXz6rGma5LNF1Bdv9AcLy/PU6ggmjCRtjd8ZoAsnxrV+72Quyi/Xu1g/FZUKacFeFYhvqZ7SYxzUNGjJFLwfqbrHDp1LDOfSVmeuiLjEvU5kcKV4Crx6bwPP5UqDXgjZcRvmjPPU6MgJc85IRpEIPEnw5KGv12ctZXgSRtBMGsJx6QTxb7edbs3zX/RTpfRmIWNunDSojseQ7Q== rsa-key-20090730 Key for Jeff Brewster <jbrewster@grameenfoundation.org> added by automated provisioning software
ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEA8EUh6YxHvLgvlvnK+L07DY4bk4O/c3ATSvt9nAtV1V0RKzxYmo0gxQkBvnCADbi9dcrak2w43ZoFt6LMB1+nNKqfAF5FBv8veQ4tFOfDqJ+N2Piq7zrVGWOrYeK2cp9om/xKM8K8T+hLVZICFAfT1UzcCUuyCmbIel10KMWF+dJthUI9yCBM2+sAVeeAYEC12wp4LaAany91dZIiC1TYNn3TZSncoFATj/FmtRAKeQv4TgF4i/yX7bxAJH+INvXMhK2Ff9cIWy331xpdfmvJGVuPsof8URsF7/GP7K/JzCFmDzqQIhjurehd49olXyn9DVVxoqTbYx01gMSfe/aoew== Key for Ryan Whitney <rwhitney@grameenfoundation.org> added by automated provisioning software
ssh-dss AAAAB3NzaC1kc3MAAACBAKLnKGMmS81ojKLE902cWAU9zEcGC8ii7E4rAgVmVzfXA6tpHzcxB5loCVT4BK8R/a3txx96c+iWWGwATFaLhvMkkdOQWKfvlT6OpZs2XjuRthszYuxVXEFXHlOV+m04KgQN7tpjGZqGaJamTUFE83TDtgoEX25O9VPTSWOHyMXlAAAAFQDIkT8HccKTTRFZBiHsu7xaMadDOwAAAIA39OEDGd3oZ6XVDSYopzjY1P+OVbRUmqamMjESrN3T9L4ElLV8ORwnYTjcaf0K8oSeYXVZWutSEpfGMBg+pD2sl+q+7uhNQdEyhqVfjUqlBz9YqbBn5Fs8nPeLJZCB7mC+iHglNaN+gyThMypUllNl3Wz0dSxxas4S8d2ZsNy6WwAAAIBDNzmnCtcbUavKCO4a/Wh1gWYhL6hhHTjgdX+9RobbuVRQtQUedXl44+eJb5McJyx8o6snW2fm+HfBnUQmRSmDAt0YqiOC/YrY4Ac4N3dRcte83ej/UbkxBgZ93Y0DsZFnsIIO0QTyZi0tsu6LF5MClIxcUQB8+smA9+ZusFJUtg== Key for Adam Feuer <afeuer@grameenfoundation.org> added by automated provisioning software
ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEAsWI6cciWatd71eQyyleid41oy3D6ZjHRCT/4PgRD3wKLZq/HhA5we2j+aUT0Ydj149/N8rufVYHkMOZhLudGp0/09FJuCiHzzW9AxfrO1M/9ZPRSbPurNfJ1knET1Dy2N6HWTtQ1/AIW+fyc+QZh0EiMjzRS2/sb5LumgZjMS3Ewjl4X17G422DCoh21NNxO/ckjiEzFnzyjE+5XlUxGCwmIO23hYtCNnnOziOIDh8OMEjt7awnAMKXZCZomD9RUmV3F00cjsmyCOTtQeWduVYKpSIpVowZdYJzMHvu5VWFDHlxTeGgkiH03lA5eM+N6PEhVQEP8DJJlYbRDcMrgoQ== Key for Feuer <afeuer@grameenfoundation.org> added by automated provisioning software
ssh-dss AAAAB3NzaC1kc3MAAACBANoQA4Mglr2Iao+4NaRozAtFU8vYdJmWfAI075lkwdBco5Wgvp+NsrICsmQCVG/ifKRb2TmH8M/GrM56KUkZRvfGOrtKoKOR8t3y71f5I5jn0jAE47Gs9nCnP//hULEAhoUpiSfhjoYK5YI1pDpc/sKX6E4vcaNbevzziXTJN83pAAAAFQCVp1i2Hv95d7FEWHt5covRwnOcOwAAAIAaCXK1/4qD7bKjL99Nv4G/LiKOFgwxAl25ZAddzNtYZg7LqPxFxGBVSq9XNURP3D3ZkYZ5G6ResUCAVK4R9AhmNzFjrWjKzcjVS1DpnbZwvEV0xB3rPF7XcFIbPaJ5CIixCFupeSqQqg/qje92LZWs8jzSwL+AQRPbkjV225tnFAAAAIBJrFo58ywHMoOsb5aazTf7vMMyfcagsPTAqYWWvE3TiQQHo6vzLEECgUv+CLbeYgxPK7czI0R6PuGbKTtFu2qYrCyKi7HZdDutCvfytsy/3vwcTgyBjiLpQ79YmUitzO9e2iadWnRCMCmlAXFdlGUxlA6/DuoTFTT/g+lJgLfR6g== Key for Van Mittal-Henkle <vanmh@grameenfoundation.org> added by automated provisioning software
ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEAqMU8LRRGD74zTe/vqw9r2rQ6rDBsBtuwYTApMngt4g3WZSmHaKIvUpvG0wQlPKc94IgqnRLI7sWttXrEkFs1hoN4rrvQbSBaLVmFzhfNABha2RYwrWvgkHs8AUwlo/Q79HfvCF/0qWYbCTUgriN4hxVn4hUnt7M625YLyWNqZO+5ygPs4UvL49NaXaF9CtXbQH+9otFTlEBe52Igt7pY3WjzlVaQUA/vdvkAprQBHsf3Asm95DH2uaiq1nc/vnCMKAqlUdoURqQr7xI/xwyEbBNcqRQHsuwFT0cInHpAiha+wDCEa4Q7GkwJ1F3GSjLlnjTynLG1XWkF1ZscIVacIw== Key for Udai Gupta <mailtoud@gmail.com> added by automated provisioning software
EndOfPublicKeys
}

firewall_enabled() {
    if sudo ufw status | grep -q 'Status: active'
    then
        return 0
    else
        return 1
    fi
}

enable_firewall() {
    sudo ufw allow proto tcp from any to any port ssh
    sudo ufw enable
}

remote_monitoring_enabled() {
    if grep -q 'grameenfoundation.org' /etc/snmp/snmpd.conf
    then
        return 0
    else
        return 1
    fi
}

# see http://www.mifos.org/developers/wiki/MonitoringSeversWithOpenNMS
enable_remote_monitoring() {
    sudo apt-get -y install snmp snmpd
    sudo mv /etc/snmp/snmpd.conf /etc/snmp/snmpd.conf.bak
    sudo sh -c "cat >> /etc/snmp/snmpd.conf" <<EndOfSnmpConf
rocommunity public default .1.3.6.1
syslocation USA
syscontact System Administrator <mifos@grameenfoundation.org>
disk /
disk /mnt
EndOfSnmpConf
    sudo mv /etc/default/snmpd /etc/default/snmpd.bak
    sudo perl -pi -e 's/ 127\.0\.0\.1//' /etc/default/snmpd
    sudo service snmpd restart
    sudo ufw allow proto udp from $REMOTE_MONITORING_SERVER_IP to any port snmp
}

prerequisite_packages_installed() {
    local javapackage=sun-java6-jre
    if dpkg -p $javapackage > /dev/null
    then
        return 0
    else
        return 1
    fi
}

install_prerequisite_packages() {
    # Sun Java 6 isn't in the main package repositories, it's in "partner"
    if ! grep partner /etc/apt/sources.list
    then
        sudo sh -c 'cat >> /etc/apt/sources.list' <<EndOfPartner
deb http://archive.canonical.com/ubuntu lucid partner
deb-src http://archive.canonical.com/ubuntu lucid partner
EndOfPartner
    fi

    sudo apt-get update 
    sudo apt-get -y install $javapackage mysql-client-5.1 apache2

    # download tomcat, place in a handy location for use by scripts which
    # set up Mifos instances
    sudo mkdir -p $DOWNLOAD_DIR
    sudo chown $UNPRIVILEGED_USER.$UNPRIVILEGED_GROUP $DOWNLOAD_DIR
    cd $DOWNLOAD_DIR
    wget http://ci.mifos.org/apache-tomcat-6.0.26.tar.gz

    sudo a2enmod proxy
    sudo a2enmod ssl
    sudo service apache2 restart

    local mifosappsconf=/etc/apache2/conf.d/mifosapps.conf
    if [ -e $mifosappsconf ]
    then
        echo ERROR, $mifosappsconf already exists
        return 1
    else
        sudo sh -c "cat >> $mifosappsconf" <<EndOfMifosAppsConf
ProxyRequests Off
<Proxy *>
    Allow from localhost
</Proxy>

SSLProxyEngine On

NameVirtualHost *:443
<VirtualHost *:443>
    SSLEngine on
    SSLCertificateFile /etc/apache2/ssl/server.crt
    SSLCertificateKeyFile /etc/apache2/ssl/server.key
    SetEnvIf User-Agent ".*MSIE.*" nokeepalive ssl-unclean-shutdown
</VirtualHost>
EndOfMifosAppsConf
    fi

    sudo mkdir /etc/apache2/ssl
    sudo ufw allow proto tcp from any to any port https
}

backup_pubkey_installed() {
    if sudo grep -q backuppc /root/.ssh/authorized_keys
    then
        return 0
    else
        return 1
    fi
}

install_backup_pubkey() {
    sudo sh -c 'echo ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEAwk7WLCpUYLUV2MxtOHAgnua0l1LlCaIA/a/L9ZCoSYgnqj+L5MTMvEEaQN19VKVujL6Xw5R9LXAePEwDF3lVGkvuVIHarQ/C4e/T89a7z5GrlLOr5OB2rqoR8/MDCRgZXbuCkJSi01Jk6p6rV66VICTSVforAi1nOtlBVE0UO5QE5wiibMlp4YzobrOaDTJbmxdO3mTkgFSFxg6mYkzle0n32XrfXLm+6BWVlIoTT9sCCYCj7sCT2YFkKuetKjTDOvCEGMoZ6abyLPpXgoJD5nnuNemG466g5EVKV5K3xKeoWaGyuCRbtE2J1PsNa0nMPxqUQ4sXepTfzgq0W4sJFw== backuppc@neem >> /root/.ssh/authorized_keys'
}

mifos_instance_root_exists() {
    if [ -d $mifos_instance_root ]
    then
        return 0
    else
        return 1
    fi
}

create_mifos_instance_root() {
    sudo mkdir $mifos_instance_root
    sudo chown $UNPRIVILEGED_USER.$UNPRIVILEGED_GROUP $mifos_instance_root
    pushd $mifos_instance_root
    tar -xzf $DOWNLOAD_DIR/apache-tomcat-6.0.26.tar.gz
    ln -s apache-tomcat-6.0.26 tomcat6
    mkdir mifos_conf
    popd
}

tomcat_configured() {
    if grep -q $tomcat_server_port $mifos_instance_root/tomcat6/conf/server.xml
    then
        return 0
    else
        return 1
    fi
}

configure_tomcat() {
    if grep -q $tomcat_server_port /opt/*/tomcat6/conf/server.xml
    then
        echo ERROR: server port $tomcat_server_port already in use!
        exit 1
    fi

    if grep -q $tomcat_connector_port /opt/*/tomcat6/conf/server.xml
    then
        echo ERROR: connector port $tomcat_connector_port already in use!
        exit 1
    fi

    pwd
    cp -n templates/context.xml $mifos_instance_root/tomcat6/conf/
    cat templates/server.xml | \
        sed -e "s/@TOMCAT_SERVER_PORT@/$tomcat_server_port/" | \
        sed -e "s/@TOMCAT_CONNECTOR_PORT@/$tomcat_connector_port/" > \
        $mifos_instance_root/tomcat6/conf/server.xml
}

init_script_setup() {
    if [ -e /etc/init.d/mifos-$instance_nickname ]
    then
        return 0
    else
        return 1
    fi
}

setup_init_script() {
    local tmpfile=`mktemp`
    cat templates/init_script | \
        sed -e "s#@DEPLOY_ROOT@#$mifos_instance_root#" | \
        sed -e "s/@INSTANCE_NICKNAME@/$instance_nickname/" | \
        sed -e "s/@UNPRIVILEGED_USER@/$UNPRIVILEGED_USER/" > \
        $tmpfile
    sudo mv $tmpfile /etc/init.d/mifos-$instance_nickname
    sudo update-rc.d mifos-$instance_nickname defaults
}

logging_configured() {
    if [ -e $mifos_instance_root/mifos_conf/loggerconfiguration.xml ]
    then
        return 0
    else
        return 1
    fi
}

configure_logging() {
    local tmpfile=`mktemp`
    cat templates/loggerconfiguration.xml | \
        set -e "s/@INSTANCE_NICKNAME@/$instance_nickname/" > \
        $tmpfile
    sudo mv $tmpfile $mifos_instance_root/mifos_conf/loggerconfiguration.xml
}

apache_frontend_setup() {
    if grep -q "$instance_nickname" $APACHE_CONF
    then
        return 0
    else
        return 1
    fi
}

setup_apache_frontend() {
    sudo sh -c "cat >> $APACHE_CONF" <<EndOfApacheConf
ProxyPass /$instance_nickname http://localhost:$tomcat_connector_port/$instance_nickname
ProxyPassReverse /$instance_nickname http://localhost:$tomcat_connector_port/$instance_nickname
EndOfApacheConf
    sudo service apache2 restart
}
