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

<%@taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
	<span style="display: none" id="page.id">editPreview_checkList</span>
	

		<script language="JavaScript"
			src="pages/application/checklist/js/validator.js"
			type="text/javascript">
</script>
<script language="JavaScript"
			src="pages/framework/js/CommonUtilities.js"
			type="text/javascript">
		</script>
		<html-el:form method="post" action="/chkListAction.do?method=update&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}" >
		<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		<html-el:hidden property="checkListId" value="${sessionScope.ChkListActionForm.checkListId}" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
					<span class="fontnormal8pt">
						<html-el:link action="AdminAction.do?method=load">
							<mifos:mifoslabel name="checklist.admin" />
						</html-el:link> /
						<html-el:link action="chkListAction.do?method=loadAllChecklist&randomNUm=${sessionScope.randomNUm}">
							<mifos:mifoslabel name="checklist.view_checklists" />
						</html-el:link> /
					</span>
					<span class="fontnormal8pt">
					<html-el:link href="chkListAction.do?method=get&checkListId=${sessionScope.ChkListActionForm.checkListId}&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
							${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'oldChecklistName')}
						</html-el:link>
					</span></td>
				</tr>
			</table>

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingleftCreates">
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange">
								<span class="heading">
									${sessionScope.ChkListActionForm.checklistName} -
								</span>
								<mifos:mifoslabel name="checklist.reviewchecklist_Info" />
							</td>
						</tr>
						<tr>
							<td class="fontnormal">
								<mifos:mifoslabel name="checklist.review_Info" />
							</td>
						</tr>
					</table>
					<br>
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
						<font class="fontnormalRedBold">
							<html-el:errors	bundle="checklistUIResources" />
						 </font>
						<td width="100%" colspan="2" class="fontnormalbold">
							<mifos:mifoslabel name="checklist.checklistdetails" />
							<br>
							<br>
							<span >
								<mifos:mifoslabel name="checklist.name" />
								<span class="fontnormal">
								<c:out value="${sessionScope.ChkListActionForm.checklistName}"></c:out>
								</span>
								<br>
								<mifos:mifoslabel name="checklist.type" />
								<span class="fontnormal">
								<c:out value="${sessionScope.ChkListActionForm.masterTypeName}"></c:out>
								</span>
								<br>
								<mifos:mifoslabel name="checklist.displayed_status"  />
								<span class="fontnormal">
								<c:forEach var="item" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'states')}" varStatus="loop">
										<c:if test="${sessionScope.ChkListActionForm.stateId==item.stateId}">
												<c:out value="${item.stateName}"></c:out>
										</c:if>
									</c:forEach>
								</span>
								<br>
								<mifos:mifoslabel name="checklist.status_checklist" />
								<span class="fontnormal">

								<c:choose>

									<c:when test='${sessionScope.ChkListActionForm.checklistStatus=="1"}'>
										<mifos:mifoslabel name="checklist.active" />
									</c:when>
									<c:otherwise>
										<mifos:mifoslabel name="checklist.inactive" />
									</c:otherwise>
								</c:choose>
								</span>
								<br>
								<br>
								<mifos:mifoslabel name="checklist.items" />
								<c:forEach	var="item" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'details')}">
									<br>
									<span class="fontnormal">
									<c:out value="${item}"></c:out>
									</span>
									<br>
								</c:forEach>
								<br>
							</span>
						</td>

						</tr>
						<tr>
							<td colspan="2" class="fontnormalbold">
							<html-el:button	property="button" styleClass="insidebuttn"  onclick="javascript:fnManageEdit(this.form)">
								<mifos:mifoslabel name="checklist.edit_button"></mifos:mifoslabel>
							</html-el:button>
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
						<td align="center">
							<html-el:submit property="submitbutton" styleClass="buttn">
								<mifos:mifoslabel name="checklist.button_submit" />
							</html-el:submit> &nbsp;
							<html-el:button property="button" styleClass="cancelbuttn" onclick="javascript:getChklist(this.form)">
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
