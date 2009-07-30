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

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
	<span style="display: none" id="page.id">ProductCategoryDetails</span>
		<html-el:form action="/productCategoryAction?method=manage">
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
			   var="BusinessKey" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <html-el:link
						href="AdminAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
						<mifos:mifoslabel name="product.admin"
							bundle="ProductDefUIResources" />
					</html-el:link> / <html-el:link href="productCategoryAction.do?method=getAllCategories&randomNUm=${sessionScope.randomNUm}">
						<mifos:mifoslabel name="product.viewprdcat"
							bundle="ProductDefUIResources" />
					</html-el:link> / </span> <span class="fontnormal8ptbold"><c:out
						value="${BusinessKey.productCategoryName}" /></span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="50%" height="23" class="headingorange"><c:out
								value="${BusinessKey.productCategoryName}" /></td>
							<td width="50%" align="right"><html-el:link
								action="/productCategoryAction.do?method=manage&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="product.buteditcat"
									bundle="ProductDefUIResources" />
							</html-el:link></td>
						</tr>
						<tr>
							<td colspan="2"><font class="fontnormalRedBold"><html-el:errors
								bundle="ProductDefUIResources" /> </font></td>
						</tr>
						<tr>
							<td height="23" colspan="2" class="fontnormalbold"><span
								class="fontnormal"> </span><span class="fontnormal"> <c:choose>
								<c:when
									test="${BusinessKey.prdCategoryStatus.id eq 1}">
									<span class="fontnormal"> <img
										src="pages/framework/images/status_activegreen.gif" width="8"
										height="9">&nbsp;<mifos:mifoslabel name="product.active"
									bundle="ProductDefUIResources" /> </span>
								</c:when>
								<c:otherwise>
									<span class="fontnormal"> <img
										src="pages/framework/images/status_closedblack.gif" width="8"
										height="9">&nbsp;<mifos:mifoslabel name="product.active"
									bundle="ProductDefUIResources" /></span>
								</c:otherwise>
							</c:choose> <br>
							<span class="fontnormal"><mifos:mifoslabel
								name="product.producttype" bundle="ProductDefUIResources" isColonRequired="yes"/> <c:forEach
								items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'ProductTypeList')}" var="productTypeObject">
								<c:if
									test="${productTypeObject.productTypeID eq BusinessKey.productType.productTypeID}">
									<c:out value="${productTypeObject.name}" /></span><br>
							</c:if> </c:forEach> <span class="fontnormal"> </span><span
								class="fontnormal"></span><span class="fontnormal"> </span><span
								class="fontnormal"></span><br>

							<c:if
								test="${BusinessKey.productCategoryDesc != null &&
								!(BusinessKey.productCategoryDesc eq '')}">
								<mifos:mifoslabel name="product.desc"
									bundle="ProductDefUIResources" />
								<span class="fontnormal"><br>
								</span>
								<span class="fontnormal"> <c:out
									value="${BusinessKey.productCategoryDesc}" /> </span>
								<span class="fontnormal"><br>
							</c:if> </span><span class="fontnormal"> </span></td>
						</tr>
						<html-el:hidden property="searchNode(search_name)"
							value="ProductCategories" />
					</table>
					<br>
					</td>
				</tr>
			</table>
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
