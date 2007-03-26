<!--

/**

 * LoanProductDetails.jsp    version: 1.0



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
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@taglib uri="/loan/loanfunctions" prefix="userfn"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<c:set var="loanPrd" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" />
			<tr>
				<td class="bluetablehead05">
					<span class="fontnormal8pt"> <html-el:link href="loanproductaction.do?method=cancelCreate&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
							<mifos:mifoslabel name="product.admin" bundle="ProductDefUIResources" />
						</html-el:link> / <html-el:link href="loanproductaction.do?method=viewAllLoanProducts&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
							<mifos:mifoslabel name="product.savingsview" bundle="ProductDefUIResources" />
							<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
							<mifos:mifoslabel name="product.products" bundle="ProductDefUIResources" />
						</html-el:link> / </span> <span class="fontnormal8ptbold"><c:out value="${loanPrd.prdOfferingName}" /></span>
				</td>
			</tr>
		</table>
		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td align="left" valign="top" class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="74%" height="23" class="headingorange">
								<c:out value="${loanPrd.prdOfferingName}" />
							</td>
							<td width="26%" align="right">
								<html-el:link href="loanproductaction.do?method=manage&prdOfferingId=${loanPrd.prdOfferingId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}&prdOfferName=${loanPrd.prdOfferingName}">
									<mifos:mifoslabel name="product.prdedit" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.info" bundle="ProductDefUIResources" />
								</html-el:link>
							</td>
						</tr>
						<tr>
							<td height="23" colspan="2" class="fontnormalbold">
								<span class="fontnormal"> </span><span class="fontnormal"> </span> <font class="fontnormalRedBold"><html-el:errors bundle="ProductDefUIResources" /> </font>
								<table width="100%" border="0" cellpadding="3" cellspacing="0">
									<tr>
										<td height="23" class="fontnormalbold">
											<span class="fontnormal"> <c:choose>
													<c:when test="${loanPrd.prdStatus.offeringStatusId eq 1}">
														<img src="pages/framework/images/status_activegreen.gif" width="8" height="9">&nbsp;
														</c:when>
													<c:otherwise>
														<img src="pages/framework/images/status_closedblack.gif" width="8" height="9">&nbsp;
														</c:otherwise>
												</c:choose> <c:out value="${loanPrd.prdStatus.prdState.name}" /> </span><span class="fontnormal"></span>
										</td>
									</tr>
									<tr>
										<td width="50%" height="23" class="fontnormalbold">
											<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
											<mifos:mifoslabel name="product.prddetails" bundle="ProductDefUIResources" />
										</td>
									</tr>
									<tr>
										<td height="23" class="fontnormalbold">
											<span class="fontnormal"> <mifos:mifoslabel name="product.prodinstname" bundle="ProductDefUIResources" />: <c:out value="${loanPrd.prdOfferingName}" /><br> <mifos:mifoslabel name="product.shortname" bundle="ProductDefUIResources" />: <c:out
													value="${loanPrd.prdOfferingShortName}" /><br> <c:if test="${!empty loanPrd.description}">
													<br>
													<mifos:mifoslabel name="product.desc" bundle="ProductDefUIResources" />
													<br>
													<c:out value="${loanPrd.description}" />
													<br>
													<br>
												</c:if> <mifos:mifoslabel name="product.prodcat" bundle="ProductDefUIResources" />: <c:out value="${loanPrd.prdCategory.productCategoryName}" /><br> <mifos:mifoslabel name="product.startdate" bundle="ProductDefUIResources" />: <c:out
													value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,loanPrd.startDate)}" /> <br> <mifos:mifoslabel name="product.enddate" bundle="ProductDefUIResources" />: <c:out
													value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,loanPrd.endDate)}" /> <br> <mifos:mifoslabel name="product.applfor" bundle="ProductDefUIResources" />: <c:out value="${loanPrd.prdApplicableMaster.name}" /> <br>
												<mifos:mifoslabel name="product.inclin" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="product.cyclecounter" bundle="ProductDefUIResources" />
												: <c:choose>
													<c:when test="${loanPrd.includeInLoanCounter}">
														<mifos:mifoslabel name="product.yes" bundle="ProductDefUIResources" />
													</c:when>
													<c:otherwise>
														<mifos:mifoslabel name="product.no" bundle="ProductDefUIResources" />
													</c:otherwise>
												</c:choose> <br> <mifos:mifoslabel name="product.max" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="product.amount"
													bundle="ProductDefUIResources" />: <c:out value="${loanPrd.maxLoanAmount}" /> <br> <mifos:mifoslabel name="product.min" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.amount" bundle="ProductDefUIResources" />: <c:out value="${loanPrd.minLoanAmount}" /> <br> <mifos:mifoslabel name="product.default" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="product.amount"
													bundle="ProductDefUIResources" />: <c:if test="${loanPrd.defaultLoanAmount.amountDoubleValue > 0.0 || loanPrd.minLoanAmount.amountDoubleValue == 0.0}">
													<c:out value="${loanPrd.defaultLoanAmount}" />
												</c:if> </span>
										</td>
									</tr>
								</table>
								<table width="96%" border="0" cellpadding="3" cellspacing="0">
									<tr>
										<td width="100%" height="23" class="fontnormalbold">
											<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
											<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />
										</td>
									</tr>
									<tr>
										<td height="23" class="fontnormalbold">
											<span class="fontnormal"> <mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="product.type"
													bundle="ProductDefUIResources" />: <c:out value="${loanPrd.interestTypes.name}" /> <br> <mifos:mifoslabel name="product.max" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}"
													bundle="ProductDefUIResources" /> <mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />: <c:out value="${userfn:getDoubleValue(loanPrd.maxInterestRate)}" /> <mifos:mifoslabel name="product.perc" bundle="ProductDefUIResources" /><br> <mifos:mifoslabel
													name="product.min" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />: <c:out
													value="${userfn:getDoubleValue(loanPrd.minInterestRate)}" /> <mifos:mifoslabel name="product.perc" bundle="ProductDefUIResources" /><br> <mifos:mifoslabel name="product.default" bundle="ProductDefUIResources" /> <mifos:mifoslabel
													name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />: <c:out value="${userfn:getDoubleValue(loanPrd.defInterestRate)}" /> <mifos:mifoslabel name="product.perc"
													bundle="ProductDefUIResources" /><br> </span>
										</td>
									</tr>
								</table>
								<table width="96%" border="0" cellpadding="3" cellspacing="0">
									<tr>
										<td width="100%" height="23" class="fontnormalbold">
											<mifos:mifoslabel name="product.repaysch" bundle="ProductDefUIResources" />
										</td>
									</tr>
									<tr>
										<td height="23" class="fontnormalbold">
											<span class="fontnormal"> <mifos:mifoslabel name="product.freqofinst" bundle="ProductDefUIResources" />: <c:out value="${loanPrd.loanOfferingMeeting.meeting.meetingDetails.recurAfter}" /> <c:if
													test="${loanPrd.loanOfferingMeeting.meeting.meetingDetails.recurrenceType.recurrenceId eq 1}">
													<mifos:mifoslabel name="product.week" bundle="ProductDefUIResources" />
												</c:if> <c:if test="${loanPrd.loanOfferingMeeting.meeting.meetingDetails.recurrenceType.recurrenceId eq 2}">
													<mifos:mifoslabel name="product.month" bundle="ProductDefUIResources" />
												</c:if> <br> <mifos:mifoslabel name="product.maxinst" bundle="ProductDefUIResources" />: <c:out value="${loanPrd.maxNoInstallments}" /><br> <mifos:mifoslabel name="product.mininst" bundle="ProductDefUIResources" />: <c:out
													value="${loanPrd.minNoInstallments}" /><br> <mifos:mifoslabel name="product.definst" bundle="ProductDefUIResources" />: <c:out value="${loanPrd.defNoInstallments}" /><br> <mifos:mifoslabel name="product.gracepertype"
													bundle="ProductDefUIResources" />: <c:out value="${loanPrd.gracePeriodType.name}" /> <br> <mifos:mifoslabel name="product.graceperdur" bundle="ProductDefUIResources" />: <c:out value="${loanPrd.gracePeriodDuration}" /> <mifos:mifoslabel
													name="product.installments" bundle="ProductDefUIResources" /><br> <mifos:mifoslabel name="product.prinlastinst" bundle="ProductDefUIResources" />: <c:choose>
													<c:when test="${loanPrd.prinDueLastInst}">
														<mifos:mifoslabel name="product.yes" bundle="ProductDefUIResources" />
													</c:when>
													<c:otherwise>
														<mifos:mifoslabel name="product.no" bundle="ProductDefUIResources" />
													</c:otherwise>
												</c:choose> <br> <mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="product.deductedatdis" bundle="ProductDefUIResources" />: <c:choose>
													<c:when test="${loanPrd.intDedDisbursement}">
														<mifos:mifoslabel name="product.yes" bundle="ProductDefUIResources" />
													</c:when>
													<c:otherwise>
														<mifos:mifoslabel name="product.no" bundle="ProductDefUIResources" />
													</c:otherwise>
												</c:choose> </span>
										</td>
									</tr>
								</table>
								<table width="96%" border="0" cellpadding="3" cellspacing="0">
									<tr>
										<td width="100%" height="23" class="fontnormalbold">
											<mifos:mifoslabel name="product.fees&pen" bundle="ProductDefUIResources" />
										</td>
									</tr>
									<tr>
										<td height="23" class="fontnormalbold">
											<span class="fontnormal"> <c:forEach items="${loanPrd.loanOfferingFees}" var="prdOfferingFees">
													<c:out value="${prdOfferingFees.fees.feeName}" />
													<br>
												</c:forEach> <br> </span>
										</td>
									</tr>
								</table>
								<table width="96%" border="0" cellpadding="3" cellspacing="0">
									<tr>
										<td width="100%" height="23" class="fontnormalbold">
											<mifos:mifoslabel name="product.accounting" bundle="ProductDefUIResources" />
										</td>
									</tr>
									<tr>
										<td height="23" class="fontnormalbold">
											<span class="fontnormal"> <mifos:mifoslabel name="product.srcfunds" bundle="ProductDefUIResources" /> : <br> <c:forEach items="${loanPrd.loanOfferingFunds}" var="loanOffeingFund">
													<c:out value="${loanOffeingFund.fund.fundName}" />
													<br>
												</c:forEach> <br> <mifos:mifoslabel name="product.productglcode" bundle="ProductDefUIResources" />:<br> <mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />: <c:out
													value="${loanPrd.interestGLcode.glcode}" /> <br> <mifos:mifoslabel name="product.principal" bundle="ProductDefUIResources" />: <c:out value="${loanPrd.principalGLcode.glcode}" /> <br> </span>
											<br>
											<br>
											<span class="fontnormal"> <html-el:link
													href="loanproductaction.do?method=loadChangeLog&entityType=LoanProduct&entityId=${loanPrd.prdOfferingId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}&prdOfferName=${loanPrd.prdOfferingName}">
													<mifos:mifoslabel name="product.viewchangelog" bundle="ProductDefUIResources" />
												</html-el:link> </span>
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
					<br>
				</td>
			</tr>
		</table>
	</tiles:put>
</tiles:insert>
