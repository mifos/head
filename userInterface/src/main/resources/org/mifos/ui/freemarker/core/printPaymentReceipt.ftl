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
<span id="page.id" title="PrintReceipt"></span>
<div class="content">
	<table id="loanAccountPayments" class="datatable">
		<tr>
			<td>
				<span class='fontnormalbold'>
					[@spring.message "printReceipt.contentMessage" /]
				<span>
			</td>
		</tr>
		[#if loanAccountPayment.adminDocuments??]
			[#list loanAccountPayment.adminDocuments as adminDocument]
				<tr>
					<td>
					    <label for="adminDocOutputType_${loanAccountPayment.paymentId?c}">${adminDocument.name}</label>
                        <select id="adminDocOutputType_${loanAccountPayment.paymentId?c}" class="adminDocOutputType">
                          <option value="0" selected="selected">PDF</option>
                          <option value="1">XLS</option>
                          <option value="2">RTF</option>
                          <option value="3">HTML</option>
                          <option value="4">XML</option>
                          <option value="5">CSV</option>
                        </select>
						<a class="adminDocOutputTypeLink"  href="executeAdminDocument.ftl?adminDocumentId=${adminDocument.id?c}&entityId=${loanAccountPayment.paymentId?c}&outputTypeId=0">
						${adminDocument.name}
						</a>
					</td>
				</tr>
			[/#list]
		[/#if]
		
	</table>
	<script type="text/javascript" src="pages/application/admindocument/js/adminDocument.js"></script>
    <script type="text/javascript">syncAdminDocumentLinkWithComboBox("adminDocOutputType", "adminDocOutputTypeLink");</script>
	[@form.returnToPage  "viewLoanAccountDetails.ftl?globalAccountNum=${globalAccountNum}" "printReceipt.returnToLoanAccount" "printpaymentreceipt.button.back"/]
</div>
[/@clientLeftPane]