<!--
/**

* clientCreationConfirmation    version: 1.0



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
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>

<!-- Inserting tile defintion for header and menu -->
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<!-- Body Begins -->
		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td align="left" valign="top" class="paddingL15T15">
				<table width="98%" border="0" cellspacing="0" cellpadding="3">
					<!-- Center confirmation message -->
					<tr>
						<td class="headingorange"><mifos:mifoslabel
							name="client.ConfirmationMessage" bundle="ClientUIResources"></mifos:mifoslabel>
						<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /><br>
						<br>
						</td>
					</tr>
					<tr>
						<!-- Displays the center system id and name of the center -->
						<td class="fontnormalbold"><mifos:mifoslabel
							name="client.Confirmation.Note" bundle="ClientUIResources"></mifos:mifoslabel>
						<span class="fontnormal"> <c:out
							value="${sessionScope.clientCustActionForm.clientName.displayName}" /> <mifos:mifoslabel
							name="client.Confirmation.NameSystemID"
							bundle="ClientUIResources"></mifos:mifoslabel></span> <c:out
							value="${sessionScope.clientCustActionForm.globalCustNum}" /> <span
							class="fontnormal"><br>
						<mifos:mifoslabel name="client.Confirmation.Information1"
							bundle="ClientUIResources"></mifos:mifoslabel> <mifos:mifoslabel
							name="${ConfigurationConstants.CLIENT}" /> <mifos:mifoslabel
							name="client.Confirmation.Information2"
							bundle="ClientUIResources"></mifos:mifoslabel> <mifos:mifoslabel
							name="${ConfigurationConstants.CLIENT}" /><mifos:mifoslabel
							name="client.Confirmation.Information3"
							bundle="ClientUIResources"></mifos:mifoslabel> <br>
						<br>
						</span> <!-- Link to view the center details --> 
						<c:choose>
						<c:when test="${sessionScope.clientCustActionForm.groupFlag eq '1'}">
						<a href="clientCustAction.do?method=get&globalCustNum=<c:out value="${sessionScope.clientCustActionForm.globalCustNum}"/>&recordOfficeId=${sessionScope.clientCustActionForm.parentGroup.office.officeId}&recordLoanOfficerId=${sessionScope.clientCustActionForm.parentGroup.personnel.personnelId}">
						</c:when>
						<c:otherwise>
						<a href="clientCustAction.do?method=get&globalCustNum=<c:out value="${sessionScope.clientCustActionForm.globalCustNum}"/>&recordOfficeId=${sessionScope.clientCustActionForm.officeId}&recordLoanOfficerId=${sessionScope.clientCustActionForm.loanOfficerId}">
						</c:otherwise>
						</c:choose>
						
						<mifos:mifoslabel name="client.ViewClientDetailsLink1"
							bundle="ClientUIResources"></mifos:mifoslabel> <mifos:mifoslabel
							name="${ConfigurationConstants.CLIENT}" /> <mifos:mifoslabel
							name="client.ViewClientDetailsLink2" bundle="ClientUIResources"></mifos:mifoslabel>
						</a> <span class="fontnormal"><br>
						<br>
						</span><span class="fontnormalboldorange"> <mifos:mifoslabel
							name="client.Confirmation.NextStep" bundle="ClientUIResources"></mifos:mifoslabel>

						</span><br>
						<c:if test="${requestScope.clientVO.statusId == 3}">
							<span class="fontnormal"></span>
							<mifos:mifoslabel name="client.AccountsHeading"
								bundle="ClientUIResources"></mifos:mifoslabel>
							<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />
							<br>
						</c:if> <span class="fontnormal"> <!-- Link to create a new savings  account link -->
						<c:if test="${requestScope.clientVO.statusId == 3}">
							<html-el:link
								href="savingsAction.do?method=getPrdOfferings&customerId=${sessionScope.clientCustActionForm.customerId}&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}">
								<mifos:mifoslabel name="client.CreateNewClientLink"
									bundle="ClientUIResources" />
								<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" />
								<mifos:mifoslabel name="client.SavingsAccountLink"
									bundle="ClientUIResources" />

							</html-el:link>
							<br>
						</c:if> <!-- Link to create a new loan  account link --> <!-- bug id 29352. Added if condition to show link to open accounts if only client is active-->
						<c:if test="${requestScope.clientVO.statusId == 3}">
							<html-el:link
								href="loanAction.do?method=getPrdOfferings&customer.customerId=${sessionScope.clientCustActionForm.customerId}">
								<mifos:mifoslabel name="client.CreateNewClientLink"
									bundle="ClientUIResources" />
								<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
								<mifos:mifoslabel name="client.SavingsAccountLink"
									bundle="ClientUIResources" />
							</html-el:link>
							<br>
						</c:if> <!-- Link to create a new group --> 
						<c:choose>
						<c:when test="${sessionScope.clientCustActionForm.groupFlag eq '1'}">
						<a	href="GroupAction.do?method=hierarchyCheck&office.officeId=<c:out value="${sessionScope.clientCustActionForm.parentGroup.office.officeId}"/>">
						</c:when>
						<c:otherwise>
						<a	href="GroupAction.do?method=hierarchyCheck&office.officeId=<c:out value="${sessionScope.clientCustActionForm.officeId}"/>">
						</c:otherwise>
						</c:choose>
						
						<mifos:mifoslabel name="client.CreateNewClientLink"
							bundle="ClientUIResources" /> <mifos:mifoslabel
							name="${ConfigurationConstants.GROUP}" /> </a> <br>
						<!-- Link to create a new client --> <a
							href="clientCreationAction.do?method=preLoad&recordOfficeId=0&recordLoanOfficerId=0" />
						<mifos:mifoslabel name="client.CreateNewClientLink"
							bundle="ClientUIResources" /> <mifos:mifoslabel
							name="${ConfigurationConstants.CLIENT}" /> </a></span></td>
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

		<body></body>
		<html></html>
	</tiles:put>
</tiles:insert>
