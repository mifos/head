<!--
 
 * review_transaction_loan.jsp  version: xxx
 
 
 
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
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">

		<script>
			function fun_return(form)
					{
						<%--form.method.value="get";
						form.action="loanAction.do?globalAccountNum=${param.accountNum}";--%>
						form.action="accountTrxn.do?method=cancel";
						form.submit();
					}
			function fun_edit(form)
					{
						
						form.action="accountTrxn.do?method=previous";
						form.submit();
					}
			function ViewDetails(){
				closedaccsearchactionform.submit();
			}
	</script>
		<html-el:form  action="accountTrxn.do?method=create">

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt">
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
							</span><span class="fontnormal8ptbold">
							 / <mifos:mifoslabel name="loan.reviewtransaction" /></span></td>
				</tr>
								
			</table>
				<table width="95%" border="0" cellpadding="0" cellspacing="0">
								<font class="fontnormalRedBold">
									<html-el:errors bundle="accountsUIResources" /> 
								</font>
					<tr>
						<td width="70%" height="24" align="left" valign="top"
							class="paddingL15T15">
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="100%" colspan="2" class="headingorange"><span
									class="heading"> 
									<c:choose>
									<c:when test="${param.input=='reviewTransactionPage'}">
										<c:out value="${param.prdOfferingName}" />&nbsp;#&nbsp;
										<c:out value="${param.globalAccountNum}" />
									</c:when>
									<c:otherwise>
										<c:out value="${param.prdOfferingName}" />
									</c:otherwise>
								</c:choose>&nbsp;-&nbsp;
									</span><mifos:mifoslabel
									name="loan.reviewtransaction"  /></td>
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
						<table width="96%" border="0" cellspacing="0" cellpadding="1">
							<tr>
				                <td align="right" class="fontnormalbold">
				                	<mifos:mifoslabel name="loan.dateoftrxn" />:&nbsp;:	
				                </td>
				                <td class="fontnormal"><c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.accountPayment.paymentDate)}" /></td>
				            </tr>
							<tr>
								<td width="22%" align="right" class="fontnormalbold"><mifos:mifoslabel
									name="loan.amount"  />:&nbsp;</td>

								<td width="78%" class="fontnormal">
								<c:out value="${requestScope.accountPayment.amount}" /></td>
							</tr>
							<tr>
								<td align="right" class="fontnormalbold"><mifos:mifoslabel
									name="loan.mode_of_payment"  />:&nbsp;</td>
								<td class="fontnormal">
									<mifoscustom:lookUpValue
										id="${requestScope.accountPayment.paymentType.paymentTypeId}"
										searchResultName="paymentType"
										mapToSeperateMasterTable="true">
									</mifoscustom:lookUpValue>
								</td>
							</tr>
							
							<tr>
								<td align="right" class="fontnormalbold"><mifos:mifoslabel
									name="loan.receiptId"  />:&nbsp;</td>
								<td class="fontnormal">
								<c:out value="${requestScope.accountPayment.receiptNumber}" /></td>
							</tr>
							<tr>
								<td align="right" class="fontnormalbold"><mifos:mifoslabel
									name="loan.receiptdate"  />:&nbsp;</td>
								<td class="fontnormal">
									<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.accountPayment.receiptDate)}" />
								</td>
							</tr>
							<tr>
				                <td align="right" class="fontnormalbold">&nbsp;</td>
				                <td class="fontnormal">&nbsp;</td>
			                </tr>
			                <tr>
                				<td height="3" colspan="2" align="center">&nbsp;</td>
              			    </tr>
              				<tr>
                				<td height="3" colspan="2">
                					<html-el:button property="editButton" styleClass="insidebuttn" 
										onclick="javascript:fun_edit(this.form)"
										style="width:115px;">
										<mifos:mifoslabel name="loan.editTrxn" />
									</html-el:button>
                				</td>
              				</tr>
							<tr align="center">
								<td height="3" colspan="2" class="blueline">&nbsp;</td>
							</tr>
							<tr align="center">
								<td height="3" colspan="2" class="fontnormal">&nbsp;</td>
							</tr>
						</table>
						<table width="96%" border="0" cellspacing="0" cellpadding="1">
							<tr>
								<td align="center"><html-el:submit styleClass="buttn"
									style="width:65px;">
									<mifos:mifoslabel name="loan.submit" />
								</html-el:submit> &nbsp; 
								<html-el:button property="cancelButton" styleClass="cancelbuttn" 
									onclick="javascript:fun_return(this.form)"
									style="width:65px;">
									<mifos:mifoslabel name="loan.cancel" />
								</html-el:button></td>
							</tr>
						</table>
						<br>
						</td>
					</tr>
				</table>
				<br>
<html-el:hidden property="searchNode(search_name)" value="ClientChargesDetails"/>
<html-el:hidden property="prdOfferingName" value="${param.prdOfferingName}"/> 
<html-el:hidden property="globalAccountNum" value="${param.globalAccountNum}"/> 
<html-el:hidden property="accountId" value="${param.accountId}"/> 
<html-el:hidden property="accountType" value="${param.accountType}"/> 	
<html-el:hidden property="input" value="${param.input}"/> 		
				<%--<html-el:hidden property="input" value="reviewTransactionPage" name="AccountTrxnActionForm"/>--%>
				<%--<html-el:hidden property="method" value="create" />--%>
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
