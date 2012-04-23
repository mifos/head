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
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.FeesUIResources"/>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
	<span id="page.id" title="FeeDetails"></span>
		<c:set value="${requestScope.GlNamesMode}" var="GlNamesMode"/>
		
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
							</html-el:link> / </span> <span class="fontnormal8ptbold"> <c:out value="${feeModel.name}"></c:out> </span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="67%" height="23" class="headingorange">
									<c:out value="${feeModel.name}"></c:out>
								</td>
								<td width="33%" align="right">
									<html-el:link href="feeaction.do?method=manage&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}&feeId=${feeModel.id}">
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
											<c:when test="${feeModel.active == true}">
												<img src="pages/framework/images/status_activegreen.gif" width="8" height="9">
											</c:when>
											<c:otherwise>
												<img src="pages/framework/images/status_closedblack.gif" width="8" height="9">
											</c:otherwise>
										</c:choose> <c:out value="${feeModel.feeStatus.name}" /> </span>
									<br>
									<br>
									<mifos:mifoslabel name="Fees.feeappliesto" bundle="FeesUIResources"></mifos:mifoslabel>
									<c:out value="${feeModel.categoryType}" />
									<br>
									<c:choose>
										<c:when test="${!feeModel.loanCategoryType}">
											<mifos:mifoslabel name="Fees.defaultFees" bundle="FeesUIResources"></mifos:mifoslabel>
											<c:choose>
												<c:when test="${feeModel.customerDefaultFee}">
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
									<c:out value="${feeModel.feeFrequency.type}" />
									<br>
									<mifos:mifoslabel name="Fees.timeToCharge" bundle="FeesUIResources"></mifos:mifoslabel>
									<c:choose>
										<c:when test="${feeModel.oneTime}">
											<c:out value="${feeModel.feeFrequency.payment}" />
										</c:when>
										<c:otherwise>
											<mifos:mifoslabel name="Fees.labelRecurEvery" />
											<c:out value="${feeModel.feeFrequency.recurAfterPeriod}"></c:out>
											<c:if test="${feeModel.feeFrequency.weekly}">
												<mifos:mifoslabel name="Fees.labelWeeks" />
											</c:if>
											<c:if test="${feeModel.feeFrequency.monthly}">
												<mifos:mifoslabel name="Fees.labelMonths" />
											</c:if>
										</c:otherwise>
									</c:choose>
                                    <c:if test='${sessionScope.isMultiCurrencyEnabled && feeModel.loanCategoryType && !feeModel.rateBasedFee }'>
									<br>
                                      <span class="fontnormal"> 
                                      <mifos:mifoslabel name="Fees.currency" bundle="FeesUIResources" isColonRequired="yes" />
                                      <c:out value="${feeModel.amount.currency.currencyCode}" />
                                      </span>
                                    </c:if>
                                    <br>
									<br>
									<span class="fontnormalbold"> <mifos:mifoslabel name="Fees.feecalculation" bundle="FeesUIResources">
										</mifos:mifoslabel> </span>
									<br>
									<c:choose>
										<c:when test="${feeModel.rateBasedFee}">
											<fmt:message key="Fees.amountCalcComplete">
												<%-- FIXME: use a rate decimal formatter --%>
												<fmt:param><fmt:formatNumber value="${feeModel.rate}" /></fmt:param>
												<fmt:param><c:out value="${feeModel.feeFormula.name}" /></fmt:param>
											</fmt:message>
										</c:when>
										<c:otherwise>
											<mifos:mifoslabel name="Fees.amount" bundle="FeesUIResources"></mifos:mifoslabel>
											<fmt:formatNumber value="${feeModel.amount}" />
										</c:otherwise>
									</c:choose>
									<br>
									<br>
									<span class="fontnormalbold"> <mifos:mifoslabel name="Fees.accounting" bundle="FeesUIResources">
										</mifos:mifoslabel> </span>
									<br>
									<mifos:mifoslabel name="Fees.GLCode" bundle="FeesUIResources"></mifos:mifoslabel>

						 					<c:choose>											
												<c:when test="${GlNamesMode == 1}">
												<c:out value="${feeModel.glCode} - ${feeModel.glCodeDto.glname}" />
												</c:when>
												<c:when test="${GlNamesMode == 2}">
												<c:out value="${feeModel.glCodeDto.glname} (${feeModel.glCode})" />
												</c:when>
												<c:when test="${GlNamesMode == 3}">
												<c:out value="${feeModel.glCodeDto.glname}" />
												</c:when>
												<c:when test="${GlNamesMode == 4}">
												<c:out value="${feeModel.glCode}" />
												</c:when>
											</c:choose> 
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
