<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<tiles:insert definition=".view">
  <tiles:put name="body" type="string">
  
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
				<tr>
					<td>
						<font class="fontnormalRedBold">
						<html-el:errors bundle="ReportsUIResources" /> 
						</font>
					</td>
				</tr>
			</table></br>

          	<table width="95%" border="0" cellspacing="0" cellpadding="3">
								<tr class="fontnormal">
									<td height="23" class="fontnormal"><mifos:mifoslabel
										name="reports.labelTitle" />:<span class="fontnormal">
	                                     <c:out value="${birtReportsUploadActionForm.reportTitle}" />
	                                     <br /><mifos:mifoslabel
										name="reports.reports" />
	                                     <mifos:mifoslabel
										name="reports.category" />:<span class="fontnormal">
									    <c:out value="${category.reportCategoryName}" />
	                                     <br /><mifos:mifoslabel
										name="reports.reports" />
	                                     <mifos:mifoslabel
										name="reports.status" />:<span class="fontnormal">
										<c:if test="${birtReportsUploadActionForm.isActive == 1}">Active</c:if>
										<c:if test="${birtReportsUploadActionForm.isActive == 0}">Inactive</c:if>
                                    </td>
								</tr>
							    <tr class="fontnormal">
									<td height="23" class="fontnormal">
										<a href="#"><mifos:mifoslabel name="reports.ReportTemplate" /></a>
									</td>
								</tr>
								<tr class="fontnormal">
									<td>
										<input class="insidebuttn" type="button" onclick="javascript:history.go(-1)" 
											value="<mifos:mifoslabel name="reports.editReportInformation" />" name="Button222"/>
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
						<table width="95%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center"><html-el:submit styleClass="buttn"
										style="width:70px;">
										<mifos:mifoslabel name="reports.submit"></mifos:mifoslabel>
									</html-el:submit> &nbsp; <html-el:button
										onclick="goToCancelPage(this.form);" property="cancelButton"
										value="Cancel" styleClass="cancelbuttn" style="width:70px">
										<mifos:mifoslabel name="reports.cancel"/>
									</html-el:button></td>
								</tr>
						</table><br>
			<html:hidden property="method" value="search" />
			
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
















