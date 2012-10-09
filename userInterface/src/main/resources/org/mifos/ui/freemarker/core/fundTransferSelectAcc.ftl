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
                 currentState="fundTransfer.flowState.selectAccount" 
                 states=["fundTransfer.flowState.selectCustomer", 
                         "fundTransfer.flowState.selectAccount",
                         "fundTransfer.flowState.enterDetails",
                         "fundTransfer.flowState.reviewAndSubmit" ]] 

<span id="page.id" title="fundTrasferCustomerSearch"></span>

<h1>[@spring.message "fundTransfer.transferFunds" /] - <span class="standout">[@spring.message "fundTransfer.selectAccount" /]</span></h1>
<br/>

<p>[@spring.message "fundTransfer.beneficiary" /]: ${beneficiary.displayName}</p>

[#if savingsAccs?has_content]
	<ul>
	[#list savingsAccs as savingsAcc]
	<li><a href="${flowExecutionUrl}&_eventId_accountSelected&targetAccGlobalNum=${savingsAcc.globalAccountNum}">
		#${savingsAcc.globalAccountNum}</a> - ${savingsAcc.prdOfferingName}, [@spring.message "Savings.accountBalance" /]: 
		${savingsAcc.savingsBalance}
	</li>
	[/#list]
	</ul>
[#else]
	<p>[@spring.message "fundTransfer.noValidAccount" /]</p>
[/#if]
<br />

[@form.errors "customerSearchFormBean.*"/]
<form action="${flowExecutionUrl}" method="post" class="two-columns">
    [@form.cancelButton label="fundTransfer.backToSelectCustomer" webflowEvent="back" /]
    [@form.cancelButton label="widget.form.buttonLabel.cancel" webflowEvent="cancel" /]
</form>
<br/>

[/@layout.webflow]