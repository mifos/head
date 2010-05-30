<%--
Copyright (c) 2005-2008 Grameen Foundation USA
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
<!-- create_ClientFamilyInfo.jsp -->

<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-bean-el" prefix="bean-el"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="/customer/customerfunctions" prefix="customerfn"%>

<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.ClientUIResources"/>

<!-- Tile  definitions -->
<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
	<span id="page.id" title="CreateClientFamilyInfo" />	
		<script language="javascript" SRC="pages/framework/js/date.js"></script>
		<script language="javascript" src="pages/application/client/js/client.js"></script>
		<script language="javascript">


  function goToCancelPage(){
	  //ConstructString();
	clientCustActionForm.action="clientCustAction.do?method=cancel";
	clientCustActionForm.submit();
  }

  function goToPreviewPage(){
	  //ConstructString()
	clientCustActionForm.action="clientCustAction.do?method=prevFamilyInfoNext";
	clientCustActionForm.submit();
  }
</script>

<script language="javascript"><!--

	/*** NLO : Date: 15/07/2006  
	
	     These functions check to see if the Father/Spouse Select is equal to Spouse
	     If Father/Spouse is equal to Spouse and the Marital Status has not already been selected
	     Then Marital Status is set to Married automatically
	  
	**/
	var bAlreadySelected;
	bAlreadySelected = false;



	function insertRow(){
		clientCustActionForm.action="clientCustAction.do?method=addFamilyRow";
		clientCustActionForm.submit();
		
	}

	function deleteThisRow(row){
		document.forms["clientCustActionForm"].elements["deleteThisRow"].value=row;
		clientCustActionForm.action="clientCustAction.do?method=deleteFamilyRow";
		clientCustActionForm.submit();
		
	}

	

	
--></script>

		<html-el:form action="/clientCustAction.do?method=familyInfoNext"	method="post" enctype="multipart/form-data"
			onsubmit="return ConstructString()">
			<!-- Hidden varaibles for the input page -->
			<html-el:hidden property="input" value="personalInfo" />
			<html-el:hidden property="nextOrPreview" value="next" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'CenterHierarchyExist')}" var="CenterHierarchyExist" />
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
						<!-- Instructions for the user -->
						
					</table>
					<table width="90%" border="0" align="center" cellpadding="0" id="familyTable"
						cellspacing="0" class="bluetableborder">
						<tr>
							<td>
								<font class="fontnormalRedBold">
									<span id="create_ClientFamilyInfo.error.message">
										<html-el:errors bundle="ClientUIResources" />
									</span> 
								</font>
							</td>
						</tr>
						<tr>
							<td align="left" valign="top" class="paddingleftCreates">
							
							<!-- Family Details -->
							<table>
								<tr class="fontnormal">
									<td align="right" class="paddingL10"><mifos:mifoslabel
										name="client.FamilyDetails" mandatory="yes"
										bundle="ClientUIResources"></mifos:mifoslabel></td>
									<td>
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr class="fontnormal">
											
												
											<td class="paddingL10">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr class="fontnormal">
													<td width="14%">
													<span id="create_ClientFamilyInfo.label.familyRelationship">
													<mifos:mifoslabel name="client.FamilyRelationship" mandatory="yes"
													bundle="ClientUIResources"></mifos:mifoslabel>
													</span>
													</td>
														<td class="paddingL10">
															<span id="create_ClientFamilyInfo.label.familyFirstName">
															<mifos:mifoslabel	name="client.FamilyFirstName" mandatory="yes"
															   bundle="ClientUIResources">
															   </mifos:mifoslabel></span>
														</td>
														<td class="paddingL10">
														<span id="create_ClientFamilyInfo.label.familyMiddleName">
																<mifos:mifoslabel keyhm="Client.SpouseFatherMiddleName" name="client.FamilyMiddleName"
																		bundle="ClientUIResources">
																		</mifos:mifoslabel>
														</span>
														</td>
														<td class="paddingL10">
														<span id="create_ClientFamilyInfo.label.familyLastName">
																<mifos:mifoslabel  name="client.FamilyLastName" mandatory="yes"
																		bundle="ClientUIResources">
																		</mifos:mifoslabel>
														</span>
														</td>
														<td class="paddingL10" width="70%">
														<span id="create_ClientFamilyInfo.label.familyDateOfBirth">
															<mifos:mifoslabel	name="client.FamilyDateOfBirth" mandatory="yes" 
																bundle="ClientUIResources"></mifos:mifoslabel>
														</span>
														</td>
														<td class="paddingL10">
														<span id="create_ClientFamilyInfo.label.familyGender">
															<mifos:mifoslabel	name="client.FamilyGender" mandatory="yes"
																bundle="ClientUIResources"></mifos:mifoslabel>
														</span>
														</td>
														<td class="paddingL10">
														<span id="create_ClientFamilyInfo.label.familyLivingStatus">
															<mifos:mifoslabel name="client.FamilyLivingStatus" mandatory="yes"
																bundle="ClientUIResources">
															</mifos:mifoslabel>
														</span>
														</td>
														<td></td>
													</tr>
													<tr>
													<td></td>
													<td></td>
													<td></td><td></td>
													<td class="paddingL10"><font size="-2">(DD /MM /YYYY)</font> </td>
													<td></td>
													<td></td>
													<td></td>
													</tr>
													 <c:forEach begin="0" end="${sessionScope.clientCustActionForm.familySize}" step="1" varStatus="savingsLoopStatus">
								 						<bean:define id="row" toScope="request">
															<c:out value="${savingsLoopStatus.index}" />
														</bean:define>
														<tr>															
																														
															<td width="80px" class="fontnormal">
																<mifos:select name="clientCustActionForm" property="familyRelationship[${row}]">
																	<c:forEach var="familyEntityList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'spouseEntity')}" >
																		<html-el:option value="${familyEntityList.id}">${familyEntityList.name}</html-el:option>
																	</c:forEach>
																</mifos:select>		
															</td>
															<td class="paddingL10">
																<mifos:mifosalphanumtext name="clientCustActionForm" property="familyFirstName[${row}]">																	
																</mifos:mifosalphanumtext>								
															</td>
															<td class="paddingL10">
																<mifos:mifosalphanumtext keyhm="Client.SpouseFatherMiddleName" name="clientCustActionForm" property="familyMiddleName[${row}]">																	
																</mifos:mifosalphanumtext>								
															</td>
															<td class="paddingL10">
																<mifos:mifosalphanumtext name="clientCustActionForm" property="familyLastName[${row}]">																	
																</mifos:mifosalphanumtext>								
															</td>															
															<td class="paddingL10" width="90px">
															<table> 
																<tr>
																	<td>
																		<mifos:mifosalphanumtext name="clientCustActionForm" property="familyDateOfBirthDD[${row}]" maxlength="2" size="2" style="width:20px;">																	
																		</mifos:mifosalphanumtext >				
																	</td>
																	<td>				
																		<mifos:mifosalphanumtext name="clientCustActionForm" property="familyDateOfBirthMM[${row}]" maxlength="2" size="2" style="width:20px;">																	
																		</mifos:mifosalphanumtext >
																	</td>
																	<td>
																		<mifos:mifosalphanumtext name="clientCustActionForm" property="familyDateOfBirthYY[${row}]"  maxlength="4" size="4" style="width:35px;">																	
																		</mifos:mifosalphanumtext>
																	</td>
																</tr>
															</table>
															</td>
															<td class="paddingL10">
																<mifos:select name="clientCustActionForm" property="familyGender[${row}]">
																	<c:forEach var="genderEntityList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'genderEntity')}" >
																		<html-el:option value="${genderEntityList.id}">${genderEntityList.name}</html-el:option>
																	</c:forEach>
																</mifos:select>		
															</td>
															<td class="paddingL10">
																<mifos:select name="clientCustActionForm" property="familyLivingStatus[${row}]">
																	<c:forEach var="livingStatusEntityList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'livingStatusEntity')}" >
																		<html-el:option value="${livingStatusEntityList.id}">${livingStatusEntityList.name}</html-el:option>
																	</c:forEach>
																</mifos:select>		
															</td>
															<td class="paddingL10">
																<c:if test="${row>=1}">
																	<html-el:link href="javascript:deleteThisRow(${row})" styleId="create_ClientFamilyInfo.button.deleteRow"
																		property="deleteRowButton" >
																		<mifos:mifoslabel name="button.deleterow" bundle="ClientUIResources"></mifos:mifoslabel>
																	</html-el:link>
																</c:if>
															</td>
														</tr>
							 						</c:forEach>													
												</table>
											</td>
										</tr>
									</table>
									</td>
								</tr>
								</table>
								
							<!-- Buttons -->
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center" class="blueline">&nbsp;</td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
								<td>
									<input type="hidden" name="deleteThisRow" bundle="ClientUIResources"/>
								</td>
									<td align="center"><c:choose>

										<c:when
											test="${param.method eq 'prevFamilyInfo' or sessionScope.clientCustActionForm.nextOrPreview eq 'preview' or sessionScope.clientCustActionForm.editFamily eq 'edit'}">
											<html:hidden property="nextOrPreview" value="preview" />

											<html-el:button styleId="create_ClientFamilyInfo.button.preview" onclick="goToPreviewPage();"
												property="submitButton" styleClass="buttn">
												<mifos:mifoslabel name="button.preview"
													bundle="ClientUIResources"></mifos:mifoslabel>
											</html-el:button>
										</c:when>
										<c:otherwise>
											<html-el:submit styleId="create_ClientFamilyInfo.button.continue" styleClass="buttn">
												<mifos:mifoslabel name="button.continue"
													bundle="ClientUIResources"></mifos:mifoslabel>
											</html-el:submit>
										</c:otherwise>
									</c:choose> &nbsp; &nbsp; <html-el:button styleId="create_ClientFamilyInfo.button.addRow"
										onclick="insertRow();" property="addrowButton"
										styleClass="cancelbuttn">
										<mifos:mifoslabel name="button.addrow"
											bundle="ClientUIResources"></mifos:mifoslabel>
									
									</html-el:button>&nbsp; &nbsp; <html-el:button styleId="create_ClientFamilyInfo.button.cancel"
										onclick="goToCancelPage();" property="cancelButton"
										styleClass="cancelbuttn">
										<mifos:mifoslabel name="button.cancel"
											bundle="ClientUIResources"></mifos:mifoslabel>
									</html-el:button></td>
								</tr>
							</table>
							<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
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
