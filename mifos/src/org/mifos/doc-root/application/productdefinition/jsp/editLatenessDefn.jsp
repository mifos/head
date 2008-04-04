<!-- 

/**

 * editLatenessDefn.jsp    version: 1.0

 

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

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<script language="javascript">
		<!--
			function fnCancel() {
				prdconfigurationactionform.method.value="cancel";
				prdconfigurationactionform.action="prdconfigurationaction.do";
				prdconfigurationactionform.submit();
			}
		//-->
		</script>
		<html-el:form action="/prdconfigurationaction">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"><html-el:link
						href="javascript:fnCancel()">
						<mifos:mifoslabel name="product.admin"
							bundle="ProductDefUIResources" />
					</html-el:link> / </span><span class="fontnormal8ptbold"><mifos:mifoslabel
						name="product.viewlatedef" bundle="ProductDefUIResources" /></span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15"><c:forEach
						var="productType" items="${requestScope.ProductTypeList}"
						varStatus="loopStatus">
						<bean:define id="ctr">
							<c:out value="${loopStatus.index}" />
						</bean:define>

						<c:choose>
							<c:when test="${productType.productTypeID eq 1}">
								<table width="98%" border="0" cellspacing="0" cellpadding="3">
									<tr>
										<td colspan="2" class="headingorange"><mifos:mifoslabel
											name="product.setlatedef" bundle="ProductDefUIResources" /><br>
											
										<br>
																						<font class="fontnormalRedBold"><html-el:errors
								bundle="ProductDefUIResources" /> </font>
		
										<span class="fontnormalbold"><mifos:mifoslabel
											name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" /></span></td>
									</tr>
									<tr>
										<td width="47%"><span class="fontnormal">
										<mifos:mifoslabel name="product.specloannodays" bundle="ProductDefUIResources" />
										<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
										<mifos:mifoslabel name="product.loanaccountchanged" bundle="ProductDefUIResources" isColonRequired="yes"/></span></td>
										<td width="53%" valign="top" class="fontnormal"><mifos:mifosnumbertext
											property="productType[${ctr}].latenessDays" maxValue="32767"
											value="${productType.latenessDays}" size="4"/> <mifos:mifoslabel
											name="product.days" bundle="ProductDefUIResources" /></td>
									</tr>
									<html-el:hidden property="productType[${ctr}].dormancyDays"
										value="${productType.dormancyDays}" />
									<html-el:hidden property="productType[${ctr}].productTypeID"
										value="${productType.productTypeID}" />
									<html-el:hidden property="productType[${ctr}].versionNo"
										value="${productType.versionNo}" />
								</table>
							</c:when>
							<c:otherwise>
								<table width="98%" border="0" cellpadding="0" cellspacing="0">
									<tr>
										<td class="blueline">&nbsp;</td>
									</tr>
									<tr>
										<td class="headingorange"><br>
										<mifos:mifoslabel name="product.setdormdef"
											bundle="ProductDefUIResources" /></td>
									</tr>
									<tr>
										<td><br>
										<span class="fontnormalbold"><mifos:mifoslabel
											name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" /></span></td>
									</tr>
								</table>
								<table width="98%" border="0" cellspacing="0" cellpadding="3">
									<tr>
										<td width="47%" class="fontnormalbold"><span
											class="fontnormal"> 
											<mifos:mifoslabel name="product.specsavdormdays" bundle="ProductDefUIResources" />
 										    <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" />
											<mifos:mifoslabel name="product.savingsaccountchanged" bundle="ProductDefUIResources" isColonRequired="yes"/>
											</span></td>
										<td width="53%" valign="top" class="fontnormal"><mifos:mifosnumbertext
											property="productType[${ctr}].dormancyDays" maxValue="32767"
											value="${productType.dormancyDays}" size="4"/> <mifos:mifoslabel
											name="product.days" bundle="ProductDefUIResources" /></td>
									</tr>
									<html-el:hidden property="productType[${ctr}].latenessDays"
										value="${productType.latenessDays}" />
									<html-el:hidden property="productType[${ctr}].productTypeID"
										value="${productType.productTypeID}" />
									<html-el:hidden property="productType[${ctr}].versionNo"
										value="${productType.versionNo}" />
									<tr>
										<td colspan="2" class="blueline">&nbsp;</td>
									</tr>
								</table>
							</c:otherwise>
						</c:choose>

					</c:forEach> <!--				<br>
					<table width="98%" border="0" cellspacing="0" cellpadding="3">
						<tr>
							<td colspan="2" class="headingorange"><mifos:mifoslabel
								name="product.setpencal" bundle="ProductDefUIResources"/><br>
							<br>
							</td>
						</tr>
						<tr>
							<td width="47%" align="right" valign="top"><span
								class="fontnormal"><mifos:mifoslabel
								name="product.specpencal" bundle="ProductDefUIResources"/>:</span></td>
							<td width="53%" valign="top">
							<table width="80%" border="0" cellspacing="0" cellpadding="0">
								<tr class="fontnormal">
									<td width="29%"><input name="radiobutton" type="radio"
										value="radiobutton" checked><mifos:mifoslabel
								name="product.automatic" bundle="ProductDefUIResources"/></td>
									<td width="71%">&nbsp;</td>
								</tr>
								<tr class="fontnormal">
									<td><input type="radio" name="radiobutton" value="radiobutton">
									<mifos:mifoslabel
								name="product.manual" bundle="ProductDefUIResources"/></td>
									<td>&nbsp;</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					<table width="98%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td class="blueline">&nbsp;</td>
						</tr>
					</table>-->
 					<html-el:hidden property="method" value="update" /> <br>
					<table width="98%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:submit styleClass="buttn">
								<mifos:mifoslabel name="product.butsubmit"
									bundle="ProductDefUIResources" />
							</html-el:submit> &nbsp; <html-el:button property="cancel"
								styleClass="cancelbuttn" 
								onclick="javascript:fnCancel()">
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
