#!/bin/sh
set -o errexit

# NAME
#     push_prep
#
# SYNOPSIS
#     ./push_prep.sh LANG
# 
# DESCRIPTION
#     push_prep converts Java .properties-based resource bundles into .po
#     (portable object) format in preparation for use with an external
#     translation tool.
#
# OPTIONS
#     LANG must be a language code like 'fr', 'es', or 'en_GB'.
#
# WARNING
#     This script is brittle and dangerous! Notice how the first argument is a
#     directory that is promptly nuked. Use at your own risk.

# NOTE: this will break in Rhino until existing locale-specific bundles are
# converted into .po files in their respective directories
defaults=`\ls *.properties`

# NOTE: new locales MUST be added here or they will not be sync'd!
for locale in es fr
do
    mkdir -p $locale

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
        # do instead at build time to generate language-specific .properties
        # NOTE: initial locale-specific .po files for Rhino should be generated
        #       using something like this.
        #prop2po -t $bundle \
        #  -i $locale/${bundleBase}_$locale.properties > $locale/$bundleBase.po
    done
done
