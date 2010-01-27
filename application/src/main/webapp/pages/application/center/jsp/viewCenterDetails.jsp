<%--
Copyright (c) 2005-2008 Grameen Foundation USA
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>

<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="/customer/customerfunctions" prefix="customerfn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.CenterUIResources"/>

<%-- Struts Tiles definition for the header and menu --%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	    <span id="page.id" title="CenterDetails" />
		<script language="javascript">
  function GoToEditPage(){
	centerActionForm.action="centerAction.do?method=manage";
	centerActionForm.submit();
  }

</script>

		<html-el:form action="centerCustAction.do">
  		   <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
				   var="BusinessKey" />
  		   <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'centerDetailsDto')}"
				   var="centerDetailsDto" />
			<html-el:hidden property="input" value="CenterDetails}" />
			<!-- Hidden properties set for customer id, version, system id and version number -->
			<html-el:hidden property="customerId"
				value="${BusinessKey.customerId}" />
			<html-el:hidden property="globalCustNum"
				value="${BusinessKey.globalCustNum}" />
			<html-el:hidden property="versionNo"
				value="${BusinessKey.versionNo}" />
			<html-el:hidden property="statusId"
				value="${BusinessKey.customerStatus.id}" />
			<html-el:hidden property="office.officeId"
				value="${BusinessKey.office.officeId}" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
				<td class="bluetablehead05">
	            <span class="fontnormal8pt">
	            	<customtags:headerLink selfLink="false"/>
	            </span>

				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<!-- Name of the center -->
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange"><span id="viewCenterDetails.text.displayName"><c:out
								value="${centerDetailsDto.displayName}" /></span></td>
							<td rowspan="2" align="right" valign="top" class="headingorange">
							<span class="fontnormal"> <!-- Edit center status link --> <a id="viewCenterDetails.link.edit"
								href="editCustomerStatusAction.do?method=loadStatus&customerId=<c:out value="${BusinessKey.customerId}"/>&input=center&currentFlowKey=${requestScope.currentFlowKey}">
							<mifos:mifoslabel name="Center.Edit" /> <mifos:mifoslabel
								name="${ConfigurationConstants.CENTER}" /> <mifos:mifoslabel
								name="Center.Status1" /> </a><br>
							</span></td>
						</tr>
						<tr>
							<td colspan="2"><font class="fontnormalRedBold"><span id="viewCenterDetails.error.message"><html-el:errors
								bundle="CenterUIResources" /></span></font></td>
						</tr>
						<tr>
							<td class="fontnormalbold"><c:set var="statusID" scope="request"
								value="${BusinessKey.customerStatus.id}" /><!-- Status of the center. Depending on the status id active or inactive is displayed -->


							<c:if test="${statusID == CustomerConstants.CENTER_ACTIVE_STATE}">
								<span class="fontnormal"><img
									src="pages/framework/images/status_activegreen.gif" width="8"
									height="9">
							</c:if> <c:if test="${statusID == CustomerConstants.CENTER_INACTIVE_STATE}">
								<span class="fontnormal"><img
									src="pages/framework/images/status_closedblack.gif" width="8"
									height="9">
								</span>
							</c:if> <span id="viewCenterDetails.text.status"><c:out value="${BusinessKey.customerStatus.name}" /></span><br>
							<!-- System Id of the center --> <span class="fontnormal"><mifos:mifoslabel
								name="Center.SystemId" />:</span> <span class="fontnormal"><c:out
								value="${BusinessKey.globalCustNum}" /><br>
							<!-- Loan Officer of the center --> <mifos:mifoslabel
								name="Center.LoanOff" ></mifos:mifoslabel>
							<span id="viewCenterDetails.text.loanOfficer"><c:out value="${BusinessKey.personnel.displayName}" /></span></span><br>
							<br>
							<!-- List of groups under the center -->
							<span class="fontnormalBold"> <mifos:mifoslabel
								name="${ConfigurationConstants.GROUP}" /><mifos:mifoslabel
								name="Center.s"  /> <mifos:mifoslabel
								name="Center.Assigned"  />
								</span>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<c:if test="${statusID != CustomerConstants.CENTER_INACTIVE_STATE}">
								<span class="fontnormal"> <a id="viewCenterDetails.link.add"
									href="groupCustAction.do?method=load&centerSystemId=<c:out value="${BusinessKey.globalCustNum}"/>&parentOfficeId=${BusinessKey.office.officeId}&recordOfficeId=${BusinessKey.office.officeId}&recordLoanOfficerId=${BusinessKey.personnel.personnelId}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="Center.Add"  />
								<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></a>
								</span>
							</c:if> <br>
							<span class="fontnormal"> <mifos:mifoslabel
								name="Center.GroupsLink1" />
							<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /> <mifos:mifoslabel
								name="Center.GroupsLink2" />
							<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /> <mifos:mifoslabel
								name="Center.GroupsLink3" />
							</span><br>
							<div id="Layer2"
								style="border: 1px solid #CECECE; height:100px; width:250px; overflow: auto; padding:6px; margin-top:5px;">
							<span class="fontnormal"> <c:choose>
								<c:when test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'groups')}">
									<c:forEach var="group" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'groups')}">
										<span class="fontnormal"> <a id="viewCenterDetails.link.group"
											href="groupCustAction.do?method=get&globalCustNum=<c:out value="${group.globalCustNum}"/>&recordOfficeId=${BusinessKey.office.officeId}&recordLoanOfficerId=${BusinessKey.personnel.personnelId}">
										<c:out value="${group.displayName}" /></a><br>
										</span>
									</c:forEach>
								</c:when>
								<c:otherwise>
                                                                <%-- FIXME: JSP tags inside HTML comments?? --%>
									<!-- <c:if test="${centerDetailsDto.numberOfGroups==0}">-->
									<mifos:mifoslabel name="Center.No" bundle="CenterUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /><mifos:mifoslabel name="Center.s" bundle="CenterUIResources" />
									<mifos:mifoslabel name="Center.Available"
										bundle="CenterUIResources" />
									<!-- </c:if> -->
								</c:otherwise>
							</c:choose></div>
							<br>
							</td>
						</tr>
					</table>

					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<!-- Account table -->
						<tr>
							<td width="36%" class="headingorange"><mifos:mifoslabel
								name="Center.AccountHeading" /></td>
						</tr>
						<tr align="right">
							<td class="headingorange"><span class="fontnormal"> <c:if
								test="${statusID == CustomerConstants.CENTER_ACTIVE_STATE}">
								<mifos:mifoslabel name="Center.AccountsLink"
									/>&nbsp;
		               <html-el:link styleId="viewCenterDetails.link.newSavingsAccount"
									href="savingsAction.do?method=getPrdOfferings&customerId=${BusinessKey.customerId}&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}">
									<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" />
								</html-el:link></span> </c:if></td>
						</tr>
					</table>


					<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customerSavingsAccountsInUse')}">
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
													<td width="65%"><span class="fontnormal"> <html-el:link styleId="viewCenterDetails.link.savingsAccount"
														href="savingsAction.do?globalAccountNum=${savings.globalAccountNum}&method=get&recordOfficeId=${param.recordOfficeId}&recordLoanOfficerId=${param.recordLoanOfficerId}">
														<c:out value="${savings.savingsOffering.prdOfferingName}" />, <mifos:mifoslabel name="Center.acc" bundle="CenterUIResources" /><c:out
															value="${savings.globalAccountNum}" />
													</html-el:link> </span></td>
													<td width="35%"><span class="fontnormal"> <mifoscustom:MifosImage
														id="${savings.accountState.id}"
														moduleName="accounts.savings" /> <c:out
														value="${savings.accountState.name}" /> </span></td>
												</tr>
											</table>
											<span class="fontnormal"><mifos:mifoslabel name="Center.balance" bundle="CenterUIResources"/> <c:out
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
							<td width="65%" align="left" valign="top"
								class="tableContentLightBlue"><span class="fontnormalbold"> <mifos:mifoslabel
								name="${ConfigurationConstants.CENTER}" /> <mifos:mifoslabel
								name="Center.Charges" bundle="CenterUIResources" /> </span>
							<table width="95%" border="0" align="center" cellpadding="0"
								cellspacing="0">
								<tr>
									<td>
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="53%"><span class="fontnormal"> <%-- <c:forEach var="cp" items="${BusinessKey.customerAccounts}">
									<c:if test="${cp.accountId==3}">
										<c:set var="accID" scope="request" value="${cp.accountId}"/>

									</c:if>
								</c:forEach> --%> <a id="viewCenterDetails.link.viewDetails"
										href="customerAccountAction.do?method=load&globalCustNum=<c:out value="${BusinessKey.globalCustNum}"/>">
										<mifos:mifoslabel name="Center.Viewdetails" bundle="CenterUIResources" />
									</a> </span></td>
										</tr>
									</table>
									<span class="fontnormal"><mifos:mifoslabel name="Center.AmountDueColon" bundle="CenterUIResources" /> <c:out
										value='${BusinessKey.customerAccount.nextDueAmount}' />
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
					<table width="50%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="pages/framework/images/trans.gif" width="10"
								height="5"></td>
						</tr>
					</table>

					<table width="96%" border="0" cellpadding="3" cellspacing="0">

						<tr>
							<td width="38%" align="right" class="fontnormal"><span
								class="fontnormal"><a id="viewCenterDetails.link.viewAllClosedAccounts"
								href="custAction.do?method=getClosedAccounts&customerId=<c:out value="${BusinessKey.customerId}"/>&input=center&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="Group.viewallclosedaccounts"
									bundle="GroupUIResources"></mifos:mifoslabel>
							</a> </span></td>
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
								name="${ConfigurationConstants.CENTER}" /><c:out value=" " /><mifos:mifoslabel
								name="Center.Information" bundle="CenterUIResources" /></td>
							<td width="50%" align="right" class="fontnormal"><html-el:link styleId="viewCenterDetails.link.editCenterDetails"
								action="centerCustAction.do?method=manage&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="Center.Edit" bundle="CenterUIResources" />
								<c:out value=" " />
								<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
								<c:out value=" " />
								<mifos:mifoslabel name="Center.details"
									bundle="CenterUIResources" />
							</html-el:link></td>
						</tr>

						<tr>
							<td height="23" colspan="2" class="fontnormalbold"><span
								class="fontnormal"> <!-- MFI Joining date --> <mifos:mifoslabel
								name="Center.MfiJoiningDate" />:
							<c:out
								value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,BusinessKey.mfiJoiningDate)}" />
							<br>
							<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /> <mifos:mifoslabel
								name="Center.CenterStartDate" />:
							<c:out
								value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,BusinessKey.createdDate)}" /></td>
						</tr>

						<!-- External Id -->
						<tr id="Center.ExternalId">
							<td height="23" colspan="2" class="fontnormalbold"><span
								class="fontnormal"> <mifos:mifoslabel
								name="${ConfigurationConstants.EXTERNALID}"
								bundle="CenterUIResources" keyhm="Center.ExternalId"
								isManadatoryIndicationNotRequired="yes" isColonRequired="yes"></mifos:mifoslabel>
							<c:out value="${BusinessKey.externalId}" /><br>
							</span></td>
						</tr>

						<!-- Client positions -->
						<tr>
							<td height="23" colspan="2" class="fontnormalbold"><br>
							<mifos:mifoslabel name="Center.OfficialTitlesHeading"
								/><br>
							<c:forEach var="pos"
								items="${BusinessKey.customerPositions}">
								<c:if test="${! empty pos.customer.customerId}">
								<span class="fontnormal">
									<c:out value="${pos.position.name}" />: <c:out
										value="${pos.customer.displayName}" />
										</span>
									<br>
								</c:if>
							</c:forEach>  <br>
							</td>
						</tr>

						<tr>
							<td height="23" colspan="2" class="fontnormalbold"><span
								class="fontnormal">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="fontnormalbold"><mifos:mifoslabel
										name="Center.MeetingsHeading" />
									</td>
									<td align="right">
									<html-el:link styleId="viewCenterDetails.link.meetings"
										action="meetingAction.do?method=edit&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}&customerLevel=${BusinessKey.customerLevel.id}">
										<mifos:mifoslabel name="Center.MeetingsLink"
											/>
									</html-el:link></td>
								</tr>
							</table>
							<span class="fontnormalRed"> <mifos:mifoslabel
								name="Center.MeetingsSubHeading" />:&nbsp;
							<c:out value="${customerfn:getMeetingSchedule(BusinessKey.customerMeeting.meeting,UserContext)}" /></span>
							<c:set var="updatedMeetingScheduleMessage" value="${customerfn:getUpdatedMeetingSchedule(BusinessKey.customerMeeting,UserContext)}" />
							<c:if test="${!empty updatedMeetingScheduleMessage}"><span class="fontnormalRed"><br><c:out value="${updatedMeetingScheduleMessage}" /></span></c:if>
							<span class="fontnormal"><br></span>
							<span class="fontnormal"><c:out
								value="${BusinessKey.customerMeeting.meeting.meetingPlace}" /></span>
							</span><span class="fontnormal"><br>
							</span><br>
							<span class="fontnormal"> </span><span class="fontnormal"> </span>
							<!-- Address Fields --></td>
						</tr>




						<tr id="Center.Address">
							<td height="23" colspan="2" class="fontnormalbold"><c:if
								test="${not empty BusinessKey.customerAddressDetail.address.phoneNumber ||
					    not empty BusinessKey.customerAddressDetail.address.line1 ||
						not empty BusinessKey.customerAddressDetail.address.line2 ||
						not empty BusinessKey.customerAddressDetail.address.line3 ||
						not empty BusinessKey.customerAddressDetail.address.city	 ||
						not empty BusinessKey.customerAddressDetail.address.state	 ||
						not empty BusinessKey.customerAddressDetail.address.country	 ||
						not empty BusinessKey.customerAddressDetail.address.zip }">
								<mifos:mifoslabel name="Center.Address"
									bundle="CenterUIResources" keyhm="Center.Address"
									isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
								<span class="fontnormal"><br>
								</span>
								<c:if test="${!empty BusinessKey.customerAddressDetail.address.displayAddress}">
									<span class="fontnormal"> <c:out
										value="${BusinessKey.customerAddressDetail.address.displayAddress}" /><br>
									</span>
								</c:if>
								<c:if
									test="${!empty BusinessKey.customerAddressDetail.address.city}">
									<span class="fontnormal"> <c:out
										value="${BusinessKey.customerAddressDetail.address.city}" /><br>
									</span>
								</c:if>
								<c:if
									test="${!empty BusinessKey.customerAddressDetail.address.state}">
									<span class="fontnormal"> <c:out
										value="${BusinessKey.customerAddressDetail.address.state}" /><br>
									</span>
								</c:if>
								<c:if
									test="${!empty BusinessKey.customerAddressDetail.address.country}">
									<span class="fontnormal"><c:out
										value="${BusinessKey.customerAddressDetail.address.country}" /><br>
									</span>
								</c:if>
								<c:if
									test="${!empty BusinessKey.customerAddressDetail.address.zip}">
									<span class="fontnormal"><c:out
										value="${BusinessKey.customerAddressDetail.address.zip}" />
									<br>
									</span>
								</c:if>
								<span class="fontnormal"> <span class="fontnormal"></span></td>
						</tr>

						<tr id="Center.PhoneNumber">
							<td height="23" colspan="2" class="fontnormalbold"><span
								class="fontnormal"> <!-- Telephone Number --> <span
								class="fontnormal"> <c:set var="phoneNumber"
								value="${BusinessKey.customerAddressDetail.address.phoneNumber}" />
							<c:if test="${!empty phoneNumber}">
								<br>
								<mifos:mifoslabel name="Center.Telephone"
									bundle="CenterUIResources" keyhm="Center.PhoneNumber"
									isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
								<c:out
									value="${BusinessKey.customerAddressDetail.address.phoneNumber}" />
								<br></span> </c:if> <br></td>
						</tr>

						<tr>
							<td height="23" colspan="2" class="fontnormalbold"><span
								class="fontnormal"> <!-- Additional Information --> </c:if> 
							<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
								<span class="fontnormalbold"> <mifos:mifoslabel
									name="Center.AdditionalInformationHeading"
									/> <br>
								</span>
							 <span class="fontnormal"> <c:forEach var="cf"
								items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
								<c:forEach var="customField"
									items="${BusinessKey.customFields}">
									<c:if test="${cf.fieldId==customField.fieldId}">
										<c:choose>
											<c:when test="${cf.fieldType == 3}">
												<mifos:mifoslabel name="${cf.lookUpEntity.entityType}"
													/>:
					         		<span class="fontnormal"><c:out
													value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,customField.fieldValue)}" /><br>
												</span>
											</c:when>
											<c:otherwise>
												<mifos:mifoslabel name="${cf.lookUpEntity.entityType}"
													/>:
					         		<span class="fontnormal"><c:out
													value="${customField.fieldValue}" /><br>
												</span>
											</c:otherwise>
										</c:choose>
									</c:if>
								</c:forEach>
							</c:forEach> <br>
							<br>
							</span>
							</c:if>
							<html-el:link styleId="viewCenterDetails.link.viewChangeLog" href="centerCustAction.do?method=loadChangeLog&entityType=Center&entityId=${BusinessKey.customerId}&currentFlowKey=${requestScope.currentFlowKey}">
							<mifos:mifoslabel
								name="Center.ChangeLogLink" bundle="CenterUIResources"></mifos:mifoslabel>
							</html-el:link> <br>

						</tr>
					</table>

					</td>



				<!-- Performance History -->
					<td width="30%" align="left" valign="top" class="paddingleft1">
					<table width="100%" border="0" cellpadding="2" cellspacing="0"
						class="bluetableborder">

						<tr>
							<td class="bluetablehead05"><span class="fontnormalbold"> <mifos:mifoslabel
								name="Center.PerformanceHistoryHeading"
								/> </span></td>
						</tr>
						<tr>
							<td class="paddingL10"><img
								src="pages/framework/images/trans.gif" width="10" height="2"></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"> 
								<fmt:message key="Center.hashof">
								<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
								</fmt:message>:
							<c:out value="${centerDetailsDto.numberOfClients}" /></span></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"> 
								<fmt:message key="Center.hashof">
								<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
								</fmt:message>:
							<c:out value="${centerDetailsDto.numberOfGroups}" /></span></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"> <mifos:mifoslabel
								name="Center.Total" bundle="CenterUIResources" /> <mifos:mifoslabel
								name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel
								name="Center.portfolio" bundle="CenterUIResources"
								isColonRequired="yes" /> <c:out
								value="${centerDetailsDto.totalOutstandingPortfolio}" /></span></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"> <mifos:mifoslabel
								name="Center.Total" bundle="CenterUIResources" /> <mifos:mifoslabel
								name="${ConfigurationConstants.SAVINGS}" isColonRequired="yes" />
							<c:out value="${centerDetailsDto.totalSavings}" /></span></td>
						</tr>

					</table>


					<!-- Performance History  end -->


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
                    <a id="viewCenterDetails.link.survey" href="surveyInstanceAction.do?method=get&value(instanceId)=${surveyInstance.instanceId}&value(surveyType)=center">
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
                  <a id="viewCenterDetails.link.attachSurvey" href="surveyInstanceAction.do?method=choosesurvey&globalNum=${BusinessKey.globalCustNum}&surveyType=center">
                    <mifos:mifoslabel name="Surveys.attachasurvey" bundle="SurveysUIResources"/>
                  </a> <br>
                  <a id="viewCenterDetails.link.viewAllSurveys" href="surveysAction.do?method=mainpage">
                    <mifos:mifoslabel name="Surveys.viewallsurveys" bundle="SurveysUIResources"/>
                  </a>
              </td>
            </tr>
          </table>
          <table width="95%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="pages/framework/images/trans.gif" width="7"
								height="8"></td>
						</tr>
		</table>
		</c:if>
          <table width="100%" border="0" cellpadding="2" cellspacing="0"
						class="bluetableborder">
						<tr>
							<td class="bluetablehead05"><span class="fontnormalbold"><mifos:mifoslabel
								name="Center.RecentNotes" bundle="CenterUIResources" /></span></td>
						</tr>
						<tr>
							<td class="paddingL10"><img
								src="pages/framework/images/trans.gif" width="10" height="2"></td>
						</tr>
						<tr>
							<td class="paddingL10"><c:choose>
								<c:when test="${!empty BusinessKey.recentCustomerNotes}">
									<c:forEach var="note" items="${BusinessKey.recentCustomerNotes}">
										<span class="fontnormal8ptbold"> <c:out
											value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,note.commentDate)}" />
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
								class="fontnormal8pt"> <c:if test="${!empty BusinessKey.customerNotes}">
								<html-el:link styleId="viewCenterDetails.link.seeAllNotes"
									href="customerNotesAction.do?method=search&customerId=${BusinessKey.customerId}&globalAccountNum=${BusinessKey.globalCustNum}&customerName=${BusinessKey.displayName}&securityParamInput=Center&levelId=${BusinessKey.customerLevel.id}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
									<mifos:mifoslabel name="center.SeeAllNotesLink"
										bundle="CenterUIResources"></mifos:mifoslabel>
								</html-el:link>
								<br>
							</c:if> <a id="viewCenterDetails.link.notesLink"
								href="customerNotesAction.do?method=load&customerId=<c:out value="${BusinessKey.customerId}"/>&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
							<mifos:mifoslabel name="Center.NotesLink"
								/> </a> </span></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
		</html-el:form>
	</tiles:put>
</tiles:insert>
