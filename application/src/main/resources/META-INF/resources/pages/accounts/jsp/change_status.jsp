<%--
Copyright (c) 2005-2011 Grameen Foundation USA
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
<!-- change_status.jsp -->

<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>


<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<span id="page.id" title="ChangeStatus"></span>
		<SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>
		<script language="javascript">
			const APPROVAL_ID = 3;

			function goToCancelPage(form, input){
				switch(input){
				case 'loan':
					form.action="viewLoanAccountDetails.ftl"
					break;
				case 'savings':
					form.action="viewSavingsAccountDetails.ftl"
					break;
				case 'grouploan':
					form.action="viewLoanAccountDetails.ftl";
					break;
				default:
					form.action="clientsAndAccounts.ftl";
				}
				form.method="get";
				form.submit();
 			 }
 			function checkStatusForTransactionDate(i) {
 			    var form = document.getElementsByName("editStatusActionForm")[0];
 				if (i==7 || i==8 || (i==APPROVAL_ID && form.allowBackDatedApprovals.value == 'true')) {
 			        document.getElementById("statusChangeTransactionDateLabelDiv").style.display = "block";
 			        document.getElementById("statusChangeTransactionDateInputDiv").style.display = "block";
 			    }
 			    else {
 			        document.getElementById("statusChangeTransactionDateLabelDiv").style.display = "none";
 			        document.getElementById("statusChangeTransactionDateInputDiv").style.display = "none";
 			    }
 			}
			function manageFlag(i) {
   				if(editStatusActionForm.flagId!=undefined){
					if(i==15){
						editStatusActionForm.flagId.disabled=false;
					}else if(i==10){
						editStatusActionForm.flagId.disabled=false;
					}else{
						editStatusActionForm.flagId.selectedIndex=0;
						editStatusActionForm.flagId.disabled=true;
					}
				}
				checkStatusForTransactionDate(i);
 			 }
		</script>
		<html-el:form action="editStatusAction.do?method=preview" onsubmit="return validateMyForm(transactionDate,transactionDateFormat,transactionDateYY)">
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />	
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'editAccountStatusDocumentsList')}" var="adminDoc" />
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
								value="${sessionScope.editStatusActionForm.accountName}" />&nbsp;#&nbsp;<c:out
								value="${sessionScope.editStatusActionForm.globalAccountNum}" />&nbsp;-&nbsp;
							</span><mifos:mifoslabel name="accounts.changestatus"
								bundle="accountsUIResources" /></td>
						</tr>
						<tr>
							<td class="headingorange"><span class="fontnormalbold"> <mifos:mifoslabel
								name="accounts.currentstatus" bundle="accountsUIResources" />:<mifoscustom:MifosImage
								id="${sessionScope.editStatusActionForm.currentStatusId}"
								moduleName="org.mifos.accounts.util.resources.accountsImages" /></span> <span class="fontnormal"><c:out value="${BusinessKey.accountState.name}" />
								</span></td>
						</tr>
						<tr><logic:messagesPresent>
							<td><br><font class="fontnormalRedBold"><span id="change_status.error.message"><html-el:errors bundle="accountsUIResources" /></span></font></td>
							</logic:messagesPresent>
						</tr>
						<tr>
							<td class="fontnormal"><br>
								<c:if test="${sessionScope.editStatusActionForm.accountTypeId == '1'}">
									<mifos:mifoslabel name="accounts.SelectLoanStatusComplete"
									    bundle="accountsUIResources" />
								</c:if>
								<c:if test="${sessionScope.editStatusActionForm.accountTypeId == '2'}">
									<mifos:mifoslabel name="accounts.SelectSavingsStatusComplete"
									    bundle="accountsUIResources" />
								</c:if>
								
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
								name="accounts.status" mandatory="yes" bundle="accountsUIResources"></mifos:mifoslabel>
							</td>
							<td align="left" valign="top">
							<table width="95%" border="0" cellpadding="0" cellspacing="0">

								<c:forEach var="status" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'statusList')}"	varStatus="loopStatus">
									<bean:define id="ctr">
										<c:out value="${loopStatus.index}" />
									</bean:define>
									<tr class="fontnormal">
										<td width="2%" align="center"><html-el:radio styleId="change_status.input.status"
											property="newStatusId" value="${status.id}"
											onclick="manageFlag(${status.id})" /></td>
										<td width="98%"><span id="change_status.label.status"><c:out value="${status.name}" /></span></td>
									</tr>
								</c:forEach>
								<c:forEach var="status" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'statusList')}">
									<c:if test="${status.id == '10' || status.id == '15'}">
										<tr class="fontnormal">
											<td align="center">&nbsp;</td>
											<td><c:if test="${sessionScope.editStatusActionForm.accountTypeId == '1'}">
													<mifos:mifoslabel name="accounts.SelectExplainationLoan" bundle="accountsUIResources" />
												</c:if>
												<c:if test="${sessionScope.editStatusActionForm.accountTypeId == '2'}">
													<mifos:mifoslabel name="accounts.SelectExplainationSavings" bundle="accountsUIResources" />
												</c:if>											
												</td>
										</tr>
										<tr class="fontnormal">
											<td align="center">&nbsp;</td>
											<td>
											<c:set var="flags" scope="request" value="${status.flagSet}" /> 
											<mifos:select name="editStatusActionForm" styleId="change_status.input.cancel_reason" property="flagId" size="1" disabled="true">
												<html-el:options collection="flags" property="id" labelProperty="statusFlagMessageText" />
											</mifos:select>
											</td>
										</tr>
									</c:if>
								</c:forEach>
								<tr class="fontnormal">
                                            <td width="2%">&nbsp;</td>
                                            <td width="98%">
                                                <div id="statusChangeTransactionDateLabelDiv"  style="display: none;">
                                                    <mifos:mifoslabel name="accounts.date_of_trxn" mandatory="yes" bundle="accountsUIResources" isColonRequired="yes" />
                                                </div>
                                                &nbsp;
                                                <div id="statusChangeTransactionDateInputDiv" style="display: none;">
                                                    <date:datetag property="transactionDate" />
                                                </div>
                                            </td>
                                       </tr>
								<tr class="fontnormal">
									<td align="center">&nbsp;</td>
									<td>&nbsp;</td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td width="7%" align="left" valign="top" class="fontnormalbold">
							<span id="change_status.label.note">
							<mifos:mifoslabel name="accounts.note" mandatory="yes"
								bundle="accountsUIResources"></mifos:mifoslabel></span></td>
							<td width="93%" align="left" valign="top"
								style="padding-left:4px;"><html-el:textarea styleId="change_status.input.note"
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
							<td align="center">
                                <html-el:submit styleId="change_status.button.submit" styleClass="buttn">
                                    <mifos:mifoslabel name="accounts.preview" />
                                </html-el:submit>
    							&nbsp;&nbsp;
                                <html-el:button styleId="change_status.button.cancel" property="btn" styleClass="cancelbuttn" onclick="goToCancelPage(this.form, '${sessionScope.editStatusActionForm.input}')">
                                    <mifos:mifoslabel name="accounts.cancel" />
                                </html-el:button>
							</td>
						</tr>
					</table>
					<table>
						<tr>
							<td>
								<span class="fontnormalbold"> 
									<c:if test="${fn:length(adminDoc) > 0}">
										<mifos:mifoslabel name="reports.administrativedocuments" /> :&nbsp
									</c:if>
								</span>
							</td>
						</tr>
						<c:forEach var="adminDoc" items="${adminDoc}">
							<tr>
								<td>
                                <label for="adminDocOutputType_${adminDoc.admindocId}">${adminDoc.adminDocumentName}</label>
                                <select id="adminDocOutputType_${adminDoc.admindocId}" class="adminDocOutputType">
                                  <option value="0" selected="selected">PDF</option>
                                  <option value="1">XLS</option>
                                  <option value="2">RTF</option>
                                  <option value="3">HTML</option>
                                  <option value="4">XML</option>
                                  <option value="5">CSV</option>
                                </select>
                                
								<html-el:link styleClass="adminDocOutputTypeLink" styleId="loanaccountdetail.link.viewAdminReport"
									href="executeAdminDocument.ftl?adminDocumentId=${adminDoc.admindocId}&entityId=${BusinessKey.globalAccountNum}&outputTypeId=0">
									<c:out value="Download" />
								</html-el:link>	  	
								</td>
							</tr>	
						</c:forEach>			
					</table>
					<br>
					<br>
					</td>
				</tr>
			</table>
			<html-el:hidden property="globalAccountNum" value="${sessionScope.editStatusActionForm.globalAccountNum}" />
            <html-el:hidden property="allowBackDatedApprovals" />
		</html-el:form>
        <script type="text/javascript" src="pages/application/admindocument/js/adminDocument.js"></script>
        <script type="text/javascript">syncAdminDocumentLinkWithComboBox("adminDocOutputType", "adminDocOutputTypeLink");</script>
		<script language="javascript">
				if(editStatusActionForm.newStatusId.length != undefined){
					for(j=0;j<editStatusActionForm.newStatusId.length;j++){ 
						if (editStatusActionForm.newStatusId[j].checked) {
							manageFlag(editStatusActionForm.newStatusId[j].value);
						}
					}
				}else{
					manageFlag(editStatusActionForm.newStatusId.value);
				}
			</script>
	</tiles:put>
</tiles:insert>
