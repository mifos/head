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
[#include "layout.ftl"]
[@clientLeftPane "ClientsAndAccounts"]
<span id="page.id" title="LoanAccountPayments"></span>
<div class="content">
	<table id="loanAccountPayments" class="datatable">
		<thead>
			<tr>
				<th>
					<span class='fontnormalbold'>
					[@spring.message "PaymentID" /]
					<span>
				</th>
				<th>
					<span class='fontnormalbold'>
					[@spring.message "Type" /]
					</span>
				</th>
				<th>
					<span class='fontnormalbold'>
					[@spring.message "Amount" /]
					</span>
				</th>
				<th>
					<span class='fontnormalbold'>
					[@spring.message "loan.paymentDate" /]
					</span>
				</th>
				<th>
					<span class='fontnormalbold'>
					[@spring.message "accounts.receiptid" /]
					</span>
				</th>
				<th>
					<span class='fontnormalbold'>
					[@spring.message "accounts.receiptdate" /]
					</span>
				</th>
				<th>
					<span class='fontnormalbold'>
					[@spring.message "reports.administrativedocuments" /]
					</span>
				</th>
			</tr>
		</thead>
		<tbody>
			[#list loanAccountPayments as loanAccountPayment]
				<tr>
					<td>
					${loanAccountPayment.paymentId}
					</td>
					<td>
					${loanAccountPayment.paymentType.name}
					</td>
					<td>
					${loanAccountPayment.amount}
					</td>
					<td>
					${i18n.date_formatter(loanAccountPayment.paymentDate, "dd/MM/yyyy", Application.LocaleSetting.locale)}
					</td>
					<td>
					${loanAccountPayment.receiptId!}
					</td>
					<td>
					${i18n.date_formatter(loanAccountPayment.receiptDate, "dd/MM/yyyy", Application.LocaleSetting.locale)}
					</td>
					<td>
					[#if loanAccountPayment.adminDocuments??]
						[#list loanAccountPayment.adminDocuments as adminDocument]
							<a href="executeAdminDocument.ftl?adminDocumentId=${adminDocument.id?c}&entityId=${loanAccountPayment.paymentId?c}">
							${adminDocument.name}
							</a>&nbsp
						[/#list]
					[/#if]
					</td>
				</tr>
			[/#list]
		</tbody>
	</table>
	[@widget.datatable "loanAccountPayments" /]
	[@form.returnToPage  "viewLoanAccountDetails.ftl?globalAccountNum=${RequestParameters.globalAccountNum}" "button.back" "loanaccountpayments.button.back"/]
</div>
[/@clientLeftPane]