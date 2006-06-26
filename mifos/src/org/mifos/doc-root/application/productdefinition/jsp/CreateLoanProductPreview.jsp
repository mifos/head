<!-- 

/**

 * CreateLoanProductPreview.jsp    version: 1.0

 

 * Copyright © 2005-2006 Grameen Foundation USA

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

<tiles:insert definition=".create">
	<tiles:put name="body" type="string">
		<script language="javascript">
		<!--
			function fnCancel(form) {
				form.method.value="cancel";
				form.action="loanprdaction.do";
				form.submit();
			}
			function fnEdit(form) {
				form.method.value="previous";
				form.action="loanprdaction.do";
				form.submit();
			}
			function func_disableSubmitBtn(){
				document.getElementById("submitBut").disabled=true;
			}
		//-->
		</script>

		<html-el:form action="/loanprdaction" onsubmit="func_disableSubmitBtn();">
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
															<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
															<mifos:mifoslabel name="product.productinfo" bundle="ProductDefUIResources" />											
														</td>
													</tr>
												</table>
											</td>
											<td width="73%" align="right">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17">
														</td>
														<td class="timelineboldorange">
															<mifos:mifoslabel name="product.review" bundle="ProductDefUIResources" />
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
												<span class="heading"> 
												<mifos:mifoslabel name="product.addnew" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.product" bundle="ProductDefUIResources" /> - </span>												
												<mifos:mifoslabel name="product.review" bundle="ProductDefUIResources" />
											</td>
										</tr>
										<tr>
											<td class="fontnormal">
												<mifos:mifoslabel name="product.previewfields" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.clicksubmit" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.clickcancinfo" bundle="ProductDefUIResources" />
											</td>
										</tr>
									</table>
									<br>
									<font class="fontnormalRedBold"><html-el:errors bundle="ProductDefUIResources" /></font>
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td width="100%" height="23" class="fontnormalbold">
												<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />&nbsp;
												<mifos:mifoslabel name="product.prddetails" bundle="ProductDefUIResources" />
											</td>
										</tr>
										<tr>
											<td height="23" class="fontnormalbold">
												<mifos:mifoslabel name="product.prodinstname" bundle="ProductDefUIResources" />: 
												<span class="fontnormal"><c:out value="${requestScope.Context.valueObject.prdOfferingName}" /> </span>
												<br>
												<mifos:mifoslabel name="product.shortname" bundle="ProductDefUIResources" />:
												 <span class="fontnormal"><c:out value="${requestScope.Context.valueObject.prdOfferingShortName}" /> </span>
												<br>
												<br>
												<mifos:mifoslabel name="product.desc" bundle="ProductDefUIResources" />
												<br>
												<span class="fontnormal"> <c:if test="${!empty requestScope.Context.valueObject.description}">
														<c:out value="${requestScope.Context.valueObject.description}" />
														<br>
													</c:if> </span>
												<br>
												<mifos:mifoslabel name="product.prodcat" bundle="ProductDefUIResources" />:
												<span class="fontnormal"><c:out value="${requestScope.Context.valueObject.prdCategory.productCategoryName}" /> </span>
												<br>
												<mifos:mifoslabel name="product.startdate" bundle="ProductDefUIResources" />:
												<span class="fontnormal"> <c:out value="${sessionScope.loanprdactionform.startDate}" /> </span>
												<br>
												<mifos:mifoslabel name="product.enddate" bundle="ProductDefUIResources" />:
												<span class="fontnormal"><c:out value="${sessionScope.loanprdactionform.endDate}" /> </span>
												<br>
												<mifos:mifoslabel name="product.applfor" bundle="ProductDefUIResources" />:
												<span class="fontnormal"> <c:choose>
														<c:when test="${param.applfor==null}">
															<c:forEach items="${requestScope.LoanApplForList}" var="ApplForList">
																<c:if test="${ApplForList.id eq requestScope.Context.valueObject.prdApplicableMaster.prdApplicableMasterId}">
																	<c:out value="${ApplForList.name}" />
																	<html-el:hidden property="applfor" value="${ApplForList.name}" />
																</c:if>
															</c:forEach>
														</c:when>
														<c:otherwise>
															<c:out value="${param.applfor}" />
															<html-el:hidden property="applfor" value="${param.applfor}" />
														</c:otherwise>
													</c:choose></span>
												<br>
												<mifos:mifoslabel name="product.inclin" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="${ConfigurationConstants.LOAN}"	bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.cyclecounter" bundle="ProductDefUIResources" />					
												
												<span class="fontnormal"> <c:choose>
														<c:when test="${param.loancounter==null}">
															<c:forEach items="${requestScope.YesNoMasterList}" var="YesNoMaster">
																<c:if test="${YesNoMaster.id eq requestScope.Context.valueObject.loanCounterFlag}">
																	<c:out value="${YesNoMaster.name}" />
																	<html-el:hidden property="loancounter" value="${YesNoMaster.name}" />
																</c:if>
															</c:forEach>
														</c:when>
														<c:otherwise>
															<c:out value="${param.loancounter}" />
															<html-el:hidden property="loancounter" value="${param.loancounter}" />
														</c:otherwise>
													</c:choose> </span>
												<br>
												<mifos:mifoslabel name="product.max" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="${ConfigurationConstants.LOAN}"	bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.amount"	bundle="ProductDefUIResources" />:
												<span class="fontnormal"> <c:out value="${requestScope.Context.valueObject.maxLoanAmount}" /></span>												
												<br>
												<mifos:mifoslabel name="product.min" bundle="ProductDefUIResources" />										
												<mifos:mifoslabel name="${ConfigurationConstants.LOAN}"	bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.amount"	bundle="ProductDefUIResources" />
												:
												<span class="fontnormal"> <c:out value="${requestScope.Context.valueObject.minLoanAmount}" /></span>												
												<br>
												<mifos:mifoslabel name="product.default" bundle="ProductDefUIResources" />										
												<mifos:mifoslabel name="${ConfigurationConstants.LOAN}"	bundle="ProductDefUIResources" />									
												<mifos:mifoslabel name="product.amount"	bundle="ProductDefUIResources" />
												:
												<span class="fontnormal"> <c:out value="${requestScope.Context.valueObject.defaultLoanAmount}" /></span>
											</td>
										</tr>
									</table>

									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td width="100%" height="23" class="fontnormalbold">
											<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources"/>										
											<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />
											</td>
										</tr>
										<tr>
											<td height="23" class="fontnormalbold">
												<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources"/>
												<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.type" bundle="ProductDefUIResources" />:
									
												<span class="fontnormal"><c:choose>
														<c:when test="${param.inttype==null}">
															<c:forEach items="${requestScope.InterestTypesList}" var="InterestTypes">
																<c:if test="${InterestTypes.id eq requestScope.Context.valueObject.interestTypes.interestTypeId}">
																	<c:out value="${InterestTypes.name}" />
																	<html-el:hidden property="inttype" value="${InterestTypes.name}" />
																</c:if>
															</c:forEach>
														</c:when>
														<c:otherwise>
															<html-el:hidden property="graceper" value="${param.inttype}" />
															<c:out value="${param.inttype}" />
														</c:otherwise>
													</c:choose></span>
												<br>
												<mifos:mifoslabel name="product.max" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />									
												<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />:
												<span class="fontnormal"><c:out value="${requestScope.Context.valueObject.maxInterestRate}" /> <mifos:mifoslabel name="product.perc" bundle="ProductDefUIResources" /></span>
												<br>
												<mifos:mifoslabel name="product.min" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />									
												<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />:
												<span class="fontnormal"><c:out value="${requestScope.Context.valueObject.minInterestRate}" /> <mifos:mifoslabel name="product.perc" bundle="ProductDefUIResources" /></span>
												<br>
												<mifos:mifoslabel name="product.default" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />									
												<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />:
												<span class="fontnormal"><c:out value="${requestScope.Context.valueObject.defInterestRate}" /> <mifos:mifoslabel name="product.perc" bundle="ProductDefUIResources" /></span>
												<br>
												<%-- Commented because this feature is not implemented now. But it may be required in fututre.

									<mifos:mifoslabel name="product.intratecalcpymt"
										bundle="ProductDefUIResources" />: <c:choose>
										<c:when test="${param.intcalcrule==null}">
											<c:forEach items="${requestScope.InterestCalcRuleList}"
												var="InterestCalcRule">
												<c:if
													test="${InterestCalcRule.id eq requestScope.Context.valueObject.interestCalcRule.interestCalcRuleId}">
													<c:out value="${InterestCalcRule.name}" />
													<html-el:hidden property="intcalcrule"
														value="${InterestCalcRule.name}" />
												</c:if>
											</c:forEach>
										</c:when>
										<c:otherwise>
											<html-el:hidden property="intcalcrule"
												value="${param.intcalcrule}" />
											<c:out value="${param.intcalcrule}" />
										</c:otherwise>
									</c:choose> 
--%>
												</span>
											</td>
										</tr>
									</table>

									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td width="100%" height="23" class="fontnormalbold">
												<mifos:mifoslabel name="product.repaysch" bundle="ProductDefUIResources" />
											</td>
										</tr>
										<tr>
											<td height="23" class="fontnormalbold">
												<mifos:mifoslabel name="product.freqofinst" bundle="ProductDefUIResources" />:
												<span class="fontnormal"><c:if test="${sessionScope.loanprdactionform.freqOfInstallments eq 1}">
														<c:out value="${sessionScope.loanprdactionform.recurWeekDay}" />
														<mifos:mifoslabel name="product.week" bundle="ProductDefUIResources" />
													</c:if> <c:if test="${sessionScope.loanprdactionform.freqOfInstallments eq 2}">
														<c:out value="${sessionScope.loanprdactionform.recurMonthDay}" />
														<mifos:mifoslabel name="product.month" bundle="ProductDefUIResources" />
													</c:if> </span>
												<br>
												<mifos:mifoslabel name="product.maxinst" bundle="ProductDefUIResources" />:
												<span class="fontnormal"><c:out value="${requestScope.Context.valueObject.maxNoInstallments}" /></span>
												<br>
												<mifos:mifoslabel name="product.mininst" bundle="ProductDefUIResources" />:
												<span class="fontnormal"><c:out value="${requestScope.Context.valueObject.minNoInstallments}" /></span>
												<br>
												<mifos:mifoslabel name="product.definst" bundle="ProductDefUIResources" />:
												<span class="fontnormal"><c:out value="${requestScope.Context.valueObject.defNoInstallments}" /></span>
												<br>
												<mifos:mifoslabel name="product.gracepertype" bundle="ProductDefUIResources" />:
												<span class="fontnormal"><c:choose>
														<c:when test="${param.graceper==null}">
															<c:forEach items="${requestScope.LoanGracePeriodTypeList}" var="LoanGracePeriodType">
																<c:if test="${LoanGracePeriodType.id eq requestScope.Context.valueObject.gracePeriodType.gracePeriodTypeId}">
																	<c:out value="${LoanGracePeriodType.name}" />
																	<html-el:hidden property="graceper" value="${LoanGracePeriodType.name}" />
																</c:if>
															</c:forEach>
														</c:when>
														<c:otherwise>
															<html-el:hidden property="graceper" value="${param.graceper}" />
															<c:out value="${param.graceper}" />
														</c:otherwise>
													</c:choose></span>
												<br>
												<mifos:mifoslabel name="product.graceperdur" bundle="ProductDefUIResources" />:
												<span class="fontnormal"><c:out value="${requestScope.Context.valueObject.gracePeriodDuration}" /> <mifos:mifoslabel name="product.installments" bundle="ProductDefUIResources" /></span>
												<br>
												<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources"/>
												<mifos:mifoslabel name="product.deductedatdis" bundle="ProductDefUIResources" />:
												<span class="fontnormal"><c:choose>
														<c:when test="${param.intded==null}">
															<c:forEach items="${requestScope.YesNoMasterList}" var="YesNoMaster">
																<c:if test="${YesNoMaster.id eq requestScope.Context.valueObject.intDedDisbursementFlag}">
																	<c:out value="${YesNoMaster.name}" />
																	<html-el:hidden property="intded" value="${YesNoMaster.name}" />
																</c:if>
															</c:forEach>
														</c:when>
														<c:otherwise>
															<c:out value="${param.intded}" />
															<html-el:hidden property="intded" value="${param.prinlast}" />
														</c:otherwise>
													</c:choose> </span>
												<br>
												<mifos:mifoslabel name="product.prinlastinst" bundle="ProductDefUIResources" />:
												<span class="fontnormal"> <c:choose>
														<c:when test="${param.prinlast==null}">
															<c:forEach items="${requestScope.YesNoMasterList}" var="YesNoMaster">
																<c:if test="${YesNoMaster.id eq requestScope.Context.valueObject.prinDueLastInstFlag}">
																	<c:out value="${YesNoMaster.name}" />
																	<html-el:hidden property="prinlast" value="${YesNoMaster.name}" />
																</c:if>
															</c:forEach>
														</c:when>
														<c:otherwise>
															<c:out value="${param.prinlast}" />
															<html-el:hidden property="prinlast" value="${param.prinlast}" />
														</c:otherwise>
													</c:choose> </span>
											</td>
										</tr>
									</table>

									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td width="100%" height="23" class="fontnormalbold">
												<mifos:mifoslabel name="product.fees&pen" bundle="ProductDefUIResources" />
											</td>
										</tr>
										<tr>
											<td height="23" class="fontnormalbold">
												<mifos:mifoslabel name="product.feestypes" bundle="ProductDefUIResources" />:
												<span class="fontnormal"> <br> <c:forEach items="${requestScope.Context.valueObject.prdOfferingFeesSet}" var="prdOfferingFees">
														<c:set var="fees" value="${prdOfferingFees.fees}" />
														<c:out value="${fees.feeName}" />
														<br>
													</c:forEach></span>
												<br>
												<mifos:mifoslabel name="product.penaltytype" bundle="ProductDefUIResources" />:
												<span class="fontnormal"> <c:choose>
														<c:when test="${param.penaltype==null}">
															<c:forEach items="${requestScope.PenaltyTypesList}" var="PenaltyTypes">
																<c:if test="${PenaltyTypes.penaltyID eq requestScope.Context.valueObject.penalty.penaltyID}">
																	<c:out value="${PenaltyTypes.penaltyType}" />
																	<html-el:hidden property="penaltype" value="${PenaltyTypes.penaltyType}" />
																</c:if>
															</c:forEach>
														</c:when>
														<c:otherwise>
															<html-el:hidden property="penaltype" value="${param.penaltype}" />
															<c:out value="${param.penaltype}" />
														</c:otherwise>
													</c:choose> </span>
												<br>
												<mifos:mifoslabel name="product.penaltyrate" bundle="ProductDefUIResources" />:
												<span class="fontnormal"> <c:out value="${requestScope.Context.valueObject.penaltyRate}" /></span>
												<br>
												<mifos:mifoslabel name="product.gracepenalty" bundle="ProductDefUIResources" />:
												<span class="fontnormal"> <c:out value="${requestScope.Context.valueObject.penaltyGrace}" /> <mifos:mifoslabel name="product.days" bundle="ProductDefUIResources" /></span>
											</td>
										</tr>
									</table>
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td width="100%" height="23" class="fontnormalbold">
												<mifos:mifoslabel name="product.accounting" bundle="ProductDefUIResources" />
											</td>
										</tr>
										<tr>
											<td height="23" class="fontnormalbold">
												<mifos:mifoslabel name="product.srcfunds" bundle="ProductDefUIResources" />:
												<span class="fontnormal"><br> <c:forEach items="${requestScope.Context.valueObject.loanOffeingFundSet}" var="loanOffeingFund">
														<c:set var="fund" value="${loanOffeingFund.fund}" />
														<c:out value="${fund.fundName}" />
														<br>
													</c:forEach></span>
												<br>
												<mifos:mifoslabel name="product.productglcode" bundle="ProductDefUIResources" />:
												<br>
												<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources"/>:
												<span class="fontnormal"> <c:forEach var="glCode" items="${requestScope.interestGLCodes}">
														<c:if test="${glCode.glcodeId == requestScope.Context.valueObject.interestGLCode.glcodeId}">
															<c:out value="${glCode.glcode}" />
														</c:if>
													</c:forEach></span>
												<br>
												<mifos:mifoslabel name="product.principal" bundle="ProductDefUIResources" />:
												<span class="fontnormal"> <c:forEach var="glCode" items="${requestScope.principalGLCodes}">
														<c:if test="${glCode.glcodeId == requestScope.Context.valueObject.principalGLCode.glcodeId}">
															<c:out value="${glCode.glcode}" />
														</c:if>
													</c:forEach></span>
												<br>
												<mifos:mifoslabel name="product.penalties" bundle="ProductDefUIResources" />:
												<span class="fontnormal"> <c:forEach var="glCode" items="${requestScope.penaltyGLCodes}">
														<c:if test="${glCode.glcodeId == requestScope.Context.valueObject.penaltyGLCode.glcodeId}">
															<c:out value="${glCode.glcode}" />
														</c:if>
													</c:forEach> </span>
											</td>
										</tr>
									</table>
									<br>
									<table width="93%" border="0" cellpadding="0" cellspacing="0">
										<html-el:hidden property="method" value="create" />
										<html-el:hidden property="input" value="admin" />
										<tr>
											<td class="blueline">
												<html-el:button property="edit" styleClass="insidebuttn" onclick="fnEdit(this.form)">
													<mifos:mifoslabel name="product.prdedit" bundle="ProductDefUIResources" />&nbsp;<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />&nbsp;<mifos:mifoslabel 
													name="product.info" bundle="ProductDefUIResources" />													
												</html-el:button>
												<br>
												<br>
											</td>
										</tr>
									</table>
									<br>
									<table width="93%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center">
												<html-el:submit styleClass="buttn" style="width:70px" property="submitBut">
													<mifos:mifoslabel name="product.butsubmit" bundle="ProductDefUIResources" />
												</html-el:submit>
												&nbsp;
												<html-el:button property="cancel" styleClass="cancelbuttn" style="width:70px" onclick="javascript:fnCancel(this.form)">
													<mifos:mifoslabel name="product.cancel" bundle="ProductDefUIResources" />
												</html-el:button>
											</td>
										</tr>
									</table>
									<br>
								</td>
							</tr>
						</table>
						</html-el:form>
						</tiles:put>
						</tiles:insert>