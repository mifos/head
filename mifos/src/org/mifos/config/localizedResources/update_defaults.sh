#!/bin/bash

# NAME
#     update_defaults
#
# SYNOPSIS
#     ./update_defaults.sh
# 
# DESCRIPTION
#     Uses *_en.po translations to rebuild default translations in Java
#     .properties format resource bundles.
#
# OPTIONS
#     None.

# NOTE: leave the "grep -v" until existing locale-specific bundles are
# converted into .po files in their respective directories
defaults=`\ls *.properties | grep -v _`

for bundle in $defaults
do
    bundleBase=`basename $bundle .properties`
    po2prop -t $bundle en/${bundleBase}_en.po > $bundle
done
