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
<%@ taglib uri="/personnel/personnelfunctions" prefix="personnelfn"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".view">
 <tiles:put name="body" type="string">
 <span id="page.id" title="preview_EditUser" />
 <SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
 <script language="javascript">
  function goToEditPage(){
	personActionForm.action="PersonAction.do?method=previousManage";
	personActionForm.submit();
  }
  function goToCancelPage(){
	personActionForm.action="PersonAction.do?method=cancel";
	personActionForm.submit();
  }
</script>
<html-el:form action="PersonAction.do?method=update" onsubmit="func_disableSubmitBtn('submitBtn')">

	<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
	<html-el:hidden property="input" value="ManageUser"/> 
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />

   <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bluetablehead05">
          <span class="fontnormal8pt"><a id="preview_EditUser.link.admin" href="AdminAction.do?method=load">
	           <mifos:mifoslabel name="Personnel.Admin" bundle="PersonnelUIResources"></mifos:mifoslabel>             	
           	</a>
		 / 
			<a id="preview_EditUser.link.viewUsers" href="PersonAction.do?method=loadSearch">
			<mifos:mifoslabel name="Personnel.ViewUsers" bundle="PersonnelUIResources"></mifos:mifoslabel>
			</a> / 
			<a id="preview_EditUser.link.viewUser" href="PersonAction.do?method=get&globalPersonnelNum=<c:out value="${BusinessKey.globalPersonnelNum}"/>">
	           <c:out value="${BusinessKey.displayName}"/>            	
           	</a>
          </span></td>
        </tr>
      </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td align="left" valign="top" class="paddingL15T15"><table width="95%" border="0" cellpadding="3" cellspacing="0">
            <tr>
              <td width="63%" class="headingorange"><span class="heading">
		<c:out value="${BusinessKey.displayName}"/> - 
		</span>
			<mifos:mifoslabel name="Personnel.PreviewUser" bundle="PersonnelUIResources"></mifos:mifoslabel>
		</td>
              </tr>
            <tr>
              <td class="fontnormal">
              <mifos:mifoslabel name="Personnel.ReviewInformation" bundle="PersonnelUIResources"></mifos:mifoslabel>
              <mifos:mifoslabel name="Personnel.SubmitOrEdit" bundle="PersonnelUIResources"></mifos:mifoslabel>
              <mifos:mifoslabel name="Personnel.ClickCancelToDetailsPage" bundle="PersonnelUIResources"></mifos:mifoslabel> 
              </td>
            </tr>
          </table>
            <br>
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
            <tr>
   				<td>
   				<font class="fontnormalRedBold">
   					<span id="preview_EditUser.error.message"><html-el:errors bundle="PersonnelUIResources"/></span>
   				</font>
				</td>
			</tr>
              <tr>
                <td width="100%" class="fontnormalbold">
					<mifos:mifoslabel name="Personnel.FirstName" bundle="PersonnelUIResources"></mifos:mifoslabel> 
					<span class="fontnormal">
						<c:out value="${personActionForm.firstName}"/> 
					</span>
				</td>
			</tr>
			
		<tr id="Personnel.MiddleName">
    		 <td width="100%" class="fontnormalbold">
				<mifos:mifoslabel name="Personnel.MiddleName" bundle="PersonnelUIResources" keyhm="Personnel.MiddleName" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
				<span class="fontnormal">
					<c:out value="${personActionForm.middleName}"/>
				</span><br>	
			</td>
	   </tr>
	
	 <tr id="Personnel.SecondLastName">
     <td width="100%" class="fontnormalbold">
     <mifos:mifoslabel name="Personnel.SecondLastName" bundle="PersonnelUIResources" keyhm="Personnel.SecondLastName" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
		<span class="fontnormal">
			<c:out value="${personActionForm.secondLastName}"/>
		</span><br>
	</td>
	</tr>
	
	 <tr>
      <td width="100%" class="fontnormalbold">
    <mifos:mifoslabel name="Personnel.LastName" bundle="PersonnelUIResources"></mifos:mifoslabel>
	<span class="fontnormal"> 
		<c:out value="${personActionForm.lastName}"/> 
	</span> <br>
	</td>
	</tr>
	
	<tr id="Personnel.GovernmentId">
     <td width="100%" class="fontnormalbold">
      	<mifos:mifoslabel name="${ConfigurationConstants.GOVERNMENT_ID}" bundle="PersonnelUIResources" isColonRequired="yes" keyhm="Personnel.GovernmentId" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> 
	<span class="fontnormal">
		<c:out value="${BusinessKey.personnelDetails.governmentIdNumber}"/> 
	</span> <br>
	</td>
	</tr>
	<tr>
      <td width="100%" height="23" class="fontnormalbold">
      	<mifos:mifoslabel name="Personnel.Email" bundle="PersonnelUIResources"></mifos:mifoslabel> 
	<span class="fontnormal">
		<c:out value="${personActionForm.emailId}"/>
	</span> <br>
	<mifos:mifoslabel name="Personnel.DOB" bundle="PersonnelUIResources"></mifos:mifoslabel>
    <span class="fontnormal">
		<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,BusinessKey.personnelDetails.dob)}" />
	</span><br>
    <mifos:mifoslabel name="Personnel.Age" bundle="PersonnelUIResources"></mifos:mifoslabel>
     <span class="fontnormal">
		<c:out value="${personActionForm.age}"/> 
	 </span><br> 	
	<mifos:mifoslabel name="Personnel.MaritalStatus" bundle="PersonnelUIResources"></mifos:mifoslabel>
	<span class="fontnormal">
	 	<c:forEach var="MS" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'maritalStatusList')}">
			<c:if test = "${MS.id == personActionForm.maritalStatus}">
				<c:out value="${MS.name}"/><br>
			</c:if>
		</c:forEach>
	</span> <br>
      	<mifos:mifoslabel name="Personnel.Gender" bundle="PersonnelUIResources"></mifos:mifoslabel> 
	<span class="fontnormal">
		<c:forEach var="genderlist" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'genderList')}">
			<c:if test = "${genderlist.id == personActionForm.gender}">
				<c:out value="${genderlist.name}"/><br>
			</c:if>
		</c:forEach>
	</span> <br>
      	<mifos:mifoslabel name="Personnel.LanguagePreferred"  bundle="PersonnelUIResources"></mifos:mifoslabel> 
	<span class="fontnormal">
		<c:forEach	items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'languageList')}"	var="item">
			<c:if test="${personActionForm.preferredLocale == item.id}">
				<c:out value="${item.name}"/>
			</c:if>
		</c:forEach> 
	</span> <br>
      	<mifos:mifoslabel name="Personnel.DOJMFI"  bundle="PersonnelUIResources"></mifos:mifoslabel> 
	<span class="fontnormal">
		<c:out value="${personActionForm.dateOfJoiningMFI}" />

	</span><br>
      <mifos:mifoslabel name="Personnel.Status"  bundle="PersonnelUIResources"></mifos:mifoslabel>: 
	<span class="fontnormal">
		<c:forEach var="personnelStatus" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'statusList')}">
			<c:if test = "${personnelStatus.id == personActionForm.status}">
				<c:out value="${personnelStatus.name}"/><br>
			</c:if>
		</c:forEach>
	</span><br>
      <br>
	<mifos:mifoslabel name="Personnel.Address"  bundle="PersonnelUIResources"></mifos:mifoslabel><br>
<%--	<mifos:mifoslabel name="Personnel.Address1"  bundle="PersonnelUIResources"></mifos:mifoslabel>--%>
	<span class="fontnormal"> 
		<c:out value="${personActionForm.address.displayAddress}"/>
      	</span><br>
	<mifos:mifoslabel name="${ConfigurationConstants.CITY}"  bundle="PersonnelUIResources"></mifos:mifoslabel>:
	<span class="fontnormal"> 
		<c:out value="${personActionForm.address.city}"/>
      </span>
      </td>
	</tr>
	
	<tr id="Personnel.State">
     <td width="100%" class="fontnormalbold">
	<mifos:mifoslabel name="${ConfigurationConstants.STATE}"  bundle="PersonnelUIResources" isColonRequired="yes" keyhm="Personnel.State" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
	<span class="fontnormal">
	 	<c:out value="${personActionForm.address.state}"/>
      	</span><br>
     </td>
	</tr>
	
	<tr id="Personnel.Country">
     <td width="100%" class="fontnormalbold">
	<mifos:mifoslabel name="Personnel.Country"  bundle="PersonnelUIResources" keyhm="Personnel.Country" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
	<span class="fontnormal">
	 	<c:out value="${personActionForm.address.country}"/>
       </span><br>
       
     </td>
	</tr>
	
	<tr id="Personnel.PostalCode">
     <td width="100%" class="fontnormalbold">
	<mifos:mifoslabel name="${ConfigurationConstants.POSTAL_CODE}" bundle="PersonnelUIResources" isColonRequired="yes" keyhm="Personnel.PostalCode" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>  
	<span class="fontnormal">
		<c:out value="${personActionForm.address.zip}"/><br>
      <br>
      	</span>

	</td>
	</tr>
	
	<tr id="Personnel.PhoneNumber">
     <td width="100%" class="fontnormalbold">	
	<mifos:mifoslabel name="Personnel.Telephone"  bundle="PersonnelUIResources" keyhm="Personnel.PhoneNumber" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
	<span class="fontnormal">
		<c:out value="${personActionForm.address.phoneNumber}"/> 
	</span><br>
	</td>
	</tr>
	<tr>
      <td width="100%" height="23" class="fontnormalbold">
      <br>
      <mifos:mifoslabel name="Personnel.OfficePermissions"  bundle="PersonnelUIResources"></mifos:mifoslabel><br>
      <mifos:mifoslabel name="Personnel.Office"  bundle="PersonnelUIResources"></mifos:mifoslabel> 
		<span class="fontnormal">
			<c:forEach var="office" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'officeList')}">
				<c:if test="${office.officeId == personActionForm.officeId}">
					<c:out value ="${office.officeName}"/>
				</c:if>
			</c:forEach>
		</span><br>
      	<mifos:mifoslabel name="Personnel.UserTitle"  bundle="PersonnelUIResources"></mifos:mifoslabel> 
		<span class="fontnormal">
			<c:forEach	items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'titleList')}"	var="item">
				<c:if test="${personActionForm.title == item.id}">
					<c:out value ="${item.name}"/>										
				</c:if>
			</c:forEach>
      	</span><br>
		<mifos:mifoslabel name="Personnel.UserHierarchy"  bundle="PersonnelUIResources"></mifos:mifoslabel>  
		<span class="fontnormal">
			<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'personnelLevelList')}"	var="item">
				<c:if test="${personActionForm.level == item.id}">
					<c:out value ="${item.name}"/>	
				</c:if>
			</c:forEach>
		</span><br>
      	<mifos:mifoslabel name="Personnel.Roles"  bundle="PersonnelUIResources"></mifos:mifoslabel> 
		<span class="fontnormal">
		<c:forEach	var="personnelRole" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'personnelRolesList')}" varStatus="loopStatus">
				<bean:define id="ctr">
					<c:out value="${loopStatus.index}" />
				</bean:define>
				<c:choose>
					<c:when test="${ctr == 0}">
					<c:out value="${personnelRole.name}" />
					</c:when>
					<c:otherwise>
					, <c:out value="${personnelRole.name}" />
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</span><br>
      <br>
      <mifos:mifoslabel name="Personnel.LoginInformation"  bundle="PersonnelUIResources"></mifos:mifoslabel> <br>
	  <mifos:mifoslabel name="Personnel.UserName"  bundle="PersonnelUIResources"></mifos:mifoslabel> 
		<span class="fontnormal">
		<c:out value="${BusinessKey.userName}"/> 
		</span><br>
      <br>
      <c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
     <mifos:mifoslabel name="Personnel.AdditionalInfo"  bundle="PersonnelUIResources"></mifos:mifoslabel> 
	  <br>
		<c:forEach var="cfdef" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
		<c:forEach var="cf"	items="${personActionForm.customFields}">
			<c:if test="${cfdef.fieldId==cf.fieldId}">
			<mifos:mifoslabel name="${cfdef.lookUpEntity.entityType}" bundle="PersonnelUIResources"></mifos:mifoslabel>:
			<span class="fontnormal"> <c:out value="${cf.fieldValue}" /></span>
			<br>
			</c:if>
		</c:forEach>
	</c:forEach> 
	<br>
	</c:if>
	<span class="fontnormal">
      <br>
	   <html-el:button styleId="preview_EditUser.button.edit" property="btn" styleClass="insidebuttn" onclick="goToEditPage()">
		 <mifos:mifoslabel name="button.EditUserInformation"  bundle="PersonnelUIResources"></mifos:mifoslabel>
       </html-el:button>

      </span>
	</td>
              </tr>
            </table>
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center" class="blueline">&nbsp;                </td>
              </tr>
            </table>            <br>
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center">	
	                <html-el:submit styleId="preview_EditUser.button.submit" property="submitBtn" styleClass="buttn">
	                  	<mifos:mifoslabel name="button.submit" bundle="PersonnelUIResources"></mifos:mifoslabel>
                  	</html-el:submit>
&nbsp;
                  	<html-el:button styleId="preview_EditUser.button.cancel" property="cancelBtn"  styleClass="cancelbuttn" onclick="goToCancelPage()">
	                    <mifos:mifoslabel name="button.cancel" bundle="PersonnelUIResources"></mifos:mifoslabel>
                  	</html-el:button>  
        	</td>
              </tr>
            </table>
            </td>
          </tr>
      </table>
      <br>

</html-el:form>
</tiles:put>
</tiles:insert>
