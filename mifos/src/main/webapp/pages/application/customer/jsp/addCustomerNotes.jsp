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
<%@ taglib uri="/sessionaccess" prefix="session"%>


<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<input type="hidden" id="page.id" value="AddCustomerNotes"/>

		<script language="javascript">

function goToCancelPage(){
	customerNotesActionForm.action="customerNotesAction.do?method=cancel";
	customerNotesActionForm.submit();
  }
</script>
		<html-el:form action="customerNotesAction.do?method=preview">
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
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
								value="${sessionScope.customerNotesActionForm.customerName}" />&nbsp;-
								 </span><mifos:mifoslabel name="Customer.addnoteheading" /></td>
						</tr>
						<tr>
							<logic:messagesPresent>
								<td><br><font class="fontnormalRedBold"><span id="addCustomerNotes.error.message"><html-el:errors
									bundle="CustomerUIResources" /></span></font></td>
							</logic:messagesPresent>
						</tr>
					</table>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td><br>
							<span class="fontnormal" id="addCustomerNotes.label.note">
								<mifos:mifoslabel name="Customer.EnterANote" ></mifos:mifoslabel>
								<mifos:mifoslabel name="Customer.ClickPreview" ></mifos:mifoslabel>
								<mifos:mifoslabel name="Customer.ClickCancel1" />
					            <c:if test="${sessionScope.customerNotesActionForm.levelId == CustomerLevel.CENTER.value}">
									<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
								</c:if>
								<c:if test="${sessionScope.customerNotesActionForm.levelId == CustomerLevel.GROUP.value}">
									<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />
								</c:if>
								<c:if test="${sessionScope.customerNotesActionForm.levelId == CustomerLevel.CLIENT.value}">
									<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />
								</c:if>
					            <mifos:mifoslabel name="Customer.ClickCancel2" />
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
							<span id="addCustomerNotes.label.note"
							<mifos:mifoslabel name="Customer.Note" mandatory="yes"></mifos:mifoslabel></span></td>
							<td width="93%" align="left" valign="top"><html-el:textarea styleId="addCustomerNotes.input.note"
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
							<td align="center"><html-el:submit styleId="addCustomerNotes.button.preview" styleClass="buttn">
								<mifos:mifoslabel name="Customer.preview"></mifos:mifoslabel>
							</html-el:submit> &nbsp;&nbsp; <html-el:button styleId="addCustomerNotes.button.cancel"
								property="cancelBtn" styleClass="cancelbuttn"
								onclick="goToCancelPage()">
								<mifos:mifoslabel name="Customer.cancel"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>
			<br>
			<html-el:hidden property="globalCustNum" value="${sessionScope.customerNotesActionForm.globalCustNum}"/>
			<html-el:hidden property="levelId" value="${sessionScope.customerNotesActionForm.levelId}"/>
		</html-el:form>
	</tiles:put>
</tiles:insert>

