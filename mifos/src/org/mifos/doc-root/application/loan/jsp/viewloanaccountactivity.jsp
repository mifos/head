<!--
 
 * editloanaccountactivity.jsp  version: 1.0
 
 
 
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
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<html-el:form method="post" action="/loanAccountAction.do" >
	<SCRIPT SRC="pages/application/loan/js/LoanAccountActivity.js"></SCRIPT>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"> <customtags:headerLink/> </span>
					</td>
				</tr>
	  </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td align="left" valign="top" class="paddingL15T15" >
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td width="83%" class="headingorange">
                	<span class="heading">
                	<c:out value="${param.prdOfferingName}"/> # <c:out value="${param.globalAccountNum}"/> - 
                	</span> 
                	<mifos:mifoslabel name="loan.acc_statement"/>
                	<c:out value="${loanfn:getCurrrentDate(sessionScope.UserContext.pereferedLocale)}" />
                </td>
              </tr>
              <tr><td>
	              <font class="fontnormalRedBold">
	              	<html-el:errors bundle="accountsUIResources" />
	              </font></td>
              </tr>
            </table>
            <c:if test="${param.accountStateId != 6 and param.accountStateId != 7 and param.accountStateId !=8 and param.accountStateId !=10}">
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td>
							<p><br>
							</p>
							</td>
						</tr>
					</table>
		
					<table width="100%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td bgcolor="#F0D4A5" style="padding-left:10px; padding-bottom:3px;">
								<span class="fontnormalbold">
									<mifos:mifoslabel name="loan.apply_trans" />
								</span>&nbsp;&nbsp;&nbsp;&nbsp;	
								<c:if test="${(param.accountStateId=='5' || param.accountStateId=='9')}">
									<html-el:link href="applyPaymentAction.do?method=load&input=loan&prdOfferingName=${param.prdOfferingName}&globalAccountNum=${param.globalAccountNum}&accountId=${param.accountId}&accountType=${param.accountType}&recordOfficeId=${param.recordOfficeId}&recordLoanOfficerId=${param.recordLoanOfficerId}&accountStateId=${param.accountStateId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
										<mifos:mifoslabel name="loan.apply_payment" />
									</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								</c:if>							
								<c:if test="${param.lastPaymentAction != '10'}">
								<c:choose>
									<c:when test="${param.accountStateId=='5' || param.accountStateId=='9'}">
								<%--	<html-el:link href="applyPaymentAction.do?method=load&input=loan&prdOfferingName=${param.prdOfferingName}&globalAccountNum=${param.globalAccountNum}&accountId=${param.accountId}&accountType=${param.accountTypeId}
															&recordOfficeId=${param.recordOfficeId}&recordLoanOfficerId=${param.recordLoanOfficerId}&accountStateId=${param.accountStateId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
										<mifos:mifoslabel name="loan.apply_payment" />
									</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								--%>		<html-el:link href="applyAdjustment.do?method=loadAdjustment&accountId=${param.accountId}&globalAccountNum=${param.globalAccountNum}&prdOfferingName=${param.prdOfferingName}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}"> 
											<mifos:mifoslabel name="loan.apply_adjustment" />
										</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									</c:when>
								</c:choose>
								</c:if>
        						 <html-el:link href="applyChargeAction.do?method=load&accountId=${param.accountId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
									<mifos:mifoslabel name="loan.apply_charges" />
								</html-el:link>
							</td>
						</tr>
					</table>
				</c:if>
       <br>
            <customtags:allActivity/>
            <br>
            
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="center">
					   <html-el:button property="returnToAccountDetailsbutton"
					       onclick="javascript:fun_return(this.form)"
						     styleClass="buttn" style="width:165px;">
						<mifos:mifoslabel name="loan.returnToAccountDetails"/>
						</html-el:button>
					</td>
				</tr>
    		</table>
          </td>
        </tr>
      </table>
      <html-el:hidden property="accountId" value="${param.accountId}"/>
      <html-el:hidden property="prdOfferingName" value="${param.prdOfferingName}"/>
      <html-el:hidden property="accountStateId" value="${param.accountStateId}"/>
	  <html-el:hidden property="accountTypeId" value="${param.accountTypeId}"/>
	  <html-el:hidden property="recordOfficeId" value="${param.recordOfficeId}"/>
	  <html-el:hidden property="recordLoanOfficerId" value="${param.recordLoanOfficerId}"/>
      <html-el:hidden property="globalAccountNum" value="${param.globalAccountNum}"/>
      <html-el:hidden property="input" value="reviewTransactionPage"/>
	<mifos:SecurityParam property="Loan" />
</html-el:form>
</tiles:put>
</tiles:insert>        
