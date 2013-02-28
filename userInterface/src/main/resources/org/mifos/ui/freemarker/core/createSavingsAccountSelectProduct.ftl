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
                 currentState="createSavingsAccount.flowState.enterAccountInfo" 
                 states=["createSavingsAccount.flowState.selectCustomer", 
                         "createSavingsAccount.flowState.enterAccountInfo", 
                         "createSavingsAccount.flowState.reviewAndSubmit"]] 
<script type="text/javascript" src="pages/js/singleitem.js"></script>

<h1>[@spring.message "createSavingsAccount.selectProduct.pageTitle" /] - <span class="standout">[@spring.message "createSavingsAccount.selectProduct.pageSubtitle" /]</span></h1>
<p>[@spring.message "createSavingsAccount.selectProduct.instructions" /]</p>
<p>[@spring.message "createSavingsAccount.selectProduct.requiredFieldsInstructions" /]</p>
<br/>

<p><span class="standout">[@spring.message "createSavingsAccount.selectProduct.accountOwnerName" /]</span> ${savingsAccountFormBean.customer.displayName}</p>
<br/>

[@form.errors "savingsAccountFormBean.*"/]
<form action="${flowExecutionUrl}" method="post" class="two-columns">
    <fieldset>
    <div class="row">
        [@form.label "productId" true][@spring.message "createSavingsAccount.selectProduct.selectSavingsProduct" /][/@form.label]
        [@form.singleSelectWithPrompt path="savingsAccountFormBean.productId" options=savingsAccountFormBean.productOfferingOptions id="createsavingsaccount.select.savingsProduct" selectPrompt="createSavingsAccount.selectProduct.selectPrompt" /]
    </div>
    </fieldset>
    <div class="row webflow-controls">
        [@form.submitButton label="widget.form.buttonLabel.continue" id="createsavingsaccount.button.continue" webflowEvent="productSelected" /]
        [@form.cancelButton label="widget.form.buttonLabel.cancel" webflowEvent="cancel"  /]
    </div>
</form>

[/@layout.webflow]