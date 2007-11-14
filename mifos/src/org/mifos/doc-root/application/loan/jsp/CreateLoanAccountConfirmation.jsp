<!--
 
 * CreateLoanAccountConfirmation.jsp  version: xxx
 
 
 
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
 
 -->

<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">

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
                                            <mifos:mifoslabel name="loan.successful_recreation" />
                                        </c:when>
                                        <c:otherwise>
                                            <mifos:mifoslabel name="loan.successful_creation" />
                                        </c:otherwise>
                                    </c:choose>
                                    <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
									<mifos:mifoslabel name="accounts.account" />

									<br>
									<br>
								</td>
							</tr>
							<tr>
								<td>
									<font class="fontnormalRedBold"> <html-el:errors bundle="loanUIResources" /> </font>
								</td>
							</tr>
							<tr>
								<td class="fontnormalbold">
									<mifos:mifoslabel name="loan.plz_note" />
									&nbsp;<span class="fontnormal"> <mifos:mifoslabel name="loan.congo1_1" /> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel name="loan.congo1_2" /> <c:out value='${customer.displayName}' /> <mifos:mifoslabel
											name="loan.congo2" /> <c:out value='${requestScope.globalAccountNum}' /> <mifos:mifoslabel name="loan.congo3" /> <br> <br> <br> </span>
									<html-el:link href="loanAccountAction.do?method=get
									&customerId=${customer.customerId}
									&globalAccountNum=${requestScope.globalAccountNum}
									&recordOfficeId=${requestScope.loan.office.officeId}
									&recordLoanOfficerId=${requestScope.loan.personnel.personnelId}&randomNUm=${sessionScope.randomNUm}">
										<mifos:mifoslabel name="loan.view_loan_acc1" />
										<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
										<mifos:mifoslabel name="loan.view_loan_acc2" />
									</html-el:link>
									<span class="fontnormal"><br> <br> </span><span class="fontnormalboldorange"><mifos:mifoslabel name="loan.suggested_steps" /></span><span class="fontnormal"> <br> <mifos:mifoslabel name="loan.open_new_acc" /> <c:out
											value='${customer.displayName}' /> <br> </span>
									<table width="80%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="2%">
												<img src="pages/framework/images/trans.gif" width="15" height="10">
											</td>
											<td width="98%">
												<span class="fontnormal"><html-el:link href="savingsAction.do?method=getPrdOfferings&customerId=${customer.customerId}&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
														<mifos:mifoslabel name="loan.open_new" />
														<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" />&nbsp;<mifos:mifoslabel name="accounts.account" />
													</html-el:link> <br> <html-el:link href="loanAccountAction.do?method=getPrdOfferings&customerId=${customer.customerId}&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
														<mifos:mifoslabel name="loan.open_new" />
														<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />&nbsp;<mifos:mifoslabel name="accounts.account" />
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
