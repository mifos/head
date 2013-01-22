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
	<span id="page.id" title="ViewLoanAccountActivity"></span>
	<form method="get" action="viewLoanAccountDetails.ftl" >
	<SCRIPT SRC="pages/application/loan/js/LoanAccountActivity.js"></SCRIPT>
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
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
                	<c:out value="${loanfn:getCurrrentDate(sessionScope.UserContext.preferredLocale)}" />
                </td>
              </tr>
              <tr><td>
	              <font class="fontnormalRedBold">
	              	<span id="viewloanaccountactivity.error.message"><html-el:errors bundle="accountsUIResources" /></span>
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
								
								<c:choose>
								<c:when test="${BusinessKey.parentGroupLoanAccount || BusinessKey.groupLoanAccountMember }">
									<c:url value="applyGroupPaymentAction.do" var="applyGroupPaymentActionMethodUrl" >										<
											<c:param name="method" value="load" />
											<c:param name="input" value="loan" />
											<c:param name="prdOfferingName" value="${param.prdOfferingName}" />
											<c:param name="globalAccountNum" value="${param.globalAccountNum}" />
											<c:param name="accountId" value="${param.accountId}" />
											<c:param name="accountType" value="${BusinessKey.accountType.accountTypeId}" />
											<c:param name="recordOfficeId" value="${param.recordOfficeId}" />
											<c:param name="recordLoanOfficerId" value="${param.recordLoanOfficerId}" />
											<c:param name="randomNUm" value="${sessionScope.randomNUm}" />
											<c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
									</c:url >
									<html-el:link styleId="loanaccountdetail.link.applyPayment"
										href="${applyGroupPaymentActionMethodUrl}">
										<mifos:mifoslabel name="loan.apply_payment" />
									</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									
											<c:url value="applyAdjustment.do" var="applyAdjustmentLoadAdjustmentMethodUrl" >
												<c:param name="method" value="listPossibleAdjustments" />
												<c:param name="accountId" value="${param.accountId}" />
												<c:param name="globalAccountNum" value="${param.globalAccountNum}" />
												<c:param name="prdOfferingName" value="${param.prdOfferingName}" />
												<c:param name="randomNUm" value="${sessionScope.randomNUm}" />
												<c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
											</c:url >
									<c:choose>
										<c:when
											test="${(BusinessKey.accountState.id=='5' || BusinessKey.accountState.id=='9' || BusinessKey.accountState.id=='6') }">
												<html-el:link styleId="loanaccountdetail.link.applyAdjustment"
												href="${applyAdjustmentLoadAdjustmentMethodUrl}">
												<mifos:mifoslabel name="loan.apply_adjustment" />
												</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										</c:when>
									</c:choose>
									<c:if test="${!BusinessKey.groupLoanAccountMember }">
										<html-el:link styleId="loanaccountdetail.link.applyCharges"
												href="applyChargeAction.do?method=load&accountId=${BusinessKey.accountId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
												<mifos:mifoslabel name="loan.apply_charges" />
												</html-el:link><br>
									</c:if>
								</c:when>
								<c:otherwise>
								
								
								
								
								<c:if test="${(param.accountStateId=='5' || param.accountStateId=='9')}">
									<html-el:link styleId="viewloanaccountactivity.link.applyPayment" href="applyPaymentAction.do?method=load&input=loan&prdOfferingName=${param.prdOfferingName}&globalAccountNum=${param.globalAccountNum}&accountId=${param.accountId}&accountType=${param.accountType}&recordOfficeId=${param.recordOfficeId}&recordLoanOfficerId=${param.recordLoanOfficerId}&accountStateId=${param.accountStateId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
										<mifos:mifoslabel name="loan.apply_payment" />
									</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								</c:if>							
								<c:if test="${param.lastPaymentAction != '10'}">
								<c:choose>
									<c:when test="${param.accountStateId=='5' || param.accountStateId=='9'}">
								<%--	<html-el:link styleId="viewloanaccountactivity.link.applyPayment" href="applyPaymentAction.do?method=load&input=loan&prdOfferingName=${param.prdOfferingName}&globalAccountNum=${param.globalAccountNum}&accountId=${param.accountId}&accountType=${param.accountTypeId}
															&recordOfficeId=${param.recordOfficeId}&recordLoanOfficerId=${param.recordLoanOfficerId}&accountStateId=${param.accountStateId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
										<mifos:mifoslabel name="loan.apply_payment" />
									</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								--%>		<html-el:link styleId="viewloanaccountactivity.link.applyAdjustment" href="applyAdjustment.do?method=listPossibleAdjustments&accountId=${param.accountId}&globalAccountNum=${param.globalAccountNum}&prdOfferingName=${param.prdOfferingName}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}"> 
											<mifos:mifoslabel name="loan.apply_adjustment" />
										</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									</c:when>
								</c:choose>
								</c:if>
        						 <html-el:link styleId="viewloanaccountactivity.link.applyCharges" href="applyChargeAction.do?method=load&accountId=${param.accountId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
									<mifos:mifoslabel name="loan.apply_charges" />
								</html-el:link>
								
								
								</c:otherwise>
							</c:choose>
								
								
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
					   <html-el:button styleId="viewloanaccountactivity.button.return" property="returnToAccountDetailsbutton"
					       onclick="javascript:fun_return(this.form, ${param.accountStateId})"
						     styleClass="buttn" >
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
</form>
</tiles:put>
</tiles:insert>        
