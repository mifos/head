<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<tiles:insert definition=".create">
  <tiles:put name="body" type="string">
  
  	<script type="text/javascript">
	function goToCancelPage(form){
		form.action = "AdminAction.do?method=load";
		form.submit();
  	}
    </script>
		<html-el:form method="post"
			action="/birtReportsUploadAction.do?method=preview"
			 enctype="multipart/form-data">
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
											<td><img src="pages/framework/images/timeline/bigarrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorange"><mifos:mifoslabel
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
												name="reports.enterRInformation" /></td>
								</tr>
								<tr>
									<td class="fontnormalbold"><span class="fontnormal">
									    <mifos:mifoslabel name="reports.statement" />
										<font color="#ff0000">*</font>
										<mifos:mifoslabel name="reports.markedFieldStatement" />
										</span>
									</td>
								</tr>
								<tr>
                                    <td class="fontnormalbold"/>
                                </tr>
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
									      <option value="-1" selected>--Select--</option>
									      <c:forEach var="reportCategory" items="${sessionScope.listOfReports}" varStatus="loop" begin='0'>
									          <option <c:if test="${birtReportsUploadActionForm.reportCategoryId == reportCategory.reportCategoryId}">selected="true"</c:if>value="${reportCategory.reportCategoryId}">${reportCategory.reportCategoryName}</option>
									      </c:forEach>
									    </select>
									</td>
								</tr>
								<tr class="fontnormal">
								    <td align="right">
									    <font color="#ff0000">*</font>
									    <mifos:mifoslabel
										name="reports.labelSelectTemplate" />:</td>
								    <td>
                                   
                                        <input type="file" value="" name="file"/>
                                        
                                   
                                        
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
										<mifos:mifoslabel name="reports.preview"></mifos:mifoslabel>
									</html-el:submit> &nbsp; <html-el:button
										onclick="goToCancelPage(this.form);" property="cancelButton"
										value="Cancel" styleClass="cancelbuttn" style="width:70px">
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
