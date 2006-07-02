<!-- /**
 
 * createnewgroup.jsp    version: 1.0
 
 
 
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
<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">

		<script language="javascript">

  function goToCancelPage(){
	groupActionForm.action="GroupAction.do?method=cancel";
	groupActionForm.submit();
  }	
  		
  function displayAmount(listBox, textBox ){
	var comboBox = document.getElementsByName(listBox)[0];
	var amountField = document.getElementsByName(textBox)[0];
	if(comboBox.selectedIndex==0)
		amountField.value = "";
	else{
		var indexSelectedFee = comboBox.selectedIndex-1;
		if (groupActionForm.selectedFeeAmntList[indexSelectedFee]!= undefined){
			var amount=groupActionForm.selectedFeeAmntList[indexSelectedFee].value;
			amountField.value = amount;
		}
		else{
			amountField.value=groupActionForm.selectedFeeAmntList.value;
		}
    }
  }
    function loadMeeting(){
	    groupActionForm.action="GroupAction.do?method=loadMeeting";
	    groupActionForm.submit();
    }
    
    function populateDefaultFormedBy(selectBox)
  	{
  		if(selectBox.selectedIndex > 0)
  		{
		  groupActionForm.action="GroupAction.do?method=setDefaultFormedByPersonnel";
		  groupActionForm.submit();		
		}
	}
    
    function chkForValidDates(){
		
	  		var trainedDate = document.getElementById("trainedDate");	  
	  	 	var trainedDateFormat = document.getElementById("trainedDateFormat");	  
	  		var trainedDateYY = document.getElementById("trainedDateYY");	  
			if(! (validateMyForm(trainedDate,trainedDateFormat,trainedDateYY)))
				return false;
	 		 	  	
			for(var i=0; i <=groupActionForm.fieldTypeList.length;i++){
			if (groupActionForm.fieldTypeList[i]!= undefined){
				if(groupActionForm.fieldTypeList[i].value == "3"){
					var customFieldDate = document.getElementById("customField["+i+"].fieldValue");
					var customFieldDateFormat = document.getElementById("customField["+i+"].fieldValueFormat");	  
			  	 	var customFieldDateYY = document.getElementById("customField["+i+"].fieldValueYY");	  
					var dateValue = customFieldDate.value;
					if(!(validateMyForm(customFieldDate,customFieldDateFormat,customFieldDateYY)))
						return false;
				}
			}
	 	}
	  }
  
</script>
		<SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>
		<html-el:form action="GroupAction.do?method=preview" onsubmit="return chkForValidDates()">
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
																<c:when test="${sessionScope.CenterHierarchyExist eq 'Yes'}">
																	<mifos:mifoslabel name="Group.select" bundle="GroupUIResources"></mifos:mifoslabel>
																	<mifos:mifoslabel name="${ConfigurationConstants.CENTER}"></mifos:mifoslabel>
																</c:when>
																<c:otherwise>
																	<mifos:mifoslabel name="Group.choosebranch" bundle="GroupUIResources"></mifos:mifoslabel>
																	<mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}"></mifos:mifoslabel>
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
															<mifos:mifoslabel name="${ConfigurationConstants.GROUP}"></mifos:mifoslabel>
															<mifos:mifoslabel name="Group.groupinformation" bundle="GroupUIResources"></mifos:mifoslabel>
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
												<span class="heading"> <mifos:mifoslabel name="Group.createnew" bundle="GroupUIResources"></mifos:mifoslabel> <mifos:mifoslabel name="${ConfigurationConstants.GROUP}"></mifos:mifoslabel> - </span>
												<mifos:mifoslabel name="Group.enter" bundle="GroupUIResources"></mifos:mifoslabel>
												<mifos:mifoslabel name="${ConfigurationConstants.GROUP}"></mifos:mifoslabel>
												<mifos:mifoslabel name="Group.groupinformation" bundle="GroupUIResources"></mifos:mifoslabel>
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
													<font class="fontnormalRedBold"><html-el:errors bundle="GroupUIResources" /></font>
												</td>
											</tr>
										</table>
									</logic:messagesPresent>
									<c:if test="${sessionScope.CenterHierarchyExist eq 'Yes'}">
										<br>
										<table width="93%" border="0" cellspacing="0" cellpadding="3">
											<tr>
												<td>
													<span class="fontnormalbold"> <mifos:mifoslabel name="Group.loanofficerassigned" bundle="GroupUIResources"></mifos:mifoslabel> </span> <span class="fontnormal"> <c:out value="${requestScope.GroupParent.personnel.displayName}" /> <html-el:hidden
															property="loanOfficerId" value="${requestScope.GroupParent.personnel.personnelId}" /> <br> </span> <span class="fontnormalbold"> <mifos:mifoslabel name="${ConfigurationConstants.CENTER}">
														</mifos:mifoslabel> <mifos:mifoslabel name="Group.assigned" bundle="GroupUIResources"></mifos:mifoslabel> </span> <span class="fontnormal"> <c:out value="${requestScope.GroupParent.displayName}" /><br> </span><span class="fontnormalbold"> <mifos:mifoslabel
															name="Group.meetingschedule" bundle="GroupUIResources"></mifos:mifoslabel> </span> <span class="fontnormal"> <c:out value="${requestScope.GroupParent.customerMeeting.meeting.meetingSchedule}" /> <br> </span> <span class="fontnormalbold"> <mifos:mifoslabel
															name="Group.locationofthemeeting" bundle="GroupUIResources"></mifos:mifoslabel> </span> <span class="fontnormal"> <c:out value="${requestScope.GroupParent.customerMeeting.meeting.meetingPlace}" /> <br> </span>
												</td>
											</tr>
										</table>
									</c:if>
									<br>
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td colspan="2" class="fontnormalbold">
												<mifos:mifoslabel name="${ConfigurationConstants.GROUP}">
												</mifos:mifoslabel>
												<mifos:mifoslabel name="Group.details" bundle="GroupUIResources"></mifos:mifoslabel>
												<br>
												<br>
											</td>
										</tr>
										<tr class="fontnormal">
											<td width="21%" align="right">
												<mifos:mifoslabel name="Group.groupname" mandatory="yes" bundle="GroupUIResources"></mifos:mifoslabel>
											</td>
											<td width="79%">
												<mifos:mifosalphanumtext property="displayName" />
											</td>
										</tr>

										<%-- Show following only if center hierarchy does not exists --%>

										<c:if test="${sessionScope.CenterHierarchyExist eq 'No'}">
											<tr class="fontnormal">
												<td align="right" class="fontnormal">
													<mifos:mifoslabel name="Group.loanofficer" bundle="GroupUIResources"></mifos:mifoslabel>
												</td>
												<td>
													<mifos:select name="groupActionForm" property="loanOfficerId" size="1" onchange="populateDefaultFormedBy(this)">
														<html-el:options collection="loanOfficers" property="personnelId" labelProperty="displayName" />
													</mifos:select>
												</td>
											</tr>
											<%--
                <tr class="fontnormal">
                  <td align="right" class="fontnormal">
                  	<mifos:mifoslabel name="Group.locationofthemeeting" bundle="GroupUIResources"></mifos:mifoslabel>
                  </td>
                  <td>
					<mifos:mifosalphanumtext name="groupActionForm" property="customerMeeting.meetingPlace"/>
                  </td>
                </tr>
				--%>
											<tr class="fontnormal">
												<td align="right" class="fontnormal">
													<mifos:mifoslabel name="Group.meetingschedule" bundle="GroupUIResources"></mifos:mifoslabel>
												</td>
												<td>
													<html-el:link href="javascript:loadMeeting()">
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
												<mifos:select property="customerFormedById" style="width:136px;">
													<c:forEach var="customerFormedBy" items="${requestScope.formedByLoanOfficers}">
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
												<date:datetag keyhm="Group.TrainedDate" property="trainedDate" />
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
															<mifos:mifosalphanumtext keyhm="Group.ExternalId" property="externalId" />
														</td>
														<td width="76%" class="fontnormal8pt">
															<mifos:mifoslabel keyhm="Group.ExternalId" name="Center.ExternalIdInfo" bundle="CenterUIResources"/>
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<%--  programs 
                <tr class="fontnormal">
                  <td align="right" valign="top" class="fontnormal">
                  <mifos:mifoslabel name="Group.programs" bundle="GroupUIResources"></mifos:mifoslabel>
                  </td>
                  <td>
                  <table width="80%" border="0" cellspacing="0" cellpadding="0">
                      <tr>
                        <td class="fontnormal">
                           <mifos:mifoslabel name="Group.programMsg" bundle="GroupUIResources"></mifos:mifoslabel>
                         </td>
                      </tr>
                      <tr>
                        <td><img src="pages/framework/images/trans.gif" width="10" height="10"></td>
                      </tr>
                   </table>                    
                   
                   <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td>
                     <mifos:MifosSelect property="programs"  inputMap="programsMap"  outputMap="customerProgramsMap" multiple="true">
					  </mifos:MifosSelect>  

                      </td>
                    </tr>
                  </table></td>
                </tr> --%>
										<%-- Collection Sheet
                <tr class="fontnormal">
                  <td align="right" class="fontnormal">
                  <mifos:mifoslabel name="Group.collectionsheettype" bundle="GroupUIResources"></mifos:mifoslabel>
                  
                  </td>
                  <td>
                	  <html-el:select name="groupActionForm" property="collectionSheetId" size="1" >
							<html-el:options collection="collectionSheets" property="collectionSheetId" labelProperty="collectionSheetName"/>
					  </html-el:select>
				  </td>
                </tr>
		--%>

									</table>

									<br>
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td colspan="2" class="fontnormalbold">
												<mifos:mifoslabel name="Group.address" bundle="GroupUIResources"></mifos:mifoslabel>
												<br>
												<br>
											</td>
										</tr>
										<tr class="fontnormal">
											<td width="21%" align="right">
												<mifos:mifoslabel keyhm="Group.Address1" isColonRequired="Yes" name="${ConfigurationConstants.ADDRESS1}" />
											</td>
											<td width="79%">
												<mifos:mifosalphanumtext keyhm="Group.Address1" name="groupActionForm" property="customerAddressDetail.line1" />
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel keyhm="Group.Address2" isColonRequired="Yes" name="${ConfigurationConstants.ADDRESS2}" />
											</td>
											<td>
												<mifos:mifosalphanumtext keyhm="Group.Address2" name="groupActionForm" property="customerAddressDetail.line2" />
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel keyhm="Group.Address3" isColonRequired="Yes" name="${ConfigurationConstants.ADDRESS3}" />
											</td>
											<td>
												<mifos:mifosalphanumtext keyhm="Group.Address3" name="groupActionForm" property="customerAddressDetail.line3" />
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel keyhm="Group.City" isColonRequired="Yes" name="${ConfigurationConstants.CITY}" />
											</td>
											<td>
												<mifos:mifosalphanumtext keyhm="Group.City" name="groupActionForm" property="customerAddressDetail.city" />
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel keyhm="Group.State" isColonRequired="Yes" name="${ConfigurationConstants.STATE}" />
											</td>
											<td>
												<mifos:mifosalphanumtext keyhm="Group.State" name="groupActionForm" property="customerAddressDetail.state" />
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel keyhm="Group.Country" name="Group.country" bundle="GroupUIResources" />
											</td>
											<td>
												<mifos:mifosalphanumtext keyhm="Group.Country" name="groupActionForm" property="customerAddressDetail.country" />
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel keyhm="Group.PostalCode" isColonRequired="Yes" name="${ConfigurationConstants.POSTAL_CODE}" />
											</td>
											<td>
												<mifos:mifosalphanumtext keyhm="Group.PostalCode" name="groupActionForm" property="customerAddressDetail.zip" />
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel keyhm="Group.PhoneNumber" name="Group.telephone" bundle="GroupUIResources" />
											</td>
											<td>
												<mifos:mifosalphanumtext keyhm="Group.PhoneNumber" name="groupActionForm" property="customerAddressDetail.phoneNumber" />
											</td>
										</tr>
									</table>
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
										<c:forEach var="cf" items="${requestScope.customFields}" varStatus="loopStatus">
											<bean:define id="ctr">
												<c:out value="${loopStatus.index}" />
											</bean:define>
											<tr class="fontnormal">
												<td width="21%" align="right">
													<mifos:mifoslabel name="${cf.lookUpEntity.entityType}" mandatory="${cf.mandatoryStringValue}" bundle="GroupUIResources"></mifos:mifoslabel>
													:
												</td>
												<td width="79%">
													<c:if test="${cf.fieldType == 1}">
														<mifos:mifosnumbertext name="groupActionForm" property='customField[${ctr}].fieldValue' maxlength="200" />
													</c:if>
													<c:if test="${cf.fieldType == 2}">
														<mifos:mifosalphanumtext name="groupActionForm" property='customField[${ctr}].fieldValue' maxlength="200" />
													</c:if>
													<c:if test="${cf.fieldType == 3}">
														<date:datetag property="customField[${ctr}].fieldValue" />
													</c:if>
													<html-el:hidden property='customField[${ctr}].fieldId' value="${cf.fieldId}"></html-el:hidden>
													<html-el:hidden property='fieldTypeList' value='${cf.fieldType}' />
												</td>
											</tr>
										</c:forEach>

									</table>

									<!--Custom Fields end  -->
									<br>
									<!-- Administrative Set Fees -->
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td colspan="2" class="fontnormalbold">
												<mifos:mifoslabel name="Group.adminsetfess" bundle="GroupUIResources"></mifos:mifoslabel>
												<br>
												<br>
											</td>
										</tr>
										<c:forEach var="adminFees" items="${requestScope.adminFeesList}" varStatus="loopStatus1">
											<bean:define id="ctr1" toScope="request">
												<c:out value="${loopStatus1.index}" />
											</bean:define>
											<tr>
												<td width="21%" align="right" class="fontnormal">
													<c:out value="${adminFees.feeName}" />
													:
												</td>
												<td width="79%" class="fontnormal">
													<table width="500" border="0" cellspacing="0" cellpadding="0">
														<tr class="fontnormal">
															<c:choose>
																<c:when test="${adminFees.feeFrequencyTypeId == 1}">
																	<td width="148">
																		<c:out value="${adminFees.rateOrAmount}" />
																		<html-el:hidden property="adminFee[${ctr1}].rateOrAmount" value="${adminFees.rateOrAmount}" />
																		<html-el:hidden property="adminFee[${ctr1}].feeId" value="${adminFees.feeId}"></html-el:hidden>
																		<html-el:hidden property="adminFee[${ctr1}].feeName" value="${adminFees.feeName}"></html-el:hidden>
																	</td>
																</c:when>
																<c:otherwise>
																	<c:choose>
																		<c:when test="${param.method eq 'load' or param.method eq 'hierarchyCheck'}">
																			<td width="148">
																				<mifos:mifosdecimalinput property="adminFee[${ctr1}].rateOrAmount" value="${adminFees.rateOrAmount}" style="width:135px;" />
																				<html-el:hidden property="adminFee[${ctr1}].feeId" value="${adminFees.feeId}"></html-el:hidden>
																				<html-el:hidden property="adminFee[${ctr1}].feeName" value="${adminFees.feeName}"></html-el:hidden>
																			</td>
																		</c:when>
																		<c:otherwise>
																			<td width="148">
																				<mifos:mifosdecimalinput property="adminFee[${ctr1}].rateOrAmount" style="width:135px;" />
																				<html-el:hidden property="adminFee[${ctr1}].feeId" value="${adminFees.feeId}"></html-el:hidden>
																				<html-el:hidden property="adminFee[${ctr1}].feeName" value="${adminFees.feeName}"></html-el:hidden>
																			</td>
																		</c:otherwise>
																	</c:choose>
																</c:otherwise>
															</c:choose>

															<td width="182">
																&nbsp;
																<mifos:mifoslabel name="Group.periodicity" bundle="GroupUIResources" />
																<c:choose>
																	<c:when test="${adminFees.feeFrequencyTypeId == 1}">
																		<c:out value="${adminFees.feeMeeting.feeMeetingSchedule}" />
																	</c:when>
																	<c:otherwise>
																		<mifos:mifoslabel name="Fees.onetime" />
																	</c:otherwise>
																</c:choose>
															</td>
															<td width="170">
																<html-el:checkbox property="adminFee[${ctr1}].checkedFee" value="1"></html-el:checkbox>
																<mifos:mifoslabel name="Group.checktoremove" bundle="GroupUIResources"></mifos:mifoslabel>
															</td>
															<c:out value="${adminFees.checkedFee}" />
														</tr>
													</table>
												</td>
											</tr>
										</c:forEach>
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
													<mifos:select name="groupActionForm" property='selectedFee[${ctr2}].feeId' onchange="displayAmount('selectedFee[${ctr2}].feeId', 'selectedFee[${ctr2}].rateOrAmount' )">
														<html-el:options collection="feesList" property="feeId" labelProperty="feeName"></html-el:options>
													</mifos:select>
												</td>
												<td align="right" width="12%" class="fontnormal">
													<mifos:mifoslabel name="Group.amount" bundle="GroupUIResources"></mifos:mifoslabel>
												</td>
												<td class="fontnormal" width="50%">
													<mifos:mifosdecimalinput property='selectedFee[${ctr2}].rateOrAmount' />
												</td>

												<c:if test="${ctr2 == 0}">
													<c:forEach var="fee" items="${requestScope.feesList}" varStatus="loopStatus3">
														<bean:define id="ctr3" toScope="request">
															<c:out value="${loopStatus3.index}" />
														</bean:define>
														<html-el:hidden property='selectedFeeAmntList' value='${fee.rateOrAmount}' />
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
												<html-el:submit styleClass="buttn" style="width:70px;">
													<mifos:mifoslabel name="button.preview" bundle="GroupUIResources"></mifos:mifoslabel>
												</html-el:submit>
												&nbsp;&nbsp;
												<html-el:button property="cancelBtn" styleClass="cancelbuttn" style="width:70px" onclick="goToCancelPage()">
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
			<html-el:hidden property="input" value="CreateNewGroup" />
		</html-el:form>

	</tiles:put>
</tiles:insert>

