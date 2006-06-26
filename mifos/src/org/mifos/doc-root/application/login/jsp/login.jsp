<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html-el:html locale="true">
<head>
<title>Mifos</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="pages/framework/css/cssstyle.css" rel="stylesheet" type="text/css">
</head>
<body>
<html-el:form action="/mifoslogin.do" focus="userName">

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td width="188" rowspan="2"><IMG height=74 alt=""
				src="pages/framework/images/logo.gif" width=188></td>

			<td align="right" bgcolor="#ffffff" class="fontnormal">&nbsp;</td>
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
			<td height="480" align="left" valign="top" bgcolor="#ffffff">
			<table width="50%" border="0" align="center" cellpadding="0"
				cellspacing="0">
				<tr>
					<td align="center" class="blueline">&nbsp;</td>
				</tr>
			</table>

			<table width="50%" border="0" align="center" cellpadding="0"
				cellspacing="0" class="bluetableborder">
				<tr>
					<td colspan="2" align="left" valign="top"
						class="tableContentLightBlue"><span class="heading"> <mifos:mifoslabel
						name="login.login" bundle="LoginUIResources"/></span></td>
				</tr>
				<tr>
					<td width="49%" align="left" valign="top"
						style="BORDER-RIGHT: #d7deee 1px solid; PADDING-RIGHT: 10px; PADDING-LEFT: 10px; 
					PADDING-BOTTOM: 10px; PADDING-TOP: 10px"><span
						class="fontnormal"><mifos:mifoslabel name="login.welcome" bundle="LoginUIResources"/> <br>
					<br>
					<A href="#">Help</a></span><br>
					</td>

					<td width="51%" align="left" valign="top"
						style="PADDING-RIGHT: 10px; PADDING-LEFT: 10px; PADDING-BOTTOM: 10px; PADDING-TOP: 10px">
					<table width="100%" border="0" cellpadding="3" cellspacing="0">
					   <tr><td colspan="2">
						<font class="fontnormalRedBold"><html-el:errors
							bundle="LoginUIResources" /> </font></td></tr>
						<tr class="fontnormal">
							<td width="33%" align="right"><mifos:mifoslabel
								name="login.username" bundle="LoginUIResources"/>:</td>

							<td width="67%"><html-el:text property="userName" /></td>
						</tr>

						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel name="login.password" bundle="LoginUIResources"/>:</td>

							<td><html-el:password property="password" redisplay="false" /></td>
						</tr>
						<html-el:hidden property="method" value="login" />
						<html-el:hidden property="newPassword" value="mifoss" />
						<tr class="fontnormal">
							<td align="right"></td>

							<td><br>
							<html-el:submit style="WIDTH: 60px" styleClass="buttn">
								<mifos:mifoslabel name="login.login" bundle="LoginUIResources"/>
							</html-el:submit>
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
</html-el:form>
</body>
</html-el:html>
