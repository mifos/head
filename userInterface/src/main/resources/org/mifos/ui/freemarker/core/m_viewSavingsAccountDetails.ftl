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
[#assign mifostag=JspTaglibs["/tags/mifos-html"]]
[#assign mifoscustom=JspTaglibs["/mifos/customtags"]]

[@widget.topNavigationNoSecurityMobile currentTab="ClientsAndAccounts" /]

<span id="page.id" title="savingsaccountdetail"></span>

<div class="content">
	<div>
		<span class="headingorange">
			${savingsAccountDetailDto.productDetails.productDetails.name} #
			<br/>
			<span id="savingsaccountdetail.text.savingsId">
				${savingsAccountDetailDto.globalAccountNum}
			</span> 
		</span><br/>
		<span class="fontnormal">
			[#if savingsAccountDetailDto.accountStateId != AccountState.SAVINGS_CANCELLED.value && savingsAccountDetailDto.accountStateId != AccountState.SAVINGS_CLOSED.value ]
				<a id="savingsaccountdetail.link.editAccountStatus" 
				href="editStatusAction.do?method=load&accountId=${savingsAccountDetailDto.accountId}&randomNUm=${Session.randomNUm}&currentFlowKey=${Request.currentFlowKey}">
					[@spring.message "Savings.editAccountStatus" /]
				</a>
			[/#if]
		</span>
	</div>
	<div>
		<font class="fontnormalRedBold">
			<span>
			</span>
		</font>
	</div>
	<div>
		<span class="fontnormal">
			[@mifostag.MifosImage id="${savingsAccountDetailDto.accountStateId}" moduleName="org.mifos.accounts.util.resources.accountsImages" /] 	
			<span id="savingsaccountdetail.status.text" >${savingsAccountDetailDto.accountStateName}</span>
		</span><br/>
		<span class="fontnormal">
			[@spring.message "Savings.accountBalance" /]:
			${savingsAccountDetailDto.accountBalance?number}
		</span><br/>
		[#if savingsAccountDetailDto.productDetails.depositType == SavingsConstants.SAVINGS_MANDATORY &&
		 ( savingsAccountDetailDto.accountStateId == AccountStates.SAVINGS_ACC_APPROVED || savingsAccountDetailDto.accountStateId == AccountStates.SAVINGS_ACC_INACTIVE ) ]
		<span class="fontnormal">
			<a id="savingsaccountdetail.link.viewDepositDueDetails" 
				href="savingsAction.do?method=getDepositDueDetails&globalAccountNum=${savingsAccountDetailDto.globalAccountNum}&currentFlowKey=${Request.currentFlowKey}&randomNUm=${Session.randomNUm}">
				[@spring.message "Savings.viewDepositDueDetails" /]
			</a>
		</span><br/>
		[/#if]
		[#if savingsAccountDetailDto.accountStateId != AccountStates.SAVINGS_ACC_CLOSED && savingsAccountDetailDto.productDetails.depositType == SavingsConstants.SAVINGS_MANDATORY ]
			[#if savingsAccountDetailDto.dueDate?has_content ]	
				[@spring.message "Savings.totalAmountDueOn" /]
				${i18n.date_formatter(savingsAccountDetailDto.dueDate, "dd/MM/yyyy", Application.LocaleSetting.locale)}:
			[#else]
				[@spring.message "Savings.totalAmountDue" /]:
			[/#if]
			<span id="savingsaccountdetail.text.totalAmountDue">
				${savingsAccountDetailDto.totalAmountDue?number}
			</span>										
		[/#if]
	</div>
	<div>
		<img src="pages/framework/images/trans.gif" width="10" height="10">
	</div>
	<div style="width: 350px">
		<span class="headingorange">
			[@spring.message "Savings.recentActivity" /]
		</span><br/>
		<span class="fontnormal">
			<a id="savingsaccountdetail.link.viewAllAccountActivity" 
				href="viewSavingsAccountRecentActivity.ftl?globalAccountNum=${savingsAccountDetailDto.globalAccountNum}&currentFlowKey=${Request.currentFlowKey}&randomNUm=${Session.randomNUm}">
				[@spring.message "Savings.viewAllAccountActivity" /]
			</a>
		</span>
		[@mifoscustom.mifostabletag source="recentActivityForDetailPage" scope="session" xmlFileName="SavingsAccountRecentActivity.xml" moduleName="org/mifos/accounts/savings/util/resources" passLocale="true" /] 
	</div>
	<div>
		<img src="pages/framework/images/trans.gif" width="10" height="10">
	</div>
	<div>
		<span class="headingorange">
			[@spring.message "Savings.AccountDetails" /]
		</span><br/>
		[#if savingsAccountDetailDto.accountStateId != AccountState.SAVINGS_CANCELLED.value && savingsAccountDetailDto.accountStateId != AccountState.SAVINGS_CLOSED.value ]
		<span>
			<a id="savingsaccountdetail.link.editAccountInformation" 
				href="savingsAction.do?method=edit&currentFlowKey=${Request.currentFlowKey}">
				[@spring.message "Savings.EditAccountInformation" /]
			</a>
		</span><br/>
		[/#if]
		<span class="fontnormal"> 
			[#if savingsAccountDetailDto.productDetails.depositType == SavingsConstants.SAVINGS_MANDATORY]
				[@spring.message "Savings.mandatoryAmountForDeposit" /]:
			[#else]
				[@spring.message "Savings.recommendedAmountForDeposit" /]:
			[/#if]
			${savingsAccountDetailDto.recommendedOrMandatoryAmount?number}
		</span><br/>
		<span class="fontnormal">
			[@spring.message "Savings.typeOfDeposits" /]: ${savingsAccountDetailDto.depositTypeName}
			<br/>
			[@spring.message "Savings.maxAmountPerWithdrawl" /]: ${savingsAccountDetailDto.productDetails.maxWithdrawal}
			<br/>
			[@spring.message "${ConfigurationConstants.INTEREST}" /] [@spring.message "Savings.rate" /]:  ${savingsAccountDetailDto.productDetails.interestRate?number} [@spring.message "Savings.perc" /]
		<span>		
	</div>
	<div>
		<span class="headingorange">
			[@spring.message "Savings.moreAccountAndTransactionDetails" /]
		</span><br/>		
		<span class="fontnormal">
			<a id="savingsaccountdetail.link.questionGroups" href="viewAndEditQuestionnaire.ftl?creatorId=${Session.UserContext.id}&entityId=${savingsAccountDetailDto.accountId}&event=Create&source=Savings&backPageUrl=${backPageUrl}">
        		[@spring.message "client.ViewQuestionGroupResponsesLink" /]
			</a><br/>
		    [#if containsQGForCloseSavings ]
                <a id="savingsaccountdetail.link.questionGroupsClose" href="viewAndEditQuestionnaire.ftl?creatorId=${Session.UserContext.id}&entityId=${savingsAccountDetailDto.accountId}&event=Close&source=Savings&backPageUrl=${backPageUrl}">
                	[@spring.message "Savings.ViewQuestionGroupForClosedSavingsResponsesLink" /]
                </a><br/>
            [/#if]
			<a id="savingsaccountdetail.link.viewTransactionHistory" 
				href="viewSavingsAccountTransactionHistory.ftl?globalAccountNum=${savingsAccountDetailDto.globalAccountNum}&currentFlowKey=${Request.currentFlowKey}&randomNUm=${Session.randomNUm}">
				[@spring.message "Savings.viewTransactionHistory" /]
			</a>
			<br/>
			<a id="savingsaccountdetail.link.viewChangeLog" href="savingsAction.do?method=loadChangeLog&entityType=Savings&entityId=${savingsAccountDetailDto.accountId}&currentFlowKey=${Request.currentFlowKey}">
				[@spring.message "Savings.viewChangeLog" /]
			</a><br/>
			<a id="savingsaccountdetail.link.viewStatusHistory" href="savingsAction.do?method=getStatusHistory&globalAccountNum=${savingsAccountDetailDto.globalAccountNum}&currentFlowKey=${Request.currentFlowKey}&randomNUm=${Session.randomNUm}">
				[@spring.message "Savings.viewStatusHistory" /]
			</a> 
		</span>
	</div>
</div>