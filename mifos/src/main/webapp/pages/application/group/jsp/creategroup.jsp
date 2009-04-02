<!-- /**

 * creategroup.jsp    version: 1.0



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

 */-->
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="/customer/customerfunctions" prefix="customerfn"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
	<input type="hidden" id="page.id" value="CreateGroup"/>


		<script src="pages/framework/js/conversion.js"></script>
		<script src="pages/framework/js/con_en.js"></script>
		<script src="pages/framework/js/con_${sessionScope['UserContext'].currentLocale}.js"></script>
		<script language="javascript">

  function goToCancelPage(){
	groupCustActionForm.action="groupCustAction.do?method=cancel";
	groupCustActionForm.submit();
  }

  function displayAmount(listBox, textBox ){
	var comboBox = document.getElementsByName(listBox)[0];
	var amountField = document.getElementsByName(textBox)[0];
	if(comboBox.selectedIndex==0)
		amountField.value = "";
	else{
		var indexSelectedFee = comboBox.selectedIndex-1;
		if (groupCustActionForm.selectedFeeAmntList[indexSelectedFee]!= undefined){
			var amount=groupCustActionForm.selectedFeeAmntList[indexSelectedFee].value;
			amountField.value = amount;
		}
		else{
			amountField.value=groupCustActionForm.selectedFeeAmntList.value;
		}
    }
  }
    function loadMeeting(){
	    groupCustActionForm.action="groupCustAction.do?method=loadMeeting";
	    groupCustActionForm.submit();
    }

    function populateDefaultFormedBy(selectBox){
  		document.forms["groupCustActionForm"].elements["formedByPersonnel"].value=document.forms["groupCustActionForm"].elements["loanOfficerId"].value;
	}

    function chkForValidDates(){
    	return chkForTrainedDate();
    }

    function chkForTrainedDate(){
		 var trainedDate = document.getElementById("trainedDate");
	  	 if (trainedDate!=undefined && trainedDate!=null){
	  	 	var trainedDateFormat = document.getElementById("trainedDateFormat");
	  	 	var trainedDateYY = document.getElementById("trainedDateYY");
		    return (validateMyForm(trainedDate,trainedDateFormat,trainedDateYY))
	 	 }
	 	 return true;
    }

</script>
		<SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>
		<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
		<fmt:setBundle basename="org.mifos.config.localizedResources.GroupUIResources"/>
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'CenterHierarchyExist')}" var="CenterHierarchyExist" />
		<html-el:form action="groupCustAction.do?method=preview">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="350" align="left" valign="top" bgcolor="#FFFFFF">
						<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center" class="heading">
									&nbsp;
								</td>
							</tr>
						</table>
						<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
							<tr>
								<td class="bluetablehead">
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="33%">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/tick.gif" width="17" height="17">
														</td>
														<td class="timelineboldgray">
															<c:choose>
																<c:when test="${CenterHierarchyExist == true}">
																	<fmt:message key="Group.select">
																	<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /></fmt:param>
																	</fmt:message>
																</c:when>
																<c:otherwise>
																	<fmt:message key="Group.choosebranch">
																	<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}" /></fmt:param>
																	</fmt:message>
																</c:otherwise>
															</c:choose>
														</td>
													</tr>
												</table>
											</td>
											<td width="34%" align="center">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17">
														</td>
														<td class="timelineboldorange">
															<fmt:message key="Group.groupinformation">
															<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
															</fmt:message>
														</td>
													</tr>
												</table>
											</td>
											<td width="33%" align="right">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17">
														</td>
														<td class="timelineboldorangelight">
															<mifos:mifoslabel name="Group.reviewandsubmit" bundle="GroupUIResources"></mifos:mifoslabel>
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>

						<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
							<tr>
								<td align="left" valign="top" class="paddingleftCreates">
									<table width="93%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td class="headingorange">
												<span class="heading"> <fmt:message key="Group.createnew">
				  								<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
				  								</fmt:message> - </span>
												<fmt:message key="Group.enterInformation">
				  								<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
				  								</fmt:message>
											</td>
										</tr>
										<tr>
											<td class="fontnormal">
												<mifos:mifoslabel name="Group.createpagehead1" bundle="GroupUIResources"></mifos:mifoslabel>
												<mifos:mifoslabel name="Group.createpagehead2" bundle="GroupUIResources"></mifos:mifoslabel>
												<mifos:mifoslabel name="Group.createpagehead3" bundle="GroupUIResources"></mifos:mifoslabel>
												<br>
												<span class="mandatorytext"> <font color="#FF0000">*</font></span>
												<mifos:mifoslabel name="Group.createpagehead4" bundle="GroupUIResources"></mifos:mifoslabel>
											</td>
										</tr>
									</table>
									<logic:messagesPresent>
										<table width="93%" border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td>
													<br>
													<font class="fontnormalRedBold"><span id="creategroup.error.message"><html-el:errors bundle="GroupUIResources" /></span></font>
												</td>
											</tr>
										</table>
									</logic:messagesPresent>


										<br>
										<table width="93%" border="0" cellspacing="0" cellpadding="3">
										<c:choose>
										<c:when test="${CenterHierarchyExist == true}">
											<tr>
												<td>
													<span class="fontnormalbold">
														<mifos:mifoslabel name="Group.loanofficerassigned" bundle="GroupUIResources"/>
													</span>
													<span class="fontnormal">
														<c:out value="${sessionScope.groupCustActionForm.parentCustomer.personnel.displayName}" />
														<html-el:hidden	property="loanOfficerId" value="${sessionScope.groupCustActionForm.parentCustomer.personnel.personnelId}" /> <br>
													</span>
													<span class="fontnormalbold">
														<fmt:message key="Group.centerAssign" >
				  										<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /></fmt:param>
				  										</fmt:message>
													</span>
													<span class="fontnormal">
														<c:out value="${sessionScope.groupCustActionForm.parentCustomer.displayName}" /><br>
													</span>
													<span class="fontnormalbold">
														<mifos:mifoslabel name="Group.meetingschedule" bundle="GroupUIResources"/>
													</span>
													<span class="fontnormal">
														<c:out value="${customerfn:getMeetingSchedule(sessionScope.groupCustActionForm.parentCustomer.customerMeeting.meeting, sessionScope.UserContext)}" /> <br>
													</span>
													<span class="fontnormalbold">
														<mifos:mifoslabel name="Group.locationofthemeeting" bundle="GroupUIResources"/>
													</span>
													<span class="fontnormal">
														<c:out value="${sessionScope.groupCustActionForm.parentCustomer.customerMeeting.meeting.meetingPlace}" /> <br>
													</span>
													<br>
												</td>
											</tr>
									</c:when>
									<c:otherwise>
									<tr>
										<td class="fontnormal">
											<br>
											<span class="fontnormalbold"> 
											<mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}" /><c:out value=" " />
												<mifos:mifoslabel name="Center.Selected" bundle="CenterUIResources" isColonRequired="yes"/>
											</span>
											<c:out value="${sessionScope.groupCustActionForm.officeName}" />
											<br>
											<br>
										</td>
										</tr>
									</c:otherwise>
									</c:choose>
									</table>

									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td colspan="2" class="fontnormalbold">
												<fmt:message key="Group.groupDetail">
												<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
												</fmt:message>
												<br>
												<br>
											</td>
										</tr>
										<tr class="fontnormal">
											<td width="21%" align="right">
												<span class="mandatorytext"><font color="#FF0000">*</font></span>
												<span id="creategroup.label.displayName">
												<fmt:message key="Group.groupname">
												<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
												</fmt:message>
												</span>
											</td>
											<td width="79%">
												<mifos:mifosalphanumtext styleId="creategroup.input.displayName" property="displayName" maxlength="200"/>
											</td>
										</tr>

										<%-- Show following only if center hierarchy does not exists --%>

										<c:if test="${CenterHierarchyExist == false}">
											<tr class="fontnormal">
												<td align="right" class="fontnormal">
													<mifos:mifoslabel name="Group.loanofficer" bundle="GroupUIResources"></mifos:mifoslabel>
												</td>
												<td>
													<mifos:select name="groupCustActionForm" property="loanOfficerId" size="1" onchange="populateDefaultFormedBy(this)">
														<c:forEach var="loanOfficer" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanOfficers')}" >
															<html-el:option value="${loanOfficer.personnelId}">${loanOfficer.displayName}</html-el:option>
														</c:forEach>
													</mifos:select>
												</td>
											</tr>
											<tr class="fontnormal">
												<td align="right" class="fontnormal">
													<mifos:mifoslabel name="Group.meetingschedule" bundle="GroupUIResources"></mifos:mifoslabel>
												</td>
												<td>
													<html-el:link styleId="creategroup.link.meetingSchedule" href="javascript:loadMeeting()">
														<mifos:mifoslabel name="Group.schedulemeeting" bundle="GroupUIResources"></mifos:mifoslabel>
													</html-el:link>
												</td>
											</tr>
										</c:if>

										<%-- show following in both the cases--%>
										<tr class="fontnormal">
											<td align="right" width="21%">
												<mifos:mifoslabel name="Group.FormedBy" mandatory="yes" bundle="GroupUIResources"></mifos:mifoslabel>
											</td>
											<td>
												<mifos:select property="formedByPersonnel">
													<c:forEach var="customerFormedBy" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'formedByLoanOfficers')}">
														<html-el:option value="${customerFormedBy.personnelId}">
															<c:out value="${customerFormedBy.displayName}" />
														</html-el:option>
													</c:forEach>
												</mifos:select>
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right" class="fontnormal">
												<mifos:mifoslabel keyhm="Group.Trained" name="Group.grouptrained" bundle="GroupUIResources" />
											</td>
											<td>
												<mifos:checkbox keyhm="Group.Trained" property="trained" value="1" />
											</td>
										</tr>

										<tr class="fontnormal">
											<td align="right" class="fontnormal">
												<mifos:mifoslabel keyhm="Group.TrainedDate" name="Group.grouptrainedon" bundle="GroupUIResources" />
											</td>
											<td>
												<date:datetag keyhm="Group.TrainedDate" property="trainedDate"/>
											</td>
										</tr>

										<tr class="fontnormal">
											<td align="right" class="fontnormal">
												<mifos:mifoslabel keyhm="Group.ExternalId" isColonRequired="Yes" name="${ConfigurationConstants.EXTERNALID}" />
											</td>
											<td>
												<table width="95%" border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td width="24%">
															<span id="creategroup.label.externalId">
															<mifos:mifosalphanumtext styleId="creategroup.input.externalId" keyhm="Group.ExternalId" property="externalId" maxlength="50"/>
															</span>
														</td>
														<td width="76%" class="fontnormal8pt">
															<mifos:mifoslabel keyhm="Group.ExternalId" name="Center.ExternalIdInfo" bundle="CenterUIResources"/>
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>

									<br>
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td colspan="2" class="fontnormalbold">
												<mifos:mifoslabel keyhm="Group.Address" name="Group.address" bundle="GroupUIResources"></mifos:mifoslabel>
											</td>
										</tr>
										<tr class="fontnormal">
											<td width="21%" align="right">
												<span id="creategroup.label.address1">
												<mifos:mifoslabel keyhm="Group.Address1" isColonRequired="Yes" name="${ConfigurationConstants.ADDRESS1}" />
												</span>
											</td>
											<td width="79%">
												<mifos:mifosalphanumtext styleId="creategroup.input.address1" keyhm="Group.Address1" name="groupCustActionForm" property="address.line1" maxlength="200"/>
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<span id="creategroup.label.address2">
												<mifos:mifoslabel keyhm="Group.Address2" isColonRequired="Yes" name="${ConfigurationConstants.ADDRESS2}" />
												</span>
											</td>
											<td>
												<mifos:mifosalphanumtext styleId="creategroup.input.address2" keyhm="Group.Address2" name="groupCustActionForm" property="address.line2" maxlength="200"/>
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<span id="creategroup.label.address3">
												<mifos:mifoslabel keyhm="Group.Address3" isColonRequired="Yes" name="${ConfigurationConstants.ADDRESS3}" />
												</span>
											</td>
											<td>
												<mifos:mifosalphanumtext styleId="creategroup.input.address3" keyhm="Group.Address3" name="groupCustActionForm" property="address.line3" maxlength="200"/>
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<span id="creategroup.label.city">
												<mifos:mifoslabel keyhm="Group.City" isColonRequired="Yes" name="${ConfigurationConstants.CITY}" />
												</span>
											</td>
											<td>
												<mifos:mifosalphanumtext styleId="creategroup.input.city" keyhm="Group.City" name="groupCustActionForm" property="address.city" maxlength="100"/>
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<span id="creategroup.label.state">
												<mifos:mifoslabel keyhm="Group.State" isColonRequired="Yes" name="${ConfigurationConstants.STATE}" />
												</span>
											</td>
											<td>
												<mifos:mifosalphanumtext styleId="creategroup.input.state" keyhm="Group.State" name="groupCustActionForm" property="address.state" maxlength="100"/>
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<span id="creategroup.label.country">
												<mifos:mifoslabel keyhm="Group.Country" name="Group.country" bundle="GroupUIResources" />
												</span>
											</td>
											<td>
												<mifos:mifosalphanumtext styleId="creategroup.input.country" keyhm="Group.Country" name="groupCustActionForm" property="address.country" maxlength="100"/>
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<span id="creategroup.label.postalCode">
												<mifos:mifoslabel keyhm="Group.PostalCode" isColonRequired="Yes" name="${ConfigurationConstants.POSTAL_CODE}" />
												</span>
											</td>
											<td>
												<mifos:mifosalphanumtext styleId="creategroup.input.postalCode" keyhm="Group.PostalCode" name="groupCustActionForm" property="address.zip" maxlength="20"/>
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<span id="creategroup.label.telephone">
												<mifos:mifoslabel keyhm="Group.PhoneNumber" name="Group.telephone" bundle="GroupUIResources" />
												</span>
											</td>
											<td>
												<mifos:mifosalphanumtext styleId="creategroup.input.telephone" keyhm="Group.PhoneNumber" name="groupCustActionForm" property="address.phoneNumber" maxlength="20"/>
											</td>
										</tr>
									</table>


							<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
							<br>
									<!-- Custom Fields -->
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td colspan="2" class="fontnormalbold">
												<mifos:mifoslabel name="Group.additionalinformation" bundle="GroupUIResources"></mifos:mifoslabel>
												<br>
												<br>
											</td>
										</tr>
										<!-- For each custom field definition in the list custom field entity is passed as key to mifos label -->
										<c:forEach var="cf" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}" varStatus="loopStatus">
											<bean:define id="ctr">
												<c:out value="${loopStatus.index}" />
											</bean:define>
											<tr class="fontnormal">
												<td width="21%" align="right">
													<span id="creategroup.label.customField"><mifos:mifoslabel name="${cf.lookUpEntity.entityType}" mandatory="${cf.mandatoryStringValue}" isColonRequired="yes" bundle="GroupUIResources"/></span>
												</td>
												<td width="79%">
													<c:if test="${cf.fieldType == CustomFieldType.NUMERIC.value}">
														<mifos:mifosnumbertext styleId="creategroup.input.customField" name="groupCustActionForm" property='customField[${ctr}].fieldValue' maxlength="200" />
													</c:if>
													<c:if test="${cf.fieldType == CustomFieldType.ALPHA_NUMERIC.value}">
														<mifos:mifosalphanumtext styleId="creategroup.input.customField" name="groupCustActionForm" property='customField[${ctr}].fieldValue' maxlength="200" />
													</c:if>
													<c:if test="${cf.fieldType == CustomFieldType.DATE.value}">
														<date:datetag property="customField[${ctr}].fieldValue" />
													</c:if>
													<html-el:hidden property='customField[${ctr}].fieldId' value="${cf.fieldId}"/>
													<html-el:hidden property='customField[${ctr}].fieldType' value='${cf.fieldType}' />
												</td>
											</tr>
										</c:forEach>

									</table>

									<!--Custom Fields end  -->
									</c:if>
									<!-- Administrative Set Fees -->
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<c:if test="${!empty sessionScope.groupCustActionForm.defaultFees}">
										<tr>
											<td colspan="2" class="fontnormalbold">
												<mifos:mifoslabel name="Group.adminsetfess" bundle="GroupUIResources"></mifos:mifoslabel>
												<br>
												<br>
											</td>
										</tr>
										<c:forEach var="adminFees" items="${sessionScope.groupCustActionForm.defaultFees}" varStatus="loopStatus1">
											<bean:define id="ctr1" toScope="request">
												<c:out value="${loopStatus1.index}" />
											</bean:define>
											<tr>
												<td width="21%" align="right" class="fontnormal">
													<c:out value="${adminFees.feeName}" />:
												</td>
												<td width="79%" class="fontnormal">
													<table width="500" border="0" cellspacing="0" cellpadding="0">
															<!-- Fee amount display as label or text field -->
															<tr class="fontnormal">
																<td width="148">
																	<c:choose>
																		<c:when test="${adminFees.periodic == true}">
																			<c:out value="${adminFees.amount}" />
																		</c:when>
																		<c:otherwise>
																			<mifos:mifosdecimalinput styleId="creategroup.input.defaultAmount" property="defaultFee[${ctr1}].amount" value="${adminFees.amount}" style="width:135px;" />
																		</c:otherwise>
																	</c:choose>
																</td>
																<td width="182">
																	&nbsp;
																	<mifos:mifoslabel name="Group.periodicity" bundle="GroupUIResources" />
																	<c:choose>
																		<c:when test="${adminFees.periodic == true}">
																			<c:out value="${adminFees.feeSchedule}" />
																		</c:when>
																		<c:otherwise>
																			<mifos:mifoslabel name="Fees.onetime" />
																		</c:otherwise>
																	</c:choose>
																</td>
																<td width="170">
																	<html-el:checkbox styleId="creategroup.input.remove" property="defaultFee[${ctr1}].feeRemoved" value="1"></html-el:checkbox>
																	<span id="creategroup.label.remove"><mifos:mifoslabel name="Group.checktoremove" bundle="GroupUIResources"/></span>
																</td>
															</tr>
														</table>
												</td>
											</tr>
										</c:forEach>
										</c:if>
									</table>

									<!-- Administrative Set Fees End-->
									<br>
									<!-- Additional Fees Start-->
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td colspan="4" class="fontnormalbold">
												<mifos:mifoslabel name="Group.additionalfees" bundle="GroupUIResources"></mifos:mifoslabel>
												<br>
												<br>
											</td>
										</tr>

										<c:forEach begin="0" end="2" step="1" varStatus="loopStatus2">
											<tr>
												<bean:define id="ctr2" toScope="request">
													<c:out value="${loopStatus2.index}" />
												</bean:define>
												<td align="right" class="fontnormal" width="21%">
													<mifos:mifoslabel name="Group.feetype" bundle="GroupUIResources"></mifos:mifoslabel>
												</td>
												<td class="fontnormal" width="17%">
													<mifos:select name="groupCustActionForm" property='selectedFee[${ctr2}].feeId' onchange="displayAmount('selectedFee[${ctr2}].feeId', 'selectedFee[${ctr2}].amount' )">
														<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'additionalFeeList')}" var="additionalFee">
															<html-el:option value="${additionalFee.feeId}">${additionalFee.feeName}</html-el:option>
														</c:forEach>
													</mifos:select>
												</td>
												<td align="right" width="12%" class="fontnormal">
													<mifos:mifoslabel name="Group.amount" bundle="GroupUIResources"></mifos:mifoslabel>
												</td>
												<td class="fontnormal" width="50%">
													<mifos:mifosdecimalinput styleId="creategroup.input.amount" property='selectedFee[${ctr2}].amount' />
												</td>

												<c:if test="${ctr2 == 0}">
													<c:forEach var="fee" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'additionalFeeList')}" varStatus="loopStatus3">
														<bean:define id="ctr3" toScope="request">
															<c:out value="${loopStatus3.index}" />
														</bean:define>
														<html-el:hidden property='selectedFeeAmntList' value='${fee.amount}' />
													</c:forEach>
												</c:if>
											</tr>
										</c:forEach>

									</table>

									<!-- Additional Fees End-->


									<table width="93%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center" class="blueline">
												&nbsp;
											</td>
										</tr>
									</table>
									<br>
									<table width="93%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center">
												<html-el:submit styleId="creategroup.button.preview" styleClass="buttn" >
													<mifos:mifoslabel name="button.preview" bundle="GroupUIResources"></mifos:mifoslabel>
												</html-el:submit>
												&nbsp;&nbsp;
												<html-el:button styleId="creategroup.button.cancel" property="cancelBtn" styleClass="cancelbuttn" onclick="goToCancelPage()">
													<mifos:mifoslabel name="button.cancel" bundle="GroupUIResources"></mifos:mifoslabel>
												</html-el:button>
											</td>
										</tr>
									</table>
									<br>
								</td>
							</tr>
						</table>
						<br>
					</td>
				</tr>
			</table>
			<html-el:hidden property="input" value="createGroup" />
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>

	</tiles:put>
</tiles:insert>

