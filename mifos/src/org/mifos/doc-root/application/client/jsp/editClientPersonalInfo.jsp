<!--
/**

* editClientPersonalInfo    version: 1.0



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
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<script language="javascript" SRC="pages/framework/js/date.js"></script>
		<script language="javascript" SRC="pages/framework/js/conversion.js"></script>
		<script>
	
		function goToCancelPage(){
		clientCreationActionForm.action="clientCreationAction.do?method=cancel";
		clientCreationActionForm.submit();
	  }	
	  function chkForValidDates(){
		if(!(chkForDateOfBirthDate())){
			return false;
		}
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
	
	  function chkForDateOfBirthDate(){
		 var statusIdValue = document.getElementById("statusId").value;	  
	  	 if (statusIdValue!=3){
	  	 	var dateOfBirth = document.getElementById("dateOfBirth");	  
	  	 	var dateOfBirthFormat = document.getElementById("dateOfBirthFormat");	  
	  	 	var dateOfBirthYY = document.getElementById("dateOfBirthYY");	  
			return (validateMyForm(dateOfBirth,dateOfBirthFormat,dateOfBirthYY));
	 	 }
	 	 else{
	 	 	return true;
	 	 }
	  }
	</script>

		<html-el:form action="clientCreationAction.do?method=preview"
			enctype="multipart/form-data" onsubmit="return chkForValidDates()">
			<html-el:hidden property="input" value="editPersonalInfo" />
			<html-el:hidden property="isClientUnderGrp"
				value="${requestScope.clientVO.groupFlag}" />
			<html-el:hidden property="groupFlag"
				value="${requestScope.clientVO.groupFlag}" />
			<html-el:hidden property="customerId"
				value="${requestScope.clientVO.customerId}" />


			<html-el:hidden name="clientCreationActionForm"
				property="customerAddressDetail.customerAddressId"
				value="${requestScope.clientVO.customerAddressDetail.customerAddressId}" />
			<html-el:hidden name="clientCreationActionForm"
				property="customerDetail.customer.customerId"
				value="${requestScope.clientVO.customerDetail.customer.customerId}" />

			<html-el:hidden property="office.officeId"
				value="${requestScope.clientVO.office.officeId}" />
			<html-el:hidden property="office.versionNo"
				value="${requestScope.clientVO.office.versionNo}" />
			<html-el:hidden property="globalCustNum"
				value="${requestScope.clientVO.globalCustNum}" />
			<html-el:hidden property="versionNo"
				value="${requestScope.clientVO.versionNo}" />
			<html-el:hidden property="statusId"
				value="${requestScope.clientVO.statusId}" />
			<html-el:hidden property="trained"
				value="${requestScope.clientVO.trained}" />
			<html-el:hidden property="loanOfficerId"
				value="${requestScope.clientVO.personnel.personnelId}" />
			<html-el:hidden property="customerFormedById"
				value="${requestScope.clientVO.customerFormedByPersonnel.personnelId}" />
			<html-el:hidden property="trainedDate"
				value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.clientVO.trainedDate)}" />
			<html-el:hidden property="externalId"
				value="${requestScope.clientVO.externalId}" />

			<html-el:hidden property="clientConfidential"
				value="${requestScope.clientVO.clientConfidential}" />



			<%-- <td align="left" valign="top" bgcolor="#FFFFFF" class="paddingleftmain"> --%>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <a
						href="CustomerSearchAction.do?method=getOfficeHomePage&officeId=<c:out value="${sessionScope.linkValues.customerOfficeId}"/>&officeName=<c:out value="${sessionScope.linkValues.customerOfficeName}"/>&loanOfficerId=<c:out value="${requestScope.Context.userContext.id}"/>">
					<c:out value="${sessionScope.linkValues.customerOfficeName}" /></a>
					/ </span> <c:if
						test="${!empty sessionScope.linkValues.customerCenterName}">
						<span class="fontnormal8pt"> <a
							href="centerAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.customerCenterGCNum}"/>">
						<c:out value="${sessionScope.linkValues.customerCenterName}" /> </a>
						/ </span>
					</c:if> <c:if
						test="${!empty sessionScope.linkValues.customerParentName}">
						<span class="fontnormal8pt"> <a
							href="GroupAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.customerParentGCNum}"/>">
						<c:out value="${sessionScope.linkValues.customerParentName}" /> </a>
						/ </span>
					</c:if> <!-- Name of the client --> <span class="fontnormal8pt"> <a
						href="clientCreationAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.globalCustNum}"/>">
					<c:out value="${sessionScope.linkValues.customerName}" /> </a> </span>
					</td>
				</tr>
			</table>
			<%-- Start of information --%>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<%-- <c:out value="${requestScope.clientVO.office.officeId}"/> id
	       
	       <c:out value="${requestScope.clientVO.office.versionNo}"/> version
	       <c:out value="${requestScope.clientVO.customerId}"/>customer id
	       
	       <c:out value="${requestScope.clientVO.versionNo}"/> customer version --%>
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange"><span class="heading"><c:out
								value="${sessionScope.linkValues.customerName}" /> - </span><mifos:mifoslabel
								name="client.EditPersonalInformationLink"
								bundle="ClientUIResources"></mifos:mifoslabel></td>
						</tr>
						<tr>
							<td class="fontnormal"><mifos:mifoslabel
								name="client.PreviewEditInfoInstruction"
								bundle="ClientUIResources"></mifos:mifoslabel> <mifos:mifoslabel
								name="client.EditPageCancelInstruction1"
								bundle="ClientUIResources"></mifos:mifoslabel> <mifos:mifoslabel
								name="${ConfigurationConstants.CLIENT}"
								bundle="ClientUIResources"></mifos:mifoslabel> <mifos:mifoslabel
								name="client.EditPageCancelInstruction2"
								bundle="ClientUIResources"></mifos:mifoslabel> <span
								class="mandatorytext"><font color="#FF0000">*</font></span> <mifos:mifoslabel
								name="client.FieldInstruction" bundle="ClientUIResources"></mifos:mifoslabel>
							</td>
						</tr>
					</table>
					<br>
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<font class="fontnormalRedBold"><html-el:errors
							bundle="ClientUIResources" /> </font>
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
							<td width="83%"><c:forEach var="customerName"
								items="${requestScope.clientVO.customerNameDetailSet}">
								<c:if test="${customerName.nameType==3}">
									<c:set var="salutationList" scope="request"
										value="${requestScope.salutationEntity.lookUpMaster}" />
									<mifos:select name="clientCreationActionForm"
										property="customerNameDetail[0].salutation" size="1"
										value="${customerName.salutation}">
										<html-el:options collection="salutationList"
											property="lookUpId" labelProperty="lookUpValue" />
									</mifos:select>
								</td>
						</tr>
						<%-- First Name --%>

						<html-el:hidden name="clientCreationActionForm"
							property="customerNameDetail[0].customerNameId"
							value="${customerName.customerNameId}" />
						<html-el:hidden name="clientCreationActionForm"
							property="customerNameDetail[0].customer.customerId"
							value="${customerName.customer.customerId}" />

						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel name="client.FirstName"
								mandatory="yes" bundle="ClientUIResources"></mifos:mifoslabel></td>
							<td><mifos:mifosalphanumtext name="clientCreationActionForm"
								property="customerNameDetail[0].firstName"
								value="${customerName.firstName}" maxlength="200" /></td>
						</tr>
						<%-- Middle Name --%>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel keyhm="Client.MiddleName" name="client.MiddleName"
								bundle="ClientUIResources"></mifos:mifoslabel></td>
							<td><mifos:mifosalphanumtext keyhm="Client.MiddleName" name="clientCreationActionForm"
								property="customerNameDetail[0].middleName"
								value="${customerName.middleName}" maxlength="200" /></td>
						</tr>
						<%-- Second Last Name --%>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel keyhm="Client.SecondLastName" name="client.SecondLastName"
								isColonRequired="yes" bundle="ClientUIResources"></mifos:mifoslabel></td>
							<td><mifos:mifosalphanumtext keyhm="Client.SecondLastName" name="clientCreationActionForm"
								property="customerNameDetail[0].secondLastName"
								value="${customerName.secondLastName}" maxlength="200" /></td>
						</tr>
						<%-- Last Name --%>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel name="client.LastName"
								mandatory="yes" bundle="ClientUIResources"></mifos:mifoslabel></td>
							<td><mifos:mifosalphanumtext name="clientCreationActionForm"
								property="customerNameDetail[0].lastName"
								value="${customerName.lastName}" maxlength="200" /></td>
						</tr>
						</c:if>
							</c:forEach>
						<html-el:hidden property="customerNameDetail[0].nameType"
							value="3" />
					
						<c:choose>

							<c:when test="${requestScope.clientVO.statusId != 3}">
								<!-- If status is approved then display government id and date of birth as labels -->
								<%-- Government Id --%>
								<tr class="fontnormal">
									<td align="right">
									<mifos:mifoslabel	keyhm="Client.GovernmentId" name="${ConfigurationConstants.GOVERNMENT_ID}" isColonRequired="yes"/>
										</td>
									<td><mifos:mifosalphanumtext keyhm="Client.GovernmentId" name="clientCreationActionForm"
										property="governmentId" maxlength="50"
										value="${requestScope.clientVO.governmentId}" /></td>
								</tr>
								<%-- Date Of  Birth  --%>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="client.DateOfBirth"
										mandatory="yes" bundle="ClientUIResources"></mifos:mifoslabel></td>
									<td><date:datetag property="dateOfBirth" name="clientVO" /></td>
								</tr>
							</c:when>
							<c:otherwise>
								<%-- Government Id Label--%>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel keyhm="Client.GovernmentId" isColonRequired="yes"
										name="${ConfigurationConstants.GOVERNMENT_ID}"></mifos:mifoslabel></td>
									<td><c:out value="${requestScope.clientVO.governmentId}" /> <html-el:hidden
										name="clientCreationActionForm" property="governmentId"
										value="${requestScope.clientVO.governmentId}" /></td>
								</tr>
								<%-- Date Of  Birth Label--%>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="client.DateOfBirth"
										mandatory="yes" bundle="ClientUIResources"></mifos:mifoslabel></td>
									<td><c:out
										value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.clientVO.dateOfBirth)}" />
									<html-el:hidden name="clientCreationActionForm"
										property="dateOfBirth"
										value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.clientVO.dateOfBirth)}" />
									</td>
								</tr>
							</c:otherwise>
						</c:choose>
						<%-- Gender --%>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel name="client.Gender"
								mandatory="yes" bundle="ClientUIResources"></mifos:mifoslabel></td>

							<td><c:set var="genderList" scope="request"
								value="${requestScope.genderEntity.lookUpMaster}" /> <mifos:select
								name="clientCreationActionForm" property="customerDetail.gender"
								size="1" value="${requestScope.clientVO.customerDetail.gender}">
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
								property="customerDetail.maritalStatus" size="1"
								value="${requestScope.clientVO.customerDetail.maritalStatus}">
								<html-el:options collection="maritalStatusList"
									property="lookUpId" labelProperty="lookUpValue" />
							</mifos:select></td>
						</tr>
						<%-- Number Of Children--%>
						<tr class="fontnormal">
							<td align="right" class="fontnormal"><mifos:mifoslabel
								name="client.NumberOfChildren" bundle="ClientUIResources"></mifos:mifoslabel></td>
							<td><mifos:mifosnumbertext name="clientCreationActionForm"
								property="customerDetail.numChildren"
								value="${requestScope.clientVO.customerDetail.numChildren}"
								maxlength="20" /></td>
						</tr>
						<%-- Citizenship --%>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel keyhm="Client.Citizenship" isColonRequired="yes"
								name="${ConfigurationConstants.CITIZENSHIP}" mandatory="yes"></mifos:mifoslabel></td>
							<td><c:set var="citizenshipList" scope="request"
								value="${requestScope.citizenshipEntity.lookUpMaster}" /> <mifos:select keyhm="Client.Citizenship"
								name="clientCreationActionForm"
								property="customerDetail.citizenship" size="1"
								value="${requestScope.clientVO.customerDetail.citizenship}">
								<html-el:options collection="citizenshipList"
									property="lookUpId" labelProperty="lookUpValue" />
							</mifos:select></td>

						</tr>
						<%-- Ethinicity --%>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel keyhm="Client.Ethinicity"
								name="${ConfigurationConstants.ETHINICITY}" isColonRequired="yes"
								bundle="ClientUIResources"></mifos:mifoslabel></td>
							<td><c:set var="ethinicityList" scope="request"
								value="${requestScope.ethinicityEntity.lookUpMaster}" /> 
								<mifos:select keyhm="Client.Ethinicity"
								name="clientCreationActionForm"
								property="customerDetail.ethinicity" size="1"
								value="${requestScope.clientVO.customerDetail.ethinicity}">
								<html-el:options collection="ethinicityList" property="lookUpId"
									labelProperty="lookUpValue" />
							</mifos:select></td>

						</tr>
						<%-- Education Level --%>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel name="client.EducationLevel" keyhm="Client.EducationLevel"
								bundle="ClientUIResources" mandatory="yes"></mifos:mifoslabel></td>

							<td><c:set var="educationLevelList" scope="request"
								value="${requestScope.educationLevelEntity.lookUpMaster}" /> <mifos:select keyhm="Client.EducationLevel"
								name="clientCreationActionForm"
								property="customerDetail.educationLevel" size="1"
								value="${requestScope.clientVO.customerDetail.educationLevel}">
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
								property="customerDetail.businessActivities" size="1"
								value="${requestScope.clientVO.customerDetail.businessActivities}">
								<html-el:options collection="businessActivitiesList"
									property="lookUpId" labelProperty="lookUpValue" />
							</mifos:select></td>
						</tr>
						<%-- Handicapped --%>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel keyhm="Client.Handicapped" isColonRequired="yes"
								name="${ConfigurationConstants.HANDICAPPED}"
								bundle="ClientUIResources"></mifos:mifoslabel></td>
							<td><c:set var="handicappedList" scope="request"
								value="${requestScope.handicappedEntity.lookUpMaster}" /> <mifos:select keyhm="Client.Handicapped"
								name="clientCreationActionForm"
								property="customerDetail.handicapped" size="1"
								value="${requestScope.clientVO.customerDetail.handicapped}">
								<html-el:options collection="handicappedList"
									property="lookUpId" labelProperty="lookUpValue" />
							</mifos:select></td>
						</tr>

						<%-- Photograph ADD CUSTOMER PICTURE TO ACTION FORM AND CUSTOMER VO --%>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel keyhm="Client.Photo" name="client.Photograph"
								bundle="ClientUIResources"></mifos:mifoslabel></td>
							<td><mifos:file keyhm="Client.Photo" property="picture" maxlength="200"
								onkeypress="return onKeyPressForFileComponent(this);" /></td>
						</tr>
						<%-- Spouse/Father details --%>
						<tr class="fontnormal">
							<td align="right" class="fontnormal"><mifos:mifoslabel
								name="client.SpouseFatherName" mandatory="yes"
								bundle="ClientUIResources"></mifos:mifoslabel></td>
							<td>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr class="fontnormal">
									<td width="14%"><mifos:mifoslabel name="client.Relationship"
										bundle="ClientUIResources"></mifos:mifoslabel> <c:forEach
										var="customerName"
										items="${requestScope.clientVO.customerNameDetailSet}">
										<c:if test="${customerName.nameType!=3}">
											<c:set var="spouseFatherList" scope="request"
												value="${requestScope.spouseEntity.lookUpMaster}" />
											<mifos:select style="width:80px;"
												name="clientCreationActionForm"
												property="customerNameDetail[1].nameType" size="1"
												value="${customerName.nameType}">
												<html-el:options collection="spouseFatherList" property="id"
													labelProperty="lookUpValue" />
											</mifos:select>
										</td>
									<html-el:hidden name="clientCreationActionForm"
										property="customerNameDetail[1].customerNameId"
										value="${customerName.customerNameId}" />
									<html-el:hidden name="clientCreationActionForm"
										property="customerNameDetail[1].customer.customerId"
										value="${customerName.customer.customerId}" />
									<td class="paddingL10">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr class="fontnormal">
												<td class="paddingL10">
													<mifos:mifoslabel name="client.SpouseFirstName" mandatory="yes"
														bundle="ClientUIResources">
													</mifos:mifoslabel>
												</td>
											</tr>
											<tr class="fontnormal">
												<td class="paddingL10">
													<mifos:mifosalphanumtext
													property="customerNameDetail[1].firstName"
													value="${customerName.firstName}" maxlength="200"
														style="width:100px;" />
												</td>
											</tr>
										</table>	
									<td class="paddingL10">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr class="fontnormal">
												<td class="paddingL10">
													<mifos:mifoslabel keyhm="Client.SpouseFatherMiddleName" name="client.SpouseMiddleName" bundle="ClientUIResources">
													</mifos:mifoslabel>
												</td>
											</tr>
											<tr class="fontnormal">
												<td class="paddingL10">
													<mifos:mifosalphanumtext keyhm="Client.SpouseFatherMiddleName"	property="customerNameDetail[1].middleName"
														value="${customerName.middleName}" maxlength="200"
															style="width:100px;" />
												</td>
											</tr>
										</table>
									</td>	
									<td class="paddingL10">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr class="fontnormal">
												<td class="paddingL10">
													<mifos:mifoslabel keyhm="Client.SpouseFatherSecondLastName"
														name="client.SpouseSecondLastName" bundle="ClientUIResources">
													</mifos:mifoslabel>
												</td>
											</tr>
											<tr class="fontnormal">
												<td class="paddingL10">
													<mifos:mifosalphanumtext keyhm="Client.SpouseFatherSecondLastName"
														property="customerNameDetail[1].secondLastName"
															value="${customerName.secondLastName}" maxlength="200"
																style="width:100px;" />
												</td>
											</tr>
										</table>
									</td>	
									<td class="paddingL10">
										<table border="0" cellspacing="0" cellpadding="0">
											<tr class="fontnormal">
												<td class="paddingL10">
													<mifos:mifoslabel name="client.SpouseLastName" 
													   mandatory="yes"	bundle="ClientUIResources">
													   </mifos:mifoslabel>
												</td>
											</tr>	
											<tr class="fontnormal">
												<td class="paddingL10">
												   <mifos:mifosalphanumtext	property="customerNameDetail[1].lastName"
														value="${customerName.lastName}" maxlength="200"
														style="width:100px;" />
												</td>
											</c:if>
									</c:forEach>	
											</tr>
										</table>
									</td>		
								</tr>
							</table>
							</td>
						</tr>
					</table>

					<br>
					<!-- Address Fields -->
					<table width="95%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
								name="client.AddressHeading"  keyhm="Client.Address" isManadatoryIndicationNotRequired="yes" bundle="ClientUIResources"></mifos:mifoslabel><br>
							<br>
							</td>
						</tr>
						<tr class="fontnormal">
							<td width="17%" align="right"><mifos:mifoslabel keyhm="Client.Address1" isColonRequired="yes"
								name="${ConfigurationConstants.ADDRESS1}"></mifos:mifoslabel></td>
							<td width="83%"><mifos:mifosalphanumtext keyhm="Client.Address1"
								name="clientCreationActionForm"
								property="customerAddressDetail.line1"
								value="${requestScope.clientVO.customerAddressDetail.line1}"
								maxlength="200" /></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel keyhm="Client.Address2" isColonRequired="yes"
								name="${ConfigurationConstants.ADDRESS2}"></mifos:mifoslabel></td>
							<td><mifos:mifosalphanumtext keyhm="Client.Address2" name="clientCreationActionForm"
								property="customerAddressDetail.line2"
								value="${requestScope.clientVO.customerAddressDetail.line2}"
								maxlength="200" /></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel keyhm="Client.Address3" isColonRequired="yes"
								name="${ConfigurationConstants.ADDRESS3}"></mifos:mifoslabel></td>
							<td><mifos:mifosalphanumtext keyhm="Client.Address3" name="clientCreationActionForm"
								property="customerAddressDetail.line3"
								value="${requestScope.clientVO.customerAddressDetail.line3}"
								maxlength="200" /></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel keyhm="Client.City" isColonRequired="yes"
								name="${ConfigurationConstants.CITY}"></mifos:mifoslabel></td>
							<td><mifos:mifosalphanumtext keyhm="Client.City" name="clientCreationActionForm"
								property="customerAddressDetail.city"
								value="${requestScope.clientVO.customerAddressDetail.city}"
								maxlength="100" /></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel keyhm="Client.State" isColonRequired="yes"
								name="${ConfigurationConstants.STATE}"></mifos:mifoslabel></td>
							<td><mifos:mifosalphanumtext  keyhm="Client.State" name="clientCreationActionForm"
								property="customerAddressDetail.state"
								value="${requestScope.clientVO.customerAddressDetail.state}"
								maxlength="100" /></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel keyhm="Client.Country" name="client.Country"
								bundle="ClientUIResources"></mifos:mifoslabel></td>
							<td><mifos:mifosalphanumtext keyhm="Client.Country" name="clientCreationActionForm"
								property="customerAddressDetail.country"
								value="${requestScope.clientVO.customerAddressDetail.country}"
								maxlength="100" /></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel keyhm="Client.PostalCode" isColonRequired="yes"
								name="${ConfigurationConstants.POSTAL_CODE}"></mifos:mifoslabel></td>
							<td><mifos:mifosalphanumtext keyhm="Client.PostalCode" name="clientCreationActionForm"
								property="customerAddressDetail.zip"
								value="${requestScope.clientVO.customerAddressDetail.zip}"
								maxlength="20" /></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel keyhm="Client.PhoneNumber" name="client.Telephone"
								bundle="ClientUIResources"></mifos:mifoslabel></td>
							<td><mifos:mifosalphanumtext keyhm="Client.PhoneNumber" name="clientCreationActionForm"
								property="customerAddressDetail.phoneNumber"
								value="${requestScope.clientVO.customerAddressDetail.phoneNumber}"
								maxlength="20" /></td>
						</tr>
					</table>
					<br>
					<!-- Custom Fields -->
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
								name="client.AdditionalInformationHeading"
								bundle="ClientUIResources"></mifos:mifoslabel><br>
							<br>
							</td>
						</tr>

						<!-- For each custom field definition in the list custom field entity is passed as key to mifos label -->




						<c:forEach var="customFieldDef"
							items="${requestScope.customFields}" varStatus="loopStatus">
							<bean:define id="ctr">
								<c:out value="${loopStatus.index}" />
							</bean:define>
							<c:forEach var="cf"
								items="${requestScope.clientVO.customFieldSet}">
								<c:if test="${customFieldDef.fieldId==cf.fieldId}">
									<tr class="fontnormal">
										<td width="17%" align="right"><mifos:mifoslabel
											name="${customFieldDef.lookUpEntity.entityType}"
											mandatory="${customFieldDef.mandatoryStringValue}"
											bundle="ClientUIResources"></mifos:mifoslabel>:</td>
										<td width="83%"><c:if test="${customFieldDef.fieldType == 1}">
											<mifos:mifosnumbertext name="clientCreationActionForm"
												property='customField[${ctr}].fieldValue'
												value="${cf.fieldValue}" maxlength="200" />
										</c:if> <c:if test="${customFieldDef.fieldType == 2}">
											<mifos:mifosalphanumtext name="clientCreationActionForm"
												property='customField[${ctr}].fieldValue'
												value="${cf.fieldValue}" maxlength="200" />
										</c:if> <c:if test="${customFieldDef.fieldType == 3}">
											<date:datetag property="customField[${ctr}].fieldValue"
												name="clientVO" />

										</c:if> <html-el:hidden property='customField[${ctr}].fieldId'
											value="${cf.fieldId}"></html-el:hidden> <html-el:hidden
											property='fieldTypeList' value='${customFieldDef.fieldType}' />
										</td>
									</tr>
								</c:if>
							</c:forEach>
						</c:forEach>

					</table>

					<!--Custom Fields end  -->
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
									bundle="ClientUIResources"></mifos:mifoslabel>
							</html-el:submit> &nbsp; &nbsp; <html-el:button
								onclick="goToCancelPage();" property="cancelButton"
								styleClass="cancelbuttn" style="width:70px">
								<mifos:mifoslabel name="button.cancel"
									bundle="ClientUIResources"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			<br>
			<td></td>
			<tr></tr>
			<table></table>
		</html-el:form>
	</tiles:put>
</tiles:insert>
