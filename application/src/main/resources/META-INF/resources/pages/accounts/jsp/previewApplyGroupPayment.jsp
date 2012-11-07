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
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">

	<tiles:put name="body" type="string">
	<span id="page.id" title="ReviewApplyGroupPayment"></span>

		<SCRIPT>
	function ViewDetails(){
			customerAccountActionForm.action="customerAccountAction.do?method=load";
		customerAccountActionForm.submit();
	}
	function ViewLoanDetails(){
		goBackToLoanAccountDetails.submit();
	}
	function goToPrevious(form){
		form.action="applyGroupPaymentAction.do?method=previous";
		form.submit()
	}
</SCRIPT>
		<form name="goBackToLoanAccountDetails" method="get" action ="viewGroupLoanAccountDetails.ftl">
			<input type="hidden" name='globalAccountNum' value="${param.globalAccountNum}"/>
		</form>
		<html-el:form method="post" action="/applyGroupPaymentAction.do?method=applyPayment">
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />	

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <customtags:headerLink />
						<c:url value="customerAccountAction.do" var="customerAccountActionLoadMethodUrl" >
							<c:param name="method" value="load" />
							<c:param name="globalCustNum" value="${param.globalCustNum}" />
						</c:url >
					<c:choose>
						<c:when test="${param.input == 'loan'}">
						</c:when>
						<c:otherwise>
							<html-el:link styleId="reviewapplypayment.link.viewCharges" href="customerAccountAction.do?method=load&globalCustNum=${param.globalCustNum}">
					          	<c:if test="${param.input == 'ViewCenterCharges'}">
	          						<mifos:mifoslabel name="Center.CenterCharges" bundle="CenterUIResources"/>
	          					</c:if>
	          					<c:if test="${param.input == 'ViewGroupCharges'}">
					          		<mifos:mifoslabel name="Center.GroupCharges" bundle="CenterUIResources"/>
					          	</c:if>
					          	<c:if test="${param.input == 'ViewClientCharges'}">
	          						<mifos:mifoslabel name="Center.ClientCharges" bundle="CenterUIResources"/>
	          					</c:if>
							</html-el:link>
						</c:otherwise>
					</c:choose> </span></td>
				</tr>
			</table>
			<br>
			<table width="96%" border="0" cellpadding="3" cellspacing="0">
				<tr>
					<td width="100%" colspan="2" class="headingorange"><span
						class="heading"><c:out value="${param.prdOfferingName}" /> # <c:out
						value="${param.globalAccountNum}" /> - </span> <mifos:mifoslabel
						name="accounts.reviewtransaction" /></td>
				</tr>
				<tr>
					<td><font class="fontnormalRedBold"> <span id="reviewapplypayment.error.message"><html-el:errors
						bundle="accountsUIResources" /> </font></td>
				</tr>

				<tr>
					<td colspan="2" class="fontnormal"><mifos:mifoslabel
						name="accounts.edittrans" /></td>
				</tr>
				<tr>
					<td colspan="2" class="blueline"><img src="pages/framework/images/trans.gif"
						width="10" height="5"></td>
				</tr>
			</table>
			<br>
			<table width="96%" border="0" cellspacing="0" cellpadding="1">
				<tr>
					<td align="right" class="fontnormalbold"><mifos:mifoslabel
						name="accounts.date_of_trxn" isColonRequired="Yes" /></td>
					<td class="fontnormal"><c:out
						value="${applyPaymentActionForm.transactionDate}" /></td>
				</tr>
				<tr>
					<td width="22%" align="right" class="fontnormalbold"><mifos:mifoslabel
						name="accounts.amount" isColonRequired="Yes" /></td>
					<td width="78%" class="fontnormal"><fmt:formatNumber
						value="${applyPaymentActionForm.amount}" /></td>
				</tr>
				<tr>
					<td align="right" class="fontnormalbold"><mifos:mifoslabel
						name="accounts.mode_of_payment" isColonRequired="Yes"  /></td>
					<td class="fontnormal"><c:forEach var="payment" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'PaymentType')}" >
						<c:if
							test="${payment.id == sessionScope.applyPaymentActionForm.paymentTypeId}">
							<c:out value="${payment.displayValue}" />
						</c:if>
					</c:forEach></td>
				</tr>
				<tr>
					<td align="right" class="fontnormalbold"><mifos:mifoslabel
						name="accounts.receiptid" isColonRequired="Yes" /></td>
					<td class="fontnormal"><c:out
						value="${applyPaymentActionForm.receiptId}" /></td>
				</tr>
				<tr>
					<td align="right" class="fontnormalbold"><mifos:mifoslabel
						name="accounts.receiptdate" isColonRequired="Yes" /></td>
					<td class="fontnormal"><c:out
						value="${applyPaymentActionForm.receiptDate}" /></td>
				</tr>
				<tr>
					<td height="3" colspan="2" align="center">&nbsp;</td>
				</tr>
				<tr>
					<td height="3" colspan="2"><html-el:button styleId="reviewapplypayment.button.editTransaction" property="Cancel"
						styleClass="insidebuttn"
						onclick="goToPrevious(this.form)">
						<mifos:mifoslabel name="accounts.edittrxn"></mifos:mifoslabel>
					</html-el:button></td>
				</tr>
				<tr>
					<td height="3" colspan="2" align="center" class="blueline">&nbsp;</td>
				</tr>
				<tr>
					<td height="3" colspan="2" align="center" class="fontnormal">&nbsp;</td>
				</tr>
			</table>
			<table width="96%" border="0" cellspacing="0" cellpadding="1">
				<tr>
					<td align="center"><html-el:submit styleId="reviewapplypayment.button.submit" styleClass="buttn"
						property="Preview">
						<mifos:mifoslabel name="accounts.submit">
						</mifos:mifoslabel>
					</html-el:submit> &nbsp; <c:choose>
								<c:when test="${param.input == 'loan'}">
									<html-el:button styleId="reviewapplypayment.button.cancel" styleClass="cancelbuttn" property="Cancel"
										onclick="ViewLoanDetails()">
										<mifos:mifoslabel name="accounts.cancel"></mifos:mifoslabel>
									</html-el:button>

								</c:when>
								<c:otherwise>
									<html-el:button styleId="reviewapplypayment.button.cancel" styleClass="cancelbuttn" property="Cancel"
										onclick="ViewDetails()">
										<mifos:mifoslabel name="accounts.cancel"></mifos:mifoslabel>
									</html-el:button>

								</c:otherwise>
							</c:choose></td>
				</tr>
			</table>
						<html-el:hidden property="prdOfferingName" value="${param.prdOfferingName}" />
						<html-el:hidden property="input" value="${param.input}" />
						<html-el:hidden property="accountId" value="${param.accountId}" />
						<html-el:hidden property="globalCustNum" value="${param.globalCustNum}" />
						<html-el:hidden property="globalAccountNum" value="${param.globalAccountNum}" />
			</html-el:form>
		<html-el:form action="customerAccountAction.do?method=load">
			<html-el:hidden property="globalCustNum" value="${param.globalCustNum}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
