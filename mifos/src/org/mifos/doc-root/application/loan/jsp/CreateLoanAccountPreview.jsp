<!--
 
 * CreateLoanAccountPreview.js version: xxx
 
 
 
 * Copyright © 2005-2006 Grameen Foundation USA
 
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

<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>

<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
		
		<SCRIPT SRC="pages/application/loan/js/CreateLoanAccountPreview.js"></SCRIPT>
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<SCRIPT>
		function displayAdminFormula(feeId , index ){
			var span = document.getElementById("feeFormulaAdminSpan"+index);
			if( feeId!= null)
				{	
					if(document.getElementById("FEE_"+feeId)!=null ){
						formula = document.getElementById("FEE_"+feeId).value;
						span.style.display="block";
						span.innerHTML ="% of "+formula;
					}else{
						span.style.display="none";
						span.innerHTML="";
					}
				}
				else
				{	
					span.style.display="none";
					span.innerHTML="";
				}
		}
		</SCRIPT>
		<html-el:form method="post" action="/loanAction.do">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="470" align="left" valign="top" bgcolor="#FFFFFF">
					<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="heading">&nbsp;</td>
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
											<td><img src="pages/framework/images/timeline/tick.gif" width="17" height="17"></td>
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
											<td><img src="pages/framework/images/timeline/tick.gif" width="17" height="17"></td>
											<td class="timelineboldgray">
												<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
												<mifos:mifoslabel name="loan.acc_info" />
											</td>
										</tr>
									</table>
									</td>
									<td width="25%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/tick.gif" width="17" height="17"></td>
											<td class="timelineboldgray">
												<mifos:mifoslabel name="loan.review/edit_ins"  />
											</td>
										</tr>
									</table>
									</td>
									<td width="25%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17"></td>
											<td class="timelineboldorange">
												<mifos:mifoslabel name="loan.review&submit"  />
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
										<span class="heading">
											<mifos:mifoslabel name="accounts.create" /><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel name="accounts.account" />&nbsp;-&nbsp;
										</span>
     										<mifos:mifoslabel name="loan.preview" /> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel name="loan.acc_info" />
									</td>
								</tr>
								<tr>
									<td class="fontnormal"><mifos:mifoslabel name="loan.reviewaccontinfo" /></td>
								</tr>
								<tr>
											<td>
												<font class="fontnormalRedBold">
													<html-el:errors bundle="loanUIResources" /> 
												</font>
											</td>
								</tr>
								<tr>
									<td class="fontnormal"><br>
										<span class="fontnormalbold">
											<mifos:mifoslabel name="loan.acc_owner" />:
										</span>
										<c:out value="${sessionScope.loanAccounts_customerMaster.displayName}" />
									</td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td width="100%" height="23" class="fontnormalboldorange">
										<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
												<mifos:mifoslabel name="loan.acc_info" />
									</td>
								</tr>
								<tr>
									<td height="23" class="fontnormalbold">
										<span class="fontnormal"></span>
										<span class="fontnormal"></span>
										<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel name="loan.instancename" />:&nbsp;
										<span class="fontnormal">
											<c:out value="${requestScope.loan.loanOffering.prdOfferingName}" /><br><br>
										</span>
										<mifos:mifoslabel name="loan.instance_info" />
										<span class="fontnormal"><br></span>
										<mifos:mifoslabel name="loan.description" />:&nbsp;
										<span class="fontnormal"> 
											<c:out value="${requestScope.loan.loanOffering.description}" /> <br>
										</span>
										<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" />
										<mifos:mifoslabel name="loan.interest_type" />:&nbsp;
										<span class="fontnormal">
											<mifoscustom:lookUpValue id="${requestScope.loan.loanOffering.interestTypes.interestTypeId}"
												searchResultName="InterestTypes" mapToSeperateMasterTable="true">
											</mifoscustom:lookUpValue> <br>
										</span>
									<%--	<mifos:mifoslabel name="loan.interest_cal_payments" ></mifos:mifoslabel>:&nbsp; 
										<span class="fontnormal">
											<mifoscustom:lookUpValue
												id="${requestScope.loan.loanOffering.interestCalcRule.interestCalcRuleId}"
												searchResultName="InterestCalcRule"
												mapToSeperateMasterTable="true">
											</mifoscustom:lookUpValue> <br>
										</span>--%>
										<mifos:mifoslabel name="loan.freq_of_inst" />:&nbsp;
										<span class="fontnormal">
											<c:out value="${requestScope.loan.loanOffering.prdOfferingMeeting.meeting.meetingDetails.recurAfter}" />&nbsp;
												<c:choose>
													<c:when test="${requestScope.loan.loanOffering.prdOfferingMeeting.meeting.meetingDetails.recurrenceType.recurrenceId == '1'}">
														<mifos:mifoslabel name="loan.week(s)" />
													</c:when>
													<c:otherwise>
														<mifos:mifoslabel name="loan.month(s)" /> 	
													</c:otherwise>
												</c:choose><br>
										</span>
										<mifos:mifoslabel name="loan.principle_due" />:&nbsp;
										<span class="fontnormal">
											<c:choose>
													<c:when test="${requestScope.loan.loanOffering.prinDueLastInstFlag eq 1}">
														<mifos:mifoslabel name="loan.yes" />
													</c:when>
													<c:otherwise>
														<mifos:mifoslabel name="loan.no" />
													</c:otherwise>
											</c:choose> <br>
										</span>
										<mifos:mifoslabel name="loan.penalty_type" />:&nbsp;
										<span class="fontnormal"> 
											<c:out value="${requestScope.loan.loanOffering.penalty.penaltyType}" /> <br>
										</span>
										<mifos:mifoslabel name="loan.grace_period_type" />:&nbsp; 
										<span class="fontnormal">
											<mifoscustom:lookUpValue
													id="${requestScope.loan.gracePeriodTypeId}"
													searchResultName="GracePeriodTypes"
													mapToSeperateMasterTable="true">
											</mifoscustom:lookUpValue> <br>
										<br>
										<br>
										</span>
									</td>
								</tr>
								<tr>
									<td class="fontnormalbold">
											<mifos:mifoslabel name="loan.amount" />:&nbsp; 
											<span class="fontnormal">
												<c:out value="${requestScope.loan.loanAmount}" /> 
												<mifos:mifoslabel name="loan.allowed_amount" ></mifos:mifoslabel>&nbsp;
												<c:out value="${requestScope.loan.loanOffering.minLoanAmount}" /> &nbsp; - &nbsp; 
												<c:out value="${requestScope.loan.loanOffering.maxLoanAmount}" />)
											</span>
									</td>
								</tr>
								<tr>
									<td class="fontnormalbold">
											<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" />
											<mifos:mifoslabel name="loan.interest_rate" />:&nbsp;
											<span class="fontnormal">
												<c:out value="${requestScope.loan.interestRateAmount}" /> 
																			<mifos:mifoslabel name="loan.allowed_interest1" />
													<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" />
													<mifos:mifoslabel name="loan.allowed_interest2" />
						:&nbsp;
												<c:out value="${requestScope.loan.loanOffering.minInterestRate}" />&nbsp; - &nbsp; 
												<c:out value="${requestScope.loan.loanOffering.maxInterestRate}" />)
											</span>
								</td>
								</tr>
								<tr>
									<td class="fontnormalbold">
											<mifos:mifoslabel name="loan.no_of_inst" />:&nbsp;
											<span class="fontnormal"> 
												<c:out value="${requestScope.loan.noOfInstallments}" /> 
												<mifos:mifoslabel name="loan.allowed_no_of_inst" ></mifos:mifoslabel>&nbsp;
												<c:out value="${requestScope.loan.loanOffering.minNoInstallments}" />&nbsp; - &nbsp; 
												<c:out value="${requestScope.loan.loanOffering.maxNoInstallments}" />)
											</span>
								</td>
								</tr>
								<tr>
									<td class="fontnormalbold">
									<%--
										<mifos:mifoslabel name="loan.coll_sheet_type" />:&nbsp;
										<span class="fontnormal"> Collection sheet type 1</span>
										<span class="fontnormal"> <br> </span>
										<span class="fontnormal"></span>
									--%>
										<mifos:mifoslabel name="loan.proposed_date" />:&nbsp;
										<span class="fontnormal"> 
											<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.loan.disbursementDate)}" /> 
										</span>
								</td>
								</tr>
								<tr>
									<td class="fontnormalbold">
									<mifos:mifoslabel name="loan.grace_period" />:&nbsp;
										<span class="fontnormal"> 
											<c:out value="${requestScope.loan.gracePeriodDuration}" />&nbsp;
											<mifos:mifoslabel name="loan.inst" ></mifos:mifoslabel>
										</span>
								</td>
								</tr>
								<tr>
									<td class="fontnormalbold">
										<mifos:mifoslabel name="loan.source_fund" />:&nbsp;
										<span class="fontnormal"> 
											<c:out value="${loanfn:getSourcesOfFund(requestScope.loan.loanOffering.loanOffeingFundSet)}" />
										</span>
								</td>
								</tr>
								<tr>
									<td class="fontnormalbold">
										<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" />
										<mifos:mifoslabel name="loan.interest_disb" />:&nbsp;
										<span class="fontnormal">
										<c:choose>
												<c:when test="${requestScope.loan.intrestAtDisbursement eq '1'}">
													<mifos:mifoslabel name="loan.yes" />
												</c:when>
												<c:otherwise>
													<mifos:mifoslabel name="loan.no" />
												</c:otherwise>
										</c:choose>
										</span>
								</td>
								</tr>
								<tr id="Loan.PurposeOfLoan">
									<td class="fontnormalbold">
										<mifos:mifoslabel name="loan.business_work_act" keyhm="Loan.PurposeOfLoan" isManadatoryIndicationNotRequired="yes"/><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" isColonRequired="yes" />&nbsp;
										<span class="fontnormal"> 
											<mifoscustom:lookUpValue
													id="${requestScope.loan.businessActivityId}"
													searchResultName="BusinessActivities" >
											</mifoscustom:lookUpValue>
										</span>
								</td>
								</tr>
								<tr id="Loan.CollateralType">
									<td class="fontnormalbold">
										<mifos:mifoslabel name="loan.collateral_type" keyhm="Loan.CollateralType" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/>&nbsp;
											<span class="fontnormal"> 
												<mifoscustom:lookUpValue
													id="${requestScope.loan.collateralTypeId}"
													searchResultName="CollateralTypes"
													mapToSeperateMasterTable="true">
												</mifoscustom:lookUpValue>
											</span>
									</td>
								</tr>
								<tr id="Loan.CollateralNotes">
									<td class="fontnormalbold">
											<mifos:mifoslabel name="loan.collateral_notes" keyhm="Loan.CollateralNotes" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/>&nbsp;
											<span class="fontnormal"><br>
									      		<c:out value="${requestScope.loan.collateralNote}" />
									      	</span>
								</td>
								</tr>
								<tr>
									<td class="fontnormalbold"><br>
									      	<mifos:mifoslabel name="loan.charged_applied" /><br>
									      	<c:forEach var="fee" items="${requestScope.feeFormulaList}" varStatus="loopStatus3">
												<input type="hidden" id="FEE_${fee.feeFormulaId}" value="${fee.feeFormulaName}" />
											</c:forEach>
											<c:forEach items="${requestScope.loan.accountFeesSet}" var="feesSet">												
														<c:if test="${feesSet.fees.feeId != null && (feesSet.checkToRemove==null || feesSet.checkToRemove=='0')}">												
														<!--<span class="fontnormal">-->
														 <table cellpadding="0" cellspacing="0">
														  <tr>
														  	<td  class="fontnormalbold" align="left">
														  	<c:out value="${feesSet.fees.feeName}" />&nbsp; 										
															<mifos:mifoslabel name="loan.amt" />:&nbsp;    
														  	</td>
															<td  class="fontnormal" align="left">
											 				<c:out value="${feesSet.accountFeeAmount}" />&nbsp;&nbsp;
											 				</td>
											 				<td  class="fontnormal" align="left">
															<!--<SPAN id="feeFormulaAdminSpan${feesSet.fees.feeId}" class="fontnormal"/></SPAN>
															<SCRIPT> displayAdminFormula(${feesSet.fees.feeId},${feesSet.fees.feeId}); </SCRIPT> -->									
															</td>
															<td class="fontnormal" align="left">
											 				&nbsp;<mifos:mifoslabel name="loan.periodicity" />:
											 				<c:choose>
																<c:when test="${feesSet.fees.feeFrequency.feeFrequencyTypeId == '1'}">
																	<c:out value="${feesSet.fees.feeFrequency.feeMeetingFrequency.feeMeetingSchedule}"/>
																</c:when>
																<c:otherwise>
																	<mifos:mifoslabel name="loan.periodicityTypeFlat" />
																</c:otherwise>
															</c:choose>
											 				<br>
											 				</td>
											 			 </table> 
											 			<!--</span>-->
													 </c:if>
											</c:forEach>
											<span class="fontnormal"> <br> <br> </span>
									<table width="80%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="headingorange">
												<mifos:mifoslabel name="loan.install_paid" ></mifos:mifoslabel>
											</td>
											<td>&nbsp;</td>
										</tr>
										<tr>
											<td height="22" class="fontnormalbold" align="center">
												<mifos:mifoslabel name="loan.amount_due"  />
											</td>
										</tr>
										<tr>
											<td align="center" class="blueline"><img src="pages/framework/images/trans.gif" width="5" height="3"></td>
										</tr>
										<tr>
											<td valign="top">
												<mifoscustom:mifostabletag source="repaymentScheduleInstallments" scope="request" xmlFileName="ProposedRepaymentSchedule.xml" moduleName="accounts/loan" passLocale="true"/>
											</td>
										</tr>
									</table>
									<span class="fontnormal"> <br>
									<br>
									<html-el:button property="editButton" styleClass="insidebuttn" 
											onclick="fnEdit(this.form)">
											<mifos:mifoslabel name="loan.edit_loan_acc" />
									</html-el:button> </span>
									</td>
								</tr>
							</table>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center" class="blueline">&nbsp;</td>
								</tr>
							</table>
							<br>
							<html-el:hidden property="method" value=""/>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center">
										<html-el:button property="saveForLaterButton" styleClass="buttn"
											style="width:100px;" onclick="javascript:fun_saveForLater(this.form)">
											<mifos:mifoslabel name="loan.saveForLater" />
										</html-el:button> &nbsp; 
									
										<c:choose >
											<c:when test='${sessionScope.loanAccounts_Context.businessResults["isPendingApproval"] == "true"}'>
												<html-el:button
													property="submitForApprovalButton" styleClass="buttn"
													style="width:130px;" onclick="javascript:fun_submitForApproval(this.form)">
													<mifos:mifoslabel name="loan.submitForApproval" />
												</html-el:button>
											</c:when>
											<c:otherwise>
												<html-el:button
													property="approvedButton" styleClass="buttn"
													style="width:130px;" onclick="javascript:fun_approved(this.form)">
													<mifos:mifoslabel name="loan.approved" />
												</html-el:button>
											</c:otherwise>
										</c:choose> &nbsp; 
										
										<html-el:button property="cancelButton" styleClass="cancelbuttn" style="width:70px;" 
											onclick="javascript:fun_cancel(this.form)">
											<mifos:mifoslabel name="loan.cancel" />
										</html-el:button>
									</td>
									<!-- This hidden field is being used in the customPreview method of the LoanAction class to discriminate the preview method call-->
									<html-el:hidden property="input" value="accountPreview" />
									<html-el:hidden  value="" property="stateSelected"/>									
								</tr>
							</table>
							<br>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
		</html-el:form>
	</tiles:put>
</tiles:insert>
