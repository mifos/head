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


<tiles:insert definition=".clientsacclayoutmenu">
	<tiles:put name="body" type="string">
	<span style="display: none" id="page.id">ClientsAccounts</span>
		<html-el:form action="custSearchAction.do">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL10">
					<table width="90%" border="0" cellpadding="0" cellspacing="3">
						<tr>
							<td align="left" valign="top"><span class="headingorange"> <c:set
								var="Office"
								value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'Office')}" />
							<c:set var="isCenterHeirarchyExists"
								value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'GrpHierExists')}" />

								<c:set var="OfficesList"
								value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'OfficesList')}" />
							<c:out value='${Office}' /> <br>
							</span><span class="fontnormalbold"> <mifos:mifoslabel
								name="CustomerSearch.revieweditinstruction1" /> <mifos:mifoslabel
								name="${ConfigurationConstants.CLIENT}" />, <c:choose>
								<c:when test="${isCenterHeirarchyExists==Constants.YES}">
									<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />,&nbsp;
	                  		<mifos:mifoslabel
										name="${ConfigurationConstants.CENTER}" />,
	                  	</c:when>
								<c:otherwise>
									<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />,
	                  		</c:otherwise>
							</c:choose> <mifos:mifoslabel name="CustomerSearch.revieweditinstruction2" /> </span></td>
						</tr>

					</table>
					<br>
					<table width="90%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="313" valign="top">
							<table width="100%" border="0" cellspacing="0" cellpadding="4">
								<tr class="fontnormal">
									<td width="100%" colspan="2" class="bglightblue"><span
										class="heading"> <mifos:mifoslabel
										name="CustomerSearch.search" /> </span></td>
								</tr>
								<font class="fontnormalRedBold"><span id="clients_accounts.error.message"><html-el:errors
									bundle="CustomerSearchUIResources" /></span> </font>
							</table>
							<table width="90%" border="0" cellspacing="0" cellpadding="4">
								<tr>
									<td class="paddingbottom03"><span class="fontnormal" id="clients_accounts.label.search"> <mifos:mifoslabel
										name="CustomerSearch.searchstring" /> </span></td>
								</tr>
							</table>

							<table border="0" cellpadding="4" cellspacing="0">
								<tr>
									<td><html-el:text styleId="clients_accounts.input.search" property="searchString" maxlength="200" /></td>
									<td class="paddingleft05notop"><html-el:select
										style="width:136px;" property="officeId">
										<html-el:option value="0">
											<mifos:mifoslabel name="CustomerSearch.all" /> <mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}" /><mifos:mifoslabel name="CustomerSearch.s" />
										</html-el:option>
										<html-el:options collection="OfficesList" property="officeId"
											labelProperty="officeName" />
									</html-el:select></td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td align="right" class="paddingleft05notop"><html-el:submit
										styleId="clients_accounts.button.search" styleClass="buttn">
										<mifos:mifoslabel name="CustomerSearch.search" />
									</html-el:submit></td>
								</tr>
							</table>
							</td>
							<td width="101" align="center" valign="top" class="headingorange">
							<mifos:mifoslabel name="CustomerSearch.or" />&nbsp;</td>
							<td width="287" valign="top">
							<table width="100%" border="0" cellspacing="0" cellpadding="4">
								<tr class="fontnormal">
									<td width="100%" colspan="2" class="bglightblue"><span
										class="heading"> <mifos:mifoslabel
										name="CustomerSearch.select" />&nbsp; <mifos:mifoslabel
										name="${ConfigurationConstants.BRANCHOFFICE}" /> </span></td>
								</tr>
							</table>
							<div id="Layer2"
								style="border: 1px solid #CECECE; height:100px; width:100%; overflow: auto; padding:6px; margin-top:5px;">
							<span class="fontnormal">
							
									<c:choose>
										<c:when test="${ not empty OfficesList }">
										
							
							
							 <c:forEach
								items="${OfficesList}" var="office">
								<html-el:link styleId="client_accounts.link.selectBranchOffice" action="custSearchAction.do?method=preview&officeId=${office.officeId}&currentFlowKey=${requestScope.currentFlowKey}"
									>
									<c:out value="${office.officeName}" />
								</html-el:link>
								<br>
							</c:forEach> 
									  </c:when>
									<c:otherwise>
											<mifos:mifoslabel name="CustomerSearch.noEntityAvailablePrefix"/>
											<mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}"/>
											<mifos:mifoslabel name="CustomerSearch.noEntityAvailableSuffix"/>
									</c:otherwise>
									</c:choose>
							
							
							
							</span></div>
							</td>
						</tr>
					</table>
					<br>
					<br>
					</td>
				</tr>
			</table>
			<br>
		<html-el:hidden property="method" value="mainSearch" />	
		<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
