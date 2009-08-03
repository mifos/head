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
	<span id="page.id" value="CreateProductCategoryConfirm" />
		<html-el:form action="/productCategoryAction?method=get">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
					<table width="98%" border="0" cellspacing="0" cellpadding="3">
						<tr>
							<td class="headingorange"><mifos:mifoslabel
								name="product.addsuccessful" bundle="ProductDefUIResources" /><br>
							<br>
							</td>
						</tr>
						<tr>
							<td class="fontnormalbold"><mifos:mifoslabel
								name="product.plsnote" bundle="ProductDefUIResources" isColonRequired="yes"/> <span
								class="fontnormal"> <mifos:mifoslabel name="product.prodnum"
								bundle="ProductDefUIResources" isColonRequired="yes"/></span> <c:out
								value="${sessionScope.productCategoryActionForm.globalPrdCategoryNum}" />
							<span class="fontnormal"><br>
							</span><span class="fontnormal"></span><span class="fontnormal"><br>
							<br>
							</span><html-el:link action="/productCategoryAction.do?method=get&randomNUm=${sessionScope.randomNUm}&globalPrdCategoryNum=${sessionScope.productCategoryActionForm.globalPrdCategoryNum}">
								<mifos:mifoslabel name="product.viewdet"
									bundle="ProductDefUIResources" />
							</html-el:link> <span class="fontnormal"><br>
							<br>
							</span><span class="fontnormal"> <html-el:link
								action="/productCategoryAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="product.defprdcat"
									bundle="ProductDefUIResources" />
							</html-el:link></span></td>
						</tr>
						<html-el:hidden property="globalPrdCategoryNum"
							value="${sessionScope.productCategoryActionForm.globalPrdCategoryNum}" />
					</table>
					<br>
					</td>
				</tr>
			</table>
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
