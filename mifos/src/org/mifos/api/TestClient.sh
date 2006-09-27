#!/bin/bash

# This should be run from the root of the mifos source tree:
#   $ ./src/org/mifos/api/TestClient.sh <LoanId>

if [ -z $MIFOS_HOME ]
  then MIFOS_HOME=`pwd` 
fi

echo $MIFOS_HOME

java -cp $MIFOS_HOME/conf:$MIFOS_HOME/lib/hibernate3.jar:$MIFOS_HOME/lib/dom4j-1.4.jar:$MIFOS_HOME/lib/commons-logging.jar:$MIFOS_HOME/lib/commons-collections.jar:$MIFOS_HOME/lib/log4j-1.2.11.jar:$MIFOS_HOME/dist/mifos-lib.jar:$MIFOS_HOME/lib/cglib-full-2.0.2.jar:$MIFOS_HOME/lib/mysql-connector-java-5.0.3-bin.jar:$MIFOS_HOME/lib/jta.jar:$MIFOS_HOME/lib/antlr-2.7.4.jar:$MIFOS_HOME/dist/mifos-x.jar org.mifos.api.TestClient $*
