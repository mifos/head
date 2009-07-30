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
	<span style="display: none" id="page.id">ManageProductCategoryPreview</span>
		<script language="javascript">
<!--
	function fnEdit(form) {
		form.action="productCategoryAction.do?method=managePrevious";
		form.submit();
	}
	function fnGet() {
		productCategoryActionForm.action="productCategoryAction.do?method=get";
		productCategoryActionForm.submit();
	}
	function func_disableSubmitBtn(){
		document.getElementById("submitBut").disabled=true;
	}
//-->
</script>
		<html-el:form action="/productCategoryAction"  onsubmit="func_disableSubmitBtn();">
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
			   	   var="BusinessKey" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"><html-el:link
						href="AdminAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
						<mifos:mifoslabel name="product.admin"
							bundle="ProductDefUIResources" />
					</html-el:link> / <html-el:link
						href="productCategoryAction.do?method=getAllCategories&randomNUm=${sessionScope.randomNUm}">
						<mifos:mifoslabel name="product.viewprdcat"
							bundle="ProductDefUIResources" />
					</html-el:link> / <html-el:link href="productCategoryAction.do?method=get&globalPrdCategoryNum=${BusinessKey.globalPrdCategoryNum}&randomNUm=${sessionScope.randomNUm}">
						<c:out value="${BusinessKey.productCategoryName}" />
					</html-el:link></span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange"><span class="heading"><c:out
								value="${BusinessKey.productCategoryName}" /> - </span><mifos:mifoslabel
								name="product.buteditcat" bundle="ProductDefUIResources" /></td>
						</tr>
						<tr>
							<td class="fontnormal"><mifos:mifoslabel
								name="product.previewfields" bundle="ProductDefUIResources" />
							<mifos:mifoslabel name="product.clicksubmit"
								bundle="ProductDefUIResources" /> <mifos:mifoslabel
								name="product.clickcanccat" bundle="ProductDefUIResources" /><span
								class="mandatorytext"></span></td>
						</tr>
					</table>
					<br>
					<font class="fontnormalRedBold"><html-el:errors
						bundle="ProductDefUIResources" /></font>
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="100%" class="fontnormalbold">
							<mifos:mifoslabel name="product.categorydetails" bundle="ProductDefUIResources" />
							<br>
							<br>
							<mifos:mifoslabel
								name="product.categoryname" bundle="ProductDefUIResources" isColonRequired="yes"/><span
								class="fontnormal"> <c:out
								value="${productCategoryActionForm.productCategoryName}" /><br>
							<br>
							</span><mifos:mifoslabel name="product.desc"
								bundle="ProductDefUIResources" /><span class="fontnormal"><br>
							<c:out
								value="${productCategoryActionForm.productCategoryDesc}" />
							<br>
							<br>
							</span> <mifos:mifoslabel name="product.status"
								bundle="ProductDefUIResources" isColonRequired="yes"/> <span class="fontnormal">
							<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'PrdCategoryStatusList')}"
										var="prdCategoryStatusObject">
								<c:if	test="${prdCategoryStatusObject.id eq productCategoryActionForm.productCategoryStatus}">
											<c:out value="${prdCategoryStatusObject.name}" />
									</span><br>
								</c:if>
							</c:forEach> <span class="fontnormal"></span><span
								class="fontnormal"> </span><span class="fontnormal"></span><span
								class="fontnormal"><br>
							<br>
							<html-el:button property="edit" styleClass="insidebuttn"
								onclick="fnEdit(this.form)">
								<mifos:mifoslabel name="product.buteditcat"
									bundle="ProductDefUIResources" />
							</html-el:button> </span></td>
						</tr>
					</table>
					<html-el:hidden property="method" value="update" />
					<html-el:hidden	property="searchNode(search_name)" value="ProductCategories" />
					<html-el:hidden property="globalPrdCategoryNum"
							value="${BusinessKey.globalPrdCategoryNum}" />

					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="93%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center">&nbsp; <html-el:submit styleClass="buttn"
								property="submitBut">
								<mifos:mifoslabel name="product.butsubmit"
									bundle="ProductDefUIResources" />
							</html-el:submit> &nbsp; <html-el:button property="cancel"
								styleClass="cancelbuttn"
								onclick="javascript:fnGet()">
								<mifos:mifoslabel name="product.cancel"
									bundle="ProductDefUIResources" />
							</html-el:button></td>
						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
