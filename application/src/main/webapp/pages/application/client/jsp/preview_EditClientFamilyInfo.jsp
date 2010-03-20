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
	<span id="page.id" title="PreviewEditClientFamilyInfo" />
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<script language="javascript">

 
  function goToCancelPage(){
	clientCustActionForm.action="clientCustAction.do?method=cancel";
	clientCustActionForm.submit();
  }

  function goToEditPage(){
	clientCustActionForm.action="clientCustAction.do?method=editPreviewEditFamilyInfo&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}";
	clientCustActionForm.submit();
  }
</script>
		<html-el:form action="clientCustAction.do?method=updateFamilyInfo"
			onsubmit="func_disableSubmitBtn('submitButton');">
						<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'CenterHierarchyExist')}" var="CenterHierarchyExist" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
				   var="BusinessKey" />
			<html-el:hidden property="input" value="editFamilyInfo" />
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
								value="${BusinessKey.displayName}" /> - </span> <mifos:mifoslabel
								name="client.PreviewFamilyInformation" bundle="ClientUIResources"></mifos:mifoslabel>

							</td>
							<td></td>
						</tr>
						<tr>
							<td class="fontnormal"><mifos:mifoslabel
								name="client.CreatePreviewPageInstruction"
								bundle="ClientUIResources"></mifos:mifoslabel> &nbsp; 
								<fmt:message key="client.EditPageCancelInstruction">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" bundle="ClientUIResources"/></fmt:param>
								</fmt:message></td>

						</tr>
					</table>
					<br>

					<table border="0" cellspacing="0" cellpadding="0">
													<tr class="fontnormal">
													<td>
													<span class="fontnormalbold">
													<mifos:mifoslabel name="client.FamilyRelationship"
													bundle="ClientUIResources"></mifos:mifoslabel>
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
														<td class="paddingL10">
															<c:out value="${familyDetails.displayName}"/>	   
														</td>		
														<td class="paddingL10">
															<c:out value="${familyDetails.dateOfBirth}"/>
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
																	<c:if test = "${livingStatusEntity.id == familyDetails.livingStatus}">
																		<c:out value="${livingStatusEntity.name}"/>
																	</c:if>
															</c:forEach>
														</td>
													</tr>
													</c:forEach>
												
									
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
							<td align="center">&nbsp; <html-el:submit styleId="preview_EditClientFamilyInfo.button.submit" property="submitButton"
								styleClass="buttn"><mifos:mifoslabel name="button.submit"
									bundle="ClientUIResources"></mifos:mifoslabel></html-el:submit> &nbsp; &nbsp;
								 <html-el:button styleId="create_ClientFamilyInfo.button.cancel"
										onclick="goToEditPage();" property="editButton"
										styleClass="cancelbuttn">
										<mifos:mifoslabel name="button.edit"
											bundle="ClientUIResources"></mifos:mifoslabel>
									</html-el:button>&nbsp; &nbsp;
								
							 <html-el:button styleId="preview_EditClientFamilyInfo.button.cancel"
								onclick="goToCancelPage();" property="cancelButton"
								styleClass="cancelbuttn">
								<mifos:mifoslabel name="button.cancel"
									bundle="ClientUIResources"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
					</table>
					<!-- Submit and cancel buttons end --></td>
				</tr>
			</table>
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>

	</tiles:put>
</tiles:insert>

