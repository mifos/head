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
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
    <mifos:NumberFormattingInfo />
    <span id="transfer.id" title="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'transferPaymentTypeId')}"></span>
	<span id="page.id" title="DisburseLoan"></span>

	<SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>
    <script type="text/javascript" src="pages/js/applyPayment.js"></script>
		
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'repaymentSchedulesIndependentOfMeetingIsEnabled')}" var="repaymentSchedulesIndependentOfMeetingIsEnabled" />
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BackDatedTransactionsAllowed')}" var="allowBackDatedTransactions" />
	<body onload="disableFields()">
		<script>
			function fun_return(){
				goBackToLoanAccountDetails.submit();
			}
			
			function disableFields(){
        			if (${allowBackDatedTransactions} == false) {
                	 		document.getElementsByName("transactionDateDD")[0].disabled=true;
                	 		document.getElementsByName("transactionDateMM")[0].disabled=true;
                	 		document.getElementsByName("transactionDateYY")[0].disabled=true;
        	 		}
	 		}



	</script>

        <fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
        
		<fmt:setBundle basename="org.mifos.config.localizedResources.LoanUIResources"/>
		<c:set value="viewLoanAccountDetails.ftl" var="formAction" />
		<form name="goBackToLoanAccountDetails" method="get" action ="${formAction }">
			<input type="hidden" name='globalAccountNum' value="${loanDisbursementActionForm.globalAccountNum}"/>
		</form>
		<html-el:form
			action="loanDisbursementAction.do?method=preview&globalAccountNum=${loanDisbursementActionForm.globalAccountNum}"
			onsubmit="return (validateMyForm(transactionDate,transactionDateFormat,transactionDateYY) && validateMyForm(receiptDate,receiptDateFormat,receiptDateYY))">
			<html-el:hidden property="currentFlowKey"
				value="${requestScope.currentFlowKey}" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt">
					<customtags:headerLink /> </span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" height="24" align="left" valign="top"
						class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="70%" class="headingorange"><span class="heading">
							<c:out value="${loanDisbursementActionForm.prdOfferingName}" />&nbsp;#&nbsp;
							<c:out value="${loanDisbursementActionForm.globalAccountNum}" />
							&nbsp;-&nbsp; </span> 
							<mifos:mifoslabel name="loan.disburseloan" /></td>
						</tr>
						<tr>
							<td class="fontnormal"><mifos:mifoslabel mandatory="Yes"
								name="loan.asterisk" /></td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellspacing="0" cellpadding="3">

						<font class="fontnormalRedBold"> <span id="DisburseLoan.error.message"><html-el:errors
							bundle="loanUIResources" /></span> </font>
						<tr>
							<td colspan="2" align="right" class="fontnormal"><img
								src="pages/framework/images/trans.gif" width="10" height="2"></td>
						</tr>
						<tr>
							<td align="right" class="fontnormal"><mifos:mifoslabel
								name="loan.dateofdisb/payment" mandatory="true" isColonRequired="Yes" /></td>
							<td class="fontnormal">

							 <date:datetag property="transactionDate" />
							 
							 </td>
							 							 
						</tr>
						<tr>
							<td align="right" class="fontnormal"><span id="DisburseLoan.label.receiptId"><mifos:mifoslabel
								name="loan.receiptId" keyhm="Loan.ReceiptId" isColonRequired="Yes"/></span></td>
							<td class="fontnormal"><mifos:mifosalphanumtext
                                                                keyhm="Loan.ReceiptId"
								styleId="DisburseLoan.input.receiptId"
								maxlength="25" property="receiptId" /></td>
						</tr>
						<tr>
							<td align="right" class="fontnormal"><mifos:mifoslabel
								name="loan.receiptdate" isColonRequired="true" keyhm="Loan.ReceiptDate" /></td>
							<td class="fontnormal"><date:datetag property="receiptDate" keyhm="Loan.ReceiptDate"/></td>
						</tr>
						<tr>
							<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
								name="loan.disbdetails" /></td>
						</tr>
						<tr>
							<td width="29%" align="right" class="fontnormal">
								<span id="DisburseLoan.label.disbursementAmount">
									<mifos:mifoslabel name="loan.loanamount" />
								</span>:&nbsp;</td>
							<td width="71%"><html-el:text styleId="DisburseLoan.input.disbursementAmount"
								property="loanAmount" name="loanDisbursementActionForm"
								disabled="true" /></td>
						</tr>
						<tr>
							<td align="right" class="fontnormal"><mifos:mifoslabel
								name="loan.mode_of_payment" mandatory="yes" />:&nbsp;</td>
							<td><mifos:select styleId="DisburseLoan.input.paymentType" property="paymentTypeId"
								style="width:136px;">
								<c:forEach var="PT"
									items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'PaymentType')}">
									<html-el:option value="${PT.id}">${PT.name}</html-el:option>
								</c:forEach>
							</mifos:select></td>
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
							<td width="29%" align="right" class="fontnormal"><span id="DisburseLoan.label.paymentAmount"><mifos:mifoslabel
								name="loan.amount" /></span>:&nbsp;</td>
							<td width="71%"><html-el:text styleId="DisburseLoan.input.paymentAmount" property="amount"
								name="loanDisbursementActionForm" disabled="true" /></td>
						</tr>
						<tr>
							<td align="right" class="fontnormal"><mifos:mifoslabel
								name="loan.mode_of_payment" mandatory="yes" />:&nbsp;</td>
							<td>
							    <c:set var="disablePaymentModeOfPayment" value="${!loanDisbursementActionForm.paymentAmountGreaterThanZero}"/>
							    <mifos:select property="paymentModeOfPayment"
                                        styleId="applypayment.input.paymentType"
										style="width:136px;" disabled="${disablePaymentModeOfPayment}">
										<c:forEach var="PT"
											items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'FeePaymentType')}">
											<html-el:option value="${PT.id}">${PT.name}</html-el:option>
										</c:forEach>
									</mifos:select>
								</td>
						</tr>
                        <tr id="applypayment.row.savingsForTransfer">
                            <td align="right" class="fontnormal"><mifos:mifoslabel
                                name="accounts.account_for_transfer" mandatory="yes" isColonRequired="Yes" /></td>

                            <td class="fontnormal"><mifos:select
                                styleId="applypayment.input.accountForTransfer" property="accountForTransfer">
                                <c:forEach var="acc" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'accountsForTransfer')}" >
                                    <html-el:option value="${acc.globalAccountNum}">
                                        ${acc.globalAccountNum} - ${acc.prdOfferingName}
                                    </html-el:option>
                                </c:forEach>
                            </mifos:select></td>
                        </tr>
                        <c:forEach var="acc" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'accountsForTransfer')}" >
                            <tr class="${acc.globalAccountNum}" style="display:none">
                                <td align="right" class="fontnormal"><mifos:mifoslabel
                                name="accounts.type" isColonRequired="Yes" /></td>
                                <td class="fontnormal">${acc.savingsType}</td>
                            </tr>
                            <tr class="${acc.globalAccountNum}" style="display:none">
                                <td align="right" class="fontnormal"><mifos:mifoslabel
                                name="accounts.balance" isColonRequired="Yes" /></td>
                                <td class="fontnormal"><fmt:formatNumber value="${acc.savingsBalance}" /></td>
                            </tr>
                            <tr class="${acc.globalAccountNum}" style="display:none">
                                <td align="right" class="fontnormal"><mifos:mifoslabel
                                name="Savings.maxAmountPerWithdrawl" isColonRequired="Yes" /></td>
                                <td class="fontnormal"><fmt:formatNumber value="${acc.maxWithdrawalAmount}" /></td>
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
							<td align="center"><html-el:submit styleId="DisburseLoan.button.reviewTransaction" styleClass="buttn">
								<mifos:mifoslabel name="loan.reviewtransaction" />
							</html-el:submit> &nbsp; <html-el:button styleId="DisburseLoan.button.cancel" property="cancelButton"
								styleClass="cancelbuttn"
								onclick="javascript:fun_return()">
								<mifos:mifoslabel name="loan.cancel" />
							</html-el:button></td>
						</tr>
					</table>
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
            <html-el:hidden property="transferPaymentTypeId"
                value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'transferPaymentTypeId')}" />
			<html-el:hidden property="method" value="" />
		</html-el:form>

	</tiles:put>
</tiles:insert>

