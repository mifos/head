<!-- /**

* clientDetails.jsp    version: 1.0



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
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>

<!-- Tils definition for the header and menu -->
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<script language="javascript">

	function ViewDetails(){
		closedaccsearchactionform.submit();
	}
  function GoToEditPage(){
	centerActionForm.action="clientCreationAction.do?method=manage";
	centerActionForm.submit();
  }
  function photopopup(custId , custName){
	
   window.open("clientCreationAction.do?method=showPicture&customerId="+ custId + "&displayName=" + custName,null,"height=250,width=200,status=no,scrollbars=no,toolbar=no,menubar=no,location=no");

  }

</script>

		<html-el:form action="clientCreationAction.do">
			<!-- Hidden properties set for customer id, version, system id and version number -->
			<html-el:hidden property="customerId"
				value="${requestScope.clientVO.customerId}" />
			<html-el:hidden property="globalCustNum"
				value="${requestScope.clientVO.globalCustNum}" />
			<html-el:hidden property="versionNo"
				value="${requestScope.clientVO.versionNo}" />
			<html-el:hidden property="statusId"
				value="${requestScope.clientVO.statusId}" />

			<html-el:hidden property="displayName"
				value="${requestScope.clientVO.displayName}" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <a
						href="CustomerSearchAction.do?method=getOfficeHomePage&officeId=<c:out value="${sessionScope.linkValues.customerOfficeId}"/>&officeName=<c:out value="${sessionScope.linkValues.customerOfficeName}"/>&loanOfficerId=<c:out value="${requestScope.Context.userContext.id}"/>">
					<c:out value="${sessionScope.linkValues.customerOfficeName}" /></a>
					<%-- <a href="CustomerSearchAction.do?method=load">
	           				<c:out value="${sessionScope.linkValues.customerOfficeName}"/>            	
           			</a> --%> / </span> <c:if
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
					</c:if> <!-- Name of the client --> <span class="fontnormal8ptbold">
					<c:out value="${requestScope.clientVO.displayName}" /> </span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<!-- Name of the center -->
				<%-- <c:out value="${requestScope.clientVO.versionNo}"/> customer version --%>
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange"><c:out
								value="${requestScope.clientVO.displayName}" /></td>
							<td rowspan="2" align="right" valign="top" class="headingorange">
							<span class="fontnormal"> <!-- Edit center status link --> <c:if
								test="${requestScope.clientVO.statusId != 6}">
								<html-el:link action="clientCreationAction.do?method=loadStatus">
									<mifos:mifoslabel name="client.EditLink"
										bundle="ClientUIResources"></mifos:mifoslabel>
									<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />
									<mifos:mifoslabel name="client.StatusLink"
										bundle="ClientUIResources"></mifos:mifoslabel>
								</html-el:link>
							</c:if> <br>
							</span></td>
						</tr>
						<tr>
							<td colspan="2"><font class="fontnormalRedBold"><html-el:errors
								bundle="ClientUIResources" /> </font></td>
						</tr>
						<tr>
							<td class="fontnormalbold"><span class="fontnormal"> <c:choose>
								<%-- Partial Application --%>
								<c:when test="${requestScope.clientVO.statusId == 1}">
									<mifos:MifosImage id="partial" moduleName="customer.client" />

								</c:when>
								<%-- Pending Approval --%>
								<c:when test="${requestScope.clientVO.statusId == 2}">
									<mifos:MifosImage id="pending" moduleName="customer.client" />
								</c:when>
								<%-- Active --%>
								<c:when test="${requestScope.clientVO.statusId == 3}">
									<mifos:MifosImage id="active" moduleName="customer.client" />

								</c:when>
								<%-- On Hold --%>
								<c:when test="${requestScope.clientVO.statusId == 4}">
									<mifos:MifosImage id="hold" moduleName="customer.client" />

								</c:when>
								<%-- Cancelled --%>
								<c:when test="${requestScope.clientVO.statusId == 5}">
									<mifos:MifosImage id="cancelled" moduleName="customer.client" />

								</c:when>
								<%-- Closed --%>
								<c:when test="${requestScope.clientVO.statusId == 6}">
									<mifos:MifosImage id="closed" moduleName="customer.client" />
								</c:when>

								<c:otherwise>
								</c:otherwise>
							</c:choose> <c:out value="${requestScope.currentStatus}" /> <c:if
								test="${!empty requestScope.currentFlag}">
        					- <c:out value="${requestScope.currentFlag}" />
							</c:if> </span> <c:if
								test="${requestScope.isBlacklisted == true}">
								<span class="fontnormal"> <mifos:MifosImage id="blackListed"
									moduleName="customer.client" /> <c:out
									value="${requestScope.blacklistedFlagName}" /> </span>
							</c:if> <br>
							<!-- System Id of the center --> <span class="fontnormal"><mifos:mifoslabel
								name="client.SystemId" bundle="ClientUIResources"></mifos:mifoslabel>:</span>
							<span class="fontnormal"><c:out
								value="${requestScope.clientVO.globalCustNum}" /></span><br>
							<!-- Loan Officer of the client --> <span class="fontnormal"> <mifos:mifoslabel
								name="client.LoanOff" bundle="ClientUIResources"></mifos:mifoslabel>
							<c:out value="${requestScope.clientVO.personnel.displayName}" /></span><br>
						</tr>
						<tr id="Client.BusinessActivities">
							<td class="fontnormalbold">
							<span class="fontnormal"> <mifos:mifoslabel
								name="client.BusinessActivities" bundle="ClientUIResources" keyhm="Client.BusinessActivities" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
							<mifoscustom:lookUpValue
								id="${requestScope.clientVO.customerDetail.businessActivities}"
								searchResultName="businessActivitiesEntity"></mifoscustom:lookUpValue>
							</span> <br>
							<%-- clientCreationAction.do?method=get&customerId=<c:out value="${requestScope.clientVO.customerId} --%>
						</tr>
						<tr>
							<td class="fontnormalbold">
							<span class="fontnormal"> <c:set var="custId"
								value="${requestScope.clientVO.customerId}" /> <c:set
								var="custName" value="${requestScope.clientVO.displayName}" />
							<c:if test="${sessionScope.noPictureOnGet eq 'No'}">
								<a href="javascript:photopopup(${custId} , '${custName}')"><mifos:mifoslabel
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
						<c:if test="${requestScope.clientVO.statusId == 3}">
							<tr align="right">
								<td class="headingorange"><span class="fontnormal">Open new
								account:&nbsp; <html-el:link
									href="loanAction.do?method=getPrdOfferings&customer.customerId=${requestScope.clientVO.customerId}">
									<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
								</html-el:link> &nbsp;|&nbsp; <html-el:link
									href="savingsAction.do?method=getPrdOfferings&customerId=${requestScope.clientVO.customerId}&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}">
									<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" />
								</html-el:link> </span></td>
							</tr>
						</c:if>
					</table>
					<c:if test="${!empty requestScope.CustomerActiveLoanAccounts}">
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
									<c:forEach items="${requestScope.CustomerActiveLoanAccounts}"
										var="loan">
										<tr>
											<td>
											<table width="100%" border="0" cellspacing="0"
												cellpadding="0">
												<tr>
													<td width="65%"><span class="fontnormal"> <html-el:link
														href="loanAccountAction.do?globalAccountNum=${loan.globalAccountNum}&method=get&recordOfficeId=${param.recordOfficeId}&recordLoanOfficerId=${param.recordLoanOfficerId}">
														<c:out value="${loan.loanOffering.prdOfferingName}" />,Acct #<c:out
															value="${loan.globalAccountNum}" />
													</html-el:link> </span></td>
													<td width="35%"><span class="fontnormal"> <mifoscustom:MifosImage
														id="${loan.accountState.id}" moduleName="accounts.loan" />
													<c:out value="${loan.accountState.name}" /> </span></td>
												</tr>
											</table>
											<c:if
												test="${loan.accountState.id==5 || loan.accountState.id==9}">
												<span class="fontnormal"> <mifos:mifoslabel
													name="loan.outstandingbalance" />: <c:out
													value="${loan.loanSummary.oustandingBalance.amountDoubleValue}" /><br>
												<mifos:mifoslabel name="loan.amount_due" />: <c:out
													value="${loan.totalAmountDue.amountDoubleValue}" /> </span>
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
						test="${!empty requestScope.CustomerActiveSavingsAccounts}">
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
										items="${requestScope.CustomerActiveSavingsAccounts}"
										var="savings">
										<tr>
											<td>
											<table width="100%" border="0" cellspacing="0"
												cellpadding="0">
												<tr>
													<td width="65%"><span class="fontnormal"> <html-el:link
														href="savingsAction.do?globalAccountNum=${savings.globalAccountNum}&method=get&recordOfficeId=${param.recordOfficeId}&recordLoanOfficerId=${param.recordLoanOfficerId}">
														<c:out value="${savings.savingsOffering.prdOfferingName}" />,Acct #<c:out
															value="${savings.globalAccountNum}" />
													</html-el:link> </span></td>
													<td width="35%"><span class="fontnormal"> <mifoscustom:MifosImage
														id="${savings.accountState.id}"
														moduleName="accounts.savings" /> <c:out
														value="${savings.accountState.name}" /> </span></td>
												</tr>
											</table>
											<span class="fontnormal"><mifos:mifoslabel
												name="Savings.balance" />: <c:out
												value="${savings.savingsBalance.amountDoubleValue}" /> </span></td>
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
								class="tableContentLightBlue"><span class="fontnormalbold"><mifos:mifoslabel
								name="${ConfigurationConstants.CLIENT}" /> <mifos:mifoslabel
								name="client.clientcharges" bundle="ClientUIResources" /></span>
							<table width="95%" border="0" align="center" cellpadding="0"
								cellspacing="0">
								<tr>
									<td>
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="53%"><span class="fontnormal"> <html-el:link
												href="javascript:ViewDetails()">
												<mifos:mifoslabel name="client.viewdetails" bundle="ClientUIResources" />
											</html-el:link> </span></td>
										</tr>
									</table>
									<span class="fontnormal"><mifos:mifoslabel name="client.amtdue" bundle="ClientUIResources"/>:
									<c:out
										value='${requestScope.Context.businessResults["totalFeeDue"]}' />
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
							<td width="69%" align="right" class="fontnormal"><span
								class="fontnormal"> <html-el:link
								href="closedaccsearchaction.do?method=search&searchNode(search_name)=AllClosedAccounts&customerId=${requestScope.clientVO.customerId}&input=ViewClientClosedAccounts">
                  View all closed accounts</html-el:link> </span></td>
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
							<td width="37%" align="right" class="fontnormal"><html-el:link
								action="clientCreationAction.do?method=editMFIInfo">
								<mifos:mifoslabel name="client.EditMfiInformationLink"
									bundle="ClientUIResources"></mifos:mifoslabel>
							</html-el:link></td>
						</tr>
						<tr id="Client.ExternalId">
							<td height="23" colspan="2" class="fontnormalbold"><span
								class="fontnormal"> <mifos:mifoslabel
								name="${ConfigurationConstants.EXTERNALID}" keyhm="Client.ExternalId" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/> <c:out
								value="${requestScope.clientVO.externalId}" /><br>
							</span> 
						</tr>
						<tr>
							<td height="23" colspan="2" class="fontnormalbold"><span class="fontnormal"> <mifos:mifoslabel
								name="${ConfigurationConstants.CLIENT}"></mifos:mifoslabel>
							<mifos:mifoslabel name="client.ClientStartDate"
								bundle="ClientUIResources"></mifos:mifoslabel>: <!-- Bug Id 27911. Changed the all the dates in the clientDetails.jsp to display as per client Locale-->
							<c:out
								value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.clientVO.customerActivationDate)}" />
							<br>
							</span> <span class="fontnormal"><mifos:mifoslabel
								name="client.FormedBy" bundle="ClientUIResources"></mifos:mifoslabel></span>
							<span class="fontnormal"><c:out
								value="${requestScope.clientVO.customerFormedByPersonnel.displayName}" />
							</span><br>

							<br></td></tr>
							<tr id="Client.TrainedDate">
							<td height="23" colspan="2" class="fontnormalbold">
							<mifos:mifoslabel name="client.TrainingStatus"
								bundle="ClientUIResources"></mifos:mifoslabel> <br>
							<!-- If the training status is set then the date is displayed -->
							<span class="fontnormal"> <mifos:mifoslabel
								name="client.TrainedOn" bundle="ClientUIResources" keyhm="Client.TrainedDate" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
							<!-- Bug Id 27911. Changed the all the dates in the clientDetails.jsp to display as per client Locale-->
							<c:out
								value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.clientVO.trainedDate)}" />
							</span> <span class="fontnormal"><br>
							<br>
							</span> <!-- Group Membership details -->
							</td></tr>
							<tr>
							<td height="23" colspan="2" class="fontnormalbold">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td width="61%" class="fontnormalbold"><mifos:mifoslabel
										name="${ConfigurationConstants.GROUP}" /> <mifos:mifoslabel
										name="client.GroupMembershipDetails"
										bundle="ClientUIResources"></mifos:mifoslabel><br>
									<span class="fontnormalRed"> <mifos:mifoslabel
										name="client.MeetingsHeading" bundle="ClientUIResources" />:&nbsp;
									<c:out
										value="${requestScope.clientVO.customerMeeting.meeting.meetingSchedule}" />
									<br>
									</span> <span class="fontnormal"> <c:if
										test="${requestScope.clientVO.customerMeeting.meeting.meetingPlace!=null && !empty requestScope.clientVO.customerMeeting.meeting.meetingPlace}">
										<c:out
											value="${requestScope.clientVO.customerMeeting.meeting.meetingPlace}" />
										<br>
									</c:if> </span></td>
									<td width="39%" align="right" valign="top"><!-- Editing group or branch membership based on whether client belongs to group or not -->
									<c:choose>
										<c:when test="${requestScope.clientVO.groupFlag == 1}">
											<html-el:link
												action="clientCreationAction.do?method=loadTransfer">
												<mifos:mifoslabel name="client.EditLink"
													bundle="ClientUIResources" />
												<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />
												<mifos:mifoslabel name="client.MembershipLink"
													bundle="ClientUIResources" />
											</html-el:link>
											<br>
										</c:when>
										<c:otherwise>
											<c:if test="${sessionScope.configurationLSM eq 'No'}">
												<html-el:link
													action="clientCreationAction.do?method=loadBranchTransfer">
													<mifos:mifoslabel name="client.EditLink"
														bundle="ClientUIResources" />
													<mifos:mifoslabel
														name="${ConfigurationConstants.BRANCHOFFICE}" />
													<mifos:mifoslabel name="client.MembershipLink"
														bundle="ClientUIResources" />
												</html-el:link>
											</c:if>
											<br>

											<c:choose>
												<c:when
													test="${ empty requestScope.clientVO.customerMeeting.meeting}">
													<html-el:link
														action="MeetingAction.do?method=loadMeeting&input=ClientDetails&customerId=${requestScope.clientVO.customerId}">
														<mifos:mifoslabel name="client.EditMeetingLink"
															bundle="ClientUIResources"></mifos:mifoslabel>
													</html-el:link>
												</c:when>
												<c:otherwise>
													<html-el:link
														action="MeetingAction.do?method=get&input=ClientDetails&meetingId=${requestScope.clientVO.customerMeeting.meeting.meetingId}">
														<mifos:mifoslabel name="client.EditMeetingLink"
															bundle="ClientUIResources"></mifos:mifoslabel>
													</html-el:link>
												</c:otherwise>
											</c:choose>
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
							<td width="50%" align="right" class="fontnormal"><html-el:link
								action="clientCreationAction.do?method=editPersonalInfo">
								<mifos:mifoslabel name="client.EditPersonalInformationLink"
									bundle="ClientUIResources"></mifos:mifoslabel>
							</html-el:link></td>
						</tr>
						<tr>
							<td class="fontnormal"><span
								class="fontnormal"> <mifos:mifoslabel name="client.DateOfBirth"
								bundle="ClientUIResources"></mifos:mifoslabel> <!-- Bug Id 27911. Changed the all the dates in the clientDetails.jsp to display as per client Locale-->
							<c:out
								value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.clientVO.dateOfBirth)}" />;
							<c:out value="${sessionScope.age}" /> <mifos:mifoslabel
								name="client.YearsOld" bundle="ClientUIResources"></mifos:mifoslabel><br></td>
						</tr>
						<tr id="Client.GovernmentId">
							<td class="fontnormal">
							<mifos:mifoslabel name="${ConfigurationConstants.GOVERNMENT_ID}" keyhm="Client.GovernmentId" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/>
							<c:out value="${requestScope.clientVO.governmentId}" /><br>
							</td>
						</tr>
						<tr>
							<td colspan="2" class="fontnormal">

							<mifoscustom:lookUpValue
								id="${requestScope.clientVO.customerDetail.maritalStatus}"
								searchResultName="maritalStatusEntity"></mifoscustom:lookUpValue><c:if
								test="${!empty requestScope.clientVO.customerDetail.maritalStatus}">;</c:if>
							<mifoscustom:lookUpValue
								id="${requestScope.Context.businessResults.spouseFatherValue}"
								searchResultName="spouseEntity" mapToSeperateMasterTable="true"></mifoscustom:lookUpValue>
							<mifos:mifoslabel name="client.Name" bundle="ClientUIResources"></mifos:mifoslabel>
							<c:out
								value="${requestScope.Context.businessResults.spouseFatherName}" /><c:if
								test="${!empty requestScope.clientVO.customerDetail.numChildren}">;
								<c:out
									value="${requestScope.clientVO.customerDetail.numChildren}" />
								<mifos:mifoslabel name="client.Children"
									bundle="ClientUIResources"></mifos:mifoslabel>
							</c:if><br>
							</td>
						</tr>
						<tr id="Client.Ethinicity">
							<td class="fontnormal">
							<c:if
								test="${!empty requestScope.clientVO.customerDetail.ethinicity}">
								<mifoscustom:lookUpValue
									id="${requestScope.clientVO.customerDetail.ethinicity}"
									searchResultName="ethinicityEntity"></mifoscustom:lookUpValue>
								<br>
							</c:if> 
							</td>
						</tr>
						<tr id="Client.EducationLevel">
							<td class="fontnormal"><c:if
								test="${!empty requestScope.clientVO.customerDetail.educationLevel}">
								<mifoscustom:lookUpValue
									id="${requestScope.clientVO.customerDetail.educationLevel}"
									searchResultName="educationLevelEntity"></mifoscustom:lookUpValue>
								<br>
							</c:if> 
							</td>
						</tr>
						<tr id="Client.Citizenship">
							<td class="fontnormal"><c:if
								test="${!empty requestScope.clientVO.customerDetail.citizenship}">
								<mifos:mifoslabel name="${ConfigurationConstants.CITIZENSHIP}" keyhm="Client.Citizenship" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/>
								<mifoscustom:lookUpValue
									id="${requestScope.clientVO.customerDetail.citizenship}"
									searchResultName="citizenshipEntity"></mifoscustom:lookUpValue>
								<br>
							</c:if> 
							</td>
						</tr>
						<tr id="Client.Handicapped">
							<td class="fontnormal"><c:if
								test="${!empty requestScope.clientVO.customerDetail.handicapped}">
								<mifos:mifoslabel name="${ConfigurationConstants.HANDICAPPED}" keyhm="Client.Handicapped" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/>
								<mifoscustom:lookUpValue
									id="${requestScope.clientVO.customerDetail.handicapped}"
									searchResultName="handicappedEntity"></mifoscustom:lookUpValue>
							</c:if>
							</td>
						</tr>
						<c:if
								test="${not empty requestScope.clientVO.customerAddressDetail.phoneNumber ||
					    				  not empty requestScope.clientVO.customerAddressDetail.line1 ||
										  not empty requestScope.clientVO.customerAddressDetail.line2 ||
										  not empty requestScope.clientVO.customerAddressDetail.line3 ||
										  not empty requestScope.clientVO.customerAddressDetail.city	 ||
										  not empty requestScope.clientVO.customerAddressDetail.state	 ||
										  not empty requestScope.clientVO.customerAddressDetail.country	 ||
										  not empty requestScope.clientVO.customerAddressDetail.zip }">
						<tr id="Client.Address">
							<td class="fontnormalbold"><br>
								<mifos:mifoslabel name="client.Address"
									bundle="ClientUIResources" keyhm="Client.Address" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
								<span class="fontnormal"><br>
								</span>
								<c:if test="${!empty requestScope.clientVO.displayAddress}">
									<span class="fontnormal"> <c:out
										value="${requestScope.clientVO.displayAddress}" />
									</span>
								</c:if>
						</td>
						</tr>
						<tr id="Client.City">
							<td class="fontnormal">
								<c:if
									test="${!empty requestScope.clientVO.customerAddressDetail.city}">
									<span class="fontnormal"><c:out
										value="${requestScope.clientVO.customerAddressDetail.city}" />
									</span>
								</c:if>
						</td>
						</tr>
						<tr id="Client.State">
							<td class="fontnormal">
								<c:if
									test="${!empty requestScope.clientVO.customerAddressDetail.state}">
									<span class="fontnormal"> <c:out
										value="${requestScope.clientVO.customerAddressDetail.state}" />
									</span>
								</c:if>
						</td>
						</tr>
						<tr id="Client.Country">
							<td class="fontnormal">
								<c:if
									test="${!empty requestScope.clientVO.customerAddressDetail.country}">
									<span class="fontnormal"><c:out
										value="${requestScope.clientVO.customerAddressDetail.country}" />
									</span>
								</c:if>
						</td>
						</tr>
						<tr id="Client.PostalCode">
							<td class="fontnormal">
								<c:if
									test="${!empty requestScope.clientVO.customerAddressDetail.zip}">
									<span class="fontnormal"><c:out
										value="${requestScope.clientVO.customerAddressDetail.zip}" />
									
									</span>
								</c:if>
						</td>
						</tr>
						<tr id="Client.PhoneNumber">
							<td class="fontnormal">
								<span class="fontnormal"> <c:set var="phoneNumber"
									value="${requestScope.clientVO.customerAddressDetail.phoneNumber}" />
								<c:if test="${!empty phoneNumber}">
									<br>
									<mifos:mifoslabel name="client.Telephone"
										bundle="ClientUIResources" keyhm="Client.PhoneNumber" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
									<c:out
										value="${requestScope.clientVO.customerAddressDetail.phoneNumber}" />
								</c:if></span>
								<br>
							</c:if>
					</td>
						</tr>
						<tr>
							<td height="23" colspan="2" class="fontnormalbold"><br>
							<c:if test="${!empty requestScope.customFields}">
								<span class="fontnormalbold"> <mifos:mifoslabel
									name="client.AdditionalInformationHeading"
									bundle="ClientUIResources"></mifos:mifoslabel> </span>
							</c:if> <span class="fontnormal"> <span class="fontnormal"><br>
							</span> <c:forEach var="cf" items="${requestScope.customFields}">
								<c:forEach var="customField"
									items="${requestScope.clientVO.customFieldSet}">
									<c:if test="${cf.fieldId==customField.fieldId}">
										<c:choose>
											<c:when test="${cf.fieldType == 3}">
												<mifos:mifoslabel name="${cf.lookUpEntity.entityType}"
													bundle="CenterUIResources"></mifos:mifoslabel>: 
									         		<span class="fontnormal"><c:out
													value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,customField.fieldValue)}" />
												</span>
											</c:when>
											<c:otherwise>
												<mifos:mifoslabel name="${cf.lookUpEntity.entityType}"
													bundle="CenterUIResources"></mifos:mifoslabel>: 
									         		<span class="fontnormal"><c:out
													value="${customField.fieldValue}" /> </span>
											</c:otherwise>
										</c:choose>
										<br>
									</c:if>
								</c:forEach>
							</c:forEach> <br>
							<!-- Bug Id 27210. Added code to pass the created date as parameter-->

							<a href="CustomerHistoricalDataAction.do?method=get&input=Client"><mifos:mifoslabel
								name="client.HistoricalDataLink" bundle="ClientUIResources"></mifos:mifoslabel>
							</a> <br>
							<html-el:link
								href="closedaccsearchaction.do?method=search&searchNode(search_name)=ChangeLogDetails&input=ViewClientLog&customerId=${requestScope.clientVO.customerId}&entityTypeId=1&createdDate=${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.clientVO.createdDate)}">
								<mifos:mifoslabel name="client.ChangeLogLink"
									bundle="ClientUIResources"></mifos:mifoslabel>
							</html-el:link><br>
							</span> </span></td>
						</tr>
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
							<td class="paddingL10"><span class="fontnormal8pt"><mifos:mifoslabel
								name="${ConfigurationConstants.LOAN}" /><mifos:mifoslabel
								name="client.CycleNo" bundle="ClientUIResources"/> <c:out
								value="${sessionScope.ClientPerformanceHistory.loanCycleNumber}" /></span></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"> <mifos:mifoslabel
								name="client.LastLoanAmount" bundle="ClientUIResources" /> <mifos:mifoslabel
								name="${ConfigurationConstants.LOAN}" isColonRequired="yes"/> <c:out
								value="${sessionScope.ClientPerformanceHistory.lastLoanAmount}" /></span></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"><mifos:mifoslabel
								name="client.NoOfActive" bundle="ClientUIResources"/> <mifos:mifoslabel
								name="${ConfigurationConstants.LOAN}" isColonRequired="yes" /> <c:out
								value="${sessionScope.ClientPerformanceHistory.noOfActiveLoans}" /></span></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"><mifos:mifoslabel
								name="client.DeliquentPortfolio" bundle="ClientUIResources"/> <c:out
								value="${sessionScope.ClientPerformanceHistory.delinquentPortfolioAmount}" /></span></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"> <mifos:mifoslabel
								name="client.Total" bundle="ClientUIResources" /> <mifos:mifoslabel
								name="${ConfigurationConstants.SAVINGS}" isColonRequired="yes"/> <c:out
								value="${sessionScope.ClientPerformanceHistory.totalSavingsAmount}" /></span></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"> <mifos:mifoslabel
								name="client.MeetingsAttended" bundle="ClientUIResources" /> <c:out
								value='${requestScope.Context.businessResults["customerPerformance"].meetingsAttended}' />
							</span></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"> <mifos:mifoslabel
								name="client.MeetingsMissed" bundle="ClientUIResources" /> <c:out
								value='${requestScope.Context.businessResults["customerPerformanceHistory"].meetingsMissed}' />
							</span></td>
						</tr>


						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"> <mifos:mifoslabel
								name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel
								name="label.loancyclecounter" bundle="CustomerUIResources"></mifos:mifoslabel>
							-</span></td>
						</tr>
						<c:forEach
							items='${sessionScope.ClientCreationAction_Context.businessResults["loanCycleCounter"]}'
							var="loanCycleCounter">
							<tr>
								<td class="paddingL10"><span class="fontnormal8pt">&nbsp;&nbsp;&nbsp;<c:out
									value="${loanCycleCounter.offeringName}" />: <c:out
									value="${loanCycleCounter.counter}" /></span></td>
							</tr>
						</c:forEach>
					</table>
					<table width="95%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="pages/framework/images/trans.gif" width="7"
								height="8"></td>
						</tr>
					</table>
					<table width="100%" border="0" cellpadding="2" cellspacing="0"
						class="bluetableborder">
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
							<td class="paddingL10"><c:choose>
								<c:when test="${!empty requestScope.notes}">
									<c:forEach var="note" items="${requestScope.notes}">
										<span class="fontnormal8ptbold"> <!-- Bug Id 27911. Changed the all the dates in the clientDetails.jsp to display as per client Locale-->
										<c:out
											value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,note.commentDate)}" />:
										</span>
										<span class="fontnormal8pt"> <c:out value="${note.comment}" />
										-<em><c:out value="${note.personnelName}" /></em><br>
										<br>
										</span>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<span class="fontnormal"> <mifos:mifoslabel
										name="Group.nonotesavailable" bundle="GroupUIResources" /> </span>
								</c:otherwise>
							</c:choose></td>
						</tr>
						<tr>
							<td align="right" class="paddingleft05"><span
								class="fontnormal8pt"> <c:if test="${!empty requestScope.notes}">
								<a href="CustomerNoteAction.do?method=get&input=Client"> <mifos:mifoslabel
									name="client.SeeAllNotesLink" bundle="ClientUIResources"></mifos:mifoslabel>
								</a>
								<br>
							</c:if> <a href="CustomerNoteAction.do?method=load&input=Client">
							<mifos:mifoslabel name="client.NotesLink"
								bundle="ClientUIResources"></mifos:mifoslabel> </a> </span></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			<br>
			<mifos:SecurityParam property="Client" />
		</html-el:form>
		<html-el:hidden property="globalAccountNum"
			value="${requestScope.GroupVO.customerAccount.globalAccountNum}" />
		<html-el:form action="closedaccsearchaction.do?method=search">
			<html-el:hidden property="searchNode(search_name)"
				value="ClientChargesDetails" />
			<html-el:hidden property="prdOfferingName"
				value="${requestScope.clientVO.displayName}" />
			<html-el:hidden property="globalAccountNum"
				value="${requestScope.clientVO.customerAccount.globalAccountNum}" />
			<html-el:hidden property="accountId"
				value="${requestScope.clientVO.customerAccount.accountId}" />
			<html-el:hidden property="accountType"
				value="${requestScope.clientVO.customerAccount.accountTypeId}" />
			<html-el:hidden property="input" value="ViewClientCharges" />
			<html-el:hidden property="customerId" value="${requestScope.clientVO.customerId}"/>
			<html-el:hidden property="statusId" value="${requestScope.clientVO.statusId}"/> 
		</html-el:form>
	</tiles:put>
</tiles:insert>
