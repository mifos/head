<!-- 

/**

 * viewclientchargesdetails.jsp    version: 1.0

 

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
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>


<tiles:insert definition=".clientsacclayoutsearchmenu">
	<c:set var="custAccount" value="${sessionScope.customerAccount}" />
	<tiles:put name="body" type="string">

		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td class="bluetablehead05"><span class="fontnormal8pt"> <customtags:headerLink />
				</span> <!-- Center Charges --> <span class="fontnormal8ptbold">/ <mifos:mifoslabel
					name="${ConfigurationConstants.CENTER}" /> <mifos:mifoslabel
					name="Center.Charges" bundle="CenterUIResources" /> </span></td>
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
							name="${ConfigurationConstants.CENTER}" /> <mifos:mifoslabel
							name="Center.Charges" bundle="CenterUIResources" /></td>
					</tr>
					<tr>
						<td align="right" class="headingorange"><img
							src="images/trans.gif" width="10" height="5"></td>
					</tr>
				</table>
				<c:if test="${sessionScope.BusinessKey.customerStatus.id == 13}">
					<table width="96%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td bgcolor="#F0D4A5"
								style="padding-left:10px; padding-bottom:3px;"><span
								class="fontnormalbold"> <mifos:mifoslabel
								name="Center.ApplyTransaction" bundle="CenterUIResources" /></span>
							&nbsp;&nbsp;&nbsp;&nbsp; <html-el:link
								href="applyPaymentAction.do?method=load&globalCustNum=${sessionScope.BusinessKey.globalCustNum}&prdOfferingName=${sessionScope.BusinessKey.displayName}&input=ViewCenterCharges&globalAccountNum=${sessionScope.BusinessKey.customerAccount.globalAccountNum}&accountType=${sessionScope.BusinessKey.customerAccount.accountType.accountTypeId}&accountId=${sessionScope.BusinessKey.customerAccount.accountId}&securityParamInput=Center">
								<mifos:mifoslabel name="accounts.apply_payment" />
							</html-el:link> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <html-el:link
								href="custApplyAdjustment.do?method=loadAdjustment&globalCustNum=${sessionScope.BusinessKey.globalCustNum}&prdOfferingName=${sessionScope.BusinessKey.displayName}&globalAccountNum=${sessionScope.BusinessKey.customerAccount.globalAccountNum}&input=ViewCenterCharges&securityParamInput=Center">
								<mifos:mifoslabel name="Center.ApplyAdjustment"
									bundle="CenterUIResources" />
							</html-el:link> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <html-el:link
								href="applyChargeAction.do?method=load&accountId=${sessionScope.BusinessKey.customerAccount.accountId}&globalCustNum=${sessionScope.BusinessKey.globalCustNum}&input=ViewCenterCharges">
								<mifos:mifoslabel name="Center.ApplyCharges" />
							</html-el:link> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
						</tr>
					</table>
					<br>
				</c:if>
				<table width="96%" border="0" cellpadding="3" cellspacing="0">
					<tr>
						<td width="66%" class="headingorange"><mifos:mifoslabel
							name="Center.AccountSummary" bundle="CenterUIResources" /></td>
					</tr>
					<tr>
						<td class="fontnormal"><span class="fontnormal"> <mifos:mifoslabel
							name="Center.AmountDue" bundle="CenterUIResources" />: <c:out
							value='${custAccount.nextDueAmount}' /> </span> <c:if
							test='${custAccount.nextDueAmount.amountDoubleValue != 0.0}'>
							<html-el:link
								href="customerAction.do?method=waiveChargeDue&globalCustNum=${sessionScope.BusinessKey.globalCustNum}&accountType=${sessionScope.BusinessKey.customerAccount.accountType.accountTypeId}&prdOfferingName=${sessionScope.BusinessKey.displayName}&statusId=${sessionScope.BusinessKey.customerStatus.id}&type=Center&accountId=${sessionScope.BusinessKey.customerAccount.accountId}&globalAccountNum=${sessionScope.BusinessKey.customerAccount.globalAccountNum}">
								<mifos:mifoslabel name="Center.waive" bundle="CenterUIResources" />
							</html-el:link>
						</c:if> <br>
						<span class="fontnormal"> <mifos:mifoslabel
							name="Center.AmountOverdue" bundle="CenterUIResources" />: <c:out
							value='${custAccount.totalAmountInArrears}' /> </span> <c:if
							test='${custAccount.totalAmountInArrears.amountDoubleValue != 0.0}'>
							<html-el:link
								href="customerAction.do?method=waiveChargeOverDue&globalCustNum=${sessionScope.BusinessKey.globalCustNum}&accountType=${sessionScope.BusinessKey.customerAccount.accountType.accountTypeId}&prdOfferingName=${sessionScope.BusinessKey.displayName}&statusId=${sessionScope.BusinessKey.customerStatus.id}&type=Center&accountId=${sessionScope.BusinessKey.customerAccount.accountId}&globalAccountNum=${sessionScope.BusinessKey.customerAccount.globalAccountNum}">
								<mifos:mifoslabel name="Center.waive" bundle="CenterUIResources" />
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
							name="Center.UpcomingCharges" bundle="CenterUIResources" /> (<c:out
							value='${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,custAccount.nextMeetingDate)}' />)
						</td>
						<td width="70%" align="right" class="fontnormal"><html-el:link
							href="accountAppAction.do?method=getTrxnHistory&statusId=${sessionScope.BusinessKey.customerStatus.id}&globalCustNum=${sessionScope.BusinessKey.globalCustNum}&input=ViewCenterCharges&globalAccountNum=${sessionScope.BusinessKey.customerAccount.globalAccountNum}&accountId=${sessionScope.BusinessKey.customerAccount.accountId}&accountType=${sessionScope.BusinessKey.customerAccount.accountType.accountTypeId}&prdOfferingName=${sessionScope.BusinessKey.displayName}&headingInput=ViewCenterCharges&searchInput=ClientChargesDetails">
							<mifos:mifoslabel name="Center.TransactionHistory" />
						</html-el:link></td>
					</tr>
				</table>

				<table width="96%" border="0" cellpadding="3" cellspacing="0">
					<c:if
						test='${!empty custAccount.detailsOfNextInstallment.accountFeesActionDetails}'>
						<tr>
							<td width="19%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="Center.FeeType" bundle="CenterUIResources" /></td>
							<td width="49%" align="right" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="Center.Amount" bundle="CenterUIResources" /></td>
							<td width="32%" class="drawtablerowboldnoline">&nbsp;</td>
						</tr>

						<c:forEach
							items="${custAccount.detailsOfNextInstallment.accountFeesActionDetails}"
							var="upcomingCharges">
							<tr>
								<td width="19%" class="drawtablerow"><c:out
									value="${upcomingCharges.fee.feeName}" /></td>
								<td width="49%" align="right" class="drawtablerow"><c:out
									value="${upcomingCharges.feeAmount}" /></td>
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
							name="Center.RecentAccountActivity" bundle="CenterUIResources" />
						</td>
						<td width="72%" align="right" class="fontnormal"><html-el:link
							href="customerAction.do?method=getAllActivity&statusId=${sessionScope.BusinessKey.customerStatus.id}&type=Center&globalCustNum=${sessionScope.BusinessKey.globalCustNum}&prdOfferingName=${sessionScope.BusinessKey.displayName}&input=ViewCenterCharges&globalAccountNum=${sessionScope.BusinessKey.customerAccount.globalAccountNum}&accountType=${sessionScope.BusinessKey.customerAccount.accountType.accountTypeId}&accountId=${sessionScope.BusinessKey.customerAccount.accountId}&securityParamInput=Center">
							<mifos:mifoslabel name="Center.AccountActivity"
								bundle="CenterUIResources" />
						</html-el:link></td>
					</tr>
				</table>

				<table width="96%" border="0" cellpadding="3" cellspacing="0">
					<c:if test='${!empty sessionScope.recentActivities}'>
						<tr>
							<td width="11%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="Center.date" bundle="CenterUIResources" /></td>
							<td width="35%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="Center.description" bundle="CenterUIResources" /></td>
							<td width="27%" align="right" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="Center.Amount" bundle="CenterUIResources" /></td>
							<td width="6%" class="drawtablerowboldnoline">&nbsp;</td>
							<td width="21%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="Center.postedby" bundle="CenterUIResources" /></td>
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
							name="Center.RecurringAccountFees" bundle="CenterUIResources" /></td>
					</tr>
				</table>
				<table width="96%" border="0" cellpadding="3" cellspacing="0">

					<c:forEach items="${custAccount.accountFees}" var="recurrenceFees">
						<c:if test="${recurrenceFees.periodic}">
							<tr class="fontnormal">
								<td width="15%"><c:out value="${recurrenceFees.fees.feeName}" />:</td>
								<td width="30%"><c:out value="${recurrenceFees.feeAmount}" />
								&nbsp;&nbsp; ( <mifos:mifoslabel name="Fees.labelRecurEvery"
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
									href="accountAppAction.do?method=removeFees&statusId=${sessionScope.BusinessKey.customerStatus.id}&globalCustNum=${sessionScope.BusinessKey.globalCustNum}&feeId=${recurrenceFees.fees.feeId}&accountId=${recurrenceFees.account.accountId}&fromPage=center&globalAccountNum=${sessionScope.BusinessKey.customerAccount.globalAccountNum}&accountType=${sessionScope.BusinessKey.customerAccount.accountType.accountTypeId}&prdOfferingName=${sessionScope.BusinessKey.displayName}">
									<mifos:mifoslabel name="Center.remove"
										bundle="CenterUIResources" />
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
		<html-el:hidden property="globalCustNum"
			value="${sessionScope.BusinessKey.globalCustNum}" />
		<html-el:hidden property="accountId"
			value="${sessionScope.BusinessKey.customerAccount.accountId}" />
		<html-el:hidden property="accountType"
			value="${sessionScope.BusinessKey.customerAccount.accountType.accountTypeId}" />
		<html-el:hidden property="prdOfferingName"
			value="${sessionScope.BusinessKey.displayName}" />
		<html-el:hidden property="headingInput" value="ViewCenterCharges" />
		<html-el:hidden property="searchInput" value="ClientChargesDetails" />
		<mifos:SecurityParam property="Center" />
		<html-el:hidden property="statusId"
			value="${sessionScope.BusinessKey.customerStatus.id}" --%>
		<mifos:SecurityParam property="Center" />
	</tiles:put>
</tiles:insert>
