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
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>


<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<span id="page.id" title="CreateMultipleLoanAccountsConfirm" />
		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td align="left" valign="top" class="paddingL15T15">
					<table width="98%" border="0" cellspacing="0" cellpadding="3">
						<tr>
							<td class="headingorange">
								<mifos:mifoslabel name="loan.succcreatenew" />
								<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
								<mifos:mifoslabel name="loan.accs" />
								<br>
								<br>
							</td>
						</tr>
						<font class="fontnormalRedBold"> <span id="CreateMultipleLoanAccountsConfirm.error.message"> <html-el:errors bundle="loanUIResources" /></span> </font>
						<tr>
							<td class="fontnormalbold">
								<mifos:mifoslabel name="loan.plz_note" />
								<span class="fontnormal"> <mifos:mifoslabel name="loan.new" /> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel name="loan.accs" /> <mifos:mifoslabel name="loan.withfollid" isColonRequired="Yes" /> <br> <br> </span>
								<c:forEach var="loanGlobalNum" items="${requestScope.accountsList}" varStatus="status" >
								<html-el:link href="loanAccountAction.do?globalAccountNum=${loanGlobalNum}&method=get" styleId="CreateMultipleLoanAccountsConfirm.link.account.${status.index}"><mifos:mifoslabel name="loan.accountNumber" />${loanGlobalNum}</html-el:link>
								<br>
								</c:forEach>
								<br>
								<br>
								<span class="fontnormalboldorange"><mifos:mifoslabel name="loan.suggested_steps" /></span>
								<br>
								<span class="fontnormal"> <html-el:link styleId="CreateMultipleLoanAccountsConfirmation.link.createMultipleLoanAccounts" href="multipleloansaction.do?method=load">
										<mifos:mifoslabel name="loan.create" />
										<mifos:mifoslabel name="loan.multiple" />
										<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
										<mifos:mifoslabel name="loan.accs" />
									</html-el:link></span>
							</td>
						</tr>
					</table>
					<br>
					<br>
				</td>
			</tr>
		</table>
	</tiles:put>
</tiles:insert>
