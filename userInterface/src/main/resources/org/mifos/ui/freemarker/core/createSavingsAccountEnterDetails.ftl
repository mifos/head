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
                 currentState="createSavingsAccount.flowState.enterAccountInfo" 
                 states=["createSavingsAccount.flowState.selectCustomer", 
                         "createSavingsAccount.flowState.enterAccountInfo", 
                         "createSavingsAccount.flowState.reviewAndSubmit"]] 

<span id="page.id" title="continuecreatesavingsaccount"></span>
[@i18n.formattingInfo /]

<h1>[@spring.message "createSavingsAccount.enterAccountInfo.pageTitle" /] - <span class="standout">[@spring.message "createSavingsAccount.enterAccountInfo.pageSubtitle" /]</span></h1>
<p>[@spring.message "createSavingsAccount.enterAccountInfo.instructions" /]</p>
<p>[@spring.message "createSavingsAccount.enterAccountInfo.requiredFieldsInstructions" /]</p>
<br/>

<p><span class="standout">[@spring.message "createSavingsAccount.enterAccountInfo.accountOwnerName" /]</span> ${savingsAccountFormBean.customer.displayName}</p>
<br/>

[@form.errors "savingsAccountFormBean.*"/]
<form action="${flowExecutionUrl}&_eventId=newProductSelected" method="post" class="two-columns">
    <fieldset>
    <div class="row">
        [@form.label "productId" true][@spring.message "createSavingsAccount.enterAccountInfo.selectSavingsProduct" /][/@form.label]
        [@form.singleSelectWithPrompt path="savingsAccountFormBean.productId" options=savingsAccountFormBean.productOfferingOptions selectPrompt="" /]
    </div>
    </fieldset>
</form>

<p><b>[@spring.message "createSavingsAccount.enterAccountInfo.productSummary.header" /]</b></p>
<div class="product-summary">
    <div class="row">
        <div class="attribute">[@spring.message "createSavingsAccount.productSummary.description"/]</div>
        <div class="value">${savingsAccountFormBean.product.savingsProductDetails.productDetails.description}</div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "createSavingsAccount.productSummary.typeOfDeposits"/]</div>
        <div class="value">[@lookup.fromMap savingsAccountFormBean.savingsTypes savingsAccountFormBean.product.savingsProductDetails.depositType?string /]</div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "createSavingsAccount.productSummary.maxWithdrawalAmount"/]</div>
        <div class="value">${savingsAccountFormBean.product.savingsProductDetails.maxWithdrawal?string.number}</div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "createSavingsAccount.productSummary.balanceForInterestCalculation"/]</div>
        <div class="value">[@lookup.fromList savingsAccountFormBean.product.interestCalcTypeOptions savingsAccountFormBean.product.savingsProductDetails.interestCalculationType?string /]</div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "createSavingsAccount.productSummary.periodForInterestCalculation"/]</div>
        <div class="value">
            ${savingsAccountFormBean.product.savingsProductDetails.interestCalculationFrequency}
            [@lookup.fromMap savingsAccountFormBean.recurrenceFrequencies savingsAccountFormBean.product.savingsProductDetails.interestCalculationFrequencyPeriod?string /]
        </div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "createSavingsAccount.productSummary.interestPostingFrequency"/]</div>
        <div class="value">
            ${savingsAccountFormBean.product.savingsProductDetails.interestPostingFrequency}
            [#if savingsAccountFormBean.product.savingsProductDetails.dailyPosting]
           		[@lookup.recurringFrequencyDay /]
            [#else]
            	[@lookup.recurringFrequencyMonth /]
            [/#if]
        </div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "createSavingsAccount.productSummary.minBalanceForInterestCalculation"/]</div>
        <div class="value">
            ${savingsAccountFormBean.product.savingsProductDetails.minBalanceForInterestCalculation?string.number}
        </div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "createSavingsAccount.productSummary.interestRate"/]</div>
        <div class="value">
            ${savingsAccountFormBean.product.savingsProductDetails.interestRate?string.number} %
        </div>
    </div>
</div>
<br/>
<br/>

<p><b>[@spring.message "createSavingsAccount.enterAccountInfo.savingAccountDetail.header" /]</b></p>
<form action="${flowExecutionUrl}" method="post" class="two-columns">
    <fieldset>
    <div class="row">
        [#if savingsAccountFormBean.product.savingsProductDetails.depositType?string == "1"]
            [@form.label "mandatoryDepositAmount" true][@spring.message "createSavingsAccount.enterAccountInfo.savingAccountDetail.depositAmount.mandatory" /][/@form.label]
        [#elseif savingsAccountFormBean.product.savingsProductDetails.depositType?string == "2"]
            [@form.label "mandatoryDepositAmount" false][@spring.message "createSavingsAccount.enterAccountInfo.savingAccountDetail.depositAmount.voluntary" /][/@form.label]
        [/#if]
        [@form.input path="savingsAccountFormBean.mandatoryDepositAmount" id="continuecreatesavingsaccount.input.recommendedAmount" attributes="class=separatedNumber" /]
    </div>
    </fieldset>
    <div class="row webflow-controls">
        [@form.submitButton label="widget.form.buttonLabel.continue" id="continuecreatesavingsaccount.button.preview" webflowEvent="detailsEntered" /]
        [@form.cancelButton label="widget.form.buttonLabel.cancel" webflowEvent="cancel" /]
    </div>
</form>


<!-- TODO: put this in a file -->
<script type="text/javascript">
$(document).ready(function() {
    $('select').change(function(e) {
        $(this).closest('form').submit();
    });
});
</script>

[/@layout.webflow]
