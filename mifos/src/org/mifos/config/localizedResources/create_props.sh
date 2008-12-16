#!/bin/bash
set -o errexit

# creates locale-specific .properties files for use by Mifos web app.
# [_] integrate into build process
# 	  [_] on build server
# 	  [_] as part of Maven build

for locale in `find . -regex '[^/]*/[a-zA-Z_]*$' -type d | cut -c3-`
do
    for translated in `find $locale -type f -name "*.po"`
    do
        # remove directory part of path
        translatedBase=`basename $translated`

        # remove file extension
        bundleBase=`echo $translatedBase | sed -e 's/\.po$//'`

        # create locale-specific .properties files for use by Mifos web app
        po2prop -t $bundleBase.properties \
            $translated ${bundleBase}_$locale.properties
    done
done
