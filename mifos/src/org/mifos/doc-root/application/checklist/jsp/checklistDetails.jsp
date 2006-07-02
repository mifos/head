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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">

		<script language="JavaScript"
			src="pages/application/checklist/js/validator.js"
			type="text/javascript">
</script>
<html-el:form action="/checkListAction">
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
						 <span class="fontnormal8ptbold">
						  	 <c:if	test="${not empty CheckListParent}">
									${CheckListParent.checkList.checklistName}
					     	 </c:if>									
						 </span>
					 </td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">		
				<tr>					
					<td width="70%" align="left" valign="top" class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="50%" height="23" class="headingorange">							
								<c:if test="${not empty CheckListParent}">
												${CheckListParent.checkList.checklistName}
								</c:if>
							</td>
							<td width="50%" align="right">					
									<html-el:link href="javascript:manage('${param.checklistId}','${param.typeId}','${param.statusOfCheckList}',${param.categoryId})">	
											Edit checklist
									</html-el:link>						
						 	</td>
						</tr>
						<tr>
							<font class="fontnormalRedBold"> 
								<html-el:errors	bundle="checklistUIResources" /> 
							</font>
						</tr>						
						<tr>							
							<td height="23" colspan="2" class="fontnormal">
								<span class="fontnormal"> </span>
								<span class="fontnormal">									
								<c:choose>
									<c:when test='${CheckListParent.checkList.checklistStatus=="1"}'>								
										<img src="pages/framework/images/status_activegreen.gif">
										Active								
									</c:when>
									<c:otherwise>
										<img src="pages/framework/images/status_closedblack.gif">
										Inactive
									</c:otherwise>
								</c:choose>								
							</span>
							<br>
							<br>
							<mifos:mifoslabel name="checklist.type" />							
							 <c:if	test="${not empty requestScope.Level}">
								<c:forEach var="checklistmaster" items="${requestScope.Level}">
									<c:choose>
										<c:when test='${Type == 1}'>
											<c:if test="${checklistmaster.checkListId ==CheckListParent.accountTypeId }">
											     ${checklistmaster.checkListName}
											  </c:if>
										</c:when>
										<c:otherwise>
											<c:if test="${checklistmaster.checkListId ==CheckListParent.levelId }">
											     ${checklistmaster.checkListName}
										  </c:if>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</c:if>
							<br> 
							<mifos:mifoslabel name="checklist.status" />: 
							<c:out	value="${param.statusOfCheckList}"/>
							 <br>
							 <mifos:mifoslabel name="checklist.createdby" />:                  			                 			
                  			<c:out	value="${CreatedBy}"/> <br>
                 			<mifos:mifoslabel name="checklist.createddate" />:                 			
                 			<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,CheckListParent.checkList.createdDate)}" />
                 			 
							<br>
							<p class="fontnormal">
							<span class="fontnormalbold">
								<mifos:mifoslabel name="checklist.items" /> 
							</span> 
							<c:if test="${not empty requestScope.CheckListParent.checkList.checklistDetailSet }">							
								<c:forEach var="details" items="${requestScope.CheckListParent.checkList.checklistDetailSet}">
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
			<html-el:hidden property="method" value="" />
			<html-el:hidden property="checklistId" value="" />			
			<html-el:hidden property="categoryId" value="" />
			<html-el:hidden property="typeId" value="create" />
			<html-el:hidden property="statusOfCheckList" />		
			<html-el:hidden property="previousChecklistId" value="${param.checklistId}" />			
			<html-el:hidden property="previousCategoryId" value="${param.categoryId}" />
			<html-el:hidden property="previousTypeId" value="${param.typeId}" />
			<html-el:hidden property="previousStatusOfCheckList" value="${param.statusOfCheckList}"/>	
</html-el:form>
	</tiles:put>
</tiles:insert>


