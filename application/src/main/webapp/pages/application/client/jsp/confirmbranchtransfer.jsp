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
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.ClientUIResources"/>

<tiles:insert definition=".detailsCustomer">
	<tiles:put name="body" type="string">
	<span id="page.id" title="ConfirmBranchTransfer" />	
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<script language="javascript">
  
   function goToCancelPage(){
	clientTransferActionForm.action="clientTransferAction.do?method=cancel";
	clientTransferActionForm.submit();
  }
</script>
		<html-el:form action="clientTransferAction.do?method=transferToBranch"
			onsubmit="func_disableSubmitBtn('submitButton');">
		<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
<c:set var="BusinessKey" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"/>


			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"> 
							<customtags:headerLink/>
						</span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange"><span class="heading"> <c:out
								value="${BusinessKey.displayName}" /> - </span><mifos:mifoslabel
								name="client.EditPreviewPersonalReviewTitle"
								bundle="ClientUIResources"></mifos:mifoslabel> <span></span></td>
							<td></td>
						</tr>
						<tr>
							<td class="fontnormal">
								<fmt:message key="client.ConfirmBranchInstructions">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}"/></fmt:param>
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"/></fmt:param>
								</fmt:message></td>
						</tr>
					</table>
					<br>

					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<font class="fontnormalRedBold"><span id="confirmbranchtransfer.error.message"><html-el:errors
								bundle="ClientUIResources" /></span></font>
							<td class="fontnormalbold"><span class="fontnormal"><br>
							</span><mifos:mifoslabel name="client.EditBranchMembershipLink" bundle="ClientUIResources"></mifos:mifoslabel>: <span class="fontnormal"><c:out
								value="${sessionScope.clientTransferActionForm.officeName}" /> <br>
							</span></td>
						</tr>
					</table>

					<!-- Submit and cancel buttons -->
					<table width="93%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="93%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center">&nbsp; <html-el:submit styleId="confirmbranchtransfer.button.submit" property="submitButton"
								styleClass="buttn">
								<mifos:mifoslabel name="button.submit"
									bundle="ClientUIResources"></mifos:mifoslabel>
							</html-el:submit> &nbsp; &nbsp; <html-el:button styleId="confirmbranchtransfer.button.cancel"
								onclick="goToCancelPage();" property="cancelButton"
								styleClass="cancelbuttn">
								<mifos:mifoslabel name="button.cancel"
									bundle="ClientUIResources"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
					</table>
					<!-- Submit and cancel buttons end --></td>
				</tr>
			</table>
			<br>
			<td></td>
			<tr></tr>
			<table></table>
		</html-el:form>

	</tiles:put>
</tiles:insert>

