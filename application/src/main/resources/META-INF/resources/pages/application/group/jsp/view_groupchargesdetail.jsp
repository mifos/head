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
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>


<tiles:insert definition=".clientsacclayoutsearchmenu">
	<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />	
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
			   var="BusinessKey" />
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'recentActivities')}"
			   var="recentActivities" />
	<tiles:put name="body" type="string">
	<span id="page.id" title="ViewGroupChargesDetail" ></span>
	<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
	<fmt:setBundle basename="org.mifos.config.localizedResources.GroupUIResources"/>
		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td class="bluetablehead05"><span class="fontnormal8pt"> <customtags:headerLink />
				</span><span class="fontnormal8ptbold"> <fmt:message key="Group.charges">
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
					</fmt:message>
					</span></td>
			</tr>
		</table>
		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td width="70%" height="24" align="left" valign="top"
					class="paddingL15T15">
				<table width="96%" border="0" cellpadding="3" cellspacing="0">
					<font class="fontnormalRedBold"><span id="view_groupchargesdetail.error.message"><html-el:errors
						bundle="CenterUIResources" /></span></font>
					<tr>
						<td width="70%" class="headingorange">
						<fmt:message key="Group.charges">
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
					</fmt:message>
							
							</td>
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

							&nbsp;&nbsp;&nbsp;&nbsp;
                            <c:url value="applyPaymentAction.do" var="applyPaymentUrl" >
                                <c:param name="method" value="load" />
                                <c:param name="globalCustNum" value="${BusinessKey.customer.globalCustNum}" />
                                <c:param name="prdOfferingName" value="${BusinessKey.customer.displayName}" />
                                <c:param name="input" value="ViewGroupCharges" />
                                <c:param name="globalAccountNum" value="${BusinessKey.globalAccountNum}" />
                                <c:param name="accountType" value="${BusinessKey.accountType.accountTypeId}" />
                                <c:param name="accountId" value="${BusinessKey.accountId}" />
                                <c:param name="randomNUm" value="${sessionScope.randomNUm}" />
                                <c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
                            </c:url> 
                            <html-el:link styleId="view_groupchargesdetail.link.applyPayment"
                                href="${applyPaymentUrl}">
                                <mifos:mifoslabel name="accounts.apply_payment" />
                            </html-el:link> 
                            <c:if test="${BusinessKey.customer.customerStatus.id == 9 || BusinessKey.customer.customerStatus.id == 10}"> 
		               &nbsp;&nbsp;&nbsp;&nbsp;
                            <c:url value="custApplyAdjustment.do" var="applyAdjustmentUrl" >
                                <c:param name="method" value="loadAdjustment" />
                                <c:param name="globalCustNum" value="${BusinessKey.customer.globalCustNum}" />
                                <c:param name="prdOfferingName" value="${BusinessKey.customer.displayName}" />
                                <c:param name="input" value="ViewGroupCharges" />
                                <c:param name="globalAccountNum" value="${BusinessKey.globalAccountNum}" />
                                <c:param name="randomNUm" value="${sessionScope.randomNUm}" />
                                <c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
                            </c:url >
                            <html-el:link styleId="view_groupchargesdetail.link.applyAdjustment"
                                    href="${applyAdjustmentUrl}">
                                    <mifos:mifoslabel name="Group.applyAdjustment"
                                        bundle="GroupUIResources" />
                            </html-el:link>
							</c:if> <c:if
								test="${BusinessKey.customer.customerStatus.id == 7 || BusinessKey.customer.customerStatus.id == 8 || BusinessKey.customer.customerStatus.id == 9 || BusinessKey.customer.customerStatus.id == 10}">
	                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <c:url value="applyChargeAction.do" var="applyChargesUrl" >
                                <c:param name="method" value="load" />
                                <c:param name="accountId" value="${BusinessKey.accountId}" />
                                <c:param name="globalCustNum" value="${BusinessKey.customer.globalCustNum}" />
                                <c:param name="input" value="ViewGroupCharges" />
                                <c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
                                <c:param name="randomNUm" value="${sessionScope.randomNUm}" />
                                <c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
                             </c:url >
                            <html-el:link styleId="view_groupchargesdetail.link.applyCharges"
                                    href="${applyChargesUrl}">
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
							name="Group.amtdue" bundle="GroupUIResources" />: <fmt:formatNumber
							value='${BusinessKey.nextDueAmount.amount}' /> </span> <c:if
							test='${BusinessKey.nextDueAmount.amountDoubleValue != 0.0}'>
                            <c:url value="customerAction.do" var="waiveChargeDueUrl">
                                <c:param name="method" value="waiveChargeDue" />
                                <c:param name="globalCustNum" value="${BusinessKey.customer.globalCustNum}" />
                                <c:param name="accountId" value="${BusinessKey.accountId}" />
                                <c:param name="accountType" value="${BusinessKey.accountType.accountTypeId}" />
                                <c:param name="prdOfferingName" value="${BusinessKey.customer.displayName}" />
                                <c:param name="statusId" value="${BusinessKey.customer.customerStatus.id}" />
                                <c:param name="type" value="Group" />
                                <c:param name="input" value="Group" />
                                <c:param name="accountId" value="${BusinessKey.accountId}" />
                                <c:param name="globalAccountNum" value="${BusinessKey.globalAccountNum}" />
                                <c:param name="randomNUm" value="${sessionScope.randomNUm}" />
                                <c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
                            </c:url >
                            <html-el:link styleId="view_groupchargesdetail.link.waiveChargeDue"
                                href="${waiveChargeDueUrl}">
                                <mifos:mifoslabel name="Group.waive" bundle="GroupUIResources" />
                            </html-el:link>
						</c:if> <br>
						<span class="fontnormal"> <mifos:mifoslabel
							name="Group.amtoverdue" bundle="GroupUIResources" />: <fmt:formatNumber
							value='${BusinessKey.totalAmountInArrears.amount}' /> </span> <c:if
							test='${BusinessKey.totalAmountInArrears.amountDoubleValue != 0.0}'>
                            <c:url value="customerAction.do" var="waiveChargeOverDueUrl">
                                <c:param name="method" value="waiveChargeOverDue" />
                                <c:param name="globalCustNum" value="${BusinessKey.customer.globalCustNum}" />
                                <c:param name="accountType" value="${BusinessKey.accountType.accountTypeId}" />
                                <c:param name="prdOfferingName" value="${BusinessKey.customer.displayName}" />
                                <c:param name="statusId" value="${BusinessKey.customer.customerStatus.id}" />
                                <c:param name="type" value="Group" />
                                <c:param name="input" value="Group" />
                                <c:param name="accountId" value="${BusinessKey.accountId}" />
                                <c:param name="globalAccountNum" value="${BusinessKey.globalAccountNum}" />
                                <c:param name="randomNUm" value="${sessionScope.randomNUm}" />
                                <c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
                            </c:url >
                            <html-el:link styleId="view_groupchargesdetail.link.waiveChargeOverDue"
                                href="${waiveChargeOverDueUrl}">
                                <mifos:mifoslabel name="Group.waive" bundle="GroupUIResources" />
                            </html-el:link>
						</c:if> <BR>
						<span class="fontnormalbold"> <mifos:mifoslabel
							name="accounts.total" isColonRequired="Yes"></mifos:mifoslabel> <fmt:formatNumber
							value='${BusinessKey.totalAmountDue.amount}'/>
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
						<td width="70%" align="right" class="fontnormal">
                            <c:url value="accountAppAction.do" var="getTrxnHistoryUrl">
                                <c:param name="method" value="getTrxnHistory" />
                                <c:param name="statusId" value="${BusinessKey.customer.customerStatus.id}" />
                                <c:param name="globalCustNum" value="${BusinessKey.customer.globalCustNum}" />
                                <c:param name="accountType" value="${BusinessKey.accountType.accountTypeId}" />
                                <c:param name="prdOfferingName" value="${BusinessKey.customer.displayName}" />
                                <c:param name="type" value="Group" />
                                <c:param name="input" value="ViewGroupCharges" />
                                <c:param name="headingInput" value="ViewGroupCharges" />
                                <c:param name="searchInput" value="GroupChargesDetails" />
                                <c:param name="accountId" value="${BusinessKey.accountId}" />
                                <c:param name="globalAccountNum" value="${BusinessKey.globalAccountNum}" />
                                <c:param name="randomNUm" value="${sessionScope.randomNUm}" />
                                <c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
                            </c:url >
                            <html-el:link styleId="view_groupchargesdetail.link.transactionHistory"
                                href="${getTrxnHistoryUrl}">
                                <mifos:mifoslabel name="Center.TransactionHistory" />
                            </html-el:link>
                        </td>
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
										value="${BusinessKey.upcomingInstallment.miscPenaltyDue.amount}" /></td>
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
						<td width="72%" align="right" class="fontnormal">
                            <c:url value="customerAction.do" var="viewAllActivitiesUrl" >
                                <c:param name="method" value="getAllActivity" />
                                <c:param name="statusId" value="${BusinessKey.customer.customerStatus.id}" />
                                <c:param name="type" value="Group" />
                                <c:param name="input" value="ViewGroupCharges" />
                                <c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
                                <c:param name="randomNUm" value="${sessionScope.randomNUm}" />
                                <c:param name="globalCustNum" value="${BusinessKey.customer.globalCustNum}" />
                                <c:param name="prdOfferingName" value="${BusinessKey.customer.displayName}" />
                                <c:param name="globalAccountNum" value="${BusinessKey.globalAccountNum}" />
                                <c:param name="accountType" value="${BusinessKey.accountType.accountTypeId}" />
                                <c:param name="accountId" value="${BusinessKey.accountId}" />
                             </c:url >
                            <html-el:link styleId="view_groupchargesdetail.link.viewAllActivities"
                                href="${viewAllActivitiesUrl}">
                                <mifos:mifoslabel name="Group.viewallactivities"
                                    bundle="GroupUIResources" />
                            </html-el:link>
                        </td>
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
							<fmt:formatDate value="${recentActivities.activityDate}" pattern="yyyy-MM-dd" var="activityDate" />
							<tr>
								<td width="11%" class="drawtablerow"><c:out
									value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,activityDate)}" /></td>
								<td width="35%" class="drawtablerow"><c:out
									value="${recentActivities.description}" /></td>
								<td width="27%" align="right" class="drawtablerow">
								<c:choose>
									<c:when test="${recentActivities.amount == '-'}">
										<c:out value="${recentActivities.amount}" />
									</c:when>
									<c:otherwise>
										<fmt:formatNumber value="${recentActivities.amount}"/>
									</c:otherwise>
								</c:choose>
								</td>
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

								<td width="30%"><c:out value="${recurrenceFees.accountFeeAmount}" />&nbsp;&nbsp;
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
                                <c:url value="accountAppAction.do" var="removeFeesUrl">
                                    <c:param name="method" value="removeFees" />
                                    <c:param name="globalCustNum" value="${BusinessKey.customer.globalCustNum}" />
                                    <c:param name="feeId" value="${recurrenceFees.fees.feeId}" />
                                    <c:param name="accountId" value="${BusinessKey.accountId}" />
                                    <c:param name="input" value="Group" />
                                    <c:param name="fromPage" value="group" />
                                    <c:param name="accountType" value="${BusinessKey.accountType.accountTypeId}" />
                                    <c:param name="prdOfferingName" value="${BusinessKey.customer.displayName}" />
                                    <c:param name="statusId" value="${BusinessKey.customer.customerStatus.id}" />
                                    <c:param name="globalAccountNum" value="${BusinessKey.globalAccountNum}" />
                                    <c:param name="randomNUm" value="${sessionScope.randomNUm}" />
                                    <c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
                                </c:url >
                                <html-el:link styleId="view_groupchargesdetail.link.remove"
                                    href="${removeFeesUrl}">
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
