#!/bin/bash
# Hudson Tomcat control script
#
# description: Provides easy control of Hudson-deployed tomcat instance(s)

# Variables for use within this script
DEPLOY_ROOT=$HOME/mifos-$JOB_NAME-deploy
JVM_TMPDIR=/tmp/hudson-$JOB_NAME-tomcat-tmp
# Variables for use by children/successors of this script
export CATALINA_HOME=$DEPLOY_ROOT/tomcat6
export CATALINA_OPTS="-Xmx512m -Djava.io.tmpdir=$JVM_TMPDIR -Djava.awt.headless=true"
export CATALINA_PID=$DEPLOY_ROOT/tomcat.pid
export MIFOS_CONF=$DEPLOY_ROOT/mifos_conf

[ -f $CATALINA_HOME/bin/catalina.sh ] || exit 0

[ -d $JVM_TMPDIR ] || mkdir -p $JVM_TMPDIR || exit 1

case $1 in
start)
        $CATALINA_HOME/bin/startup.sh
        ;;
stop)  
        $CATALINA_HOME/bin/shutdown.sh -force
        rm -f $CATALINA_PID
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
        $CATALINA_HOME/bin/shutdown.sh
        sleep 1
        $CATALINA_HOME/bin/startup.sh
        ;;
*)
        echo "Usage: $0 {start|stop|status}"
        exit 1
        ;;
esac   
exit 0
