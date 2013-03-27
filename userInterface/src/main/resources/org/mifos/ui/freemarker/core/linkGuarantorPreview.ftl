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
                 currentState="linkGuarantor.preview" 
                 states=["linkGuarantor.search.title", 
                         "linkGuarantor.preview"]] 

<span id="page.id" title="GuarantorPreview"></span>


<h1>[@spring.message "linkGuarantor.title" /] - <span class="standout">[@spring.message "linkGuarantor.preview" /]</span></h1>
<p>[@spring.message "linkGuarantor.preview.instructions" /]</p>
<br/>

<fieldset>

<div class="product-summary">
    <div>
        <h1><span class="standout">[@spring.message "linkGuarantor.guarantor.title" /]</span></h1>
    </div>
    <div class="row">
        <div class="attribute">
            <span class="standout">[@spring.message "linkGuarantor.guarantor.name" /]</span>
            <div class="value">${clientDto.clientDisplay.displayName}</div>
        </div>
    </div>
    <div class="row">
        <div class="attribute">
            <span class="standout">[@spring.message "linkGuarantor.guarantor.globalId" /]</span>
            <div class="value">${clientDto.clientDisplay.globalCustNum}</div>
        </div>
    </div></br><br></br>
    <div class="row">
        <h1><span class="standout">[@spring.message "linkGuarantor.loan.title" /]</span></h1>
    </div>
    <div class="row">
        <div class="attribute">
            <span class="standout">[@spring.message "linkGuarantor.loan.owner.name" /]</span>
            <div class="value">${accOwnerDto.clientDisplay.displayName}</div>
        </div>
    </div>
    <div class="row">
        <div class="attribute">
            <span class="standout">[@spring.message "linkGuarantor.loan.owner.id" /]</span>
            <div class="value">${accOwnerDto.clientDisplay.globalCustNum}</div>
        </div>
    </div>
    <div class="row">
        <div class="attribute">
            <span class="standout">[@spring.message "linkGuarantor.loan.name" /]</span>
            <div class="value">${prdOfferingName}</div>
        </div>
    </div>
    <div class="row">
        <div class="attribute">
            <span class="standout">[@spring.message "linkGuarantor.loan.globalId" /]</span>
            <div class="value">${loanAccountNumber}</div>
        </div>
    </div>
</div>
</fieldset>
<form action="${flowExecutionUrl}" method="post" class="two-columns">
        [@form.submitButton label="widget.form.buttonLabel.submit" id="apply.guarantor.submit" webflowEvent="submit" /]
        [@form.cancelButton label="widget.form.buttonLabel.cancel" webflowEvent="cancel" /]
</form>
[/@layout.webflow]