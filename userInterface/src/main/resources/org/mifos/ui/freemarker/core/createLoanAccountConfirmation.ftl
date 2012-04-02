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
                 currentState="createLoanAccount.flowState.reviewAndSubmit" 
                 states=["createLoanAccount.flowState.selectCustomer", 
                         "createLoanAccount.flowState.enterAccountInfo",
                         "createLoanAccount.flowState.reviewInstallments", 
                         "createLoanAccount.flowState.reviewAndSubmit"]]

<span id="page.id" title="CreateLoanAccountConfirmation"></span>

<h1 class="success">[@spring.message "createLoanAccount.accountCreated.successMessage" /]</h1>

[#assign args=[loanCreationProductDetailsDto.customerDetailDto.displayName] /]
<p><span class="standout">[@spring.message "createLoanAccount.accountCreated.pleasenote" /] </span>
 [@spring.messageArgs "createLoanAccount.accountCreated.accountDetails" args /]
<span class="account-number"> ${loanCreationResultDto.globalAccountNum}. </span>
 [@spring.message "createLoanAccount.accountCreated.searchInstruction" /]</p>
<br/>

<p><a id="CreateLoanAccountConfirmation.link.viewLoanDetails" href="viewLoanAccountDetails.ftl?globalAccountNum=${loanCreationResultDto.globalAccountNum}" class="standout">[@spring.message "createLoanAccount.accountCreated.action.viewSavingsAccount" /]</a></p>
<br/>

<div class="suggestion">[@spring.message "createLoanAccount.accountCreated.nextSteps" /]</div>
<ul>
    <li><a href="createSavingsAccount.ftl?customerId=${loanCreationProductDetailsDto.customerDetailDto.customerId?c}">[@spring.message "createLoanAccount.accountCreated.action.openNewSavingsAccount" /]</a></li>
    <li><a href="createLoanAccount.ftl?customerId=${loanCreationProductDetailsDto.customerDetailDto.customerId?c}">[@spring.message "createLoanAccount.accountCreated.action.openNewLoanAccount" /]</a></li>
</ul>
 
[/@layout.webflow]