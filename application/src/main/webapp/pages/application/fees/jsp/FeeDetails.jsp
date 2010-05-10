<%--
Copyright (c) 2005-2009 Grameen Foundation USA
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
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.FeesUIResources"/>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
	<span id="page.id" title="FeeDetails" />
	
		<script src="pages/application/fees/js/Fees.js"></script>
		<html-el:form action="/feeaction.do">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"> <html-el:link href="feeaction.do?method=cancelCreate&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="Fees.admin" bundle="FeesUIResources">
								</mifos:mifoslabel>
							</html-el:link> / <html-el:link href="feeaction.do?method=viewAll&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="Fees.viewfees" bundle="FeesUIResources">
								</mifos:mifoslabel>
							</html-el:link> / </span> <span class="fontnormal8ptbold"> <c:out value="${model.name}"></c:out> </span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="67%" height="23" class="headingorange">
									<c:out value="${model.name}"></c:out>
								</td>
								<td width="33%" align="right">
									<html-el:link href="feeaction.do?method=manage&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}&feeId=${model.id}">
										<mifos:mifoslabel name="Fees.editfeeinformation" bundle="FeesUIResources">
										</mifos:mifoslabel>
									</html-el:link>
								</td>
							</tr>
						</table>
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td height="23" class="fontnormal">
									<span class="fontnormal"> </span>
									<br>
									<font class="fontnormalRedBold"> <html-el:errors bundle="FeesUIResources" /> </font> <span class="fontnormal"> <c:choose>
											<c:when test="${model.active == true}">
												<img src="pages/framework/images/status_activegreen.gif" width="8" height="9">
											</c:when>
											<c:otherwise>
												<img src="pages/framework/images/status_closedblack.gif" width="8" height="9">
											</c:otherwise>
										</c:choose> <c:out value="${model.feeStatus.name}" /> </span>
									<br>
									<br>
									<mifos:mifoslabel name="Fees.feeappliesto" bundle="FeesUIResources"></mifos:mifoslabel>
									<c:out value="${model.categoryType}" />
									<br>
									<c:choose>
										<c:when test="${!model.loanCategoryType}">
											<mifos:mifoslabel name="Fees.defaultFees" bundle="FeesUIResources"></mifos:mifoslabel>
											<c:choose>
												<c:when test="${model.customerDefaultFee}">
													<mifos:mifoslabel name="Fees.DefaultFeeYes" />
												</c:when>
												<c:otherwise>
													<mifos:mifoslabel name="Fees.DefaultFeeNo" />
												</c:otherwise>
											</c:choose>
											<br>
										</c:when>
									</c:choose>
									<mifos:mifoslabel name="Fees.frequency" bundle="FeesUIResources"></mifos:mifoslabel>
									<c:out value="${model.feeFrequency.type}" />
									<br>
									<mifos:mifoslabel name="Fees.timeToCharge" bundle="FeesUIResources"></mifos:mifoslabel>
									<c:choose>
										<c:when test="${model.oneTime}">
											<c:out value="${model.feeFrequency.payment}" />
										</c:when>
										<c:otherwise>
											<mifos:mifoslabel name="Fees.labelRecurEvery" />
											<c:out value="${model.feeFrequency.recurAfterPeriod}"></c:out>
											<c:if test="${model.feeFrequency.weekly}">
												<mifos:mifoslabel name="Fees.labelWeeks" />
											</c:if>
											<c:if test="${model.feeFrequency.monthly}">
												<mifos:mifoslabel name="Fees.labelMonths" />
											</c:if>
										</c:otherwise>
									</c:choose>
                                    <c:if test='${sessionScope.isMultiCurrencyEnabled && model.loanCategoryType && !model.rateBasedFee }'>
									<br>
                                      <span class="fontnormal"> 
                                      <mifos:mifoslabel name="Fees.currency" bundle="FeesUIResources" isColonRequired="yes" />
                                      <c:out value="${model.amount.currency.currencyCode}" />
                                      </span>
                                    </c:if>
                                    <br>
									<br>
									<span class="fontnormalbold"> <mifos:mifoslabel name="Fees.feecalculation" bundle="FeesUIResources">
										</mifos:mifoslabel> </span>
									<br>
									<c:choose>
										<c:when test="${model.rateBasedFee}">
											<fmt:message key="Fees.amountCalcComplete">
												<%-- FIXME: use a rate decimal formatter --%>
												<fmt:param><c:out value="${model.rate}" /></fmt:param>
												<fmt:param><c:out value="${model.feeFormula.name}" /></fmt:param>
											</fmt:message>
										</c:when>
										<c:otherwise>
											<mifos:mifoslabel name="Fees.amount" bundle="FeesUIResources"></mifos:mifoslabel>
											<c:out value="${model.amount}" />
										</c:otherwise>
									</c:choose>
									<br>
									<br>
									<span class="fontnormalbold"> <mifos:mifoslabel name="Fees.accounting" bundle="FeesUIResources">
										</mifos:mifoslabel> </span>
									<br>
									<mifos:mifoslabel name="Fees.GLCode" bundle="FeesUIResources"></mifos:mifoslabel>

									<c:out value="${model.glCode}" />
								</td>
								<td height="23" align="right" valign="top" class="fontnormalbold">
									<span class="fontnormal"> <br> <br> </span>
								</td>
							</tr>
							<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
						</table>
						<br>
					</td>
				</tr>
			</table>
		</html-el:form>
	</tiles:put>
</tiles:insert>
