<!-- 

/**

 * EditSavingsProductPreview.jsp    version: 1.0

 

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
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".view">
	<tiles:put name="body" type="string">

		<script language="javascript">
<!--
	function fnEdit() {
		savingsproductactionform.action="savingsproductaction.do?method=previousManage";
		savingsproductactionform.submit();
	}
	function func_disableSubmitBtn(){
		document.getElementById("submitBut").disabled=true;
	}
	function fnCancel() {
		savingsproductactionform.action="savingsproductaction.do?method=cancelEdit";
		savingsproductactionform.submit();
	}
//-->
</script>
		<html-el:form action="/savingsproductaction.do?method=update" onsubmit="func_disableSubmitBtn();">
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<html-el:hidden property="input" value="details" />  
			<html-el:hidden	property="searchNode(search_name)" value="SavingsProducts" />
			<c:set	value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"> <html-el:link href="AdminAction.do?method=load">
								<mifos:mifoslabel name="product.admin" bundle="ProductDefUIResources" />
							</html-el:link> / <html-el:link href="savingsproductaction.do?method=search">
								<mifos:mifoslabel name="product.savingsview" bundle="ProductDefUIResources" />
								<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" />
								<mifos:mifoslabel name="product.products" bundle="ProductDefUIResources" />
							</html-el:link> / <html-el:link href="savingsproductaction.do?method=get&prdOfferingId=${BusinessKey.prdOfferingId}&randomNUm=${sessionScope.randomNUm}">
								<c:out value="${BusinessKey.prdOfferingName}" />
							</html-el:link></span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td class="headingorange">
									<span class="heading"><c:out value="${BusinessKey.prdOfferingName}" /> - </span>
									<mifos:mifoslabel name="product.preview" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.productinfo" bundle="ProductDefUIResources" />
								</td>
							</tr>
							<tr>
								<td class="fontnormal">
									<mifos:mifoslabel name="product.previewfields" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.clicksubmit" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.clickcancel" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.withoutsubmit" bundle="ProductDefUIResources" />
								</td>
							</tr>
						</table>
						<br>
						<font class="fontnormalRedBold"><html-el:errors bundle="ProductDefUIResources" /></font>
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="100%" height="23" class="fontnormalbold">
									<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.prddetails" bundle="ProductDefUIResources" />
								</td>
							</tr>
							<tr>
								<td height="23" class="fontnormalbold">
									<mifos:mifoslabel name="product.prodinstname" bundle="ProductDefUIResources" isColonRequired="yes"/>
									<span class="fontnormal"> <c:out value="${sessionScope.savingsproductactionform.prdOfferingName}" /> </span>
									<br>
									<mifos:mifoslabel name="product.shortname" bundle="ProductDefUIResources" isColonRequired="yes"/>
									<span class="fontnormal"> <c:out value="${sessionScope.savingsproductactionform.prdOfferingShortName}" /> </span>
									<br>
									<br>
									<mifos:mifoslabel name="product.desc" bundle="ProductDefUIResources" />
									<br>
									<span class="fontnormal"> <c:out value="${sessionScope.savingsproductactionform.description}" />
									<br>
									</span>
									<br>
									<mifos:mifoslabel name="product.prodcat" bundle="ProductDefUIResources" isColonRequired="yes"/>
									<span class="fontnormal"> 
									<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'SavingsProductCategoryList')}" var="category">
											<c:if test="${category.productCategoryID eq sessionScope.savingsproductactionform.prdCategory}">
												<c:out value="${category.productCategoryName}" />
											</c:if>
										</c:forEach> </span>
									<br>
									<mifos:mifoslabel name="product.startdate" bundle="ProductDefUIResources" isColonRequired="yes"/>
									<span class="fontnormal"> <c:out value="${sessionScope.savingsproductactionform.startDate}" /> </span>
									<br>
									<mifos:mifoslabel name="product.enddate" bundle="ProductDefUIResources" isColonRequired="yes"/>
									<span class="fontnormal">  <c:out value="${sessionScope.savingsproductactionform.endDate}" /> </span>
									<br>
									<mifos:mifoslabel name="product.applfor" bundle="ProductDefUIResources" isColonRequired="yes"/>
									<span class="fontnormal"> <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'SavingsApplForList')}" var="ApplForList">
												<c:if test="${ApplForList.id eq sessionScope.savingsproductactionform.prdApplicableMaster}">
													<c:out value="${ApplForList.name}" />
												</c:if>
											</c:forEach> </span>
									<br>
									<br>
									<mifos:mifoslabel name="product.tardepwidrest" bundle="ProductDefUIResources" />
									<br>
									<br>
									<mifos:mifoslabel name="product.typeofdep" bundle="ProductDefUIResources" isColonRequired="yes"/>
									<span class="fontnormal"> 
										<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'SavingsTypesList')}" var="DepositType">
													<c:if test="${DepositType.id eq sessionScope.savingsproductactionform.savingsType}">
														<c:out value="${DepositType.name}" />
													</c:if>
										</c:forEach> 
										</span>
									<br>
									<c:choose>
												<c:when test="${sessionScope.savingsproductactionform.savingsType eq 1}">
													<mifos:mifoslabel name="product.mandamntdep" bundle="ProductDefUIResources" isColonRequired="yes"/>
												</c:when>
												<c:otherwise>
													<mifos:mifoslabel name="product.recamtdep" bundle="ProductDefUIResources" isColonRequired="yes"/>
												</c:otherwise>
											</c:choose>
									<span class="fontnormal"> <c:out value="${sessionScope.savingsproductactionform.recommendedAmount}" /> </span>
									<br>
									<mifos:mifoslabel name="product.recamtappl" bundle="ProductDefUIResources" isColonRequired="yes"/>
									<span class="fontnormal"> 
										<c:forEach	items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'RecAmntUnitList')}" var="RecAmnt">
											<c:if test="${RecAmnt.id eq sessionScope.savingsproductactionform.recommendedAmntUnit}">
													<c:out value="${RecAmnt.name}" />
											</c:if>
										</c:forEach> </span>
									<br>
									<mifos:mifoslabel name="product.maxamtwid" bundle="ProductDefUIResources" isColonRequired="yes"/>
									<span class="fontnormal"> <c:out value="${sessionScope.savingsproductactionform.maxAmntWithdrawl}" /></span>
								</td>
							</tr>
						</table>
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="100%" height="23" class="fontnormalbold">
									<mifos:mifoslabel name="product.status" bundle="ProductDefUIResources" />
								</td>
							</tr>
							<tr>
								<td height="23" class="fontnormalbold">
									<mifos:mifoslabel name="product.status" bundle="ProductDefUIResources" isColonRequired="yes"/>
									<span class="fontnormal"> 
										<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'PrdCategoryStatusList')}" var="statusValue">
											<c:if test="${statusValue.offeringStatusId eq sessionScope.savingsproductactionform.status}">
												<c:out value="${statusValue.prdState.name}" />
											</c:if>
										</c:forEach></span>
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
								<td height="23" class="fontnormalbold">
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" isColonRequired="yes"/>
									<span class="fontnormal"> <c:out value="${sessionScope.savingsproductactionform.interestRate}" /> <mifos:mifoslabel name="product.perc" bundle="ProductDefUIResources" /> </span>
									<br>
									<mifos:mifoslabel name="product.balusedfor" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.calc" bundle="ProductDefUIResources" isColonRequired="yes"/>
									<span class="fontnormal">
										 <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'IntCalcTypesList')}" var="IntCalcType">
											<c:if test="${IntCalcType.id eq sessionScope.savingsproductactionform.interestCalcType}">
												<c:out value="${IntCalcType.name}" />
											</c:if>
										</c:forEach> </span><br>
									<mifos:mifoslabel name="product.timeper" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.calc" bundle="ProductDefUIResources" isColonRequired="yes"/>
									<span class="fontnormal"> 
										<c:out value="${sessionScope.savingsproductactionform.timeForInterestCacl}" /> 
										<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'SavingsRecurrenceTypeList')}"	var="recType">
											<c:if test="${recType.recurrenceId eq sessionScope.savingsproductactionform.recurTypeFortimeForInterestCacl}">
												<c:out value="${recType.recurrenceName}" />
											</c:if>
										</c:forEach></span>
									<br>
									<mifos:mifoslabel name="product.freq" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.postacc" bundle="ProductDefUIResources" isColonRequired="yes"/>
									<span class="fontnormal"> <c:out value="${sessionScope.savingsproductactionform.freqOfInterest}" /> <mifos:mifoslabel name="product.month" bundle="ProductDefUIResources" /> </span>
									<br>
									<mifos:mifoslabel name="product.minbalreq" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.calc" bundle="ProductDefUIResources" isColonRequired="yes"/>
									<span class="fontnormal"> <c:out value="${sessionScope.savingsproductactionform.minAmntForInt}" /> </span>
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
									<mifos:mifoslabel name="product.glcodedep" bundle="ProductDefUIResources" isColonRequired="yes"/>
									<span class="fontnormal"> 
									 <c:forEach var="glCode" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'depositGLCodes')}">
											<c:if test="${glCode.glcodeId == sessionScope.savingsproductactionform.depositGLCode}">
												<c:out value="${glCode.glcode}" />
											</c:if>
										</c:forEach> 
										 </span>
									<br>
									<mifos:mifoslabel name="product.Glcodefor" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" isColonRequired="yes"/>
									<span class="fontnormal"> 
										<c:forEach var="glCode" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'interestGLCodes')}">
											<c:if test="${glCode.glcodeId == sessionScope.savingsproductactionform.interestGLCode}">
												<c:out value="${glCode.glcode}" />
											</c:if>
										</c:forEach> 
										</span>
								</td>
							</tr>
						</table>
						<br>
						<table width="93%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td class="blueline">
									<span class="fontnormal"> <html-el:button property="edit" styleClass="insidebuttn" onclick="fnEdit()">
											<mifos:mifoslabel name="product.prdedit" bundle="ProductDefUIResources" />&nbsp;<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" />&nbsp;<mifos:mifoslabel name="product.info"
												bundle="ProductDefUIResources" />
										</html-el:button> <br> <br> </span>
								</td>
							</tr>
						</table>
						<br>
						<table width="93%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center">
									&nbsp;
									<html-el:submit styleClass="buttn" property="submitBut">
										<mifos:mifoslabel name="product.butsubmit" bundle="ProductDefUIResources" />
									</html-el:submit>
									&nbsp;
									<html-el:button property="cancel" styleClass="cancelbuttn" onclick="javascript:fnCancel()">
										<mifos:mifoslabel name="product.cancel" bundle="ProductDefUIResources" />
									</html-el:button>
								</td>
							</tr>
						</table>
						<br>
						</html-el:form>
						</tiles:put>
						</tiles:insert>
