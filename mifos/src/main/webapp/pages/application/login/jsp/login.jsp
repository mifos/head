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
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html-el:html locale="true">
<head>
<title>Mifos</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="pages/framework/css/cssstyle.css" rel="stylesheet" type="text/css">
</head>
<body>
<html-el:form action="/loginAction.do" focus="userName">

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
						class="tableContentLightBlue"><span class="heading" id="login.label.heading"> <mifos:mifoslabel
						name="login.login" bundle="LoginUIResources"/></span></td>
				</tr>
				<tr>
					<td width="45%" align="left" valign="top"
						style="BORDER-RIGHT: #d7deee 1px solid; PADDING-RIGHT: 10px; PADDING-LEFT: 10px;
					PADDING-BOTTOM: 10px; PADDING-TOP: 10px"><span
						class="fontnormal" id="login.label.welcome"><mifos:mifoslabel name="login.welcome" bundle="LoginUIResources"/> <br>
					<br>
					</span><br>
					</td>

					<td width="55%" align="left" valign="top"
						style="PADDING-RIGHT: 10px; PADDING-LEFT: 10px; PADDING-BOTTOM: 10px; PADDING-TOP: 10px">
					<table width="100%" border="0" cellpadding="3" cellspacing="0">
					   <tr><td colspan="2">
						<font class="fontnormalRedBold"><span id="login.error.message"><html-el:errors
							bundle="LoginUIResources" /></span></font></td></tr>
						<tr class="fontnormal">
							<td width="33%" align="right"><span id="login.label.username"><mifos:mifoslabel
                     name="login.username" bundle="LoginUIResources"/></span>:</td>

							<td width="67%"><html-el:text styleId="login.input.username" property="userName" /></td>
						</tr>

						<tr class="fontnormal">
							<td align="right">
                        <span id="login.label.password"><mifos:mifoslabel name="login.password" bundle="LoginUIResources"/></span>:</td>
							<td><html-el:password styleId="login.input.password" property="password" redisplay="false" /></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"></td>

							<td><br>
							<html-el:submit styleClass="buttn" styleId="login.button.login">
								<mifos:mifoslabel name="login.login" bundle="LoginUIResources"/>
							</html-el:submit>
							<html-el:hidden property="method" value="login" />
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
