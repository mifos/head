<!-- 

/**

 * LoanProductDetails.jsp    version: 1.0

 

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
		form.action="loanprdaction.do";
		form.submit();
	}
	function fnManage(form) {
		form.method.value="manage";
		form.action="loanprdaction.do";
		form.submit();
	}
	function fnCancel() {
		loanprdactionform.method.value="cancel";
		loanprdactionform.input.value="admin";
		loanprdactionform.action="loanprdaction.do";
		loanprdactionform.submit();
	}
	function changelog(form) {
		form.method.value="search";
		form.input.value="ChangeLog";
		document.getElementById("searchNode(search_name)").value="ChangeLogDetails";
		form.action="loanprdaction.do?searchNode(search_name)=ChangeLogDetails";
		form.submit();
	}
//-->
</script>
		<html-el:form action="/loanprdaction">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <html-el:link
						href="javascript:fnCancel()">
						<mifos:mifoslabel name="product.admin"
							bundle="ProductDefUIResources" />
					</html-el:link> / <html-el:link
						href="javascript:fnSearch(loanprdactionform)">
						<mifos:mifoslabel name="product.savingsview" bundle="ProductDefUIResources" />
					<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
					<mifos:mifoslabel name="product.products" bundle="ProductDefUIResources" />
					</html-el:link> / </span> <span class="fontnormal8ptbold"><c:out
						value="${requestScope.Context.valueObject.prdOfferingName}" /></span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="74%" height="23" class="headingorange"><c:out
								value="${requestScope.Context.valueObject.prdOfferingName}" /></td>
							<td width="26%" align="right"><html-el:link
								href="javascript:fnManage(loanprdactionform)">
								<mifos:mifoslabel name="product.prdedit"	bundle="ProductDefUIResources" />
								<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
								<mifos:mifoslabel name="product.info" bundle="ProductDefUIResources" />
							</html-el:link></td>
						</tr>
						<tr>
							<td height="23" colspan="2" class="fontnormalbold"><span
								class="fontnormal"> </span><span class="fontnormal"> </span>
								<font class="fontnormalRedBold"><html-el:errors
								bundle="ProductDefUIResources" /> </font>
							<table width="100%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td height="23" class="fontnormalbold"><span class="fontnormal">
									<c:choose>
										<c:when
											test="${requestScope.Context.valueObject.prdStatus.offeringStatusId eq 1}">
											<img src="pages/framework/images/status_activegreen.gif"
												width="8" height="9">&nbsp;
										</c:when>
										<c:otherwise>
											<img src="pages/framework/images/status_closedblack.gif"
												width="8" height="9">&nbsp;
										</c:otherwise>
									</c:choose> <c:forEach
										items="${requestScope.LoanPrdStatusList}" var="PrdStatus">
										<c:if
											test="${PrdStatus.id eq requestScope.Context.valueObject.prdStatus.offeringStatusId}">
											<c:out value="${PrdStatus.name}" />
											<html-el:hidden property="status" value="${PrdStatus.name}" />
										</c:if>
									</c:forEach> </span><span class="fontnormal"></span></td>
								</tr>
								<tr>
									<td width="50%" height="23" class="fontnormalbold">
									<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.prddetails" bundle="ProductDefUIResources" />
									</td>
								</tr>
								<tr>
									<td height="23" class="fontnormalbold"><span class="fontnormal">
									<mifos:mifoslabel name="product.prodinstname"
										bundle="ProductDefUIResources" />: <c:out
										value="${requestScope.Context.valueObject.prdOfferingName}" /><br>
									<mifos:mifoslabel name="product.shortname"
										bundle="ProductDefUIResources" />: <c:out
										value="${requestScope.Context.valueObject.prdOfferingShortName}" /><br>
<!--bug id 25574  added if loop-->
<c:if test="${requestScope.Context.valueObject.description != null && 
	!(requestScope.Context.valueObject.description eq '')}">
									<br>
									<mifos:mifoslabel name="product.desc"
										bundle="ProductDefUIResources" /><br>
									<c:out value="${requestScope.Context.valueObject.description}" /><br>
									<br>
</c:if>
									<mifos:mifoslabel name="product.prodcat"
										bundle="ProductDefUIResources" />: <c:out
										value="${requestScope.Context.valueObject.prdCategory.productCategoryName}" /><br>
									<mifos:mifoslabel name="product.startdate"
										bundle="ProductDefUIResources" />: 
<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.Context.valueObject.startDate)}" />
										<br>
									<mifos:mifoslabel name="product.enddate"
										bundle="ProductDefUIResources" />: 
<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.Context.valueObject.endDate)}" />
										<br>
									<mifos:mifoslabel name="product.applfor"
										bundle="ProductDefUIResources" />: <c:choose>
										<c:when test="${param.applfor==null}">
											<c:forEach items="${requestScope.LoanApplForList}"
												var="ApplForList">
												<c:if
													test="${ApplForList.id eq requestScope.Context.valueObject.prdApplicableMaster.prdApplicableMasterId}">
													<c:out value="${ApplForList.name}" />
													<html-el:hidden property="applfor"
														value="${ApplForList.name}" />
												</c:if>
											</c:forEach>
										</c:when>
										<c:otherwise>
											<c:out value="${param.applfor}" />
											<html-el:hidden property="applfor" value="${param.applfor}" />
										</c:otherwise>
									</c:choose> <br>
									<mifos:mifoslabel name="product.inclin" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.LOAN}"	bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.cyclecounter" bundle="ProductDefUIResources" />					
									: <c:choose>
										<c:when test="${param.loancounter==null}">
											<c:forEach items="${requestScope.YesNoMasterList}"
												var="YesNoMaster">
												<c:if
													test="${YesNoMaster.id eq requestScope.Context.valueObject.loanCounterFlag}">
													<c:out value="${YesNoMaster.name}" />
													<html-el:hidden property="loancounter"
														value="${YesNoMaster.name}" />
												</c:if>
											</c:forEach>
										</c:when>
										<c:otherwise>
											<c:out value="${param.loancounter}" />
											<html-el:hidden property="loancounter"
												value="${param.loancounter}" />
										</c:otherwise>
									</c:choose><br>
									<!--bug id 26829 added code to display min/max and default loan amounts-->
									<mifos:mifoslabel name="product.max" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.LOAN}"	bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.amount"	bundle="ProductDefUIResources" />: <c:out
										value="${requestScope.Context.valueObject.maxLoanAmount}" /><br>
									<mifos:mifoslabel name="product.min" bundle="ProductDefUIResources" />										
									<mifos:mifoslabel name="${ConfigurationConstants.LOAN}"	bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.amount"	bundle="ProductDefUIResources" />: <c:out
										value="${requestScope.Context.valueObject.minLoanAmount}" /><br>									
									<mifos:mifoslabel name="product.default" bundle="ProductDefUIResources" />																			
									<mifos:mifoslabel name="product.amount"	bundle="ProductDefUIResources" />: <c:out
										value="${requestScope.Context.valueObject.defaultLoanAmount}" />
									</span></td>
								</tr>
							</table>
							
							<table width="96%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td width="100%" height="23" class="fontnormalbold">
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources"/>										
									<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />
									</td>
								</tr>
								<tr>
									<td height="23" class="fontnormalbold"><span class="fontnormal">
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources"/>
									<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.type" bundle="ProductDefUIResources" />: <c:choose>
										<c:when test="${param.inttype==null}">
											<c:forEach items="${requestScope.InterestTypesList}"
												var="InterestTypes">
												<c:if
													test="${InterestTypes.id eq requestScope.Context.valueObject.interestTypes.interestTypeId}">
													<c:out value="${InterestTypes.name}" />
													<html-el:hidden property="inttype"
														value="${InterestTypes.name}" />
												</c:if>
											</c:forEach>
										</c:when>
										<c:otherwise>
											<html-el:hidden property="graceper" value="${param.inttype}" />
											<c:out value="${param.inttype}" />
										</c:otherwise>
									</c:choose> <br>
									<mifos:mifoslabel name="product.max" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />									
									<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />: <c:out
										value="${requestScope.Context.valueObject.maxInterestRate}" />
									<mifos:mifoslabel name="product.perc"
										bundle="ProductDefUIResources" /><br>
									<mifos:mifoslabel name="product.min" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />									
									<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />: <c:out
										value="${requestScope.Context.valueObject.minInterestRate}" />
									<mifos:mifoslabel name="product.perc"
										bundle="ProductDefUIResources" /><br>
									<mifos:mifoslabel name="product.default" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />									
									<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />: <c:out
										value="${requestScope.Context.valueObject.defInterestRate}" />
									<mifos:mifoslabel name="product.perc"
										bundle="ProductDefUIResources" /><br>
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
									</span></td>
								</tr>
							</table>
							
							<table width="96%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td width="100%" height="23" class="fontnormalbold"><mifos:mifoslabel
										name="product.repaysch" bundle="ProductDefUIResources" /></td>
								</tr>
								<tr>
									<!-- Bug id 26832 added mifos label instead getting it from database-->
									<td height="23" class="fontnormalbold"><span class="fontnormal">									 
									<html-el:hidden property="freqOfInstallments" value="${requestScope.Context.valueObject.prdOfferingMeeting.meeting.meetingDetails.recurrenceType.recurrenceId}" />
									<c:if test="${requestScope.Context.valueObject.prdOfferingMeeting.meeting.meetingDetails.recurrenceType.recurrenceId eq 1}">
										<mifos:mifoslabel name="product.freqofinst"	bundle="ProductDefUIResources" />:
										<c:out	value="${requestScope.Context.valueObject.prdOfferingMeeting.meeting.meetingDetails.recurAfter}" />
										<mifos:mifoslabel name="product.week" bundle="ProductDefUIResources" />
										<html-el:hidden property="recurWeekDay"	value="${requestScope.Context.valueObject.prdOfferingMeeting.meeting.meetingDetails.recurAfter}" />
									</c:if> 
									<c:if test="${requestScope.Context.valueObject.prdOfferingMeeting.meeting.meetingDetails.recurrenceType.recurrenceId eq 2}">
									    <mifos:mifoslabel name="product.freqofinst"	bundle="ProductDefUIResources" />:
										<c:out value="${requestScope.Context.valueObject.prdOfferingMeeting.meeting.meetingDetails.recurAfter}" />
										<mifos:mifoslabel name="product.month" bundle="ProductDefUIResources" />
										<html-el:hidden property="recurMonthDay" value="${requestScope.Context.valueObject.prdOfferingMeeting.meeting.meetingDetails.recurAfter}" />
									</c:if> 
									<html-el:hidden	property="prdOfferingMeeting.prdOfferingMeetingId" value="${requestScope.Context.valueObject.prdOfferingMeeting.prdOfferingMeetingId}" />
									<br>
									<mifos:mifoslabel name="product.maxinst"
										bundle="ProductDefUIResources" />: <c:out
										value="${requestScope.Context.valueObject.maxNoInstallments}" /><br>
									<mifos:mifoslabel name="product.mininst"
										bundle="ProductDefUIResources" />: <c:out
										value="${requestScope.Context.valueObject.minNoInstallments}" /><br>
									<mifos:mifoslabel name="product.definst"
										bundle="ProductDefUIResources" />: <c:out
										value="${requestScope.Context.valueObject.defNoInstallments}" /><br>
									<mifos:mifoslabel name="product.gracepertype"
										bundle="ProductDefUIResources" />: <c:choose>
										<c:when test="${param.graceper==null}">
											<c:forEach items="${requestScope.LoanGracePeriodTypeList}"
												var="LoanGracePeriodType">
												<c:if
													test="${LoanGracePeriodType.id eq requestScope.Context.valueObject.gracePeriodType.gracePeriodTypeId}">
													<c:out value="${LoanGracePeriodType.name}" />
													<html-el:hidden property="graceper"
														value="${LoanGracePeriodType.name}" />
												</c:if>
											</c:forEach>
										</c:when>
										<c:otherwise>
											<html-el:hidden property="graceper" value="${param.graceper}" />
											<c:out value="${param.graceper}" />
										</c:otherwise>
									</c:choose> <br>
									<mifos:mifoslabel name="product.graceperdur"
										bundle="ProductDefUIResources" />: <c:out
										value="${requestScope.Context.valueObject.gracePeriodDuration}" />
									<mifos:mifoslabel name="product.installments"
										bundle="ProductDefUIResources" /><br>
									<mifos:mifoslabel name="product.prinlastinst"
										bundle="ProductDefUIResources" />: <c:choose>
										<c:when test="${param.prinlast==null}">
											<c:forEach items="${requestScope.YesNoMasterList}"
												var="YesNoMaster">
												<c:if
													test="${YesNoMaster.id eq requestScope.Context.valueObject.prinDueLastInstFlag}">
													<c:out value="${YesNoMaster.name}" />
													<html-el:hidden property="prinlast"
														value="${YesNoMaster.name}" />
												</c:if>
											</c:forEach>
										</c:when>
										<c:otherwise>
											<c:out value="${param.prinlast}" />
											<html-el:hidden property="prinlast" value="${param.prinlast}" />
										</c:otherwise>
									</c:choose> <br>
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources"/>
									<mifos:mifoslabel name="product.deductedatdis" bundle="ProductDefUIResources" />: <c:choose>
										<c:when test="${param.intded==null}">
											<c:forEach items="${requestScope.YesNoMasterList}"
												var="YesNoMaster">
												<c:if
													test="${YesNoMaster.id eq requestScope.Context.valueObject.intDedDisbursementFlag}">
													<c:out value="${YesNoMaster.name}" />
													<html-el:hidden property="intded"
														value="${YesNoMaster.name}" />
												</c:if>
											</c:forEach>
										</c:when>
										<c:otherwise>
											<c:out value="${param.intded}" />
											<html-el:hidden property="intded" value="${param.prinlast}" />
										</c:otherwise>
									</c:choose> </span></td>
								</tr>
							</table>
							
							<table width="96%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td width="100%" height="23" class="fontnormalbold"><mifos:mifoslabel
										name="product.fees&pen" bundle="ProductDefUIResources" /></td>
								</tr>
								<tr>
									<td height="23" class="fontnormalbold"><span class="fontnormal">
									<mifos:mifoslabel name="product.feestypes"
										bundle="ProductDefUIResources" />: <br>
									<c:forEach
										items="${requestScope.Context.valueObject.prdOfferingFeesSet}"
										var="prdOfferingFees">
										<c:set var="fees" value="${prdOfferingFees.fees}" />
										<c:out value="${fees.feeName}" />
										<br>
									</c:forEach> <br>
									<mifos:mifoslabel name="product.penaltytype"
										bundle="ProductDefUIResources" />: <c:choose>
								<c:when test="${param.penaltype==null}">
									<c:forEach items="${requestScope.PenaltyTypesList}"
										var="PenaltyTypes">
										<c:if
											test="${PenaltyTypes.penaltyID eq requestScope.Context.valueObject.penalty.penaltyID}">
											<c:out value="${PenaltyTypes.penaltyType}" />
											<html-el:hidden property="penaltype"
												value="${PenaltyTypes.penaltyType}" />
										</c:if>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<html-el:hidden property="penaltype" value="${param.penaltype}" />
									<c:out value="${param.penaltype}" />
								</c:otherwise>
							</c:choose> <br>
									<mifos:mifoslabel name="product.penaltyrate"
										bundle="ProductDefUIResources" />: <c:out
										value="${requestScope.Context.valueObject.penaltyRate}" /><br>
									<mifos:mifoslabel name="product.gracepenalty"
										bundle="ProductDefUIResources" />: <c:out
										value="${requestScope.Context.valueObject.penaltyGrace}" />
										<mifos:mifoslabel name="product.days"
										bundle="ProductDefUIResources" /> </span>
									</td>
								</tr>
							</table>
							<table width="96%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td width="100%" height="23" class="fontnormalbold"><mifos:mifoslabel
										name="product.accounting" bundle="ProductDefUIResources" /></td>
								</tr>
								<tr>
									<td height="23" class="fontnormalbold"><span class="fontnormal">
									<mifos:mifoslabel name="product.srcfunds"
										bundle="ProductDefUIResources" /> : <br>
									<c:forEach
										items="${requestScope.Context.valueObject.loanOffeingFundSet}"
										var="loanOffeingFund">
										<c:set var="fund" value="${loanOffeingFund.fund}" />
										<c:out value="${fund.fundName}" />
										<br>
									</c:forEach> <br>
									<mifos:mifoslabel name="product.productglcode"
										bundle="ProductDefUIResources" />:<br>
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources"/>:
										<c:forEach var="glCode" items="${requestScope.interestGLCodes}"> 
											<c:if test="${glCode.glcodeId == requestScope.Context.valueObject.interestGLCode.glcodeId}">
												<c:out value="${glCode.glcode}"/>
											</c:if>
										</c:forEach>
										<br>
									<mifos:mifoslabel name="product.principal"
										bundle="ProductDefUIResources" />:
										<c:forEach var="glCode" items="${requestScope.principalGLCodes}"> 
											<c:if test="${glCode.glcodeId == requestScope.Context.valueObject.principalGLCode.glcodeId}">
												<c:out value="${glCode.glcode}"/>
											</c:if>
										</c:forEach>
										<br>
									<mifos:mifoslabel name="product.penalties"
										bundle="ProductDefUIResources" />:
										<c:forEach var="glCode" items="${requestScope.penaltyGLCodes}"> 
											<c:if test="${glCode.glcodeId == requestScope.Context.valueObject.penaltyGLCode.glcodeId}">
												<c:out value="${glCode.glcode}"/>
											</c:if>
										</c:forEach>
									</span><br>
									<br>
									<span class="fontnormal">
<!--bug id 25573 added the link -->
<html-el:link href="javascript:changelog(loanprdactionform)">
<mifos:mifoslabel name="product.changelog" bundle="ProductDefUIResources" />
</html-el:link>
									</span></td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					<br>
					</td>
					<html-el:hidden property="method" value="manage" />
					<html-el:hidden property="input" />
					<html-el:hidden property="prdCategory.productCategoryID"
						value="${requestScope.Context.valueObject.prdCategory.productCategoryID}" />
					<html-el:hidden property="searchNode(search_name)" value="LoanProducts" />
					<html-el:hidden property="prdOfferingName" value="${requestScope.Context.valueObject.prdOfferingName}"/>
					<html-el:hidden property="prdOfferingId" value="${requestScope.Context.valueObject.prdOfferingId}" />
				</tr>
			</table>
		</html-el:form>
	</tiles:put>
</tiles:insert>
