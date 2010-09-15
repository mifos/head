#!/bin/bash
set -o errexit

# NAME
#     locale_sync
#
# SYNOPSIS
#     ./locale_sync.sh <naming_convention_regex> <bundle_basedir>
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

naming_convention=$1
bundle_basedir=$2

# TODO: make sure args were passed, die otherwise

defaults=`\ls ${bundle_basedir}*.properties | grep -P "$naming_convention_regex"`

for locale in `find $bundle_basedir -regex '.*/[a-zA-Z_]+$' -type d | xargs basename`
do
    for bundle in $defaults
    do
        bundleBase=`basename $bundle .properties`
        echo $bundleBase
        potPath=$bundle_basedir/$locale/$bundleBase.pot
        poPath=$bundle_basedir/$locale/$bundleBase.po
        prop2po $bundle --pot $potPath
        if [ ! -e $poPath ]
        then
            pot2po $potPath $poPath
        else
            pot2po -t $poPath $potPath $poPath.TEMP
            mv $poPath.TEMP $poPath
        fi
        rm $potPath
    done
done
