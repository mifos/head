<!--
 
 * editLoanAccount.jsp  version: xxx
 
 
 
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
<%@taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<body onload="disableFields()">
		<SCRIPT SRC="pages/application/loan/js/EditLoanAccount.js"></SCRIPT>
		<SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<html-el:form action="/loanAccountAction.do?method=managePreview"
			onsubmit="return (validateMyForm(disbursementDate,disbursementDateFormat,disbursementDateYY));">
			<td height="200" align="left" valign="top" bgcolor="#FFFFFF"
				class="paddingleftmain">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <customtags:headerLink />
					</span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="83%" class="headingorange"><span class="heading"> <c:out
								value="${sessionScope.BusinessKey.loanOffering.prdOfferingName}" />&nbsp;#
							<c:out value="${sessionScope.BusinessKey.globalAccountNum}" />-&nbsp;
							</span> <mifos:mifoslabel name="loan.edit_loan_acc" /></td>
						</tr>
						<tr>
							<td class="fontnormal"><mifos:mifoslabel name="loan.edit_info" />
							<br>
							<font color="#FF0000">*</font> <mifos:mifoslabel
								name="loan.asterisk" /></td>
						</tr>
					</table>
					<br>
					<font class="fontnormalRedBold"> <html-el:errors
						bundle="loanUIResources" /> </font>
					<table width="95%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
								name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel
								name="loan.loan_acc_details" /><br>
							<br>
							</td>
						</tr>
						<tr class="fontnormal">
							<td width="30%" align="right" class="fontnormal"><mifos:mifoslabel
								name="${ConfigurationConstants.LOAN}" mandatory="yes" /> <mifos:mifoslabel
								name="loan.amt" />:&nbsp;</td>
							<td valign="top"><mifos:mifosdecimalinput property="loanAmount"
								value="${sessionScope.loanAccountActionForm.loanAmount}" /> <mifos:mifoslabel
								name="loan.allowed_amount" />&nbsp; <c:out
								value="${sessionScope.BusinessKey.loanOffering.minLoanAmount}" />
							&nbsp; - &nbsp; <c:out
								value="${sessionScope.BusinessKey.loanOffering.maxLoanAmount}" />)
							</td>

						</tr>
						<tr class="fontnormal">
							<td width="30%" align="right" class="fontnormal"><mifos:mifoslabel
								name="${ConfigurationConstants.INTEREST}" mandatory="yes" /> <mifos:mifoslabel
								name="loan.interest_rate" />:&nbsp;</td>
							<td width="70%" valign="top"><mifos:mifosdecimalinput
								property="interestRate"
								value="${sessionScope.loanAccountActionForm.interestRate}"
								decimalFmt="10.5" /> <mifos:mifoslabel
								name="loan.allowed_interest1" /> <mifos:mifoslabel
								name="${ConfigurationConstants.INTEREST}" /> <mifos:mifoslabel
								name="loan.int_rate" />&nbsp; <c:out
								value="${sessionScope.BusinessKey.loanOffering.minInterestRate}" />&nbsp;
							- &nbsp; <c:out
								value="${sessionScope.BusinessKey.loanOffering.maxInterestRate}" />
							%)</td>
						</tr>
						<tr class="fontnormal">
							<td align="right" class="fontnormal"><span class="mandatorytext"></span>
							<mifos:mifoslabel name="loan.no_of_inst" mandatory="yes" />:&nbsp;
							</td>
							<td valign="top"><mifos:mifosnumbertext
								name="loanAccountActionForm" property="noOfInstallments"
								value="${sessionScope.loanAccountActionForm.noOfInstallments}" />
							<mifos:mifoslabel name="loan.allowed_no_of_inst" />&nbsp; <c:out
								value="${sessionScope.BusinessKey.loanOffering.minNoInstallments}" />&nbsp;
							- &nbsp; <c:out
								value="${sessionScope.BusinessKey.loanOffering.maxNoInstallments}" />)
							</td>
						</tr>

						<tr class="fontnormal">
							<td align="right" class="fontnormal"><span class="mandatorytext"></span>
							<mifos:mifoslabel name="loan.proposed_date" mandatory="yes" />:&nbsp;
							</td>
							<td valign="top"><date:datetag property="disbursementDate" /></td>
						</tr>
						<tr class="fontnormal">
							<td align="right" class="fontnormal"><mifos:mifoslabel
								name="${ConfigurationConstants.INTEREST}" /> <mifos:mifoslabel
								name="loan.interest_disb" />:&nbsp;</td>
							<td valign="top">
							<html-el:hidden property="intDedDisbursement" value="${sessionScope.loanAccountActionForm.intDedDisbursement}" />
							<input type="checkbox" name="intDedDisb" onclick="setGracePeriod()" /></td>
						</tr>
						<tr class="fontnormal">
							<td align="right" class="fontnormal"><span class="mandatorytext"></span>
							<mifos:mifoslabel name="loan.grace_period" />:&nbsp;</td>
							<td valign="top">
								<input type="text" name="gracePeriod" onchange="setGracePeriodDurationValue();"/></td>
						</tr>
						<html-el:hidden property="inheritedGracePeriodDuration"
								value="${sessionScope.loanAccountActionForm.gracePeriodDuration}" />
							<html-el:hidden property="gracePeriodDuration"/>
							<html-el:hidden property="gracePeriodTypeId" value="${sessionScope.BusinessKey.gracePeriodType.id}" />
						<script>setIntrestAtDisb();</script>
						<tr class="fontnormal">
							<td align="right" class="fontnormal"><mifos:mifoslabel
								keyhm="Loan.PurposeOfLoan" name="Loan.PurposeOfLoan"
								bundle="loanUIResources" /><mifos:mifoslabel
								keyhm="Loan.PurposeOfLoan" name="${ConfigurationConstants.LOAN}"
								isColonRequired="yes" bundle="loanUIResources"
								isManadatoryIndicationNotRequired="yes" /></td>
							<td valign="top"><mifos:select keyhm="Loan.PurposeOfLoan"
								property="businessActivityId" style="width:136px;">
								<html-el:options collection="BusinessActivities" property="id"
									labelProperty="name" />
							</mifos:select></td>
						</tr>
						<c:if
							test="${sessionScope.BusinessKey.accountState.id != 1 || sessionScope.BusinessKey.accountState.id != 2}">
							<html-el:hidden
								value="${sessionScope.loanAccountActionForm.loanAmount}"
								property="loanAmount" />
							<html-el:hidden
								value="${sessionScope.loanAccountActionForm.interestRate}"
								property="interestRate" />
							<html-el:hidden
								value="${sessionScope.loanAccountActionForm.noOfInstallments}"
								property="noOfInstallments" />
							<!-- <html-el:hidden value="${requestScope.loan.businessActivityId}" property="businessActivityId"/> -->
							<html-el:hidden
								value="${sessionScope.loanAccountActionForm.gracePeriodDuration}"
								property="gracePeriodDuration" />
							<html-el:hidden
								value="${sessionScope.loanAccountActionForm.intDedDisbursement}"
								property="intDedDisbursement" />
						</c:if>
						<tr class="fontnormal">
							<td align="right" class="fontnormal"><mifos:mifoslabel
								keyhm="Loan.CollateralType" name="Loan.CollateralType"
								isColonRequired="yes" bundle="loanUIResources" /></td>
							<td valign="top"><mifos:select keyhm="Loan.CollateralType"
								property="collateralTypeId" style="width:136px;">
								<html-el:options collection="CollateralTypes" property="id"
									labelProperty="name" />
							</mifos:select></td>
						</tr>
						<tr class="fontnormal">
							<td align="right" valign="top" class="fontnormal"><mifos:mifoslabel
								keyhm="Loan.CollateralNotes" name="Loan.CollateralNotes"
								isColonRequired="yes" bundle="loanUIResources" /></td>
							<td valign="top"><mifos:textarea keyhm="Loan.CollateralNotes"
								property="collateralNote" style="width:320px; height:110px;"></mifos:textarea></td>
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
							<td align="center"><html-el:submit property="editDetailsBtn"
								styleClass="buttn" style="width:70px;">
								<mifos:mifoslabel name="loan.preview" />
							</html-el:submit> &nbsp; <html-el:button property="cancelButton"
								onclick="javascript:fun_cancel(this.form)"
								styleClass="cancelbuttn" style="width:70px;">
								<mifos:mifoslabel name="loan.cancel" />
							</html-el:button></td>
						</tr>
					</table>
					<br>
					<br>
					</td>
				</tr>
			</table>
			</td>
			<html-el:hidden value="${sessionScope.BusinessKey.globalAccountNum}"
				property="globalAccountNum" />
			<html-el:hidden value="${sessionScope.BusinessKey.accountState.id}"
				property="accountStateId" />
			
		</html-el:form>
		</body>
	</tiles:put>
</tiles:insert>
