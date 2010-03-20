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
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<tr>
	<td class="leftpanelinks">
	<table width="90%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="paddingbottom03"><span class="fontnormal8ptbold"><mifos:mifoslabel name="framework.searchCriteria" bundle="FrameworkUIResources"></mifos:mifoslabel> </span></td>
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
			<td align="right"><input type="button" name="Button33" value="Search"
				onClick="" class="buttn"></td>
		</tr>
	</table>


	<br>
	<span class="fontnormalbold"><mifos:mifoslabel name="framework.manageCollectionSheets" bundle="FrameworkUIResources"></mifos:mifoslabel></span><br>
	<a href="#"><mifos:mifoslabel name="framework.enterCollectionSheetData" bundle="FrameworkUIResources"></mifos:mifoslabel></a><br>
	<br>
	<span class="fontnormalbold"><mifos:mifoslabel name="framework.createNewClients" bundle="FrameworkUIResources"></mifos:mifoslabel></span><br>
	<a href="CreateCenter_branch.htm"><mifos:mifoslabel name="framework.createNewCenter" bundle="FrameworkUIResources"></mifos:mifoslabel></a><br>
	<a href="CreateGroupSearch.htm"><mifos:mifoslabel name="framework.createNewGroup" bundle="FrameworkUIResources"></mifos:mifoslabel></a><br>
	<a href="CreateClientSearch.htm"><mifos:mifoslabel name="framework.createNewClient" bundle="FrameworkUIResources"></mifos:mifoslabel></a><br>
	<br>
	<span class="fontnormalbold"><mifos:mifoslabel name="framework.createNewAccounts" bundle="FrameworkUIResources"></mifos:mifoslabel></span><br>
	<a href="CreateSavingsAccountSearch.htm"><mifos:mifoslabel name="framework.createSavingsAccount" bundle="FrameworkUIResources"></mifos:mifoslabel></a><br>

	<a href="CreateLoanAccountSearch.htm"><mifos:mifoslabel name="framework.createLoanAccount" bundle="FrameworkUIResources"></mifos:mifoslabel></a></td>
</tr>
