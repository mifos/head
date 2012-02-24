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
[#assign loanfn=JspTaglibs["/WEB-INF/tlds/application/loan/loanfunctions.tld"]]

[@widget.topNavigationNoSecurityMobile currentTab="ClientsAndAccounts" /]

<span id="page.id" title="LoanRepayment"></span>

<div class="content" style="width: 350px">
	<div>
		<span class="heading">
			${loanInformationDto.prdOfferingName} #&nbsp;${loanInformationDto.globalAccountNum} -
		</span>
		<span class="headingorange">
			[@spring.message "loan.repayment_sched"/]
			${i18n.date_formatter(currentDate, "dd/MM/yyyy", Application.LocaleSetting.locale)}
		</span>
	</div>
	<div>
		[@loanfn.getLoanRepaymentTable /]
	</div>
	<div>
		<form action="viewLoanAccountDetails.ftl" method="get">
			 <input type="submit" value="[@spring.message "loan.returnToAccountDetails"/]" class="buttn" id="loanRepayment.button.return" name="returnToAccountDetailsbutton"/>
			 <input type="hidden" name="globalAccountNum" value="${loanInformationDto.globalAccountNum}"/>	
		</form>
	</div>
</div>