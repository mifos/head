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
                 currentState="createLoanAccount.flowState.selectCustomer" 
                 states=["createLoanAccount.flowState.selectCustomer", 
                         "createLoanAccount.flowState.enterAccountInfo",
                         "createLoanAccount.flowState.reviewInstallments", 
                         "createLoanAccount.flowState.reviewAndSubmit"]] 

<span id="page.id" title="CustSearchAccount"></span>

[#if customerSearchFormBean.redoLoanAccount]
<p class="red standout" style="margin-bottom: 5px;">[@spring.message "redoLoanAccount.wizard.highlightedNote" /]</p>
<h1>[@spring.message "redoLoanAccount.wizard.title" /] - <span class="standout">[@spring.message "createLoanAccount.customerSearch.pageSubtitle" /]</span></h1>
[#else]
<h1>[@spring.message "createLoanAccount.wizard.title" /] - <span class="standout">[@spring.message "createLoanAccount.customerSearch.pageSubtitle" /]</span></h1>
[/#if]
<p>[@spring.message "selectCustomer.instructions" /]</p>
<br/>

[@form.errors "customerSearchFormBean.*"/]
<form action="${flowExecutionUrl}" method="post">
    <div class="row">
        <label for="searchString">[@spring.message "customerSearch.searchTerm" /]:</label>
        [@form.input path="customerSearchFormBean.searchString" id="cust_search_account.input.searchString" attributes="" /]
        [@form.submitButton label="widget.form.buttonLabel.search" webflowEvent="searchTermEntered" /]
    </div>
</form>

<br/>
<div class="search-results">
<table id="customerSearchResults" class="datatable">
	<thead>
		<tr>
			<th style="display: none;">Index</th>
			<th style="display: none;">Customer</th>
		</tr>
	</thead>
    <tbody>
        [#list customerSearchResultsDto.pagedDetails as customer]
            <tr>
               <td>
               ${customer.searchIndex}. &nbsp;&nbsp;&nbsp;
               </td>
               <td>
               ${customer.branchName}
               [#if customer.centerName != "--"]
               <b>/</b>${customer.centerName}
               [/#if]
               [#if customer.groupName != "--"]
               <b>/</b>${customer.groupName}
               [/#if]
                  <b>/</b><b><a href="${flowExecutionUrl}&_eventId=customerSelected&customerId=${customer.customerId?c}">${customer.clientName}:ID${customer.globalId}</a></b>
               </td>
            </tr>
        [/#list]
    </tbody>
</table>
[@widget.datatable "customerSearchResults" /]
</div>
<div class="clear"/>

<form action="${flowExecutionUrl}" method="post" class="webflow-controls">
    <div class="row centered">
        [@form.cancelButton label="widget.form.buttonLabel.cancel" webflowEvent="cancel" /]
    </div>
</form>

[/@layout.webflow]