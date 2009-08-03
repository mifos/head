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
  <span id="page.id" value="editReport" />
  
  	<script type="text/javascript">
	function goToCancelPage(form){
		form.action = "birtReportsUploadAction.do?method=getViewReportPage";
		form.submit();
  	}
    </script>
		<html-el:form method="post"
			action="/birtReportsUploadAction.do?method=editpreview"
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
					 
					<span class="fontnormal8ptbold">${birtReportsUploadActionForm.reportTitle}</span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="3" cellspacing="0">
						<tr>
						  <td class="headingorange"><span class="heading">${birtReportsUploadActionForm.reportTitle} - </span><span class="headingorange">
							<mifos:mifoslabel name="reports.editinformation" bundle="reportsUIResources" /></span>
						  </td>
                        </tr>
                        <tr>
                          <td class="fontnormal"><mifos:mifoslabel name="reports.editreporttips" bundle="reportsUIResources" /><span class="mandatorytext">
                          <font color="#FF0000"><br> *</font></span><mifos:mifoslabel name="reports.editreporttipswithasterisk" bundle="reportsUIResources" /></td>
                        </tr>
					</table>
					</td>
				</tr>
			</table>

           <table width="95%" border="0" cellpadding="0"
						cellspacing="0" class="">
						<tr>
							<td width="90%" height="24" align="left" valign="top"
								class="paddingleftCreates">
							<table width="98%" border="0" cellspacing="0" cellpadding="3">
								<tr>
								    <td class="fontnormalbold"> 
								       <mifos:mifoslabel name="reports.details" />
								    </td>
								</tr>
								<tr>
									<td>
										<font class="fontnormalRedBold">
											<html-el:errors	bundle="reportsUIResources" />	
										</font>
									</td>
								</tr>
							</table>
							<br>
							<table width="90%" border="0" cellspacing="0" cellpadding="3">
								<tr class="fontnormal">
									<td align="right">
									    <font color="#ff0000">*</font>
									    <mifos:mifoslabel
										name="reports.labelTitle" />:</td>
									<td><html-el:text styleId="111"
										property="reportTitle" maxlength="200"/> </td>
								</tr>
							    <tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="reports.labelCategory" mandatory="yes"/>
									</td>
									<td>
									    <select style="width:136px;" name="reportCategoryId">
									      <option value="-1" selected><mifos:mifoslabel name="select" bundle="UIResources"/></option>
									      <c:forEach var="reportCategory" items="${sessionScope.listOfReports}" varStatus="loop" begin='0'>
									          <option <c:if test="${birtReportsUploadActionForm.reportCategoryId == reportCategory.reportCategoryId}">selected="true"</c:if>value="${reportCategory.reportCategoryId}">${reportCategory.reportCategoryName}</option>
									      </c:forEach>
									    </select>
									</td>
								</tr>
								
								 <tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="reports.labelReportStatus" mandatory="yes"/>
									</td>
									<td>
									    <select style="width:136px;" name="isActive">
									      <option value="-1" selected>
									      	<mifos:mifoslabel name="select" bundle="UIResources"/>
									      </option>
									      <option <c:if test="${birtReportsUploadActionForm.isActive == 1}">selected="true"</c:if> value="1">
									      	<mifos:mifoslabel name="reports.active"/>
									      </option>  
									      <option <c:if test="${birtReportsUploadActionForm.isActive == 0}">selected="true"</c:if> value="0">
									      	<mifos:mifoslabel name="reports.inactive"/>
									      </option>  
									    </select>
									</td>
								</tr>
								
								<tr class="fontnormal">
								    <td align="right">
									    <mifos:mifoslabel
										name="reports.labelSelectTemplate" />:</td>
								    <td>
                                   
                                        <input type="file" value="" name="file"/>
                                        
                                   
                                        
                                    </td>
								</tr>
						  </table><br>	
						 <table width="95%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center" class="blueline">
												&nbsp;
											</td>
										</tr>
						  </table><br>	
						<table width="90%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center"><html-el:submit styleClass="buttn">
										<mifos:mifoslabel name="reports.preview"/>
									</html-el:submit> &nbsp; <html-el:button
										onclick="goToCancelPage(this.form);" property="cancelButton" styleClass="cancelbuttn">
										<mifos:mifoslabel name="reports.cancel"/>
									</html-el:button></td>
								</tr>
						</table><br>
							</td>
						</tr>
					</table><br>

					</td>
				</tr>
			</table>
			<html:hidden property="method" value="search" />
			
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>

















