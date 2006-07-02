<!-- 

/**

 * editChecklists.jsp    version: 1.0

 

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
				form.action="mifosprocessflowaction.do";
				form.submit();
			}
		//-->
		</script>
		<html:form action="/mifosprocessflowaction">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"><a
						href="../admin.htm"><mifos:mifoslabel name="product.admin"
						bundle="ProductDefUIResources" /></a> / </span><span
						class="fontnormal8ptbold"> <mifos:mifoslabel
						name="product.defprocessflow" bundle="ProductDefUIResources" /> </span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
					<table width="98%" border="0" cellspacing="0" cellpadding="3">
						<tr>
							<td class="headingorange"><mifos:mifoslabel
								name="product.defclientprocessflow"
								bundle="ProductDefUIResources" /> <br>
							<br>
							</td>
						</tr>
						<tr>
							<td valign="top" class="fontnormalbold"><mifos:mifoslabel
								name="product.clients" bundle="ProductDefUIResources" /></td>
						</tr>
						<tr>
							<td valign="top" class="fontnormal"><mifos:mifoslabel
								name="product.optionalclientstatus"
								bundle="ProductDefUIResources" />:</td>
						</tr>
						<tr>
							<td width="35%" valign="top"><span class="fontnormal"> <html-el:checkbox
								property="clientsSubmitApproval"
								value="${requestScope.Context.valueObject.clientsSubmitApproval}">
								<mifos:mifoslabel name="product.submitforapproval"
									bundle="ProductDefUIResources" />
							</html-el:checkbox> </span></td>
						</tr>
					</table>
					<br>
					<table width="98%" border="0" cellspacing="0" cellpadding="3">
						<tr>
							<td valign="top" class="fontnormalbold"><mifos:mifoslabel
								name="product.groups" bundle="ProductDefUIResources" /></td>
						</tr>
						<tr>
							<td valign="top" class="fontnormal"><mifos:mifoslabel
								name="product.optionalgroupstatus"
								bundle="ProductDefUIResources" />:</td>
						</tr>
						<tr>
							<td width="35%" valign="top"><span class="fontnormal"> <html-el:checkbox
								property="groupsSubmitApproval"
								value="${requestScope.Context.valueObject.groupsSubmitApproval}">
								<mifos:mifoslabel name="product.submitforapproval"
									bundle="ProductDefUIResources" />
							</html-el:checkbox> </span></td>
						</tr>
						<tr>
							<td valign="top" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="98%" border="0" cellspacing="0" cellpadding="3">
						<tr>
							<td class="headingorange"><mifos:mifoslabel
								name="product.defaccountprocessflow"
								bundle="ProductDefUIResources" /> <br>
							<br>
							</td>
						</tr>
						<tr>
							<td valign="top" class="fontnormalbold"><mifos:mifoslabel
								name="product.loans" bundle="ProductDefUIResources" /></td>
						</tr>
						<tr>
							<td valign="top" class="fontnormal"><mifos:mifoslabel
								name="product.optionalloanaccountstatus"
								bundle="ProductDefUIResources" /></td>
						</tr>
						<tr>
							<td width="35%" valign="top"><span class="fontnormal"> <html-el:checkbox
								property="loansSubmitApproval"
								value="${requestScope.Context.valueObject.loansSubmitApproval}">
								<mifos:mifoslabel name="product.submitforapproval"
									bundle="ProductDefUIResources" />
							</html-el:checkbox> </span></td>
						</tr>
						<tr>
							<td valign="top" class="fontnormal"><span class="fontnormal"> <html-el:checkbox
								property="loanDisbursLO"
								value="${requestScope.Context.valueObject.loanDisbursLO}">
								<mifos:mifoslabel name="product.submitforapproval"
									bundle="ProductDefUIResources" />
							</html-el:checkbox> </span></td>
						</tr>
					</table>
					<br>
					<table width="98%" border="0" cellspacing="0" cellpadding="3">
						<tr>
							<td valign="top" class="fontnormalbold"><mifos:mifoslabel
								name="product.savings" bundle="ProductDefUIResources" /></td>
						</tr>
						<tr>
							<td valign="top" class="fontnormal"><mifos:mifoslabel
								name="product.optionalsavingsaccountstatus"
								bundle="ProductDefUIResources" /></td>
						</tr>
						<tr>
							<td width="35%" valign="top"><span class="fontnormal"> <html-el:checkbox
								property="savingsSubmitApproval"
								value="${requestScope.Context.valueObject.savingsSubmitApproval}">
								<mifos:mifoslabel name="product.submitforapproval"
									bundle="ProductDefUIResources" />
							</html-el:checkbox> </span></td>
						</tr>
					</table>
					<br>
					<table width="98%" border="0" cellspacing="0" cellpadding="3">
						<tr>
							<td valign="top" class="fontnormalbold"><mifos:mifoslabel
								name="product.insurance" bundle="ProductDefUIResources" /></td>
						</tr>
						<tr>
							<td valign="top" class="fontnormal"><mifos:mifoslabel
								name="product.optionalinsuranceaccountstatus"
								bundle="ProductDefUIResources" /></td>
						</tr>
						<tr>
							<td width="35%" valign="top"><span class="fontnormal"> <html-el:checkbox
								property="insuranceSubmitApproval"
								value="${requestScope.Context.valueObject.insuranceSubmitApproval}">
								<mifos:mifoslabel name="product.submitforapproval"
									bundle="ProductDefUIResources" />
							</html-el:checkbox> </span></td>
						</tr>
					</table>
					<table width="98%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td class="blueline">&nbsp;</td>
						</tr>
					</table>
					<html-el:hidden property="method" value="update" /> <br>
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
