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
        [@form.label "amount" true /][@spring.message "createLoanAccount.amount"/]
        [@form.input path="loanAccountFormBean.amount"  id="amount" /]
        <span>([@spring.message "createLoanAccount.allowedAmount"/])</span>
    </div>
    <div class="row">
        [@form.label "interestRate" true /][@spring.message "createLoanAccount.interestRate"/]
        [@form.input path="loanAccountFormBean.interestRate" id="interestRate" /]
        <span>([@spring.message "createLoanAccount.allowedInterestRate"/])</span>
    </div>
    <div class="row">
        [@form.label "numberOfInstallments" true /][@spring.message "createLoanAccount.numberOfInstallments"/]
        [@form.input path="loanAccountFormBean.numberOfInstallments" id="numberOfInstallments" /]
        <span>([@spring.message "createLoanAccount.allowedNumberOfInstallments"/])</span>
    </div>
    <div class="row">
        [@form.label "disbursaldatedd" true /][@spring.message "createLoanAccount.disbursalDate"/]
        [@form.input path="loanAccountFormBean.disbursalDateDay" id="disbursaldatedd" attributes="size=1 maxlength=2" /]<span>[@spring.message "datefield.dd"/]</span>
        [@form.input path="loanAccountFormBean.disbursalDateMonth" id="disbursaldatemm" attributes="size=1 maxlength=2" /]<span>[@spring.message "datefield.mm"/]</span>
        [@form.input path="loanAccountFormBean.disbursalDateYear" id="disbursaldateyyyy" attributes="size=3 maxlength=4" /]<span>[@spring.message "datefield.yyyy"/]</span>
    </div>
    <div class="row">
        [@form.label "graceduration" true /][@spring.message "createLoanAccount.graceDuration"/]
        [@form.input path="loanAccountFormBean.graceDuration" id="graceduration" /]
        <span>[@spring.message "createLoanAccount.allowedGraceInInstallments"/]</span>
    </div>
    <div class="row">
        [@form.label "fundId" false][@spring.message "createLoanAccount.sourceOfFund" /][/@form.label]
        [@form.singleSelectWithPrompt path="loanAccountFormBean.fundId" options=loanProductReferenceData.fundOptions selectPrompt="selectPrompt" /]
    </div>

    <div class="row">
        [@form.label "loanPurposeId" false][@spring.message "createLoanAccount.purposeOfLoan" /][/@form.label]
        [@form.singleSelectWithPrompt path="loanAccountFormBean.loanPurposeId" options=loanProductReferenceData.purposeOfLoanOptions selectPrompt="selectPrompt" /]
    </div>
    
    <div class="row">
        [@form.label "collateralTypeId" false][@spring.message "createLoanAccount.collateralType" /][/@form.label]
        [@form.singleSelectWithPrompt path="loanAccountFormBean.collateralTypeId" options=loanProductReferenceData.collateralOptions selectPrompt="selectPrompt" /]
    </div>
    
    <div class="row">
        [@form.label "collateralNotes" false][@spring.message "createLoanAccount.collateralNotes" /][/@form.label]
        [@spring.bind "loanAccountFormBean.collateralNotes" /]
        <textarea name="${spring.status.expression}" rows="4" cols="50" maxlength="200">${spring.status.value?if_exists}</textarea>
    </div>
    
    <div class="row">
        [@form.label "externalId" false /][@spring.message "createLoanAccount.externalId"/]
        [@form.input path="loanAccountFormBean.externalId" id="externalId" /]
    </div>
    
    <p><span class="standout">[@spring.message "createLoanAccount.enterAccountInfo.defaultfees.header" /]</span></p>
    
    <br />
    <p><span class="standout">[@spring.message "createLoanAccount.enterAccountInfo.additionalfees.header" /]</span></p>
    

<!-- additional fees -->    
    [@form.label "selectedFeeId[1]" false][@spring.message "createLoanAccount.feeType" /][/@form.label]
    [@form.singleSelectWithPrompt path="loanAccountFormBean.selectedFeeId[0]" options=loanProductReferenceData.additionalFeeOptions selectPrompt="selectPrompt" /]
    
    [@form.label "selectedFeeId0Amount" false][@spring.message "createLoanAccount.feeAmount" /][/@form.label]
    [@form.input path="loanAccountFormBean.selectedFeeAmount[0]" id="selectedFeeId0Amount" /]

	[@form.label "selectedFeeId[1]" false][@spring.message "createLoanAccount.feeType" /][/@form.label]
    [@form.singleSelectWithPrompt path="loanAccountFormBean.selectedFeeId[1]" options=loanProductReferenceData.additionalFeeOptions selectPrompt="selectPrompt" /]
    
    [@form.label "selectedFeeId1Amount" false][@spring.message "createLoanAccount.feeAmount" /][/@form.label]
    [@form.input path="loanAccountFormBean.selectedFeeAmount[1]" id="selectedFeeId1Amount" /]
    
    [@form.label "selectedFeeId[2]" false][@spring.message "createLoanAccount.feeType" /][/@form.label]
    [@form.singleSelectWithPrompt path="loanAccountFormBean.selectedFeeId[2]" options=loanProductReferenceData.additionalFeeOptions selectPrompt="selectPrompt" /]
    
    [@form.label "selectedFeeId2Amount" false][@spring.message "createLoanAccount.feeAmount" /][/@form.label]
    [@form.input path="loanAccountFormBean.selectedFeeAmount[2]" id="selectedFeeId2Amount" /]

    </fieldset>
    <div class="row webflow-controls">
        [@form.submitButton label="widget.form.buttonLabel.continue" id="continuecreateloanaccount.button.preview" webflowEvent="detailsEntered" /]
        [@form.cancelButton label="widget.form.buttonLabel.cancel" webflowEvent="cancel" /]
    </div>
</form>

<script type="text/javascript">
$(document).ready(function() {
    $('#productId').change(function(e) {
        $(this).closest('form').submit();
    });
});
</script>

[/@layout.webflow]