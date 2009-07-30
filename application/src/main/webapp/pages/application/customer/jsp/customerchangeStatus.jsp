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
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<span style="display: none" id="page.id">CustomerChangeStatus</span>
		<script language="javascript">
			function goToCancelPage(form){
				form.action="editCustomerStatusAction.do?method=cancelStatus";
				form.submit();
 			 }
			function manageFlag(i) {
   				if(editCustomerStatusActionForm.flagId!=undefined){
					if(i==5){
						editCustomerStatusActionForm.flagId.disabled=false;
					}else if(i==6){
						editCustomerStatusActionForm.flagId.disabled=false;
					}else if(i==11){
						editCustomerStatusActionForm.flagId.disabled=false;
					}else if(i==12){
						editCustomerStatusActionForm.flagId.disabled=false;
					}else{
						editCustomerStatusActionForm.flagId.selectedIndex=0;
						editCustomerStatusActionForm.flagId.disabled=true;
					}
				}
 			 }
		</script>
		<html-el:form action="editCustomerStatusAction.do?method=previewStatus">
			<c:set
				value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
				var="BusinessKey" />
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
							<td width="83%" class="headingorange"><span class="heading"> <c:out
								value="${sessionScope.editCustomerStatusActionForm.customerName}" />&nbsp;-&nbsp;
							</span><mifos:mifoslabel name="Customer.changestatus" /></td>
						</tr>
						<tr>
							<td class="headingorange"><span class="fontnormalbold"> <mifos:mifoslabel
								name="accounts.currentstatus" />:<mifoscustom:MifosImage
								id="${sessionScope.editCustomerStatusActionForm.currentStatusId}"
								moduleName="customer" /></span> <span class="fontnormal"><c:out value="${BusinessKey.customerStatus.name}" />
								</span></td>
						</tr>
						<tr><logic:messagesPresent>
							<td><br><font class="fontnormalRedBold"><span id="customerchangeStatus.error.message"><html-el:errors bundle="CustomerUIResources" /></span></font></td>
							</logic:messagesPresent>
						</tr>
						<tr>
							<td class="fontnormal"><br>
							<mifos:mifoslabel name="Customer.SelectStatus"></mifos:mifoslabel> 
							<mifos:mifoslabel name="Customer.ClickContinue" ></mifos:mifoslabel>
							<mifos:mifoslabel name="Customer.ClickCancel1"></mifos:mifoslabel>
								<c:if test="${sessionScope.editCustomerStatusActionForm.levelId == CustomerLevel.CENTER.value}">
									<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
								</c:if>
								<c:if test="${sessionScope.editCustomerStatusActionForm.levelId == CustomerLevel.GROUP.value}">
									<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />
								</c:if>
								<c:if test="${sessionScope.editCustomerStatusActionForm.levelId == CustomerLevel.CLIENT.value}">
									<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />
								</c:if>
							<mifos:mifoslabel name="Customer.ClickCancel2"></mifos:mifoslabel>
								</td>
						</tr>
						<tr>
							<td class="blueline"><img src="pages/framework/images/trans.gif"
								width="10" height="12"></td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="left" valign="top" class="fontnormalbold"><mifos:mifoslabel
								name="accounts.status" mandatory="yes" ></mifos:mifoslabel>
							</td>
							<td align="left" valign="top">
							<table width="95%" border="0" cellpadding="0" cellspacing="0">
								<c:forEach var="status" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'statusList')}" varStatus="loopStatus">
										<bean:define id="ctr">
										<c:out value="${loopStatus.index}" />
									</bean:define>
									<tr class="fontnormal">
										<td width="2%" align="center"><html-el:radio styleId="customerchangeStatus.input.status"
											property="newStatusId" value="${status.id}"
											onclick="manageFlag(${status.id})" /></td>
										<td width="98%"><span id="customerchangeStatus.label.status"><c:out value="${status.name}" /></span></td>
									</tr>
								</c:forEach>
								<c:forEach var="status" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'statusList')}">
									<c:if test="${status.id == CustomerStatus.CLIENT_CANCELLED.value || status.id == CustomerStatus.CLIENT_CLOSED.value ||status.id == CustomerStatus.GROUP_CANCELLED.value || status.id == CustomerStatus.GROUP_CLOSED.value}">
										<tr class="fontnormal">
											<td align="center">&nbsp;</td>
											<td><mifos:mifoslabel name="Customer.SelectExplaination1" />
												<c:if test="${sessionScope.editCustomerStatusActionForm.levelId == '1'}">
													<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />
												</c:if>
												<c:if test="${sessionScope.editCustomerStatusActionForm.levelId == '2'}">
													<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />
												</c:if>											
												<mifos:mifoslabel name="Customer.SelectExplaination2" />
												</td>
										</tr>
										<tr class="fontnormal">
											<td align="center">&nbsp;</td>
											<td><c:set var="flags" scope="request"
												value="${status.flagSet}" /> <mifos:select
												name="editCustomerStatusActionForm" property="flagId" styleId="customerchangeStatus.input.cancel_reason" size="1" disabled="true">
												<html-el:options collection="flags" property="id"
													labelProperty="lookUpValue.messageText" />
											</mifos:select></td>
										</tr>
									</c:if>
								</c:forEach>
								<tr class="fontnormal">
									<td align="center">&nbsp;</td>
									<td>&nbsp;</td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td width="7%" align="left" valign="top" class="fontnormalbold">
							<span id="customerchangeStatus.label.notes">
							<mifos:mifoslabel name="Customer.note" mandatory="yes"></mifos:mifoslabel></span></td>
							<td width="93%" align="left" valign="top"
								style="padding-left:4px;"><html-el:textarea styleId="customerchangeStatus.input.notes"
								property="notes" style="width:320px; height:110px;" /></td>
						</tr>
					</table>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td height="25" align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:submit styleId="customerchangeStatus.button.preview" styleClass="buttn">
								<mifos:mifoslabel name="Customer.preview" />
							</html-el:submit> &nbsp;&nbsp; <html-el:button styleId="customerchangeStatus.button.cancel" property="btn"
								styleClass="cancelbuttn"
								onclick="goToCancelPage(this.form)">
								<mifos:mifoslabel name="Customer.cancel" />
							</html-el:button></td>
						</tr>
					</table>
					<br>
					<br>
					</td>
				</tr>
			</table>
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
		<script language="javascript">
				if(editCustomerStatusActionForm.newStatusId.length != undefined){
					for(j=0;j<editCustomerStatusActionForm.newStatusId.length;j++){ 
						if (editCustomerStatusActionForm.newStatusId[j].checked) {
							manageFlag(editCustomerStatusActionForm.newStatusId[j].value);
						}
					}
				}else{
				}
			</script>
	</tiles:put>
</tiles:insert>
