#!/bin/bash

# renames WAR file so the revision number is part of the filename

tdir=`mktemp -d`
vinfo=WEB-INF/classes/org/mifos/config/resources/versionInfo.properties
src_war=$WAR_ARCHIVE/mifos.war

if [ ! -r $src_war ]
then
    echo $src_war does not exist or is not readable. Exiting...
    exit 0
fi

cd $tdir
jar -xf $src_war $vinfo
# \+ requires GNU sed
revnum=`grep revision $vinfo | sed -e 's/^[^=]*=\([[:digit:]]\+\)$/\1/'`
dest_war=$WAR_ARCHIVE/mifos-$revnum.war

[ -e $dest_war ] && echo $dest_war already exists and will be overwritten

mv $src_war $WAR_ARCHIVE/mifos-$revnum.war

cd /tmp
rm -rf $tdir
