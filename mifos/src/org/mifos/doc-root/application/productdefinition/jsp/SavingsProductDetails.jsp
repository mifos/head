<!-- 

/**

 * SavingsProductDetails.jsp    version: 1.0

 

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
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">

		<script language="javascript">
<!--
	function fnSearch(form) {
		form.method.value="search";
		form.action="savingsprdaction.do";
		form.submit();
	}
	function fnManage(form) {
		form.method.value="manage";
		form.action="savingsprdaction.do";
		form.submit();
	}
	function fnCancel() {
		savingsprdactionform.method.value="cancel";
		savingsprdactionform.input.value="admin";
		savingsprdactionform.action="savingsprdaction.do";
		savingsprdactionform.submit();
	}
	function changelog(form) {
		form.method.value="search";
		form.input.value="ChangeLog";
		document.getElementById("searchNode(search_name)").value="ChangeLogDetails";
		form.action="savingsprdaction.do?searchNode(search_name)=ChangeLogDetails";
		form.submit();
	}
//-->
</script>
		<html-el:form action="/savingsprdaction">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"> <html-el:link href="javascript:fnCancel()">
								<mifos:mifoslabel name="product.admin" bundle="ProductDefUIResources" />
							</html-el:link> / <html-el:link href="javascript:fnSearch(savingsprdactionform)">
								<mifos:mifoslabel name="product.savingsview" bundle="ProductDefUIResources" />
								<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" />
								<mifos:mifoslabel name="product.products" bundle="ProductDefUIResources" />
							</html-el:link> / </span><span class="fontnormal8ptbold"><c:out value="${requestScope.Context.valueObject.prdOfferingName}" /></span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="68%" height="23" class="headingorange">
									<c:out value="${requestScope.Context.valueObject.prdOfferingName}" />
								</td>
								<td width="32%" align="right">
									<html-el:link href="javascript:fnManage(savingsprdactionform)">

										<mifos:mifoslabel name="product.prdedit" bundle="ProductDefUIResources" />
										<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" />
										<mifos:mifoslabel name="product.info" bundle="ProductDefUIResources" />

									</html-el:link>
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
												<span class="fontnormal"> <c:choose>
														<c:when test="${requestScope.Context.valueObject.prdStatus.offeringStatusId eq 2}">
															<img src="pages/framework/images/status_activegreen.gif" width="8" height="9">&nbsp;
										</c:when>
														<c:otherwise>
															<img src="pages/framework/images/status_closedblack.gif" width="8" height="9">&nbsp;
										</c:otherwise>
													</c:choose> <c:forEach items="${requestScope.SavingsPrdStatusList}" var="PrdStatus">
														<c:if test="${PrdStatus.id eq requestScope.Context.valueObject.prdStatus.offeringStatusId}">
															<c:out value="${PrdStatus.name}" />
															<html-el:hidden property="status" value="${PrdStatus.name}" />
														</c:if>
													</c:forEach> </span><span class="fontnormal"></span>
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
												<span class="fontnormal"> <mifos:mifoslabel name="product.prodinstname" bundle="ProductDefUIResources" />: <c:out value="${requestScope.Context.valueObject.prdOfferingName}" /><br> <mifos:mifoslabel name="product.shortname"
														bundle="ProductDefUIResources" />: <c:out value="${requestScope.Context.valueObject.prdOfferingShortName}" /><br> <!--bug id 25574  added if loop--> <c:if
														test="${requestScope.Context.valueObject.description != null && 
	!(requestScope.Context.valueObject.description eq '')}">
														<br>
														<mifos:mifoslabel name="product.desc" bundle="ProductDefUIResources" />
														<br>
														<c:out value="${requestScope.Context.valueObject.description}" />
														<br>
														<br>
													</c:if> <mifos:mifoslabel name="product.prodcat" bundle="ProductDefUIResources" />: <c:out value="${requestScope.Context.valueObject.prdCategory.productCategoryName}" /><br> <mifos:mifoslabel name="product.startdate"
														bundle="ProductDefUIResources" />: <c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.Context.valueObject.startDate)}" /> <br> <mifos:mifoslabel name="product.enddate"
														bundle="ProductDefUIResources" />: <c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.Context.valueObject.endDate)}" /> <br> <mifos:mifoslabel name="product.applfor"
														bundle="ProductDefUIResources" />: <c:choose>
														<c:when test="${param.applfor==null}">
															<c:forEach items="${requestScope.SavingsApplForList}" var="ApplForList">
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
													</c:choose> <br> <mifos:mifoslabel name="product.typeofdep" bundle="ProductDefUIResources" />: <c:choose>
														<c:when test="${param.deposittype==null}">
															<c:forEach items="${requestScope.SavingsTypesList}" var="DepositType">
																<c:if test="${DepositType.id eq requestScope.Context.valueObject.savingsType.savingsTypeId}">
																	<c:out value="${DepositType.name}" />
																	<html-el:hidden property="deposittype" value="${DepositType.name}" />
																</c:if>
															</c:forEach>
														</c:when>
														<c:otherwise>
															<c:out value="${param.deposittype}" />
															<html-el:hidden property="deposittype" value="${param.deposittype}" />

														</c:otherwise>
													</c:choose> <br> <c:choose>
														<c:when test="${requestScope.Context.valueObject.savingsType.savingsTypeId eq 1}">
															<mifos:mifoslabel name="product.mandamntdep" bundle="ProductDefUIResources" />:
										</c:when>
														<c:otherwise>
															<mifos:mifoslabel name="product.recamtdep" bundle="ProductDefUIResources" />:
										</c:otherwise>
													</c:choose> <c:out value="${requestScope.Context.valueObject.recommendedAmount}" /><br> <mifos:mifoslabel name="product.recamtappl" bundle="ProductDefUIResources" />: <c:choose>
														<c:when test="${param.recamnt==null}">
															<c:forEach items="${requestScope.RecAmntUnitList}" var="RecAmnt">
																<c:if test="${RecAmnt.id eq requestScope.Context.valueObject.recommendedAmntUnit.recommendedAmntUnitId}">
																	<c:out value="${RecAmnt.name}" />
																	<html-el:hidden property="recamnt" value="${RecAmnt.name}" />
																</c:if>
															</c:forEach>
														</c:when>
														<c:otherwise>
															<c:out value="${param.recamnt}" />
															<html-el:hidden property="recamnt" value="${param.recamnt}" />
														</c:otherwise>
													</c:choose> <br> <mifos:mifoslabel name="product.maxamtwid" bundle="ProductDefUIResources" />: <c:out value="${requestScope.Context.valueObject.maxAmntWithdrawl}" /> </span>
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
												<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />
												:
												<c:out value="${requestScope.Context.valueObject.interestRate}" />
												<mifos:mifoslabel name="product.perc" bundle="ProductDefUIResources" />
												<br>
												<mifos:mifoslabel name="product.balusedfor" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.calc" bundle="ProductDefUIResources" />
												:
												<c:choose>
													<c:when test="${param.intcalctype==null}">
														<c:forEach items="${requestScope.IntCalcTypesList}" var="IntCalcType">
															<c:if test="${IntCalcType.id eq requestScope.Context.valueObject.interestCalcType.interestCalculationTypeID}">
																<c:out value="${IntCalcType.name}" />
																<html-el:hidden property="intcalctype" value="${IntCalcType.name}" />
																<br>
															</c:if>
														</c:forEach>
													</c:when>
													<c:otherwise>
														<c:out value="${param.intcalctype}" />
														<html-el:hidden property="intcalctype" value="${param.intcalctype}" />
														<br>
													</c:otherwise>
												</c:choose>
												<!-- Bug id 26832 added mifos label instead getting it from database-->
												<mifos:mifoslabel name="product.timeper" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.calc" bundle="ProductDefUIResources" />
												:
												<c:out value="${requestScope.Context.valueObject.timePerForInstcalc.meeting.meetingDetails.recurAfter}" />
												<c:forEach items="${requestScope.SavingsRecurrenceTypeList}" var="recType">
													<c:if test="${recType.recurrenceId eq requestScope.Context.valueObject.timePerForInstcalc.meeting.meetingDetails.recurrenceType.recurrenceId}">
														<c:out value="${recType.recurrenceName}" />
													</c:if>
												</c:forEach>
												<html-el:hidden property="timeForInterestCacl" value="${requestScope.Context.valueObject.timePerForInstcalc.meeting.meetingDetails.recurAfter}" />
												<html-el:hidden property="recurTypeFortimeForInterestCacl" value="${requestScope.Context.valueObject.timePerForInstcalc.meeting.meetingDetails.recurrenceType.recurrenceId}" />
												<html-el:hidden property="timePerForInstcalc.prdOfferingMeetingId" value="${requestScope.Context.valueObject.timePerForInstcalc.prdOfferingMeetingId}" />
												<br>
												<mifos:mifoslabel name="product.freq" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.postacc" bundle="ProductDefUIResources" />
												:
												<c:out value="${requestScope.Context.valueObject.freqOfPostIntcalc.meeting.meetingDetails.recurAfter}" />
												<mifos:mifoslabel name="product.month" bundle="ProductDefUIResources" />
												<html-el:hidden property="freqOfInterest" value="${requestScope.Context.valueObject.freqOfPostIntcalc.meeting.meetingDetails.recurAfter}" />
												<html-el:hidden property="freqOfPostIntcalc.prdOfferingMeetingId" value="${requestScope.Context.valueObject.freqOfPostIntcalc.prdOfferingMeetingId}" />
												<br>
												<mifos:mifoslabel name="product.minbalreq" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.calc" bundle="ProductDefUIResources" />
												:
												<c:out value="${requestScope.Context.valueObject.minAmntForInt}" />
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
												<span class="fontnormal"> <mifos:mifoslabel name="product.glcodedep" bundle="ProductDefUIResources" />: <c:forEach var="glCode" items="${requestScope.depositGLCodes}">
														<c:if test="${glCode.glcodeId == requestScope.Context.valueObject.depositGLCode.glcodeId}">
															<c:out value="${glCode.glcode}" />
														</c:if>
													</c:forEach> <br> <mifos:mifoslabel name="product.Glcodefor" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />: <c:forEach var="glCode"
														items="${requestScope.interestGLCodes}">
														<c:if test="${glCode.glcodeId == requestScope.Context.valueObject.interestGLCode.glcodeId}">
															<c:out value="${glCode.glcode}" />
														</c:if>
													</c:forEach> <br> <br> <!--bug id 25573 added the link --> <html-el:link href="javascript:changelog(savingsprdactionform)">
														<mifos:mifoslabel name="product.changelog" bundle="ProductDefUIResources" />
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
				<html-el:hidden property="method" value="manage" />
				<html-el:hidden property="input" />
				<html-el:hidden property="prdCategory.productCategoryID" value="${requestScope.Context.valueObject.prdCategory.productCategoryID}" />
				<html-el:hidden property="searchNode(search_name)" value="SavingsProducts" />
				<html-el:hidden property="prdOfferingName" value="${requestScope.Context.valueObject.prdOfferingName}" />
				<html-el:hidden property="prdOfferingId" value="${requestScope.Context.valueObject.prdOfferingId}" />
			</table>
		</html-el:form>
	</tiles:put>
</tiles:insert>
