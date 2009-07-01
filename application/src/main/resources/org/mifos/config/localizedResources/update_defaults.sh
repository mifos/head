#!/bin/bash
set -o errexit

# NAME
#     update_defaults
#
# SYNOPSIS
#     ./update_defaults.sh
# 
# DESCRIPTION
#     Uses en/*.po translations to rebuild default translations in Java
#     .properties format resource bundles.
#
# OPTIONS
#     None.

defaults=`\ls *.properties | grep -v _`

for bundle in $defaults
do
    bundleBase=`basename $bundle .properties`
    en_po="en/${bundleBase}.po"

    # blow away pootle's "memory"... it may interfere with default strings
    sed -e '/^#~.*/d' $en_po > $en_po.copy
    mv $en_po.copy $en_po

    po2prop -t $bundle $en_po > $bundle.copy
    mv $bundle.copy $bundle

    prop2po $bundle > $en_po

    ./locale_sync.sh
done
