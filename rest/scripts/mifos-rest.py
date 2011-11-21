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


# #################################
# ### libraries' initialization ###
# #################################

urlopen = urllib2.urlopen
cj = cookielib.MozillaCookieJar()
Request = urllib2.Request
opener = urllib2.build_opener(urllib2.HTTPCookieProcessor(cj))
urllib2.install_opener(opener)


# #####################
# ### configuration ###
# #####################

base_url = 'http://localhost:8080/mifos'
def_username = 'mifos'
def_password = 'testmifos'


# #################
# ### constants ###
# #################

headers =  {'User-agent' : 'Mifos REST Client', 'Content-Type':'application/x-www-form-urlencoded'}


# ###################################
# ### methods related to REST API ###
# ###################################

def login():
    # read username from the user
    username = raw_input("Enter username : ")
    if username == '':
        username = def_username

    # read password from the user
    password = getpass.getpass()
    if password == '':
        password = def_password

    # encode username/password in the request
    url = base_url + '/j_spring_security_check'
    data = urllib.urlencode({'j_username' : username, 'j_password' : password, 'spring-security-redirect' : '/status.json'})
    req = Request(url, data, headers)

    # open json page, read the response and verify the result
    handle = urlopen(req)
    responseText = handle.read()
    response = json.loads(responseText)
    if response['status'] != 'Success':
        raise Exception('Invalid username/password')

def getClientDetails():

    # read client global number from the user
    clientGlobalNumber = raw_input("Enter Client Number (eg. 0002-000000003): ")

    # encode client global number in the request
    url = base_url + '/client/num-'+ clientGlobalNumber +'.json'
    data = None
    req = Request(url, data, headers)

    # add the authorization cookie (automatically created during login method)
    cj.add_cookie_header(req)

    # open json page, read the response and return it
    handle = urlopen(req)
    responseText = handle.read()
    return clientGlobalNumber, json.loads(responseText)

def loanRepayment():

    # get client details
    clientGlobalNumber, client = getClientDetails()

    # print client information
    print ' [Loan Repayment] '
    print ' Client Number: ' + clientGlobalNumber
    print ' Client Name: ' + client['clientDisplay']['displayName']
    print ' Outstanding Loans'

    # create the map of client loan accounts
    i = 1
    param = []
    for l in client['loanAccountsInUse']:
        print ' '+str(i) +'. '+ l['prdOfferingName']+': '+l['outstandingBalance']
        i = i+1
        param.insert(i, l['globalAccountNum'])

    # read loan number from the user
    loan = raw_input('Select loan to repay: ')

    # read repayment amount from the user
    amount = raw_input('Enter amount: ')

    # encode loan global number in the request
    url = base_url + '/account/loan/repay/num-'+param[int(loan) - 1]+'.json'
    data = urllib.urlencode({'amount' : amount, 'client' : clientGlobalNumber})
    req = Request(url, data, headers) 

    # add the authorization cookie (automatically created during login method)
    cj.add_cookie_header(req)

    # open json page, read the response and print confirmation
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

def savingsPayment(depositTrxn):

    # get client details
    clientGlobalNumber, client = getClientDetails()

    # print client information
    if depositTrxn:
        print ' [Savings Deposit] '
    else:
        print ' [Savings Withdrawal] '
    print ' Client Number: '+clientGlobalNumber
    print ' Client Name: ' + client['clientDisplay']['displayName']
    print ' Investment/Savings Accounts'

    # create the map of client savings accounts
    i = 1
    param = []
    for s in client['savingsAccountsInUse']:
        print ' '+str(i) +'. '+ s['prdOfferingName']+': '+s['savingsBalance']
        i = i+1
        param.insert(i, s['globalAccountNum'])

    # read savings number from the user
    savings = raw_input('Select account to credit: ')

    # read transaction amount from the user
    amount = raw_input('Enter amount: ')

    # encode savings global number in the request
    if depositTrxn:
        url = base_url + '/account/savings/deposit/num-'+param[int(savings) - 1]+'.json'
    else:
        url = base_url + '/account/savings/withdraw/num-'+param[int(savings) - 1]+'.json'
    data = urllib.urlencode({'amount' : amount, 'client' : clientGlobalNumber})
    req = Request(url, data, headers) 

    # add the authorization cookie (automatically created during login method)
    cj.add_cookie_header(req)

    # open json page, read the response and print confirmation
    handle = urlopen(req)
    responseText = handle.read()
    response = json.loads(responseText)
    print ''
    if depositTrxn:
        print '---Savings Deposit Receipt---'
    else:
        print '---Savings Withdrawal Receipt---'
    print 'Client Name: '+ response['clientName']
    print 'Client Number: '+ response['clientNumber']
    print 'Loan Account: '+response['savingsDisplayName']
    print 'Loan Officer: '+response['paymentMadeBy']
    print 'Payment Amount: '+response['paymentAmount']
    print 'Payment Date: '+response['paymentDate']
    print 'Payment Time: '+response['paymentTime']
    print 'Old account balance: '+response['balanceBeforePayment']
    print 'New account balance: '+response['balanceAfterPayment']


# #######################################
# ### methods related to local client ###
# #######################################

def printMenuAndSelectOperation():
    print '''              
            [Main Menu] 
             1. Loan Repayment
             2. Savings Deposit
             3. Savings Withdrawal
             '''
    return raw_input("Select operation: ")


# ########################
# ### application code ###
# ########################

login()

operation = printMenuAndSelectOperation()
if operation == '1':
    loanRepayment()
elif operation == '2':
    savingsPayment(True)
elif operation == '3':
    savingsPayment(False)
else:
    raise Exception('Unsupported operation')
