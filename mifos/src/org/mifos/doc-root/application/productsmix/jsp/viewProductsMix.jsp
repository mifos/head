<!--

/**

 * viewProductsMix.jsp    version: 1.0



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

 */

-->
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<html-el:form action="/productMixAction?method=search">
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'ProductMixList')}"
			var="mixedlist"/>

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"><html-el:link
						href="AdminAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
						<mifos:mifoslabel name="product.admin"
							bundle="ProductDefUIResources" />
					</html-el:link>/ </span> <span class="fontnormal8ptbold"><mifos:mifoslabel
						name="product.viewprdmix" bundle="ProductDefUIResources" /></span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange"> <mifos:mifoslabel
								name="product.view" bundle="ProductDefUIResources" />
								<mifos:mifoslabel name="product.pro" bundle="ProductDefUIResources" />
								<mifos:mifoslabel name="product.mix" bundle="ProductDefUIResources" />
							</td>
						</tr>
						<tr>
							<td class="fontnormalbold"><span class="fontnormal"><mifos:mifoslabel
								name="product.clickToViewMixDetails" bundle="ProductDefUIResources" />&nbsp;<html-el:link
								href="productMixAction.do?method=load&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="product.DefineMixForNewProduct"
									bundle="ProductDefUIResources" />
							</html-el:link>
							<br> <br>
							<font class="fontnormalRedBold"><html-el:errors
								bundle="ProductDefUIResources" /> </font>
							</span>
							<c:forEach var="productCategory"
								items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'ProductCategoryList')}">
							
								<c:if	test="${empty id}">
									<c:set var="id"
										value="${productCategory.productType.productTypeID}" />

								<c:if test="${!empty mixedlist}">
									<table width="95%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="61%">
											   <span class="fontnormalbold">
												 <c:out value="${productCategory.productType.name}" />
											   </span>
											 </td>
										</tr>
									</table>
								</c:if>
								<c:forEach var="productMix"
								items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'ProductMixList')}">	
								<c:if	test="${!empty id && id == productMix.prdCategory.productType.productTypeID}">	
									<span class="fontnormalbold"> </span>								
								<table width="90%" border="0" cellspacing="0" cellpadding="0">
									<tr class="fontnormal">
										<td width="1%"><img
											src="pages/framework/images/bullet_circle.gif" width="9"
											height="11"></td>
										<td width="99%"><html-el:link
											href="productMixAction.do?method=get&prdOfferingId=${productMix.prdOfferingId}&productType=${productMix.prdType.productTypeID}&randomNUm=${sessionScope.randomNUm}">
											<c:out value="${productMix.prdOfferingName}" />
										</html-el:link>
										</td>
									</tr>
								</table>
								</c:if>
								</c:forEach>
								<br>
								</c:if>
								<c:if
									test="${!empty id && id != productCategory.productType.productTypeID}">
									<c:set var="id"
										value="${productCategory.productType.productTypeID}" />

								<c:if test="${!empty mixedlist}">

									<table width="95%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="61%">
											   <span class="fontnormalbold">
												 <c:out value="${productCategory.productType.name}" />
											   </span>
											 </td>
										</tr>
									</table>
								</c:if>
								<c:forEach var="productMix"
								items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'ProductMixList')}">	
								<c:if	test="${!empty id && id == productMix.prdCategory.productType.productTypeID}">	
									<span class="fontnormalbold"> </span>								
								<table width="90%" border="0" cellspacing="0" cellpadding="0">
									<tr class="fontnormal">
										<td width="1%"><img
											src="pages/framework/images/bullet_circle.gif" width="9"
											height="11"></td>
										<td width="99%"><html-el:link
											href="productMixAction.do?method=get&prdOfferingId=${productMix.prdOfferingId}&productType=${productMix.prdType.productTypeID}&randomNUm=${sessionScope.randomNUm}">
											<c:out value="${productMix.prdOfferingName}" />
										</html-el:link>
										</td>
									</tr>
								</table>
								</c:if>
								</c:forEach>	
								</c:if>							
							</c:forEach>
							<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
						</html-el:form>
					</tiles:put>
				</tiles:insert>
