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
        if [ ! -e $locale/${bundleBase}.po ]
        then
            pot2po $bundleBase.pot $locale/${bundleBase}.po
        else
            pot2po -t $locale/${bundleBase}.po $bundleBase.pot \
                ${bundleBase}.po.TEMP
            mv ${bundleBase}.po.TEMP $locale/${bundleBase}.po
        fi
        rm $bundleBase.pot
    done
done
