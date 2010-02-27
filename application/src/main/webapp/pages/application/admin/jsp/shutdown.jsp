<%--
Copyright (c) 2005-2009 Grameen Foundation USA
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

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
        <span id="page.id" title="Shutdown" />
		<html-el:form action="shutdownAction.do?method=shutdown">
			<td align="left" valign="top" bgcolor="#FFFFFF"
				style="padding-left:8px; padding-top:10px;">
				<table width="95%">
					<tr>
						<td colspan="2"><span class="headingorange"><mifos:mifoslabel name="admin.shutdown.title" bundle="adminUIResources" /></span><br />
							<span class="fontnormal"><mifos:mifoslabel name="admin.shutdown.welcome" bundle="adminUIResources" /></span></td>
					</tr>
					<tr><td colspan="2">&nbsp;</td></tr>
                    <tr>
						<td><span class="fontnormal"><mifos:mifoslabel name="admin.shutdown.status" bundle="adminUIResources" /></span></td>
						<td><span class="fontnormal" id="shutdown.text.status"><c:out value="${sessionScope.shutdownManager.status}"/></span></td>
					</tr>
                    <tr>
						<td><span class="fontnormal"><mifos:mifoslabel name="admin.shutdown.timeout" bundle="adminUIResources" /></span></td>
						<td><span class="fontnormal" id="shutdown.text.timeout"><mifos:mifosnumbertext name="shutdownActionForm" property="shutdownTimeout" /></span></td>
					</tr>
				</table>
                <table width="98%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td class="blueline">
							&nbsp;
						</td>
					</tr>
				</table>
				<br>
				<table width="98%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td align="center">
							<html-el:submit styleId="admin.shutdown.button.shutdown" property="submitButton" styleClass="buttn">
								<mifos:mifoslabel name="admin.shutdown.button.submit" bundle="adminUIResources"/>
							</html-el:submit>
							&nbsp;
								<html-el:button styleId="admin.shutdown.button.cancel" property="cancelButton" onclick="location.href='shutdownAction.do?method=cancelShutdown'" styleClass="cancelbuttn">
								<mifos:mifoslabel name="admin.shutdown.button.cancel" bundle="adminUIResources"/>
							</html-el:button>
						</td>
					</tr>
				</table>
			</td>
		</html-el:form>
	</tiles:put>
</tiles:insert>
