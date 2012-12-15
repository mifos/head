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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/tags/date" prefix="date"%>

<SCRIPT>
    function ViewLoanDetails(){
    	goBackToLoanAccountDetails.submit();
    }
</SCRIPT>


<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<span id="page.id" title="ApplyAdjustment"></span>
		<SCRIPT SRC="pages/accounts/js/applyadjustment.js"></SCRIPT>
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
		
        <form name="goBackToLoanAccountDetails" method="get" action ="viewLoanAccountDetails.ftl">
			<input type="hidden" name='globalAccountNum' value="${BusinessKey.globalAccountNum}"/>
		</form>
        <form name="goBackToAdjustmentDetails" method="post" action ="applyAdjustment.do?method=editAdjustment">
            <input type="hidden" name='globalAccountNum' value="${BusinessKey.globalAccountNum}"/>
            <input type="hidden" name="currentFlowKey" value="${requestScope.currentFlowKey}" />
            <input type="hidden" name="isRevert" value="${sessionScope.applyAdjustmentActionForm.adjustcheckbox}" />
        </form>
        
		<html-el:form method="post" action="applyAdjustment.do" onsubmit="return fn_submit();">
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'adjAmount')}" var="adjAmount" />

            <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'adjustmentPaymentType')}" var="adjustmentPaymentType" />
            
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
					<td width="70%" height="24" align="left" valign="top"
						class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="70%" class="headingorange">
								<span class="heading"> 
									<c:out value="${param.prdOfferingName}" />&nbsp;#&nbsp;
									<c:out value="${param.globalAccountNum}" />
									&nbsp;-&nbsp;
								</span> 
								<mifos:mifoslabel name="accounts.divide.group.adjustment" bundle="accountsUIResources" />
							</td>
						</tr>
					</table>
					<br>
					<logic:messagesPresent>
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td>
									<font class="fontnormalRedBold">
										<span id="applyadjustment.error.message"><html-el:errors bundle="accountsUIResources" /></span> 
									</font>
								</td>
							</tr>			
						</table>
						<br>
					</logic:messagesPresent>
	
					<table width="750" border="0" cellpadding="0" cellspacing="0">
						<thead>
							<tr>
								<th>
									<span class='fontnormalbold'>
									   <mifos:mifoslabel name="accounts.acc_owner"/>
									</span>
								</th>
								<th>
                                    <span class='fontnormalbold'>
                                        Loan System Id
                                    </span>
                                </th>
                                <th>
                                    <span class='fontnormalbold'>
                                        Previous Amount
                                    </span>     
                                </th>
                                <th>
                                    <span class='fontnormalbold'>
                                        <mifos:mifoslabel name="accounts.amount"/>
                                    </span>
                                </th>
							</tr>
						</thead>
						 <c:forEach items="${applyAdjustmentActionForm.memberAdjustmentDtoList}" var="memberAdjustment">
	                     <tr>
                            <td>
                                ${memberAdjustment.clientDisplayName}    
                            </td>
                            <td>
                                ${memberAdjustment.globalAccountNum}
                            </td>
                            <td>
                                <input type="text" value="<fmt:formatNumber value='${memberAdjustment.previousAmount}' />" class="separatedNumber" disabled="disabled"/>
                            </td>
                            <td>
                                <input type="text" name="newAmount(${memberAdjustment.accountId})" value="<fmt:formatNumber value='${memberAdjustment.newAmount}' />" class="separatedNumber"/>
                            </td>
	                     </tr>
						</c:forEach>
					</table>
					<br>
	               <table width="95%" border="0" cellpadding="0" cellspacing="0">
					    <tr>
					        <td>                                                    
					            <html-el:button styleId="applyadjustment.button.edit" styleClass="cancelbuttn" 
					                onclick="javascript:fun_back()" property="edit">
					                <mifos:mifoslabel name="accounts.edit_adjustment" />
					            </html-el:button>
					        </td>
					    </tr>
                        <tr>
                            <td align="center" class="blueline">&nbsp;
                            </td>
                        </tr>
					</table>
					<table width="95%" border="0" cellspacing="0" cellpadding="1">
						<tr>
							<td align="center">
								<html-el:submit styleId="applyadjustment.button.submit" styleClass="buttn" property="submit_btn">
									<mifos:mifoslabel name="accounts.btn_reviewAdjustment">
									</mifos:mifoslabel>
								</html-el:submit>
								 &nbsp; 
								<html-el:button styleId="applyadjustment.button.cancel" styleClass="cancelbuttn" onclick="ViewLoanDetails()" property="cancel">
									<mifos:mifoslabel name="accounts.cancel">
									</mifos:mifoslabel>
								</html-el:button></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			<br>
 			<html-el:hidden property="method" value="divide"/>
 			<html-el:hidden property="globalAccountNum" value="${param.globalAccountNum}"/>
 			<html-el:hidden property="prdOfferingName" value="${param.prdOfferingName}"/>
            <html-el:hidden property="paymentId"/>
</html-el:form>


	</tiles:put>
</tiles:insert>
