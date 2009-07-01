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
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".create">
	<tiles:put name="body" type="string">
		<script src="pages/application/fees/js/Fees.js"></script>
		<script src="pages/framework/js/conversion.js"></script>
		<script src="pages/framework/js/con_en.js"></script>
		<script src="pages/framework/js/con_${sessionScope['UserContext'].currentLocale}.js"></script>
		
		<html-el:form action="/feeaction.do?method=preview" focus="feeName">
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
															<img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17">
														</td>
														<td class="timelineboldorange">
															<mifos:mifoslabel name="Fees.feeinformation" />
														</td>
													</tr>
												</table>
											</td>
											<td width="73%" align="right">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17">
														</td>
														<td class="timelineboldorangelight">
															<mifos:mifoslabel name="Fees.reviewAndSubmit" />
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
												<mifos:mifoslabel name="Fees.enterfeeinformation" />
											</td>
										</tr>
										<tr>
											<td class="fontnormal">
												<mifos:mifoslabel name="Fees.CreateFeesInstruction" />
												<mifos:mifoslabel name="Fees.CreateFeesCancelInstruction" />
												<br>
												<mifos:mifoslabel name="Fees.CreateFeesFieldInstruction" mandatory="yes" />
											</td>
										</tr>
									</table>
									<br>
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<font class="fontnormalRedBold"><html-el:errors bundle="FeesUIResources" /></font>
										<tr>
											<td colspan="2" class="fontnormalbold">
												<mifos:mifoslabel name="Fees.feedetails" />
												<br>
												<br>
											</td>
										</tr>
										<tr class="fontnormal">
											<td width="27%" align="right">
												<mifos:mifoslabel name="Fees.feename" mandatory="yes" />
											</td>
											<td width="73%" valign="top">
												<mifos:mifosalphanumtext property="feeName" />
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel name="Fees.feeappliesto" mandatory="yes" />
											</td>
											<td valign="top">
												<mifos:select property="categoryType" onchange="onPageLoad();">
													<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'CategoryList')}" var="category">
														<html-el:option value="${category.id}">${category.name}</html-el:option>
													</c:forEach>
												</mifos:select>
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right" valign="top">
												<mifos:mifoslabel name="Fees.defaultfees" />
											</td>
											<td valign="top">
												<html-el:checkbox property="customerDefaultFee" value="1" />
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right" valign="top">
												<mifos:mifoslabel name="Fees.frequency" mandatory="yes" />
											</td>
											<td valign="top">
												<html-el:radio property="feeFrequencyType" value="${FeeFrequencyType.PERIODIC.value}" onclick="onPageLoad();">
													<c:forEach var="entity" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'feeFrequencyTypeList')}">
														<c:if test="${entity.id == FeeFrequencyType.PERIODIC.value}">
															<c:out value="${entity.name}" />
														</c:if>
													</c:forEach>
												</html-el:radio>
												<br>
												<html-el:radio property="feeFrequencyType" value="${FeeFrequencyType.ONETIME.value}" onclick="onPageLoad();">
													<c:forEach var="entity" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'feeFrequencyTypeList')}">
														<c:if test="${entity.id == FeeFrequencyType.ONETIME.value}">
															<c:out value="${entity.name}" />
														</c:if>
													</c:forEach>
												</html-el:radio>
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right" valign="top">
												&nbsp;
											</td>
											<td valign="top">
												<div id="timeofchargeDiv">
													<mifos:mifoslabel name="Fees.selecttimeofcharge" />
													<br>
													<div id="loanTimeOfChargeDiv">
														<mifos:select property="loanCharge">
															<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'TimeOfCharges')}" var="timeOfcharge">
																<html-el:option value="${timeOfcharge.id}">${timeOfcharge.name}</html-el:option>
															</c:forEach>
														</mifos:select>
													</div>
													<div id="customerTimeOfChargeDiv">
														<mifos:select property="customerCharge">
															<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'CustomerTimeOfCharges')}" var="customerTimeOfCharge">
																<html-el:option value="${customerTimeOfCharge.id}">${customerTimeOfCharge.name}</html-el:option>
															</c:forEach>
														</mifos:select>
													</div>
												</div>
												<div id="scheduleDIV">
													<table width="90%" border="0" cellpadding="3" cellspacing="0">
														<tr class="fontnormal">
															<td align="left" valign="top" style="border-top: 1px solid #CECECE; border-left: 1px solid #CECECE; border-right: 1px solid #CECECE;">
																<table width="98%" border="0" cellspacing="0" cellpadding="2">
																	<tr class="fontnormal">

																		<td width="49%">
																			<html-el:radio property="feeRecurrenceType" value="${RecurrenceType.WEEKLY.value}" onclick="onPageLoad();">
																				<mifos:mifoslabel name="Fees.weekly" />
																			</html-el:radio>
																		</td>

																		<td width="49%">
																			<html-el:radio property="feeRecurrenceType" value="${RecurrenceType.MONTHLY.value}" onclick="onPageLoad();">
																				<mifos:mifoslabel name="Fees.monthly" />
																			</html-el:radio>
																		</td>

																	</tr>
																</table>
															</td>
														</tr>
														<tr class="fontnormal">
															<td width="59%" align="left" valign="top" style="border: 1px solid #CECECE;">

																<div id="weekDIV" style="height:40px; width:380px; display:block;">
																	<mifos:mifoslabel name="Fees.labelRecurWeeks" />
																	<table border="0" cellspacing="0" cellpadding="2">
																		<tr class="fontnormal">
																			<td colspan="4">
																				<mifos:mifoslabel name="Fees.labelRecurEvery" />
																				<mifos:mifosnumbertext property="weekRecurAfter" size="3" maxlength="3" />
																				<mifos:mifoslabel name="Fees.labelWeeks" />
																			</td>
																		</tr>
																	</table>
																</div>
																<div id="monthDIV" style="height:40px; width:380px; ">
																	<mifos:mifoslabel name="Fees.labelRecurMonths" />
																	<br>
																	<table border="0" cellspacing="0" cellpadding="2">
																		<tr class="fontnormal">
																			<td>
																				<mifos:mifoslabel name="Fees.labelRecurEvery" />
																				<mifos:mifosnumbertext property="monthRecurAfter" size="3" maxlength="3" />
																				<mifos:mifoslabel name="Fees.labelMonths" />
																			</td>
																		</tr>
																	</table>
																</div>
															</td>
														</tr>
													</table>
												</div>
											</td>
										</tr>
										<tr class="fontnormal">
											<td colspan="2" valign="top" class="fontnormalbold">
												<mifos:mifoslabel name="Fees.feecalculation" />
												<br>
												<br>
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel name="Fees.enteramount" mandatory="yes" />
											</td>
											<td valign="top">
												<mifos:mifosdecimalinput property="amount"></mifos:mifosdecimalinput>
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right" valign="middle" style="padding-top:25px;">
												<div id="rateDivHeading">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr class="fontnormal">
															<td colspan="3">
																<mifos:mifoslabel name="Fees.calculatefeeas" mandatory="yes" />
															</td>
														</tr>
													</table>
												</div>
											</td>
											<td valign="top">
												<div id="rateDiv">
													<table width="90%" border="0" cellspacing="0" cellpadding="0">
														<tr class="fontnormal">
															<td colspan="3">
																<mifos:mifoslabel name="Fees.or" />
															</td>
														</tr>

														<tr class="fontnormal">
															<td colspan="3">
																<img src="pages/framework/images/trans.gif" width="5" height="1">
															</td>
														</tr>
														<tr class="fontnormal">
															<td width="16%">
																<mifos:mifosdecimalinput property="rate" size="3" decimalFmt="3.5"></mifos:mifosdecimalinput>
																<mifos:mifoslabel name="Fees.percentof" />
																<mifos:select property="feeFormula">
																	<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'FormulaList')}" var="formula">
																		<html-el:option value="${formula.id}">${formula.name}</html-el:option>
																	</c:forEach>
																</mifos:select>
															</td>
															<td width="17%">
																&nbsp;
															</td>
														</tr>
													</table>
												</div>
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												&nbsp;
											</td>
											<td valign="top">
												&nbsp;
											</td>
										</tr>
										<tr class="fontnormal">
											<td colspan="2" class="fontnormalbold">
												<mifos:mifoslabel name="Fees.accounting" />
												<br>
												<br>
											</td>
										</tr>
										<tr class="fontnormal">

											<td align="right">
												<mifos:mifoslabel name="Fees.GLCode" mandatory="yes" />
											</td>
											<td valign="top">
												<mifos:select property="glCode" style="width:136px;">
													<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'glCodeList')}" var="glCode">
														<html-el:option value="${glCode.glcodeId}">${glCode.glcode}</html-el:option>
													</c:forEach>
												</mifos:select>
											</td>
										</tr>
									</table>
									<html-el:hidden property="loanCategoryId" value="${FeeCategory.LOAN.value}" />
									<script>
										onPageLoad();
									</script>
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
												<html-el:submit property="preview" styleClass="buttn">
													<mifos:mifoslabel name="Fees.preview" />
												</html-el:submit>
												&nbsp;
												<html-el:button property="cancel" styleClass="cancelbuttn" onclick="javascript:fnCancel(this.form)">
													<mifos:mifoslabel name="Fees.cancel" />
												</html-el:button>
											</td>
										</tr>
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
