<!--
/**

* editClientMfiInfo    version: 1.0



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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.ClientUIResources"/>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<script language="javascript" SRC="pages/framework/js/date.js"></script>
		<script>

	function goToCancelPage(){
	clientCustActionForm.action="clientCustAction.do?method=cancel";
	clientCustActionForm.submit();
  }
	</script>

		<html-el:form action="clientCustAction.do?method=previewEditMfiInfo">
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
				   var="BusinessKey" />
			<html-el:hidden property="input" value="editMfiInfo" />
			<%-- <td align="left" valign="top" bgcolor="#FFFFFF" class="paddingleftmain"> --%>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <customtags:headerLink/> </span>
				</tr>
			</table>
			<%-- Start of information --%>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>

							<td class="headingorange"><span class="heading"><c:out
								value="${BusinessKey.displayName}" /> </span><mifos:mifoslabel
								name="client.EditMfiInformationTitle" bundle="ClientUIResources"></mifos:mifoslabel></td>
						</tr>
						<tr>
							<td class="fontnormal"><mifos:mifoslabel
								name="client.PreviewEditInfoInstruction"
								bundle="ClientUIResources"></mifos:mifoslabel> 
								<fmt:message key="client.EditPageCancelInstruction">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"/></fmt:param>
								</fmt:message> <span
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
						<c:choose>
						<c:when test="${sessionScope.clientCustActionForm.groupFlag eq '0'}">
							<tr class="fontnormal">
								<td width="17%" align="right"><mifos:mifoslabel
									name="client.LoanOfficer" bundle="ClientUIResources"></mifos:mifoslabel></td>
								<td width="83%">
									<mifos:select property="loanOfficerId">
										<c:forEach var="loanOfficersList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanOfficers')}" >
											<html-el:option value="${loanOfficersList.personnelId}">${loanOfficersList.displayName}</html-el:option>
										</c:forEach>
									</mifos:select></td>
							</tr>

						</c:when>
						</c:choose>
						<%-- External Id --%>
						<tr class="fontnormal">
							<td width="17%" height="23" align="right" class="fontnormal"><mifos:mifoslabel
								name="client.FormedBy" bundle="ClientUIResources"></mifos:mifoslabel></td>
							<td width="83%"><c:out
								value="${BusinessKey.customerFormedByPersonnel.displayName}" />
							</td>
						</tr>
						<tr class="fontnormal">
							<td width="17%" height="23" align="right" class="fontnormal">
							<mifos:mifoslabel keyhm="Client.ExternalId" isColonRequired="yes"
								name="${ConfigurationConstants.EXTERNALID}"></mifos:mifoslabel></td>
							<td width="83%">
								<mifos:mifosalphanumtext keyhm="Client.ExternalId" property="externalId" maxlength="50" />
							</td>
						</tr>
						<%-- Trained and Training date.. If the client is already trained then the date appears as label else option allowed to choose training --%>
						<c:choose>
							<c:when test="${BusinessKey.trained == true}">
								<tr class="fontnormal">
									<td width="25%" align="right" class="fontnormal"><mifos:mifoslabel keyhm="Client.Trained"
										name="client.Trained" bundle="ClientUIResources"></mifos:mifoslabel>
									</td>
									<td width="75%"><mifos:checkbox keyhm="Client.Trained"
										name="clientCustActionForm" property="trained" value="1"
										disabled="true" />
										<html-el:hidden property="trained"	value="1" />
										</td>
								</tr>
								<tr class="fontnormal">
									<td align="right" class="fontnormal">
									<mifos:mifoslabel keyhm="Client.TrainedDate"
										name="client.TrainedOnDate" bundle="ClientUIResources"></mifos:mifoslabel>
									</td>
									<td><c:out
										value="${sessionScope.clientCustActionForm.trainedDate}" />
									<html-el:hidden property="trainedDate"
										value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,BusinessKey.trainedDate)}" />
									<html-el:hidden property="trainedDateFormat" value=""/>
									<html-el:hidden property="trainedDateYY" value=""/>
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
									<td align="right" class="fontnormal">
									<mifos:mifoslabel keyhm="Client.TrainedDate" name="client.TrainedOnDate" bundle="ClientUIResources"></mifos:mifoslabel>

									</td>
									<td>
									<date:datetag keyhm="Client.TrainedDate" property="trainedDate" /></td>
								</tr>
							</c:otherwise>
						</c:choose>

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
							<td align="center"><html-el:submit styleClass="buttn">
								<mifos:mifoslabel name="button.preview"
									bundle="ClientUIResources"></mifos:mifoslabel>
							</html-el:submit> &nbsp; &nbsp; <html-el:button
								onclick="goToCancelPage();" property="cancelButton"
								styleClass="cancelbuttn">
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
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
