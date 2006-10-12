<!-- 

/**

 * CreateLoanProduct.jsp    version: 1.0

 

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
<%@taglib uri="/tags/date" prefix="date"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".create">
	<tiles:put name="body" type="string">
		<script>
		<!--
			function showMeetingFrequency(){
				if (document.getElementsByName("freqOfInstallments")[1].checked == true){
					document.getElementsByName("week")[0].style.display = "none";
					document.getElementsByName("month")[0].style.display = "block";
				}
				else {
					document.getElementsByName("week")[0].style.display = "block";
					document.getElementsByName("freqOfInstallments")[0].checked = true;
					document.getElementsByName("month")[0].style.display = "none";
				}
			}
			function fnCancel(form) {
				form.method.value="cancelCreate";
				form.action="loanproductaction.do";
				form.submit();
			}
			function fnIntDesbr() {
				if(document.getElementsByName("intDedDisbursementFlag")[0].checked==true) {
					document.getElementsByName("gracePeriodType")[0].disabled=true;
					document.getElementsByName("gracePeriodType")[0].selectedIndex=0;
					document.getElementsByName("gracePeriodDuration")[0].value="";			
					document.getElementsByName("gracePeriodDuration")[0].disabled=true;
				}
				else {
					document.getElementsByName("gracePeriodType")[0].disabled=false;
					document.getElementsByName("gracePeriodDuration")[0].disabled=false;
				}
			}
			function fnGracePeriod() {
				if(document.getElementsByName("gracePeriodType")[0].selectedIndex==0 ||
					document.getElementsByName("gracePeriodType")[0].value==1) {
					document.getElementsByName("gracePeriodDuration")[0].value="";
					document.getElementsByName("gracePeriodDuration")[0].disabled=true;
				}else {
					document.getElementsByName("gracePeriodDuration")[0].disabled=false;
				}
			}
		//-->
		</script>
		<script src="pages/framework/js/date.js"></script>
		<html-el:form action="/loanproductaction" onsubmit="return (validateMyForm(startDate,startDateFormat,startDateYY) && 
				validateMyForm(endDate,endDateFormat,endDateYY))" focus="prdOfferingName">
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
															<img src=" pages/framework/images/timeline/bigarrow.gif" width="17" height="17">
														</td>
														<td class="timelineboldorange">
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
															<img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17">
														</td>
														<td class="timelineboldorangelight">
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
												<span class="heading"> <mifos:mifoslabel name="product.addnew" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="product.product"
														bundle="ProductDefUIResources" /> - </span>
												<mifos:mifoslabel name="product.enter" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.productinfo" bundle="ProductDefUIResources" />
											</td>
										</tr>
										<tr>
											<td class="fontnormal">
												<mifos:mifoslabel name="product.compfields" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.clickpreview" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.clickcancinfo" bundle="ProductDefUIResources" />
												<br>
												<mifos:mifoslabel name="product.fieldsrequired" mandatory="yes" bundle="ProductDefUIResources" />
											</td>
										</tr>
									</table>
									<br>
									<font class="fontnormalRedBold"><html-el:errors bundle="ProductDefUIResources" /></font>
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td colspan="2" class="fontnormalbold">
												<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.prddetails" bundle="ProductDefUIResources" />
												<br>
												<br>
											</td>
										</tr>
										<tr class="fontnormal">
											<td width="30%" align="right">
												<mifos:mifoslabel name="product.prodinstname" mandatory="yes" bundle="ProductDefUIResources" />
												:
											</td>
											<td width="70%" valign="top">
												<mifos:mifosalphanumtext property="prdOfferingName" />
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel name="product.shortname" mandatory="yes" bundle="ProductDefUIResources" />
												:
											</td>
											<td valign="top">
												<mifos:mifosalphanumtext property="prdOfferingShortName" maxlength="4" size="4" />
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right" valign="top">
												<span class="mandatorytext"></span>
												<mifos:mifoslabel name="product.desc" bundle="ProductDefUIResources" />
												:
											</td>
											<td valign="top">
												<html-el:textarea property="description" style="width:320px; height:110px;">
												</html-el:textarea>
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel name="product.prodcat" mandatory="yes" bundle="ProductDefUIResources" />
												:
											</td>
											<td valign="top">
												<mifos:select property="prdCategory" style="width:136px;">
													<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'LoanProductCategoryList')}" var="category">
														<html-el:option value="${category.productCategoryID}">${category.productCategoryName}</html-el:option>
													</c:forEach>
												</mifos:select>
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel name="product.startdate" mandatory="yes" bundle="ProductDefUIResources" />
												:
											</td>
											<td valign="top">
												<date:datetag property="startDate" />
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<span class="mandatorytext"></span>
												<mifos:mifoslabel name="product.enddate" bundle="ProductDefUIResources" />
												:
											</td>
											<td valign="top">
												<date:datetag property="endDate" />
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel mandatory="yes" name="product.applfor" bundle="ProductDefUIResources" />
												:
											</td>
											<td valign="top">
												<mifos:select property="prdApplicableMaster" style="width:136px;">
													<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'LoanApplForList')}" var="prdAppl">
														<html-el:option value="${prdAppl.id}">${prdAppl.name}</html-el:option>
													</c:forEach>
												</mifos:select>
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel name="product.inclin" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.cyclecounter" bundle="ProductDefUIResources" />
												:
											</td>
											<td valign="top">
												<html-el:checkbox property="loanCounter" value="1" />
											</td>
										</tr>

										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel mandatory="yes" name="product.max" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.amount" bundle="ProductDefUIResources" />
												:
											</td>
											<td valign="top">
												<mifos:mifosdecimalinput property="maxLoanAmount" />
											</td>
										</tr>

										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel mandatory="yes" name="product.min" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.amount" bundle="ProductDefUIResources" />
												:
											</td>
											<td valign="top">
												<mifos:mifosdecimalinput property="minLoanAmount" />
											</td>
										</tr>

										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel name="product.default" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.amount" bundle="ProductDefUIResources" />
												:
											</td>
											<td valign="top">
												<mifos:mifosdecimalinput property="defaultLoanAmount" />
											</td>
										</tr>
									</table>
									<br>

									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td colspan="2" class="fontnormalbold">
												<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />
												<br>
												<br>
											</td>
										</tr>
										<tr class="fontnormal">
											<td width="30%" align="right">
												<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" mandatory="yes" />
												<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.type" bundle="ProductDefUIResources" />
												:
											</td>
											<td width="70%" valign="top">
												<mifos:select property="interestTypes" style="width:136px;">
													<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'InterestTypesList')}" var="intType">
														<html-el:option value="${intType.id}">${intType.name}</html-el:option>
													</c:forEach>

												</mifos:select>
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel mandatory="yes" name="product.max" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />
												:
											</td>
											<td valign="top">
												<mifos:mifosdecimalinput max="999" min="0" property="maxInterestRate" decimalFmt="10.5" />
												<mifos:mifoslabel name="product.rate" bundle="ProductDefUIResources" />
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel mandatory="yes" name="product.min" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />
												:
											</td>
											<td valign="top">
												<mifos:mifosdecimalinput max="999" min="0" property="minInterestRate" decimalFmt="10.5" />
												<mifos:mifoslabel name="product.rate" bundle="ProductDefUIResources" />
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<span class="mandatorytext"></span>
												<mifos:mifoslabel mandatory="yes" name="product.default" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />
												:
											</td>
											<td valign="top">
												<mifos:mifosdecimalinput max="999" min="0" property="defInterestRate" decimalFmt="10.5" />
												<mifos:mifoslabel name="product.rate" bundle="ProductDefUIResources" />
											</td>
										</tr>
									</table>
									<br>
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td colspan="2" class="fontnormalbold">
												<mifos:mifoslabel name="product.repaysch" bundle="ProductDefUIResources" />
												<br>
												<br>
											</td>
										</tr>
										<tr class="fontnormal">
											<td width="30%" align="right" valign="top">
												<mifos:mifoslabel mandatory="yes" name="product.freqofinst" bundle="ProductDefUIResources" />
												:
											</td>
											<td width="70%" valign="top">

												<table width="90%" border="0" cellpadding="3" cellspacing="0">
													<tr class="fontnormal">
														<td align="left" valign="top" style="border-top: 1px solid #CECECE; border-left: 1px solid #CECECE; border-right: 1px solid #CECECE;">
															<table width="98%" border="0" cellspacing="0" cellpadding="2">
																<tr valign="top" class="fontnormal">
																	<td width="24%">
																		<html-el:radio property="freqOfInstallments" value="1" onclick="showMeetingFrequency();" />
																		<mifos:mifoslabel name="product.weeks" bundle="ProductDefUIResources" />
																	</td>
																	<td width="55%">
																		<html-el:radio property="freqOfInstallments" value="2" onclick="showMeetingFrequency();" />
																		<mifos:mifoslabel name="product.months" bundle="ProductDefUIResources" />
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr class="fontnormal">
														<td width="59%" align="left" valign="top" style="border: 1px solid #CECECE;">
															<div id="weekDIV" style="height:40px; width:380px;">
																<mifos:mifoslabel name="product.enterfoll" bundle="ProductDefUIResources" />
																:
																<table border="0" cellspacing="0" cellpadding="2">
																	<tr class="fontnormal">
																		<td colspan="3">
																			<mifos:mifoslabel name="product.recur" bundle="ProductDefUIResources" />
																			<mifos:mifosnumbertext property="recurAfter" size="3" maxlength="3" />
																		</td>
																		<td>
																			<span id="week"> <mifos:mifoslabel name="product.week" bundle="ProductDefUIResources" /> </span> <span id="month"> <mifos:mifoslabel name="product.month" bundle="ProductDefUIResources" /> </span>
																		</td>
																	</tr>
																</table>
															</div>
														</td>
													</tr>
												</table>
												<script>
										showMeetingFrequency();
									</script>
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel mandatory="yes" name="product.maxinst" bundle="ProductDefUIResources" />
												:
											</td>
											<td valign="top">
												<mifos:mifosnumbertext property="maxNoInstallments" />
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel mandatory="yes" name="product.mininst" bundle="ProductDefUIResources" />
												:
											</td>
											<td valign="top">
												<mifos:mifosnumbertext property="minNoInstallments" />
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<span class="mandatorytext"></span>
												<mifos:mifoslabel name="product.definst" bundle="ProductDefUIResources" mandatory="yes" />
												:
											</td>
											<td valign="top">
												<mifos:mifosnumbertext property="defNoInstallments" />
											</td>
										</tr>
										<tr class="fontnormal" id="intdeddis">
											<td align="right">
												<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.deductedatdis" bundle="ProductDefUIResources" />
												:
											</td>
											<td valign="top">
												<html-el:checkbox property="intDedDisbursementFlag" value="1" onclick="fnIntDesbr();" />
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel name="product.prinlastinst" bundle="ProductDefUIResources" />
												:
											</td>
											<td valign="top">
												<html-el:checkbox property="prinDueLastInstFlag" value="1" />
											</td>
										</tr>
										<tr class="fontnormal" id="gracepertype">
											<td align="right">
												<mifos:mifoslabel name="product.gracepertype" bundle="ProductDefUIResources" />
												:
											</td>
											<td valign="top">
												<mifos:select property="gracePeriodType" style="width:136px;" onchange="fnGracePeriod();">
													<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'LoanGracePeriodTypeList')}" var="graceType">
														<html-el:option value="${graceType.id}">${graceType.name}</html-el:option>
													</c:forEach>

												</mifos:select>
											</td>
										</tr>
										<tr class="fontnormal" id="graceperdur">
											<td align="right">
												<mifos:mifoslabel name="product.graceperdur" bundle="ProductDefUIResources" />
												:
											</td>
											<td valign="top">
												<mifos:mifosnumbertext property="gracePeriodDuration" />
												<mifos:mifoslabel name="product.installments" bundle="ProductDefUIResources" />
											</td>
										</tr>

									</table>
									<br>
									<script type="text/javascript">
										fnIntDesbr();
										fnGracePeriod();
									</script>

									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td colspan="2" class="fontnormalbold">
												<mifos:mifoslabel name="product.fees&pen" bundle="ProductDefUIResources" />
												<br>
												<br>
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right" valign="top">
												<mifos:mifoslabel name="product.attachfeestypes" bundle="ProductDefUIResources" />
												:
											</td>
											<td valign="top">
												<table width="80%" border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td class="fontnormal">
															<mifos:mifoslabel name="product.clickfeetypes" bundle="ProductDefUIResources" />
														</td>
													</tr>
													<tr>
														<td>
															<img src="pages/framework/images/trans.gif" width="1" height="1">
														</td>
													</tr>
												</table>

												<c:set var="LoanFeesList" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'LoanFeesList')}" />
												<c:set var="selectedFeeList" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanprdfeeselectedlist')}" />
												<mifos:MifosSelect property="prdOfferinFees" input="LoanFeesList" output="selectedFeeList" property1="feeId" property2="feeName" multiple="true">
												</mifos:MifosSelect>
											</td>
										</tr>
									</table>

									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td colspan="2" class="fontnormalbold">
												<mifos:mifoslabel name="product.accounting" bundle="ProductDefUIResources" />
												<br>
												<br>
											</td>
										</tr>
										<tr class="fontnormal">
											<td width="30%" align="right" valign="top">
												<mifos:mifoslabel name="product.srcfunds" bundle="ProductDefUIResources" />
												:
											</td>
											<td width="70%" valign="top">
												<table width="80%" border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td class="fontnormal">
															<mifos:mifoslabel name="product.clickfunds" bundle="ProductDefUIResources" />
														</td>
													</tr>
													<tr>
														<td>
															<img src="pages/framework/images/trans.gif" width="1" height="1">
														</td>
													</tr>
												</table>
												<c:set var="SrcFundsList" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'SrcFundsList')}" />
												<c:set var="selectedFundList" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanprdfundselectedlist')}" />
												<mifos:MifosSelect property="loanOfferingFunds" input="SrcFundsList" output="selectedFundList" property1="fundId" property2="fundName" multiple="true">
												</mifos:MifosSelect>

											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right" valign="top" style="padding-top:8px;">
												<mifos:mifoslabel mandatory="yes" name="product.productglcode" bundle="ProductDefUIResources" />
												:
											</td>
											<td valign="top">
												<table width="80%" border="0" cellspacing="0" cellpadding="2">
													<tr class="fontnormal">
														<td width="15%">
															<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
															:
														</td>
														<td width="85%">
															<mifos:select property="interestGLCode" style="width:136px;">
																<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'interestGLCodes')}" var="glCodes">
																	<html-el:option value="${glCodes.glcodeId}">${glCodes.glcode}</html-el:option>
																</c:forEach>
															</mifos:select>
														</td>
													</tr>
													<tr class="fontnormal">
														<td>
															<mifos:mifoslabel name="product.principal" bundle="ProductDefUIResources" />
															:
														</td>
														<td>
															<mifos:select property="principalGLCode" style="width:136px;">
																<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'principalGLCodes')}" var="glCodes">
																	<html-el:option value="${glCodes.glcodeId}">${glCodes.glcode}</html-el:option>
																</c:forEach>
															</mifos:select>
														</td>
													</tr>
												</table>
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
												<html-el:submit styleClass="buttn" style="width:70px" onclick="transferData(this.form.loanOfferingFunds);transferData(this.form.prdOfferinFees);">
													<mifos:mifoslabel name="product.preview" bundle="ProductDefUIResources" />
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
						<html-el:hidden property="method" value="preview" />
						<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
						</html-el:form>
						</tiles:put>
						</tiles:insert>