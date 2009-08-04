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

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
	<span id="page.id" title="manage_checkList" />
	
		<script language="JavaScript"
			src="pages/application/checklist/js/validator.js"
			type="text/javascript">
</script>
<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>

		<html-el:form action="/chkListAction.do?method=managePreview">
			<c:set var="checkList" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" />
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<html-el:hidden property="checkListId" value="${sessionScope.ChkListActionForm.checkListId}" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
					<span class="fontnormal8pt">
						<html-el:link action="AdminAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
							<mifos:mifoslabel name="checklist.admin" />
						</html-el:link> /
						<html-el:link action="chkListAction.do?method=loadAllChecklist&randomNUm=${sessionScope.randomNUm}">
							<mifos:mifoslabel name="checklist.view_checklists" />
						</html-el:link> /
					</span>
					<span class="fontnormal8pt">
						<html-el:link href="chkListAction.do?method=get&checkListId=${sessionScope.ChkListActionForm.checkListId}&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
							${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'oldChecklistName')}
						</html-el:link>
					</span></td>
				</tr>
			</table>

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="50%" height="23" class="headingorange">
								<span class="heading">${sessionScope.ChkListActionForm.checklistName} -</span>
								<mifos:mifoslabel name="checklist.edit_checklist" />
							</td>
						</tr>
						<tr>
							<td height="23" class="fontnormal"><span class="fontnormal"> </span>
								<mifos:mifoslabel name="checklist.edit_checklist_info" /> <br>
								<mifos:mifoslabel name="checklist.enter_Info_two" mandatory="yes" />
							</td>
						</tr>
					</table>
					<br>
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<font class="fontnormalRedBold">
								<html-el:errors	bundle="checklistUIResources" />
							</font>
							<td colspan="2" class="fontnormalbold">
								<mifos:mifoslabel name="checklist.checklistdetails" /><br>
								<br>
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right" valign="top">
								<mifos:mifoslabel name="checklist.name" mandatory="yes" />
							</td>
							<td valign="top">
								<html-el:text property="checklistName" />
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right" valign="top">
								<mifos:mifoslabel name="checklist.type" mandatory="yes" />
							</td>
							<td valign="top">
								<mifos:select property="type" style="width:136px;" onchange="populateStatesEdit(this.form,this);" disabled="true">
								<c:forEach var="item" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'checkList_masterData')}" varStatus="loop">
											<html-el:option value="${loop.index}">${item.masterTypeName}</html-el:option>
								</c:forEach>
								</mifos:select>
							<html-el:hidden property="isCustomers" value="" />
							<html-el:hidden property="masterIds" value="" />
							<html-el:hidden property="masterNames" value="" />
						<c:forEach

										var="item" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'checkList_masterData')}" varStatus="loop">
										<html-el:hidden property="isCustomers"
											value="${item.isCustomer}" />
										<html-el:hidden property="masterIds"
											value="${item.masterTypeId}" />
										<html-el:hidden property="masterNames"
											value="${item.masterTypeName}" />
									</c:forEach>
							</td>
						</tr>

						<tr class="fontnormal">
							<td align="right" valign="top">
								<mifos:mifoslabel name="checklist.displayed_status" mandatory="yes" />
							</td>
							<td valign="top">
								<mifos:select property="stateId" style="width:136px;"
									onchange="populateStateName(this.form,this);">
												<c:forEach var="item" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'states')}" varStatus="loop">
												<html-el:option	value="${item.stateId}" >${item.stateName}</html-el:option>
												</c:forEach>
									</mifos:select>
									<c:forEach var="item" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'states')}" varStatus="loop">

										<html-el:hidden property="stateName"
												value="${item.stateName}" />
										<html-el:hidden property="stateNames"
												value="${item.stateName}" />
									</c:forEach>
								</td>
						</tr>

						 <tr class="fontnormal">
                  <td align="right" valign="top">
					<mifos:mifoslabel name="checklist.status_checklist" />
				  </td>
                  <td valign="top">
                 	 <html-el:select property="checklistStatus"  style="width:136px;">
	                 	 <c:choose>
							<c:when test='${sessionScope.ChkListActionForm.checklistStatus=="1"}'>
								<option value="1" selected><mifos:mifoslabel name="checklist.active" /></option>
								<option value="0"><mifos:mifoslabel name="checklist.inactive" /></option>
							</c:when>
							<c:otherwise>
								<option value="0" selected><mifos:mifoslabel name="checklist.inactive" /></option>
								<option value="1"><mifos:mifoslabel name="checklist.active" /></option>
							</c:otherwise>
						</c:choose>
                 	 </html-el:select>
					</td>
                	</tr>
						<tr class="fontnormal">
							<td width="29%" align="right" valign="top">
								<mifos:mifoslabel name="checklist.items" mandatory="yes" />
							</td>
							<td width="71%" valign="top">
							<table width="80%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="fontnormal">
										<mifos:mifoslabel name="checklist.items_info" />
									</td>
								</tr>
								<tr>
									<td><img src="pages/framework/images/trans.gif" width="1" height="1"></td>
								</tr>
							</table>
							<table width="86%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td width="34%" valign="top">
										<html-el:textarea property="text" value="" cols="50" rows="5" onkeypress="return restrictScript(event);"></html-el:textarea>
									</td>
									<td width="66%" valign="top" class="paddingleft05notop">
									<html-el:button	property="button" styleClass="insidebuttn" onclick="createCheckList();isButtonRequired()">
										<mifos:mifoslabel name="checklist.button_add" />
									</html-el:button>
									</td>
								</tr>
							</table>
							<br>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr valign="top" class="fontnormal">
									<td>&nbsp;</td>
								</tr>

										<c:forEach var="item"
											items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'details')}"
											varStatus="loop">
												<tr id="myvalue${loop.index}div">
													<td class="fontnormal">
													<input name='mycheckBOx' type="checkbox"
														value="${item}" />${item}
													<input name='detailsList[${loop.index}]' type="hidden"
														value="${item}" />
													</td>
												</tr>

										</c:forEach>
								<tr>
									<td>
										<div id="myDiv">
										</div>
										<br>
									<html-el:hidden property="numberOfPreviousItems" value="0"/>

										<c:forEach var="item"
											items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'details')}"
											varStatus="loop">
												<input name="numberOfPreviousItems" type="hidden"
														value='${loop.index}' />
										</c:forEach>

									<div id="removeButton" style="display:none">
										<html-el:button property="removeSelected" styleClass="insidebuttn" onclick="RemoveSelected();isButtonRequired() ">
											<mifos:mifoslabel name="checklist.button_removeselected" />
										</html-el:button>
									</div>

										<script>

											setNumberOfPreviousItems();
											isButtonRequired();
										</script>
									</td>
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
					<c:if test='${param.method=="previous"}'>
						<html-el:hidden property="previousStatusId" value="" />
						<script>
							setPreviousStatusId('${param.method}');
						</script>
					</c:if>
					<table width="93%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center">
								<html-el:submit	styleClass="buttn">
								<mifos:mifoslabel name="checklist.button_preview" />
								</html-el:submit> &nbsp;

								<html-el:button property="cancelBttn" 	styleClass="cancelbuttn" onclick="javascript:getChklist(this.form)">
										<mifos:mifoslabel name="checklist.button_cancel" />
								</html-el:button>

							</td>
						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>
			<br>
		</html-el:form>
	</tiles:put>
</tiles:insert>
