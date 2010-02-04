<%--
Copyright (c) 2005-2009 Grameen Foundation USA
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
	<span id="page.id" title="feesedit" />
	
		<script src="pages/application/fees/js/Fees.js"></script>
		
		<html-el:form action="/feeaction.do?method=editPreview">
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"> <html-el:link href="feeaction.do?method=cancelCreate&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="Fees.admin" />

							</html-el:link> / <html-el:link href="feeaction.do?method=viewAll&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="Fees.viewfees" />

							</html-el:link> / <html-el:link href="feeaction.do?method=cancelEdit&feeId=${BusinessKey.feeId}&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
								<c:out value="${BusinessKey.feeName}"></c:out>
							</html-el:link> </span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<font class="fontnormalRedBold">
								<td class="headingorange">
									<span class="heading"> <c:out value="${BusinessKey.feeName}"></c:out> - </span>
									<mifos:mifoslabel name="Fees.editfeeinformation" />
								</td>
							</tr>
							<tr>
								<td class="fontnormal">
									<mifos:mifoslabel name="Fees.editFeesMessage" />
									<br>
									<mifos:mifoslabel name="Fees.CreateFeesFieldInstruction" mandatory="yes" />
								</td>
							</tr>
						</table>
						<br>
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<font class="fontnormalRedBold"><html-el:errors bundle="FeesUIResources" /> </font>
							<tr>
								<td colspan="2" class="fontnormalbold">
									<mifos:mifoslabel name="Fees.feedetails" />
									<br>
									<br>
								</td>
							</tr>
							<tr class="fontnormal">
								<td width="27%" align="right">
									<c:choose>
										<c:when test="${BusinessKey.feeType.value==RateAmountFlag.RATE.value}">
											<mifos:mifoslabel name="Fees.calculatefeeas" mandatory="yes" />
										</c:when>
										<c:otherwise>
											<mifos:mifoslabel name="Fees.enteramount" mandatory="yes" />

										</c:otherwise>
									</c:choose>
								</td>
								<td width="73%" valign="top">
									<c:choose>
										<c:when test="${BusinessKey.feeType.value==RateAmountFlag.RATE.value}">
											<html-el:text property="rate" size="3"/>
											<mifos:mifoslabel name="Fees.percentof" />
											<c:out value="${BusinessKey.feeFormula.name}" />
										</c:when>
										<c:otherwise>
											<html-el:text property="amount" />
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="Fees.status" mandatory="yes" />
								</td>
								<td valign="top">
									<mifos:select property="feeStatus">
										<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'StatusList')}" var="status">
											<html-el:option value="${status.id}">${status.name}</html-el:option>
										</c:forEach>
									</mifos:select>
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
									<html-el:submit property="previewBtn" styleClass="buttn">
										<mifos:mifoslabel name="Fees.preview" />
									</html-el:submit>
									&nbsp;
									<html-el:button property="cancelBtn" styleClass="cancelbuttn" onclick="javascript:fnOnEditCancel(${BusinessKey.feeId})">
										<mifos:mifoslabel name="Fees.cancel" />
									</html-el:button>
								</td>
							</tr>
							<html-el:hidden property="input" value="edit" />
							<html-el:hidden property="feeIdTemp" value="${BusinessKey.feeId}" />
							<html-el:hidden property="feeId" value="${BusinessKey.feeId}" />
							<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
						</table>

						<br>
					</td>
				</tr>
			</table>
		</html-el:form>
	</tiles:put>
</tiles:insert>
