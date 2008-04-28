<!--
/**

* createClientMfiInfo.jsp    version: 1.0



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
<%@taglib uri="http://struts.apache.org/tags-bean-el" prefix="bean-el"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.ClientUIResources"/>

<!-- Tile  definitions -->
<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
		<script language="javascript" SRC="pages/framework/js/date.js"></script>
		<script src="pages/framework/js/conversion.js"></script>
		<script src="pages/framework/js/con_en.js"></script>
		<script src="pages/framework/js/con_${sessionScope["UserContext"].currentLocale}.js"></script>
		<script language="javascript">


  function goToCancelPage(){
	clientCustActionForm.action="clientCustAction.do?method=cancel";
	clientCustActionForm.submit();
  }

  function populateDefaultFormedBy(selectBox)
  {
  		document.forms["clientCustActionForm"].elements["formedByPersonnel"].value=document.forms["clientCustActionForm"].elements["loanOfficerId"].value;
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
			if (clientCustActionForm.selectedFeeAmntList[indexSelectedFee]!= undefined){
				//obtaining the fee amount for a particular fee
				var amount=clientCustActionForm.selectedFeeAmntList[indexSelectedFee].value;
				//assigning the value of the fee amount to the text box
				amountField.value = amount;
			}
			else{
				amountField.value=clientCustActionForm.selectedFeeAmntList.value;
			}

		}
	}
	function loadMeeting(){
	    clientCustActionForm.action="clientCustAction.do?method=loadMeeting";
	    clientCustActionForm.submit();
    }
</script>
		<html-el:form action="clientCustAction.do?method=preview">
			<!-- Hidden varaibles for the locale and for the input page -->
			<html-el:hidden property="input" value="mfiInfo" />
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="350" align="left" valign="top" bgcolor="#FFFFFF">
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td align="center" class="heading">&nbsp;</td>
						</tr>
					</table>
					<!-- Pipeline Bar -->
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td class="bluetablehead">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td width="25%">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/tick.gif"
												width="17" height="17"></td>
											<td class="timelineboldgray"><mifos:mifoslabel
												name="client.select" bundle="ClientUIResources"></mifos:mifoslabel>
												<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />/<mifos:mifoslabel
													name="${ConfigurationConstants.BRANCHOFFICE}" />
											</td>
										</tr>
									</table>
									</td>
									<td width="25%" align="center">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/tick.gif"
												width="17" height="17"></td>
											<td class="timelineboldgray"><mifos:mifoslabel
												name="client.PersonalInformationHeading"
												bundle="ClientUIResources"></mifos:mifoslabel></td>
										</tr>
									</table>
									</td>
									<td width="25%" align="center">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/bigarrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorange"><mifos:mifoslabel
												name="client.MFIInformationHeading"
												bundle="ClientUIResources"></mifos:mifoslabel></td>
										</tr>
									</table>
									</td>
									<td width="25%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorangelight"><mifos:mifoslabel
												name="client.ReviewSubmitHeading" bundle="ClientUIResources"></mifos:mifoslabel></td>
										</tr>
									</table>
									</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0" class="bluetableborder">
						<tr>
							<td align="left" valign="top" class="paddingleftCreates">
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td class="headingorange"><%-- Displaying the heading of the page based on whether the user is comign first time or to edit the information --%>
										<span class="heading"> 
											<fmt:message key=client.createNewClient">
												<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
											</fmt:message>
											</span>
											<mifos:mifoslabel name="client.CreateMfiInformationTitle"
												bundle="ClientUIResources"></mifos:mifoslabel>
									</td>
								</tr>
								<!-- Instructions for the user -->
								<tr>
									<td class="fontnormal"><c:choose>
										<c:when test="${param.method eq 'prevMFIInfo'}">
											<mifos:mifoslabel name="client.PreviewEditInfoInstruction"
												bundle="ClientUIResources"></mifos:mifoslabel>
											<fmt:message key="client.PreviewEditCancelInstruction">
												<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
											</fmt:message>
											<span class="mandatorytext"><font color="#FF0000">*</font></span>
											<mifos:mifoslabel name="client.FieldInstruction"
												bundle="ClientUIResources"></mifos:mifoslabel>
										</c:when>
										<c:otherwise>
											<mifos:mifoslabel
												name="client.CreateMfiInformationInstruction"
												bundle="ClientUIResources"></mifos:mifoslabel>
											<mifos:mifoslabel name="client.CreatePageCancelInstruction1"
												bundle="ClientUIResources"></mifos:mifoslabel>
																						<br>
											<span class="mandatorytext"><font color="#FF0000">*</font></span>
											<mifos:mifoslabel name="client.FieldInstruction"
												bundle="ClientUIResources"></mifos:mifoslabel>
										</c:otherwise>
									</c:choose></td>
								</tr>
								<tr>
									<td><font class="fontnormalRedBold"><html-el:errors
										bundle="ClientUIResources" /></font></td>
								</tr>
							</table>
							<br>
							<%-- MFI Information Heading --%>

							<table width="93%" border="0" cellpadding="3" cellspacing="0">

								<tr>
									<td height="23" colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="client.MfiInformationLabel" bundle="ClientUIResources"></mifos:mifoslabel></td>
								</tr>
								<%-- Loan Officer Name --%>
								<c:if
									test="${sessionScope.clientCustActionForm.groupFlag eq '0'}">
									<tr class="fontnormal">
										<td align="right" width="27%"><mifos:mifoslabel
											name="client.LoanOfficer" bundle="ClientUIResources"></mifos:mifoslabel></td>
										<td><mifos:select property="loanOfficerId"
											onchange="populateDefaultFormedBy(this)">
											<c:forEach var="loanOfficersList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanOfficers')}" >
												<html-el:option value="${loanOfficersList.personnelId}">${loanOfficersList.displayName}</html-el:option>
											</c:forEach>
										</mifos:select></td>
									</tr>
									<%-- Meeting schedule --%>
									<tr class="fontnormal">
										<td align="right"><mifos:mifoslabel
											name="client.MeetingSchedule" bundle="ClientUIResources"></mifos:mifoslabel></td>
										<td><html-el:link href="javascript:loadMeeting()">
											<mifos:mifoslabel name="client.MeetingScheduleLink"
												bundle="ClientUIResources"></mifos:mifoslabel>
										</html-el:link></td>
									</tr>
								</c:if>
								<tr class="fontnormal">
									<td align="right" width="27%"><mifos:mifoslabel
										name="client.FormedBy" mandatory="yes"
										bundle="ClientUIResources"></mifos:mifoslabel></td>
									<td><mifos:select property="formedByPersonnel">
										<c:forEach var="customerFormedBy"
											items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'formedByLoanOfficers')}">
											<html-el:option value="${customerFormedBy.personnelId}">
												<c:out value="${customerFormedBy.displayName}" />
											</html-el:option>
										</c:forEach>
									</mifos:select></td>
								</tr>
								<%-- External Id --%>
								<tr class="fontnormal">
									<td width="27%" align="right"><mifos:mifoslabel keyhm="Client.ExternalId" isColonRequired="yes"
										name="${ConfigurationConstants.EXTERNALID}"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext keyhm="Client.ExternalId" property="externalId"
										maxlength="50" /></td>
								</tr>
								<%-- Trained --%>
								<tr class="fontnormal">
									<td align="right" valign="top"><mifos:mifoslabel keyhm="Client.Trained"
										name="client.Trained" bundle="ClientUIResources"></mifos:mifoslabel>
									</td>
									<td><mifos:checkbox keyhm="Client.Trained" property="trained" value="1" /></td>
								</tr>
								<%--Trained Date--%>
								<tr class="fontnormal">
									<td align="right" valign="top"><mifos:mifoslabel keyhm="Client.TrainedDate"
										name="client.TrainedOnDate" bundle="ClientUIResources"></mifos:mifoslabel>
									</td>
									<td><date:datetag keyhm="Client.TrainedDate" property="trainedDate" /></td>
								</tr>
								<td></td>
								<tr></tr>
							</table>
							<br>
							<!-- Administrative Set Fees -->
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<c:if test="${!empty sessionScope.clientCustActionForm.defaultFees}">
								<tr>
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="client.AdministrativeFeesHeading"
										bundle="ClientUIResources"></mifos:mifoslabel><br>
									<br>
									</td>
								</tr>
								<!-- For each admin fee that is retrieved the name and amoutn is displayed -->
								 <c:forEach var="adminFees" items="${sessionScope.clientCustActionForm.defaultFees}" varStatus="loopStatus1">
										<bean:define id="ctr1" toScope="request">
											<c:out value="${loopStatus1.index}" />
										</bean:define>
										<tr>
											<td align="right" width="27%" class="fontnormal">
											     <c:out	value="${adminFees.feeName}" />:
											</td>
											<td width="73%" class="fontnormal">
												<table width="500" border="0" cellspacing="0" cellpadding="0">
													<tr class="fontnormal">
														<c:choose>
															<c:when test="${adminFees.periodic == true}">
																<td width="20%"><c:out value="${adminFees.amount}" /></td>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${param.method eq 'next'}">
																		<td width="20%"><mifos:mifosdecimalinput property="defaultFee[${ctr1}].amount" value="${adminFees.amount}" style="width:135px;" /></td>
																	</c:when>
																	<c:otherwise>
																		<td width="20%"><mifos:mifosdecimalinput property="defaultFee[${ctr1}].amount"  style="width:135px;" /></td>
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
														<td width="25%">&nbsp; <mifos:mifoslabel name="client.Periodicity" bundle="ClientUIResources" />
															<c:choose>
																<c:when test="${adminFees.periodic == true}">
																	<c:out value="${adminFees.feeSchedule}" />
																</c:when>
																<c:otherwise>
																	<mifos:mifoslabel name="Fees.onetime" />
																</c:otherwise>
															</c:choose>
														</td>
														<td width="25%"><html-el:checkbox
															property="defaultFee[${ctr1}].feeRemoved" value="1"></html-el:checkbox><mifos:mifoslabel name="client.CheckToRemove" bundle="ClientUIResources" /></td>
													<%-- <c:out value="${adminFees.checkedFee}" /> --%>
													</tr>
												</table>
											 </td>
									 	</tr>
									</c:forEach>
								</c:if>
							</table>
							<br>
							<!-- Administrative Set Fees End--> <!-- Fee Type -->
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="4" class="fontnormalbold"><mifos:mifoslabel
										name="client.AdditionalFeesHeading" bundle="ClientUIResources"></mifos:mifoslabel><br>
									<br>
									</td>
								</tr>

								<tr>
								 	<c:forEach begin="0" end="2" step="1" varStatus="loopStatus2">
											<bean:define id="ctr2" toScope="request">
												<c:out value="${loopStatus2.index}" />
											</bean:define>
											<td width="27%" align="right" class="fontnormal">
												<mifos:mifoslabel name="client.FeeType" bundle="ClientUIResources" />
											</td>
											<td width="11%" class="fontnormal">
												<mifos:select name="clientCustActionForm" property='selectedFee[${ctr2}].feeId' onchange="displayAmount('selectedFee[${ctr2}].feeId', 'selectedFee[${ctr2}].amount' )">
													<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'additionalFeeList')}" var="additionalFee">
														<html-el:option value="${additionalFee.feeId}">${additionalFee.feeName}</html-el:option>
													</c:forEach>
												</mifos:select>
											</td>
											<td width="14%" align="right" class="fontnormal">
												<mifos:mifoslabel name="client.Amount" bundle="ClientUIResources"/>
											</td>
											<td width="48%" class="fontnormal">
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


							<!-- Fees End --> <br>

							<!--  Savings accounts-->
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="4" class="fontnormalbold">
										<fmt:message key="client.createAccounts">
											<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/></fmt:param>
										</fmt:message>
										<br><br>
									</td>
								</tr>
							 	<c:forEach begin="0" end="2" step="1" varStatus="savingsLoopStatus">
								 	<bean:define id="savingsCtr" toScope="request">
										<c:out value="${savingsLoopStatus.index}" />
									</bean:define>
									<tr>
										<td width="27%" align="right" class="fontnormal">
											<fmt:message key="client.savingsInstanceName">
												<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/></fmt:param>
											</fmt:message>:
										</td>
										<td width="73%" class="fontnormal">
											<mifos:select name="clientCustActionForm" property="savingsOffering[${savingsCtr}]">
												<c:forEach var="offering" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'savingsOfferingList')}" >
													<html-el:option value="${offering.prdOfferingId}">${offering.prdOfferingName}</html-el:option>
												</c:forEach>
											</mifos:select>
										</td>
									</tr>
							 	</c:forEach>
							</table>
							<br>
							<!-- Buttons -->
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
							<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
							<!-- Button end --> <br>
							<!-- before main closing --></td>
						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>
		</html-el:form>
	</tiles:put>
</tiles:insert>
