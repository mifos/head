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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<script type="text/javascript" src="pages/js/jquery/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="pages/js/singleitem.js"></script>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<span id="page.id" title="RepayLoan"></span>
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<script>
			function fun_return()
					{
						goBackToLoanAccountDetails.submit();
					}
		</script>
		<SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>
		<SCRIPT SRC="pages/application/loan/js/repayLoan.js"></SCRIPT>

		<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
		<fmt:setBundle basename="org.mifos.config.localizedResources.LoanUIResources"/>
		<form name="goBackToLoanAccountDetails" method="get" action ="viewLoanAccountDetails.ftl">
			<input type="hidden" name='globalAccountNum' value="${param.globalAccountNum}"/>
		</form>
		<html-el:form action="repayLoanAction.do?method=preview&globalAccountNum=${param.globalAccountNum}"
		    onsubmit="return validateMyForm(receiptDate,receiptDateFormat,receiptDateYY) && validateMyForm(dateOfPayment,dateOfPaymentFormat,dateOfPaymentYY)">
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />	
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"> <customtags:headerLink/> </span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" height="24" align="left" valign="top" class="paddingL15T15">
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="70%" class="headingorange">
									<span class="heading"> <c:out value="${param.prdOfferingName}" />&nbsp;#&nbsp; <c:out value="${param.globalAccountNum}" /> &nbsp;-&nbsp; </span>
										<mifos:mifoslabel name="loan.repayloan" />
								</td>
							</tr>
							<tr>
								<td class="fontnormal">
									<span class="mandatorytext"><font color="#FF0000">*</font></span>
									<mifos:mifoslabel name="loan.Fieldsmarkedwithanasteriskarerequired" />
								</td>
							</tr>
							<tr>
								<td>
									<font class="fontnormalRedBold"><span id="RepayLoan.error.message"><html-el:errors bundle="SavingsUIResources" /></span></font>
								</td>
							</tr>
						</table>
						<br>
						<div id="waiverInterestWarning" class="fontnormalRed">
                            <mifos:mifoslabel name="loan.waiverInterestWarning"/>
                            <br>
						</div>
						<table width="95%" border="0" cellspacing="0" cellpadding="3">
						    <tr>
                                <c:if test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'waiverInterest')==true}">
                                    <td align="right" class="fontnormal"><mifos:mifoslabel name="loan.waiverInterestMessage"/> :</td>
                                    <td align="left" class="fontnormal">
                                    <c:choose>
                                        <c:when test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'waiverInterestSelected')==true}">
                                            <input type="checkbox" value="true" name="waiverInterestChckBox" checked="checked">
                                        </c:when>
                                        <c:otherwise>
                                            <input type="checkbox" value="true" name="waiverInterestChckBox">
                                        </c:otherwise>
                                    </c:choose>
                                    </td>
                                 </c:if>
						    </tr>
							<tr>
								<td colspan="2" align="right" class="fontnormal">
									<img src="pages/framework/images/trans.gif" width="10" height="2">
								</td>
							</tr>
							<tr>
                                <td width="29%" align="right" class="fontnormal">
                                    <mifos:mifoslabel mandatory="yes" isColonRequired="Yes" name="accounts.date_of_trxn" />
                                </td>
                                <td width="71%" class="fontnormal">
                                    <date:datetag property="dateOfPayment" />
                                </td>
							</tr>
							<tr>
								<td align="right" class="fontnormal">
									<mifos:mifoslabel name="loan.amount" />
									:
								</td>
								<td class="fontnormal">
                                    <div id="totalRepaymentAmount">
                                        <fmt:formatNumber value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'totalRepaymentAmount').amount}"/>
                                    </div>
                                    <div id="waivedRepaymentAmount">
                                        <fmt:formatNumber value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'waivedRepaymentAmount').amount}"/>
                                    </div>
								</td>
							</tr>
							<tr>
								<td align="right" class="fontnormal">
									<span class="mandatorytext"><font color="#FF0000">*</font></span>
									<mifos:mifoslabel name="loan.mode_of_payment" />
									:
								</td>
								<td class="fontnormal">
									<mifos:select styleId="RepayLoan.input.modeOfRepayment" name="repayLoanActionForm" property="paymentTypeId" style="width:136px;">
										<c:forEach var="PT" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'PaymentType')}" >
									<html-el:option value="${PT.id}">${PT.name}</html-el:option>
								</c:forEach>
									</mifos:select>
								</td>
							</tr>
                            <tr id="repayLoan.row.savingsForTransfer">
                                <td align="right" class="fontnormal"><mifos:mifoslabel
                                    name="accounts.account_for_transfer" mandatory="yes" isColonRequired="Yes" /></td>
    
                                <td class="fontnormal"><mifos:select
                                    name="repayLoanActionForm" styleId="repayLoan.input.accountForTransfer" property="accountForTransfer">
                                    <c:forEach var="acc" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'accountsForTransfer')}" >
                                         <html-el:option value="${acc.globalAccountNum}">
                                            ${acc.globalAccountNum}; ${acc.prdOfferingName}; ${acc.savingsType}; 
                                            <mifos:mifoslabel name="accounts.balance"/>: 
                                            <fmt:formatNumber value="${acc.savingsBalance}" />;
                                            <mifos:mifoslabel name="Savings.maxAmountPerWithdrawl"/>: 
                                            <fmt:formatNumber value="${acc.maxWithdrawalAmount}" />
                                         </html-el:option>
                                    </c:forEach>
                                </mifos:select></td>
                            </tr>
							<tr>
								<td align="right" class="fontnormal">
									<span id="RepayLoan.label.receiptId">
									<mifos:mifoslabel keyhm="Loan.ReceiptId" isColonRequired="Yes" name="loan.receiptId" />
									</span>
								</td>
								<td class="fontnormal">
									<mifos:mifosalphanumtext styleId="RepayLoan.input.receiptId" keyhm="Loan.ReceiptId" property="receiptNumber" />
								</td>
							</tr>
							<tr>
								<td align="right" class="fontnormal">
									<mifos:mifoslabel keyhm="Loan.ReceiptDate" isColonRequired="Yes" name="loan.receiptdate" />
								</td>
								<td class="fontnormal">
									<date:datetag keyhm="Loan.ReceiptDate" property="receiptDate" />
								</td>
							</tr>
						</table>
						<table width="96%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center" class="blueline">
									&nbsp;
								</td>
							</tr>
							<tr>
								<td align="center">
									&nbsp;
								</td>
							</tr>
							<tr>
							<td align="left">
								<c:choose>
								<c:when test="${repayLoanActionForm.truePrintReceipt}">
									<input id="accounts.printReceipt" type="checkbox" name="printReceipt" checked="yes" value="true">
								</c:when>
								<c:otherwise>
									<input id="accounts.printReceipt" type="checkbox" name="printReceipt" value="true">
								</c:otherwise>
								</c:choose>
								
								<span id="payment.label.printReceipt">
								<mifos:mifoslabel name="accounts.check_to_print_payment_receipt"/></span>
							</td>
						</tr>
							<tr>
								<td align="center">
									<html-el:submit styleId="RepayLoan.button.reviewTransaction" styleClass="buttn" >
										<mifos:mifoslabel name="loan.reviewtransaction" />
									</html-el:submit>
									&nbsp;
									<html-el:button styleId="RepayLoan.button.cancel" property="cancelButton" styleClass="cancelbuttn"  onclick="javascript:fun_return()">
										<mifos:mifoslabel name="loan.cancel" />
									</html-el:button>
								</td>
							</tr>
						</table>
						<br>
					</td>
				</tr>
			</table>
			<br>
			<html-el:hidden property="method" value="${requestScope.method}" />
			<html-el:hidden property="globalAccountNum" value="${param.globalAccountNum}" />
			<html-el:hidden property="prdOfferingName" value="${param.prdOfferingName}" />
			<html-el:hidden property="amount" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'totalRepaymentAmount')}" />
			<html-el:hidden property="repaymentAmount" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'totalRepaymentAmount')}" />
			<html-el:hidden property="waivedAmount" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'waivedRepaymentAmount')}" />
			<html-el:hidden property="waiverInterest" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'waiverInterest')}" />
		    <html-el:hidden property="transferPaymentTypeId" />
        </html-el:form>
	</tiles:put>
</tiles:insert>
