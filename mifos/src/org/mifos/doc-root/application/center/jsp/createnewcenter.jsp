<!--
/**

* createCenter.jsp    version: 1.0



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
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-bean-el" prefix="bean-el"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<!-- Tile  definitions -->
<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
		<script language="javascript" SRC="pages/framework/js/date.js"></script>
		<script src="pages/framework/js/conversion.js"></script>
		<script src="pages/framework/js/con_en.js"></script>
		<script src="pages/framework/js/con_${sessionScope["UserContext"].currentLocale}.js"></script>
		<script language="javascript">


  function goToCancelPage(){
	centerCustActionForm.action="centerCustAction.do?method=cancel";
	centerCustActionForm.submit();
  }

  //Function to display the fee amount in the text box on the selection of a particualar fee from the combo box
	function displayAmount(listBox, textBox ){
		//The fee combo box
		var comboBox = document.getElementsByName(listBox)[0];

		//The amount text box
		var amountField = document.getElementsByName(textBox)[0];

		//If no fee is selected then the amount field displays nothing
		if(comboBox.selectedIndex==0)
			amountField.value = "";
		else{
			var indexSelectedFee = comboBox.selectedIndex-1;
			if (centerCustActionForm.selectedFeeAmntList[indexSelectedFee]!= undefined){
				//obtaining the fee amount for a particular fee
				var amount=centerCustActionForm.selectedFeeAmntList[indexSelectedFee].value;
				//assigning the value of the fee amount to the text box
				amountField.value = amount;
			}
			else{
				amountField.value=centerCustActionForm.selectedFeeAmntList.value;
			}

		}
	}
	function chkForValidDates(){

	  		var mfiJoiningDate = document.getElementById("mfiJoiningDate");
	  	 	var mfiJoiningDateFormat = document.getElementById("mfiJoiningDateFormat");
	  		var mfiJoiningDateYY = document.getElementById("mfiJoiningDateYY");
			if(! (validateMyForm(mfiJoiningDate,mfiJoiningDateFormat,mfiJoiningDateYY)))
				return false;
	  }
	function loadMeeting(){
	    centerCustActionForm.action="centerCustAction.do?method=loadMeeting";
	    centerCustActionForm.submit();
    }

		</script>
		<html-el:form action="centerCustAction.do?method=preview" onsubmit="return chkForValidDates()">
			<!-- Hidden varaibles for the locale and for the input page -->
			<html-el:hidden property="input" value="create" />
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
						<!-- Pipeline Bar -->
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
															<mifos:mifoslabel name="Center.Choose" bundle="CenterUIResources" />
															<mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}" />
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
															<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
															<c:out value=" " />
															<mifos:mifoslabel name="Center.Information" bundle="CenterUIResources" />
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
															<mifos:mifoslabel name="Center.ReviewSubmit" bundle="CenterUIResources" />
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
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td class="headingorange">
												<span class="heading"><mifos:mifoslabel name="Center.CreateNew" bundle="CenterUIResources" /> <mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /><c:out value=" " /> <mifos:mifoslabel name="Center.dash" bundle="CenterUIResources" />
												</span>
												<mifos:mifoslabel name="Center.Enter" bundle="CenterUIResources" />
												<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
												<c:out value=" " />
												<mifos:mifoslabel name="Center.Information" bundle="CenterUIResources" />
											</td>
										</tr>
										<!-- Instructions for the user -->
										<tr>
											<td class="fontnormal">
												<mifos:mifoslabel name="Center.CreatePageInstruction" bundle="CenterUIResources" />
												<mifos:mifoslabel name="Center.CreatePageCancelInstruction" bundle="CenterUIResources" />
												<br>
												<span class="mandatorytext"><font color="#FF0000">*</font></span>
												<mifos:mifoslabel name="Center.FieldInstruction" bundle="CenterUIResources" />
												<br>
											</td>
										</tr>
									</table>
									<logic:messagesPresent>
										<table width="93%" border="0" cellpadding="3" cellspacing="0">
											<!-- Error Display if any -->
											<tr>
												<td>
													<font class="fontnormalRedBold"><html-el:errors bundle="CenterUIResources" /></font>
												</td>
											</tr>
										</table>
									</logic:messagesPresent>
									<!-- Displaying the selected office name -->
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td colspan="2" class="fontnormal">
												<br>
												<span class="fontnormalbold">
												<mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}" /><c:out value=" " /> <mifos:mifoslabel name="Center.Selected" bundle="CenterUIResources" isColonRequired="yes"/>
												</span>
												<c:out value="${sessionScope.centerCustActionForm.officeName}" />
												<br>
												<br>
											</td>
										</tr>

										<tr>
											<td colspan="2" class="fontnormalbold">
												<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
												<c:out value=" " />
												<mifos:mifoslabel name="Center.details" bundle="CenterUIResources" />
												<br>
												<br>
											</td>
										</tr>
										<!-- Center Name -->
										<tr class="fontnormal">
											<td width="21%" align="right">
												<mifos:mifoslabel name="Center.Name" mandatory="yes" bundle="CenterUIResources" />
											</td>
											<td>
												<mifos:mifosalphanumtext property="displayName" maxlength="200" />
											</td>
										</tr>
										<!-- Loan Officer Name -->
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel name="Center.LoanOfficer" mandatory="yes" bundle="CenterUIResources" />
											</td>
											<td>
												<mifos:select property="loanOfficerId" size="1">
													<c:forEach var="loanOfficersList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanOfficers')}" >
														<html-el:option value="${loanOfficersList.personnelId}">${loanOfficersList.displayName}</html-el:option>
													</c:forEach>
												</mifos:select>
											</td>
										</tr>
										<!-- Meeting schedule -->
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel name="Center.MeetingSchedule" mandatory="yes" bundle="CenterUIResources" />
											</td>
											<td>
												<html-el:link href="javascript:loadMeeting()">
													<mifos:mifoslabel name="Center.MeetingScheduleLink" bundle="CenterUIResources" />
												</html-el:link>
											</td>
										</tr>
										<!-- External Id -->
										<tr class="fontnormal">
											<td align="right" class="fontnormal">
												<mifos:mifoslabel keyhm="Center.ExternalId" isColonRequired="Yes" name="${ConfigurationConstants.EXTERNALID}" />
											</td>
											<td>
												<table width="95%" border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td width="24%">
															<mifos:mifosalphanumtext keyhm="Center.ExternalId" property="externalId" maxlength="50" />
														</td>
														<td width="76%" class="fontnormal8pt">
															<mifos:mifoslabel keyhm="Center.ExternalId" name="Center.ExternalIdInfo" bundle="CenterUIResources" />
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<!-- MFI Joining Date -->
										<tr class="fontnormal">
											<td align="right" class="fontnormal">
												<mifos:mifoslabel name="Center.MfiJoiningDate" mandatory="yes" bundle="CenterUIResources" isColonRequired="yes"/>
											</td>
											<td valign="top">
												<date:datetag property="mfiJoiningDate" renderstyle="simple"/>
											</td>
										</tr>

									</table>
									<br>
									<!-- Address -->

									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td colspan="2" class="fontnormalbold">
												<mifos:mifoslabel name="Center.AddressHeading" bundle="CenterUIResources" />
												<br>
												<br>
											</td>
										</tr>
										<!-- Line 1 of address -->
										<tr class="fontnormal">
											<td align="right" width="21%" class="fontnormal">
												<mifos:mifoslabel keyhm="Center.Address1" isColonRequired="Yes" name="${ConfigurationConstants.ADDRESS1}" />
											</td>
											<td width="79%">
												<mifos:mifosalphanumtext keyhm="Center.Address1" name="centerCustActionForm" property="address.line1" maxlength="200" />
											</td>
										</tr>

										<!-- Line 2 of address -->

										<tr class="fontnormal">
											<td align="right" class="fontnormal">
												<mifos:mifoslabel keyhm="Center.Address2" isColonRequired="Yes" name="${ConfigurationConstants.ADDRESS2}" />
											</td>
											<td>
												<mifos:mifosalphanumtext keyhm="Center.Address2" name="centerCustActionForm" property="address.line2" maxlength="200" />
											</td>
										</tr>
										<!-- Line 3 of address -->
										<tr class="fontnormal">
											<td align="right" class="fontnormal">
												<mifos:mifoslabel keyhm="Center.Address3" isColonRequired="Yes" name="${ConfigurationConstants.ADDRESS3}" />
											</td>
											<td>
												<mifos:mifosalphanumtext keyhm="Center.Address3" name="centerCustActionForm" property="address.line3" maxlength="200" />
											</td>
										</tr>
										<!-- City of address -->
										<tr class="fontnormal">
											<td align="right" class="fontnormal">
												<mifos:mifoslabel keyhm="Center.City" isColonRequired="Yes" name="${ConfigurationConstants.CITY}" />
											</td>
											<td>
												<mifos:mifosalphanumtext keyhm="Center.City" name="centerCustActionForm" property="address.city" maxlength="100" />
											</td>
										</tr>
										<!-- State of address -->
										<tr class="fontnormal">
											<td align="right" class="fontnormal">
												<mifos:mifoslabel keyhm="Center.State" isColonRequired="Yes" name="${ConfigurationConstants.STATE}" />
											</td>
											<td>
												<mifos:mifosalphanumtext keyhm="Center.State" name="centerCustActionForm" property="address.state" maxlength="100" />
											</td>
										</tr>
										<!-- Country of address -->
										<tr class="fontnormal">

											<td align="right" class="fontnormal">
												<mifos:mifoslabel keyhm="Center.Country" name="Center.Country" bundle="CenterUIResources" />
											</td>
											<td>
												<mifos:mifosalphanumtext keyhm="Center.Country" name="centerCustActionForm" property="address.country" maxlength="100" />
											</td>
										</tr>
										<!-- Postal Code of address -->
										<tr class="fontnormal">
											<td align="right" class="fontnormal">
												<mifos:mifoslabel keyhm="Center.PostalCode" name="${ConfigurationConstants.POSTAL_CODE}" isColonRequired="yes"/>
											</td>
											<td>
												<mifos:mifosalphanumtext keyhm="Center.PostalCode" name="centerCustActionForm" property="address.zip" maxlength="20" />
											</td>
										</tr>
										<!-- Telephone of address -->
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel keyhm="Center.PhoneNumber" name="Center.Telephone" bundle="CenterUIResources" />
											</td>
											<td>
												<mifos:mifosalphanumtext keyhm="Center.PhoneNumber" name="centerCustActionForm" property="address.phoneNumber" maxlength="20" />
											</td>
										</tr>
									</table>
									
									<!-- Address end -->
									<!-- Custom Fields -->
									<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
									<br>
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td colspan="2" class="fontnormalbold">
												<mifos:mifoslabel name="Center.AdditionalInformationHeading" bundle="CenterUIResources" />
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
													<mifos:mifoslabel name="${cf.lookUpEntity.entityType}" mandatory="${cf.mandatoryStringValue}" bundle="CenterUIResources" isColonRequired="yes"/>
												</td>
												<td width="79%">
													<html-el:hidden property='customField[${ctr}].fieldId' value="${cf.fieldId}"></html-el:hidden>
													<html-el:hidden property='fieldTypeList' value='${cf.fieldType}' />
													<c:if test="${cf.fieldType == CustomFieldType.NUMERIC.value}">
														<mifos:mifosnumbertext name="centerCustActionForm" property='customField[${ctr}].fieldValue' maxlength="200" />
													</c:if>
													<c:if test="${cf.fieldType == CustomFieldType.ALPHA_NUMERIC.value}">
														<mifos:mifosalphanumtext name="centerCustActionForm" property='customField[${ctr}].fieldValue' maxlength="200" />
													</c:if>
													<c:if test="${cf.fieldType == CustomFieldType.DATE.value}">
														<date:datetag property="customField[${ctr}].fieldValue" />
													</c:if>
												</td>
											</tr>
										</c:forEach>
									</table>
									<br>
									</c:if>
									<!--Custom Fields end  -->
									<!-- Administrative Set Fees -->
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<c:if test="${!empty sessionScope.centerCustActionForm.defaultFees}">
										  <tr>
											<td colspan="2" class="fontnormalbold">
												<mifos:mifoslabel name="Center.AdministrativeFeesHeading" bundle="CenterUIResources" />
												<br>
												<br>
											</td>
										  </tr>
											<!-- For each admin fee that is retrieved the name and amoutn is displayed -->
											<c:forEach var="adminFees" items="${sessionScope.centerCustActionForm.defaultFees}" varStatus="loopStatus1">
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
																			<mifos:mifosdecimalinput property="defaultFee[${ctr1}].amount" value="${adminFees.amount}" style="width:135px;" />
																		</c:otherwise>
																	</c:choose>
																</td>
																<td width="182">
																	&nbsp;
																	<mifos:mifoslabel name="Center.Periodicity" bundle="CenterUIResources" />
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
																	<html-el:checkbox property="defaultFee[${ctr1}].feeRemoved" value="1"></html-el:checkbox>
																	Check to Remove
																</td>
															</tr>
														</table>
													</td>
												</tr>
											</c:forEach>
										</c:if>
									</table>
									<br>
									<!-- Administrative Set Fees End-->
									<!-- Fee Type -->
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td colspan="4" class="fontnormalbold">
												<mifos:mifoslabel name="Center.AdditionalFeesHeading" bundle="CenterUIResources" />
												<br>
												<br>
											</td>
										</tr>
										<tr>
										<c:forEach begin="0" end="2" step="1" varStatus="loopStatus2">
											<bean:define id="ctr2" toScope="request">
												<c:out value="${loopStatus2.index}" />
											</bean:define>
											<td width="21%" align="right" class="fontnormal">
												<mifos:mifoslabel name="Center.FeeType" bundle="CenterUIResources" />
											</td>
											<td width="17%" class="fontnormal">
												<mifos:select name="centerCustActionForm" property='selectedFee[${ctr2}].feeId' onchange="displayAmount('selectedFee[${ctr2}].feeId', 'selectedFee[${ctr2}].amount' )">
													<c:forEach var="feeList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'additionalFeeList')}" >
														<html-el:option value="${feeList.feeId}">${feeList.feeName}</html-el:option>
													</c:forEach>
												</mifos:select>
											</td>
											<td width="12%" align="right" class="fontnormal">
												<mifos:mifoslabel name="Center.Amount" bundle="CenterUIResources" />
											</td>
											<td width="50%" class="fontnormal">
												<mifos:mifosdecimalinput property='selectedFee[${ctr2}].amount' />
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
									<!-- Fees End -->
									<br>
									<!-- Buttons -->
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
													<mifos:mifoslabel name="button.preview" bundle="CenterUIResources" />
												</html-el:submit>
												&nbsp; &nbsp;
												<html-el:button onclick="goToCancelPage();" property="cancelButton" styleClass="cancelbuttn" style="width:70px">
													<mifos:mifoslabel name="button.cancel" bundle="CenterUIResources" />
												</html-el:button>
											</td>
										</tr>
									</table>
									<!-- Button end -->
									<br>
									<!-- before main closing -->
								</td>
							</tr>
						</table>
						<br>
					</td>
				</tr>
			</table>
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
