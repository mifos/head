<%--
Copyright (c) 2005-2008 Grameen Foundation USA
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

<%@ page contentType="text/html; charset=utf-8"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<span id="page.id"
			title="import.transactions.confirm" />
		<table width="98%" border="0" cellspacing="0" cellpadding="3">
			<tr>
				<td class="headingorange"><span
					id="importexport.label.importsuccess"> <mifos:mifoslabel
					name="admin.importexport.successfully_imported"
					bundle="adminUIResources" /> </span> <br>
				<br>
				</td>
			</tr>
			<tr>
				<td class="fontnormalbold"><mifos:mifoslabel
					name="configuration.please_note" isColonRequired="Yes" /> <span
					class="fontnormal"> <c:out
					value="${importTransactionsForm.importTransactionsFile.fileName}" /> <mifos:mifoslabel
					name="admin.importexport.has_been_imported"
					bundle="adminUIResources" /><br>
				<br>
				<font class="fontnormalRedBold"> <html-el:messages
					id="import.confirm.message" bundle="adminUIResources" /> <html-el:errors
					bundle="adminUIResources" /> </font> </span> <html-el:link styleId="import_transactions_confirmation.link.importTransactions"
					href="manageImportAction.do?method=load">
					<mifos:mifoslabel name="admin.importexport.importtransactions"
						bundle="adminUIResources" />
				</html-el:link> <span class="fontnormal"><br>
				<br>
				</span></td>
			</tr>
		</table>
		<br>
	</tiles:put>
</tiles:insert>
