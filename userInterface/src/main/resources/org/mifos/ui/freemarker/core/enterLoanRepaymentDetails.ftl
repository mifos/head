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
                 currentState="loan.paymentdetails" 
                 states=["loan.paymentdetails", "accounts.review&submit"]
]
 
<script type="text/javascript" src="pages/js/padDate.js"></script> 
                         
<span id="page.id" title="ApplyLoanRepayment"></span>
[@i18n.formattingInfo /]

<h1># ${loanAccountNumber} - <span class="standout">[@spring.message "loan.apply_prepayment" /]</span></h1>
<p>[@spring.message "accounts.asterisk" /]</p>
<br/>

[@form.errors "loanRepaymentFormBean.*"/]
<form action="${flowExecutionUrl}" method="post" class="two-columns">
    <fieldset>
    <div class="row">
        <label for="paymentDateDD"><span class="mandatory">*</span>[@spring.message "accounts.date_of_trxn" /]:</label>
        [@form.input path="loanRepaymentFormBean.paymentDateDD" id="paymentDateDD" attributes="size=1 maxlength=2" /]<span>[@spring.message "datefield.dd"/]</span>
        [@form.input path="loanRepaymentFormBean.paymentDateMM" id="paymentDateMM" attributes="size=1 maxlength=2" /]<span>[@spring.message "datefield.mm"/]</span>
        [@form.input path="loanRepaymentFormBean.paymentDateYY" id="paymentDateYY" attributes="size=3 maxlength=4" /]<span>[@spring.message "datefield.yyyy"/]</span>
    </div>
    <div class="row">
        <label for="paymentAmount"><span class="mandatory">*</span>[@spring.message "Amount" /]:</label>
        [@form.input path="loanRepaymentFormBean.paymentAmount" id="paymentAmount" attributes="class=separatedNumber" /]
    </div>
    <div class="row">
        <label for="paymentType"><span class="mandatory">*</span>[@spring.message "accounts.mode_of_payment" /]:</label>
        [@form.singleSelectWithPrompt path="loanRepaymentFormBean.paymentType" id="paymentType" options=loanRepaymentFormBean.allowedPaymentTypes /]
    </div>
    <div class="row">
        <label for="receiptId">[@spring.message "loan.receiptId" /]:</label>
        [@form.input path="loanRepaymentFormBean.receiptId" id="receiptId" /]
    </div>
    <div class="row">
        <label for="receiptDateDD">[@spring.message "loan.receiptdate" /]:</label>
        [@form.input path="loanRepaymentFormBean.receiptDateDD" id="receiptDateDD" attributes="size=1 maxlength=2" /]<span>[@spring.message "datefield.dd"/]</span>
        [@form.input path="loanRepaymentFormBean.receiptDateMM" id="receiptDateMM" attributes="size=1 maxlength=2" /]<span>[@spring.message "datefield.mm"/]</span>
        [@form.input path="loanRepaymentFormBean.receiptDateYY" id="receiptDateYY" attributes="size=3 maxlength=4" /]<span>[@spring.message "datefield.yyyy"/]</span>
    </div>
    </fieldset>
    <div class="row">
        [@form.submitButton label="widget.form.buttonLabel.submit" id="enterLoanRepaymentDetails.submit" webflowEvent="detailsEntered" /]
        [@form.cancelButton label="widget.form.buttonLabel.cancel" webflowEvent="cancel" /]
    </div>
</form>
<br/>

[/@layout.webflow]