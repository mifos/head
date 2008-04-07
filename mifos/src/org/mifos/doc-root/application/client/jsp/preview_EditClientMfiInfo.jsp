<!--
/**

* previewEditClientMfiInfo.jsp    version: 1.0



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
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">

		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<script language="javascript">

  function goToMfiPage(){
	clientCustActionForm.action="clientCustAction.do?method=prevEditMfiInfo";
	clientCustActionForm.submit();
  }
  function goToCancelPage(){
	clientCustActionForm.action="clientCustAction.do?method=cancel";
	clientCustActionForm.submit();
  }
</script>
		<html-el:form action="clientCustAction.do?method=updateMfiInfo"
			onsubmit="func_disableSubmitBtn('submitButton');">
						<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'CenterHierarchyExist')}" var="CenterHierarchyExist" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
				   var="BusinessKey" />
			<html-el:hidden property="input" value="editMfiInfo" />
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
								name="client.PreviewMFIInformation" bundle="ClientUIResources"></mifos:mifoslabel>

							</td>
							<td></td>
						</tr>
						<tr>
							<td class="fontnormal"><mifos:mifoslabel
								name="client.CreatePreviewPageInstruction"
								bundle="ClientUIResources"></mifos:mifoslabel> &nbsp; <mifos:mifoslabel
								name="client.EditPageCancelInstruction1"
								bundle="ClientUIResources"></mifos:mifoslabel> <mifos:mifoslabel
								name="${ConfigurationConstants.CLIENT}"
								bundle="ClientUIResources"></mifos:mifoslabel> <mifos:mifoslabel
								name="client.EditPageCancelInstruction2"
								bundle="ClientUIResources"></mifos:mifoslabel></td>

						</tr>
					</table>
					<br>

					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<font class="fontnormalRedBold"><html-el:errors
							bundle="ClientUIResources" /> </font>
						<tr>
							<td width="50%" height="23" class="fontnormalboldorange"><mifos:mifoslabel
								name="client.MfiInformationLabel" bundle="ClientUIResources"></mifos:mifoslabel></td>
						</tr>
						<tr>
							<td height="23" class="fontnormalbold">
							<c:if test="${sessionScope.clientCustActionForm.groupFlag eq '0'}">
								<mifos:mifoslabel name="client.LoanOfficer" bundle="ClientUIResources"></mifos:mifoslabel>
									<c:forEach var="LO" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanOfficers')}">
										<c:if test = "${LO.personnelId == sessionScope.clientCustActionForm.loanOfficerId}">
											<span class="fontnormal"><c:out value="${LO.displayName}"/><br></span>
										</c:if>
									</c:forEach>
							</c:if><br>
							<c:if test="${sessionScope.clientCustActionForm.groupFlag eq '1'}">
								<span class="fontnormalbold"> <mifos:mifoslabel	name="client.LoanOfficer" bundle="ClientUIResources"></mifos:mifoslabel></span>
								<span class="fontnormal">
									<c:if test="${!empty BusinessKey.personnel}">
										<c:out value="${BusinessKey.personnel.displayName}" />
									</c:if>
									<br>
								</span>
								<span class="fontnormalbold">
								<c:if test="${CenterHierarchyExist == true}">
									<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
									<mifos:mifoslabel name="client.Centers" bundle="ClientUIResources"></mifos:mifoslabel></span>
									<span class="fontnormal"><c:out	value="${sessionScope.clientCustActionForm.parentGroup.parentCustomer.displayName}" /><br></span>
									</c:if>
									<span class="fontnormalbold">
									  <mifos:mifoslabel	name="${ConfigurationConstants.GROUP}" />
									  <mifos:mifoslabel	name="client.Centers" bundle="ClientUIResources"></mifos:mifoslabel></span>
									  <span class="fontnormal"><c:out value="${sessionScope.clientCustActionForm.parentGroup.displayName}" /></span>
									  <span class="fontnormal"><br></span>
							</c:if> <br>
						</tr>
						<tr>
							<td class="fontnormalbold">
							<mifos:mifoslabel name="client.FormedBy"
								bundle="ClientUIResources"></mifos:mifoslabel> <span
								class="fontnormal"><c:out
								value="${BusinessKey.customerFormedByPersonnel.displayName}" /><br>
							</span>
						</tr>
						<tr id="Client.ExternalId">
							<td class="fontnormalbold"><mifos:mifoslabel
								name="${ConfigurationConstants.EXTERNALID}" keyhm="Client.ExternalId" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
							<span class="fontnormal"> <c:out
								value="${sessionScope.clientCustActionForm.externalId}" /><br>
							</span>
						</tr>
						<tr id="Client.Trained">
							<td class="fontnormalbold"><html-el:hidden property="trained" value="${sessionScope.clientCustActionForm.trained}"/><mifos:mifoslabel name="client.Trained"
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
							</c:choose>
						</tr>
						<tr id="Client.TrainedDate">
							<td class="fontnormalbold"><mifos:mifoslabel name="client.TrainedOnDate"
								bundle="ClientUIResources" keyhm="Client.TrainedDate" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <span
								class="fontnormal"> <c:out
								value="${sessionScope.clientCustActionForm.trainedDate}" />
							<br>
							</span>
	   	 				</tr>
						<tr>
							<td class="fontnormalbold">
	   	 					<span class="fontnormal"><br>
							</span> <!-- Edit MFI Detail Button --> <html-el:button
								onclick="goToMfiPage()" property="editButton"
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
							<td align="center">&nbsp; <html-el:submit property="submitButton"
								styleClass="buttn">
								<mifos:mifoslabel name="button.submit"
									bundle="ClientUIResources"></mifos:mifoslabel>
							</html-el:submit> &nbsp; &nbsp; <html-el:button
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

