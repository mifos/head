<!--
/**

* manageCheckList.jsp    version: 1.0



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
<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/tags/date" prefix="date"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<script language="JavaScript"
			src="pages/application/checklist/js/validator.js"
			type="text/javascript">
</script>


		<html-el:form action="/checkListAction.do" onsubmit="return validateFields(this)">

			<html-el:hidden property="displayedStatus" value="" />
			<html-el:hidden property="incomingMethod" value="" />

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
					<span class="fontnormal8pt">
						<html-el:link action="AdminAction.do?method=load">
							<mifos:mifoslabel name="checklist.admin" />
						</html-el:link> /
						<html-el:link action="checkListAction.do?method=loadall">
							<mifos:mifoslabel name="checklist.view_checklists" />
						</html-el:link> / 
					</span>
					<span class="fontnormal8pt">
						<html-el:link href="javascript:getChecklist('${param.previousChecklistId}','${param.previousTypeId}','${param.previousStatusOfCheckList}',${param.previousCategoryId})">	
									${CheckListParent.checkList.checklistName}
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
								<span class="heading">${CheckListParent.checkList.checklistName} -</span>
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
								<c:choose>
									<c:when test='${param.method == "loadParent"}'>
										<html-el:text property="checklistName" />
									</c:when>
									<c:otherwise>
										<c:if test='${param.method == "manage"}'>
											<html-el:text property="checklistName" value="${CheckListParent.checkList.checklistName}" />
										</c:if>
										<c:if test='${param.method == "previous"}'>
											<html-el:text property="checklistName" />
										</c:if>
										<c:if test='${param.method == "validate"}'>
											<html-el:text property="checklistName" />
										</c:if>
										<c:if test='${param.method == "preview"}'>
											<html-el:text property="checklistName" />
										</c:if>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right" valign="top">
								<mifos:mifoslabel name="checklist.type" mandatory="yes" />
							</td>
							<td valign="top">
								<mifos:select property="type" style="width:136px;" onchange="populateParent(this.form,this);">
								<c:choose>
									<c:when test='${param.method == "manage"}'>
										<c:forEach var="item" items="${catagory}" varStatus="loop">
											<c:if test='${CheckListParent.class.name == "org.mifos.application.accounts.util.valueobjects.AccountCheckList"}'>
												<c:choose>
													<c:when test='${item.checkListStatus == 1}'>
														<c:choose>
															<c:when test='${item.checkListId == CheckListParent.accountTypeId}'>
																<option value="${loop.index}" selected="selected">${item.checkListName}</option>
															</c:when>
															<c:otherwise>
																<option value="${loop.index}">${item.checkListName}</option>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<option value="${loop.index}">${item.checkListName}</option>
													</c:otherwise>
												</c:choose>
											</c:if>
											<c:if test='${CheckListParent.class.name == "org.mifos.application.checklist.util.valueobjects.CustomerCheckList"}'>
												<c:choose>
													<c:when test='${item.checkListStatus == 0}'>
														<c:choose>
															<c:when test='${item.checkListId == CheckListParent.levelId}'>
																<option value="${loop.index}" selected="selected">${item.checkListName}</option>
															</c:when>
															<c:otherwise>
																<option value="${loop.index}">${item.checkListName}</option>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<option value="${loop.index}">${item.checkListName}</option>
													</c:otherwise>
												</c:choose>
											</c:if>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<c:forEach var="item" items="${catagory}" varStatus="loop">
											<c:if test="{${loop.index}==0}">
												<html-el:hidden property="typeName"	value="${item.checkListName}" />
											</c:if>
											<html-el:option value="${loop.index}">${item.checkListName}</html-el:option>
										</c:forEach>
										</c:otherwise>
								</c:choose>
							</mifos:select> 
							<html-el:hidden property="typeOfLevel" value="" />
							<html-el:hidden property="indexOfLevel" value="" />
							<c:forEach var="item" items="${catagory}" varStatus="loop">
								<html-el:hidden property="typeOfLevel"	value="${item.checkListStatus}" />
								<html-el:hidden property="indexOfLevel"	value="${item.checkListId}" />
							</c:forEach>
							</td>
						</tr>

						<tr class="fontnormal">
							<td align="right" valign="top">
								<mifos:mifoslabel name="checklist.displayed_status" mandatory="yes" />
							</td>
							<td valign="top">
								<html-el:select property="status" style="width:136px;">  
								<c:choose>
								<c:when test="${not empty StateMaster}">
									<c:choose>
										<c:when test='${param.method == "manage"}'>
											<c:forEach var="state" items="${StateMaster}" varStatus="loop">
											<c:if test='${loop.index=="0"}'>
													<option value="">--Select--</option>
											</c:if>
												<c:if test='${CheckListParent.class.name == "org.mifos.application.accounts.util.valueobjects.AccountCheckList"}'>
													<c:choose>
														<c:when test='${state.statusId == CheckListParent.accountStatusId}'>									
														
															<option value="${state.statusId}" selected="selected">${state.statusName}
															</option>
														</c:when>
														<c:otherwise>
															<option value="${state.statusId}">${state.statusName}</option>
														</c:otherwise>
													</c:choose>
												</c:if>
												<c:if test='${CheckListParent.class.name == "org.mifos.application.checklist.util.valueobjects.CustomerCheckList"}'>
													<c:choose>
														<c:when	test='${state.statusId == CheckListParent.customerState.statusId}'>
															<option value="${state.statusId}" selected="selected">${state.statusName}
															</option>
														</c:when>
														<c:otherwise>
															<c:choose>
																<c:when test='${Type=="0" && (state.statusId=="13" || state.statusId=="1" || state.statusId=="7")}'>
																</c:when>
																<c:otherwise>												
																	<option value="${state.statusId}">${state.statusName}</option>
																</c:otherwise>
															</c:choose>
														</c:otherwise>
											   		  </c:choose>
													</c:if>
												</c:forEach>
												<c:forEach var="state" items="${StateMaster}">
													<c:if test='${CheckListParent.class.name == "org.mifos.application.accounts.util.valueobjects.AccountCheckList"}'>
														<c:choose>
															<c:when	test='${state.statusId == CheckListParent.accountStatusId}'>
																<html-el:hidden property="previousStatusId"	value="${state.statusId}" />
															</c:when>
														</c:choose>
													</c:if>
													<c:if test='${CheckListParent.class.name == "org.mifos.application.checklist.util.valueobjects.CustomerCheckList"}'>
														<c:choose>
															<c:when	test='${state.statusId == CheckListParent.customerState.statusId}'>
															<html-el:hidden property="previousStatusId"	value="${state.statusId}" />
															</c:when>
														</c:choose>
													</c:if>
												</c:forEach>
										</c:when>
										<c:otherwise>										
											<c:choose>
												<c:when test="${not empty StateMaster}">
													<c:forEach var="item" items="${StateMaster}" varStatus="loop">												
														<c:if test='${loop.index=="0"}'>
															<html-el:option value="">--Select--</html-el:option>
														</c:if>												
														<c:choose>
															<c:when test='${Type=="0" && (item.statusId=="13" || item.statusId=="1" || item.statusId=="7")}'>
															</c:when>
															<c:otherwise>
																<html-el:option	value="${item.statusId}" >${item.statusName}</html-el:option>
															</c:otherwise>
														</c:choose>
													</c:forEach>
												</c:when>
												<c:otherwise>
													<html-el:option value="">--Select--</html-el:option>
												</c:otherwise>
											</c:choose>											
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise>
								<option value="">--Select--</option>
								</c:otherwise>
								</c:choose>
							</html-el:select></td>
						</tr>
						
						 <tr class="fontnormal">
                  <td align="right" valign="top">
					<mifos:mifoslabel name="checklist.status_checklist" />
				  </td>
                  <td valign="top">
                 	 <html-el:select property="checklistStatus"  style="width:136px;">
	                 	 <c:choose>
							<c:when test='${CheckListParent.checkList.checklistStatus=="1"}'>								
								<option value="1" selected>Active</option>
								<option value="0">Inactive</option>
							</c:when>
							<c:otherwise>
								<option value="0" selected>Inactive</option>
								<option value="1">Active</option>
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
									<td><img src="../images/trans.gif" width="1" height="1"></td>
								</tr>
							</table>
							<table width="86%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td width="34%" valign="top">
										<html-el:textarea property="text" value="" cols="50" rows="5"></html-el:textarea>
									</td>
									<td width="66%" valign="top" class="paddingleft05notop">
									<html-el:button	property="button" styleClass="insidebuttn" style="width:65px" onclick="createCheckList();isButtonRequired()">
										<mifos:mifoslabel name="checklist.button_add" />
									</html-el:button>
									</td>
								</tr>
							</table>
							<br>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr valign="top" class="fontnormal">
									<td>&nbsp;</td>
									<td>&nbsp;</td>
								</tr>
								<tr class="fontnormal">
									
										<c:choose>
											<c:when test='${param.method=="previous"}'>
											<br>
											<c:forEach var="item" items="${requestScope.Context.valueObject.checklistDetailSet}" varStatus="loop">
												<tr id="myvalue${loop.index}div">
													<input name='mycheckBOx' type="checkbox" value="${item.detailText}" />${item.detailText}														
													<input name='myvalue(${loop.index})' type="hidden" value="${item.detailText}" />
												</tr>
											</c:forEach>
											<div id="myDiv">
											</div>
											</c:when>
											<c:otherwise>
											<c:if test='${param.method=="manage"}'>
												<br>
												<c:forEach var="item" items="${CheckListParent.checkList.checklistDetailSet}" varStatus="loop">
													<tr id="myvalue${loop.index}div">
														<input name='mycheckBOx' type="checkbox" value="${item.detailText}" />${item.detailText}															
														<input name='myvalue(${loop.index})' type="hidden"	value="${item.detailText}" />
													</tr>
												</c:forEach>
												<div id="myDiv">
												</div>
											</c:if>
											<c:if test='${param.method=="loadParent"}'>
												<br>
												<c:forEach var="item" items="${requestScope.Context.valueObject.checklistDetailSet}" varStatus="loop">
													<tr id="myvalue${loop.index}div">
														<input name='mycheckBOx' type="checkbox" value="${item.detailText}" />${item.detailText}
														<input name='myvalue(${loop.index})' type="hidden"	value="${item.detailText}" />
													</tr>
												</c:forEach>
												<div id="myDiv">
												</div>
											</c:if>
											<c:if test='${param.method=="preview"}'>
												<br>
												<c:forEach var="item" items="${requestScope.Context.valueObject.checklistDetailSet}" varStatus="loop">
													<tr id="myvalue${loop.index}div">
														<input name='mycheckBOx' type="checkbox" value="${item.detailText}" />${item.detailText}
														<input name='myvalue(${loop.index})' type="hidden"	value="${item.detailText}" />
													</tr>
												</c:forEach>
												<div id="myDiv">
												</div>
											</c:if>
											<c:if test='${param.method=="validate"}'>
												<br>
												<c:forEach var="item" items="${requestScope.Context.valueObject.checklistDetailSet}" varStatus="loop">
													<tr id="myvalue${loop.index}div">
														<input name='mycheckBOx' type="checkbox" value="${item.detailText}" />${item.detailText}
														<input name='myvalue(${loop.index})' type="hidden"	value="${item.detailText}" />
													</tr>
												</c:forEach>
												<div id="myDiv">
												</div>
											</c:if>
										</c:otherwise>
									</c:choose>
									<br>
								</tr>
								<tr valign="top" class="fontnormal"> 
									<td colspan="2">									
									<html-el:hidden property="numberOfPreviousItems" value="0"/>
									<c:if test='${param.method=="loadParent"}'>										
										<c:forEach	var="item" items="${requestScope.Context.valueObject.checklistDetailSet}" varStatus="loop" >																												
										 <input name="numberOfPreviousItems" type ="hidden" value='${loop.index}'/>
										</c:forEach>										
									</c:if>									
									<c:if test='${param.method=="manage"}'>										
										<c:forEach var="item" items="${CheckListParent.checkList.checklistDetailSet}" varStatus="loop">
										<input name="numberOfPreviousItems" type ="hidden" value="${loop.index}"/>
										</c:forEach>									
									</c:if>									
									<c:if test='${param.method=="validate"}'>	
									<c:forEach	var="item" items="${requestScope.Context.valueObject.checklistDetailSet}" varStatus="loop" >																												
										<input name="numberOfPreviousItems" type ="hidden" value='${loop.index}'/>
										</c:forEach>									
									</c:if>
									<c:if test='${param.method=="previous"}'>
									<c:forEach	var="item" items="${requestScope.Context.valueObject.checklistDetailSet}" varStatus="loop" >																												
										<input name="numberOfPreviousItems" type ="hidden" value='${loop.index}'/>
										</c:forEach>									
									</c:if>
									<c:if test='${param.method=="preview"}'>
									<c:forEach	var="item" items="${requestScope.Context.valueObject.checklistDetailSet}" varStatus="loop" >																												
										<input name="numberOfPreviousItems" type ="hidden" value='${loop.index}'/>
										</c:forEach>									
									</c:if>
									<div id="removeButton" style="display:none">								
										<html-el:button property="removeSelected" styleClass="insidebuttn" value="Remove Selected" style="width:120px" onclick="RemoveSelected();isButtonRequired() ">
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
								<html-el:submit style="width:70px"	styleClass="buttn" onclick="return validateFields(this)">								<mifos:mifoslabel name="checklist.button_preview" />
								</html-el:submit> &nbsp;							
								<html-el:cancel style="width:70px"	styleClass="cancelbuttn" onclick="javascript:getChecklist('${param.previousChecklistId}','${param.previousTypeId}','${param.previousStatusOfCheckList}',${param.previousCategoryId})">
										<mifos:mifoslabel name="checklist.button_cancel" />
								</html-el:cancel>									
							</td>
						</tr>
						<html-el:hidden property="method" value="" />
						<html-el:hidden property="categoryId" value="" />
						<html-el:hidden property="input" value="manage" />
						<html-el:hidden property="typeId" value="create" />
						<html-el:hidden property="typeName" value="" />
						<html-el:hidden property="checklistId" />
						<html-el:hidden property="checklistName" />
						<html-el:hidden property="statusOfCheckList" />
						<html-el:hidden property="previousChecklistId" value="${param.previousChecklistId}" />			
						<html-el:hidden property="previousCategoryId" value="${param.previousCategoryId}" />
						<html-el:hidden property="previousTypeId" value="${param.previousTypeId}" />
						<html-el:hidden property="previousStatusOfCheckList" value="${param.previousStatusOfCheckList}"/>	
					</table>
					<br>
					</td>
				</tr>
			</table>
			<br>
		</html-el:form>
	</tiles:put>
</tiles:insert>
