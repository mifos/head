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
		<SCRIPT SRC="pages/application/savings/js/CreateSavingsAccount.js"></SCRIPT>
	<html-el:form method="post" action="/savingsAction.do?method=editPreview" >
	<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />

      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bluetablehead05">
			  <span class="fontnormal8pt">
	          	<customtags:headerLink/> 
	          </span>               
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
                	<c:out value="${BusinessKey.savingsOffering.prdOfferingName}"/> # <c:out value="${BusinessKey.globalAccountNum}"/> - 
                	</span> 
                	<mifos:mifoslabel name="Savings.Accountstatementasof"/>
                	<c:out value="${loanfn:getCurrrentDate(sessionScope.UserContext.preferredLocale)}" />
                </td>
              </tr>
            </table>
            <br>
        <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'recentActivityList')}" var="recentActivityList" scope="session" />
            <mifoscustom:mifostabletag source="recentActivityList" scope="session" xmlFileName="SavingsAccountActivity.xml" moduleName="accounts/savings" passLocale="true"/>
            <br>
            
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="center">
					   <html-el:button property="returnToAccountDetailsbutton"
					       onclick="javascript:fun_editCancel(this.form)"
						     styleClass="buttn">
						<mifos:mifoslabel name="Savings.returnToAccountDetails"/>
						</html-el:button>
					</td>
				</tr>
    		</table>
          </td>
        </tr>
      </table>
      <html-el:hidden property="accountId" value="${BusinessKey.accountId}"/>
      <html-el:hidden property="globalAccountNum" value="${BusinessKey.globalAccountNum}"/>
</html-el:form>
</tiles:put>
</tiles:insert>        
