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
                 currentState="createLoanAccount.flowState.reviewInstallments" 
                 states=["createLoanAccount.flowState.selectCustomer", 
                         "createLoanAccount.flowState.enterAccountInfo",
                         "createLoanAccount.flowState.reviewInstallments", 
                         "createLoanAccount.flowState.reviewAndSubmit"]]

[#if loanProductReferenceData.variableInstallmentsAllowed]
<script type="text/javascript" src="pages/js/jquery/jquery-ui.min.js"></script>
<script type="text/javascript" src="pages/js/jquery/jquery.datePicker.configuration.js"></script>
<script type="text/javascript" src="pages/js/jquery/jquery.ui.datepicker.min.js"></script>
<script type="text/javascript" src="pages/js/jquery/jquery-ui-i18n.js"></script>
<script type="text/javascript" src="pages/framework/js/CommonUtilities.js"></script>
<script>
$(document).ready(function() {

    $(":regex(id, .*\\.[0-9]+)").datepicker({
        dateFormat: 'dd-M-yy',
        showOn: "button",
        buttonImage: "pages/framework/images/mainbox/calendaricon.gif",
		buttonImageOnly: true
    });
  }
);

$(function() {
	$.datepicker.setDefaults($.datepicker.regional[""]);
});
</script>
[/#if]
                         
<h1>[@spring.message "createLoanAccount.wizard.title" /] - <span class="standout">[@spring.message "createLoanAccount.reviewInstallments.pageSubtitle" /]</span></h1>
<p>[@spring.message "createLoanAccount.reviewInstallments.instructions" /]</p>
<br/>

[@form.errors "loanScheduleFormBean.*"/]
[@form.errors "cashFlowSummaryFormBean.*"/]

<div class="product-summary">
	<div class="row">
	    <div class="attribute"><span class="standout">[@spring.message "selectProduct.accountOwnerName"/]</span></div>
	    <div class="value">${loanScheduleReferenceData.accountOwner}</div>
	</div>
	<div class="row">
	    <div class="attribute"><span class="standout">[@spring.message "reviewInstallments.loanAmount"/]</span></div>
	    <div class="value">${loanScheduleReferenceData.loanAmount?string.currency}</div>
	</div>
	<div class="row">
	    <div class="attribute"><span class="standout">[@spring.message "reviewInstallments.disbursmentDate"/]</span></div>
	    <div class="value">${loanScheduleReferenceData.disbursementDate?date?string.medium}</div>
	</div>
[#if loanProductReferenceData.variableInstallmentsAllowed]
	<div class="row">
	    <div class="attribute"><span class="standout">[@spring.message "productSummary.variabeInstallments.minInstallmentAmount"/]</span></div>
	    [#if loanProductReferenceData.minInstallmentAmount == 0.0]
	    <div class="value">[@spring.message "productSummary.variabeInstallments.minInstallmentAmount.notapplicable"/]</div>
	    [#else]
	    <div class="value">${loanProductReferenceData.minInstallmentAmount?string.number}</div>
	    [/#if]
	</div>
	<div class="row">
	    <div class="attribute"><span class="standout">[@spring.message "productSummary.variableInstallmentsAllowed"/]</span></div>
	    <div class="value">[@spring.message "boolean.yes"/]</div>
	</div>
	<div class="row">
	    <div class="attribute"><span class="standout">[@spring.message "productSummary.variabeInstallments.minGap"/]</span></div>
	    <div class="value">${loanProductReferenceData.minGapInDays?string.number}</div>
	</div>
	<div class="row">
	    <div class="attribute"><span class="standout">[@spring.message "productSummary.variabeInstallments.maxGap"/]</span></div>
	    <div class="value">${loanProductReferenceData.maxGapInDays?string.number}</div>
	</div>
[/#if]
</div>
<div class="product-summary">
	[#assign index = 0]
	[#list loanProductReferenceData.defaultFees as defaultFee]
	    <div class="row">
	        <div class="attribute"><span class="standout">${defaultFee.name}</span></div>
	        <div class="value">
	        	[#if defaultFee.rateBasedFee]
	        		[#assign rateAsFraction = defaultFee.rate/100]
	        		${rateAsFraction?string.percent}
	        	[#else]
	        		${defaultFee.amountAsNumber?string.currency}
	        	[/#if]

				[#if defaultFee.rateBasedFee]
		        	${defaultFee.feeFormula.name}
		       	[/#if]
		       		        	
				[#if defaultFee.feeFrequencyType == "Periodic"]
		        	<span class="standout">[@spring.message "createLoanAccount.periodicity"/]:</span> ${defaultFee.feeFrequency.recurAfterPeriod}
		        	[#if defaultFee.feeFrequency.weekly]
		        		<span>[@spring.message "createLoanAccount.weeks"/]</span>
		       		[#else]
		        		<span>[@spring.message "createLoanAccount.months"/]</span>
		       		[/#if]
		       	[#else]
		       		<span class="standout">[@spring.message "createLoanAccount.periodicity"/]:</span> ${defaultFee.feeFrequencyType}
		       	[/#if]
	       	</div>
	    <!-- end of row -->
	    </div>
 		[#assign index = index + 1]	    	
    [/#list]
<!-- end of product summary -->
</div>
<br/>

[#if loanProductReferenceData.variableInstallmentsAllowed]
<form action="${flowExecutionUrl}" method="post" class="two-columns">
[/#if]
<h1><span class="standout">[@spring.message "reviewInstallments.heading" /]</span></h1>
<table>
	<tbody>
		<tr>
			<th>[@spring.message "reviewInstallments.installmentHeading" /]</th>
			<th>[@spring.message "reviewInstallments.dueDateHeading" /]</th>
			<th>[@spring.message "reviewInstallments.principalHeading" /]</th>
			<th>[@spring.message "reviewInstallments.interestHeading" /]</th>
			<th>[@spring.message "reviewInstallments.feesHeading" /]</th>
			<th>[@spring.message "reviewInstallments.totalHeading" /]</th>
		</tr>
		[#assign ind = 0]
		[#list loanScheduleReferenceData.installments as row]
		<tr>
			<td>${row.installmentNumber?string.number}</td>
			[#if loanProductReferenceData.variableInstallmentsAllowed]
				[#if loanProductReferenceData.compareCashflowEnabled]
					[@spring.bind "cashFlowSummaryFormBean.installments[${ind}]"/]
					<td><input type="text" name="installments[${ind}]" size="10" value="${cashFlowSummaryFormBean.installments[ind]?date?string.medium}" id="installment.dueDate.${ind}" class="date-pick" /></td>
				[#else]
					[@spring.bind "loanScheduleFormBean.installments[${ind}]"/]
					<td><input type="text" name="installments[${ind}]" size="10" value="${loanScheduleFormBean.installments[ind]?date?string.medium}" id="installment.dueDate.${ind}" class="date-pick" /></td>
				[/#if]
			[#else]
				[#if loanProductReferenceData.compareCashflowEnabled]
					[@spring.bind "cashFlowSummaryFormBean.installments[${ind}]"/]
					<td><input type="text" name="installments[${ind}]" size="10" value="${cashFlowSummaryFormBean.installments[ind]?date?string.medium}" id="installment.dueDate.${ind}" disabled=disabled /></td>
				[#else]
					[@spring.bind "loanScheduleFormBean.installments[${ind}]"/]
					<td><input type="text" name="installments[${ind}]" size="10" value="${loanScheduleFormBean.installments[ind]?date?string.medium}" id="installment.dueDate.${ind}" disabled=disabled /></td>
				[/#if]
			[/#if]
			<td>${row.principal?string.currency}</td>
			<td>${row.interest?string.currency}</td>
			<td>${row.fees?string.currency}</td>
			<td>${row.total?string.currency}</td>
		</tr>
		[#assign ind = ind + 1]
		[/#list]
	</tbody>
</table>

[#if cashflowSummaryDetails??]
<br />
<h1><span class="standout">[@spring.message "cashflow.summary.heading" /]</span></h1>
<table>
	<tbody>
		<tr>
			<th>[@spring.message "cashflow.summary.column.months" /]</th>
			<th>[@spring.message "cashflow.summary.column.cumulative" /]</th>
			<th>[@spring.message "cashflow.summary.column.installmentpermonth" /]</th>
			<th>[@spring.message "cashflow.summary.column.installmentpermonthpercentage" /]</th>
			<th>[@spring.message "cashflow.summary.column.notes" /]</th>
		</tr>
		[#list cashflowSummaryDetails as row]
		<tr>
			<td>${row.month} ${row.year}</td>
			<td>${row.cumulativeCashFlow}</td>
			<td>${row.diffCumulativeCashflowAndInstallment}</td>
			<td>${row.diffCumulativeCashflowAndInstallmentPercent}</td>
			<td>${row.notes}</td>
		</tr>
		[/#list]
	</tbody>
</table>
[/#if]

[#if loanProductReferenceData.variableInstallmentsAllowed]
	<div class="row webflow-controls">
		<input type="submit" id="createloanaccount.validate" class="submit" name="_eventId_validate" value=[@spring.message "widget.form.buttonLabel.validate" /] />
		<input type="submit" id="continuecreateloanaccount.button.preview" class="submit" style="margin-left: 1em;" name="_eventId_preview" value=[@spring.message "widget.form.buttonLabel.preview" /] />
        [@form.cancelButton label="widget.form.buttonLabel.cancel" webflowEvent="cancel" /]
    </div>
</form>
[#else]
<form action="${flowExecutionUrl}" method="post" class="two-columns">
<div class="row webflow-controls">
        [@form.submitButton label="widget.form.buttonLabel.preview" id="continuecreateloanaccount.button.preview" webflowEvent="preview" /]
        [@form.cancelButton label="widget.form.buttonLabel.cancel" webflowEvent="cancel" /]
    </div>
</form>
[/#if]
	
<br/>
[/@layout.webflow]