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
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".clientsacclayoutmenu">
	<tiles:put name="body" type="string">
	<span style="display: none" id="page.id">LoanConfirmation</span>
	
	
		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td align="left" valign="top" class="paddingL10">
				<table width="90%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td align="left" valign="top"><span class="headingorange"><mifos:mifoslabel
							name="accountStatus.changeaccountstatus" /> - <mifos:mifoslabel
							name="accountStatus.confirmation" /><br><br></span></td>
					</tr>
				</table>
				
				<table width="96%" border="0" cellpadding="0" cellspacing="0">
					<tr class="fontnormalbold">
						<td width="100%" colspan="2" valign="top"><mifos:mifoslabel
							name="accountStatus.statusmessage" />:<br><br></td>
							
					</tr>
					<c:forEach var="account" items="${accountsList}">
					<tr class="fontnormal">
						<td valign="top">
							<span class="fontnormal">
								<html-el:link styleId="confirmation.link.viewLoanAccount" href="loanAccountAction.do?method=get&globalAccountNum=${account}&randomNUm=${sessionScope.randomNUm}">
									<mifos:mifoslabel name="accountStatus.account" />
									<c:out value="${account}" />
								</html-el:link>
							</span>
						</td>
					</tr>	
					</c:forEach>
				</table>
	</tiles:put>
</tiles:insert>
