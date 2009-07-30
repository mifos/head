<%--
Copyright (c) 2005-2008 Grameen Foundation USA
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
<!-- CreateMultipleLoanAccountsSearchResults.jsp -->
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>


<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
		<span style="display: none" id="page.id">CreateMultipleLoanAccountsSearchResults</span>
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<script src="pages/framework/js/conversion.js"></script>
		<script src="pages/framework/js/con_en.js"></script>
		<script src="pages/framework/js/con_${sessionScope['UserContext'].currentLocale}.js"></script>
		<script>
			function selectAll(x) {
				for(var i=0,l=x.form.length; i<l; i++)
				{
					if(x.form[i].type == 'checkbox' && x.form[i].name != 'selectAll1'){
						x.form[i].checked=x.checked
					}
				}
			}
	
			function selectAllCheck(x){
				var checked = true;
				for(var i=0,l=x.form.length; i<l; i++){
					if(x.form[i].type == 'checkbox' && x.form[i].name != 'selectAll1'){
						if(x.form[i].checked == false){
							checked = false;
						}
					}
				}
				for(var i=0,l=x.form.length; i<l; i++){
					if(x.form[i].type == 'checkbox' && x.form[i].name == 'selectAll1'){
						x.form[i].checked = checked;
					}
				}
			}
			function fun_saveForLater(form) {
				form.method.value="create";
				form.stateSelected.value="1";
				form.action="multipleloansaction.do";
				form.submit();
				func_disableSubmitBtn("saveForLaterButton");
			}
			function fun_submitForApproval(form) {
				form.method.value="create";
				form.stateSelected.value="2";
				form.action="multipleloansaction.do";
				form.submit();
				func_disableSubmitBtn("submitForApprovalButton");
				
			}
			function fun_approved(form) {
				form.method.value="create";
				form.stateSelected.value="3";
				form.action="multipleloansaction.do";
				form.submit();
				func_disableSubmitBtn("approvedButton");
				
			}
 		</script>
		<html-el:form action="/multipleloansaction.do">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="470" align="left" valign="top" bgcolor="#FFFFFF">
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
											<td width="50%">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/tick.gif" width="17" height="17">
														</td>
														<td class="timelineboldgray">
															<mifos:mifoslabel name="loan.search" />
															<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /><mifos:mifoslabel name="loan.s" />
														</td>
													</tr>
												</table>
											</td>
											<td width="50%" align="center">
												<table border="0" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17">
														</td>
														<td class="timelineboldorange">
															<mifos:mifoslabel name="loan.Select" />
															<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /><mifos:mifoslabel name="loan.s" />
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
								<td width="70%" height="24" align="left" valign="top" class="paddingleftCreates">
									<table width="98%" border="0" cellspacing="0" cellpadding="3">
										<tr>
											<td class="headingorange">
												<span class="heading"><mifos:mifoslabel name="loan.create" /> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel name="loan.accounts" /> - </span>
												<mifos:mifoslabel name="loan.search" />
												<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /><mifos:mifoslabel name="loan.s" />
											</td>
										</tr>
										<tr>
											<td class="fontnormalbold">
												<span class="fontnormal"><mifos:mifoslabel name="loan.Enter" /> <mifos:mifoslabel name="loan.detailssearch" /> <mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /><mifos:mifoslabel name="loan.s" /> &amp; <mifos:mifoslabel
														name="loan.accwithoutsub" /></span>
												<br>
												<br>
												<mifos:mifoslabel name="${ConfigurationConstants.LOAN}"/>
												<mifos:mifoslabel name="loan.instancename" isColonRequired="Yes" />
												<c:set  var="loanPrdOffering" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanOffering')}" scope="page"/>
												<span class="fontnormal">
													<c:out value="${loanPrdOffering.prdOfferingName}" />
												</span>
											</td>
										</tr>
									</table>
									<font class="fontnormalRedBold">  <span id="CreateMultipleLoanAccountsSearchResults.error.message"> <html-el:errors bundle="loanUIResources" /> </span></font>
									<br>
									<!-- begin search results list -->
									<table width="96%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td width="5%" valign="top" class="drawtablerowboldnolinebg">
												<input id="CreateMultipleLoanAccountsSearchResults.input.selectAll" type="checkbox" onclick="selectAll(this)" name="selectAll1">
											</td>
											<td width="29%" class="drawtablerowboldnolinebg">
												<mifos:mifoslabel name="loan.acc_owner" />
											</td>
											<td width="15%" class="drawtablerowboldnolinebg">
												<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel name="loan.amt" />
											</td>
											<td width="15%" class="drawtablerowboldnolinebg">
												<mifos:mifoslabel name="loan.allowed_amount1" /> 
											</td>
											<td width="15%" class="drawtablerowboldnolinebg">
												<mifos:mifoslabel name="loan.no_of_inst" /> 
											</td>
											<td width="35%" class="drawtablerowboldnolinebg">
												<mifos:mifoslabel name="loan.business_work_act" /> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}"/>
											</td>
										</tr>
										<c:forEach var="clientDetail" varStatus="loopStatus" items="${sessionScope.multipleloansactionform.clientDetails}" >
										<tr>
											<td valign="top" class="drawtablerow">
												<html-el:checkbox property="clientDetails[${loopStatus.index}].selected" value="true" onclick="selectAllCheck(this)" styleId="CreateMultipleLoanAccountsSearchResults.checkbox.${loopStatus.index}"/>
											</td>
											<td width="29%" valign="top" class="drawtablerow">
												<span class="fontnormalbold"><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" isColonRequired="Yes"/></span> 
												<span id="CreateMultipleLoanAccountsSearchResults.clientName.${loopStatus.index}"><c:out value="${clientDetail.client.displayName}"/></span>: <mifos:mifoslabel name="${ConfigurationConstants.ID}" /> <c:out value="${clientDetail.client.globalCustNum}"/>
												<br>
												<span class="fontnormalbold"><mifos:mifoslabel name="bulkEntry.loanofficer" isColonRequired="Yes" /></span> 
												<c:out value="${clientDetail.client.personnel.displayName}"/>
												<br>
												<c:out value="${clientDetail.client.office.officeName}"/> / <c:out value="${clientDetail.client.parentCustomer.parentCustomer.displayName}"/> / <c:out value="${clientDetail.client.parentCustomer.displayName}"/>
											</td>
											<td width="15%" valign="top" class="drawtablerow">
												<mifos:mifosdecimalinput 
												styleId="CreateMultipleLoanAccountsSearchResults.input.loanAmount"
												property="clientDetails[${loopStatus.index}].loanAmount"/>
											</td>
											<td width="15%" valign="top" class="drawtablerow">
												<c:out value="${clientDetail.minLoanAmount}" /> &nbsp; - &nbsp; 
												<c:out value="${clientDetail.maxLoanAmount}" />
											</td>
											<td width="15%" valign="top" class="drawtablerow">
												<c:out value="${clientDetail.defaultNoOfInstall}" /> 
											</td>
											<td width="20%" valign="top" class="drawtablerow">
												<mifos:select property="clientDetails[${loopStatus.index}].businessActivity" style="width:136px;">
													<c:forEach var="BusinessActivity" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessActivities')}" >
														<html-el:option value="${BusinessActivity.id}">${BusinessActivity.name}</html-el:option>
													</c:forEach>
												</mifos:select>
											</td>
										</tr>
										</c:forEach>
									</table>
									<table width="96%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center" class="blueline">
												&nbsp;
											</td>
										</tr>
									</table>
									<br>
									<table width="96%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center">
												<html-el:button styleId="CreateMultipleLoanAccountsSearchResults.button.saveForLater" property="saveForLaterButton" styleClass="buttn"  onclick="javascript:fun_saveForLater(this.form)">
													<mifos:mifoslabel name="loan.saveForLater" />
												</html-el:button>
												&nbsp;
												<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'pendingApprovalDefined')}" var="PendingApproval" />
												<c:choose>
													<c:when test='${PendingApproval == true}'>
														<html-el:button property="submitForApprovalButton" styleClass="buttn"  onclick="javascript:fun_submitForApproval(this.form)" styleId="CreateMultipleLoanAccountsSearchResults.button.submit">
															<mifos:mifoslabel name="loan.submitForApproval" />
														</html-el:button>
													</c:when>
													<c:otherwise>
														<html-el:button styleId="CreateMultipleLoanAccountsSearchResults.button.approve" property="approvedButton" styleClass="buttn" onclick="javascript:fun_approved(this.form)">
															<mifos:mifoslabel name="loan.approved" />
														</html-el:button>
													</c:otherwise>
												</c:choose>
												&nbsp;
												<html-el:button styleId="CreateMultipleLoanAccountsSearchResults.button.cancel" property="cancelButton" styleClass="cancelbuttn"  onclick="location.href='multipleloansaction.do?method=cancel&currentFlowKey=${requestScope.currentFlowKey}'">
													<mifos:mifoslabel name="loan.cancel" />
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
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<html-el:hidden property="method" value="create" />
			<html-el:hidden value="" property="stateSelected" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
