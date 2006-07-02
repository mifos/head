<!-- 

/**

 * editAcceptedPaymentTypes.jsp    version: 1.0

 

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
			function fnCancel(form) {
				form.method.value="cancel";
				form.action="mifospmttypesaction.do";
				form.submit();
			}
		//-->
		</script>
		<html:form action="/mifospmttypesaction">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"><a
						href="../admin.htm"><mifos:mifoslabel name="product.admin"
						bundle="ProductDefUIResources" /></a> / </span><span
						class="fontnormal8ptbold"><mifos:mifoslabel
						name="product.viewacceptpymttypes" bundle="ProductDefUIResources" /></span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
					<table width="98%" border="0" cellspacing="0" cellpadding="3">
						<tr>
							<td colspan="2" class="headingorange"><mifos:mifoslabel
								name="product.viewacceptpymttypes"
								bundle="ProductDefUIResources" /><br>
							<br>
							</td>
						</tr>
						<tr>
							<td colspan="2" valign="top" class="fontnormalbold"><mifos:mifoslabel
								name="product.clientgroupcenters" bundle="ProductDefUIResources" /></td>
						</tr>
						<tr>
							<td colspan="2" valign="top" class="fontnormal"><mifos:mifoslabel
								name="product.specacceptpymttypescgc"
								bundle="ProductDefUIResources" /></td>
						</tr>
						<tr>
							<td width="35%" align="right" valign="top"><span
								class="fontnormal"> <mifos:mifoslabel
								name="product.pymttypesfees" bundle="ProductDefUIResources" />:
							</span></td>
							<td width="65%" valign="top">
							<table width="80%" border="0" cellspacing="0" cellpadding="1">
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="product.clickpymtselect" bundle="ProductDefUIResources" /></td>
								</tr>
								<tr>
									<td><img src="pages/framework/images/trans.gif" width="1"
										height="1"></td>
								</tr>
							</table>
							<mifos:MifosSelect property="xyz" inputMap="dsfdsf" /></td>
						</tr>
					</table>
					<br>
					<table width="98%" border="0" cellspacing="0" cellpadding="3">
						<tr>
							<td colspan="2" valign="top" class="fontnormalbold"><mifos:mifoslabel
								name="product.loans" bundle="ProductDefUIResources" /></td>
						</tr>
						<tr>
							<td colspan="2" valign="top" class="fontnormal"><mifos:mifoslabel
								name="product.specacceptpymtloans"
								bundle="ProductDefUIResources" /></td>
						</tr>
						<tr>
							<td width="35%" align="right" valign="top"><span
								class="fontnormal"> <mifos:mifoslabel
								name="product.pymttypesdisbur" bundle="ProductDefUIResources" />:
							</span></td>
							<td width="65%" valign="top">
							<table width="80%" border="0" cellspacing="0" cellpadding="1">
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="product.clickpymtselect" bundle="ProductDefUIResources" /></td>
								</tr>
								<tr>
									<td><img src="pages/framework/images/trans.gif" width="1"
										height="1"></td>
								</tr>
							</table>
							<mifos:MifosSelect property="xyz" inputMap="dsfdsf" /></td>
						</tr>
						<tr>
							<td align="right" valign="top" class="fontnormal"><mifos:mifoslabel
								name="product.pymttypesrepay" bundle="ProductDefUIResources" />:</td>
							<td valign="top">
							<table width="80%" border="0" cellspacing="0" cellpadding="1">
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="product.clickpymtselect" bundle="ProductDefUIResources" /></td>
								</tr>
								<tr>
									<td><img src="pages/framework/images/trans.gif" width="1"
										height="1"></td>
								</tr>
							</table>
							<mifos:MifosSelect property="xyz" inputMap="dsfdsf" /></td>
						</tr>
					</table>
					<br>
					<table width="98%" border="0" cellspacing="0" cellpadding="3">
						<tr>
							<td colspan="2" valign="top" class="fontnormalbold"><mifos:mifoslabel
								name="product.savings" bundle="ProductDefUIResources" /></td>
						</tr>
						<tr>
							<td colspan="2" valign="top" class="fontnormal"><mifos:mifoslabel
								name="product.specpymttypessavings"
								bundle="ProductDefUIResources" /></td>
						</tr>
						<tr>
							<td width="35%" align="right" valign="top" class="fontnormal"><mifos:mifoslabel
								name="product.pymttypeswithdrwals"
								bundle="ProductDefUIResources" />:</td>
							<td width="65%" valign="top">
							<table width="80%" border="0" cellspacing="0" cellpadding="1">
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="product.clickpymtselect" bundle="ProductDefUIResources" /></td>
								</tr>
								<tr>
									<td><img src="pages/framework/images/trans.gif" width="1"
										height="1"></td>
								</tr>
							</table>
							<mifos:MifosSelect property="xyz" inputMap="dsfdsf" /></td>
						</tr>
						<tr>
							<td align="right" valign="top" class="fontnormal"><mifos:mifoslabel
								name="product.pymttypesdeposits" bundle="ProductDefUIResources" />:</td>
							<td valign="top">
							<table width="80%" border="0" cellspacing="0" cellpadding="1">
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="product.clickpymtselect" bundle="ProductDefUIResources" /></td>
								</tr>
								<tr>
									<td><img src="pages/framework/images/trans.gif" width="1"
										height="1"></td>
								</tr>
							</table>
							<mifos:MifosSelect property="xyz" inputMap="dsfdsf" /></td>
						</tr>
					</table>
					<br>
					<table width="98%" border="0" cellspacing="0" cellpadding="3">
						<tr>
							<td colspan="2" valign="top" class="fontnormalbold"><mifos:mifoslabel
								name="product.insurance" bundle="ProductDefUIResources" /></td>
						</tr>
						<tr>
							<td colspan="2" valign="top" class="fontnormal"><mifos:mifoslabel
								name="product.specacceptpymtinsurance"
								bundle="ProductDefUIResources" /></td>
						</tr>
						<tr>
							<td width="35%" align="right" valign="top" class="fontnormal"><mifos:mifoslabel
								name="product.pymttypesdeposits" bundle="ProductDefUIResources" />:</td>
							<td width="65%" valign="top">
							<table width="80%" border="0" cellspacing="0" cellpadding="1">
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="product.clickpymtselect" bundle="ProductDefUIResources" /></td>
								</tr>
								<tr>
									<td><img src="pages/framework/images/trans.gif" width="1"
										height="1"></td>
								</tr>
							</table>
							<mifos:MifosSelect property="xyz" inputMap="dsfdsf" /></td>
						</tr>
					</table>
					<table width="98%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<html-el:hidden property="method" value="update" />
					<table width="98%" border="0" cellpadding="0" cellspacing="0">
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
		</html:form>
	</tiles:put>
</tiles:insert>
