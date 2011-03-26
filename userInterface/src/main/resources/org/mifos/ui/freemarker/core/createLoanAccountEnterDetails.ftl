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
<p><span class="mandatory">*</span>[@spring.message "requiredFieldsInstructions" /]</p>
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
    [#if loanProductReferenceData.variableInstallmentsAllowed]
    <div class="row">
        <div class="attribute">[@spring.message "productSummary.variableInstallmentsAllowed"/]</div>
        <div class="value">[@spring.message "boolean.yes"/]</div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "productSummary.variabeInstallments.minGap"/]</div>
        <div class="value">${loanProductReferenceData.minGapInDays?string.number}</div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "productSummary.variabeInstallments.maxGap"/]</div>
        <div class="value">${loanProductReferenceData.maxGapInDays?string.number}</div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "productSummary.variabeInstallments.minInstallmentAmount"/]</div>
        [#if loanProductReferenceData.minInstallmentAmount == 0.0]
        <div class="value">[@spring.message "productSummary.variabeInstallments.minInstallmentAmount.notapplicable"/]</div>
        [#else]
        <div class="value">${loanProductReferenceData.minInstallmentAmount?string.number}</div>
        [/#if]
    </div>
    [/#if]
</div>
<br/>
<br/>

<p><span class="standout">[@spring.message "createLoanAccount.enterAccountInfo.accountDetail.header" /]</span></p>
<form action="${flowExecutionUrl}" method="post" class="two-columns">
    <fieldset>
    [#if loanProductReferenceData.glimApplicable]
    <script type="text/javascript">
    	function calculateTotalLoanAmount() {
    		$(document).ready(function () {
				var total = 0;
				$('.amountfield').each(function(index) {
					var fieldamount = parseFloat($(this).val());
					if (isNaN(fieldamount)) {
						fieldamount = 0;
					}
					$('.clientbox').each(function(subindex) {
						if (this.checked && index==subindex) {
							total = total + fieldamount;
						};
					});	
  				});
				
				$('#amount').val(total);
    		});
    	}
    
    	$(document).ready(function () {
			$('#selectAll').change(function() {
				 if (this.checked)
				 {
				    $('.clientbox').attr("checked",true);
				 } else {
				 	$('.clientbox').attr("checked",false);
				 }
				 calculateTotalLoanAmount();
			});
			
			$('.clientbox').click(function() {
				calculateTotalLoanAmount();
			});
			
			$('.amountfield').change(function() {
				calculateTotalLoanAmount();
			});
    	});
    </script>
    <table border="1">
    	<tr>
    		<td><input type="checkbox" name="selectAll" id="selectAll" /></td>
    		<td>[@spring.message "selectProduct.accountOwnerName" /]</td>
    		<td>&nbsp;</td>
    		<td><span class="mandatory">*</span>[@spring.message "createLoanAccount.amount"/]</td>
    		<td>&nbsp;</td>
    		<td>[#if loanAccountFormBean.purposeOfLoanMandatory]<span class="mandatory">*</span>[/#if][@spring.message "createLoanAccount.purposeOfLoan" /]</td>
    	</tr>
    	[#assign index = 0]
    	[#list loanProductReferenceData.clientDetails as clientdata]
    	<tr>
    		<td>
    			[@spring.formCheckbox "loanAccountFormBean.clientSelectForGroup[${index}]" "class='clientbox'"/]
    		</td>
    		<td>
    			<div><span class="standout">Client:</span>${clientdata.clientName}</div>
    			<div><span class="standout">Client Id</span>:${clientdata.clientId}</div>
    			[@spring.formHiddenInput "loanAccountFormBean.clientGlobalId[${index}]" /]
    		</td>
    		<td>&nbsp;</td>
    		<td>[@form.input path="loanAccountFormBean.clientAmount[${index}]"  id="clientAmount[${index}]" attributes="class='amountfield'"/]</td>
    		<td>&nbsp;</td>
    		<td>[@form.singleSelectWithPrompt path="loanAccountFormBean.clientLoanPurposeId[${index}]" options=loanProductReferenceData.purposeOfLoanOptions selectPrompt="selectPrompt" attributes="class=trigger"/]</td>
    	</tr>
    	[#assign index = index + 1]
    	[/#list]
    	<tr>
    		<td>&nbsp;</td>
    		<td>&nbsp;</td>
    		<td>Total amount:</td>
    		<td>[@form.input path="loanAccountFormBean.amount"  id="amount" /]</td>
    		<td><span>([@spring.message "createLoanAccount.allowedAmount"/] ${loanProductReferenceData.minLoanAmount?string.number} - ${loanProductReferenceData.maxLoanAmount?string.number})</span></td>
    		<td>&nbsp;</td>
    	</tr>
    </table>
    [#else]
    <div class="row">
        [@form.label "amount" true ][@spring.message "createLoanAccount.amount"/][/@form.label]
        [@form.input path="loanAccountFormBean.amount"  id="amount" /]
        <span>([@spring.message "createLoanAccount.allowedAmount"/] ${loanProductReferenceData.minLoanAmount?string.number} - ${loanProductReferenceData.maxLoanAmount?string.number})</span>
    </div>
    [/#if]
    <div class="row">
        [@form.label "interestRate" true ][@spring.message "createLoanAccount.interestRate"/][/@form.label]
        [@form.input path="loanAccountFormBean.interestRate" id="interestRate" /]
        <span>([@spring.message "createLoanAccount.allowedInterestRate"/] ${loanProductReferenceData.minInterestRate?string.number} - ${loanProductReferenceData.maxInterestRate?string.number} %)</span>
    </div>
    <div class="row">
        [@form.label "numberOfInstallments" true ][@spring.message "createLoanAccount.numberOfInstallments"/][/@form.label]
        [@form.input path="loanAccountFormBean.numberOfInstallments" id="numberOfInstallments" /]
        <span>([@spring.message "createLoanAccount.allowedNumberOfInstallments"/] ${loanProductReferenceData.minNumberOfInstallments?string.number} - ${loanProductReferenceData.maxNumberOfInstallments?string.number})</span>
    </div>
    <div class="row">
        [@form.label "disbursaldatedd" true ][@spring.message "createLoanAccount.disbursalDate"/][/@form.label]
        [@form.input path="loanAccountFormBean.disbursalDateDay" id="disbursaldatedd" attributes="size=1 maxlength=2" /]<span>[@spring.message "datefield.dd"/]</span>
        [@form.input path="loanAccountFormBean.disbursalDateMonth" id="disbursaldatemm" attributes="size=1 maxlength=2" /]<span>[@spring.message "datefield.mm"/]</span>
        [@form.input path="loanAccountFormBean.disbursalDateYear" id="disbursaldateyyyy" attributes="size=3 maxlength=4" /]<span>[@spring.message "datefield.yyyy"/]</span>
    </div>
    
    [#if loanProductReferenceData.repaymentIndependentOfMeetingEnabled]
    	[@form.label "weekly.repaymentFrequency" true ][@spring.message "createLoanAccount.repaymentDay"/][/@form.label]
    	
    		[#if loanProductReferenceData.loanOfferingMeetingDetail.meetingDetailsDto.recurrenceTypeId == 1]
    		<div class="row">
	    		<input type="radio" id="weekly.repaymentFrequency" name="repaymentFrequency" checked=checked />
	    		<span>[@spring.message "manageLoanProducts.defineLoanProduct.week(s)"/]</span>
	    		<input type="radio" id="monthly.repaymentFrequency" name="repaymentFrequency" disabled=disabled/>
	    		<span>[@spring.message "manageLoanProducts.defineLoanProduct.month(s)"/]</span>
    		</div>
    		<div id="week" class="paddingLeft" id="weekDIV" >
		        <span id="weekSpecifyMessage">[@spring.message "manageLoanProducts.defineLoanProduct.ifweeks,specifythefollowing" /]</span>
		        <span id="monthSpecifyMessage" style="display:none">[@spring.message "manageLoanProducts.defineLoanProduct.ifmonths,specifythefollowing" /]</span>
		        <br />
		        [@spring.message "manageLoanProducts.defineLoanProduct.recurevery" /]
		        [@form.input path="loanAccountFormBean.repaymentRecursEvery" id="recursEvery" attributes="size=3 maxlength=2"/]
		        <span id="weekLabelMessage">[@spring.message "manageLoanProducts.defineLoanProduct.week(s)" /]</span>
		        <span id="monthLabelMessage" style="display:none">[@spring.message "manageLoanProducts.defineLoanProduct.month(s)" /]</span>
		        [@spring.formSingleSelect "loanAccountFormBean.repaymentDayOfWeek" loanProductReferenceData.daysOfTheWeekOptions /]
	    	</div>
    		[#else]
    		<div class="row">
	    		<input type="radio" id="weekly.repaymentFrequency" name="repaymentFrequency" disabled=disabled />
	    		<span>[@spring.message "manageLoanProducts.defineLoanProduct.week(s)"/]</span>
	    		<input type="radio" id="monthly.repaymentFrequency" name="repaymentFrequency" checked=checked />
	    		<span>[@spring.message "manageLoanProducts.defineLoanProduct.month(s)"/]</span>
	    	</div>
    		[/#if]
    [/#if]
    
    <div class="row">
        [@form.label "graceduration" true ][@spring.message "createLoanAccount.graceDuration"/][/@form.label]
        [@form.input path="loanAccountFormBean.graceDuration" id="graceduration" attributes="disabled"/]
        <span>[@spring.message "createLoanAccount.allowedGraceInInstallments"/]</span>
    </div>
    <div class="row">
        [@form.label "fundId" loanAccountFormBean.sourceOfFundsMandatory][@spring.message "createLoanAccount.sourceOfFund" /][/@form.label]
        [@form.singleSelectWithPrompt path="loanAccountFormBean.fundId" options=loanProductReferenceData.fundOptions selectPrompt="selectPrompt" /]
    </div>

	[#if loanProductReferenceData.glimApplicable]
	[#else]
    <div class="row">
        [@form.label "loanPurposeId" loanAccountFormBean.purposeOfLoanMandatory][@spring.message "createLoanAccount.purposeOfLoan" /][/@form.label]
        [@form.singleSelectWithPrompt path="loanAccountFormBean.loanPurposeId" options=loanProductReferenceData.purposeOfLoanOptions selectPrompt="selectPrompt" /]
    </div>
    [/#if]
    
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
        [@form.label "externalId" false ][@spring.message "createLoanAccount.externalId"/][/@form.label]
        [@form.input path="loanAccountFormBean.externalId" id="externalId" /]
    </div>
    
    <p><span class="standout">[@spring.message "createLoanAccount.enterAccountInfo.defaultfees.header" /]</span></p>
<div class="default-fees">
<style type="text/css">
.default-fees .name
{
    width: 25em;
    margin-right: 0.5em;
    text-align: right;
    float: left;
}
.default-fees .value
{
    float: left;
    width: 15%;
}	

.default-fees .details 
{
    float: left;
   	width: 25%;
}

.default-fees .removeCheckbox 
{
    float: left;
   	width: 15%;
}
</style>

    	[#assign index = 0]
    	[#list loanProductReferenceData.defaultFees as defaultFee]
	    	[#assign index = index + 1]
		    <div class="row">
		        <div class="name">${defaultFee.name}:</div>
		        [#if defaultFee.rateBasedFee]
		        <div class="value">${defaultFee.rate}</div>
		        [#else]
		        <div class="value">${defaultFee.amount}</div>
		        [/#if]
		        [#if defaultFee.feeFrequencyType == "Periodic"]
		        <div class="details">[@spring.message "createLoanAccount.periodicity"/] ${defaultFee.feeFrequency.recurAfterPeriod} 
		        	[#if defaultFee.feeFrequency.weekly]
		        		[@spring.message "createLoanAccount.weeks"/]
		       		[#else]
		        		[@spring.message "createLoanAccount.months"/]
		       		[/#if]
		        </div>
		        [#else]
		        <div class="details">[@spring.message "createLoanAccount.periodicity"/] ${defaultFee.feeFrequencyType}
		        	[#if defaultFee.rateBasedFee]
		        	<br/>[@spring.message "createLoanAccount.formula"/] ${defaultFee.feeFormula.name}
		        	[/#if]
		        </div>
		        [/#if]
		        <div class="removeCheckbox">Check to remove</div>
		     </div>	    	
	    [/#list]
</div>  
<div class="clear"/>  
    <br />
    <p><div class="standout">[@spring.message "createLoanAccount.enterAccountInfo.additionalfees.header" /]</div></p>
    

<!-- additional fees -->
<!-- FIXME: keithw - leave out fees for now - keithw -->
<!--    
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
-->
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