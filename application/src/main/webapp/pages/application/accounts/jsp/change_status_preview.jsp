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
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>


<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<span id="page.id" title="ChangeStatusPreview" />
		<script language="javascript">
			  function goToCancelPage(form){
				form.action="editStatusAction.do?method=cancel";
				form.submit();
			  }
			  function GoToEditPage(form){
				form.action="editStatusAction.do?method=previous";
				form.submit();
			  }
		</script>
		<html-el:form action="editStatusAction.do?method=update">
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />	
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt">
							<customtags:headerLink/>
						</span>
					</td>
				</tr>
			</table> 
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="83%" class="headingorange"><span class="heading"> <c:out
								value="${sessionScope.editStatusActionForm.accountName}" />&nbsp;#&nbsp;<c:out
								value="${sessionScope.editStatusActionForm.globalAccountNum}" />&nbsp;-</span>
							<mifos:mifoslabel name="accounts.ConfirmStatusChange"	bundle="accountsUIResources" /></td>
						</tr>
						<tr>
							<td class="headingorange"><span class="fontnormalbold"> <mifos:mifoslabel
								name="accounts.NewStatus" bundle="accountsUIResources"></mifos:mifoslabel>:
							</span> <span class="fontnormal"> <mifoscustom:MifosImage
								id="${sessionScope.editStatusActionForm.newStatusId}"
								moduleName="org.mifos.application.accounts.util.resources.accountsImages" /><c:out
								value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'newStatusName')}" /> <c:if
								test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'flagName')}">
                     	- <c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'flagName')}" />
							</c:if></span></td>
						</tr>
						<tr><logic:messagesPresent>
							<td><br><font class="fontnormalRedBold"><span id="change_status_preview.error.message"><html-el:errors bundle="accountsUIResources" /></font></td>
							</logic:messagesPresent>
						</tr>
						<tr>
							<td class="fontnormal"><br>
							<span class="fontnormalbold"> <c:out
								value="${sessionScope.editStatusActionForm.commentDate}" />
							</span> <span class="fontnormal"><br>
							<c:out value="${sessionScope.editStatusActionForm.notes}"/>&nbsp;-</span> 
							<em><c:out
								value="${sessionScope.UserContext.name}" /></em>
							</td>
						</tr>
						<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'checklist')}">
							<tr>
								<td class="blueline"><img src="pages/framework/images/trans.gif"
									width="10" height="12"></td>
							</tr>
							<tr>
								<td class="fontnormal">&nbsp;</td>
							</tr>
							<tr>
								<td class="fontnormal">
								<c:if test="${sessionScope.editStatusActionForm.accountTypeId == '1'}">
									<mifos:mifoslabel name="accounts.CheckLoanTasksComplete" bundle="accountsUIResources" />
								</c:if>
								<c:if test="${sessionScope.editStatusActionForm.accountTypeId == '2'}">
									<mifos:mifoslabel name="accounts.CheckSavingsTasksComplete" bundle="accountsUIResources" />
								</c:if>
								</td>
							</tr>
						</c:if>
					</table>
					<br>
					<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'checklist')}">
						<table width="95%" border="0" cellpadding="3" cellspacing="0">
							<c:forEach var="chklist" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'checklist')}">
								<c:forEach var="chklistDetail" items="${chklist.checklistDetails}">
									<bean:size collection="${chklist.checklistDetails}" id="listSize" />
									<html-el:hidden property="chklistSize"	value="${pageScope.listSize}" />
								<tr class="fontnormal">
									<html-el:multibox name="editStatusActionForm"
										property="selectedItems">
										<td width="2%" valign="top"><c:out
											value="${chklistDetail.detailId}" /></td>
									</html-el:multibox>
									<c:out value="${chklistDetail.detailText}" />
								</tr>
								</c:forEach>
							</c:forEach>
						</table>
					</c:if>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td style="padding-top:5px;"><html-el:button styleId="change_status_preview.button.edit" property="editInfo"
								styleClass="insidebuttn"
								onclick="GoToEditPage(this.form)">
								<mifos:mifoslabel name="accounts.EditStatus"
									bundle="accountsUIResources"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:submit styleId="change_status_preview.button.submit" property="btn"
								styleClass="buttn">
								<mifos:mifoslabel name="accounts.submit" bundle="accountsUIResources"></mifos:mifoslabel>
							</html-el:submit> &nbsp;&nbsp; <html-el:button styleId="change_status_preview.button.cancel" property="btn"
								styleClass="cancelbuttn"
								onclick="goToCancelPage(this.form)">
								<mifos:mifoslabel name="accounts.cancel" bundle="accountsUIResources"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
					</table>
					<br>
					</td>
			</table>
			<html-el:hidden property="globalAccountNum" value="${sessionScope.editStatusActionForm.globalAccountNum}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
