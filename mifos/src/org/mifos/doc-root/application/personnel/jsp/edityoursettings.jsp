<!-- /**
 
 * edityoursettings.jsp    version: 1.0
 
 
 
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
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".noorangetab">
	<tiles:put name="body" type="string">
		<script language="javascript">
  function goToCancelPage(){
	personnelSettingsActionForm.action="yourSettings.do?method=get";
	personnelSettingsActionForm.submit();
  }
</script>
		<html-el:form action="yourSettings.do?method=preview">
			<c:set var="form" value="${sessionScope.personnelSettingsActionForm}" />
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="350" align="left" valign="top" bgcolor="#FFFFFF">
					<table width="590" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<table width="590" border="0" align="center" cellpadding="0"
						cellspacing="0" class="bluetableborder">
						<tr>
							<td align="left" valign="top" class="paddingleftCreates">
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td class="headingorange"><mifos:mifoslabel
										name="Personnel.EditYourSettings" /></td>
								</tr>
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="Personnel.ModifySettings" /> <mifos:mifoslabel
										name="Personnel.ClickPreview" /> <mifos:mifoslabel
										name="Personnel.CancelSettings" /> <br>
									<font color="#FF0000">*</font><mifos:mifoslabel
										name="Personnel.FieldsMandatory" /></td>
								</tr>
							</table>
							<br>
							
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="2"><font class="fontnormalRedBold"> <html-el:errors
										bundle="PersonnelUIResources" /> </font></td>
								</tr>
								<tr class="fontnormal">
									<td height="28" colspan="2"><span class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.YourDetails" /><br>
									<br>
									</span></td>
								</tr>
								<tr class="fontnormal">
									<td width="32%" height="28" align="right"><mifos:mifoslabel
										name="Personnel.FirstName" mandatory="yes" /></td>
									<td width="68%"><mifos:mifosalphanumtext
										name="PersonnelSettingsActionForm"
										property="firstName"
										maxlength="100"
										value="${form.firstName}" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="Personnel.MiddleName" /></td>
									<td><mifos:mifosalphanumtext name="PersonnelSettingsActionForm"
										property="middleName"
              							maxlength="100"
										value="${form.middleName}" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="Personnel.SecondLastName" /></td>
									<td><mifos:mifosalphanumtext name="PersonnelSettingsActionForm"
										property="secondLastName"
										maxlength="100"
										value="${form.secondLastName}" />
									</td>
								</tr>

								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="Personnel.LastName"
										mandatory="yes" /></td>
									<td><mifos:mifosalphanumtext name="PersonnelSettingsActionForm"
										property="lastName"
										maxlength="100"
										value="${form.lastName}" />
									</td>
								</tr>

								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="${ConfigurationConstants.GOVERNMENT_ID}"
										bundle="PersonnelUIResources"></mifos:mifoslabel>:</td>
									<td><c:out
										value="${form.governmentIdNumber}" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="Personnel.Email" /></td>
									<td><mifos:mifosalphanumtext property="emailId"
										name="PersonnelSettingsActionForm"
										maxlength="255"
										value="${form.emailId}" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="Personnel.DOB"
										bundle="PersonnelUIResources"></mifos:mifoslabel></td>
									<td><c:out
										value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,form.dob)}" />
									</td>
								</tr>

								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="Personnel.MaritalStatus" /></td>
									
									<td>
									
								
								
									<mifos:select property="maritalStatus" value="${form.maritalStatus}">
											<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'maritalStatusList')}" 
											var="maritalStatus">
											<html-el:option value="${maritalStatus.id}">${maritalStatus.name}</html-el:option>
											</c:forEach>
											
									</mifos:select>
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="Personnel.Gender"
										mandatory="yes" /></td>
									
									<td>
									
									<mifos:select property="gender" value="${form.gender}">
											<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'genderList')}" 
											var="gender">
											<html-el:option value="${gender.id}">${gender.name}</html-el:option>
											</c:forEach>
											
									</mifos:select>
									</td>
								</tr>

								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="Personnel.LanguagePreferred" /></td>
										
									<td>
									<mifos:select property="preferredLocale" value="${form.preferredLocale}">
											<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'languageList')}" 
											var="languageList">
											<html-el:option value="${languageList.id}">${languageList.name}</html-el:option>
											</c:forEach>
											
									</mifos:select>
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="Personnel.UserName"
										bundle="PersonnelUIResources"></mifos:mifoslabel></td>
									<td><c:out value="${form.userName}" /></td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.Address" /><br>
									<br>
									</td>
								</tr>
								<tr class="fontnormal">
									<td width="32%" align="right"><mifos:mifoslabel
										name="${ConfigurationConstants.ADDRESS1}" />:</td>
									<td width="68%"><mifos:mifosalphanumtext
										name="PersonnelSettingsActionForm"
										property="address.line1"
										value="${form.address.line1}" 
										maxlength="200" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="${ConfigurationConstants.ADDRESS2}" />:</td>
									<td><mifos:mifosalphanumtext name="PersonnelSettingsActionForm"
										property="address.line2"
										value="${form.address.line2}" 
										maxlength="200" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="${ConfigurationConstants.ADDRESS3}" />:</td>
									<td><mifos:mifosalphanumtext name="PersonnelSettingsActionForm"
										property="address.line3"
										value="${form.address.line3}" 
										maxlength="200" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="${ConfigurationConstants.CITY}" />:</td>
									<td><mifos:mifosalphanumtext name="PersonnelSettingsActionForm"
										property="address.city"
										value="${form.address.city}" 
										maxlength="100" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="${ConfigurationConstants.STATE}" />:</td>
									<td><mifos:mifosalphanumtext name="PersonnelSettingsActionForm"
										property="address.state"
										value="${form.address.state}" 
										maxlength="100" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="Personnel.Country" /></td>
									<td><mifos:mifosalphanumtext name="PersonnelSettingsActionForm"
										property="address.country"
										value="${form.address.country}" 
										maxlength="100" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="${ConfigurationConstants.POSTAL_CODE}" />:</td>
									<td><mifos:mifosalphanumtext name="PersonnelSettingsActionForm"
										property="address.zip"
										value="${form.address.zip}" 
										maxlength="100" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="Personnel.Telephone" /></td>
									<td><mifos:mifosalphanumtext name="PersonnelSettingsActionForm"
										property="address.phoneNumber"
										value="${form.address.phoneNumber}" 
										maxlength="20" />
									</td>
								</tr>
							</table>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center" class="blueline">&nbsp;</td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center"><html-el:submit styleClass="buttn"
										style="width:70px;">
										<mifos:mifoslabel name="button.preview"
											bundle="PersonnelUIResources"></mifos:mifoslabel>
									</html-el:submit> &nbsp; <html-el:button property="cancelBtn"
										styleClass="cancelbuttn" style="width:70px"
										onclick="goToCancelPage()">
										<mifos:mifoslabel name="button.cancel"
											bundle="PersonnelUIResources"></mifos:mifoslabel>
									</html-el:button></td>
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
		</html-el:form>
	</tiles:put>
</tiles:insert>
