<!--
 
 * DisburseLoan.jsp  version: xxx
 
 
 
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
	<SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'repaymentSchedulesIndependentOfMeetingIsEnabled')}" var="repaymentSchedulesIndependentOfMeetingIsEnabled" />
	<body onload="disableFields()">
		<script>
			function fun_return(form){
				form.action="loanAccountAction.do";
				form.method.value="get";
				form.submit();
			}
			
			function disableFields(){
			if (${repaymentSchedulesIndependentOfMeetingIsEnabled}==1)
			{
	 		document.getElementsByName("transactionDateDD")[0].disabled=true;
	 		document.getElementsByName("transactionDateMM")[0].disabled=true;
	 		document.getElementsByName("transactionDateYY")[0].disabled=true; 			 		
	 		}
	 		} 		
				

					
	</script>

        <fmt:setLocale value='${sessionScope["LOCALE"]}'/>
		<fmt:setBundle basename="org.mifos.config.localizedResources.LoanUIResources"/>
		
		<html-el:form
			action="loanDisbursmentAction.do?method=preview&globalAccountNum=${loanDisbursmentActionForm.globalAccountNum}"
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
							<c:out value="${loanDisbursmentActionForm.prdOfferingName}" />&nbsp;#&nbsp;
							<c:out value="${loanDisbursmentActionForm.globalAccountNum}" />
							&nbsp;-&nbsp; </span> 
							<fmt:message key="loan.disburseLoan">
								<fmt:param><mifos:mifoslabel
									name="${ConfigurationConstants.LOAN}" /></fmt:param>
							</fmt:message></td>
						</tr>
						<tr>
							<td class="fontnormal"><mifos:mifoslabel mandatory="Yes"
								name="loan.asterisk" /></td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellspacing="0" cellpadding="3">

						<font class="fontnormalRedBold"> <html-el:errors
							bundle="loanUIResources" /> </font>
						<tr>
							<td colspan="2" align="right" class="fontnormal"><img
								src="pages/framework/images/trans.gif" width="10" height="2"></td>
						</tr>
						<tr>
							<td align="right" class="fontnormal"><mifos:mifoslabel
								name="loan.dateofdisb/payment" mandatory="true" />:&nbsp;</td>
							<td class="fontnormal">

							 <date:datetag property="transactionDate" />
							 
							 </td>
							 							 
						</tr>
						<tr>
							<td align="right" class="fontnormal"><mifos:mifoslabel
								name="loan.receiptId" />:&nbsp;</td>
							<td class="fontnormal"><mifos:mifosalphanumtext
								maxlength="25" property="receiptId" /></td>
						</tr>
						<tr>
							<td align="right" class="fontnormal"><mifos:mifoslabel
								name="loan.receiptdate" />:&nbsp;</td>
							<td class="fontnormal"><date:datetag property="receiptDate" /></td>
						</tr>
						<tr>
							<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
								name="loan.disbdetails" /></td>
						</tr>
						<tr>
							<td width="29%" align="right" class="fontnormal">
								<fmt:message key="loan.loanAmount">
									<fmt:param><mifos:mifoslabel
										name="${ConfigurationConstants.LOAN}" /></fmt:param>
								</fmt:message>:&nbsp;</td>
							<td width="71%"><mifos:mifosdecimalinput
								property="loanAmount" name="loanDisbursmentActionForm"
								disabled="true" /></td>
						</tr>
						<tr>
							<td align="right" class="fontnormal"><mifos:mifoslabel
								name="loan.mode_of_payment" mandatory="yes" />:&nbsp;</td>
							<td><mifos:select property="paymentTypeId"
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
							<td width="29%" align="right" class="fontnormal"><mifos:mifoslabel
								name="loan.amount" />:&nbsp;</td>
							<td width="71%"><mifos:mifosdecimalinput property="amount"
								name="loanDisbursmentActionForm" disabled="true" /></td>
						</tr>
						<tr>
							<td align="right" class="fontnormal"><mifos:mifoslabel
								name="loan.mode_of_payment" mandatory="yes" />:&nbsp;</td>
							<td><c:choose>
								<c:when
									test="${loanDisbursmentActionForm.amount.amountDoubleValue == 0.0}">
									<mifos:select property="paymentModeOfPayment"
										style="width:136px;" disabled="true">
										<c:forEach var="PT"
											items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'PaymentType')}">
											<html-el:option value="${PT.id}">${PT.name}</html-el:option>
										</c:forEach>
									</mifos:select>
								</c:when>
								<c:otherwise>
									<mifos:select property="paymentModeOfPayment"
										style="width:136px;">
										<c:forEach var="PT"
											items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'PaymentType')}">
											<html-el:option value="${PT.id}">${PT.name}</html-el:option>
										</c:forEach>
									</mifos:select>
								</c:otherwise>
							</c:choose></td>
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
							<td align="center"><html-el:submit styleClass="buttn">
								<mifos:mifoslabel name="loan.reviewtransaction" />
							</html-el:submit> &nbsp; <html-el:button property="cancelButton"
								styleClass="cancelbuttn"
								onclick="javascript:fun_return(this.form)">
								<mifos:mifoslabel name="loan.cancel" />
							</html-el:button></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			<br>
			<html-el:hidden property="prdOfferingName"
				value="${loanDisbursmentActionForm.prdOfferingName}" />
			<html-el:hidden property="globalAccountNum"
				value="${loanDisbursmentActionForm.globalAccountNum}" />
			<html-el:hidden property="accountId"
				value="${loanDisbursmentActionForm.accountId}" />
			<html-el:hidden property="method" value="" />
		</html-el:form>

	</tiles:put>
</tiles:insert>

