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
	<span id="page.id" title="ApplyAdjustment" />
		<SCRIPT SRC="pages/application/accounts/js/applyadjustment.js"></SCRIPT>
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<html-el:form method="post" action="applyAdjustment.do" onsubmit="return fn_submit();">
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />	
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"> <customtags:headerLink/> </span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" height="24" align="left" valign="top"
						class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="70%" class="headingorange"><span class="heading"> 
								
								<c:out value="${param.prdOfferingName}" />&nbsp;#&nbsp;
								<c:out value="${param.globalAccountNum}" />
								
								&nbsp;-&nbsp; </span> 
								<c:choose>
									<c:when test="${requestScope.method=='loadAdjustment'}">
										<mifos:mifoslabel name="accounts.applyadjustment" />
									</c:when>
									<c:otherwise>
										<mifos:mifoslabel name="accounts.reviewadjustment" />
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</table>
					<br>
					<logic:messagesPresent>
						<table width="93%" border="0" cellpadding="3" cellspacing="0"><tr><td>
									<font class="fontnormalRedBold">
										<span id="applyadjustment.error.message"><html-el:errors bundle="accountsUIResources" /></span> 
									</font></td></tr>			
						</table><br>
					</logic:messagesPresent>
				<%--	<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr class="fontnormal">
								<c:choose>
									<c:when test="${requestScope.method=='loadAdjustment'}">
										<mifos:mifoslabel name="accounts.adjustment_detail" />
									</c:when>
									<c:otherwise>
										<mifos:mifoslabel name="accounts.reviewadjustment_detail" />
									</c:otherwise>
								</c:choose>		
							</tr>
					</table>
				--%>	
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td colspan="2" class="fontnormal">
								<c:choose>
									<c:when test="${requestScope.method=='loadAdjustment'}">
										<mifos:mifoslabel name="accounts.adjustment_detail" />
										<br><br>
									</c:when>
									<c:otherwise>
										<mifos:mifoslabel name="accounts.reviewadjustment_detail" />
										<br><br>
							</td>
						</tr>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${requestScope.method=='loadAdjustment'}">
										<mifos:mifoslabel name="accounts.last_pmnt"/>:
										<c:out value="${BusinessKey.lastPmntAmntToBeAdjusted}"/>
										<br><br>
									</c:when>
									<c:otherwise>
										<td width="25%" align="right" valign="top" class="fontnormalbold">
											<mifos:mifoslabel name="accounts.amnt_tobe_adjusted" />:<br>
										</td>
										<td width="75%" class="fontnormal">	
											<c:out value="${BusinessKey.lastPmntAmntToBeAdjusted}"/>
										</td>
									</tr>	
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${requestScope.method=='loadAdjustment' && sessionScope.applyAdjustmentActionForm.adjustcheckbox=='true'}">
										<input id="applyadjustment.input.revertLastPayment" type="checkbox" name="adjustcheckbox" value="true" checked="true">
										<span id="applyadjustment.label.revertLastPayment">
										<mifos:mifoslabel name="accounts.chk_revert_last_pmnt" mandatory="yes"/></span>
										<br>
										<br>
									</td>
								</tr>
									</c:when>
									<c:when test="${requestScope.method=='loadAdjustment'&& sessionScope.applyAdjustmentActionForm.adjustcheckbox=='false'}">
										<input id="applyadjustment.input.revertLastPayment" type="checkbox" name="adjustcheckbox" value="true" >
										<span id="applyadjustment.label.revertLastPayment">
										<mifos:mifoslabel name="accounts.chk_revert_last_pmnt" mandatory="yes"/></span>
										<br>
										<br>
								</td>
								</tr>
									</c:when>
								</c:choose>
								<c:choose>
									<c:when test="${requestScope.method=='loadAdjustment'}">
									<tr>
										<td width="5%" valign="top" align="right" class="fontnormal">
											<span id="applyadjustment.label.note"><mifos:mifoslabel 
											name="accounts.notes" mandatory="yes"/></span>:
											<br>
			    			            </td>
						                <td width="95%" class="fontnormal">
										<html-el:textarea styleId="applyadjustment.input.note"
											property="adjustmentNote" cols="37" style="width:320px; height:110px;">
										</html-el:textarea></td>
									</c:when>
									<c:otherwise>
										<td valign="top" align="right" class="fontnormalbold">
											<mifos:mifoslabel name="accounts.notes" />:
											<br>
			    			            </td>
						                <td class="fontnormal">
										<c:out value="${sessionScope.applyAdjustmentActionForm.adjustmentNote}"/></td>
									</c:otherwise>
								</c:choose>
							</tr>
					</table>	
					
				<%--	<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr class="fontnormal">
							
								
								<c:choose>
									<c:when test="${requestScope.method=='loadAdjustment'}">
										<td width="27%" align="right" class="fontnormal">
										<br>
										<br>
										<mifos:mifoslabel name="accounts.last_pmnt"/>:
										<c:out value="${BusinessKey.lastPmntAmnt}"/>
									</c:when>
									<c:otherwise>
										<td width="27%" align="right" class="fontnormalbold">
										<mifos:mifoslabel name="accounts.amnt_tobe_adjusted" />:
										<td >
												
											<c:out value="${BusinessKey.lastPmntAmnt}"/>
										</td>	
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${requestScope.method=='loadAdjustment' && sessionScope.applyAdjustmentActionForm.adjustcheckbox=='true'}">
										<br>
										<br>
										<input id="applyadjustment.input.revertLastPayment" type="checkbox" name="adjustcheckbox" value="true" checked="true">
										<span id="applyadjustment.label.revertLastPayment">
										<mifos:mifoslabel name="accounts.chk_revert_last_pmnt" mandatory="yes"/></span>
									</c:when>
									<c:when test="${requestScope.method=='loadAdjustment'&& sessionScope.applyAdjustmentActionForm.adjustcheckbox=='false'}">
										<br>
										<br>
										<input id="applyadjustment.input.revertLastPayment" type="checkbox" name="adjustcheckbox" value="true" >
										<span id="applyadjustment.label.revertLastPayment">
										<mifos:mifoslabel name="accounts.chk_revert_last_pmnt" mandatory="yes"/></span>
									</c:when>
								</c:choose>
								<br>
								<br>
							</td>
						</tr>	
						<tr>	
							
			                	<c:choose>
									<c:when test="${requestScope.method=='loadAdjustment'}">
										<td width="5%" valign="top" align="right" class="fontnormal">
											<span id="applyadjustment.label.note"><mifos:mifoslabel name="accounts.notes" mandatory="yes"/></span>:
											<br>
			    			            </td>
						                <td width="95%" class="fontnormal">
										<html-el:textarea styleId="applyadjustment.input.note"
											property="adjustmentNote" cols="37" style="width:320px; height:110px;">
										</html-el:textarea>
									</c:when>
									<c:otherwise>
										<td width="5%" valign="top" align="right" class="fontnormalbold">
											<mifos:mifoslabel name="accounts.notes" />:
											<br>
			    			            </td>
						                <td width="95%" class="fontnormal">
										<c:out value="${sessionScope.applyAdjustmentActionForm.adjustmentNote}"/>
									</c:otherwise>
								</c:choose>
			                	
			                </td>
						</tr>
					</table>
				--%>
					<table width="750" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;
							</td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellspacing="0" cellpadding="1">
						<tr>
							<td align="center">
								<c:choose>
									<c:when test="${requestScope.method=='loadAdjustment'}">
										<c:choose>
										<c:when test="${requestScope.isDisabled}">
											<html-el:submit styleId="applyadjustment.button.submit" styleClass="buttn" property="submit_btn" disabled="true">
												<mifos:mifoslabel name="accounts.btn_reviewAdjustment">
												</mifos:mifoslabel>
											</html-el:submit>
										</c:when>
										<c:otherwise>
											<html-el:submit styleId="applyadjustment.button.submit" styleClass="buttn" property="submit_btn">
												<mifos:mifoslabel name="accounts.btn_reviewAdjustment">
												</mifos:mifoslabel>
											</html-el:submit>
										</c:otherwise>
									</c:choose>
									</c:when>
									<c:otherwise>
										<html-el:submit styleId="applyadjustment.button.submit" styleClass="buttn" property="submit_btn">
											<mifos:mifoslabel name="accounts.submit">
											</mifos:mifoslabel>
										</html-el:submit>
									</c:otherwise>
								</c:choose>

								 &nbsp; 
								<html-el:button styleId="applyadjustment.button.cancel" styleClass="cancelbuttn" onclick="javascript:fun_cancel(this.form)" property="cancel">
									<mifos:mifoslabel name="accounts.cancel">
									</mifos:mifoslabel>
								</html-el:button></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			<br>
 			<html-el:hidden property="method" value="${requestScope.method}"/>
 			<html-el:hidden property="globalAccountNum" value="${param.globalAccountNum}"/>
 			<html-el:hidden property="prdOfferingName" value="${param.prdOfferingName}"/>
</html-el:form>


	</tiles:put>
</tiles:insert>
