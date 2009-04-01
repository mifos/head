<!-- /**
 
 * viewpersonnelnotes.jsp    version: 1.0
 
 
 
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
 
 */-->
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<html-el:form action="personnelNoteAction.do?method=preview">
			<c:set var="personnelBO" scope="request"
						value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <a id="viewpersonnelnotes.link.admin"
						href="AdminAction.do?method=load"> <mifos:mifoslabel
						name="Personnel.Admin" bundle="PersonnelUIResources"></mifos:mifoslabel>
					</a> / <a id="viewpersonnelnotes.link.viewUsers" href="PersonAction.do?method=loadSearch"> <mifos:mifoslabel
						name="Personnel.ViewUsers" bundle="PersonnelUIResources"></mifos:mifoslabel>
					</a> / <c:set var="personnelBO" scope="request"
						value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" />
					<a id="viewpersonnelnotes.link.viewUser" href="PersonAction.do?method=get&globalPersonnelNum=<c:out value="${personnelBO.globalPersonnelNum}"/>">	<c:out	value="${personnelBO.displayName}" /></a></span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td><font class="fontnormalRedBold"> <span id="viewpersonnelnotess.error.message"><html-el:errors
						bundle="PersonnelUIResources" /></span> </font></td>
				</tr>
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="83%" class="headingorange"><span class="heading"> <c:out
								value="${sessionScope.personnelNoteActionForm.personnelName}" />
							- </span> <mifos:mifoslabel name="Personnel.Notes"
								bundle="PersonnelUIResources"></mifos:mifoslabel></td>
							<td width="17%" align="right" class="fontnormal"><a id="viewpersonnelnotes.link.addNote"
								href="personnelNoteAction.do?method=load&randomNUm=${sessionScope.randomNUm}&personnelId=<c:out value="${sessionScope.personnelNoteActionForm.personnelId}"/>&currentFlowKey=${requestScope.currentFlowKey}"> <mifos:mifoslabel
								name="Personnel.AddNewNote" bundle="PersonnelUIResources"></mifos:mifoslabel>
							</a></td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td><mifos:mifostabletagdata name="personnelNotes" key="allnotes"
								type="single" className="PersonnelNotes" width="100%" border="0"
								cellspacing="0" cellpadding="0" /></td>
						</tr>
					</table>

					<br>
					</td>
				</tr>
			</table>
			<br>
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
