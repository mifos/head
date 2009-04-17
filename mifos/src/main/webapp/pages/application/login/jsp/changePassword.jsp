<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<html-el:html locale="true">
<head>
<title>Mifos</title>
<script language="javascript">
<!--
	function fnCancel() {
		loginActionForm.action="loginAction.do?method=cancel";
		loginActionForm.submit();
	}
	function fnLogout() {
		location.href="loginAction.do?method=logout";
	}
//-->
</script>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="pages/framework/css/cssstyle.css" rel="stylesheet" type="text/css">
</head>

<body>
<html-el:form action="/loginAction.do" focus="oldPassword">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td width="188" rowspan="2"><img src="pages/framework/images/logo.gif" width="188"
				height="74"></td>
			<td align="right" bgcolor="#FFFFFF" class="fontnormal"><html-el:link styleId="changePassword.link.logout"
				href="javascript:fnLogout()"><mifos:mifoslabel
				name="login.logout" bundle="LoginUIResources"/></html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		</tr>
		<tr>
			<td align="left" valign="bottom" bgcolor="#ffffff">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="2" class="bgorange"><IMG height=6
				src="pages/framework/images/trans.gif" width=6></td>
		</tr>

		<tr>
			<td colspan="2" class="bgwhite"><IMG height=2
				src="pages/framework/images/trans.gif" width=100></td>
		</tr>
	</table>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="350" align="left" valign="top" bgcolor="#FFFFFF">
			<table width="590" border="0" align="center" cellpadding="0"
				cellspacing="0">
				<tr>
					<td align="center" class="blueline">&nbsp;</td>
				</tr>
			</table>

			<table width="590" border="0" align="center" cellpadding="0"
				cellspacing="0" class="bluetableborder">
				<tr>
					<td align="left" valign="top" class="paddingleftCreates">
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange"><mifos:mifoslabel
								name="login.changepassword" bundle="LoginUIResources" /> </td>
		</tr>
		<tr>
			<td class="fontnormal"><mifos:mifoslabel
				name="login.changepassword.text" bundle="LoginUIResources"/></td>
		</tr>
	</table>
	<br>
	<font class="fontnormalRedBold"><span id="changePassword.error.message"><html-el:errors bundle="LoginUIResources" /></span></font>
	<table width="93%" border="0" cellpadding="3" cellspacing="0">
		<tr class="fontnormal">
			<td align="right"><span id="changePassword.label.oldPassword"><mifos:mifoslabel name="login.oldpassword" bundle="LoginUIResources"/></span> </td>
			<td><html-el:password styleId="changePassword.input.oldPassword" property="oldPassword" style="width:136px;"
				redisplay="false" /></td>
		</tr>
		<tr class="fontnormal">
			<td width="32%" align="right"><span class="mandatorytext"></span> <span id="changePassword.label.newPassword"><mifos:mifoslabel
				name="login.newpassword" bundle="LoginUIResources"/></span>
			</td>
			<td width="68%"><html-el:password styleId="changePassword.input.newPassword" property="newPassword"
				style="width:136px;" redisplay="false" /></td>
		</tr>
		<tr class="fontnormal">
			<td align="right"><span class="mandatorytext"></span> <span id="changePassword.label.confirmPassword"><mifos:mifoslabel
				name="login.confirmpassword" bundle="LoginUIResources"/></span> </td>
			<td><html-el:password styleId="changePassword.input.confirmPassword" property="confirmPassword" style="width:136px;"
				redisplay="false" /></td>
		</tr>
		<html-el:hidden property="input" value="LoginChangePW" />
		<html-el:hidden property="method" value="updatePassword" />
		<html-el:hidden property="userId"
			value="${sessionScope.UserContext.id}" />
		<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
	</table>
	<table width="93%" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td align="center" class="blueline">&nbsp;</td>
		</tr>
	</table>
	<br>
	<table width="93%" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td align="center"> <html-el:submit styleId="changePassword.button.submit"
				styleClass="buttn">
				<mifos:mifoslabel name="login.submit" bundle="LoginUIResources"/>
			</html-el:submit>&nbsp; <html-el:button styleId="changePassword.button.cancel" property="cancel"
				styleClass="cancelbuttn" onclick="fnCancel()">
				<mifos:mifoslabel name="login.cancel" bundle="LoginUIResources"/>
			</html-el:button></td>
		</tr>
	</table>
	<br>
	<br>
	</td>
	</tr>
	</table>
	<br>
	</td>
	</tr>
	</table>
</html-el:form>
</body>
</html-el:html>
