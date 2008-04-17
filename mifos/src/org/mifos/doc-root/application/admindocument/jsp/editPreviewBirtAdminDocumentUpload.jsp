<!--

/**

 * editPreviewBirtAdminDocumentUpload.jsp    version: 1.0



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

 */

-->

<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".create">
	<script type="text/javascript">
	function goToPreviousPage(form) {
		form.action = 'birtAdminDocumentUploadAction.do?method=previous';
		form.submit();
	}
	function goToCancelPage(form){
		form.action = "AdminAction.do?method=load";
		form.submit();
  	}
	</script>
	<tiles:put name="body" type="string">
		<html-el:form method="post"
			action="/birtAdminDocumentUploadAction.do?method=editThenUpload">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="470" align="left" valign="top" bgcolor="#FFFFFF">
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
								<tr>
									<td width="33%">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/tick.gif"
												width="17" height="17" alt=""></td>
											<td class="timelineboldgray"><mifos:mifoslabel
												name="reports.documentInformation" /></td>
										</tr>
									</table>
									</td>
									<td width="33%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorangelight"><mifos:mifoslabel
												name="reports.review&submit" /></td>
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
							<td width="70%" height="24" align="left" valign="top"
								class="paddingleftCreates">
							<table width="98%" border="0" cellspacing="0" cellpadding="3">
								<tr>
									<td class="headingorange"><span class="heading"> <mifos:mifoslabel
										name="reports.uploadAdministrativeDocument" /> - </span> <mifos:mifoslabel
										name="reports.review&submit" /></td>
								</tr>
								<tr>
									<td class="fontnormalbold"><span class="fontnormal">
									<mifos:mifoslabel name="reports.reviewStatement" /> <mifos:mifoslabel
										name="reports.clickSubmit" /> <mifos:mifoslabel
										name="reports.clickCancel" /> </span></td>
								</tr>
								<tr>
									<td class="fontnormalbold" />
								</tr>
								<tr>
									<td class="fontnormalbold"><br />
									<mifos:mifoslabel name="reports.documentInformation" /></td>
								</tr>
								<tr>
									<td><font class="fontnormalRedBold"> <html-el:errors
										bundle="ReportsUIResources" /> </font></td>
								</tr>
							</table>
							<table width="90%" border="0" cellspacing="0" cellpadding="3">
								<tr class="fontnormal">
									<td height="23" class="fontnormal"><mifos:mifoslabel
										name="reports.admindocumenttitle" isColonRequired="Yes"/><span class="fontnormal">
									<c:out
										value="${birtAdminDocumentUploadActionForm.adminiDocumentTitle}" />
									<br />
									<mifos:mifoslabel name="reports.accountType" isColonRequired="Yes"/><span
										class="fontnormal"> <c:out
										value="${birtAdminDocumentUploadActionForm.accountTypeName}" />
									<br />
									<mifos:mifoslabel name="reports.ShowWhenStatus" isColonRequired="Yes"/>  
									<c:forEach
										items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'SelectedStatus')}"
										var="selectedStatus">
										<c:out value="${selectedStatus.description}" />,
										
									</c:forEach>
									</td>
								</tr>
								<tr class="fontnormal">
									<td height="23" class="fontnormal"><a href="#"><mifos:mifoslabel
										name="reports.administrativedocument" /></a></td>
								</tr>
								<tr class="fontnormal">
									<td><input class="insidebuttn" type="button"
										onclick="javascript:history.go(-1)"
										value="<mifos:mifoslabel name="reports.editDocumentInformation" />"
										name="Button222" /></td>
								</tr>
							</table>
							<br>
							<table width="90%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center" class="blueline">&nbsp;</td>
								</tr>
							</table>
							<br>
							<table width="90%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center"><html-el:submit styleClass="buttn">
										<mifos:mifoslabel name="reports.submit"></mifos:mifoslabel>
									</html-el:submit> &nbsp; <html-el:button onclick="goToCancelPage(this.form);"
										property="cancelButton" value="Cancel"
										styleClass="cancelbuttn" >
										<mifos:mifoslabel name="reports.cancel" />
									</html-el:button></td>
								</tr>
							</table>
							<br>
							</td>
						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>
			<html:hidden property="method" value="search" />
			<html-el:hidden property="currentFlowKey"
				value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
