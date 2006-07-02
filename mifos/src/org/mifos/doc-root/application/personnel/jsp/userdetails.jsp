<!-- /**
 
 * userdetails.jsp    version: 1.0
 
 
 
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
 
<script language="javascript">

  function goToCancelPage(){
	personnelActionForm.action="PersonnelAction.do?method=cancel";
	personnelActionForm.submit();
  }
  function viewChangeLog(){
	  personnelActionForm.action="PersonnelAction.do?method=search&input=UserChangeLog";
	  personnelActionForm.submit();
  }
  function AddNote(){
	personnelNotesActionForm.action="PersonnelNotesAction.do?method=load";
	personnelNotesActionForm.submit();
  }
 function SeeAllNotes(){
	personnelNotesActionForm.action="PersonnelNotesAction.do?method=get";
	personnelNotesActionForm.submit();
  }
</script>
<html-el:form action="PersonnelAction.do?method=preview">

   <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td class="bluetablehead05">
            <span class="fontnormal8pt">
            	<a href="AdminAction.do?method=load">
	           		<mifos:mifoslabel name="Personnel.Admin" bundle="PersonnelUIResources"></mifos:mifoslabel>             	
           		</a>	/ 
				<a href="PersonnelAction.do?method=loadSearch">
					<mifos:mifoslabel name="Personnel.ViewUsers" bundle="PersonnelUIResources"></mifos:mifoslabel>
				</a> /
             </span>
             <span class="fontnormal8ptbold"><c:out value="${requestScope.PersonnelVO.displayName}"/></span></td>
          </tr>
        </table>
        <table width="95%" border="0" cellpadding="0" cellspacing="0">
        
            <td width="70%" align="left" valign="top" class="paddingL15T15">
            <table width="96%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td width="50%" height="23" class="headingorange">
			<c:out value="${requestScope.PersonnelVO.displayName}"/>
			</td>
                <td width="50%" align="right">
		<a href="PersonnelAction.do?method=manage">
		<mifos:mifoslabel name="Personnel.EditUserInformation" bundle="PersonnelUIResources"></mifos:mifoslabel>
		</a></td>
              </tr>
              <tr>
   				<td colspan="2">
   				<font class="fontnormalRedBold">
   					<html-el:errors bundle="PersonnelUIResources"/>
   				</font>
				</td>
			</tr>
              <tr>
                <td height="23" class="fontnormalbold">      
		<span class="fontnormal">
	          <c:choose>
				<%-- Active State --%>
				<c:when test="${requestScope.PersonnelVO.personnelStatus == 1}">
					<mifos:MifosImage id="active" moduleName="personnel"/>
				</c:when>
				<c:when test="${requestScope.PersonnelVO.personnelStatus == 2}">
					<mifos:MifosImage id="inactive" moduleName="personnel"/>
				</c:when>
				<c:otherwise>
				</c:otherwise>
			 </c:choose>
								<c:out value="${requestScope.currentStatus}"/>
		</span> 
		<c:if test="${requestScope.PersonnelVO.locked == 1}">
			<span class="fontnormalRed">
				<mifos:mifoslabel name="Personnel.Locked" bundle="PersonnelUIResources"></mifos:mifoslabel>
			</span>
		</c:if>
		 &nbsp;<br>
		 </td>
		 </tr>
		 
		 <tr id="Personnel.GovernmentId">
                <td class="fontnormalbold">
                  <span class="fontnormal">
		<mifos:mifoslabel name="${ConfigurationConstants.GOVERNMENT_ID}" bundle="PersonnelUIResources" keyhm="Personnel.GovernmentId" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> 
		 <c:out value="${requestScope.PersonnelVO.personnelDetails.governmentIdNumber}"/>  <br>
		</td>
		</tr>
		
		<tr>
         <td class="fontnormalbold">
         <span class="fontnormal">
      		<mifos:mifoslabel name="Personnel.Email" bundle="PersonnelUIResources"></mifos:mifoslabel> 
		<c:out value="${requestScope.PersonnelVO.emailId}"/> <br>
      
		<mifos:mifoslabel name="Personnel.DOB" bundle="PersonnelUIResources"></mifos:mifoslabel> 
			<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.PersonnelVO.personnelDetails.dob)}" />; <c:out value="${sessionScope.personnelAge}"/>  
			<mifos:mifoslabel name="Personnel.YearsOld" bundle="PersonnelUIResources"></mifos:mifoslabel> <br>
			
     	<mifoscustom:lookUpValue id="${requestScope.PersonnelVO.personnelDetails.gender}" searchResultName="genderList"></mifoscustom:lookUpValue><c:if test="${!empty requestScope.PersonnelVO.personnelDetails.maritalStatus}"><c:out value=";"/>
      	<mifoscustom:lookUpValue id="${requestScope.PersonnelVO.personnelDetails.maritalStatus}" searchResultName="maritalStatusList"></mifoscustom:lookUpValue>
      	</c:if>
      		<br>	
      	<mifos:mifoslabel name="Personnel.LanguagePreferred"  bundle="PersonnelUIResources"></mifos:mifoslabel> 
      	<c:if test="${!empty requestScope.PersonnelVO.preferredLocale}">
			<c:out value ="${requestScope.PersonnelVO.preferredLocale.language.languageName}"/>  
		</c:if>
		<br>
     	 <mifos:mifoslabel name="Personnel.DOJMFI"  bundle="PersonnelUIResources"></mifos:mifoslabel> 
     	 <c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.PersonnelVO.personnelDetails.dateOfJoiningMFI)}" />
		<%--<c:out value ="${requestScope.PersonnelVO.personnelDetails.dateOfJoiningMFI}"/>--%><br>
      		<mifos:mifoslabel name="Personnel.DOJBranch"  bundle="PersonnelUIResources"></mifos:mifoslabel> 
 		<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.PersonnelVO.personnelDetails.dateOfJoiningBranch)}" />
		<%--<c:out value ="${requestScope.PersonnelVO.personnelDetails.dateOfJoiningBranch}"/>--%></span><br>
      <br>
      <mifos:mifoslabel name="Personnel.Address"  bundle="PersonnelUIResources"></mifos:mifoslabel><br>
	<span class="fontnormal">
		<c:if test="${!empty requestScope.displayAddress}">
			<c:out value="${requestScope.displayAddress}"/><br>
		</c:if>
       	<c:if test="${!empty requestScope.PersonnelVO.personnelDetails.city}">
			<c:out value="${requestScope.PersonnelVO.personnelDetails.city}"/><br>
		</c:if>
      		<c:if test="${!empty requestScope.PersonnelVO.personnelDetails.state}">
			<c:out value="${requestScope.PersonnelVO.personnelDetails.state}"/><br>
		</c:if>
		<c:if test="${!empty requestScope.PersonnelVO.personnelDetails.country}">
			<c:out value="${requestScope.PersonnelVO.personnelDetails.country}"/><br>
		</c:if>
		<c:if test="${!empty requestScope.PersonnelVO.personnelDetails.postalCode}">
			<c:out value="${requestScope.PersonnelVO.personnelDetails.postalCode}"/><br>
		</c:if>
      <br></span>
	<c:if test="${!empty requestScope.PersonnelVO.personnelDetails.telephone}">
		<span class="fontnormal">
	      <mifos:mifoslabel name="Personnel.Telephone"  bundle="PersonnelUIResources" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
			<c:out value="${requestScope.PersonnelVO.personnelDetails.telephone}"/> 
		</span><br>
      	<br>
	</c:if>
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td width="71%" class="fontnormalbold">
		<mifos:mifoslabel name="Personnel.OfficePermissions"  bundle="PersonnelUIResources"></mifos:mifoslabel>
		<br>
            <span class="fontnormal">
		<mifos:mifoslabel name="Personnel.Office"  bundle="PersonnelUIResources"></mifos:mifoslabel> 
			<c:out value="${requestScope.PersonnelVO.office.officeName}"/>  <br>
		<mifos:mifoslabel name="Personnel.UserTitle"  bundle="PersonnelUIResources"></mifos:mifoslabel> 
			<mifoscustom:lookUpValue id="${requestScope.PersonnelVO.title}" searchResultName="titleList"></mifoscustom:lookUpValue><br>
		<mifos:mifoslabel name="Personnel.UserHierarchy"  bundle="PersonnelUIResources"></mifos:mifoslabel> 
			<mifoscustom:lookUpValue id="${requestScope.PersonnelVO.level.levelId}" searchResultName="personnelLevelList" mapToSeperateMasterTable="true"></mifoscustom:lookUpValue><br>
		<mifos:mifoslabel name="Personnel.Roles"  bundle="PersonnelUIResources"></mifos:mifoslabel>  
		<%--<c:forEach var="personnelRole" items="${requestScope.PersonnelVO.personnelRolesSet}" varStatus="loopStatus">
			<bean:define id="ctr">
              	<c:out value="${loopStatus.index}"/>
            </bean:define>
            <c:choose>
	            <c:when test="${ctr == 0}">
    	        	<c:out value="${personnelRole.role.name}"/>
        	    </c:when>
				<c:otherwise>
					 , <c:out value="${personnelRole.role.name}"/>
				</c:otherwise>
			</c:choose>
			</c:forEach>--%>
			<c:out value="${personnelfn:getRoleNamesFromSet(requestScope.PersonnelVO.personnelRolesSet)}" />
			</span></td>
          </tr>
      </table>      
      <br>
      <mifos:mifoslabel name="Personnel.LoginInformation"  bundle="PersonnelUIResources"></mifos:mifoslabel><br>
      <span class="fontnormal">
		<mifos:mifoslabel name="Personnel.UserName"  bundle="PersonnelUIResources"></mifos:mifoslabel> 
			<c:out value="${requestScope.PersonnelVO.userName}"/>
     </span><br>
      <br>
      <c:if test="${!empty requestScope.customFields}">
      <mifos:mifoslabel name="Personnel.AdditionalInfo"  bundle="PersonnelUIResources"></mifos:mifoslabel><br>
      </c:if>

	<c:forEach var="cfdef" items="${requestScope.customFields}">
		<c:forEach var="cf" items="${requestScope.PersonnelVO.customFieldSet}">
			<c:if test="${cfdef.fieldId==cf.fieldId}">
				<span class="fontnormal">
				<mifos:mifoslabel name="${cfdef.lookUpEntity.entityType}" bundle="PersonnelUIResources"></mifos:mifoslabel>:
				<!-- 	<mifos:mifoslabel name="cfdef.entityId" bundle="PersonnelUIResources"></mifos:mifoslabel>:-->
					 <c:out value="${cf.fieldValue}"/>
				</span><br>
			</c:if>
		</c:forEach>
  	</c:forEach>
  	<br>
  	<span class="fontnormal">
      <html-el:link href="javascript:viewChangeLog()">
		<mifos:mifoslabel name="Personnel.ViewChangeLog"  bundle="PersonnelUIResources"></mifos:mifoslabel>
	 </html-el:link>
	 </span>     </td>
        <td height="23" align="right" valign="top" class="fontnormal">
        <c:if test="${requestScope.PersonnelVO.locked == 1}">
			<a href="PersonnelAction.do?method=loadUnLockUser">
				<mifos:mifoslabel name="Personnel.UnlockUser"  bundle="PersonnelUIResources"></mifos:mifoslabel>
			</a>
		</c:if>
		</td>
           </tr>
            </table>              
            <br></td>
            <td width="30%" align="left" valign="top" class="paddingleft1">
	<table width="100%" border="0" cellpadding="2" cellspacing="0" class="bluetableborder">
              <tr>
                <td class="bluetablehead05">
			<span class="fontnormalbold">
				<mifos:mifoslabel name="Personnel.RecentNotes"  bundle="PersonnelUIResources"></mifos:mifoslabel>
			</span></td>
              </tr>
              <tr>
                <td class="paddingL10"><img src="images/trans.gif" width="10" height="2"></td>
              </tr>
              <tr>
                <td class="paddingL10">
			<c:choose>
              <c:when test="${!empty requestScope.notes}">
		 	    	<c:forEach var="note" items="${requestScope.notes}">
                    	<span class="fontnormal8ptbold"> 
                    		<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,note.commentDate)}"/>:
                    	</span>
                    	<span class="fontnormal8pt"> 
                    		<c:out value="${note.comment}"/>
                    	 -<em><c:out value="${note.officer.displayName}"/></em><br><br>
                      	</span>
             </c:forEach>
             </c:when>
             <c:otherwise>
	             <span class="fontnormal"> 
	                  	 <mifos:mifoslabel name="Personnel.NoNotesAvailable" bundle="PersonnelUIResources"/>
	             </span>
             </c:otherwise>
             </c:choose>
		</td>

              </tr>
              <tr>
                <td align="right" class="paddingleft05"><span class="fontnormal8pt">
		<c:if test="${!empty requestScope.notes}">
		    <a href="javascript:SeeAllNotes()">
			<mifos:mifoslabel name="Personnel.SeeAllNotes"  bundle="PersonnelUIResources"></mifos:mifoslabel>
		    </a><br>
		</c:if>
                <a href="javascript:AddNote()">
			<mifos:mifoslabel name="Personnel.AddNote"  bundle="PersonnelUIResources"></mifos:mifoslabel>
		</a>
		</span>
		</td>
              </tr>
            </table>            
              <p class="paddingleft1">&nbsp;</p></td>
         
        </table>        
        <br>

</html-el:form>
<html-el:form action="PersonnelNotesAction.do?method=load">
<html-el:hidden property="officeName" value="${requestScope.PersonnelVO.office.officeName}"/>
<html-el:hidden property="officeId" value="${requestScope.PersonnelVO.office.officeId}"/>
<html-el:hidden property="personnelId" value="${requestScope.PersonnelVO.personnelId}"/>
<html-el:hidden property="globalPersonnelNum" value="${requestScope.PersonnelVO.globalPersonnelNum}"/>
<html-el:hidden property="personnelName" value="${requestScope.PersonnelVO.displayName}"/>
</html-el:form>
</tiles:put>
</tiles:insert>
