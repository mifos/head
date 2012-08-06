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


<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<script type="text/javascript" src="pages/js/jquery/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="pages/js/singleitem.js"></script>

<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
        <span id="page.id" title="BulkEntry"></span>
		<script language="javascript">
		<!--
            // trap the return/enter key to prevent accidental form submission
            jQuery(document).ready(function() {
                jQuery(':input').bind("keypress", function(e) {
                    var code = (e.keyCode ? e.keyCode : e.which);
                    if (code == 13) { // Enter keycode
                        e.preventDefault();
                    }
                });
            });

			function fnCancel(form) {
				form.method.value="cancel";
				form.action="collectionsheetaction.do";
				form.submit();
			}
			
			function fnSubmit(form, buttonSubmit) {
				buttonSubmit.disabled=true;
				form.method.value="get";
				form.action="collectionsheetaction.do";
				form.submit();
			}
			
			function fnLoadLoanOfficers(form) {
				if(document.getElementsByName("officeId")[0].selectedIndex==0) {
					form.method.value="load";
					form.action="collectionsheetaction.do";
					form.submit();
				}
				else {
					form.method.value="loadLoanOfficers";
					form.action="collectionsheetaction.do";
					form.submit();
				}
			}
			
			function fnLoadCustomers(form) {
				if(document.getElementsByName("officeId")[0].selectedIndex==0) {
					form.method.value="load";
					form.action="collectionsheetaction.do";
					form.submit();
				}
				else if(document.getElementsByName("loanOfficerId")[0].selectedIndex==0) {
					form.method.value="loadLoanOfficers";
					form.action="collectionsheetaction.do";
					form.submit();
				}
				
				else {
					document.getElementsByName("customerId")[0].selectedIndex=0;
					form.method.value="loadCustomerList";
					form.action="collectionsheetaction.do";
					form.submit();
				}
			}
			
			function getLastMeetingDateForCustomer(form) {
				if(document.getElementsByName("customerId")[0].selectedIndex==0) {
					return;
				}
				if(document.getElementsByName("officeId")[0].selectedIndex==0) {
					form.method.value="load";
					form.action="collectionsheetaction.do";
					form.submit();
				}
				else if(document.getElementsByName("loanOfficerId")[0].selectedIndex==0) {
					form.method.value="load";
					form.action="collectionsheetaction.do";
					form.submit();
				}
				else {
					form.method.value="getLastMeetingDateForCustomer";
					form.action="collectionsheetaction.do";
					form.submit();
				}
			}
			
			function fnLoadMembers(form) {
				var groupId = document.getElementsByName("groupId")[0].value				
				if(groupId.length == 0){
					alert("Please select a group.");	
					return false;
				}			
				
				var url="collectionsheetmemberaction.do?method=loadMembers&groupId="+groupId;
				window.open(url,"_blank","directories=no, status=no,scrollbars=yes, width=900, height=500,top=0,left=0"); 
							
			}
		
			function getValueFromPopupWindow(membName,membId)
			{				
			  document.getElementById('bulkentry.input.memberName').value = membName;
			  document.getElementById('bulkentry.input.memberId').value = membId;			  
			};
		//-->
		</script>
		<script src="pages/framework/js/date.js"></script>
		<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
		<fmt:setBundle basename="org.mifos.config.localizedResources.BulkEntryUIResources"/>
		<html-el:form action="/collectionsheetaction" onsubmit="return (validateMyForm(receiptDate,receiptDateFormat,receiptDateYY) && validateMyForm(transactionDate,transactionDateFormat,transactionDateYY))">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="350" align="left" valign="top" bgcolor="#FFFFFF">
						<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center" class="heading">
									&nbsp;
								</td>
							</tr>
						</table>
						<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
							<tr>
								<td class="bluetablehead">
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="33%">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17">
														</td>
														<td class="timelineboldorange">
															<mifos:mifoslabel name="bulkEntry.select" />
															<c:choose>
																<c:when test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'isCenterHierarchyExists')==Constants.YES}">
																	<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
																</c:when>
																<c:otherwise>
																	<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />
																</c:otherwise>
															</c:choose>
														</td>
													</tr>
												</table>
											</td>
											<td width="34%" align="center">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17">
														</td>
														<td class="timelineboldorangelight">
															<mifos:mifoslabel name="bulkEntry.enterdata" />
														</td>
													</tr>
												</table>
											</td>
											<td width="33%" align="right">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17">
														</td>
														<td class="timelineboldorangelight">
															<mifos:mifoslabel name="bulkEntry.revnsub" />
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
						<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
							<tr>
								<td align="left" valign="top" class="paddingleftCreates">
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td class="headingorange">
												<span id="bulkentry.heading" class="heading"> <mifos:mifoslabel name="${ConfigurationConstants.BULKENTRY}" /> - </span>
												<mifos:mifoslabel name="bulkEntry.select" />
												<c:choose>
													<c:when test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'isCenterHierarchyExists')==Constants.YES}">
														<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
													</c:when>
													<c:otherwise>
														<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />
													</c:otherwise>
												</c:choose>
											</td>
										</tr>
										<tr>
											<td class="fontnormal">
												<fmt:message key="bulkEntry.selectOfficeFromList">
												<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.OFFICE}" /></fmt:param>
												</fmt:message>
												<c:choose>
													<c:when test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'isCenterHierarchyExists')==Constants.YES}">
														<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
													</c:when>
													<c:otherwise>
														<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />
													</c:otherwise>
												</c:choose>
												.
												<mifos:mifoslabel name="bulkEntry.clickcanc" />
												<br>
												<span class="mandatorytext"> <font color="#FF0000">*</font> </span>
												<mifos:mifoslabel name="bulkEntry.mandatory" />
											</td>
										</tr>
									</table>
									<logic:messagesPresent>
										<font class="fontnormalRedBold"><span id="BulkEntry.error.message"> <html-el:errors bundle="bulkEntryUIResources" /> </span> </font>
										<br>
									</logic:messagesPresent>
									<br>
									<table width="93%" border="0" cellpadding="3" cellspacing="0">

										<c:choose>
											<c:when test="${requestScope.refresh==Constants.NO}">
												<tr class="fontnormal">
													<td align="right">
														<mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}" mandatory="yes" isColonRequired="Yes"/>
														
													</td>
													<td>
														<mifos:select property="officeId">
															<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'OfficesBranchOffices')}" var="office">
																<html-el:option value="${office.officeId}">${office.officeName}</html-el:option>
															</c:forEach>
														</mifos:select>
													</td>
												</tr>
												<tr class="fontnormal">
													<td align="right">
														<mifos:mifoslabel name="bulkEntry.loanofficer" mandatory="yes" isColonRequired="Yes"/>
														
													</td>
													<td>
														<mifos:select property="loanOfficerId">
															<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanOfficers')}" var="loanOfficer">
																<html-el:option value="${loanOfficer.personnelId}">${loanOfficer.displayName}</html-el:option>
															</c:forEach>
														</mifos:select>
													</td>
												</tr>
											</c:when>
											<c:otherwise>
												<tr class="fontnormal">
													<td align="right">
														<mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}" mandatory="yes" />
														:
													</td>
													<td>
														<mifos:select property="officeId" onchange="fnLoadLoanOfficers(this.form)">
															<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'OfficesBranchOffices')}" var="office">
																<html-el:option value="${office.officeId}">${office.officeName}</html-el:option>
															</c:forEach>
														</mifos:select>
													</td>
												</tr>
												<tr class="fontnormal">
													<td align="right">
														<mifos:mifoslabel name="bulkEntry.loanofficer" mandatory="yes" isColonRequired="Yes"/>
														
													</td>
													<td>
														<mifos:select property="loanOfficerId" onchange="fnLoadCustomers(this.form)">
															<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanOfficers')}" var="loanOfficer">
																<html-el:option value="${loanOfficer.personnelId}">${loanOfficer.displayName}</html-el:option>
															</c:forEach>
														</mifos:select>
													</td>
												</tr>
											</c:otherwise>
										</c:choose>
										<tr class="fontnormal">
											<td width="27%" align="right">
												<c:choose>
													<c:when test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'isCenterHierarchyExists')==Constants.YES}">
														<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" mandatory="yes" isColonRequired="Yes"/>
													</c:when>
													<c:otherwise>
														<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" mandatory="yes" isColonRequired="Yes"/>
													</c:otherwise>
												</c:choose>
												
											</td>
											<td width="73%">
												<mifos:select property="customerId" onchange="getLastMeetingDateForCustomer(this.form)">
													<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'CustomersList')}" var="customer">
														<html-el:option value="${customer.customerId}">${customer.displayName}</html-el:option>
													</c:forEach>
												</mifos:select>
											</td>
										</tr>
										<tr class="fontnormal">
													<td align="right">
														<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" mandatory="no" isColonRequired="Yes"/>														
													</td>
													<td>
														<mifos:select property="groupId">
														<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'CenterGroupList')}" var="customerGroups">
															<html-el:option value="${customerGroups.customerId}">${customerGroups.displayName}</html-el:option>
														</c:forEach>
														</mifos:select>
													</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" mandatory="no" isColonRequired="Yes"/>	
											</td>
											<td>
												<input readonly="readonly" type="text" id="bulkentry.input.memberName" style="width:272px" maxlength="60" />
												<html-el:hidden styleId="bulkentry.input.memberId" property="memberId"/>								
												<input  type="button" name="select" value="Select" onclick="fnLoadMembers(this.form);"/>
													
											</td>
											
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel name="bulkEntry.dateoftrxn" mandatory="yes" />
												:
											</td>
											<td>
                                                <date:datetag property="transactionDate" isDisabled="Yes" renderstyle="simple"/>
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel name="bulkEntry.pmnttype" mandatory="yes" isColonRequired="Yes"/>
												
											</td>
											<td>
												<mifos:select property="paymentId">
													<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'PaymentTypesList')}" var="payment">
														<html-el:option value="${payment.id}">${payment.displayValue}</html-el:option>
													</c:forEach>
												</mifos:select>
											</td>
										</tr>

										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel keyhm="BulkEntry.ReceiptId" isColonRequired="Yes" name="bulkEntry.rcptid" />
											</td>
											<td>
												<mifos:mifosalphanumtext styleId="bulkentry.input.receiptId" keyhm="BulkEntry.ReceiptId" property="receiptId" style="width:272px" maxlength="60" />
											</td>
										</tr>
										<tr class="fontnormal">
											<td align="right">
												<mifos:mifoslabel keyhm="BulkEntry.ReceiptDate" isColonRequired="Yes" name="bulkEntry.rcptdate" />
											</td>
											<td>
												<date:datetag keyhm="BulkEntry.ReceiptDate" property="receiptDate" renderstyle="simple"/>
											</td>
										</tr>
									</table>
									<br>
									<table width="93%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center" class="blueline">
												&nbsp;
											</td>
										</tr>
									</table>
									<html-el:hidden property="method" value="get" />
									<html-el:hidden property="input" value="load" />
									<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
									<br>
									<table width="93%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center">
												<html-el:submit styleId="bulkentry.button.continue" styleClass="buttn"  onclick="fnSubmit(this.form, this)">
													<mifos:mifoslabel name="bulkEntry.cont" />
												</html-el:submit>
												&nbsp;
												<html-el:button  styleId="bulkentry.button.cancel" property="cancel" styleClass="cancelbuttn"  onclick="fnCancel(this.form);">
													<mifos:mifoslabel name="bulkEntry.cancel" />
												</html-el:button>
											</td>
										</tr>
									</table>
									<br>
								</td>
							</tr>
						</table>
						<br>
					</td>
				</tr>
			</table>
		</html-el:form>
	</tiles:put>
</tiles:insert>
