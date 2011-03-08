#! /bin/sh

if [ -n "${LIQUIBASE_HOME+x}" ]; then
echo "Liquibase Home: $LIQUIBASE_HOME"
else
  echo "Liquibase Home is not set."

  ## resolve links - $0 may be a symlink
  PRG="$0"
  while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
    else
    PRG=`dirname "$PRG"`"/$link"
    fi
  done


  LIQUIBASE_HOME=`dirname "$PRG"`

  # make it fully qualified
  LIQUIBASE_HOME=`cd "$LIQUIBASE_HOME" && pwd`
  echo "Liquibase Home: $LIQUIBASE_HOME"
fi

cd $LIQUIBASE_HOME/..

# build classpath from all jars in lib
if [ -f /usr/bin/cygpath ]; then
  CP=.
  for i in "$LIQUIBASE_HOME"/../liquibase*.jar; do
    i=`cygpath --windows "$i"`
    CP="$CP;$i"
  done
  for i in "$LIQUIBASE_HOME"/../lib/*.jar; do
    i=`cygpath --windows "$i"`
    CP="$CP;$i"
  done
else
  CP=.
  for i in "$LIQUIBASE_HOME"/../liquibase*.jar; do
    CP="$CP":"$i"
  done
  for i in "$LIQUIBASE_HOME"/../lib/*.jar; do
    CP="$CP":"$i"
  done
fi

# add any JVM options here
JAVA_OPTS=

DB_PROPS_FILE=$LIQUIBASE_HOME/../mifos-db.properties
CHANGE_LOG_FILE=$LIQUIBASE_HOME/../changesets/changelog-master.xml

DRIVER=com.mysql.jdbc.Driver
DEFAULT_SCHEMA_NAME=`sed '/^\#/d' $DB_PROPS_FILE | grep 'defaultSchemaName'  | tail -n 1 | cut -d "=" -f2-`
USERNAME=`sed '/^\#/d' $DB_PROPS_FILE | grep 'username'  | tail -n 1 | cut -d "=" -f2-`
PASSWORD=`sed '/^\#/d' $DB_PROPS_FILE | grep 'password'  | tail -n 1 | cut -d "=" -f2-`
HOST=`sed '/^\#/d' $DB_PROPS_FILE | grep 'databaseHost'  | tail -n 1 | cut -d "=" -f2-`
PORT=`sed '/^\#/d' $DB_PROPS_FILE | grep 'databasePort'  | tail -n 1 | cut -d "=" -f2-`
URL=jdbc:mysql://$HOST:$PORT/$DEFAULT_SCHEMA_NAME

java -cp "$CP" $JAVA_OPTS liquibase.integration.commandline.Main --changeLogFile=$CHANGE_LOG_FILE --driver=$DRIVER --url=$URL --username=$USERNAME --password=$PASSWORD --defaultSchemaName=$DEFAULT_SCHEMA_NAME ${1+"$@"}
