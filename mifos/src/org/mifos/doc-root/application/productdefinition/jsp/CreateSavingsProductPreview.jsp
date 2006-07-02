<!-- 

/**

 * CreateSavingsProductPreview.jsp    version: 1.0

 

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

<tiles:insert definition=".create">
	<tiles:put name="body" type="string">
		<script language="javascript">
		<!--
			function fnCancel(form) {
				form.method.value="cancel";
				form.action="savingsprdaction.do";
				form.submit();
			}
			function fnEdit(form) {
				form.method.value="previous";
				form.action="savingsprdaction.do";
				form.submit();
			}
			function func_disableSubmitBtn(){
				document.getElementById("submitBut").disabled=true;
			}
		//-->
		</script>
		<html-el:form action="/savingsprdaction" onsubmit="func_disableSubmitBtn();">
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
												<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" />
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
									<span class="heading"> <mifos:mifoslabel name="product.addnew" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="product.product"
											bundle="ProductDefUIResources" /> - </span>
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
									<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.prddetails" bundle="ProductDefUIResources" />
								</td>
							</tr>
							<tr>
								<td height="23" class="fontnormalbold">
									<mifos:mifoslabel name="product.prodinstname" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:out value="${requestScope.Context.valueObject.prdOfferingName}" /></span>
									<br>
									<mifos:mifoslabel name="product.shortname" bundle="ProductDefUIResources" />
									: <span class="fontnormal"><c:out value="${requestScope.Context.valueObject.prdOfferingShortName}" /></span>
									<br>
									<br>
									<mifos:mifoslabel name="product.desc" bundle="ProductDefUIResources" />
									<br>
									<span class="fontnormal"> <c:if test="${!empty requestScope.Context.valueObject.description}">
											<c:out value="${requestScope.Context.valueObject.description}" />
											<br>
										</c:if></span>
									<br>
									<mifos:mifoslabel name="product.prodcat" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:out value="${requestScope.Context.valueObject.prdCategory.productCategoryName}" /> </span>
									<br>
									<mifos:mifoslabel name="product.startdate" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:out value="${sessionScope.savingsprdactionform.startDate}" /> </span>
									<br>
									<mifos:mifoslabel name="product.enddate" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:out value="${sessionScope.savingsprdactionform.endDate}" /> </span>
									<br>
									<mifos:mifoslabel name="product.applfor" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:choose>
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
										</c:choose> </span>
									<br>
									<br>
									<mifos:mifoslabel name="product.tardepwidrest" bundle="ProductDefUIResources" />
									<br>
									<br>
									<span class="fontnormalbold"><mifos:mifoslabel name="product.typeofdep" bundle="ProductDefUIResources" />:<span class="fontnormal"> <c:choose>
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
											</c:choose> </span><br> <c:choose>
											<c:when test="${requestScope.Context.valueObject.savingsType.savingsTypeId eq 1}">
												<mifos:mifoslabel name="product.mandamntdep" bundle="ProductDefUIResources" />:
								</c:when>
											<c:otherwise>
												<mifos:mifoslabel name="product.recamtdep" bundle="ProductDefUIResources" />:
								</c:otherwise>
										</c:choose><span class="fontnormal"> <c:out value="${requestScope.Context.valueObject.recommendedAmount}" /></span><br> <mifos:mifoslabel name="product.recamtappl" bundle="ProductDefUIResources" /> :<span class="fontnormal"> <c:choose>
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
											</c:choose> </span><br> <mifos:mifoslabel name="product.maxamtwid" bundle="ProductDefUIResources" /> :<span class="fontnormal"> <c:out value="${requestScope.Context.valueObject.maxAmntWithdrawl}" /></span>
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
									: <span class="fontnormal"> <c:out value="${requestScope.Context.valueObject.interestRate}" /> <mifos:mifoslabel name="product.perc" bundle="ProductDefUIResources" /> </span>
									<br>
									<mifos:mifoslabel name="product.balusedfor" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.calc" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:choose>
											<c:when test="${param.intcalctype==null}">
												<c:forEach items="${requestScope.IntCalcTypesList}" var="IntCalcType">
													<c:if test="${IntCalcType.id eq requestScope.Context.valueObject.interestCalcType.interestCalculationTypeID}">
														<c:out value="${IntCalcType.name}" />
														<html-el:hidden property="intcalctype" value="${IntCalcType.name}" />
													</c:if>
												</c:forEach>
											</c:when>
											<c:otherwise>
												<c:out value="${param.intcalctype}" />
												<html-el:hidden property="intcalctype" value="${param.intcalctype}" />
											</c:otherwise>
										</c:choose> </span>
									<br>
									<mifos:mifoslabel name="product.timeper" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.calc" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:out value="${sessionScope.savingsprdactionform.timeForInterestCacl}" /> <c:forEach items="${requestScope.SavingsRecurrenceTypeList}" var="recType">
											<c:if test="${recType.recurrenceId eq sessionScope.savingsprdactionform.recurTypeFortimeForInterestCacl}">
												<c:out value="${recType.recurrenceName}" />
											</c:if>
										</c:forEach> </span>
									<br>
									<mifos:mifoslabel name="product.freq" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.postacc" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:out value="${sessionScope.savingsprdactionform.freqOfInterest}" /> <mifos:mifoslabel name="product.month" bundle="ProductDefUIResources" /> </span>
									<br>
									<mifos:mifoslabel name="product.minbalreq" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.calc" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:out value="${requestScope.Context.valueObject.minAmntForInt}" /> </span>
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
									<mifos:mifoslabel name="product.glcodedep" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:forEach var="glCode" items="${requestScope.depositGLCodes}">
											<c:if test="${glCode.glcodeId == requestScope.Context.valueObject.depositGLCode.glcodeId}">
												<c:out value="${glCode.glcode}" />
											</c:if>
										</c:forEach> </span>
									<br>
									<mifos:mifoslabel name="product.Glcodefor" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="${ConfigurationConstants.SERVICE_CHARGE}" bundle="ProductDefUIResources" />
									: <span class="fontnormal"> <c:forEach var="glCode" items="${requestScope.interestGLCodes}">
											<c:if test="${glCode.glcodeId == requestScope.Context.valueObject.interestGLCode.glcodeId}">
												<c:out value="${glCode.glcode}" />
											</c:if>
										</c:forEach> </span>
								</td>
							</tr>
						</table>
						<br>
						<table width="93%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td class="blueline">
									<html-el:button property="edit" styleClass="insidebuttn" onclick="fnEdit(this.form)">
										<mifos:mifoslabel name="product.prdedit" bundle="ProductDefUIResources" />&nbsp;<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" />&nbsp;<mifos:mifoslabel name="product.info"
											bundle="ProductDefUIResources" />

									</html-el:button>
									<br>
									<br>
								</td>
							</tr>
						</table>
						<br>
						<html-el:hidden property="method" value="create" />
						<html-el:hidden property="input" value="admin" />
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
