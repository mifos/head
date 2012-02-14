<%--
Copyright (c) 2005-2011 Grameen Foundation USA
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

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
	<span id="page.id" title="view_checkList"></span>
	
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td class="bluetablehead05">
					<span class="fontnormal8pt"> <html-el:link action="AdminAction.do?method=load">
							<mifos:mifoslabel name="checklist.admin" />
						</html-el:link> / </span> <span class="fontnormal8ptbold"> <mifos:mifoslabel name="checklist.view_checklists" /> </span>
				</td>
			</tr>
		</table>
		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange">
								<span class="headingorange"> <mifos:mifoslabel name="checklist.view_checklists" /> </span>
							</td>
						</tr>
						<tr>
							<td class="fontnormalbold">
								<span class="fontnormal"> <mifos:mifoslabel name="checklist.description" /> <html-el:link action="chkListAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
										<mifos:mifoslabel name="checklist.definenewchecklist" />
									</html-el:link> <br> </span> <span class="fontnormalbold"> </span> <font class="fontnormalRedBold"> <html-el:errors bundle="checklistUIResources" /> </font>
								<table width="90%" border="0" cellspacing="0" cellpadding="0">
									<tr class="fontnormal">
										<td width="99%">

											<br>
											<span class="fontnormalbold"><mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /></span>
											<c:forEach var="checklist" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'centerchecklist')}">
												<br>
											<c:url value="chkListAction.do" var="chkListActionGetMethodUrl" >
												<c:param name="method" value="get" />
												<c:param name="checkListId" value="${checklist.checklistId}" />
												<c:param name="recordOfficeId" value="${UserContext.branchId}" />
												<c:param name="recordLoanOfficerId" value="${UserContext.id}" />
												<c:param name="randomNUm" value="${sessionScope.randomNUm}" />
												<c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
											</c:url >
												<img src="pages/framework/images/bullet_circle.gif" width="9" height="11">
												<html-el:link href="${chkListActionGetMethodUrl}">	
															${checklist.checklistName}
														</html-el:link>
														(${checklist.name})
														<c:if test="${checklist.checklistStatus == 0}">
													<img src="pages/framework/images/status_closedblack.gif">
														<mifos:mifoslabel name="checklist.inactive" />
														</c:if>

											</c:forEach>
											<br>
											<br>
											<span class="fontnormalbold"><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></span>
											<c:forEach var="checklist" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'groupchecklist')}">
												<br>
											<c:url value="chkListAction.do" var="chkListActionGetMethodUrl" >
												<c:param name="method" value="get" />
												<c:param name="checkListId" value="${checklist.checklistId}" />
												<c:param name="recordOfficeId" value="${UserContext.branchId}" />
												<c:param name="recordLoanOfficerId" value="${UserContext.id}" />
												<c:param name="randomNUm" value="${sessionScope.randomNUm}" />
												<c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
											</c:url >
												<img src="pages/framework/images/bullet_circle.gif" width="9" height="11">
												<html-el:link href="${chkListActionGetMethodUrl}">	
															${checklist.checklistName}
														</html-el:link>
														(${checklist.name})
														<c:if test="${checklist.checklistStatus == 0}">
													<img src="pages/framework/images/status_closedblack.gif">
														<mifos:mifoslabel name="checklist.inactive" />
														</c:if>

											</c:forEach>
											<br>
											<br>
											<span class="fontnormalbold"><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></span>
											<c:forEach var="checklist" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'clientchecklist')}">
												<br>
											<c:url value="chkListAction.do" var="chkListActionGetMethodUrl" >
												<c:param name="method" value="get" />
												<c:param name="checkListId" value="${checklist.checklistId}" />
												<c:param name="recordOfficeId" value="${UserContext.branchId}" />
												<c:param name="recordLoanOfficerId" value="${UserContext.id}" />
												<c:param name="randomNUm" value="${sessionScope.randomNUm}" />
												<c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
											</c:url >
												<img src="pages/framework/images/bullet_circle.gif" width="9" height="11">
												<html-el:link href="${chkListActionGetMethodUrl}">	
															${checklist.checklistName}
														</html-el:link>
														(${checklist.name})
														<c:if test="${checklist.checklistStatus == 0}">
													<img src="pages/framework/images/status_closedblack.gif">
														<mifos:mifoslabel name="checklist.inactive" />
														</c:if>
											</c:forEach>
											<br>
											<br>
											<span class="fontnormalbold"><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /></span>
											<c:forEach var="checklist" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanchecklist')}">
												<br>
											<c:url value="chkListAction.do" var="chkListActionGetMethodUrl" >
												<c:param name="method" value="get" />
												<c:param name="checkListId" value="${checklist.checklistId}" />
												<c:param name="recordOfficeId" value="${UserContext.branchId}" />
												<c:param name="recordLoanOfficerId" value="${UserContext.id}" />
												<c:param name="randomNUm" value="${sessionScope.randomNUm}" />
												<c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
											</c:url >
												<img src="pages/framework/images/bullet_circle.gif" width="9" height="11">
												<html-el:link href="${chkListActionGetMethodUrl}">	
															${checklist.checklistName}
														</html-el:link>
														(${checklist.name})
														<c:if test="${checklist.checklistStatus == 0}">
													<img src="pages/framework/images/status_closedblack.gif">
														<mifos:mifoslabel name="checklist.inactive" />
														</c:if>
											</c:forEach>
											<br>
											<br>
											<span class="fontnormalbold"><mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" /></span>
											<c:forEach var="checklist" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'savingschecklist')}">
												<br>
											<c:url value="chkListAction.do" var="chkListActionGetMethodUrl" >
												<c:param name="method" value="get" />
												<c:param name="checkListId" value="${checklist.checklistId}" />
												<c:param name="recordOfficeId" value="${UserContext.branchId}" />
												<c:param name="recordLoanOfficerId" value="${UserContext.id}" />
												<c:param name="randomNUm" value="${sessionScope.randomNUm}" />
												<c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
											</c:url >
												<img src="pages/framework/images/bullet_circle.gif" width="9" height="11">
												<html-el:link href="${chkListActionGetMethodUrl}">	
															${checklist.checklistName}
														</html-el:link>
														(${checklist.name})
														<c:if test="${checklist.checklistStatus == 0}">
													<img src="pages/framework/images/status_closedblack.gif">
														<mifos:mifoslabel name="checklist.inactive" />
														</c:if>
											</c:forEach>
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
					<br>
				</td>
			</tr>
		</table>
		<br>
		<br>
	</tiles:put>
</tiles:insert>




