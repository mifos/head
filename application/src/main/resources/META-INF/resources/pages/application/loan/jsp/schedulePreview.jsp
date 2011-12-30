<%--
Copyright (c) 2005-2011 Grameen Foundation USA
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing
permissions and limitations under the License.

See also http://www.apache.org/licenses/LICENSE-2.0.html for an
explanation of the license and how it is applied.
--%>

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
<%@ taglib uri="/tags/date" prefix="date"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
		<span id="page.id" title="SchedulePreview"></span>
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
        <STYLE TYPE="text/css"><!-- @import url(pages/css/jquery/jquery-ui.css); --></STYLE>
        <script type="text/javascript" src="pages/js/jquery/jquery-1.4.2.min.js"></script>
        <script type="text/javascript" src="pages/js/jquery/jquery-ui.min.js"></script>
        <script type="text/javascript" src="pages/js/jquery/jquery.datePicker.configuration.js"></script>
        <script type="text/javascript" src="pages/js/jquery/jquery.ui.datepicker.min.js"></script>
        <script type="text/javascript" src="pages/js/jquery/jquery-ui-i18n.js"></script>
        <STYLE TYPE="text/css"><!-- @import url(pages/css/datepicker/datepicker.css); --></STYLE>
        <script type="text/javascript" src="pages/framework/js/CommonUtilities.js"></script>
		<!--[if IE]><script type="text/javascript" src="pages/js/jquery/jquery.bgiframe.js"></script><![endif]-->
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<script type="text/javascript" src="pages/application/loan/js/schedulePreview.js"></script>
		<script>
            $(function() {
                var locale = document.all.h_user_locale.value;
                if (locale != null) {
                    var country = locale.substring(locale.indexOf('_') + 1).toLowerCase();
                    $.datepicker.setDefaults($.datepicker.regional[country]);
                } else {
                    $.datepicker.setDefaults($.datepicker.regional[""]);
                }
            });
		</script>
        <html-el:form action="/loanAccountAction.do">
		<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanAccountOwner')}" var="customer" />
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanAmount')}" var="loanAmount" />
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'disbursementDate')}" var="disbursementDate" />
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'accountFees')}" var="accountFees" />
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
                                                    <mifos:mifoslabel name="accounts.accountLower" />&nbsp;-&nbsp;
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
												<mifos:mifoslabel name="loan.amt" /> :&nbsp;
												<span class="fontnormal"> <span id="schedulepreview.text.loanamount"><fmt:formatNumber value="${loanAmount}" /></span> <br> </span>
												<mifos:mifoslabel name="loan.proposed_date" /> :&nbsp;
												<span class="fontnormal">
												    <c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,disbursementDate)}" /> <br>
												</span>
												<span class="fontnormal"> <br> </span>
												<c:forEach items="${accountFees}" var="accountFee">
                                                    <table cellpadding="0" cellspacing="0">
                                                        <tr>
                                                            <td class="fontnormalbold" align="left">
                                                                <c:out value="${accountFee.feeName}" />
                                                                &nbsp;
                                                                <mifos:mifoslabel name="loan.amt" />
                                                                :&nbsp;
                                                            </td>
                                                            <td class="fontnormal" align="left">
                                                                <fmt:formatNumber value="${accountFee.amount}" />
                                                                &nbsp;&nbsp;
                                                            </td>
                                                        </tr>
                                                    </table>
												</c:forEach>
												<span class="fontnormal"> <br> </span>
                                            </td>
                                        </tr>
                                        <c:if test="${loanAccountActionForm.variableInstallmentsAllowed}">
                                        <tr class="fontnormal">
                                            <td width="100%" height="23" class="fontnormalbold">
                                                <mifos:mifoslabel
                                                    name="product.canConfigureVariableInstallments"
                                                    bundle="ProductDefUIResources" isColonRequired="yes" />&nbsp;
                                                <span class="fontnormal">
                                                    <c:choose>
                                                        <c:when test="${loanAccountActionForm.variableInstallmentsAllowed}">
                                                            <mifos:mifoslabel name="product.yes"
                                                                bundle="ProductDefUIResources" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <mifos:mifoslabel name="product.no"
                                                                bundle="ProductDefUIResources" />
                                                        </c:otherwise>
                                                    </c:choose>
                                                </span><br>
                                                <mifos:mifoslabel name="product.minimumGapBetweenInstallments"
                                                    bundle="ProductDefUIResources" isColonRequired="yes" />&nbsp;
                                                <span class="fontnormal">
                                                    <c:choose>
                                                        <c:when
                                                            test="${empty loanAccountActionForm.minimumGapInDays}">
                                                            <mifos:mifoslabel name="product.notApplicable"
                                                                bundle="ProductDefUIResources" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <fmt:formatNumber value="${loanAccountActionForm.minimumGapInDays}" />
                                                            <span id="days"> <mifos:mifoslabel
                                                                name="product.days" bundle="ProductDefUIResources" /> </span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </span><br>
                                                <mifos:mifoslabel name="product.maximumGapBetweenInstallments"
                                                    bundle="ProductDefUIResources" isColonRequired="yes" />&nbsp;
                                                <span class="fontnormal">
                                                    <c:choose>
                                                        <c:when
                                                            test="${empty loanAccountActionForm.maximumGapInDays}">
                                                            <mifos:mifoslabel name="product.notApplicable"
                                                                bundle="ProductDefUIResources" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <fmt:formatNumber value="${loanAccountActionForm.maximumGapInDays}" />
                                                            <span id="days"> <mifos:mifoslabel
                                                                name="product.days" bundle="ProductDefUIResources" /> </span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </span><br>
                                                <mifos:mifoslabel name="product.minimumInstallmentAmount"
                                                    bundle="ProductDefUIResources" isColonRequired="yes" />&nbsp;
                                                <span class="fontnormal">
                                                    <c:choose>
                                                        <c:when
                                                            test="${loanAccountActionForm.minInstallmentAmount.amountDoubleValue == 0.0}">
                                                            <mifos:mifoslabel name="product.notApplicable"
                                                                bundle="ProductDefUIResources" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <fmt:formatNumber value="${loanAccountActionForm.minInstallmentAmount}" />
                                                        </c:otherwise>
                                                    </c:choose>
                                                </span><br><br>
                                            </td>
                                        </tr>
                                        </c:if>
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
                                                        <c:choose>
                                                            <c:when test="${loanAccountActionForm.variableInstallmentsAllowed}">
                                                                <td valign="top" align="center">
                                                                    <table width="100%" border="0" cellpadding="3" cellspacing="0" id="installments">
                                                                    <tr  class="drawtablerowbold"  >
                                                                        <td width="10%" class="drawtablerow" align="center" ><b><mifos:mifoslabel name="loan.installments" /></b></td>
                                                                        <td width="22%" class="drawtablerow" align="center" ><b><mifos:mifoslabel name="loan.duedate" /></b></td>
                                                                        <td width="17%" class="drawtablerow" align="center" ><b><mifos:mifoslabel name="loan.principal" /></b></td>
                                                                        <td width="17%" class="drawtablerow" align="center" ><b><mifos:mifoslabel name="loan.interest" /></b></td>
                                                                        <td width="17%" class="drawtablerow" align="center" ><b><mifos:mifoslabel name="loan.fees" /></b></td>
                                                                        <td width="17%" class="drawtablerow" align="center" ><b><mifos:mifoslabel name="loan.total" /></b></td>
                                                                    </tr>
                                                                    <c:forEach var="installments" items="${loanAccountActionForm.installments}" varStatus="loopStatus">
                                                                    <tr>
                                                                        <td class="drawtablerow" align="center">
                                                                            <fmt:formatNumber value="${installments.installment}" />
                                                                        </td>
                                                                        <td class="drawtablerow" align="center">
                                                                            <html-el:text styleId="installment.dueDate.${loopStatus.index}" styleClass="date-pick" indexed="true" name="installments" property="dueDate" size="10" />
                                                                        </td>
                                                                        <td class="drawtablerow" align="center">
                                                                            <fmt:formatNumber value="${installments.principal}" />
                                                                        </td>
                                                                        <td class="drawtablerow" align="center">
                                                                            <fmt:formatNumber value="${installments.interest}" />
                                                                        </td>
                                                                        <td class="drawtablerow" align="center">
                                                                            <fmt:formatNumber value="${installments.fees}" />
                                                                        </td>
                                                                        <td class="drawtablerow" align="center">
                                                                            <c:choose>
                                                                                <c:when test="${loopStatus.index == (fn:length(loanAccountActionForm.installments) - 1)}">
                                                                                    <fmt:formatNumber value="${installments.total}" />
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                    <html-el:text styleId="installments.total" name="installments" indexed="true" property="total" size="10" />
                                                                                </c:otherwise>
                                                                            </c:choose>
                                                                        </td>
                                                                    </tr>
                                                                    </c:forEach>
                                                                </table>
                                                                </td>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <td valign="top" align="center">
                                                                    <mifoscustom:mifostabletag source="installments" scope="session"
                                                                            xmlFileName="ProposedRepaymentSchedule.xml"
                                                                            moduleName="org/mifos/accounts/loan/util/resources" passLocale="true"/>
                                                                </td>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:if>
                                                   </tr>
                                                    <tr>
                                                    <c:if test="${requestScope.perspective == 'redoLoan'}">
                                                    <td valign="top" align="center">
                                                        <table width="100%" border="0" id="scheduleTable" cellpadding="3" cellspacing="0">
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


                                                        <c:forEach var="paymentDataBeans" items="${loanAccountActionForm.paymentDataBeans}" varStatus="loopStatus">
                                                        <tr>
                                                            <td class="drawtablerow" align="center">
                                                                <fmt:formatNumber value="${paymentDataBeans.installment.installment}" />
                                                            </td>
                                                            <td class="drawtablerow" align="center">
                                                                <c:choose>
                                                                    <c:when test="${loanAccountActionForm.variableInstallmentsAllowed}">
                                                                        <html-el:text styleId="paymentDataBeans.dueDate.${loopStatus.index}"
                                                                        styleClass="date-pick-payment-data-beans" indexed="true"
                                                                        name="paymentDataBeans" property="dueDate" size="10" />
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <c:out value="${paymentDataBeans.installment.dueDate}" />
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <td class="drawtablerow" align="center">
                                                            	<html-el:text styleId="paymentDataBeans.date.${loopStatus.index}"
                                                            	styleClass="date-pick-payment-data-beans" indexed="true"
                                                            	name="paymentDataBeans" property="date" size="10" />
                                                            </td>
                                                            <td> &nbsp; </td>
                                                            <td class="drawtablerow" align="center">
                                                                <fmt:formatNumber value="${paymentDataBeans.installment.principal}" />
                                                            </td>
                                                            <td class="drawtablerow" align="center">
                                                                <fmt:formatNumber value="${paymentDataBeans.installment.interest}" />
                                                            </td>
                                                            <td class="drawtablerow" align="center">
                                                                <fmt:formatNumber value="${paymentDataBeans.installment.fees}" />
                                                            </td>
                                                            <td class="drawtablerow" align="center">
                                                                <c:choose>
                                                                    <c:when test="${loanAccountActionForm.variableInstallmentsAllowed == false || loopStatus.index == (fn:length(loanAccountActionForm.paymentDataBeans) - 1)}">
                                                                        <fmt:formatNumber value="${paymentDataBeans.total}" />
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <html-el:text styleId="paymentDataBeans.total"
                                                                        indexed="true" name="paymentDataBeans" property="total" size="10" />
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <td class="drawtablerow" align="center">
                                                                <html-el:text styleId="schedulePreview.input.loanAmount" name="paymentDataBeans" indexed="true" property="amount" size="10" />
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
											<td align="center">
												&nbsp;
											</td>
										</tr>
										<br/>
										<br/>

                                        <c:if test="${loanAccountActionForm.cashflowDataDtos != null}">
										<tr>
                                            <td>
                                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
													<tr>
                                                        <td class="headingorange">
															<mifos:mifoslabel name="loan.cashflowSummary" />
														</td>
                                                    </tr>
                                                    <tr>
											            <td align="center">
												            &nbsp;
											            </td>
										            </tr>
                                                    <tr>
                                                        <td valign="top" align="center">
                                                            <table width="100%" border="0" cellpadding="3" cellspacing="0" id="cashflow" name="cashflow">
                                                            <tr  class="drawtablerowbold"  >
                                                                <td width="10%" class="drawtablerow" align="left" ><b><mifos:mifoslabel name="loan.months" /></b></td>
                                                                <td width="22%" class="drawtablerow" align="center" ><b><mifos:mifoslabel name="loan.cumulativecashflow" /></b></td>
                                                                <td width="22%" class="drawtablerow" align="center" ><b><mifos:mifoslabel name="loan.cumulativecashflowminustotalinstallment" /></b></td>
                                                                <td width="22%" class="drawtablerow" align="center" ><b><mifos:mifoslabel name="loan.totalinstallmentaspercent" /></b></td>
                                                                <td width="17%" class="drawtablerow" align="left" ><b><mifos:mifoslabel name="loan.cashflownotes" /></b></td>
                                                            </tr>
                                                                <c:forEach var="cashflowDataDto" items="${loanAccountActionForm.cashflowDataDtos}" varStatus="loopStatus">
                                                                <tr>
                                                                    <td class="drawtablerow" align="left">
                                                                    	<c:out value="${cashflowDataDto.month}" />
                                                                        <c:out value="${cashflowDataDto.year}" />
                                                                    </td>
                                                                    <td class="drawtablerow" align="center">
                                                                        <fmt:formatNumber value="${cashflowDataDto.cumulativeCashFlow}" />
                                                                    </td>
                                                                    <td class="drawtablerow" align="center">
																		<fmt:formatNumber value="${cashflowDataDto.diffCumulativeCashflowAndInstallment}" />
                                                                    </td>
                                                                    <td class="drawtablerow" align="center">
                                                                        <fmt:formatNumber value="${cashflowDataDto.diffCumulativeCashflowAndInstallmentPercent}" />
                                                                    </td>
                                                                    <td class="drawtablerow" align="left">
                                                                        <c:out value="${cashflowDataDto.notes}" />
                                                                    </td>
                                                                </tr>
                                                                </c:forEach>
                                                            </table>
                                                        </td>
                                                    </tr>
												</table>
											</td>
										</tr>
                                        </c:if>
                                        <tr>
										    <td align="center">
										          &nbsp;
										    </td>
										</tr>
										<tr>
											<td align="center">
											    <c:if test="${loanAccountActionForm.variableInstallmentsAllowed && requestScope.perspective != 'redoLoan'}">
                                                    <html-el:submit styleId="schedulePreview.button.validate" property="validateBtn" styleClass="buttn" >
                                                        <mifos:mifoslabel name="loan.validate" />
                                                    </html-el:submit>
                                                    &nbsp;
                                                </c:if>
												<html-el:submit styleId="schedulePreview.button.preview" property="previewBtn" styleClass="buttn" >
													<mifos:mifoslabel name="loan.preview" />
												</html-el:submit>
												&nbsp;
												<html-el:button styleId="schedulePreview.button.cancel" property="cancelButton" onclick="javascript:fun_cancel(this.form)" styleClass="cancelbuttn" >
													<mifos:mifoslabel name="loan.cancel" />
												</html-el:button>
											</td>
										</tr>
									<br/>
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
