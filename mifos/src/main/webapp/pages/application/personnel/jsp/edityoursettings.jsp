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
									<td colspan="2"><font class="fontnormalRedBold"> <span id="edityoursettings.error.message"><html-el:errors
										bundle="PersonnelUIResources" /></span> </font></td>
								</tr>
								<tr class="fontnormal">
									<td height="28" colspan="2"><span class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.YourDetails" /><br>
									<br>
									</span></td>
								</tr>
								<tr class="fontnormal">
									<td width="32%" height="28" align="right"><span id="edityoursettings.label.firstName"><mifos:mifoslabel
										name="Personnel.FirstName" mandatory="yes" /></span></td>
									<td width="68%"><mifos:mifosalphanumtext
										styleId="edityoursettings.input.firstName"
										name="PersonnelSettingsActionForm"
										property="firstName"
										maxlength="100"
										value="${form.firstName}" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="edityoursettings.label.middleName"><mifos:mifoslabel keyhm="Personnel.MiddleName"
									name="Personnel.MiddleName" /></span></td>
									<td><mifos:mifosalphanumtext name="PersonnelSettingsActionForm"
										styleId="edityoursettings.input.middleName"
										property="middleName" keyhm="Personnel.MiddleName"
              							maxlength="100"
										value="${form.middleName}" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="edityoursettings.label.secondLastName"><mifos:mifoslabel
										name="Personnel.SecondLastName" keyhm="Personnel.SecondLastName" /></span></td>
									<td><mifos:mifosalphanumtext name="PersonnelSettingsActionForm"
										styleId="edityoursettings.input.secondLastName"
										keyhm="Personnel.SecondLastName"
										property="secondLastName"
										maxlength="100"
										value="${form.secondLastName}" />
									</td>
								</tr>

								<tr class="fontnormal">
									<td align="right"><span id="edityoursettings.label.lastName"><mifos:mifoslabel name="Personnel.LastName"
										mandatory="yes" /></span></td>
									<td><mifos:mifosalphanumtext name="PersonnelSettingsActionForm"
										styleId="edityoursettings.input.lastName"
										property="lastName"
										maxlength="100"
										value="${form.lastName}" />
									</td>
								</tr>

								<tr id="Personnel.GovernmentId" class="fontnormal">
									<td align="right"><mifos:mifoslabel
										keyhm="Personnel.GovernmentId"
										name="${ConfigurationConstants.GOVERNMENT_ID}"
										bundle="PersonnelUIResources"></mifos:mifoslabel>:</td>
									<td><c:out
										value="${form.governmentIdNumber}" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="edityoursettings.label.email"><mifos:mifoslabel name="Personnel.Email" /></span></td>
									<td><mifos:mifosalphanumtext property="emailId"
										styleId="edityoursettings.input.email"
										name="PersonnelSettingsActionForm"
										maxlength="255"
										value="${form.emailId}" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="Personnel.DOB"
										bundle="PersonnelUIResources"></mifos:mifoslabel></td>
									<td><c:out
										value="${userdatefn:getUserLocaleDateObject(sessionScope.UserContext.preferredLocale,form.dobDateObject)}" />
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
									<td width="32%" align="right"><span id="edityoursettings.label.address1"><mifos:mifoslabel
										name="${ConfigurationConstants.ADDRESS1}" /></span>:</td>
									<td width="68%"><mifos:mifosalphanumtext
										styleId="edityoursettings.input.address1"
										name="PersonnelSettingsActionForm"
										property="address.line1"
										value="${form.address.line1}"
										maxlength="200" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="edityoursettings.label.address2"><mifos:mifoslabel
										name="${ConfigurationConstants.ADDRESS2}" /></span>:</td>
									<td><mifos:mifosalphanumtext name="PersonnelSettingsActionForm"
										styleId="edityoursettings.input.address2"
										property="address.line2"
										value="${form.address.line2}"
										maxlength="200" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="edityoursettings.label.address3"><mifos:mifoslabel
										keyhm="Personnel.Address3"
										name="${ConfigurationConstants.ADDRESS3}" /></span>:</td>
									<td><mifos:mifosalphanumtext name="PersonnelSettingsActionForm"
										styleId="edityoursettings.input.address3"
										keyhm="Personnel.Address3"
										property="address.line3"
										value="${form.address.line3}"
										maxlength="200" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="edityoursettings.label.city"><mifos:mifoslabel
										name="${ConfigurationConstants.CITY}" /></span>:</td>
									<td><mifos:mifosalphanumtext name="PersonnelSettingsActionForm"
										styleId="edityoursettings.input.city"
										property="address.city"
										value="${form.address.city}"
										maxlength="100" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="edityoursettings.label.state"><mifos:mifoslabel
										keyhm="Personnel.State"
										name="${ConfigurationConstants.STATE}" /></span>:</td>
									<td><mifos:mifosalphanumtext name="PersonnelSettingsActionForm"
										styleId="edityoursettings.input.state"
										property="address.state"
										keyhm="Personnel.State"
										value="${form.address.state}"
										maxlength="100" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="edityoursettings.label.country"><mifos:mifoslabel name="Personnel.Country"
									keyhm="Personnel.Country" /></span>:</td>
									<td><mifos:mifosalphanumtext name="PersonnelSettingsActionForm"
										styleId="edityoursettings.input.country"
										keyhm="Personnel.Country"
										property="address.country"
										value="${form.address.country}"
										maxlength="100" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="edityoursettings.label.postalCode"><mifos:mifoslabel
										keyhm="Personnel.PostalCode"
										name="${ConfigurationConstants.POSTAL_CODE}" /></span>:</td>
									<td><mifos:mifosalphanumtext name="PersonnelSettingsActionForm"
										styleId="edityoursettings.input.postalCode"
										keyhm="Personnel.PostalCode"
										property="address.zip"
										value="${form.address.zip}"
										maxlength="100" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="edityoursettings.label.telephone"><mifos:mifoslabel
									keyhm="Personnel.Telephone" name="Personnel.Telephone" /></span></td>
									<td><mifos:mifosalphanumtext name="PersonnelSettingsActionForm"
										styleId="edityoursettings.input.telephone"
										keyhm="Personnel.Telephone"
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
									<td align="center"><html-el:submit styleId="edityoursettings.button.preview" styleClass="buttn">
										<mifos:mifoslabel name="button.preview"
											bundle="PersonnelUIResources"></mifos:mifoslabel>
									</html-el:submit> &nbsp; <html-el:button styleId="edityoursettings.button.cancel" property="cancelBtn"
										styleClass="cancelbuttn" onclick="goToCancelPage()">
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
