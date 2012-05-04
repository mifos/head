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
                 currentState="fundTransfer.flowState.reviewAndSubmit"
                 states=["fundTransfer.flowState.selectCustomer", 
                         "fundTransfer.flowState.selectAccount",
                         "fundTransfer.flowState.enterDetails",
                         "fundTransfer.flowState.reviewAndSubmit" ]] 

<span id="page.id" title="fundTrasferPreview"></span>

<h1>[@spring.message "fundTransfer.transferFunds" /] - <span class="standout">[@spring.message "accounts.reviewtransaction" /]</span></h1>
<br/>

[#if transferError??]
<div class="validationErrors">
	<ul>
		<li><b>[@spring.messageArgs transferError.msgKey transferError.msgArgs /]</b></li>
	<ul>
</div>
[/#if]

<div class="row">
	<p>[@spring.message "fundTransfer.sourceAccount" /]: #${sourceAccount.globalAccountNum} - ${sourceAccount.prdOfferingName},&nbsp;
		[@spring.message "Savings.accountBalance" /]: ${fundTransferFormBean.sourceBalance}</p>
	<p>[@spring.message "fundTransfer.beneficiary" /]: ${beneficiary.displayName}</p>
	<p>[@spring.message "fundTransfer.targetAccount" /]: #${targetAccount.globalAccountNum} - ${targetAccount.prdOfferingName},&nbsp;
		[@spring.message "Savings.accountBalance" /]: ${fundTransferFormBean.targetBalance}</p>
</div>

<br />

<div class="row">
	<p><b>[@spring.message "Savings.dateOfTrxn"/]:</b> 
		${i18n.date_formatter(fundTransferFormBean.trxnDate, "dd/MM/yyyy", Application.UserLocale.locale)}</p>
	<p><b>[@spring.message "Amount"/]:</b> ${fundTransferFormBean.amount}</p>
	<p><b>[@spring.message "Savings.receiptId"/]:</b> ${fundTransferFormBean.receiptId}</p>
	<p><b>[@spring.message "Savings.receiptDate"/]:</b> 
		${i18n.date_formatter(fundTransferFormBean.receiptDate, "dd/MM/yyyy", Application.UserLocale.locale)}</p>
</div>
<br />

<div class="row">
	<form action="${flowExecutionUrl}" method="post">
		[@form.submitButton label="accounts.edittrxn" id="fundTransfer.preview.back" webflowEvent="back" /]
	</form>
</div>
<br />

<div class="row">
	<form action="${flowExecutionUrl}" method="post" class="two-columns">
        [@form.submitButton label="widget.form.buttonLabel.submit" id="fundTransfer.preview.submit" webflowEvent="submit" /]
        [@form.cancelButton label="widget.form.buttonLabel.cancel" webflowEvent="cancel" /]
    </form>
</div>

[/@layout.webflow]