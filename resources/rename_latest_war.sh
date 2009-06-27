#!/bin/bash

# renames WAR file so the revision number is part of the filename
#
# BUILD_NUMBER and WAR_ARCHIVE are assumed set by caller (Hudson)

tdir=`mktemp -d`
vinfo=WEB-INF/classes/org/mifos/config/resources/versionInfo.properties
src_war=$WAR_ARCHIVE/mifos-webapp.war

if [ ! -r $src_war ]
then
    echo $src_war does not exist or is not readable. Exiting...
    exit 0
fi

cd $tdir
jar -xf $src_war $vinfo
# \+ requires GNU sed
revnum=`grep revision $vinfo | sed -e 's/^[^=]*=\([[:digit:]]\+\)$/\1/'`
dest_war=$WAR_ARCHIVE/mifos-$BUILD_NUMBER-$revnum.war

if [ -e $dest_war ]
then
    echo ERROR: $dest_war already exists! This should not happen.
    exit 1
fi

mv $src_war $dest_war

cd /tmp
rm -rf $tdir
