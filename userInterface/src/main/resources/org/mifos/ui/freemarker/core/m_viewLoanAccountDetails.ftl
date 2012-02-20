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

<span id="page.id" title="LoanAccountDetail"></span>

<div class="content" style="width: 350px">
	<div>
		<span class="headingorange">
			${loanInformationDto.prdOfferingName}&nbsp;# 
			<span id="loanaccountdetail.text.loanid">
				${loanInformationDto.globalAccountNum}
			</span><br/>
		</span>
		<span class="fontnormal">
			[#if loanInformationDto.accountStateId != 6 && loanInformationDto.accountStateId != 7 && loanInformationDto.accountStateId !=8 && loanInformationDto.accountStateId !=10 ]
				<a id="loanaccountdetail.link.editAccountStatus"
				href="editStatusAction.do?method=load&accountId=${loanInformationDto.accountId}&randomNUm=${Session.randomNUm}&currentFlowKey=${Request.currentFlowKey}">
					[@spring.message "loan.edit_acc_status" /]
				</a>
			[/#if]
		</span><br/>
		<span class="fontnormal">
			[@mifostag.MifosImage id="${loanInformationDto.accountStateId}" moduleName="org.mifos.accounts.util.resources.accountsImages" /] 
			<span id="loanaccountdetail.text.status">
				${loanInformationDto.accountStateName}&nbsp; 
				[#list loanInformationDto.accountFlagNames as flagSet ]
					<span class="fontnormal">
						${flagSet}
					</span>
				[/#list] 
			</span>
		</span><br/>
		<span class="fontnormal">
			[@spring.message "loan.proposed_date" /]: 
			<span id="loanaccountdetail.details.disbursaldate">
				${i18n.date_formatter(loanInformationDto.disbursementDate, "dd/MM/yyyy", Application.LocaleSetting.locale)}:
			[#if loanInformationDto.redone ]
                &nbsp([@spring.message "loan.is_redo_loan" /])
            [/#if]
		</span><br/>
		<span id="Loan.PurposeOfLoan">
			<span class="fontnormal">
				[@spring.message "loan.business_work_act" /] [@spring.message "${ConfigurationConstants.LOAN}" /]:
				[#--
				<c:forEach var="busId"
					items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessActivities')}">
					<c:if test="${busId.id eq loanInformationDto.businessActivityId}">
						<span id="loanaccountdetail.text.purposeofloan"><c:out value="${busId.name}" /></span>
					</c:if>
				</c:forEach>
				--]
			</span>
		</span>	
	</div>
	<div>
		<img src="pages/framework/images/trans.gif" width="10" height="10">
	</div>
	<div>
		<span class="headingorange">
		[@spring.message "loan.acc_summary" /]
		</span><br/>
		<span class="fontnormal"> 
			<a id="loanaccountdetail.link.viewRepaymentSchedule"
				href="loanAccountAction.do?method=getLoanRepaymentSchedule&input=reviewTransactionPage&accountId=${loanInformationDto.accountId}&prdOfferingName=${loanInformationDto.prdOfferingName}&globalAccountNum=${loanInformationDto.globalAccountNum}&accountType=${loanInformationDto.accountTypeId}&accountStateId=${loanInformationDto.accountStateId}&recordOfficeId=${loanInformationDto.officeId}&recordLoanOfficerId=${loanInformationDto.personnelId}&lastPaymentAction=${loanInformationDto.accountId}&randomNUm=${Session.randomNUm}&currentFlowKey=${Request.currentFlowKey}">
				[@spring.message "loan.view_schd" /]
			</a>
		</span><br/>
		[#if loanInformationDto.accountStateId == 5 || loanInformationDto.accountStateId == 9 ]
		<span class="fontnormal">
			[@spring.message "loan.totalAmtDue" /] ${i18n.date_formatter(loanInformationDto.nextMeetingDate, "dd/MM/yyyy", Application.LocaleSetting.locale)}: ${loanInformationDto.totalAmountDue}
		</span><br/>
		<span class="fontnormal">
			[@spring.message "loan.arrear" /]: ${loanInformationDto.totalAmountInArrears?number}
		</span><br/>
		[/#if]
		[#if loanInformationDto.accountStateId == 5 || loanInformationDto.accountStateId == 9 ]
		<span class="fontnormal"> 
			<a id="loanaccountdetail.link.viewInstallmentDetails"
			href="loanAccountAction.do?method=getInstallmentDetails&accountId=${loanInformationDto.accountId}&prdOfferingName=${loanInformationDto.prdOfferingName}&globalAccountNum=${loanInformationDto.globalAccountNum}&accountType=${loanInformationDto.accountTypeId}&accountStateId=${loanInformationDto.accountStateId}&recordOfficeId=${loanInformationDto.officeId}&recordLoanOfficerId=${loanInformationDto.personnelId}&lastPaymentAction=${loanInformationDto.accountId}&randomNUm=${Session.randomNUm}&currentFlowKey=${Request.currentFlowKey}">
				[@spring.message "loan.view_installment_details" /]
			</a>
		</span><br/>
		[/#if]
		<div>
			<table width="96%" border="0" cellpadding="3" cellspacing="0" id="loanSummaryTable">
				<tr class="drawtablerow">
					<td width="24%">&nbsp;</td>
					<td width="20%" align="right" class="drawtablerowboldnoline">
						[#assign arguments = ["${springMacroRequestContext.getMessage(ConfigurationConstants.LOAN)}"]/]
						[@spring.messageArgs "loan.originalLoan" arguments/] 
					</td>
					<td width="28%" align="right" class="drawtablerowboldnoline">
						[@spring.message "loan.amt_paid" /]
					</td>
					<td width="28%" align="right" class="drawtablerowboldnoline">
						[#assign arguments = ["${springMacroRequestContext.getMessage(ConfigurationConstants.LOAN)}"]/]
						[@spring.messageArgs "loan.loanBalance" arguments/] 
					</td>
				</tr>
				<tr>
					<td class="drawtablerow">
						[@spring.message "loan.principal" /]
					</td>
					<td align="right" class="drawtablerow">
						${loanInformationDto.loanSummary.originalPrincipal?number}
					</td>
					<td align="right" class="drawtablerow">
						${loanInformationDto.loanSummary.principalPaid?number}
					</td>
					<td align="right" class="drawtablerow">
						${loanInformationDto.loanSummary.principalDue?number}
					</td>
				</tr>
				<tr>
					<td class="drawtablerow">
						[@spring.message "${ConfigurationConstants.INTEREST}" /]
					</td>
					<td align="right" class="drawtablerow">
						${loanInformationDto.loanSummary.originalInterest?number}
					</td>
					<td align="right" class="drawtablerow">
						${loanInformationDto.loanSummary.interestPaid?number}
					<td align="right" class="drawtablerow">
						${loanInformationDto.loanSummary.interestDue?number}
					</td>
				</tr>
				<tr>
					<td class="drawtablerow">
						[@spring.message "loan.fees" /]
					</td>
					<td align="right" class="drawtablerow" id="LoanAccountDetail.text.loanFees">
						${loanInformationDto.loanSummary.originalFees?number}
					</td>
					<td align="right" class="drawtablerow">
						${loanInformationDto.loanSummary.feesPaid?number}
					</td>
					<td align="right" class="drawtablerow">
						${loanInformationDto.loanSummary.feesDue?number}
					</td>
				</tr>
				<tr>
					<td class="drawtablerow">
						[@spring.message "loan.penalty" /]
					</td>
					<td align="right" class="drawtablerow">
						${loanInformationDto.loanSummary.originalPenalty?number}
					</td>
					<td align="right" class="drawtablerow">
						${loanInformationDto.loanSummary.penaltyPaid?number}
					</td>
					<td align="right" class="drawtablerow">
						${loanInformationDto.loanSummary.penaltyDue?number}
					</td>
				</tr>
				<tr>
					<td class="drawtablerow">
						[@spring.message "loan.total" /]
					</td>
					<td align="right" class="drawtablerow">
						${loanInformationDto.loanSummary.totalLoanAmnt?number}
					</td>
					<td align="right" class="drawtablerow">
						${loanInformationDto.loanSummary.totalAmntPaid?number}
					</td>
					<td align="right" class="drawtablerow">
						${loanInformationDto.loanSummary.totalAmntDue?number}
					</td>
				</tr>
				<tr>
					<td colspan="4">&nbsp;</td>
				</tr>
			</table>
		</div>
		<div>
			[#if loanInformationDto.accountStateId == 3 || loanInformationDto.accountStateId == 4 || loanInformationDto.accountStateId == 5
			|| loanInformationDto.accountStateId == 6 || loanInformationDto.accountStateId == 7 || loanInformationDto.accountStateId == 8 || loanInformationDto.accountStateId == 9 ]
				<span class="headingorange">
					[@spring.message "loan.recentActivity" /]
				</span><br/>
				[#if loanInformationDto.loanActivityDetails == true]
					</span class="fontnormal">
						<a id="loanaccountdetail.link.viewAccountActivity"
							href="loanAccountAction.do?method=getAllActivity&accountId=${loanInformationDto.accountId}&prdOfferingName=${loanInformationDto.prdOfferingName}&accountStateId=${loanInformationDto.accountStateId}&globalAccountNum=${loanInformationDto.globalAccountNum}&lastPaymentAction=${loanInformationDto.accountId}&accountType=${loanInformationDto.accountTypeId}&randomNUm=${Session.randomNUm}&currentFlowKey=${Request.currentFlowKey}">
						[@spring.message "loan.view_acc_activity" /]
						</a>
					</span><br/>
				[/#if]
				<div>
					[@mifoscustom.mifostabletag source="recentAccountActivities" scope="session" xmlFileName="RecentAccountActivity.xml" moduleName="org/mifos/accounts/loan/util/resources" passLocale="true" /] 
				</div>
			[/#if]
		</div>
	</div>
	<div>
		<div>
			<span class="headingorange">
				[@spring.message "loan.acc_details" /]
			</span><br/>
			<span class="fontnormal">
				[#if loanInformationDto.accountStateId != 6 && loanInformationDto.accountStateId != 7 && loanInformationDto.accountStateId !=8 && loanInformationDto.accountStateId !=10 ]
					<a id="loanaccountdetail.link.editAccountInformation"
					href="loanAccountAction.do?method=manage&customerId=${loanInformationDto.customerId}&globalAccountNum=${loanInformationDto.globalAccountNum}&randomNUm=${Session.randomNUm}&currentFlowKey=${Request.currentFlowKey}">
						[@spring.message "loan.edit_acc_info" /]
					</a>
				[/#if]
			</span><br/>
			<span class="fontnormalbold">
				[#assign arguments = ["${springMacroRequestContext.getMessage(ConfigurationConstants.INTEREST)}"]/]
				[@spring.messageArgs "loan.interestRules" arguments/] 
			</span><br/> 
			<span class="fontnormal">
				[@spring.messageArgs "loan.interestRateType" arguments/]:&nbsp; ${loanInformationDto.interestTypeName?if_exists} <br/>
				[@spring.messageArgs "loan.interestRate" arguments/]:&nbsp; 
				<span id="loanaccountdetail.text.interestRate">	
					${loanInformationDto.interestRate?if_exists?number} [@spring.message "loan.apr" /]
				</span><br/>
				[@spring.messageArgs "loan.interestDisbursement" arguments/]:
				[#if loanInformationDto.interestDeductedAtDisbursement]
					[@spring.message "loan.yes" /]
				[#else]
					[@spring.message "loan.no" /]
				[/#if]<br/>
				<br/>
				<span class="fontnormalbold"> 
					[@spring.message "loan.repaymentRules" /] 
				</span><br/>
				[@spring.message "loan.freq_of_inst" /]:&nbsp;
				${loanInformationDto.recurAfter?if_exists}
				[#if loanInformationDto.recurrenceId == 1 ]
					[@spring.message "loan.week(s)" /]
				[#else]
					[@spring.message "loan.month(s)" /]
				[/#if]<br/>
				[@spring.message "loan.principle_due" /]:
				[#if loanInformationDto.prinDueLastInst == true ]
					[@spring.message "loan.yes" /]
				[#else]
					[@spring.message "loan.no" /]
				[/#if] <br/>
				[@spring.message "loan.grace_period_type" /]:&nbsp; 
				${loanInformationDto.gracePeriodTypeName?if_exists}<br/>
				[@spring.message "loan.no_of_inst" /]:&nbsp;
				<span id="loanaccountdetail.text.noOfInst">
					${loanInformationDto.noOfInstallments?if_exists?number}
				</span> 
				[@spring.message "loan.allowed_no_of_inst" /]&nbsp;${loanInformationDto.minNoOfInstall?if_exists} -&nbsp;${loanInformationDto.maxNoOfInstall?if_exists})
				<br/>
				[@spring.message "loan.grace_period" /]:&nbsp;
				${loanInformationDto.gracePeriodDuration?if_exists?number}&nbsp;[@spring.message "loan.inst" /]<br/>
				[@spring.message "loan.source_fund" /]:&nbsp; ${loanInformationDto.fundName?if_exists}<br/>
			</span>
		</div>
		[#--
		<div>
			[#if loanInformationDto.group == true ]
				[#if loanAccountDetailsView?has_content ]
                    [#if loanInformationDto.disbursed ]
						[@mifoscustom.mifostabletag source="loanAccountDetailsView" scope="session"
						xmlFileName="LoanAccountDetails.xml" moduleName="org/mifos/accounts/loan/util/resources"
						passLocale="true" randomNUm="${Session.randomNUm}"
						currentFlowKey="${Request.currentFlowKey}" /]
                    [#else]
                        [@mifoscustom.mifostabletag source="loanAccountDetailsView" scope="session"
                      	xmlFileName="LoanAccountDetails.xml" moduleName="org/mifos/accounts/loan/util/resources"
                      	passLocale="true"/]
                	[/#if]
				</#if>
			</#if> 
		</div>
		--]
		<div>
			<img src="pages/framework/images/trans.gif" width="10" height="10">
		</div>
        <div id="Loan.ExternalId">
            <span class="fontnormalbold">
            	[@spring.message "accounts.externalId" /]:
           		<span id="loanaccountdetail.text.externalid" class="fontnormal">${loanInformationDto.externalId?if_exists}
       		</span> 
        </div>
    	<div>
			<img src="pages/framework/images/trans.gif" width="10" height="10">
		</div>
		<div>
			<span class="fontnormalbold"> 
				[@spring.message "loan.recurring_acc_fees" /]
			</span><br/> 
			<span>
				[#list loanInformationDto.accountFees as feesSet]
					[#if (feesSet.feeFrequencyTypeId?has_content && feesSet.feeStatus?has_content) && feesSet.feeFrequencyTypeId == 1 && feesSet.feeStatus != 2]
						<span class="fontnormal">
							${feesSet.feeName}: ${feesSet.accountFeeAmount?number}&nbsp;( [@spring.message "loan.periodicityTypeRate" /] ${feesSet.meetingRecurrence})
							<a id="loanaccountdetail.link.removeFee"
							href="accountAppAction.do?method=removeFees&feeId=${feesSet.feeId}&globalAccountNum=${loanInformationDto.globalAccountNum}&accountId=${loanInformationDto.accountId}&recordOfficeId=${loanInformationDto.officeId}&recordLoanOfficerId=${loanInformationDto.personnelId}&createdDate=${loanInformationDto.createdDate}&randomNUm=${Session.randomNUm}&currentFlowKey=${Request.currentFlowKey}&input=Loan">
								[@spring.message "loan.remove" /]
							</a> 
						</span><br/>
					[/#if]
				 [/#list]<br/>
 			</span>
			<span class="fontnormalbold"> 
				[@spring.message "loan.one_time_acc_fees" /]
			</span><br/>
			[#assign status = 0 /] 
			[#list loanInformationDto.accountFees as feesSet]
				[#if (feesSet.feeFrequencyTypeId?has_content && feesSet.feeStatus?has_content) && feesSet.feeFrequencyTypeId == 2 && feesSet.feeStatus != 2]
					<span id="loanAccountDetail.text.oneTimeFeeName_${status?number}"/>
						${feesSet.feeName}
					</span>:
					<span class="fontnormal"> 
						${feesSet.accountFeeAmount?number}&nbsp;
						<!-- if account state is LOAN_PARTIAL_APPLICATION or LOAN_PENDING_APPROVAL then enable removal -->
						[#if loanInformationDto.accountStateId == 1 || loanInformationDto.accountStateId == 2 ]					
							<a id="loanAccountDetail.link.removeOneTimeFee_${status}"
							href="accountAppAction.do?method=removeFees&feeId=${feesSet.feeId}&globalAccountNum=${loanInformationDto.globalAccountNum}&accountId=${loanInformationDto.accountId}&recordOfficeId=${loanInformationDto.officeId}&recordLoanOfficerId=${loanInformationDto.personnelId}&createdDate=${loanInformationDto.createdDate}&randomNUm=${Session.randomNUm}&currentFlowKey=${Request.currentFlowKey}&input=Loan">
								[@spring.message "loan.remove" /]
							</a> 
						[/#if] <br/>
					</span>
				[/#if]
				[#assign status = status + 1 /] 
			[/#list]<br/>							
            <span class="fontnormalbold">
                [@spring.message "loan.recurring_acc_penalties" /]
                <br/>
            </span>
            [#list loanInformationDto.accountPenalties as penaltySet ]
                [#if (penaltySet.penaltyFrequencyId?has_content && penaltySet.penaltyStatus?has_content) && penaltySet.penaltyFrequencyId != 1 && penaltySet.penaltyStatus != 2]
                    ${penaltySet.penaltyName}:
                    <span class="fontnormal">
                        ${penaltySet.accountPenaltyAmount?number}&nbsp; (${penaltySet.penaltyFrequencyName})
                    </span>
                [/#if]
            [/#list]
            <br/>
            <span class="fontnormalbold">
                [@spring.message "loan.one_time_acc_penalties" /]
            </span><br/>
            [#assign status = 0 /]
            [#list loanInformationDto.accountPenalties as penaltySet]
            	[#if (penaltySet.penaltyFrequencyId?has_content && penaltySet.penaltyStatus?has_content) && penaltySet.penaltyFrequencyId == 1 && penaltySet.penaltyStatus != 2 ]
                    <span id="loanAccountDetail.text.oneTimePenaltyName_${status?number}"/>
                        ${penaltySet.penaltyName}
                    </span>:
                    <span class="fontnormal">
                        ${penaltySet.accountPenaltyAmount?number}&nbsp;
                    </span><br/>
                [/#if]
				[#assign status = status + 1 /] 
            [/#list]
            <br/>
		</div>
		<div>
			<span class="headingorange">
				[@spring.message "loan.more_details" /]
			</span><br/>
			<span class="fontnormal">
	            <a id="loanaccountdetail.link.questionGroups" href="loanAccountAction.do?method=viewAndEditAdditionalInformation&creatorId=${Session.UserContext.id}&entityId=${loanInformationDto.accountId}&event=Create&source=Loan&backPageUrl=${backPageUrl}">
	    			[@spring.message "client.ViewQuestionGroupResponsesLink" /]
				</a>
	            <br/>
                [#if containsQGForCloseLoan]
	                <a id="loanaccountdetail.link.questionGroupsClose" href="viewAndEditQuestionnaire.ftl?creatorId=${Session.UserContext.id}&entityId=${loanInformationDto.accountId}&event=Close&source=Loan&backPageUrl=${backPageUrl}">
	                	[@spring.message "loan.ViewQuestionGroupForClosedLoanResponsesLink" /]
	                </a> <br/>
                [/#if]
			    <a id="loanaccountdetail.link.viewStatusHistory"
		          href="loanAccountAction.do?method=viewStatusHistory&globalAccountNum=${loanInformationDto.globalAccountNum}&randomNUm=${Session.randomNUm}&currentFlowKey=${Request.currentFlowKey}">
		          [@spring.message "loan.view_status_history" /]
	            </a>
	            <br/>
				<a id="loanaccountdetail.link.viewChangeLog"
				href="loanAccountAction.do?method=loadChangeLog&entityType=Loan&entityId=${loanInformationDto.accountId}&currentFlowKey=${Request.currentFlowKey}">
					[@spring.message "loan.view_change_log" /]
				</a>
				<br/>
				<a id="loanaccountdetail.link.viewTransactionHistory"
				href="viewLoanAccountTransactionHistory.ftl?globalAccountNum=${loanInformationDto.globalAccountNum}&accountId=${loanInformationDto.accountId}&prdOfferingName=${loanInformationDto.prdOfferingName}&randomNUm=${Session.randomNUm}&currentFlowKey=${Request.currentFlowKey}">
					[@spring.message "Center.TransactionHistory" /]
				</a> 
			</span>
		</div>
	</div>
</div>