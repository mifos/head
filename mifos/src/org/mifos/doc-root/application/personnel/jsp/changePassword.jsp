<!-- /**
 
 * changePassword.jsp    version: 1.0
 
 
 
 * Copyright (c) 2005-2006 Grameen Foundation USA
 
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
 
 */-->
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>


<tiles:insert definition=".noorangetab">
 <tiles:put name="body" type="string">
<script language="javascript">
<!--
	function fnCancel(form) {
		form.action="PersonnelAction.do?method=getDetails";
		form.submit();
	}
	function fnLogout() {
		location.href="mifoslogout.do?method=logout";
	}
//-->
</script>
<html-el:form action="/mifoslogin.do" focus="oldPassword">
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
	<font class="fontnormalRedBold"><html-el:errors bundle="LoginUIResources" /></font>
	<table width="93%" border="0" cellpadding="3" cellspacing="0">
		<tr class="fontnormal">
			<td align="right"><mifos:mifoslabel name="login.oldpassword" bundle="LoginUIResources"/> </td>
			<td><html-el:password property="oldPassword" style="width:136px;"
				redisplay="false" /></td>
		</tr>
		<tr class="fontnormal">
			<td width="32%" align="right"><span class="mandatorytext"></span> <mifos:mifoslabel
				name="login.newpassword" bundle="LoginUIResources"/>
			</td>
			<td width="68%"><html-el:password property="newPassword"
				style="width:136px;" redisplay="false" /></td>
		</tr>
		<tr class="fontnormal">
			<td align="right"><span class="mandatorytext"></span> <mifos:mifoslabel
				name="login.confirmpassword" bundle="LoginUIResources"/> </td>
			<td><html-el:password property="confirmPassword" style="width:136px;"
				redisplay="false" /></td>
		</tr>
		<html-el:hidden property="method" value="update" />
		<html-el:hidden property="input" value="SettingsChangePW" />
		<html-el:hidden property="userId"
			value="${sessionScope.UserContext.id}" />
		<html-el:hidden property="userName" value="${sessionScope.UserContext.name}"/>
	</table>
	<table width="93%" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td align="center" class="blueline">&nbsp;</td>
		</tr>
	</table>
	<br>
	<table width="93%" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td align="center"> <html-el:submit
				style="width:70px" styleClass="buttn">
				<mifos:mifoslabel name="login.submit" bundle="LoginUIResources"/>
			</html-el:submit>&nbsp; <html-el:button property="cancel"
				styleClass="cancelbuttn" style="width:70px"
				onclick="fnCancel(mifosloginform)">
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
</tiles:put>
</tiles:insert>
