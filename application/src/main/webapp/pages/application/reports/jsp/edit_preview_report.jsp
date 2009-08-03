<%-- 
Copyright (c) 2005-2008 Grameen Foundation USA
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

<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<tiles:insert definition=".view">
  <tiles:put name="body" type="string">
  <span id="page.id" value="edit_preview_report" />
  
  
  	<script type="text/javascript">
  	function goToPreviousPage(form) {
		form.action = 'birtReportsUploadAction.do?method=editprevious';
		form.submit();
	}
	function goToCancelPage(form){
		form.action = "birtReportsUploadAction.do?method=getViewReportPage";
		form.submit();
  	}
    </script>
    
		<html-el:form method="post"
			action="/birtReportsUploadAction.do?method=editThenUpload"
			 enctype="multipart/form-data">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt">
					<html-el:link href="AdminAction.do?method=load">
						<mifos:mifoslabel name="reports.Admin"/>
					</html-el:link> / </span> 
					<span class="fontnormal8pt"><html-el:link href="birtReportsUploadAction.do?method=getViewReportPage">
						<mifos:mifoslabel name="reports.linkviewreport"	bundle="reportsUIResources" />
					</html-el:link> / </span> 
					 
					<span class="fontnormal8ptbold">${birtReportsUploadActionForm.reportTitle}
					</span></td>
				</tr>
			</table>
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="3" cellspacing="0">
						<tr>
						  <td class="headingorange"><span class="heading">${birtReportsUploadActionForm.reportTitle} - </span><span class="headingorange">
							<mifos:mifoslabel name="reports.editinformation" bundle="reportsUIResources" /></span>
						  </td>
                        </tr>
                        <tr>
                          <td class="fontnormal">
                          	<mifos:mifoslabel name="reports.previewreporttips" bundle="reportsUIResources" />
                          </td>
                        </tr>
					</table>
					</td>
				</tr>
				<tr>
					<td>
						<font class="fontnormalRedBold">
						<html-el:errors bundle="reportsUIResources" /> 
						</font>
					</td>
				</tr>
			</table></br>

          	<table border="0" cellspacing="0" cellpadding="3" class="fontnormal">
				<tr>
					<td>
						<mifos:mifoslabel name="reports.labelTitle" />:
					</td>
					<td>
						<c:out value="${birtReportsUploadActionForm.reportTitle}" />
					</td>
				</tr>
				<tr>
					<td>
	                    <mifos:mifoslabel name="reports.reports" />
	                    <mifos:mifoslabel name="reports.category" />:
	                </td>
	                <td>
	                    <c:out value="${category.reportCategoryName}" />
	                </td>
	            </tr>
	            <tr>
	                <td>
	                    <mifos:mifoslabel name="reports.reports" />
	                    <mifos:mifoslabel name="reports.status" />:
	                </td>
	                <td>
						<c:if test="${birtReportsUploadActionForm.isActive == 1}">
							<mifos:mifoslabel name="reports.active"/>
						</c:if>
						<c:if test="${birtReportsUploadActionForm.isActive == 0}">
							<mifos:mifoslabel name="reports.inactive"/>
						</c:if>
                    </td>
				</tr>
				<tr>
					<td height="23">
						<a href="#"><mifos:mifoslabel name="reports.ReportTemplate" /></a>
					</td>
				</tr>
				<tr>
					<td>
						<input class="insidebuttn" type="button" onclick="javascript:history.go(-1)" 
											value="<mifos:mifoslabel name="reports.editReportInformation" />" name="Button222"/>
					</td>
				</tr>
			</table>
			<br>	
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="center" class="blueline">
						&nbsp;
					</td>
				</tr>
			</table>
			<br>	
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="center">
						<html-el:submit styleClass="buttn">
							<mifos:mifoslabel name="reports.submit"/>
						</html-el:submit>
						&nbsp; 
						<html-el:button
										onclick="goToCancelPage(this.form);" property="cancelButton"
										styleClass="cancelbuttn">
							<mifos:mifoslabel name="reports.cancel"/>
						</html-el:button>
					</td>
				</tr>
			</table>
			<br>
			<html:hidden property="method" value="search" />
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
















