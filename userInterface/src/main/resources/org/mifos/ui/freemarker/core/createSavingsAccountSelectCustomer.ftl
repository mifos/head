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

<h1>[@spring.message "createSavingsAccount.selectCustomer.pageTitle" /] - <span class="standout">[@spring.message "createSavingsAccount.selectCustomer.pageSubtitle" /]</span></h1>
<p>[@spring.message "createSavingsAccount.selectCustomer.instructions" /]</p>
<br/>

<!-- Client search form -->
[@form.errors "savingsAccountFormBean.*"/]
<form action="${flowExecutionUrl}" method="post">
    <div class="row">
        <label for="searchString">[@spring.message "createSavingsAccount.selectCustomer.searchTerm" /]:</label>
        [@spring.formInput "savingsAccountFormBean.searchString" /]
        [@form.submitButton "createSavingsAccount.selectCustomer.searchButton" "searchTermEntered" /]
    </div>
</form>

<!-- Search results -->
<br/>
<div class="search-results">
<table id="customerSearchResults" class="datatable">
    <thead>
        <tr>
            <th>Branch</th>
            <th>Center</th>
            <th>Group</th>
            <th>Client</th>
        </tr>
    </thead>
    <tbody>
        [#list customerSearchResultsDto.pagedDetails as customer]
            <tr>
                <td>${customer.branchName}</td>
                <td>${customer.centerName}</td>
                <td>${customer.groupName}</td>
                <td><a href="${flowExecutionUrl}&_eventId=customerSelected&customerId=${customer.customerId}">${customer.clientName}</a></td>
            </tr>
        [/#list]
    </tbody>
</table>
[@widget.datatable "customerSearchResults" /]
</div>
<div class="clear"/>

<!-- Cancel. Yeah, just one button. -->
<form action="${flowExecutionUrl}" method="post" class="webflow-controls">
    <div class="row centered">
        [@form.cancelButton "createSavingsAccount.selectCustomer.cancelButton" "cancel" /]
    </div>
</form>

[/@layout.webflow]
