<!--
/**

* previewEditClientPersonalInfo.jsp    version: 1.0



* Copyright © 2005-2006 Grameen Foundation USA

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

<tiles:insert definition=".detailsCustomer">
	<tiles:put name="body" type="string">
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>

		<script language="javascript">
  
  function goToPersonalPage(){
	clientCreationActionForm.action="clientCreationAction.do?method=prevPersonalInfo";
	clientCreationActionForm.submit();
  }
  function goToCancelPage(){
	clientCreationActionForm.action="clientCreationAction.do?method=cancel";
	clientCreationActionForm.submit();
  }
</script>
		<html-el:form action="clientCreationAction.do?method=update"
			onsubmit="func_disableSubmitBtn('submitButton');">
			<html-el:hidden property="input" value="editPersonalInfo" />
			<html-el:hidden property="customerId"
				value="${requestScope.clientVO.customerId}" />
			<html-el:hidden property="office.officeId"
				value="${requestScope.clientVO.office.officeId}" />
			<html-el:hidden property="office.versionNo"
				value="${requestScope.clientVO.office.versionNo}" />
			<html-el:hidden property="versionNo"
				value="${requestScope.clientVO.versionNo}" />
			<td align="left" valign="top" bgcolor="#FFFFFF"
				class="paddingleftmain">
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
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange"><span class="heading"> <c:out
								value="${requestScope.clientVO.displayName}" /> - </span><mifos:mifoslabel
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
							<%-- <c:out value="${requestScope.clientVO.office.officeId}"/> id
	       
	       <c:out value="${requestScope.clientVO.office.versionNo}"/> version
	       <c:out value="${requestScope.clientVO.customerId}"/>customer id
	       
	       <c:out value="${requestScope.clientVO.versionNo}"/> customer version --%>
							<td class="fontnormalbold"><c:if
								test="${sessionScope.noPicture eq 'No'}">
								<img
									src="/Mifos/clientCreationAction.do?method=retrievePictureOnPreview"
									height="100" width="150" />

							</c:if> <br>
							</td></tr>
						<tr><td class="fontnormalbold">
							<mifos:mifoslabel name="client.Name" bundle="ClientUIResources"></mifos:mifoslabel>
							<span class="fontnormal"> <c:forEach var="customerName"
								items="${requestScope.clientVO.customerNameDetailSet}">
								<c:if test="${customerName.nameType==3}">
									<mifoscustom:lookUpValue id="${customerName.salutation}"
										searchResultName="salutationEntity"></mifoscustom:lookUpValue>
								</c:if>
							</c:forEach> <c:out value="${requestScope.clientVO.displayName}" />
							<br>
							</span>
						</td></tr> 
									<tr id="Client.GovernmentId"><td class="fontnormalbold"> <mifos:mifoslabel
								name="${ConfigurationConstants.GOVERNMENT_ID}" keyhm="Client.GovernmentId" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
							<span class="fontnormal"><c:out
								value="${requestScope.clientVO.governmentId}" /> <br>
							</span> 
						</td></tr>
									<tr><td class="fontnormalbold"><mifos:mifoslabel name="client.DateOfBirth"
								bundle="ClientUIResources"></mifos:mifoslabel> <span
								class="fontnormal"><c:out
								value="${sessionScope.clientCreationActionForm.dateOfBirth}" />
							<br>
							</span> 
					</td></tr>
									<tr><td class="fontnormalbold"><mifos:mifoslabel name="client.Age"
								bundle="ClientUIResources"></mifos:mifoslabel> <span
								class="fontnormal"><c:out
								value="${sessionScope.clientCreationActionForm.age}" /><br>
							</span> 
					</td></tr>
									<tr><td class="fontnormalbold"><mifos:mifoslabel name="client.Gender"
								bundle="ClientUIResources"></mifos:mifoslabel> <span
								class="fontnormal"><mifoscustom:lookUpValue
								id="${requestScope.clientVO.customerDetail.gender}"
								searchResultName="genderEntity"></mifoscustom:lookUpValue> <br>
							</span> 
						</td></tr>
									<tr><td class="fontnormalbold"><mifos:mifoslabel name="client.MaritalStatus"
								bundle="ClientUIResources"></mifos:mifoslabel> <span
								class="fontnormal"><mifoscustom:lookUpValue
								id="${requestScope.clientVO.customerDetail.maritalStatus}"
								searchResultName="maritalStatusEntity"></mifoscustom:lookUpValue>
							<br>
							</span></td></tr>
									<tr><td class="fontnormalbold"> <c:forEach var="customerName"
								items="${requestScope.clientVO.customerNameDetailSet}">
								<c:if test="${customerName.nameType!=3}">
									<c:set var="spouseFatherType" scope="request"
										value="${customerName.nameType}" />

								</c:if>
							</c:forEach> <c:choose>
								<c:when test="${spouseFatherType == 1}">
									<span class="fontnormalbold"><mifos:mifoslabel
										name="client.SpouseLabel" bundle="ClientUIResources"></mifos:mifoslabel></span>
								</c:when>
								<c:otherwise>
									<span class="fontnormalbold"><mifos:mifoslabel
										name="client.FatherLabel" bundle="ClientUIResources"></mifos:mifoslabel></span>
								</c:otherwise>
							</c:choose> <span class="fontnormal"> <c:out
								value="${sessionScope.clientCreationActionForm.spouseFatherDisplayName}" />
							<br>
							</span>
						</td></tr>
									<tr><td class="fontnormalbold"><mifos:mifoslabel
								name="client.NumberOfChildren" bundle="ClientUIResources"></mifos:mifoslabel>
							<span class="fontnormal"> <c:out
								value="${requestScope.clientVO.customerDetail.numChildren}" /><br>
							</span> <%-- Citizenship --%> 
						</td></tr>
									<tr id="Client.Citizenship"><td class="fontnormalbold"><mifos:mifoslabel
								name="${ConfigurationConstants.CITIZENSHIP}" keyhm="Client.Citizenship" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
							<span class="fontnormal"> <mifoscustom:lookUpValue
								id="${requestScope.clientVO.customerDetail.citizenship}"
								searchResultName="citizenshipEntity"></mifoscustom:lookUpValue><br>
							</span> 
						</td></tr>
									<tr id="Client.Ethinicity"><td class="fontnormalbold"><mifos:mifoslabel
								name="${ConfigurationConstants.ETHINICITY}" keyhm="Client.Ethinicity" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
							<span class="fontnormal"> <mifoscustom:lookUpValue
								id="${requestScope.clientVO.customerDetail.ethinicity}"
								searchResultName="ethinicityEntity"></mifoscustom:lookUpValue><br>
							</span> 
						</td></tr>
									<tr id="Client.EducationLevel"><td class="fontnormalbold"><mifos:mifoslabel name="client.EducationLevel"
								bundle="ClientUIResources" keyhm="Client.EducationLevel" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <span
								class="fontnormal"> <mifoscustom:lookUpValue
								id="${requestScope.clientVO.customerDetail.educationLevel}"
								searchResultName="educationLevelEntity"></mifoscustom:lookUpValue><br>
							</span> 
						</td></tr>
									<tr id="Client.BusinessActivities"><td class="fontnormalbold"><mifos:mifoslabel name="client.BusinessActivities"
								bundle="ClientUIResources" keyhm="Client.BusinessActivities" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <span
								class="fontnormal"> <mifoscustom:lookUpValue
								id="${requestScope.clientVO.customerDetail.businessActivities}"
								searchResultName="businessActivitiesEntity"></mifoscustom:lookUpValue><br>
							</span> 
						</td></tr>
									<tr id="Client.Handicapped"><td class="fontnormalbold"><mifos:mifoslabel
								name="${ConfigurationConstants.HANDICAPED}" keyhm="Client.Handicapped" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
							<span class="fontnormal"> <mifoscustom:lookUpValue
								id="${requestScope.clientVO.customerDetail.handicapped}"
								searchResultName="handicappedEntity"></mifoscustom:lookUpValue>
							</span> 
							</td></tr>
									<tr id="Client.Address"><td class="fontnormalbold"><br>
							<mifos:mifoslabel name="client.Address" bundle="ClientUIResources" keyhm="Client.Address" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>

							<span class="fontnormal"><br>
							</span> <span class="fontnormal"> <c:out
								value="${requestScope.clientVO.displayAddress}" />
							</span> 
						</td></tr>
									<tr id="Client.City"><td class="fontnormalbold"><mifos:mifoslabel name="${ConfigurationConstants.CITY}" keyhm="Client.City" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
							<span class="fontnormal"> <c:out
								value="${requestScope.clientVO.customerAddressDetail.city}" /> 
							</span> 
						</td></tr>
									<tr id="Client.State"><td class="fontnormalbold"><mifos:mifoslabel name="${ConfigurationConstants.STATE}" keyhm="Client.State" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
							<span class="fontnormal"> <c:out
								value="${requestScope.clientVO.customerAddressDetail.state}" />
							
							</span> 
						</td></tr>
									<tr id="Client.Country"><td class="fontnormalbold"><mifos:mifoslabel name="client.Country"
								bundle="ClientUIResources" keyhm="Client.Country" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <span
								class="fontnormal"> <c:out
								value="${requestScope.clientVO.customerAddressDetail.country}" />
							</span> 
						</td></tr>
									<tr id="Client.PostalCode"><td class="fontnormalbold"><mifos:mifoslabel
								name="${ConfigurationConstants.POSTAL_CODE}" keyhm="Client.PostalCode" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
							<span class="fontnormal"><c:out
								value="${requestScope.clientVO.customerAddressDetail.zip}" /> 
							</span> 
						</td></tr>
									<tr id="Client.PhoneNumber"><td class="fontnormalbold"><br><mifos:mifoslabel name="client.Telephone"
								bundle="ClientUIResources" keyhm="Client.PhoneNumber" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <span
								class="fontnormal"><c:out
								value="${requestScope.clientVO.customerAddressDetail.phoneNumber}" />
							</span>

							<!--CustomField addition --> 
						</td></tr>
									<tr><td height="23" class="fontnormalbold"><br><mifos:mifoslabel
								name="client.AdditionalInformationHeading"
								bundle="ClientUIResources"></mifos:mifoslabel><span></span> <span
								class="fontnormal"><br>
							 <c:forEach var="cf" items="${requestScope.customFields}">
								<c:forEach var="customField"
									items="${requestScope.clientVO.customFieldSet}">
									<c:if test="${cf.fieldId==customField.fieldId}">
										<mifos:mifoslabel name="${cf.lookUpEntity.entityType}"
											bundle="ClientUIResources"></mifos:mifoslabel>: 
		                	 	<c:out value="${customField.fieldValue}" />
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

