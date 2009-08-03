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
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>

<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
	<span id="page.id" value="CustSearchAccount" />
    <c:choose>
            <c:when test="${requestScope.perspective == 'redoLoan'}">
                <script>
                function fun_cancel(form)
                {

                    form.action="AdminAction.do";
                    form.method.value="load";
                    form.submit();
                }
                </script>
            </c:when>
            <c:otherwise>
                <script>
                function fun_cancel(form)
                {

                    form.action="custSearchAction.do";
                    form.method.value="loadMainSearch";
                    form.submit();
                }
                </script>
            </c:otherwise>
        </c:choose>
		<html-el:form method="post"
			action="/custSearchAction.do?method=search">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="470" align="left" valign="top" bgcolor="#FFFFFF">
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td align="center" class="heading">&nbsp;</td>
						</tr>
					</table>
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td class="bluetablehead">
						<c:choose>
						<c:when test="${custSearchActionForm.input == 'savings'}">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td width="33%">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/bigarrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorange"><mifos:mifoslabel
												name="accounts.SelectCustomer" />
												</td>
										</tr>
									</table>
									</td>
									<td width="34%" align="center">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorangelight">
											<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" />
											<mifos:mifoslabel name="Savings.accountInformation" /></td>
										</tr>
									</table>
									</td>
									<td width="33%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorangelight"><mifos:mifoslabel
												name="accounts.review&submit" /></td>
										</tr>
									</table>
									</td>
								</tr>
							</table>
						</c:when>
						<c:otherwise>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td width="25%">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/bigarrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorange"><mifos:mifoslabel
												name="accounts.SelectCustomer" /></td>
										</tr>
									</table>
									</td>
									<td width="25%" align="center">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorangelight"><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /><c:out value=" "/><mifos:mifoslabel
												name="accounts.acc_info" /></td>
										</tr>
									</table>
									</td>
									<td width="25%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorangelight"><mifos:mifoslabel
												name="accounts.review/edit_ins" /></td>
										</tr>
									</table>
									</td>
									<td width="25%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorangelight"><mifos:mifoslabel
												name="accounts.review&submit" /></td>
										</tr>
									</table>
									</td>
								</tr>
							</table>
						</c:otherwise>
						</c:choose>
							</td>
						</tr>
					</table>
				
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0" class="bluetableborder">
						<tr>
							<td width="70%" height="24" align="left" valign="top"
								class="paddingleftCreates">
							<table width="98%" border="0" cellspacing="0" cellpadding="3">
                                <c:if test="${requestScope.perspective == 'redoLoan'}">  
                                <tr>
                                    <td><span class="fontnormalRedBold"><mifos:mifoslabel name="loan.redo_loan_note"/></span></td>
                                </tr>
                                </c:if>
                                <tr>
									<td class="headingorange">
								<c:choose>
									<c:when test="${custSearchActionForm.input == 'savings'}">
										<span class="heading"> 
											<mifos:mifoslabel name="accounts.create" />
											<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" />
											<mifos:mifoslabel name="accounts.Account" /> - 
										</span>  
									</c:when>
									<c:otherwise>
										<span class="heading">
                                            <c:choose>
                                            <c:when test="${requestScope.perspective == 'redoLoan'}">
                                                <mifos:mifoslabel name="admin.redo" />
                                            </c:when>
                                            <c:otherwise>
                                                <mifos:mifoslabel name="accounts.create" />
                                            </c:otherwise>
                                            </c:choose>
                                            <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
											<mifos:mifoslabel name="accounts.Account" /> - 
										</span>  
									</c:otherwise>
								</c:choose>
									<mifos:mifoslabel
												name="accounts.SelectACustomer" /></td>
								</tr>
								<tr>
									<td class="fontnormalbold"><span class="fontnormal"><mifos:mifoslabel
										name="accounts.enter_client_details1" />
										<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />
										<mifos:mifoslabel name="accounts.or" />
										<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />
										<c:if test="${custSearchActionForm.input == 'savings'}">
											<mifos:mifoslabel name="accounts.or" />
											<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
										</c:if>
										<c:out value=" "/><mifos:mifoslabel
										name="accounts.enter_client_details2" /></span></td>
								</tr>
								<tr>
									<td>
										<font class="fontnormalRedBold">
											<span id="cust_search_account.error.message"><html-el:errors bundle="CustomerSearchUIResources" /></span> 
										</font>
									</td>
								</tr>
							</table>
							<br>
							<table width="90%" border="0" cellspacing="0" cellpadding="3">
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="accounts.name" />:</td>
									<td><html-el:text styleId="cust_search_account.input.searchString"
										property="searchString" maxlength="200"/> </td>
								</tr>
								<tr class="fontnormal">
									<td width="30%">&nbsp;</td>
									<td width="70%"><br>
									<html-el:submit styleId="cust_search_account.button.search" property="searchButton" styleClass="buttn">
										<mifos:mifoslabel name="accounts.search" />
									</html-el:submit>&nbsp;
                                    <html-el:button styleId="cust_search_account.button.cancel" property="cancelButton"
                                        onclick="javascript:fun_cancel(this.form)"
                                        styleClass="cancelbuttn">
                                        <mifos:mifoslabel name="accounts.cancel" />
                                    </html-el:button>
                                    </td>
								</tr>
							</table><br>
							</td>
						</tr>
					</table><br>
					</td>
				</tr>
			</table>
			<html:hidden property="method" value="search" />
			<html:hidden property="perspective" value="${requestScope.perspective}"/>
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
