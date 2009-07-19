#!/bin/bash
set -o errexit

# creates locale-specific .properties files for use by Mifos web app.

sourceDir=$1
targetDir=$2

for localeDir in `find $sourceDir -mindepth 1 -maxdepth 1 \
    -regex '[^.]*/[a-zA-Z_]+$' -type d`
do
    locale=`basename $localeDir`
    for translated in `find $localeDir -type f -name "*.po"`
    do
        # remove directory part of path
        translatedBase=`basename $translated`

        # remove file extension
        bundleBase=`echo $translatedBase | sed -e 's/\.po$//'`

        # create locale-specific .properties files for use by Mifos web app
        po2prop -t $sourceDir/$bundleBase.properties \
            $translated $targetDir/${bundleBase}_$locale.properties
    done
done
