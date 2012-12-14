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
<!-- preview_ClientDetails.jsp -->

<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="/customer/customerfunctions" prefix="customerfn"%>

<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.ClientUIResources"/>

<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
	<span id="page.id" title="PreviewClientPersonalInfo"></span>
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<script language="javascript">

  function setClientStatus(status){
	disableButtons();
	document.getElementsByName("status")[0].value=status;
	clientCustActionForm.action="clientCustAction.do?method=create";
	clientCustActionForm.submit();

  }
  function disableButtons(){
	func_disableSubmitBtn('submitButton');
	func_disableSubmitBtn('submitButton1');
  }
  function goToCancelPage(){
	clientCustActionForm.action="clientCustAction.do?method=cancel";
	clientCustActionForm.submit();
  }
   function goToMfiPage(){
	clientCustActionForm.action="clientCustAction.do?method=prevMFIInfo";
	clientCustActionForm.submit();
  }
   function goToPersonalPage(){
	clientCustActionForm.action="clientCustAction.do?method=prevPersonalInfo";
	clientCustActionForm.submit();
  }
  
  function goToFamilyPage(){
	clientCustActionForm.action="clientCustAction.do?method=prevFamilyInfo";
	clientCustActionForm.submit();
  }

</script>
		<html-el:form action="clientCustAction.do?method=create">
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'CenterHierarchyExist')}" var="CenterHierarchyExist" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'meeting')}" var="meeting" />
			<html-el:hidden property="input" value="create" />
			<html-el:hidden property="status" value="" />
			<!-- Body begins -->
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="350" align="left" valign="top" bgcolor="#FFFFFF">
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td align="center" class="heading">&nbsp;</td>
						</tr>
					</table>
					<!-- Pipeline Information -->
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td class="bluetablehead">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td width="25%">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/tick.gif"
												width="17" height="17"></td>
											<td class="timelineboldgray"><mifos:mifoslabel
												name="client.select" bundle="ClientUIResources"></mifos:mifoslabel>
											<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />/<mifos:mifoslabel
												name="${ConfigurationConstants.BRANCHOFFICE}" /></td>
										</tr>
									</table>
									</td>
									<td width="25%" align="center">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/tick.gif"
												width="17" height="17"></td>
											<td class="timelineboldgray"><mifos:mifoslabel
												name="client.PersonalInformationHeading"
												bundle="ClientUIResources"></mifos:mifoslabel></td>
										</tr>
									</table>
									</td>
									<td width="25%" align="center">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/tick.gif"
												width="17" height="17"></td>
											<td class="timelineboldgray"><mifos:mifoslabel
												name="client.MFIInformationHeading"
												bundle="ClientUIResources"></mifos:mifoslabel></td>
										</tr>
									</table>
									</td>
									<td width="25%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/bigarrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorange"><mifos:mifoslabel
												name="client.ReviewSubmitHeading" bundle="ClientUIResources"></mifos:mifoslabel></td>
										</tr>
									</table>
									</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					<!-- Pipeline Information Ends--> <!-- Preview information begins -->
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0" class="bluetableborder">
						<tr>
							<td align="left" valign="top" class="paddingleftCreates"><!-- Preview page instruction -->
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td class="headingorange"><span class="heading">
										<fmt:message key="client.createNewClient">
											<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
										</fmt:message> </span> <mifos:mifoslabel
										name="client.CreatePreviewReviewSubmitTitle"
										bundle="ClientUIResources"></mifos:mifoslabel></td>
								</tr>
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="client.CreatePreviewPageInstruction"
										bundle="ClientUIResources"></mifos:mifoslabel> &nbsp; 
										<fmt:message key="client.PreviewEditCancelInstruction">
											<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
										</fmt:message>
									</td>
								</tr>
							</table>
							<!-- Preview page instruction ends--> <!-- Client information entered on the create page -->
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<!-- Warning Messages -->
								<tr>
									<td>
										<font class="fontnormalRedBold"><html-el:messages id="warningMessage" name="org.mifos.application.WARNING_MESSAGES" 
											bundle="ClientUIResources">
												<bean:write name="warningMessage"/>
											</html-el:messages></font>
									</td>
								</tr>
								<logic:messagesPresent>
								<!-- Error Messages -->
								<tr>
									<td>
									<br>
									<font class="fontnormalRedBold">
										<span id="preview_ClientDetails.error.message"><html-el:errors bundle="ClientUIResources" /></span>
									</font>
									</td>
								</tr>
								</logic:messagesPresent>
							</table>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td class="fontnormal"><span class="fontnormalbold"> 
										<fmt:message key="client.CreatePreviewPageTitle">
											<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}" /></fmt:param>
										</fmt:message></span>
									<c:choose>
										<c:when	test="${sessionScope.clientCustActionForm.groupFlag eq '1'}">
											<c:out value="${sessionScope.clientCustActionForm.officeName}" />
										</c:when>
										<c:otherwise>
											<c:out value="${sessionScope.clientCustActionForm.officeName}" />
										</c:otherwise>
										</c:choose>
									</td>
								</tr>
							</table>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td width="100%" height="23" class="fontnormalboldorange"><mifos:mifoslabel
										name="client.PersonalInformationHeading"
										bundle="ClientUIResources"></mifos:mifoslabel></td>
								</tr>
								<%-- Personal Information --%>
								<tr>
									<td class="fontnormalbold">
									<c:if test = "${sessionScope.clientCustActionForm.picture != null &&  sessionScope.clientCustActionForm.picture.fileName != ''}" >
									 	 <img src="clientCustAction.do?method=retrievePictureOnPreview&currentFlowKey=${requestScope.currentFlowKey}" width="150"/>
								    </c:if>
									</td></tr>
									<tr><td class="fontnormalbold"><mifos:mifoslabel name="client.Name"
										bundle="ClientUIResources"></mifos:mifoslabel> <span
										class="fontnormal">
										<c:forEach var="salutation" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'salutationEntity')}">
											<c:if test = "${salutation.id == sessionScope.clientCustActionForm.clientName.salutation}">
												<c:out value="${salutation.name}"/>
											</c:if>
										</c:forEach>
										<c:out	value="${sessionScope.clientCustActionForm.clientName.displayName}" /> <br>
									</span></td></tr>
									<tr id="Client.GovernmentId"><td class="fontnormalbold"><mifos:mifoslabel
										name="${ConfigurationConstants.GOVERNMENT_ID}" keyhm="Client.GovernmentId" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
									<span class="fontnormal"><c:out
										value="${sessionScope.clientCustActionForm.governmentId}" /> <br>
									</span></td></tr>
									<tr><td class="fontnormalbold"><mifos:mifoslabel name="client.DateOfBirth"
										bundle="ClientUIResources"></mifos:mifoslabel> <span
										class="fontnormal"><c:out
										value="${sessionScope.clientCustActionForm.dateOfBirth}" />
									<br>
									</span></td></tr>
									<tr><td class="fontnormalbold"><mifos:mifoslabel name="client.Age"
										bundle="ClientUIResources"></mifos:mifoslabel> <span
										class="fontnormal"><c:out
										value="${sessionScope.clientCustActionForm.age}" /> <br>
									</span></td></tr>
									<tr><td class="fontnormalbold"> <mifos:mifoslabel name="client.Gender"
										bundle="ClientUIResources"></mifos:mifoslabel> <span
										class="fontnormal">
										<c:forEach var="gender" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'genderEntity')}">
											<c:if test = "${gender.id == sessionScope.clientCustActionForm.clientDetailView.gender}">
												<c:out value="${gender.name}"/>
											</c:if>
										</c:forEach> <br>
									</span></td></tr>
									<tr><td class="fontnormalbold"><mifos:mifoslabel name="client.MaritalStatus"
										bundle="ClientUIResources"></mifos:mifoslabel> <span
										class="fontnormal">
										<c:forEach var="maritalStatus" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'maritalStatusEntity')}">
											<c:if test = "${maritalStatus.id == sessionScope.clientCustActionForm.clientDetailView.maritalStatus}">
												<c:out value="${maritalStatus.name}"/>
											</c:if>
										</c:forEach>
									<br>
									</span></td></tr>
									<tr><td class="fontnormalbold">
									 <c:choose>
										<c:when test="${sessionScope.clientCustActionForm.spouseName.nameType == 1}">
											<span class="fontnormalbold"><mifos:mifoslabel
												name="client.SpouseLabel" bundle="ClientUIResources"></mifos:mifoslabel></span>
										</c:when>
										<c:otherwise>
											<span class="fontnormalbold"><mifos:mifoslabel
												name="client.FatherLabel" bundle="ClientUIResources"></mifos:mifoslabel></span>
										</c:otherwise>
									</c:choose> <span class="fontnormal"> <c:out
										value="${sessionScope.clientCustActionForm.spouseName.displayName}" />
									<br>
									</span></td></tr>
									<tr><td class="fontnormalbold"><mifos:mifoslabel
										name="client.NumberOfChildren" bundle="ClientUIResources"></mifos:mifoslabel>
									<span class="fontnormal"> <c:out
										value="${sessionScope.clientCustActionForm.clientDetailView.numChildren}" />
									</span> <%-- Citizenship --%></td></tr>
									<tr id="Client.Citizenship"><td class="fontnormalbold"><mifos:mifoslabel
										name="${ConfigurationConstants.CITIZENSHIP}" keyhm="Client.Citizenship" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/> <span
										class="fontnormal">
										 <c:forEach var="citizenship" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'citizenshipEntity')}">
											<c:if test = "${citizenship.id == sessionScope.clientCustActionForm.clientDetailView.citizenship}">
												<c:out value="${citizenship.name}"/>
											</c:if>
										</c:forEach><br>
									</span> <%-- Ethnicity --%></td></tr>
									<tr id="Client.Ethnicity"><td class="fontnormalbold"><mifos:mifoslabel
										name="${ConfigurationConstants.ETHNICITY}" keyhm="Client.Ethnicity" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/> <span
										class="fontnormal">
										<c:forEach var="ethnicity" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'ethnicityEntity')}">
											<c:if test = "${ethnicity.id == sessionScope.clientCustActionForm.clientDetailView.ethnicity}">
												<c:out value="${ethnicity.name}"/>
											</c:if>
										</c:forEach><br>
									</span></td></tr>
									<tr id="Client.EducationLevel"><td class="fontnormalbold">
									<mifos:mifoslabel name="client.EducationLevel" bundle="ClientUIResources" keyhm="Client.EducationLevel" isManadatoryIndicationNotRequired="yes"/> <span
										class="fontnormal">
										<c:forEach var="educationLevel" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'educationLevelEntity')}">
											<c:if test = "${educationLevel.id == sessionScope.clientCustActionForm.clientDetailView.educationLevel}">
												<c:out value="${educationLevel.name}"/>
											</c:if>
										</c:forEach><br>
									</span></td></tr>
									<tr id="Client.BusinessActivities"><td class="fontnormalbold"><mifos:mifoslabel name="client.BusinessActivities"
										bundle="ClientUIResources" keyhm="Client.BusinessActivities" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <span
										class="fontnormal">
										<c:forEach var="businessActivities" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'businessActivitiesEntity')}">
											<c:if test = "${businessActivities.id == sessionScope.clientCustActionForm.clientDetailView.businessActivities}">
												<c:out value="${businessActivities.name}"/>
											</c:if>
										</c:forEach><br>
									</span></td></tr>
									<tr id="Client.PovertyStatus"><td class="fontnormalbold"><mifos:mifoslabel name="client.PovertyStatus"
										bundle="ClientUIResources" keyhm="Client.PovertyStatus" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <span
										class="fontnormal">
										<c:forEach var="povertyStatus" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'povertyStatus')}">
											<c:if test = "${povertyStatus.id == sessionScope.clientCustActionForm.clientDetailView.povertyStatus}">
												<c:out value="${povertyStatus.name}"/>
											</c:if>
										</c:forEach><br>
									</span></td></tr>
									<tr id="Client.Handicapped"><td class="fontnormalbold"><mifos:mifoslabel
										name="${ConfigurationConstants.HANDICAPPED}" keyhm="Client.Handicapped" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/> <span
										class="fontnormal">
										<c:forEach var="handicapped" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'handicappedEntity')}">
											<c:if test = "${handicapped.id == sessionScope.clientCustActionForm.clientDetailView.handicapped}">
												<c:out value="${handicapped.name}"/>
											</c:if>
										</c:forEach><br>
									</span> </td></tr>
									<tr id="Client.Address"><td class="fontnormalbold"><br>
									<mifos:mifoslabel name="client.Address" bundle="ClientUIResources" keyhm="Client.Address" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>

									<span class="fontnormal"><br>
									</span> <span class="fontnormal"> <c:out
										value="${sessionScope.clientCustActionForm.address.displayAddress}" /><br>
									</span></td></tr>
									<tr id="Client.City"><td class="fontnormalbold"><mifos:mifoslabel name="${ConfigurationConstants.CITY}" keyhm="Client.City" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/>
									<span class="fontnormal"> <c:out
										value="${sessionScope.clientCustActionForm.address.city}" />
									<br>
									</span></td></tr>
									<tr id="Client.State"><td class="fontnormalbold"><mifos:mifoslabel
										name="${ConfigurationConstants.STATE}" keyhm="Client.State" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/><span
										class="fontnormal"> <c:out
										value="${sessionScope.clientCustActionForm.address.state}" />
									<br>
									</span></td></tr>
									<tr id="Client.Country"><td class="fontnormalbold"><mifos:mifoslabel name="client.Country"
										bundle="ClientUIResources" keyhm="Client.Country" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <span
										class="fontnormal"> <c:out
										value="${sessionScope.clientCustActionForm.address.country}" /><br>
									</span></td></tr>
									<tr id="Client.PostalCode"><td class="fontnormalbold"><mifos:mifoslabel
										name="${ConfigurationConstants.POSTAL_CODE}" keyhm="Client.PostalCode" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
									<span class="fontnormal"><c:out
										value="${sessionScope.clientCustActionForm.address.zip}" />
									<br>
									</span> </td></tr>
									<tr id="Client.PhoneNumber"><td class="fontnormalbold"><br><mifos:mifoslabel name="client.Telephone"
										bundle="ClientUIResources" keyhm="Client.PhoneNumber" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <span
										class="fontnormal"><c:out
										value="${sessionScope.clientCustActionForm.address.phoneNumber}" />
									</span><br>

									<!--CustomField addition --> <span class="fontnormal">
									
									<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
										<tr><td height="23" class="fontnormalbold"><br><mifos:mifoslabel
											name="client.AdditionalInformationHeading"
											bundle="ClientUIResources"></mifos:mifoslabel><span></span> <span
											class="fontnormal"><br>
										</span>
										<c:forEach var="cf" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
											 <c:forEach var="customField" items="${sessionScope.clientCustActionForm.customFields}">
												<c:if test="${cf.fieldId==customField.fieldId}">
													<mifos:mifoslabel name="${cf.lookUpEntityType}" bundle="CenterUIResources" isColonRequired="yes"></mifos:mifoslabel>
										         	<span class="fontnormal"><c:out value="${customField.fieldValue}"/></span><br>
												</c:if>
											</c:forEach>
				    				  	</c:forEach>
									</c:if>
									<br>
									</span> </td></tr>
									
                                    <tr><td class="fontnormal">
                                       <b><mifos:mifoslabel
                                            name="client.Attachements"
                                            bundle="ClientUIResources"></mifos:mifoslabel></b>
                                        <ol id="filesToUpload">
		                                    <c:forEach var="fileToUpload" items="${sessionScope.clientCustActionForm.filesMetadata}">
		                                        <li id="${fileToUpload.name}">
		                                            <b>${fileToUpload.name}</b><br/>
		                                            ${fileToUpload.description}<br/><br/>
		                                        </li>
		                                    </c:forEach>
	                                    </ol>
                                    </td></tr>
									
									<tr><td>									
										<!-- Edit Button --> <html-el:button styleId="preview_ClientDetails.button.editPersonalInformation"
											onclick="goToPersonalPage()" property="editButton"
											styleClass="insidebuttn">
											<mifos:mifoslabel name="button.previousPersonalInfo"
												bundle="ClientUIResources"></mifos:mifoslabel>
										</html-el:button></td>
									</tr>
							</table>
							<%-- Personal Information end --%> <br>
							
							
							<%-- Family Information --%>
							<c:if test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'areFamilyDetailsRequired')}">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td width="100%" height="23" class="fontnormalboldorange"><mifos:mifoslabel
										name="client.FamilyInformationLabel" bundle="ClientUIResources"></mifos:mifoslabel>
									</td>
								</tr>
							</table>
							<table border="0" cellspacing="0" cellpadding="0">
									<tr class="fontnormal">
										<td>
											<span class="fontnormalbold">
										<mifos:mifoslabel name="client.FamilyRelationship" bundle="ClientUIResources"></mifos:mifoslabel>
											</span>
										</td>
										<td class="paddingL10">
											<span class="fontnormalbold">
											<mifos:mifoslabel	name="client.FamilyDisplayName"
											   bundle="ClientUIResources">
											   </mifos:mifoslabel></span>
										</td>
										<td class="paddingL10">
											<span class="fontnormalbold">
												<mifos:mifoslabel	name="client.FamilyDateOfBirth" 
													bundle="ClientUIResources"></mifos:mifoslabel>
											</span>
										</td>
										<td class="paddingL10">
											<span class="fontnormalbold">
												<mifos:mifoslabel	name="client.FamilyGender" 
													bundle="ClientUIResources"></mifos:mifoslabel>
											</span>
										</td>
										<td class="paddingL10">
											<span class="fontnormalbold">
												<mifos:mifoslabel name="client.FamilyLivingStatus"
													bundle="ClientUIResources">
												</mifos:mifoslabel>
											</span>
										</td>
									</tr>
							 		<c:forEach var="familyDetails" items="${sessionScope.clientCustActionForm.familyDetails}"> 
									<tr class="fontnormal">
										<td>
											<c:forEach var="familyEntity" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'spouseEntity')}">
												<c:if test = "${familyEntity.id == familyDetails.relationship}">
													<c:out value="${familyEntity.name}"/>
												</c:if>
											</c:forEach>
										</td>
										<td class="paddingL10"> <div id="displayName">
											<c:out value="${familyDetails.displayName}"/>	</div>   
										</td>		
										<td class="paddingL10">
											<c:out value="${familyDetails.dateOfBirthForBrowser}"/>
										</td>
										<td class="paddingL10">
											<c:forEach var="genderEntity" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'genderEntity')}">
											<c:if test = "${genderEntity.id == familyDetails.gender}">
												<c:out value="${genderEntity.name}"/>
											</c:if>
											</c:forEach>
										</td>
										<td class="paddingL10">
											<c:forEach var="livingStatusEntity" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'livingStatusEntity')}">
													<c:if test = "${livingStatusEntity.id ==familyDetails.livingStatus}">
														<c:out value="${livingStatusEntity.name}"/>
													</c:if>
											</c:forEach>
										</td>
									</tr>
									</c:forEach>
									<tr>
										<td>	
										<br>
										<!-- Edit Button --> 
											<html-el:button styleId="preview_ClientDetails.button.editFamilyInformation" onclick="goToFamilyPage()" property="editButton" styleClass="insidebuttn">
												<mifos:mifoslabel name="button.previousFamilyInfo" bundle="ClientUIResources"></mifos:mifoslabel>
											</html-el:button>
										</td>
									</tr>
							</table>
							<br>
							</c:if>
							<%-- MFI Information --%>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td width="100%" height="23" class="fontnormalboldorange"><mifos:mifoslabel
										name="client.MfiInformationLabel" bundle="ClientUIResources"></mifos:mifoslabel>
									</td>
								</tr>
								<tr>
									<td height="23" class="fontnormalbold"><span class="fontnormal"></span>
									<c:if test="${sessionScope.clientCustActionForm.groupFlag eq '0'}">
									<span class="fontnormalbold"> <mifos:mifoslabel
										name="client.LoanOfficer" bundle="ClientUIResources"></mifos:mifoslabel>
									</span>
									<c:forEach var="LO" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanOfficers')}">
										<c:if test = "${LO.personnelId == sessionScope.clientCustActionForm.loanOfficerId}">
												<span class="fontnormal"><c:out value="${LO.displayName}"/></span>
										</c:if>
									</c:forEach>
									<br>
									</c:if>
									<c:if test="${sessionScope.clientCustActionForm.groupFlag eq '1'}">
										<span class="fontnormalbold"> <mifos:mifoslabel	name="client.LoanOfficer" bundle="ClientUIResources"></mifos:mifoslabel></span>
										<span class="fontnormal">
											<c:out value="${sessionScope.clientCustActionForm.loanOfficerName}" /><br>
										</span>
										<c:if test="${CenterHierarchyExist == true}">
										<span class="fontnormalbold"> 
											<fmt:message key="client.Centers">
												<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /></fmt:param>
											</fmt:message></span>
										<span class="fontnormal"><c:out
											value="${sessionScope.clientCustActionForm.centerDisplayName}" /> <br>
										</span>
										</c:if>
										<span class="fontnormalbold">
											<fmt:message key="client.Centers">
												<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
											</fmt:message></span>
										<span class="fontnormal"> <c:out
											value="${sessionScope.clientCustActionForm.groupDisplayName}" /></span>
										<span class="fontnormal"><br>
										</span>

									</c:if> <span class="fontnormalbold"><mifos:mifoslabel
										name="client.FormedBy" bundle="ClientUIResources"></mifos:mifoslabel></span>
										<span class="fontnormal">
										<c:forEach var="formedByLO" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'formedByLoanOfficers')}">
										<c:if test = "${formedByLO.personnelId == sessionScope.clientCustActionForm.formedByPersonnel}">
											<c:out value="${formedByLO.displayName}"/><br>
										</c:if>
									</c:forEach>
									</span><br>
									<span class="fontnormalbold"><mifos:mifoslabel
										name="client.MeetingSchedule" bundle="ClientUIResources"></mifos:mifoslabel></span>								
										<c:choose>
											<c:when test="${sessionScope.clientCustActionForm.groupFlag eq '1'}">							
											<span class="fontnormal" id="preview_ClientDetails.text.meetingSchedule"><c:out
												value="${meeting.meetingSchedule}" /><br>
											</span>
											<span class="fontnormalbold"><mifos:mifoslabel
												name="client.LocationOfMeeting" bundle="ClientUIResources"></mifos:mifoslabel></span>
											<span class="fontnormal" id="preview_ClientDetails.text.meetingPlace"> <c:out
												value="${meeting.meetingPlace}" /><br>
											</span>
										</c:when>
										<c:otherwise>
										<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customerMeeting')}" var="clientMeeting" />
											<span class="fontnormal"><c:out
												value="${customerfn:getMeetingSchedule(clientMeeting,sessionScope.UserContext)}" />
											</span>
											<br>
											<span class="fontnormalbold"><mifos:mifoslabel
												name="client.LocationOfMeeting" bundle="ClientUIResources"></mifos:mifoslabel></span><span
												class="fontnormal"> <c:out
												value="${clientMeeting.meetingPlace}" /><br>
											</span>
										</c:otherwise>
									</c:choose>
									<br>
									</td></tr>
									<tr id="Client.ExternalId"><td class="fontnormalbold">
									<mifos:mifoslabel name="${ConfigurationConstants.EXTERNALID}" keyhm="Client.ExternalId" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/>
									<span class="fontnormal"> <c:out
										value="${sessionScope.clientCustActionForm.externalId}" /><br>
									</span></td></tr>
									<tr id="Client.Trained"><td class="fontnormalbold"><mifos:mifoslabel name="client.Trained"
										bundle="ClientUIResources" keyhm="Client.Trained" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <c:choose>
										<c:when test="${sessionScope.clientCustActionForm.trained eq '1'}">
											<span class="fontnormal"><mifos:mifoslabel
												name="client.YesLabel" bundle="ClientUIResources"></mifos:mifoslabel><br>
											</span>
										</c:when>
										<c:otherwise>
											<span class="fontnormal"><mifos:mifoslabel
												name="client.NoLabel" bundle="ClientUIResources"></mifos:mifoslabel><br>
											</span>
										</c:otherwise>
									</c:choose></td></tr>
									<tr id="Client.TrainedDate"><td class="fontnormalbold"> <mifos:mifoslabel name="client.TrainedOnDate"
										bundle="ClientUIResources" keyhm="Client.TrainedDate" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <span
										class="fontnormal"> <c:out
										value="${sessionScope.clientCustActionForm.trainedDate}" />
									<br>
									</span><br></td></tr>
									<tr><td class="fontnormalbold">
									<c:if test="${!empty sessionScope.clientCustActionForm.defaultFees}">
									<mifos:mifoslabel name="client.ChargesApplied"
										bundle="ClientUIResources"></mifos:mifoslabel> <span
										class="fontnormal"><br>
									</span>
									 <c:forEach var="adminFee" items="${sessionScope.clientCustActionForm.defaultFees}">
										<c:if test="${adminFee.removed == false}">
									  		 <c:out value="${adminFee.feeName}"/>:
									   		<span class="fontnormal">
									   			<fmt:formatNumber value="${adminFee.amount}"/>
									   			<mifos:mifoslabel name="Center.Periodicity" bundle="CenterUIResources"/>
										   		<c:choose>
													<c:when test="${adminFee.periodic}">
														<c:out value="${adminFee.feeSchedule}"/>
													</c:when>
													<c:otherwise>
														<mifos:mifoslabel name="Fees.onetime"/>
													</c:otherwise>
												</c:choose>
											</span><br>
										</c:if>
									</c:forEach>
									</c:if>
									<c:forEach var="fee" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'additionalFeeList')}" >
										<c:forEach var="selectedFee" items="${sessionScope.clientCustActionForm.additionalFees}" >
											<c:if test="${fee.feeId == selectedFee.feeId}">
							           	  		<c:out value="${fee.feeName}"/>:
												<span class="fontnormal"><span class="fontnormal">
												<fmt:formatNumber value="${selectedFee.amount}"/>
												<mifos:mifoslabel name="Center.Periodicity" bundle="CenterUIResources"/>
												<c:choose>
													<c:when test="${fee.periodic == true}">
														<c:out value="${fee.feeSchedule}"/>
													</c:when>
													<c:otherwise>
														<mifos:mifoslabel name="Fees.onetime"/>
													</c:otherwise>
												</c:choose></span><br></span>
											</c:if>
										</c:forEach>
									</c:forEach>
									</td>
								</tr>
								<!--  savings offerings -->
								<tr>
									<td class="fontnormalbold"><br>
										<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
										<mifos:mifoslabel name="client.accounts" bundle="ClientUIResources"/>
										<br>
										 <c:forEach var="selectedOffering" items="${sessionScope.clientCustActionForm.selectedOfferings}">
											<c:forEach var="offering" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'savingsOfferingList')}">
												<c:if test="${selectedOffering == offering.prdOfferingId}">
													<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
													<mifos:mifoslabel name="client.instanceName" bundle="ClientUIResources" isColonRequired="yes"/>
														<span class="fontnormal">
														<c:out value="${offering.prdOfferingName}"/>
													</span><br>
												</c:if>
											</c:forEach>
										</c:forEach>
									<br>
									<!-- Additional Fees preview end --> <!-- Edit MFI Detail Button -->
									<html-el:button styleId="preview_ClientDetails.button.editMfiInfo" onclick="goToMfiPage()" property="editButton"
										styleClass="insidebuttn">
										<mifos:mifoslabel name="button.previousMFIInfo"
											bundle="ClientUIResources"></mifos:mifoslabel>
									</html-el:button>
									</td>
								</tr>
                                <tr>
                                   <td>
                                        <c:set var="questionsHostForm" value="${clientCustActionForm}" scope="request" />
                                        <c:import url="/pages/application/surveys/jsp/viewQuestionResponses.jsp">
                                           <c:param name="editResponseURL" value="clientCustAction.do?method=editQuestionResponses&currentFlowKey=${requestScope.currentFlowKey}"/>
                                           <c:param name="responseDivStyleClass" value="viewQuestionResponseDiv"/>
                                        </c:import>
                                   </td>
                                </tr>
							</table>

							<!-- Submit and cancel buttons -->
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center" class="blueline">&nbsp;</td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center">&nbsp; <html-el:button styleId="preview_ClientDetails.button.saveForLater"
										property="submitButton1" styleClass="buttn"
										onclick="setClientStatus('1');">
										<mifos:mifoslabel name="button.SaveForLater"
											bundle="ClientUIResources"></mifos:mifoslabel>
									</html-el:button> &nbsp; &nbsp;
									<c:choose>
										<c:when test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'pendingApprovalDefined') eq 'Yes'}">
											<html-el:button styleId="preview_ClientDetails.button.submitForApproval" property="submitButton" styleClass="buttn"
												onclick="setClientStatus('2');">
												<mifos:mifoslabel name="button.SubmitForApproval"
													bundle="ClientUIResources"></mifos:mifoslabel>
											</html-el:button>
										</c:when>
										<c:otherwise>
											<html-el:button styleId="preview_ClientDetails.button.approve" property="submitButton" styleClass="buttn"
												onclick="setClientStatus('3');">
												<mifos:mifoslabel name="button.Approved"
													bundle="ClientUIResources"></mifos:mifoslabel>
											</html-el:button>
										</c:otherwise>
									</c:choose> &nbsp; &nbsp; <html-el:button styleId="preview_ClientDetails.button.cancel"
										onclick="goToCancelPage();" property="cancelButton"
										styleClass="cancelbuttn">
										<mifos:mifoslabel name="button.cancel"
											bundle="ClientUIResources"></mifos:mifoslabel>
									</html-el:button></td>
								</tr>
							</table>
							<!-- Submit and cancel buttons end --> <br>
							<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
							</td>
						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>
		</html-el:form>
	</tiles:put>
</tiles:insert>

