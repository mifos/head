<!--
/**

* previewEditClientPersonalInfo.jsp    version: 1.0



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
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<tiles:insert definition=".detailsCustomer">
	<tiles:put name="body" type="string">
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>

		<script language="javascript">
  
  function goToPersonalPage(){
	clientCustActionForm.action="clientCustAction.do?method=prevEditPersonalInfo";
	clientCustActionForm.submit();
  }
  function goToCancelPage(){
	clientCustActionForm.action="clientCustAction.do?method=cancel";
	clientCustActionForm.submit();
  }
</script>
		<html-el:form action="clientCustAction.do?method=updatePersonalInfo"
			onsubmit="func_disableSubmitBtn('submitButton');">
			<html-el:hidden property="input" value="editPersonalInfo" />
			<td align="left" valign="top" bgcolor="#FFFFFF"
				class="paddingleftmain">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <customtags:headerLink/> </span>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange"><span class="heading"> <c:out
								value="${sessionScope.clientCustActionForm.clientName.displayName}" /> - </span><mifos:mifoslabel
								name="client.EditPreviewPersonalReviewTitle"
								bundle="ClientUIResources"></mifos:mifoslabel> <span></span></td>
							<td></td>
						</tr>
						<tr>
							<td class="fontnormal"><mifos:mifoslabel
								name="client.CreatePreviewPageInstruction"
								bundle="ClientUIResources"></mifos:mifoslabel> &nbsp; <mifos:mifoslabel
								name="client.EditPageCancelInstruction1"
								bundle="ClientUIResources"></mifos:mifoslabel> <mifos:mifoslabel
								name="${ConfigurationConstants.CLIENT}"></mifos:mifoslabel> <mifos:mifoslabel
								name="client.EditPageCancelInstruction2"
								bundle="ClientUIResources"></mifos:mifoslabel></td>
						</tr>
						<tr>
							<td><font class="fontnormalRedBold"><html-el:errors
								bundle="ClientUIResources" /></font></td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="50%" height="23" class="fontnormalboldorange"><mifos:mifoslabel
								name="client.PersonalInformationHeading"
								bundle="ClientUIResources"></mifos:mifoslabel></td>
						</tr>
						<tr>
							<td class="fontnormalbold">
								<c:if test="${sessionScope.noPicture eq 'No'}">
								<img src="/Mifos/clientCustAction.do?method=retrievePictureOnPreview"
									height="100" width="150" />

							</c:if> <br>
							</td></tr>
						<tr><td class="fontnormalbold">
							<mifos:mifoslabel name="client.Name" bundle="ClientUIResources"></mifos:mifoslabel>
							<span class="fontnormal"> 
								<c:forEach var="salutation" items="${sessionScope.salutationEntity}">
									<c:if test = "${salutation.id == sessionScope.clientCustActionForm.clientName.salutation}">
										<c:out value="${salutation.name}"/>
									</c:if>
								</c:forEach> 
								<c:out	value="${sessionScope.clientCustActionForm.clientName.displayName}" />
							<br>
							</span>
						</td></tr> 
									<tr id="Client.GovernmentId"><td class="fontnormalbold"> <mifos:mifoslabel
								name="${ConfigurationConstants.GOVERNMENT_ID}" keyhm="Client.GovernmentId" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
							<span class="fontnormal"><c:out
										value="${sessionScope.clientCustActionForm.governmentId}" /><br>
							</span> 
						</td></tr>
									<tr><td class="fontnormalbold"><mifos:mifoslabel name="client.DateOfBirth"
								bundle="ClientUIResources"></mifos:mifoslabel> <span
								class="fontnormal">
									<c:out	value="${sessionScope.clientCustActionForm.dateOfBirth}" />
							<br>
							</span> 
					</td></tr>
									<tr><td class="fontnormalbold"><mifos:mifoslabel name="client.Age"
								bundle="ClientUIResources"></mifos:mifoslabel> <span
								class="fontnormal"><c:out
										value="${sessionScope.clientCustActionForm.age}" /><br>
							</span> 
					</td></tr>
									<tr><td class="fontnormalbold"><mifos:mifoslabel name="client.Gender"
								bundle="ClientUIResources"></mifos:mifoslabel> <span
								class="fontnormal"><c:forEach var="gender" items="${sessionScope.genderEntity}">
											<c:if test = "${gender.id == sessionScope.clientCustActionForm.clientDetailView.gender}">
												<c:out value="${gender.name}"/>
											</c:if>
										</c:forEach>  <br>
							</span> 
						</td>
						</tr>
						<tr>
							<td class="fontnormalbold">
								<mifos:mifoslabel name="client.MaritalStatus" bundle="ClientUIResources"></mifos:mifoslabel> 
									<span class="fontnormal">
										<c:forEach var="maritalStatus" items="${sessionScope.maritalStatusEntity}">
											<c:if test = "${maritalStatus.id == sessionScope.clientCustActionForm.clientDetailView.maritalStatus}">
												<c:out value="${maritalStatus.name}"/>
											</c:if>
										</c:forEach><br>
									</span>
							</td>
						</tr>
						<tr>
							<td class="fontnormalbold"> 
								<c:choose>
									<c:when test="${sessionScope.clientCustActionForm.spouseName.nameType == 1}">
										<span class="fontnormalbold">
										<mifos:mifoslabel name="client.SpouseLabel" bundle="ClientUIResources"></mifos:mifoslabel></span>
									</c:when>
									<c:otherwise>
										<span class="fontnormalbold">
										<mifos:mifoslabel name="client.FatherLabel" bundle="ClientUIResources"></mifos:mifoslabel></span>
									</c:otherwise>
								</c:choose> 
								<span class="fontnormal"> <c:out value="${sessionScope.clientCustActionForm.spouseName.displayName}" /><br></span>
							</td>
						</tr>
						<tr>
							<td class="fontnormalbold">
								<mifos:mifoslabel name="client.NumberOfChildren" bundle="ClientUIResources"></mifos:mifoslabel>
								<span class="fontnormal"> 
								<c:out	value="${sessionScope.clientCustActionForm.clientDetailView.numChildren}" /><br>
								</span> 
							</td>
						</tr>
						<%-- Citizenship --%> 
						<tr id="Client.Citizenship">
							<td class="fontnormalbold">
								<mifos:mifoslabel name="${ConfigurationConstants.CITIZENSHIP}" keyhm="Client.Citizenship" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
								<span class="fontnormal"> 
								<c:forEach var="citizenship" items="${sessionScope.citizenshipEntity}">
									<c:if test = "${citizenship.id == sessionScope.clientCustActionForm.clientDetailView.citizenship}">
										<c:out value="${citizenship.name}"/>
									</c:if>
								</c:forEach><br></span> 
							</td>
						</tr>
						<tr id="Client.Ethinicity">
							<td class="fontnormalbold">
								<mifos:mifoslabel name="${ConfigurationConstants.ETHINICITY}" keyhm="Client.Ethinicity" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
								<span class="fontnormal"> 
								<c:forEach var="ethinicity" items="${sessionScope.ethinicityEntity}">
									<c:if test = "${ethinicity.id == sessionScope.clientCustActionForm.clientDetailView.ethinicity}">
										<c:out value="${ethinicity.name}"/>
									</c:if>
								</c:forEach><br></span> 
							</td>
						</tr>
						<tr id="Client.EducationLevel">
							<td class="fontnormalbold">
								<mifos:mifoslabel name="client.EducationLevel"	bundle="ClientUIResources" keyhm="Client.EducationLevel" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> 
								<span class="fontnormal"> 
									<c:forEach var="educationLevel" items="${sessionScope.educationLevelEntity}">
										<c:if test = "${educationLevel.id == sessionScope.clientCustActionForm.clientDetailView.educationLevel}">
											<c:out value="${educationLevel.name}"/>
										</c:if>
									</c:forEach><br></span> 
							</td>
						</tr>
						<tr id="Client.BusinessActivities">
							<td class="fontnormalbold">
								<mifos:mifoslabel name="client.BusinessActivities"	bundle="ClientUIResources" keyhm="Client.BusinessActivities" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> 
								<span class="fontnormal"> 
								<c:forEach var="businessActivities" items="${sessionScope.businessActivitiesEntity}">
									<c:if test = "${businessActivities.id == sessionScope.clientCustActionForm.clientDetailView.businessActivities}">
										<c:out value="${businessActivities.name}"/>
									</c:if>
								</c:forEach><br></span> 
							</td>
						</tr>
						<tr id="Client.Handicapped">
							<td class="fontnormalbold">
								<mifos:mifoslabel name="${ConfigurationConstants.HANDICAPED}" keyhm="Client.Handicapped" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
								<span class="fontnormal"> 
									<c:forEach var="handicapped" items="${sessionScope.handicappedEntity}">
										<c:if test = "${handicapped.id == sessionScope.clientCustActionForm.clientDetailView.handicapped}">
											<c:out value="${handicapped.name}"/>
										</c:if>
									</c:forEach>
								</span> 
							</td>
						</tr>
						<tr id="Client.Address">
							<td class="fontnormalbold"><br>
								<mifos:mifoslabel name="client.Address" bundle="ClientUIResources" keyhm="Client.Address" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
								<span class="fontnormal"><br></span>
								<span class="fontnormal"> <c:out value="${sessionScope.clientCustActionForm.address.displayAddress}" /></span> 
							</td>
						</tr>
						<tr id="Client.City">
							<td class="fontnormalbold">
								<mifos:mifoslabel name="${ConfigurationConstants.CITY}" keyhm="Client.City" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
								<span class="fontnormal"> 
									<c:out value="${sessionScope.clientCustActionForm.address.city}" /> 
								</span> 
							</td>
						</tr>
									<tr id="Client.State"><td class="fontnormalbold"><mifos:mifoslabel name="${ConfigurationConstants.STATE}" keyhm="Client.State" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
							<span class="fontnormal"> <c:out
								value="${sessionScope.clientCustActionForm.address.state}" />
							
							</span> 
						</td></tr>
									<tr id="Client.Country"><td class="fontnormalbold"><mifos:mifoslabel name="client.Country"
								bundle="ClientUIResources" keyhm="Client.Country" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <span
								class="fontnormal"> <c:out
								value="${sessionScope.clientCustActionForm.address.country}" />
							</span> 
						</td></tr>
									<tr id="Client.PostalCode"><td class="fontnormalbold"><mifos:mifoslabel
								name="${ConfigurationConstants.POSTAL_CODE}" keyhm="Client.PostalCode" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
							<span class="fontnormal"><c:out
								value="${sessionScope.clientCustActionForm.address.zip}" /> 
							</span> 
						</td></tr>
									<tr id="Client.PhoneNumber"><td class="fontnormalbold"><br><mifos:mifoslabel name="client.Telephone"
								bundle="ClientUIResources" keyhm="Client.PhoneNumber" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <span
								class="fontnormal"><c:out
								value="${sessionScope.clientCustActionForm.address.phoneNumber}" />
							</span>

							<!--CustomField addition --> 
						</td></tr>
									<tr><td height="23" class="fontnormalbold"><br><mifos:mifoslabel
								name="client.AdditionalInformationHeading"
								bundle="ClientUIResources"></mifos:mifoslabel><span></span> 
								<br>
							 <c:forEach var="cf" items="${sessionScope.customFields}">
								<c:forEach var="customField"
									items="${sessionScope.clientCustActionForm.customFields}">
									<c:if test="${cf.fieldId==customField.fieldId}">
										<mifos:mifoslabel name="${cf.lookUpEntity.entityType}"
											bundle="ClientUIResources"></mifos:mifoslabel>: 
		                	 <span class="fontnormal">	<c:out value="${customField.fieldValue}" /></span>
										<br>
									</c:if>
								</c:forEach>
							</c:forEach></span> <br>
							<!-- Edit Button --> <html-el:button onclick="goToPersonalPage()"
								property="editButton" styleClass="insidebuttn">
								<mifos:mifoslabel name="button.previousPersonalInfo"
									bundle="ClientUIResources"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
					</table>
					<%-- Personal Information end --%> <!-- Submit and cancel buttons -->
					<table width="93%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="93%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center">&nbsp; <html-el:submit property="submitButton"
								style="width:70px" styleClass="buttn">
								<mifos:mifoslabel name="button.submit"
									bundle="ClientUIResources"></mifos:mifoslabel>
							</html-el:submit> &nbsp; &nbsp; <html-el:button
								onclick="goToCancelPage();" property="cancelButton"
								styleClass="cancelbuttn" style="width:70px">
								<mifos:mifoslabel name="button.cancel"
									bundle="ClientUIResources"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
					</table>
					<!-- Submit and cancel buttons end --></td>
				</tr>
			</table>
			<br>
			</td>
			<tr></tr>
			<table></table>
		</html-el:form>

	</tiles:put>
</tiles:insert>

