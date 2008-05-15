<%-- 
Copyright (c) 2005-2008 Grameen Foundation USA
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing
permissions and limitations under the License.

See also http://www.apache.org/licenses/LICENSE-2.0.html for an
explanation of the license and how it is applied.
--%>
<!-- loancretiondetails.jsp -->

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
		<SCRIPT type="text/javascript" SRC="pages/application/loan/js/CreateLoanAccount.js"></SCRIPT>
		<SCRIPT type="text/javascript" SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<SCRIPT type="text/javascript" SRC="pages/framework/js/date.js"></SCRIPT>
		<script type="text/javascript" src="pages/application/loan/js/loan.js"></script>
		<script type="text/javascript" src="pages/application/meeting/js/meeting.js"></script>
		<script type="text/javascript" src="pages/framework/js/conversion.js"></script>
		<script type="text/javascript" src="pages/framework/js/con_en.js"></script>
		<script type="text/javascript" src="pages/framework/js/con_${sessionScope["UserContext"].currentLocale}.js"></script>
		
        <c:if test="${requestScope.perspective == 'redoLoan'}">
            <SCRIPT>
            function fun_cancel(form)
            {
                location.href="AdminAction.do?method=load";
            }
            </SCRIPT>
		</c:if>

        <fmt:setLocale value='${sessionScope["LOCALE"]}'/>
		<fmt:setBundle basename="org.mifos.config.localizedResources.LoanUIResources"/>


		<!-- html-el:form action="/loanAccountAction.do?method=schedulePreview" onsubmit="return (validateMyForm(disbursementDate,disbursementDateFormat,disbursementDateYY));"-->
		<html-el:form action="/loanAccountAction.do?method=schedulePreview">
			<c:set
				value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanOffering')}"
				var="LoanOffering" />
			<c:set
				value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanIndividualMonitoringIsEnabled')}"
				var="loanIndividualMonitoringIsEnabled" />
			<c:set
				value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanaccountownerisagroup')}"
				var="loanaccountownerisagroup" />
			<c:set
				value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'repaymentSchedulesIndependentOfMeetingIsEnabled')}"
				var="repaymentSchedulesIndependentOfMeetingIsEnabled" />

			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="470" align="left" valign="top" bgcolor="#FFFFFF">
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td align="center" class="heading">&nbsp;</td>
						</tr>
					</table>
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td class="bluetablehead">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td width="25%">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/tick.gif"
												width="17" height="17"></td>
											<td class="timelineboldgray">
												<fmt:message key="loan.selection">
													<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
													<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
												</fmt:message>
											</td>
										</tr>
									</table>
									</td>
									<td width="25%" align="center">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/bigarrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorange">
												<fmt:message key="loan.entityAccInfo">
													<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /></fmt:param>
												</fmt:message>
											</td>
										</tr>
									</table>
									</td>
									<td width="25%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorangelight"><mifos:mifoslabel
												name="loan.review/edit_ins" /></td>
										</tr>
									</table>
									</td>
									<td width="25%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorangelight"><mifos:mifoslabel
												name="loan.review&submit" /></td>
										</tr>
									</table>
									</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0" class="bluetableborder">
						<tr>
							<td width="70%" height="24" align="left" valign="top"
								class="paddingleftCreates">
							<table width="98%" border="0" cellspacing="0" cellpadding="3">
								<c:if test="${requestScope.perspective == 'redoLoan'}">
									<tr>
										<td><span class="fontnormalRedBold"><mifos:mifoslabel
											name="loan.redo_loan_note" /></span></td>
									</tr>
								</c:if>
								<tr>
									<td class="headingorange"><span class="heading"> <c:choose>
										<c:when test="${requestScope.perspective == 'redoLoan'}">
                                           	<fmt:message key="loan.redoAccount">
                                           		<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /></fmt:param>
                                           	</fmt:message>
										</c:when>
										<c:otherwise>
        	                              	<fmt:message key="loan.createAccount">
                                          		<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /></fmt:param>
                                           	</fmt:message>
										</c:otherwise>
									</c:choose>&nbsp;-&nbsp;</span> 
										<fmt:message key="loan.enterAccInfo">
											<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /></fmt:param>
										</fmt:message>
									</td>
								</tr>
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="loan.complete_field" /> <br>
									<font color="#FF0000">*</font> <mifos:mifoslabel
										name="loan.asterisk" /></td>
								</tr>
								<tr>
									<td><font class="fontnormalRedBold"> <html-el:errors
										bundle="loanUIResources" /> </font></td>
								</tr>


								<tr>
									<td class="fontnormal"><br>
									<span class="fontnormalbold"> <mifos:mifoslabel
										name="loan.acc_owner" /><mifos:mifoslabel name="loan.colon" />
									</span> <c:out value="${sessionScope.loanAccountOwner.displayName}" />
									</td>
								</tr>
							</table>

							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr class="fontnormal">
									<td width="30%" align="right" class="fontnormal">
										<span class="mandatorytext"><font color="#FF0000">*</font>
										<fmt:message key="loan.loanInstanceName">
											<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}"/></fmt:param>
										</fmt:message>
										:</span></td>
									<td width="70%"><html-el:select
										onchange="javascript:fun_refresh(this.form)"
										property="prdOfferingId">
										<c:forEach var="loanPrdOffering"
											items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanPrdOfferings')}">
											<html-el:option value="${loanPrdOffering.prdOfferingId}">${loanPrdOffering.prdOfferingName}</html-el:option>
										</c:forEach>
									</html-el:select></td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr class="fontnormal">
									<td colspan="2" valign="top" class="fontnormalbold">
										<fmt:message key="loan.loanProductSummary">
											<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}"/></fmt:param>
										</fmt:message>
									<br>
				 					<br>
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right" valign="top"><mifos:mifoslabel
										name="loan.description" />:</td>
									<td valign="top"><c:out
										value="${LoanOffering.description}" /></td>
								</tr>

								<tr class="fontnormal">
									<td width="30%" align="right">
									<fmt:message key="loan.interestRateType">
										<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.INTEREST}"/></fmt:param>
									</fmt:message>:</td>
									<td width="70%" valign="top"><c:out
										value="${LoanOffering.interestTypes.name}" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="loan.freq_of_inst" />:</td>
									<td valign="top"><c:out
										value="${LoanOffering.loanOfferingMeeting.meeting.meetingDetails.recurAfter}" />
									&nbsp; <c:choose>
										<c:when
											test="${LoanOffering.loanOfferingMeeting.meeting.meetingDetails.recurrenceType.recurrenceId == 1}">
											<mifos:mifoslabel name="loan.week(s)" />
										</c:when>
										<c:otherwise>
											<mifos:mifoslabel name="loan.month(s)" />
										</c:otherwise>
									</c:choose></td>
								</tr>

								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="loan.principle_due" />:</td>
									<td valign="top"><c:choose>
										<c:when test="${LoanOffering.prinDueLastInst}">
											<mifos:mifoslabel name="loan.yes" />
										</c:when>
										<c:otherwise>
											<mifos:mifoslabel name="loan.no" />
										</c:otherwise>
									</c:choose></td>
								</tr>
							</table>
							<br>

							<!-- Prerequisites : create loan account for Group / When Group loan with individual Monitoring is enabled -->
							<c:if test="${loanIndividualMonitoringIsEnabled == '1'}">
								<c:if test="${loanaccountownerisagroup == 'yes'}">
									<c:set
										value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'clientListSize')}"
										var="clientListSize" />
									<table width="96%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td width="5%" valign="top" class="drawtablerowboldnolinebg"><input
												type="checkbox"
												onchange="CalculateTotalLoanAmount(${clientListSize});"
												onclick="for(var i=0,l=this.form.length; i<l;
												i++){if(this.form[i].type==
												'checkbox' && this.form[i].name !='selectAll1'
												&& this.form[i].name !='intDedDisbursement'
												){this.form[i].checked=this.checked}}
												"
														name="selectAll1"></td>
											<td width="29%" class="drawtablerowboldnolinebg"><mifos:mifoslabel
												name="loan.acc_owner" /></td>
											<td width="31%" class="drawtablerowboldnolinebg"><mifos:mifoslabel
												name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel
												name="loan.amt" /></td>
											<td width="35%" class="drawtablerowboldnolinebg"><mifos:mifoslabel
												name="loan.business_work_act" /> <mifos:mifoslabel
												name="${ConfigurationConstants.LOAN}" /></td>
										</tr>

										<c:forEach var="client" varStatus="loopStatus1"
											items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'clientList')}">
											<bean:define id="indice" toScope="request">
												<c:out value="${loopStatus1.index}" />
											</bean:define>
											<tr>
												<td valign="top" class="drawtablerow"><html-el:checkbox
													property="clients[${indice}]" value="${client.customerId}"
													onclick="selectAllCheck(this)"
													onchange="CalculateTotalLoanAmount(${clientListSize});" /></td>
												<td width="29%" valign="top" class="drawtablerow"><span
													class="fontnormalbold"><mifos:mifoslabel
													name="${ConfigurationConstants.CLIENT}"
													isColonRequired="Yes" /></span> <c:out
													value="${client.displayName}" /> <br>
												<span class="fontnormalbold"><mifos:mifoslabel
													name="${ConfigurationConstants.CLIENT_ID}"
													isColonRequired="Yes" /></span> <c:out
													value="${client.globalCustNum}" /> <br>
												<span class="fontnormalbold"><c:out
													value="${ConfigurationConstants.GOVERNMENT}" />&nbsp;<c:out
													value="${ConfigurationConstants.ID}" />:&nbsp;</span> <c:out
													value="${client.governmentId}" /> <br>
												</td>
												<td width="31%" valign="top" class="drawtablerow"><mifos:mifosdecimalinput
													property="clientDetails[${indice}].loanAmount" value="0.0"
													onchange="CalculateTotalLoanAmount(${clientListSize});" /></td>
												<td width="35%" valign="top" class="drawtablerow"><mifos:select
													property="clientDetails[${indice}].businessActivity"
													style="width:136px;">
													<c:forEach var="BusinessActivity"
														items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessActivities')}">
														<html-el:option value="${BusinessActivity.id}">${BusinessActivity.name}</html-el:option>
													</c:forEach>
												</mifos:select></td>
											</tr>

										</c:forEach>

									</table>
									<table align="right" width="93%" border="0" cellpadding="3"
										cellspacing="0">
										<tr>
											<td align="right" class="fontnormalbold" width="28%"><mifos:mifoslabel
												name="loan.totalamount" />:</td>
											<td valign="top" class="fontnormal"><mifos:mifosdecimalinput
												property="loanAmount" value="0.0" readonly="true" />
											<mifos:mifoslabel
												name="loan.allowed_amount" /> &nbsp; <c:out
												value="${LoanOffering.minLoanAmount}" /> &nbsp; - &nbsp; <c:out
												value="${LoanOffering.maxLoanAmount}" /> )</td>	
										</tr>
									</table>s
									

								</c:if>
							</c:if> <!--  -->
<br>
<br>
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="loan.acc_details" /> <br>
									<br>
									</td>
								</tr>
								<tr class="fontnormal">
									<c:if test="${loanaccountownerisagroup != 'yes'}">
											<td align="right" class="fontnormal" width="30%"><mifos:mifoslabel
												name="loan.amount" mandatory="yes" />:</td>
											<td valign="top"><mifos:mifosdecimalinput
												property="loanAmount" readonly="false" /> <mifos:mifoslabel
												name="loan.allowed_amount" /> &nbsp; <c:out
												value="${LoanOffering.minLoanAmount}" /> &nbsp; - &nbsp; <c:out
												value="${LoanOffering.maxLoanAmount}" /> )</td>
									</c:if>
								</tr>
								<tr class="fontnormal">
									<td width="30%" align="right" class="fontnormal"><span
										class="mandatorytext"></span> 
										<font color="#FF0000">*</font>
										<fmt:message key="loan.interestRate">
											<fmt:param><mifos:mifoslabel
												name="${ConfigurationConstants.INTEREST}"/></fmt:param>
										</fmt:message>:</td>
									<td width="70%" valign="top"><mifos:mifosdecimalinput
										property="interestRate" decimalFmt="10.5" />
										<fmt:message key="loan.allowedInterest">
											<fmt:param><mifos:mifoslabel
												name="${ConfigurationConstants.INTEREST}" /></fmt:param>
										</fmt:message> &nbsp; <c:out
										value="${LoanOffering.minInterestRate}" /> &nbsp; - &nbsp; <c:out
										value="${LoanOffering.maxInterestRate}" /> %)</td>
								</tr>
								<tr class="fontnormal">
									<td align="right" class="fontnormal"><span
										class="mandatorytext"></span> <mifos:mifoslabel
										name="loan.no_of_inst" mandatory="yes" />:</td>
									<td valign="top"><mifos:mifosnumbertext
										property="noOfInstallments" /> <mifos:mifoslabel
										name="loan.allowed_no_of_inst" /> &nbsp; <c:out
										value="${LoanOffering.minNoInstallments}" /> &nbsp; - &nbsp;
									<c:out value="${LoanOffering.maxNoInstallments}" /> )</td>
								</tr>
								<tr class="fontnormal">

									<td align="right" class="fontnormal"><span
										class="mandatorytext"></span> <mifos:mifoslabel
										name="loan.proposed_date" mandatory="yes" />:</td>
									<td valign="top"><date:datetag property="disbursementDate" />
									</td>
								</tr>

								<!--  For Repayment day  -->
								<c:if
									test="${repaymentSchedulesIndependentOfMeetingIsEnabled == '1'}">
									<tr class="fontnormal">

										<td align="right" class="fontnormal"><span
											class="mandatorytext"></span> <mifos:mifoslabel
											name="loan.repayment_date" mandatory="yes" />:</td>
										<td valign="top"><c:if
											test="${LoanOffering.loanOfferingMeeting.meeting.meetingDetails.recurrenceType.recurrenceId == '2'}">
											<mifos:mifoslabel name="meeting.labelThe"
												bundle="MeetingResources" />
											<mifos:select property="monthRank">
												<c:forEach var="weekRank"
													items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'WeekRankList')}">
													<html-el:option value="${weekRank.id}">${weekRank.name}</html-el:option>
												</c:forEach>
											</mifos:select>
										</c:if> <mifos:select property="monthWeek">
											<c:forEach var="weekDay"
												items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'WeekDayList')}">
												<html-el:option value="${weekDay.value}">${weekDay.name}</html-el:option>
											</c:forEach>
										</mifos:select> <mifos:mifoslabel name="meeting.labelOfEvery"
											bundle="MeetingResources" /> <mifos:mifosnumbertext
											property="recurMonth" size="3" maxlength="3" disabled="true" />
										<c:out
											value="${LoanOffering.loanOfferingMeeting.meeting.meetingDetails.recurrenceType.recurrenceName}" />
										</td>
									</tr>

									<!-- TODO: Change the property of checkbox -->
									<tr class="fontnormal">
										<td align="right" class="fontnormal">
											<fmt:message key="loan.interestDisbursement">
												<fmt:param><mifos:mifoslabel
													name="${ConfigurationConstants.INTEREST}" /></fmt:param>
											</fmt:message>:</td>
										<td valign="top"><html-el:checkbox
											property="intDedDisbursement" value="1"
											onclick="intDedAtDisb()" disabled="true" /></td>
									</tr>
								</c:if>

								<c:if
									test="${repaymentSchedulesIndependentOfMeetingIsEnabled != '1'}">

									<!-- TODO: Change the property of checkbox -->
									<tr class="fontnormal">
										<td align="right" class="fontnormal">
											<fmt:message key="loan.interestDisbursement">
												<fmt:param><mifos:mifoslabel
													name="${ConfigurationConstants.INTEREST}" /></fmt:param>
											</fmt:message>:</td>
										<td valign="top"><html-el:checkbox
											property="intDedDisbursement" value="1"
											onclick="intDedAtDisb()" disabled="false" /></td>
									</tr>
								</c:if>

								<tr class="fontnormal">
									<td align="right" class="fontnormal"><span
										class="mandatorytext"></span> <mifos:mifoslabel
										name="loan.grace_period" mandatory="yes" isColonRequired="yes"/></td>
									<td valign="top"><mifos:mifosnumbertext
										property="gracePeriodDuration" /> <mifos:mifoslabel
										name="loan.inst" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right" class="fontnormal"><span
										class="mandatorytext"></span> <mifos:mifoslabel
										name="loan.source_fund" isColonRequired="yes"/></td>
									<td valign="top"><mifos:select property="loanOfferingFund">
										<c:forEach var="loanfund"
											items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanfunds')}">
											<html-el:option value="${loanfund.fundId}">${loanfund.fundName}</html-el:option>
										</c:forEach>
									</mifos:select></td>
								</tr>
								<tr class="fontnormal">
									<td align="right" class="fontnormal"><mifos:mifoslabel
										keyhm="Loan.PurposeOfLoan" name="Loan.PurposeOfLoan"
										bundle="loanUIResources" /> <mifos:mifoslabel
										keyhm="Loan.PurposeOfLoan"
										name="${ConfigurationConstants.LOAN}" isColonRequired="yes"
										bundle="loanUIResources"
										isManadatoryIndicationNotRequired="yes" /></td>
									<td valign="top"><mifos:select keyhm="Loan.PurposeOfLoan"
										property="businessActivityId">
										<c:forEach var="BusinessActivity"
											items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessActivities')}">
											<html-el:option value="${BusinessActivity.id}">${BusinessActivity.name}</html-el:option>
										</c:forEach>
									</mifos:select></td>
								</tr>

								<tr class="fontnormal">
									<td align="right" class="fontnormal"><mifos:mifoslabel
										keyhm="Loan.CollateralType" name="Loan.CollateralType"
										isColonRequired="yes" bundle="loanUIResources" /></td>
									<td valign="top"><mifos:select keyhm="Loan.CollateralType"
										property="collateralTypeId" style="width:136px;">
										<c:forEach var="CollateralType"
											items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'CollateralTypes')}">
											<html-el:option value="${CollateralType.id}">${CollateralType.name}</html-el:option>
										</c:forEach>
									</mifos:select></td>
								</tr>
								<tr class="fontnormal">
									<td align="right" valign="top" class="fontnormal"><mifos:mifoslabel
										keyhm="Loan.CollateralNotes" name="Loan.CollateralNotes"
										isColonRequired="yes" bundle="loanUIResources" /></td>
									<td valign="top"><mifos:textarea
										keyhm="Loan.CollateralNotes" property="collateralNote"
										style="width:320px; height:110px;">
									</mifos:textarea></td>
								</tr>

							</table>
							<br>
							<c:if
								test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
								<table width="93%" border="0" cellpadding="3" cellspacing="0">
									<tr>
										<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
											name="loan.additionalInfo" bundle="loanUIResources" /></td>
									</tr>
									<c:forEach var="cf"
										items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}"
										varStatus="loopStatus">
										<bean:define id="ctr">
											<c:out value="${loopStatus.index}" />
										</bean:define>

										<tr class="fontnormal">
											<td width="30%" align="right"><mifos:mifoslabel
												name="${cf.lookUpEntity.entityType}"
												mandatory="${cf.mandatoryStringValue}"
												bundle="loanUIResources"></mifos:mifoslabel>:</td>
											<td width="70%"><c:if
												test="${cf.fieldType == MasterConstants.CUSTOMFIELD_NUMBER}">
												<mifos:mifosnumbertext name="loanAccountActionForm"
													property='customField[${ctr}].fieldValue' maxlength="200" />
											</c:if> <c:if
												test="${cf.fieldType == MasterConstants.CUSTOMFIELD_ALPHANUMBER}">
												<mifos:mifosalphanumtext name="loanAccountActionForm"
													property='customField[${ctr}].fieldValue' maxlength="200" />
											</c:if> <c:if
												test="${cf.fieldType == MasterConstants.CUSTOMFIELD_DATE}">
												<mifos:mifosalphanumtext name="loanAccountActionForm"
													property='customField[${ctr}].fieldValue' maxlength="200" />
											</c:if> <html-el:hidden property='customField[${ctr}].fieldId'
												value="${cf.fieldId}"></html-el:hidden></td>
										</tr>
									</c:forEach>
								</table>
								<br>
							</c:if> <!-- Administrative Set Fees -->
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="loan.admin_set_fees" /> <br>
									<br>
									</td>
								</tr>
								<!-- For each admin fee that is retrieved the name and amoutn is displayed -->
								<c:forEach var="adminFees"
									items="${sessionScope.loanAccountActionForm.defaultFees}"
									varStatus="loopStatus1">
									<bean:define id="ctr1" toScope="request">
										<c:out value="${loopStatus1.index}" />
									</bean:define>
									<tr>
										<td width="30%" align="right" class="fontnormal"><c:out
											value="${adminFees.feeName}" />:</td>
										<td width="70%" class="fontnormal">
										<table width="100%" border="0" cellspacing="0" cellpadding="0">
											<!-- Fee amount display as label or text field -->
											<tr class="fontnormal">
												<td width="20%"><c:choose>
													<c:when test="${adminFees.periodic == true}">
														<c:out value="${adminFees.amount}" />
													</c:when>
													<c:otherwise>
														<mifos:mifosdecimalinput
															property="defaultFee[${ctr1}].amount"
															value="${adminFees.amount}" style="width:135px;" />
													</c:otherwise>
												</c:choose></td>
												<td>&nbsp;<mifos:mifoslabel name="loan.periodicity" />
												<c:choose>
													<c:when test="${adminFees.periodic == true}">
														<c:out value="${adminFees.feeSchedule}" />
													</c:when>
													<c:otherwise>
														<mifos:mifoslabel name="Fees.onetime" />
													</c:otherwise>
												</c:choose> <c:if test="${!empty adminFees.feeFormula}">
													<br>&nbsp;<SPAN class="fontnormal"><c:out
														value="${adminFees.feeFormula}" /></SPAN>
												</c:if></td>
												<td width="30%" align="center"><html-el:checkbox
													property="defaultFee[${ctr1}].feeRemoved" value="1"></html-el:checkbox>
												<mifos:mifoslabel name="loan.checkToRemove" /></td>
											</tr>
										</table>
										</td>
									</tr>
								</c:forEach>
							</table>
							<br>
							<!-- Administrative Set Fees End--> <!-- Fee Type -->
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="4" class="fontnormalbold"><mifos:mifoslabel
										name="loan.apply_add_fees" /> <br>
									<br>
									</td>
								</tr>
								<tr>
									<c:forEach begin="0" end="2" step="1" varStatus="loopStatus2">
										<bean:define id="ctr2" toScope="request">
											<c:out value="${loopStatus2.index}" />
										</bean:define>
										<td width="30%" align="right" class="fontnormal"><mifos:mifoslabel
											name="loan.fee_type" />:</td>
										<td width="17%" class="fontnormal"><mifos:select
											name="loanAccountActionForm"
											property='selectedFee[${ctr2}].feeId'
											onchange="displayAmount('selectedFee[${ctr2}].feeId', 'selectedFee[${ctr2}].amount',${loopStatus2.index} )">
											<c:forEach var="additionalFee"
												items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'additionalFeeList')}">
												<html-el:option value="${additionalFee.feeId}">${additionalFee.feeName}</html-el:option>
											</c:forEach>
										</mifos:select></td>
										<td align="left" width="6%" class="fontnormal"><mifos:mifoslabel
											name="loan.amount" />:</td>
										<td align="left" class="fontnormal"><mifos:mifosdecimalinput
											property='selectedFee[${ctr2}].amount' decimalFmt="10.5"
											style="width:70px;" /></td>
										<td><SPAN id="feeFormulaSpan${loopStatus2.index}"
											class="fontnormal"></SPAN></td>
										<c:if test="${ctr2 == 0}">
											<c:forEach var="fee"
												items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'additionalFeeList')}"
												varStatus="loopStatus3">
												<bean:define id="ctr3" toScope="request">
													<c:out value="${loopStatus3.index}" />
												</bean:define>
												<html-el:hidden property='selectedFeeAmntList'
													value='${fee.amount}' />
												<html-el:hidden property='feeFormulaList'
													value='${fee.feeFormula}' />
											</c:forEach>
										</c:if>
										<script>displayFormula('selectedFee[${ctr2}].feeId', 'selectedFee[${ctr2}].amount',${loopStatus2.index} )</script>
								</tr>
								</c:forEach>
							</table>
							<!-- Fees End --> <br>


							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center"><html-el:submit
										property="continueButton" styleClass="buttn">
										<mifos:mifoslabel name="loan.continue" />
									</html-el:submit> &nbsp; <html-el:button property="cancelButton"
										onclick="javascript:fun_cancel(this.form)"
										styleClass="cancelbuttn">
										<mifos:mifoslabel name="loan.cancel" />
									</html-el:button></td>
								</tr>
							</table>
							<br>
							</td>
						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>
			<html-el:hidden property="gracePeriodTypeId"
				value="${LoanOffering.gracePeriodType.id}" />
			<html-el:hidden property="gracePeriodname"
				value="${LoanOffering.gracePeriodType.name}" />
			<html-el:hidden property="currentFlowKey"
				value="${requestScope.currentFlowKey}" />
			<html-el:hidden property="perspective"
				value="${loanAccountActionForm.perspective}" />
			<script>intDedAtDisb();</script>
		</html-el:form>
	</tiles:put>
</tiles:insert>
