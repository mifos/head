<!-- 

/**

 * bulkentry_preview.jsp    version: 1.0

 

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

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@taglib uri="/mifos/bulkentrytags" prefix="bulkentry"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
		<script language="javascript">
		<!--
			function fnCancel(form) {
				form.method.value="cancel";
				form.action="bulkentryaction.do";
				form.submit();
			}
			
			function fnPrevious(form) {
				form.method.value="previous";
				form.action="bulkentryaction.do";
				form.submit();
			}
			
			function fun_submit() {
				func_disableSubmitBtn("submitBttn");
				return true;
			}
		//-->
		</script>
		<script SRC="pages/framework/js/CommonUtilities.js"></script>
		<html-el:form action="/bulkentryaction" onsubmit="return fun_submit();">
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BulkEntry')}" var="BulkEntry" />
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="350" align="left" valign="top" bgcolor="#FFFFFF">
						<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center" class="heading">
									&nbsp;
								</td>
							</tr>
						</table>
						<table width="95%" border="0" align="center" cellpadding="0" cellspacing="0">
							<tr>
								<td class="bluetablehead">
									<table width="900" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="33%">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/tick.gif" width="17" height="17">
														</td>
														<td class="timelineboldgray">
															<mifos:mifoslabel name="bulkEntry.select" />
															<c:choose>
																<c:when test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'isCenterHeirarchyExists')==Constants.YES}">
																	<mifos:mifoslabel name="${LABEL_CENTER}" />
																</c:when>
																<c:otherwise>
																	<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />
																</c:otherwise>
															</c:choose>
														</td>
													</tr>
												</table>
											</td>
											<td width="34%" align="center">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/tick.gif" width="17" height="17">
														</td>
														<td class="timelineboldgray">
															<mifos:mifoslabel name="bulkEntry.enterdata" />
														</td>
													</tr>
												</table>
											</td>
											<td width="33%" align="right">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17">
														</td>
														<td class="timelineboldorange">
															<mifos:mifoslabel name="bulkEntry.revnsub" />
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
						<table width="95%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
							<tr>
								<td width="1613" height="476" align="left" valign="top" class="paddingleftCreates">
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td class="headingorange">
												<span class="heading"> <mifos:mifoslabel name="${LABEL_BULKENTRY}" /> - </span>
												<mifos:mifoslabel name="bulkEntry.revnsub" />
												<br>
												<br>
												<table width="590" border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<span class="fontnormalbold"> <c:choose>
																	<c:when test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'isCenterHeirarchyExists')==Constants.YES}">
																		<mifos:mifoslabel name="${LABEL_CENTER}" />
																	</c:when>
																	<c:otherwise>
																		<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />
																	</c:otherwise>
																</c:choose>: <c:out value="${BulkEntry.bulkEntryParent.customerDetail.displayName}" /><br> <mifos:mifoslabel name="bulkEntry.dateoftrxn" />: <c:out
																	value='${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,BulkEntry.transactionDate)}' /> </span>
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr valign="top">
											<td width="77%" height="52" class="fontnormal">
												<table width="590" border="0" cellspacing="0" cellpadding="0">
													<tr valign="top">
														<td width="207" class="fontnormal">
															<mifos:mifoslabel name="${LABEL_BRANCHOFFICE}" />
															:
															<c:out value="${BulkEntry.office.officeName}" />
															<br>
															<mifos:mifoslabel name="bulkEntry.loanofficer" />
															:
															<c:out value="${BulkEntry.loanOfficer.displayName}" />
														</td>
														<td width="383" class="fontnormal">
															<mifos:mifoslabel name="bulkEntry.pmnttype" />
															:
															<c:out value="${BulkEntry.paymentType.paymentTypeValue}" />
															<table>
																<tr id="BulkEntry.ReceiptId">
																	<td class="fontnormal">
																		<mifos:mifoslabel name="bulkEntry.rcptid" keyhm="BulkEntry.ReceiptId" isColonRequired="yes" isManadatoryIndicationNotRequired="yes" />
																		<c:out value="${BulkEntry.receiptId}" />
																	</td>
																</tr>
																<tr id="BulkEntry.ReceiptDate">
																	<td class="fontnormal">
																		<mifos:mifoslabel name="bulkEntry.rcptdate" keyhm="BulkEntry.ReceiptDate" isColonRequired="yes" isManadatoryIndicationNotRequired="yes" />
																		<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,BulkEntry.receiptDate)}" />
																	</td>
																</tr>
															</table>
														</td>
													</tr>
												</table>
												<br>
											</td>
										</tr>
										<tr valign="top">
											<td class="fontnormal">
												<mifos:mifoslabel name="bulkEntry.prevdataclisub" />
												<mifos:mifoslabel name="bulkEntry.clickcanc" />
												<br>
											</td>
										</tr>
									</table>
									<br>
									<logic:messagesPresent>
										<font class="fontnormalRedBold"> <html-el:errors bundle="bulkEntryUIResources" /> </font>
										<br>
									</logic:messagesPresent>
									<bulkentry:bulkentrytag />
									<br>
									<table width="97%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td>
												<html-el:button property="edit" styleClass="insidebuttn" style="width:70px" onclick="fnPrevious(this.form);">
													<mifos:mifoslabel name="bulkEntry.editdata" />
												</html-el:button>
											</td>
										</tr>
										<tr>
											<td align="center" class="blueline">
												&nbsp;
											</td>
										</tr>
									</table>
									<html-el:hidden property="method" value="create" />
									<html-el:hidden property="input" value="preview" />
									<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
									<br>
									<table width="97%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center">
												<html-el:submit styleClass="buttn" style="width:70px" property="submitBttn">
													<mifos:mifoslabel name="bulkEntry.submit" />
												</html-el:submit>
												&nbsp;
												<html-el:button property="cancel" styleClass="cancelbuttn" style="width:70px" onclick="fnCancel(this.form);">
													<mifos:mifoslabel name="bulkEntry.cancel" />
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