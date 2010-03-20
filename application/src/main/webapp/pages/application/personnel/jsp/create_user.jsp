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
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".create">
	<tiles:put name="body" type="string">
	<span id="page.id" title="create_user" />
	
		<script language="javascript">

  function goToCancelPage(){
	personActionForm.action="PersonAction.do";
	personActionForm.method.value="cancel";
	personActionForm.submit();
  }
 
</script>

		<SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>
		<html-el:form action="PersonAction.do"
			focus="firstName">

			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="350" align="left" valign="top" bgcolor="#FFFFFF">
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td align="center" class="heading">&nbsp;</td>
						</tr>
					</table>
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td class="bluetablehead">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td width="33%">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/tick.gif"
												width="17" height="17"></td>
											<td class="timelineboldgray"><mifos:mifoslabel
												name="Personnel.ChooseOffice" /></td>
										</tr>
									</table>
									</td>
									<td width="34%" align="center">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/bigarrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorange"><mifos:mifoslabel
												name="Personnel.UserInformation" /></td>
										</tr>
									</table>
									</td>
									<td width="33%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorangelight"><mifos:mifoslabel
												name="Personnel.ReviewSubmit" /></td>
										</tr>
									</table>
									</td>
							</table>
							</td>
						</tr>
					</table>
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0" class="bluetableborder">
						<tr>
							<td align="left" valign="top" class="paddingleftCreates">
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td class="headingorange"><span class="heading"> <mifos:mifoslabel
										name="Personnel.AddNewUser" /> - </span> <mifos:mifoslabel
										name="Personnel.EnterUserInformation" /></td>
								</tr>
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="Personnel.CompleteFields" /> <mifos:mifoslabel
										name="Personnel.ClickPreview" /> <mifos:mifoslabel
										name="Personnel.ClickCancel" /> <br>

									<mifos:mifoslabel name="Personnel.FieldsMandatory"
										mandatory="yes" /></td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellspacing="0" cellpadding="3">
								<tr>
									<td><font class="fontnormalRedBold"> <span id="create_user.error.message"><html-el:errors
										bundle="PersonnelUIResources" /></span> </font></td>
								</tr>
								<tr>
									<td class="fontnormal"><span class="fontnormalbold"> <mifos:mifoslabel
										name="Personnel.Office" /> </span> <c:out
										value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'office').officeName}" /></td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr class="fontnormal">
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.UserDetails" /> <br>
									<br>
									</td>
								</tr>
								<tr class="fontnormal">
									<td width="22%" height="28" align="right"><span id="create_user.label.firstName"><mifos:mifoslabel
										name="Personnel.FirstName" bundle="PersonnelUIResources"
										mandatory="yes"></mifos:mifoslabel></span></td>
									<td width="78%"><mifos:mifosalphanumtext styleId="create_user.input.firstName"
										name="personActionForm"
										property="firstName" maxlength="100"/></td>
										
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="create_user.label.middleName"><mifos:mifoslabel name="Personnel.MiddleName"
										bundle="PersonnelUIResources" keyhm="Personnel.MiddleName"></mifos:mifoslabel></span>
									</td>
									<td><mifos:mifosalphanumtext styleId="create_user.input.middleName" keyhm="Personnel.MiddleName"
										name="personActionForm"
										property="middleName" maxlength="100"/></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="create_user.label.secondLastName"><mifos:mifoslabel
										keyhm="Personnel.SecondLastName"
										name="Personnel.SecondLastName" /></span></td>
									<td><mifos:mifosalphanumtext styleId="create_user.input.secondLastName" keyhm="Personnel.SecondLastName"
										name="personActionForm"
										property="secondLastName" maxlength="100"/></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="create_user.label.lastName"><mifos:mifoslabel name="Personnel.LastName"
										mandatory="yes" /></span></td>
									<td><mifos:mifosalphanumtext styleId="create_user.input.lastName" name="personActionForm"
										property="lastName" maxlength="100"/></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="create_user.label.governmentId"><mifos:mifoslabel
										keyhm="Personnel.GovernmentId" isColonRequired="yes"
										name="${ConfigurationConstants.GOVERNMENT_ID}" bundle="PersonnelUIResources"/></span></td>
									<td><mifos:mifosalphanumtext styleId="create_user.input.governmentId" keyhm="Personnel.GovernmentId"
										name="personActionForm"
										property="governmentIdNumber" maxlength="50"/></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="create_user.label.email"><mifos:mifoslabel name="Personnel.Email" /></span></td>
									<td><mifos:mifosalphanumtext styleId="create_user.input.email" property="emailId" maxlength="200" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="Personnel.DOB"
										mandatory="yes" /></td>
									<td><date:datetag renderstyle="simple" property="dob" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="Personnel.MaritalStatus" /></td>
									<td><mifos:select name="personActionForm"
										property="maritalStatus">
											<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'maritalStatusList')}" 
											var="maritalStatus">
											<html-el:option value="${maritalStatus.id}">${maritalStatus.name}</html-el:option>
											</c:forEach>
											
									</mifos:select></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="Personnel.Gender"
										mandatory="yes" /></td>
									<td><mifos:select name="personActionForm"
										property="gender">
										<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'genderList')}" 
											var="genderlist">
											<html-el:option value="${genderlist.id}">${genderlist.name}</html-el:option>
											</c:forEach>
									</mifos:select></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="Personnel.LanguagePreferred" /></td>
									<td>
                                        <mifos:select name="personActionForm" property="preferredLocale">
                                            <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'languageList')}"
                                                       var="languageList">
                                                <html-el:option value="${languageList.id}">${languageList.name}</html-el:option>
                                            </c:forEach>
                                        </mifos:select>
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="Personnel.DOJMFI" /></td>
									<td><date:datetag property="dateOfJoiningMFI" /></td>
								</tr>

							</table>
							<br>
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.Address" /> <br>
									<br>
									</td>
								</tr>
								<tr class="fontnormal">
									<td width="22%" align="right"><span id="create_user.label.address1"><mifos:mifoslabel keyhm="Personnel.Address1" isColonRequired="yes" 
										name="${ConfigurationConstants.ADDRESS1}" bundle="PersonnelUIResources"/></span></td>
									<td width="78%"><mifos:mifosalphanumtext styleId="create_user.input.address1" keyhm="Personnel.Address1" 
										name="personActionForm"
										property="address.line1" maxlength="200"/></td>
								</tr>
								<tr class="fontnormal">							
									<td align="right"><span id="create_user.label.address2"><mifos:mifoslabel keyhm="Personnel.Address2"
										isColonRequired="yes" 
										name="${ConfigurationConstants.ADDRESS2}" bundle="PersonnelUIResources"/></span></td>
									<td><mifos:mifosalphanumtext styleId="create_user.input.address2" keyhm="Personnel.Address2"
										name="personActionForm"
										property="address.line2" maxlength="200"/></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="create_user.label.address3"><mifos:mifoslabel keyhm="Personnel.Address3"
										isColonRequired="yes"
										name="${ConfigurationConstants.ADDRESS3}" bundle="PersonnelUIResources"/></span></td>
									<td><mifos:mifosalphanumtext styleId="create_user.input.address3" keyhm="Personnel.Address3"
										name="personActionForm"
										property="address.line3" maxlength="200" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="create_user.label.city"><mifos:mifoslabel
										name="${ConfigurationConstants.CITY}" bundle="PersonnelUIResources"/></span>:</td>
									<td><mifos:mifosalphanumtext styleId="create_user.input.city" name="personActionForm"
										property="address.city" maxlength="100" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="create_user.label.state"><mifos:mifoslabel
										name="${ConfigurationConstants.STATE}"
										bundle="PersonnelUIResources" keyhm="Personnel.State"
										isColonRequired="yes"></mifos:mifoslabel></span></td>
									<td><mifos:mifosalphanumtext styleId="create_user.input.state" name="personActionForm"
										property="address.state" keyhm="Personnel.State" maxlength="100"/></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="create_user.label.country"><mifos:mifoslabel name="Personnel.Country"
										bundle="PersonnelUIResources" keyhm="Personnel.Country"></mifos:mifoslabel></span>
									</td>
									<td><mifos:mifosalphanumtext styleId="create_user.input.country" name="personActionForm"
										property="address.country" keyhm="Personnel.Country" maxlength="100"/>
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="create_user.label.postalCode"><mifos:mifoslabel
										name="${ConfigurationConstants.POSTAL_CODE}"
										bundle="PersonnelUIResources" isColonRequired="yes"
										keyhm="Personnel.PostalCode"></mifos:mifoslabel></span></td>
									<td><mifos:mifosalphanumtext styleId="create_user.input.postalCode" name="personActionForm"
										property="address.zip"
										keyhm="Personnel.PostalCode" maxlength="20"/></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="create_user.label.phoneNumber"><mifos:mifoslabel
										keyhm="Personnel.PhoneNumber" name="Personnel.Telephone" /></span></td>
									<td><mifos:mifosalphanumtext styleId="create_user.input.phoneNumber" keyhm="Personnel.PhoneNumber"
										name="personActionForm"
										property="address.phoneNumber" maxlength="20"/></td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr class="fontnormal">
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.Permission" /> <br>
									<br>
									</td>
								</tr>
								<tr class="fontnormal">
									<td width="22%" align="right"><mifos:mifoslabel
										name="Personnel.UserTitle" /></td>
									<td width="78%">
										 <mifos:select
										name="personActionForm" property="title">
										<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'titleList')}" 
											var="titlelist">
											<html-el:option value="${titlelist.id}">${titlelist.name}</html-el:option>
											</c:forEach>
									</mifos:select> </td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="Personnel.UserHierarchy" mandatory="yes" /></td>

									<td>
										<mifos:select
										name="personActionForm" property="level">
										<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'personnelLevelList')}" 
											var="item">
											<html-el:option value="${item.id}">${item.name}</html-el:option>
											</c:forEach>
									</mifos:select></td>
								</tr>

								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="Personnel.Roles" /></td>
									<td>
									<c:set var="rolelist" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'rolesList')}"/> 
									<c:set var="personnelRolesList" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'personnelRolesList')}"/> 
									<mifos:MifosSelect property="personnelRoles"
										input="rolelist" output="personnelRolesList" property1="id"
										property2="name" multiple="true">
									</mifos:MifosSelect>  </td>
								</tr>

							</table>
							<br>
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.LoginInformation" /> <br>
									<br>
									</td>
								</tr>
								<tr class="fontnormal">
									<td width="22%" align="right"><span id="create_user.label.userName"><mifos:mifoslabel
										name="Personnel.UserName" mandatory="yes" /></span></td>
									<td width="78%"><mifos:mifosalphanumtext styleId="create_user.input.userName" property="loginName" name="personActionForm" maxlength="20" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="create_user.label.password"><mifos:mifoslabel name="Personnel.Password"
										mandatory="yes" /></span></td>
									<td><html-el:password styleId="create_user.input.password" property="userPassword" name="personActionForm" style="width:136px;"
										redisplay="false" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="create_user.label.passwordRepeat"><mifos:mifoslabel
										name="Personnel.ConfirmPassword" mandatory="yes" /></span></td>
									<td><html-el:password styleId="create_user.input.passwordRepeat" property="passwordRepeat"
										style="width:136px;" name="personActionForm" redisplay="false" /></td>
								</tr>
							</table>
							<br>
							<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
							
							<c:set var="customFieldsList" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}"/> 								
									<tr>
										<td colspan="2" class="fontnormalbold" >
										<mifos:mifoslabel	name="Personnel.AdditionalInfo"	/><br>
										<br>
										</td>
									</tr>
								
								<c:forEach var="cf" items="${requestScope.customFieldsList}"
									varStatus="loopStatus">
									<bean:define id="ctr">
										<c:out value="${loopStatus.index}" />
									</bean:define>
									<tr class="fontnormal">
										<td width="22%" align="right"><span id="create_user.label.customField"><mifos:mifoslabel
											name="${cf.lookUpEntity.entityType}"
											mandatory="${cf.mandatoryStringValue}"
											bundle="PersonnelUIResources"></mifos:mifoslabel></span>: 
										</td>
										<td width="78%" ><c:if test="${cf.fieldType == 1}">
											<mifos:mifosnumbertext styleId="create_user.input.customField" name="personActionForm"
												property='customField[${ctr}].fieldValue' maxlength="200" />
										</c:if> <c:if test="${cf.fieldType == 2}">
											<mifos:mifosalphanumtext styleId="create_user.input.customField" name="personActionForm"
												property='customField[${ctr}].fieldValue' maxlength="200" />
										</c:if> <c:if test="${cf.fieldType == 3}">
											<mifos:mifosalphanumtext styleId="create_user.input.customField" name="personActionForm"
												property='customField[${ctr}].fieldValue' maxlength="200" />

										</c:if></td>
									</tr>
								</c:forEach>

							</table>
						</c:if>
							<!--Custom Fields end  -->
							
							
							

							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center" class="blueline">&nbsp;</td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center"><html-el:submit styleId="create_user.button.preview" styleClass="buttn"
										onclick="transferData(this.form.personnelRoles)">
										<mifos:mifoslabel name="button.preview"
											bundle="PersonnelUIResources"></mifos:mifoslabel>
									</html-el:submit> &nbsp; <html-el:button property="cancelBtn"
										styleId="create_user.button.cancel" styleClass="cancelbuttn"
										onclick="goToCancelPage()">
										<mifos:mifoslabel name="button.cancel"
											bundle="PersonnelUIResources"></mifos:mifoslabel>
									</html-el:button></td>
								</tr>
							</table>
							<br>
							</td>
						</tr>
					</table>
					<br>
					</td>
				</tr> 
			</table>
			<html-el:hidden property="input" value="CreateUser" />
			<html-el:hidden property="method" value="preview" />
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>

