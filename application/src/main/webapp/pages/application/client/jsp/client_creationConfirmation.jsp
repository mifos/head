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
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.ClientUIResources"/>

<!-- Inserting tile defintion for header and menu -->
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<span id="page.id" title="ClientCreationConfirmation" />	
		<!-- Body Begins -->
		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td align="left" valign="top" class="paddingL15T15">
				<table width="98%" border="0" cellspacing="0" cellpadding="3">
					<!-- Center confirmation message -->
					<tr>
						<td class="headingorange">
							<span id="client_creationConfirmation.heading"><fmt:message key="client.ConfirmationMessage">
								<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
							</fmt:message></span><br>
						<br>
						</td>
					</tr>
					<tr>
						<!-- Displays the center system id and name of the center -->
						<td class="fontnormalbold"><mifos:mifoslabel
							name="client.Confirmation.Note" bundle="ClientUIResources"></mifos:mifoslabel>
						<span class="fontnormal" id="client_creationConfirmation.text.confirmation"><c:out
							value="${sessionScope.clientCustActionForm.clientName.displayName}" /> <mifos:mifoslabel
							name="client.Confirmation.NameSystemID"
							bundle="ClientUIResources"></mifos:mifoslabel></span> <c:out
							value="${sessionScope.clientCustActionForm.globalCustNum}" /> <span
							class="fontnormal"><br>
						<fmt:message key="client.Confirmation.Information">
							<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
							<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
						</fmt:message> <br>
						<br>
						</span> <!-- Link to view the center details -->
						<c:choose>
						<c:when test="${sessionScope.clientCustActionForm.groupFlag eq '1'}">
						<a id="client_creationConfirmation.link.viewClientDetailsLink" href="clientCustAction.do?method=get&globalCustNum=<c:out value="${sessionScope.clientCustActionForm.globalCustNum}"/>&recordOfficeId=${sessionScope.clientCustActionForm.officeId}&recordLoanOfficerId=${sessionScope.clientCustActionForm.loanOfficerId}">
						</c:when>
						<c:otherwise>
						<a id="client_creationConfirmation.link.viewClientDetailsLink" href="clientCustAction.do?method=get&globalCustNum=<c:out value="${sessionScope.clientCustActionForm.globalCustNum}"/>&recordOfficeId=${sessionScope.clientCustActionForm.officeId}&recordLoanOfficerId=${sessionScope.clientCustActionForm.loanOfficerId}">
						</c:otherwise>
						</c:choose>

						<fmt:message key="client.ViewClientDetailsLink">
							<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
						</fmt:message>
						</a> <span class="fontnormal"><br>
						<br>
						</span><span class="fontnormalboldorange"> <mifos:mifoslabel
							name="client.Confirmation.NextStep" bundle="ClientUIResources"></mifos:mifoslabel>

						</span><br>
						<c:if test="${requestScope.clientVO.statusId == 3}">
							<span class="fontnormal"></span>
							<fmt:message key="client.AccountsHeading">
								<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
							</fmt:message>
							<br>
						</c:if> <span class="fontnormal"> <!-- Link to create a new savings  account link -->
						<c:if test="${requestScope.clientVO.statusId == 3}">
							<html-el:link styleId="client_creationConfirmation.link.createNewSavingsAccount"
								href="savingsAction.do?method=getPrdOfferings&customerId=${sessionScope.clientCustActionForm.customerId}&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<fmt:message key="client.CreateNewClientLink">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" /></fmt:param>
								</fmt:message>
							</html-el:link>
							<br>
						</c:if> <!-- Link to create a new loan  account link --> <!-- bug id 29352. Added if condition to show link to open accounts if only client is active-->
						<c:if test="${requestScope.clientVO.statusId == 3}">
							<html-el:link styleId="client_creationConfirmation.link.createNewLoanAccount"
								href="loanAction.do?method=getPrdOfferings&customer.customerId=${sessionScope.clientCustActionForm.customerId}&randomNUm=${sessionScope.randomNUm}">
								<fmt:message key="client.CreateNewClientLink">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /></fmt:param>
								</fmt:message>
							</html-el:link>
							<br>
						</c:if> <!-- Link to create a new group -->
						<c:choose>
						<c:when test="${sessionScope.clientCustActionForm.groupFlag eq '1'}">
						<a id="client_creationConfirmation.link.createNewGroup" href="groupCustAction.do?method=hierarchyCheck&randomNUm=${sessionScope.randomNUm}&input=createGroup&office.officeId=<c:out value="${sessionScope.clientCustActionForm.officeId}"/>">
						</c:when>
						<c:otherwise>
						<a id="client_creationConfirmation.link.createNewGroup" href="groupCustAction.do?method=hierarchyCheck&randomNUm=${sessionScope.randomNUm}&input=createGroup&office.officeId=<c:out value="${sessionScope.clientCustActionForm.officeId}"/>">
						</c:otherwise>
						</c:choose>

						<fmt:message key="client.createNewClient">
							<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
						</fmt:message> </a> <br>
						<!-- Link to create a new client --> <a id="client_creationConfirmation.link.createNewClient"
							href="groupCustAction.do?method=loadSearch&input=createClient&recordOfficeId=0&recordLoanOfficerId=0" />
						<fmt:message key="client.createNewClient">
							<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
						</fmt:message> </a></span></td>
					</tr>
				</table>
				<br>
				<br>
				</td>
			</tr>
		</table>
		<br>
		<td></td>
		<tr></tr>
		<table></table>
		<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		<body></body>
		<html></html>
	</tiles:put>
</tiles:insert>
