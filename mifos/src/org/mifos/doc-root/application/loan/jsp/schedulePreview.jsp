<!--
 
 * schedulePreview.jsp  version: xxx
 
 
 
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
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">

		<SCRIPT SRC="pages/application/loan/js/CreateLoanAccountInstallments.js"></SCRIPT>
		<SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
        <html-el:form action="/loanAccountAction.do">
		<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanAccountOwner')}" var="customer" />
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
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
															<img src="pages/framework/images/timeline/tick.gif" width="17" height="17">
														</td>
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
														<td>
															<img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17">
														</td>
														<td class="timelineboldorange">
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
										<c:if test="${requestScope.perspective == 'redoLoan'}">
                                        <tr>
                                            <td><span class="fontnormalRedBold"><mifos:mifoslabel name="loan.redo_loan_note"/></span></td>
                                        </tr>
                                        </c:if>
                                        <tr>
											<td class="headingorange">
												<span class="heading"> <mifos:mifoslabel name="accounts.create" /><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel name="accounts.account" />&nbsp;-&nbsp; </span>
												<mifos:mifoslabel name="loan.review&edit" />
											</td>
										</tr>
										<tr>
											<td class="fontnormal">
												<mifos:mifoslabel name="loan.review_payment_schedule" />
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
												<span class="fontnormalbold"> <mifos:mifoslabel name="loan.acc_owner" />: </span>
												<c:out value="${customer.displayName}" />
											</td>
										</tr>
									</table>
									<br>
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td width="100%" height="23" class="fontnormalbold">
												<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
												<mifos:mifoslabel name="loan.amt" />
												:&nbsp; <span class="fontnormal"> <c:out value="${BusinessKey.loanAmount}" /> <br> </span>
												<mifos:mifoslabel name="loan.proposed_date" />
												:&nbsp; <span class="fontnormal"> <c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,BusinessKey.disbursementDate)}" /> <br> </span> <span class="fontnormal"> <br> </span>
												<c:forEach items="${BusinessKey.accountFees}" var="feesSet">
													<c:if test="${feesSet.timeOfDisbursement}">
														<table cellpadding="0" cellspacing="0">
															<tr>
																<td class="fontnormalbold" align="left">
																	<c:out value="${feesSet.fees.feeName}" />
																	&nbsp;
																	<mifos:mifoslabel name="loan.amt" />
																	:&nbsp;
																</td>
																<td class="fontnormal" align="left">
																	<c:out value="${feesSet.accountFeeAmount}" />
																	&nbsp;&nbsp;
																</td>
															</tr>
														</table>
													</c:if>
												</c:forEach>
												<span class="fontnormal"> <br> </span>
												<table width="80%" border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td class="headingorange">
															<mifos:mifoslabel name="loan.inst" />
														</td>
														<td>
															&nbsp;
														</td>
													</tr>
													<tr>
														<td height="22" class="fontnormalbold" align="center">
															<mifos:mifoslabel name="loan.amount_due" />
														</td>
													</tr>
													<tr>
														<td align="center" class="blueline">
															<img src="pages/framework/images/trans.gif" width="5" height="3">
														</td>
													</tr>
                                                    <tr>
                                                    <td valign="top">
                                                        <mifoscustom:mifostabletag source="repaymentScheduleInstallments"
                                                                scope="session" xmlFileName="ProposedRepaymentSchedule.xml"
                                                                moduleName="accounts/loan" passLocale="true"/>
                                                    </td>
                                                    <c:if test="${requestScope.perspective == 'redoLoan'}">
                                                    <td valign="top">
                                                        <c:forEach var="paymentDataBeans" items="${loanAccountActionForm.paymentDataBeans}">
                                                            <date:datetag name="paymentDataBeans" indexed="true" property="date" />
                                                            <mifos:mifosdecimalinput name="paymentDataBeans" indexed="true" property="amount" />
                                                        </c:forEach>
                                                    </td>
                                                    </c:if>
													</tr>
												</table>
											</td>
										</tr>
									</table>
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
												<html-el:submit property="previewBtn" styleClass="buttn" style="width:70px;">
													<mifos:mifoslabel name="loan.preview" />
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
			<html-el:hidden property="method" value="preview" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
