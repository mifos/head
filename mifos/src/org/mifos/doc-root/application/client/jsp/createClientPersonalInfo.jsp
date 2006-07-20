<!--
/**

* createClientPersonalInfo.jsp    version: 1.0



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
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-bean-el" prefix="bean-el"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>


<!-- Tile  definitions -->
<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
		<script language="javascript" SRC="pages/framework/js/date.js"></script>
		<script language="javascript" SRC="pages/framework/js/conversion.js"></script>
		<script language="javascript">

	
  function goToCancelPage(){
	clientCreationActionForm.action="clientCreationAction.do?method=cancel";
	clientCreationActionForm.submit();
  }
  
  function goToPreviewPage(){
	document.getElementById("nextOrPreview").value='preview';
	clientCreationActionForm.action="clientCreationAction.do?method=preview";
	if( chkForValidDates()){
		clientCreationActionForm.submit();
	}

  }
  function chkForValidDates(){
			var dateOfBirth = document.getElementById("dateOfBirth");	  
	  	 	var dateOfBirthFormat = document.getElementById("dateOfBirthFormat");	  
	  	 	var dateOfBirthYY = document.getElementById("dateOfBirthYY");	  
			if(! (validateMyForm(dateOfBirth,dateOfBirthFormat,dateOfBirthYY)))
				return false;
			if (clientCreationActionForm.fieldTypeList.length!= undefined && clientCreationActionForm.fieldTypeList.length!= null){ 	
				for(var i=0; i <=clientCreationActionForm.fieldTypeList.length;i++){
					if (clientCreationActionForm.fieldTypeList[i]!= undefined){
						if(clientCreationActionForm.fieldTypeList[i].value == "3"){
							var customFieldDate = document.getElementById("customField["+i+"].fieldValue");
							var customFieldDateFormat = document.getElementById("customField["+i+"].fieldValueFormat");	  
						 	var customFieldDateYY = document.getElementById("customField["+i+"].fieldValueYY");	  
							var dateValue = customFieldDate.value;
							if(!(validateMyForm(customFieldDate,customFieldDateFormat,customFieldDateYY)))
								return false;
						}
					}
			 	}
		 	}
			return true;
  }
</script>


		<html-el:form action="/clientCreationAction.do?method=next"
			method="post" enctype="multipart/form-data"
			onsubmit="return chkForValidDates()">
			<!-- Hidden varaibles for the input page -->
			<html-el:hidden property="input" value="personalInfo" />
			<html-el:hidden property="nextOrPreview" value="next" />
			<html-el:hidden property="office.officeId"
				value="${requestScope.clientVO.office.officeId}" />
			<html-el:hidden property="office.officeName"
				value="${requestScope.clientVO.office.officeName}" />
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="350" align="left" valign="top" bgcolor="#FFFFFF">
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td align="center" class="heading">&nbsp;</td>
						</tr>
					</table>
					<!-- Pipeline Bar -->
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
													name="${ConfigurationConstants.BRANCHOFFICE}" />
											</td>
										</tr>
									</table>
									</td>
									<td width="25%" align="center">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/bigarrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorange"><mifos:mifoslabel
												name="client.PersonalInformationHeading"
												bundle="ClientUIResources"></mifos:mifoslabel></td>
										</tr>
									</table>
									</td>
									<td width="25%" align="center">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorangelight"><mifos:mifoslabel
												name="client.MFIInformationHeading"
												bundle="ClientUIResources"></mifos:mifoslabel></td>
										</tr>
									</table>
									</td>
									<td width="25%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorangelight"><mifos:mifoslabel
												name="client.ReviewSubmitHeading" bundle="ClientUIResources"></mifos:mifoslabel></td>
										</tr>
									</table>
									</td>
								</tr>
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
									<td class="headingorange"><%-- Displaying the heading of the page based on whether the user is comign first time or to edit the information --%>
									<c:choose>
										<c:when test="${param.method eq 'prevPersonalInfo'}">
											<span class="heading"> <mifos:mifoslabel
												name="client.ManageTitle" bundle="ClientUIResources"></mifos:mifoslabel>
											<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />
											</span>
											<mifos:mifoslabel name="client.EditPersonalInformationTitle"
												bundle="ClientUIResources"></mifos:mifoslabel>

										</c:when>
										<c:otherwise>
											<span class="heading"> <mifos:mifoslabel
												name="client.CreateTitle" bundle="ClientUIResources"></mifos:mifoslabel> 
											<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /> </span>
											<mifos:mifoslabel
												name="client.CreatePersonalInformationTitle"
												bundle="ClientUIResources"></mifos:mifoslabel>
										</c:otherwise>
									</c:choose></td>
								</tr>
								<!-- Instructions for the user -->
								<tr>
									<td class="fontnormal"><c:choose>
										<c:when test="${param.method eq 'prevPersonalInfo'}">
											<mifos:mifoslabel name="client.PreviewEditInfoInstruction"
												bundle="ClientUIResources"></mifos:mifoslabel>
											<mifos:mifoslabel name="client.PreviewEditCancelInstruction1"
												bundle="ClientUIResources"></mifos:mifoslabel>
											<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />
											<mifos:mifoslabel name="client.PreviewEditCancelInstruction2"
												bundle="ClientUIResources"></mifos:mifoslabel>
											<span class="mandatorytext"><font color="#FF0000">*</font></span>
											<mifos:mifoslabel name="client.FieldInstruction"
												bundle="ClientUIResources"></mifos:mifoslabel>
										</c:when>
										<c:otherwise>
											<mifos:mifoslabel
												name="client.CreatePersonalInformationInstruction"
												bundle="ClientUIResources"></mifos:mifoslabel>
											<mifos:mifoslabel name="client.CreatePageCancelInstruction1"
												bundle="ClientUIResources"></mifos:mifoslabel>
											
											<br>
											<span class="mandatorytext"><font color="#FF0000">*</font></span>
											<mifos:mifoslabel name="client.FieldInstruction"
												bundle="ClientUIResources"></mifos:mifoslabel>
										</c:otherwise>
									</c:choose></td>
								</tr>
							</table>
							<br>
							<%-- Group information if client belongs to group --%> <c:choose>
								<c:when
									test="${sessionScope.clientCreationActionForm.isClientUnderGrp == 1}">
									<logic:messagesPresent>
										<table width="93%" border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td><font class="fontnormalRedBold"><html-el:errors
													bundle="ClientUIResources" /> </font></td>
											</tr>
										</table>
										<br>
									</logic:messagesPresent>
									<!-- Displaying the selected office name -->
									<table width="93%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="fontnormal"><span class="fontnormalbold"> <mifos:mifoslabel
												name="${ConfigurationConstants.BRANCHOFFICE}" /> <mifos:mifoslabel
												name="client.BranchSelected" bundle="ClientUIResources"></mifos:mifoslabel></span>
											<c:out value="${requestScope.clientVO.office.officeName}" /></td>
										</tr>
										<tr>
											<td><br>
											<span class="fontnormalbold"><mifos:mifoslabel
												name="client.LoanOfficer" bundle="ClientUIResources"></mifos:mifoslabel></span>
											<span class="fontnormal"> <c:out
												value="${requestScope.clientVO.parentCustomer.personnel.displayName}" />
											</span> <html-el:hidden property="loanOfficerId"
												value="${requestScope.clientVO.parentCustomer.personnel.personnelId}" />
											</td></tr>
											<c:choose>
												<c:when test="${sessionScope.isCenterHeirarchyExists==Constants.YES}">
												<tr>
											<td>
													<span class="fontnormalbold">
														<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
														<mifos:mifoslabel name="client.Centers" bundle="ClientUIResources" />
													</span>
													<span class="fontnormal">
														<c:out value="${requestScope.clientVO.parentCustomer.parentCustomer.displayName}" />
													</span></tr>
										<tr>
											<td>
													<span class="fontnormalbold">
														<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />
														<mifos:mifoslabel name="client.Centers" bundle="ClientUIResources" />
													</span>
													<span class="fontnormal">
														<c:out value="${requestScope.clientVO.parentCustomer.displayName}" />
													</span></td></tr>
												</c:when>
												<c:otherwise><tr>
											<td>
													<span class="fontnormalbold">
														<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />
														<mifos:mifoslabel name="client.Centers" bundle="ClientUIResources" />
													</span>
													<span class="fontnormal">
														<c:out value="${requestScope.clientVO.parentCustomer.displayName}" />
													</span></td></tr>
												</c:otherwise>
											</c:choose>
											<tr>
											<td class="fontnormal">	
											<span class="fontnormalbold"><mifos:mifoslabel
												name="client.MeetingSchedule" bundle="ClientUIResources"></mifos:mifoslabel></span>
											<span class="fontnormal"> <c:out
												value="${requestScope.clientVO.customerMeeting.meeting.meetingSchedule}" />
											<br>
											</span> <span class="fontnormalbold"><mifos:mifoslabel
												name="client.LocationOfMeeting" bundle="ClientUIResources"></mifos:mifoslabel></span>
											<span class="fontnormal"> <c:out
												value="${requestScope.clientVO.customerMeeting.meeting.meetingPlace}" />
											<br>
											</span></td>

										</tr>


									</table>
								</c:when>
								<c:otherwise>
									<table width="93%" border="0" cellspacing="0" cellpadding="3">
										<font class="fontnormalRedBold"><html-el:errors
											bundle="ClientUIResources" /> </font>

										<!-- Displaying the selected office name -->
										<tr>
											<td class="fontnormal"><span class="fontnormalbold"> <mifos:mifoslabel
												name="${ConfigurationConstants.BRANCHOFFICE}" /> <mifos:mifoslabel
												name="client.BranchSelected" bundle="ClientUIResources"></mifos:mifoslabel></span>
											<c:out value="${requestScope.clientVO.office.officeName}" /></td>
										</tr>
									</table>

								</c:otherwise>
							</c:choose> <br>
							<%-- Personal Information Heading --%>
							<table width="93%" border="0" cellpadding="3" cellspacing="0">


								<tr>
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="client.PersonalInformationHeading"
										bundle="ClientUIResources"></mifos:mifoslabel><br>
									<br>
									</td>
								</tr>
								<%-- Salutation --%>
								<tr class="fontnormal">
									<td width="17%" align="right"><mifos:mifoslabel
										name="client.Salutation" mandatory="yes"
										bundle="ClientUIResources"></mifos:mifoslabel></td>
									<td width="83%"><c:set var="salutationList" scope="request"
										value="${requestScope.salutationEntity.lookUpMaster}" /> <mifos:select
										name="clientCreationActionForm"
										property="customerNameDetail[0].salutation" size="1">
										<html-el:options collection="salutationList"
											property="lookUpId" labelProperty="lookUpValue" />
									</mifos:select></td>
								</tr>
								<%-- First Name --%>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="client.FirstName"
										mandatory="yes" bundle="ClientUIResources"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext name="clientCreationActionForm"
										property="customerNameDetail[0].firstName" maxlength="200" /></td>
								</tr>
								<%-- Middle Name --%>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel keyhm="Client.MiddleName" name="client.MiddleName"
										bundle="ClientUIResources"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext keyhm="Client.MiddleName" name="clientCreationActionForm"
										property="customerNameDetail[0].middleName" maxlength="200" /></td>
								</tr>
								<%-- Second Last Name --%>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel keyhm="Client.SecondLastName"
										name="client.SecondLastName" bundle="ClientUIResources"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext keyhm="Client.SecondLastName" name="clientCreationActionForm"
										property="customerNameDetail[0].secondLastName"
										maxlength="200" /></td>
								</tr>
								<%-- Last Name --%>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="client.LastName"
										mandatory="yes" bundle="ClientUIResources"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext name="clientCreationActionForm"
										property="customerNameDetail[0].lastName" maxlength="200" /></td>
								</tr>
								<html-el:hidden property="customerNameDetail[0].nameType"
									value="3" />
								<%-- Government Id --%>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel keyhm="Client.GovernmentId" isColonRequired="yes"
										name="${ConfigurationConstants.GOVERNMENT_ID}" bundle="ClientUIResources"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext keyhm="Client.GovernmentId" name="clientCreationActionForm"
										property="governmentId" maxlength="50" /></td>
								</tr>
								<%-- Date Of  Birth  --%>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="client.DateOfBirth"
										mandatory="yes" bundle="ClientUIResources"></mifos:mifoslabel></td>
									<td><date:datetag property="dateOfBirth" /></td>
								</tr>
								<%-- Gender --%>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="client.Gender"
										mandatory="yes" bundle="ClientUIResources"></mifos:mifoslabel></td>

									<td><c:set var="genderList" scope="request"
										value="${requestScope.genderEntity.lookUpMaster}" /> <mifos:select
										name="clientCreationActionForm"
										property="customerDetail.gender" size="1">
										<html-el:options collection="genderList" property="lookUpId"
											labelProperty="lookUpValue" />
									</mifos:select></td>
								</tr>
								<%-- Marital Status --%>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="client.MaritalStatus"
										bundle="ClientUIResources"></mifos:mifoslabel></td>

									<td><c:set var="maritalStatusList" scope="request"
										value="${requestScope.maritalStatusEntity.lookUpMaster}" /> <mifos:select
										name="clientCreationActionForm"
										property="customerDetail.maritalStatus" size="1">
										<html-el:options collection="maritalStatusList"
											property="lookUpId" labelProperty="lookUpValue" />
									</mifos:select></td>
								</tr>
								<%-- Number Of Children--%>
								<tr class="fontnormal">
									<td align="right" class="fontnormal"><mifos:mifoslabel
										name="client.NumberOfChildren" bundle="ClientUIResources"></mifos:mifoslabel></td>
									<td><mifos:mifosnumbertext name="clientCreationActionForm"
										property="customerDetail.numChildren" maxlength="5" size="10" /></td>
								</tr>
								<%-- Citizenship --%>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel keyhm="Client.Citizenship" isColonRequired="yes"
										name="${ConfigurationConstants.CITIZENSHIP}" ></mifos:mifoslabel></td>
									<td><c:set var="citizenshipList" scope="request"
										value="${requestScope.citizenshipEntity.lookUpMaster}" /> <mifos:select keyhm="Client.Citizenship"
										name="clientCreationActionForm"
										property="customerDetail.citizenship" size="1">
										<html-el:options collection="citizenshipList"
											property="lookUpId" labelProperty="lookUpValue" />
									</mifos:select></td>

								</tr>
								<%-- Ethinicity --%>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel keyhm="Client.Ethinicity" isColonRequired="yes"
										name="${ConfigurationConstants.ETHINICITY}" bundle="ClientUIResources"></mifos:mifoslabel></td>
									<td><c:set var="ethinicityList" scope="request"
										value="${requestScope.ethinicityEntity.lookUpMaster}" /> <mifos:select keyhm="Client.Ethinicity"
										name="clientCreationActionForm"
										property="customerDetail.ethinicity" size="1">
										<html-el:options collection="ethinicityList"
											property="lookUpId" labelProperty="lookUpValue" />
									</mifos:select></td>

								</tr>
								<%-- Education Level --%>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel keyhm="Client.EducationLevel"
										name="client.EducationLevel" bundle="ClientUIResources"></mifos:mifoslabel></td>

									<td><c:set var="educationLevelList" scope="request"
										value="${requestScope.educationLevelEntity.lookUpMaster}" /> <mifos:select keyhm="Client.EducationLevel"
										name="clientCreationActionForm"
										property="customerDetail.educationLevel" size="1">
										<html-el:options collection="educationLevelList"
											property="lookUpId" labelProperty="lookUpValue" />
									</mifos:select></td>
								</tr>
								<%-- Business Activities --%>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel keyhm="Client.BusinessActivities" 
										name="client.BusinessActivities" bundle="ClientUIResources"></mifos:mifoslabel></td>
									<td><c:set var="businessActivitiesList" scope="request"
										value="${requestScope.businessActivitiesEntity.lookUpMaster}" />
									<mifos:select name="clientCreationActionForm" keyhm="Client.BusinessActivities"
										property="customerDetail.businessActivities" size="1">
										<html-el:options collection="businessActivitiesList"
											property="lookUpId" labelProperty="lookUpValue" />
									</mifos:select></td>
								</tr>
								<%-- Handicapped --%>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel keyhm="Client.Handicapped" isColonRequired="yes"
										name="${ConfigurationConstants.HANDICAPPED}"></mifos:mifoslabel></td>
									<td><c:set var="handicappedList" scope="request"
										value="${requestScope.handicappedEntity.lookUpMaster}" /> <mifos:select keyhm="Client.Handicapped"
										name="clientCreationActionForm"
										property="customerDetail.handicapped" size="1">
										<html-el:options collection="handicappedList"
											property="lookUpId" labelProperty="lookUpValue" />
									</mifos:select></td>
								</tr>

								<%-- Photograph ADD CUSTOMER PICTURE TO ACTION FORM AND CUSTOMER VO--%>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel keyhm="Client.Photo" name="client.Photograph"
										bundle="ClientUIResources"></mifos:mifoslabel></td>
									<td><mifos:file keyhm="Client.Photo" property="picture" maxlength="200"
										onkeypress="return onKeyPressForFileComponent(this);" /></td>
								</tr>
								<%-- Spouse/Father details --%>
								<tr class="fontnormal">
									<td align="right" class="paddingL10"><mifos:mifoslabel
										name="client.SpouseFatherName" mandatory="yes"
										bundle="ClientUIResources"></mifos:mifoslabel></td>
									<td>
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr class="fontnormal">
											<td width="14%"><mifos:mifoslabel name="client.Relationship"
												bundle="ClientUIResources"></mifos:mifoslabel> <c:set
												var="spouseFatherList" scope="request"
												value="${requestScope.spouseEntity.lookUpMaster}" /> <mifos:select
												style="width:80px;" name="clientCreationActionForm"
												property="customerNameDetail[1].nameType" size="1">
												<html-el:options collection="spouseFatherList" property="id"
													labelProperty="lookUpValue" />
											</mifos:select></td>
											<td class="paddingL10">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr class="fontnormal">
														<td class="paddingL10">
															<mifos:mifoslabel	name="client.SpouseFirstName" mandatory="yes" 
															   bundle="ClientUIResources">
															   </mifos:mifoslabel>
														</td>
													</tr>
													<tr class="fontnormal">
														<td class="paddingL10">
															<mifos:mifosalphanumtext property="customerNameDetail[1].firstName" 
															   maxlength="200"	style="width:100px;" />
														</td>
													</tr>
												</table>
											</td>			
											<td class="paddingL10">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr class="fontnormal">
														<td class="paddingL10">
																<mifos:mifoslabel  keyhm="Client.SpouseFatherMiddleName" name="client.SpouseMiddleName"
																		bundle="ClientUIResources">
																		</mifos:mifoslabel>
														</td>
													</tr>
													<tr class="fontnormal">
														<td class="paddingL10">
																<mifos:mifosalphanumtext keyhm="Client.SpouseFatherMiddleName" property="customerNameDetail[1].middleName" maxlength="200"
																	style="width:100px;" />
														</td>
													</tr>
												</table>
											</td>		
											<td class="paddingL10">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr class="fontnormal">
														<td class="paddingL10">
															<mifos:mifoslabel keyhm="Client.SpouseFatherSecondLastName" name="client.SpouseSecondLastName"
																bundle="ClientUIResources">
															</mifos:mifoslabel>
														</td>
													</tr>
													<tr class="fontnormal">
														<td class="paddingL10">
															<mifos:mifosalphanumtext keyhm="Client.SpouseFatherSecondLastName" 
																property="customerNameDetail[1].secondLastName" 
																	maxlength="200" style="width:100px;" />
														</td>
													</tr>
												</table>
											</td>			
											<td class="paddingL10">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr class="fontnormal">
														<td class="paddingL10">
															<mifos:mifoslabel	name="client.SpouseLastName" mandatory="yes"
																bundle="ClientUIResources"></mifos:mifoslabel>
														</td>
													</tr>
													<tr class="fontnormal">
														<td class="paddingL10">
															<mifos:mifosalphanumtext property="customerNameDetail[1].lastName" maxlength="200"
																style="width:100px;" />
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
									</td>
								</tr>
							</table>
							<br>
							<!-- Address -->

							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel keyhm="Client.Address" isManadatoryIndicationNotRequired="yes"
										name="client.AddressHeading" bundle="ClientUIResources"></mifos:mifoslabel>									
									</td>
								</tr>
								<!-- Line 1 of address -->
								<tr class="fontnormal">
									<td width="17%" align="right" class="fontnormal">
									<mifos:mifoslabel keyhm="Client.Address1" 	isColonRequired="yes"
										name="${ConfigurationConstants.ADDRESS1}"></mifos:mifoslabel>
									</td>
									<td width="83%"><mifos:mifosalphanumtext keyhm="Client.Address1"
										name="clientCreationActionForm"
										property="customerAddressDetail.line1" maxlength="200" /></td>
								</tr>

								<!-- Line 2 of address -->

								<tr class="fontnormal">
									<td width="17%" align="right" class="fontnormal">
									<mifos:mifoslabel keyhm="Client.Address2" isColonRequired="yes"
										name="${ConfigurationConstants.ADDRESS2}"></mifos:mifoslabel></td>
									<td width="83%"><mifos:mifosalphanumtext keyhm="Client.Address2"
										name="clientCreationActionForm"
										property="customerAddressDetail.line2" maxlength="200" /></td>
								</tr>
								<!-- Line 3 of address -->
								<tr class="fontnormal">
									<td align="right" class="fontnormal">
									<mifos:mifoslabel keyhm="Client.Address3" isColonRequired="yes"
										name="${ConfigurationConstants.ADDRESS3}"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext keyhm="Client.Address3" name="clientCreationActionForm"
										property="customerAddressDetail.line3" maxlength="200" /></td>
								</tr>
								<!-- City of address -->
								<tr class="fontnormal">
									<td align="right" class="fontnormal">
									<mifos:mifoslabel  keyhm="Client.City" isColonRequired="yes"
										name="${ConfigurationConstants.CITY}"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext keyhm="Client.City" name="clientCreationActionForm"
										property="customerAddressDetail.city" maxlength="100" /></td>
								</tr>
								<!-- State of address -->
								<tr class="fontnormal">
									<td align="right" class="fontnormal">
									<mifos:mifoslabel keyhm="Client.State" isColonRequired="yes"
										name="${ConfigurationConstants.STATE}"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext keyhm="Client.State" name="clientCreationActionForm"
										property="customerAddressDetail.state" maxlength="100" /></td>

								</tr>
								<!-- Country of address -->
								<tr class="fontnormal">
									<td align="right" class="fontnormal">
									<mifos:mifoslabel keyhm="Client.Country"
										name="client.Country" bundle="ClientUIResources"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext keyhm="Client.Country" name="clientCreationActionForm" 
										property="customerAddressDetail.country" maxlength="100" /></td>
								</tr>
								<!-- Postal Code of address -->
								<tr class="fontnormal">
									<td align="right" class="fontnormal">
									<mifos:mifoslabel keyhm="Client.PostalCode" isColonRequired="yes"
										name="${ConfigurationConstants.POSTAL_CODE}"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext keyhm="Client.PostalCode" name="clientCreationActionForm"
										property="customerAddressDetail.zip" maxlength="20" /></td>
								</tr>
								<!-- Telephone of address -->
								<tr class="fontnormal">
									<td align="right" class="fontnormal"><mifos:mifoslabel keyhm="Client.PhoneNumber"
										name="client.Telephone" bundle="ClientUIResources"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext keyhm="Client.PhoneNumber" name="clientCreationActionForm"
										property="customerAddressDetail.phoneNumber" maxlength="20" /></td>

								</tr>


							</table>
							<br>
							<!-- Address end --> <!-- Custom Fields -->
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="2" class="fontnormalbold">Additional information<br>
									<br>
									</td>
								</tr>

								<!-- For each custom field definition in the list custom field entity is passed as key to mifos label -->
								<c:forEach var="cf" items="${requestScope.customFields}"
									varStatus="loopStatus">
									<bean:define id="ctr">
										<c:out value="${loopStatus.index}" />
									</bean:define>
									<tr class="fontnormal">
										<td width="17%" align="right"><mifos:mifoslabel
											name="${cf.lookUpEntity.entityType}"
											mandatory="${cf.mandatoryStringValue}"
											bundle="ClientUIResources"></mifos:mifoslabel>:</td>
										<td width="83%"><c:if test="${cf.fieldType == 1}">
											<mifos:mifosnumbertext name="clientCreationActionForm"
												property='customField[${ctr}].fieldValue' maxlength="200" />
										</c:if> <c:if test="${cf.fieldType == 2}">
											<mifos:mifosalphanumtext name="clientCreationActionForm"
												property='customField[${ctr}].fieldValue' maxlength="200" />
										</c:if> <c:if test="${cf.fieldType == 3}">
											<date:datetag property="customField[${ctr}].fieldValue" />
										</c:if> <html-el:hidden property='customField[${ctr}].fieldId'
											value="${cf.fieldId}"></html-el:hidden> <html-el:hidden
											property='fieldTypeList' value='${cf.fieldType}' /></td>
									</tr>
								</c:forEach>

							</table>
							<br>
							<!-- Buttons -->
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center" class="blueline">&nbsp;</td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center"><c:choose>

										<c:when
											test="${param.method eq 'prevPersonalInfo' or sessionScope.clientCreationActionForm.nextOrPreview eq 'preview'}">
											<html:hidden property="nextOrPreview" value="preview" />

											<html-el:button onclick="goToPreviewPage();"
												property="submitButton" styleClass="buttn"
												style="width:70px;">
												<mifos:mifoslabel name="button.preview"
													bundle="ClientUIResources"></mifos:mifoslabel>
											</html-el:button>
										</c:when>
										<c:otherwise>
											<html-el:submit styleClass="buttn" style="width:70px;">
												<mifos:mifoslabel name="button.continue"
													bundle="ClientUIResources"></mifos:mifoslabel>
											</html-el:submit>
										</c:otherwise>
									</c:choose> &nbsp; &nbsp; <html-el:button
										onclick="goToCancelPage();" property="cancelButton"
										styleClass="cancelbuttn" style="width:70px">
										<mifos:mifoslabel name="button.cancel"
											bundle="ClientUIResources"></mifos:mifoslabel>
									</html-el:button></td>
								</tr>
							</table>
							<!-- Button end --> <br>
							<!-- before main closing --></td>
						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>
		</html-el:form>
	</tiles:put>
</tiles:insert>
