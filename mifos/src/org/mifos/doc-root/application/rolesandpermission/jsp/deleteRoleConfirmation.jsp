<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<tiles:insert definition=".view">
	<tiles:put name="body" type="string">

		<script language="javascript"
			src="pages/application/rolesandpermission/js/checkBoxLogic.js">
</script>
		<html-el:form action="/manageRolesAndPermission.do">

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"><html-el:link
						href="javascript:submitAdminLink()">
						<mifos:mifoslabel name="roleandpermission.labelAdmin"
							bundle="RolesAndPermissionResources"></mifos:mifoslabel>
					</html-el:link> / </span><span class="fontnormal8pt"><html-el:link
						href="javascript:submitRolesAndPermissionLink()">
						<mifos:mifoslabel name="roleandpermission.labelRoleAndPermission"
							bundle="RolesAndPermissionResources"></mifos:mifoslabel>
					</html-el:link></span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">

						<tr>
							
							<td width="50%" height="23" class="heading"><c:out
								value="${requestScope.Role.name}" /> - <span
								class="headingorange"><mifos:mifoslabel
								name="roleandpermission.labelDeleteRole"
								bundle="RolesAndPermissionResources"></mifos:mifoslabel> <br>
							</span><span class="fontnormal"><mifos:mifoslabel
								name="roleandpermission.labelDeleteRoleInstruction"
								bundle="RolesAndPermissionResources"></mifos:mifoslabel></span></td>
						</tr>
												<tr>
							<td><font class="fontnormalRedBold"><html-el:errors
								bundle="RolesAndPermissionResources" /> </font></td>
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
							<td align="center"><html-el:submit 
								styleClass="buttn" style="width:70px;">
								<mifos:mifoslabel name="roleandpermission.button.submit"
									bundle="RolesAndPermissionResources" />


							</html-el:submit> &nbsp; <html-el:button
								onclick=" goToCancelPage()"  property="SS"
								styleClass="cancelbuttn" style="width:70px;">

								<mifos:mifoslabel name="roleandpermission.button.cancel"
									bundle="RolesAndPermissionResources" />
							</html-el:button></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			<br>
			<html-el:hidden property="method" value="delete" />
			<html-el:hidden property="name" value="${requestScope.Role.name}" />
			<html-el:hidden property="id" value="${requestScope.Role.id}" />
		</html-el:form>
	</tiles:put>

</tiles:insert>
