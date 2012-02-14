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
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.ProductDefinitionResources"/>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
	<span id="page.id" title="CreateSavingsProductConfirmation"></span>
		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td width="70%" align="left" valign="top" class="paddingL15T15">
					<table width="98%" border="0" cellspacing="0" cellpadding="3">
						<tr>
							<td class="headingorange">
								<fmt:message key="product.marginMoneySuccessSavings">
								<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" /></fmt:param>
								</fmt:message>
								<br>
								<br>
							</td>
						</tr>
						<tr>
							<td class="fontnormalbold">
								<mifos:mifoslabel name="product.plsnote" bundle="ProductDefUIResources" isColonRequired="yes"/>
								&nbsp;<span class="fontnormal"> 
								<fmt:message key="product.savingsAssignedTo">
								<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" /></fmt:param>
								</fmt:message>:</span>
								<c:out value="${requestScope.savingsprdglobalofferingnum}" />
								<span class="fontnormal"><br> </span><span class="fontnormal"><br> <br> 
							<c:url value="savingsproductaction.do" var="savingsproductactionGetMethodUrl" >
								<c:param name="method" value="get" />
								<c:param name="prdOfferingId" value="${requestScope.savingsId}" />
								<c:param name="randomNUm" value="${sessionScope.randomNUm}" />
							</c:url >
							</span>
								<html-el:link href="${savingsproductactionGetMethodUrl}" styleId="CreateSavingsProductConfirmation.link.viewSavingsDetails">
									<fmt:message key="product.viewSavingsDetails">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" /></fmt:param>
									</fmt:message>
							<c:url value="savingsproductaction.do" var="savingsproductactionLoadMethodUrl" >
								<c:param name="method" value="load" />
								<c:param name="recordOfficeId" value="${UserContext.branchId}" />
								<c:param name="recordLoanOfficerId" value="${UserContext.id}" />
								<c:param name="randomNUm" value="${sessionScope.randomNUm}" />
							</c:url >
								</html-el:link>
								<span class="fontnormal"><br> <br> </span><span class="fontnormal"> <html-el:link href="${savingsproductactionLoadMethodUrl}">
									<fmt:message key="product.defineNew">
										<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" /></fmt:param>
									</fmt:message>
									</html-el:link></span>
							</td>
						</tr>
					</table>
					<br>
				</td>
			</tr>
		</table>

	</tiles:put>
</tiles:insert>
