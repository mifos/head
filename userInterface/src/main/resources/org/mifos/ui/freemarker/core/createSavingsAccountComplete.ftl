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

[@layout.webflow currentState="createSavingsAccount.flowState.reviewAndSubmit" 
                 states=["createSavingsAccount.flowState.selectCustomer", 
                 		 "createSavingsAccount.flowState.enterAccountInfo", 
                 		 "createSavingsAccount.flowState.reviewAndSubmit"]] 

<h1 class="success">You have successfully created a new Savings account</h1>

<p><span class="standout">Please Note:</span> A new Savings account for Test Customer1188 has been assigned account # <span class="account-number">000100000000567</span>. You can enter an account number into the search box to access account details.</p>
<br/>

<p><a href="" class="standout">View Savings account details now</a></p>
<br/>

<div class="suggestion">Suggested next steps</div>
<ul>
	<li><a href="">Open a new Savings account</a></li>
	<li><a href="">Open a new Loan account</a></li>
</ul>
 
[/@layout.webflow]