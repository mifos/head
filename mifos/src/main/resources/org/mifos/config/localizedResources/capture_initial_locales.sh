#!/bin/bash
set -o errexit

# generate initial locale-specific .po files based on existing locale-specific
# Java .properties-based resource bundles.
# ex: Blah_fr.properties -> fr/Blah.po

defaults=`\ls *.properties | grep -v _`

for locale in `find . -regex '[^/]*/[a-zA-Z_]*$' -type d | cut -c3-`
do
    for bundle in $defaults
    do
        bundleBase=`basename $bundle .properties`
        if [ ! -e $locale/${bundleBase}.po ]
        then
            prop2po -t $bundle \
              -i ${bundleBase}_$locale.properties > $locale/$bundleBase.po
        else
            echo -n "$locale/${bundleBase}.po already exists."
            echo " Delete first and re-run this script to overwrite."
        fi
    done
done
