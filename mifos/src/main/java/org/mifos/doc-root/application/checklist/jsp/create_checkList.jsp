<!--
/**

* addNewChecklists.jsp    version: 1.0



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
*
*/
 -->

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".create">
	<tiles:put name="body" type="string">
<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<script language="JavaScript"
			src="pages/application/checklist/js/validator.js"
			type="text/javascript">
	</script>

		<html-el:form action="/chkListAction.do?method=preview"
			focus="checklistName">
			<html-el:hidden property="typeName" value="" />
			<html-el:hidden property="displayedStatus" value="" />
			<html-el:hidden property="currentFlowKey"
				value="${requestScope.currentFlowKey}" />
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="350" align="left" valign="top" bgcolor="#FFFFFF">
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td align="center" class="heading">&nbsp;</td>
						</tr>
					</table>

					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td class="bluetablehead">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/bigarrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorange"><mifos:mifoslabel
												name="checklist.checklist_info" /></td>
										</tr>
									</table>
									</td>
									<td align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorangelight"><mifos:mifoslabel
												name="checklist.reviewandsubmit" /></td>
										</tr>
									</table>
									</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0" class="bluetableborder">
						<tr>
							<td align="left" valign="top" class="paddingleftCreates">

							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td class="headingorange"><span class="heading"> <mifos:mifoslabel
										name="checklist.addnewchecklist" /> - </span> <mifos:mifoslabel
										name="checklist.enterchecklist_Info" /></td>
								</tr>
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="checklist.enter_Info_one" /> <br>
									<mifos:mifoslabel name="checklist.enter_Info_two"
										mandatory="yes" /></td>
								</tr>
							</table>
							<br>
							<table width="96%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<font class="fontnormalRedBold"> <html-el:errors
										bundle="checklistUIResources" /> </font>
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="checklist.checklistdetails" /> <br>
									<br>
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right" valign="top"><mifos:mifoslabel
										name="checklist.name" mandatory="yes" /></td>
									<td valign="top"><c:choose>
										<c:when test='${param.method=="load"}'>
											<html-el:text property="checklistName" value="" />
										</c:when>
										<c:otherwise>
											<html-el:text property="checklistName" />
										</c:otherwise>
									</c:choose></td>
								</tr>

								<tr class="fontnormal">
									<td align="right" valign="top"><mifos:mifoslabel
										name="checklist.type" mandatory="yes" /></td>
									<td valign="top"><mifos:select property="type"
										style="width:136px;"
										onchange="populateStates(this.form,this);">
										<c:forEach var="item"
											items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'checkList_masterData')}"
											varStatus="loop">
											<html-el:option value="${loop.index}">${item.masterTypeName}</html-el:option>
										</c:forEach>
									</mifos:select> <html-el:hidden property="isCustomers" value="" />
									<html-el:hidden property="masterIds" value="" /> <html-el:hidden
										property="masterNames" value="" /> <c:forEach var="item"
										items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'checkList_masterData')}"
										varStatus="loop">
										<html-el:hidden property="isCustomers"
											value="${item.isCustomer}" />
										<html-el:hidden property="masterIds"
											value="${item.masterTypeId}" />
										<html-el:hidden property="masterNames"
											value="${item.masterTypeName}" />
									</c:forEach></td>
								</tr>
								<tr class="fontnormal">
									<td align="right" valign="top"><mifos:mifoslabel
										name="checklist.displayed_status" mandatory="yes" /></td>
									<td valign="top"><mifos:select property="stateId"
										onchange="populateStateName(this.form,this);">
										<c:forEach var="item"
											items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'states')}"
											varStatus="loop">
											<html-el:option value="${item.stateId}">${item.stateName}</html-el:option>
										</c:forEach>
									</mifos:select> <c:forEach var="item"
										items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'states')}"
										varStatus="loop">

										<html-el:hidden property="stateName" value="${item.stateName}" />
										<html-el:hidden property="stateNames"
											value="${item.stateName}" />
									</c:forEach></td>
								</tr>

								<tr class="fontnormal">
									<td width="29%" align="right" valign="top"><mifos:mifoslabel
										name="checklist.items" mandatory="yes" /></td>
									<td width="71%" valign="top">
									<table width="80%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="fontnormal"><mifos:mifoslabel
												name="checklist.items_info" /></td>
										</tr>
										<tr>
											<td><img src="pages/framework/images/trans.gif" width="1"
												height="1"></td>
										</tr>
									</table>

									<table width="86%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="34%" valign="top"><html-el:textarea
												property="text" cols="50" rows="5" onkeypress="return restrictScript(event);">
											</html-el:textarea></td>

											<td width="66%" valign="top" class="paddingleft05notop"><html-el:button
												property="button" styleClass="insidebuttn"
												onclick="createCheckList();isButtonRequired()">
												<mifos:mifoslabel name="checklist.button_add" />
											</html-el:button></td>
										</tr>
									</table>
									<br>
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr class="fontnormal">
											<br>
											<c:forEach var="item"
												items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'details')}"
												varStatus="loop">
												<c:if test="${item != ''}">
													<tr id="myvalue${loop.index}div">
														<input name='mycheckBOx' type="checkbox" value="${item}" />
														${item}
														<input name='detailsList[${loop.index}]' type="hidden"
															value="${item}" />
													</tr>
												</c:if>
											</c:forEach>
											<div id="myDiv"></div>

											<br>
										</tr>
										<tr valign="top" class="fontnormal">
											<td colspan="2"><html-el:hidden
												property="numberOfPreviousItems" value="0" /> <c:forEach
												var="item"
												items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'details')}"
												varStatus="loop">
												<input name="numberOfPreviousItems" type="hidden"
													value='${loop.index}' />
											</c:forEach>
											<div id="removeButton" style="display:none"><html-el:button
												property="removeSelected" styleClass="insidebuttn"
												
												onclick="RemoveSelected();isButtonRequired() ">
												<mifos:mifoslabel name="checklist.button_removeselected" />
											</html-el:button></div>
											<script>
										setNumberOfPreviousItems();
										isButtonRequired();
									</script></td>
										</tr>
									</table>
									</td>
								</tr>
							</table>

							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center" class="blueline">&nbsp;</td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center"><html-el:submit
										property="button" styleClass="buttn">
										<mifos:mifoslabel name="checklist.button_preview" />
									</html-el:submit> &nbsp; <html-el:button 
										property="cancelBttn" styleClass="cancelbuttn"
										onclick="javascript:fnCancel(this.form)">
										<mifos:mifoslabel name="checklist.button_cancel" />
									</html-el:button></td>
								</tr>
							</table>
							<br>
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
