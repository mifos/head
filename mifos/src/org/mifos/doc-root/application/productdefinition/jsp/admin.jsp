<!-- 

/**

 * admin.jsp    version: 1.0

 

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

 */

-->

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Mifos</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="pages/framework/css/cssstyle.css" rel="stylesheet"
	type="text/css">
</head>

<body>
<form name="form1" method="post" action="">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td width="188" rowspan="2"><img src="pages/framework/images/logo.gif"
			width="188" height="74"></td>
		<td align="right" bgcolor="#FFFFFF" class="fontnormal"><a
			href="yourSettings.htm">Your Settings</a> &nbsp;|&nbsp; <a
			href="login.htm">Logout</a> &nbsp;|&nbsp; <a href="#">Help</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>
	<tr>
		<td align="left" valign="bottom" bgcolor="#FFFFFF">
		<table border="0" cellspacing="1" cellpadding="0">
			<tr>
				<td class="tablightorange"><a href="index.htm">Home</a></td>
				<td class="tablightorange"><a href="clientsaccounts.htm">Clients
				&amp; Accounts </a></td>
				<td class="tablightorange"><a href="reports.htm">Reports</a></td>
				<td class="taborange"><a href="admin.htm" class="tabfontwhite">Admin</a></td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td colspan="2" class="bgorange"><img
			src="pages/framework/images/trans.gif" width="6" height="6"></td>
	</tr>
	<tr>
		<td colspan="2" class="bgwhite"><img
			src="pages/framework/images/trans.gif" width="100" height="2"></td>
	</tr>
</table>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td width="174" height="350" align="left" valign="top"
			class="bgorangeleft">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td class="leftpanehead">Administrative Tasks</td>
			</tr>
			<tr>
				<td class="leftpanelinks">
				<table width="90%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="paddingbottom03"><span class="fontnormal8ptbold">Search
						by client name, system ID or account number </span></td>
					</tr>
				</table>
				<table width="90%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td width="100%" colspan="2"><input name="textfield" type="text"
							size="20"></td>
					</tr>
				</table>
				<table width="143" border="0" cellspacing="0" cellpadding="10">
					<tr>
						<td align="right"><input type="button" name="Button33"
							value="Search" onClick="location.href='searchResult.htm';"
							class="buttn" style="width:60px;"></td>
					</tr>
				</table>
				<span class="fontnormalbold"> </span></td>
			</tr>
		</table>
		</td>
		<td align="left" valign="top" bgcolor="#FFFFFF" class="paddingL30T15">
		<table width="90%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td colspan="2" align="left" valign="top"><span
					class="headingorange">Administrative Tasks</span><br>
				<span class="fontnormal">Welcome to mifos administrative area. Click
				on a link below to begin.</span></td>
			</tr>
			<tr>
				<td width="48%" height="240" align="left" valign="top"
					class="paddingleft"><span class="headingorange">Manage organization</span><br>
				<span class="fontnormalbold"> <span class="fontnormalbold">System
				users</span><br>
				</span>
				<table width="90%" border="0" cellspacing="0" cellpadding="0">
					<tr class="fontnormal">
						<td width="3%"><img src="pages/framework/images/bullet_circle.gif"
							width="9" height="11"></td>
						<td width="97%"><a href="admin/viewEditUsers.htm">View system
						users</a> | <a href="admin/chooseOffice.htm">Define new system
						user</a></td>
					</tr>
					<tr class="fontnormal">
						<td><img src="pages/framework/images/bullet_circle.gif" width="9"
							height="11"></td>
						<td><a href="admin/rolespermissions.htm">Manage roles and
						permissions</a></td>
					</tr>
				</table>
				<br>
				<span class="fontnormalbold"> Offices</span><br>
				<table width="90%" border="0" cellspacing="0" cellpadding="0">
					<tr class="fontnormal">
						<td width="3%"><img src="pages/framework/images/bullet_circle.gif"
							width="9" height="11"></td>
						<td width="97%"><a href="admin/viewEditOffices.htm">View offices</a>
						| <a href="admin/CreateOffice.htm">Define a new office</a></td>
					</tr>
					<tr class="fontnormal">
						<td><img src="pages/framework/images/bullet_circle.gif" width="9"
							height="11"></td>
						<td><a href="admin/OfficeHierarchy.htm">View office hierarchy</a></td>
					</tr>
				</table>
				<br>
				<span class="fontnormalbold">Calendar / Accounting</span><br>
				<table width="90%" border="0" cellspacing="0" cellpadding="0">
					<tr class="fontnormal">
						<td width="3%"><img src="pages/framework/images/bullet_circle.gif"
							width="9" height="11"></td>
						<td colspan="2"><a href="admin/office_holidays.htm">View holidays</a></td>
					</tr>
					<tr class="fontnormal">
						<td><img src="pages/framework/images/bullet_circle.gif" width="9"
							height="11"></td>
						<td colspan="2"><a href="#">Define chart of accounts</a></td>
					</tr>
					<tr class="fontnormal">
						<td><img src="pages/framework/images/bullet_circle.gif" width="9"
							height="11"></td>
						<td colspan="2"><a href="admin/ViewSettings.htm">View Settings</a></td>
					</tr>
					<tr class="fontnormal">
						<td>&nbsp;</td>
						<td width="2%"><img src="pages/framework/images/bullet_circle.gif"
							width="9" height="11"></td>
						<td width="95%"><a href="admin/DefineWorkingdays.htm">Define
						calendar rules and fiscal year</a></td>
					</tr>
					<tr class="fontnormal">
						<td>&nbsp;</td>
						<td><img src="pages/framework/images/bullet_circle.gif" width="9"
							height="11"></td>
						<td><a href="admin/DefineFiscalYear.htm">Define currency rules</a></td>
					</tr>
				</table>
				<br>
				<span class="fontnormalbold"> Organization Preferences</span><br>
				<table width="90%" border="0" cellspacing="0" cellpadding="0">
					<tr class="fontnormal">
						<td width="3%"><img src="pages/framework/images/bullet_circle.gif"
							width="9" height="11"></td>
						<td width="97%"><a href="admin/ViewEditFees.htm">View Fees</a> | <a
							href="admin/createFees.htm">Define new fees</a></td>
					</tr>
					<tr class="fontnormal">
						<td><img src="pages/framework/images/bullet_circle.gif" width="9"
							height="11"></td>
						<td><a href="#">View funds</a> | <a href="#">Define new fund</a></td>
					</tr>
					<tr class="fontnormal">
						<td><img src="pages/framework/images/bullet_circle.gif" width="9"
							height="11"></td>
						<td><a href="mifospmttypesaction.do?method=load">View accepted
						payment types</a></td>
					</tr>
					<tr class="fontnormal">
						<td><img src="pages/framework/images/bullet_circle.gif" width="9"
							height="11"></td>
						<td><a href="admin/ChooseCategory.htm">Define additional data
						fields</a></td>
					</tr>
					<tr class="fontnormal">
						<td><img src="pages/framework/images/bullet_circle.gif" width="9"
							height="11"></td>
						<td><a href="mifosprocessflowaction.do?method=load">Define process
						flow </a></td>
					</tr>
				</table>
				<br>
				<span class="fontnormalbold">Display</span><br>
				<table width="90%" border="0" cellspacing="0" cellpadding="0">
					<tr class="fontnormal">
						<td width="3%"><img src="pages/framework/images/bullet_circle.gif"
							width="9" height="11"></td>
						<td width="97%"><a href="#">Set display sequence of names</a></td>
					</tr>
					<tr class="fontnormal">
						<td><img src="pages/framework/images/bullet_circle.gif" width="9"
							height="11"></td>
						<td><a href="admin/DefineLabels.htm">Define labels</a></td>
					</tr>
					<tr class="fontnormal">
						<td><img src="pages/framework/images/bullet_circle.gif" width="9"
							height="11"></td>
						<td><a href="admin/DefineLookupOptions.htm">Define look-up options</a></td>
					</tr>
					<tr class="fontnormal">
						<td><img src="pages/framework/images/bullet_circle.gif" width="9"
							height="11"></td>
						<td><a href="admin/ViewEditChecklists.htm">View checklists</a> | <a
							href="admin/addNewChecklists.htm">Define new checklist</a></td>
					</tr>
				</table>
				<br>
				<span class="fontnormalbold">Miscellaneous</span><br>
				<table width="90%" border="0" cellspacing="0" cellpadding="0">
					<tr class="fontnormal">
						<td width="3%"><img src="pages/framework/images/bullet_circle.gif"
							width="9" height="11"></td>
						<td width="97%"><a href="#">Collection sheets</a></td>
					</tr>
					<tr class="fontnormal">
						<td><img src="pages/framework/images/bullet_circle.gif" width="9"
							height="11"></td>
						<td><a href="#">Define session timeout rules</a></td>
					</tr>
				</table>
				</td>
				<td width="52%" align="left" valign="top" class="paddingleft"><span
					class="headingorange"><span class="headingorange">Manage client
				attributes</span><br>
				<span class="fontnormalbold">Client rules &amp; attributes</span><br>
				</span>
				<table width="90%" border="0" cellspacing="0" cellpadding="0">
					<tr class="fontnormal">
						<td width="3%"><img src="pages/framework/images/bullet_circle.gif"
							width="9" height="11"></td>
						<td width="97%"><a href="#">View programs</a> | <a href="#">Define
						new program </a></td>
					</tr>
					<tr class="fontnormal">
						<td><img src="pages/framework/images/bullet_circle.gif" width="9"
							height="11"></td>
						<td><a href="admin/DefineClientHierarchy.htm">Define client rules</a></td>
					</tr>
					<tr class="fontnormal">
						<td><img src="pages/framework/images/bullet_circle.gif" width="9"
							height="11"></td>
						<td><a href="admin/editClientFees.htm">View client fees</a></td>
					</tr>
					<tr class="fontnormal">
						<td><img src="pages/framework/images/bullet_circle.gif" width="9"
							height="11"></td>
						<td><a href="admin/DefineRequiredDataFields.htm">Define required
						data fields</a></td>
					</tr>
				</table>
				<br>
				<span class="headingorange">Manage products</span><br>
				<span class="fontnormalbold">Product rules &amp; attributes</span><br>
				<table width="90%" border="0" cellspacing="0" cellpadding="0">
					<tr class="fontnormal">
						<td width="3%"><img src="pages/framework/images/bullet_circle.gif"
							width="9" height="11"></td>
						<td width="97%"><a
							href="mifosproddefaction.do?method=search&searchNode(search_name)=ProductCategories">View
						product categories</a> | <a
							href="mifosproddefaction.do?method=load">Define new category</a></td>
					</tr>
					<tr class="fontnormal">
						<td><img src="pages/framework/images/bullet_circle.gif" width="9"
							height="11"></td>
						<td><a
							href="prdconfigurationaction.do?method=search&searchNode(search_name)=ProductTypes">View
						lateness/dormancy definition / penalty calculation</a></td>
					</tr>
				</table>
				<br>
				<span class="fontnormalbold">Manage Loan Products</span><br>
				<table width="90%" border="0" cellspacing="0" cellpadding="0">
					<tr class="fontnormal">
						<td width="3%"><img src="pages/framework/images/bullet_circle.gif"
							width="9" height="11"></td>
						<td width="97%"><a
							href="loanprdaction.do?method=search&searchNode(search_name)=LoanProducts">View
						loan products</a> | <a href="loanprdaction.do?method=load">Define
						new loan product</a></td>
					</tr>
				</table>
				<br>
				<span class="fontnormalbold">Manage Savings Products</span><br>
				<table width="90%" border="0" cellspacing="0" cellpadding="0">
					<tr class="fontnormal">
						<td width="3%"><img src="pages/framework/images/bullet_circle.gif"
							width="9" height="11"></td>
						<td width="97%"><a
							href="savingsprdaction.do?method=search&searchNode(search_name)=SavingsProducts">View
						savings products</a> | <a href="savingsprdaction.do?method=load">Define
						new savings product</a></td>
					</tr>
				</table>
				<br>
				<span class="fontnormalbold">Manage Insurance Products</span><br>
				<table width="90%" border="0" cellspacing="0" cellpadding="0">
					<tr class="fontnormal">
						<td width="3%"><img src="pages/framework/images/bullet_circle.gif"
							width="9" height="11"></td>
						<td width="97%"><a href="admin/ViewEditInsuranceProduct.htm">View
						insurance products</a> | <a
							href="admin/CreateInsuranceProduct.htm">Define new insurance
						product</a></td>
					</tr>
				</table>
				<br>
				<span class="fontnormalbold">Manage Group Funded Loans</span><br>
				<table width="90%" border="0" cellspacing="0" cellpadding="0">
					<tr class="fontnormal">
						<td width="3%" valign="top"><img
							src="pages/framework/images/bullet_circle.gif" width="9"
							height="11"></td>
						<td width="97%"><a href="#">View group funded loan products</a> |
						<a href="#">Define new group funded loan product</a></td>
					</tr>
				</table>
				<br>
				<span class="headingorange">Surveys</span><br>
				<table width="90%" border="0" cellspacing="0" cellpadding="0">
					<tr class="fontnormal">
						<td width="3%"><img src="pages/framework/images/bullet_circle.gif"
							width="9" height="11"></td>
						<td width="97%"><a href="#">View surveys</a> | <a href="#">Define
						a new survey</a></td>
					</tr>
					<tr class="fontnormal">
						<td><img src="pages/framework/images/bullet_circle.gif" width="9"
							height="11"></td>
						<td><a href="#">View questions</a> | <a href="#">Define new
						questions</a></td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		<br>
		</td>
	</tr>
</table>
<br>
</form>
</body>
</html>
