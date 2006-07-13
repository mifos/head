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
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<SCRIPT>
	function ViewDetails(){
		closedaccsearchactionform.submit();
	}
	function ViewLoanDetails(form){
		form.action="loanAction.do?method=get";
		form.submit();
	}
	function goToPrevious(form){
		form.action="applyPaymentAction.do?method=previous";
		form.submit()
	}
</SCRIPT>
		<html-el:form method="post" action="/applyPaymentAction.do?method=applyPayment">

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
							<html-el:link href="javascript:ViewDetails()">
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
			<br>
			<table width="96%" border="0" cellpadding="3" cellspacing="0">
				<tr>
					<td width="100%" colspan="2" class="headingorange"><span
						class="heading"><c:out value="${param.prdOfferingName}" /> # <c:out
						value="${param.globalAccountNum}" /> - </span> <mifos:mifoslabel
						name="accounts.reviewtransaction" /></td>
				</tr>
				<tr>
					<td><font class="fontnormalRedBold"> <html-el:errors
						bundle="accountsUIResources" /> </font></td>
				</tr>

				<tr>
					<td colspan="2" class="fontnormal"><mifos:mifoslabel
						name="accounts.edittrans" /></td>
				</tr>
				<tr>
					<td colspan="2" class="blueline"><img src="images/trans.gif"
						width="10" height="5"></td>
				</tr>
			</table>
			<br>
			<table width="96%" border="0" cellspacing="0" cellpadding="1">
				<tr>
					<td align="right" class="fontnormalbold"><mifos:mifoslabel
						name="accounts.date_of_trxn" /><mifos:mifoslabel
						name="accounts.colon" /></td>
					<td class="fontnormal"><c:out
						value="${applyPaymentActionForm.transactionDate}" /></td>
				</tr>
				<tr>
					<td width="22%" align="right" class="fontnormalbold"><mifos:mifoslabel
						name="accounts.amount" /> <mifos:mifoslabel name="accounts.colon" /></td>
					<td width="78%" class="fontnormal"><c:out
						value="${applyPaymentActionForm.amount}" /></td>
				</tr>
				<tr>
					<td align="right" class="fontnormalbold"><mifos:mifoslabel
						name="accounts.mode_of_payment"  /> <mifos:mifoslabel
						name="accounts.colon" /></td>
					<td class="fontnormal"><c:forEach var="payment"
						items="${sessionScope.PaymentType}">
						<c:if
							test="${payment.id == sessionScope.applyPaymentActionForm.paymentTypeId}">
							<c:out value="${payment.name}" />
						</c:if>
					</c:forEach></td>
				</tr>
				<tr>
					<td align="right" class="fontnormalbold"><mifos:mifoslabel
						name="accounts.receiptid" /> <mifos:mifoslabel
						name="accounts.colon" /></td>
					<td class="fontnormal"><c:out
						value="${applyPaymentActionForm.receiptId}" /></td>
				</tr>
				<tr>
					<td align="right" class="fontnormalbold"><mifos:mifoslabel
						name="accounts.receiptdate" /> <mifos:mifoslabel
						name="accounts.colon" /></td>
					<td class="fontnormal"><c:out
						value="${applyPaymentActionForm.receiptDate}" /></td>
				</tr>
				<tr>
					<td height="3" colspan="2" align="center">&nbsp;</td>
				</tr>
				<tr>
					<td height="3" colspan="2"><html-el:button property="Cancel"
						styleClass="insidebuttn" style="width:115px;"
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
					<td align="center"><html-el:submit styleClass="buttn"
						property="Preview">
						<mifos:mifoslabel name="accounts.submit">
						</mifos:mifoslabel>
					</html-el:submit> &nbsp; <c:choose>
								<c:when test="${param.input == 'loan'}">
									<html-el:button styleClass="cancelbuttn" property="Cancel"
										style="width:65px;" onclick="ViewLoanDetails(this.form)">
										<mifos:mifoslabel name="accounts.cancel"></mifos:mifoslabel>
									</html-el:button>

								</c:when>
								<c:otherwise>
									<html-el:button styleClass="cancelbuttn" property="Cancel"
										style="width:65px;" onclick="ViewDetails()">
										<mifos:mifoslabel name="accounts.cancel"></mifos:mifoslabel>
									</html-el:button>

								</c:otherwise>
							</c:choose></td>
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
			<html-el:hidden property="searchInput" value="${param.searchInput}" />

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
			<html-el:hidden property="searchInput" value="${param.searchInput}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
