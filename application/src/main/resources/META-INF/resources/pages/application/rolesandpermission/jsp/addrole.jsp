<%-- 
Copyright (c) 2005-2011 Grameen Foundation USA
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
<%@taglib uri="/activity" prefix="activity"%>
<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
	<span id="page.id" title="addrole"></span>

<script language="javascript" src="pages/application/rolesandpermission/js/checkBoxLogic.js">
</script>
<script type="text/javascript" src="pages/js/separator.js"></script>	
<mifos:NumberFormattingInfo />
		<html-el:form action="/rolesPermission.do?method=create" focus="name">

		
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td class="bluetablehead05"><span class="fontnormal8pt">
							
												<html-el:link href="rolesPermission.do?method=cancel&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel
								name="roleandpermission.labelAdmin"
								bundle="RolesAndPermissionResources"></mifos:mifoslabel>
								</html-el:link> 
								 /<span
								class="fontnormal8ptbold"> </span><span class="fontnormal8pt">
								
								
								
								 <html-el:link href="rolesPermission.do?method=viewRoles&randomNUm=${sessionScope.randomNUm}"
								>
								
								
								 <mifos:mifoslabel
								name="roleandpermission.labelRoleAndPermission"
								bundle="RolesAndPermissionResources"></mifos:mifoslabel> 
								
								
								
								</html-el:link>

							 </span> </td>
						</tr>
					</table>
					
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
					
						<tr>
							<td align="left" valign="top" class="paddingL15T15">
							<table width="96%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td width="50%" height="23" class="heading"><span
										class="headingorange"><mifos:mifoslabel
								name="roleandpermission.labelAddNewRole"
								bundle="RolesAndPermissionResources"></mifos:mifoslabel></span></td>
								</tr>
							</table>
							<table width="95%" border="0" cellpadding="3" cellspacing="0">
													<tr>
						<td colspan="2">
						 	<font class="fontnormalRedBold"><html-el:errors
								bundle="RolesAndPermissionResources" /> </font>
						 
						</td>
						</tr>
							
								<tr class="fontnormal">
									<td width="11%"><mifos:mifoslabel
								name="roleandpermission.labelRoleName"
								bundle="RolesAndPermissionResources"></mifos:mifoslabel></td>
									<td width="89%">
									
									<!-- bug 23759 taking more than 50 character -->
									<mifos:mifosalphanumtext property="name" size="50" maxlength="50"
										style="width:136px;" /> 
									</td>
								</tr>
								<tr>
									<td colspan="2" class="fontnormalbold">&nbsp;</td>
								</tr>
							</table>

						<activity:getActivitiesTag/>
						 <br>
							<table width="95%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center"><html-el:submit  property="SS"
										styleClass="buttn submit"> 
										<mifos:mifoslabel name="roleandpermission.button.submit" bundle="RolesAndPermissionResources" />
										</html-el:submit>
										
										 &nbsp; <html-el:button
										onclick="goToCancelPage()" property="SS"
										styleClass="cancelbuttn">
										
										<mifos:mifoslabel name="roleandpermission.button.cancel" bundle="RolesAndPermissionResources" />
										</html-el:button>
										</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					<br>
					
				</tr>
			</table>
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>

	</tiles:put>

</tiles:insert>
