<%--
Copyright (c) 2005-2011 Grameen Foundation USA
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
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/customer/customerfunctions" prefix="customerfn"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<span id="page.id" title="ViewGroupDetails"></span>
	<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
	<fmt:setBundle basename="org.mifos.config.localizedResources.GroupUIResources"/>
		<html-el:form action="groupCustAction.do">
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<c:set value="${requestScope.currentPageUrl}" var="currentPageUrl"/>
			<c:if test="${currentPageUrl == null}">
				<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'currentPageUrl')}&method=get" var="currentPageUrl"/>
			</c:if>
			<c:set 
				value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'groupInformationDto')}"
			   	var="groupInformationDto" />			
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"><customtags:headerLink
						selfLink="false" /> </span>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="73%" class="headingorange"><c:out
								value="${groupInformationDto.groupDisplay.displayName}" /> <br>
							</td>
							<td width="27%" rowspan="2" align="right" valign="top"
								class="fontnormal"><c:if
								test="${groupInformationDto.groupDisplay.customerStatusId != CustomerStatus.GROUP_CLOSED.value}">
								<a id="viewgroupdetails.link.editStatus" href="editCustomerStatusAction.do?method=loadStatus&customerId=${groupInformationDto.groupDisplay.customerId}&input=group&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
									
									<fmt:message key="Group.editStatus">
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
					</fmt:message>
										
								</a>
							</c:if></td>
						</tr>
						<tr>
							<td colspan="2"><font class="fontnormalRedBold"><span id="viewgroupdetails.error.message"><html-el:errors
								bundle="GroupUIResources" /></span></font></td>
						</tr>
						<tr>
							<td class="fontnormalbold"><span class="fontnormal"> <mifoscustom:MifosImage
								id="${groupInformationDto.groupDisplay.customerStatusId}" moduleName="org.mifos.customers.util.resources.customerImages" />

							<span id="viewgroupdetails.text.status"><c:out value="${groupInformationDto.groupDisplay.customerStatusName}" /></span>
                            <c:if
                                test="${groupInformationDto.groupDisplay.blackListed}">
                                <span id="viewgroupdetails.img.blackflag"><mifoscustom:MifosImage id="blackListed" moduleName="org.mifos.customers.util.resources.customerImages" /></span>
                            </c:if>
                            <c:forEach
                                var="flagSet" items="${groupInformationDto.customerFlags}">
                                <span class="fontnormal"><span id="viewgroupdetails.text.closeCancelReason"><c:out value="${flagSet.statusFlagName}" /></span></span>

							</c:forEach> <span class="fontnormal"><br>
							<mifos:mifoslabel name="Group.systemId" bundle="GroupUIResources" />
							<c:out value="${groupInformationDto.groupDisplay.globalCustNum}" /> </span><br>
							<span class="fontnormal"> <mifos:mifoslabel
								name="Group.loanofficer" bundle="GroupUIResources"></mifos:mifoslabel>
							<c:out value="${groupInformationDto.groupDisplay.loanOfficerName}" /> </span><br>
							<br>
							<span class="fontnormalbold">
								<fmt:message key="Group.clientAssign" >
				  				<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
				  				</fmt:message>
								</span>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<c:if
								test="${groupInformationDto.groupDisplay.customerStatusId != CustomerStatus.GROUP_CANCELLED.value and groupInformationDto.groupDisplay.customerStatusId != CustomerStatus.GROUP_CLOSED.value}">
								<span class="fontnormal"> <a id="viewgroupdetails.link.add"
									href="clientCustAction.do?method=load&groupFlag=1&parentGroupId=${groupInformationDto.groupDisplay.customerId}&recordOfficeId=${groupInformationDto.groupDisplay.branchId}&recordLoanOfficerId=${groupInformationDto.groupDisplay.loanOfficerId}&randomNUm=${sessionScope.randomNUm}">
								    <fmt:message key="Group.Add" >
				  				    <fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
				  				    </fmt:message>
								</a>
								</span>
							</c:if> <br>
							<span class="fontnormal"> 
							
								
								<fmt:message key="Group.groupdetailViewMsg" >
				  				<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
				  				</fmt:message>
							<br>
							</span>
							<div id="Layer2"
								style="border: 1px solid #CECECE; height:100px; width:250px; overflow: auto; padding:6px; margin-top:5px;">
							<span class="fontnormal"> <!-- Display all client under this group -->
							<c:choose>
								<c:when test="${!empty groupInformationDto.clientsOtherThanClosedAndCancelled}">
									<c:choose>
										<c:when
											test="${!empty groupInformationDto.groupDisplay and !empty groupInformationDto.groupDisplay.loanOfficerId}">
											<c:forEach var="client" items="${groupInformationDto.clientsOtherThanClosedAndCancelled}">
												<a id="viewgroupdetails.link.client"
													href="viewClientDetails.ftl?globalCustNum=${client.globalCustNum}&recordOfficeId=${groupInformationDto.groupDisplay.branchId}&recordLoanOfficerId=${groupInformationDto.groupDisplay.loanOfficerId}">
												<c:out value="${client.displayName}" /> <c:out
													value="${customerfn:getClientPosition(groupInformationDto.customerPositions,client)}" />
												<br>
												</a>
											</c:forEach>
										</c:when>									
										<c:otherwise>
											<c:forEach var="client" items="${groupInformationDto.clientsOtherThanClosedAndCancelled}">
												<a id="viewgroupdetails.link.client"
													href="viewClientDetails.ftl?globalCustNum=${client.globalCustNum}&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}">
												<c:out value="${client.displayName}" /><%-- <c:out
													value="${customerfn:getClientPositions(requestScope.customerPositions,client)}" />--%>
												<br>
												</a>
											</c:forEach>
										</c:otherwise>
									</c:choose>

								</c:when>
								<c:otherwise>
									
										<fmt:message key="Group.noclientsavailable" >
				  						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
				  						</fmt:message>
							<br>
								</c:otherwise>
							</c:choose> </span><br>
							</div>
							<table width="50%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td><img src="pages/framework/images/trans.gif" width="10"
										height="10"></td>
								</tr>
							</table></td>
						</tr>
					</table>
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="36%" class="headingorange"><mifos:mifoslabel
								name="Group.accountinformation" bundle="GroupUIResources"></mifos:mifoslabel>
							</td>
						</tr>
						<!-- bug id 28004. Added if condition to show link to open accounts if only client is active-->
						<c:if
							test="${groupInformationDto.groupDisplay.customerStatusId == CustomerStatus.GROUP_ACTIVE.value}">
							<tr align="right">
								<td class="headingorange"><span class="fontnormal"> <mifos:mifoslabel
									name="Group.opennewaccount" bundle="GroupUIResources"></mifos:mifoslabel>

								&nbsp; <c:if test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'isGroupLoanAllowed') == true}">
								
								<c:url value="createLoanAccount.ftl" var="createLoanAccountMethodUrl" >
									<c:param name="customerId" value="${groupInformationDto.groupDisplay.customerId}" />
									<c:param name="recordOfficeId" value="${UserContext.branchId}" />
									<c:param name="recordLoanOfficerId" value="${UserContext.id}" />
								</c:url >
								
									<html-el:link styleId="viewgroupdetails.link.newLoanAccount"
									href="${createLoanAccountMethodUrl}">
										<mifos:mifoslabel name="${ConfigurationConstants.LOAN}"></mifos:mifoslabel>
									</html-el:link> &nbsp;|&nbsp;
		              </c:if> 
								<c:url value="createSavingsAccount.ftl" var="createSavingsAccountMethodUrl" >
									<c:param name="customerId" value="${groupInformationDto.groupDisplay.customerId}" />
									<c:param name="recordOfficeId" value="${UserContext.branchId}" />
									<c:param name="recordLoanOfficerId" value="${UserContext.id}" />
								</c:url >
								<html-el:link styleId="viewgroupdetails.link.newSavingsAccount"
									href="${createSavingsAccountMethodUrl}">
									<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"></mifos:mifoslabel>
								</html-el:link> </span></td>
							</tr>
						</c:if>
					</table>


					<c:if test="${!empty groupInformationDto.loanAccountsInUse}">
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
									<c:forEach items="${groupInformationDto.loanAccountsInUse}"
										var="loan">
										<tr>
											<td>
											<table width="100%" border="0" cellspacing="0"
												cellpadding="0">
												<tr>

													<td width="65%"><span class="fontnormal"> 
													<c:url value="viewLoanAccountDetails.ftl" var="viewLoanAccountDetailsUrl" >
														<c:param name="globalAccountNum" value="${loan.globalAccountNum}" />
														<c:param name="customerId" value="${groupInformationDto.groupDisplay.customerId}" />
														<c:param name="recordOfficeId" value="${param.recordOfficeId}" />
														<c:param name="recordLoanOfficerId" value="${param.recordLoanOfficerId}" />
													</c:url >
													<html-el:link styleId="viewgroupdetails.link.viewLoanAccount"
														href="${viewLoanAccountDetailsUrl}">
														<c:out value="${loan.prdOfferingName}" />, <mifos:mifoslabel name="Group.acc" bundle="GroupUIResources" /><c:out
															value="${loan.globalAccountNum}" />
													</html-el:link> </span></td>
													<td width="35%"><span class="fontnormal"> <mifoscustom:MifosImage
														id="${loan.accountStateId}" moduleName="org.mifos.accounts.loan.util.resources.loanImages" />
													<c:out value="${loan.accountStateName}" /> </span></td>
												</tr>
											</table>
											<c:if
												test="${loan.accountStateId==5 || loan.accountStateId==9}">
												<span class="fontnormal"> <mifos:mifoslabel
													name="loan.outstandingbalance" />: <fmt:formatNumber
													value="${loan.outstandingBalance}" /><br>
												<mifos:mifoslabel name="loan.amount_due" />: <fmt:formatNumber
													value="${loan.totalAmountDue}" /> </span>
											</c:if></td>
										</tr>
										<tr>
											<td><img src="pages/framework/images/trans.gif" width="5"
												height="20"></td>
										</tr>
									</c:forEach>
								</table>
						</table>

						<table width="50%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td><img src="pages/framework/images/trans.gif" width="10"
									height="10"></td>
							</tr>
						</table>
					</c:if> <c:if
						test="${!empty groupInformationDto.savingsAccountsInUse}">
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
									<c:forEach items="${groupInformationDto.savingsAccountsInUse}"
										var="savings">
										<tr>
											<td>
											<table width="100%" border="0" cellspacing="0"
												cellpadding="0">
												<tr>
													<td width="65%"><span class="fontnormal"> 
													<c:url value="viewSavingsAccountDetails.ftl" var="viewSavingsAccountDetailsUrl" >
														<c:param name="globalAccountNum" value="${savings.globalAccountNum}" />
														<c:param name="recordOfficeId" value="${param.recordOfficeId}" />
														<c:param name="recordLoanOfficerId" value="${param.recordLoanOfficerId}" />
													</c:url >
													<html-el:link styleId="viewgroupdetails.link.viewSavingsAccount"
														href="${viewSavingsAccountDetailsUrl}">
														<c:out value="${savings.prdOfferingName}" />, <mifos:mifoslabel name="Group.acc" bundle="GroupUIResources" /><c:out
															value="${savings.globalAccountNum}" />
													</html-el:link> </span></td>
													<td width="35%"><span class="fontnormal"> <mifoscustom:MifosImage
														id="${savings.accountStateId}"
														moduleName="org.mifos.accounts.savings.util.resources.savingsImages" /> <c:out
														value="${savings.accountStateName}" /> </span></td>
												</tr>
											</table>
											<span class="fontnormal"><mifos:mifoslabel
												name="Group.balance" /> <fmt:formatNumber
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
								class="tableContentLightBlue"><span class="fontnormalbold"> <fmt:message key="Group.charges">
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
					</fmt:message>
							</span>
							<table width="95%" border="0" align="center" cellpadding="0"
								cellspacing="0">
								<tr>
									<td>
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="53%"><span class="fontnormal"> <a id="viewgroupdetails.link.viewDetails"
												href="customerAccountAction.do?method=load&globalCustNum=${groupInformationDto.groupDisplay.globalCustNum}">
											<mifos:mifoslabel name="Group.viewdetails"
												bundle="GroupUIResources" /> </a> </span></td>
										</tr>
										<tr>
											<td><span class="fontnormal"><mifos:mifoslabel
												name="Group.amountdue" bundle="GroupUIResources" />
												<span id="viewgroupdetails.text.amountDue">
													<fmt:formatNumber value="${groupInformationDto.customerAccountSummary.nextDueAmount}" />
												</span> 
											</span></td>
										</tr>
									</table>
									</td>
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
							<td width="38%" align="right" class="fontnormal">
							<span class="fontnormal">
								<c:if test="${groupInformationDto.groupDisplay.customerStatusId !=7 && groupInformationDto.groupDisplay.customerStatusId !=8}">
									<html-el:link styleId="viewgroupdetails.link.viewAllClosedAccounts" href="custAction.do?method=getClosedAccounts&customerId=${groupInformationDto.groupDisplay.customerId}&input=group&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
										<mifos:mifoslabel name="Group.viewallclosedaccounts" bundle="GroupUIResources"></mifos:mifoslabel>
									</html-el:link>
								</c:if>
							</span>
							</td>
						</tr>
					</table>

					<table width="50%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="pages/framework/images/trans.gif" width="10"
								height="10"></td>
						</tr>
					</table>
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>

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
							<td width="63%" height="23" class="headingorange"><fmt:message key="Group.groupinformation">
				<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
				</fmt:message>
							</td>
							<td width="37%" align="right" class="fontnormal"><html-el:link styleId="viewgroupdetails.link.editGroupInformation"
								action="groupCustAction.do?method=manage&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
								
									<fmt:message key="Group.editInformation">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
									</fmt:message>
							</html-el:link></td>
						</tr>

						<tr>
							<td colspan="2" class="fontnormalbold"><span class="fontnormal">
							
								<fmt:message key="Group.approvaldate">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
									</fmt:message>
								<c:out
								value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,groupInformationDto.groupDisplay.customerActivationDate)}" />
							<br></td>
						</tr>

						<tr id="Group.ExternalId">
							<td class="fontnormalbold"><span class="fontnormal"> <mifos:mifoslabel
								name="${ConfigurationConstants.EXTERNALID}"
								isColonRequired="Yes" keyhm="Group.ExternalId"
								isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <c:out
								value="${groupInformationDto.groupDisplay.externalId}" /> <br></td>
						</tr>




						<tr>
							<td class="fontnormalbold"><span class="fontnormal"> <mifos:mifoslabel
								name="Group.FormedBy" bundle="GroupUIResources"></mifos:mifoslabel>
							<c:out
								value="${groupInformationDto.groupDisplay.customerFormedByDisplayName}" /><br>

							<%-- Collection Sheet
				  <mifos:mifoslabel name="Group.collectionsheettype" bundle="GroupUIResources"></mifos:mifoslabel> Collection Sheet Type 1
				  <c:out value="${requestScope.GroupVO.collectionSheet.collectionSheetName}"/> --%>
							</span> <br>
							<span class="fontnormal"></span></td>
						</tr>

						<tr id="Group.Trained">
							<td class="fontnormalbold"><mifos:mifoslabel
								name="Group.trainingstatus" bundle="GroupUIResources"
                                keyhm="Group.Trained"
								isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <br>
							<span class="fontnormal"> <mifos:mifoslabel
								name="Group.trainedon" bundle="GroupUIResources"
								keyhm="Group.Trained"
								isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <c:if
								test="${groupInformationDto.groupDisplay.trained}">
								<c:out
									value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,groupInformationDto.groupDisplay.trainedDate)}" />
							</c:if> </span><br>
							<br>
							<%--  programs
					<mifos:mifoslabel name="Group.programs" bundle="GroupUIResources"></mifos:mifoslabel>
						<span class="fontnormal"><span class="fontnormal"><br>
								<c:forEach var="program" items="${requestScope.customerPrograms}">
									<c:out value="${program.programName}"/><br>
								</c:forEach>
					 </span></span><br>
					<br> --%></td>
						</tr>

						<tr>
							<td class="fontnormalbold"><mifos:mifoslabel
								name="Group.officialtitlesassigned" bundle="GroupUIResources"></mifos:mifoslabel>
							<span class="fontnormal"><br>
							</span> <span class="fontnormal"><c:forEach var="pos"
								items="${groupInformationDto.customerPositions}">
								<c:if test="${! empty pos.positionName}">
									<c:out value="${pos.positionName}" />: <c:out
										value="${pos.customerDisplayName}" />
									<br>
								</c:if>
							</c:forEach> </span><br>
							</td>
						</tr>


						<%--Address --%>
						<tr id="Group.Address">
							<td class="fontnormalbold"><mifos:mifoslabel name="Group.address"
								bundle="GroupUIResources" keyhm="Group.Address"
								isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <br>
							<c:if
								test="${!empty groupInformationDto.address.displayAddress}">
								<span class="fontnormal"> <c:out
									value="${groupInformationDto.address.displayAddress}" /><br>
								</span>
							</c:if> <c:if
								test="${!empty groupInformationDto.address.city}">
								<span class="fontnormal"> <c:out
									value="${groupInformationDto.address.city}" /><br>
								</span>
							</c:if> <c:if
								test="${!empty groupInformationDto.address.state}">
								<span class="fontnormal"> <c:out
									value="${groupInformationDto.address.state}" /><br>
								</span>
							</c:if> <c:if
								test="${!empty groupInformationDto.address.country}">
								<span class="fontnormal"> <c:out
									value="${groupInformationDto.address.country}" />
								<br>
								</span>
							</c:if> <c:if
								test="${!empty groupInformationDto.address.zip}">
								<span class="fontnormal"> <c:out
									value="${groupInformationDto.address.zip}" /><br>
								</span>
							</c:if></td>
						</tr>

						<tr id="Group.PhoneNumber">
							<td class="fontnormalbold"><span class="fontnormal"> <c:if
								test="${!empty groupInformationDto.address.phoneNumber}">
								<br>
								<span class="fontnormal"> <mifos:mifoslabel
									name="Group.telephone" bundle="GroupUIResources"
									keyhm="Group.PhoneNumber"
									isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <c:out
									value="${groupInformationDto.address.phoneNumber}" />
								</span>
								<br>
							</c:if> <br></td>
						</tr>
						<%--Additional information custom fields--%>
						<c:if test="${!empty groupInformationDto.customFields}">
						<tr>
							<td height="23" colspan="2" class="fontnormalbold"><br>
								<mifos:mifoslabel name="Group.additionalinformation"
									bundle="GroupUIResources"></mifos:mifoslabel>
							<span class="fontnormal"><br>
							<c:forEach var="customField" items="${groupInformationDto.customFields}">
										<c:choose>
											<c:when test="${customField.fieldType == 3}">
												<mifos:mifoslabel name="${customField.lookUpEntityType}"
													bundle="CenterUIResources"></mifos:mifoslabel>:
							    	<span class="fontnormal"><c:out
													value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,customField.fieldValue)}" />
											</c:when>
											<c:otherwise>
												<mifos:mifoslabel name="${customField.lookUpEntityType}"
													bundle="CenterUIResources"></mifos:mifoslabel>:
							    	<span class="fontnormal"><c:out value="${customField.fieldValue}" />
											</c:otherwise>
										</c:choose>
										<br>
							</c:forEach> <br>
							</span></td>
						</tr>
						</c:if>
						<tr>
							<td colspan="2" class="fontnormal"><span class="fontnormal"> <!-- Dynamic links -->
							<c:choose>
								<c:when test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'CenterHierarchyExist') == true}">
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="59%" class="fontnormalbold">
											
												<fmt:message key="Group.centermembership&meetingdetails">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /></fmt:param>
									</fmt:message><span
												class="fontnormalRed">
												
												<br>
											<mifos:mifoslabel name="Group.meetings"
												bundle="GroupUIResources" />&nbsp; <span id="viewgroupdetails.text.meetingSchedule"><c:out
												value="${groupInformationDto.customerMeeting.meetingSchedule}" /></span></span>
											<span class="fontnormal"><br></span>
											</span> <span id="viewgroupdetails.text.meetingPlace" class="fontnormal"> <c:if
												test="${groupInformationDto.customerMeeting.meetingPlace!=null && !empty groupInformationDto.customerMeeting.meetingPlace}">
												<c:out
													value="${groupInformationDto.customerMeeting.meetingPlace}" />
												</span><br><span id="viewgroupdetails.text.parentCustomerDisplayName" class="fontnormal">
											</c:if> <c:out
												value="${groupInformationDto.groupDisplay.parentCustomerDisplayName}" /><br>
											</span></td>
											<td width="41%" align="right" valign="top" class="fontnormal">
											<html-el:link styleId="viewgroupdetails.link.editCenterMembership" href="groupTransferAction.do?method=loadParents&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}"> 
											
												<fmt:message key="Group.editMembership">
												<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /></fmt:param>
												</fmt:message> 
												</html-el:link></td>
										</tr>
									</table>
								</c:when>
								<c:otherwise>
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="59%" class="fontnormalbold"><mifos:mifoslabel
												name="Group.meetingdetails" bundle="GroupUIResources"></mifos:mifoslabel>
											<span class="fontnormalRed"><br>
											<mifos:mifoslabel name="Group.meetings"
												bundle="GroupUIResources" /> <span id="viewgroupdetails.text.meetingSchedule"><c:out
												value="${groupInformationDto.customerMeeting.meetingSchedule}" /></span>
											</span> <br>
											<span class="fontnormal"> <c:out
												value="${groupInformationDto.customerMeeting.meetingPlace}" />
											</span></td>
											<td width="41%" align="right" valign="top" class="fontnormal"><br>

											<html-el:link styleId="viewgroupdetails.link.editMeetingSchedule"
												action="meetingAction.do?method=edit&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}&customerLevel=${groupInformationDto.groupDisplay.customerLevelId}">
												<mifos:mifoslabel name="Group.editmeetingchedule"
													bundle="GroupUIResources" />
												<br>
											</html-el:link>

											<a id="viewgroupdetails.link.editOfficeMembership" href="groupTransferAction.do?method=loadBranches&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}"> <mifos:mifoslabel
												name="Group.editOfficeMembership" bundle="GroupUIResources" />
											</a></td>
										</tr>
									</table>

								</c:otherwise>
							</c:choose> <br>

							<%--Historical data link--%>
							<span class="fontnormal">
                           <c:url value="viewAndEditQuestionnaire.ftl" var="viewAndEditQuestionnaireMethodUrl" >
                            <c:param name="creatorId" value="${sessionScope.UserContext.id}" />
                            <c:param name="entityId" value="${groupInformationDto.groupDisplay.customerId}" />
                            <c:param name="event" value="Create" />
                            <c:param name="source" value="Group" />
                            <c:param name="backPageUrl" value="${currentPageUrl}" />
                           </c:url >
                            <c:set var="questionnaireFor" scope="session" value="${groupInformationDto.groupDisplay.displayName}"/>
                            <a id="groupdetail.link.questionGroups" href="${viewAndEditQuestionnaireMethodUrl}">
                                <mifos:mifoslabel name="client.ViewQuestionGroupResponsesLink" bundle="ClientUIResources" />
                            </a>
                            <br/>
							<a id="viewgroupdetails.link.viewHistoricalData"
								href="custHistoricalDataAction.do?method=getHistoricalData&globalCustNum=${groupInformationDto.groupDisplay.globalCustNum}&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
							<mifos:mifoslabel name="Group.viewhistoricaldata"
								bundle="GroupUIResources"></mifos:mifoslabel> </a> <br>
								<html-el:link styleId="viewgroupdetails.link.viewChangeLog" href="groupCustAction.do?method=loadChangeLog&entityType=Group&entityId=${groupInformationDto.groupDisplay.customerId}&currentFlowKey=${requestScope.currentFlowKey}">
							<mifos:mifoslabel name="Group.viewchangelog"
								bundle="GroupUIResources"></mifos:mifoslabel> </html-el:link> <br>
							</span></td>
						</tr>
					</table>

					</td>
					<td width="30%" align="left" valign="top" class="paddingleft1">
					<table width="100%" border="0" cellpadding="2" cellspacing="0"
						class="bluetableborder">
						<tr>
							<td class="bluetablehead05"><span class="fontnormalbold"> <mifos:mifoslabel
								name="Group.performancehistory" bundle="GroupUIResources"></mifos:mifoslabel>
							</span></td>
						</tr>

						<tr>
							<td class="paddingL10"><img
								src="pages/framework/images/trans.gif" width="10" height="2"></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"> 
								
								<fmt:message key="Group.hashof">
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
					</fmt:message> 
								<span id="viewgroupdetails.text.numberOfClientsInGroup"><fmt:formatNumber
								value="${groupInformationDto.groupPerformanceHistory.activeClientCount}" /></span>
							</span></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"> 
								<fmt:message key="Group.Amountoflast">
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /></fmt:param>
					</fmt:message>:  
								
							<fmt:formatNumber
								value="${groupInformationDto.groupPerformanceHistory.lastGroupLoanAmount}" /></span></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"> 
							
								<fmt:message key="Group.Avgindividual">
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /></fmt:param>
					</fmt:message>   
								<span id="viewgroupdetails.text.avgIndyvidualLoanSize">
                                    <c:if test="${groupInformationDto.groupPerformanceHistory.avgLoanAmountForMemberInvalid}">
                                        <c:out value="${groupInformationDto.groupPerformanceHistory.avgLoanAmountForMember}" />
                                    </c:if>
                                    <c:if test="${!groupInformationDto.groupPerformanceHistory.avgLoanAmountForMemberInvalid}">
                                        <fmt:formatNumber value="${groupInformationDto.groupPerformanceHistory.avgLoanAmountForMember}" />
                                    </c:if>
								</span></span></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"> 
								<fmt:message key="Group.totalPortfolio">
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /></fmt:param>
					</fmt:message>    
								
								<span id="viewgroupdetails.text.totalLoanPortfolio">
                                    <c:if test="${groupInformationDto.groupPerformanceHistory.totalOutStandingLoanAmountInvalid}">
                                        <c:out value="${groupInformationDto.groupPerformanceHistory.totalOutStandingLoanAmount}" />
                                    </c:if>
                                    <c:if test="${!groupInformationDto.groupPerformanceHistory.totalOutStandingLoanAmountInvalid}">
                                        <fmt:formatNumber value="${groupInformationDto.groupPerformanceHistory.totalOutStandingLoanAmount}" />
                                    </c:if>
								</span></span></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"> <mifos:mifoslabel
								name="Group.PortfolioAtRisk" bundle="GroupUIResources" />
                                <c:if test="${groupInformationDto.groupPerformanceHistory.portfolioAtRiskInvalid}">
                                    <c:out value="${groupInformationDto.groupPerformanceHistory.portfolioAtRisk}" />
                                </c:if>
                                <c:if test="${!groupInformationDto.groupPerformanceHistory.portfolioAtRiskInvalid}">
                                    <fmt:formatNumber value="${groupInformationDto.groupPerformanceHistory.portfolioAtRisk}" />
                                </c:if>
								</span></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"> <mifos:mifoslabel
								name="Group.Total" bundle="GroupUIResources" /> <mifos:mifoslabel
								name="${ConfigurationConstants.SAVINGS}" isColonRequired="yes" />
                                <c:if test="${groupInformationDto.groupPerformanceHistory.totalSavingsAmountInvalid}">
                                    <c:out value="${groupInformationDto.groupPerformanceHistory.totalSavingsAmount}" />
                                </c:if>
                                <c:if test="${!groupInformationDto.groupPerformanceHistory.totalSavingsAmountInvalid}">
                                    <fmt:formatNumber value="${groupInformationDto.groupPerformanceHistory.totalSavingsAmount}" />
                                </c:if>
							</span></td>
						</tr>
						<c:if test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'isGroupLoanAllowed') == true}">
							<tr>
								<td class="paddingL10"><span class="fontnormal8pt"> 
									<fmt:message key="Group.loancyclecounter">
										<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}"/></fmt:param>
									</fmt:message>:
								</span></td>
							</tr>

							<c:forEach items="${groupInformationDto.groupPerformanceHistory.loanCycleCounters}"
								var="loanCycleCounter">
								<tr>
									<td class="paddingL10"><span class="fontnormal8pt">&nbsp;&nbsp;&nbsp;<c:out
										value="${loanCycleCounter.offeringName}" />: <fmt:formatNumber
										value="${loanCycleCounter.counter}" /></span></td>
								</tr>
							</c:forEach>
						</c:if>
					</table>
					<table width="95%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="pages/framework/images/trans.gif" width="7"
								height="8"></td>
						</tr>
					</table>
          <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'questionGroupInstances')}"
               var="questionGroupInstances" />
          <table id="questionGroupInstances" name="questionGroupInstances" width="100%" border="0" cellpadding="2" cellspacing="0" class="bluetableborder">
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
          <c:if test="${!empty questionGroupInstances}">
            <c:forEach items="${questionGroupInstances}" var="questionGroupInstance">
              <tr>
                <td width="70%" class="paddingL10">
                   <c:url value="viewAndEditQuestionnaire.ftl" var="viewAndEditQuestionnaireMethodUrl" >
                    <c:param name="creatorId" value="${sessionScope.UserContext.id}" />
                    <c:param name="entityId" value="${groupInformationDto.groupDisplay.customerId}" />
                    <c:param name="instanceId" value="${questionGroupInstance.id}" />
                    <c:param name="event" value="View" />
                    <c:param name="source" value="Group" />
                    <c:param name="backPageUrl" value="${currentPageUrl}" />
                   </c:url >
                  <span class="fontnormal8pt">
                    <a id="${questionGroupInstance.id}" href="${viewAndEditQuestionnaireMethodUrl}">
                      <c:out value="${questionGroupInstance.questionGroupTitle}"/>
                    </a>
                  </span>
                </td>
                <td width="30%" align="left" class="paddingL10">
                  <span class="fontnormal8pt">
                    <label id="label.${questionGroupInstance.id}">
                        <c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale, questionGroupInstance.dateCompleted)}" />
                    </label>
                  </span>
                </td>
              </tr>
            </c:forEach>
          </c:if>
            <tr>
              <td colspan="2" align="right" class="paddingleft05">
                <span class="fontnormal8pt">
                  <c:set var="questionnaireFor" scope="session" value="${groupInformationDto.groupDisplay.displayName}"/>
                  <c:remove var="urlMap" />
                  <jsp:useBean id="urlMap" class="java.util.LinkedHashMap"  type="java.util.HashMap" scope="session"/>
                  <c:set target="${urlMap}" property="${groupInformationDto.groupDisplay.displayName}" value="groupCustAction.do?method=get&globalCustNum=${groupInformationDto.groupDisplay.globalCustNum}"/>
                  <c:url value="questionnaire.ftl" var="questionnaireUrl" >
                    <c:param name="creatorId" value="${sessionScope.UserContext.id}" />
                    <c:param name="entityId" value="${groupInformationDto.groupDisplay.customerId}" />
                    <c:param name="event" value="View" />
                    <c:param name="source" value="Group" />
                    <c:param name="backPageUrl" value="${currentPageUrl}" />
                  </c:url >
                  <a id="viewgroupdetails.link.attachSurvey" href="${questionnaireUrl}">
                    <mifos:mifoslabel name="Surveys.attachasurvey" bundle="SurveysUIResources"/>
                  </a> <br>
                </span>
              </td>
            </tr>
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
								name="Group.recentnotes" bundle="GroupUIResources"></mifos:mifoslabel>
							</span></td>
						</tr>
						<tr>
							<td class="paddingL10"><img
								src="pages/framework/images/trans.gif" width="10" height="2"></td>
						</tr>
						<tr>
							<td class="paddingL10"><c:choose>
								<c:when test="${!empty groupInformationDto.recentCustomerNotes}">
									<c:forEach var="note" items="${groupInformationDto.recentCustomerNotes}">
										<span class="fontnormal8ptbold"> <c:out
											value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,note.commentDate)}" />:
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
								class="fontnormal8pt"> <c:if test="${!empty groupInformationDto.recentCustomerNotes}">
								<a id="viewgroupdetails.link.seeAllNotes"
									href="customerNotesAction.do?method=search&customerId=${groupInformationDto.groupDisplay.customerId}&globalAccountNum=${groupInformationDto.groupDisplay.globalCustNum}&customerName=${groupInformationDto.groupDisplay.displayName}&securityParamInput=Group&levelId=${groupInformationDto.groupDisplay.customerLevelId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="Group.seeallnotes"
									bundle="GroupUIResources"></mifos:mifoslabel> </a>
								<br>
							</c:if> <a id="viewgroupdetails.link.addNote"
								href="customerNotesAction.do?method=load&customerId=${groupInformationDto.groupDisplay.customerId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
							<mifos:mifoslabel name="Group.addanote" bundle="GroupUIResources"></mifos:mifoslabel>
							</a> </span></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			<br>
			<br>
			<html-el:hidden property="customerId"
				value="${groupInformationDto.groupDisplay.customerId}" />
			<html-el:hidden property="globalCustNum"
				value="${groupInformationDto.groupDisplay.globalCustNum}" />
			<html-el:hidden property="globalAccountNum"
				value="${groupInformationDto.customerAccountSummary.globalAccountNum}" />
			<html-el:hidden property="versionNo"
				value="${requestScope.GroupVO.versionNo}" />
			<html-el:hidden property="displayName"
				value="${groupInformationDto.groupDisplay.displayName}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>


