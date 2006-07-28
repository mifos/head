<!--
/**

* FeeDetails.jsp    version: 1.0



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
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<script>
function meetingpopup(){
	window.open("schedulemeetingpopup.htm",null,"height=400,width=800,status=yes,scrollbars=yes,toolbar=no,menubar=no,location=no");
}

function fnOnAdmin(form){
	form.method.value="load";
	form.action="AdminAction.do";
	form.submit();
}

function fnOnView(form){
	form.method.value="viewAll";
	form.action="feeaction.do";
	form.submit();
}

function fnOnEditFeeInformation(form){
	form.method.value="manage";
	form.action="feeaction.do";
	form.submit();
}
</script>


		<html-el:form action="/feeaction.do">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>

					<td class="bluetablehead05">
						<span class="fontnormal8pt"> <html-el:link href="javascript:fnOnAdmin(feeactionform)">
								<mifos:mifoslabel name="Fees.admin" bundle="FeesUIResources">
								</mifos:mifoslabel>
							</html-el:link> / <html-el:link href="javascript:fnOnView(feeactionform)">
								<mifos:mifoslabel name="Fees.viewfees" bundle="FeesUIResources">
								</mifos:mifoslabel>
							</html-el:link> / </span> <span class="fontnormal8ptbold"> <c:out value="${requestScope.fees.feeName}"></c:out> </span>
					</td>
				</tr>
			</table>

			<table width="95%" border="0" cellpadding="0" cellspacing="0">

				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">

						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="67%" height="23" class="headingorange">
									<c:out value="${sessionScope.BusinessKey.feeName}"></c:out>
								</td>
								<td width="33%" align="right">
									<html-el:link href="javascript:fnOnEditFeeInformation(feeactionform)">
										<mifos:mifoslabel name="Fees.editfeeinformation" bundle="FeesUIResources">
										</mifos:mifoslabel>
									</html-el:link>
								</td>
							</tr>
						</table>

						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td height="23" class="fontnormal">
									<span class="fontnormal"> </span>

									<br>
									<font class="fontnormalRedBold"> <html-el:errors bundle="FeesUIResources" /> </font> <span class="fontnormal"> <c:choose>
											<c:when test="${sessionScope.BusinessKey.active == true}">
												<img src="pages/framework/images/status_activegreen.gif" width="8" height="9">
											</c:when>
											<c:otherwise>
												<img src="pages/framework/images/status_closedblack.gif" width="8" height="9">
											</c:otherwise>
										</c:choose>
										<c:out value="${sessionScope.BusinessKey.feeStatus.name}"/>
										 </span>
									<br>
									<br>

									<mifos:mifoslabel name="Fees.feeappliesto" bundle="FeesUIResources"></mifos:mifoslabel>
									<c:out value="${sessionScope.BusinessKey.categoryType.name}"/>
									<br>
									<c:choose>
										<c:when test="${sessionScope.BusinessKey.categoryType.id != FeeCategory.LOAN.value}">
											<mifos:mifoslabel name="Fees.defaultFees" bundle="FeesUIResources"></mifos:mifoslabel>
											<c:choose>
												<c:when test="${sessionScope.BusinessKey.customerDefaultFee}">
													<mifos:mifoslabel name="Fees.DefaultFeeYes" />
												</c:when>
												<c:otherwise>
													<mifos:mifoslabel name="Fees.DefaultFeeNo" />
												</c:otherwise>
											</c:choose>
											<br>
										</c:when>
									</c:choose>
									<mifos:mifoslabel name="Fees.frequency" bundle="FeesUIResources"></mifos:mifoslabel>
									<c:out value="${sessionScope.BusinessKey.feeFrequency.feeFrequencyType.name}"/>
									<br>
									<mifos:mifoslabel name="Fees.timeToCharge" bundle="FeesUIResources"></mifos:mifoslabel>
									<c:choose>
										<c:when test="${sessionScope.BusinessKey.oneTime==true}">
											<c:out value="${sessionScope.BusinessKey.feeFrequency.feePayment.name}"/>
										</c:when>
										<c:otherwise>
											<mifos:mifoslabel name="Fees.labelRecurEvery" />
											<c:out value="${sessionScope.BusinessKey.feeFrequency.feeMeetingFrequency.meetingDetails.recurAfter}"></c:out>
											<c:if test="${sessionScope.BusinessKey.feeFrequency.feeMeetingFrequency.weekly==true}">
												<mifos:mifoslabel name="Fees.labelWeeks" />
											</c:if>
											<c:if test="${sessionScope.BusinessKey.feeFrequency.feeMeetingFrequency.monthly==true}">
												<mifos:mifoslabel name="Fees.labelMonths" />
											</c:if>
											
										</c:otherwise>
									</c:choose>
									<br>
									<br>

									<span class="fontnormalbold"> <mifos:mifoslabel name="Fees.feecalculation" bundle="FeesUIResources">
										</mifos:mifoslabel> </span>
									<br>
									<c:choose>
										<c:when test="${sessionScope.BusinessKey.feeType.value==RateAmountFlag.RATE.value}">
											<mifos:mifoslabel name="Fees.amountcalculatedas" bundle="FeesUIResources"></mifos:mifoslabel>
											<c:out value ="${sessionScope.BusinessKey.rate}"/>
											<mifos:mifoslabel name="Fees.ofa" bundle="FeesUIResources"></mifos:mifoslabel>
											<c:out value="${sessionScope.BusinessKey.feeFormula.name}"/>
										</c:when>
										<c:otherwise>
											<mifos:mifoslabel name="Fees.amount" bundle="FeesUIResources"></mifos:mifoslabel>
											<c:out value ="${sessionScope.BusinessKey.feeAmount}"/>
										</c:otherwise>
									</c:choose>
									<br>
									<br>
									<span class="fontnormalbold"> <mifos:mifoslabel name="Fees.accounting" bundle="FeesUIResources">
										</mifos:mifoslabel> </span>
									<br>
									<mifos:mifoslabel name="Fees.GLCode" bundle="FeesUIResources"></mifos:mifoslabel>
									
									<c:out value ="${sessionScope.BusinessKey.glCode.glcode}"/>
								</td>
								<td height="23" align="right" valign="top" class="fontnormalbold">
									<span class="fontnormal"> <br> <br> </span>
								</td>
							</tr>
							<html-el:hidden property="method" value="cancel" />
						</table>
						<br>
					</td>
				</tr>
			</table>
		</html-el:form>
	</tiles:put>
</tiles:insert>
