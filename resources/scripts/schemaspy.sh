#!/bin/bash
set -o errexit

TRUNK_DB_VERSION=`echo 'select database_version from database_version' | mysql --skip-column-names -u hudson -phudson hudson_mifos_gazelle_trunk`
outputDir=/var/www/schema/trunk/$TRUNK_DB_VERSION

if [ -d $outputDir ]
then
    echo $outputDir exists, assuming schema has not changed. Exiting gracefully.
fi

mkdir $outputDir

java -jar $HOME/arc/schemaSpy_4.1.1.jar -t mysql -host localhost -u hudson -p hudson -db hudson_mifos_gazelle_trunk -dp $HOME/arc/mysql-connector-java-5.1.12-bin.jar -hq -o $outputDir
