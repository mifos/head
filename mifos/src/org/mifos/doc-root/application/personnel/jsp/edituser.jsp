<!-- /**

 * edituser.jsp    version: 1.0

 

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
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<tiles:insert definition=".view">
 <tiles:put name="body" type="string">
<script language="javascript">

  function goToCancelPage(){
	personnelActionForm.action="PersonnelAction.do?method=cancel";
	personnelActionForm.submit();
  }
 
</script>
<SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>
<html-el:form action="PersonnelAction.do?method=preview" onsubmit="return (validateMyForm(dateOfJoiningMFI,dateOfJoiningMFIFormat,dateOfJoiningMFIYY))">

   <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bluetablehead05">
          <span class="fontnormal8pt">
          <a href="AdminAction.do?method=load">
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
              <td class="headingorange"><span class="heading">
              <c:out value="${requestScope.PersonnelVO.displayName}"/>   - </span>
		<mifos:mifoslabel name="Personnel.EditUserInformation" bundle="PersonnelUIResources"></mifos:mifoslabel>
		</td>
            </tr>
            <tr>
              <td class="fontnormal">
		<mifos:mifoslabel name="Personnel.CompleteFields" bundle="PersonnelUIResources"></mifos:mifoslabel> 
		<mifos:mifoslabel name="Personnel.ClickPreview" bundle="PersonnelUIResources"></mifos:mifoslabel> 
		<mifos:mifoslabel name="Personnel.ClickCancelToDetailsPage" bundle="PersonnelUIResources"></mifos:mifoslabel>
		<br>
                <span class="mandatorytext"><font color="#FF0000">*</font></span>
		<mifos:mifoslabel name="Personnel.FieldsMandatory" bundle="PersonnelUIResources"></mifos:mifoslabel> 	
	     </td>
            </tr>
          </table>
            
            <table width="95%" border="0" cellpadding="3" cellspacing="0">
                <tr>
   				<td colspan="2">
   				<font class="fontnormalRedBold">
   					<html-el:errors bundle="PersonnelUIResources"/>
   				</font>
				</td>
		     	</tr>
            
              <tr class="fontnormal">
                <td colspan="2" class="fontnormalbold">
		<mifos:mifoslabel name="Personnel.UserDetails" bundle="PersonnelUIResources"></mifos:mifoslabel>
		<br>
                    <br>
                </td>
              </tr>
		
		<tr class="fontnormal">
                <td width="22%" height="28" align="right">
			<mifos:mifoslabel name="Personnel.FirstName" bundle="PersonnelUIResources" mandatory="yes"></mifos:mifoslabel>
		</td>
                <td width="78%">
                	<mifos:mifosalphanumtext name="personnelActionForm" property="personnelDetails.firstName" value="${requestScope.PersonnelVO.personnelDetails.firstName}"/>
                </td>
              </tr>
              
	            <tr class="fontnormal">
                <td align="right">
					<mifos:mifoslabel name="Personnel.MiddleName" bundle="PersonnelUIResources" keyhm="Personnel.MiddleName"></mifos:mifoslabel>
				</td>
                <td>
                	<mifos:mifosalphanumtext name="personnelActionForm" property="personnelDetails.middleName" value="${requestScope.PersonnelVO.personnelDetails.middleName}" keyhm="Personnel.MiddleName"/>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right">
			<mifos:mifoslabel keyhm="Personnel.SecondLastName" name="Personnel.SecondLastName" bundle="PersonnelUIResources"></mifos:mifoslabel>
		</td>
                <td>
	             	<mifos:mifosalphanumtext keyhm="Personnel.SecondLastName" name="personnelActionForm" property="personnelDetails.secondLastName" value="${requestScope.PersonnelVO.personnelDetails.secondLastName}"/>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right">
			<mifos:mifoslabel name="Personnel.LastName" mandatory="yes" bundle="PersonnelUIResources"></mifos:mifoslabel>
		</td>
                <td>  
                	<mifos:mifosalphanumtext name="personnelActionForm" property="personnelDetails.lastName" value="${requestScope.PersonnelVO.personnelDetails.lastName}"/>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right">
			<mifos:mifoslabel keyhm="Personnel.GovernmentId" isColonRequired="yes" name="${ConfigurationConstants.GOVERNMENT_ID}"  bundle="PersonnelUIResources"></mifos:mifoslabel>
		</td>
                <td>  
                	<c:out value="${requestScope.PersonnelVO.personnelDetails.governmentIdNumber}"/>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right">
			<mifos:mifoslabel name="Personnel.Email" bundle="PersonnelUIResources"></mifos:mifoslabel>
		</td>
                <td>
                	<mifos:mifosalphanumtext  property="emailId" value="${requestScope.PersonnelVO.emailId}"/>
                </td>
              </tr>
			<tr class="fontnormal">
                <td align="right">
				<mifos:mifoslabel name="Personnel.DOB"  bundle="PersonnelUIResources"></mifos:mifoslabel>
				</td>
                <td> <c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.PersonnelVO.dob)}" />
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right">
			<mifos:mifoslabel name="Personnel.MaritalStatus" bundle="PersonnelUIResources"></mifos:mifoslabel>
		</td>
                <c:set var="maritalStatusCol" scope="request" value="${requestScope.maritalStatusList.lookUpMaster}"/> 
                <td><mifos:select name="personnelActionForm" property="personnelDetails.maritalStatus" value="${requestScope.PersonnelVO.personnelDetails.maritalStatus}">
	                	<html-el:options collection="maritalStatusCol" property="lookUpId" labelProperty="lookUpValue"></html-el:options> 
                	</mifos:select>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right">
			<mifos:mifoslabel name="Personnel.Gender" mandatory="yes" bundle="PersonnelUIResources"></mifos:mifoslabel>
		</td>
                <c:set var="gendercol" scope="request" value="${requestScope.genderList.lookUpMaster}"/> 
                <td>
                	<mifos:select name="personnelActionForm" property="personnelDetails.gender" value="${requestScope.PersonnelVO.personnelDetails.gender}">
	                	<html-el:options collection="gendercol" property="lookUpId" labelProperty="lookUpValue"></html-el:options> 
                	</mifos:select>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right">
			<mifos:mifoslabel name="Personnel.LanguagePreferred"  bundle="PersonnelUIResources"></mifos:mifoslabel>
		</td>
               <td>
               		 <mifos:select name="personnelActionForm" property="preferredLocale.localeId" value="${requestScope.PersonnelVO.preferredLocale.localeId}">
	                	<html-el:options collection="languageList" property="localeId" labelProperty="language.languageName"></html-el:options> 
                	</mifos:select> 
                </td>
              </tr>


              <tr class="fontnormal">
                <td align="right">
			<mifos:mifoslabel name="Personnel.DOJMFI"  bundle="PersonnelUIResources"></mifos:mifoslabel>
		</td>
                <td><date:datetag property="dateOfJoiningMFI" name="PersonnelVO"/></td>
              </tr>
            <br>
            <table width="95%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td colspan="2" class="fontnormalbold">
			<mifos:mifoslabel name="Personnel.Status"  bundle="PersonnelUIResources"></mifos:mifoslabel>
			<br>
                    <br>
                </td>
              </tr>
              <tr class="fontnormal">
                <td width="22%" align="right">
		<mifos:mifoslabel name="Personnel.ChangeStatus"  bundle="PersonnelUIResources"></mifos:mifoslabel>
		</td>
                <td width="78%">
				<c:set var="statusCol" scope="request" value="${requestScope.statusList.lookUpMaster}"/> 
                 <html-el:select name="personnelActionForm" property="personnelStatus" value="${requestScope.PersonnelVO.personnelStatus}">
	                	<html-el:options collection="statusCol" property="id" labelProperty="lookUpValue"></html-el:options> 
                </html-el:select>
                </td>
              </tr>
            </table>            <br>

            <table width="95%" border="0" cellpadding="3" cellspacing="0">
		<tr>
                <td colspan="2" class="fontnormalbold">
		 	<mifos:mifoslabel name="Personnel.Address"  bundle="PersonnelUIResources"></mifos:mifoslabel>
		<br>
                    <br>
                </td>
              </tr>
              <tr class="fontnormal">
                <td width="22%" align="right">
			<mifos:mifoslabel name="${ConfigurationConstants.ADDRESS1}"  bundle="PersonnelUIResources"></mifos:mifoslabel>:
		</td>
                <td width="78%">
	                <mifos:mifosalphanumtext name="personnelActionForm"  property="personnelDetails.address1" value="${requestScope.PersonnelVO.personnelDetails.address1}"/>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right">
			<mifos:mifoslabel name="${ConfigurationConstants.ADDRESS2}"  bundle="PersonnelUIResources"></mifos:mifoslabel>:
		</td>
                <td>
		            <mifos:mifosalphanumtext name="personnelActionForm" property="personnelDetails.address2" value="${requestScope.PersonnelVO.personnelDetails.address2}"/>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right">
			<mifos:mifoslabel keyhm="Personnel.Address3" isColonRequired="yes" name="${ConfigurationConstants.ADDRESS3}"  bundle="PersonnelUIResources"></mifos:mifoslabel>
		</td>
                <td>
		            <mifos:mifosalphanumtext keyhm="Personnel.Address3" name="personnelActionForm" property="personnelDetails.address3" value="${requestScope.PersonnelVO.personnelDetails.address3}"/>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right">
			<mifos:mifoslabel name="${ConfigurationConstants.CITY}"  bundle="PersonnelUIResources"></mifos:mifoslabel>:
		</td>
                <td>
                    <mifos:mifosalphanumtext name="personnelActionForm" property="personnelDetails.city" value="${requestScope.PersonnelVO.personnelDetails.city}"/>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right">
			<mifos:mifoslabel name="${ConfigurationConstants.STATE}"  bundle="PersonnelUIResources" keyhm="Personnel.State" isColonRequired="yes"></mifos:mifoslabel>
		</td>
                <td>
		            <mifos:mifosalphanumtext name="personnelActionForm" property="personnelDetails.state" value="${requestScope.PersonnelVO.personnelDetails.state}" keyhm="Personnel.State"/>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right">
			<mifos:mifoslabel name="Personnel.Country"  bundle="PersonnelUIResources" keyhm="Personnel.Country"></mifos:mifoslabel>		</td>
                <td>	
                    <mifos:mifosalphanumtext name="personnelActionForm" property="personnelDetails.country" value="${requestScope.PersonnelVO.personnelDetails.country}" keyhm="Personnel.Country"/>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right">
			<mifos:mifoslabel name="${ConfigurationConstants.POSTAL_CODE}"  bundle="PersonnelUIResources" keyhm="Personnel.PostalCode" isColonRequired="yes"></mifos:mifoslabel>
		</td>
                <td>
		           <mifos:mifosalphanumtext name="personnelActionForm" property="personnelDetails.postalCode" value="${requestScope.PersonnelVO.personnelDetails.postalCode}" keyhm="Personnel.PostalCode"/>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right">
			<mifos:mifoslabel keyhm="Personnel.PhoneNumber" name="Personnel.Telephone"  bundle="PersonnelUIResources"></mifos:mifoslabel>
		</td>
                <td>
                  <mifos:mifosalphanumtext keyhm="Personnel.PhoneNumber" name="personnelActionForm" property="personnelDetails.telephone" value="${requestScope.PersonnelVO.personnelDetails.telephone}"/>
                </td>
              </tr>
           </table>
            <br>
        
	    <table width="95%" border="0" cellpadding="3" cellspacing="0">
              <tr class="fontnormal">
                <td colspan="2" class="fontnormalbold">
			<mifos:mifoslabel name="Personnel.OfficePermissions"  bundle="PersonnelUIResources"></mifos:mifoslabel>
			<br>
                    <br>
                </td>
              </tr>
              <tr class="fontnormal">
                <td width="22%" align="right">
		<mifos:mifoslabel name="Personnel.Office"  bundle="PersonnelUIResources" mandatory="yes"></mifos:mifoslabel>
		</td>
                <td width="78%"> 
               		<mifos:select name="personnelActionForm" property="office.officeId" value="${requestScope.PersonnelVO.office.officeId}">
	                	<html-el:options collection="officeList" property="officeId" labelProperty="officeName"></html-el:options> 
                	</mifos:select> 
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right" valign="top">
			<mifos:mifoslabel name="Personnel.UserTitle"  bundle="PersonnelUIResources"></mifos:mifoslabel>
		</td>
                <td>
                	 <c:set var="titlecol" scope="request" value="${requestScope.titleList.lookUpMaster}"/> 
                	<mifos:select name="personnelActionForm" property="title" value="${requestScope.PersonnelVO.title}">
	                	<html-el:options collection="titlecol" property="lookUpId" labelProperty="lookUpValue"></html-el:options> 
                	</mifos:select> 
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right" valign="top">
		<mifos:mifoslabel name="Personnel.UserHierarchy"  mandatory="yes" bundle="PersonnelUIResources"></mifos:mifoslabel>
		</td>
                <td> 
                	<c:set var="personnelLevelCol" scope="request" value="${requestScope.personnelLevelList.lookUpMaster}"/> 
               		 <mifos:select name="personnelActionForm" property="level.levelId" value="${requestScope.PersonnelVO.level.levelId}">
	                	<html-el:options collection="personnelLevelCol" property="id" labelProperty="lookUpValue"></html-el:options> 
                	</mifos:select>

                </td>
              </tr>
			<%--User Roles--%>
              <tr class="fontnormal">
                <td align="right" valign="top">
			  	<mifos:mifoslabel name="Personnel.Roles"  bundle="PersonnelUIResources"></mifos:mifoslabel>
		        </td>
                <td>
                 <mifos:MifosSelect property="personnelRoles" input="rolesList" output="personnelRolesList" property1="id" property2="name" multiple="true" >
				</mifos:MifosSelect> 
                </td>
            </tr>

            </table>
            <br>
            <table width="95%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td colspan="2" class="fontnormalbold">
		<mifos:mifoslabel name="Personnel.LoginInformation"  bundle="PersonnelUIResources"></mifos:mifoslabel>
		<br>
                    <br>
                </td>
              </tr>
              <tr class="fontnormal">
         <td width="22%" align="right">
			<mifos:mifoslabel name="Personnel.UserName" bundle="PersonnelUIResources"></mifos:mifoslabel>
		</td>
        <td width="78%">
           	<c:out value="${requestScope.PersonnelVO.userName}"/>
        </td>
         </tr>
          <tr class="fontnormal">
             <td align="right">
				<mifos:mifoslabel name="Personnel.Password" bundle="PersonnelUIResources"></mifos:mifoslabel>
		     </td>
             <td>
                	<html-el:password property="password" style="width:136px;" redisplay="false"/>
             </td>
          </tr>
          <tr class="fontnormal">
              <td align="right">
				<mifos:mifoslabel name="Personnel.ConfirmPassword" bundle="PersonnelUIResources"></mifos:mifoslabel>
		      </td>
              <td>
               	<html-el:password property="passwordRepeat" style="width:136px;" redisplay="false"/>
              </td>
          </tr>
        </table>
            <br>
	
            <table width="95%" border="0" cellpadding="3" cellspacing="0">
            <c:if test="${!empty requestScope.customFields}">
              <tr>
                <td colspan="2" class="fontnormalbold">
                
				<mifos:mifoslabel name="Personnel.AdditionalInfo"  bundle="PersonnelUIResources"></mifos:mifoslabel>
				<br>
                  <br>
                </td>
              </tr>
			</c:if>              
              
              <c:forEach var="cfdef" items="${requestScope.customFields}" varStatus="loopStatus">
              	 <bean:define id="ctr">
                	<c:out value="${loopStatus.index}"/>
                </bean:define>
                <c:forEach var="cf" items="${requestScope.PersonnelVO.customFieldSet}" varStatus="loopStatus">
                 <c:if test="${cfdef.fieldId==cf.fieldId}">
              	  <tr class="fontnormal">
	               <td width="22%" align="right">
              <mifos:mifoslabel name="${cfdef.lookUpEntity.entityType}" mandatory="${cfdef.mandatoryStringValue}" bundle="PersonnelUIResources"></mifos:mifoslabel>: 
			  <!-- 	<mifos:mifoslabel name="cf.lookUpEntity.entityId" bundle="PersonnelUIResources"></mifos:mifoslabel>:-->
			</td>
	               <td width="78%">
	                  <c:if test="${cfdef.fieldType == 1}">  
	                  	<mifos:mifosnumbertext  name = "personnelActionForm" property='customField[${ctr}].fieldValue' value="${cf.fieldValue}" /> 
	                  </c:if>
	               	  <c:if test="${cfdef.fieldType == 2}">
	                 	<mifos:mifosalphanumtext name = "personnelActionForm" property='customField[${ctr}].fieldValue' value="${cf.fieldValue}"/>
		    	 </c:if>
	                 <c:if test="${cfdef.fieldType == 3}"> 
	                 	<mifos:mifosalphanumtext name = "personnelActionForm" property='customField[${ctr}].fieldValue' value="${cf.fieldValue}"/>
	                 </c:if>
	                  	<html-el:hidden property='customField[${ctr}].fieldId' value="${cfdef.fieldId}"></html-el:hidden>
	                </td>
	             </tr>
	           </c:if>
	          </c:forEach>
   	        </c:forEach>

            </table>

            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center" class="blueline">&nbsp;                </td>
              </tr>
            </table>            <br>
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center">
         	<html-el:submit styleClass="buttn" style="width:70px;" onclick="transferData(this.form.personnelRoles)">
	               	<mifos:mifoslabel name="button.preview" bundle="PersonnelUIResources"></mifos:mifoslabel>
              	</html-el:submit>
&nbsp;
               	<html-el:button property="cancelBtn"  styleClass="cancelbuttn" style="width:70px" onclick="goToCancelPage()">
	                <mifos:mifoslabel name="button.cancel" bundle="PersonnelUIResources"></mifos:mifoslabel>
               	</html-el:button>
                        </td>
              </tr>
            </table>
            
      </table>
<html-el:hidden property="input" value="ManageUser"/> 
<html-el:hidden property="personnelDetails.governmentIdNumber" value="${requestScope.PersonnelVO.personnelDetails.governmentIdNumber}"/> 
<html-el:hidden property="userName" value="${requestScope.PersonnelVO.userName}"/> 
<html-el:hidden property="versionNo" value="${requestScope.PersonnelVO.versionNo}"/> 
<html-el:hidden property="dob" value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.PersonnelVO.dob)}"/>
</html-el:form>
</tiles:put>
</tiles:insert>
