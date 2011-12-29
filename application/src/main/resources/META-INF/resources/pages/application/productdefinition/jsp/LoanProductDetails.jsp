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
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@taglib uri="/loan/loanfunctions" prefix="userfn"%>

<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.ProductDefinitionResources"/>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<span id="page.id" title="LoanProductDetails"></span>
		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<c:set var="loanPrd" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" />
			<c:set var="loanAmountType" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanAmountType')}" />
			<c:set var="installType" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'installType')}" />
			<tr>
				<td class="bluetablehead05">
					<span class="fontnormal8pt"> <html-el:link href="loanproductaction.do?method=cancelCreate&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
							<mifos:mifoslabel name="product.admin" bundle="ProductDefUIResources" />
						</html-el:link> / <html-el:link href="loanproductaction.do?method=viewAllLoanProducts&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
							<fmt:message key="product.viewLoanProducts">
							<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" /></fmt:param>
							</fmt:message>
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
								<html-el:link styleId="loanproductdetails.link.editLoanProduct" href="loanproductaction.do?method=manage&prdOfferingId=${loanPrd.prdOfferingId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}&prdOfferName=${loanPrd.prdOfferingName}">
									<fmt:message key="product.editLoanInfo">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" /></fmt:param>
									</fmt:message>
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
											<fmt:message key="product.loanProductDetails">
											<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" /></fmt:param>
											</fmt:message>
										</td>
									</tr>
									<tr>
										<td height="23" class="fontnormalbold">
											<span class="fontnormal"> <mifos:mifoslabel name="product.prodinstname" bundle="ProductDefUIResources" isColonRequired="yes"/> <c:out value="${loanPrd.prdOfferingName}" /><br> <mifos:mifoslabel name="product.shortname" bundle="ProductDefUIResources" />: <c:out
													value="${loanPrd.prdOfferingShortName}" /><br> <c:if test="${!empty loanPrd.description}">
													<br>
													<mifos:mifoslabel name="product.desc" bundle="ProductDefUIResources" />
													<br>
													<c:out value="${loanPrd.description}" />
													<br>
													<br>
												</c:if> <mifos:mifoslabel name="product.prodcat" bundle="ProductDefUIResources" />: <c:out value="${loanPrd.prdCategory.productCategoryName}" /><br> <mifos:mifoslabel name="product.startdate" bundle="ProductDefUIResources" />: <c:out
													value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,loanPrd.startDate)}" /> <br> <mifos:mifoslabel name="product.enddate" bundle="ProductDefUIResources" isColonRequired="yes"/> <c:out
													value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,loanPrd.endDate)}" /> <br> <mifos:mifoslabel name="product.applfor" bundle="ProductDefUIResources" isColonRequired="yes"/> <c:out value="${loanPrd.prdApplicableMaster.name}" /> <br>
												<c:if test='${sessionScope.isMultiCurrencyEnabled}'>
                                                 <span class="fontnormal"> <mifos:mifoslabel name="product.currency"
                                                 bundle="ProductDefUIResources" isColonRequired="yes" /> 
                                                 <c:out value="${loanPrd.currency.currencyCode}" /> 
                                                 </span> <br>
                                                 </c:if>
                                                <mifos:mifoslabel name="product.inclin" bundle="ProductDefUIResources" /> 
                                                <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" /> 
                                                <mifos:mifoslabel name="product.cyclecounter" bundle="ProductDefUIResources" isColonRequired="yes"/>
												<c:choose>
													<c:when test="${loanPrd.includeInLoanCounter}">
														<mifos:mifoslabel name="product.yes" bundle="ProductDefUIResources" />
													</c:when>
													<c:otherwise>
														<mifos:mifoslabel name="product.no" bundle="ProductDefUIResources" />
													</c:otherwise>
												</c:choose> <br>
												<mifos:mifoslabel name="product.include.interest.waiver" bundle="ProductDefUIResources" isColonRequired="yes"/>
												<c:choose>
													<c:when test="${loanPrd.interestWaived}">
														<mifos:mifoslabel name="product.yes" bundle="ProductDefUIResources" />
													</c:when>
													<c:otherwise>
														<mifos:mifoslabel name="product.no" bundle="ProductDefUIResources" />
													</c:otherwise>
												</c:choose> <br>
												<%--<mifos:mifoslabel name="product.max" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="product.amount"
													bundle="ProductDefUIResources" />: <fmt:formatNumber value="${loanPrd.maxLoanAmount}" /> <br> <mifos:mifoslabel name="product.min" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.amount" bundle="ProductDefUIResources" />: <fmt:formatNumber value="${loanPrd.minLoanAmount}" /> <br> <mifos:mifoslabel name="product.default" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="product.amount"
													bundle="ProductDefUIResources" />: <c:if test="${loanPrd.defaultLoanAmount.amountDoubleValue > 0.0 || loanPrd.minLoanAmount.amountDoubleValue == 0.0}">
													<fmt:formatNumber value="${loanPrd.defaultLoanAmount}" />
												</c:if> 
												--%>
												<%-- FIXME: use a constant here instead of literal 1 --%>
												<c:if test="${loanAmountType==1}">
												<mifos:mifoslabel name="product.calcloanamount" bundle="ProductDefUIResources" isColonRequired="yes"/> <mifos:mifoslabel name="product.sameforallloans" bundle="ProductDefUIResources" />
													<br>
													<table id="loanAmountSameTable" width="40%" border="0" cellspacing="0" cellpadding="3">
														<tr>
															<td width="20%" class="drawtablehd"> <mifos:mifoslabel name="product.minloanamt" bundle="ProductDefUIResources" /></td>
															<td width="20%" class="drawtablehd" align="right"> <mifos:mifoslabel name="product.maxloanamt" bundle="ProductDefUIResources" /></td>
															<td width="20%" class="drawtablehd" align="right"> <mifos:mifoslabel name="product.defamt" bundle="ProductDefUIResources" /></td>	
														</tr>	
														<c:forEach items="${loanPrd.loanAmountSameForAllLoan}" var="loanAmountSameForAllLoan">	
														<tr>							
															<td class="fontnormal" width="20%"><fmt:formatNumber value="${loanAmountSameForAllLoan.minLoanAmountString}"type="number"/></td>
															<td class="fontnormal" width="20%" align="right"><fmt:formatNumber value="${loanAmountSameForAllLoan.maxLoanAmountString}" type="number"/></td>
															<td class="fontnormal" width="20%" align="right"><fmt:formatNumber value="${loanAmountSameForAllLoan.defaultLoanAmountString}" type="number"/></td>																													
														</tr>
													</c:forEach>
													</table>
												</c:if>
												<c:if test="${loanAmountType=='2'}">
													<br>
													<table id="loanAmountFromLastTable" width="40%" border="0" cellpadding="3" cellspacing="0">
													<mifos:mifoslabel name="product.calcloanamount" bundle="ProductDefUIResources" isColonRequired="yes"/> <mifos:mifoslabel name="product.bylastloanamount" bundle="ProductDefUIResources" />
													<tr>
															<td width="25%" class="drawtablehd" > <mifos:mifoslabel name="product.lastloanamount" bundle="ProductDefUIResources" /> </td>
															<td width="15%" class="drawtablehd" align="right"> <mifos:mifoslabel name="product.minloanamt" bundle="ProductDefUIResources" /></td>
															<td width="15%" class="drawtablehd" align="right"> <mifos:mifoslabel name="product.maxloanamt" bundle="ProductDefUIResources" /></td>
															<td width="20%" class="drawtablehd" align="right"> <mifos:mifoslabel name="product.defamt" bundle="ProductDefUIResources" /></td>	
													</tr>
													
													<c:forEach items="${loanPrd.loanAmountFromLastLoan}" var="loanAmountFromLastLoan">
													<tr>				
														<td class="fontnormal"> 
															<fmt:formatNumber value="${loanAmountFromLastLoan.startRange}" type="number"/>												
														  -	<fmt:formatNumber value="${loanAmountFromLastLoan.endRange}" type="number"/></td>
															<td class="fontnormal" align="right"> <fmt:formatNumber value="${loanAmountFromLastLoan.minLoanAmountString}" type="number"/></td>
															<td class="fontnormal" align="right"> <fmt:formatNumber value="${loanAmountFromLastLoan.maxLoanAmountString}" type="number"/> </td>
															<td class="fontnormal" align="right"> <fmt:formatNumber value="${loanAmountFromLastLoan.defaultLoanAmountString}" type="number"/></td>
														</tr>	
													</c:forEach>
													</table>
												</c:if>
												<c:if test="${loanAmountType=='3'}">
												<br/>
													<mifos:mifoslabel name="product.calcloanamount" bundle="ProductDefUIResources" isColonRequired="yes"/> <mifos:mifoslabel name="product.byloancycle" bundle="ProductDefUIResources" />
													<br/>
													
													<table id="loanAmountFromCycleTable" width="40%" border="0" cellspacing="0" cellpadding="3">
														<tr>
															<td width="15%" class="drawtablehd"> <mifos:mifoslabel name="product.loancycleno" bundle="ProductDefUIResources" /> </td>
															<td width="30%" class="drawtablehd" align="right"> <mifos:mifoslabel name="product.minloanamt" bundle="ProductDefUIResources" /></td>
															<td width="30%" class="drawtablehd" align="right"> <mifos:mifoslabel name="product.maxloanamt" bundle="ProductDefUIResources" /></td>
															<td width="30%" class="drawtablehd" align="right"> <mifos:mifoslabel name="product.defamt" bundle="ProductDefUIResources" /></td>	
														</tr>
														<c:forEach items="${loanPrd.loanAmountFromLoanCycle}" var="loanAmountFromLoanCycle">
														<tr>	
															<td class="fontnormal" width="10%"><fmt:formatNumber value="${loanAmountFromLoanCycle.rangeIndex}"type="number"/></td>
															<td class="fontnormal" width="30%" align="right"><fmt:formatNumber value="${loanAmountFromLoanCycle.minLoanAmountString}"type="number"/></td>
															<td class="fontnormal" width="30%" align="right"><fmt:formatNumber value="${loanAmountFromLoanCycle.maxLoanAmountString}" type="number"/></td>
															<td class="fontnormal" width="30%" align="right"><fmt:formatNumber value="${loanAmountFromLoanCycle.defaultLoanAmountString}" type="number"/></td>																													
														</tr>
														</c:forEach>
													</table>
												</c:if>
												</span>
										</td>
									</tr>
								</table>
                                <table width="93%" border="0" cellpadding="3" cellspacing="0">
                                    <tr>
                                        <td height="23" class="fontnormalbold"><mifos:mifoslabel
                                            name="product.assocedquestiongroups" bundle="ProductDefUIResources" /> : <span
                                            class="fontnormal"><br>
                                        <c:forEach
                                            items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'SelectedQGList')}"
                                            var="questionGroup">
                                            <c:out value="${questionGroup.title}" />
                                            <br>
                                        </c:forEach></span> <br>
                                    </tr>
                                </table>
								<table width="96%" border="0" cellpadding="3" cellspacing="0">
									<tr>
										<td width="100%" height="23" class="fontnormalbold">
											<fmt:message key="product.productRate">
											<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" /></fmt:param>
											</fmt:message>
										</td>
									</tr>
									<tr>
										<td height="23" class="fontnormalbold">
											<span class="fontnormal"> 
												<fmt:message key="product.rateType">
												<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" /></fmt:param>
												</fmt:message>: <c:out value="${loanPrd.interestTypes.name}" /> <br>
												<fmt:message key="product.minRate">
												<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" /></fmt:param> 
												</fmt:message>: <fmt:formatNumber value="${userfn:getDoubleValue(loanPrd.minInterestRate)}" /> <mifos:mifoslabel name="product.perc" bundle="ProductDefUIResources" /><br> 
												<fmt:message key="product.maxRate">
                                                <fmt:param><mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" /></fmt:param>
                                                </fmt:message>: <fmt:formatNumber value="${userfn:getDoubleValue(loanPrd.maxInterestRate)}" /> <mifos:mifoslabel name="product.perc" bundle="ProductDefUIResources" /><br> 
                                                <fmt:message key="product.defaultRate">
												<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" /></fmt:param>
												</fmt:message>: <fmt:formatNumber value="${userfn:getDoubleValue(loanPrd.defInterestRate)}" /> <mifos:mifoslabel name="product.perc"
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
                                        <td height="23" class="fontnormal">
                                            <mifos:mifoslabel name="product.canConfigureVariableInstallments" bundle="ProductDefUIResources" isColonRequired="yes" />
                                            <span class="fontnormal">
                                                <c:choose>
                                                    <c:when test="${loanPrd.variableInstallmentsAllowed}">
                                                        <mifos:mifoslabel name="product.yes" bundle="ProductDefUIResources" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <mifos:mifoslabel name="product.no" bundle="ProductDefUIResources" />
                                                    </c:otherwise>
                                                </c:choose>
                                            </span>
                                        </td>
                                    </tr>
                                    <c:if test="${loanPrd.variableInstallmentsAllowed}">
                                        <tr>
                                            <td height="23" class="fontnormal">
                                                <mifos:mifoslabel name="product.minimumGapBetweenInstallments" bundle="ProductDefUIResources" isColonRequired="yes" />
                                                <span class="fontnormal">
	                                                <c:choose>
		                                                <c:when test="${empty loanPrd.variableInstallmentDetails.minGapInDays}">
		                                                    <mifos:mifoslabel name="product.notApplicable" bundle="ProductDefUIResources" />
		                                                </c:when>
		                                                <c:otherwise>
	                                                    <fmt:formatNumber value="${loanPrd.variableInstallmentDetails.minGapInDays}" />
		                                                	 <span id="days"> <mifos:mifoslabel name="product.days" bundle="ProductDefUIResources" /> </span>
		                                                </c:otherwise>
		                                            </c:choose>
                                                </span>
                                                <br/>
                                                <mifos:mifoslabel name="product.maximumGapBetweenInstallments" bundle="ProductDefUIResources" isColonRequired="yes" />
                                                <span class="fontnormal">
                                                	<c:choose>
		                                                <c:when test="${empty loanPrd.variableInstallmentDetails.maxGapInDays}">
		                                                    <mifos:mifoslabel name="product.notApplicable" bundle="ProductDefUIResources" />
		                                                </c:when>
		                                                <c:otherwise>
		                                                     <fmt:formatNumber value="${loanPrd.variableInstallmentDetails.maxGapInDays}" />
		                                                	 <span id="days"> <mifos:mifoslabel name="product.days" bundle="ProductDefUIResources" /> </span>
		                                                </c:otherwise>
		                                            </c:choose>
                                                </span>
                                                <br/>
                                                <mifos:mifoslabel name="product.minimumInstallmentAmount" bundle="ProductDefUIResources" isColonRequired="yes" />
                                                <span class="fontnormal">
                                                    <c:choose>
		                                                <c:when test="${loanPrd.variableInstallmentDetails.minInstallmentAmount.amountDoubleValue == 0.0}">
		                                                    <mifos:mifoslabel name="product.notApplicable" bundle="ProductDefUIResources" />
		                                                </c:when>
		                                                <c:otherwise>
		                                                    <fmt:formatNumber value="${loanPrd.variableInstallmentDetails.minInstallmentAmount.amountDoubleValue}" type="number"/>
		                                                </c:otherwise>
		                                            </c:choose>
                                                </span>
                                            </td>
                                        </tr>
                                    </c:if>
                                    
                                   <tr>
                                        <td height="23" class="fontnormal">
                                            <mifos:mifoslabel name="product.cashFlowValidation" bundle="ProductDefUIResources" isColonRequired="yes" />
                                            <span class="fontnormal">
                                                <c:choose>
                                                    <c:when test="${loanPrd.cashFlowCheckEnabled}">
                                                        <mifos:mifoslabel name="product.yes" bundle="ProductDefUIResources" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <mifos:mifoslabel name="product.no" bundle="ProductDefUIResources" />
                                                    </c:otherwise>
                                                </c:choose>
                                            </span>
                                        </td>
                                    </tr>
                                    <c:if test="${loanPrd.cashFlowCheckEnabled}">
                                        <tr>
                                            <td height="23" class="fontnormal">
                                                <mifos:mifoslabel name="product.cashFlowThreshold" bundle="ProductDefUIResources" isColonRequired="yes" />
                                                <span class="fontnormal">
	                                                <c:choose>
		                                                <c:when test="${empty loanPrd.cashFlowDetail.cashFlowThreshold}">
		                                                    <mifos:mifoslabel name="product.notApplicable" bundle="ProductDefUIResources" />
		                                                </c:when>
		                                                <c:otherwise>
	                                                  		<fmt:formatNumber value="${loanPrd.cashFlowDetail.cashFlowThreshold}" />
		                                              		<mifos:mifoslabel name="product.perc" bundle="ProductDefUIResources" />
		                                                </c:otherwise>
		                                            </c:choose>
                                                </span>
                                                <br/>
                                                <mifos:mifoslabel name="product.indebtednessRatio" bundle="ProductDefUIResources" isColonRequired="yes" />
                                                <span class="fontnormal">
	                                                <c:choose>
		                                                <c:when test="${empty loanPrd.cashFlowDetail.indebtednessRatio}">
		                                                    <mifos:mifoslabel name="product.notApplicable" bundle="ProductDefUIResources" />
		                                                </c:when>
		                                                <c:otherwise>
	                                                  		<fmt:formatNumber value="${loanPrd.cashFlowDetail.indebtednessRatio}" />
		                                              		<mifos:mifoslabel name="product.perc" bundle="ProductDefUIResources" />
		                                                </c:otherwise>
		                                            </c:choose>
                                                </span>
                                                <br/>
                                                <mifos:mifoslabel name="product.repaymentCapacity" bundle="ProductDefUIResources" isColonRequired="yes" />
                                                <span class="fontnormal">
	                                                <c:choose>
		                                                <c:when test="${empty loanPrd.cashFlowDetail.repaymentCapacity}">
		                                                    <mifos:mifoslabel name="product.notApplicable" bundle="ProductDefUIResources" />
		                                                </c:when>
		                                                <c:otherwise>
	                                                  		<fmt:formatNumber value="${loanPrd.cashFlowDetail.repaymentCapacity}" />
		                                              		<mifos:mifoslabel name="product.perc" bundle="ProductDefUIResources" />
		                                                </c:otherwise>
		                                            </c:choose>
                                                </span>
                                              </td>
                                        </tr>
                                    </c:if>

									<tr>
										<td height="23" class="fontnormalbold">
											<span class="fontnormal"> <mifos:mifoslabel name="product.freqofinst" bundle="ProductDefUIResources" isColonRequired="yes"/> <c:out value="${loanPrd.loanOfferingMeeting.meeting.meetingDetails.recurAfter}" /> <c:if
													test="${loanPrd.loanOfferingMeeting.meeting.meetingDetails.recurrenceType.recurrenceId eq 1}">
													<mifos:mifoslabel name="product.week" bundle="ProductDefUIResources" />
												</c:if> <c:if test="${loanPrd.loanOfferingMeeting.meeting.meetingDetails.recurrenceType.recurrenceId eq 2}">
													<mifos:mifoslabel name="product.month" bundle="ProductDefUIResources" />
												</c:if> <br> 
												<%--<mifos:mifoslabel name="product.maxinst" bundle="ProductDefUIResources" />: <fmt:formatNumber value="${loanPrd.maxNoInstallments}" /><br> <mifos:mifoslabel name="product.mininst" bundle="ProductDefUIResources" />: <fmt:formatNumber
													value="${loanPrd.minNoInstallments}" /><br> <mifos:mifoslabel name="product.definst" bundle="ProductDefUIResources" />: <fmt:formatNumber value="${loanPrd.defNoInstallments}" />
												--%>
													<c:if test="${installType=='1'}">
													<mifos:mifoslabel name="product.calcInstallment" bundle="ProductDefUIResources" isColonRequired="yes"/> <mifos:mifoslabel name="product.sameforallinstallment" bundle="ProductDefUIResources"/>
														<br>
														<table id="noOfInstallSameTable" width="41.5%" border="0" cellspacing="0" cellpadding="3">
																<tr>
																	<td width="20%" class="drawtablehd"> <mifos:mifoslabel name="product.mininst" bundle="ProductDefUIResources" /></td>
																	<td width="20%" class="drawtablehd" > <mifos:mifoslabel name="product.maxinst" bundle="ProductDefUIResources" /></td>
																	<td width="20%" class="drawtablehd" > <mifos:mifoslabel name="product.definst" bundle="ProductDefUIResources" /></td>
																</tr>	
																	<c:forEach items="${loanPrd.noOfInstallSameForAllLoan}" var="noOfInstallSameForAllLoan">					
																<tr>																														
																	<td class="fontnormal" width="20%"> <fmt:formatNumber value="${noOfInstallSameForAllLoan.minNoOfInstall}" type="number"/></td>
																	<td class="fontnormal" width="20%"> <fmt:formatNumber value="${noOfInstallSameForAllLoan.maxNoOfInstall}" type="number"/></td>
																	<td class="fontnormal" width="20%"> <fmt:formatNumber value="${noOfInstallSameForAllLoan.defaultNoOfInstall}" type="number"/></td>																															
																</tr>																
																	</c:forEach>														
																</table>
													</c:if>
													
													<c:if test="${installType=='2'}">
													<mifos:mifoslabel name="product.calcInstallment" bundle="ProductDefUIResources" isColonRequired="yes"/> <mifos:mifoslabel name="product.installbylastloanamount" bundle="ProductDefUIResources"/>
														<br>
														<table id="noOfInstallFromLastTable" width="41.5%" border="0" cellpadding="3" cellspacing="0">
													<tr>
																	<td width="25%" class="drawtablehd"> <mifos:mifoslabel name="product.lastloanamount" bundle="ProductDefUIResources" /> </td>
																	<td width="15%" class="drawtablehd" > <mifos:mifoslabel name="product.mininst" bundle="ProductDefUIResources" /></td>
																	<td width="15%" class="drawtablehd" > <mifos:mifoslabel name="product.maxinst" bundle="ProductDefUIResources" /></td>
																	<td width="20%" class="drawtablehd" > <mifos:mifoslabel name="product.definst" bundle="ProductDefUIResources" /></td>	
																</tr>
													<c:forEach items="${loanPrd.noOfInstallFromLastLoan}" var="noOfInstallFromLastLoan">
													<tr>				
													<td class="fontnormal"> 
																<fmt:formatNumber value="${noOfInstallFromLastLoan.startRange}" type="number"/>												
														-	    <fmt:formatNumber value="${noOfInstallFromLastLoan.endRange}" type="number"/></td>
																<td class="fontnormal" > <fmt:formatNumber value="${noOfInstallFromLastLoan.minNoOfInstall}" type="number"/></td>
																<td class="fontnormal" > <fmt:formatNumber value="${noOfInstallFromLastLoan.maxNoOfInstall}" type="number"/> </td>
																<td class="fontnormal" > <fmt:formatNumber value="${noOfInstallFromLastLoan.defaultNoOfInstall}" type="number"/></td>
															</tr>		
													</c:forEach>
																</table>
													</c:if>
													<c:if test="${installType=='3'}">
													<mifos:mifoslabel name="product.calcInstallment" bundle="ProductDefUIResources" isColonRequired="yes"/> <mifos:mifoslabel name="product.installbyloancycle" bundle="ProductDefUIResources"/>
														<br>
															<table id="noOfInstallFromCycleTable" width="41.5%" border="0" cellspacing="0" cellpadding="3">
																<tr>
																	<td width="15%" class="drawtablehd"> <mifos:mifoslabel name="product.loancycleno" bundle="ProductDefUIResources" /> </td>
																	<td width="30%" class="drawtablehd" > <mifos:mifoslabel name="product.mininst" bundle="ProductDefUIResources" /></td>
																	<td width="30%" class="drawtablehd" > <mifos:mifoslabel name="product.maxinst" bundle="ProductDefUIResources" /></td>
																	<td width="30%" class="drawtablehd" > <mifos:mifoslabel name="product.definst" bundle="ProductDefUIResources" /></td>	
																</tr>
																<c:forEach items="${loanPrd.noOfInstallFromLoanCycle}" var="noOfInstallFromLoanCycle">
																<tr>	
																	<td class="fontnormal" width="10%"><fmt:formatNumber value="${noOfInstallFromLoanCycle.rangeIndex}"type="number"/></td>
																	<td class="fontnormal" width="30%" ><fmt:formatNumber value="${noOfInstallFromLoanCycle.minNoOfInstall}"type="number"/></td>
																	<td class="fontnormal" width="30%" ><fmt:formatNumber value="${noOfInstallFromLoanCycle.maxNoOfInstall}" type="number"/></td>
																	<td class="fontnormal" width="30%" ><fmt:formatNumber value="${noOfInstallFromLoanCycle.defaultNoOfInstall}" type="number"/></td>																													
																</tr>
																</c:forEach>
																</table>
													</c:if>
													
													<br> <mifos:mifoslabel name="product.gracepertype" bundle="ProductDefUIResources" isColonRequired="yes"/> <c:out value="${loanPrd.gracePeriodType.name}" /> <br> <mifos:mifoslabel name="product.graceperdur" bundle="ProductDefUIResources" isColonRequired="yes"/> <fmt:formatNumber value="${loanPrd.gracePeriodDuration}" /> <mifos:mifoslabel
													name="product.installments" bundle="ProductDefUIResources" /><br>
													<!--  
													<mifos:mifoslabel name="product.prinlastinst" bundle="ProductDefUIResources" isColonRequired="yes"/> <c:choose>
													<c:when test="${loanPrd.prinDueLastInst}">
														<mifos:mifoslabel name="product.yes" bundle="ProductDefUIResources" />
													</c:when>
													<c:otherwise>
														<mifos:mifoslabel name="product.no" bundle="ProductDefUIResources" />
													</c:otherwise>
												</c:choose> <br> 
												<fmt:message key="product.deductedatdis">
												<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" /></fmt:param>
												</fmt:message>: <c:choose>
													<c:when test="${loanPrd.intDedDisbursement}">
														<mifos:mifoslabel name="product.yes" bundle="ProductDefUIResources" />
													</c:when>
													<c:otherwise>
														<mifos:mifoslabel name="product.no" bundle="ProductDefUIResources" />
													</c:otherwise>
												</c:choose> </span>
												--> 
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
											<span class="fontnormal"> <mifos:mifoslabel name="product.srcfunds" bundle="ProductDefUIResources" isColonRequired="yes"/> <br> <c:forEach items="${loanPrd.loanOfferingFunds}" var="loanOffeingFund">
													<c:out value="${loanOffeingFund.fund.fundName}" />
													<br>
												</c:forEach> <br> <mifos:mifoslabel name="product.productglcode" bundle="ProductDefUIResources" isColonRequired="yes"/><br> <mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" isColonRequired="yes"/> <c:out
													value="${loanPrd.interestGLcode.glcode}" /> <br> <mifos:mifoslabel name="product.principal" bundle="ProductDefUIResources" isColonRequired="yes"/> <c:out value="${loanPrd.principalGLcode.glcode}" /> <br> </span>
											<br>
											<br>
											<span class="fontnormal"> <html-el:link styleId="loanproductdetails.link.viewChangeLog" 
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
