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
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="/customer/customerfunctions" prefix="customerfn"%>

<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.ClientUIResources"/>

<%-- Struts Tiles definition for the header and menu --%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<span id="page.id" title="ViewClientDetails" />
		<script language="javascript">
  function photopopup(custId , custName, currentFlow){
	  window.open("clientCustAction.do?method=showPicture&customerId="+ custId + "&displayName=" + custName+ "&currentFlowKey=" + currentFlow,null,"height=250,width=200,status=no,scrollbars=no,toolbar=no,menubar=no,location=no");

  }

</script>

		<html-el:form action="clientCustAction.do">
		<!-- The new way to send data is using a data transfer object -->
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'clientDetailsDto')}"
			   var="clientDetailsDto" />
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
			   var="BusinessKey" />
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customerPerformance')}"
			   var="customerPerformance" />
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customerPerformanceHistory')}"
			   var="customerPerformanceHistory" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <customtags:headerLink
						selfLink="false" /> </span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange"><span id="viewClientDetails.heading"><c:out
								value="${clientDetailsDto.displayName}" /></span></td>
							<td rowspan="2" align="right" valign="top" class="headingorange">
							<span class="fontnormal"> <!-- Edit center status link --> <c:if
								test="${BusinessKey.customerStatus.id != 6}">
								<a id="viewClientDetails.link.editStatus" href="editCustomerStatusAction.do?method=loadStatus&customerId=<c:out value="${BusinessKey.customerId}"/>&input=client&currentFlowKey=${requestScope.currentFlowKey}">
									<fmt:message key="client.editStatus">
										<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
									</fmt:message>
								</a>
							</c:if> <br>
							</span></td>
						</tr>
						<tr>
							<td colspan="2"><font class="fontnormalRedBold"><span id="viewClientDetails.error.message"><html-el:errors
								bundle="ClientUIResources" /></span> </font></td>
						</tr>
						<tr>
							<td class="fontnormalbold"><span class="fontnormal">
							<mifoscustom:MifosImage
								id="${BusinessKey.customerStatus.id}" moduleName="org.mifos.application.customer.util.resources.customerImages" /> <span id="viewClientDetails.text.status"><c:out
								value="${BusinessKey.customerStatus.name}" /></span> <c:forEach
								var="flagSet" items="${BusinessKey.customerFlags}">
								<span class="fontnormal"> <c:if
									test="${BusinessKey.blackListed}">
									<mifos:MifosImage id="blackListed" moduleName="org.mifos.application.customer.client.util.resources.clientImages" />
								</c:if> <c:out value="${flagSet.statusFlag.name}" /> </span>
							</c:forEach> </span><br>
							<!-- System Id of the center --> <span class="fontnormal"><mifos:mifoslabel
								name="client.SystemId" bundle="ClientUIResources" isColonRequired="yes"></mifos:mifoslabel></span>
							<span class="fontnormal"><c:out
								value="${BusinessKey.globalCustNum}" /></span><br>
							<!-- Loan Officer of the client --> <span class="fontnormal"> <mifos:mifoslabel
								name="client.LoanOff" bundle="ClientUIResources"></mifos:mifoslabel>
							<c:out value="${BusinessKey.personnel.displayName}" /></span><br>
						</tr>
						<tr id="Client.BusinessActivities">
							<td class="fontnormalbold"><span class="fontnormal"> <mifos:mifoslabel
								name="client.BusinessActivities" bundle="ClientUIResources"
								keyhm="Client.BusinessActivities"
								isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
								<c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'businessActivitiesEntityName')}" /></span> <br>
						</tr>
						<tr>
							<td class="fontnormalbold"><span class="fontnormal"> 
							<c:set var="custId" value="${BusinessKey.customerId}" />
							<c:set var="custName"	value="${BusinessKey.personnel.displayName}" /> 	
							<c:set var="pic"	value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'noPictureOnGet')}" /> 
							<%-- <c:out value="pic val;ue: "/>
							<c:out value="${pic}"/> --%>
							<c:if test="${pic eq 'No'}">
								<a id="viewClientDetails.link.seePhoto" href="javascript:photopopup(${custId} , '${custName}' ,'${requestScope.currentFlowKey}')"><mifos:mifoslabel
								name="client.seephoto" bundle="ClientUIResources"></mifos:mifoslabel></a>
							</c:if> </span><br>
							</td>
						</tr>
					</table>

					<!--- Accounts Information Begins -->
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="36%" class="headingorange"><mifos:mifoslabel
								name="client.AccountHeading" bundle="ClientUIResources"></mifos:mifoslabel></td>
						</tr>
						<!-- bug id 28004. Added if condition to show link to open accounts if only client is active-->
						<c:if test="${BusinessKey.customerStatus.id == 3}">
							<tr align="right">
								<td class="headingorange"><span class="fontnormal"><mifos:mifoslabel
									name="client.AccountsLink" bundle="ClientUIResources"/>&nbsp; <html-el:link styleId="viewClientDetails.link.newLoanAccount"
									href="loanAccountAction.do?method=getPrdOfferings&customerId=${BusinessKey.customerId}&randomNUm=${sessionScope.randomNUm}">
									<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
								</html-el:link> &nbsp;|&nbsp; <html-el:link styleId="viewClientDetails.link.newSavingsAccount"
									href="savingsAction.do?method=getPrdOfferings&customerId=${BusinessKey.customerId}&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
									<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" />
								</html-el:link> </span></td>
							</tr>
						</c:if>
					</table>
					<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customerLoanAccountsInUse')}">
						<table width="96%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td width="63%" align="left" valign="top"
									class="tableContentLightBlue">
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td width="63%"><span class="fontnormalbold"> <mifos:mifoslabel
											name="${ConfigurationConstants.LOAN}" /> </span> <span
											class="fontnormal"></span></td>
									</tr>
								</table>
								<span class="fontnormal"></span>
								<table width="95%" border="0" align="center" cellpadding="0"
									cellspacing="0">
									<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customerLoanAccountsInUse')}"
										var="loan">
										<tr>
											<td>
											<table width="100%" border="0" cellspacing="0"
												cellpadding="0">
												<tr>
													<td width="65%"><span class="fontnormal"> <html-el:link styleId="viewClientDetails.link.viewLoanAccount"
														href="loanAccountAction.do?globalAccountNum=${loan.globalAccountNum}&customerId=${BusinessKey.customerId}&method=get&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
														<c:out value="${loan.loanOffering.prdOfferingName}" />, <mifos:mifoslabel name="client.acc" bundle="ClientUIResources" /><c:out
															value="${loan.globalAccountNum}" />
													</html-el:link> </span></td>
													<td width="35%"><span class="fontnormal"> <mifoscustom:MifosImage
														id="${loan.accountState.id}" moduleName="org.mifos.accounts.loan.util.resources.loanImages" />
													<c:out value="${loan.accountState.name}" /> </span></td>
												</tr>
											</table>
											<c:if
												test="${loan.accountState.id==5 || loan.accountState.id==9}">
												<span class="fontnormal"> <mifos:mifoslabel
													name="loan.outstandingbalance" isColonRequired="yes"/> <c:out
													value="${loan.loanSummary.oustandingBalance}" /><br>
												<mifos:mifoslabel name="loan.amount_due" isColonRequired="yes"/> <c:out
													value="${loan.totalAmountDue}" /> </span>
											</c:if></td>
										</tr>
										<tr>
											<td><img src="pages/framework/images/trans.gif" width="5"
												height="20"></td>
										</tr>
									</c:forEach>
								</table>
								</td>
							</tr>
						</table>

						<table width="50%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td><img src="pages/framework/images/trans.gif" width="10"
									height="10"></td>
							</tr>
						</table>
					</c:if> <c:if
						test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customerSavingsAccountsInUse')}">
						<table width="96%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td width="63%" align="left" valign="top"
									class="tableContentLightBlue">
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td width="63%"><span class="fontnormalbold"> <mifos:mifoslabel
											name="${ConfigurationConstants.SAVINGS}" /> </span> <span
											class="fontnormal"></span></td>
									</tr>
								</table>
								<span class="fontnormal"></span>
								<table width="95%" border="0" align="center" cellpadding="0"
									cellspacing="0">
									<c:forEach
										items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customerSavingsAccountsInUse')}"
										var="savings">
										<tr>
											<td>
											<table width="100%" border="0" cellspacing="0"
												cellpadding="0">
												<tr>
													<td width="65%"><span class="fontnormal"> <html-el:link styleId="viewClientDetails.link.viewSavingsAccount"
														href="savingsAction.do?globalAccountNum=${savings.globalAccountNum}&method=get&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
														<c:out value="${savings.savingsOffering.prdOfferingName}" />, <mifos:mifoslabel name="client.acc" bundle="ClientUIResources" /><c:out
															value="${savings.globalAccountNum}" />
													</html-el:link> </span></td>
													<td width="35%"><span class="fontnormal"> <mifoscustom:MifosImage
														id="${savings.accountState.id}"
														moduleName="org.mifos.accounts.savings.util.resources.savingsImages" /> <c:out
														value="${savings.accountState.name}" /> </span></td>
												</tr>
											</table>
											<span class="fontnormal"><mifos:mifoslabel	name="Client.balance" /> <c:out
												value="${savings.savingsBalance}" /> </span></td>
										</tr>
										<tr>
											<td><img src="pages/framework/images/trans.gif" width="5"
												height="20"></td>
										</tr>
									</c:forEach>
								</table>
								</td>
							</tr>

						</table>
						<table width="50%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td><img src="pages/framework/images/trans.gif" width="10"
									height="10"></td>
							</tr>
						</table>
					</c:if>
					<table width="96%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td width="65%" height="69" align="left" valign="top"
								class="tableContentLightBlue"><span class="fontnormalbold">
								<fmt:message key="client.clientcharges">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
								</fmt:message></span>
							<table width="95%" border="0" align="center" cellpadding="0"
								cellspacing="0">
								<tr>
									<td>
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="53%"><span class="fontnormal"> <a id="viewClientDetails.link.viewDetails"
												href="customerAccountAction.do?method=load&globalCustNum=<c:out value="${BusinessKey.globalCustNum}"/>">
											<mifos:mifoslabel name="client.viewdetails"
												bundle="ClientUIResources" /> </a> </span></td>
										</tr>
									</table>
									<span class="fontnormal"><mifos:mifoslabel name="client.amtdue"
										bundle="ClientUIResources" isColonRequired="yes"/> <c:out
										value="${BusinessKey.customerAccount.nextDueAmount}" />
									</span></td>
								</tr>

								<tr>
									<td><img src="pages/framework/images/trans.gif" width="5"
										height="5"></td>
								</tr>
							</table>
							</td>
						</tr>
					</table>

					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="69%" align="right" class="fontnormal">
							<span class="fontnormal">
								<c:if test="${BusinessKey.customerStatus.id !=1 && BusinessKey.customerStatus.id !=2}">
									<html-el:link styleId="viewClientDetails.link.viewAllClosedAccounts" href="custAction.do?method=getClosedAccounts&customerId=${BusinessKey.customerId}&input=client&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
                  						<mifos:mifoslabel name="client.ClosedAccountsLink" bundle="ClientUIResources" />
                  					</html-el:link>
                  				</c:if>
                  			</span>
                  			</td>
						</tr>
					</table>

					<!-- Account Info ends --> <!--- MFI Information Starts -->
					<table width="50%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="pages/framework/images/trans.gif" width="10"
								height="10"></td>
						</tr>
					</table>
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="63%" height="23" class="headingorange"><mifos:mifoslabel
								name="client.MFIInformationHeading" bundle="ClientUIResources"></mifos:mifoslabel></td>
							<td width="37%" align="right" class="fontnormal"><html-el:link styleId="viewClientDetails.link.editMfiInformation"
								action="clientCustAction.do?method=editMfiInfo&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="client.EditMfiInformationLink"
									bundle="ClientUIResources"></mifos:mifoslabel>
							</html-el:link></td>
						</tr>
						<tr id="Client.ExternalId">
							<td height="23" colspan="2" class="fontnormalbold"><span
								class="fontnormal"> <mifos:mifoslabel
								name="${ConfigurationConstants.EXTERNALID}"
								keyhm="Client.ExternalId" isColonRequired="yes"
								isManadatoryIndicationNotRequired="yes" /> <c:out
								value="${BusinessKey.externalId}" /><br>
							</span>
						</tr>
						<tr>
							<td height="23" colspan="2" class="fontnormalbold"><span
								class="fontnormal"> 
								<fmt:message key="client.ClientStartDate">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"/></fmt:param>
								</fmt:message>:
							<!-- Bug Id 27911. Changed the all the dates in the clientDetails.jsp to display as per client Locale-->
							<c:out
								value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,BusinessKey.customerActivationDate)}" />
							<br>
							</span> <span class="fontnormal"><mifos:mifoslabel
								name="client.FormedBy" bundle="ClientUIResources"></mifos:mifoslabel></span>
							<span class="fontnormal"><c:out
								value="${BusinessKey.customerFormedByPersonnel.displayName}" />
							</span><br>

							<br>
							</td>
						</tr>
						<tr id="Client.TrainedDate">
							<td height="23" colspan="2" class="fontnormalbold"><mifos:mifoslabel
								name="client.TrainingStatus" bundle="ClientUIResources"></mifos:mifoslabel>
							<br>
							<!-- If the training status is set then the date is displayed -->
							<span class="fontnormal"> <mifos:mifoslabel
								name="client.TrainedOn" bundle="ClientUIResources"
								keyhm="Client.TrainedDate"
								isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <!-- Bug Id 27911. Changed the all the dates in the clientDetails.jsp to display as per client Locale-->
							<c:out
								value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,BusinessKey.trainedDate)}" />
							</span> <span class="fontnormal"><br>
							<br>
							</span> <!-- Group Membership details --></td>
						</tr>
						<tr>
							<td height="23" colspan="2" class="fontnormalbold">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td width="61%" class="fontnormalbold">
									<c:choose>
										<c:when test="${BusinessKey.clientUnderGroup}">
											<fmt:message key="client.GroupMembershipDetails">
												<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
											</fmt:message>
										</c:when>
										<c:otherwise>
											<fmt:message key="client.GroupMembershipDetails">
												<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}" /></fmt:param>
											</fmt:message>
										</c:otherwise></c:choose><br>
									<span class="fontnormalRed"> <mifos:mifoslabel
										name="client.MeetingsHeading" bundle="ClientUIResources" isColonRequired="yes"/>&nbsp;
									<c:out value="${customerfn:getMeetingSchedule(BusinessKey.customerMeeting.meeting,UserContext)}" /></span>									
									<c:set var="updatedMeetingScheduleMessage" value="${customerfn:getUpdatedMeetingSchedule(BusinessKey.customerMeeting,UserContext)}" />
									<c:if test="${!empty updatedMeetingScheduleMessage}"><span class="fontnormalRed"><br><c:out value="${updatedMeetingScheduleMessage}" /></span></c:if>
									<span class="fontnormal"><br></span>
									</span> <span class="fontnormal"> <c:if
										test="${BusinessKey.customerMeeting.meeting.meetingPlace!=null && !empty BusinessKey.customerMeeting.meeting.meetingPlace}">
										<c:out
											value="${BusinessKey.customerMeeting.meeting.meetingPlace}" />
										<br>
									</c:if> <c:choose>
										<c:when test="${BusinessKey.clientUnderGroup}">
									<c:out value="${BusinessKey.parentCustomer.displayName}" /><br></c:when>
										<c:otherwise><c:out value="${BusinessKey.office.officeName}" /><br></c:otherwise></c:choose></span></td>
									<td width="39%" align="right" valign="top"><!-- Editing group or branch membership based on whether client belongs to group or not -->
									<c:choose>
										<c:when test="${BusinessKey.clientUnderGroup}">
											<html-el:link styleId="viewClientDetails.link.editRemoveGroupMembership"
												action="clientTransferAction.do?method=loadParents&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
												<fmt:message key="client.EditRemoveMembership">
													<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
												</fmt:message>
											</html-el:link>
											<br>
										</c:when>
										<c:otherwise>
											<%--<c:if test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'configurationLSM')} eq 'No'}">--%>
												<html-el:link styleId="viewClientDetails.link.editBranchOfficeMembership"
													action="clientTransferAction.do?method=loadBranches&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
													<fmt:message key="client.editMembership">
														<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}" /></fmt:param>
													</fmt:message>
												</html-el:link>
											<%--</c:if>--%>
											<br>
											<html-el:link styleId="viewClientDetails.link.editMeetingScheduleAddGroup"
												action="meetingAction.do?method=edit&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}&customerLevel=${BusinessKey.customerLevel.id}">
												<mifos:mifoslabel name="client.EditMeetingLink" bundle="ClientUIResources"/>
												<mifos:mifoslabel name="client.Separator" bundle="ClientUIResources"/>
												<mifos:mifoslabel name="client.AddGroup" bundle="ClientUIResources"/>												
												
											</html-el:link>
											<br>
										</c:otherwise>
									</c:choose></td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					<table width="50%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="pages/framework/images/trans.gif" width="10"
								height="10"></td>
						</tr>
					</table>
					<table width="96%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="50%" height="23" class="headingorange"><mifos:mifoslabel
								name="client.PersonalInformationHeading"
								bundle="ClientUIResources"></mifos:mifoslabel></td>
							<td width="50%" align="right" class="fontnormal"><html-el:link styleId="viewClientDetails.link.editPersonalInformation"
								action="clientCustAction.do?method=editPersonalInfo&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="client.EditPersonalInformationLink"
									bundle="ClientUIResources"></mifos:mifoslabel>
							</html-el:link></td>
						</tr>
						<tr>
							<td class="fontnormal"><span class="fontnormal"> <mifos:mifoslabel
								name="client.DateOfBirth" bundle="ClientUIResources"></mifos:mifoslabel>
							<!-- Bug Id 27911. Changed the all the dates in the clientDetails.jsp to display as per client Locale-->
							<span id="viewClientDetails.text.dateOfBirth"><c:out
								value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,BusinessKey.dateOfBirth)}" /></span>;
							<c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'age')}" /> <mifos:mifoslabel
								name="client.YearsOld" bundle="ClientUIResources"></mifos:mifoslabel><br></td>
						</tr>
						<tr id="Client.GovernmentId">
							<td class="fontnormal"><mifos:mifoslabel
								name="${ConfigurationConstants.GOVERNMENT_ID}"
								keyhm="Client.GovernmentId" isColonRequired="yes"
								isManadatoryIndicationNotRequired="yes" /> <c:out
								value="${BusinessKey.governmentId}" /><br>
							</td>
						</tr>
						<c:if test="${!session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'areFamilyDetailsRequired')}">
						<tr>
							<td colspan="2" class="fontnormal"><c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'maritalStatusEntityName')}" />
							<c:if
								test="${!empty BusinessKey.customerDetail.maritalStatus}">;</c:if>
							<c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'spouseFatherValue')}" />
							<mifos:mifoslabel name="client.Name" bundle="ClientUIResources"></mifos:mifoslabel>
							<span id="viewClientDetails.text.spouseFatherName"><c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'spouseFatherName')}" /></span>
								<c:if
								test="${!empty BusinessKey.customerDetail.numChildren}">;
								<c:out
									value="${BusinessKey.customerDetail.numChildren}" />
								<mifos:mifoslabel name="client.Children"
									bundle="ClientUIResources"></mifos:mifoslabel>
							</c:if><br>
							</td>
						</tr>
						</c:if>
						<tr id="Client.Ethinicity">
							<td class="fontnormal">
							<mifos:mifoslabel name="${ConfigurationConstants.ETHINICITY}" keyhm="Client.Ethinicity" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/>
							<c:if test="${!empty BusinessKey.customerDetail.ethinicity}">
								<c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'ethinicityEntityName')}" />
							</c:if>
							<br>
							</td>
						</tr>
						<tr id="Client.EducationLevel">
							<td class="fontnormal">
							<mifos:mifoslabel name="client.EducationLevel" bundle="ClientUIResources" keyhm="Client.EducationLevel" isManadatoryIndicationNotRequired="yes"/>
							<c:if test="${!empty BusinessKey.customerDetail.educationLevel}">
								<c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'educationLevelEntityName')}" />
							</c:if>
							<br>
							</td>
						</tr>
						<tr id="Client.PovertyStatus">
							<td class="fontnormal">
							<mifos:mifoslabel name="client.PovertyStatus" bundle="ClientUIResources" keyhm="Client.PovertyStatus" isManadatoryIndicationNotRequired="yes"/>
							<c:forEach var="povertyStatus" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'povertyStatus')}">
									<c:if test = "${povertyStatus.id == BusinessKey.customerDetail.povertyStatus}">
										<span id="viewClientDetails.text.povertyStatus"><c:out value="${povertyStatus.name}"/></span>
									</c:if>
							</c:forEach>
							<br>
							</td>
						</tr>
						<tr id="Client.Citizenship">
							<td class="fontnormal">
							<mifos:mifoslabel name="${ConfigurationConstants.CITIZENSHIP}" keyhm="Client.Citizenship" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/>
							<c:if test="${!empty BusinessKey.customerDetail.citizenship}">
								<c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'citizenshipEntityName')}" />
							</c:if>
							<br>
							</td>
						</tr>
						<tr id="Client.Handicapped">
							<td class="fontnormal"><c:if
								test="${!empty BusinessKey.customerDetail.handicapped}">
								<mifos:mifoslabel name="${ConfigurationConstants.HANDICAPPED}" keyhm="Client.Handicapped" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/>
								<span id="viewClientDetails.text.handicapped"><c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'handicappedEntityName')}" /></span>
							</c:if></td>
						</tr>
						<c:if
							test="${not empty BusinessKey.customerAddressDetail.address.phoneNumber ||
					    				  not empty BusinessKey.customerAddressDetail.address.line1 ||
										  not empty BusinessKey.customerAddressDetail.address.line2 ||
										  not empty BusinessKey.customerAddressDetail.address.line3 ||
										  not empty BusinessKey.customerAddressDetail.address.city	 ||
										  not empty BusinessKey.customerAddressDetail.address.state	 ||
										  not empty BusinessKey.customerAddressDetail.address.country	 ||
										  not empty BusinessKey.customerAddressDetail.address.zip }">
							<tr id="Client.Address">
								<td class="fontnormalbold"><br>
								<mifos:mifoslabel name="client.Address"
									bundle="ClientUIResources" keyhm="Client.Address"
									isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <span
									class="fontnormal"><br>
								</span> <c:if
									test="${!empty BusinessKey.customerAddressDetail.address.displayAddress}">
									<span class="fontnormal"> <c:out
										value="${BusinessKey.customerAddressDetail.address.displayAddress}" /> </span>
								</c:if></td>
							</tr>
							<tr id="Client.City">
								<td class="fontnormal"><c:if
									test="${!empty BusinessKey.customerAddressDetail.address.city}">
									<span class="fontnormal"><c:out
										value="${BusinessKey.customerAddressDetail.address.city}" />
									</span>
								</c:if></td>
							</tr>
							<tr id="Client.State">
								<td class="fontnormal"><c:if
									test="${!empty BusinessKey.customerAddressDetail.address.state}">
									<span class="fontnormal"> <c:out
										value="${BusinessKey.customerAddressDetail.address.state}" />
									</span>
								</c:if></td>
							</tr>
							<tr id="Client.Country">
								<td class="fontnormal"><c:if
									test="${!empty BusinessKey.customerAddressDetail.address.country}">
									<span class="fontnormal"><c:out
										value="${BusinessKey.customerAddressDetail.address.country}" />
									</span>
								</c:if></td>
							</tr>
							<tr id="Client.PostalCode">
								<td class="fontnormal"><c:if
									test="${!empty BusinessKey.customerAddressDetail.address.zip}">
									<span class="fontnormal"><c:out
										value="${BusinessKey.customerAddressDetail.address.zip}" />

									</span>
								</c:if></td>
							</tr>
							<tr id="Client.PhoneNumber">
								<td class="fontnormal"><span class="fontnormal"> <c:set
									var="phoneNumber"
									value="${BusinessKey.customerAddressDetail.address.phoneNumber}" />
								<c:if test="${!empty phoneNumber}">
									<br>
									<mifos:mifoslabel name="client.Telephone"
										bundle="ClientUIResources" keyhm="Client.PhoneNumber"
										isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
									<c:out
										value="${BusinessKey.customerAddressDetail.address.phoneNumber}" />
								</c:if></span> <br>
						</c:if>
						<tr>
							<td height="23" colspan="2" class="fontnormalbold"><br>
							<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
								<span class="fontnormalbold"> <mifos:mifoslabel
									name="client.AdditionalInformationHeading"
									bundle="ClientUIResources"></mifos:mifoslabel> </span>
							<span class="fontnormal"> <br>
							<c:forEach var="cf" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
								<c:forEach var="customField" items="${BusinessKey.customFields}">
									<c:if test="${cf.fieldId==customField.fieldId}">
										<c:choose>
											<c:when test="${cf.fieldType == 3}"> <%-- FIXME: use a constant here instead --%>
												<mifos:mifoslabel name="${cf.lookUpEntity.entityType}"
													bundle="CenterUIResources" isColonRequired="yes"></mifos:mifoslabel>
									         		<span class="fontnormal"><c:out
													value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,customField.fieldValue)}" />
												</span>
											</c:when>
											<c:otherwise>
												<mifos:mifoslabel name="${cf.lookUpEntity.entityType}"
													bundle="CenterUIResources" isColonRequired="yes"></mifos:mifoslabel>
									         		<span class="fontnormal"><c:out
													value="${customField.fieldValue}" /> </span>
											</c:otherwise>
										</c:choose>
										<br>
									</c:if>
								</c:forEach>
							</c:forEach> <br>
							</span>
							</c:if>
							<span class="fontnormal">							
							<!-- Bug Id 27210. Added code to pass the created date as parameter-->

							
							</span> </td>
						</tr>
					</table>
					<!-- Family Details -->
					<c:if test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'areFamilyDetailsRequired')}">
						<table width="96%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="50%" height="23" class="headingorange"><mifos:mifoslabel
								name="client.FamilyInformationLabel"
								bundle="ClientUIResources"></mifos:mifoslabel></td>
							<td width="50%" align="right" class="fontnormal"><html-el:link styleId="viewClientDetails.link.editFamilyInformation"
								action="clientCustAction.do?method=editFamilyInfo&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="client.EditFamilyInformationLink"
									bundle="ClientUIResources"></mifos:mifoslabel>
							</html-el:link></td>
						</tr>
						</table>
					<table>
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
											<div id="displayName"> 
												<c:out value="${familyDetails.displayName}"/>
											</div>	   
										</td>		
										<td class="paddingL10">
											<c:out value="${familyDetails.dateOfBirthForBrowser}"/>
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
													<c:if test = "${livingStatusEntity.id ==familyDetails.livingStatus}">
														<c:out value="${livingStatusEntity.name}"/>
													</c:if>
											</c:forEach>
										</td>
									</tr>
								</c:forEach>
  							</c:if>
							<tr><td class="paddingL10"> <br>
							<a id="viewClientDetails.link.historicalDataLink" href="custHistoricalDataAction.do?method=getHistoricalData&globalCustNum=<c:out value="${BusinessKey.globalCustNum}"/>&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}"><mifos:mifoslabel
								name="client.HistoricalDataLink" bundle="ClientUIResources"></mifos:mifoslabel>
							</a> <br>
							<html-el:link styleId="viewClientDetails.link.viewChangeLog" href="clientCustAction.do?method=loadChangeLog&entityType=Client&entityId=${BusinessKey.customerId}&currentFlowKey=${requestScope.currentFlowKey}">
							<mifos:mifoslabel name="client.ChangeLogLink" bundle="ClientUIResources"/>
							</html-el:link> <br>
  							</td></tr>
						
					</table>
					
					</td>
					
					<!-- Performance History -->
					<td width="30%" align="left" valign="top" class="paddingleft1">
					<table width="100%" border="0" cellpadding="2" cellspacing="0"
						class="bluetableborder">
						<tr>
							<td class="bluetablehead05"><span class="fontnormalbold"> <mifos:mifoslabel
								name="client.PerformanceHistoryHeading"
								bundle="ClientUIResources"></mifos:mifoslabel> </span></td>
						</tr>

						<tr>
							<td class="paddingL10"><img
								src="pages/framework/images/trans.gif" width="10" height="2"></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt">
								<fmt:message key="client.CycleNo" > 
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /></fmt:param>
								</fmt:message>
								<c:out value="${BusinessKey.performanceHistory.loanCycleNumber}" /></span></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"> <fmt:message
								key="client.LastLoanAmount"> <fmt:param><mifos:mifoslabel
								name="${ConfigurationConstants.LOAN}"/></fmt:param></fmt:message>:
							<c:out
								value="${BusinessKey.performanceHistory.lastLoanAmount}" /></span></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"><fmt:message
								key="client.NoOfActive"> <fmt:param><mifos:mifoslabel
								name="${ConfigurationConstants.LOAN}"/></fmt:param></fmt:message>:
							<c:out
								value="${BusinessKey.performanceHistory.noOfActiveLoans}" /></span></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"><mifos:mifoslabel
								name="client.DeliquentPortfolio" bundle="ClientUIResources" />
							<c:out
								value="${clientDetailsDto.delinquentPortfolioAmount}" /></span></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"> <fmt:message
								key="client.Total"> <fmt:param><mifos:mifoslabel
								name="${ConfigurationConstants.SAVINGS}"/></fmt:param></fmt:message>
							<c:out
								value="${BusinessKey.performanceHistory.totalSavingsAmount}" /></span></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"> <mifos:mifoslabel
								name="client.MeetingsAttended" bundle="ClientUIResources" /> <c:out
								value="${customerPerformance.meetingsAttended}" />
							</span></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"> <mifos:mifoslabel
								name="client.MeetingsMissed" bundle="ClientUIResources" /> <c:out
								value="${customerPerformanceHistory.meetingsMissed}" />
							</span></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"> 
								<fmt:message key="client.loanCycleCounter">
									<fmt:param><mifos:mifoslabel
										name="${ConfigurationConstants.LOAN}" /></fmt:param>
								</fmt:message>:
							</span></td>
						</tr>
						<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanCycleCounter')}"
							var="loanCycle">
							<tr>
								<td class="paddingL10"><span class="fontnormal8pt">&nbsp;&nbsp;&nbsp;<c:out
									value="${loanCycle.offeringName}" />: <c:out
									value="${loanCycle.counter}" /></span></td>
							</tr>
						</c:forEach>
					</table>
					<table width="95%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="pages/framework/images/trans.gif" width="7"
								height="8"></td>
						</tr>
					</table>
          <c:if test="${surveyCount}">
          <table width="100%" border="0" cellpadding="2" cellspacing="0" class="bluetableborder">
            <tr>
              <td colspan="2" class="bluetablehead05">
                <span class="fontnormalbold">
                  <mifos:mifoslabel name="Surveys.Surveys" bundle="SurveysUIResources"/>
                </span>
              </td>
            </tr>
            <tr>
              <td colspan="2" class="paddingL10"><img src="pages/framework/images/trans.gif" width="10" height="2"></td>
            </tr>
            <c:forEach items="${requestScope.customerSurveys}" var="surveyInstance">
              <tr>
                <td width="70%" class="paddingL10">
                  <span class="fontnormal8pt">
                    <a id="viewClientDetails.link.survey" href="surveyInstanceAction.do?method=get&value(instanceId)=${surveyInstance.instanceId}&value(surveyType)=client">
                      <c:out value="${surveyInstance.survey.name}"/>
                    </a>
                  </span>
                </td>
                <td width="30%" align="left" class="paddingL10">
                  <span class="fontnormal8pt">
                    <c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,surveyInstance.dateConducted)}" />
                  </span>
                </td>
              </tr>
            </c:forEach>
            <tr>
              <td colspan="2" align="right" class="paddingleft05">
                <span class="fontnormal8pt">
                  <a id="viewClientDetails.link.attachSurvey" href="surveyInstanceAction.do?method=choosesurvey&globalNum=${BusinessKey.globalCustNum}&surveyType=client">
                    <mifos:mifoslabel name="Surveys.attachasurvey" bundle="SurveysUIResources"/>
                  </a> <br>
                  <a id="viewClientDetails.link.viewAllSurveys" href="surveysAction.do?method=mainpage">
                    <mifos:mifoslabel name="Surveys.viewallsurveys" bundle="SurveysUIResources"/>
                  </a>
          </table>
					<table width="95%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="pages/framework/images/trans.gif" width="7"
								height="8"></td>
						</tr>
					</table>
		</c:if>
					<table width="100%" border="0" cellpadding="2" cellspacing="0" class="bluetableborder">
						<tr>
							<td class="bluetablehead05"><span class="fontnormalbold"> <mifos:mifoslabel
								name="client.RecentNotes" bundle="ClientUIResources"></mifos:mifoslabel>
							</span></td>
						</tr>
						<tr>
							<td class="paddingL10"><img
								src="pages/framework/images/trans.gif" width="10" height="2"></td>
						</tr>
						<tr>

							<td class="paddingL10">
              <span id="viewClientDetails.text.notes">
                <c:choose>
                  <c:when test="${!empty BusinessKey.recentCustomerNotes}">
                    <c:forEach var="note" items="${BusinessKey.recentCustomerNotes}">
                      <span class="fontnormal8ptbold"> <!-- Bug Id 27911. Changed the all the dates in the clientDetails.jsp to display as per client Locale-->
                        <c:out
                        value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,note.commentDate)}" />:
                      </span>
                      <span class="fontnormal8pt"> 
                        <c:out value="${note.comment}" /> -<em><c:out value="${note.personnelName}" /></em>
                        <br>
                        <br>
                      </span>
                    </c:forEach>
                  </c:when>
                  <c:otherwise>
                    <span class="fontnormal"> <mifos:mifoslabel
                      name="Group.nonotesavailable" bundle="GroupUIResources" /> 
                    </span>
                  </c:otherwise>
							  </c:choose></span>
              </td>
						</tr>
						<tr>
							<td align="right" class="paddingleft05">
                <span class="fontnormal8pt"> 
                  <c:if test="${!empty BusinessKey.customerNotes}">
                    <html-el:link styleId="viewClientDetails.link.seeAllNotes"
                    href="customerNotesAction.do?method=search&customerId=${BusinessKey.customerId}&globalAccountNum=${BusinessKey.globalCustNum}&customerName=${BusinessKey.displayName}&levelId=${BusinessKey.customerLevel.id}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
                      <mifos:mifoslabel name="client.SeeAllNotesLink" bundle="ClientUIResources"/>
                    </html-el:link>
								    <br/>
							    </c:if> 
                  <a id="viewClientDetails.link.notesLink" href="customerNotesAction.do?method=load&customerId=<c:out value="${BusinessKey.customerId}"/>&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
							      <mifos:mifoslabel name="client.NotesLink" bundle="ClientUIResources"/> 
                  </a> 
                </span>
              </td>
						</tr>
					</table>

					</td>
				</tr>
			</table>
			<br>
			<html-el:hidden property="globalAccountNum" value="${BusinessKey.customerAccount.globalAccountNum}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
