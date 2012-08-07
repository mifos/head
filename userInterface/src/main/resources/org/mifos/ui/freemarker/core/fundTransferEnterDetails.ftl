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
                 currentState="fundTransfer.flowState.enterDetails" 
                 states=["fundTransfer.flowState.selectCustomer", 
                         "fundTransfer.flowState.selectAccount",
                         "fundTransfer.flowState.enterDetails",
                         "fundTransfer.flowState.reviewAndSubmit" ]] 

<script type="text/javascript" src="pages/js/padDate.js"></script> 
<span id="page.id" title="fundTransferEnterDetails"></span>
[@i18n.formattingInfo /]

<h1>[@spring.message "fundTransfer.transferFunds" /] - <span class="standout">[@spring.message "fundTransfer.enterDetails" /]</span></h1>

<div class="row">
	<p>[@spring.message "fundTransfer.sourceAccount" /]: #${sourceAccount.globalAccountNum} - ${sourceAccount.prdOfferingName},&nbsp;
		[@spring.message "Savings.accountBalance" /]: ${fundTransferFormBean.sourceBalance}</p>
	<p>[@spring.message "fundTransfer.beneficiary" /]: ${beneficiary.displayName}</p>
	<p>[@spring.message "fundTransfer.targetAccount" /]: #${targetAccount.globalAccountNum} - ${targetAccount.prdOfferingName},&nbsp;
		[@spring.message "Savings.accountBalance" /]: ${fundTransferFormBean.targetBalance}</p>
</div>

<br />
<p>[@spring.message "fieldsmarkedwithanasteriskarerequired." /]</p>
<br />

[#if targetAccount.accountStateId == 18]
	<p class="red"><b>[@spring.message "fundTransfer.inactiveBeneficiaryAcc" /]</b></p>
[/#if]

[@form.errors "fundTransferFormBean.*"/]
<form action="${flowExecutionUrl}" method="post" class="two-columns">
    <fieldset>
    <div class="row">
        <label for="fundTransfer.trxnDateDD"><span class="mandatory">*</span>[@spring.message "Savings.dateOfTrxn" /]:</label>
        [@form.input path="fundTransferFormBean.trxnDateDD" id="fundTransfer.trxnDateDD" attributes="size=1 maxlength=2" /]<span>[@spring.message "datefield.dd"/]</span>
        [@form.input path="fundTransferFormBean.trxnDateMM" id="fundTransfer.trxnDateMM" attributes="size=1 maxlength=2" /]<span>[@spring.message "datefield.mm"/]</span>
        [@form.input path="fundTransferFormBean.trxnDateYY" id="fundTransfer.trxnDateYY" attributes="size=3 maxlength=4" /]<span>[@spring.message "datefield.yyyy"/]</span>
    </div>
    <div class="row">
        <label for="fundTransfer.amount"><span class="mandatory">*</span>[@spring.message "Amount" /]:</label>
        [@form.input path="fundTransferFormBean.amount" id="fundTransfer.amount" attributes="class=separatedNumber" /]
    </div>
    <div class="row">
        <label for="receiptId">[@spring.message "Savings.receiptId" /]:</label>
        [@form.input path="fundTransferFormBean.receiptId" id="receiptId" /]
    </div>
    <div class="row">
        <label for="fundTransfer.receiptDateDD">[@spring.message "Savings.receiptDate" /]:</label>
        [@form.input path="fundTransferFormBean.receiptDateDD" id="fundTransfer.receiptDateDD" attributes="size=1 maxlength=2" /]<span>[@spring.message "datefield.dd"/]</span>
        [@form.input path="fundTransferFormBean.receiptDateMM" id="fundTransfer.receiptDateMM" attributes="size=1 maxlength=2" /]<span>[@spring.message "datefield.mm"/]</span>
        [@form.input path="fundTransferFormBean.receiptDateYY" id="fundTransfer.receiptDateYY" attributes="size=3 maxlength=4" /]<span>[@spring.message "datefield.yyyy"/]</span>
    </div>
    </fieldset>
    <div class="row">
        [@form.submitButton label="widget.form.buttonLabel.submit" id="fundTransfer.enterDetails.submit" webflowEvent="detailsEntered" /]
        [@form.cancelButton label="fundTransfer.changeAccount" id="fundTransfer.enterDetails.back" webflowEvent="back" /]
        [@form.cancelButton label="widget.form.buttonLabel.cancel" webflowEvent="cancel" /]
    </div>
</form>
<br />

[/@layout.webflow] 