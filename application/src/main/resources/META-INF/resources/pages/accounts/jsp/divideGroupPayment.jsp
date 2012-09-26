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
<script type="text/javascript" src="pages/js/applyPayment.js"></script>

<tiles:insert definition=".clientsacclayoutsearchmenu">
<%@ taglib uri="/sessionaccess" prefix="session"%>

	<tiles:put name="body" type="string">
	<span id="page.id" title="ApplyPayment"></span>
	<mifos:NumberFormattingInfo />
    <span id="transfer.id" title="${applyPaymentActionForm.transferPaymentTypeId}"></span>
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
		<form name="goBackToLoanAccountDetails" method="get" action ="viewLoanAccountDetails.ftl">
			<input type="hidden" name='globalAccountNum' value="${param.globalAccountNum}"/>
		</form>
		<html-el:form method="post"
			action="/applyGroupPaymentAction.do?method=divide"
			focus="paymentTypeId">
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'AccountType')}" var="AccountType" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'AccountId')}" var="AccountId" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'memberAccounts')}" var="memberAccounts" />
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
					<table width="96%" border="0" cellpadding="0" cellspacing="0">
						<c:forEach items="${memberAccounts}" var="member" varStatus="rowId">
							<tr>
								<td>
								     ${member}
								</td>
								<td>
									<html-el:text property="individualValues[${rowId.count-1}]" styleClass="separatedNumber"
										styleId="applypayment.input.amount"
										name="applyPaymentActionForm" />
									
								</td>
							</tr>
						</c:forEach>
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
									<html-el:submit styleId="applypayment.button.reviewTransaction" styleClass="buttn submit"  property="Preview">
										<mifos:mifoslabel name="accounts.reviewtransaction">
										</mifos:mifoslabel>
									</html-el:submit>
									<html-el:button styleId="applypayment.button.cancel" styleClass="cancelbuttn" property="Cancel"
										onclick="ViewLoanDetails()">
										<mifos:mifoslabel name="accounts.cancel"></mifos:mifoslabel>
									</html-el:button>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			<html-el:hidden property="prdOfferingName" value="${param.prdOfferingName}" />
			<html-el:hidden property="input" value="${param.input}" />
			<html-el:hidden property="globalCustNum" value="${param.globalCustNum}" />
			<html-el:hidden property="globalAccountNum" value="${param.globalAccountNum}" />
			<html-el:hidden property="accountType" value="${AccountType}" />			
			<html-el:hidden property="accountId" value="${AccountId}" />
		</html-el:form>
		<html-el:form action="customerAccountAction.do?method=load">
			<html-el:hidden property="globalCustNum" value="${param.globalCustNum}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
