<!--
/**

* previewClientDetails    version: 1.0



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
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>



<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<script language="javascript">
  
  function setClientStatus(status){
	disableButtons();
	document.getElementsByName("statusId")[0].value=status;
	clientCreationActionForm.submit();
  }
  function disableButtons(){
	func_disableSubmitBtn('submitButton');
	func_disableSubmitBtn('submitButton1');
  }
  function goToCancelPage(){
	clientCreationActionForm.action="clientCreationAction.do?method=cancel";
	clientCreationActionForm.submit();
  }
   function goToMfiPage(){
	clientCreationActionForm.action="clientCreationAction.do?method=prevMFIInfo";
	clientCreationActionForm.submit();
  }
   function goToPersonalPage(){
	clientCreationActionForm.action="clientCreationAction.do?method=prevPersonalInfo";
	clientCreationActionForm.submit();
  }
  function photopopup(){
   window.open("clientCreationAction.do?method=retrievePictureOnPreview",null,"height=200,width=200,status=no,scrollbars=no,toolbar=no,menubar=no,location=no");
  }
</script>
		<html-el:form action="clientCreationAction.do?method=create">
			<html-el:hidden property="input" value="create" />
			<html-el:hidden property="statusId" value="" />
			<html-el:hidden property="isClientUnderGrp"
				value="${requestScope.clientVO.isClientUnderGrp}" />

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
									<td class="headingorange"><span class="heading"><mifos:mifoslabel
										name="client.CreatePreviewPageTitle"
										bundle="ClientUIResources"></mifos:mifoslabel> <mifos:mifoslabel
										name="${ConfigurationConstants.CLIENT}" /> </span> <mifos:mifoslabel
										name="client.CreatePreviewReviewSubmitTitle"
										bundle="ClientUIResources"></mifos:mifoslabel></td>
								</tr>
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="client.CreatePreviewPageInstruction"
										bundle="ClientUIResources"></mifos:mifoslabel> &nbsp; <mifos:mifoslabel
										name="client.PreviewEditCancelInstruction1"
										bundle="ClientUIResources"></mifos:mifoslabel> <mifos:mifoslabel
										name="${ConfigurationConstants.CLIENT}" /> <mifos:mifoslabel
										name="client.PreviewEditCancelInstruction2"
										bundle="ClientUIResources"></mifos:mifoslabel></td>
								</tr>
							</table>
							<!-- Preview page instruction ends--> <!-- Client information entered on the create page -->
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td><font class="fontnormalRedBold"><html-el:errors
										bundle="ClientUIResources" /></font></td>
								</tr>
							</table>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td class="fontnormal"><span class="fontnormalbold"> <mifos:mifoslabel
										name="${ConfigurationConstants.BRANCHOFFICE}" /> <mifos:mifoslabel
										name="client.BranchSelected" bundle="ClientUIResources"></mifos:mifoslabel></span>
									<c:out value="${requestScope.clientVO.office.officeName}" /></td>
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
									<td class="fontnormalbold"><c:if
										test="${sessionScope.noPicture eq 'No'}">
										<img
											src="/Mifos/clientCreationAction.do?method=retrievePictureOnPreview"
											height="100" width="150" />
										<br>
									</c:if></td></tr>
									<tr><td class="fontnormalbold"><mifos:mifoslabel name="client.Name"
										bundle="ClientUIResources"></mifos:mifoslabel> <span
										class="fontnormal"> <c:forEach var="customerName"
										items="${requestScope.clientVO.customerNameDetailSet}">
										<c:if test="${customerName.nameType==3}">
											<mifoscustom:lookUpValue id="${customerName.salutation}"
												searchResultName="salutationEntity"></mifoscustom:lookUpValue>
										</c:if>
									</c:forEach> <c:out
										value="${requestScope.clientVO.displayName}" /> <br>
									</span></td></tr> 
									<tr id="Client.GovernmentId"><td class="fontnormalbold"><mifos:mifoslabel
										name="${ConfigurationConstants.GOVERNMENT_ID}" keyhm="Client.GovernmentId" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
									<span class="fontnormal"><c:out
										value="${requestScope.clientVO.governmentId}" /> <br>
									</span></td></tr>
									<tr><td class="fontnormalbold"><mifos:mifoslabel name="client.DateOfBirth"
										bundle="ClientUIResources"></mifos:mifoslabel> <span
										class="fontnormal"><c:out
										value="${sessionScope.clientCreationActionForm.dateOfBirth}" />
									<br>
									</span></td></tr>
									<tr><td class="fontnormalbold"><mifos:mifoslabel name="client.Age"
										bundle="ClientUIResources"></mifos:mifoslabel> <span
										class="fontnormal"><c:out
										value="${sessionScope.clientCreationActionForm.age}" /> <br>
									</span></td></tr>
									<tr><td class="fontnormalbold"> <mifos:mifoslabel name="client.Gender"
										bundle="ClientUIResources"></mifos:mifoslabel> <span
										class="fontnormal"><mifoscustom:lookUpValue
										id="${requestScope.clientVO.customerDetail.gender}"
										searchResultName="genderEntity"></mifoscustom:lookUpValue><br>
									</span></td></tr>
									<tr><td class="fontnormalbold"><mifos:mifoslabel name="client.MaritalStatus"
										bundle="ClientUIResources"></mifos:mifoslabel> <span
										class="fontnormal"><mifoscustom:lookUpValue
										id="${requestScope.clientVO.customerDetail.maritalStatus}"
										searchResultName="maritalStatusEntity"></mifoscustom:lookUpValue>
									<br>
									</span></td></tr>
									<tr><td class="fontnormalbold"><c:forEach var="customerName1"
										items="${requestScope.clientVO.customerNameDetailSet}">
										<c:if test="${customerName1.nameType!=3}">
											<c:set var="spouseFatherType" scope="request"
												value="${customerName1.nameType}" />

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
									</span></td></tr>
									<tr><td class="fontnormalbold"><mifos:mifoslabel
										name="client.NumberOfChildren" bundle="ClientUIResources"></mifos:mifoslabel>
									<span class="fontnormal"> <c:out
										value="${requestScope.clientVO.customerDetail.numChildren}" /><br>
									</span> <%-- Citizenship --%></td></tr>
									<tr id="Client.Citizenship"><td class="fontnormalbold"><mifos:mifoslabel
										name="${ConfigurationConstants.CITIZENSHIP}" keyhm="Client.Citizenship" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/> <span
										class="fontnormal"> <mifoscustom:lookUpValue
										id="${requestScope.clientVO.customerDetail.citizenship}"
										searchResultName="citizenshipEntity"></mifoscustom:lookUpValue><br>
									</span> <%-- Ethinicity --%></td></tr>
									<tr id="Client.Ethinicity"><td class="fontnormalbold"><mifos:mifoslabel
										name="${ConfigurationConstants.ETHINICITY}" keyhm="Client.Ethinicity" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/> <span
										class="fontnormal"> <mifoscustom:lookUpValue
										id="${requestScope.clientVO.customerDetail.ethinicity}"
										searchResultName="ethinicityEntity"></mifoscustom:lookUpValue><br>
									</span></td></tr>
									<tr id="Client.EducationLevel"><td class="fontnormalbold">
									<mifos:mifoslabel name="client.EducationLevel" bundle="ClientUIResources" keyhm="Client.EducationLevel" isManadatoryIndicationNotRequired="yes"/> <span
										class="fontnormal"> <mifoscustom:lookUpValue
										id="${requestScope.clientVO.customerDetail.educationLevel}"
										searchResultName="educationLevelEntity"></mifoscustom:lookUpValue><br>
									</span></td></tr>
									<tr id="Client.BusinessActivities"><td class="fontnormalbold"><mifos:mifoslabel name="client.BusinessActivities"
										bundle="ClientUIResources" keyhm="Client.BusinessActivities" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <span
										class="fontnormal"> <mifoscustom:lookUpValue
										id="${requestScope.clientVO.customerDetail.businessActivities}"
										searchResultName="businessActivitiesEntity"></mifoscustom:lookUpValue><br>
									</span></td></tr>
									<tr id="Client.Handicapped"><td class="fontnormalbold"><mifos:mifoslabel
										name="${ConfigurationConstants.HANDICAPPED}" keyhm="Client.Handicapped" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/> <span
										class="fontnormal"> <mifoscustom:lookUpValue
										id="${requestScope.clientVO.customerDetail.handicapped}"
										searchResultName="handicappedEntity"></mifoscustom:lookUpValue><br>
									</span> </td></tr>
									<tr id="Client.Address"><td class="fontnormalbold"><br>
									<mifos:mifoslabel name="client.Address" bundle="ClientUIResources" keyhm="Client.Address" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>

									<span class="fontnormal"><br>
									</span> <span class="fontnormal"> <c:out
										value="${requestScope.clientVO.displayAddress}" /><br>
									</span></td></tr>
									<tr id="Client.City"><td class="fontnormalbold"><mifos:mifoslabel name="${ConfigurationConstants.CITY}" keyhm="Client.City" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/>
									<span class="fontnormal"> <c:out
										value="${requestScope.clientVO.customerAddressDetail.city}" />
									<br>
									</span></td></tr>
									<tr id="Client.State"><td class="fontnormalbold"><mifos:mifoslabel
										name="${ConfigurationConstants.STATE}" keyhm="Client.State" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/><span
										class="fontnormal"> <c:out
										value="${requestScope.clientVO.customerAddressDetail.state}" />
									<br>
									</span></td></tr>
									<tr id="Client.Country"><td class="fontnormalbold"><mifos:mifoslabel name="client.Country"
										bundle="ClientUIResources" keyhm="Client.Country" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <span
										class="fontnormal"> <c:out
										value="${requestScope.clientVO.customerAddressDetail.country}" /><br>
									</span></td></tr>
									<tr id="Client.PostalCode"><td class="fontnormalbold"><mifos:mifoslabel
										name="${ConfigurationConstants.POSTAL_CODE}" keyhm="Client.PostalCode" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
									<span class="fontnormal"><c:out
										value="${requestScope.clientVO.customerAddressDetail.zip}" />
									<br>
									<br>
									</span> </td></tr>
									<tr id="Client.PhoneNumber"><td class="fontnormalbold"><mifos:mifoslabel name="client.Telephone"
										bundle="ClientUIResources" keyhm="Client.PhoneNumber" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <span
										class="fontnormal"><c:out
										value="${requestScope.clientVO.customerAddressDetail.phoneNumber}" />
									</span><br>

									<!--CustomField addition --> <span class="fontnormal">
									</span> </td></tr>
									<tr><td height="23" class="fontnormalbold"><br><mifos:mifoslabel
										name="client.AdditionalInformationHeading"
										bundle="ClientUIResources"></mifos:mifoslabel><span></span> <span
										class="fontnormal"><br>
									</span> <c:forEach var="cf"
										items="${requestScope.customFields}">
										<c:forEach var="customField"
											items="${requestScope.clientVO.customFieldSet}">
											<c:if test="${cf.fieldId==customField.fieldId}">
												<mifos:mifoslabel name="${cf.lookUpEntity.entityType}"
													bundle="ClientUIResources"></mifos:mifoslabel>: 
		                	 	<span class="fontnormal"><c:out
													value="${customField.fieldValue}" /></span>
												<br>
											</c:if>
										</c:forEach>
									</c:forEach> <br>
									<!-- Edit Button --> <html-el:button
										onclick="goToPersonalPage()" property="editButton"
										styleClass="insidebuttn">
										<mifos:mifoslabel name="button.previousPersonalInfo"
											bundle="ClientUIResources"></mifos:mifoslabel>
									</html-el:button></td>
								</tr>
							</table>
							<%-- Personal Information end --%> <br>
							<%-- MFI Information --%>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td width="100%" height="23" class="fontnormalboldorange"><mifos:mifoslabel
										name="client.MfiInformationLabel" bundle="ClientUIResources"></mifos:mifoslabel>
									</td>
								</tr>
								<tr>
									<td height="23" class="fontnormalbold"><span class="fontnormal"></span>
									<span class="fontnormalbold"> <mifos:mifoslabel
										name="client.LoanOfficer" bundle="ClientUIResources"></mifos:mifoslabel>
									</span> <span class="fontnormal"><c:out
										value="${requestScope.clientVO.personnel.displayName}" /> <br>
									</span> <span class="fontnormal"></span> <c:if
										test="${sessionScope.clientCreationActionForm.isClientUnderGrp == 1}">
										<span class="fontnormalbold"> <mifos:mifoslabel
											name="${ConfigurationConstants.CENTER}" /> <mifos:mifoslabel
											name="client.Centers" bundle="ClientUIResources"></mifos:mifoslabel></span>
										<span class="fontnormal"><c:out
											value="${requestScope.clientVO.parentCustomer.parentCustomer.displayName}" /><br>
										</span>
										<span class="fontnormalbold"><mifos:mifoslabel
											name="${ConfigurationConstants.GROUP}" /> <mifos:mifoslabel
											name="client.Centers" bundle="ClientUIResources"></mifos:mifoslabel></span>
										<span class="fontnormal"> <c:out
											value="${requestScope.clientVO.parentCustomer.displayName}" /></span>
										<span class="fontnormal"><br>
										</span>

									</c:if> <span class="fontnormalbold"><mifos:mifoslabel
										name="client.FormedBy" bundle="ClientUIResources"></mifos:mifoslabel></span>
									<span class="fontnormal"><c:out
										value="${requestScope.clientVO.customerFormedByPersonnel.displayName}" />
									</span><br>
									<span class="fontnormalbold"><mifos:mifoslabel
										name="client.MeetingSchedule" bundle="ClientUIResources"></mifos:mifoslabel></span>
									<span class="fontnormal"><c:out
										value="${requestScope.clientVO.customerMeeting.meeting.meetingSchedule}" />
									</span><br>
									<span class="fontnormalbold"><mifos:mifoslabel
										name="client.LocationOfMeeting" bundle="ClientUIResources"></mifos:mifoslabel></span><span
										class="fontnormal"> <c:out
										value="${requestScope.clientVO.customerMeeting.meeting.meetingPlace}" /><br>
									</span> <br></td></tr>
									<tr id="Client.ExternalId"><td class="fontnormalbold">
									<mifos:mifoslabel name="${ConfigurationConstants.EXTERNALID}" keyhm="Client.ExternalId" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/>
									<span class="fontnormal"> <c:out
										value="${requestScope.clientVO.externalId}" /><br>
									</span></td></tr> 
									<tr id="Client.Trained"><td class="fontnormalbold"><mifos:mifoslabel name="client.Trained"
										bundle="ClientUIResources" keyhm="Client.Trained" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <c:choose>
										<c:when test="${requestScope.clientVO.trained == 1}">
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
										value="${sessionScope.clientCreationActionForm.trainedDate}" />
									<br>
									</span><br></td></tr> 
									<tr><td class="fontnormalbold">
									<mifos:mifoslabel name="client.ChargesApplied"
										bundle="ClientUIResources"></mifos:mifoslabel> <span
										class="fontnormal"><br>
									</span> <%-- First admin fees are displayed --%> <c:forEach
										var="adminFeeMaster" items="${requestScope.adminFeesList}">
										<c:forEach var="adminFee"
											items="${sessionScope.clientCreationActionForm.adminFeeList}">
											<c:if test="${adminFeeMaster.feeId==adminFee.feeId}">
												<c:if test="${adminFee.checkedFee != 1}">
													<c:out value="${adminFee.feeName}" />:
									   <span class="fontnormal"> <c:out
														value="${adminFee.rateOrAmount}" /> <mifos:mifoslabel
														name="client.Periodicity" bundle="ClientUIResources" /> <c:choose>
														<c:when test="${adminFeeMaster.feeFrequencyTypeId == 1}">
															<c:out
																value="${adminFeeMaster.feeMeeting.feeMeetingSchedule}" />
														</c:when>
														<c:otherwise>
															<mifos:mifoslabel name="Fees.onetime" />
														</c:otherwise>
													</c:choose> </span>
													<br>
												</c:if>
											</c:if>
										</c:forEach>
									</c:forEach> <!-- Fee Types ---> <!-- Bug id 26820 .Added the logic to display the data from actionform-->


									<c:forEach var="additionalFee1"
										items="${requestScope.additionalFees}">
										<c:out value="${additionalFee1.feeName}" />:
							<span class="fontnormal"><span class="fontnormal"> <c:out
											value="${additionalFee1.rateOrAmount}" /> <mifos:mifoslabel
											name="client.Periodicity" bundle="ClientUIResources" /> <c:choose>
											<c:when test="${additionalFee1.feeFrequencyTypeId == 1}">
												<c:out
													value="${additionalFee1.feeMeeting.feeMeetingSchedule}" />
											</c:when>
											<c:otherwise>
												<mifos:mifoslabel name="Fees.onetime" />
											</c:otherwise>
										</c:choose> </span><br>
										</span>
									</c:forEach> <br>
									<!-- Additional Fees preview end --> <!-- Edit MFI Detail Button -->
									<html-el:button onclick="goToMfiPage()" property="editButton"
										styleClass="insidebuttn">
										<mifos:mifoslabel name="button.previousMFIInfo"
											bundle="ClientUIResources"></mifos:mifoslabel>
									</html-el:button></td>
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
									<td align="center">&nbsp; <html-el:button
										property="submitButton1" styleClass="buttn"
										onclick="setClientStatus('1')" style="width:130px">
										<mifos:mifoslabel name="button.SaveForLater"
											bundle="ClientUIResources"></mifos:mifoslabel>
									</html-el:button> &nbsp; &nbsp; <c:choose>
										<c:when test="${sessionScope.pendingApprovalDefined eq 'Yes'}">
											<html-el:button property="submitButton" styleClass="buttn"
												onclick="setClientStatus('2')" style="width:130px">
												<mifos:mifoslabel name="button.SubmitForApproval"
													bundle="ClientUIResources"></mifos:mifoslabel>
											</html-el:button>
										</c:when>
										<c:otherwise>
											<html-el:button property="submitButton" styleClass="buttn"
												onclick="setClientStatus('3')" style="width:80px">
												<mifos:mifoslabel name="button.Approved"
													bundle="ClientUIResources"></mifos:mifoslabel>
											</html-el:button>
										</c:otherwise>
									</c:choose> &nbsp; &nbsp; <html-el:button
										onclick="goToCancelPage();" property="cancelButton"
										styleClass="cancelbuttn" style="width:70px">
										<mifos:mifoslabel name="button.cancel"
											bundle="ClientUIResources"></mifos:mifoslabel>
									</html-el:button></td>
								</tr>
							</table>
							<!-- Submit and cancel buttons end --> <br>
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

