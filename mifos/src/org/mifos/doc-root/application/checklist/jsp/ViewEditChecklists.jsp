	<!--
/**

* ViewEditChecklists.jsp    version: 1.0



* Copyright © 2005-2006 Grameen Foundation USA

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
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/tags/date" prefix="date"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">

		<script>
  function getChecklist(id,type,status,categoryId)
 {
 		
     	document.CheckListForm.typeId.value=type;
		document.CheckListForm.checklistId.value=id;
		document.CheckListForm.categoryId.value=categoryId;
		document.CheckListForm.statusOfCheckList.value=status;
 		document.CheckListForm.method.value="get";
 		document.CheckListForm.submit();
 }
</script>
		<html-el:form action="/checkListAction">   
			<table width="100%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"> 
							<html-el:link action="AdminAction.do?method=load">
								<mifos:mifoslabel name="checklist.admin" />
							</html-el:link> / 
						</span> 
						<span class="fontnormal8ptbold">
							 <mifos:mifoslabel name="checklist.view_checklists" /> 
						</span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="3" cellspacing="0">
						<tr>						
						<td class="headingorange"><span class="headingorange">
							<mifos:mifoslabel name="checklist.view_checklists" /> </span>
						</td>
						</tr>
						<tr>
							<td class="fontnormalbold">
								<span class="fontnormal"> 
									<mifos:mifoslabel name="checklist.description" />
										<html-el:link action="checkListAction.do?method=load">
											<mifos:mifoslabel name="checklist.definenewchecklist" />
										</html-el:link> 
									<br>							
								</span>
								<span class="fontnormalbold"> </span>
								<font class="fontnormalRedBold">
							 <html-el:errors bundle="checklistUIResources" /> 
						</font>
							<table width="90%" border="0" cellspacing="0" cellpadding="0">
								<tr class="fontnormal">
									<td width="99%">
									<c:if test="${not empty requestScope.CustomerLevelMaster}">
										<c:forEach var="customermaster"	items="${requestScope.CustomerLevelMaster}">											
											<br>
											<span class="fontnormalbold">${customermaster.checkListName}</span>
											<c:if test="${not empty requestScope.CustomerSearchMaster}">
												<c:forEach var="custSearchMaster" items="${requestScope.CustomerSearchMaster}">
													<c:if test="${custSearchMaster.stateId == customermaster.checkListId}">														
														<br>
														<img src="pages/framework/images/bullet_circle.gif" width="9" height="11">
														<html-el:link href="javascript:getChecklist(${custSearchMaster.checkListId},${custSearchMaster.recordType},'${custSearchMaster.stateName}',${custSearchMaster.stateId})">	
															${custSearchMaster.checkListName}
														</html-el:link>
														(${custSearchMaster.stateName})
														<c:if test="${custSearchMaster.checklistStatus == 0}">
														<img src="pages/framework/images/status_closedblack.gif">
														Inactive
														</c:if>
													</c:if>
												</c:forEach>
											</c:if>
											<br>
										</c:forEach>
									</c:if>									
									<c:if test="${not empty requestScope.PrdTypeMaster}">
										<c:forEach var="checklistmaster" items="${requestScope.PrdTypeMaster}">
											<br>											
											<span class="fontnormalbold">${checklistmaster.checkListName}
											</span>
											<c:if test="${not empty requestScope.PrdSearchMaster}">
												<c:forEach var="SearchMaster" items="${requestScope.PrdSearchMaster}">
													<c:if test="${SearchMaster.typeNameId == checklistmaster.checkListId}">														
														<br>
														<img src="pages/framework/images/bullet_circle.gif" width="9" height="11">
														<html-el:link href="javascript:getChecklist(${SearchMaster.checkListId},${SearchMaster.recordType},'${SearchMaster.stateName}',${SearchMaster.typeNameId})">	${SearchMaster.checkListName}</html-el:link>
														(${SearchMaster.stateName})
														<c:if test="${SearchMaster.checklistStatus == 0}">
														<img src="pages/framework/images/status_closedblack.gif">
														Inactive
														</c:if>
													</c:if>
												</c:forEach>
											</c:if>
											<br>
										</c:forEach>
									</c:if> </td>
								</tr>
								<html-el:hidden property="method" value="" />
								<html-el:hidden property="input" value="" />
								<html-el:hidden property="checklistId" />
								<html-el:hidden property="checklistName" />
								<html-el:hidden property="statusOfCheckList" />
								<html-el:hidden property="typeId" />
								<html-el:hidden property="categoryId" value="" />            
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
		</html-el:form>
	</tiles:put>
</tiles:insert>




