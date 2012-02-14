<%--
Copyright (c) 2005-2011 Grameen Foundation USA
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.ClientUIResources"/>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />	
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
			   var="BusinessKey" />
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'recentActivities')}"
			   var="recentActivities" />
	<tiles:put name="body" type="string">
	<span id="page.id" title="ViewClientChargesDetail" ></span>
		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td class="bluetablehead05"><span class="fontnormal8pt"> <customtags:headerLink />  
				</span><span class="fontnormal8ptbold"> 
					<fmt:message key="client.clientcharges">
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
					</fmt:message></span></td>
			</tr>
		</table>
		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td width="70%" height="24" align="left" valign="top"
					class="paddingL15T15">
				<table width="96%" border="0" cellpadding="3" cellspacing="0">
					
					<tr>
						<td width="70%" class="headingorange">
					<fmt:message key="client.clientcharges">
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
					</fmt:message></td>
					</tr>
					<tr>
						<td width="70%" class="headingorange"><font class="fontnormalRedBold"><span id="view_clientchargesdetail.error.message"><html-el:errors
						bundle="ClientUIResources" /></span></font></td>
					</tr>
					<tr>
						<td align="right" class="headingorange"><img
							src="pages/framework/images/trans.gif" width="10" height="5"></td>
					</tr>
				</table>
				<c:if
					test="${BusinessKey.customer.customerStatus.id == 1 || BusinessKey.customer.customerStatus.id == 2 || BusinessKey.customer.customerStatus.id == 3 || BusinessKey.customer.customerStatus.id == 4}">
					<table width="96%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td bgcolor="#F0D4A5"
								style="padding-left:10px; padding-bottom:3px;"><span
								class="fontnormalbold"> <mifos:mifoslabel
								name="client.ApplyTransaction" bundle="ClientUIResources" /> </span>
							&nbsp;&nbsp;&nbsp;&nbsp; 
							<c:url value="applyPaymentAction.do" var="applyPaymentActionLoadMethodUrl" >
								<c:param name="method" value="load" />
								<c:param name="input" value="fee" />
								<c:param name="globalCustNum" value="${BusinessKey.customer.globalCustNum}" />
								<c:param name="prdOfferingName" value="${BusinessKey.customer.displayName}" />
								<c:param name="input" value="ViewClientCharges" />
								<c:param name="globalAccountNum" value="${BusinessKey.globalAccountNum}" />
								<c:param name="accountType" value="${BusinessKey.accountType.accountTypeId}" />
								<c:param name="accountId" value="${BusinessKey.accountId}" />
								<c:param name="randomNUm" value="${sessionScope.randomNUm}" />
								<c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
							</c:url >
							<html-el:link styleId="view_clientchargesdetail.link.applyPayment"
								href="${applyPaymentActionLoadMethodUrl}">
								<mifos:mifoslabel name="client.apply_payment"
									bundle="ClientUIResources" />
							</html-el:link> <c:if
								test="${BusinessKey.customer.customerStatus.id == 3 || BusinessKey.customer.customerStatus.id == 4}">
	                &nbsp;&nbsp;&nbsp;&nbsp;
								<c:url value="custApplyAdjustment.do" var="custApplyAdjustmentLoadAdjustmentMethodUrl" >
									<c:param name="method" value="loadAdjustment" />
									<c:param name="globalCustNum" value="${BusinessKey.customer.globalCustNum}" />
									<c:param name="prdOfferingName" value="${BusinessKey.customer.displayName}" />
									<c:param name="globalAccountNum" value="${BusinessKey.globalAccountNum}" />
									<c:param name="input" value="ViewClientCharges" />
									<c:param name="randomNUm" value="${sessionScope.randomNUm}" />
									<c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
								</c:url >
		                <html-el:link styleId="view_clientchargesdetail.link.applyAdjustment"
									href="${custApplyAdjustmentLoadAdjustmentMethodUrl}">
									<mifos:mifoslabel name="client.applyadjustment"
										bundle="ClientUIResources" />
								</html-el:link>
							</c:if> <c:if
								test="${BusinessKey.customer.customerStatus.id == 1 || BusinessKey.customer.customerStatus.id == 2 || BusinessKey.customer.customerStatus.id == 3 || BusinessKey.customer.customerStatus.id == 4}">
			             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								<c:url value="applyChargeAction.do" var="applyChargeActionLoadMethodUrl" >
									<c:param name="method" value="load" />
									<c:param name="accountId" value="${BusinessKey.accountId}" />
									<c:param name="globalCustNum" value="${BusinessKey.customer.globalCustNum}" />
									<c:param name="input" value="ViewClientCharges" />
									<c:param name="randomNUm" value="${sessionScope.randomNUm}" />
									<c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
								</c:url >
		                <html-el:link styleId="view_clientchargesdetail.link.applyCharges"
									href="${applyChargeActionLoadMethodUrl}">
									<mifos:mifoslabel name="clinet.applycharges"
										bundle="ClientUIResources" />
								</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                </c:if></td>
						</tr>
					</table>
					<br>
				</c:if>
				<table width="96%" border="0" cellpadding="3" cellspacing="0">
					<tr>
						<td width="66%" class="headingorange"><mifos:mifoslabel
							name="client.accsum" bundle="ClientUIResources" /></td>
					</tr>
					<tr>
						<td class="fontnormal"><span class="fontnormal"> <mifos:mifoslabel
							name="client.amtdue" bundle="ClientUIResources" isColonRequired="yes"/> <fmt:formatNumber
							value='${BusinessKey.nextDueAmount.amount}' /> </span> <c:if
							test='${BusinessKey.nextDueAmount.amountDoubleValue != 0.0}'>
							<c:url value="customerAction.do" var="customerActionWaiveChargeDueMethodUrl" >
								<c:param name="method" value="waiveChargeDue" />
								<c:param name="globalCustNum" value="${BusinessKey.customer.globalCustNum}" />
								<c:param name="accountType" value="${BusinessKey.accountType.accountTypeId}" />
								<c:param name="prdOfferingName" value="${BusinessKey.customer.displayName}" />
								<c:param name="statusId" value="${BusinessKey.customer.customerStatus.id}" />
								<c:param name="type" value="Client" />
								<c:param name="input" value="Client" />
								<c:param name="accountId" value="${BusinessKey.accountId}" />
								<c:param name="globalAccountNum" value="${BusinessKey.globalAccountNum}" />
								<c:param name="randomNUm" value="${sessionScope.randomNUm}" />
								<c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
							</c:url >
							<html-el:link styleId="view_clientchargesdetail.link.waiveChargeDue"
								href="${customerActionWaiveChargeDueMethodUrl}">
								<mifos:mifoslabel name="client.waive" bundle="ClientUIResources" />
							</html-el:link>
						</c:if> <br>
						<span class="fontnormal"> <mifos:mifoslabel
							name="client.amtoverdue" bundle="ClientUIResources" isColonRequired="yes"/> <fmt:formatNumber
							value='${BusinessKey.totalAmountInArrears.amount}' /> </span> <c:if
							test='${BusinessKey.totalAmountInArrears.amountDoubleValue != 0.0}'>
							<c:url value="customerAction.do" var="customerActionWaiveChargeOverDueMethodUrl" >
								<c:param name="method" value="waiveChargeOverDue" />
								<c:param name="globalCustNum" value="${BusinessKey.customer.globalCustNum}" />
								<c:param name="accountType" value="${BusinessKey.accountType.accountTypeId}" />
								<c:param name="prdOfferingName" value="${BusinessKey.customer.displayName}" />
								<c:param name="statusId" value="${BusinessKey.customer.customerStatus.id}" />
								<c:param name="type" value="Client" />
								<c:param name="input" value="Client" />
								<c:param name="accountId" value="${BusinessKey.accountId}" />
								<c:param name="globalAccountNum" value="${BusinessKey.globalAccountNum}" />
								<c:param name="randomNUm" value="${sessionScope.randomNUm}" />
								<c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
							</c:url >
							<html-el:link styleId="view_clientchargesdetail.link.waiveChargeOverDue"
								href="${customerActionWaiveChargeOverDueMethodUrl}">
								<mifos:mifoslabel name="client.waive" bundle="ClientUIResources" />
							</html-el:link>
						</c:if> <BR>
						<span class="fontnormalbold"> <mifos:mifoslabel
							name="client.total" isColonRequired="Yes"
							bundle="ClientUIResources" /> <fmt:formatNumber
							value='${BusinessKey.totalAmountDue.amount}'/>
						</span></td>
					</tr>
				</table>
				<br>
				<table width="96%" border="0" cellpadding="3" cellspacing="0">
					<tr>
						<td width="70%" class="headingorange"><mifos:mifoslabel
							name="client.upcomcharges" bundle="ClientUIResources" /> (<c:out
							value='${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,BusinessKey.upcomingChargesDate)}' />)
						</td>
						<td width="70%" align="right" class="fontnormal">
						<c:url value="accountAppAction.do" var="accountAppActionGetTrxnHistoryMethodUrl" >
							<c:param name="method" value="getTrxnHistory" />
							<c:param name="statusId" value="${BusinessKey.customer.customerStatus.id}" />
							<c:param name="globalCustNum" value="${BusinessKey.customer.globalCustNum}" />
							<c:param name="input" value="ViewClientCharges" />
							<c:param name="globalAccountNum" value="${BusinessKey.globalAccountNum}" />
							<c:param name="accountId" value="${BusinessKey.accountId}" />
							<c:param name="accountType" value="${BusinessKey.accountType.accountTypeId}" />
							<c:param name="prdOfferingName" value="${BusinessKey.customer.displayName}" />
							<c:param name="headingInput" value="ViewClientCharges" />
							<c:param name="searchInput" value="ClientChargesDetails" />
							<c:param name="randomNUm" value="${sessionScope.randomNUm}" />
							<c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
						</c:url >
						<html-el:link styleId="view_clientchargesdetail.link.transactionHistory"
							href="${accountAppActionGetTrxnHistoryMethodUrl}">
							<mifos:mifoslabel name="client.TransactionHistory"
								bundle="ClientUIResources" />
						</html-el:link></td>
					</tr>
				</table>


				<table width="96%" border="0" cellpadding="3" cellspacing="0">
					<c:if
						test='${!empty BusinessKey.upcomingInstallment.accountFeesActionDetails or (!empty BusinessKey.upcomingInstallment.miscFeeDue) and (BusinessKey.upcomingInstallment.miscFeeDue.amountDoubleValue!=0.0) or (!empty BusinessKey.upcomingInstallment.miscPenaltyDue) and (BusinessKey.upcomingInstallment.miscPenaltyDue.amountDoubleValue!=0.0)}'>
						<tr>
							<td width="19%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="client.FeeType" bundle="ClientUIResources" /></td>
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
									<td width="49%" align="right" class="drawtablerow"><fmt:formatNumber
										value="${upcomingCharges.feeAmount.amount}" /></td>
									<td width="32%" class="drawtablerow">&nbsp;</td>
								</tr>
							</c:if>
						</c:forEach>
						<c:if test='${(!empty BusinessKey.upcomingInstallment.miscFeeDue) and (BusinessKey.upcomingInstallment.miscFeeDue.amountDoubleValue!=0.0)}'>
								<tr>
									<td width="19%" class="drawtablerow"><mifos:mifoslabel name="Customer.miscfee" bundle="CustomerUIResources" /></td>
									<td width="49%" align="right" class="drawtablerow"><fmt:formatNumber
										value="${BusinessKey.upcomingInstallment.miscFeeDue.amount}" /></td>
									<td width="32%" class="drawtablerow">&nbsp;</td>
								</tr>
						</c:if>
						<c:if test='${(!empty BusinessKey.upcomingInstallment.miscPenaltyDue) and (BusinessKey.upcomingInstallment.miscPenaltyDue.amountDoubleValue!=0.0)}'>
								<tr>
									<td width="19%" class="drawtablerow"><mifos:mifoslabel name="Customer.miscpenalty" bundle="CustomerUIResources" /></td>
									<td width="49%" align="right" class="drawtablerow"><fmt:formatNumber
										value="${BusinessKey.upcomingInstallment.miscPenaltyDue.aomunt}" /></td>
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
							name="client.recaccact" bundle="ClientUIResources" /></td>
						<td width="72%" align="right" class="fontnormal">
						<c:url value="customerAction.do" var="customerActionGetAllActivityMethodUrl" >
							<c:param name="method" value="getAllActivity" />
							<c:param name="statusId" value="${BusinessKey.customer.customerStatus.id}" />
							<c:param name="type" value="Client" />
							<c:param name="globalCustNum" value="${BusinessKey.customer.globalCustNum}" />
							<c:param name="prdOfferingName" value="${BusinessKey.customer.displayName}" />
							<c:param name="input" value="ViewClientCharges" />
							<c:param name="globalAccountNum" value="${BusinessKey.globalAccountNum}" />
							<c:param name="accountType" value="${BusinessKey.accountType.accountTypeId}" />
							<c:param name="accountId" value="${BusinessKey.accountId}" />
							<c:param name="randomNUm" value="${sessionScope.randomNUm}" />
							<c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
						</c:url >
						<html-el:link styleId="view_clientchargesdetail.link.viewAllActivities"
							href="${customerActionGetAllActivityMethodUrl}">
							<mifos:mifoslabel name="client.viewallactivities"
								bundle="ClientUIResources" />
						</html-el:link></td>
					</tr>
				</table>
				<table width="96%" border="0" cellpadding="3" cellspacing="0">
					<c:if test='${!empty recentActivities}'>
						<tr>
							<td width="11%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="client.date" bundle="ClientUIResources" /></td>
							<td width="35%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="client.description" bundle="ClientUIResources" /></td>
							<td width="27%" align="right" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="Customer.Amount" bundle="CustomerUIResources" /></td>
							<td width="6%" class="drawtablerowboldnoline">&nbsp;</td>
							<td width="21%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="client.postedby" bundle="ClientUIResources" /></td>
						</tr>
						<c:forEach items="${recentActivities}"
							var="recentActivities">
							<tr>
								<td width="11%" class="drawtablerow"><c:out
									value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,recentActivities.activityDate)}" /></td>
								<td width="35%" class="drawtablerow"><c:out
									value="${recentActivities.description}" /></td>
								<td width="27%" align="right" class="drawtablerow"><fmt:formatNumber
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
							name="client.recurringAccountFees" bundle="ClientUIResources" /></td>
					</tr>
				</table>
				<table width="96%" border="0" cellpadding="3" cellspacing="0">

					<c:forEach items="${BusinessKey.accountFees}" var="recurrenceFees">
						<c:if test="${recurrenceFees.periodic && recurrenceFees.active}">

							<tr class="fontnormal">
								<td width="15%"><c:out value="${recurrenceFees.fees.feeName}" />:</td>
								<td width="30%"><fmt:formatNumber value="${recurrenceFees.accountFeeAmount.amount}" />&nbsp;&nbsp;
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
								<c:url value="accountAppAction.do" var="accountAppActionRemoveFeesMethodUrl" >
									<c:param name="method" value="removeFees" />
									<c:param name="statusId" value="${BusinessKey.customer.customerStatus.id}" />
									<c:param name="globalCustNum" value="${BusinessKey.customer.globalCustNum}" />
									<c:param name="feeId" value="${recurrenceFees.fees.feeId}" />
									<c:param name="accountId" value="${recurrenceFees.account.accountId}" />
									<c:param name="fromPage" value="client" />
									<c:param name="input" value="Client" />
									<c:param name="globalAccountNum" value="${BusinessKey.globalAccountNum}" />
									<c:param name="accountType" value="${BusinessKey.accountType.accountTypeId}" />
									<c:param name="prdOfferingName" value="${BusinessKey.customer.displayName}" />
									<c:param name="randomNUm" value="${sessionScope.randomNUm}" />
									<c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
								</c:url >
								<html-el:link styleId="view_clientchargesdetail.link.remove"
									href="${accountAppActionRemoveFeesMethodUrl}">
									<mifos:mifoslabel name="client.remove"
										bundle="ClientUIResources" />
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
