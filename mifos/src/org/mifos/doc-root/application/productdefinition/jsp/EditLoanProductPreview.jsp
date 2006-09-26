<!-- 

/**

 * EditLoanProductPreview.jsp    version: 1.0

 

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
			function fnCancel(form) {
				form.method.value="editCancel";
				form.action="loanproductaction.do";
				form.submit();
			}
			function fnEdit(form) {
				form.method.value="editPrevious";
				form.action="loanproductaction.do";
				form.submit();
			}
			function func_disableSubmitBtn(){
				document.getElementById("submitBut").disabled=true;
			}
		//-->
		</script>
		<html-el:form action="/loanproductaction" onsubmit="func_disableSubmitBtn();">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"> <html-el:link href="loanproductaction.do?method=cancelCreate&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="product.admin" bundle="ProductDefUIResources" />
							</html-el:link> / <html-el:link href="loanproductaction.do?method=viewAllLoanProducts&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="product.savingsview" bundle="ProductDefUIResources" />
								<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
								<mifos:mifoslabel name="product.products" bundle="ProductDefUIResources" />
							</html-el:link> / <html-el:link href="loanproductaction.do?method=get&prdOfferingId=${sessionScope.loanproductactionform.prdOfferingId}&randomNUm=${sessionScope.randomNUm}">
								<c:out value="${param.prdOfferName}" />
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
									<span class="heading"><c:out value="${param.prdOfferName}" /> - </span>
									<mifos:mifoslabel name="product.preview" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.productinfo" bundle="ProductDefUIResources" />
								</td>
							</tr>
							<tr>
								<td class="fontnormal">
									<mifos:mifoslabel name="product.previewfields" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.clicksubmit" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.clickcancel" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.withoutsubmit" bundle="ProductDefUIResources" />
								</td>
							</tr>
						</table>
						<font class="fontnormalRedBold"><html-el:errors bundle="ProductDefUIResources" /></font>
						<br>
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="100%" height="23" class="fontnormalbold">
									<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.prddetails" bundle="ProductDefUIResources" />
								</td>
							</tr>
							<tr>
								<td height="23" class="fontnormalbold">
									<mifos:mifoslabel name="product.prodinstname" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:out value="${sessionScope.loanproductactionform.prdOfferingName}" /></span>
									<br>
									<mifos:mifoslabel name="product.shortname" bundle="ProductDefUIResources" />
									: <span class="fontnormal"><c:out value="${sessionScope.loanproductactionform.prdOfferingShortName}" /></span>
									<br>
									<br>
									<mifos:mifoslabel name="product.desc" bundle="ProductDefUIResources" />
									<br>
									<span class="fontnormal"> <c:if test="${!empty sessionScope.loanproductactionform.description}">
											<c:out value="${sessionScope.loanproductactionform.description}" />
											<br>
										</c:if></span>
									<br>
									<mifos:mifoslabel name="product.prodcat" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'LoanProductCategoryList')}" var="category">
											<c:if test="${category.productCategoryID eq sessionScope.loanproductactionform.prdCategory}">
												<c:out value="${category.productCategoryName}" />
											</c:if>
										</c:forEach> </span>
									<br>
									<mifos:mifoslabel name="product.startdate" bundle="ProductDefUIResources" />
									: <span class="fontnormal"><c:out value="${sessionScope.loanproductactionform.startDate}" /></span>
									<br>
									<mifos:mifoslabel name="product.enddate" bundle="ProductDefUIResources" />
									: <span class="fontnormal"><c:out value="${sessionScope.loanproductactionform.endDate}" /></span>
									<br>
									<mifos:mifoslabel name="product.applfor" bundle="ProductDefUIResources" />
									: <span class="fontnormal"><c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'LoanApplForList')}" var="ApplForList">
											<c:if test="${ApplForList.id eq sessionScope.loanproductactionform.prdApplicableMaster}">
												<c:out value="${ApplForList.name}" />

											</c:if>
										</c:forEach> </span>
									<br>
									<mifos:mifoslabel name="product.inclin" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.cyclecounter" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:choose>
											<c:when test="${sessionScope.loanproductactionform.loanCounter==1}">
												<mifos:mifoslabel name="product.yes" bundle="ProductDefUIResources" />
											</c:when>
											<c:otherwise>
												<mifos:mifoslabel name="product.no" bundle="ProductDefUIResources" />
											</c:otherwise>
										</c:choose> </span>
									<br>
									<mifos:mifoslabel name="product.max" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.amount" bundle="ProductDefUIResources" />
									: <span class="fontnormal"><c:out value="${sessionScope.loanproductactionform.maxLoanAmount}" /> </span>
									<br>
									<mifos:mifoslabel name="product.min" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.amount" bundle="ProductDefUIResources" />
									: <span class="fontnormal"><c:out value="${sessionScope.loanproductactionform.minLoanAmount}" /> </span>
									<br>
									<mifos:mifoslabel name="product.default" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.amount" bundle="ProductDefUIResources" />
									: <span class="fontnormal"><c:out value="${sessionScope.loanproductactionform.defaultLoanAmount}" /> </span>
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
									<mifos:mifoslabel name="product.status" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'LoanPrdStatusList')}" var="PrdStatus">
											<c:if test="${PrdStatus.offeringStatusId eq sessionScope.loanproductactionform.prdStatus}">
												<c:out value="${PrdStatus.prdState.name}" />
											</c:if>
										</c:forEach> </span>
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
									<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.type" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'InterestTypesList')}" var="InterestTypes">
											<c:if test="${InterestTypes.id eq sessionScope.loanproductactionform.interestTypes}">
												<c:out value="${InterestTypes.name}" />
											</c:if>
										</c:forEach> </span>
									<br>
									<mifos:mifoslabel name="product.max" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:out value="${sessionScope.loanproductactionform.maxInterestRate}" /> <mifos:mifoslabel name="product.perc" bundle="ProductDefUIResources" /></span>
									<br>
									<mifos:mifoslabel name="product.min" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:out value="${sessionScope.loanproductactionform.minInterestRate}" /> <mifos:mifoslabel name="product.perc" bundle="ProductDefUIResources" /></span>
									<br>
									<mifos:mifoslabel name="product.default" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.prdrate" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:out value="${sessionScope.loanproductactionform.defInterestRate}" /> <mifos:mifoslabel name="product.perc" bundle="ProductDefUIResources" /></span>
									<br>
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
									<mifos:mifoslabel name="product.freqofinst" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:out value="${sessionScope.loanproductactionform.recurAfter}" /> <c:if test="${sessionScope.loanproductactionform.freqOfInstallments eq 1}">
											<mifos:mifoslabel name="product.week" bundle="ProductDefUIResources" />
										</c:if> <c:if test="${sessionScope.loanproductactionform.freqOfInstallments eq 2}">
											<mifos:mifoslabel name="product.month" bundle="ProductDefUIResources" />
										</c:if> </span>
									<br>
									<mifos:mifoslabel name="product.maxinst" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:out value="${sessionScope.loanproductactionform.maxNoInstallments}" /></span>
									<br>
									<mifos:mifoslabel name="product.mininst" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:out value="${sessionScope.loanproductactionform.minNoInstallments}" /></span>
									<br>
									<mifos:mifoslabel name="product.definst" bundle="ProductDefUIResources" />
									: <span class="fontnormal"><c:out value="${sessionScope.loanproductactionform.defNoInstallments}" /></span>
									<br>
									<mifos:mifoslabel name="product.gracepertype" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'LoanGracePeriodTypeList')}" var="LoanGracePeriodType">
											<c:if test="${LoanGracePeriodType.id eq sessionScope.loanproductactionform.gracePeriodType}">
												<c:out value="${LoanGracePeriodType.name}" />
											</c:if>
										</c:forEach> </span>
									<br>
									<mifos:mifoslabel name="product.graceperdur" bundle="ProductDefUIResources" />
									: <span class="fontnormal"><c:out value="${sessionScope.loanproductactionform.gracePeriodDuration}" /> <mifos:mifoslabel name="product.installments" bundle="ProductDefUIResources" /></span>
									<br>
									<mifos:mifoslabel name="product.prinlastinst" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:choose>
											<c:when test="${sessionScope.loanproductactionform.intDedDisbursementFlag==1}">
												<mifos:mifoslabel name="product.yes" bundle="ProductDefUIResources" />
											</c:when>
											<c:otherwise>
												<mifos:mifoslabel name="product.no" bundle="ProductDefUIResources" />
											</c:otherwise>
										</c:choose> </span>
									<br>
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.deductedatdis" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:choose>
											<c:when test="${sessionScope.loanproductactionform.prinDueLastInstFlag==1}">
												<mifos:mifoslabel name="product.yes" bundle="ProductDefUIResources" />
											</c:when>
											<c:otherwise>
												<mifos:mifoslabel name="product.no" bundle="ProductDefUIResources" />
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
									<mifos:mifoslabel name="product.feestypes" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <br> <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanprdfeeselectedlist')}" var="prdOfferingFees">
											<c:out value="${prdOfferingFees.feeName}" />
											<br>
										</c:forEach></span>
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
									<mifos:mifoslabel name="product.srcfunds" bundle="ProductDefUIResources" />
									:
									<br>
									<span class="fontnormal"><br> <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanprdfundselectedlist')}" var="loanOffeingFund">
											<c:out value="${loanOffeingFund.fundName}" />
											<br>
										</c:forEach></span>
									<br>
									<mifos:mifoslabel name="product.productglcode" bundle="ProductDefUIResources" />
									:
									<br>
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:forEach var="glCode" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'interestGLCodes'}">
											<c:if test="${glCode.glcodeId == sessionScope.loanproductactionform.interestGLCode}">
												<c:out value="${glCode.glcode}" />
											</c:if>
										</c:forEach></span>
									<br>
									<mifos:mifoslabel name="product.principal" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:forEach var="glCode" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'principalGLCodes')}">
											<c:if test="${glCode.glcodeId == sessionScope.loanproductactionform.principalGLCode}">
												<c:out value="${glCode.glcode}" />
											</c:if>
										</c:forEach></span>
									<br>
								</td>
							</tr>
						</table>
						<br>
						<table width="93%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td class="blueline">
									<span class="fontnormal"> <html-el:button property="edit" styleClass="insidebuttn" onclick="fnEdit(this.form)">
											<mifos:mifoslabel name="product.prdedit" bundle="ProductDefUIResources" />&nbsp;<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />&nbsp;<mifos:mifoslabel name="product.info" bundle="ProductDefUIResources" />
										</html-el:button> <br> <br> <br> </span>
								</td>
							</tr>
						</table>
						<span class="fontnormal"> </span>
						<br>
						<table width="93%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center">
									&nbsp;
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
			<html-el:hidden property="method" value="update" />
			<html-el:hidden property="prdOfferName" value="${param.prdOfferName}" />
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
