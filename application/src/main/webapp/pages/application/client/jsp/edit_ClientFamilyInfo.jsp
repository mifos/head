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
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.ClientUIResources"/>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<span id="page.id" title="EditClientFamilyInfo" />
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<script language="javascript">

 
  function goToCancelPage(){
	clientCustActionForm.action="clientCustAction.do?method=cancel";
	clientCustActionForm.submit();
  }

  function goToPreviewPage(){
	clientCustActionForm.action="clientCustAction.do?method=previewEditFamilyInfo";
	clientCustActionForm.submit();
  }
  </script>

<script language="javascript">

	/***<!-- NLO : Date: 15/07/2006  -->
	<!--
	     These functions check to see if the Father/Spouse Select is equal to Spouse
	     If Father/Spouse is equal to Spouse and the Marital Status has not already been selected
	     Then Marital Status is set to Married automatically
	  -->
	**/
	var bAlreadySelected;
	bAlreadySelected = false;

	

	function MaritalStatusSelected(){
		bAlreadySelected = true;
	}



		function InsertRow(){
			clientCustActionForm.action="clientCustAction.do?method=editAddFamilyRow";
			clientCustActionForm.submit();
		}

		function deleteThisRow(row){
			document.forms["clientCustActionForm"].elements["deleteThisRow"].value=row;
			clientCustActionForm.action="clientCustAction.do?method=editDeleteFamilyRow";
			clientCustActionForm.submit();
		}
	
</script>
<html-el:form action="clientCustAction.do?method=previewEditPersonalInfo"
			enctype="multipart/form-data" onsubmit="return chkForValidDates()">
						<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'CenterHierarchyExist')}" var="CenterHierarchyExist" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
				   var="BusinessKey" />
			<html-el:hidden property="input" value="editFamilyInfo" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <customtags:headerLink/> </span>
					</td>
				</tr>
			</table>
			
			<!-- Pipeline Bar -->
					<table width="95%" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange"><span class="heading"><c:out
								value="${sessionScope.clientCustActionForm.clientName.displayName}" /> - </span><mifos:mifoslabel
								name="family.EditFamilyInformationLink"
								bundle="ClientUIResources"></mifos:mifoslabel>
							</td>
						</tr>
						<tr>
							<td class="fontnormal"><mifos:mifoslabel
								name="client.PreviewEditInfoInstruction"
								bundle="ClientUIResources"></mifos:mifoslabel> 
								<fmt:message key="client.EditPageCancelInstruction">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"
										bundle="ClientUIResources"/></fmt:param>
								</fmt:message> <span
								class="mandatorytext"><font color="#FF0000">*</font></span> <mifos:mifoslabel
								name="client.FieldInstruction" bundle="ClientUIResources"></mifos:mifoslabel>
							</td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="3" cellspacing="0">
						<font class="fontnormalRedBold"><span id="edit_ClientFamilyInfo.error.message"><html-el:errors
							bundle="ClientUIResources" /></span> </font>
						<tr>
							<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
								name="family.FamilyInformationHeading"
								bundle="ClientUIResources"></mifos:mifoslabel><br>
							<br>
							</td>
						</tr>
					</table>
					</td>
					</tr>
					</table>
					<br>
					<table width="80%" border="0" align="center" cellpadding="0" id="familyTable"
						cellspacing="0">
						<tr class="fontnormal">		
										<td>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr class="fontnormal">
												<td class="paddingL10">
												<mifos:mifoslabel name="client.FamilyRelationship" mandatory="yes"
												bundle="ClientUIResources"></mifos:mifoslabel>
												</td>
													<td class="paddingL10">
														<span id="create_ClientFamilyInfo.label.familyFirstName">
														<mifos:mifoslabel	name="client.FamilyFirstName" mandatory="yes"
														   bundle="ClientUIResources">
														   </mifos:mifoslabel></span>
													</td>
													
													<td class="paddingL10">
													<span id="create_ClientFamilyInfo.label.familyMiddleName">
															<mifos:mifoslabel  name="client.FamilyMiddleName"  
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
													<td class="paddingL10">
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
													<td>
													</td>
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
															<mifos:mifosalphanumtext name="clientCustActionForm" property="familyMiddleName[${row}]">																	
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
																<html-el:link href="javascript:deleteThisRow(${row})" styleId="edit_ClientFamilyInfo.button.deleteRow"
																	 property="editButton" >
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
						<table width="93%" border="0" cellpadding="0" cellspacing="0">	
						<tr>
							<td>	
							<!-- Buttons -->
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center" class="blueline">&nbsp;</td>
								</tr>
							</table>
							<br>
							</td>
							<td>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
								<td>
									<input type="hidden" name="deleteThisRow" bundle="ClientUIResources"/>
								</td>
									<td align="center">
																						
											<html:hidden property="nextOrPreview" value="preview" />

											<html-el:button styleId="edit_ClientFamilyInfo.button.preview" onclick="goToPreviewPage();"
												property="submitButton" styleClass="buttn">
												<mifos:mifoslabel name="button.preview"
													bundle="ClientUIResources"></mifos:mifoslabel>
											</html-el:button>
										 &nbsp; &nbsp; <html-el:button styleId="edit_ClientFamilyInfo.button.addRow"
										onclick="InsertRow();" property="addrowButton"
										styleClass="cancelbuttn">
										<mifos:mifoslabel name="button.addrow"
											bundle="ClientUIResources"></mifos:mifoslabel>
									
									</html-el:button>&nbsp; &nbsp; <html-el:button styleId="edit_ClientFamilyInfo.button.cancel"
										onclick="goToCancelPage();" property="cancelButton"
										styleClass="cancelbuttn">
										<mifos:mifoslabel name="button.cancel"
											bundle="ClientUIResources"></mifos:mifoslabel>
									</html-el:button></td>
								</tr>
							 </table>
							 </td>
							<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
							<!-- Button end --> <br>
							<!-- before main closing -->
						</tr>
					</table>
					
		</html-el:form>
	</tiles:put>
</tiles:insert>
