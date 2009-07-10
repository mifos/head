#!/bin/sh
#
# Mostly borrowed from /etc/init.d/tomcat6 on Ubuntu 9.04.
#
# Disables Tomcat security.

set -e

# JOB_NAME environment variable must be set. We count on Hudson for this.

PATH=/bin:/usr/bin:/sbin:/usr/sbin
NAME=tomcat6
DESC="Tomcat servlet engine"
DAEMON=/usr/bin/jsvc

DEPLOY_ROOT=$HOME/mifos-$JOB_NAME-deploy
DEPLOY_ROOT=`realpath "$DEPLOY_ROOT"`
CATALINA_HOME=$DEPLOY_ROOT/tomcat6
export MIFOS_CONF=$DEPLOY_ROOT/mifos_conf

. /lib/lsb/init-functions
. /etc/default/rcS

# Run Tomcat 6 as this user ID
TOMCAT6_USER=hudson
JVM_TMP=/tmp/$TOMCAT6_USER-$JOB_NAME-tomcat-tmp

# The first existing directory is used for JAVA_HOME (if JAVA_HOME is not
# defined in $DEFAULT)
JDK_DIRS="/usr/lib/jvm/java-6-openjdk /usr/lib/jvm/java-6-sun /usr/lib/jvm/java-1.5.0-sun /usr/lib/j2sdk1.5-sun /usr/lib/j2sdk1.5-ibm"

# Look for the right JVM to use
for jdir in $JDK_DIRS; do
    if [ -r "$jdir/bin/java" -a -z "${JAVA_HOME}" ]; then
        JAVA_HOME="$jdir"
    fi
done
export JAVA_HOME

# Directory for per-instance configuration files and webapps
CATALINA_BASE=$CATALINA_HOME
LOG_PATH=$CATALINA_HOME/logs

# Default Java options
# Set java.awt.headless=true if JAVA_OPTS is not set so the
# Xalan XSL transformer can work without X11 display on JDK 1.4+
# It also looks like the default heap size of 64M is not enough for most cases
# so the maximum heap size is set to 128M
# if [ -z "$JAVA_OPTS" ]; then
#     JAVA_OPTS="-Djava.awt.headless=true -Xmx128M"
# fi

if [ ! -f "$CATALINA_HOME/bin/bootstrap.jar" ]; then
    log_failure_msg "$NAME is not installed."
    exit 1
fi

if [ ! -f "$DAEMON" ]; then
    log_failure_msg "missing $DAEMON"
    exit 1
fi

JAVA_OPTS="$JAVA_OPTS -Djava.endorsed.dirs=$CATALINA_HOME/endorsed -Dcatalina.base=$CATALINA_BASE -Dcatalina.home=$CATALINA_HOME -Djava.io.tmpdir=$JVM_TMP -Djava.awt.headless=true"

# Set the JSP compiler if set in the tomcat6.default file
if [ -n "$JSP_COMPILER" ]; then
    JAVA_OPTS="$JAVA_OPTS -Dbuild.compiler=$JSP_COMPILER"
fi

# Define other required variables
CATALINA_PID="$DEPLOY_ROOT/$NAME.pid"
BOOTSTRAP_CLASS=org.apache.catalina.startup.Bootstrap
JSVC_CLASSPATH="/usr/share/java/commons-daemon.jar:$CATALINA_HOME/bin/bootstrap.jar"

# Look for Java Secure Sockets Extension (JSSE) JARs
if [ -z "${JSSE_HOME}" -a -r "${JAVA_HOME}/jre/lib/jsse.jar" ]; then
    JSSE_HOME="${JAVA_HOME}/jre/"
fi
export JSSE_HOME

case "$1" in
    start)
        if [ -z "$JAVA_HOME" ]; then
            log_failure_msg "no JDK found - please set JAVA_HOME"
            exit 1
        fi

        if [ ! -d "$CATALINA_BASE/conf" ]; then
            log_failure_msg "invalid CATALINA_BASE: $CATALINA_BASE"
            exit 1
        fi

        log_daemon_msg "Starting $DESC" "$NAME"
        if start-stop-daemon --test --start --pidfile "$CATALINA_PID" \
            --user $TOMCAT6_USER --startas "$JAVA_HOME/bin/java" \
            >/dev/null; then

            umask 022

            # Remove / recreate JVM_TMP directory
            rm -rf "$JVM_TMP"
            mkdir "$JVM_TMP" || {
                log_failure_msg "could not create JVM temporary directory"
                exit 1
            }
            chown $TOMCAT6_USER "$JVM_TMP"
            cd "$JVM_TMP"

            $DAEMON -user "$TOMCAT6_USER" -cp "$JSVC_CLASSPATH" \
                -outfile $LOG_PATH/console.log -errfile SYSLOG \
                -pidfile "$CATALINA_PID" $JAVA_OPTS "$BOOTSTRAP_CLASS"

            sleep 5

            if start-stop-daemon --test --start --pidfile "$CATALINA_PID" \
                --user $TOMCAT6_USER --startas "$JAVA_HOME/bin/java" \
                >/dev/null; then
                log_end_msg 1
            else
                log_end_msg 0
            fi
        else
                log_progress_msg "(already running)"
            log_end_msg 0
        fi
        ;;
    stop)
        log_daemon_msg "Stopping $DESC" "$NAME"
        if start-stop-daemon --test --start --pidfile "$CATALINA_PID" \
            --user "$TOMCAT6_USER" --startas "$JAVA_HOME/bin/java" \
            >/dev/null; then
            log_progress_msg "(not running)"
        else
            $DAEMON -cp "$JSVC_CLASSPATH" -pidfile "$CATALINA_PID" \
                 -stop "$BOOTSTRAP_CLASS"
        fi
        rm -rf "$JVM_TMP"
        log_end_msg 0
        ;;
    status)
        if start-stop-daemon --test --start --pidfile "$CATALINA_PID" \
            --user $TOMCAT6_USER --startas "$JAVA_HOME/bin/java" \
            >/dev/null; then

        if [ -f "$CATALINA_PID" ]; then
                log_success_msg "$DESC is not running, but pid file exists."
                exit 1
            else
                log_success_msg "$DESC is not running."
                exit 3
            fi
        else
            log_success_msg "$DESC is running with pid `cat $CATALINA_PID`"
        fi
        ;;
    restart|force-reload)
        if start-stop-daemon --test --stop --pidfile "$CATALINA_PID" \
            --user $TOMCAT6_USER --startas "$JAVA_HOME/bin/java" \
            >/dev/null; then
            $0 stop
            sleep 1
        fi
        $0 start
        ;;
    try-restart)
        if start-stop-daemon --test --start --pidfile "$CATALINA_PID" \
            --user $TOMCAT6_USER --startas "$JAVA_HOME/bin/java" \
            >/dev/null; then
            $0 start
        fi
        ;;
    *)
        log_success_msg "Usage: $0 {start|stop|restart|try-restart|force-reload|status}"
        exit 1
        ;;
esac

exit 0
