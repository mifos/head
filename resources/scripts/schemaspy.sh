#!/bin/bash
set -o errexit

DATABASE_VERSION=`echo 'select database_version from database_version' | mysql --skip-column-names -u hudson -phudson hudson_mifos_gazelle_trunk`
tempDir=`mktemp -d`
outputDir=/var/www/schema/trunk/$DATABASE_VERSION

if [ -d $outputDir ]
then
    echo "$outputDir exists--looks like the schema has not changed. Exiting gracefully."
    exit 0
fi

echo "Generating schema documentation for database version $DATABASE_VERSION"

mkdir $tempDir

java -jar $HOME/arc/schemaSpy_4.1.1.jar -t mysql -host localhost -u hudson -p hudson -db hudson_mifos_gazelle_trunk -dp $HOME/arc/mysql-connector-java-5.1.12-bin.jar -hq -o $tempDir

# this mv increases the probability that $outputDir will not exist unless schemaSpy succeeded
# FIXME: this won't work unless schemaSpy is modified to return exit codes that make sense... currently it appears to always return "success"
mv $tempDir $outputDir
