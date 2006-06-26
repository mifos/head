<!-- 

/**

 * rolespermission.jap    version: 1.0

 

 * Copyright © 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */

-->
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<script language="javascript">
function goToCancelPage(){
	document.officeActionForm.method.value="cancel";
	officeActionForm.submit();
  }
    function  submitAdminLink()
	{
		document.rolesandPermissionActionForm.method.value="load";
		
		document.rolesandPermissionActionForm.action="AdminAction.do";
		document.rolesandPermissionActionForm.submit();
	}

 function  getRole(id,name)
 {
 			document.rolesandPermissionActionForm.name.value=name;
 			document.rolesandPermissionActionForm.id.value=id;
 			document.rolesandPermissionActionForm.method.value="get";
 			document.rolesandPermissionActionForm.action="manageRolesAndPermission.do";
 			document.rolesandPermissionActionForm.submit();
 			
 }	
 
 function  deleteRole(id,name)
 {
 			document.rolesandPermissionActionForm.name.value=name;
 			document.rolesandPermissionActionForm.id.value=id;
 			document.rolesandPermissionActionForm.method.value="preview";
 			document.rolesandPermissionActionForm.action="manageRolesAndPermission.do";
 			document.rolesandPermissionActionForm.submit();
 			
 }	
	
</script>

		<html-el:form action="/manageRolesAndPermission.do?method=manage">

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <html-el:link
						href="javascript:submitAdminLink()">


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
							<span class="fontnormal"> <html-el:link
								action="/manageRolesAndPermission.do?method=load&name=a">
								<mifos:mifoslabel name="roleandpermission.labelNewRole"
									bundle="RolesAndPermissionResources"></mifos:mifoslabel>
							</html-el:link>
							</td>
						</tr>
					</table>
					
					<font class="fontnormalRedBold"><html-el:errors
						bundle="RolesAndPermissionResources" /> </font>
						<br>
					<c:if test="${not empty requestScope.Roles}">
						<table width="90%" border="0" cellpadding="3" cellspacing="0">
							<c:forEach var="item" items="${requestScope.Roles}">
								<tr>
									<td width="39%" class="blueline"><span class="fontnormalbold">
									<html-el:link
										href="javascript:getRole(${item.id},'${item.name}')">${item.name}</html-el:link>
									</span></td>
									<td width="61%" class="blueline"><span class="fontnormal"> <html-el:link
										href="javascript:deleteRole(${item.id},'${item.name}')">
										<mifos:mifoslabel name="roleandpermission.labelDeleteRole"
											bundle="RolesAndPermissionResources"></mifos:mifoslabel>
									</html-el:link> </span></td>
								</tr>
							</c:forEach>
						</table>
					</c:if>
					
					</td>
				</tr>
			</table>
			<br>

			<html-el:hidden property="name" value="hiu" />
			<html-el:hidden property="method" value="manage" />
			<html-el:hidden property="id" />
		</html-el:form>
	</tiles:put>

</tiles:insert>
