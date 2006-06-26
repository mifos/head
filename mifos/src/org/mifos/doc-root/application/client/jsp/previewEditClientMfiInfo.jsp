<!--
/**

* previewEditClientMfiInfo.jsp    version: 1.0



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


<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">

		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<script language="javascript">
  
  function goToMfiPage(){
	clientCreationActionForm.action="clientCreationAction.do?method=editMFIInfo";
	clientCreationActionForm.input.value="previous";
	clientCreationActionForm.submit();
  }
  function goToCancelPage(){
	clientCreationActionForm.action="clientCreationAction.do?method=cancel";
	clientCreationActionForm.submit();
  }
</script>
		<html-el:form action="clientCreationAction.do?method=updateMfi"
			onsubmit="func_disableSubmitBtn('submitButton');">
			<html-el:hidden property="input" value="editMfiInfo" />
			<html-el:hidden property="groupFlag"
				value="${requestScope.clientVO.groupFlag}" />
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
								value="${sessionScope.linkValues.customerName}" /> - </span> <mifos:mifoslabel
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
						<tr>
							<td width="50%" height="23" class="fontnormalboldorange"><mifos:mifoslabel
								name="client.MfiInformationLabel" bundle="ClientUIResources"></mifos:mifoslabel></td>
						</tr>
						<tr>
							<td height="23" class="fontnormalbold"><mifos:mifoslabel
								name="client.LoanOfficer" bundle="ClientUIResources"></mifos:mifoslabel>
							<span></span> <span class="fontnormal"><c:out
								value="${requestScope.clientVO.personnel.displayName}" /> <br>
							</span> <span class="fontnormal"></span> <c:if
								test="${requestScope.clientVO.groupFlag == 1}">
								<span class="fontnormalbold"><mifos:mifoslabel
									name="${ConfigurationConstants.CENTER}" /><mifos:mifoslabel
									name="client.Centers" bundle="ClientUIResources"></mifos:mifoslabel></span>
								<span class="fontnormal"><c:out
									value="${sessionScope.oldClient.parentCustomer.parentCustomer.displayName}" /><br>
								</span>
								<span class="fontnormalbold"><mifos:mifoslabel
									name="${ConfigurationConstants.GROUP}" /><mifos:mifoslabel
									name="client.Centers" bundle="ClientUIResources"></mifos:mifoslabel></span>
								<span class="fontnormal"> <c:out
									value="${sessionScope.oldClient.parentCustomer.displayName}" /></span>
								<span class="fontnormal"><br>
								</span>
							</c:if> <br>
						</tr>
						<tr>
							<td class="fontnormalbold">
							<mifos:mifoslabel name="client.FormedBy"
								bundle="ClientUIResources"></mifos:mifoslabel> <span
								class="fontnormal"><c:out
								value="${requestScope.clientVO.customerFormedByPersonnel.displayName}" /><br>
							</span> 
						</tr>
						<tr id="Client.ExternalId">
							<td class="fontnormalbold"><mifos:mifoslabel
								name="${ConfigurationConstants.EXTERNALID}" keyhm="Client.ExternalId" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
							<span class="fontnormal"> <c:out
								value="${requestScope.clientVO.externalId}" /><br>
							</span> 
						</tr>
						<tr id="Client.Trained">
							<td class="fontnormalbold"><mifos:mifoslabel name="client.Trained"
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
							</c:choose> 
						</tr>
						<tr id="Client.TrainedDate">
							<td class="fontnormalbold"><mifos:mifoslabel name="client.TrainedOnDate"
								bundle="ClientUIResources" keyhm="Client.TrainedDate" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <span
								class="fontnormal"> <c:out
								value="${sessionScope.clientCreationActionForm.trainedDate}" />
							<br>
							</span> <%-- <mifos:mifoslabel name="client.Confidential" bundle="ClientUIResources"></mifos:mifoslabel>
            	<c:choose>
					<c:when test="${requestScope.clientVO.clientConfidential == 1}">
                    	<span class="fontnormal"><mifos:mifoslabel name="client.YesLabel" bundle="ClientUIResources"></mifos:mifoslabel><br></span>
                    </c:when>
				    <c:otherwise>
	                    <span class="fontnormal"><mifos:mifoslabel name="client.NoLabel" bundle="ClientUIResources"></mifos:mifoslabel><br></span>
				    </c:otherwise>
	   	 		</c:choose> --%> 
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
		</html-el:form>

	</tiles:put>
</tiles:insert>

