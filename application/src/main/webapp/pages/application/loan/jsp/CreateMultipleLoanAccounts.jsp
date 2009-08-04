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
<!-- CreateMultipleLoanAccounts.jsp -->
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


<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
		<span id="page.id" title="CreateMultipleLoanAccounts" />
		<script language="javascript">
		<!--
			function fnLoadLoanOfficers(form) {
				form.method.value="getLoanOfficers";
				form.action="multipleloansaction.do";
				form.submit();
			}
			function fnLoadCustomers(form) {
				form.method.value="getCenters";
				form.action="multipleloansaction.do";
				form.submit();
			}
			function getApplPrdOfferingsForCustomer(form) {
				form.method.value="getPrdOfferings";
				form.action="multipleloansaction.do";
				form.submit();
			}
		//-->
		</script>
		<html-el:form action="/multipleloansaction.do">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="470" align="left" valign="top" bgcolor="#FFFFFF">
						<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center" class="heading">
									&nbsp;
								</td>
							</tr>
						</table>
						<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
							<tr>
								<td class="bluetablehead">
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="50%">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17">
														</td>
														<td class="timelineboldorange">
															<mifos:mifoslabel name="loan.search" />
															<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /><mifos:mifoslabel name="loan.s" />
														</td>
													</tr>
												</table>
											</td>
											<td width="50%" align="center">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17">
														</td>
														<td class="timelineboldorangelight">
															<mifos:mifoslabel name="loan.Select" />
															<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /><mifos:mifoslabel name="loan.s" />
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
						<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
							<tr>
								<td width="70%" height="24" align="left" valign="top" class="paddingleftCreates">
									<table width="98%" border="0" cellspacing="0" cellpadding="3">
										<tr>
											<td class="headingorange">
												<span class="heading"><mifos:mifoslabel name="loan.create" /> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel name="loan.accounts" /> - </span>
												<mifos:mifoslabel name="loan.search" />
												<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /><mifos:mifoslabel name="loan.s" />
											</td>
										</tr>
										<tr>
											<td class="fontnormalbold">
												<span class="fontnormal"><mifos:mifoslabel name="loan.Enter" /> <mifos:mifoslabel name="loan.detailssearch" /> <mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /><mifos:mifoslabel name="loan.s" /> &amp; <mifos:mifoslabel
														name="loan.accwithoutsub" /></span>
											</td>
										</tr>
									</table>
									<font class="fontnormalRedBold"> <span id="CreateMultipleLoanAccounts.error.message"><html-el:errors bundle="loanUIResources" /></span> </font>
									<br>
									<table width="100%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td valign="top" class="fontnormal">
												<table width="50%" border="0" cellpadding="4" cellspacing="0">
													<tr class="fontnormal">
														<td width="32%" align="right">
															<mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}" mandatory="yes" isColonRequired="Yes" />
														</td>
														<td width="68%">
															<mifos:select property="branchOfficeId" onchange="fnLoadLoanOfficers(this.form)" style="width:136px;" styleId="createMultipleLoanAccounts.select.branchOffice">
																<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'multipleloansofficeslist')}" var="office">
																	<html-el:option value="${office.officeId}">${office.officeName}</html-el:option>
																</c:forEach>
															</mifos:select>
														</td>
													</tr>
													<tr class="fontnormal">
														<td align="right">
															<span class="mandatorytext"></span>
															<mifos:mifoslabel name="loan.loanOfficer" mandatory="yes" isColonRequired="Yes" />
														</td>
														<td>
															<mifos:select property="loanOfficerId" onchange="fnLoadCustomers(this.form)" style="width:136px;" styleId="createMultipleLoanAccounts.select.loanOfficer" >
																<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'multipleloansloanofficerslist')}" var="loanOfficer">
																	<html-el:option value="${loanOfficer.personnelId}">${loanOfficer.displayName}</html-el:option>
																</c:forEach>
															</mifos:select>
														</td>
													</tr>
													<tr class="fontnormal">
														<td align="right">
															<c:choose>
																<c:when test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'isCenterHeirarchyExists')==Constants.YES}">
																	<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" mandatory="yes" isColonRequired="Yes"/>
																</c:when>
																<c:otherwise>
																	<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" mandatory="yes" isColonRequired="Yes"/>
																</c:otherwise>
															</c:choose>
														</td>
														<td>
															<mifos:select property="centerId" onchange="getApplPrdOfferingsForCustomer(this.form)" style="width:136px;" styleId="createMultipleLoanAccounts.select.center" >
																<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'multipleloanscenterslist')}" var="customer">
																	<html-el:option value="${customer.customerId}">${customer.displayName}</html-el:option>
																</c:forEach>
															</mifos:select>
														</td>
													</tr>
													<tr class="fontnormal">
														<td align="right">
															<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" mandatory="Yes" />
															<mifos:mifoslabel name="loan.instancename" isColonRequired="Yes" />
														</td>
														<td>
															<mifos:select property="prdOfferingId" style="width:136px;" styleId="createMultipleLoanAccounts.select.loanProduct" >
																<c:forEach var="loanPrdOffering" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanPrdOfferings')}" >
																		<html-el:option value="${loanPrdOffering.prdOfferingId}">${loanPrdOffering.prdOfferingName}</html-el:option>
																	</c:forEach>
															</mifos:select>
														</td>
													</tr>
													<tr class="fontnormal">
														<td align="right">
															&nbsp;
														</td>
														<td>
															<br>
															<html-el:submit styleId="createMultipleLoanAccounts.button.submit" styleClass="buttn" >
																<mifos:mifoslabel name="loan.search" />
															</html-el:submit>
															&nbsp;
															<html-el:button property="cancel" styleClass="cancelbuttn"  onclick="location.href='multipleloansaction.do?method=cancel&currentFlowKey=${requestScope.currentFlowKey}'">
																<mifos:mifoslabel name="loan.cancel" />
															</html-el:button>
														</td>
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
					</td>
				</tr>
			</table>
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<html-el:hidden property="method" value="get" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
