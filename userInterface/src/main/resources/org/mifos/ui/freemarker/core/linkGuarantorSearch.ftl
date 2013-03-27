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
                 currentState="linkGuarantor.search.title" 
                 states=["linkGuarantor.search.title", 
                         "linkGuarantor.preview"]] 

<span id="page.id" title="GuarantorSearch"></span>


<h1>[@spring.message "linkGuarantor.title" /] - <span class="standout">[@spring.message "linkGuarantor.search.title" /]</span></h1>

<p>[@spring.message "linkGuarantor.search.instructions" /]</p>
<br/>

[@form.errors "customerSearchFormBean.*"/]
<form action="${flowExecutionUrl}" method="post" class="two-columns">
    <fieldset>
    <div class="row">
        <label for="searchString">[@spring.message "customerSearch.searchTerm" /]:</label>
        [@form.input path="customerSearchFormBean.searchString" id="cust_search_account.input.searchString" attributes="" /]
    </div>
    </fieldset>
    <div class="row">
        [@form.submitButton label="widget.form.buttonLabel.search" id="cust_search_account.button.search" webflowEvent="searchTermEntered" /]
        [@form.cancelButton label="widget.form.buttonLabel.cancel" webflowEvent="cancel" /]
    </div>
</form>
<br/>
[/@layout.webflow]