<!--
/**

* managePreview.jsp    version: 1.0



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

<%@taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>


<tiles:insert definition=".view">
	<tiles:put name="body" type="string">

		<script language="JavaScript"
			src="pages/application/checklist/js/validator.js"
			type="text/javascript">
</script>
<script language="JavaScript"
			src="pages/framework/js/CommonUtilities.js"
			type="text/javascript">
		</script>
		<html-el:form method="post" action="/checkListAction.do" >

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
					<td align="left" valign="top" class="paddingleftCreates">
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange">
								<span class="heading">
									<mifos:mifoslabel name="checklist.addnewchecklist" /> - 
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
								<c:out value="${requestScope.Context.valueObject.checklistName}"></c:out>
								</span>
								<br>
								<mifos:mifoslabel name="checklist.type" /> 
								<span class="fontnormal">
								<c:out value="${param.typeName}"></c:out> 							
								</span>
								<br>
								<mifos:mifoslabel name="checklist.displayed_status"  />
								<span class="fontnormal">
								<c:out value="${param.displayedStatus}"></c:out>
								</span>
								<br>
								<mifos:mifoslabel name="checklist.status_checklist" />
								<span class="fontnormal">
								<c:choose>
									<c:when test='${param.checklistStatus=="1"}'>								
										Active
									</c:when>
									<c:otherwise>
										Inactive
									</c:otherwise>
								</c:choose>				
								</span>						
								<br>
								<br>
								<mifos:mifoslabel name="checklist.items" /> 
								<c:forEach	var="item" items="${requestScope.Context.valueObject.checklistDetailSet}">
								<br>
								<span class="fontnormal"><c:out value="${item.detailText}"></c:out></span>
								<br>																				
								</c:forEach> 
								<br>
							</span>
						</td>
							
						</tr>
						<tr>
							<td colspan="2" class="fontnormalbold">
							<html-el:button	property="button" styleClass="insidebuttn" style="width:140px" onclick="javascript:fnEdit(this.form)">
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
							<html-el:submit property="submitbutton" style="width:70px" styleClass="buttn" onclick="javascript:fnUpdate(this.form)">
								<mifos:mifoslabel name="checklist.button_submit" />
							</html-el:submit> &nbsp; 														
							<c:if test='${CheckListParent.class.name == "org.mifos.application.accounts.util.valueobjects.AccountCheckList"}'>																																
								<html-el:hidden property="checklistId" value="${CheckListParent.checkList.checklistId}"/>			
							</c:if>	
							<c:if test='${CheckListParent.class.name == "org.mifos.application.checklist.util.valueobjects.CustomerCheckList"}'>												
								<html-el:hidden property="checklistId" value="${CheckListParent.checkList.checklistId}"/>									
							</c:if>	
							<html-el:button property="button" style="width:70px"	styleClass="cancelbuttn" onclick="javascript:getChecklist('${param.previousChecklistId}','${param.previousTypeId}','${param.previousStatusOfCheckList}',${param.previousCategoryId})">
									<mifos:mifoslabel name="checklist.button_cancel" />
							</html-el:button>									
						</td>
						</tr>
						<html-el:hidden property="method" value="update" />
						<html-el:hidden property="input" value="manage" />												
						<html-el:hidden property="statusOfCheckList" value="${param.displayedStatus}"/>						
						<html-el:hidden property="categoryId" value="${param.categoryId}" />
						<html-el:hidden property="typeId" value="${param.typeId}" />	
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
