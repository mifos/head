<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<tiles:insert definition=".create">
	<script type="text/javascript">
	function goToPreviousPage(form) {
		form.action = 'birtReportsUploadAction.do?method=previous';
		form.submit();
	}
	</script>
  <tiles:put name="body" type="string">
		<html-el:form method="post"
			action="/birtReportsUploadAction.do?method=upload">
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
												name="reports.information"/>
												</td>
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
									<td class="headingorange">
										<span class="heading"> 
											<mifos:mifoslabel name="reports.upload" /> -
										</span>  
									<mifos:mifoslabel
												name="reports.review&submit" /></td>
								</tr>
								<tr>
									<td class="fontnormalbold"><span class="fontnormal">
									    <mifos:mifoslabel name="reports.reviewStatement" />
										<mifos:mifoslabel name="reports.clickSubmit" />
										<mifos:mifoslabel name="reports.clickCancel" />
										</span>
									</td>
								</tr>
								<tr>
                                    <td class="fontnormalbold"/>
                                </tr>
								<tr>
								    <td class="fontnormalbold"> 
								    	<br />
								       <mifos:mifoslabel name="reports.information" />
								    </td>
								</tr>
								<tr>
									<td>
										<font class="fontnormalRedBold">
											<html-el:errors bundle="CustomerSearchUIResources" /> 
										</font>
									</td>
								</tr>
							</table>
							<table width="90%" border="0" cellspacing="0" cellpadding="3">
								<tr class="fontnormal">
									<td height="23" class="fontnormal"><mifos:mifoslabel
										name="reports.labelTitle" />:<span class="fontnormal">
	                                     <c:out value="${birtReportsUploadActionForm.reportTitle}" />
	                                     <br />
	                                     <mifos:mifoslabel
										name="reports.labelTitle" />:<span class="fontnormal">
									    <c:out value="${category.reportCategoryName}" />
                                    </td>
								</tr>
							    <tr class="fontnormal">
									<td height="23" class="fontnormal">
										<a href="#"><mifos:mifoslabel name="reports.linkReportTemplate" /></a>
									</td>
								</tr>
								<tr class="fontnormal">
									<td>
										<input class="insidebuttn" type="button" onclick="javascript:goToPreviousPage(this.form)" 
											value="<mifos:mifoslabel name="reports.editReportInformation" />" name="Button222"/>
									</td>
								</tr>
						  </table><br>	
						 <table width="90%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center" class="blueline">
												&nbsp;
											</td>
										</tr>
						  </table><br>	
						<table width="90%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center"><html-el:submit styleClass="buttn"
										style="width:70px;">
										<mifos:mifoslabel name="reports.submit"></mifos:mifoslabel>
									</html-el:submit> &nbsp; <html-el:button property="cancelBtn"
										styleClass="cancelbuttn" style="width:70px">
										<mifos:mifoslabel name="reports.cancel"></mifos:mifoslabel>
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
