[#ftl]
[#--
* Copyright (c) 2005-2010 Grameen Foundation USA
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

[@layout.webflow currentState="createSavingsAccount.flowState.enterAccountInfo" 
                 states=["createSavingsAccount.flowState.selectCustomer", 
                 		 "createSavingsAccount.flowState.enterAccountInfo", 
                 		 "createSavingsAccount.flowState.reviewAndSubmit"]] 

<h1>Create Savings account - <span class="standout">Enter Savings account information</span></h1>
<p>Select a Savings instance. Then click Continue. Click Cancel to return to Clients & Accounts without submitting information.</p>
<p>*Fields marked with an asterisk are required.</p>
<br/>

<p><span class="standout">Account Owner</span>: Test Customer1188</p>
<br/>

[@form.errors "accountDetailFormBean.*"/]
<form action="${flowExecutionUrl}&_eventId=newProductSelected" method="post" class="two-columns">
	<fieldset>
	<div class="row">
		<label for="selectedPrdOfferingId" class="mandatory">Savings instance name:</label>
		<select id="createsavingsaccount.select.savingsProduct" name="selectedPrdOfferingId">
			<option>Product 1</option>
			<option>Product 2</option>
			<option>Product 3</option>
		</select>
	</div>
	</fieldset>
</form>

<p><b>Savings product summary</b></p>
<div class="product-summary">
	<div class="row">
		<div class="attribute">[@spring.message "createSavingsAccount.enterAccountInfo.productSummary.description"/]</div>
		<div class="value">${product.savingsProductDetails.productDetails.description}</div>
	</div>
	<div class="row">
		<div class="attribute">[@spring.message "createSavingsAccount.enterAccountInfo.productSummary.typeOfDeposits"/]:</div>
		<div class="value">Voluntary</div>
	</div>
	<div class="row">
		<div class="attribute">[@spring.message "createSavingsAccount.enterAccountInfo.productSummary.maxWithdrawalAmount"/]:</div>
		<div class="value">TODO: localize ${product.savingsProductDetails.maxWithdrawal}</div>
	</div>
	<div class="row">
		<div class="attribute">[@spring.message "createSavingsAccount.enterAccountInfo.productSummary.balanceForInterestCalculation"/]:</div>
		<div class="value">TODO interest calculation: ${product.savingsProductDetails.interestCalculationType}</div>
	</div>
	<div class="row">
		<div class="attribute">[@spring.message "createSavingsAccount.enterAccountInfo.productSummary.periodForInterestCalculation"/]:</div>
		<div class="value">30 day(s)</div>
	</div>
	<div class="row">
		<div class="attribute">[@spring.message "createSavingsAccount.enterAccountInfo.productSummary.interestPostingFrequency"/]:</div>
		<div class="value">1 month(s)</div>
	</div>
	<div class="row">
		<div class="attribute">[@spring.message "createSavingsAccount.enterAccountInfo.productSummary.minBalanceForInterestCalculation"/]:</div>
		<div class="value">50.0</div>
	</div>
	<div class="row">
		<div class="attribute">[@spring.message "createSavingsAccount.enterAccountInfo.productSummary.interestRate"/]:</div>
		<div class="value">25.0 %</div>
	</div>
</div>
<br/>
<br/>

<p><b>Savings account details</b></p>
<form action="${flowExecutionUrl}" method="post" class="two-columns">
	<fieldset>
	<div class="row">
		<label for="selectedPrdOfferingId" class="mandatory">Recommended amount for deposit:</label>
		<input type="text" id="continuecreatesavingsaccount.input.recommendedAmount" name="recommendedAmount" value=""/>
	</div>
	</fieldset>
	<div class="row webflow-controls">
		<input type="submit" class="submit" value="Continue" name="_eventId_detailsEntered" />
		<input type="submit" class="cancel" value="Cancel" name="_eventId_cancel" />
	</div>
</form>


<!-- TODO: put this in a file -->
<script type="text/javascript">
$(document).ready(function() {
	$('select').change(function(e) {
		console.log('changed!', e);
		$(this).closest('form').submit();
				
	});
});
</script>

[/@layout.webflow]
