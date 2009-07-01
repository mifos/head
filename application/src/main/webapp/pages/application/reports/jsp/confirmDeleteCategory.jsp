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


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
    
<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
	<script type="text/javascript">
	function goToCancelPage(form){
		form.action = "reportsCategoryAction.do?method=viewReportsCategory";
		form.submit();
	}
	</script>
		<html-el:form method = "post" action="/reportsCategoryAction.do?method=deleteReportsCategory">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt">
					<html-el:link href="AdminAction.do?method=load">
						<mifos:mifoslabel name="product.admin" bundle="ProductDefUIResources" />
					</html-el:link> / </span> 
					<span class="fontnormal8pt"><html-el:link href="reportsCategoryAction.do?method=viewReportsCategory">
						<mifos:mifoslabel name="reportsCategory.linkViewReportCategory"	bundle="reportsCategoryUIResources" />
					</html-el:link> / </span> 
					 
					<span class="fontnormal8ptbold">${reportsCategoryActionForm.categoryName}</span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="50%" height="23" class="heading">
							<c:out value="${reportsCategoryActionForm.categoryName}" />
							-
							<span class="headingorange">
								<mifos:mifoslabel name="reportsCategory.deleteCategory"	bundle="reportsCategoryUIResources" />
							</span>
							<% if (null == request.getAttribute(org.apache.struts.Globals.ERROR_KEY)) { %>
							<br /><br />
							<span class="fontnormalRedBold">
								<mifos:mifoslabel name="reportsCategory.deleteCategoryConfirm" bundle="reportsCategoryUIResources" />
							</span>
							<br /><br />
							<span class="fontnormal">
								<mifos:mifoslabel name="reportsCategory.deleteCategoryInstruction" bundle="reportsCategoryUIResources" />
							</span>
							<% } %>
							</td>
						</tr>
						<tr>
							<td>
								<font class="fontnormalRedBold">
									<html-el:errors	bundle="reportsCategoryUIResources" />	
								</font>
							</td>
						</tr>
					</table>
					<table width="93%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center">
							<% if (null == request.getAttribute(org.apache.struts.Globals.ERROR_KEY)) { %>
							<html-el:submit styleClass="buttn">
								<mifos:mifoslabel name="reportsCategory.submit" bundle="reportsCategoryUIResources" />
							</html-el:submit>
							<% } %>
							&nbsp;
							<html-el:button
								onclick="goToCancelPage(this.form)"  property="SS"
								styleClass="cancelbuttn">
								<mifos:mifoslabel name="reportsCategory.cancel"
									bundle="reportsCategoryUIResources" />
							</html-el:button></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			<br>
			<input type="hidden" name="categoryId" value="<%=request.getParameter("categoryId")%>">
		</html-el:form>
	</tiles:put>

</tiles:insert>
