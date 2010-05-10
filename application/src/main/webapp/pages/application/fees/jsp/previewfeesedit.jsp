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

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
	<span id="page.id" title="previewfeesedit" />
	
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<script src="pages/application/fees/js/Fees.js"></script>
		<html-el:form action="/feeaction.do?method=update" onsubmit="return func_disableSubmitBtn('submitBtn');">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"> <html-el:link href="feeaction.do?method=cancelCreate&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="Fees.admin">
								</mifos:mifoslabel>
							</html-el:link> / <html-el:link href="feeaction.do?method=viewAll&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="Fees.viewfees">
								</mifos:mifoslabel>
							</html-el:link> / <html-el:link href="feeaction.do?method=cancelEdit&feeId=${model.id}&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
								<c:out value="${model.name}" />
							</html-el:link> </span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td class="headingorange">
									<span class="heading"> <c:out value="${model.name}" /> - </span>
									<mifos:mifoslabel name="Fees.previewfeeinformation">
									</mifos:mifoslabel>
								</td>
							</tr>
							<tr>
								<td class="fontnormal">
									<mifos:mifoslabel name="Fees.messageForPreviewFeeDetails">
									</mifos:mifoslabel>
								</td>
							</tr>
						</table>
						<br>
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td class="fontnormalbold">
									<font class="fontnormalRedBold"><html-el:errors bundle="FeesUIResources" /> </font>
								</td>
							</tr>
							<tr>
								<td width="100%" height="23" class="fontnormal">
									<span class="fontnormalbold"> <c:choose>
											<c:when test="${model.rateBasedFee}">
												<mifos:mifoslabel name="Fees.amountcalculatedas" />
											</c:when>
											<c:otherwise>
												<mifos:mifoslabel name="Fees.amount" />
											</c:otherwise>
										</c:choose> </span>
									<c:choose>
										<c:when test="${model.rateBasedFee}">
											<c:out value="${sessionScope.feeactionform.rate}" />
											<mifos:mifoslabel name="Fees.ofa" />
											<c:out value="${model.feeFormula.name}" />
										</c:when>
										<c:otherwise>
											<c:out value="${sessionScope.feeactionform.amount}" />
										</c:otherwise>
									</c:choose>
									<br>
									<span class="fontnormalbold"> <mifos:mifoslabel name="Fees.status" /> </span>
									<c:forEach var="entity" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'StatusList')}">
										<c:if test="${entity.id == sessionScope.feeactionform.feeStatus}">
											<c:out value="${entity.name}" />
										</c:if>
									</c:forEach>
									<br>
									<br>

									<span class="fontnormal"> </span>

									<html-el:button property="editBtn" styleClass="insidebuttn" onclick="javascript:fnOnEditPreviousFeeInformation(feeactionform)">
										<mifos:mifoslabel name="Fees.edit" />
									</html-el:button>
								</td>
							</tr>
						</table>
						<table width="95%" border="0" cellpadding="0" cellspacing="0">
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
									<html-el:button property="cancelBtn" styleClass="cancelbuttn" onclick="javascript:fnOnEditCancel(${model.id})">
										<mifos:mifoslabel name="Fees.cancel" />
									</html-el:button>
								</td>
							</tr>
							<html-el:hidden property="input" value="previewEditFees" />
							<html-el:hidden property="feeIdTemp" value="${model.id}" />
							<html-el:hidden property="feeId" value="${model.id}" />
							<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
						</table>
						<br>
					</td>
				</tr>
			</table>
		</html-el:form>
	</tiles:put>
</tiles:insert>
