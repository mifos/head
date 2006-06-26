<!--
 
 * applyPayment_loanAccount.jsp  version: xxx
 
 
 
 * Copyright © 2005-2006 Grameen Foundation USA
 
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
<%@ taglib uri="/tags/date" prefix="date" %>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	
	<script>
			function fun_return(form)
					{
						<%--form.action="loanAction.do?method=get&globalAccountNum=${param.accountNum}";--%>
						form.action="accountTrxn.do?method=cancel";
						form.submit();
					}
			function ViewDetails(){
				closedaccsearchactionform.submit();
			}
	</script>
	<script src="pages/framework/js/date.js"></script>
		<html-el:form  action="accountTrxn.do?method=preview&globalAccountNum=${param.globalAccountNum}" onsubmit="return (validateMyForm(paymentDate,paymentDateFormat,paymentDateYY) && validateMyForm(receiptDate,receiptDateFormat,receiptDateYY))">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt">
							<mifoscustom:getLoanHeader loanHeader='${sessionScope.header_load}'/>
							<c:choose>
		
								<c:when test="${param.input=='ViewClientCharges'}">
									<html-el:link href="javascript:ViewDetails()">
									    <mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />
										<mifos:mifoslabel name="loan.charges" />
									</html-el:link> 
								</c:when>
								<c:when test="${param.input=='ViewGroupCharges'}">
									<html-el:link href="javascript:ViewDetails()">
									    <mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />
										<mifos:mifoslabel name="loan.charges" />
									</html-el:link> 
								</c:when>
								<c:when test="${param.input=='ViewCenterCharges'}">
									<html-el:link href="javascript:ViewDetails()">
									    <mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
										<mifos:mifoslabel name="loan.charges" />
									</html-el:link> 
								</c:when>
								<c:otherwise>
									<html-el:link href="loanAction.do?method=get&globalAccountNum=${param.globalAccountNum}">
										<c:out value="${param.prdOfferingName}" />
									</html-el:link> 
								</c:otherwise>
							</c:choose> 
						</span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" height="24" align="left" valign="top" class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="70%" class="headingorange">
								<span class="heading"> 
									
									<c:choose>
									<c:when test="${param.input=='reviewTransactionPage'}">
										<c:out value="${param.prdOfferingName}" />&nbsp;#&nbsp;
										<c:out value="${param.globalAccountNum}" />
									</c:when>
									<c:otherwise>
										<c:out value="${param.prdOfferingName}" />
									</c:otherwise>
								</c:choose>
									&nbsp;-&nbsp;
								</span> 
								<mifos:mifoslabel name="loan.apply_payment" />
							</td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellspacing="0" cellpadding="3">
						<tr>
			                <td colspan="2" class="fontnormal">
			                	<span class="mandatorytext"><font color="#FF0000">*</font></span> 
			                		<mifos:mifoslabel name="accounts.asterisk"  /><br><br>
			                </td>
              			</tr>
						<tr><logic:messagesPresent>
							<td colspan="2"><font class="fontnormalRedBold"><html-el:errors bundle="accountsUIResources" /></font><br><br></td>
							</logic:messagesPresent>
						</tr>
						<tr>
							<td align="right" class="fontnormal">
								<mifos:mifoslabel name="loan.dateoftrxn"  mandatory="yes"/>:&nbsp;
							</td>
							<!-- here we are using payment date as the date of transaction.As of now it is current date. -->
                			<td class="fontnormal"><date:datetag property="paymentDate" /></td>
						</tr>
						<tr>
							<td width="24%" align="right" class="fontnormal">
								<mifos:mifoslabel name="loan.amount" mandatory="yes"/>:&nbsp;
							</td>
							<td width="76%">
							<mifos:mifosdecimalinput name="AccountTrxnActionForm"
								property="amount" value="${requestScope.accountPayment.amount}"/>
							</td>
						</tr>
						<tr>
							<td align="right" class="fontnormal"><mifos:mifoslabel name="loan.mode_of_payment" mandatory="yes"/>:&nbsp;</td>
							<td>
							
							<c:set var="paymentType" scope="request" value="${requestScope.paymentType.lookUpMaster}"/>
							<mifos:select name="AccountTrxnActionForm" property="paymentType.paymentTypeId" style="width:136px;">
								<html-el:options collection="paymentType" property="id" labelProperty="lookUpValue" />
							</mifos:select>  
							</td>
						</tr>
						<tr>

							<td align="right" class="fontnormal"><mifos:mifoslabel name="loan.receiptId"  />:&nbsp;</td>
							<td class="fontnormal">
								<mifos:mifosalphanumtext property="receiptNumber" name="AccountTrxnActionForm" 
									value="${requestScope.accountPayment.receiptNumber}" maxlength="25"/>
							</td>
						</tr>
						<tr>
			                <td align="right" class="fontnormal">
			                	<mifos:mifoslabel name="loan.receiptdate" />:&nbsp;
			                </td>
			                <td class="fontnormal">
								<date:datetag property="receiptDate"/>
							</td>
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
							<td align="center">
								<c:choose>
									<c:when test="${requestScope.isDisabled}">
										<html-el:submit styleClass="buttn" style="width:130px;" disabled="true">
											<mifos:mifoslabel name="loan.reviewtransaction" />
										</html-el:submit> &nbsp;
									</c:when>
									<c:otherwise>
										<html-el:submit styleClass="buttn" style="width:130px;" >
											<mifos:mifoslabel name="loan.reviewtransaction" />
										</html-el:submit> &nbsp;
									</c:otherwise>
								</c:choose>
								<html-el:button property="cancelButton" styleClass="cancelbuttn" style="width:65px;" 
									onclick="javascript:fun_return(this.form)">
									<mifos:mifoslabel name="loan.cancel" />
								</html-el:button>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			<br>
			<%--<html-el:hidden property="input" value="reviewTransaction" name="AccountTrxnActionForm"/>--%>
			<html-el:hidden property="amount" name="AccountTrxnActionForm" value="${requestScope.accountPayment.amount}" />
			<html-el:hidden property="prdOfferingName" name="AccountTrxnActionForm" value="${param.prdOfferingName}" />
			<html-el:hidden property="installmentId" name="AccountTrxnActionForm" value="${requestScope.accountPayment.installmentId}" />
			<%--html-el:hidden property="dueDate" name="AccountTrxnActionForm" value="${requestScope.accountPayment.dueDate}" /--%>
<html-el:hidden property="searchNode(search_name)" value="ClientChargesDetails"/>
<html-el:hidden property="prdOfferingName" value="${param.prdOfferingName}"/> 
<html-el:hidden property="globalAccountNum" value="${param.globalAccountNum}"/> 
<html-el:hidden property="accountId" value="${param.accountId}"/> 
<html-el:hidden property="accountType" value="${param.accountType}"/> 	
<html-el:hidden property="input" value="${param.input}"/> 
		</html-el:form>
		
<html-el:form  action="closedaccsearchaction.do?method=search">
<html-el:hidden property="searchNode(search_name)" value="ClientChargesDetails"/>
<html-el:hidden property="prdOfferingName" value="${param.prdOfferingName}"/> 
<html-el:hidden property="globalAccountNum" value="${param.globalAccountNum}"/> 
<html-el:hidden property="accountId" value="${param.accountId}"/> 
<html-el:hidden property="accountType" value="${param.accountType}"/> 
<html-el:hidden property="input" value="${param.input}"/> 
</html-el:form>

	</tiles:put>
</tiles:insert>

