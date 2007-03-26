<!--
/**

* checklistDetails.jsp    version: 1.0



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
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">

		<script language="JavaScript" src="pages/application/checklist/js/validator.js" type="text/javascript">
</script>
		<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<c:set var="checkList" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" />
			<tr>
				<td class="bluetablehead05">
					<span class="fontnormal8pt"> <html-el:link action="AdminAction.do?method=load">
							<mifos:mifoslabel name="checklist.admin" />
						</html-el:link> / <html-el:link action="chkListAction.do?method=loadAllChecklist&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
							<mifos:mifoslabel name="checklist.view_checklists" />
						</html-el:link> / </span> <span class="fontnormal8ptbold"> ${checkList.checklistName} </span>
				</td>
			</tr>
		</table>
		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td width="70%" align="left" valign="top" class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="50%" height="23" class="headingorange">
								${checkList.checklistName}
							</td>
							<td width="50%" align="right">
								<html-el:link href="chkListAction.do?method=manage&checkListId=${checkList.checklistId}&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
									<mifos:mifoslabel name="checklist.edit_checklist" />
								</html-el:link>
							</td>
						</tr>
						<tr>
							<font class="fontnormalRedBold"> <html-el:errors bundle="checklistUIResources" /> </font>
						</tr>
						<tr>
							<td height="23" colspan="2" class="fontnormal">
								<span class="fontnormal"> </span> <span class="fontnormal"><c:choose>
										<c:when test="${checkList.checklistStatus==1}">
											<img src="pages/framework/images/status_activegreen.gif">
											<mifos:mifoslabel name="checklist.active" />
										</c:when>
										<c:otherwise>
											<img src="pages/framework/images/status_closedblack.gif">
											<mifos:mifoslabel name="checklist.inactive" />
										</c:otherwise>
									</c:choose> </span>
								<br>
								<br>
								<mifos:mifoslabel name="checklist.type" />
								<c:choose>
									<c:when test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'Type') == 1}">
										<c:if test="${checkList.customerLevel.id==3}">
											<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
										</c:if>
										<c:if test="${checkList.customerLevel.id==2}">
											<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />
										</c:if>
										<c:if test="${checkList.customerLevel.id==1}">
											<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />
										</c:if>

									</c:when>
									<c:otherwise>
										<c:out value="${checkList.productTypeEntity.name}" />
									</c:otherwise>
								</c:choose>
								<br>
								<mifos:mifoslabel name="checklist.status" />
								:
								<c:choose>
									<c:when test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'Type') == 1}">
										<c:out value="${checkList.customerStatus.name}" />

									</c:when>
									<c:otherwise>
										<c:out value="${checkList.accountStateEntity.name}" />

									</c:otherwise>
								</c:choose>
								<br>
								<mifos:mifoslabel name="checklist.createdby" />
								:
								<c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'createdname')}" />
								<br>
								<mifos:mifoslabel name="checklist.createddate" />
								:
								<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,checkList.createdDate)}" />

								<br>
								<p class="fontnormal">
									<span class="fontnormalbold"> <mifos:mifoslabel name="checklist.items" /> </span>
									<c:if test="${not empty checkList.checklistDetails}">
										<c:forEach var="details" items="${checkList.checklistDetails}">
											<br>
											<br>
											<span class="fontnormal"> ${details.detailText} </span>
										</c:forEach>
									</c:if>
							</td>
						</tr>
					</table>
					<br>
				</td>
			</tr>
		</table>
		<br>
	</tiles:put>
</tiles:insert>


