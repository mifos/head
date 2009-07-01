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
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.ProductDefinitionResources"/>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<script language="javascript">
		<!--
			function fnCancel() {
				prdconfactionform.action="AdminAction.do?method=load";
				prdconfactionform.submit();
			}
		//-->
		</script>
		<html-el:form action="/prdconfaction.do?method=update">
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
					<td width="70%" align="left" valign="top" class="paddingL15T15">
			
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
										<fmt:message key="product.specLoanNoDaysAccountChanged">
										<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" /></fmt:param>
										</fmt:message>:
										</span></td>
										<td width="53%" valign="top" class="fontnormal"><mifos:mifosnumbertext
											property="latenessDays" maxValue="32767"
											maxlength="4"
											value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'lateness_days')}" size="4"/> <mifos:mifoslabel
											name="product.days" bundle="ProductDefUIResources" /></td>
									</tr>
									
								</table>
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
										<fmt:message key="product.specSavingsDormDaysAccountChanged">
										<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="ProductDefUIResources" /></fmt:param>
										</fmt:message>:
											</span></td>
										<td width="53%" valign="top" class="fontnormal"><mifos:mifosnumbertext
											property="dormancyDays" maxValue="32767"
											maxlength="4"
											value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'dormancy_days')}" size="4"/> <mifos:mifoslabel
											name="product.days" bundle="ProductDefUIResources" /></td>
									</tr>
									<tr>
										<td colspan="2" class="blueline">&nbsp;</td>
									</tr>
								</table>
 				 <br>  
 				 <html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
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
