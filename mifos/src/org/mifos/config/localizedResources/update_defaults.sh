#!/bin/bash
set -o errexit

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
    # blow away pootle's "memory"... it may interfere with default strings
    sed -e '/^#~.*/d' $bundle > $bundle.copy
    cp $bundle.copy $bundle
    po2prop -t $bundle en/${bundleBase}_en.po > $bundle.copy
    mv $bundle.copy $bundle
done
