<!--
 
 * loan_account_detail.jsp  version: xxx
 
 
 
 * Copyright (c) 2005-2006 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
 
 * All rights reserved.
 
 
 
 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 
 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *
 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 
 
 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 
 
 * and how it is applied. 
 
 *
 
 -->

<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>

<script language="javascript">
  function AddNote(){
	accountNotesActionForm.action="AccountNotesAction.do?method=load";
	accountNotesActionForm.submit();
  }
 function SeeAllNotes(){
	accountNotesActionForm.action="AccountNotesAction.do?method=get";
	accountNotesActionForm.submit();
  }
 function statusHistory(){
	 loanStatusActionForm.action="LoanStatusAction.do?method=search";
	 loanStatusActionForm.submit();
 }
 function changeStatus(){
	 loanStatusActionForm.action="LoanStatusAction.do?method=load";
	 loanStatusActionForm.submit();
 }
</script>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">

		<html-el:form action="/loanAction.do">

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <mifoscustom:getLoanHeader
						loanHeader='${sessionScope.header_get}' /> </span> <span
						class="fontnormal8ptbold"> <c:out
						value="${requestScope.loan.loanOffering.prdOfferingName}" /> </span>
					</td>
				</tr>
			</table>

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top"
						class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="62%" class="headingorange"><c:out
								value="${requestScope.loan.loanOffering.prdOfferingName}" />&nbsp;#
							<c:out value="${requestScope.loan.globalAccountNum}" /> <br>
							</td>
							<td width="38%" rowspan="2" align="right" valign="top" class="fontnormal"><c:if
								test="${requestScope.loan.accountStateId != 6 and requestScope.loan.accountStateId != 7 and requestScope.loan.accountStateId !=8 and requestScope.loan.accountStateId !=10}">
								<html-el:link href="javascript:changeStatus()">
									<mifos:mifoslabel name="loan.edit_acc_status" />
								</html-el:link>
							</c:if><br>
							</td>
						</tr>
					</table>
					<table width="100%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td><font class="fontnormalRedBold"> <html-el:errors
								bundle="loanUIResources" /> </font></td>
						</tr>
					</table>
					<table width="96%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td class="fontnormalbold"><span class="fontnormal"> <mifoscustom:MifosImage
								id="${requestScope.loan.accountStateId}"
								moduleName="accounts.loan" /> <mifoscustom:lookUpValue
								id="${requestScope.loan.accountStateId}"
								searchResultName="AccountStates" mapToSeperateMasterTable="true">
							</mifoscustom:lookUpValue>&nbsp; <mifoscustom:lookUpValue
								id="${requestScope.loan.accountFlagDetail.flagId}"
								searchResultName="AccountFlags" mapToSeperateMasterTable="true">
							</mifoscustom:lookUpValue> 
							</td>
						</tr>
						<tr>
							<td class="fontnormal">
							<mifos:mifoslabel name="loan.proposed_date" />: <c:out
								value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.loan.disbursementDate)}" />
						</tr>
						<tr id="Loan.PurposeOfLoan">
							<td class="fontnormal">
							<mifos:mifoslabel name="loan.business_work_act" keyhm="Loan.PurposeOfLoan" isManadatoryIndicationNotRequired="yes"/><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" isColonRequired="yes"/> <mifoscustom:lookUpValue
								id="${requestScope.loan.businessActivityId}"
								searchResultName="BusinessActivities">
							</mifoscustom:lookUpValue> </span></td>
						</tr>
					</table>
					<table width="50%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="pages/framework/images/trans.gif" width="10"
								height="10"></td>
						</tr>
					</table>
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="33%" class="headingorange"><mifos:mifoslabel
								name="loan.acc_summary" /></td>
							<td width="33%" align="right" class="fontnormal"><html-el:link
								href="accountTrxn.do?method=getInstallmentHistory&input=reviewTransactionPage&accountId=${requestScope.loan.accountId}&accountName=${requestScope.loan.loanOffering.prdOfferingName}&globalAccountNum=${requestScope.loan.globalAccountNum}
															&accountType=${requestScope.loan.accountTypeId}
															&accountStateId=${requestScope.loan.accountStateId}
															&recordOfficeId=${requestScope.loan.officeId}
															&recordLoanOfficerId=${requestScope.loan.personnelId}
															&lastPaymentAction=${requestScope.Context.businessResults['lastPaymentAction']}">
								<mifos:mifoslabel name="loan.view_schd" />
							</html-el:link></td>
						</tr>



					</table>
					<c:if
						test="${requestScope.loan.accountStateId == 5 || requestScope.loan.accountStateId == 9}">
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="58%" class="fontnormal"><mifos:mifoslabel
									name="loan.totalAmtDue" /> <c:out
									value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.LoanNextMeeetingDate)}" />:
								<c:out value="${requestScope.LoanAmountDue}" /></td>
								<%--<span
									class="fontnormal"><a href="nextPayment_loanAccount.htm"><mifos:mifoslabel name="loan.view_inst_details" /></a></span><a href="#"><span
									class="fontnormalbold"> </span></a>--%>
								<%--<c:if test="${requestScope.loan.accountStateId == 5 || requestScope.loan.accountStateId == 9}">
								<td width="42%" align="right" class="fontnormal">
									<span class="fontnormal"><html-el:link href="loanAction.do?method=getInstallmentDetails&accountId=${requestScope.loan.accountId}&accountName=${requestScope.loan.loanOffering.prdOfferingName}&globalAccountNum=${requestScope.loan.globalAccountNum}
															&accountType=${requestScope.loan.accountTypeId}
															&accountStateId=${requestScope.loan.accountStateId}
															&recordOfficeId=${requestScope.loan.officeId}
															&recordLoanOfficerId=${requestScope.loan.personnelId}"> 
										<mifos:mifoslabel name="loan.view_installment_details" /></span>
									</html-el:link>
								</td>
              				  </c:if>--%>
							</tr>
							<tr>
								<td colspan="2" class="fontnormal"><mifos:mifoslabel
									name="loan.arrear" />: <c:out
									value="${requestScope.LoanAmountInArrears}" /></td>
							</tr>
						</table>
					</c:if>
					
					
					  <table width="96%" border="0" cellpadding="3" cellspacing="0">
		                <tr>		                
		                  <td width="42%" align="right" class="fontnormal">
		                  <span class="fontnormal">
		                  	<html-el:link href="loanAccountAction.do?method=getInstallmentDetails&accountId=${requestScope.loan.accountId}&prdOfferingName=${requestScope.loan.loanOffering.prdOfferingName}&globalAccountNum=${requestScope.loan.globalAccountNum}
															&accountType=${requestScope.loan.accountTypeId}
															&accountStateId=${requestScope.loan.accountStateId}
															&recordOfficeId=${requestScope.loan.officeId}
															&recordLoanOfficerId=${requestScope.loan.personnelId}"> 
								<mifos:mifoslabel name="loan.view_installment_details" />								
							</html-el:link>
						</span>
			              </td>
		                </tr>		               
		              </table>
		              
		              
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr class="drawtablerow">
							<td width="24%">&nbsp;</td>
							<td width="20%" align="right" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="loan.original_loan" /> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}"/></td>
							<td width="28%" align="right" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="loan.amt_paid" /></td>
							<td width="28%" align="right" class="drawtablerowboldnoline">
							<mifos:mifoslabel name="${ConfigurationConstants.LOAN}"/>
							<mifos:mifoslabel
								name="loan.loan_balance" /></td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel name="loan.principal" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${requestScope.loan.loanSummary.originalPrincipal}" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${requestScope.loan.loanSummary.principalPaid}" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${requestScope.loan.loanSummary.principalDue}" /></td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${requestScope.loan.loanSummary.originalInterest}" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${requestScope.loan.loanSummary.interestPaid}" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${requestScope.loan.loanSummary.interestDue}" /></td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel name="loan.fees" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${requestScope.loan.loanSummary.originalFees}" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${requestScope.loan.loanSummary.feesPaid}" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${requestScope.loan.loanSummary.feesDue}" /></td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel name="loan.penalty" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${requestScope.loan.loanSummary.originalPenalty}" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${requestScope.loan.loanSummary.penaltyPaid}" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${requestScope.loan.loanSummary.penaltyDue}" /></td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel name="loan.total" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${requestScope.loan.loanSummary.totalLoanAmnt}" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${requestScope.loan.loanSummary.totalAmntPaid}" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${requestScope.loan.loanSummary.totalAmntDue}" /></td>
						</tr>
						<tr>
							<td colspan="4">&nbsp;</td>
						</tr>
					</table>
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="35%" class="headingorange"><mifos:mifoslabel
								name="loan.recentActivity" /></td>
							<td width="65%" align="right" class="fontnormal">&nbsp; <html-el:link
								href="loanAccountAction.do?method=getAllActivity&
										accountId=${requestScope.loan.accountId}
										&prdOfferingName=${requestScope.loan.loanOffering.prdOfferingName}&
										accountStateId=${requestScope.loan.accountStateId}&
										globalAccountNum=${requestScope.loan.globalAccountNum}&
										lastPaymentAction=${requestScope.Context.businessResults['lastPaymentAction']}">
								<mifos:mifoslabel name="loan.view_acc_activity" />
							</html-el:link></td>
						</tr>
					</table>
					<mifoscustom:mifostabletag source="recentAccountActivities"
						scope="request" xmlFileName="RecentAccountActivity.xml"
						moduleName="accounts\\loan" passLocale="true" />
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td colspan="3">&nbsp;</td>
						</tr>
					</table>
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="69%" class="headingorange"><mifos:mifoslabel
								name="loan.acc_details" /></td>
							<td width="31%" align="right" valign="top" class="fontnormal"><c:if
								test="${requestScope.loan.accountStateId != 6 && requestScope.loan.accountStateId != 7 && requestScope.loan.accountStateId !=8 && requestScope.loan.accountStateId !=10}">
								<html-el:link
									action="loanAction.do?method=manage&prdOfferingName=${requestScope.loan.loanOffering.prdOfferingName}">
									<mifos:mifoslabel name="loan.edit_acc_info" />
								</html-el:link>
							</c:if></td>
						</tr>
						<tr>
							<td height="23" colspan="2" class="fontnormal"><span
								class="fontnormalbold">
								<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" />
								<mifos:mifoslabel
								name="loan.interestRules" /></span> <span class="fontnormal"><br>
							<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" />	
							<mifos:mifoslabel name="loan.interest_type" />:&nbsp;<mifoscustom:lookUpValue
								id="${requestScope.loan.interestTypeId}"
								searchResultName="InterestTypes" mapToSeperateMasterTable="true">
							</mifoscustom:lookUpValue><br>
							<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" />
							<mifos:mifoslabel name="loan.interest_amount" />:&nbsp;<span
								class="fontnormal"><c:out
								value="${requestScope.loan.interestRateAmount}" />%&nbsp;<mifos:mifoslabel
								name="loan.apr" /> </span><br>
							</span> 
							<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" />
							<mifos:mifoslabel name="loan.interest_disb" />:<c:choose>
								<c:when test="${requestScope.loan.intrestAtDisbursement eq 1}">
									<mifos:mifoslabel name="loan.yes" />
								</c:when>
								<c:otherwise>
									<mifos:mifoslabel name="loan.no" />
								</c:otherwise>
							</c:choose><br>
							<%--mifos:mifoslabel name="loan.interest_cal_payments" />:&nbsp; <mifoscustom:lookUpValue
								id="${requestScope.loan.loanOffering.interestCalcRule.interestCalcRuleId}"
								searchResultName="InterestCalcRule"
								mapToSeperateMasterTable="true">
							</mifoscustom:lookUpValue><br--%>
							<br>
							<span class="fontnormalbold"> <mifos:mifoslabel
								name="loan.repaymentRules" /> </span><br>
							<mifos:mifoslabel name="loan.freq_of_inst" />:&nbsp;<c:out
								value="${requestScope.loan.loanOffering.prdOfferingMeeting.meeting.meetingDetails.recurAfter}" />
							<c:choose>
								<c:when
									test="${requestScope.loan.loanOffering.prdOfferingMeeting.meeting.meetingDetails.recurrenceType.recurrenceId == '1'}">
									<mifos:mifoslabel name="loan.week(s)" />
								</c:when>
								<c:otherwise>
									<mifos:mifoslabel name="loan.month(s)" />
								</c:otherwise>
							</c:choose> <br>
							<mifos:mifoslabel name="loan.principle_due" />:<c:choose>
								<c:when
									test="${requestScope.loan.loanOffering.prinDueLastInstFlag eq 1}">
									<mifos:mifoslabel name="loan.yes" />
								</c:when>
								<c:otherwise>
									<mifos:mifoslabel name="loan.no" />
								</c:otherwise>
							</c:choose> <br>
							<mifos:mifoslabel name="loan.penalty_type" />:&nbsp;<c:out
								value="${requestScope.loan.loanOffering.penalty.penaltyType}" /><br>
							<mifos:mifoslabel name="loan.grace_period_type" />:&nbsp;<mifoscustom:lookUpValue
								id="${requestScope.loan.gracePeriodTypeId}"
								searchResultName="GracePeriodTypes"
								mapToSeperateMasterTable="true">
							</mifoscustom:lookUpValue><br>
							<mifos:mifoslabel name="loan.no_of_inst" />:&nbsp;<c:out
								value="${requestScope.loan.noOfInstallments}" /> <mifos:mifoslabel
								name="loan.allowed_no_of_inst" />&nbsp;<c:out
								value="${requestScope.loan.loanOffering.minNoInstallments}" />
							-&nbsp;<c:out
								value="${requestScope.loan.loanOffering.maxNoInstallments}" />
							)<br>
							<mifos:mifoslabel name="loan.grace_period" />:&nbsp;<c:out
								value="${requestScope.loan.gracePeriodDuration}" />&nbsp;<mifos:mifoslabel
								name="loan.inst" /><br>
							<mifos:mifoslabel name="loan.source_fund" />:&nbsp;<c:out
								value="${loanfn:getSourcesOfFund(requestScope.loan.loanOffering.loanOffeingFundSet)}" /><br>
						</td>
						</tr>
					</table>
					<table width="96%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td class="fontnormal"><br>
							<span class="fontnormalbold"><mifos:mifoslabel
								name="loan.collateralDetails" /></span>
						</td>
						</tr>
						<tr id="Loan.CollateralType">
							<td class="fontnormal">
							<mifos:mifoslabel name="loan.collateral_type" keyhm="Loan.CollateralType" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/>&nbsp;<mifoscustom:lookUpValue
								id="${requestScope.loan.collateralTypeId}"
								searchResultName="CollateralTypes"
								mapToSeperateMasterTable="true">
							</mifoscustom:lookUpValue>
						</td>
						</tr>
						<tr id="Loan.CollateralNotes">
							<td class="fontnormal">
							<br>
							<mifos:mifoslabel name="loan.collateral_notes" keyhm="Loan.CollateralNotes" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/>&nbsp;<br>
							<c:out value="${requestScope.loan.collateralNote}" />
						</td>
						</tr>
						<tr>
							<td class="fontnormal"><br>
							<span class="fontnormalbold"><mifos:mifoslabel
								name="loan.additionalInfo" /></span> <span class="fontnormal"><br>
							</span> <br>
							<span class="fontnormalbold"> <mifos:mifoslabel
								name="loan.recurring_acc_fees" /><br>
							</span> <c:forEach items="${requestScope.loan.accountFeesSet}"
								var="feesSet">
								<c:if
									test="${feesSet.fees.feeFrequency.feeFrequencyTypeId == '1' && feesSet.feeStatus != '2'}">
									<c:out value="${feesSet.fees.feeName}" />:
										<span class="fontnormal"> <c:out
										value="${feesSet.accountFeeAmount}" />&nbsp;( <mifos:mifoslabel
										name="loan.periodicityTypeRate" /> <c:out
										value="${feesSet.fees.feeFrequency.feeMeetingFrequency.feeMeetingSchedule}" />)
									<html-el:link
										href="accountAppAction.do?method=removeFees&feeId=${feesSet.fees.feeId}
														&accountId=${requestScope.loan.accountId}&recordOfficeId=${requestScope.loan.officeId}
														&recordLoanOfficerId=${requestScope.loan.personnelId}&createdDate=${requestScope.loan.createdDate}"> 
														Remove
										    </html-el:link> <br>
									</span>
								</c:if>
							</c:forEach><br>
							<%--	<span class="fontnormal"><a href="loan_account_detail_partial.htm">Detail
										- partial/pending/cancelled</a><br>
										<a href="loan_account_detail_closed.htm">Detail - closed</a></span>
										<br> --%></td>
						</tr>
					</table>
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="66%" class="headingorange"><mifos:mifoslabel
								name="loan.more_details" /></td>
						</tr>
						<tr>
							<td class="fontnormal"><%--
									<html-el:link href="transaction_history_loanAccount.htm"> <mifos:mifoslabel name="loan.view_transc_history" />
									</html-el:link><br>--%> <span class="fontnormal"> <html-el:link
								href="loanAction.do?method=search&input=LoanChangeLog
														&prdOfferingName=${requestScope.loan.loanOffering.prdOfferingName}
														&accountId=${requestScope.loan.accountId}&recordOfficeId=${requestScope.loan.officeId}
														&recordLoanOfficerId=${requestScope.loan.personnelId}&createdDate=${requestScope.loan.createdDate}">
								<mifos:mifoslabel name="loan.view_change_log" />
							</html-el:link> <br>
							<html-el:link href="javascript:statusHistory()">
								<mifos:mifoslabel name="loan.view_status_history" />
							</html-el:link><br>
							<html-el:link href="accountAppAction.do?method=getTrxnHistory&input=LoanDetails&globalAccountNum=${requestScope.loan.globalAccountNum}&accountId=${requestScope.loan.accountId}&prdOfferingName=${requestScope.loan.loanOffering.prdOfferingName}">
                    	<mifos:mifoslabel name="Center.TransactionHistory" />
                    	</html-el:link>
							 </span></td>
						</tr>
					</table>
					</td>
					<td width="30%" align="left" valign="top" class="paddingleft1">
					<table width="100%" border="0" cellpadding="2" cellspacing="0"
						class="orangetableborder">
						<tr>
							<td class="orangetablehead05"><span class="fontnormalbold"><mifos:mifoslabel
								name="loan.trxn" /></span></td>
						</tr>
						<tr>
							<td class="paddingL10"><c:choose>
								<c:when
									test="${requestScope.loan.accountStateId=='5' || requestScope.loan.accountStateId=='7' || requestScope.loan.accountStateId=='8' || requestScope.loan.accountStateId=='9'}">
									<span class="fontnormal8pt"> 
										<c:if test="${(requestScope.loan.accountStateId=='5' || requestScope.loan.accountStateId=='9')}">
											<html-el:link href="applyPaymentAction.do?method=load&input=loan&prdOfferingName=${requestScope.loan.loanOffering.prdOfferingName}&globalAccountNum=${requestScope.loan.globalAccountNum}&accountId=${requestScope.loan.accountId}&accountType=${requestScope.loan.accountTypeId}
																	&recordOfficeId=${requestScope.loan.officeId}&recordLoanOfficerId=${requestScope.loan.personnelId}">
												<mifos:mifoslabel name="loan.apply_payment" />
											</html-el:link><br> 
										</c:if>
									 <html-el:link
										href="AccountsApplyChargesAction.do?method=load&input=reviewTransactionPage&accountId=${requestScope.loan.accountId}&globalAccountNum=${requestScope.loan.globalAccountNum}&prdOfferingName=${requestScope.loan.loanOffering.prdOfferingName}">
										<mifos:mifoslabel name="loan.apply_charges" />
									</html-el:link><br>
									<c:choose>
										<c:when
											test="${(requestScope.loan.accountStateId=='5' || requestScope.loan.accountStateId=='9') }">
											<c:if test="${requestScope.Context.businessResults['lastPaymentAction'] != '10'}">
												<html-el:link
													href="applyAdjustment.do?method=loadAdjustment&accountId=${requestScope.loan.accountId}&globalAccountNum=${requestScope.loan.globalAccountNum}&prdOfferingName=${requestScope.loan.loanOffering.prdOfferingName}">
													<mifos:mifoslabel name="loan.apply_adjustment" />
												</html-el:link>
											<br>
											</c:if>
										</c:when>
									</c:choose> </span>


								</c:when>
							</c:choose> <c:choose>
								<c:when
									test="${requestScope.loan.accountStateId=='1' || requestScope.loan.accountStateId=='2' || requestScope.loan.accountStateId=='3' || requestScope.loan.accountStateId=='4'}">

									<span class="fontnormal8pt"> <html-el:link
										href="AccountsApplyChargesAction.do?method=load&input=reviewTransactionPage&accountId=${requestScope.loan.accountId}&globalAccountNum=${requestScope.loan.globalAccountNum}&prdOfferingName=${requestScope.loan.loanOffering.prdOfferingName}">
										<mifos:mifoslabel name="loan.apply_charges" />
									</html-el:link><br>
									</span>
								</c:when>
							</c:choose> <%--c:choose>
								<c:when test="${requestScope.loan.accountStateId=='3' || requestScope.loan.accountStateId=='4'}">
									<tr>
										<td class="paddingL10"><span class="fontnormal8pt">
											<html-el:link href="loanDisbursmentAction.do?method=load&accountId=${requestScope.loan.accountId}&globalAccountNum=${requestScope.loan.globalAccountNum}&prdOfferingName=${requestScope.loan.loanOffering.prdOfferingName}"> 
												<mifos:mifoslabel name="loan.disburseloan" />
											</html-el:link>
											<br></span>
										</td>
									</tr>
								</c:when>
							</c:choose--%> <c:choose>
								<c:when
									test="${ requestScope.loan.accountStateId=='9' || requestScope.loan.accountStateId=='5'}">
									<span class="fontnormal8pt"> <html-el:link
										href="repayLoanAction.do?method=loadRepayment&accountId=${requestScope.loan.accountId}&globalAccountNum=${requestScope.loan.globalAccountNum}&prdOfferingName=${requestScope.loan.loanOffering.prdOfferingName}">
										<mifos:mifoslabel name="loan.repay" /><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
									</html-el:link><br>
									</span>
								</c:when>
							</c:choose></td>
						<tr>
					</table>
					<table width="95%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="pages/framework/images/trans.gif" width="7"
								height="8"></td>
						</tr>
					</table>
					<table width="100%" border="0" cellpadding="2" cellspacing="0"
						class="bluetableborder">
						<tr>
							<td class="bluetablehead05"><span class="fontnormalbold"> <mifos:mifoslabel
								name="loan.performance_history" /> </span></td>
						</tr>
					</table>
					<table width="95%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="pages/framework/images/trans.gif" width="7"
								height="8"></td>
						</tr>
					</table>
					<table width="100%" border="0" cellpadding="2" cellspacing="0"
						class="bluetableborder">
						<tr>
							<td class="bluetablehead05"><span class="fontnormalbold"> <mifos:mifoslabel
								name="loan.recent_notes" /> </span></td>
						</tr>
						<tr>
							<td class="paddingL10"><img
								src="pages/framework/images/trans.gif" width="10" height="2"></td>
						</tr>
						<tr>
							<td class="paddingL10"><c:choose>
								<c:when test="${!empty requestScope.notes}">
									<c:forEach var="note" items="${requestScope.notes}">
										<span class="fontnormal8ptbold"> <c:out
											value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,note.commentDate)}" />:</span>
										<span class="fontnormal8pt"> <c:out value="${note.comment}" />
										-<em> <c:out value="${note.officer.displayName}" /> </em><br>
										<br>
										</span>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<span class="fontnormal"> <mifos:mifoslabel
										name="loan.NoNotesAvailable" /> </span>
								</c:otherwise>
							</c:choose></td>
						</tr>
						<tr>
							<td align="right" class="paddingleft05"><span
								class="fontnormal8pt"> <c:if test="${!empty requestScope.notes}">
								<html-el:link href="javascript:SeeAllNotes()">
									<mifos:mifoslabel name="loan.seeallnotes" />
								</html-el:link>
							</c:if> <br>
							<html-el:link href="javascript:AddNote()">
								<mifos:mifoslabel name="loan.addnote" />
							</html-el:link> </span></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			<mifos:SecurityParam property="Loan" />
			<!-- This hidden variable is being used in the next page -->
			<html-el:hidden property="accountTypeId"
				value="${requestScope.loan.accountTypeId}" />
			<html-el:hidden property="accountId"
				value="${requestScope.loan.accountId}" />
			<html-el:hidden
				value='${sessionScope.loanAccounts_Context.businessResults["loanAccGlobalNum"]}'
				property="globalAccountNum" />
		</html-el:form>
		<html-el:form action="AccountNotesAction.do?method=load">
			<html-el:hidden property="accountId"
				value="${requestScope.loan.accountId}" />
			<html-el:hidden property="accountTypeId"
				value="${requestScope.loan.accountTypeId}" />
			<html-el:hidden property="accountName"
				value="${requestScope.loan.loanOffering.prdOfferingName}" />
			<html-el:hidden value='${requestScope.loan.globalAccountNum}'
				property="globalAccountNum" />
		</html-el:form>
		<html-el:form action="LoanStatusAction.do?method=load">
			<html-el:hidden property="accountId"
				value="${requestScope.loan.accountId}" />
			<html-el:hidden property="globalAccountNum"
				value="${requestScope.loan.globalAccountNum}" />
			<html-el:hidden property="accountName"
				value="${requestScope.loan.loanOffering.prdOfferingName}" />
			<html-el:hidden property="versionNo"
				value="${requestScope.loan.versionNo}" />
			<html-el:hidden property="currentStatusId"
				value="${requestScope.loan.accountStateId}" />
		</html-el:form>

	</tiles:put>
</tiles:insert>
