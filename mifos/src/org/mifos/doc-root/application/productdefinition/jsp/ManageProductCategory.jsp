<!--

/**

 * ManageProductCategory.jsp    version: 1.0



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
		<!-- html:javascript formName="/mifosproddefaction"
			bundle="ProductDefUIResources" /-->
		<script language="javascript">
		<!--
			function fnSearch() {
				productCategoryActionForm.method.value="getAllCategories";
				productCategoryActionForm.action="productCategoryAction.do";
				productCategoryActionForm.submit();
			}
			function fnGet() {
				productCategoryActionForm.method.value="get";
				productCategoryActionForm.action="productCategoryAction.do";
				productCategoryActionForm.submit();
			}
			function fnCancel() {
				productCategoryActionForm.method.value="load";
				productCategoryActionForm.action="AdminAction.do";
				productCategoryActionForm.submit();
			}
		//-->
		</script>

		<html-el:form action="/productCategoryAction">
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
				   var="BusinessKey" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"><html-el:link
						href="javascript:fnCancel()">
						<mifos:mifoslabel name="product.admin"
							bundle="ProductDefUIResources" />
					</html-el:link> / <html-el:link
						href="javascript:fnSearch()">
						<mifos:mifoslabel name="product.viewprdcat"
							bundle="ProductDefUIResources" />
					</html-el:link> / <html-el:link href="javascript:fnGet()">
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
								name="product.editfields" bundle="ProductDefUIResources" /> <mifos:mifoslabel
								name="product.clickpreview" bundle="ProductDefUIResources" /> <mifos:mifoslabel
								name="product.clickcanccat" bundle="ProductDefUIResources" /> <br>
							<mifos:mifoslabel name="product.fieldsrequired" mandatory="yes"
								bundle="ProductDefUIResources" /></td>
						</tr>
					</table>
					<br>
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<font class="fontnormalRedBold"><html-el:errors
							bundle="ProductDefUIResources" /></font>
						<tr>
							<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
								name="product.categorydet" bundle="ProductDefUIResources" /><br>
							<br>
							</td>
						</tr>
						<tr class="fontnormal">
							<td width="20%" align="right"><span class="mandatorytext"></span>
							<mifos:mifoslabel name="product.categoryname" mandatory="yes"
								bundle="ProductDefUIResources" />:</td>
							<td width="80%"><html-el:text property="productCategoryName"
								value="${productCategoryActionForm.productCategoryName}" /></td>
						</tr>
						<tr class="fontnormal">
							<td align="right" valign="top"><mifos:mifoslabel
								name="product.desc" bundle="ProductDefUIResources" />:</td>
							<td><html-el:textarea style="width:320px; height:110px;"
								property="productCategoryDesc"
								value="${productCategoryActionForm.productCategoryDesc}"></html-el:textarea>
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel name="product.status"
								mandatory="yes" bundle="ProductDefUIResources" />:</td>
							<td><mifos:select
								property="productCategoryStatus"
								name="productCategoryActionForm" style="width:136px;">
								<c:forEach var="PrdCategoryStatusList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'PrdCategoryStatusList')}" >
									<html-el:option value="${PrdCategoryStatusList.id}">${PrdCategoryStatusList.name}</html-el:option>
								</c:forEach>
							</mifos:select></td>
						</tr>
					</table>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<html-el:hidden property="method" value="managePreview" />
					<html-el:hidden property="globalPrdCategoryNum"
							value="${BusinessKey.globalPrdCategoryNum}" />
					<html-el:hidden property="searchNode(search_name)"
						value="ProductCategories" />
					<table width="93%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:submit styleClass="buttn"
								style="width:70px">
								<mifos:mifoslabel name="product.preview"
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
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
