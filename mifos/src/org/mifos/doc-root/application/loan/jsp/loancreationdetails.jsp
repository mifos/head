<!--
 
 * ContinuecreateLoanAccount.jsp  version: xxx
 
 
 
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
 
 -->

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
		<SCRIPT SRC="pages/application/loan/js/CreateLoanAccount.js"></SCRIPT>
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>

		<SCRIPT>
				function intDedAtDisb() {
					if(document.getElementsByName("gracePeriodTypeId")[0].value==1) {
						document.getElementsByName("gracePeriodDuration")[0].disabled=true;
					} else  {
						if(document.getElementsByName("intDedDisbursement")[0].checked==true) {
						    document.getElementsByName("gracePeriodDuration")[0].value="0";
							document.getElementsByName("gracePeriodDuration")[0].disabled=true;
						}else{
							document.getElementsByName("gracePeriodDuration")[0].disabled=false;
						}
					}
				}
				
				function displayAmount(listBox, textBox,index ){
					//The fee combo box		
					var comboBox = document.getElementsByName(listBox)[0];
					//The amount text box
					var amountField = document.getElementsByName(textBox)[0];		
					//If no fee is selected then the amount field displays nothing
					if(comboBox.selectedIndex==0)
						amountField.value = "";
					else{
						var indexSelectedFee = comboBox.selectedIndex-1;
						var formula = "";
						if (loanAccountActionForm.selectedFeeAmntList[indexSelectedFee]!= undefined){
							//obtaining the fee amount for a particular fee
							var amount=loanAccountActionForm.selectedFeeAmntList[indexSelectedFee].value;
							//assigning the value of the fee amount to the text box
							amountField.value = amount;
							formula = loanAccountActionForm.feeFormulaList[indexSelectedFee].value
						}
						else{			
							amountField.value=loanAccountActionForm.selectedFeeAmntList.value;
							formula = loanAccountActionForm.feeFormulaList.value
						}
						var span = document.getElementsByName("feeFormulaSpan"+index)[0];
						span.innerHTML =formula;
					}
				}	
				
				function displayFormula(listBox, textBox,index ){
					//The fee combo box		
					var comboBox = document.getElementsByName(listBox)[0];
					//The amount text box
					var amountField = document.getElementsByName(textBox)[0];		
					//If no fee is selected then the amount field displays nothing
					if(comboBox.selectedIndex==0)
						amountField.value = "";
					else{
						var indexSelectedFee = comboBox.selectedIndex-1;
						var formula = "";
						if (loanAccountActionForm.selectedFeeAmntList[indexSelectedFee]!= undefined){
							formula = loanAccountActionForm.feeFormulaList[indexSelectedFee].value
						}
						else{			
							formula = loanAccountActionForm.feeFormulaList.value
						}
						var span = document.getElementsByName("feeFormulaSpan"+index)[0];
						span.innerHTML =formula;
					}
				}	
				
				function fun_refresh(form)
					{
						
							form.action="loanAccountAction.do?method=load";
							form.submit();
							
					}
			</SCRIPT>
		<html-el:form action="/loanAccountAction.do?method=schedulePreview" onsubmit="return (validateMyForm(disbursementDate,disbursementDateFormat,disbursementDateYY));">
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanOffering')}" var="LoanOffering" />
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="470" align="left" valign="top" bgcolor="#FFFFFF">
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
											<td width="25%">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/tick.gif" width="17" height="17">
														</td>
														<td class="timelineboldgray">
															<mifos:mifoslabel name="loan.Select" />
															<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />
															<mifos:mifoslabel name="loan.Slash" />
															<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />

														</td>
													</tr>
												</table>
											</td>
											<td width="25%" align="center">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17">
														</td>
														<td class="timelineboldorange">
															<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
															<mifos:mifoslabel name="loan.acc_info" />
														</td>
													</tr>
												</table>
											</td>
											<td width="25%" align="right">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17">
														</td>
														<td class="timelineboldorangelight">
															<mifos:mifoslabel name="loan.review/edit_ins" />
														</td>
													</tr>
												</table>
											</td>
											<td width="25%" align="right">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17">
														</td>
														<td class="timelineboldorangelight">
															<mifos:mifoslabel name="loan.review&submit" />
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
								<td width="70%" height="24" align="left" valign="top" class="paddingleftCreates">
									<table width="98%" border="0" cellspacing="0" cellpadding="3">
										<tr>
											<td class="headingorange">
												<span class="heading"> <mifos:mifoslabel name="accounts.create" /> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel name="accounts.account" />&nbsp;-&nbsp; </span>
												<mifos:mifoslabel name="loan.Enter" />
												<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
												<mifos:mifoslabel name="loan.acc_info" />
											</td>
										</tr>
										<tr>
											<td class="fontnormal">
												<mifos:mifoslabel name="loan.complete_field" />
												<br>
												<font color="#FF0000">*</font>
												<mifos:mifoslabel name="loan.asterisk" />

											</td>
										</tr>
										<tr>
											<td>
												<font class="fontnormalRedBold"> <html-el:errors bundle="loanUIResources" /> </font>
											</td>
										</tr>


										<tr>
											<td class="fontnormal">
												<br>
												<span class="fontnormalbold"> <mifos:mifoslabel name="loan.acc_owner" /><mifos:mifoslabel name="loan.colon" /> </span>
												<c:out value="${sessionScope.loanAccountOwner.displayName}" />
											</td>
										</tr>
									</table>

									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr class="fontnormal">
											<td width="30%" align="right" class="fontnormal">
												<span class="mandatorytext"></span>
												<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" mandatory="yes" />
												<mifos:mifoslabel name="loan.instancename" />:
											</td>
											<td width="70%">
												<html-el:select onchange="javascript:fun_refresh(this.form)" property="prdOfferingId" style="width:136px;">
													<c:forEach var="loanPrdOffering" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanPrdOfferings')}" >
															<html-el:option value="${loanPrdOffering.prdOfferingId}">${loanPrdOffering.prdOfferingName}</html-el:option>
														</c:forEach>
												</html-el:select>
											</td>
										</tr>
									</table>
									<br>
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr class="fontnormal">
											<td colspan="2" valign="top" class="fontnormalbold">
												<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
												<mifos:mifoslabel name="loan.product_summary" />
												<br>
												<br>
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right" valign="top">
												<mifos:mifoslabel name="loan.description" />:
											</td>
											<td valign="top">
												<c:out value="${LoanOffering.description}" />
											</td>
										</tr>

										<tr class="fontnormal">
											<td width="30%" align="right">
												<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" />
												<mifos:mifoslabel name="loan.interest_type" />:
											</td>
											<td width="70%" valign="top">
												<c:out value="${LoanOffering.interestTypes.name}" />
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel name="loan.freq_of_inst" />:
											</td>
											<td valign="top">
												<c:out value="${LoanOffering.loanOfferingMeeting.meeting.meetingDetails.recurAfter}" />
												&nbsp;
												<c:choose>
													<c:when test="${LoanOffering.loanOfferingMeeting.meeting.meetingDetails.recurrenceType.recurrenceId == 1}">
														<mifos:mifoslabel name="loan.week(s)" />
													</c:when>
													<c:otherwise>
														<mifos:mifoslabel name="loan.month(s)" />
													</c:otherwise>
												</c:choose>
											</td>
										</tr>

										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel name="loan.principle_due" />:
											</td>
											<td valign="top">
												<c:choose>
													<c:when test="${LoanOffering.prinDueLastInst}">
														<mifos:mifoslabel name="loan.yes" />
													</c:when>
													<c:otherwise>
														<mifos:mifoslabel name="loan.no" />
													</c:otherwise>
												</c:choose>
											</td>
										</tr>
									</table>
									<br>
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td colspan="2" class="fontnormalbold">
												<mifos:mifoslabel name="loan.acc_details" />
												<br>
												<br>
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right" class="fontnormal" width="30%">
												<mifos:mifoslabel name="loan.amount" mandatory="yes" />:
											</td>
											<td valign="top">
												<mifos:mifosdecimalinput property="loanAmount" />
												<mifos:mifoslabel name="loan.allowed_amount" />
												&nbsp;
												<c:out value="${LoanOffering.minLoanAmount}" />
												&nbsp; - &nbsp;
												<c:out value="${LoanOffering.maxLoanAmount}" />
												)
											</td>
										</tr>
										<tr class="fontnormal">
											<td width="30%" align="right" class="fontnormal">
												<span class="mandatorytext"></span>
												<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" mandatory="yes" />
												<mifos:mifoslabel name="loan.interest_rate" />:
											</td>
											<td width="70%" valign="top">
												<mifos:mifosdecimalinput property="interestRate" decimalFmt="10.5" />
												<mifos:mifoslabel name="loan.allowed_interest1" />
												<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" />
												<mifos:mifoslabel name="loan.allowed_interest2" />
												&nbsp;
												<c:out value="${LoanOffering.minInterestRate}" />
												&nbsp; - &nbsp;
												<c:out value="${LoanOffering.maxInterestRate}" />
												%)
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right" class="fontnormal">
												<span class="mandatorytext"></span>
												<mifos:mifoslabel name="loan.no_of_inst" mandatory="yes" />:
											</td>
											<td valign="top">
												<mifos:mifosnumbertext property="noOfInstallments" />
												<mifos:mifoslabel name="loan.allowed_no_of_inst" />
												&nbsp;
												<c:out value="${LoanOffering.minNoInstallments}" />
												&nbsp; - &nbsp;
												<c:out value="${LoanOffering.maxNoInstallments}" />
												)
											</td>
										</tr>
										<tr class="fontnormal">

											<td align="right" class="fontnormal">
												<span class="mandatorytext"></span>
												<mifos:mifoslabel name="loan.proposed_date" mandatory="yes" />:
											</td>
											<td valign="top">
												<date:datetag property="disbursementDate" />
											</td>
										</tr>
										<!-- TODO: Change the property of checkbox -->
										<tr class="fontnormal">
											<td align="right" class="fontnormal">
												<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" />
												<mifos:mifoslabel name="loan.interest_disb" />:
											</td>
											<td valign="top">
												<html-el:checkbox property="intDedDisbursement" value="1" onclick="intDedAtDisb()" />
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right" class="fontnormal">
												<span class="mandatorytext"></span>
												<mifos:mifoslabel name="loan.grace_period" mandatory="yes" />:
											</td>
											<td valign="top">
												<mifos:mifosnumbertext property="gracePeriodDuration" />
												<mifos:mifoslabel name="loan.inst" />

											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right" class="fontnormal">
												<span class="mandatorytext"></span>
												<mifos:mifoslabel name="loan.source_fund" />:
											</td>
											<td valign="top">
												<mifos:select property="loanOfferingFund" style="width:136px;">
													<c:forEach var="loanfund" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanfunds')}" >
															<html-el:option value="${loanfund.fundId}">${loanfund.fundName}</html-el:option>
													</c:forEach>
												</mifos:select>
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right" class="fontnormal">
												<mifos:mifoslabel keyhm="Loan.PurposeOfLoan" name="Loan.PurposeOfLoan" bundle="loanUIResources" />
												<mifos:mifoslabel keyhm="Loan.PurposeOfLoan" name="${ConfigurationConstants.LOAN}" isColonRequired="yes" bundle="loanUIResources" isManadatoryIndicationNotRequired="yes" />
											</td>
											<td valign="top">
												<mifos:select keyhm="Loan.PurposeOfLoan" property="businessActivityId" style="width:136px;">
													<c:forEach var="BusinessActivity" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessActivities')}" >
															<html-el:option value="${BusinessActivity.id}">${BusinessActivity.name}</html-el:option>
													</c:forEach>
												</mifos:select>
											</td>
										</tr>

										<tr class="fontnormal">
											<td align="right" class="fontnormal">
												<mifos:mifoslabel keyhm="Loan.CollateralType" name="Loan.CollateralType" isColonRequired="yes" bundle="loanUIResources" />
											</td>
											<td valign="top">
												<mifos:select keyhm="Loan.CollateralType" property="collateralTypeId" style="width:136px;">
													<c:forEach var="CollateralType" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'CollateralTypes')}" >
															<html-el:option value="${CollateralType.id}">${CollateralType.name}</html-el:option>
													</c:forEach>
												</mifos:select>
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right" valign="top" class="fontnormal">
												<mifos:mifoslabel keyhm="Loan.CollateralNotes" name="Loan.CollateralNotes" isColonRequired="yes" bundle="loanUIResources" />
											</td>
											<td valign="top">
												<mifos:textarea keyhm="Loan.CollateralNotes" property="collateralNote" style="width:320px; height:110px;">
												</mifos:textarea>
											</td>
										</tr>

									</table>
									<br>
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
						                <tr>
						                  <td colspan="2" class="fontnormalbold">
						                  <mifos:mifoslabel name="loan.additionalInfo" bundle="loanUIResources"/></td>
						                </tr>
						                <c:forEach var="cf" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}" varStatus="loopStatus">
						              	 <bean:define id="ctr">
						                	<c:out value="${loopStatus.index}"/>
						                </bean:define>
						
									<tr class="fontnormal">
						                <td width="30%" align="right">
											<mifos:mifoslabel name="${cf.lookUpEntity.entityType}" mandatory="${cf.mandatoryStringValue}" bundle="loanUIResources"></mifos:mifoslabel>:
										</td>
						                <td width="70%">          
											<c:if test="${cf.fieldType == MasterConstants.CUSTOMFIELD_NUMBER}">  
							                	<mifos:mifosnumbertext  name = "loanAccountActionForm" property='customField[${ctr}].fieldValue' maxlength="200"/>
							                </c:if>
							               	<c:if test="${cf.fieldType == MasterConstants.CUSTOMFIELD_ALPHANUMBER}">
							                	<mifos:mifosalphanumtext name = "loanAccountActionForm" property='customField[${ctr}].fieldValue' maxlength="200"/>
											</c:if>
							                <c:if test="${cf.fieldType == MasterConstants.CUSTOMFIELD_DATE}"> 
							                	<mifos:mifosalphanumtext name = "loanAccountActionForm" property='customField[${ctr}].fieldValue' maxlength="200"/>
							                </c:if>
							                <html-el:hidden property='customField[${ctr}].fieldId' value="${cf.fieldId}"></html-el:hidden>
						                </td>
						           </tr>
						         </c:forEach>
						              </table>
						              <br>
									<!-- Administrative Set Fees -->
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td colspan="2" class="fontnormalbold">
												<mifos:mifoslabel name="loan.admin_set_fees" />
												<br>
												<br>
											</td>
										</tr>
										<!-- For each admin fee that is retrieved the name and amoutn is displayed -->
										<c:forEach var="adminFees" items="${sessionScope.loanAccountActionForm.defaultFees}" varStatus="loopStatus1">
											<bean:define id="ctr1" toScope="request">
												<c:out value="${loopStatus1.index}" />
											</bean:define>
											<tr>
												<td width="30%" align="right" class="fontnormal">
													<c:out value="${adminFees.feeName}" />:
												</td>
												<td width="70%" class="fontnormal">
													<table width="420" border="0" cellspacing="0" cellpadding="0">
														<!-- Fee amount display as label or text field -->
														<tr class="fontnormal">
															<td width="140">
																<c:choose>
																	<c:when test="${adminFees.periodic == true}">
																		<c:out value="${adminFees.amount}" />
																	</c:when>
																	<c:otherwise>
																		<mifos:mifosdecimalinput property="defaultFee[${ctr1}].amount" value="${adminFees.amount}" style="width:135px;" />
																	</c:otherwise>
																</c:choose>
																<c:out value="${adminFees.feeFormula}" />
															</td>
															<td width="140">
																&nbsp;
																<mifos:mifoslabel name="loan.periodicity" />
																<c:choose>
																	<c:when test="${adminFees.periodic == true}">
																		<c:out value="${adminFees.feeSchedule}" />
																	</c:when>
																	<c:otherwise>
																		<mifos:mifoslabel name="Fees.onetime" />
																	</c:otherwise>
																</c:choose>
															</td>
															<td width="140">
																<html-el:checkbox property="defaultFee[${ctr1}].feeRemoved" value="1"></html-el:checkbox>
																<mifos:mifoslabel name="loan.checkToRemove" />
															</td>
															<%--<c:out value="${adminFees.feeRemoved}" />--%>
														</tr>
													</table>
												</td>
											</tr>
										</c:forEach>
									</table>
									<br>
									<!-- Administrative Set Fees End-->
									<!-- Fee Type -->
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td colspan="4" class="fontnormalbold">
												<mifos:mifoslabel name="loan.apply_add_fees" />
												<br>
												<br>
											</td>
										</tr>
										<tr>
											<c:forEach begin="0" end="2" step="1" varStatus="loopStatus2">
												<bean:define id="ctr2" toScope="request">
													<c:out value="${loopStatus2.index}" />
												</bean:define>
												<td width="30%" align="right" class="fontnormal">
													<mifos:mifoslabel name="loan.fee_type" />:
												</td>
												<td width="17%" class="fontnormal">
													<mifos:select name="loanAccountActionForm" property='selectedFee[${ctr2}].feeId' onchange="displayAmount('selectedFee[${ctr2}].feeId', 'selectedFee[${ctr2}].amount',${loopStatus2.index} )">
														<c:forEach var="additionalFee" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'additionalFeeList')}" >
															<html-el:option value="${additionalFee.feeId}">${additionalFee.feeName}</html-el:option>
														</c:forEach>
													</mifos:select>
												</td>
												<td width="12%" align="right" class="fontnormal">
													<mifos:mifoslabel name="loan.amount" />:
												</td>
												<td width="50%" class="fontnormal">
													<mifos:mifosdecimalinput property='selectedFee[${ctr2}].amount' decimalFmt="10.5"/>
													<SPAN id="feeFormulaSpan${loopStatus2.index}" class="fontnormal"></SPAN>

												</td>
												<c:if test="${ctr2 == 0}">
													<c:forEach var="fee" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'additionalFeeList')}" varStatus="loopStatus3">
														<bean:define id="ctr3" toScope="request">
															<c:out value="${loopStatus3.index}" />
														</bean:define>
														<html-el:hidden property='selectedFeeAmntList' value='${fee.amount}' />
														<html-el:hidden property='feeFormulaList' value='${fee.feeFormula}' />
													</c:forEach>
												</c:if>
												<script>displayFormula('selectedFee[${ctr2}].feeId', 'selectedFee[${ctr2}].amount',${loopStatus2.index} )</script>
										</tr>
										</c:forEach>
									</table>
									<!-- Fees End -->
									<br>


									<table width="93%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center">
												<html-el:submit property="continueButton" styleClass="buttn" style="width:70px;">
													<mifos:mifoslabel name="loan.continue" />
												</html-el:submit>
												&nbsp;

												<html-el:button property="cancelButton" onclick="javascript:fun_cancel(this.form)" styleClass="cancelbuttn" style="width:70px;">
													<mifos:mifoslabel name="loan.cancel" />
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
			<html-el:hidden property="gracePeriodTypeId" value="${LoanOffering.gracePeriodType.id}" />
			<html-el:hidden property="gracePeriodname" value="${LoanOffering.gracePeriodType.name}" />
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<script>intDedAtDisb();</script>
		</html-el:form>
	</tiles:put>
</tiles:insert>
