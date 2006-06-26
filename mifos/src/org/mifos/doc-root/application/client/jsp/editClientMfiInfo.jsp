<!--
/**

* editClientMfiInfo    version: 1.0



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
		<script>
	
	function goToCancelPage(){
	clientCreationActionForm.action="clientCreationAction.do?method=cancel";
	clientCreationActionForm.submit();
  }	
	</script>

		<html-el:form action="clientCreationAction.do?method=preview"
			onsubmit="return (validateMyForm(trainedDate,trainedDateFormat,trainedDateYY))">
			<html-el:hidden property="input" value="editMfiInfo" />
			<html-el:hidden property="isClientUnderGrp"
				value="${requestScope.clientVO.groupFlag}" />
			<html-el:hidden property="groupFlag"
				value="${requestScope.clientVO.groupFlag}" />
			<html-el:hidden property="statusId"
				value="${requestScope.clientVO.statusId}" />


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
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>

							<td class="headingorange"><span class="heading"><c:out
								value="${sessionScope.linkValues.customerName}" /> </span><mifos:mifoslabel
								name="client.EditMfiInformationTitle" bundle="ClientUIResources"></mifos:mifoslabel></td>
						</tr>
						<tr>
							<td class="fontnormal"><mifos:mifoslabel
								name="client.PreviewEditInfoInstruction"
								bundle="ClientUIResources"></mifos:mifoslabel> <mifos:mifoslabel
								name="client.EditPageCancelInstruction1"
								bundle="ClientUIResources"></mifos:mifoslabel> <mifos:mifoslabel
								name="${ConfigurationConstants.CLIENT}"></mifos:mifoslabel> <mifos:mifoslabel
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
								name="client.MfiInformationLabel" bundle="ClientUIResources"></mifos:mifoslabel><br>
							<br>
							</td>
						</tr>
						<%-- Loan Officer Name --%>
						<c:if test="${requestScope.clientVO.groupFlag == 0}">
							<tr class="fontnormal">
								<td width="17%" align="right"><mifos:mifoslabel
									name="client.LoanOfficer" bundle="ClientUIResources"></mifos:mifoslabel></td>
								<td width="83%"><mifos:select property="loanOfficerId"
									style="width:136px;"
									value="${requestScope.clientVO.personnel.personnelId}">
									<c:forEach var="loanOfficer"
										items="${requestScope.loanOfficers}">
										<html-el:option value="${loanOfficer.personnelId}">
											<c:out value="${loanOfficer.displayName}" />
										</html-el:option>
									</c:forEach>
								</mifos:select></td>
							</tr>

						</c:if>
						<c:if test="${requestScope.clientVO.groupFlag == 1}">
							<html-el:hidden property="loanOfficerId"
								value="${sessionScope.oldClient.parentCustomer.personnel.personnelId}" />
						</c:if>
						<%-- External Id --%>
						<tr class="fontnormal">
							<td width="17%" height="23" align="right" class="fontnormal"><mifos:mifoslabel
								name="client.FormedBy" bundle="ClientUIResources"></mifos:mifoslabel></td>
							<td width="83%"><c:out
								value="${requestScope.clientVO.customerFormedByPersonnel.displayName}" />
							<html-el:hidden property="customerFormedById"
								value="${requestScope.clientVO.customerFormedByPersonnel.personnelId}" />
							</td>
						</tr>
						<tr class="fontnormal">
							<td width="17%" height="23" align="right" class="fontnormal">
							<mifos:mifoslabel keyhm="Client.ExternalId" isColonRequired="yes"
								name="${ConfigurationConstants.EXTERNALID}"></mifos:mifoslabel></td>
							<td width="83%"><mifos:mifosalphanumtext keyhm="Client.ExternalId" property="externalId"
								maxlength="50" value="${requestScope.clientVO.externalId}" /></td>
						</tr>
						<%-- Trained and Training date.. If the client is already trained then the date appears as label else option allowed to choose training --%>
						<c:choose>
							<c:when test="${sessionScope.isTrained == 1}">
								<tr class="fontnormal">
									<td width="25%" align="right" class="fontnormal"><mifos:mifoslabel keyhm="Client.Trained"
										name="client.Trained" bundle="ClientUIResources"></mifos:mifoslabel>
									</td>
									<td width="75%"><mifos:checkbox keyhm="Client.Trained"
										name="clientCreationActionForm" property="trained" value="1"
										disabled="true" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right" class="fontnormal"><mifos:mifoslabel keyhm="Client.TrainedDate"
										name="client.TrainedOnDate" bundle="ClientUIResources"></mifos:mifoslabel>
									</td>
									<td><c:out
										value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.clientVO.trainedDate)}" />
									<html-el:hidden property="trainedDate"
										value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.clientVO.trainedDate)}" />
									</td>
								</tr>
							</c:when>
							<c:otherwise>
								<tr class="fontnormal">
									<td width="25%" align="right" class="fontnormal"><mifos:mifoslabel keyhm="Client.Trained"
										name="client.Trained" bundle="ClientUIResources"></mifos:mifoslabel>
									</td>
									<td width="75%"><mifos:checkbox keyhm="Client.Trained" property="trained" value="1" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right" class="fontnormal"><mifos:mifoslabel keyhm="Client.TrainedDate"
										name="client.TrainedOnDate" bundle="ClientUIResources"></mifos:mifoslabel>
									</td>
									<td><date:datetag keyhm="Client.TrainedDate" property="trainedDate" name="clientVO" /></td>
								</tr>
							</c:otherwise>
						</c:choose>

						<%-- Confidential 
	               <tr class="fontnormal">
	                  <td align="right" class="fontnormal">
	                  	<mifos:mifoslabel name="client.Confidential" bundle="ClientUIResources"></mifos:mifoslabel>
	                  </td>
	                  <td>
	                  	<html-el:checkbox property="clientConfidential" value="1"/>
	                  </td>
	              </tr> --%>
						<td></td>
						<tr></tr>
					</table>
					<br>
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
