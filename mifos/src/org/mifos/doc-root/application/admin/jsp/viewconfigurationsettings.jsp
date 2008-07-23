<%-- 
Copyright (c) 2005-2008 Grameen Foundation USA
1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
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

<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>

<c:set value="${requestScope.configSettings}" var="configSettings"/>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<html-el:form action="systemInfoAction.do?method=load">
			<td align="left" valign="top" bgcolor="#FFFFFF"
				style="padding-left:8px; padding-top:10px;">
				
				<div>
					<div><span class="headingorange"><mifos:mifoslabel name="VCS.title" bundle="viewConfigurationSettingsUIResources" /></span></div>
				<div>
				
				<div style="margin-top: 0.5em;"><span class="fontnormal">For all configuration parameters framed as questions, 1 &#8658; Yes, 0 &#8658; No</span></div>
				
				<table cellpadding="3" border="1"  style="margin-top: 1em;">
				   <tbody>
				      <c:forEach var="setting" items="${configSettings}">
				      <tr>
				         <td borde><span class="fontnormal" title="${setting.dbKey}"><c:out value="${setting.uiString}"/></span></td>
				         <td><span class="fontnormal"><c:out value="${setting.value}"/></span></td>
				      </tr>
				      </c:forEach>
				   </tbody>
				</table>

			</td>
		</html-el:form>
	</tiles:put>
</tiles:insert>
