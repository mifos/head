<!-- 

/**

 * applyadjustment.jsp    version: 1.0

 

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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<SCRIPT>
	function ViewDetails(){
		closedaccsearchactionform.submit();
	}
</SCRIPT>
		<SCRIPT SRC="pages/application/customer/js/applyadjustment.js"></SCRIPT>
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<html-el:form method="post" action="custApplyAdjustment.do"
			onsubmit="return fn_submit();">
			<html-el:hidden property="currentFlowKey"
				value="${requestScope.currentFlowKey}" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <customtags:headerLink />
					<a href="customerAccountAction.do?method=load"> <!--  html-el:link href="javascript:ViewDetails()" -->/
					<c:if test="${param.input == 'ViewCenterCharges'}">
						<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
					</c:if> <c:if test="${param.input == 'ViewGroupCharges'}">
						<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />
					</c:if> <c:if test="${param.input == 'ViewClientCharges'}">
						<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />
					</c:if> <mifos:mifoslabel name="Center.Charges"
						bundle="CenterUIResources" /> <!--  /html-el:link --> </a> </span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" height="24" align="left" valign="top"
						class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="70%" class="headingorange"><span class="heading"> <c:out
								value="${param.prdOfferingName}" /> &nbsp;-&nbsp; </span> <c:choose>
								<c:when test="${requestScope.method=='loadAdjustment'}">
									<mifos:mifoslabel name="Customer.applyadjustment" />
								</c:when>
								<c:otherwise>
									<mifos:mifoslabel name="Customer.reviewadjustment" />
								</c:otherwise>
							</c:choose></td>
						</tr>
					</table>
					<br>
					<logic:messagesPresent>
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td><font class="fontnormalRedBold"> <html-el:errors
									bundle="CustomerUIResources" /> </font></td>
							</tr>
						</table>
						<br>
					</logic:messagesPresent>
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td colspan="2" class="fontnormal"><c:choose>
								<c:when test="${requestScope.method=='loadAdjustment'}">
									<mifos:mifoslabel name="Customer.adjustment_detail" />
									<br>
									<br>
								</c:when>
								<c:otherwise>
									<mifos:mifoslabel name="Customer.reviewadjustment_detail" />
									<br>
									<br></td>
						</tr>
						</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${requestScope.method=='loadAdjustment'}">
								<mifos:mifoslabel name="Customer.last_pmnt" />: <c:out
									value="${BusinessKey.customerAccount.lastPmntAmntToBeAdjusted}" />
								<br>
								<br>
							</c:when>
							<c:otherwise>
								<tr>
									<td width="25%" align="right" valign="top"
										class="fontnormalbold"><mifos:mifoslabel
										name="Customer.amnt_tobe_adjusted" />: <br>
									</td>
									<td width="75%" class="fontnormal"><c:out
										value="${BusinessKey.customerAccount.lastPmntAmntToBeAdjusted}" /></td>
								</tr>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when
								test="${requestScope.method=='loadAdjustment' && sessionScope.custApplyAdjustmentActionForm.adjustcheckbox=='true'}">
								<input type="checkbox" name="adjustcheckbox" value="true"
									checked="true">
								<mifos:mifoslabel name="Customer.chk_revert_last_pmnt"
									mandatory="yes" />
								<br>
								<br>
								</td>
								</tr>
							</c:when>
							<c:when
								test="${requestScope.method=='loadAdjustment'&& sessionScope.custApplyAdjustmentActionForm.adjustcheckbox=='false'}">
								<input type="checkbox" name="adjustcheckbox" value="true">
								<mifos:mifoslabel name="Customer.chk_revert_last_pmnt"
									mandatory="yes" />
								<br>
								<br>
								</td>
								</tr>
							</c:when>
						</c:choose>
						<c:choose>
							<c:when test="${requestScope.method=='loadAdjustment'}">
								<tr>
									<td width="5%" valign="top" align="right" class="fontnormal"><mifos:mifoslabel
										name="Customer.Notes" mandatory="yes" />: <br>
									</td>
									<td width="95%" class="fontnormal"><html-el:textarea
										property="adjustmentNote" cols="37"
										style="width:320px; height:110px;">
									</html-el:textarea></td>
							</c:when>
							<c:otherwise>
								<td valign="top" align="right" class="fontnormalbold"><mifos:mifoslabel
									name="Customer.Notes" />: <br>
								</td>
								<td class="fontnormal"><c:out
									value="${sessionScope.custApplyAdjustmentActionForm.adjustmentNote}" /></td>
							</c:otherwise>
						</c:choose>
						</tr>
					</table>
					<table width="750" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellspacing="0" cellpadding="1">
						<tr>
							<td align="center"><c:choose>
								<c:when test="${requestScope.method=='loadAdjustment'}">
									<c:choose>
										<c:when test="${requestScope.isDisabled}">
											<html-el:submit styleClass="buttn"
												property="submit_btn" disabled="true">
												<mifos:mifoslabel name="Customer.btn_reviewAdjustment">
												</mifos:mifoslabel>
											</html-el:submit>
										</c:when>
										<c:otherwise>
											<html-el:submit styleClass="buttn"
												property="submit_btn">
												<mifos:mifoslabel name="Customer.btn_reviewAdjustment">
												</mifos:mifoslabel>
											</html-el:submit>
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise>
									<html-el:submit styleClass="buttn"
										property="submit_btn">
										<mifos:mifoslabel name="Customer.submit">
										</mifos:mifoslabel>
									</html-el:submit>
								</c:otherwise>
							</c:choose> &nbsp; <html-el:button styleClass="cancelbuttn"
								onclick="javascript:fun_cancel(this.form)"
								property="cancel">
								<mifos:mifoslabel name="Customer.cancel">
								</mifos:mifoslabel>
							</html-el:button></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			<br>
			<html-el:hidden property="method" value="${requestScope.method}" />
			<html-el:hidden property="globalCustNum"
				value="${param.globalCustNum}" />
			<html-el:hidden property="globalAccountNum"
				value="${param.globalAccountNum}" />
			<html-el:hidden property="prdOfferingName"
				value="${param.prdOfferingName}" />
			<html-el:hidden property="input" value="${param.input}" />
			<html-el:hidden property="accountId" value="${param.accountId}" />
			<html-el:hidden property="accountType" value="${param.accountType}" />
		</html-el:form>

	</tiles:put>
</tiles:insert>
