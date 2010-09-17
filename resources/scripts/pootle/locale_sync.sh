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
#     Example invocations (from the top-level dir of the Mifos source tree):
#
#       resources/scripts/pootle/locale_sync.sh '^[A-Za-z/]+\.properties$' application/src/main/resources/org/mifos/config/localizedResources
#
#       resources/scripts/pootle/locale_sync.sh '[A-Za-z]+\.properties$' userInterface/src/main/resources/org/mifos/ui/localizedProperties
#
# OPTIONS
#     None.

naming_convention=$1
bundle_basedir=$2

if [ -z "$naming_convention" ] || [ -z "$bundle_basedir" ]; then
    echo "Usage: $0 NAMING_CONVENTION_REGEX BUNDLE_BASEDIR"
    exit 1
fi

# TODO: make sure args were passed, die otherwise

defaults=`\ls ${bundle_basedir}/*.properties | grep -P "$naming_convention"`

for locale in `find $bundle_basedir -mindepth 1 -regex '.*/[a-zA-Z_]+$' -type d | xargs -n 1 basename`
do
    for bundle in $defaults
    do
        bundleBase=`basename $bundle .properties`
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
