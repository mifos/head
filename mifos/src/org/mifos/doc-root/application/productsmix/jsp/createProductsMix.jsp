<!--

/**

 * createProductsMix.jsp    version: 1.0



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
<%@taglib uri="/tags/date" prefix="date"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".create">
	<tiles:put name="body" type="string">
		<script>

		
			function fnCancel(form) {
				form.method.value="cancel";
				form.action="productMixAction.do";
				form.submit();
			}
			
			function populateParent(selectBox){
				if(selectBox.selectedIndex > 0)
				{
				  document.productMixActionForm.action="productMixAction.do?method=loadProductInstance";
				  productMixActionForm.submit();
				}
			 }
			function populateDefaultAllowedProduct(selectBox){
				if(selectBox.selectedIndex > 0)
				{
				  document.productMixActionForm.action="productMixAction.do?method=loadDefaultAllowedProduct";
				  productMixActionForm.submit();
				}
			 }
		
		</script>
		<html-el:form action="/productMixAction?method=preview" >
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="350" align="left" valign="top" bgcolor="#FFFFFF">
						<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center" class="heading">
									&nbsp;
								</td>
							</tr>
						</table>
						<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
							<tr>
								<td class="bluetablehead">
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="27%">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src=" pages/framework/images/timeline/bigarrow.gif" width="17" height="17">
														</td>
														<td class="timelineboldorange">
															<mifos:mifoslabel name="product.product"  bundle="ProductDefUIResources" />
															<mifos:mifoslabel name="product.mix" bundle="ProductDefUIResources" />
															<mifos:mifoslabel name="product.inf" bundle="ProductDefUIResources" />
														</td>	
													</tr>
												</table>
											</td>
											<td width="73%" align="right">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17">
														</td>
														<td class="timelineboldorangelight">
															<mifos:mifoslabel name="product.review" bundle="ProductDefUIResources" />
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
						<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
							<tr>
								<td align="left" valign="top" class="paddingleftCreates">
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td class="headingorange">
												<span class="heading"> <mifos:mifoslabel name="product.addnew" bundle="ProductDefUIResources" /> <mifos:mifoslabel name="product.pro"
														bundle="ProductDefUIResources" /> <mifos:mifoslabel name="product.mix" bundle="ProductDefUIResources" /> - </span>
												<mifos:mifoslabel name="product.enter" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.pro" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.mix" bundle="ProductDefUIResources" /> 	 	
												<mifos:mifoslabel name="product.inf" bundle="ProductDefUIResources" />
											</td>
										</tr>
										<tr>
											<td class="fontnormal">
												<mifos:mifoslabel name="product.compfields" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.clickpreview" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.clickcancinfo" bundle="ProductDefUIResources" />
												<br>
												<mifos:mifoslabel name="product.fieldsrequired" mandatory="yes" bundle="ProductDefUIResources" />
											</td>
										</tr>
									</table>
									<br>
									<font class="fontnormalRedBold"><html-el:errors bundle="ProductDefUIResources" /></font>
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td colspan="2" class="fontnormalbold">
												<mifos:mifoslabel name="product.product" bundle="ProductDefUIResources" />
												<mifos:mifoslabel name="product.mixdetails" bundle="ProductDefUIResources" />
												<br>
												<br>
											</td>
										</tr>
										<tr class="fontnormal">
											<td width="30%" align="right">
												<mifos:mifoslabel name="product.productType" mandatory="yes" bundle="ProductDefUIResources" />
												:
											</td>
											<td>
											<mifos:select property="productType"
											onchange="return populateParent(this)" >
												<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'ProductTypeList')}" var="producttype">
													<html-el:option value="${producttype.productTypeID}">${producttype.name}</html-el:option>
												</c:forEach>
											</mifos:select>
											</td>
										</tr>

										<tr class="fontnormal">
											<td width="30%" align="right">
												<mifos:mifoslabel name="product.prodinstname" mandatory="yes" bundle="ProductDefUIResources" />
												:
											</td>
									    	<td>
											<mifos:select property="productInstance"
												onchange="return populateDefaultAllowedProduct(this)" >
												<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'ProductInstanceList')}" var="prdInstName">
													<html-el:option value="${prdInstName.prdOfferingId}">${prdInstName.prdOfferingName}</html-el:option>
												</c:forEach>
											</mifos:select>
											</td>
											
										</tr>

									</table>
									<br>

									<br>
									
									<!-- Allowed products  -->									
									
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr class="fontnormal">
											<td align="right" valign="top">
												<mifos:mifoslabel name="product.removenotallowed" bundle="ProductDefUIResources" />
												:
											</td>
											<td valign="top">
												<table width="80%" border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td class="fontnormal">
															<mifos:mifoslabel name="product.clickrighttoselect" bundle="ProductDefUIResources" />
														</td>
													</tr>
													<tr>
														<td>
															<img src="pages/framework/images/trans.gif" width="1" height="1">
														</td>
													</tr>
												</table>

												<c:set var="NotAllowedProductList" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'NotAllowedProductList')}" />
												<c:set var="AllowedProductList" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'AllowedProductList')}" />
												<mifos:MifosSelect property="productMix" input="NotAllowedProductList" output="AllowedProductList" property1="prdOfferingId" property2="prdOfferingName" multiple="true">
												</mifos:MifosSelect>
											</td>
										</tr>
									</table>

									<!-- End Allowed products  -->	
									<table width="93%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center" class="blueline">
												&nbsp;
											
											</td>
										</tr>
									</table>
									<br>
									<table width="93%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center">
												<html-el:submit styleClass="buttn" onclick="transferData(this.form.productMix);">
													<mifos:mifoslabel name="product.preview" bundle="ProductDefUIResources" />
												</html-el:submit>
												&nbsp;
												<html-el:button property="cancel" styleClass="cancelbuttn" onclick="javascript:fnCancel(this.form)">
													<mifos:mifoslabel name="product.cancel" bundle="ProductDefUIResources" />
												</html-el:button>
											</td>
										</tr>
									</table>
									<br>
								</td>
							</tr>
						</table>
						<html-el:hidden property="method" value="preview" />
						<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
						</html-el:form>
						</tiles:put>
						</tiles:insert>