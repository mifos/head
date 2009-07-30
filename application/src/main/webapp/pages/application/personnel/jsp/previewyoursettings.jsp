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
<%@ taglib uri="/mifos/customtags" prefix = "mifoscustom"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".noorangetab">
 <tiles:put name="body" type="string">
 <span style="display: none" id="page.id">previewyoursettings</span>
 
 <SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
<script language="javascript">
  function goToCancelPage(){
	personnelSettingsActionForm.action="yourSettings.do?method=get";
	personnelSettingsActionForm.submit();
  }
   function goToEditPage(){
	personnelSettingsActionForm.action="yourSettings.do?method=previous";
	personnelSettingsActionForm.submit();
  }
</script>
<html-el:form action="yourSettings.do?method=update">
<c:set var="form" value="${sessionScope.personnelSettingsActionForm}" />
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td height="350" align="left" valign="top" bgcolor="#FFFFFF">
         <table width="590" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <td align="center" class="blueline">&nbsp;</td>
        </tr>
    </table>


      <table width="590" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
          <tr>
            <td align="left" valign="top" class="paddingleftCreates">

            <table width="93%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td class="headingorange">
                  <mifos:mifoslabel name="Personnel.PreviewYourSettings"/></td>
                </tr>
                <tr>
                  <td class="fontnormal"><mifos:mifoslabel name="Personnel.ReviewInformation"/>
                  <mifos:mifoslabel name="Personnel.SubmitOrEdit"/>
                  <mifos:mifoslabel name="Personnel.CancelSettings"/>
                  </td>
                </tr>
              </table>
              <br>
          <table width="93%" border="0" cellpadding="3" cellspacing="0">
           <tr>
	   			<td>
	   				<font class="fontnormalRedBold">
	   					<span id="previewyoursettings.error.message"><html-el:errors bundle="PersonnelUIResources"/></span>
	   				</font>
				</td>
		   </tr>
           <tr>
            <tr class="fontnormal">
              <td>
	              <span class="fontnormalbold"><mifos:mifoslabel name="Personnel.YourDetails"/>
	              <br>
              </span>
              </td>
            </tr>

            <tr>
           	 <td width="100%">
			  <table width="100%" border="0" cellpadding="0" cellspacing="0">
			    <tr><td  class="fontnormalbold">
	           	 <mifos:mifoslabel name="Personnel.FirstName"/>
	           	 <span class="fontnormal">
		           	 <c:out value="${form.firstName}"/>
	           	 </span> <br>
				</td></tr>
				<tr id="Personnel.MiddleName"><td  class="fontnormalbold">
	           	 <mifos:mifoslabel keyhm="Personnel.MiddleName" name="Personnel.MiddleName"/>
	           	 <span class="fontnormal">
	           		 <c:out value="${form.middleName}"/>
	           	 </span><br>
				</td></tr>
				<tr id="Personnel.SecondLastName"><td class="fontnormalbold">
	           	 <mifos:mifoslabel keyhm="Personnel.SecondLastName" name="Personnel.SecondLastName"/>
	           	 <span class="fontnormal">
		           	 <c:out value="${form.secondLastName}"/>
	           	 </span><br>
				</td></tr>
				<tr><td  class="fontnormalbold">
	           	 <mifos:mifoslabel name="Personnel.LastName"/>
	           	 <span class="fontnormal">
		           	 <c:out value="${form.lastName}"/>
	           	 </span><br>
				</td></tr>
				<tr id="Personnel.GovernmentId"><td class="fontnormalbold">
	           	 <mifos:mifoslabel name="${ConfigurationConstants.GOVERNMENT_ID}" bundle="PersonnelUIResources"></mifos:mifoslabel>:
				 <span class="fontnormal">
					<c:out value="${form.governmentIdNumber}"/>
				 </span> <br>
				 </td></tr>
				<tr><td class="fontnormalbold">
	           	 <mifos:mifoslabel name="Personnel.Email" />
 	           	 <span class="fontnormal">
		           	 <c:out value="${form.emailId}"/>
	           	 </span><br>
				</tr></td>
				<tr><td class="fontnormalbold">
	           	 <mifos:mifoslabel name="Personnel.DOB" bundle="PersonnelUIResources"></mifos:mifoslabel>
			    <span class="fontnormal">
					<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,form.dob)}" />
				</span><br>
				</td></tr>
				<tr><td class="fontnormalbold">
			    <mifos:mifoslabel name="Personnel.Age" bundle="PersonnelUIResources"></mifos:mifoslabel>
			     <span class="fontnormal">
					<c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'personnelAge')}"/>
				 </span><br>
				</td></tr>
				<tr><td class="fontnormalbold">
	           	 <mifos:mifoslabel name="Personnel.MaritalStatus" />
 	           	 <span class="fontnormal">
		           	 <c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'MaritalStatus')}"/>
	           	 </span><br>
				</td></tr>
				<tr><td class="fontnormalbold">
	           	 <mifos:mifoslabel name="Personnel.Gender" />
				 <span class="fontnormal">
		           	 <c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'Gender')}"/>
	           	 </span><br>
				</td></tr>
				<tr><td class="fontnormalbold">
	           	 <mifos:mifoslabel name="Personnel.LanguagePreferred" />
	           	 <span class="fontnormal">
		           	 <c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'Language')}"/>
	           	 </span><br>
				</td></tr>
				<tr><td class="fontnormalbold">
	           	 <mifos:mifoslabel name="Personnel.UserName"  bundle="PersonnelUIResources"></mifos:mifoslabel>
				<span class="fontnormal">
					<c:out value="${form.userName}"/>
				</span><br>
	           	 <br>
				</td></tr>
				<tr><td class="fontnormalbold">
	           	 <mifos:mifoslabel name="Personnel.Address" /><br>
 				 <span class="fontnormal">
		           	 <c:out value="${form.addressDetails}"/>
	           	 </span><br>
				</td></tr>
				<tr><td class="fontnormalbold">
	           	 <mifos:mifoslabel name="${ConfigurationConstants.CITY}" />:
	           	 <span class="fontnormal">
		           	 <c:out value="${form.address.city}"/>
	           	 </span><br>
				</td></tr>
				<tr id="Personnel.State"><td class="fontnormalbold">
	           	 <mifos:mifoslabel keyhm="Personnel.State" name="${ConfigurationConstants.STATE}" />:
	           	 <span class="fontnormal">
		           	 <c:out value="${form.address.state}"/>
	           	 </span><br>
				</td></tr>
				<tr id="Personnel.Country"><td class="fontnormalbold">
	           	 <mifos:mifoslabel name="Personnel.Country" />
	           	 <span class="fontnormal">
	    	       	 <c:out value="${form.address.country}"/>
	           	 </span><br>
				</td></tr>
				<tr id="Personnel.PostalCode"><td class="fontnormalbold">
	           	 <mifos:mifoslabel name="${ConfigurationConstants.POSTAL_CODE}"/>:
	           	 <span class="fontnormal">
		           	 <c:out value="${form.address.zip}"/>
	           	 </span><br>
				</td></tr>
				<tr id="Personnel.Telephone"><td class="fontnormalbold">
	           	 <mifos:mifoslabel name="Personnel.Telephone"/>
	           	 <span class="fontnormal">
		           	 <c:out value="${form.address.phoneNumber}"/>
	           	 </span><br>
	           	 </td></tr>
	          </table>
   			 </td>
            </tr>

          </table>
              <br>
         <table width="93%" border="0" cellpadding="3" cellspacing="0">



      <tr class="fontnormal">
	   <td>
           <html-el:button styleId="previewyoursettings.button.edit" property="btn" styleClass="insidebuttn" onclick="goToEditPage()">
		   <mifos:mifoslabel name="Personnel.EditInformation" />
           </html-el:button>
        </td>
      </tr>
              </table>
              <table width="93%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center" class="blueline">&nbsp; </td>
                </tr>
              </table>
              <br>
              <table width="93%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center">
                  <html-el:submit property="submitBtn" styleClass="buttn">
	               	<mifos:mifoslabel name="button.submit" bundle="PersonnelUIResources"></mifos:mifoslabel>
              	 </html-el:submit>
&nbsp;
		      	<html-el:button styleId="previewyoursettings.button.cancel" property="cancelBtn"  styleClass="cancelbuttn" onclick="goToCancelPage()">
	                <mifos:mifoslabel name="button.cancel" bundle="PersonnelUIResources"></mifos:mifoslabel>
               	</html-el:button>
                  </td>
                </tr>
              </table>              <br>
            <br></td>
          </tr>
        </table>
      <br></td>
  </tr>
</table>
<html-el:hidden property="input" value="EditSettings"/>
<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
<html-el:hidden property="randomNUm" value="${sessionScope.randomNUm}" />
</html-el:form>

</tiles:put>
</tiles:insert>
