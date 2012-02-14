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
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".view">
 <tiles:put name="body" type="string">
 <span id="page.id" title="edit_user"></span>
 
<script language="javascript">

  function goToCancelPage(){
	personActionForm.action="PersonAction.do?method=cancel";
	personActionForm.submit();
  }
 
</script>
<SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>
<html-el:form action="PersonAction.do?method=previewManage">
	<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
	<html-el:hidden property="input" value="ManageUser"/> 
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
	 <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
         <c:url value="AdminAction.do" var="AdminActionLoadMethodUrl" >
          <c:param name="method" value="load" />
         </c:url >
          <td class="bluetablehead05">
          <span class="fontnormal8pt"><a id="edit_user.link.admin" href="${AdminActionLoadMethodUrl}">
	           <mifos:mifoslabel name="Personnel.Admin" bundle="PersonnelUIResources"></mifos:mifoslabel>             	
           	</a>
		 / 
		<c:url value="PersonAction.do" var="PersonActionLoadSearchMethodUrl" >
			<c:param name="method" value="loadSearch" />
		</c:url >
		
			<a id="edit_user.link.viewUsers" href="${PersonActionLoadSearchMethodUrl}">
			<mifos:mifoslabel name="Personnel.ViewUsers" bundle="PersonnelUIResources"></mifos:mifoslabel>
		<c:url value="PersonAction.do" var="PersonActionGetMethodUrl" >
			<c:param name="method" value="get" />
			<c:param name="globalPersonnelNum" value="${BusinessKey.globalPersonnelNum}" />
		</c:url >
			</a> / 
			<a id="edit_user.link.viewUser" href="${PersonActionGetMethodUrl}">
	           <c:out value="${BusinessKey.displayName}"/>            	
           	</a>
          </span></td>
        </tr>
      </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td align="left" valign="top" class="paddingL15T15"><table width="95%" border="0" cellpadding="3" cellspacing="0">
            <tr>
              <td class="headingorange"><span class="heading">
              <c:out value="${BusinessKey.displayName}"/>   - </span>
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
   					<span id="edit_user.error.message"><html-el:errors bundle="PersonnelUIResources"/></span>
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
                <span id="edit_user.label.firstName">
			<mifos:mifoslabel name="Personnel.FirstName" bundle="PersonnelUIResources" mandatory="yes"></mifos:mifoslabel>
			</span>
		</td>
                <td width="78%">
                	<mifos:mifosalphanumtext styleId="edit_user.input.firstName" name="personActionForm" property="firstName" maxlength="200" />
                </td>
              </tr>
              
	            <tr class="fontnormal">
                <td align="right">
                	<span id="edit_user.label.firstName">
					<mifos:mifoslabel name="Personnel.MiddleName" bundle="PersonnelUIResources" keyhm="Personnel.MiddleName"></mifos:mifoslabel>
					</span>
				</td>
                <td>
                	<mifos:mifosalphanumtext styleId="edit_user.input.middleName" name="personActionForm" property="middleName" keyhm="Personnel.MiddleName" maxlength="200"/>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right">
                <span id="edit_user.label.SecondLastName">
			<mifos:mifoslabel keyhm="Personnel.SecondLastName" name="Personnel.SecondLastName" bundle="PersonnelUIResources"></mifos:mifoslabel>
			</span>
		</td>
                <td>
	             	<mifos:mifosalphanumtext styleId="edit_user.input.secondLastName" keyhm="Personnel.SecondLastName" name="personActionForm" property="secondLastName" maxlength="200"/>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right">
                <span id="edit_user.label.lastName">
			<mifos:mifoslabel name="Personnel.LastName" mandatory="yes" bundle="PersonnelUIResources"></mifos:mifoslabel>
			</span>
		</td>
                <td>  
                	<mifos:mifosalphanumtext styleId="edit_user.input.lastName" name="personActionForm" property="lastName" maxlength="200" />
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right">
			<mifos:mifoslabel keyhm="Personnel.GovernmentId" isColonRequired="yes" name="${ConfigurationConstants.GOVERNMENT_ID}"  bundle="PersonnelUIResources"></mifos:mifoslabel>
		</td>
                <td>  
                	<c:out value="${BusinessKey.personnelDetails.governmentIdNumber}"/>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right">
                <span id="edit_user.label.email">
			<mifos:mifoslabel name="Personnel.Email" bundle="PersonnelUIResources"></mifos:mifoslabel>
			</span>
		</td>
                <td>
                	<mifos:mifosalphanumtext styleId="edit_user.input.email"  property="emailId"/>
                </td>
              </tr>
			<tr class="fontnormal">
                <td align="right">
				<mifos:mifoslabel name="Personnel.DOB"  bundle="PersonnelUIResources"></mifos:mifoslabel>
				</td>
                <td>
                    <date:datetag renderstyle="simple" property="dob" />
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right">
				<mifos:mifoslabel name="Personnel.MaritalStatus" bundle="PersonnelUIResources"></mifos:mifoslabel>
				</td>
                <td><mifos:select name="personActionForm" property="maritalStatus">
						<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'maritalStatusList')}" var="maritalStatuslist">
							<html-el:option value="${maritalStatuslist.id}">${maritalStatuslist.name}</html-el:option>
						</c:forEach>
					</mifos:select>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right">
				<mifos:mifoslabel name="Personnel.Gender" mandatory="yes" bundle="PersonnelUIResources"></mifos:mifoslabel>
				</td>
                <td>
                	<mifos:select name="personActionForm" property="gender">
					   <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'genderList')}" var="genderlist">
							<html-el:option value="${genderlist.id}">${genderlist.name}</html-el:option>
						</c:forEach>
					</mifos:select>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right"><mifos:mifoslabel name="Personnel.DOJMFI"  bundle="PersonnelUIResources"></mifos:mifoslabel></td>
                <td><c:out value="${personActionForm.dateOfJoiningMFI}" /></td>
              </tr>
	            <br>
            	<table width="95%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td colspan="2" class="fontnormalbold">
			<mifos:mifoslabel name="Personnel.Status" bundle="PersonnelUIResources"></mifos:mifoslabel>
			<br>
                    <br>
                </td>
              </tr>
              <tr class="fontnormal">
                <td width="22%" align="right">
				<mifos:mifoslabel name="Personnel.ChangeStatus" mandatory="yes"  bundle="PersonnelUIResources"></mifos:mifoslabel>
				</td>
                <td width="78%">
					<mifos:select name="personActionForm" property="status">
						<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'statusList')}" 	var="statuslist">
							<html-el:option value="${statuslist.id}">${statuslist.name}</html-el:option>
						</c:forEach>
					 </mifos:select>
				</td>
              </tr>
            </table><br>

            <table width="95%" border="0" cellpadding="3" cellspacing="0">
			<tr>
              <td colspan="2" class="fontnormalbold"><mifos:mifoslabel name="Personnel.Address"  bundle="PersonnelUIResources"></mifos:mifoslabel>
			  <br>
              <br>
              </td>
            </tr>
            <tr class="fontnormal">
                <td width="22%" align="right">
                <span id="edit_user.label.address1">
			<mifos:mifoslabel name="${ConfigurationConstants.ADDRESS1}"  keyhm="Personnel.Address1" isColonRequired="yes" bundle="PersonnelUIResources"></mifos:mifoslabel>
			</span>
		</td>
                <td width="78%">
	                <mifos:mifosalphanumtext styleId="edit_user.input.address1" name="personActionForm"  keyhm="Personnel.Address1" property="address.line1" maxlength="200" />
                </td>
              </tr>
				<tr class="fontnormal">							
					<td align="right"><span id="edit_user.label.address2"><mifos:mifoslabel keyhm="Personnel.Address2"
						isColonRequired="yes" 
						name="${ConfigurationConstants.ADDRESS2}" bundle="PersonnelUIResources"/></span></td>
					<td><mifos:mifosalphanumtext styleId="edit_user.input.address2" name="personActionForm" keyhm="Personnel.Address2"
						property="address.line2" maxlength="200"/></td>
				</tr>
               <tr class="fontnormal">
                <td align="right">
                <span id="edit_user.label.address3">
			<mifos:mifoslabel keyhm="Personnel.Address3" isColonRequired="yes" name="${ConfigurationConstants.ADDRESS3}"  bundle="PersonnelUIResources"></mifos:mifoslabel>
			</span>
		</td>
                <td>
		            <mifos:mifosalphanumtext styleId="edit_user.input.address3" keyhm="Personnel.Address3" name="personActionForm" property="address.line3" maxlength="200" />
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right">
                <span id="edit_user.label.city">
			<mifos:mifoslabel keyhm="Personnel.City" isColonRequired="yes" name="${ConfigurationConstants.CITY}"  bundle="PersonnelUIResources"></mifos:mifoslabel>
			</span>
		</td>
                <td>
                    <mifos:mifosalphanumtext styleId="edit_user.input.city" keyhm="Personnel.City" name="personActionForm" property="address.city" maxlength="100" />
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right">
                <span id="edit_user.label.state">
			<mifos:mifoslabel name="${ConfigurationConstants.STATE}"  bundle="PersonnelUIResources" keyhm="Personnel.State" isColonRequired="yes"></mifos:mifoslabel>
			</span>
		</td>
                <td>
		            <mifos:mifosalphanumtext styleId="edit_user.input.state" name="personActionForm" property="address.state"  keyhm="Personnel.State" maxlength="100"/>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right">
           <span id="edit_user.label.country">
			<mifos:mifoslabel name="Personnel.Country"  bundle="PersonnelUIResources" keyhm="Personnel.Country"></mifos:mifoslabel>
			</span>
			</td>
                <td>	
                    <mifos:mifosalphanumtext styleId="edit_user.input.country" name="personActionForm" property="address.country" keyhm="Personnel.Country" maxlength="100"/>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right">
                <span id="edit_user.label.postalCode">
				<mifos:mifoslabel name="${ConfigurationConstants.POSTAL_CODE}"  bundle="PersonnelUIResources" keyhm="Personnel.PostalCode" isColonRequired="yes"></mifos:mifoslabel>
				</span>
				</td>
                <td>
		           <mifos:mifosalphanumtext styleId="edit_user.input.postalCode" name="personActionForm" property="address.zip" keyhm="Personnel.PostalCode" maxlength="100"/>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right">
                <span id="edit_user.label.phoneNumber">
					<mifos:mifoslabel keyhm="Personnel.PhoneNumber" name="Personnel.Telephone"  bundle="PersonnelUIResources"></mifos:mifoslabel>
					</span>
				</td>
                <td>
                  <mifos:mifosalphanumtext styleId="edit_user.input.phoneNumber" keyhm="Personnel.PhoneNumber" name="personActionForm" property="address.phoneNumber" maxlength="20" />
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
               		<mifos:select name="personActionForm" property="officeId">
						<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'officeList')}" 	var="office">
							<html-el:option value="${office.officeId}">${office.officeName}</html-el:option>
						</c:forEach>
					 </mifos:select>
               	</td>
              </tr>
              <tr class="fontnormal">
                <td align="right" valign="top">
			<mifos:mifoslabel name="Personnel.UserTitle"  bundle="PersonnelUIResources"></mifos:mifoslabel>
			</td>
                <td>
                	<mifos:select name="personActionForm" property="title">
						<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'titleList')}" var="titlelist">
							<html-el:option value="${titlelist.id}">${titlelist.name}</html-el:option>
						</c:forEach>
					</mifos:select>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right" valign="top">
				<mifos:mifoslabel name="Personnel.UserHierarchy"  mandatory="yes" bundle="PersonnelUIResources"></mifos:mifoslabel>
				</td>
                <td> 
                	<mifos:select name="personActionForm" property="level">
					  <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'personnelLevelList')}" 	var="personnelLevel">
						<html-el:option value="${personnelLevel.id}">${personnelLevel.name}</html-el:option>
						</c:forEach>
					</mifos:select>
                </td>
              </tr>
			<%--User Roles--%>
              <tr class="fontnormal">
                <td align="right" valign="top">
			  	<mifos:mifoslabel name="Personnel.Roles"  bundle="PersonnelUIResources"></mifos:mifoslabel>
		        </td>
                <td>
                <c:set var="rolelist" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'rolesList')}"/> 
				<c:set var="personnelRolesList" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'personnelRolesList')}"/> 
				<mifos:MifosSelect property="personnelRoles"
					input="rolelist" output="personnelRolesList" property1="id"
					property2="name" multiple="true">
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
           	<c:out value="${sessionScope.personActionForm.loginName}"/>
        	<html-el:hidden property="loginName" value="${BusinessKey.userName}"/> 
        </td>
         </tr>
          <tr class="fontnormal">
             <td align="right">
				<mifos:mifoslabel name="Personnel.Password" bundle="PersonnelUIResources"></mifos:mifoslabel>
		     </td>
             <td>
                	<html-el:password styleId="edit_user.input.userPassword" property="userPassword" style="width:136px;" redisplay="false"/>
             </td>
          </tr>
          <tr class="fontnormal">
              <td align="right">
				<mifos:mifoslabel name="Personnel.ConfirmPassword" bundle="PersonnelUIResources"></mifos:mifoslabel>
		      </td>
              <td>
               	<html-el:password styleId="edit_user.input.passwordRepeat" property="passwordRepeat" style="width:136px;" redisplay="false"/>
              </td>
          </tr>
        </table>
		<br />
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center" class="blueline">&nbsp;                </td>
              </tr>
            </table>            <br>
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center">
         	<html-el:submit styleId="edit_user.button.preview" styleClass="buttn" onclick="transferData(this.form.personnelRoles)">
	               	<mifos:mifoslabel name="button.preview" bundle="PersonnelUIResources"></mifos:mifoslabel>
              	</html-el:submit>
&nbsp;
               	<html-el:button styleId="edit_user.button.cancel" property="cancelBtn"  styleClass="cancelbuttn" onclick="goToCancelPage()">
	                <mifos:mifoslabel name="button.cancel" bundle="PersonnelUIResources"></mifos:mifoslabel>
               	</html-el:button>
                        </td>
              </tr>
            </table>
            
      </table>

</html-el:form>
</tiles:put>
</tiles:insert>
