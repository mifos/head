<!-- 

/**

 * SavingsProductDetails.jsp    version: 1.0

 

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
<tiles:insert definition=".view">
	<tiles:put name="body" type="string">

		<script language="javascript">
<!--
	function fnManage(form) {
		form.method.value="manage";
		form.action="savingsproductaction.do";
		form.submit();
	}
	/*
	function changelog(form) {
		form.method.value="search";
		form.input.value="ChangeLog";
		document.getElementById("SavingsProduct.SearchName").value="ChangeLogDetails";
		form.action="savingsprdaction.do?searchNode(search_name)=ChangeLogDetails";
		form.submit();
	}*/
//-->
</script>
		<html-el:form action="/savingsproductaction.do">
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<c:set	value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"> <html-el:link href="AdminAction.do?method=load">
								<mifos:mifoslabel name="product.admin" bundle="ProductDefUIResources" />
							</html-el:link> / 
							<a	href="savingsproductaction.do?method=search">
								<mifos:mifoslabel name="product.savingsview" bundle="ProductDefUIResources" />
								<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" />
								<mifos:mifoslabel name="product.products" bundle="ProductDefUIResources" />
							</a> / </span><span class="fontnormal8ptbold"><c:out value="${BusinessKey.prdOfferingName}" /></span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="68%" height="23" class="headingorange">
									<c:out value="${BusinessKey.prdOfferingName}" />
								</td>
								<td width="32%" align="right">
									<a	href="savingsproductaction.do?method=manage&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">

										<mifos:mifoslabel name="product.prdedit" bundle="ProductDefUIResources" />
										<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" />
										<mifos:mifoslabel name="product.info" bundle="ProductDefUIResources" />

									</a>
								</td>
							</tr>
							<tr>
								<td>
									<font class="fontnormalRedBold"><html-el:errors bundle="CustomerSearchUIResources" /> </font>
								</td>
							</tr>
							<tr>
								<td height="23" colspan="2" class="fontnormalbold">
									<span class="fontnormal"> </span><span class="fontnormal"> </span>
									<table width="100%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td height="23" class="fontnormalbold">
												<span class="fontnormal">
												 <c:choose>
														<c:when test="${BusinessKey.prdStatus.offeringStatusId == PrdStatus.SAVINGS_ACTIVE.value}">
															<img src="pages/framework/images/status_activegreen.gif" width="8" height="9">&nbsp;
														</c:when>
														<c:otherwise>
															<img src="pages/framework/images/status_closedblack.gif" width="8" height="9">&nbsp;
														</c:otherwise>
													</c:choose>
										<c:out value="${BusinessKey.prdStatus.prdState.name}" />
										<html-el:hidden property="status" value="${BusinessKey.prdStatus.prdState.name}" />
										 </span><span class="fontnormal"></span>
											</td>
										</tr>
										<tr>
											<td width="50%" height="23" class="fontnormalbold">
												<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.prddetails" bundle="ProductDefUIResources" />
											</td>
										</tr>
										<tr>
											<td height="23" class="fontnormalbold">
												<span class="fontnormal"> <mifos:mifoslabel name="product.prodinstname" bundle="ProductDefUIResources" isColonRequired="yes"/> 
												<c:out value="${BusinessKey.prdOfferingName}" /><br> 
												<mifos:mifoslabel name="product.shortname"	bundle="ProductDefUIResources" isColonRequired="yes"/> 
												<c:out value="${BusinessKey.prdOfferingShortName}" /><br> 
												<c:if test="${BusinessKey.description != null && !(BusinessKey.description eq '')}">
														<br>
														<mifos:mifoslabel name="product.desc" bundle="ProductDefUIResources" />
														<br>
														<c:out value="${BusinessKey.description}" />
														<br>
														<br>
													</c:if> <mifos:mifoslabel name="product.prodcat" bundle="ProductDefUIResources" />: <c:out value="${BusinessKey.prdCategory.productCategoryName}" /><br> 
													<mifos:mifoslabel name="product.startdate"	bundle="ProductDefUIResources" isColonRequired="yes"/> 
													<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,BusinessKey.startDate)}" /> <br>
													 <mifos:mifoslabel name="product.enddate"	bundle="ProductDefUIResources" isColonRequired="yes"/>
													 <c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,BusinessKey.endDate)}" /> <br>
													  <mifos:mifoslabel name="product.applfor"	bundle="ProductDefUIResources" isColonRequired="yes"/>
													  	<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'SavingsApplForList')}" var="ApplForList">
																<c:if test="${ApplForList.id eq BusinessKey.prdApplicableMaster.id}">
																	<c:out value="${ApplForList.name}" />
																	<html-el:hidden property="applfor" value="${ApplForList.name}" />
																</c:if>
															</c:forEach>
														 <br> 
														 <mifos:mifoslabel name="product.typeofdep" bundle="ProductDefUIResources" />: 
														 	<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'SavingsTypesList')}" var="DepositType">
																<c:if test="${DepositType.id eq BusinessKey.savingsType.id}">
																	<c:out value="${DepositType.name}" />
																	<html-el:hidden property="deposittype" value="${DepositType.name}" />
																</c:if>
															</c:forEach>
															<br> <c:choose>
														<c:when test="${BusinessKey.savingsType.id eq 1}">
															<mifos:mifoslabel name="product.mandamntdep" bundle="ProductDefUIResources" isColonRequired="yes"/>
														</c:when>
														<c:otherwise>
															<mifos:mifoslabel name="product.recamtdep" bundle="ProductDefUIResources" isColonRequired="yes"/>
														</c:otherwise>
														</c:choose>
														<c:out value="${BusinessKey.recommendedAmount}" />
														<br> 
														<mifos:mifoslabel name="product.recamtappl" bundle="ProductDefUIResources" isColonRequired="yes"/>
															
															<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'RecAmntUnitList')}" var="RecAmnt">
																<c:if test="${RecAmnt.id eq BusinessKey.recommendedAmntUnit.id}">
																	<c:out value="${RecAmnt.name}" />
																	<html-el:hidden property="recamnt" value="${RecAmnt.name}" />
																</c:if>
															</c:forEach>
														<br> 
														<mifos:mifoslabel name="product.maxamtwid" bundle="ProductDefUIResources" isColonRequired="yes"/>
														<c:out value="${BusinessKey.maxAmntWithdrawl}" /> </span>
												</td>
											</tr>
										</table>
										<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td width="100%" height="23" class="fontnormalbold">
												<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />
											</td>
										</tr>
										<tr>
											<td height="23" class="fontnormal">
												<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" isColonRequired="yes"/>
												<c:out value="${BusinessKey.interestRate}" />
												<mifos:mifoslabel name="product.perc" bundle="ProductDefUIResources" />
												<br>
												<mifos:mifoslabel name="product.balusedfor" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.calc" bundle="ProductDefUIResources" isColonRequired="yes"/>
												<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'IntCalcTypesList')}" var="IntCalcType">
													<c:if test="${IntCalcType.id eq BusinessKey.interestCalcType.id}">
														<c:out value="${IntCalcType.name}" />
														<html-el:hidden property="intcalctype" value="${IntCalcType.name}" />
													</c:if>
												</c:forEach>
												<br>
												<mifos:mifoslabel name="product.timeper" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.calc" bundle="ProductDefUIResources" isColonRequired="yes"/>
												<c:out value="${BusinessKey.timePerForInstcalc.meeting.meetingDetails.recurAfter}" />
												<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'SavingsRecurrenceTypeList')}" var="recType">
													<c:if test="${recType.recurrenceId eq BusinessKey.timePerForInstcalc.meeting.meetingDetails.recurrenceType.recurrenceId}">
														<c:out value="${recType.recurrenceName}" />
													</c:if>
												</c:forEach>
												<br>
												<mifos:mifoslabel name="product.freq" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.postacc" bundle="ProductDefUIResources" isColonRequired="yes"/>
												<c:out value="${BusinessKey.freqOfPostIntcalc.meeting.meetingDetails.recurAfter}" />
												<mifos:mifoslabel name="product.month" bundle="ProductDefUIResources" />
												<br>
												<mifos:mifoslabel name="product.minbalreq" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.calc" bundle="ProductDefUIResources" isColonRequired="yes"/>
												<c:out value="${BusinessKey.minAmntForInt}" />
												<br>
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
												<span class="fontnormal"> <mifos:mifoslabel name="product.glcodedep" bundle="ProductDefUIResources" isColonRequired="yes"/>
												
												<c:forEach var="glCode" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'depositGLCodes')}">
														<c:if test="${glCode.glcodeId == BusinessKey.depositGLCode.glcodeId}">
															<c:out value="${glCode.glcode}" />
														</c:if>
													</c:forEach> <br> <mifos:mifoslabel name="product.Glcodefor" bundle="ProductDefUIResources" /> 
													<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" isColonRequired="yes"/>
													
													<c:forEach var="glCode"	items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'interestGLCodes')}">
														<c:if test="${glCode.glcodeId == BusinessKey.interestGLCode.glcodeId}">
															<c:out value="${glCode.glcode}" />
														</c:if>
													</c:forEach> <br> <br>
													 <html-el:link	href="savingsproductaction.do?method=loadChangeLog&entityType=SavingsProduct&entityId=${BusinessKey.prdOfferingId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}&prdOfferName=${requestScope.BusinessKey.prdOfferingName}">
														<mifos:mifoslabel name="product.viewchangelog" bundle="ProductDefUIResources" />
													</html-el:link> 
													</span>
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
		</html-el:form>
	</tiles:put>
</tiles:insert>
