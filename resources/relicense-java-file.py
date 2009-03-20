#!/usr/bin/env python
# Copyright (c) 2005-2009 Grameen Foundation USA
# All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
# implied. See the License for the specific language governing
# permissions and limitations under the License.
#
# See also http://www.apache.org/licenses/LICENSE-2.0.html for an
# explanation of the license and how it is applied.

import sys, re

LICENSE_TEXT="""/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
"""

class Relicense:
    """Changes the license text that appears at the start of Mifos java files. Will add a license to files that do not contain one.
To relicense all java files in the source tree, do something like this:
find . -not -ipath "*/target/*" -not -ipath "*.svn*" -iname "*.java"| xargs -ifoo ./resources/relicense-java-file.py foo

"""
    def __init__(self):
        pass
    
    def main(self):
        if len(sys.argv) < 2:
            sys.exit(0)
        filename = sys.argv[1]
        self.relicense(filename)
        
    def relicense(self, filename):
        contents = self.readEntireFile(filename)
        newContents = self.replaceLicense(contents, LICENSE_TEXT)
        if (contents != newContents):
            self.writeEntireFile(filename, newContents)
            print "Relicensed file: %s" % filename
        
    def replaceLicense(self, contents, license):
        noLicenseRe = re.match("^\w", contents, re.MULTILINE | re.DOTALL)
        if (noLicenseRe):
            return license + contents
        licenseRe = re.compile("^(/\*.*\*/\s*)", re.MULTILINE | re.DOTALL)
        return licenseRe.sub(license, contents, 1)
    
    def readEntireFile(self, filename):
        file = open(filename, "r")
        contents = file.read()
        file.close
        return contents

    def writeEntireFile(self, filename, contents):
        file = open(filename, "w")
        contents = file.write(contents)
        file.close


if __name__ == "__main__":
    Relicense().main()
