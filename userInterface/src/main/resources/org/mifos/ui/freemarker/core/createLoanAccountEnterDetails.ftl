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
                         
<span id="page.id" title="LoanCreationDetail"></span>

[#if customerSearchFormBean.redoLoanAccount]
<p class="red standout" style="margin-bottom: 5px;">[@spring.message "redoLoanAccount.wizard.highlightedNote" /]</p>
<h1>[@spring.message "redoLoanAccount.wizard.title" /] - <span class="standout">[@spring.message "createLoanAccount.selectProduct.pageSubtitle" /]</span></h1>
[#else]
<h1>[@spring.message "createLoanAccount.wizard.title" /] - <span class="standout">[@spring.message "createLoanAccount.selectProduct.pageSubtitle" /]</span></h1>
[/#if]
<p>[@spring.message "createLoanAccount.selectProduct.instructions" /]</p>
<p><span class="mandatory">*</span>[@spring.message "requiredFieldsInstructions" /]</p>
<br/>

<p><span class="standout">[@spring.message "selectProduct.accountOwnerName" /]</span> ${loanProductReferenceData.customerDetailDto.displayName}</p>
<br/>

<span id="loancreationdetails.error.message">[@form.errors "loanAccountFormBean.*"/]</span>
<form action="${flowExecutionUrl}&_eventId=newProductSelected" method="post" class="two-columns">
    <fieldset>
    <div class="row">
        [@form.label "loancreationprodofferingselect.select.loanProduct" true][@spring.message "selectProduct.selectProductLabel" /][/@form.label]
        [@form.singleSelectWithPrompt path="loanAccountFormBean.productId" options=loanProductReferenceData.productOptions id="loancreationprodofferingselect.select.loanProduct" attributes="class='selectnewproduct'" /]
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
        <div class="value">${loanProductReferenceData.minGapInDays?string.number}<span>&nbsp;[@spring.message "productSummary.variabeInstallments.days"/]</span></div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "productSummary.variabeInstallments.maxGap"/]</div>
        [#if loanProductReferenceData.maxGapInDays??]
        <div class="value">${loanProductReferenceData.maxGapInDays?string.number}<span>&nbsp;[@spring.message "productSummary.variabeInstallments.days"/]</span></div>
        [#else]
		<div class="value">[@spring.message "productSummary.variabeInstallments.minInstallmentAmount.notapplicable"/]</div>        
        [/#if]
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

[#if loanProductReferenceData.glimApplicable]
<p><span class="standout">[@spring.message "createLoanAccount.enterAccountInfo.accountDetail.glim.individualdetails.header" /]</span></p>
[#else]
<p><span class="standout">[@spring.message "createLoanAccount.enterAccountInfo.accountDetail.header" /]</span></p>
[/#if]
<form action="${flowExecutionUrl}" method="post" class="two-columns">
    <fieldset>
    [#if loanProductReferenceData.glimApplicable]
    <script type="text/javascript">
    	function calculateTotalLoanAmount() {
    		$(document).ready(function () {
    		    var decimalPlaces = ${loanAccountFormBean.digitsAfterDecimalForMonetaryAmounts};
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
  				
				$('#glimsumloanamount').val(total.toFixed(decimalPlaces));
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
    			<div><span class="standout">Client:</span><span id="GLIMLoanAccounts.clientName.${index}">${clientdata.clientName}</span></div>
    			<div><span class="standout">Client Id</span>:<span id="GLIMLoanAccounts.clientId.${index}">${clientdata.clientId}</span></div>
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
    		<td>[@form.input path="loanAccountFormBean.amount"  id="glimsumloanamount" attributes="readonly=readonly"/]</td>
    		<td><span id="createloan.allowedamounttext">([@spring.message "createLoanAccount.allowedAmount"/] ${loanProductReferenceData.minLoanAmount?string("0.#")} - ${loanProductReferenceData.maxLoanAmount?string("0.#")})</span></td>
    		<td>&nbsp;</td>
    	</tr>
    </table>
    <p><span class="standout">[@spring.message "createLoanAccount.enterAccountInfo.accountDetail.header" /]</span></p>
    [#else]
    <div class="row">
        [@form.label "amount" true ][@spring.message "createLoanAccount.amount"/][/@form.label]
        [@form.input path="loanAccountFormBean.amount"  id="loancreationdetails.input.sumLoanAmount" /]
        <span id="createloan.allowedamounttext">([@spring.message "createLoanAccount.allowedAmount"/] ${loanProductReferenceData.minLoanAmount?string("0.#")} - ${loanProductReferenceData.maxLoanAmount?string("0.#")})</span>
    </div>
    [/#if]
    
    <div class="row">
        [@form.label "interestRate" true ][@spring.message "createLoanAccount.interestRate"/][/@form.label]
        [@form.input path="loanAccountFormBean.interestRate" id="loancreationdetails.input.interestRate" /]
        <span>([@spring.message "createLoanAccount.allowedInterestRate"/] ${loanProductReferenceData.minInterestRate?string("0.#")} - ${loanProductReferenceData.maxInterestRate?string("0.#")} %)</span>
    </div>
    <div class="row">
        [@form.label "numberOfInstallments" true ][@spring.message "createLoanAccount.numberOfInstallments"/][/@form.label]
        [@form.input path="loanAccountFormBean.numberOfInstallments" id="loancreationdetails.input.numberOfInstallments" /]
        <span>([@spring.message "createLoanAccount.allowedNumberOfInstallments"/] ${loanProductReferenceData.minNumberOfInstallments?string.number} - ${loanProductReferenceData.maxNumberOfInstallments?string.number})</span>
    </div>
    <div class="row">
        [@form.label "disbursaldatedd" true ][@spring.message "createLoanAccount.disbursalDate"/][/@form.label]
        [@form.input path="loanAccountFormBean.disbursementDateDD" id="disbursementDateDD" attributes="size=1 maxlength=2" /]<span>[@spring.message "datefield.dd"/]</span>
        [@form.input path="loanAccountFormBean.disbursementDateMM" id="disbursementDateMM" attributes="size=1 maxlength=2" /]<span>[@spring.message "datefield.mm"/]</span>
        [@form.input path="loanAccountFormBean.disbursementDateYY" id="disbursementDateYY" attributes="size=3 maxlength=4" /]<span>[@spring.message "datefield.yyyy"/]</span>
    </div>
    
    [#if loanProductReferenceData.repaymentIndependentOfMeetingEnabled]
    	[@form.label "weekly.repaymentFrequency" true ][@spring.message "createLoanAccount.repaymentDay"/][/@form.label]
    	
    		[#if loanProductReferenceData.loanOfferingMeetingDetail.meetingDetailsDto.recurrenceTypeId == 1]
    		<div class="row">
	    		<input type="radio" id="loancreationdetails.input.frequencyWeeks" name="repaymentFrequency" checked=checked />
	    		<span>[@spring.message "manageLoanProducts.defineLoanProduct.week(s)"/]</span>
	    		<input type="radio" id="loancreationdetails.input.frequencyMonths" name="repaymentFrequency" disabled=disabled/>
	    		<span>[@spring.message "manageLoanProducts.defineLoanProduct.month(s)"/]</span>
		    	<div id="week" id="weekDIV" style="margin-left: 306px; margin-bottom: 5px; margin-top: 5px; border: 1px solid grey; padding-left: 4px;">
		    		<div style="margin-top: 2px; margin-bottom: 2px;">[@spring.message "manageLoanProducts.defineLoanProduct.ifweeks,specifythefollowing" /]</div>
			        [@spring.message "manageLoanProducts.defineLoanProduct.recurevery" /]
			        [@form.input path="loanAccountFormBean.repaymentRecursEvery" id="loancreationdetails.input.weekFrequency" attributes="size=3 maxlength=2"/]
			        <span id="weekLabelMessage">[@spring.message "manageLoanProducts.defineLoanProduct.week(s)" /]</span>
			        [@form.singleSelectWithPrompt path="loanAccountFormBean.repaymentDayOfWeek" options=loanProductReferenceData.daysOfTheWeekOptions id="weekDay" /]
		    	</div>
    		</div>
    		[#else]
    		<div class="row">
	    		<input type="radio" id="loancreationdetails.input.frequencyWeeks" name="repaymentFrequency" disabled=disabled />
	    		<span>[@spring.message "manageLoanProducts.defineLoanProduct.week(s)"/]</span>
	    		<input type="radio" id="loancreationdetails.input.frequencyMonths" name="repaymentFrequency" checked=checked />
	    		<span>[@spring.message "manageLoanProducts.defineLoanProduct.month(s)"/]</span>
	    		<div id="montlycontainer" style="margin-left: 306px; margin-bottom: 5px; margin-top: 5px; border: 1px solid grey; padding-left: 4px;">
			    	<div id="monthlyoption1">
			    		<div style="margin-top: 2px; margin-bottom: 2px;">[@spring.message "manageLoanProducts.defineLoanProduct.ifmonths,specifythefollowing" /]</div>
			    		[@spring.bind "loanAccountFormBean.montlyOption" /]
			    		<input type="radio" id="${spring.status.expression}0" name="montlyOption" value="dayOfMonth"
			    			[#if spring.stringStatusValue == "dayOfMonth"]
			    				checked="checked"
			    			[/#if]
			    		/>
			    		<span>[@spring.message "manageLoanProducts.defineLoanProduct.dayOfMonthLabel" /]</span>
			    		[@form.input path="loanAccountFormBean.repaymentDayOfMonth" id="loancreationdetails.input.dayOfMonth" attributes="size=3 maxlength=2"/]
			    		<span>[@spring.message "manageLoanProducts.defineLoanProduct.ofEvery" /]</span>
				        [@form.input path="loanAccountFormBean.repaymentRecursEvery" id="recursEvery" attributes="size=3 maxlength=2 disabled=disabled"/]
				        <span id="monthLabelMessage">[@spring.message "manageLoanProducts.defineLoanProduct.month(s)" /]</span>
			    	</div>
			    	<div id="monthlyoption2">
			    		[@spring.bind "loanAccountFormBean.montlyOption" /]
			    		<input type="radio" id="${spring.status.expression}1" name="montlyOption" value="weekOfMonth"
			    			[#if spring.stringStatusValue == "weekOfMonth"]
			    				checked="checked"
			    			[/#if]
			    		/>
			    		<span>[@spring.message "manageLoanProducts.defineLoanProduct.weekOfMonthLabel" /]</span>
			    		[@form.singleSelectWithPrompt path="loanAccountFormBean.repaymentWeekOfMonth" options=loanProductReferenceData.weeksOfTheMonthOptions selectPrompt="selectPrompt" id="monthRank" /]
			    		[@form.singleSelectWithPrompt path="loanAccountFormBean.repaymentDayOfWeek" options=loanProductReferenceData.daysOfTheWeekOptions selectPrompt="selectPrompt" id="monthWeek"/]
			    		<span>[@spring.message "manageLoanProducts.defineLoanProduct.ofEvery" /]</span>
				        [@form.input path="loanAccountFormBean.repaymentRecursEvery" id="recursEvery" attributes="size=3 maxlength=2 disabled=disabled"/]
				        <span id="monthLabelMessage">[@spring.message "manageLoanProducts.defineLoanProduct.month(s)" /]</span>
			    	</div>
			    </div>
	    	</div>
    		[/#if]
    [/#if]
    
    <div class="row">
        [@form.label "graceduration" true ][@spring.message "createLoanAccount.graceDuration"/][/@form.label]
        [#if loanAccountFormBean.graceDuration == 0]
	    	[@form.input path="loanAccountFormBean.graceDuration" id="loancreationdetails.input.gracePeriod" attributes="disabled"/]
	    [#else]
	    	[@form.input path="loanAccountFormBean.graceDuration" id="loancreationdetails.input.gracePeriod" /]
	   	[/#if]
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
    
    [#if loanAccountFormBean.collateralTypeAndNotesHidden]
    [#else]
    <div class="row">
        [@form.label "collateralTypeId" false][@spring.message "createLoanAccount.collateralType" /][/@form.label]
        [@form.singleSelectWithPrompt path="loanAccountFormBean.collateralTypeId" options=loanProductReferenceData.collateralOptions selectPrompt="selectPrompt" /]
    </div>
    
    <div class="row">
        [@form.label "collateralNotes" false][@spring.message "createLoanAccount.collateralNotes" /][/@form.label]
        [@spring.bind "loanAccountFormBean.collateralNotes" /]
        <textarea name="${spring.status.expression}" rows="4" cols="50" maxlength="200">${spring.status.value?if_exists}</textarea>
    </div>
    [/#if]
    
    [#if loanAccountFormBean.externalIdHidden]
    [#else]
    <div class="row">
        [@form.label "externalId" loanAccountFormBean.externalIdMandatory][@spring.message "createLoanAccount.externalId"/][/@form.label]
        [@form.input path="loanAccountFormBean.externalId" id="externalId" /]
    </div>
    [/#if]
    
    <p><span class="standout">[@spring.message "createLoanAccount.enterAccountInfo.defaultfees.header" /]</span></p>

	<div class="default-fees">
		[#assign index = 0]
    	[#list loanProductReferenceData.defaultFees as defaultFee]
		    <div class="row">
		        [@form.label "defaultFeeAmountOrRate[${index}]" false ]${defaultFee.name}:[/@form.label]
		        
		        [#if defaultFee.feeFrequency.oneTime]
		        	[@form.input path="loanAccountFormBean.defaultFeeAmountOrRate[${index}]" id="defaultFeeAmountOrRate[${index}]" /]
		        [#else]
					[@form.input path="loanAccountFormBean.defaultFeeAmountOrRate[${index}]" id="defaultFeeAmountOrRate[${index}]" attributes="disabled='disabled'"/]							        	
		        [/#if]
		        
		        <span style="margin-left: 20px;">
		        [#if defaultFee.feeFrequency.oneTime]
		        	[@spring.message "createLoanAccount.periodicity"/] ${defaultFee.feeFrequencyType}
		        [#else]
		        	[@spring.message "createLoanAccount.periodicity"/] ${defaultFee.feeFrequency.recurAfterPeriod}
		        	[#if defaultFee.feeFrequency.weekly]
		        		<span>[@spring.message "createLoanAccount.weeks"/]</span>
		       		[#else]
		        		<span>[@spring.message "createLoanAccount.months"/]</span>
		       		[/#if]
		        [/#if]
		        
		       	[#if defaultFee.rateBasedFee]
		        	<span style="position:relative; left: -116px; top: 15px">[@spring.message "createLoanAccount.formula"/] ${defaultFee.feeFormula.name}</span>
		       	[/#if]
		       	</span>
		       	<div style="position:relative; left: 650px; top: -25px; height: 2px;">[@spring.formCheckbox "loanAccountFormBean.defaultFeeSelected[${index}]"/]Check to remove</div>
		       	[@spring.formHiddenInput "loanAccountFormBean.defaultFeeId[${index}]" /]
		    </div>
     		[#assign index = index + 1]	    	
	    [/#list]
	</div>  
	<div class="clear"/>  
    <br />
    <p><div class="standout">[@spring.message "createLoanAccount.enterAccountInfo.additionalfees.header" /]</div></p>
    
    <div class="additional-fees">
	    <div class="row">
			[@form.label "selectedFeeId0" false][@spring.message "createLoanAccount.feeType" /][/@form.label]
	    	[@form.singleSelectWithPrompt path="loanAccountFormBean.selectedFeeId[0]" options=loanProductReferenceData.additionalFeeOptions selectPrompt="selectPrompt" id="selectedFeeId0" /]
	    	<span style="margin-left: 10px;">[@spring.message "createLoanAccount.feeAmount" /]</span>
	    	[@form.input path="loanAccountFormBean.selectedFeeAmount[0]" id="selectedFeeId0Amount" attributes="style='margin-left: 20px;'"/]
	    </div>
	    <div class="row">
			[@form.label "selectedFeeId1" false][@spring.message "createLoanAccount.feeType" /][/@form.label]
	    	[@form.singleSelectWithPrompt path="loanAccountFormBean.selectedFeeId[1]" options=loanProductReferenceData.additionalFeeOptions selectPrompt="selectPrompt" id="selectedFeeId1" /]
	    	<span style="margin-left: 10px;">[@spring.message "createLoanAccount.feeAmount" /]</span>
	    	[@form.input path="loanAccountFormBean.selectedFeeAmount[1]" id="selectedFeeId1Amount" attributes="style='margin-left: 20px;'"/]
	    </div>
	    <div class="row">
			[@form.label "selectedFeeId2" false][@spring.message "createLoanAccount.feeType" /][/@form.label]
	    	[@form.singleSelectWithPrompt path="loanAccountFormBean.selectedFeeId[2]" options=loanProductReferenceData.additionalFeeOptions selectPrompt="selectPrompt" id="selectedFeeId2" /]
	    	<span style="margin-left: 10px;">[@spring.message "createLoanAccount.feeAmount" /]</span>
	    	[@form.input path="loanAccountFormBean.selectedFeeAmount[2]" id="selectedFeeId2Amount" attributes="style='margin-left: 20px;'"/]
	    </div>
	</div>
    </fieldset>
    <div class="row webflow-controls">
        [@form.submitButton label="widget.form.buttonLabel.continue" id="loancreationdetails.button.continue" webflowEvent="detailsEntered" /]
        [@form.cancelButton label="widget.form.buttonLabel.cancel" webflowEvent="cancel" /]
    </div>
    
    [#list loanProductReferenceData.additionalFees as additionalFee]
    	<input type="hidden" id="hiddenFeeAmount${additionalFee.id}" value="${additionalFee.amountOrRate}" />
    [/#list]
</form>

<script type="text/javascript">
$(document).ready(function() {
    $('.selectnewproduct').change(function(e) {
        $(this).closest('form').submit();
    });
    
    $('#selectedFeeId0').change(function(e) {
          $("#selectedFeeId0 option:selected").each(function () {
          		var selectedValue = $(this).val();
                if (selectedValue == null || selectedValue == "") {
          			$('#selectedFeeId0Amount').val("");
          		} else {
	                var hiddenField = "#hiddenFeeAmount" + selectedValue;
	                var hiddenValue = $(hiddenField).val();
	                $('#selectedFeeId0Amount').val(hiddenValue);
                }
           });
    });
    
    $('#selectedFeeId1').change(function(e) {
          $("#selectedFeeId1 option:selected").each(function () {
          		var selectedValue = $(this).val();
                if (selectedValue == null || selectedValue == "") {
          			$('#selectedFeeId1Amount').val("");
          		} else {
	                var hiddenField = "#hiddenFeeAmount" + selectedValue;
	                var hiddenValue = $(hiddenField).val();
	                $('#selectedFeeId1Amount').val(hiddenValue);
                }
           });
    });
    
    $('#selectedFeeId2').change(function(e) {
          $("#selectedFeeId2 option:selected").each(function () {
          		var selectedValue = $(this).val();
                if (selectedValue == null || selectedValue == "") {
          			$('#selectedFeeId2Amount').val("");
          		} else {
	                var hiddenField = "#hiddenFeeAmount" + selectedValue;
	                var hiddenValue = $(hiddenField).val();
	                $('#selectedFeeId2Amount').val(hiddenValue);
                }
           });
    });
});
</script>

[/@layout.webflow]