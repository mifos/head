<!--
/**

* centerConfirmation.jsp    version: 1.0



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

*/
 -->
<%@taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-bean-el" prefix="bean-el"%>

<!-- Inserting tile defintion for header and menu -->
<tiles:insert definition=".clientsacclayoutsearchmenu">
<tiles:put name="body" type="string">



 <!-- Body Begins -->
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td align="left" valign="top" class="paddingL15T15" >
              <table width="98%" border="0" cellspacing="0" cellpadding="3">
               <!-- Center confirmation message -->
              	<tr>
                  <td class="headingorange"><mifos:mifoslabel name="Center.ConfirmationMessage" bundle="CenterUIResources"/><mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /><br></td>
              	</tr>
              	<tr>
                  <!-- Displays the center system id and name of the center -->
                  <td class="fontnormalbold"> <mifos:mifoslabel name="Center.Confirmation.Note" bundle="CenterUIResources"></mifos:mifoslabel>
                	<span class="fontnormal"> <c:out value="${sessionScope.centerCustActionForm.displayName}"/> 
                   <mifos:mifoslabel name="Center.Confirmation.NameSystemID" bundle="CenterUIResources"></mifos:mifoslabel></span> <c:out value="${requestScope.centerVO.globalCustNum}"/> <span class="fontnormal"><br>
      				<mifos:mifoslabel name="Center.Confirmation.Information1" bundle="CenterUIResources"/>
      				<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
      				<mifos:mifoslabel name="Center.Confirmation.Information2" bundle="CenterUIResources"/>
      				<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /><mifos:mifoslabel name="Center.Confirmation.Information3" bundle="CenterUIResources"/>      				
      				<br>
                    <br>
                    </span>
                     <!-- Link to view the center details -->
                    <a href="centerAction.do?method=get&globalCustNum=<c:out value="${sessionScope.centerCustActionForm.globalCustNum}"/>&recordOfficeId=${sessionScope.centerCustActionForm.officeId}&recordLoanOfficerId=${sessionScope.centerCustActionForm.loanOfficerId}">
                    	<mifos:mifoslabel name="Center.View" bundle="CenterUIResources"/>
         				<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
                    	<mifos:mifoslabel name="Center.DetailsNow" bundle="CenterUIResources"/>
                    	
                    </a>
                    <span class="fontnormal"><br>
                    <br>
                    </span><span class="fontnormalboldorange"><mifos:mifoslabel name="Center.Confirmation.NextStep" bundle="CenterUIResources"></mifos:mifoslabel>
      				</span><span class="fontnormal"> <br>
                    </span><mifos:mifoslabel name="Center.AccountsHeading" bundle="CenterUIResources"/><mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /><span class="fontnormal"><br>
					<!-- Link to create a new savingsa account link -->
                    <html-el:link href="savingsAction.do?method=getPrdOfferings&customerId=${requestScope.centerVO.customerId}&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}">
                    <mifos:mifoslabel name="Center.CreateNew" bundle="CenterUIResources"/>
                    <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
                    <mifos:mifoslabel name="Center.account" bundle="CenterUIResources"/>
                    </html-el:link><br>
                    <br>
                    <!-- Link to create a new center -->
                    <a href="centerAction.do?method=chooseOffice&recordOfficeId=0&recordLoanOfficerId=0"/>
                    <mifos:mifoslabel name="Center.CreateNew" bundle="CenterUIResources"></mifos:mifoslabel>
                    <mifos:mifoslabel name="${ConfigurationConstants.CENTER}"/>
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

</body>
</html>
</tiles:put>
</tiles:insert>
