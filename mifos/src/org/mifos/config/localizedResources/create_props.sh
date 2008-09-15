#!/bin/bash
set -o errexit

for locale in `find . -regex '[^/]*/[a-zA-Z_]*$' -type d | cut -c3-`
do
    for translated in `find $locale -type f -name "*$locale.po"`
    do
        # remove directory part of path
        translatedBase=`basename $translated`

        # remove locale and file extension
        bundleBase=`echo $translatedBase | sed -e 's/_[a-zA-Z]*\.po//'`

        # create locale-specific .properties files for use by Mifos web app
        po2prop -t $bundleBase.properties \
            $translated ${bundleBase}_$locale.properties
    done
done
