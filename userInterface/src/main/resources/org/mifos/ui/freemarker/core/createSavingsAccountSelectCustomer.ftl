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

[@layout.webflow currentState="createSavingsAccount.flowState.selectCustomer" 
                 states=["createSavingsAccount.flowState.selectCustomer", 
                 		 "createSavingsAccount.flowState.enterAccountInfo", 
                 		 "createSavingsAccount.flowState.reviewAndSubmit"]] 

<h1>Create Savings account - <span class="standout">Select a customer</span></h1>
<p>To select, click on a resulting Client or Group or Center from the list below. Click Cancel to return to Clients & Accounts without submitting information.</p>

<!-- Client search form -->
[@form.errors "customerSearchFormBean.*"/]
<form action="${flowExecutionUrl}" method="post">
	<div class="row">
		<label for="searchString">Search for:</label>
		[@spring.formInput "customerSearchFormBean.searchString" /]
		<input type="submit" class="submit" value="Search" name="_eventId_searchTermEntered" />
	</div>
</form>

<!-- Search results -->
<div class="search-results">
<ol>
	[#list customerSearchResultsDto.pagedDetails as customer]
		<li>${customer.displayName} / <a href="${flowExecutionUrl}&_eventId=customerSelected&customerId=${customer.customerId}">${customer.displayName}</a></li>
	[/#list]
</ol>
</div>

[#-- skwoka: TODO pagination --]
Previous	Results 1-10 of 11 	Next

<!-- Cancel. Yeah, just one button. -->
<form action="${flowExecutionUrl}" method="post" class="webflow-controls">
	<div class="row centered">
		<input type="submit" class="cancel" value="Cancel" name="_eventId_cancel" />
	</div>
</form>

[/@layout.webflow]
