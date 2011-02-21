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
                 currentState="createLoanAccount.flowState.enterAccountInfo" 
                 states=["createLoanAccount.flowState.selectCustomer", 
                         "createLoanAccount.flowState.enterAccountInfo",
                         "createLoanAccount.flowState.reviewInstallments", 
                         "createLoanAccount.flowState.reviewAndSubmit"]]
                         
<span id="page.id" title="continuecreatesavingsaccount"></span>

<h1>[@spring.message "createLoanAccount.wizard.title" /] - <span class="standout">[@spring.message "createLoanAccount.selectProduct.pageSubtitle" /]</span></h1>
<p>[@spring.message "createLoanAccount.selectProduct.instructions" /]</p>
<p><span>*</span>[@spring.message "requiredFieldsInstructions" /]</p>
<br/>

<p><span class="standout">[@spring.message "selectProduct.accountOwnerName" /]</span> ${loanProductReferenceData.customerDetailDto.displayName}</p>
<br/>

[@form.errors "loanAccountFormBean.*"/]
<form action="${flowExecutionUrl}&_eventId=newProductSelected" method="post" class="two-columns">
    <fieldset>
    <div class="row">
        [@form.label "productId" true][@spring.message "selectProduct.selectProductLabel" /][/@form.label]
        [@spring.formSingleSelect "loanAccountFormBean.productId" loanProductReferenceData.productOptions /]
    </div>
    </fieldset>
</form>

<p><span class="standout">[@spring.message "createLoanAccount.enterAccountInfo.productSummary.header" /]</span></p>
<div class="product-summary">
    <div class="row">
        <div class="attribute">[@spring.message "productSummary.description"/]</div>
        <div class="value">${loanProductReferenceData.productDto.description}</div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "productSummary.interestRateType"/]</div>
        <div class="value">${loanProductReferenceData.interestRateType}</div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "productSummary.freqOfInstallments"/]</div>
        <div class="value">${loanProductReferenceData.loanOfferingMeetingDetail.meetingDetailsDto.every}&nbsp;${loanProductReferenceData.loanOfferingMeetingDetail.meetingDetailsDto.recurrenceName}</div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "productSummary.principalDueOnLastInstallment"/]</div>
        <div class="value">
        [#if loanProductReferenceData.principalDueOnLastInstallment]
        	[@spring.message "boolean.yes"/]
        [#else]
        	[@spring.message "boolean.no"/]
        [/#if]
        </div>
    </div>
</div>
<br/>
<br/>

<p><span class="standout">[@spring.message "createLoanAccount.enterAccountInfo.accountDetail.header" /]</span></p>
<form action="${flowExecutionUrl}" method="post" class="two-columns">
    <fieldset>
    <div class="row">
        [@form.label "loanAccountFormBean.amount" true /][@spring.message "createLoanAccount.amount"/]
        [@form.input path="loanAccountFormBean.amount" /]
        <span>([@spring.message "createLoanAccount.allowedAmount"/])</span>
    </div>
    <div class="row">
        [@form.label "loanAccountFormBean.interestRate" true /][@spring.message "createLoanAccount.interestRate"/]
        [@form.input path="loanAccountFormBean.interestRate" /]
        <span>([@spring.message "createLoanAccount.allowedInterestRate"/])</span>
    </div>
    <div class="row">
        [@form.label "loanAccountFormBean.numberOfInstallments" true /][@spring.message "createLoanAccount.numberOfInstallments"/]
        [@form.input path="loanAccountFormBean.numberOfInstallments" /]
        <span>([@spring.message "createLoanAccount.allowedNumberOfInstallments"/])</span>
    </div>
    </fieldset>
    <div class="row webflow-controls">
        [@form.submitButton label="widget.form.buttonLabel.continue" id="continuecreateloanaccount.button.preview" webflowEvent="detailsEntered" /]
        [@form.cancelButton label="widget.form.buttonLabel.cancel" webflowEvent="cancel" /]
    </div>
</form>

<script type="text/javascript">
$(document).ready(function() {
    $('select').change(function(e) {
        $(this).closest('form').submit();
    });
});
</script>

[/@layout.webflow]