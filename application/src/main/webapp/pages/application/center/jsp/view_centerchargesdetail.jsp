<%--
Copyright (c) 2005-2009 Grameen Foundation USA
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

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
<span id="page.id" title="view_centerchargesdetail" />

	<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />	
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
			   var="BusinessKey" />
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'recentActivities')}"
			   var="recentActivities" />
	<tiles:put name="body" type="string">

		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td class="bluetablehead05"><span class="fontnormal8pt"> <customtags:headerLink />
				</span> <!-- Center Charges --> <span class="fontnormal8ptbold"> <mifos:mifoslabel
					name="${ConfigurationConstants.CENTER}" /> <mifos:mifoslabel
					name="Center.Charges" bundle="CenterUIResources" /> </span></td>
			</tr>
		</table>
		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td width="70%" height="24" align="left" valign="top"
					class="paddingL15T15">
				<table width="96%" border="0" cellpadding="3" cellspacing="0">
					<font class="fontnormalRedBold"><span id="view_centerchargesdetail.error.message"><html-el:errors
						bundle="CenterUIResources" /></span></font>
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
				<c:if test="${BusinessKey.customer.customerStatus.id == 13}">
					<table width="96%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td bgcolor="#F0D4A5"
								style="padding-left:10px; padding-bottom:3px;"><span
								class="fontnormalbold"> <mifos:mifoslabel
								name="Center.ApplyTransaction" bundle="CenterUIResources" /></span>
							&nbsp;&nbsp;&nbsp;&nbsp; <html-el:link styleId="view_centerchargesdetail.link.applyPayment"
								href="applyPaymentAction.do?method=load&globalCustNum=${BusinessKey.customer.globalCustNum}&prdOfferingName=${BusinessKey.customer.displayName}&input=ViewCenterCharges&globalAccountNum=${BusinessKey.globalAccountNum}&accountType=${BusinessKey.accountType.accountTypeId}&accountId=${BusinessKey.accountId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="accounts.apply_payment" />
							</html-el:link> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <html-el:link styleId="view_centerchargesdetail.link.applyAdjustment"
								href="custApplyAdjustment.do?method=loadAdjustment&globalCustNum=${BusinessKey.customer.globalCustNum}&prdOfferingName=${BusinessKey.customer.displayName}&globalAccountNum=${BusinessKey.globalAccountNum}&input=ViewCenterCharges&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="Center.ApplyAdjustment"
									bundle="CenterUIResources" />
							</html-el:link> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <html-el:link styleId="view_centerchargesdetail.link.applyCharges"
								href="applyChargeAction.do?method=load&accountId=${BusinessKey.accountId}&globalCustNum=${BusinessKey.customer.globalCustNum}&input=ViewCenterCharges&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
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
							name="Center.AmountDue" bundle="CenterUIResources" isColonRequired="yes" /><c:out
							value='${BusinessKey.nextDueAmount}' /> </span> <c:if
							test='${BusinessKey.nextDueAmount.amountDoubleValue != 0.0}'>
							<html-el:link styleId="view_centerchargesdetail.link.waiveChargeDue"
								href="customerAction.do?method=waiveChargeDue&globalCustNum=${BusinessKey.customer.globalCustNum}&accountType=${BusinessKey.accountType.accountTypeId}&prdOfferingName=${BusinessKey.customer.displayName}&statusId=${BusinessKey.customer.customerStatus.id}&type=Center&input=Center&accountId=${BusinessKey.accountId}&globalAccountNum=${BusinessKey.globalAccountNum}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="Center.waive" bundle="CenterUIResources" />
							</html-el:link>
						</c:if> <br>
						<span class="fontnormal"> <mifos:mifoslabel
							name="Center.AmountOverdue" bundle="CenterUIResources" isColonRequired="yes" /><c:out
							value='${BusinessKey.totalAmountInArrears}' /> </span> <c:if
							test='${BusinessKey.totalAmountInArrears.amountDoubleValue != 0.0}'>
							<html-el:link styleId="view_centerchargesdetail.link.waiveChargeOverDue"
								href="customerAction.do?method=waiveChargeOverDue&globalCustNum=${BusinessKey.customer.globalCustNum}&accountType=${BusinessKey.accountType.accountTypeId}&prdOfferingName=${BusinessKey.customer.displayName}&statusId=${BusinessKey.customer.customerStatus.id}&type=Center&input=Center&accountId=${BusinessKey.accountId}&globalAccountNum=${BusinessKey.globalAccountNum}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="Center.waive" bundle="CenterUIResources" />
							</html-el:link>
						</c:if> <BR>
						<span class="fontnormalbold"> <mifos:mifoslabel
							name="accounts.total" isColonRequired="Yes"></mifos:mifoslabel> <c:out
							value='${BusinessKey.totalAmountDue}'></c:out>

						</span></td>
					</tr>
				</table>
				<br>
				<table width="96%" border="0" cellpadding="3" cellspacing="0">
					<tr>
						<td width="70%" class="headingorange"><mifos:mifoslabel
							name="Center.UpcomingCharges" bundle="CenterUIResources" /> (<c:out
							value='${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,BusinessKey.upcomingChargesDate)}' />)
						</td>
						<td width="70%" align="right" class="fontnormal"><html-el:link styleId="view_centerchargesdetail.link.transactionHistory"
							href="accountAppAction.do?method=getTrxnHistory&statusId=${BusinessKey.customer.customerStatus.id}&globalCustNum=${BusinessKey.customer.globalCustNum}&input=ViewCenterCharges&globalAccountNum=${BusinessKey.globalAccountNum}&accountId=${BusinessKey.accountId}&accountType=${BusinessKey.accountType.accountTypeId}&prdOfferingName=${BusinessKey.customer.displayName}&headingInput=ViewCenterCharges&searchInput=ClientChargesDetails&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
							<mifos:mifoslabel name="Center.TransactionHistory" />
						</html-el:link></td>
					</tr>
				</table>

				<table width="96%" border="0" cellpadding="3" cellspacing="0">
					<c:if
						test='${!empty BusinessKey.upcomingInstallment.accountFeesActionDetails or (!empty BusinessKey.upcomingInstallment.miscFeeDue) and (BusinessKey.upcomingInstallment.miscFeeDue.amountDoubleValue!=0.0) or (!empty BusinessKey.upcomingInstallment.miscPenaltyDue) and (BusinessKey.upcomingInstallment.miscPenaltyDue.amountDoubleValue!=0.0)}'>
						<tr>
							<td width="19%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="Center.FeeType" bundle="CenterUIResources" /></td>
							<td width="49%" align="right" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="Customer.Amount" bundle="CustomerUIResources" /></td>
							<td width="32%" class="drawtablerowboldnoline">&nbsp;</td>
						</tr>

						<c:forEach
							items="${BusinessKey.upcomingInstallment.accountFeesActionDetails}"
							var="upcomingCharges">
							<c:if test='${upcomingCharges.feeAmount.amountDoubleValue!=0.0}'>
								<tr>
									<td width="19%" class="drawtablerow"><c:out
										value="${upcomingCharges.fee.feeName}" /></td>
									<td width="49%" align="right" class="drawtablerow"><c:out
										value="${upcomingCharges.feeAmount}" /></td>
									<td width="32%" class="drawtablerow">&nbsp;</td>
								</tr>
							</c:if>
						</c:forEach>
						<c:if test='${(!empty BusinessKey.upcomingInstallment.miscFeeDue) and (BusinessKey.upcomingInstallment.miscFeeDue.amountDoubleValue!=0.0)}'>
								<tr>
									<td width="19%" class="drawtablerow"><mifos:mifoslabel name="Customer.miscfee" bundle="CustomerUIResources" /></td>
									<td width="49%" align="right" class="drawtablerow"><c:out
										value="${BusinessKey.upcomingInstallment.miscFeeDue}" /></td>
									<td width="32%" class="drawtablerow">&nbsp;</td>
								</tr>
						</c:if>
						<c:if test='${(!empty BusinessKey.upcomingInstallment.miscPenaltyDue) and (BusinessKey.upcomingInstallment.miscPenaltyDue.amountDoubleValue!=0.0)}'>
								<tr>
									<td width="19%" class="drawtablerow"><mifos:mifoslabel name="Customer.miscpenalty" bundle="CustomerUIResources" /></td>
									<td width="49%" align="right" class="drawtablerow"><c:out
										value="${BusinessKey.upcomingInstallment.miscPenaltyDue}" /></td>
									<td width="32%" class="drawtablerow">&nbsp;</td>
								</tr>
						</c:if>
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
						<td width="72%" align="right" class="fontnormal"><html-el:link styleId="view_centerchargesdetail.link.accountActivity"
							href="customerAction.do?method=getAllActivity&statusId=${BusinessKey.customer.customerStatus.id}&type=Center&globalCustNum=${BusinessKey.customer.globalCustNum}&prdOfferingName=${BusinessKey.customer.displayName}&input=ViewCenterCharges&globalAccountNum=${BusinessKey.globalAccountNum}&accountType=${BusinessKey.accountType.accountTypeId}&accountId=${BusinessKey.accountId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
							<mifos:mifoslabel name="Center.AccountActivity"
								bundle="CenterUIResources" />
						</html-el:link></td>
					</tr>
				</table>

				<table width="96%" border="0" cellpadding="3" cellspacing="0">
					<c:if test='${!empty recentActivities}'>
						<tr>
							<td width="11%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="Center.date" bundle="CenterUIResources" /></td>
							<td width="35%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="Center.description" bundle="CenterUIResources" /></td>
							<td width="27%" align="right" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="Customer.Amount" bundle="CustomerUIResources" /></td>
							<td width="6%" class="drawtablerowboldnoline">&nbsp;</td>
							<td width="21%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="Center.postedby" bundle="CenterUIResources" /></td>
						</tr>
						<c:forEach items="${recentActivities}"
							var="recentActivities">
							<tr>
								<td width="11%" class="drawtablerow"><c:out
									value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,recentActivities.activityDate)}" /></td>
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

					<c:forEach items="${BusinessKey.accountFees}" var="recurrenceFees">
						<c:if test="${recurrenceFees.periodic && recurrenceFees.active}">
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
								<td width="55%"><html-el:link styleId="view_centerchargesdetail.link.remove"
									href="accountAppAction.do?method=removeFees&statusId=${BusinessKey.customer.customerStatus.id}&globalCustNum=${BusinessKey.customer.globalCustNum}&feeId=${recurrenceFees.fees.feeId}&accountId=${recurrenceFees.account.accountId}&fromPage=center&input=Center&globalAccountNum=${BusinessKey.globalAccountNum}&accountType=${BusinessKey.accountType.accountTypeId}&prdOfferingName=${BusinessKey.customer.displayName}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
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
	</tiles:put>
</tiles:insert>
