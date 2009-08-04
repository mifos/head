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
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
	<span id="page.id" title="viewProductsMixDetails" />

		<script language="javascript">

	function fnManage(form) {
		form.method.value="manage";
		form.action="productMixAction.do";
		form.submit();
	}

</script>
		<html-el:form action="/productMixAction.do">
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<c:set	value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
		<tr>
				<td class="bluetablehead05">
					<span class="fontnormal8pt"><html-el:link href="productMixAction.do?method=cancelCreate&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
							<mifos:mifoslabel name="product.admin" bundle="ProductDefUIResources" />
						</html-el:link> / </span> <span class="fontnormal8pt">
						<html-el:link
						href="productMixAction.do?method=viewAllProductMix&randomNUm=${sessionScope.randomNUm}">
						 <mifos:mifoslabel name="product.viewprdmix" bundle="ProductDefUIResources" />
						 </html-el:link> /
						<span class="fontnormal8ptbold"><c:out value="${BusinessKey.prdOfferingName}" /></span>	
						</td>
			</tr>
	
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="68%" height="23" class="headingorange">
									<c:out value="${BusinessKey.prdOfferingName}" />
								</td>
								<td width="32%" align="right">
									<a	href="productMixAction.do?method=manage&prdOfferingId=${BusinessKey.prdOfferingId}&productInstance=${BusinessKey.prdOfferingId}&productType=${BusinessKey.prdType.productTypeID}&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">

										<mifos:mifoslabel name="product.prdedit" bundle="ProductDefUIResources" />
										<mifos:mifoslabel name="product.pro" bundle="ProductDefUIResources" />
										<mifos:mifoslabel name="product.mix" bundle="ProductDefUIResources" />
										<mifos:mifoslabel name="product.inf" bundle="ProductDefUIResources" />

									</a>
								</td>
							</tr>
							<tr>
								<td>
									<font class="fontnormalRedBold"><html-el:errors bundle="ProductDefUIResources" /> </font>
								</td>
							</tr>
							<tr>
								<td height="23" colspan="2" class="fontnormal">
									<span class="fontnormal"> </span><span class="fontnormal"> </span>
									<table width="100%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td height="23" class="fontnormalbold">
												<mifos:mifoslabel name="product.allowedProducts"
													bundle="ProductDefUIResources" /> : <span class="fontnormal">
													 <br>
												<c:forEach
													items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'AllowedProductList')}"
													var="allowedProduct">
													<c:out value="${allowedProduct.prdOfferingName}" />
													<br>
												</c:forEach> <br>
												 <span class="fontnormalbold">
											     <mifos:mifoslabel name="product.notAllowedProducts"
													bundle="ProductDefUIResources" /> :  </span><span class="fontnormal">
													 <br>
												<c:forEach
													items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'NotAllowedProductList')}"
													var="notAllowedProduct">
													<c:out value="${notAllowedProduct.prdOfferingName}" />
													<br>
												</c:forEach> <br>
												
												<html-el:link	href="productMixAction.do?method=loadChangeLog&entityType=ProductMix&entityId=${BusinessKey.prdOfferingId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}&prdOfferName=${requestScope.BusinessKey.prdOfferingName}">
														<mifos:mifoslabel name="product.viewchangelog" bundle="ProductDefUIResources" />
												</html-el:link> 									
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
		</html-el:form>
	</tiles:put>
</tiles:insert>
