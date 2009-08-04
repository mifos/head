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

<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<span id="page.id" title="CreateLoanAccountConfirmation" />

        <fmt:setLocale value='${sessionScope["LOCALE"]}'/>
		<fmt:setBundle basename="org.mifos.config.localizedResources.LoanUIResources"/>

		<html-el:form method="post" action="/loanAccountAction.do?method=get">
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
						<table width="98%" border="0" cellspacing="0" cellpadding="3">
							<tr>
								<td class="headingorange">
                                    <c:choose>
										<c:when test="${requestScope.perspective == 'redoLoan'}">
											<fmt:message key="loan.successfulRecreation">
												<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /></fmt:param>
											</fmt:message>
                                        </c:when>
                                        <c:otherwise>
											<fmt:message key="loan.successfulCreation">
												<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /></fmt:param>
											</fmt:message>
                                        </c:otherwise>
                                    </c:choose>

									<br>
									<br>
								</td>
							</tr>
							<tr>
								<td>
									<font class="fontnormalRedBold"> <span id="CreateLoanAccountConfirmation.error.message"><html-el:errors bundle="loanUIResources" /></span> </font>
								</td>
							</tr>
							<tr>
								<td class="fontnormalbold">
									<mifos:mifoslabel name="loan.plz_note" />
									&nbsp;<span class="fontnormal">
										<fmt:message key="loan.accountAssigned">
											<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /></fmt:param>
											<fmt:param><c:out value='${customer.displayName}' /></fmt:param>
											<fmt:param><c:out value='${requestScope.globalAccountNum}' /></fmt:param>
										</fmt:message><br> <br> <br> </span>
									<html-el:link styleId="CreateLoanAccountConfirmation.link.viewLoanDetails" href="loanAccountAction.do?method=get
									&customerId=${customer.customerId}
									&globalAccountNum=${requestScope.globalAccountNum}
									&recordOfficeId=${requestScope.loan.office.officeId}
									&recordLoanOfficerId=${requestScope.loan.personnel.personnelId}&randomNUm=${sessionScope.randomNUm}">
										<fmt:message key="loan.viewLoanDetails">
											<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /></fmt:param>
										</fmt:message>
									</html-el:link>
									<span class="fontnormal"><br> <br> </span><span class="fontnormalboldorange"><mifos:mifoslabel name="loan.suggested_steps" /></span><span class="fontnormal"> <br> 
										<fmt:message key="loan.openNewAccountFor">
											<fmt:param><c:out value='${customer.displayName}' /></fmt:param>
										</fmt:message> <br> </span>
									<table width="80%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="2%">
												<img src="pages/framework/images/trans.gif" width="15" height="10">
											</td>
											<td width="98%">
												<span class="fontnormal"><html-el:link styleId="CreateLoanAccountConfirmation.link.newSavingsAccount" href="savingsAction.do?method=getPrdOfferings&customerId=${customer.customerId}&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
														<fmt:message key="loan.openNewSavingsAccount" >
															<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" /></fmt:param>
														</fmt:message>
													</html-el:link> <br> <html-el:link styleId="CreateLoanAccountConfirmation.link.newLoanAccount" href="loanAccountAction.do?method=getPrdOfferings&customerId=${customer.customerId}&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
														<fmt:message key="loan.openNewLoanAccount" >
															<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /></fmt:param>
														</fmt:message>
													</html-el:link></span>
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
						<br>
						<br>
					</td>
				</tr>
			</table>
		</html-el:form>
	</tiles:put>
</tiles:insert>
