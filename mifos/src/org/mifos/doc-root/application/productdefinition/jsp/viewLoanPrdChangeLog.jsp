<!-- 

/**

 * loanproductaction.jsp    version: 1.0

 

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
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom" %>
<%@taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<script>
	function returnToDetails(){
		loanproductactionform.action="loanproductaction.do?method=cancelChangeLog";
		loanproductactionform.submit();
	}
	</script>
		<html-el:form method="post" action="loanproductaction.do">
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<c:set
				value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
				var="BusinessKey" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"> <html-el:link href="loanproductaction.do?method=cancelCreate&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="product.admin" bundle="ProductDefUIResources" />
							</html-el:link> / <html-el:link href="loanproductaction.do?method=viewAllLoanProducts&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="product.savingsview" bundle="ProductDefUIResources" />
								<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="ProductDefUIResources" />
								<mifos:mifoslabel name="product.products" bundle="ProductDefUIResources" />
							</html-el:link> / <html-el:link href="loanproductaction.do?method=get&prdOfferingId=${sessionScope.loanproductactionform.prdOfferingId}&randomNUm=${sessionScope.randomNUm}">
								<c:out value="${BusinessKey.prdOfferingName}" />
							</html-el:link></span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" height="24" align="left" valign="top"
						class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
									<td>
										<font class="fontnormalRedBold">
											<html-el:errors bundle="ProductDefUIResources" /> 
										</font>
									</td>
						</tr>
						<tr>
							<td class="headingorange">
								<span class="heading">
									<c:out value="${BusinessKey.prdOfferingName}" />&nbsp;
										-&nbsp;  
								</span>
								<mifos:mifoslabel name="product.changelog"  bundle="ProductDefUIResources"/>
							</td>
						</tr>

						<tr>
							<td class="fontnormal"><br>
								<mifos:mifoslabel name="product.recordCreationDate"  bundle="ProductDefUIResources"/>:&nbsp;
								<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,BusinessKey.createdDate)}" />
							</td>
						</tr>
					</table>
					<br>
					<mifoscustom:mifostabletag rootName="framework" moduleName="components/audit" scope="session" source="auditLogRecords" xmlFileName="AuditLog.xml" passLocale="true"/>
					<br>
					<table width="96%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:button property="returnToAccountDetailsbutton"
								onclick="returnToDetails()"
								styleClass="buttn" style="width:165px;">
								<mifos:mifoslabel name="product.back"
									bundle="ProductDefUIResources" />
							</html-el:button></td>
						</tr>
					</table>
					</td>

				</tr>
			</table>
			<br>
			
			<html-el:hidden property="entityType" value="${param.entityType}"/> 
		</html-el:form>
	</tiles:put>
</tiles:insert>
