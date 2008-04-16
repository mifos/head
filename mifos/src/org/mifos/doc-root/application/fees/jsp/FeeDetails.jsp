<!--
/**

* FeeDetails.jsp    version: 1.0



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

*/
 -->
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.FeesUIResources?"/>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<script src="pages/application/fees/js/Fees.js"></script>
		<html-el:form action="/feeaction.do">
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"> <html-el:link href="feeaction.do?method=cancelCreate&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="Fees.admin" bundle="FeesUIResources">
								</mifos:mifoslabel>
							</html-el:link> / <html-el:link href="feeaction.do?method=viewAll&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="Fees.viewfees" bundle="FeesUIResources">
								</mifos:mifoslabel>
							</html-el:link> / </span> <span class="fontnormal8ptbold"> <c:out value="${BusinessKey.feeName}"></c:out> </span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="67%" height="23" class="headingorange">
									<c:out value="${BusinessKey.feeName}"></c:out>
								</td>
								<td width="33%" align="right">
									<html-el:link href="feeaction.do?method=manage&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
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
											<c:when test="${BusinessKey.active == true}">
												<img src="pages/framework/images/status_activegreen.gif" width="8" height="9">
											</c:when>
											<c:otherwise>
												<img src="pages/framework/images/status_closedblack.gif" width="8" height="9">
											</c:otherwise>
										</c:choose> <c:out value="${BusinessKey.feeStatus.name}" /> </span>
									<br>
									<br>
									<mifos:mifoslabel name="Fees.feeappliesto" bundle="FeesUIResources"></mifos:mifoslabel>
									<c:out value="${BusinessKey.categoryType.name}" />
									<br>
									<c:choose>
										<c:when test="${BusinessKey.categoryType.id != FeeCategory.LOAN.value}">
											<mifos:mifoslabel name="Fees.defaultFees" bundle="FeesUIResources"></mifos:mifoslabel>
											<c:choose>
												<c:when test="${BusinessKey.customerDefaultFee}">
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
									<c:out value="${BusinessKey.feeFrequency.feeFrequencyType.name}" />
									<br>
									<mifos:mifoslabel name="Fees.timeToCharge" bundle="FeesUIResources"></mifos:mifoslabel>
									<c:choose>
										<c:when test="${BusinessKey.oneTime==true}">
											<c:out value="${BusinessKey.feeFrequency.feePayment.name}" />
										</c:when>
										<c:otherwise>
											<mifos:mifoslabel name="Fees.labelRecurEvery" />
											<c:out value="${BusinessKey.feeFrequency.feeMeetingFrequency.meetingDetails.recurAfter}"></c:out>
											<c:if test="${BusinessKey.feeFrequency.feeMeetingFrequency.weekly==true}">
												<mifos:mifoslabel name="Fees.labelWeeks" />
											</c:if>
											<c:if test="${BusinessKey.feeFrequency.feeMeetingFrequency.monthly==true}">
												<mifos:mifoslabel name="Fees.labelMonths" />
											</c:if>
										</c:otherwise>
									</c:choose>
									<br>
									<br>
									<span class="fontnormalbold"> <mifos:mifoslabel name="Fees.feecalculation" bundle="FeesUIResources">
										</mifos:mifoslabel> </span>
									<br>
									<c:choose>
										<c:when test="${BusinessKey.feeType.value==RateAmountFlag.RATE.value}">
											<fmt:message key="Fees.amountCalcComplete">
												<fmt:param><c:out value="${BusinessKey.rate}" /></fmt:param>
												<fmt:param><c:out value="${BusinessKey.feeFormula.name}" /></fmt:param>
											</fmt:message>
										</c:when>
										<c:otherwise>
											<mifos:mifoslabel name="Fees.amount" bundle="FeesUIResources"></mifos:mifoslabel>
											<c:out value="${BusinessKey.feeAmount}" />
										</c:otherwise>
									</c:choose>
									<br>
									<br>
									<span class="fontnormalbold"> <mifos:mifoslabel name="Fees.accounting" bundle="FeesUIResources">
										</mifos:mifoslabel> </span>
									<br>
									<mifos:mifoslabel name="Fees.GLCode" bundle="FeesUIResources"></mifos:mifoslabel>

									<c:out value="${BusinessKey.glCode.glcode}" />
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
