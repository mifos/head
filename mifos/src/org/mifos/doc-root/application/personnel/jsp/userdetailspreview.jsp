<!-- /**
 
 * userdetailspreview.jsp    version: 1.0
 
 
 
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
 
 */-->
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@ taglib uri="/mifos/customtags" prefix = "mifoscustom"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/personnel/personnelfunctions" prefix="personnelfn"%>

<tiles:insert definition=".view">
 <tiles:put name="body" type="string">
 <SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
 <script language="javascript">
  function goToEditPage(){
	personnelActionForm.action="PersonnelAction.do?method=previous";
	personnelActionForm.submit();
  }
  function goToCancelPage(){
	personnelActionForm.action="PersonnelAction.do?method=cancel";
	personnelActionForm.submit();
  }
</script>
<html-el:form action="PersonnelAction.do?method=update" onsubmit="func_disableSubmitBtn('submitBtn')">


   <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bluetablehead05">
          <span class="fontnormal8pt"><a href="AdminAction.do?method=load">
	           <mifos:mifoslabel name="Personnel.Admin" bundle="PersonnelUIResources"></mifos:mifoslabel>             	
           	</a>
		 / 
			<a href="PersonnelAction.do?method=loadSearch">
			<mifos:mifoslabel name="Personnel.ViewUsers" bundle="PersonnelUIResources"></mifos:mifoslabel>
			</a> / 
			<a href="PersonnelAction.do?method=get&globalPersonnelNum=<c:out value="${requestScope.PersonnelVO.globalPersonnelNum}"/>">
	           <c:out value="${requestScope.PersonnelVO.displayName}"/>            	
           	</a>
          </span></td>
        </tr>
      </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td align="left" valign="top" class="paddingL15T15"><table width="95%" border="0" cellpadding="3" cellspacing="0">
            <tr>
              <td width="63%" class="headingorange"><span class="heading">
		<c:out value="${requestScope.PersonnelVO.displayName}"/> - 
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
   					<html-el:errors bundle="PersonnelUIResources"/>
   				</font>
				</td>
			</tr>
              <tr>
                <td width="100%" class="fontnormalbold">
					<mifos:mifoslabel name="Personnel.FirstName" bundle="PersonnelUIResources"></mifos:mifoslabel> 
					<span class="fontnormal">
						<c:out value="${requestScope.PersonnelVO.personnelDetails.firstName}"/> 
					</span>
				</td>
			</tr>
			
		<tr id="Personnel.MiddleName">
    		 <td width="100%" class="fontnormalbold">
				<mifos:mifoslabel name="Personnel.MiddleName" bundle="PersonnelUIResources" keyhm="Personnel.MiddleName" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
				<span class="fontnormal">
					<c:out value="${requestScope.PersonnelVO.personnelDetails.middleName}"/>
				</span><br>	
			</td>
	   </tr>
	
	 <tr id="Personnel.SecondLastName">
     <td width="100%" class="fontnormalbold">
     <mifos:mifoslabel name="Personnel.SecondLastName" bundle="PersonnelUIResources" keyhm="Personnel.SecondLastName" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
		<span class="fontnormal">
			<c:out value="${requestScope.PersonnelVO.personnelDetails.secondLastName}"/>
		</span><br>
	</td>
	</tr>
	
	 <tr>
      <td width="100%" class="fontnormalbold">
    <mifos:mifoslabel name="Personnel.LastName" bundle="PersonnelUIResources"></mifos:mifoslabel>
	<span class="fontnormal"> 
		<c:out value="${requestScope.PersonnelVO.personnelDetails.lastName}"/> 
	</span> <br>
	</td>
	</tr>
	
	<tr id="Personnel.GovernmentId">
     <td width="100%" class="fontnormalbold">
      	<mifos:mifoslabel name="${ConfigurationConstants.GOVERNMENT_ID}" bundle="PersonnelUIResources" isColonRequired="yes" keyhm="Personnel.GovernmentId" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> 
	<span class="fontnormal">
		<c:out value="${requestScope.PersonnelVO.personnelDetails.governmentIdNumber}"/> 
	</span> <br>
	</td>
	</tr>
	<tr>
      <td width="100%" height="23" class="fontnormalbold">
      	<mifos:mifoslabel name="Personnel.Email" bundle="PersonnelUIResources"></mifos:mifoslabel> 
	<span class="fontnormal">
		<c:out value="${requestScope.PersonnelVO.emailId}"/>
	</span> <br>
	<mifos:mifoslabel name="Personnel.DOB" bundle="PersonnelUIResources"></mifos:mifoslabel>
    <span class="fontnormal">
		<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.PersonnelVO.dob)}" />
	</span><br>
    <mifos:mifoslabel name="Personnel.Age" bundle="PersonnelUIResources"></mifos:mifoslabel>
     <span class="fontnormal">
		<c:out value="${sessionScope.personnelAge}"/> 
	 </span><br> 	
	<mifos:mifoslabel name="Personnel.MaritalStatus" bundle="PersonnelUIResources"></mifos:mifoslabel>
	<span class="fontnormal">
	 	<mifoscustom:lookUpValue id="${requestScope.PersonnelVO.personnelDetails.maritalStatus}" searchResultName="maritalStatusList"></mifoscustom:lookUpValue> 
	</span> <br>
      	<mifos:mifoslabel name="Personnel.Gender" bundle="PersonnelUIResources"></mifos:mifoslabel> 
	<span class="fontnormal">
		<mifoscustom:lookUpValue id="${requestScope.PersonnelVO.personnelDetails.gender}" searchResultName="genderList"></mifoscustom:lookUpValue>
	</span> <br>
      	<mifos:mifoslabel name="Personnel.LanguagePreferred"  bundle="PersonnelUIResources"></mifos:mifoslabel> 
	<span class="fontnormal">
		<c:out value="${requestScope.languageName}"/> 
	</span> <br>
      	<mifos:mifoslabel name="Personnel.DOJMFI"  bundle="PersonnelUIResources"></mifos:mifoslabel> 
	<span class="fontnormal">
		<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.PersonnelVO.dateOfJoiningMFI)}" />

	</span><br>
      <mifos:mifoslabel name="Personnel.Status"  bundle="PersonnelUIResources"></mifos:mifoslabel>: 
	<span class="fontnormal">
		<mifoscustom:lookUpValue id="${requestScope.PersonnelVO.personnelStatus}" searchResultName="statusList" mapToSeperateMasterTable="true"></mifoscustom:lookUpValue>
	</span><br>
      <br>
	<mifos:mifoslabel name="Personnel.Address"  bundle="PersonnelUIResources"></mifos:mifoslabel><br>
<%--	<mifos:mifoslabel name="Personnel.Address1"  bundle="PersonnelUIResources"></mifos:mifoslabel>--%>
	<span class="fontnormal"> 
		<c:out value="${requestScope.displayAddress}"/>
      	</span><br>
	<mifos:mifoslabel name="${ConfigurationConstants.CITY}"  bundle="PersonnelUIResources"></mifos:mifoslabel>:
	<span class="fontnormal"> 
		<c:out value="${requestScope.PersonnelVO.personnelDetails.city}"/>
      </span>
      </td>
	</tr>
	
	<tr id="Personnel.State">
     <td width="100%" class="fontnormalbold">
	<mifos:mifoslabel name="${ConfigurationConstants.STATE}"  bundle="PersonnelUIResources" isColonRequired="yes" keyhm="Personnel.State" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
	<span class="fontnormal">
	 	<c:out value="${requestScope.PersonnelVO.personnelDetails.state}"/>
      	</span><br>
     </td>
	</tr>
	
	<tr id="Personnel.Country">
     <td width="100%" class="fontnormalbold">
	<mifos:mifoslabel name="Personnel.Country"  bundle="PersonnelUIResources" keyhm="Personnel.Country" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
	<span class="fontnormal">
	 	<c:out value="${requestScope.PersonnelVO.personnelDetails.country}"/>
       </span><br>
       
     </td>
	</tr>
	
	<tr id="Personnel.PostalCode">
     <td width="100%" class="fontnormalbold">
	<mifos:mifoslabel name="${ConfigurationConstants.POSTAL_CODE}" bundle="PersonnelUIResources" isColonRequired="yes" keyhm="Personnel.PostalCode" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>  
	<span class="fontnormal">
		<c:out value="${requestScope.PersonnelVO.personnelDetails.postalCode}"/><br>
      <br>
      	</span>

	</td>
	</tr>
	
	<tr id="Personnel.PhoneNumber">
     <td width="100%" class="fontnormalbold">	
	<mifos:mifoslabel name="Personnel.Telephone"  bundle="PersonnelUIResources" keyhm="Personnel.PhoneNumber" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
	<span class="fontnormal">
		<c:out value="${requestScope.PersonnelVO.personnelDetails.telephone}"/> 
	</span><br>
	</td>
	</tr>
	<tr>
      <td width="100%" height="23" class="fontnormalbold">
      <br>
      <mifos:mifoslabel name="Personnel.OfficePermissions"  bundle="PersonnelUIResources"></mifos:mifoslabel><br>
      <mifos:mifoslabel name="Personnel.Office"  bundle="PersonnelUIResources"></mifos:mifoslabel> 
	<span class="fontnormal">
	
		<c:forEach var="office" items="${requestScope.officeList}">
			<c:if test="${office.officeId == sessionScope.personnelActionForm.office.officeId}">
				<c:out value ="${office.officeName}"/>
			</c:if>
		</c:forEach>
	</span><br>
      	<mifos:mifoslabel name="Personnel.UserTitle"  bundle="PersonnelUIResources"></mifos:mifoslabel> 
	<span class="fontnormal">
	<mifoscustom:lookUpValue id="${requestScope.PersonnelVO.title}" searchResultName="titleList"></mifoscustom:lookUpValue><br>
      	</span>
	<mifos:mifoslabel name="Personnel.UserHierarchy"  bundle="PersonnelUIResources"></mifos:mifoslabel>  
	<span class="fontnormal">
		<mifoscustom:lookUpValue id="${requestScope.PersonnelVO.level.levelId}" searchResultName="personnelLevelList" mapToSeperateMasterTable="true"></mifoscustom:lookUpValue>
	</span><br>
      	<mifos:mifoslabel name="Personnel.Roles"  bundle="PersonnelUIResources"></mifos:mifoslabel> 
	<span class="fontnormal">
<%--		<c:forEach var="role" items="${requestScope.personnelRolesList}" varStatus="loopStatus">
			<bean:define id="ctr">
              	<c:out value="${loopStatus.index}"/>
            </bean:define>
            <c:choose>
	            <c:when test="${ctr == 0}">
    	        	<c:out value="${role.name}"/>
        	    </c:when>
				<c:otherwise>
				  , <c:out value="${role.name}"/>
				</c:otherwise>
			</c:choose>
		</c:forEach>--%>
		<c:out value="${personnelfn:getRoleNamesFromList(requestScope.personnelRolesList)}" />
	</span><br>
      <br>
      <mifos:mifoslabel name="Personnel.LoginInformation"  bundle="PersonnelUIResources"></mifos:mifoslabel> <br>
	<mifos:mifoslabel name="Personnel.UserName"  bundle="PersonnelUIResources"></mifos:mifoslabel> 
	<span class="fontnormal">
		<c:out value="${requestScope.PersonnelVO.userName}"/> 
	</span><br>
      <br>
      	<c:if test="${!empty requestScope.customFields}">
      	<mifos:mifoslabel name="Personnel.AdditionalInfo"  bundle="PersonnelUIResources"></mifos:mifoslabel> 
	<br>
	</c:if>

	<c:forEach var="cfdef" items="${requestScope.customFields}">
		<c:forEach var="cf" items="${requestScope.PersonnelVO.customFieldSet}">
			<c:if test="${cfdef.fieldId==cf.fieldId}">
			<mifos:mifoslabel name="${cfdef.lookUpEntity.entityType}" bundle="PersonnelUIResources"></mifos:mifoslabel>:
			<!-- 	<mifos:mifoslabel name="cfdef.entityId" bundle="PersonnelUIResources"></mifos:mifoslabel>:-->
	          	 	<span class="fontnormal">
					<c:out value="${cf.fieldValue}"/>
				</span><br>
			</c:if>
		</c:forEach>
  	</c:forEach>

	<span class="fontnormal"><br>
      <br>
	   <html-el:button property="btn" styleClass="insidebuttn" style="width:130px;" onclick="goToEditPage()">
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
	                <html-el:submit property="submitBtn" styleClass="buttn" style="width:70px;">
	                  	<mifos:mifoslabel name="button.submit" bundle="PersonnelUIResources"></mifos:mifoslabel>
                  	</html-el:submit>
&nbsp;
                  	<html-el:button property="cancelBtn"  styleClass="cancelbuttn" style="width:70px" onclick="goToCancelPage()">
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
