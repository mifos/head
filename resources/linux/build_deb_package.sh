#!/bin/bash

if ! ( [ -e README ] && [ -e pom.xml ] && [ -d serviceInterfaces ] )
then
    echo Run this script as resources/linux/`basename $0` from the topmost directory
    echo of a Mifos head clone.
    exit 1
fi

# disable tests, I just want to build a .deb quickly
export DEB_BUILD_OPTIONS="nocheck"

dpkg-buildpackage -A -tc
