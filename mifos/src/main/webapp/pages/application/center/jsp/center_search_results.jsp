<%--
Copyright (c) 2005-2009 Grameen Foundation USA
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

<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://struts.apache.org/tags-bean-el" prefix="bean-el"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<script type="text/javascript">
 
 function goToCancelPage()
 {
     centerCustActionForm.input.value="create";
      centerCustActionForm.method.value="cancel";
     centerCustActionForm.submit();
 }
    
 </script>
<tiles:insert definition=".withoutmenu">
 <tiles:put name="body" type="string">
<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.CenterUIResources"/>
<html-el:form method="post" action="centerCustAction.do">

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
									<td class="timelineboldorange">
									<fmt:message key="Center.select">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /></fmt:param>
									</fmt:message></td>
								</tr>
							</table>
							</td>
							<td width="34%" align="center">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td><img src="pages/framework/images/timeline/orangearrow.gif"
										width="17" height="17"></td>
									<td class="timelineboldorangelight">
									<mifos:mifoslabel	name="${ConfigurationConstants.GROUP}"/>
									<mifos:mifoslabel	name="Center.Information" bundle="CenterUIResources"></mifos:mifoslabel></td>
								</tr>
							</table>
							</td>
							<td width="33%" align="right">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td><img src="pages/framework/images/timeline/orangearrow.gif"
										width="17" height="17"></td>
									<td class="timelineboldorangelight"><mifos:mifoslabel
										name="Center.ReviewSubmit" bundle="CenterUIResources"></mifos:mifoslabel></td>
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
							<td class="headingorange"><span class="heading"> 
							
							<mifos:mifoslabel name="Center.CreateNew" bundle="CenterUIResources"/>
							<mifos:mifoslabel name="${ConfigurationConstants.GROUP}"/>	
							<mifos:mifoslabel name="Center.dash" bundle="CenterUIResources"></mifos:mifoslabel>	
							</span> <mifos:mifoslabel name="Center.SearchSelectCenterHeading"
								bundle="CenterUIResources"></mifos:mifoslabel>
								<mifos:mifoslabel name="${ConfigurationConstants.CENTER}"/></td>
						</tr>
						<tr>
							<td class="fontnormalbold"><span class="fontnormal">                
				 <mifos:mifoslabel name="Center.SearchInstructions1"  bundle="CenterUIResources"></mifos:mifoslabel>
                <mifos:mifoslabel name="${ConfigurationConstants.CENTER}"/>
                <mifos:mifoslabel name="Center.SearchInstructions2"  bundle="CenterUIResources"></mifos:mifoslabel>

							<mifos:mifoslabel name="Center.CreatePageCancelInstruction"
								bundle="CenterUIResources"></mifos:mifoslabel> </span></td>
						</tr>
						<tr><logic:messagesPresent>
							<td><font class="fontnormalRedBold"><span id="center_search_results.error.message"><html-el:errors
								bundle="CenterUIResources" /></span> </font></td>
							</logic:messagesPresent>
						</tr>
					</table>


					<table width="96%" border="0" cellspacing="0" cellpadding="0">

						<tr class="fontnormal">
							<td><br><span id="center_search_results.label.search" class="fontnormal"> 
							<mifos:mifoslabel name="${ConfigurationConstants.CENTER}"/><c:out value=" "/>
							<mifos:mifoslabel name="Center.Name" bundle="CenterUIResources"></mifos:mifoslabel></span>
							<html-el:text styleId="center_search_results.input.search" property="searchString" />  <html-el:submit
								styleId="center_search_results.button.search" styleClass="buttn" style="width:70px;">
								<mifos:mifoslabel name="button.Search"
									bundle="CenterUIResources"></mifos:mifoslabel>
							</html-el:submit></td>
						</tr>
						<!-- Search results--->
						<tr>
							<td><mifos:mifostabletagdata name="center" key="centerSearch"
								type="single" className="CenterSearchResults" width="100%"
								border="0" cellspacing="0" cellpadding="0" /></td>

						</tr>
					</table>
					 <br>
					<table width="96%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td align="center"><html-el:button styleId="center_search_results.button.cancel" property="cancelButton"
								onclick="goToCancelPage();" styleClass="cancelbuttn"
								style="width:70px;">
								<mifos:mifoslabel name="button.cancel"
									bundle="CenterUIResources"></mifos:mifoslabel>
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
	<html-el:hidden property="input" value="" />
	<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
	<html-el:hidden property="method" value="search" />
</html-el:form>
</tiles:put>
</tiles:insert>



