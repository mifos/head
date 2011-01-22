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

[@layout.webflow currentState="createSavingsAccount.progress.enterAccountInfo" 
                 states=["createSavingsAccount.progress.selectCustomer", 
                 		 "createSavingsAccount.progress.enterAccountInfo", 
                 		 "createSavingsAccount.progress.reviewAndSubmit"]] 

<h1>Create Savings account - <b>Select a customer</b></h1>
<p>To select, click on a resulting Client or Group or Center from the list below. Click Cancel to return to Clients & Accounts without submitting information.</p>

<!-- Client search form -->
<form action="${flowExecutionUrl}" method="post">
	<div class="row">
		<label for="searchString">Search for:</label>
		<input type="text" name="searchString">
		<input type="submit" class="submit" value="Search" name="_eventId_searchTermEntered" />
	</div>
</form>

<!-- Search results -->
[#-- skwoka: TODO move canned data to controller --]
[#assign cannedResults={"Bangalore_branch1244723261188":"Test Customer1188:ID0011-000000087", 
						"Bangalore_branch1244724101456" : "Test Customer1456:ID0012-000000088",
						"Villupuram-I" : "Test Customer3940:ID0010-000000086",
						"Chitradurga / Test center-12 / Test Group" : "Test Member T:ID0039-000000178",
						"Bangalore_branch1244723261188 testcentre chandan" : "test test:ID0011-000000171",
						"Chitradurga / Test center-12" : "Test Group:ID0039-000000177",
						"Chitradurga / Ramnagara -12" : "tEST gROUP-12:ID0039-000000180",
						"Bangalore_branch1244723261188 testcentre" : "testgroup:ID0011-000000093",
						"Taguig Branch" : "test center:ID0003-000000145",
						"Chitradurga" : "Test center-12:ID0039-000000175"
						}]
[#assign keys = cannedResults?keys]
<ol>
	[#list keys as key]
		<li>${key} / <a href="${flowExecutionUrl}&_eventId=customerSelected">${cannedResults[key]}</a></li>
	[/#list]
</ol>

[#-- skwoka: TODO pagination --]
Previous	Results 1-10 of 11 	Next

<!-- Cancel. Yeah, just one button. -->
<form action="${flowExecutionUrl}" method="post">
	<div class="row">
		<input type="submit" class="cancel" value="Cancel" name="_eventId_cancel" />
	</div>
</form>

[/@layout.webflow]
