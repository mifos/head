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
[#import "/widgets/questionnaire.ftl" as questionnaire /]

[@layout.webflow currentTab="ClientsAndAccounts"
                 currentState="createLoanAccount.flowState.reviewAndSubmit" 
                 states=["createLoanAccount.flowState.selectCustomer", 
                         "createLoanAccount.flowState.enterAccountInfo",
                         "createLoanAccount.flowState.reviewInstallments", 
                         "createLoanAccount.flowState.reviewAndSubmit"]]
<span id="page.id" title="CreateLoanPreview"></span>

[#if customerSearchFormBean.redoLoanAccount]
<p class="red standout" style="margin-bottom: 5px;">[@spring.message "redoLoanAccount.wizard.highlightedNote" /]</p>
<h1>[@spring.message "redoLoanAccount.wizard.title" /] - <span class="standout">[@spring.message "createLoanAccount.preview.pageSubtitle" /]</span></h1>
[#else]
<h1>[@spring.message "createLoanAccount.wizard.title" /] - <span class="standout">[@spring.message "createLoanAccount.preview.pageSubtitle" /]</span></h1>
[/#if] 
<p>[@spring.message "createLoanAccount.preview.instructions" /]</p>
<br/>

<p><span class="standout">[@spring.message "selectProduct.accountOwnerName" /]</span> ${loanScheduleReferenceData.accountOwner}</p>
<br />
<h1><span class="standout">[@spring.message "createLoanAccount.preview.loanHeading" /]</span></h1>
<p><span class="standout">[@spring.message "selectProduct.selectProductLabel" /]</span> 
	[#if loanAccountFormBean.productId??]
    	[@lookup.fromNonLocalisedMap loanProductReferenceData.productOptions loanAccountFormBean.productId?string /]
    [/#if]
</p>

<p><span class="standout">[@spring.message "createLoanAccount.preview.instanceHeading" /]</span></p>
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
        <div class="attribute">[@spring.message "productSummary.gracePeriodtype"/]</div>
        [#if loanScheduleReferenceData.graceType == 1]
        <div class="value">[@spring.message "productSummary.gracePeriod.none"/]</div>
        [/#if]
        [#if loanScheduleReferenceData.graceType == 2]
        <div class="value">[@spring.message "productSummary.gracePeriod.allrepayments"/]</div>
        [/#if]
        [#if loanScheduleReferenceData.graceType == 3]
        <div class="value">[@spring.message "productSummary.gracePeriod.principalonly"/]</div>
        [/#if]
    </div>
</div>
<br />
[#if loanProductReferenceData.glimApplicable]
<table style="margin-bottom: 15px;">
		<thead>
		<tr>
			<th style="border-top: 1px solid grey;">[@spring.message "createLoanAccount.preview.glim.clientIdHeading" /]</th>
			<th style="border-top: 1px solid grey;">[@spring.message "createLoanAccount.preview.glim.clientNameHeading" /]</th>
			<th style="border-top: 1px solid grey;">[@spring.message "createLoanAccount.preview.glim.governmentIdHeading" /]</th>
			<th style="border-top: 1px solid grey;">[@spring.message "createLoanAccount.preview.glim.loanAmountHeading" /]</th>
			<th style="border-top: 1px solid grey;">[@spring.message "createLoanAccount.preview.glim.loanPurposeHeading" /]</th>
		</tr>
		</thead>
		<tbody>
		[#assign index = 0]
    	[#list loanProductReferenceData.clientDetails as clientdata]
    	[#if loanAccountFormBean.isClientSelected[index]]
    	<tr>
    		<td style="border-top: 1px solid grey;">${clientdata.clientId}</td>
    		<td style="border-top: 1px solid grey;">${clientdata.clientName}</td>
			<td style="border-top: 1px solid grey;">${clientdata.govermentId}</td>
			<td style="border-top: 1px solid grey;">${loanAccountFormBean.clientAmount[index]}</td>
			[#assign purposeId = loanAccountFormBean.clientLoanPurposeId[index]]
			<td style="border-top: 1px solid grey;">${loanProductReferenceData.getPurposeOfLoan(purposeId)}</td>
    	</tr>
    	[/#if]
    	[#assign index = index + 1]
    	[/#list]
		</tbody>
</table>
[/#if]
[#setting number_format=loanAccountFormBean.numberFormatForMonetaryAmounts]
<div class="product-summary">
    <div class="row">
        <div class="attribute">[@spring.message "createLoanAccount.amount"/]</div>
        <div class="value"><span id="createloanpreview.text.loanamount">${loanAccountFormBean.amount?string.number}</span> <span id="createloan.allowedamounttext">([@spring.message "createLoanAccount.allowedAmount"/] ${loanProductReferenceData.minLoanAmount?string.number} - ${loanProductReferenceData.maxLoanAmount?string.number})</span></div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "createLoanAccount.interestRate"/]</div>
        <div class="value">${loanAccountFormBean.interestRate?string.number} <span>([@spring.message "createLoanAccount.allowedInterestRate"/] ${loanProductReferenceData.minInterestRate?string.number} - ${loanProductReferenceData.maxInterestRate?string.number} %)</span></div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "createLoanAccount.numberOfInstallments"/]</div>
        <div class="value">${loanAccountFormBean.numberOfInstallments?string.number} <span>([@spring.message "createLoanAccount.allowedNumberOfInstallments"/] ${loanProductReferenceData.minNumberOfInstallments?string.number} - ${loanProductReferenceData.maxNumberOfInstallments?string.number})</span></div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "createLoanAccount.disbursalDate"/]</div>
        <div class="value">${loanScheduleReferenceData.disbursementDate?date?string.medium}</div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "createLoanAccount.graceDuration"/]</div>
        <div class="value">${loanAccountFormBean.graceDuration?string.number} <span>[@spring.message "createLoanAccount.allowedGraceInInstallments"/]</span></div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "createLoanAccount.sourceOfFund"/]</div>
        <div class="value">
        	[#if loanAccountFormBean.fundId??]
        		[@lookup.fromNonLocalisedMap loanProductReferenceData.fundOptions loanAccountFormBean.fundId?string /]
        	[/#if]
        </div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "createLoanAccount.purposeOfLoan"/]</div>
        <div class="value">
	        [#if loanAccountFormBean.loanPurposeId??]
	        	[@lookup.fromNonLocalisedMap loanProductReferenceData.purposeOfLoanOptions loanAccountFormBean.loanPurposeId?string /]
	        [/#if]
        </div>
    </div>
    [#if loanAccountFormBean.collateralTypeAndNotesHidden]
    [#else]
    <div class="row">
        <div class="attribute">[@spring.message "createLoanAccount.collateralType"/]</div>
        <div class="value">
	        [#if loanAccountFormBean.collateralTypeId??]
	        	[@lookup.fromNonLocalisedMap loanProductReferenceData.collateralOptions loanAccountFormBean.collateralTypeId?string /]
	        [/#if]
        </div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "createLoanAccount.collateralNotes"/]</div>
        <div class="value">${loanAccountFormBean.collateralNotes}</div>
    </div>
    [/#if]
    [#if loanAccountFormBean.externalIdHidden]
    [#else]
    <div class="row">
        <div class="attribute">[@spring.message "createLoanAccount.externalId"/]</div>
        <div class="value">${loanAccountFormBean.externalId}</div>
    </div>
    [/#if]
</div>
<br/>
<div class="product-summary">
	[#assign index = 0]
	
	[#if loanProductReferenceData.compareCashflowEnabled]
		[#list cashFlowSummaryFormBean.applicableFees as defaultFee]
		[#if index == 0]<p><span class="standout">[@spring.message "createLoanAccount.preview.chargesAppliedHeading" /]</span></p>[/#if]
		    <div class="row">
		        <div class="attribute"><span class="standout">${defaultFee.name}</span></div>
		        <div class="value">
				[#if defaultFee.rateBasedFee]
	        		[#setting number_format="#.###"]
	        		${defaultFee.rate?string.number}%
					[#setting number_format=loanAccountFormBean.numberFormatForMonetaryAmounts]	        		
	        	[#else]
	        		${defaultFee.amountAsNumber?string.number}
	        	[/#if]
	        	
				[#if defaultFee.rateBasedFee]
		        	${defaultFee.feeFormula.name}
		       	[/#if]
		       	
		       	[#if defaultFee.feeFrequency.oneTime]
		       		<span class="standout">[@spring.message "createLoanAccount.periodicity"/]</span> 
		       		<span style="margin-left: 5px;">[#if defaultFee.feeFrequency.oneTime][@spring.message "createLoanAccount.periodicity.onetime"/][/#if]</span>
		       		<span class="standout" style="margin-left: 5px;">[@spring.message "createLoanAccount.frequency"/]</span>
		       		<span style="margin-left: 5px;">
		       			[#if defaultFee.feeFrequency.paymentId == 1][@spring.message "createLoanAccount.frequency.upfront"/][/#if]
		       			[#if defaultFee.feeFrequency.paymentId == 2][@spring.message "createLoanAccount.frequency.timeofdisbursement"/][/#if]
		       			[#if defaultFee.feeFrequency.paymentId == 3][@spring.message "createLoanAccount.frequency.timeoffirstloanrepayment"/][/#if]
		       		</span>
				[#else]		       		        	
		        	<span class="standout">[@spring.message "createLoanAccount.periodicity"/]</span> ${defaultFee.feeFrequency.recurAfterPeriod}
		        	[#if defaultFee.feeFrequency.weekly]
		        		<span>[@spring.message "createLoanAccount.weeks"/]</span>
		       		[#else]
		        		<span>[@spring.message "createLoanAccount.months"/]</span>
		       		[/#if]
		       	[/#if]
		       	</div>
		    <!-- end of row -->
		    </div>
	 		[#assign index = index + 1]	    	
	    [/#list]
	[#else]
		[#list loanScheduleFormBean.applicableFees as defaultFee]
			[#if index == 0]<p><span class="standout">[@spring.message "createLoanAccount.preview.chargesAppliedHeading" /]</span></p>[/#if]
		    <div class="row">
		        <div class="attribute"><span class="standout">${defaultFee.name}</span></div>
		        <div class="value">
			    [#if defaultFee.rateBasedFee]
	        		[#setting number_format="#.###"]
	        		${defaultFee.rate?string.number}%
					[#setting number_format=loanAccountFormBean.numberFormatForMonetaryAmounts]	        		
	        	[#else]
	        		${defaultFee.amountAsNumber?string.number}
	        	[/#if]
	        	
				[#if defaultFee.rateBasedFee]
		        	${defaultFee.feeFormula.name}
		       	[/#if]
		       	
		       	[#if defaultFee.feeFrequency.oneTime]
		       		<span class="standout">[@spring.message "createLoanAccount.periodicity"/]</span> 
		       		<span style="margin-left: 5px;">[#if defaultFee.feeFrequency.oneTime][@spring.message "createLoanAccount.periodicity.onetime"/][/#if]</span>
		       		<span class="standout" style="margin-left: 5px;">[@spring.message "createLoanAccount.frequency"/]</span>
		       		<span style="margin-left: 5px;">
		       			[#if defaultFee.feeFrequency.paymentId == 1][@spring.message "createLoanAccount.frequency.upfront"/][/#if]
		       			[#if defaultFee.feeFrequency.paymentId == 2][@spring.message "createLoanAccount.frequency.timeofdisbursement"/][/#if]
		       			[#if defaultFee.feeFrequency.paymentId == 3][@spring.message "createLoanAccount.frequency.timeoffirstloanrepayment"/][/#if]
		       		</span>
				[#else]		       		        	
		        	<span class="standout">[@spring.message "createLoanAccount.periodicity"/]</span> ${defaultFee.feeFrequency.recurAfterPeriod}
		        	[#if defaultFee.feeFrequency.weekly]
		        		<span>[@spring.message "createLoanAccount.weeks"/]</span>
		       		[#else]
		        		<span>[@spring.message "createLoanAccount.months"/]</span>
		       		[/#if]
		       	[/#if]
		       	</div>
		    <!-- end of row -->
		    </div>
	 		[#assign index = index + 1]	    	
	    [/#list]
	[/#if]	
    
<!-- end of product summary -->
</div>
	[#if index > 0]
	<br/>
	<br/>
	[/#if]
<br/>
<form action="${flowExecutionUrl}" method="post">
	[@form.submitButton label="widget.form.buttonLabel.editaccountinfo" id="createloanpreview.button.edit" webflowEvent="editAccountDetails" /]
</form>
<br />

<h1><span class="standout">[@spring.message "reviewInstallments.heading" /]</span></h1>
<table style="margin-bottom: 15px;" id="installments">
		<thead>
		<tr>
			<th style="border-top: 1px solid grey; width: 100px;">[@spring.message "reviewInstallments.installmentHeading" /]</th>
			<th style="border-top: 1px solid grey; width: 100px;">[@spring.message "reviewInstallments.dueDateHeading" /]</th>
			<th style="border-top: 1px solid grey; width: 100px;">[@spring.message "reviewInstallments.redo.datePaidHeading" /]</th>
			<th style="border-top: 1px solid grey; width: 70px;">[@spring.message "reviewInstallments.principalHeading" /]</th>
			<th style="border-top: 1px solid grey; width: 70px;">[@spring.message "reviewInstallments.interestHeading" /]</th>
			<th style="border-top: 1px solid grey; width: 70px;">[@spring.message "reviewInstallments.feesHeading" /]</th>
			<th style="border-top: 1px solid grey; width: 70px;">[@spring.message "reviewInstallments.totalHeading" /]</th>
			<th style="border-top: 1px solid grey;">&nbsp;</th>
			<th colspan="4" style="border-top: 1px solid grey; text-align: center;">[@spring.message "reviewInstallments.redo.runningBalanceHeading" /]</th>
		</tr>
		<tr>
			<th colspan="8" style="border-top: 1px solid grey;">[@spring.message "reviewInstallments.redo.paidInstallmentsHeading" /]</th>
			<th style="border-top: 1px solid grey; ">[@spring.message "reviewInstallments.redo.principalHeading" /]</th>
			<th style="border-top: 1px solid grey; ">[@spring.message "reviewInstallments.redo.interestHeading" /]</th>
			<th style="border-top: 1px solid grey; ">[@spring.message "reviewInstallments.redo.feesHeading" /]</th>
			<th style="border-top: 1px solid grey; ">[@spring.message "reviewInstallments.redo.totalInstallmentHeading" /]</th>
		</tr>
		</thead>
		<tbody>
		[#if loanProductReferenceData.compareCashflowEnabled]
			[#list cashFlowSummaryFormBean.loanRepaymentPaidInstallmentsWithRunningBalance as row]
			<tr>
				<td style="border-top: 1px solid grey;">${row.installmentDetails.installmentNumber?string.number}</td>
				<td style="border-top: 1px solid grey;">${row.installmentDetails.dueDate?date?string.medium}</td>
				<td style="border-top: 1px solid grey;">${row.paymentDate?date?string.medium}</td>
				<td style="border-top: 1px solid grey;">${row.installmentDetails.principal?string.number}</td>
				<td style="border-top: 1px solid grey;">${row.installmentDetails.interest?string.number}</td>
				<td style="border-top: 1px solid grey;">${row.installmentDetails.fees?string.number}</td>
				<td style="border-top: 1px solid grey;">${row.total?string.number}</td>
				<th style="border-top: 1px solid grey;">&nbsp;</th>
				<td style="border-top: 1px solid grey;">${row.principal?string.number}</td>
				<td style="border-top: 1px solid grey;">${row.interest?string.number}</td>
				<td style="border-top: 1px solid grey;">${row.fees?string.number}</td>
				<td style="border-top: 1px solid grey;">${row.totalInstallment?string.number}</td>
			</tr>
			[/#list]
		[#else]
			[#list loanScheduleFormBean.loanRepaymentPaidInstallmentsWithRunningBalance as row]
			<tr>
				<td style="border-top: 1px solid grey;">${row.installmentDetails.installmentNumber?string.number}</td>
				<td style="border-top: 1px solid grey;">${row.installmentDetails.dueDate?date?string.medium}</td>
				<td style="border-top: 1px solid grey;">${row.paymentDate?date?string.medium}</td>
				<td style="border-top: 1px solid grey;">${row.installmentDetails.principal?string.number}</td>
				<td style="border-top: 1px solid grey;">${row.installmentDetails.interest?string.number}</td>
				<td style="border-top: 1px solid grey;">${row.installmentDetails.fees?string.number}</td>
				<td style="border-top: 1px solid grey;">${row.total?string.number}</td>
				<th style="border-top: 1px solid grey;">&nbsp;</th>
				<td style="border-top: 1px solid grey;">${row.principal?string.number}</td>
				<td style="border-top: 1px solid grey;">${row.interest?string.number}</td>
				<td style="border-top: 1px solid grey;">${row.fees?string.number}</td>
				<td style="border-top: 1px solid grey;">${row.totalInstallment?string.number}</td>
			</tr>
			[/#list]
		[/#if]
		</tbody>
</table>

[#if loanProductReferenceData.compareCashflowEnabled]
	[#if cashFlowSummaryFormBean.loanRepaymentFutureInstallments?has_content]
	<table style="margin-bottom: 15px; width: 605px;" id="futureInstallments">
			<thead>
			<tr>
				<th style="border-top: 1px solid grey; width: 100px;">[@spring.message "reviewInstallments.installmentHeading" /]</th>
				<th style="border-top: 1px solid grey; width: 100px;">[@spring.message "reviewInstallments.dueDateHeading" /]</th>
				<th style="border-top: 1px solid grey; width: 100px;">[@spring.message "reviewInstallments.redo.datePaidHeading" /]</th>
				<th style="border-top: 1px solid grey; width: 70px;">[@spring.message "reviewInstallments.principalHeading" /]</th>
				<th style="border-top: 1px solid grey; width: 70px;">[@spring.message "reviewInstallments.interestHeading" /]</th>
				<th style="border-top: 1px solid grey; width: 70px;">[@spring.message "reviewInstallments.feesHeading" /]</th>
				<th style="border-top: 1px solid grey; width: 70px;">[@spring.message "reviewInstallments.totalHeading" /]</th>
			</tr>
			<tr>
				<th colspan="7" style="border-top: 1px solid grey;">[@spring.message "reviewInstallments.redo.futureInstallmentsHeading" /]</th>
			</tr>
			</thead>
			<tbody>
			[#list cashFlowSummaryFormBean.loanRepaymentFutureInstallments as row]
			<tr>
				<td style="border-top: 1px solid grey;">${row.installmentNumber?string.number}</td>
				<td style="border-top: 1px solid grey;">${row.dueDate?date?string.medium}</td>
				<td style="border-top: 1px solid grey; width: 20px;">-</td>
				<td style="border-top: 1px solid grey;">${row.principal?string.number}</td>
				<td style="border-top: 1px solid grey;">${row.interest?string.number}</td>
				<td style="border-top: 1px solid grey;">${row.fees?string.number}</td>
				<td style="border-top: 1px solid grey;">${row.total?string.number}</td>
			</tr>
			[/#list]
			</tbody>
	</table>
	[/#if]
	[#else]
	[#if loanScheduleFormBean.loanRepaymentFutureInstallments?has_content]
	<table style="margin-bottom: 15px; width: 605px;"  id="futureInstallments">
			<thead>
			<tr>
				<th style="border-top: 1px solid grey; width: 100px;">[@spring.message "reviewInstallments.installmentHeading" /]</th>
				<th style="border-top: 1px solid grey; width: 100px;">[@spring.message "reviewInstallments.dueDateHeading" /]</th>
				<th style="border-top: 1px solid grey; width: 100px;">[@spring.message "reviewInstallments.redo.datePaidHeading" /]</th>
				<th style="border-top: 1px solid grey; width: 70px;">[@spring.message "reviewInstallments.principalHeading" /]</th>
				<th style="border-top: 1px solid grey; width: 70px;">[@spring.message "reviewInstallments.interestHeading" /]</th>
				<th style="border-top: 1px solid grey; width: 70px;">[@spring.message "reviewInstallments.feesHeading" /]</th>
				<th style="border-top: 1px solid grey; width: 70px;">[@spring.message "reviewInstallments.totalHeading" /]</th>
			</tr>
			<tr>
				<th colspan="7" style="border-top: 1px solid grey;">[@spring.message "reviewInstallments.redo.futureInstallmentsHeading" /]</th>
			</tr>
			</thead>
			<tbody>
			[#list loanScheduleFormBean.loanRepaymentFutureInstallments as row]
			<tr>
				<td style="border-top: 1px solid grey;">${row.installmentNumber?string.number}</td>
				<td style="border-top: 1px solid grey;">${row.dueDate?date?string.medium}</td>
				<td style="border-top: 1px solid grey; width: 20px;">-</td>
				<td style="border-top: 1px solid grey;">${row.principal?string.number}</td>
				<td style="border-top: 1px solid grey;">${row.interest?string.number}</td>
				<td style="border-top: 1px solid grey;">${row.fees?string.number}</td>
				<td style="border-top: 1px solid grey;">${row.total?string.number}</td>
			</tr>
			[/#list]
			</tbody>
	</table>
	[/#if]	
[/#if]
<form action="${flowExecutionUrl}" method="post">
	[@form.submitButton label="widget.form.buttonLabel.editloanscheduleinfo" id="redoloanpreview.button.editloanschedule" webflowEvent="editLoanSchedule" /]
</form>
[#if loanAccountQuestionGroupFormBean.questionGroups?size > 0]
<br/>
<br/>
    [@questionnaire.preview loanAccountQuestionGroupFormBean.questionGroups /]
    <form action="${flowExecutionUrl}" method="post">
    	[@form.submitButton label="widget.form.buttonLabel.editquestiongroupinfo" id="continuecreateloanaccount.button.preview" webflowEvent="editQuestionGroups" /]
    </form>
    <br/>
[/#if]

<form action="${flowExecutionUrl}" method="post" class="webflow-controls centered">
    <div class="row">
        [@form.submitButton id="createloanpreview.button.submitForApproval" label="widget.form.buttonLabel.submit" webflowEvent="submit" /]
        [@form.cancelButton label="widget.form.buttonLabel.cancel" webflowEvent="cancel" /]
    </div>
</form>
<br/>
[/@layout.webflow]