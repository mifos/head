<!--

/**

 * CreateCategory.jsp    version: 1.0



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

<tiles:insert definition=".create">
	<tiles:put name="body" type="string">
		<script language="javascript">
		<!--
			function fnCancel(form) {
				form.action="AdminAction.do?method=load";
				form.submit();
			}
		//-->
		</script>
		<html-el:form action="/productCategoryAction.do?method=createPreview">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="350" align="left" valign="top" bgcolor="#FFFFFF">
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
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td width="27%" align="left">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><html-el:img
												src="pages/framework/images/timeline/bigarrow.gif"
												width="17" height="17" /></td>
											<td class="timelineboldorange"><mifos:mifoslabel
												name="product.productcatinfo" bundle="ProductDefUIResources" /></td>
										</tr>
									</table>
									</td>
									<td width="73%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><html-el:img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17" height="17" /></td>
											<td class="timelineboldorangelight"><mifos:mifoslabel
												name="product.review" bundle="ProductDefUIResources" /></td>
										</tr>
									</table>
									</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0" class="bluetableborder">
						<tr>
							<td align="left" valign="top" class="paddingleftCreates">
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td class="headingorange"><span class="heading"><mifos:mifoslabel
										name="product.define" bundle="ProductDefUIResources" /> - </span><mifos:mifoslabel
										name="product.categoryinfo" bundle="ProductDefUIResources" /></td>
								</tr>
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="product.compfields" bundle="ProductDefUIResources" /> <mifos:mifoslabel
										name="product.clickpreview" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.clickcanc"
										bundle="ProductDefUIResources" /><br>
									<mifos:mifoslabel name="product.fieldsrequired" mandatory="yes"
										bundle="ProductDefUIResources" /></td>
								</tr>
							</table>
							<br>
							<font class="fontnormalRedBold"><html-el:errors
								bundle="ProductDefUIResources" /></font>
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="product.categorydet" bundle="ProductDefUIResources" /><br>
									<br>
									</td>
								</tr>
								<tr class="fontnormal">
									<td width="20%" align="right"><mifos:mifoslabel
										name="product.producttype" mandatory="yes"
										bundle="ProductDefUIResources" />:</td>
									<td width="80%"><mifos:select name="productCategoryActionForm"
										property="productType" style="width:136px;">
										<c:forEach var="ProductTypeList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'ProductTypeList')}" >
											<html-el:option value="${ProductTypeList.productTypeID}">${ProductTypeList.name}</html-el:option>
										</c:forEach>
									</mifos:select></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="product.categoryname"
										mandatory="yes" bundle="ProductDefUIResources" />:</td>
									<td><mifos:mifosalphanumtext property="productCategoryName" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right" valign="top"><mifos:mifoslabel
										name="product.categorydesc" bundle="ProductDefUIResources" />:</td>
									<td><html-el:textarea style="width:320px; height:110px;"
										property="productCategoryDesc"></html-el:textarea></td>
								</tr>
								<html-el:hidden property="method" value="preview" />
								<html-el:hidden property="input" value="admin" />
								<html-el:hidden property="productCategoryStatus" value="1" />
							</table>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center" class="blueline">&nbsp;</td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center"><html-el:submit styleClass="buttn"
										style="width:70px">
										<mifos:mifoslabel name="product.preview"
											bundle="ProductDefUIResources" />
									</html-el:submit> &nbsp; <html-el:button property="cancel"
										styleClass="cancelbuttn" style="width:70px"
										onclick="javascript:fnCancel(this.form)">
										<mifos:mifoslabel name="product.cancel"
											bundle="ProductDefUIResources" />
									</html-el:button></td>
								</tr>
							</table>
							<br>
							</td>
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
