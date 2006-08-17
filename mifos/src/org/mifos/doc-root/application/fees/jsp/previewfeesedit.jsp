<!--
/**

* Previewfeesedit.jsp    version: 1.0



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

		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>

		<script>
			function fnOnCancel(Id){
				document.feeactionform.method.value="get";
				document.feeactionform.feeIdTemp.value=Id;
				document.feeactionform.action="feeaction.do";
				document.feeactionform.submit();
			}
			function fnOnAdmin(form){
				form.method.value="load";
				form.action="AdminAction.do";
				form.submit();
			}
			function fnOnSubmit(){
				document.feeactionform.method.value="update";
				document.feeactionform.action="feeaction.do";
				document.feeactionform.submit();
			}
			function fnOnEditFeeInformation(form){
				form.method.value="editPrevious";
				form.action="feeaction.do";
				form.submit();
			}
			function fnOnView(form){
				form.method.value="viewAll";
				form.action="feeaction.do";
				form.submit();
			}
		</script>
		<html-el:form action="/feeaction.do" onsubmit="return func_disableSubmitBtn('submitBtn');">
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"> <html-el:link href="javascript:fnOnAdmin(feeactionform)">
								<mifos:mifoslabel name="Fees.admin">
								</mifos:mifoslabel>
							</html-el:link> / <html-el:link href="javascript:fnOnView(feeactionform)">
								<mifos:mifoslabel name="Fees.viewfees">
								</mifos:mifoslabel>
							</html-el:link> / <html-el:link href="javascript:fnOnCancel(${BusinessKey.feeId})">
								<c:out value="${BusinessKey.feeName}" />
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
									<span class="heading"> <c:out value="${BusinessKey.feeName}" /> - </span>
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
											<c:when test="${BusinessKey.feeType.value==RateAmountFlag.RATE.value}">
												<mifos:mifoslabel name="Fees.amountcalculatedas" />
											</c:when>
											<c:otherwise>
												<mifos:mifoslabel name="Fees.amount" />
											</c:otherwise>
										</c:choose> </span>
									<c:choose>
										<c:when test="${BusinessKey.feeType.value==RateAmountFlag.RATE.value}">
											<c:out value="${sessionScope.feeactionform.rate}" />
											<mifos:mifoslabel name="Fees.ofa" />
											<c:out value="${BusinessKey.feeFormula.name}" />
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

									<html-el:button property="editBtn" styleClass="insidebuttn" onclick="javascript:fnOnEditFeeInformation(feeactionform)">
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
									<html-el:submit property="submitBtn" styleClass="buttn" style="width:65px">
										<mifos:mifoslabel name="Fees.submit" />
									</html-el:submit>
									&nbsp;
									<html-el:button property="cancelBtn" styleClass="cancelbuttn" style="width:65px" onclick="javascript:fnOnCancel(${BusinessKey.feeId})">
										<mifos:mifoslabel name="Fees.cancel" />
									</html-el:button>
								</td>
							</tr>
							<html-el:hidden property="method" value="update" />
							<html-el:hidden property="input" value="previewEditFees" />
							<html-el:hidden property="feeIdTemp" value="${BusinessKey.feeId}" />
							<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
						</table>
						<br>
					</td>
				</tr>
			</table>
		</html-el:form>
	</tiles:put>
</tiles:insert>
