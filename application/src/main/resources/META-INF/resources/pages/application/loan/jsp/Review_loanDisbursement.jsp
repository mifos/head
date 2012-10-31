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

<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
<%@ taglib uri="/sessionaccess" prefix="session"%>
	<tiles:put name="body" type="string">
	<span id="page.id" title="ReviewLoanDisbursement"></span>

		<script>
			function fun_return()
					{
						goBackToLoanAccountDetails.submit();
					}
					
			function fun_edit(form)
					{
						
						form.action="loanDisbursementAction.do?method=previous";
						form.submit();
					}
	</script>
		<c:set value="viewLoanAccountDetails.ftl" var="formAction" />
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'isGroupLoan')}" var="isGroupLoan" />
		<c:if test="${isGroupLoan }">
			<c:set value="viewGroupLoanAccountDetails.ftl" var="formAction" />
		</c:if>
		<form name="goBackToLoanAccountDetails" method="get" action ="${formAction }">
			<input type="hidden" name='globalAccountNum' value="${loanDisbursementActionForm.globalAccountNum}"/>
		</form>
		<html-el:form action="loanDisbursementAction.do?method=update">
		<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <customtags:headerLink />
					</span></td>
				</tr>

			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				
				<tr>
					<td width="70%" height="24" align="left" valign="top"
						class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="100%" colspan="2" class="headingorange"><span
								class="heading"> <c:out
								value="${loanDisbursementActionForm.prdOfferingName}" />&nbsp;#&nbsp;
							<c:out value="${loanDisbursementActionForm.globalAccountNum}" />
							&nbsp;-&nbsp; </span><mifos:mifoslabel
								name="loan.reviewtransaction" /></td>
						</tr>
						<tr>
							<td colspan="2" class="fontnormal"><mifos:mifoslabel
								name="loan.edittrans" /></td>
						</tr>
						<tr>
						  <td>
						   <font class="fontnormalRedBold"> <span id="Review_loanDisbursement.error.message"><html-el:errors
							bundle="loanUIResources" /></span> </font>
						  </td>
						</tr>
						<tr>
							<td colspan="2" class="blueline"><img
								src="pages/framework/images/trans.gif" width="10" height="5"></td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellspacing="0" cellpadding="3">
						<tr>
							<td colspan="2" align="right" class="fontnormal"><img
								src="pages/framework/images/trans.gif" width="10" height="2"></td>
						</tr>
						<tr>
							<td align="right" class="fontnormalbold"><mifos:mifoslabel
								name="loan.dateofdisb/payment" />:&nbsp;</td>
							<td class="fontnormal"><c:out
								value="${loanDisbursementActionForm.transactionDate}" /></td>
						</tr>
						<tr>
							<td align="right" class="fontnormalbold"><mifos:mifoslabel
								name="loan.receiptId" />:&nbsp;</td>
							<td class="fontnormal"><c:out
								value="${loanDisbursementActionForm.receiptId}" /></td>
						</tr>
						<tr>
							<td align="right" class="fontnormalbold"><mifos:mifoslabel
								name="loan.receiptdate" />:&nbsp;</td>
							<td class="fontnormal"><c:out
								value="${loanDisbursementActionForm.receiptDate}" /></td>
						</tr>
						<tr>
							<td colspan="2" class="fontnormalbold"><img
								src="pages/framework/images/trans.gif" width="10" height="2"></td>
						</tr>
						<tr>
							<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
								name="loan.disbdetails" /></td>
						</tr>
						<tr>
							<td width="28%" align="right" class="fontnormalbold"><mifos:mifoslabel
								name="${ConfigurationConstants.LOAN}" /><mifos:mifoslabel
								name="loan.amt" />:&nbsp;</td>
							<td width="72%" class="fontnormal"><fmt:formatNumber
								value="${loanDisbursementActionForm.loanAmount}" /></td>
						</tr>
						<tr>
							<td align="right" class="fontnormalbold"><mifos:mifoslabel
								name="loan.mode_of_payment" />:&nbsp;</td>
							<td class="fontnormal"><c:forEach var="payment" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'PaymentType')}" >
								<c:if
									test="${payment.id == sessionScope.loanDisbursementActionForm.paymentTypeId}">
									<c:out value="${payment.name}" />
								</c:if>
							</c:forEach></td>
						</tr>
					</table>
					<table width="95%" border="0" cellspacing="0" cellpadding="3">
						<tr>
							<td colspan="2" align="right" class="fontnormal"><img
								src="pages/framework/images/trans.gif" width="10" height="2"></td>
						</tr>
						<tr>
							<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
								name="loan.paymentdetails" /></td>
						</tr>
						<tr>
							<td width="28%" align="right" class="fontnormalbold"><mifos:mifoslabel
								name="loan.amount" />:&nbsp;</td>
							<td width="72%" class="fontnormal"><fmt:formatNumber
								value="${loanDisbursementActionForm.amount}" /></td>
						</tr>
						<tr>
							<td align="right" class="fontnormalbold"><mifos:mifoslabel
								name="loan.mode_of_payment" />:&nbsp;</td>
							<td class="fontnormal">
							
							<c:forEach var="payment" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'PaymentType')}" >
								<c:if
									test="${payment.id == sessionScope.loanDisbursementActionForm.paymentModeOfPayment}">
									<c:out value="${payment.name}" />
								</c:if>
							</c:forEach>
							</td>
						</tr>
					</table>
					<table width="96%" border="0" cellspacing="0" cellpadding="1">
						<tr align="center">
							<td height="3" align="left">&nbsp;</td>
						</tr>
						<tr align="center">
							<td height="3" align="left"><html-el:button styleId="Review_loanDisbursement.button.edit" property="editButton"
								styleClass="insidebuttn"
								onclick="javascript:fun_edit(this.form)">
								<mifos:mifoslabel name="loan.editTrxn" />
							</html-el:button></td>
						</tr>
						               <tr>
               <td>
                    <c:set var="questionsHostForm" value="${loanDisbursementActionForm}" scope="request" />
                    <c:import url="/pages/application/surveys/jsp/viewQuestionResponses.jsp">
                       <c:param name="editResponseURL" value="loanDisbursementAction.do?method=editQuestionResponses&currentFlowKey=${requestScope.currentFlowKey}"/>
                       <c:param name="responseDivStyleClass" value="viewQuestionResponseDiv"/>
                    </c:import>
               </td>
            </tr>
						<tr align="center">
							<td width="100%" height="3" colspan="2" align="center"
								class="blueline">&nbsp;</td>
						</tr>
						<tr align="center">
							<td height="3" colspan="2" align="center" class="fontnormal">&nbsp;</td>
						</tr>
					</table>
					<table width="96%" border="0" cellspacing="0" cellpadding="1">
						<tr>
							<td align="center"><html-el:submit styleId="Review_loanDisbursement.button.submit" styleClass="buttn">
								<mifos:mifoslabel name="loan.submit" />
							</html-el:submit> &nbsp; <html-el:button styleId="Review_loanDisbursement.button.cancel" property="cancelButton"
								styleClass="cancelbuttn"
								onclick="javascript:fun_return()">
								<mifos:mifoslabel name="loan.cancel" />
							</html-el:button></td>
						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>
			<br>
			<html-el:hidden property="prdOfferingName"
				value="${loanDisbursementActionForm.prdOfferingName}" />
			<html-el:hidden property="globalAccountNum"
				value="${loanDisbursementActionForm.globalAccountNum}" />
			<html-el:hidden property="accountId"
				value="${loanDisbursementActionForm.accountId}" />
			<html-el:hidden property="method" value="" />

		</html-el:form>

	</tiles:put>
</tiles:insert>
