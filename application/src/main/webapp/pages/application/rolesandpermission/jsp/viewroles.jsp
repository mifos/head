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
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
	<span id="page.id" title="viewroles" />
		<html-el:form action="/rolesPermission.do" >

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <html-el:link
						href="rolesPermission.do?method=cancel&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">


						<mifos:mifoslabel name="roleandpermission.labelAdmin"
							bundle="RolesAndPermissionResources"></mifos:mifoslabel>


					</html-el:link> /</span><span class="fontnormal8ptbold"> <mifos:mifoslabel
						name="roleandpermission.labelRoleAndPermission"
						bundle="RolesAndPermissionResources"></mifos:mifoslabel></span></td>
				</tr>
			</table>


			<table width="95%" border="0" cellpadding="0" cellspacing="0">

				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="90%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="50%" height="23" class="headingorange"><mifos:mifoslabel
								name="roleandpermission.labelRoleAndPermission"
								bundle="RolesAndPermissionResources"></mifos:mifoslabel> <br>
							<span class="fontnormal"><mifos:mifoslabel
								name="roleandpermission.labelAddNewRoleInstruction"
								bundle="RolesAndPermissionResources"></mifos:mifoslabel> </span>
							<span class="fontnormal"> <html-el:link href="rolesPermission.do?method=load&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="roleandpermission.labelNewRole"
									bundle="RolesAndPermissionResources"></mifos:mifoslabel>
							</html-el:link></td>
						</tr>
					</table>

					<font class="fontnormalRedBold"><html-el:errors
						bundle="RolesAndPermissionResources" /> </font> <br>
					<c:set  value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'Roles')}" var="Roles"/>	
					<c:if test="${not empty Roles}">
						<table width="90%" border="0" cellpadding="3" cellspacing="0">
							<c:forEach var="item" items="${Roles}">
								<tr>
									<td width="39%" class="blueline"><span class="fontnormalbold">
									<html-el:link
										href="rolesPermission.do?method=manage&id=${item.id}&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}"><c:out value="${item.name}"/></html-el:link>
									</span></td>
									<td width="61%" class="blueline"><span class="fontnormal"> <html-el:link
										href="rolesPermission.do?method=preview&id=${item.id}&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
										<mifos:mifoslabel name="roleandpermission.labelDeleteRole"
											bundle="RolesAndPermissionResources"></mifos:mifoslabel>
									</html-el:link> </span></td>
								</tr>
							</c:forEach>
						</table>
					</c:if></td>
				</tr>
			</table>
			<br>

			<html-el:hidden property="name" value="" />
			<html-el:hidden property="id" value="" />
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>

</tiles:insert>
