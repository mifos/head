<!-- 

/**

 * ProductDetailsPreview.jsp    version: 1.0

 

 * Copyright © 2005-2006 Grameen Foundation USA

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

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<script language="javascript">
<!--
	function fnCancel(form) {
		form.method.value="cancel";
		form.action="mifosproddefaction.do";
		form.submit();
	}
	function fnEdit(form) {
		form.method.value="previous";
		form.action="mifosproddefaction.do";
		form.submit();
	}
	function fnSearch(form) {
		form.method.value="search";
		form.action="mifosproddefaction.do";
		form.submit();
	}
	function fnGet() {
		mifosproddefactionform.method.value="get";
		mifosproddefactionform.action="mifosproddefaction.do";
		mifosproddefactionform.submit();
	}
	function fnCancel() {
		mifosproddefactionform.method.value="cancel";
		mifosproddefactionform.input.value="admin";
		mifosproddefactionform.action="mifosproddefaction.do";
		mifosproddefactionform.submit();
	}
	function func_disableSubmitBtn(){
		document.getElementById("submitBut").disabled=true;
	}
//-->
</script>
		<html-el:form action="/mifosproddefaction"  onsubmit="func_disableSubmitBtn();">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"><html-el:link
						href="javascript:fnCancel()">
						<mifos:mifoslabel name="product.admin"
							bundle="ProductDefUIResources" />
					</html-el:link> / <html-el:link
						href="javascript:fnSearch(mifosproddefactionform)">
						<mifos:mifoslabel name="product.viewprdcat"
							bundle="ProductDefUIResources" />
					</html-el:link> / <html-el:link href="javascript:fnGet()">
						<c:out value="${param.prdCategoryName}" />
					</html-el:link></span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange"><span class="heading"><c:out
								value="${param.prdCategoryName}" /> - </span><mifos:mifoslabel
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
								name="product.categoryname" bundle="ProductDefUIResources" />:<span
								class="fontnormal"> <c:out
								value="${sessionScope.mifosproddefactionform.productCategoryName}" /><br>
							<br>
							</span><mifos:mifoslabel name="product.desc"
								bundle="ProductDefUIResources" /><span class="fontnormal"><br>
							<c:out
								value="${sessionScope.mifosproddefactionform.productCategoryDesc}" />
							<br>
							<br>
							</span> <mifos:mifoslabel name="product.status"
								bundle="ProductDefUIResources" />: <span class="fontnormal"> <c:choose>
								<c:when test="${param.status==null}">
									<c:forEach items="${requestScope.PrdCategoryStatusList}"
										var="prdCategoryStatus">
										<c:if
											test="${prdCategoryStatus.id eq requestScope.Context.valueObject.prdCategoryStatus.prdCategoryStatusId}">
											<c:out value="${prdCategoryStatus.name}" />
											<html-el:hidden property="status"
												value="${prdCategoryStatus.name}" /></span><br>
							</c:if> </c:forEach> </c:when> <c:otherwise>
								<c:out value="${param.status}" />
								</span>
								<br>
							</c:otherwise> </c:choose> <span class="fontnormal"></span><span
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
					<c:forEach items="${requestScope.ProductType}" var="productType">
						<c:if
							test="${productType.id eq requestScope.Context.valueObject.productType.productTypeID}">
							<html-el:hidden property="productType.productTypeID"
								value="${requestScope.Context.valueObject.productType.productTypeID}" />
							<html-el:hidden property="productType.versionNo"
								value="${productType.versionNo}" />
						</c:if>
					</c:forEach> <html-el:hidden property="method" value="update" /> <html-el:hidden
						property="input" value="details" /> <html-el:hidden
						property="prdCategoryName" value="${param.prdCategoryName}" /> <html-el:hidden
						property="searchNode(search_name)" value="ProductCategories" />

					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="93%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center">&nbsp; <html-el:submit styleClass="buttn"
								style="width:70px"  property="submitBut">
								<mifos:mifoslabel name="product.butsubmit"
									bundle="ProductDefUIResources" />
							</html-el:submit> &nbsp; <html-el:button property="cancel"
								styleClass="cancelbuttn" style="width:70px"
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
		</html-el:form>
	</tiles:put>
</tiles:insert>
