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



<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<span id="page.id" title="createsavingsaccountconfirmation" />
<html-el:form method="get" action="/savingsAction.do">
  <table width="95%" border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td align="left" valign="top" class="paddingL15T15" >
        <table width="98%" border="0" cellspacing="0" cellpadding="3">
          <tr>
            <td class="headingorange">
            <mifos:mifoslabel name="Savings.successfullyCreated"/>
            <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
            <mifos:mifoslabel name="Savings.account"/>
              <br><br>
            </td>
          </tr>
          <tr>
            <td class="fontnormalbold">
            	<mifos:mifoslabel name="Savings.pleaseNote" isColonRequired="yes"/>
            	<span class="fontnormal">
            	<mifos:mifoslabel name="Savings.ANew"/>
            	<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
            	<mifos:mifoslabel name="Savings.account"/>
            	<mifos:mifoslabel name="Savings.for"/> <c:out value="${requestScope.clientName}" />
            	<mifos:mifoslabel name="Savings.accountAssigned"/> <c:out value="${requestScope.globalAccountNum}"/>.
            	<mifos:mifoslabel name="Savings.searchBoxMsg"/>
            	<br>
                <br>
                        <br>
               </span>
                    <html-el:link href="savingsAction.do?method=get&globalAccountNum=${requestScope.globalAccountNum}&recordOfficeId=${requestScope.recordOfficeId}&recordLoanOfficerId=${sessionScope.UserContext.id}">
	                    <mifos:mifoslabel name="Savings.View"/>
		            	<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
		            	<mifos:mifoslabel name="Savings.account"/>
		            	<mifos:mifoslabel name="Savings.detailsNow"/>
                   </html-el:link><br><br>
                   <span class="fontnormalboldorange">
                   <mifos:mifoslabel name="Savings.suggestedNextSteps"/>
                   </span>
                   <span class="fontnormal">
	                    <br>
	                    <mifos:mifoslabel name="Savings.openAnotherAccount"/> <mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"/><br>
	               </span>
                    <table width="80%" border="0" cellspacing="0" cellpadding="0">
                      <tr>
                        <td width="2%"><img src="pages/framework/images/trans.gif" width="15" height="10"></td>
                        <td width="98%"><span class="fontnormal">
                        <html-el:link href="savingsAction.do?method=getPrdOfferings">
	                        <mifos:mifoslabel name="Savings.openANew"/>
			            	<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
			            	<mifos:mifoslabel name="Savings.account"/> 
                        </html-el:link><br>
                    <c:if test="${requestScope.clientLevel != CustomerConstants.CENTER_LEVEL_ID}">
                        <html-el:link href="loanAccountAction.do?method=getPrdOfferings&customerId=${requestScope.clientId}&randomNUm=${sessionScope.randomNUm}">
                            <mifos:mifoslabel name="Savings.openANew"/>
			            	<mifos:mifoslabel name="${ConfigurationConstants.LOAN}"/>
			            	<mifos:mifoslabel name="Savings.account"/> 
                        </html-el:link>
                    </c:if>
                        </span></td>
                      </tr>
                    </table></td>
          </tr>
        </table>
        <br>
        <br>
      </td>
    </tr>
  </table>
</html-el:form>
</tiles:put>
</tiles:insert>
