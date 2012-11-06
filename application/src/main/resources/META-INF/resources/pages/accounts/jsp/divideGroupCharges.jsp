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

<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<script type="text/javascript" src="pages/js/jquery/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="pages/js/singleitem.js"></script>
<script type="text/javascript" src="pages/js/separator.js"></script>

<tiles:insert definition=".clientsacclayoutsearchmenu">
<%@ taglib uri="/sessionaccess" prefix="session"%>

	<tiles:put name="body" type="string">
	<span id="page.id" title="DivideGroupCharges"></span>
	<mifos:NumberFormattingInfo />
<SCRIPT>
	function ViewDetails(){
		customerAccountActionForm.action="customerAccountAction.do?method=load";
		customerAccountActionForm.submit();
	}

	function ViewLoanDetails(){
		goBackToLoanAccountDetails.submit();
	}
</SCRIPT>
		<SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>
		<form name="goBackToLoanAccountDetails" method="get" action ="viewGroupLoanAccountDetails.ftl">
			<input type="hidden" name='globalAccountNum' value="${param.globalAccountNum}"/>
		</form>
		<html-el:form method="post" action="/applyChargeAction.do?method=update" focus="chargeType">
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'AccountId')}" var="AccountId"/>
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'memberInfos')}" var="memberAccounts"/>
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<table width="96%" border="0" cellpadding="3" cellspacing="0">
				<tr>
					<td width="100%" colspan="2" class="headingorange"><span
						class="heading"><c:out value="${param.prdOfferingName}" /> # <c:out
						value="${param.globalAccountNum}" /> - </span> <mifos:mifoslabel
						name="accounts.apply.charges.divide" bundle="accountsUIResources" /></td>
				</tr>
				<tr>
					<td><font class="fontnormalRedBold"> <span id="reviewapplypayment.error.message"><html-el:errors
						bundle="accountsUIResources" /> </font></td>
				</tr>
				<tr>
					<td colspan="2" class="fontnormal"><mifos:mifoslabel
						name="accounts.apply.charges.divide.amount" bundle="accountsUIResources"/></td>
				</tr>
				<tr>
					<td colspan="2" class="blueline"><img src="pages/framework/images/trans.gif"
						width="10" height="5"></td>
				</tr>
			</table>			
			<table width="96%" border="0" cellpadding="0" cellspacing="0">
					<c:set value="${ApplyChargeActionForm.individualValues}" var="memberIdsAndValues"/>
							<tr>
								<td>
									<table width="96%" border="0" cellspacing="0" cellpadding="1">
									<c:forEach items="${memberAccounts}" var="member" varStatus="rowId">
									<c:if test="${rowId.count -1 == 0}">
										<tr>
										<td width="2%"></td>
											<td align="center" class="fontnormalbold" colspan="2" width="5%">
												<mifos:mifoslabel name="accounts.acc_owner"/>
											</td>
											<td align="left" class="fontnormalbold" colspan="1" width="20%">
												<mifos:mifoslabel name="accounts.amount"/>
											</td>
										<td></td>
										</tr>
									</c:if>
									<tr>
										<td align="right" class="fontnormal" width="15%"> 
										<span class="paddingright03">${rowId.count}.</span>
										</td>
										<td align="right" class="fontnormal" width="5%">
											<span class="paddingright20"><mifos:mifoslabel name="accounts.apply.charges.client" 
													bundle="accountsUIResources"/></span> <br/>
											<mifos:mifoslabel name="accounts.apply.charges.account" 
													bundle="accountsUIResources"/>
										</td>
										<td align=center class="fontnormal" width="5%">
											<c:out value="${member.customer.displayName}" /> <br/>
											<c:out value="${member.globalAccountNum}" />
										</td>
										<td align="left" colspan="1" width="5%">
										<c:set value="${member.accountId}" var="accId"/>
											<html-el:text property="updateIndividualValues(${accId})" styleClass="separatedNumber"
												styleId="applypayment.input.amount" value="${memberIdsAndValues[accId]}"
												name="individualValues.value[${rowId.count-1}]" />
										</td>
										<td width="2%"></td>
									</tr>
									</c:forEach>
									</table>
								</td>
							</tr>
					</table>
					<table width="96%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
						<tr>
							<td align="center">&nbsp;</td>
						</tr>

						<tr>
							<td align="center">
									<html-el:submit styleId="applypayment.button.reviewTransaction" styleClass="buttn submit"  property="Confirm">
										<mifos:mifoslabel name="accounts.apply.charges.confirm">
										</mifos:mifoslabel>
									</html-el:submit>
									<html-el:button styleId="applypayment.button.cancel" styleClass="cancelbuttn" property="Cancel"
										onclick="ViewLoanDetails()">
										<mifos:mifoslabel name="accounts.cancel"></mifos:mifoslabel>
									</html-el:button>
							</td>
						</tr>
					</table>
            <c:forEach var="fee" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'applicableChargeList')}" >
                    <html-el:hidden property="amount" value="${fee.amountOrRate}"/>
                    <html-el:hidden property="changedAmount" value="${fee.amountOrRate}"/>
                    <html-el:hidden property="formulaId" value="${fee.formula}"/>
                    <html-el:hidden property="periodicity" value="${fee.periodicity}"/>
                    <html-el:hidden property="paymentType" value="${fee.paymentType}"/>
            </c:forEach>
			<html-el:hidden property="globalCustNum" value="${param.globalCustNum}" />
			<html-el:hidden property="globalAccountNum" value="${param.globalAccountNum}" />
			<html-el:hidden property="accountId" value="${BusinessKey.accountId}"/>
		</html-el:form>
		<html-el:form action="customerAccountAction.do?method=load">
			<html-el:hidden property="globalCustNum" value="${param.globalCustNum}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
