<%--
Copyright (c) 2005-2008 Grameen Foundation USA
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
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".create">
	<tiles:put name="body" type="string">
	<span id="page.id" title="previewfeescreate" />
	
		<script src="pages/framework/js/CommonUtilities.js"></script>
		<script src="pages/application/fees/js/Fees.js"></script>
		<html-el:form action="/feeaction.do?method=create" onsubmit="return func_disableSubmitBtn('submitBtn');">

			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="350" align="left" valign="top" bgcolor="#FFFFFF">
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
											<td width="27%">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/tick.gif" width="17" height="17">
														</td>
														<td class="timelineboldgray">
															<mifos:mifoslabel name="Fees.feeinformation">
															</mifos:mifoslabel>
														</td>
													</tr>
												</table>
											</td>
											<td width="73%" align="right">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework//images/timeline/bigarrow.gif" width="17" height="17">
														</td>
														<td class="timelineboldorange">
															<mifos:mifoslabel name="Fees.reviewAndSubmit"/>
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
								<td align="left" valign="top" class="paddingleftCreates">
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td class="headingorange">
												<span class="heading"> <mifos:mifoslabel name="Fees.definenewfee" /> - </span>
												<mifos:mifoslabel name="Fees.previewfeeinformation" />
											</td>
										</tr>
										<tr>
											<td class="fontnormal">
												<mifos:mifoslabel name="Fees.PreviewFeesInstruction" />
												<mifos:mifoslabel name="Fees.CreateFeesCancelInstruction" />
											</td>
										</tr>
									</table>
									<br>
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td class="fontnormal">
												<font class="fontnormalRedBold"><html-el:errors bundle="FeesUIResources" /> </font>
											</td>
										</tr>
										<tr>
											<td width="100%" height="23" class="fontnormal">
												<span class="fontnormalbold"> <mifos:mifoslabel name="Fees.previewFeeDetails">
													</mifos:mifoslabel> </span>
												<br>
												<span class="fontnormalbold"> <mifos:mifoslabel name="Fees.feename" /> </span>
												<c:out value="${sessionScope.feeactionform.feeName}" />
												<br>
												<span class="fontnormalbold"> <mifos:mifoslabel name="Fees.feeappliesto" /> </span>
												<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'CategoryList')}" var="entity">
													<c:if test="${entity.id == sessionScope.feeactionform.categoryType}">
														<c:out value="${entity.name}" />
													</c:if>
												</c:forEach>
												<br>
												<c:choose>
													<c:when test="${sessionScope.feeactionform.categoryType!=FeeCategory.LOAN.value}">
														<span class="fontnormalbold"> <mifos:mifoslabel name="Fees.defaultFees" /> </span>
														<c:choose>
															<c:when test="${sessionScope.feeactionform.customerDefaultFee == true}">
																<mifos:mifoslabel name="Fees.DefaultFeeYes" />
																<br>
															</c:when>
															<c:otherwise>
																<mifos:mifoslabel name="Fees.DefaultFeeNo" />
																<br>
															</c:otherwise>
														</c:choose>
													</c:when>
												</c:choose>

												<span class="fontnormalbold"> <mifos:mifoslabel name="Fees.frequency" /> </span>
												<c:forEach var="entity" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'feeFrequencyTypeList')}">
													<c:if test="${entity.id == sessionScope.feeactionform.feeFrequencyType}">
														<c:out value="${entity.name}" />
													</c:if>
												</c:forEach>
												<br>
												<c:choose>
													<c:when test="${sessionScope.feeactionform.feeFrequencyType==FeeFrequencyType.PERIODIC.value}">
														<span class="fontnormalbold"> <mifos:mifoslabel name="Fees.timeToCharge" /></span>
														<mifos:mifoslabel name="Fees.labelRecurEvery" />
														<c:if test="${sessionScope.feeactionform.feeRecurrenceType==RecurrenceType.WEEKLY.value}">
															<c:out value="${sessionScope.feeactionform.weekRecurAfter}" />
															<mifos:mifoslabel name="Fees.labelWeeks" />
														</c:if>
														<c:if test="${sessionScope.feeactionform.feeRecurrenceType==RecurrenceType.MONTHLY.value}">
															<c:out value="${sessionScope.feeactionform.monthRecurAfter}" />
															<mifos:mifoslabel name="Fees.labelMonths" />
														</c:if>
													</c:when>
													<c:otherwise>
														<span class="fontnormalbold"> <mifos:mifoslabel name="Fees.timeToCharge" /> </span>
														<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'TimeOfCharges')}" var="entity">
															<c:if test="${entity.id == sessionScope.feeactionform.feePaymentType}">
																<c:out value="${entity.name}" />
															</c:if>
														</c:forEach>
													</c:otherwise>
												</c:choose>
												<br>
                                                 <span class="fontnormalbold">
                                                 <mifos:mifoslabel name="Fees.currency"
                                                  bundle="FeesUIResources" isColonRequired="yes" />
                                                 </span><c:out value="${requestScope.currencyCode}" /> 
                                               <br>
												<span class="fontnormalbold"> <mifos:mifoslabel name="Fees.feecalculation" /> </span>
												<br>
												<c:choose>
													<c:when test="${sessionScope.feeactionform.categoryType==FeeCategory.LOAN.value && sessionScope.feeactionform.rateFee==true}">
														<span class="fontnormalbold"> <mifos:mifoslabel name="Fees.amountcalculatedas" /> </span>
														<c:out value="${sessionScope.feeactionform.rate}" />
													</c:when>
													<c:otherwise>
														<span class="fontnormalbold"> <mifos:mifoslabel name="Fees.amount" /> </span>
														<c:out value="${sessionScope.feeactionform.amount}" />
													</c:otherwise>
												</c:choose>
												<c:if test="${sessionScope.feeactionform.rateFee==true}">
													<mifos:mifoslabel name="Fees.ofa" />
													<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'FormulaList')}" var="entity">
														<c:if test="${entity.id == sessionScope.feeactionform.feeFormula}">
															<c:out value="${entity.name}" />
														</c:if>
													</c:forEach>
												</c:if>
												<br>
												<br>
												<span class="fontnormalbold"> <mifos:mifoslabel name="Fees.accounting" /> <br> <mifos:mifoslabel name="Fees.GLCode" /> </span>
												<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'glCodeList')}" var="glCodeList">
													<c:if test="${glCodeList.glcodeId == sessionScope.feeactionform.glCode}">
														<c:out value="${glCodeList.glcode}" />
													</c:if>
												</c:forEach>
												<br>
												<br>
												<br>

												<span class="fontnormal"></span>

												<html-el:button property="editBtn" styleClass="insidebuttn" onclick="javascript:fnEdit(this.form)">
													<mifos:mifoslabel name="Fees.edit" />
												</html-el:button>
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
												<html-el:submit property="submitBtn" styleClass="buttn">
													<mifos:mifoslabel name="Fees.submit" />
												</html-el:submit>
												&nbsp;
												<html-el:button property="cancelBtn" styleClass="cancelbuttn" onclick="javascript:fnCancel(this.form)">
													<mifos:mifoslabel name="Fees.cancel" />
												</html-el:button>
											</td>
										</tr>
										<html-el:hidden property="input" value="previewFees" />
										<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
									</table>
									<br>
								</td>
							</tr>
						</table>
						<br>
					</td>
				</tr>
			</table>
			<br>
		</html-el:form>
	</tiles:put>
</tiles:insert>
