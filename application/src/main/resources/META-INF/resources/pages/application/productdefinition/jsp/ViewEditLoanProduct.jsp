<%--
Copyright (c) 2005-2011 Grameen Foundation USA
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
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.ProductDefinitionResources"/>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
	<span id="page.id" title="ViewEditLoanProduct"></span>

		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td class="bluetablehead05">
					<span class="fontnormal8pt"><html-el:link href="loanproductaction.do?method=cancelCreate&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
							<mifos:mifoslabel name="product.admin" bundle="ProductDefUIResources" />
						</html-el:link> / </span> <span class="fontnormal8ptbold"> 
						<fmt:message key="product.viewLoanProducts">
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" /></fmt:param>
						</fmt:message></span>
				</td>
			</tr>
		</table>
		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange">
								<span class="headingorange">
								<fmt:message key="product.viewLoanProducts">
								<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" /></fmt:param>
								</fmt:message></span>
							</td>
						</tr>
						<tr>
							<td class="fontnormalbold">
								<span class="fontnormal"> <mifos:mifoslabel name="product.clickon" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="product.pro"
										bundle="ProductDefUIResources" /> <mifos:mifoslabel name="product.makechanges" bundle="ProductDefUIResources" /> &nbsp;<html-el:link
										href="loanproductaction.do?method=load&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
										<fmt:message key="product.defineNew">
										<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" /></fmt:param>
										</fmt:message>
									</html-el:link><br> <br> </span><span class="fontnormalbold"><br> </span> <span class="fontnormalbold"> </span> <font class="fontnormalRedBold"><html:errors bundle="ProductDefUIResources" /> </font>
                            </td>
                        </tr>
                        <c:if test='${sessionScope.isMultiCurrencyEnabled}'>
                            <tr>
                                <td align="left" style="padding-bottom: 10px">
                                    <span class="fontnormalbold"><mifos:mifoslabel name="product.currency" bundle="ProductDefUIResources" />:</span>
                                    <select id="currencyFilter" style="padding-left: 4px; padding-right: 4px;">
                                        <option value="-1" <c:if test="${empty param.currencyId}">selected</c:if>>-</option>
                                        <c:forEach items="${sessionScope.currencies}" var="currency">
                                            <option value="${currency.currencyId}" <c:if test="${param.currencyId == currency.currencyId}">selected</c:if>>${currency.currencyCode}</option>
                                        </c:forEach>
                                    </select>
                                    <script type="text/javascript">
                                    $("#currencyFilter").change(function() {
                                        var url = "loanproductaction.do?method=viewAllLoanProducts&recordOfficeId=${param.recordOfficeId}&recordLoanOfficerId=${param.recordLoanOfficerId}&randomNUm=${param.randomNUm}";
                                        if ($(this).val() != "-1") {
                                            url += "&currencyId=" + $(this).val();
                                        }
                                        window.location.replace(url); 
                                    });
                                    </script>
                                </td>
                            </tr>
                        </c:if>    
                        <tr>
                           <td>
								<table width="90%" border="0" cellspacing="0" cellpadding="0">
									<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'LoanProductList')}" var="LoanProduct">
                                        <c:if test='${!sessionScope.isMultiCurrencyEnabled || empty param.currencyId || param.currencyId == LoanProduct.currency.currencyId}'>
										<tr class="fontnormal">
											<td width="1%">
												<img src="pages/framework/images/bullet_circle.gif" width="9" height="11">
											</td>
											<td width="99%">
												<html-el:link href="loanproductaction.do?method=get&prdOfferingId=${LoanProduct.prdOfferingId}&randomNUm=${sessionScope.randomNUm}">
													<c:out value="${LoanProduct.prdOfferingName}" />
												</html-el:link>
												<c:if test="${LoanProduct.prdStatus.offeringStatusId eq 4}">
											&nbsp;<span class="fontnormal"><img src="pages/framework/images/status_closedblack.gif" width="8" height="9">&nbsp; <c:out value="${LoanProduct.prdStatus.prdState.name}" /></span>
												</c:if>
											</td>
                                        </tr>
                                        </c:if>    
									</c:forEach>
								</table>
							</td>
						</tr>
					</table>
					<br>
			  	    <html-el:submit property="backButton" styleId="vieweditloanprd.button.back" styleClass="buttn" onclick="window.location.href='AdminAction.do?method=load'">
						<mifos:mifoslabel name="button.back" bundle ="PersonnelUIResources"></mifos:mifoslabel> 
					</html-el:submit>			
				</td>
			</tr>
		</table>
		<br>
	</tiles:put>
</tiles:insert>
