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
<%@taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-bean-el" prefix="bean-el"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!-- Inserting tile defintion for header and menu -->
<tiles:insert definition=".clientsacclayoutsearchmenu">
<tiles:put name="body" type="string">

<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
		<fmt:setBundle basename="org.mifos.config.localizedResources.CenterUIResources"/>


 <!-- Body Begins -->
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td align="left" valign="top" class="paddingL15T15" >
              <table width="98%" border="0" cellspacing="0" cellpadding="3">
               <!-- Center confirmation message -->
              	<tr>
                  <td class="headingorange">
                  
                  <span id="createcenterconfirmation.text.confirmation"><fmt:message key="Center.ConfMessage">
							<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /></fmt:param>
				  </fmt:message></span>
				  <br>
				  </td>
                              
                  
              	</tr>
              	<tr>
                  <!-- Displays the center system id and name of the center -->
                  <td class="fontnormalbold"> <mifos:mifoslabel name="Center.Confirmation.Note" bundle="CenterUIResources"></mifos:mifoslabel>
                	<span class="fontnormal"> <c:out value="${sessionScope.centerCustActionForm.displayName}"/>
                   <mifos:mifoslabel name="Center.Confirmation.NameSystemID" bundle="CenterUIResources"></mifos:mifoslabel></span> <c:out value="${sessionScope.centerCustActionForm.globalCustNum}"/> <span class="fontnormal"><br>
      				      				      				
      				<fmt:message key="Center.Confirmation.AllInformation">
							<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /></fmt:param>
				    </fmt:message>
      				
      				
      				
      				<br>
                    <br>
                    </span>
                     <!-- Link to view the center details -->
                    <a id="createcenterconfirmation.link.viewDetailsInfo" href="centerCustAction.do?method=get&globalCustNum=<c:out value="${sessionScope.centerCustActionForm.globalCustNum}"/>&recordOfficeId=${sessionScope.centerCustActionForm.officeId}&recordLoanOfficerId=${sessionScope.centerCustActionForm.loanOfficerId}&randomNUm=${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'randomNUm')}">
                    	
                    	<fmt:message key="Center.ViewDetailsInfo">
							<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /></fmt:param>
				        </fmt:message>
                    	
                    	

                    </a>
                    <span class="fontnormal"><br>
                    <br>
                    </span><span class="fontnormalboldorange"><mifos:mifoslabel name="Center.Confirmation.NextStep" bundle="CenterUIResources"></mifos:mifoslabel>
      				</span><span class="fontnormal"> <br>
                    </span><mifos:mifoslabel name="Center.AccountsHeading" bundle="CenterUIResources"/><mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /><span class="fontnormal"><br>
					<!-- Link to create a new savingsa account link -->
                    <html-el:link styleId="createcenterconfirmation.link.createNewSavingsAccount" href="savingsAction.do?method=getPrdOfferings&customerId=${sessionScope.centerCustActionForm.customerId}&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'randomNUm')}">
                    
                    <fmt:message key="Center.CreateNewAccount">
							<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" /></fmt:param>
				    </fmt:message>
                    
                    
                    </html-el:link><br>
                    <br>
                    <!-- Link to create a new center -->
                    <a id="createcenterconfirmation.link.CreateLinkNewCenter" href="centerCustAction.do?method=chooseOffice&recordOfficeId=0&recordLoanOfficerId=0&randomNUm=${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'randomNUm')}"/>
                    
                    <fmt:message key="Center.CreateLinkNewCenter">
							<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /></fmt:param>
				    </fmt:message>
                    
                    
                    </a>
                    </span>
                  </td>
              	</tr>
        	</table>
        	<br>
       	 	<br>
      	  </td>
    	</tr>
   	  </table>
      <br>
    </td>
  </tr>
</table>
<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
</body>
</html>
</tiles:put>
</tiles:insert>
