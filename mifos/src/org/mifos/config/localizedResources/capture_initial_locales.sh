#!/bin/bash
set -o errexit

defaults=`\ls *.properties | grep -v _`

for locale in `find . -regex '[^/]*/[a-zA-Z_]*$' -type d | cut -c3-`
do
    for bundle in $defaults
    do
        bundleBase=`basename $bundle .properties`
        echo $bundleBase
        prop2po -t $bundle \
          -i $locale/${bundleBase}_$locale.properties > $locale/$bundleBase.po
    done
done
