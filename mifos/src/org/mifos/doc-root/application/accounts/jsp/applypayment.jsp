<!--
 
 * applypayment.jsp  version: 1.0
 
 
 
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<SCRIPT>
	function ViewDetails(){
		closedaccsearchactionform.submit();
	}
	function ViewLoanDetails(form){
		form.submit();
	}

</SCRIPT>
		<SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>
		<html-el:form method="post"
			action="/applyPaymentAction.do?method=preview"
			onsubmit="return (validateMyForm(transactionDate,transactionDateFormat,transactionDateYY) && validateMyForm(receiptDate,receiptDateFormat,receiptDateYY))"
			focus="personnelDetails.firstName">

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <customtags:headerLink />

					<c:choose>
						<c:when test="${param.input == 'LoanDetails'}">
							<html-el:link
								action="loanAction.do?globalAccountNum=${param.globalAccountNum}&method=get"> /
	          	  			  <c:out value="${param.prdOfferingName}"></c:out>
							</html-el:link>
						</c:when>
						<c:otherwise>
							<html-el:link href="javascript:ViewDetails()"> /
	          					<c:if test="${param.input == 'ViewCenterCharges'}">
									<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
								</c:if>
								<c:if test="${param.input == 'ViewGroupCharges'}">
									<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />
								</c:if>
								<c:if test="${param.input == 'ViewClientCharges'}">
									<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />
								</c:if>
								<mifos:mifoslabel name="Center.Charges"
									bundle="CenterUIResources" />
							</html-el:link>
						</c:otherwise>
					</c:choose> </span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" height="24" align="left" valign="top"
						class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="70%" class="headingorange"><span class="heading"> <c:out
								value="${param.prdOfferingName}" /> - </span><mifos:mifoslabel
								name="accounts.apply_payment" /></td>
						</tr>
						<tr>
							<td><font class="fontnormalRedBold"> <html-el:errors
								bundle="PersonnelUIResources" /> </font></td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellspacing="0" cellpadding="3">
						<tr>
							<td colspan="2" class="fontnormal"><mifos:mifoslabel
								name="accounts.asterisk" /><br>
							<br>
							</td>
						</tr>
						<tr>
							<td align="right" class="fontnormal"><mifos:mifoslabel
								mandatory="yes" name="accounts.date_of_trxn" /></td>
							<td class="fontnormal"><date:datetag property="transactionDate" /></td>
						</tr>
						<tr>
							<td width="24%" align="right" class="fontnormal"><mifos:mifoslabel
								mandatory="yes" name="accounts.amount" /> <mifos:mifoslabel
								name="accounts.colon" /></td>
							<td width="76%"><mifos:mifosdecimalinput property="amount"
								name="applyPaymentActionForm" /></td>
						</tr>
						<tr>
							<td align="right" class="fontnormal"><mifos:mifoslabel
								name="accounts.mode_of_payment" mandatory="yes" /> <mifos:mifoslabel
								name="accounts.colon" /></td>

							<td class="fontnormal"><mifos:select name="applyPaymentActionForm"
								property="paymentTypeId">
								<html-el:options collection="PaymentType" property="id"
									labelProperty="name"></html-el:options>
							</mifos:select></td>
						</tr>
						<tr>
							<td align="right" class="fontnormal"><mifos:mifoslabel
								name="accounts.receiptid" /> <mifos:mifoslabel
								name="accounts.colon" /></td>
							<td class="fontnormal"><mifos:mifosalphanumtext
								property="receiptId" name="applyPaymentActionForm" /></td>
						</tr>
						<tr>
							<td align="right" class="fontnormal"><mifos:mifoslabel
								name="accounts.receiptdate" /> <mifos:mifoslabel
								name="accounts.colon" /></td>
							<td class="fontnormal"><date:datetag property="receiptDate" /></td>
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
							<td align="center"><html-el:submit styleClass="buttn"
								property="Preview">
								<mifos:mifoslabel name="accounts.reviewtransaction">
								</mifos:mifoslabel>
							</html-el:submit> &nbsp; <html-el:button styleClass="buttn"
								property="Cancel" style="width:65px;" onclick="ViewDetails()">
								<mifos:mifoslabel name="accounts.cancel"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			<html-el:hidden property="statusId" value="${param.statusId}" />
			<html-el:hidden property="searchNode(search_name)"
				value="${param.searchInput}" />
			<html-el:hidden property="prdOfferingName"
				value="${param.prdOfferingName}" />
			<html-el:hidden property="globalAccountNum"
				value="${param.globalAccountNum}" />
			<html-el:hidden property="accountId" value="${param.accountId}" />
			<html-el:hidden property="accountType" value="${param.accountType}" />
			<html-el:hidden property="input" value="${param.input}" />
			<html-el:hidden property="statusId" value="${param.statusId}" />

		</html-el:form>
		<html-el:form action="closedaccsearchaction.do?method=search">
			<html-el:hidden property="searchNode(search_name)"
				value="${param.searchInput}" />
			<html-el:hidden property="prdOfferingName"
				value="${param.prdOfferingName}" />
			<html-el:hidden property="globalAccountNum"
				value="${param.globalAccountNum}" />
			<html-el:hidden property="accountId" value="${param.accountId}" />
			<html-el:hidden property="accountType" value="${param.accountType}" />
			<html-el:hidden property="input" value="${param.input}" />
			<html-el:hidden property="statusId" value="${param.statusId}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
