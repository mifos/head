<!-- 

/**

 * RepayLoan.jsp    version: 1.0

 

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
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<input type="hidden" id="page.id" value="RepayLoan"/>
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<script>
			function fun_return(form)
					{
						form.action="loanAccountAction.do?method=get";
						form.submit();
					}
		</script>
		<SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>
		
		<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
		<fmt:setBundle basename="org.mifos.config.localizedResources.LoanUIResources"/>
		
		<html-el:form action="repayLoanAction.do?method=preview&globalAccountNum=${param.globalAccountNum}" onsubmit="return validateMyForm(recieptDate,recieptDateFormat,recieptDateYY)">
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
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
									<fmt:message key="loan.repayLoan">
										<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /></fmt:param>
									</fmt:message>
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
						<table width="95%" border="0" cellspacing="0" cellpadding="3">
							<tr>
								<td colspan="2" align="right" class="fontnormal">
									<img src="images/trans.gif" width="10" height="2">
								</td>
							</tr>
							<tr>
								<td width="29%" align="right" class="fontnormal">
									<mifos:mifoslabel name="loan.dateofpayment" />
									:
								</td>
								<td width="71%" class="fontnormal">
									<c:out value="${loanfn:getCurrrentDate(sessionScope.UserContext.preferredLocale)}" />
								</td>
							</tr>
							<tr>
								<td align="right" class="fontnormal">
									<mifos:mifoslabel name="loan.amount" />
									:
								</td>
								<td class="fontnormal">
									<c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'totalRepaymentAmount')}" />
								</td>
							</tr>
							<tr>
								<td align="right" class="fontnormal">
									<span class="mandatorytext"><font color="#FF0000">*</font></span>
									<mifos:mifoslabel name="loan.mode_of_payment" />
									:
								</td>
								<td class="fontnormal">
									<mifos:select name="repayLoanActionForm" property="paymentTypeId" style="width:136px;">
										<c:forEach var="PT" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'PaymentType')}" >
									<html-el:option value="${PT.id}">${PT.name}</html-el:option>
								</c:forEach>
									</mifos:select>
								</td>
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
									<date:datetag keyhm="Loan.ReceiptDate" property="recieptDate" />
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
								<td align="center">
									<html-el:submit styleId="RepayLoan.button.reviewTransaction" styleClass="buttn" >
										<mifos:mifoslabel name="loan.reviewtransaction" />
									</html-el:submit>
									&nbsp;
									<html-el:button styleId="RepayLoan.button.cancel" property="cancelButton" styleClass="cancelbuttn"  onclick="javascript:fun_return(this.form)">
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
			<html-el:hidden property="dateOfPayment" value="${loanfn:getCurrrentDate(sessionScope.UserContext.preferredLocale)}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
