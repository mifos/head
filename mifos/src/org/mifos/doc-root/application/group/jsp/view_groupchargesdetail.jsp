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
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />	
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
			   var="BusinessKey" />
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'recentActivities')}"
			   var="recentActivities" />
	<tiles:put name="body" type="string">

		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td class="bluetablehead05"><span class="fontnormal8pt"> <customtags:headerLink />
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
					test="${BusinessKey.customer.customerStatus.id == 7 || BusinessKey.customer.customerStatus.id == 8 || BusinessKey.customer.customerStatus.id == 9 || BusinessKey.customer.customerStatus.id == 10}">
					<table width="96%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td bgcolor="#F0D4A5"
								style="padding-left:10px; padding-bottom:3px;"><span
								class="fontnormalbold"><mifos:mifoslabel
								name="Group.applyTransaction" bundle="GroupUIResources" />:</span>

							&nbsp;&nbsp;&nbsp;&nbsp; <html-el:link
								href="applyPaymentAction.do?method=load&globalCustNum=${BusinessKey.customer.globalCustNum}&prdOfferingName=${BusinessKey.customer.displayName}&input=ViewGroupCharges&globalAccountNum=${BusinessKey.globalAccountNum}&accountType=${BusinessKey.accountType.accountTypeId}&accountId=${BusinessKey.accountId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="accounts.apply_payment" />
							</html-el:link> <c:if
								test="${BusinessKey.customer.customerStatus.id == 9 || BusinessKey.customer.customerStatus.id == 10}"> 
		               &nbsp;&nbsp;&nbsp;&nbsp;
		                    <html-el:link
									href="custApplyAdjustment.do?method=loadAdjustment&globalCustNum=${BusinessKey.customer.globalCustNum}&prdOfferingName=${BusinessKey.customer.displayName}&globalAccountNum=${BusinessKey.globalAccountNum}&input=ViewGroupCharges&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
									<mifos:mifoslabel name="Group.applyAdjustment"
										bundle="GroupUIResources" />
								</html-el:link>
							</c:if> <c:if
								test="${BusinessKey.customer.customerStatus.id == 7 || BusinessKey.customer.customerStatus.id == 8 || BusinessKey.customer.customerStatus.id == 9 || BusinessKey.customer.customerStatus.id == 10}">
	                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    <html-el:link
									href="applyChargeAction.do?method=load&accountId=${BusinessKey.accountId}&input=ViewGroupCharges&globalCustNum=${BusinessKey.customer.globalCustNum}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
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
							value='${BusinessKey.nextDueAmount}' /> </span> <c:if
							test='${BusinessKey.nextDueAmount.amountDoubleValue != 0.0}'>
							<html-el:link
								href="customerAction.do?method=waiveChargeDue&globalCustNum=${BusinessKey.customer.globalCustNum}&accountType=${BusinessKey.accountType.accountTypeId}&prdOfferingName=${BusinessKey.customer.displayName}&statusId=${BusinessKey.customer.customerStatus.id}&type=Group&input=Group&accountId=${BusinessKey.accountId}&globalAccountNum=${BusinessKey.globalAccountNum}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="Group.waive" bundle="GroupUIResources" />
							</html-el:link>
						</c:if> <br>
						<span class="fontnormal"> <mifos:mifoslabel
							name="Group.amtoverdue" bundle="GroupUIResources" />: <c:out
							value='${BusinessKey.totalAmountInArrears}' /> </span> <c:if
							test='${BusinessKey.totalAmountInArrears.amountDoubleValue != 0.0}'>
							<html-el:link
								href="customerAction.do?method=waiveChargeOverDue&globalCustNum=${BusinessKey.customer.globalCustNum}&accountType=${BusinessKey.accountType.accountTypeId}&prdOfferingName=${BusinessKey.customer.displayName}&statusId=${BusinessKey.customer.customerStatus.id}&type=Group&input=Group&accountId=${BusinessKey.accountId}&globalAccountNum=${BusinessKey.globalAccountNum}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="Group.waive" bundle="GroupUIResources" />
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
							name="Group.upcomcharges" bundle="GroupUIResources" /> (<c:out
							value='${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,BusinessKey.upcomingChargesDate)}' />)
						<!-- c:out value='${requestScope.Context.businessResults["UpcomingChargesDate"]}'/-->
						</td>
						<td width="70%" align="right" class="fontnormal"><html-el:link
							href="accountAppAction.do?method=getTrxnHistory&statusId=${BusinessKey.customer.customerStatus.id}&globalCustNum=${BusinessKey.customer.globalCustNum}&input=ViewGroupCharges&globalAccountNum=${BusinessKey.globalAccountNum}&accountId=${BusinessKey.accountId}&accountType=${BusinessKey.accountType.accountTypeId}&prdOfferingName=${BusinessKey.customer.displayName}&headingInput=ViewGroupCharges&searchInput=ClientChargesDetails&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
							<mifos:mifoslabel name="Center.TransactionHistory" />
						</html-el:link></td>
					</tr>
				</table>

				<table width="96%" border="0" cellpadding="3" cellspacing="0">
					<c:if
						test='${!empty BusinessKey.upcomingInstallment.accountFeesActionDetails or (!empty BusinessKey.upcomingInstallment.miscFeeDue) and (BusinessKey.upcomingInstallment.miscFeeDue.amountDoubleValue!=0.0) or (!empty BusinessKey.upcomingInstallment.miscPenaltyDue) and (BusinessKey.upcomingInstallment.miscPenaltyDue.amountDoubleValue!=0.0)}'>
						<tr>
							<td width="19%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="Group.feetype" bundle="GroupUIResources" /></td>
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
							name="Group.recaccact" bundle="GroupUIResources" /></td>
						<td width="72%" align="right" class="fontnormal"><html-el:link
							href="customerAction.do?method=getAllActivity&statusId=${BusinessKey.customer.customerStatus.id}&type=Group&globalCustNum=${BusinessKey.customer.globalCustNum}&prdOfferingName=${BusinessKey.customer.displayName}&input=ViewGroupCharges&globalAccountNum=${BusinessKey.globalAccountNum}&accountType=${BusinessKey.accountType.accountTypeId}&accountId=${BusinessKey.accountId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
							<mifos:mifoslabel name="Group.viewallactivities"
								bundle="GroupUIResources" />
						</html-el:link></td>
					</tr>
				</table>
				<table width="96%" border="0" cellpadding="3" cellspacing="0">
					<c:if test='${!empty recentActivities}'>
						<tr>
							<td width="11%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="Group.date" bundle="GroupUIResources" /></td>
							<td width="35%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="Group.description" bundle="GroupUIResources" /></td>
							<td width="27%" align="right" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="Customer.Amount" bundle="CustomerUIResources" /></td>
							<td width="6%" class="drawtablerowboldnoline">&nbsp;</td>
							<td width="21%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="Group.postedby" bundle="GroupUIResources" /></td>
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
							name="Group.recurringAccountFees" bundle="GroupUIResources" /></td>
					</tr>
				</table>
				<table width="96%" border="0" cellpadding="3" cellspacing="0">
					<c:forEach items="${BusinessKey.accountFees}" var="recurrenceFees">
						<c:if test="${recurrenceFees.periodic && recurrenceFees.active}">
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
								<td width="55%">
								<c:if test="${BusinessKey.customer.customerStatus.id != CustomerStatus.GROUP_CANCELLED.value and BusinessKey.customer.customerStatus.id != CustomerStatus.GROUP_CLOSED.value}">
								<html-el:link
									href="accountAppAction.do?method=removeFees&globalCustNum=${BusinessKey.customer.globalCustNum}&statusId=${BusinessKey.customer.customerStatus.id}&feeId=${recurrenceFees.fees.feeId}&accountId=${recurrenceFees.account.accountId}&fromPage=group&input=Group&globalAccountNum=${BusinessKey.globalAccountNum}&accountType=${BusinessKey.accountType.accountTypeId}&prdOfferingName=${BusinessKey.customer.displayName}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
									<mifos:mifoslabel name="Group.remove" />
								</html-el:link>
								</c:if>
								</td>
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
