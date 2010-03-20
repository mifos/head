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
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>


<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
    <span id="page.id" title="AccountAddNotes" />
     
		<script language="javascript">

function goToCancelPage(){
	notesActionForm.action="notesAction.do?method=cancel";
	notesActionForm.submit();
  }
</script>
		<html-el:form
			action="notesAction.do?method=preview&globalAccountNum=${sessionScope.notesActionForm.globalAccountNum}">

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
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="83%" class="headingorange"><span class="heading"><c:out
								value="${sessionScope.notesActionForm.prdOfferingName}" /> # <c:out
								value="${sessionScope.notesActionForm.globalAccountNum}" /> - </span><mifos:mifoslabel
								name="Account.AddNote" bundle="accountsUIResources" /></td>
						</tr>
						<tr>
							<logic:messagesPresent>
								<td><br><font class="fontnormalRedBold"><span id="account_addnotes.error.message"><html-el:errors
									bundle="accountsUIResources" /></span></font></td>
							</logic:messagesPresent>
						</tr>
					</table>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td><br>
							<span class="fontnormal">
					            <c:if test="${sessionScope.notesActionForm.accountTypeId == '1'}">
									<mifos:mifoslabel name="accounts.EnterNoteLoanComplete" bundle="accountsUIResources" />
								</c:if>
								<c:if test="${sessionScope.notesActionForm.accountTypeId == '2'}">
									<mifos:mifoslabel name="accounts.EnterNoteSavingsComplete" bundle="accountsUIResources" />
								</c:if>
					         </span></td>
						</tr>
						<tr>
							<td class="blueline"><img src="pages/framework/images/trans.gif"
								width="10" height="12"></td>
						</tr>

					</table>
					<br>
					<table width="95%" border="0" cellpadding="3" cellspacing="0">

						<tr>
							<td width="7%" align="left" valign="top" class="fontnormalbold">
							<span id="account_addnotes.label.note"><mifos:mifoslabel name="Account.Note"
								bundle="accountsUIResources" mandatory="yes"></mifos:mifoslabel></span></td>
							<td width="93%" align="left" valign="top"><html-el:textarea styleId="account_addnotes.input.note"
								property="comment" cols="37" style="width:320px; height:110px;">
							</html-el:textarea></td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:submit styleId="account_addnotes.button.preview" styleClass="buttn">
								<mifos:mifoslabel name="accounts.preview"
									bundle="accountsUIResources"></mifos:mifoslabel>
							</html-el:submit> &nbsp;&nbsp; <html-el:button styleId="account_addnotes.button.cancel"
								property="cancelBtn" styleClass="cancelbuttn"
								onclick="goToCancelPage()">
								<mifos:mifoslabel name="accounts.cancel"
									bundle="accountsUIResources"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>
			<br>
			<html-el:hidden property="globalAccountNum" value="${sessionScope.notesActionForm.globalAccountNum}"/>
			<html-el:hidden property="accountTypeId" value="${sessionScope.notesActionForm.accountTypeId}"/>
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>


