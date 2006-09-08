<!-- 

/**

 * viewgroupchargesdetails.jsp    version: 1.0

 

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

 */

-->

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<c:set var="custAccount" value="${sessionScope.customerAccount}" />
	<tiles:put name="body" type="string">

		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td class="bluetablehead05"><span class="fontnormal8pt"> <customtags:headerLink />/
				</span><span class="fontnormal8ptbold"> <mifos:mifoslabel
					name="${ConfigurationConstants.GROUP}" /> <mifos:mifoslabel
					name="Group.charges" bundle="GroupUIResources" /></span></td>
			</tr>
		</table>
		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td width="70%" height="24" align="left" valign="top"
					class="paddingL15T15">
				<table width="96%" border="0" cellpadding="3" cellspacing="0">
					<font class="fontnormalRedBold"><html-el:errors
						bundle="CenterUIResources" /></font>
					<tr>
						<td width="70%" class="headingorange"><mifos:mifoslabel
							name="${ConfigurationConstants.GROUP}" /> <mifos:mifoslabel
							name="Group.charges" bundle="GroupUIResources" /></td>
					</tr>
					<tr>
						<td align="right" class="headingorange"><img
							src="pages/framework/images/trans.gif" width="10" height="5"></td>
					</tr>
				</table>
				<c:if
					test="${sessionScope.BusinessKey.customerStatus.id == 7 || sessionScope.BusinessKey.customerStatus.id == 8 || sessionScope.BusinessKey.customerStatus.id == 9 || sessionScope.BusinessKey.customerStatus.id == 10}">
					<table width="96%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td bgcolor="#F0D4A5"
								style="padding-left:10px; padding-bottom:3px;"><span
								class="fontnormalbold"><mifos:mifoslabel
								name="Group.applyTransaction" bundle="GroupUIResources" />:</span>

							&nbsp;&nbsp;&nbsp;&nbsp; <html-el:link
								href="applyPaymentAction.do?method=load&globalCustNum=${sessionScope.BusinessKey.globalCustNum}&prdOfferingName=${sessionScope.BusinessKey.displayName}&input=ViewGroupCharges&globalAccountNum=${sessionScope.BusinessKey.customerAccount.globalAccountNum}&accountType=${sessionScope.BusinessKey.customerAccount.accountType.accountTypeId}&accountId=${sessionScope.BusinessKey.customerAccount.accountId}&securityParamInput=Group">
								<mifos:mifoslabel name="accounts.apply_payment" />
							</html-el:link> <c:if
								test="${sessionScope.BusinessKey.customerStatus.id == 9 || sessionScope.BusinessKey.customerStatus.id == 10}"> 
		               &nbsp;&nbsp;&nbsp;&nbsp;
		                    <html-el:link
									href="custApplyAdjustment.do?method=loadAdjustment&globalCustNum=${sessionScope.BusinessKey.globalCustNum}&prdOfferingName=${sessionScope.BusinessKey.displayName}&globalAccountNum=${sessionScope.BusinessKey.customerAccount.globalAccountNum}&input=ViewGroupCharges&securityParamInput=Group">
									<mifos:mifoslabel name="Group.applyAdjustment"
										bundle="GroupUIResources" />
								</html-el:link>
							</c:if> <c:if
								test="${sessionScope.BusinessKey.customerStatus.id == 7 || sessionScope.BusinessKey.customerStatus.id == 8 || sessionScope.BusinessKey.customerStatus.id == 9 || sessionScope.BusinessKey.customerStatus.id == 10}">
	                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    <html-el:link
									href="applyChargeAction.do?method=load&accountId=${sessionScope.BusinessKey.customerAccount.accountId}&input=ViewGroupCharges&globalCustNum=${sessionScope.BusinessKey.globalCustNum}">
									<mifos:mifoslabel name="group.applycharges"
										bundle="GroupUIResources" />
								</html-el:link>
	                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                 </c:if></td>
						</tr>
					</table>
					<br>
				</c:if>
				<table width="96%" border="0" cellpadding="3" cellspacing="0">
					<tr>
						<td width="66%" class="headingorange"><mifos:mifoslabel
							name="Group.accsum" bundle="GroupUIResources" /></td>
					</tr>
					<tr>
						<td class="fontnormal"><span class="fontnormal"> <mifos:mifoslabel
							name="Group.amtdue" bundle="GroupUIResources" />: <c:out
							value='${custAccount.nextDueAmount}' /> </span> <c:if
							test='${custAccount.nextDueAmount.amountDoubleValue != 0.0}'>
							<html-el:link
								href="customerAction.do?method=waiveChargeDue&globalCustNum=${sessionScope.BusinessKey.globalCustNum}&accountType=${sessionScope.BusinessKey.customerAccount.accountType.accountTypeId}&prdOfferingName=${sessionScope.BusinessKey.displayName}&statusId=${sessionScope.BusinessKey.customerStatus.id}&type=Group&accountId=${sessionScope.BusinessKey.customerAccount.accountId}&globalAccountNum=${sessionScope.BusinessKey.customerAccount.globalAccountNum}">
								<mifos:mifoslabel name="Group.waive" bundle="GroupUIResources" />
							</html-el:link>
						</c:if> <br>
						<span class="fontnormal"> <mifos:mifoslabel
							name="Group.amtoverdue" bundle="GroupUIResources" />: <c:out
							value='${custAccount.totalAmountInArrears}' /> </span> <c:if
							test='${custAccount.totalAmountInArrears.amountDoubleValue != 0.0}'>
							<html-el:link
								href="customerAction.do?method=waiveChargeOverDue&globalCustNum=${sessionScope.BusinessKey.globalCustNum}&accountType=${sessionScope.BusinessKey.customerAccount.accountType.accountTypeId}&prdOfferingName=${sessionScope.BusinessKey.displayName}&statusId=${sessionScope.BusinessKey.customerStatus.id}&type=Group&accountId=${sessionScope.BusinessKey.customerAccount.accountId}&globalAccountNum=${sessionScope.BusinessKey.customerAccount.globalAccountNum}">
								<mifos:mifoslabel name="Group.waive" bundle="GroupUIResources" />
							</html-el:link>
						</c:if> <BR>
						<span class="fontnormalbold"> <mifos:mifoslabel
							name="accounts.total" isColonRequired="Yes"></mifos:mifoslabel> <c:out
							value='${custAccount.totalAmountDue.amountDoubleValue}'></c:out>
						</span></td>
					</tr>
				</table>
				<br>
				<table width="96%" border="0" cellpadding="3" cellspacing="0">
					<tr>
						<td width="70%" class="headingorange"><mifos:mifoslabel
							name="Group.upcomcharges" bundle="GroupUIResources" /> (<c:out
							value='${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,custAccount.nextMeetingDate)}' />)
						<!-- c:out value='${requestScope.Context.businessResults["UpcomingChargesDate"]}'/-->
						</td>
						<td width="70%" align="right" class="fontnormal"><html-el:link
							href="accountAppAction.do?method=getTrxnHistory&statusId=${sessionScope.BusinessKey.customerStatus.id}&globalCustNum=${sessionScope.BusinessKey.globalCustNum}&input=ViewGroupCharges&globalAccountNum=${sessionScope.BusinessKey.customerAccount.globalAccountNum}&accountId=${sessionScope.BusinessKey.customerAccount.accountId}&accountType=${sessionScope.BusinessKey.customerAccount.accountType.accountTypeId}&prdOfferingName=${sessionScope.BusinessKey.displayName}&headingInput=ViewGroupCharges&searchInput=ClientChargesDetails">
							<mifos:mifoslabel name="Center.TransactionHistory" />
						</html-el:link></td>
					</tr>
				</table>

				<table width="96%" border="0" cellpadding="3" cellspacing="0">
					<c:if
						test='${!empty custAccount.detailsOfNextInstallment.accountFeesActionDetails}'>
						<tr>
							<td width="19%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="Group.feetype" bundle="GroupUIResources" /></td>
							<td width="49%" align="right" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="Group.amount" bundle="GroupUIResources" /></td>
							<td width="32%" class="drawtablerowboldnoline">&nbsp;</td>
						</tr>

						<c:forEach
							items="${custAccount.detailsOfNextInstallment.accountFeesActionDetails}"
							var="upcomingCharges">
							<tr>
								<td width="19%" class="drawtablerow"><c:out
									value="${upcomingCharges.fee.feeName}" /></td>
								<td width="49%" align="right" class="drawtablerow"><c:out
									value="${upcomingCharges.feeDue}" /></td>
								<td width="32%" class="drawtablerow">&nbsp;</td>
							</tr>
						</c:forEach>
						<tr>
							<td width="19%" class="drawtablerow">&nbsp;</td>
							<td width="49%" align="right" class="drawtablerow">&nbsp;</td>
							<td width="32%" class="drawtablerow">&nbsp;</td>
						</tr>
					</c:if>
				</table>

				<br>
				<table width="96%" border="0" cellpadding="3" cellspacing="0">
					<tr>
						<td width="28%" class="headingorange"><mifos:mifoslabel
							name="Group.recaccact" bundle="GroupUIResources" /></td>
						<td width="72%" align="right" class="fontnormal"><html-el:link
							href="customerAction.do?method=getAllActivity&statusId=${sessionScope.BusinessKey.customerStatus.id}&type=Group&globalCustNum=${sessionScope.BusinessKey.globalCustNum}&prdOfferingName=${sessionScope.BusinessKey.displayName}&input=ViewGroupCharges&globalAccountNum=${sessionScope.BusinessKey.customerAccount.globalAccountNum}&accountType=${sessionScope.BusinessKey.customerAccount.accountType.accountTypeId}&accountId=${sessionScope.BusinessKey.customerAccount.accountId}">
							<mifos:mifoslabel name="Group.viewallactivities"
								bundle="GroupUIResources" />
						</html-el:link></td>
					</tr>
				</table>
				<table width="96%" border="0" cellpadding="3" cellspacing="0">
					<c:if test='${!empty sessionScope.recentActivities}'>
						<tr>
							<td width="11%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="Group.date" bundle="GroupUIResources" /></td>
							<td width="35%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="Group.description" bundle="GroupUIResources" /></td>
							<td width="27%" align="right" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="Group.amount" bundle="GroupUIResources" /></td>
							<td width="6%" class="drawtablerowboldnoline">&nbsp;</td>
							<td width="21%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="Group.postedby" bundle="GroupUIResources" /></td>
						</tr>
						<c:forEach items="${sessionScope.recentActivities}"
							var="recentActivities">
							<tr>
								<td width="11%" class="drawtablerow"><c:out
									value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,recentActivities.activityDate)}" /></td>
								<td width="35%" class="drawtablerow"><c:out
									value="${recentActivities.description}" /></td>
								<td width="27%" align="right" class="drawtablerow"><c:out
									value="${recentActivities.amount}" /></td>
								<td width="6%" class="drawtablerow">&nbsp;</td>
								<td width="21%" class="drawtablerow"><c:out
									value="${recentActivities.postedBy}" /></td>
							</tr>
						</c:forEach>

						<tr>
							<td width="11%" class="drawtablerow">&nbsp;</td>
							<td width="35%" class="drawtablerow">&nbsp;</td>
							<td width="27%" align="right" class="drawtablerow">&nbsp;</td>
							<td width="6%" class="drawtablerow">&nbsp;</td>
							<td width="21%" class="drawtablerow">&nbsp;</td>
						</tr>
					</c:if>
				</table>

				<br>
				<table width="96%" border="0" cellpadding="3" cellspacing="0">

					<tr>
						<td width="35%" class="headingorange"><mifos:mifoslabel
							name="Group.recurringAccountFees" bundle="GroupUIResources" /></td>
					</tr>
				</table>
				<table width="96%" border="0" cellpadding="3" cellspacing="0">
					<c:forEach items="${custAccount.accountFees}" var="recurrenceFees">
						<c:if test="${recurrenceFees.periodic}">
							<tr class="fontnormal">
								<td width="15%"><c:out value="${recurrenceFees.fees.feeName}" />:</td>

								<td width="30%"><c:out value="${recurrenceFees.feeAmount}" />&nbsp;&nbsp;
								( <mifos:mifoslabel name="Fees.labelRecurEvery"
									bundle="FeesUIResources" /> <c:out
									value="${recurrenceFees.fees.feeFrequency.feeMeetingFrequency.meetingDetails.recurAfter}"></c:out>

								<c:if
									test="${recurrenceFees.fees.feeFrequency.feeMeetingFrequency.weekly==true}">
									<mifos:mifoslabel name="Fees.labelWeeks"
										bundle="FeesUIResources" />
								</c:if> <c:if
									test="${recurrenceFees.fees.feeFrequency.feeMeetingFrequency.monthly==true}">
									<mifos:mifoslabel name="Fees.labelMonths"
										bundle="FeesUIResources" />
								</c:if> )</td>
								<td width="55%"><html-el:link
									href="accountAppAction.do?method=removeFees&globalCustNum=${sessionScope.BusinessKey.globalCustNum}&statusId=${sessionScope.BusinessKey.customerStatus.id}&feeId=${recurrenceFees.fees.feeId}&accountId=${recurrenceFees.account.accountId}&fromPage=group&globalAccountNum=${sessionScope.BusinessKey.customerAccount.globalAccountNum}&accountType=${sessionScope.BusinessKey.customerAccount.accountType.accountTypeId}&prdOfferingName=${sessionScope.BusinessKey.displayName}">
									<mifos:mifoslabel name="Group.remove" />
								</html-el:link></td>
							</tr>
						</c:if>
					</c:forEach>

				</table>
				<br>
				</td>
			</tr>
		</table>
		<%--  html-el:hidden property="searchNode(search_name)"
			value="ClientChargesDetails" />
		<html-el:hidden property="globalAccountNum"
			value="${sessionScope.BusinessKey.customerAccount.globalAccountNum}" />
		<html-el:hidden property="accountId"
			value="${sessionScope.BusinessKey.customerAccount.accountId}" />
		<html-el:hidden property="accountType"
			value="${sessionScope.BusinessKey.customerAccount.accountType.accountTypeId}" />
		<html-el:hidden property="prdOfferingName"
			value="${sessionScope.BusinessKey.displayName}" />
		<html-el:hidden property="headingInput" value="ViewGroupCharges" />
		<mifos:SecurityParam property="Group" />
		<html-el:hidden property="statusId"
			value="${sessionScope.BusinessKey.customerStatus.id}" />
		<html-el:hidden property="globalCustNum"
			value="${sessionScope.BusinessKey.globalCustNum}" --%>

		<mifos:SecurityParam property="Group" />
	</tiles:put>
</tiles:insert>
