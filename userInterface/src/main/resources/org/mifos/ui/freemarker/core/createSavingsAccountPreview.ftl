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
<script type="text/javascript" src="pages/js/jquery/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="pages/js/singleitem.js"></script>

[@layout.webflow currentTab="ClientsAndAccounts"
                 currentState="createSavingsAccount.flowState.reviewAndSubmit" 
                 states=["createSavingsAccount.flowState.selectCustomer", 
                         "createSavingsAccount.flowState.enterAccountInfo", 
                         "createSavingsAccount.flowState.reviewAndSubmit"]] 

<span id="page.id" title="createsavingsaccountpreview"></span>

<h1>[@spring.message "createSavingsAccount.preview.pageTitle" /] - <span class="standout">[@spring.message "createSavingsAccount.preview.pageSubtitle" /]</span></h1>
<p>[@spring.message "createSavingsAccount.preview.instructions" /]</p>
<br/>

<p><span class="standout">[@spring.message "createSavingsAccount.preview.accountOwnerName" /]</span> ${savingsAccountFormBean.customer.displayName}</p>
<br/>

<p class="standout">[@spring.message "createSavingsAccount.preview.productSummary.header" /]</p>
<br/>

<div class="preview">
    <div class="row">
        <div class="attribute">[@spring.message "createSavingsAccount.preview.savingsInstanceName" /]</div>
        <div class="value">${savingsAccountFormBean.product.savingsProductDetails.productDetails.name}</div>
    </div>
    <div class="row">&nbsp;</div>
    <div class="row divider">[@spring.message "createSavingsAccount.preview.instanceInfo" /]</div>
    <div class="row">
        <div class="attribute">[@spring.message "createSavingsAccount.productSummary.description"/]</div>
        <div class="value">${savingsAccountFormBean.product.savingsProductDetails.productDetails.description}</div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "createSavingsAccount.productSummary.typeOfDeposits"/]</div>
        <div class="value">[@lookup.fromMap savingsAccountFormBean.savingsTypes savingsAccountFormBean.product.savingsProductDetails.depositType?string /]</div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "createSavingsAccount.productSummary.maxWithdrawalAmount"/]</div>
        <div class="value">${savingsAccountFormBean.product.savingsProductDetails.maxWithdrawal?string.number}</div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "createSavingsAccount.productSummary.balanceForInterestCalculation"/]</div>
        <div class="value">[@lookup.fromList savingsAccountFormBean.product.interestCalcTypeOptions savingsAccountFormBean.product.savingsProductDetails.interestCalculationType?string /]</div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "createSavingsAccount.productSummary.periodForInterestCalculation"/]</div>
        <div class="value">
            ${savingsAccountFormBean.product.savingsProductDetails.interestCalculationFrequency?string.number}
            [@lookup.fromMap savingsAccountFormBean.recurrenceFrequencies savingsAccountFormBean.product.savingsProductDetails.interestCalculationFrequencyPeriod?string /]
        </div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "createSavingsAccount.productSummary.interestPostingFrequency"/]</div>
        <div class="value">
            ${savingsAccountFormBean.product.savingsProductDetails.interestPostingFrequency}
            [#if savingsAccountFormBean.product.savingsProductDetails.dailyPosting]
           		[@lookup.recurringFrequencyDay /]
            [#else]
            	[@lookup.recurringFrequencyMonth /]
            [/#if]
        </div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "createSavingsAccount.productSummary.minBalanceForInterestCalculation"/]</div>
        <div class="value">${savingsAccountFormBean.product.savingsProductDetails.minBalanceForInterestCalculation?string.number}</div>
    </div>
    <div class="row">
        <div class="attribute">[@spring.message "createSavingsAccount.productSummary.interestRate"/]</div>
        <div class="value">${savingsAccountFormBean.product.savingsProductDetails.interestRate?string.number} %</div>
    </div>
    <div class="row">&nbsp;</div>
    <div class="row">
        [#if savingsAccountFormBean.product.savingsProductDetails.depositType?string == "1"]
            <div class="attribute">[@spring.message "createSavingsAccount.preview.savingAccountDetail.depositAmount.mandatory" /]</div>
        [#elseif savingsAccountFormBean.product.savingsProductDetails.depositType?string == "2"]
            <div class="attribute">[@spring.message "createSavingsAccount.preview.savingAccountDetail.depositAmount.voluntary" /]</div>
        [/#if]
        <div class="value">${savingsAccountFormBean.mandatoryDepositAmount?number ! "0"}</div>
    </div>
    <div class="row">&nbsp;</div>
    <div class="row">
        <form action="${flowExecutionUrl}" method="post">
            <input type="submit" class="edit" value="[@spring.message "createSavingsAccount.preview.editAccountDetailsButton" /]" name="_eventId_editAccountDetails" />
        </form>
    </div>
    <div class="clear"/>
</div>
<br/>

[#if savingsAccountFormBean.questionGroups?size > 0]
    <!-- question group preview -->
    [@questionnaire.preview savingsAccountFormBean.questionGroups /]
    <form action="${flowExecutionUrl}" method="post">
        <input type="submit" class="edit" value="[@spring.message "createSavingsAccount.preview.editQuestionGroupButton" /]" name="_eventId_editQuestionGroup" />
    </form>
    <br/>
[/#if]

<form action="${flowExecutionUrl}" method="post" class="webflow-controls centered">
    <div class="row">
        [@form.submitButton id="createsavingsaccountpreview.button.submitForLater" label="widget.form.buttonLabel.saveForLater" webflowEvent="saveForLater" /]
        [#if savingsAccountFormBean.product.savingsPendingApprovalEnabled ]
            [@form.submitButton id="createsavingsaccountpreview.button.submitForApproval" label="widget.form.buttonLabel.saveForApproval" webflowEvent="saveForApproval" /]
        [#else]
            [@form.submitButton id="approvedButton" label="widget.form.buttonLabel.approve" webflowEvent="approve" /]
        [/#if]
        [@form.cancelButton label="widget.form.buttonLabel.cancel" webflowEvent="cancel"  /]
    </div>
</form>

[/@layout.webflow]