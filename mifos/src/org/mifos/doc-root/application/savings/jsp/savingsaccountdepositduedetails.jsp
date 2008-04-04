<!--
 
 * savingsaccountdetail.jsp  version: 1.0
 
 
 
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
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@taglib uri="/mifos/savingsoverdue" prefix="savingsoverdue"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<html-el:form method="post" action="/savingsAction.do">
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
		
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"><customtags:headerLink /> </span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" height="24" align="left" valign="top" class="paddingL15T15">
						<table width="95%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td width="83%" class="headingorange">
									<span class="heading"> <c:out value="${BusinessKey.savingsOffering.prdOfferingName}" /> #<c:out value="${BusinessKey.globalAccountNum}" /> -</span>
									<mifos:mifoslabel name="Savings.depositduedetails" />
								</td>
							</tr>
							<tr>
								<td>
									<img src="pages/framework/images/trans.gif" width="10" height="12">
								</td>
							</tr>
						</table>
						<font class="fontnormalRedBold"><html-el:errors bundle="SavingsUIResources" /></font>
						<table width="60%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td bgcolor="#F0D4A5" style="padding-left:10px; padding-bottom:3px;">
									<span class="fontnormalbold"><mifos:mifoslabel name="Savings.applytrans" isColonRequired="yes"/></span>&nbsp;&nbsp;&nbsp;&nbsp; 
									<html-el:link  href="savingsDepositWithdrawalAction.do?method=load&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
										<mifos:mifoslabel name="Savings.makeDepositWithdrawl" />
									</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
									<html-el:link href="savingsApplyAdjustmentAction.do?method=load&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
										<mifos:mifoslabel name="Savings.applyAdjustment" />
									</html-el:link>
								</td>
							</tr>
						</table>
						<br>
						<table width="60%" border="0" cellpadding="3" cellspacing="0">
							<tr class="drawtablerowboldnoline">
								<td width="60%">
									&nbsp;
								</td>
								<td width="27%" align="right">
									<mifos:mifoslabel name="Savings.amount" />
								</td>
								<td width="13%" align="right">
									&nbsp;
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="Savings.nextdeposit" />
								</td>
								<td align="right" class="drawtablerow">
									<c:out value="${BusinessKey.totalAmountDueForNextInstallment}" />
								</td>
								<td align="right" class="drawtablerow">
									<c:choose>
										<c:when test="${BusinessKey.totalAmountDueForNextInstallment.amount > 0}">
											<html-el:link action="/savingsAction.do?method=waiveAmountDue&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
												<mifos:mifoslabel name="Savings.waive" />
											</html-el:link>
										</c:when>
										<c:otherwise>
										&nbsp;
									</c:otherwise>
									</c:choose>
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="Savings.pastdepositsdue" />
								</td>
								<td align="right" class="drawtablerow">
									&nbsp;
								</td>
								<td align="right" class="drawtablerow">
									&nbsp;
								</td>
							</tr>
							<savingsoverdue:savingsoverdueamount />
							<tr>
								<td class="drawtablerow">
									<em>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <mifos:mifoslabel name="Savings.subtotal" /> </em>
								</td>
								<td align="right" class="drawtablerow">
									<c:out value="${BusinessKey.totalAmountInArrears}" />
								</td>
								<td align="right" class="drawtablerow">
									<c:choose>
										<c:when test="${BusinessKey.totalAmountInArrears.amount > 0}">
											<html-el:link action="/savingsAction.do?method=waiveAmountOverDue&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
												<mifos:mifoslabel name="Savings.waive" />
											</html-el:link>
										</c:when>
										<c:otherwise>
										&nbsp;
									</c:otherwise>
									</c:choose>
								</td>
							</tr>
							<tr>
								<td class="drawtablerowbold">
									<mifos:mifoslabel name="Savings.totalAmountDue" />
									<c:if test="${!empty BusinessKey.nextMeetingDate}">										
										<mifos:mifoslabel name="Savings.on" />
										<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,BusinessKey.nextMeetingDate)}" />
									</c:if>
								</td>
								<td align="right" class="drawtablerow">
									<c:out value="${BusinessKey.totalAmountDue}" />
								</td>
								<td align="right" class="drawtablerow">
									&nbsp;
								</td>
							</tr>
							<tr>
								<td colspan="3">
									&nbsp;
								</td>
							</tr>
						</table>
						<table width="96%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td>
									<html-el:submit styleClass="buttn">
										<mifos:mifoslabel name="Savings.btnreturntodetails" />
									</html-el:submit>
								</td>
							</tr>
						</table>
						<html-el:hidden property="method" value="get" />
						<html-el:hidden property="globalAccountNum" value="${BusinessKey.globalAccountNum}" />
						<html-el:hidden property="recordOfficeId" value="${param.recordOfficeId}" />
						<html-el:hidden property="recordLoanOfficerId" value="${param.recordLoanOfficerId}" />
    					 <html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
					</td>
				</tr>
				<tr>
					<td height="24" align="left" valign="top" class="paddingL15T15">
						&nbsp;
					</td>
				</tr>
			</table>
			<br>
		</html-el:form>
	</tiles:put>
</tiles:insert>
