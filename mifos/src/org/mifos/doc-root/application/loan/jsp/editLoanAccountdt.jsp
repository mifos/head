<!--
 
 * editLoanAccountdt.jsp  version: xxx
 
 
 
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
<%@taglib uri="/tags/date" prefix="date"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<body onload="disableFields()">
		<SCRIPT SRC="pages/application/loan/js/EditLoanAccountdt.js"></SCRIPT>
		<SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<html-el:form  action="/loanAction.do" onsubmit="if(validateMyForm(disbursementDate,disbursementDateFormat,disbursementDateYY)){
				return gracePeriodDurationRangeCheck();
				}else{
				return false;
				} ">
		<td height="200" align="left" valign="top" bgcolor="#FFFFFF" class="paddingleftmain">		
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
			    <tr><td class="bluetablehead05"><span class="fontnormal8pt">
					<mifoscustom:getLoanHeader loanHeader='${sessionScope.header_get}'/>
					
					<html-el:link action="loanAction.do?method=get&globalAccountNum=${sessionScope.loanActionForm.globalAccountNum}">
							<c:out value="${requestScope.loan.loanOffering.prdOfferingName}" />
						</html-el:link></span>
				</td></tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="83%" class="headingorange">
								<span class="heading"> 
									<c:out value="${requestScope.loan.loanOffering.prdOfferingName}" />&nbsp;#
									<c:out value="${requestScope.loan.globalAccountNum}" />-&nbsp; 
								</span> 
								<mifos:mifoslabel name="loan.edit_loan_acc" />
							</td>
						</tr>
						<tr>
							<td class="fontnormal">
								<mifos:mifoslabel name="loan.edit_info" /> <br>
								<font color="#FF0000">*</font> 
								<mifos:mifoslabel name="loan.asterisk" />
							</td>
						</tr>
					</table>
					<br>
						<font class="fontnormalRedBold">
							<html-el:errors bundle="loanUIResources" /> 
						</font>		
					<table width="95%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td colspan="2" class="fontnormalbold">
								<mifos:mifoslabel name="${ConfigurationConstants.LOAN}"  /> <mifos:mifoslabel name="loan.loan_acc_details"  /><br><br>
							</td>
						</tr>
						<tr class="fontnormal">
							<td width="30%" align="right" class="fontnormal">
								<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" mandatory="yes" /> <mifos:mifoslabel name="loan.amt"  />:&nbsp;
							</td>
							<td valign="top">
								<mifos:mifosdecimalinput name="loanActionForm"
									property="loanAmount" value="${requestScope.loan.loanAmount}"
									min="${requestScope.loan.loanOffering.minLoanAmount}" 
									max="${requestScope.loan.loanOffering.maxLoanAmount}" /> 
								<mifos:mifoslabel name="loan.allowed_amount" />&nbsp;
								<c:out value="${requestScope.loan.loanOffering.minLoanAmount}" /> &nbsp; - &nbsp; 
								<c:out value="${requestScope.loan.loanOffering.maxLoanAmount}" />)
							</td>
							
						</tr>
						<tr class="fontnormal">
							<td width="30%" align="right" class="fontnormal">
							    <mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" mandatory="yes" />
								<mifos:mifoslabel name="loan.interest_rate"  />:&nbsp;
							</td>
							<td width="70%" valign="top">
								<mifos:mifosdecimalinput
									name="loanActionForm" property="interestRateAmount"
									value="${requestScope.loan.interestRateAmount}"
									max="${requestScope.loan.loanOffering.maxInterestRate}"
									min="${requestScope.loan.loanOffering.minInterestRate}" decimalFmt="10.5" /> 
															<mifos:mifoslabel name="loan.allowed_interest1" />
													<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" />
													<mifos:mifoslabel name="loan.allowed_interest2" />
						&nbsp;
								<c:out value="${requestScope.loan.loanOffering.minInterestRate}" />&nbsp; - &nbsp; 
								<c:out value="${requestScope.loan.loanOffering.maxInterestRate}" /> %)
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right" class="fontnormal">
								<span class="mandatorytext"></span>
								<mifos:mifoslabel name="loan.no_of_inst" mandatory="yes"/>:&nbsp;
							</td>
							<td valign="top">
								<mifos:mifosnumbertext name="loanActionForm"
									property="noOfInstallments"
									value="${requestScope.loan.noOfInstallments}"
									/> 
								<mifos:mifoslabel name="loan.allowed_no_of_inst" />&nbsp;
								<c:out value="${requestScope.loan.loanOffering.minNoInstallments}" />&nbsp; - &nbsp; 
								<c:out value="${requestScope.loan.loanOffering.maxNoInstallments}" />)
							</td>
						</tr>
						
						<tr class="fontnormal">
							<td align="right" class="fontnormal">
								<span class="mandatorytext"></span>
								<mifos:mifoslabel name="loan.proposed_date" mandatory="yes"/>:&nbsp;
							</td>
							<td valign="top">
								<date:datetag property="disbursementDate" name="loan"/>							
							</td>
						</tr>
						<tr class="fontnormal">
								<td align="right" class="fontnormal">
								<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" />
								<mifos:mifoslabel
												name="loan.interest_disb" />:&nbsp;</td>
								<td valign="top">
										<c:choose>
												<c:when test="${requestScope.loan.loanOffering.gracePeriodType.gracePeriodTypeId==1}">
													<c:choose>
														<c:when test="${param.method =='manage'}">
												<!-- the following is when it comes from manage method.-->
														<html-el:checkbox property="intrestAtDisbursement" value="1" name="loan" onclick="fun_setDisbursementdateDis();"/>
														<html-el:hidden  property="intrestAtDisbursement" value="${requestScope.loan.intrestAtDisbursement}" />
														</c:when>
														<c:otherwise>
														<!-- the following is when it comes from previous method. It reads the context.-->
															<html-el:checkbox property="intrestAtDisbursement" value="1" onclick="fun_setDisbursementdateDis();"/>
															<html-el:hidden  property="intrestAtDisbursement" value="${requestScope.loan.intrestAtDisbursement}" name="loanActionForm"/>
														</c:otherwise>
													</c:choose>
												</c:when>
												<c:when test="${param.method =='manage'}">
											<!-- the following is when it comes from manage method.-->
													<html-el:checkbox property="intrestAtDisbursement" value="1" name="loan" onclick="fn_setIntrestAtDisb();"/>
													<html-el:hidden  property="intrestAtDisbursement" value="${requestScope.loan.intrestAtDisbursement}" />
												</c:when>
												<c:otherwise>
												<!-- the following is when it comes from previous method. It reads the context.-->
													<html-el:checkbox property="intrestAtDisbursement" value="1" onclick="fn_setIntrestAtDisb();"/>
													<html-el:hidden  property="intrestAtDisbursement" value="${requestScope.loan.intrestAtDisbursement}" name="loanActionForm"/>
												</c:otherwise>
										</c:choose>
								</td>
						</tr>
						<tr class="fontnormal">
							<td align="right" class="fontnormal">
								<span class="mandatorytext"></span>
								<mifos:mifoslabel name="loan.grace_period" />:&nbsp;
							</td>
							<td valign="top">
								<html-el:hidden property="gracePeriodTypeId" value="${requestScope.loan.loanOffering.gracePeriodType.gracePeriodTypeId}" />
								<c:choose>
									<c:when test="${requestScope.loan.gracePeriodTypeId==1}">
										<mifos:mifosnumbertext name="loanActionForm"
											property="gracePeriodDuration"
											value="${requestScope.loan.gracePeriodDuration}" disabled="true"/>
										<mifos:mifoslabel name="loan.inst" />
									</c:when>
									<c:otherwise>
										<mifos:mifosnumbertext name="loanActionForm"
											property="gracePeriodDuration"
											value="${requestScope.loan.gracePeriodDuration}"/>
										<mifos:mifoslabel name="loan.inst" />
									</c:otherwise>
								</c:choose>
							</td>
							<!--This hidden field is used restore the value of grace period duration after unchecking the interest deducted at disbursement flag.-->
							<html-el:hidden property="inheritedGracePeriodDuration" value="${requestScope.loan.gracePeriodDuration}"/>
							<script language="javascript">
								//fun_onPageSubmit();
							</script>
						</tr>			
						<tr class="fontnormal">
							<td align="right" class="fontnormal">
								<mifos:mifoslabel keyhm="Loan.PurposeOfLoan" name="Loan.PurposeOfLoan"  bundle="loanUIResources" /><mifos:mifoslabel keyhm="Loan.PurposeOfLoan" name="${ConfigurationConstants.LOAN}" isColonRequired="yes" bundle="loanUIResources" isManadatoryIndicationNotRequired="yes"/>
							</td>
							<td valign="top">
								<mifos:select keyhm="Loan.PurposeOfLoan" name="loan" property="businessActivityId" style="width:136px;">
									<html-el:optionsCollection name="BusinessActivities" property="lookUpMaster" value="lookUpId" label="lookUpValue" />
								</mifos:select>
							</td>
						</tr>
						<c:if test="${requestScope.loan.accountStateId != 1 || requestScope.loan.accountStateId != 2}">
								<html-el:hidden value="${requestScope.loan.loanAmount}" property="loanAmount"/>
								<html-el:hidden value="${requestScope.loan.interestRateAmount}" property="interestRateAmount"/>
								<html-el:hidden value="${requestScope.loan.loanOffering.maxNoInstallments}" property="noOfInstallments"/>
								<html-el:hidden value="${requestScope.loan.businessActivityId}" property="businessActivityId"/>
								<html-el:hidden value="${requestScope.loan.gracePeriodDuration}" property="gracePeriodDuration"/>
							</c:if>
						<tr class="fontnormal">
							<td align="right" class="fontnormal">
								<mifos:mifoslabel keyhm="Loan.CollateralType" name="Loan.CollateralType" isColonRequired="yes" bundle="loanUIResources"/>
							</td>
							<td valign="top">
								<mifos:select keyhm="Loan.CollateralType" name="loan" property="collateralTypeId" style="width:136px;">
									<html-el:optionsCollection name="CollateralTypes" property="lookUpMaster" label="lookUpValue" value="id" />
								</mifos:select>
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right" valign="top" class="fontnormal">
								<mifos:mifoslabel keyhm="Loan.CollateralNotes" name="Loan.CollateralNotes" isColonRequired="yes" bundle="loanUIResources"/>
							</td>
							<td valign="top">
								<mifos:textarea keyhm="Loan.CollateralNotes" name="loanActionForm" property="collateralNote" value="${loan.collateralNote}" style="width:320px; height:110px;"></mifos:textarea>
							</td>
						</tr>
					</table>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center">
								<html-el:submit property="editDetailsBtn" styleClass="buttn" style="width:70px;">
									<mifos:mifoslabel name="loan.preview" />
								</html-el:submit> &nbsp; 
								<html-el:button property="cancelButton" onclick="javascript:fun_cancel(this.form)" styleClass="cancelbuttn" style="width:70px;">
									<mifos:mifoslabel name="loan.cancel" />
								</html-el:button>
							</td>
						</tr>
					</table>
					<br>
					<br>
					</td>
				</tr>
			</table>
		</td>
			<html-el:hidden property="method" value="preview"/>
			<!-- This hidden field is being used in the customPreview method of the LoanAction class to discriminate the preview method call-->
			<html-el:hidden property="input" value="editDetails" />
			<!--The following fields are being added as hidden fields because we need them for value object to action form conversion-->
			<html-el:hidden property="selectedPrdOfferingId" value="${requestScope.loan.loanOffering.prdOfferingId}" name="loanActionForm"/>
			<html-el:hidden value="${sessionScope.loanActionForm.globalAccountNum}" property="globalAccountNum"/>
			<html-el:hidden property="accountId" value="${requestScope.loan.accountId}" name="loanActionForm"/>
			<html-el:hidden property="officeId" value="${requestScope.loan.officeId}" name="loanActionForm"/>
			<html-el:hidden property="personnelId" value="${requestScope.loan.personnelId}" name="loanActionForm"/>
			<html-el:hidden property="accountStateId" value="${requestScope.loan.accountStateId}" name="loanActionForm"/>
			<html-el:hidden property="versionNo" value="${requestScope.loan.versionNo}" name="loanActionForm"/>
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
