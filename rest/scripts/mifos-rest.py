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

import os, cookielib, urllib2, urllib, json, getpass
from time import sleep

urlopen = urllib2.urlopen
cj = cookielib.MozillaCookieJar()
Request = urllib2.Request

opener = urllib2.build_opener(urllib2.HTTPCookieProcessor(cj))
urllib2.install_opener(opener)

devMode = True
base_url = 'http://localhost:8083/mifos'

os.system('clear')

if devMode:
    username = raw_input("Enter usrname : ")
else:
    username = 'mifos'

if devMode:
    password = getpass.getpass()
else:
    password = 'testmifos'

os.system('clear')

url = base_url + '/j_spring_security_check'
data = urllib.urlencode({'j_username' : username, 'j_password' : password, 'spring-security-redirect' : '/status.json'})
headers =  {'User-agent' : 'Mifos REST Client', 'Content-Type':'application/x-www-form-urlencoded'}
req = Request(url, data, headers)
handle = urlopen(req)
print handle.info()
print handle.read()
for index, cookie in enumerate(cj):
    print index, '  :  ', cookie

os.system('clear')

print '''              
        [Main Menu] 
         1. Loan Repayment
         2. Savings Deposit
         '''

if devMode:      
    operation = raw_input("Select operation : ")
else:
    operation = '2'

os.system('clear')

if devMode:
    clientGlobalNumber = raw_input("Enter Client Number (eg. 0002-000000003): ")
else:
    clientGlobalNumber = '0002-000000003'

url = base_url + '/client/num-'+ clientGlobalNumber +'.json'
data = None
headers =  {'User-agent' : 'Mifos REST Client'}
req = Request(url, data, headers) 
cj.add_cookie_header(req)
handle = urlopen(req)
responseText = handle.read()
client = json.loads(responseText)

os.system('clear')

param = []
if operation == '1':
    print ' [Loan Repayment] '
    print ' Client Number: '+clientGlobalNumber
    print ' Client Name: '+ client['clientDisplay']['displayName']
    print ' Outstanding Loans'
    i = 1;
    for l in client['loanAccountsInUse']:
        print ' '+str(i) +'. '+ l['prdOfferingName']+': '+l['outstandingBalance']
        i= i+1
        param.insert(i, l['globalAccountNum'])
    loan = raw_input('Select loan to repay: ')
    amount = raw_input('Enter amount: ')
    url = base_url + '/account/loan/repay/num-'+param[int(loan) - 1]+'.json'
    print url
    data = urllib.urlencode({'amount' : amount, 'client' : clientGlobalNumber})
    headers =  {'User-agent' : 'Mifos REST Client'}
    req = Request(url, data, headers) 
    cj.add_cookie_header(req)
    handle = urlopen(req)
    responseText = handle.read()
    response = json.loads(responseText)
    print ''
    print '---Loan Repayment Receipt---'
    print 'Client Name: '+ response['clientName']
    print 'Client Number: '+ response['clientNumber']
    print 'Loan Account: '+response['loanDisplayName']
    print 'Loan Officer: '+response['paymentMadeBy']
    print 'Payment Amount: '+response['paymentAmount']
    print 'Payment Date: '+response['paymentDate']
    print 'Payment Time: '+response['paymentTime']
    print 'Old amount outstanding: '+response['outstandingBeforePayment']
    print 'New amount outstanding: '+response['outstandingAfterPayment']
else:
    print ' [Savings Deposit] '
    print ' Client Number: '+clientGlobalNumber
    print ' Investment/Savings Accounts'
    i = 1
    for s in client['savingsAccountsInUse']:
        print ' '+str(i) +'. '+ s['prdOfferingName']+': '+s['savingsBalance']
        i = i+1
        param.insert(i, s['globalAccountNum'])
    savings = raw_input('Select account to credit: ')
    amount = raw_input('Enter amount: ')
    url = base_url + '/account/savings/deposit/num-'+param[int(savings) - 1]+'.json'
    print url
    data = urllib.urlencode({'amount' : amount, 'client' : clientGlobalNumber})
    headers =  {'User-agent' : 'Mifos REST Client'}
    req = Request(url, data, headers) 
    cj.add_cookie_header(req)
    handle = urlopen(req)
    responseText = handle.read()
    response = json.loads(responseText)
    print ''
    print '---Savings Deposit Receipt---'
    print 'Client Name: '+ response['clientName']
    print 'Client Number: '+ response['clientNumber']
    print 'Loan Account: '+response['savingsDisplayName']
    print 'Loan Officer: '+response['paymentMadeBy']
    print 'Payment Amount: '+response['paymentAmount']
    print 'Payment Date: '+response['paymentDate']
    print 'Payment Time: '+response['paymentTime']
    print 'Old account balance: '+response['balanceBeforePayment']
    print 'New account balance: '+response['balanceAfterPayment']

