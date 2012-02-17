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
[@layout.header "mifos" /]
[#assign mifoscustom=JspTaglibs["/mifos/customtags"]]

[@widget.topNavigationNoSecurityMobile currentTab="ClientsAndAccounts" /]

<span id="page.id" title="viewsavingstrxnhistory"></span>

<div class="content" style="width: 350px">
	<div>
		<span class="heading">
			${savingsAccountDetailDto.productDetails.productDetails.name} #&nbsp;${savingsAccountDetailDto.globalAccountNum} -
		</span>
		<span class="headingorange">
			[@spring.message "Savings.Transactionhistory"/]
		</span>
	</div>
	<div>
		[@mifoscustom.mifostabletag source="trxnHistoryList" scope="request" xmlFileName="SavingsTrxnHistory.xml" moduleName="org/mifos/accounts/savings/util/resources" passLocale="true"/]
	</div>
	<div>
		<form action="viewSavingsAccountDetails.ftl" method="get">
			 <input type="submit" value="[@spring.message "Savings.returnToAccountDetails"/]" class="buttn" />
			 <input type="hidden" name="globalAccountNum" value="${savingsAccountDetailDto.globalAccountNum}"/>	
		</form>
	</div>
</div>