<%--
Copyright (c) 2005-2009 Grameen Foundation USA
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
<%@ taglib uri="/tags/date" prefix="date" %>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<span id="page.id" title="ReviewRepayLoan" />
		<script>
			function fun_return(form){
					form.action="loanAccountAction.do?method=get";
					form.submit();
			}
			function fun_edit(form){
					form.action="repayLoanAction.do?method=previous";
					form.submit();
			}
	</script>
		<html-el:form  action="repayLoanAction.do?method=makeRepayment">
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
								<font class="fontnormalRedBold">
									<span id="Review_RepayLoan.error.message"><html-el:errors bundle="loanUIResources" /> 
								</font>
					<tr>
						<td width="70%" height="24" align="left" valign="top"
							class="paddingL15T15">
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="100%" colspan="2" class="headingorange">
									<span class="heading"> 
										<c:out value="${param.prdOfferingName}" />&nbsp;#&nbsp;
										<c:out value="${param.globalAccountNum}" />
											&nbsp;-&nbsp; 
									</span> 	
								
									<mifos:mifoslabel name="loan.reviewtransaction"  /></td>
							</tr>
							<tr>
								<td colspan="2" class="fontnormal"><mifos:mifoslabel
									name="loan.edittrans"  /></td>
							</tr>
							<tr>
								<td colspan="2" class="blueline"><img src="pages/framework/images/trans.gif"
									width="10" height="5"></td>
							</tr>
						</table>
						<br>
						<table width="95%" border="0" cellspacing="0" cellpadding="3">
							<tr>
                				<td colspan="2" align="right" class="fontnormal">
                					<img src="pages/framework/images/trans.gif" width="10" height="2">
                				</td>
              				</tr>
              				<tr>
								<td width="28%" align="right" class="fontnormalbold"><mifos:mifoslabel
									name="loan.dateofpayment"  />:&nbsp;</td>
								<td width="72%" class="fontnormal">
									<c:out value="${sessionScope.repayLoanActionForm.dateOfPayment}" />
								</td>
							</tr>
							<tr>
								<td align="right" class="fontnormalbold">
									<mifos:mifoslabel name="loan.amount"  />:&nbsp;
								</td>
								<td class="fontnormal">
								<c:out value="${sessionScope.repayLoanActionForm.amount}" />
								</td>
							</tr>
							<tr>
								<td align="right" class="fontnormalbold"><mifos:mifoslabel
									name="loan.mode_of_payment"  />:&nbsp;</td>
								<td class="fontnormal">
									<c:forEach var="payment" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'PaymentType')}" >
                 						<c:if test="${payment.id == sessionScope.repayLoanActionForm.paymentTypeId}">
                 							<c:out value="${payment.name}"/>
                 						</c:if>
                 					</c:forEach>
								</td>
							</tr>
							<tr id="Loan.ReceiptId">
								<td align="right" class="fontnormalbold">
									<mifos:mifoslabel name="loan.receiptId"  keyhm="Loan.ReceiptId" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/>&nbsp;
								</td>
								<td class="fontnormal">
									<c:out value="${sessionScope.repayLoanActionForm.receiptNumber}" />
								</td>
							</tr>
							<tr id="Loan.ReceiptDate">
				                <td align="right" class="fontnormalbold">
									<mifos:mifoslabel name="loan.receiptdate" keyhm="Loan.ReceiptDate" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/>&nbsp;
								</td>
								<td class="fontnormal">
								<c:out value="${sessionScope.repayLoanActionForm.receiptDate}" />
								
								</td>
				            </tr>
				          </table>
				          <table width="96%" border="0" cellspacing="0" cellpadding="1">
							<tr align="center">
				                <td height="3" colspan="2" align="center">&nbsp;</td>
				            </tr>
				            <tr align="center">
				                <td height="3" colspan="2" align="left">
				                	<html-el:button styleId="Review_RepayLoan.button.edit" property="editButton" styleClass="insidebuttn" 
										onclick="javascript:fun_edit(this.form)">
										<mifos:mifoslabel name="loan.editTrxn" />
									</html-el:button>
				                </td>
				            </tr>
				            <tr align="center">
				                <td width="100%" height="3" colspan="2" align="center" class="blueline">&nbsp;</td>
				            </tr>
				            <tr align="center">
				                <td height="3" colspan="2" align="center" class="fontnormal">&nbsp;</td>
				            </tr>
							</table>
	            			<table width="96%" border="0" cellspacing="0" cellpadding="1">
								<tr>
									<td align="center"><html-el:submit styleId="Review_RepayLoan.button.submit" styleClass="buttn">
										<mifos:mifoslabel name="loan.submit" />
									</html-el:submit> &nbsp; <html-el:button styleId="Review_RepayLoan.button.cancel" property="cancelButton" styleClass="cancelbuttn" 
										onclick="javascript:fun_return(this.form)">
										<mifos:mifoslabel name="loan.cancel" />
									</html-el:button></td>
								</tr>
							</table><br>
						</td>
					</tr>
				</table>
				<br>
	<html-el:hidden property="prdOfferingName" value="${param.prdOfferingName}"/> 
	<html-el:hidden property="globalAccountNum" value="${param.globalAccountNum}"/>
	<html-el:hidden property="accountId" value="${param.accountId}"/>
</html-el:form>
</tiles:put>
</tiles:insert>
