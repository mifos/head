#!/bin/bash
set -o errexit

# NAME
#     locale_sync
#
# SYNOPSIS
#     ./locale_sync.sh
# 
# DESCRIPTION
#     locale_sync converts Java .properties-based resource bundles into .po
#     (portable object) format in preparation for use with an external
#     translation tool.
#
#     Should be executed after updating default translations to update .po
#     files.
#
# OPTIONS
#     None.

defaults=`\ls *.properties | grep -v _`

for locale in `find . -regex '[^/]*/[a-zA-Z_]*$' -type d | cut -c3-`
do
    for bundle in $defaults
    do
        bundleBase=`basename $bundle .properties`
        echo $bundleBase
        prop2po $bundle --pot $bundleBase.pot
        if [ ! -e $locale/${bundleBase}_$locale.po ]
        then
            pot2po $bundleBase.pot $locale/${bundleBase}_$locale.po
        else
            pot2po -t $locale/${bundleBase}_$locale.po $bundleBase.pot \
                ${bundleBase}_$locale.po.TEMP
            mv ${bundleBase}_$locale.po.TEMP $locale/${bundleBase}_$locale.po
        fi
        rm $bundleBase.pot
        # NOTE: initial locale-specific .po files for Rhino should be generated
        #       using something like this.
        #prop2po -t $bundle \
        #  -i $locale/${bundleBase}_$locale.properties > $locale/$bundleBase.po
    done
done
