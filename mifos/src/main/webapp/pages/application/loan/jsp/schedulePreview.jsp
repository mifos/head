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
		<input type="hidden" id="page.id" value="SchedulePreview"/>
		<script src="pages/framework/js/conversion.js"></script>
		<script src="pages/framework/js/con_en.js"></script>
		<script src="pages/framework/js/con_${sessionScope["UserContext"].currentLocale}.js"></script>
		<c:choose>
            <c:when test="${requestScope.perspective == 'redoLoan'}">
                <script>
                function fun_cancel(form)
                {
                    location.href="AdminAction.do?method=load";
                }
                </script>
            </c:when>
            <c:otherwise>
                <script>
                function fun_cancel(form)
                {

                    form.action="custSearchAction.do";
                    form.method.value="loadMainSearch";
                    form.submit();
                }
                </script>
            </c:otherwise>
        </c:choose>
        <SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
        <html-el:form action="/loanAccountAction.do">
		<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanAccountOwner')}" var="customer" />
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="470" align="left" valign="top" bgcolor="#FFFFFF">
						<table width="95%" border="0" align="center" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center" class="heading">
									&nbsp;
								</td>
							</tr>
						</table>
						<table width="95%" border="0" align="center" cellpadding="0" cellspacing="0">
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
						<table width="95%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
							<tr>
								<td width="95%" height="24" align="left" valign="top" class="paddingleftCreates">
									<table width="98%" border="0" cellspacing="0" cellpadding="0">
										<c:if test="${requestScope.perspective == 'redoLoan'}">
                                        <tr>
                                            <td><span class="fontnormalRedBold"><mifos:mifoslabel name="loan.redo_loan_note"/></span></td>
                                        </tr>
                                        </c:if>
                                        <tr>
											<td class="headingorange">
												<span class="heading">
                                                    <c:choose>
                                                    <c:when test="${requestScope.perspective == 'redoLoan'}">
                                                        <mifos:mifoslabel name="admin.redo" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <mifos:mifoslabel name="accounts.create" />
                                                    </c:otherwise>
                                                    </c:choose>
                                                    <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
                                                    <mifos:mifoslabel name="accounts.account" />&nbsp;-&nbsp;
                                                </span>
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
												<font class="fontnormalRedBold"> <span id="schedulePreview.error.message"><html-el:errors bundle="loanUIResources" /></span> </font>
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
									<table width="98%" border="0" cellpadding="0" cellspacing="0">
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
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
													<tr>
                                                        <td class="headingorange">
															<mifos:mifoslabel name="loan.inst" />
														</td>
                                                    </tr>
                                                    <c:if test="${requestScope.perspective != 'redoLoan'}">
                                                    <tr>
                                                        <td height="22" class="fontnormalbold" align="center">
															<mifos:mifoslabel name="loan.amount_due" />
														</td>
                                                    </tr>
                                                    </c:if>
                                                    <tr>
                                                        <c:if test="${requestScope.perspective != 'redoLoan'}">
														<td align="center" class="blueline">
															<img src="pages/framework/images/trans.gif" width="5" height="3">
														</td>
                                                        </c:if>
                                                        <c:if test="${requestScope.perspective == 'redoLoan'}">
                                                        <td align="center" class="blueline">
															<img src="pages/framework/images/trans.gif" width="5" height="3">
														</td>
                                                        </c:if>
                                                    </tr>
                                                    <tr>
                                                    <c:if test="${requestScope.perspective != 'redoLoan'}">
                                                    <td valign="top" align="center">
                                                        <mifoscustom:mifostabletag source="repaymentScheduleInstallments"
                                                                scope="session" xmlFileName="ProposedRepaymentSchedule.xml"
                                                                moduleName="accounts/loan" passLocale="true"/>
                                                    </td>
                                                    </c:if>
                                                    <c:if test="${requestScope.perspective == 'redoLoan'}">
                                                    <td valign="top" align="center">
                                                        <table width="100%" border="0" cellpadding="3" cellspacing="0">
                                                          <tr>
                                                            <td colspan="3" />
                                                            <td colspan="1" />
                                                            <td colspan="4" height="22" class="fontnormalbold"><b><mifos:mifoslabel name="loan.amountdue" /></b></td>
                                                            <td colspan="1" height="22" align="center" class="fontnormalbold"><b><mifos:mifoslabel name="loan.amountpaid" /></b></td>
                                                          </tr>
                                                        <tr  class="drawtablerowbold"  >
                                                            <td width="5%" class="drawtablerow" align="center" ><b><mifos:mifoslabel name="loan.installments" /></b></td>
                                                            <td width="10%" class="drawtablerow" align="center" ><b><mifos:mifoslabel name="loan.duedate" /></b></td>
                                                            <td width="30%" class="drawtablerow" align="center" ><b><mifos:mifoslabel name="loan.dateofpayment" /></b></td>
                                                            <td width="15"> &nbsp; </td>
                                                            <td width="5%" class="drawtablerow" align="center" ><b><mifos:mifoslabel name="loan.principal" /></b></td>
                                                            <td width="5%" class="drawtablerow" align="center" ><b><mifos:mifoslabel name="loan.interest" /></b></td>
                                                            <td width="5%" class="drawtablerow" align="center" ><b><mifos:mifoslabel name="loan.fees" /></b></td>
                                                            <td width="5%" class="drawtablerow" align="center" ><b><mifos:mifoslabel name="loan.total" /></b></td>
                                                            <td width="30%" class="drawtablerow" align="center" ><b><mifos:mifoslabel name="loan.amount" /></b></td>
                                                        </tr>
                                                        <c:forEach var="paymentDataBeans" items="${loanAccountActionForm.paymentDataBeans}">
                                                        <tr>
                                                            <td class="drawtablerow" align="center">
                                                                <c:out value="${paymentDataBeans.installment.installment}" />
                                                            </td>
                                                            <td class="drawtablerow" align="center">
                                                                <c:out value="${paymentDataBeans.installment.dueDate}" />
                                                            </td>
                                                            <td class="drawtablerow" align="center">
                                                                <date:datetag name="paymentDataBeans" indexed="true" property="date" />
                                                            </td>
                                                            <td> &nbsp; </td>
                                                            <td class="drawtablerow" align="center">
                                                                <c:out value="${paymentDataBeans.installment.principal}" />
                                                            </td>
                                                            <td class="drawtablerow" align="center">
                                                                <c:out value="${paymentDataBeans.installment.interest}" />
                                                            </td>
                                                            <td class="drawtablerow" align="center">
                                                                <c:out value="${paymentDataBeans.installment.fees}" />
                                                            </td>
                                                            <td class="drawtablerow" align="center">
                                                                <c:out value="${paymentDataBeans.installment.total}" />
                                                            </td>
                                                            <td class="drawtablerow" align="center">
                                                                <mifos:mifosdecimalinput styleId="schedulePreview.input.loanAmount" name="paymentDataBeans" indexed="true" property="amount" size="10" />
                                                            </td>
                                                        </tr>
                                                        </c:forEach>
                                                        </table>
                                                    </td>
                                                    </c:if>
													</tr>
												</table>
											</td>
										</tr>
                                        <tr>
											<td align="center" class="blueline">
												&nbsp;
											</td>
										</tr>
									<br>
										<tr>
											<td align="center">
												<html-el:submit styleId="schedulePreview.button.preview" property="previewBtn" styleClass="buttn" >
													<mifos:mifoslabel name="loan.preview" />
												</html-el:submit>
												&nbsp;
												<html-el:button styleId="schedulePreview.button.cancel" property="cancelButton" onclick="javascript:fun_cancel(this.form)" styleClass="cancelbuttn" >
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
            <html-el:hidden property="perspective" value="${requestScope.perspective}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
