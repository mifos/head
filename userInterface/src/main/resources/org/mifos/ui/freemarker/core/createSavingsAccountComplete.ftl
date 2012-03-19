[#ftl]
[#--
* Copyright (c) 2005-2011 Grameen Foundation USA
*  All rights reserved.
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
*  implied. See the License for the specific language governing
*  permissions and limitations under the License.
*
*  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
*  explanation of the license and how it is applied.
--]

[@layout.webflow currentTab="ClientsAndAccounts"
                 currentState="createSavingsAccount.flowState.reviewAndSubmit" 
                 states=["createSavingsAccount.flowState.selectCustomer", 
                         "createSavingsAccount.flowState.enterAccountInfo", 
                         "createSavingsAccount.flowState.reviewAndSubmit"]] 

<span id="page.id" title="createsavingsaccountconfirmation"></span>

<h1 class="success">[@spring.message "createSavingsAccount.accountCreated.successMessage" /]</h1>

[#assign args=[savingsAccountFormBean.customer.displayName] /]
<p>
<span class="standout">[@spring.message "createSavingsAccount.accountCreated.pleasenote" /] </span>
 [@spring.messageArgs "createSavingsAccount.accountCreated.accountDetails" args /]
<span class="account-number"> ${account.globalAccountNum}. </span>
 [@spring.message "createSavingsAccount.accountCreated.searchInstruction" /]</p>
<br/>

<p><a id="createsavingsaccountconfirmation.link.viewSavingsAccount" href="savingsAction.do?method=get&globalAccountNum=${account.globalAccountNum}" class="standout">[@spring.message "createSavingsAccount.accountCreated.action.viewSavingsAccount" /]</a></p>
<br/>

<div class="suggestion">[@spring.message "createSavingsAccount.accountCreated.nextSteps" /]</div>
<ul>
    <li><a href="createSavingsAccount.ftl?customerId=${savingsAccountFormBean.customer.customerId?c}">[@spring.message "createSavingsAccount.accountCreated.action.openNewSavingsAccount" /]</a></li>
    <li><a href="createLoanAccount.ftl?customerId=${savingsAccountFormBean.customer.customerId?c}">[@spring.message "createSavingsAccount.accountCreated.action.openNewLoanAccount" /]</a></li>
</ul>
 
[/@layout.webflow]