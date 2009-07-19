#!/bin/bash
# Hudson Tomcat control script
#
# description: Provides easy control of Hudson-deployed tomcat instance(s)

# Set Tomcat environment.
DEPLOY_ROOT=$HOME/mifos-$JOB_NAME-deploy
CATALINA_HOME=$DEPLOY_ROOT/tomcat6
CATALINA_PID=$DEPLOY_ROOT/tomcat.pid
CATALINA_OPTS="-Xmx512m -Djava.io.tmpdir=/tmp/$TOMCAT6_USER-$JOB_NAME-tomcat-tmp -Djava.awt.headless=true"
export MIFOS_CONF=$DEPLOY_ROOT/mifos_conf

[ -f $CATALINA_HOME/bin/catalina.sh ] || exit 0

case $1 in
start)
        $CATALINA_HOME/bin/startup.sh
        ;;
stop)  
        $CATALINA_HOME/bin/shutdown.sh
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
        $CATALINA_HOME/bin/startup.sh
        ;;
*)
        echo "Usage: $0 {start|stop|status}"
        exit 1
        ;;
esac   
exit 0
