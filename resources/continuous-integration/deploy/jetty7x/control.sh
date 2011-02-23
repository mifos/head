#!/bin/bash
# Hudson/Jenkins Jetty control script
#
# description: Provides easy control of Hudson/Jenkins-maintained Jetty instances

DEPLOY_ROOT=$HOME/deploys/mifos-$JOB_NAME-deploy
JVM_TMPDIR=/tmp/hudson-$JOB_NAME-jetty-tmp

export MIFOS_CONF=$DEPLOY_ROOT/mifos_conf
export JAVA_OPTIONS="-Xmx768m -XX:MaxPermSize=256m -Djava.io.tmpdir=$JVM_TMPDIR -Djava.awt.headless=true"
export JETTY_HOME=$DEPLOY_ROOT/jetty7x
export JETTY_PID=$DEPLOY_ROOT/jetty.pid

controlScript=$JETTY_HOME/bin/jetty.sh

[[ -x $controlScript ]] || exit 1

[[ -d $JVM_TMPDIR ]] || mkdir -p $JVM_TMPDIR || exit 1

case "$1" in
    start)
        mkdir -p $JETTY_HOME/work # MIFOS-4769 & MIFOS-4765
        $controlScript start
        ;;
    stop)
        $controlScript stop
        ;;
    *)
        echo "Usage: $0 [start|stop]"
        exit 1
        ;;
esac

exit 0
