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
