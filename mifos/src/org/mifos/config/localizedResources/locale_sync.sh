#!/bin/sh
set -o errexit

# NAME
#     push_prep
#
# SYNOPSIS
#     ./push_prep.sh
# 
# DESCRIPTION
#     push_prep converts Java .properties-based resource bundles into .po
#     (portable object) format in preparation for use with an external
#     translation tool.
#
# OPTIONS
#     None.

# NOTE: this will break in Rhino until existing locale-specific bundles are
# converted into .po files in their respective directories
defaults=`\ls *.properties`

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
