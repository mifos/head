<!--
 
 * Review_loanDisbursment.jsp  version: xxx
 
 
 
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
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">

		<script>
			function fun_return(form)
					{
						form.action="loanAccountAction.do";
						form.method.value="get";
						form.submit();
					}
					
			function fun_edit(form)
					{
						
						form.action="loanDisbursmentAction.do?method=previous";
						form.submit();
					}
	</script>
		<html-el:form action="loanDisbursmentAction.do?method=update">

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
								value="${loanDisbursmentActionForm.prdOfferingName}" />&nbsp;#&nbsp;
							<c:out value="${loanDisbursmentActionForm.globalAccountNum}" />
							&nbsp;-&nbsp; </span><mifos:mifoslabel
								name="loan.reviewtransaction" /></td>
						</tr>
						<tr>
							<td colspan="2" class="fontnormal"><mifos:mifoslabel
								name="loan.edittrans" /></td>
						</tr>
						<tr>
						  <td>
						   <font class="fontnormalRedBold"> <html-el:errors
							bundle="loanUIResources" /> </font>
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
								value="${loanDisbursmentActionForm.transactionDate}" /></td>
						</tr>
						<tr>
							<td align="right" class="fontnormalbold"><mifos:mifoslabel
								name="loan.receiptId" />:&nbsp;</td>
							<td class="fontnormal"><c:out
								value="${loanDisbursmentActionForm.receiptId}" /></td>
						</tr>
						<tr>
							<td align="right" class="fontnormalbold"><mifos:mifoslabel
								name="loan.receiptdate" />:&nbsp;</td>
							<td class="fontnormal"><c:out
								value="${loanDisbursmentActionForm.receiptDate}" /></td>
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
							<td width="72%" class="fontnormal"><c:out
								value="${loanDisbursmentActionForm.loanAmount}" /></td>
						</tr>
						<tr>
							<td align="right" class="fontnormalbold"><mifos:mifoslabel
								name="loan.mode_of_payment" />:&nbsp;</td>
							<td class="fontnormal"><c:forEach var="payment"
								items="${sessionScope.PaymentType}">
								<c:if
									test="${payment.id == sessionScope.loanDisbursmentActionForm.paymentTypeId}">
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
							<td width="72%" class="fontnormal"><c:out
								value="${loanDisbursmentActionForm.amount}" /></td>
						</tr>
						<tr>
							<td align="right" class="fontnormalbold"><mifos:mifoslabel
								name="loan.mode_of_payment" />:&nbsp;</td>
							<td class="fontnormal">
							
							<c:forEach var="payment"
								items="${sessionScope.PaymentType}">
								<c:if
									test="${payment.id == sessionScope.loanDisbursmentActionForm.paymentModeOfPayment}">
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
							<td height="3" align="left"><html-el:button property="editButton"
								styleClass="insidebuttn"
								onclick="javascript:fun_edit(this.form)" style="width:115px;">
								<mifos:mifoslabel name="loan.editTrxn" />
							</html-el:button></td>
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
							<td align="center"><html-el:submit styleClass="buttn"
								style="width:65px;">
								<mifos:mifoslabel name="loan.submit" />
							</html-el:submit> &nbsp; <html-el:button property="cancelButton"
								styleClass="cancelbuttn"
								onclick="javascript:fun_return(this.form)" style="width:65px;">
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
				value="${loanDisbursmentActionForm.prdOfferingName}" />
			<html-el:hidden property="globalAccountNum"
				value="${loanDisbursmentActionForm.globalAccountNum}" />
			<html-el:hidden property="accountId"
				value="${loanDisbursmentActionForm.accountId}" />
			<html-el:hidden property="method" value="" />

		</html-el:form>

	</tiles:put>
</tiles:insert>
