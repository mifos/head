<!-- 

/**

 * previewProductsMix.jsp    version: 1.0

 

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
				form.method.value="cancelCreate";
				form.action="productMixAction.do";
				form.submit();
			}
			function fnEdit(form) {
				form.method.value="previous";
				form.action="productMixAction.do";
				form.submit();
			}
			function func_disableSubmitBtn(){
				document.getElementById("submitBut").disabled=true;
			}
		//-->
		</script>
		<html-el:form action="/productMixAction"
			onsubmit="func_disableSubmitBtn();">

			<c:set
				value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
				var="BusinessKey" />



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
							<td width="27%">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td><img src="pages/framework/images/timeline/tick.gif"
										width="17" height="17"></td>
									<td class="timelineboldgray"><mifos:mifoslabel
										name="product.product" bundle="ProductDefUIResources" /> <mifos:mifoslabel
										name="product.mix" bundle="ProductDefUIResources" /> <mifos:mifoslabel
										name="product.inf" bundle="ProductDefUIResources" /></td>
								</tr>
							</table>
							</td>
							<td width="73%" align="right">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td><img
										src="pages/framework/images/timeline/bigarrow.gif" width="17"
										height="17"></td>
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
							<td class="headingorange"><span class="heading"> <mifos:mifoslabel
								name="product.addnew" bundle="ProductDefUIResources" /> <mifos:mifoslabel
								name="product.pro" bundle="ProductDefUIResources" /> <mifos:mifoslabel
								name="product.mix" bundle="ProductDefUIResources" /> - </span> <mifos:mifoslabel
								name="product.review" bundle="ProductDefUIResources" /></td>
						</tr>
						<tr>
							<td class="fontnormal"><mifos:mifoslabel
								name="product.previewfields" bundle="ProductDefUIResources" />
							<mifos:mifoslabel name="product.clicksubmit"
								bundle="ProductDefUIResources" /> <mifos:mifoslabel
								name="product.clickcancinfo" bundle="ProductDefUIResources" />
							</td>
						</tr>
						<tr>
							<td class="fontnormalRedBold">
						<c:set
							value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'showMessage')}"
							var="showMessage" />
				
							<c:if
								test="${showMessage}">
								<mifos:mifoslabel name="product.productnotallowedto"
								bundle="ProductDefUIResources" />
								<c:out value="${BusinessKey.prdOfferingName}" />
							</c:if>
							<br>
							<c:forEach
								items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'tabAllowed')}"
								var="tabAllowed">
								<c:out value="${tabAllowed.prdOfferingName}" />
								<br>
							</c:forEach>
							<br>
							<c:if
								test="${showMessage}">
								<mifos:mifoslabel name="product.tocontinueclickonsubmit"
								bundle="ProductDefUIResources" />
							</c:if>
							<c:if
								test="${showMessage}">
								<mifos:mifoslabel name="product.othersprdwillbeupdatedauto"
								bundle="ProductDefUIResources" />
							</c:if>
							</td>
						</tr>

					</table>
					<br>
					<font class="fontnormalRedBold"><html-el:errors
						bundle="ProductDefUIResources" /></font>
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="100%" height="23" class="fontnormalbold"><mifos:mifoslabel
								name="product.product" bundle="ProductDefUIResources" /> <mifos:mifoslabel
								name="product.mixdetails" bundle="ProductDefUIResources" /></td>
						</tr>
						<tr>
							<td height="23" class="fontnormal"><mifos:mifoslabel
								name="product.productType" bundle="ProductDefUIResources" /> :
							<span class="fontnormal"> <c:forEach
								items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'ProductTypeList')}"
								var="productTypeObject">
								<c:if
									test="${productTypeObject.productTypeID eq productMixActionForm.productType}">
									<c:out value="${productTypeObject.name}" />
								</c:if>
							</c:forEach> </span> <br>
							<mifos:mifoslabel name="product.prodinstname"
								bundle="ProductDefUIResources" /> : <span class="fontnormal"><c:out
								value="${BusinessKey.prdOfferingName}" /></span> <br><br>
							<span class="fontnormal"> <mifos:mifoslabel
								name="product.allowedProducts" bundle="ProductDefUIResources" />
							:</span> <span class="fontnormal"> <br>
							<c:forEach
								items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'AllowedProductList')}"
								var="allowedProduct">
								<c:out value="${allowedProduct.prdOfferingName}" />
								<br>
							</c:forEach> <br>
							<span class="fontnormal"> <mifos:mifoslabel
								name="product.notAllowedProducts" bundle="ProductDefUIResources" />
							:</span> <span class="fontnormal"> <br>
							<c:forEach
								items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'NotAllowedProductList')}"
								var="notAllowedProduct">
								<c:out value="${notAllowedProduct.prdOfferingName}" />
								<br>
							</c:forEach> <br></td>
						</tr>

					</table>
					<table width="93%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td class="blueline"><span class="fontnormal"> <html-el:button
								property="edit" styleClass="insidebuttn"
								onclick="fnEdit(this.form)">
								<mifos:mifoslabel name="product.editprdmixinf"
									bundle="ProductDefUIResources" />&nbsp;
										</html-el:button> <br>
							<br>
							</span></td>
						</tr>
					</table>


					<br>

					<table width="93%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:submit styleClass="buttn"
								style="width:70px" property="submitBut">
								<mifos:mifoslabel name="product.butsubmit"
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
			<html-el:hidden property="method" value="create" />
			<html-el:hidden property="currentFlowKey"
				value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
