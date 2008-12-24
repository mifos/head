<!--
/**

* addNewChecklistsPreview.jsp    version: 1.0



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

<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insert definition=".create">
	<tiles:put name="body" type="string">

		<script language="JavaScript"
			src="pages/application/checklist/js/validator.js"
			type="text/javascript">
		</script>
		<script language="JavaScript"
			src="pages/framework/js/CommonUtilities.js"
			type="text/javascript">
		</script>
		<html-el:form method="post" action="/chkListAction.do?method=create" >
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
								<font class="fontnormalRedBold">
									<html-el:errors bundle="checklistUIResources"/>								
							 	</font>
								<tr>
									<td>
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/tick.gif"
												width="17" height="17"></td>
											<td class="timelineboldgray"><mifos:mifoslabel
												name="checklist.checklist_info" /></td>
										</tr>
									</table>
									</td>
									<td align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/bigarrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorange"><mifos:mifoslabel
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
									<td class="headingorange"><span class="heading"><mifos:mifoslabel
										name="checklist.addnewchecklist" /> - </span> <mifos:mifoslabel
										name="checklist.reviewchecklist_Info" /></td>
								</tr>
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="checklist.review_Info" /></td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
								<font class="fontnormalRedBold"> 
								
								<html-el:errors	bundle="checklistUIResources" /> </font>
								
								<td width="100%" colspan="2" class="fontnormalbold">
								
								<mifos:mifoslabel name="checklist.checklistdetails" /> 
								<br>
								<br>
								
								<span> 
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

								<mifos:mifoslabel name="checklist.displayed_status"/>
									<span class="fontnormal">
									<c:out value="${sessionScope.ChkListActionForm.stateName}"></c:out>
									</span>
										<br>
										<br>

									<mifos:mifoslabel name="checklist.items" /> 
								<c:forEach	var="item" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'details')}">  
							<%--			<c:forEach	var="item" items="${sessionScope.details}"> --%>
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
									<td colspan="2" class="fontnormalbold"><html-el:button
										property="button" styleClass="insidebuttn" 
										onclick="javascript:fnEdit(this.form)">
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


									<td align="center"><html-el:submit
										styleClass="buttn" property="submitBtn">

										<mifos:mifoslabel name="checklist.button_submit" />

									</html-el:submit> &nbsp; <html-el:button property="cancelButton"
										styleClass="cancelbuttn" onclick="javascript:fnCancel(this.form)">

										<mifos:mifoslabel name="checklist.button_cancel" />

									</html-el:button>								
									
									</td>
								</tr>
								<html-el:hidden property="method" value="create" />
							</table>
							<br>
							</td>
						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
