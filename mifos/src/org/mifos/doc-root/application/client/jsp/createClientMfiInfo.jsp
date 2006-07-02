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
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>


<!-- Tile  definitions -->
<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
		<script language="javascript" SRC="pages/framework/js/date.js"></script>
		<script language="javascript">

	
  function goToCancelPage(){
	clientCreationActionForm.action="clientCreationAction.do?method=cancel";
	clientCreationActionForm.submit();
  }
  
  function populateDefaultFormedBy(selectBox)
  {
  		if(selectBox.selectedIndex > 0)
  		{
		  clientCreationActionForm.action="clientCreationAction.do?method=setDefaultFormedByPersonnel";
		  clientCreationActionForm.submit();		
		}
		
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
			if (clientCreationActionForm.selectedFeeAmntList[indexSelectedFee]!= undefined){
				//obtaining the fee amount for a particular fee
				var amount=clientCreationActionForm.selectedFeeAmntList[indexSelectedFee].value;
				//assigning the value of the fee amount to the text box
				amountField.value = amount;
			}
			else{
				amountField.value=clientCreationActionForm.selectedFeeAmntList.value;
			}
			
		}
	}
	function loadMeeting(){
	    clientCreationActionForm.action="clientCreationAction.do?method=loadMeeting";
	    clientCreationActionForm.submit();
    }
</script>
		<html-el:form action="clientCreationAction.do?method=preview"
				onsubmit="return (validateMyForm(trainedDate,trainedDateFormat,trainedDateYY));" >
			<!-- Hidden varaibles for the locale and for the input page -->
			<html-el:hidden property="input" value="mfiInfo" />
			<html-el:hidden property="office.officeId"
				value="${requestScope.clientVO.office.officeId}" />
			<html-el:hidden property="office.officeName"
				value="${requestScope.clientVO.office.officeName}" />
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
									<c:choose>
										<c:when test="${param.method eq 'prevMFIInfo'}">
											<span class="heading"> <mifos:mifoslabel
												name="client.ManageTitle" bundle="ClientUIResources"></mifos:mifoslabel>
											<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />
											</span>
											<mifos:mifoslabel name="client.EditMfiInformationTitle"
												bundle="ClientUIResources"></mifos:mifoslabel>

										</c:when>
										<c:otherwise>
											<span class="heading"> <mifos:mifoslabel
												name="client.CreateTitle" bundle="ClientUIResources"></mifos:mifoslabel>
											<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />
											</span>
											<mifos:mifoslabel name="client.CreateMfiInformationTitle"
												bundle="ClientUIResources"></mifos:mifoslabel>

										</c:otherwise>
									</c:choose></td>
								</tr>
								<!-- Instructions for the user -->
								<tr>
									<td class="fontnormal"><c:choose>
										<c:when test="${param.method eq 'prevMFIInfo'}">
											<mifos:mifoslabel name="client.PreviewEditInfoInstruction"
												bundle="ClientUIResources"></mifos:mifoslabel>
											<mifos:mifoslabel name="client.PreviewEditCancelInstruction1"
												bundle="ClientUIResources"></mifos:mifoslabel>
											<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />
											<mifos:mifoslabel name="client.PreviewEditCancelInstruction2"
												bundle="ClientUIResources"></mifos:mifoslabel>
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
									test="${sessionScope.clientCreationActionForm.isClientUnderGrp == 0}">
									<tr class="fontnormal">
										<td align="right" width="27%"><mifos:mifoslabel
											name="client.LoanOfficer" bundle="ClientUIResources"></mifos:mifoslabel></td>
										<td><mifos:select property="loanOfficerId"
											style="width:136px;" onchange="populateDefaultFormedBy(this)">
											<c:forEach var="loanOfficer"
												items="${requestScope.loanOfficers}">
												<html-el:option value="${loanOfficer.personnelId}">
													<c:out value="${loanOfficer.displayName}" />
												</html-el:option>
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
									<td><mifos:select property="customerFormedById"
										style="width:136px;">
										<c:forEach var="customerFormedBy"
											items="${requestScope.formedByLoanOfficers}">
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
								<%-- Confidential 
	               <tr class="fontnormal">
	                  <td align="right" class="fontnormal">
	                  	<mifos:mifoslabel name="client.Confidential" bundle="ClientUIResources"></mifos:mifoslabel>
	                  </td>
	                  <td>
	                  	<html-el:checkbox property="clientConfidential" value="1"/>
	                  </td>
	               </tr> --%>

								<td></td>
								<tr></tr>
							</table>
							<br>
							<!-- Administrative Set Fees -->
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="client.AdministrativeFeesHeading"
										bundle="ClientUIResources"></mifos:mifoslabel><br>
									<br>
									</td>
								</tr>
								<!-- For each admin fee that is retrieved the name and amoutn is displayed -->
								<c:forEach var="adminFees" items="${requestScope.adminFeesList}"
									varStatus="loopStatus1">
									<bean:define id="ctr1" toScope="request">
										<c:out value="${loopStatus1.index}" />
									</bean:define>
									<tr>
										<td align="right" width="27%" class="fontnormal"><c:out
											value="${adminFees.feeName}" />:</td>
										<td width="73%" class="fontnormal">
										<table width="500" border="0" cellspacing="0" cellpadding="0">
											<tr class="fontnormal">
												<c:choose>
													<c:when test="${adminFees.feeFrequencyTypeId == 1}">
														<td width="20%"><c:out value="${adminFees.rateOrAmount}" />
														<html-el:hidden property="adminFee[${ctr1}].rateOrAmount"
															value="${adminFees.rateOrAmount}" /> <html-el:hidden
															property="adminFee[${ctr1}].feeId"
															value="${adminFees.feeId}"></html-el:hidden> <html-el:hidden
															property="adminFee[${ctr1}].feeName"
															value="${adminFees.feeName}"></html-el:hidden> <html-el:hidden
															property="adminFee[${ctr1}].feeFrequencyTypeId"
															value="${adminFees.feeFrequencyTypeId}"></html-el:hidden>
														</td>
													</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${param.method eq 'next'}">
																<td width="20%"><!--Bug id 26810 & 28312  Removed the format,max,min values specified in mifosdecimaltag-->
																<mifos:mifosdecimalinput
																	property="adminFee[${ctr1}].rateOrAmount"
																	value="${adminFees.rateOrAmount}" style="width:135px;"
																	size="8" /> <html-el:hidden
																	property="adminFee[${ctr1}].feeId"
																	value="${adminFees.feeId}"></html-el:hidden> <html-el:hidden
																	property="adminFee[${ctr1}].feeName"
																	value="${adminFees.feeName}"></html-el:hidden> <html-el:hidden
																	property="adminFee[${ctr1}].feeFrequencyTypeId"
																	value="${adminFees.feeFrequencyTypeId}"></html-el:hidden>
																</td>
															</c:when>
															<c:otherwise>
																<td width="20%"><mifos:mifosdecimalinput
																	property="adminFee[${ctr1}].rateOrAmount"
																	style="width:135px;" /> <html-el:hidden
																	property="adminFee[${ctr1}].feeId"
																	value="${adminFees.feeId}"></html-el:hidden> <html-el:hidden
																	property="adminFee[${ctr1}].feeName"
																	value="${adminFees.feeName}"></html-el:hidden> <html-el:hidden
																	property="adminFee[${ctr1}].feeFrequencyTypeId"
																	value="${adminFees.feeFrequencyTypeId}"></html-el:hidden>
																</td>
															</c:otherwise>
														</c:choose>
													</c:otherwise>
												</c:choose>

												<td width="25%">&nbsp; <mifos:mifoslabel
													name="client.Periodicity" bundle="ClientUIResources" /> <c:choose>
													<c:when test="${adminFees.feeFrequencyTypeId == 1}">
														<c:out value="${adminFees.feeMeeting.feeMeetingSchedule}" />
													</c:when>
													<c:otherwise>
														<mifos:mifoslabel name="Fees.onetime" />
													</c:otherwise>
												</c:choose></td>
												<td width="25%"><html-el:checkbox
													property="adminFee[${ctr1}].checkedFee" value="1"></html-el:checkbox>Check
												to Remove</td>
												<c:out value="${adminFees.checkedFee}" />
											</tr>
										</table>
										</td>
									</tr>
								</c:forEach>
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

								<c:forEach begin="0" end="2" step="1" varStatus="loopStatus2">
									<bean:define id="ctr2" toScope="request">
										<c:out value="${loopStatus2.index}" />
									</bean:define>
									<td width="27%" align="right" class="fontnormal"><mifos:mifoslabel
										name="client.FeeType" bundle="ClientUIResources"></mifos:mifoslabel></td>
									<td width="11%" class="fontnormal"><mifos:select
										name="clientCreationActionForm"
										property='selectedFee[${ctr2}].feeId'
										onchange="displayAmount('selectedFee[${ctr2}].feeId', 'selectedFee[${ctr2}].rateOrAmount' )">
										<html-el:options collection="feesList" property="feeId"
											labelProperty="feeName"></html-el:options>

									</mifos:select></td>
									<td align="right" width="14%" class="fontnormal"><mifos:mifoslabel
										name="client.Amount" bundle="ClientUIResources"></mifos:mifoslabel></td>
									<td width="48%" class="fontnormal"><!--Bug id 26810 & 28312  Removed the format,max,min values  specified in mifosdecimaltag-->
									<mifos:mifosdecimalinput
										property='selectedFee[${ctr2}].rateOrAmount' /></td>

									<c:if test="${ctr2 == 0}">
										<c:forEach var="fee" items="${requestScope.feesList}"
											varStatus="loopStatus3">
											<bean:define id="ctr3" toScope="request">
												<c:out value="${loopStatus3.index}" />
											</bean:define>
											<html-el:hidden property='selectedFeeAmntList'
												value='${fee.rateOrAmount}' />


										</c:forEach>
									</c:if>


									<tr></tr>

								</c:forEach>

							</table>


							<!-- Fees End --> <br>


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
									<td align="center"><html-el:submit styleClass="buttn"
										style="width:70px;">
										<mifos:mifoslabel name="button.preview"
											bundle="ClientUIResources"></mifos:mifoslabel>
									</html-el:submit> &nbsp; &nbsp; <html-el:button
										onclick="goToCancelPage();" property="cancelButton"
										styleClass="cancelbuttn" style="width:70px">
										<mifos:mifoslabel name="button.cancel"
											bundle="ClientUIResources"></mifos:mifoslabel>
									</html-el:button></td>
								</tr>
							</table>
							<!-- Button end --> <br>
							<!-- before main closing --></td>
						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>
			<mifos:SecurityParam property="ClientCreate"></mifos:SecurityParam>
		</html-el:form>
	</tiles:put>
</tiles:insert>
