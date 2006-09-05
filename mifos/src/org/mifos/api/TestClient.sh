#!/bin/bash

# This should be run from the root of the mifos source tree:
#   $ ./src/org/mifos/api/TestClient.sh <LoanId>

java -cp conf:lib/hibernate3.jar:lib/dom4j-1.4.jar:lib/commons-logging.jar:lib/commons-collections.jar:lib/log4j-1.2.11.jar:dist/mifos-lib.jar:lib/cglib-full-2.0.2.jar:lib/mysql-connector-java-3.1.7-bin.jar:lib/jta.jar:lib/antlr-2.7.4.jar:dist/mifos-x.jar org.mifos.api.TestClient $*
