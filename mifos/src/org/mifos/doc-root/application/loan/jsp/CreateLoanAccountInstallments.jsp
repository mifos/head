<!--
 
 * CreateLoanAccountInstallments.jsp  version: xxx
 
 
 
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
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>

<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">

		<SCRIPT SRC="pages/application/loan/js/CreateLoanAccountInstallments.js"></SCRIPT>
		<SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<html-el:form method="post" action="/loanAction.do" onsubmit="func_disableSubmitBtn('previewBtn')">
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
											<td class="timelineboldgray"><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
												<mifos:mifoslabel name="loan.acc_info" /></td>
										</tr>
									</table>
									</td>
									<td width="25%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17"></td>
											<td class="timelineboldorange"><mifos:mifoslabel name="loan.review/edit_ins"  /></td>
										</tr>
									</table>
									</td>
									<td width="25%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17"></td>
											<td class="timelineboldorangelight"><mifos:mifoslabel name="loan.review&submit"  /></td>
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
											<mifos:mifoslabel name="accounts.create" /><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel name="accounts.account" />&nbsp;-&nbsp; </span>
											<mifos:mifoslabel name="loan.review&edit"  />
									</td>
								</tr>
								<tr>
									<td class="fontnormal"><mifos:mifoslabel name="loan.review_payment_schedule"  /></td>
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
											<mifos:mifoslabel name="loan.acc_owner"  />: 
										</span> 
										<c:out value="${sessionScope.loanAccounts_customerMaster.displayName}" />
									</td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td width="100%" height="23" class="fontnormalbold">
										<mifos:mifoslabel name="${ConfigurationConstants.LOAN}"  /> <mifos:mifoslabel name="loan.amt"  />:&nbsp;
										<span class="fontnormal"> 
											<c:out value="${requestScope.loan.loanAmount}" /> <br>
										</span>
										<mifos:mifoslabel name="loan.proposed_date" />:&nbsp;
										<span class="fontnormal"> 
											<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.loan.disbursementDate)}" /> <br>
											
										</span>
										<span class="fontnormal"> <br></span>
										<c:forEach items="${requestScope.loan.accountFeesSet}" var="feesSet">
													<c:if test="${feesSet.fees.feeId != null && (feesSet.checkToRemove==null || feesSet.checkToRemove=='0') && (feesSet.fees.feeFrequency.feeFrequencyTypeId == '2' && feesSet.fees.feeFrequency.feePaymentId == '2')}">												
														<c:set var="doesTimeOfDisbursmentFeeExist" value="Yes" scope="page"></c:set>
													 </c:if>
										</c:forEach>										
										<c:if test="${doesTimeOfDisbursmentFeeExist=='Yes'}">
											<mifos:mifoslabel name="loan.TimeOfDisbursmentFees" />		
										</c:if>
										<c:forEach items="${requestScope.loan.accountFeesSet}" var="feesSet">
													<c:if test="${feesSet.fees.feeId != null && (feesSet.checkToRemove==null || feesSet.checkToRemove=='0') && (feesSet.fees.feeFrequency.feeFrequencyTypeId == '2' && feesSet.fees.feeFrequency.feePaymentId == '2')}">												
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
											 				</tr>
											 			 </table> 
											 			<!--</span>-->
													 </c:if>
											</c:forEach>
										<span class="fontnormal"> <br> </span>
									<table width="80%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="headingorange"><mifos:mifoslabel name="loan.inst" /></td>
											<td>&nbsp;</td>
										</tr>
										<tr>
											<td height="22" class="fontnormalbold" align="center"><mifos:mifoslabel name="loan.amount_due"  /></td>
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
									</td>
								</tr>
							</table>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center" class="blueline">&nbsp;</td>
								</tr>
							</table>
							<br>
							<!-- Preview is the method called on the click of Continue button-->
							<html-el:hidden property="method" value="preview" />
							<!-- This hidden field is being used in the customPreview method of the LoanAction class to discriminate the preview method call-->
							<html-el:hidden property="input" value="installation" />
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center">
										<html-el:submit property="previewBtn" styleClass="buttn" style="width:70px;"> 
											<mifos:mifoslabel name="loan.preview" />
										</html-el:submit> &nbsp; 
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
		</html-el:form>
	</tiles:put>
</tiles:insert>
