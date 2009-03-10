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
        <input type="hidden" id="page.id" value="SysInfo"/>
		<html-el:form action="systemInfoAction.do?method=load">
			<td align="left" valign="top" bgcolor="#FFFFFF"
				style="padding-left:8px; padding-top:10px;">
				<table width="95%">
					<tr>
						<td colspan="2"><span class="headingorange"><mifos:mifoslabel name="admin.sysinfo.title" bundle="adminUIResources" /></span><br />
							<span class="fontnormal"><mifos:mifoslabel name="admin.sysinfo.welcome" bundle="adminUIResources" /></span></td>
					</tr>
					<tr><td colspan="2">&nbsp;</td></tr>
					<tr>
						<td><span class="fontnormal"><mifos:mifoslabel name="admin.sysinfo.operatingSystem" bundle="adminUIResources" /></span></td>
						<td><span class="fontnormal" id="sysinfo.text.operationSystem"><c:out value="${sessionScope.systemInfo.osName} / ${sessionScope.systemInfo.osArch} / ${sessionScope.systemInfo.osVersion}"/></span></td>
					</tr>
					<tr>
						<td><span class="fontnormal"><mifos:mifoslabel name="admin.sysinfo.java" bundle="adminUIResources" /></span></td>
						<td><span class="fontnormal" id="sysinfo.text.javaVendor"><c:out value="${sessionScope.systemInfo.javaVendor} / ${sessionScope.systemInfo.javaVersion}"/></span></td>
					</tr>
					<tr>
						<td><span class="fontnormal"><mifos:mifoslabel name="admin.sysinfo.database" bundle="adminUIResources" /></span></td>
						<td><span class="fontnormal" id="sysinfo.text.databaseVendor"><c:out value="${sessionScope.systemInfo.databaseVendor} / ${sessionScope.systemInfo.databaseVersion}"/></span></td>
					</tr>
					<tr>
						<td><span class="fontnormal"><mifos:mifoslabel name="admin.sysinfo.driver" bundle="adminUIResources" /></span></td>
						<td><span class="fontnormal" id="sysinfo.text.driverName"><c:out value="${sessionScope.systemInfo.driverName} / ${sessionScope.systemInfo.driverVersion}"/></span></td>
					</tr>
					<tr>
						<td><span class="fontnormal"><mifos:mifoslabel name="admin.sysinfo.appserver" bundle="adminUIResources" /></span></td>
						<td><span class="fontnormal" id="sysinfo.text.applicationServerInfo"><c:out value="${sessionScope.systemInfo.applicationServerInfo}"/></span></td>
					</tr>
					<tr>
						<td><span class="fontnormal"><mifos:mifoslabel name="admin.sysinfo.applicationversion" bundle="adminUIResources" /></span></td>
						<td><span class="fontnormal" id="sysinfo.text.applicationVersion"><c:out value="${sessionScope.systemInfo.applicationVersion}"/></span></td>
					</tr>
					<tr>
						<td><span class="fontnormal"><mifos:mifoslabel name="admin.sysinfo.svnrevision" bundle="adminUIResources" /></span></td>
						<td><span class="fontnormal" id="sysinfo.text.svnRevision"><c:out value="${sessionScope.systemInfo.svnBranch}"/>@<c:out value="${sessionScope.systemInfo.svnRevision}"/></span></td>
					</tr>
					<tr>
						<td><span class="fontnormal"><mifos:mifoslabel name="admin.sysinfo.customreportsdir" bundle="adminUIResources" /></span></td>
						<td><span class="fontnormal" id="sysinfo.text.customReportsDir"><c:out value="${sessionScope.systemInfo.customReportsDir}"/></span></td>
					</tr>
					<tr>
						<td><span class="fontnormal"><mifos:mifoslabel name="admin.sysinfo.source" bundle="adminUIResources" /></span></td>
						<td><span class="fontnormal" id="sysinfo.text.infoSource"><c:out value="${sessionScope.systemInfo.infoSource}"/></span></td>
					</tr>
					<tr>
						<td><span class="fontnormal"><mifos:mifoslabel name="admin.sysinfo.server" bundle="adminUIResources" /></span></td>
						<td><span class="fontnormal" id="sysinfo.text.datebaseServer"><c:out value="${sessionScope.systemInfo.databaseServer}"/></span></td>
					</tr>
					<tr>
						<td><span class="fontnormal"><mifos:mifoslabel name="admin.sysinfo.port" bundle="adminUIResources" /></span></td>
						<td><span class="fontnormal" id="sysinfo.text.databasePort"><c:out value="${sessionScope.systemInfo.databasePort}"/></span></td>
					</tr>
					<tr>
						<td><span class="fontnormal"><mifos:mifoslabel name="admin.sysinfo.name" bundle="adminUIResources" /></span></td>
						<td><span class="fontnormal" id="sysinfo.text.datebaseName"><c:out value="${sessionScope.systemInfo.databaseName}"/></span></td>
					</tr>
                    <tr>
                        <td><span class="fontnormal"><mifos:mifoslabel name="admin.sysinfo.user" bundle="adminUIResources" /></span></td>
                        <td><span class="fontnormal" id="sysinfo.text.databaseUser"><c:out value="${sessionScope.systemInfo.databaseUser}"/></span></td>
                    </tr>
                    <tr>
                        <td><span class="fontnormal"><mifos:mifoslabel name="admin.sysinfo.datetime" bundle="adminUIResources" /></span></td>
                        <td><span class="fontnormal" id="sysinfo.text.dateTime"><c:out value="${sessionScope.systemInfo.dateTimeString}"/></span></td>
                    </tr>
				</table>
			</td>
		</html-el:form>
	</tiles:put>
</tiles:insert>
