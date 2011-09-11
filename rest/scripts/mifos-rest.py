#!/usr/bin/env python
# Copyright (c) 2005-2011 Grameen Foundation USA
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

import os.path, cookielib, urllib2, urllib, json
from time import sleep

urlopen = urllib2.urlopen
cj = cookielib.MozillaCookieJar()
Request = urllib2.Request

opener = urllib2.build_opener(urllib2.HTTPCookieProcessor(cj))
urllib2.install_opener(opener)

base_url = 'http://localhost:8083/mifos'
username = 'mifos'
password = 'testmifos'

url = base_url + '/j_spring_security_check'
data = urllib.urlencode({'j_username' : username, 'j_password' : password, 'spring-security-redirect' : '/status.json'})
headers =  {'User-agent' : 'Mifos REST Client', 'Content-Type':'application/x-www-form-urlencoded'}
req = Request(url, data, headers) 
handle = urlopen(req)
print handle.info()
print handle.read()
for index, cookie in enumerate(cj):
    print index, '  :  ', cookie

clientGlobalNumber = '0002-000000014'

url = base_url + '/client/num-'+ clientGlobalNumber +'.json'
data = None
headers =  {'User-agent' : 'Mifos REST Client'}
req = Request(url, data, headers) 
cj.add_cookie_header(req)
handle = urlopen(req)
print handle.info()
responseText = handle.read()
print responseText

#clientObject = json.loads(responseText)
#print client

