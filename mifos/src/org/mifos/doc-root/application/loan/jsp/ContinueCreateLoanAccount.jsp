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

<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/tags/date" prefix="date"%>

<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
		<body onload="fun_onPageSubmit()">
			<SCRIPT SRC="pages/application/loan/js/ContinueCreateLoanAccount.js"></SCRIPT>
			<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
			<SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>
			<script>
		function fn(){
			
			var bool;
			bool = validateMyForm(document.loanActionForm.disbursementDate,
			document.loanActionForm.disbursementDateFormat,
			document.loanActionForm.disbursementDateYY);
			//alert("boolean is " + bool);
			if(bool==false){
				return false;
			}else{
				bool = fn_submit();
			}
			//alert("boolean is " + bool);							
			if(bool==true){
				func_disableSubmitBtn("continueButton");
				return true;
			}else{
				return false;
			}
			
			
		}
		
		function displayAmount(listBox, textBox , index ){
			var comboBox = document.getElementsByName(listBox)[0];
			var amountField = document.getElementsByName(textBox)[0];
			if(comboBox.selectedIndex==0){
				amountField.value = "";
				var span = document.getElementsByName("feeFormulaSpan"+index)[0];
				span.style.display="none";
				span.innerHTML="";
			}else{
				var indexSelectedFee = comboBox.selectedIndex-1;
				if (loanActionForm.selectedFeeAmntList[indexSelectedFee]!= undefined){
					var amount=loanActionForm.selectedFeeAmntList[indexSelectedFee].value;
					amountField.value = amount;
				}else
					amountField.value=loanActionForm.selectedFeeAmntList.value;
			 var span = document.getElementsByName("feeFormulaSpan"+index)[0];
			 if( comboBox[comboBox.selectedIndex]!= undefined)
				{
					var actualFeeId = comboBox[comboBox.selectedIndex].value;
					if(document.getElementsByName("FEE_"+actualFeeId)[0]!=null ){
						formula = document.getElementsByName("FEE_"+actualFeeId)[0].value;
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
		}
		
		
		function displayFormula(listBox, textBox , index ){
			var comboBox = document.getElementsByName(listBox)[0];
			var amountField = document.getElementsByName(textBox)[0];
			var span = document.getElementsByName("feeFormulaSpan"+index)[0];
			if( comboBox[comboBox.selectedIndex]!= undefined)
				{
					var actualFeeId = comboBox[comboBox.selectedIndex].value;
					if(document.getElementsByName("FEE_"+actualFeeId)[0]!=null ){
						formula = document.getElementsByName("FEE_"+actualFeeId)[0].value;
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
		
		
		function displayAdminFormula(feeId , index ){
			var span = document.getElementsByName("feeFormulaAdminSpan"+index)[0];
			if( feeId!= null)
				{	
					if(document.getElementsByName("FEE_"+feeId)[0]!=null ){
						formula = document.getElementsByName("FEE_"+feeId)[0].value;
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
		</script>
			<html-el:form method="post" action="/loanAction.do" onsubmit="return fn();">
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
							<html-el:hidden property="method" value="next" />
							<html-el:hidden property="input" value="nextPage" />
							<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
								<tr>
									<td width="70%" height="24" align="left" valign="top" class="paddingleftCreates">
										<table width="98%" border="0" cellspacing="0" cellpadding="3">
											<tr>
												<td class="headingorange">
													<span class="heading"> <mifos:mifoslabel name="accounts.create" /> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel name="accounts.account" />&nbsp;-&nbsp; </span>
													<mifos:mifoslabel name="loan.Enter" /> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel name="loan.acc_info" />
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
													<c:out value="${sessionScope.loanAccounts_customerMaster.displayName}" />
												</td>
											</tr>
										</table>

										<table width="93%" border="0" cellpadding="3" cellspacing="0">
											<tr class="fontnormal">
												<td width="30%" align="right" class="fontnormal">
													<span class="mandatorytext"></span>
												<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" mandatory="yes" />
												<mifos:mifoslabel name="loan.instancename"  />:
												</td>
												<td width="70%">
													<mifos:select name="loanActionForm" onchange="javascript:fun_refresh(this.form)" property="selectedPrdOfferingId" style="width:136px;">
														<html-el:options collection="loanPrdOfferings" property="prdOfferingId" labelProperty="prdOfferingName" />
													</mifos:select>
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
													<c:out value="${requestScope.loan.loanOffering.description}" />
												</td>
											</tr>

											<tr class="fontnormal">
												<td width="30%" align="right">
													<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" />
													<mifos:mifoslabel name="loan.interest_type" />:
												</td>
												<td width="70%" valign="top">
													<mifoscustom:lookUpValue id="${requestScope.loan.loanOffering.interestTypes.interestTypeId}" searchResultName="InterestTypes" mapToSeperateMasterTable="true">
													</mifoscustom:lookUpValue>
												</td>
											</tr>

									<%--		<tr class="fontnormal">
												<td align="right">
													<mifos:mifoslabel name="loan.interest_cal_payments" />:
												</td>
												<td valign="top">
													<mifoscustom:lookUpValue id="${requestScope.loan.loanOffering.interestCalcRule.interestCalcRuleId}" searchResultName="InterestCalcRule" mapToSeperateMasterTable="true">
													</mifoscustom:lookUpValue>
												</td>
											</tr> --%> 

											<tr class="fontnormal">
												<td align="right">
													<mifos:mifoslabel name="loan.freq_of_inst" />:
												</td>
												<td valign="top">
													<c:out value="${requestScope.loan.loanOffering.prdOfferingMeeting.meeting.meetingDetails.recurAfter}" />
													&nbsp;
													<c:choose>
														<c:when test="${requestScope.loan.loanOffering.prdOfferingMeeting.meeting.meetingDetails.recurrenceType.recurrenceId == '1'}">
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
														<c:when test="${requestScope.loan.loanOffering.prinDueLastInstFlag eq 1}">
															<mifos:mifoslabel name="loan.yes" />
														</c:when>
														<c:otherwise>
															<mifos:mifoslabel name="loan.no" />
														</c:otherwise>
													</c:choose>
												</td>
											</tr>
											<tr class="fontnormal">
												<td align="right">
													<mifos:mifoslabel name="loan.penalty_type" />:
												</td>
												<td valign="top">
													<c:out value="${requestScope.loan.loanOffering.penalty.penaltyType}" />
												</td>
											</tr>
											<tr class="fontnormal">
												<td align="right">
													<mifos:mifoslabel name="loan.grace_period_type" />:
												</td>
												<td valign="top">
													<mifoscustom:lookUpValue id="${requestScope.loan.loanOffering.gracePeriodType.gracePeriodTypeId}" searchResultName="GracePeriodTypes" mapToSeperateMasterTable="true">
													</mifoscustom:lookUpValue>
													<html-el:hidden property="gracePeriodTypeId" value="${requestScope.loan.loanOffering.gracePeriodType.gracePeriodTypeId}" />
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
													<c:choose>
														<c:when test="${param.method =='load'}">
															<!-- the following is when it comes from load method. It reads the loan offering and updates the same with user selection.-->
															<mifos:mifosdecimalinput name="loanActionForm" property="loanAmount" value="${requestScope.loan.loanOffering.defaultLoanAmount}" />

														</c:when>
														<c:otherwise>
															<!-- the following is when it comes from previous method. It reads the context.-->
															<mifos:mifosdecimalinput name="loanActionForm" property="loanAmount" value="${requestScope.loan.loanAmount}" />
														</c:otherwise>
													</c:choose>
													<mifos:mifoslabel name="loan.allowed_amount" />
													&nbsp;
													<c:out value="${requestScope.loan.loanOffering.minLoanAmount}" />
													&nbsp; - &nbsp;
													<c:out value="${requestScope.loan.loanOffering.maxLoanAmount}" />)
												</td>
											</tr>
											<tr class="fontnormal">
												<td width="30%" align="right" class="fontnormal">
													<span class="mandatorytext"></span>
													<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" mandatory="yes"/>
													<mifos:mifoslabel name="loan.interest_rate"/>:
												</td>
												<td width="70%" valign="top">
													<c:choose>
														<c:when test="${param.method =='load'}">
															<!-- the following is when it comes from load method. It reads the loan offering and updates the same with user selection.-->
															<mifos:mifosdecimalinput name="loanActionForm" property="interestRateAmount" value="${requestScope.loan.loanOffering.defInterestRate}" decimalFmt="10.5" />
														</c:when>
														<c:otherwise>
															<!-- the following is when it comes from previous method. It reads the context.-->
															<mifos:mifosdecimalinput name="loanActionForm" property="interestRateAmount" value="${requestScope.loan.interestRateAmount}" decimalFmt="10.5"/>
														</c:otherwise>
													</c:choose>
													<mifos:mifoslabel name="loan.allowed_interest1" />
													<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" />
													<mifos:mifoslabel name="loan.allowed_interest2" />
													&nbsp;
													<c:out value="${requestScope.loan.loanOffering.minInterestRate}" />
													&nbsp; - &nbsp;
													<c:out value="${requestScope.loan.loanOffering.maxInterestRate}" />
													%)
												</td>
											</tr>
											<tr class="fontnormal">
												<td align="right" class="fontnormal">
													<span class="mandatorytext"></span>
													<mifos:mifoslabel name="loan.no_of_inst" mandatory="yes" />:
												</td>
												<td valign="top">
													<c:choose>
														<c:when test="${param.method =='load'}">
															<!-- the following is when it comes from load method. It reads the loan offering and updates the same with user selection.-->
															<mifos:mifosnumbertext name="loanActionForm" property="noOfInstallments" value="${requestScope.loan.loanOffering.defNoInstallments}" />
														</c:when>
														<c:otherwise>
															<!-- the following is when it comes from previous method. It reads the context.-->
															<mifos:mifosnumbertext name="loanActionForm" property="noOfInstallments" value="${requestScope.loan.noOfInstallments}" />
														</c:otherwise>
													</c:choose>
													<mifos:mifoslabel name="loan.allowed_no_of_inst" />
													&nbsp;
													<c:out value="${requestScope.loan.loanOffering.minNoInstallments}" />
													&nbsp; - &nbsp;
													<c:out value="${requestScope.loan.loanOffering.maxNoInstallments}" />)
												</td>
											</tr>
											<tr class="fontnormal">

												<td align="right" class="fontnormal">
													<span class="mandatorytext"></span>
													<mifos:mifoslabel name="loan.proposed_date" mandatory="yes" />:
												</td>
												<td valign="top">
													<date:datetag property="disbursementDate" name="loan" />
												</td>
											</tr>
											<!-- TODO: Change the property of checkbox -->
											<tr class="fontnormal">
												<td align="right" class="fontnormal">
												    <mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" />
													<mifos:mifoslabel name="loan.interest_disb" />:
												</td>
												<td valign="top">
													<c:choose>
														<c:when test="${requestScope.loan.loanOffering.gracePeriodType.gracePeriodTypeId==1}">
															<c:choose>
																<c:when test="${param.method =='load'}">
																	<html-el:checkbox property="loanOffering.intDedDisbursementFlag" value="1" name="loan" onclick="fun_setDisbursementdateDisable();" />
																	<html-el:hidden property="intrestAtDisbursement" value="${requestScope.loan.loanOffering.intDedDisbursementFlag}" />
																</c:when>
																<c:otherwise>
																	<html-el:checkbox property="intrestAtDisbursement" value="1" onclick="fun_setDisbursedateDis();" />
																	<html-el:hidden property="intrestAtDisbursement" value="${requestScope.loan.intrestAtDisbursement}" name="loanActionForm" />
																</c:otherwise>
															</c:choose>
														</c:when>
														<c:when test="${param.method =='load'}">
															<!-- the following is when it comes from load method. It reads the loan offering and updates the same with user selection.-->
															<html-el:checkbox property="loanOffering.intDedDisbursementFlag" value="1" name="loan" onclick="fn_setIntrestAtDisbursement();" />
															<%-- This hidden field is required to set the property of the check box in the action form--%>
															<html-el:hidden property="intrestAtDisbursement" value="${requestScope.loan.loanOffering.intDedDisbursementFlag}" />
														</c:when>
														<c:otherwise>
															<!-- the following is when it comes from previous method. It reads the context.-->
															<html-el:checkbox property="intrestAtDisbursement" value="1" onclick="fn_setIntrestAtDisb();" />
															<%-- This hidden field is required to set the property of the check box in the action form--%>
															<html-el:hidden property="intrestAtDisbursement" value="${requestScope.loan.intrestAtDisbursement}" name="loanActionForm" />
														</c:otherwise>
													</c:choose>

												</td>
											</tr>


											<tr class="fontnormal">
												<td align="right" class="fontnormal">
													<span class="mandatorytext"></span>
													<mifos:mifoslabel name="loan.source_fund" mandatory="yes" />:
												</td>
												<td valign="top">
													<c:out value="${loanfn:getSourcesOfFund(requestScope.loan.loanOffering.loanOffeingFundSet)}" />
												</td>
											</tr>

											<tr class="fontnormal">
												<td align="right" class="fontnormal">
													<span class="mandatorytext"></span>
													<mifos:mifoslabel name="loan.grace_period" mandatory="yes" />:
												</td>
												<td valign="top">
													<c:choose>
														<c:when test="${requestScope.loan.loanOffering.gracePeriodType.gracePeriodTypeId==1}">
															<mifos:mifosnumbertext name="loanActionForm" property="gracePeriodDuration" value="0" disabled="true" />
														</c:when>
														<c:when test="${param.method =='load'}">
															<!-- the following is when it comes from load method. It reads the loan offering and updates the same with user selection.-->

															<mifos:mifosnumbertext name="loanActionForm" property="gracePeriodDuration" value="${requestScope.loan.loanOffering.gracePeriodDuration}" />
															<!--This hidden field is used restore the value of grace period duration after unchecking the interest deducted at disbursement flag.-->
															<input type="hidden" name="inheritedGracePeriodDuration" value="<c:out value='${requestScope.loan.loanOffering.gracePeriodDuration}'/>" />
														</c:when>
														<c:otherwise>
															<!-- the following is when it comes from previous method. It reads the context.-->
															<mifos:mifosnumbertext name="loanActionForm" property="gracePeriodDuration" value="${requestScope.loan.gracePeriodDuration}" />
															<!--This hidden field is used restore the value of grace period duration after unchecking the interest deducted at disbursement flag.-->
															<input type="hidden" name="inheritedGracePeriodDuration" value="<c:out value='${requestScope.loan.gracePeriodDuration}'/>" />
														</c:otherwise>
													</c:choose>

													<mifos:mifoslabel name="loan.inst" />

												</td>
											</tr>
											<tr class="fontnormal">
												<td align="right" class="fontnormal">
													<mifos:mifoslabel keyhm="Loan.PurposeOfLoan" name="Loan.PurposeOfLoan"  bundle="loanUIResources" /><mifos:mifoslabel keyhm="Loan.PurposeOfLoan" name="${ConfigurationConstants.LOAN}" isColonRequired="yes" bundle="loanUIResources" isManadatoryIndicationNotRequired="yes"/>
												</td>
												<td valign="top">
													<mifos:select keyhm="Loan.PurposeOfLoan" name="loanActionForm" property="businessActivityId" style="width:136px;">
														<html-el:optionsCollection name="BusinessActivities" property="lookUpMaster" value="lookUpId" label="lookUpValue" />
													</mifos:select>
												</td>
											</tr>

											<tr class="fontnormal">
												<td align="right" class="fontnormal">
													<mifos:mifoslabel keyhm="Loan.CollateralType" name="Loan.CollateralType" isColonRequired="yes" bundle="loanUIResources"/>
												</td>
												<td valign="top">
													<mifos:select keyhm="Loan.CollateralType" name="loanActionForm" property="collateralTypeId" style="width:136px;">
														<html-el:optionsCollection name="CollateralTypes" property="lookUpMaster" label="lookUpValue" value="id" />
													</mifos:select>
												</td>
											</tr>
											<tr class="fontnormal">
												<td align="right" valign="top" class="fontnormal">
													<mifos:mifoslabel keyhm="Loan.CollateralNotes" name="Loan.CollateralNotes" isColonRequired="yes" bundle="loanUIResources"/>
												</td>
												<td valign="top">
													<mifos:textarea keyhm="Loan.CollateralNotes" name="loanActionForm" property="collateralNote" style="width:320px; height:110px;">
													</mifos:textarea>
												</td>
											</tr>

										</table>
										<br>
										<c:forEach var="fee" items="${requestScope.feeFormulaList}" varStatus="loopStatus3">
												<input type="hidden" id="FEE_${fee.feeFormulaId}" value="${fee.feeFormulaName}" />
										</c:forEach>
										<!-- Administrative Set Fees -->
										<c:set scope="page" value="0" var="feeIndex" />
										<table width="93%" border="0" cellpadding="3" cellspacing="0">
											<tr>
												<td colspan="2" class="fontnormalbold">
													<mifos:mifoslabel name="loan.admin_set_fees"></mifos:mifoslabel>
													<br>
													<br>
												</td>
											</tr>

											<c:forEach var="prdOfferingFee" items="${requestScope.loan.loanOffering.prdOfferingFeesSet}" varStatus="loopStatus1">
												<bean:define id="ctr1" toScope="request">
													<c:out value="${loopStatus1.index}" />
												</bean:define>
												<tr>
													<td width="30%" align="right" class="fontnormal">
														<c:out value="${prdOfferingFee.fees.feeName}" />:
													</td>
													<td width="70%" class="fontnormal">
														<table width="500" border="0" cellspacing="0" cellpadding="0">
															<tr class="fontnormal">
																<c:choose>
																	<c:when test="${param.method == 'load'}">
																		<td width="115">
																			<mifos:mifosdecimalinput property="accountFees[${ctr1}].feeAmount" value="${prdOfferingFee.fees.rateOrAmount}" style="width:100px;" />
																			<html-el:hidden property="accountFees[${ctr1}].fees.feeId" value="${prdOfferingFee.fees.feeId}"></html-el:hidden>
																			<html-el:hidden property="accountFees[${ctr1}].fees.feeName" value="${prdOfferingFee.fees.feeName}"></html-el:hidden>
																		</td>
																	</c:when>
																	<c:otherwise>
																		<td width="115">
																			<mifos:mifosdecimalinput property="accountFees[${ctr1}].feeAmount" style="width:100px;" />
																			<html-el:hidden property="accountFees[${ctr1}].fees.feeId" value="${prdOfferingFee.fees.feeId}"></html-el:hidden>
																			<html-el:hidden property="accountFees[${ctr1}].fees.feeName" value="${prdOfferingFee.fees.feeName}"></html-el:hidden>
																		</td>
																	</c:otherwise>
																</c:choose>

																<td width="190">
																    <SPAN id="feeFormulaAdminSpan${loopStatus1.index}" class="fontnormal"></SPAN>
																	<SCRIPT> displayAdminFormula(${prdOfferingFee.fees.feeId},${loopStatus1.index}); </SCRIPT>
																	&nbsp;
																	<mifos:mifoslabel name="loan.periodicity" /> :
																	<c:choose>
																		<c:when test="${prdOfferingFee.fees.feeFrequency.feeFrequencyTypeId == 1}">
																			<c:out value="${prdOfferingFee.fees.feeFrequency.feeMeetingFrequency.feeMeetingSchedule}" />
																		</c:when>
																		<c:otherwise>
																			<mifos:mifoslabel name="Fees.onetime" />
																		</c:otherwise>
																	</c:choose>
																</td>
																<td width="162">
																	<html-el:checkbox property="accountFees[${ctr1}].checkToRemove" value="1" onclick="fn_setCheckToRemove('accountFees[${ctr1}].checkToRemove')" />
																	<html-el:hidden property="accountFees[${ctr1}].checkToRemove" name="loanActionForm" />
																	<mifos:mifoslabel name="loan.checkToRemove" />
																</td>

															</tr>
														</table>
													</td>
												</tr>

												<c:set scope="page" value="${ctr1+1}" var="feeIndex" />
											</c:forEach>
										</table>

										<!-- Administrative Set Fees End-->
										<br>
										<!-- Fee Type -->
										<table width="93%" border="0" cellpadding="3" cellspacing="0">
											<tr>
												<td colspan="4" class="fontnormalbold">
													<mifos:mifoslabel name="loan.apply_add_fees" />
													<br>
													<br>
												</td>
											</tr>

											<c:forEach begin="0" end="2" step="1" varStatus="loopStatus2">
												<bean:define id="ctr2" toScope="request">
													<c:out value="${loopStatus2.index}" />
												</bean:define>
												<td width="30%" align="right" class="fontnormal">
													<mifos:mifoslabel name="loan.fee_type"></mifos:mifoslabel>:
												</td>
												<td width="19%" class="fontnormal">


													<mifos:select name="loanActionForm" property='accountFees[${ctr2+feeIndex}].fees.feeId' onchange="displayAmount('accountFees[${ctr2+feeIndex}].fees.feeId', 'accountFees[${ctr2+feeIndex}].feeAmount',${loopStatus2.index} )">
														<html-el:options collection="applicableFees" property="feeId" labelProperty="feeName"></html-el:options>
													</mifos:select>
												</td>
												<td align="right" class="fontnormal">
													<mifos:mifoslabel name="loan.amount"></mifos:mifoslabel>:
												</td>
												<td>
													<mifos:mifosdecimalinput property='accountFees[${ctr2+feeIndex}].feeAmount' style="width:100px;" />
												</td>
												<td>
													<SPAN id="feeFormulaSpan${loopStatus2.index}" class="fontnormal"></SPAN>
													<script>displayFormula("accountFees["+${ctr2+feeIndex}+"].fees.feeId", "accountFees["+${ctr2+feeIndex}+"].feeAmount",${loopStatus2.index});</script>
												</td>
												<c:if test="${ctr2 == 0}">
													<c:forEach var="fee" items="${requestScope.applicableFees}" varStatus="loopStatus3">
														<bean:define id="ctr3" toScope="request">
															<c:out value="${loopStatus3.index}" />
														</bean:define>
														<html-el:hidden property='selectedFeeAmntList' value='${fee.rateOrAmount}' />
													</c:forEach>
												</c:if>
												</tr>
											</c:forEach>
										</table>
										<!-- Fees End -->

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
													<html-el:submit property="continueButton" styleClass="buttn" style="width:70px;" onclick="fun_next(this.form)">
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
				<html-el:hidden property="minLoanAmount" value="${requestScope.loan.loanOffering.minLoanAmount}" />
				<html-el:hidden property="maxLoanAmount" value="${requestScope.loan.loanOffering.maxLoanAmount}" />
				<html-el:hidden property="minInterestRate" value="${requestScope.loan.loanOffering.minInterestRate}" />
				<html-el:hidden property="maxInterestRate" value="${requestScope.loan.loanOffering.maxInterestRate}" />
				<html-el:hidden property="minNoInstallments" value="${requestScope.loan.loanOffering.minNoInstallments}" />
				<html-el:hidden property="maxNoInstallments" value="${requestScope.loan.loanOffering.maxNoInstallments}" />
			</html-el:form>
		</body>
	</tiles:put>
</tiles:insert>
