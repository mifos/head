<!--
 
 * PreviewLoanAccountdt.jsp  version: xxx
 
 
 
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

<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>


<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	
		<SCRIPT SRC="pages/application/loan/js/PreviewLoanAccountdt.js"></SCRIPT>
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<html-el:form method="post" action="/loanAction.do" onsubmit="func_disableSubmitBtn('previewDetailsBtn')">
			<td height="822" align="left" valign="top" bgcolor="#FFFFFF" class="paddingleftmain">
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
  										<mifos:mifoslabel name="loan.preview" /><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /><mifos:mifoslabel name="loan.acc_info" />
									</td>
								</tr>
								<tr>
									<td class="fontnormal">
										<mifos:mifoslabel name="loan.reviewaccountinformation1" />
										<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
										<mifos:mifoslabel name="loan.reviewaccountinformation2" />
										<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
										<mifos:mifoslabel name="loan.reviewaccountinformation3" />
									</td>
								</tr>
								<tr>
									<td>
										<font class="fontnormalRedBold">
											<html-el:errors bundle="loanUIResources" /> 
										</font>
									</td>
								</tr>
							</table>
							<br>
							
							<table width="95%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="2">
										<span class="fontnormalbold"> 
											<mifos:mifoslabel name="loan.acc_owner" />:&nbsp;
										</span>
										<c:out value='${sessionScope.loanAccounts_Context.businessResults["customerMaster"].displayName}' />
									</td>
								</tr>
								<tr>
									<td width="100%" colspan="2" class="fontnormalbold">
										<mifos:mifoslabel name="${ConfigurationConstants.LOAN}"  /> <mifos:mifoslabel name="loan.loan_acc_details"  /> <br> <br>
										<span class="fontnormal"> 
											<mifos:mifoslabel name="${ConfigurationConstants.LOAN}"  /><mifos:mifoslabel name="loan.amt"  />:&nbsp;
											<span class="fontnormal"> 
												<c:out value="${requestScope.loan.loanAmount}" />
											</span><br>
											<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" />
											<mifos:mifoslabel name="loan.interest_rate" />:&nbsp;
											<span class="fontnormal"> 
												<c:out value="${requestScope.loan.interestRateAmount}" />
											</span><br>
											<mifos:mifoslabel name="loan.definst" />:&nbsp;
											<span class="fontnormal"> 
												<c:out value="${requestScope.loan.noOfInstallments}" /> 
											</span><br> 
											<mifos:mifoslabel name="loan.proposed_date" />:
											<span class="fontnormal">
												<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.loan.disbursementDate)}" /> <br>
											</span>
									
											<mifos:mifoslabel name="loan.grace_period" />:&nbsp; 
											<span class="fontnormal"> 
												<c:out value="${requestScope.loan.gracePeriodDuration}" />&nbsp;
												<mifos:mifoslabel name="loan.inst" /><br>
											</span>
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
							</table>
							<table width="95%" border="0" cellpadding="0" cellspacing="0">
								<tr id="Loan.PurposeOfLoan">
									<td class="fontnormalbold">
											<mifos:mifoslabel name="loan.business_work_act" keyhm="Loan.PurposeOfLoan" isManadatoryIndicationNotRequired="yes"/><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" isColonRequired="yes"/>&nbsp;
											<span class="fontnormal"> 
												<mifoscustom:lookUpValue id="${requestScope.loan.businessActivityId}"
													searchResultName="BusinessActivities">
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
									<td class="fontnormalbold"><br>
											<mifos:mifoslabel name="loan.collateral_notes" keyhm="Loan.CollateralNotes" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/>&nbsp; 
											<span class="fontnormal"><br>
												<c:out value="${requestScope.loan.collateralNote}" /> <br> <br>
												<html-el:button property="editButton" styleClass="insidebuttn" onclick="fnEdit(this.form)">
													<mifos:mifoslabel name="loan.edit_loan_acc" />
												</html-el:button> 
											</span>
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
										<html-el:submit property="previewDetailsBtn" styleClass="buttn" style="width:75px;" >
											<mifos:mifoslabel name="loan.submit" />
										</html-el:submit> &nbsp; 
										
										<html-el:button property="cancelButton" styleClass="cancelbuttn" style="width:70px;" onclick="javascript:fun_cancel(this.form)">
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
			<html-el:hidden property="method" value="update" />
			 <!--This hidden field is being used in the customPrevious method of the LoanAction class to discriminate the previous method call -->
			<html-el:hidden property="input" value="previewdt" />
			<html-el:hidden value="${sessionScope.loanActionForm.globalAccountNum}" property="globalAccountNum"/>
		</html-el:form>
	</tiles:put>
</tiles:insert>
