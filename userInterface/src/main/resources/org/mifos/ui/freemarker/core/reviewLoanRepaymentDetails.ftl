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
                 currentState="accounts.review&submit" 
                 states=["loan.paymentdetails", "accounts.review&submit"]
]
                         
<span id="page.id" title="ApplyLoanRepayment"></span>
[@i18n.formattingInfo /]

<h1># ${loanAccountNumber} - <span class="standout">[@spring.message "accounts.reviewtransaction" /]</span></h1>
<p>[@spring.message "accounts.edittrans" /]</p>
<br/>

<div class="row">
	<p><b>[@spring.message "accounts.date_of_trxn"/]:</b> 
		${i18n.date_formatter(loanRepaymentFormBean.paymentDate, "dd/MM/yyyy", Application.UserLocale.locale)}</p>
	<p><b>[@spring.message "Amount"/]:</b> ${loanRepaymentFormBean.paymentAmount}</p>
	<p><b>[@spring.message "accounts.mode_of_payment"/]:</b> ${loanRepaymentFormBean.paymentTypeName}</p>
	<p><b>[@spring.message "loan.receiptId"/]:</b> ${loanRepaymentFormBean.receiptId}</p>
	<p><b>[@spring.message "loan.receiptdate"/]:</b> 
		${i18n.date_formatter(loanRepaymentFormBean.receiptDate, "dd/MM/yyyy", Application.UserLocale.locale)}</p>
</div>
<br />

<div class="row">
	<form action="${flowExecutionUrl}" method="post">
		[@form.submitButton label="accounts.edittrxn" id="reviewLoanRepayment.back" webflowEvent="back" /]
	</form>
</div>

<div class="row">
	<form action="${flowExecutionUrl}" method="post" class="two-columns">
        [@form.submitButton label="widget.form.buttonLabel.submit" id="reviewLoanRepayment.submit" webflowEvent="submit" /]
        [@form.cancelButton label="widget.form.buttonLabel.cancel" webflowEvent="cancel" /]
    </form>
</div>
<br/>

[/@layout.webflow]