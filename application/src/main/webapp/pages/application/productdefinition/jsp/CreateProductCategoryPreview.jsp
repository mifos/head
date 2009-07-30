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

<tiles:insert definition=".create">
	<tiles:put name="body" type="string">
	<span style="display: none" id="page.id">CreateProductCategoryPreview</span>
	

		<script language="javascript">
		<!--
			function fnCancel(form) {
				form.action="AdminAction.do?method=load";
				form.submit();
			}
			function fnEdit(form) {
				form.action="productCategoryAction.do?method=createPrevious";
				form.submit();
			}

			function func_disableSubmitBtn(){
				document.getElementById("submitBut").disabled=true;
			}
		//-->
		</script>

		<html-el:form action="/productCategoryAction.do?method=create" onsubmit="func_disableSubmitBtn();">
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
											<td><img src="pages/framework/images/timeline/tick.gif"
												width="17" height="17"></td>
											<td class="timelineboldgray"><mifos:mifoslabel
												name="product.productcatinfo" bundle="ProductDefUIResources" /></td>
										</tr>
									</table>
									</td>
									<td width="73%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/bigarrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorange"><mifos:mifoslabel
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
										name="product.review" bundle="ProductDefUIResources" /></td>
								</tr>
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="product.previewfields" bundle="ProductDefUIResources" />
									<mifos:mifoslabel name="product.clicksubmit"
										bundle="ProductDefUIResources" />
										<mifos:mifoslabel
										name="product.clickcancinfo" bundle="ProductDefUIResources" /></td>
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
										name="product.producttype" bundle="ProductDefUIResources" />:

									<span class="fontnormal">
									<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'ProductTypeList')}"
												var="productTypeObject">
												<c:if
													test="${productTypeObject.productTypeID eq productCategoryActionForm.productType}">
													<c:out value="${productTypeObject.name}" />
												</c:if>
									</c:forEach>
									</span><br>
									<mifos:mifoslabel
										name="product.categoryname" bundle="ProductDefUIResources" isColonRequired="yes"/>
									<span class="fontnormal"><c:out
										value="${productCategoryActionForm.productCategoryName}" /><br>
									<br>
									</span><mifos:mifoslabel name="product.desc"
										bundle="ProductDefUIResources" /><span class="fontnormal"><br>
									<c:out
										value="${productCategoryActionForm.productCategoryDesc}" />
									<br>
									<br>
									</span><span class="fontnormal"> <br>
									 <html-el:hidden
										property="input" value="admin" /> <html-el:button
										property="edit" styleClass="insidebuttn"
										onclick="fnEdit(this.form)">
										<mifos:mifoslabel name="product.buteditcat"
											bundle="ProductDefUIResources" />
									</html-el:button> </span></td>
								</tr>
							</table>
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

