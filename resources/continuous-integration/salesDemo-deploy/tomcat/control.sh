#!/bin/bash
# Hudson Tomcat control script
#
# description: Provides easy control of Hudson-deployed tomcat instance(s)

# Variables for use within this script
DEPLOY_ROOT=$HOME/deploys/mifos-$JOB_NAME-deploy
JVM_TMPDIR=/tmp/hudson-$JOB_NAME-tomcat-tmp
# Variables for use by children/successors of this script
export CATALINA_HOME=$DEPLOY_ROOT/tomcat6
export CATALINA_OPTS="-Xmx512m -XX:MaxPermSize=128m -Djava.io.tmpdir=$JVM_TMPDIR -Djava.awt.headless=true"
export CATALINA_PID=$DEPLOY_ROOT/tomcat.pid
export MIFOS_CONF=$DEPLOY_ROOT/mifos_conf

[ -f $CATALINA_HOME/bin/catalina.sh ] || exit 0

[ -d $JVM_TMPDIR ] || mkdir -p $JVM_TMPDIR || exit 1

start_tomcat() {
        $CATALINA_HOME/bin/startup.sh
}

stop_tomcat() {
        if [ -e $CATALINA_PID ]
        then
            $CATALINA_HOME/bin/shutdown.sh -force
            rm -f $CATALINA_PID
        else
            $CATALINA_HOME/bin/shutdown.sh
        fi
}

case $1 in
start)
        start_tomcat
        ;;
stop)  
        stop_tomcat
        ;;
status)  
        if [ -e $CATALINA_PID ]
        then
            echo "Tomcat appears to be running as process id `cat $CATALINA_PID`"
        else
            echo "$CATALINA_PID does not exist, Tomcat probably is not running."
        fi
        ;;
restart)
        stop_tomcat
        sleep 1
        start_tomcat
        ;;
*)
        echo "Usage: $0 {start|stop|status}"
        exit 1
        ;;
esac   
exit 0
