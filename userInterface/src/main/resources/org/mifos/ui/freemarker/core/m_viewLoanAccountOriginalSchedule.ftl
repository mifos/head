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

<span id="page.id" title="ViewOriginalSchedule"></span>

<div class="content" style="width: 350px">
	<div>
		<span class="heading">
			${loanInformationDto.prdOfferingName} #&nbsp;${loanInformationDto.globalAccountNum} -
		</span>
		<span class="headingorange">
			[#if RequestParameters.individual?? ]
				[@spring.message "loan.individual.schedule"/]
			[#else]
				[@spring.message "loan.original.schedule"/]
			[/#if]
		</span>
	</div>
	<div>
        <span class="fontnormalbold">
	        [@spring.message "loan.amt" /]:&nbsp;
	    </span>
	    <span class="fontnormal">
	        ${originalScheduleInfoDto.loanAmount}
	    </span>
	    <br/>
	    <span class="fontnormalbold">
	        [@spring.message "loan.proposed_date" /]:&nbsp;
	    </span>
	    <span class="fontnormal">
	        ${i18n.date_formatter(originalScheduleInfoDto.disbursementDate, "dd/MM/yyyy", Application.LocaleSetting.locale)}
	    </span>
	</div>
	<br/>
	<div>
	    <span class="headingorange">
	        [@spring.message "loan.originalScheduleInstallments" /]
	    </span>
		[@mifoscustom.mifostabletag source="originalInstallments" scope="request" xmlFileName="OriginalSchedule.xml" moduleName="org/mifos/accounts/loan/util/resources" passLocale="true"/]
	</div>
	<div>
		[#if RequestParameters.individual?? ]
		<form name="goBackToLoanAccountDetails" method="get" action ="viewLoanAccountDetails.ftl">
		 	<input type="submit" value="[@spring.message "loan.returnToAccountDetails"/]" class="buttn" id="loanRepayment.button.return" name="returnToAccountDetailsbutton"/>
			<input type="hidden" name="globalAccountNum" value="${RequestParameters.parentLoanGlobalAccountNum}"/>
     	</form>
     	[#else]
		<form name="goBackToLoanAccountRepaymentSchedule" method="get" action="viewLoanAccountRepaymentSchedule.ftl">
		 	<input type="submit" value="[@spring.message "loan.returnToRepaymentSchedule"/]" class="buttn" id="loanRepayment.button.return" name="returnToRepaymentScheduleButton"/>
			<input type="hidden" name='globalAccountNum' value="${loanInformationDto.globalAccountNum}"/>
			<input type="hidden" name='randomNUm' value="${Session.randomNUm?c}"/>
			<input type="hidden" name='currentFlowKey' value="${Request.currentFlowKey}"/>
		</form>
     	[/#if]	
	</div>
</div>