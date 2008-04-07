<!--

 * editLoanAccount.jsp  version: xxx



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

 -->

<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<body onload="disableFields()">
		<SCRIPT SRC="pages/application/loan/js/EditLoanAccount.js"></SCRIPT>
		
		<SCRIPT SRC="pages/application/loan/js/CreateLoanAccountPreview.js"></SCRIPT>
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		
		
		<SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
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
		
		<html-el:form action="/loanAccountAction.do?method=managePreview"
			onsubmit="return (validateMyForm(disbursementDate,disbursementDateFormat,disbursementDateYY));">
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanIndividualMonitoringIsEnabled')}"	var="loanIndividualMonitoringIsEnabled" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanaccountownerisagroup')}" var="loanaccountownerisagroup" />
			
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />

			<td height="200" align="left" valign="top" bgcolor="#FFFFFF"
				class="paddingleftmain">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <customtags:headerLink />
					</span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="83%" class="headingorange"><span class="heading"> <c:out
								value="${BusinessKey.loanOffering.prdOfferingName}" />&nbsp;#
							<c:out value="${BusinessKey.globalAccountNum}" />-&nbsp;
							</span> <mifos:mifoslabel name="loan.edit_loan_acc" /></td>
						</tr>
						<tr>
							<td class="fontnormal"><mifos:mifoslabel name="loan.edit_info" />
							<br>
							<font color="#FF0000">*</font> <mifos:mifoslabel
								name="loan.asterisk" /></td>
						</tr>
					</table>
					<br>
					<font class="fontnormalRedBold"> <html-el:errors
						bundle="loanUIResources" /> </font>
					<table width="95%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
								name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel
								name="loan.loan_acc_details" /><br>
							<br>
							</td>
						</tr>
						
		
					<!-- Prerequisites : create loan account for Group / When Group loan with individual Monitoring is enabled -->
					<c:if test="${loanIndividualMonitoringIsEnabled == '1'}">
						<c:if test="${loanaccountownerisagroup == 'yes'}">

							
								<tr>
									<td width="5%" valign="top"
										class="drawtablerowboldnolinebg"><input
										type="checkbox" onclick="for(var i=0,l=this.form.length; i<l; i++){if(this.form[i].type == 'checkbox' && this.form[i].name != 'selectAll1'){this.form[i].checked=this.checked}}"
										name="selectAll1"></td>
									<td width="29%" class="drawtablerowboldnolinebg"><mifos:mifoslabel
										name="loan.acc_owner" /></td>
									<td width="31%" class="drawtablerowboldnolinebg"><mifos:mifoslabel
										name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel
										name="loan.amt" /></td>
									<td width="35%" class="drawtablerowboldnolinebg"><mifos:mifoslabel
										name="loan.business_work_act" /> <mifos:mifoslabel
										name="${ConfigurationConstants.LOAN}" /></td>
								</tr>
								<c:forEach var="client" varStatus="loopStatus1"
									items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'clientList')}">
									<bean:define id="indice" toScope="request">
										<c:out value="${loopStatus1.index}" />
									</bean:define>
									<tr>
										<td valign="top" class="drawtablerow"><html-el:checkbox
											property="clients[${indice}]"
											value="${client.customerId}"
											onclick="selectAllCheck(this)" /></td>
										<td width="29%" valign="top" class="drawtablerow"><span
											class="fontnormalbold"><mifos:mifoslabel
											name="${ConfigurationConstants.CLIENT}"
											isColonRequired="Yes" /></span> <c:out
											value="${client.displayName}" /> <br>
										<span class="fontnormalbold"><mifos:mifoslabel
											name="${ConfigurationConstants.CLIENT_ID}"
											isColonRequired="Yes" /></span> <c:out
											value="${client.globalCustNum}" /> <br>
										<span class="fontnormalbold"><c:out
											value="${ConfigurationConstants.GOVERNMENT}" />&nbsp;<c:out
											value="${ConfigurationConstants.ID}" />:&nbsp;</span> <c:out
											value="${client.governmentId}" />
										<br>
										</td>
										<td width="31%" valign="top" class="drawtablerow"><mifos:mifosdecimalinput
											property="clientDetails[${indice}].loanAmount" /></td>
										<td width="35%" valign="top" class="drawtablerow"><mifos:select
											property="clientDetails[${indice}].businessActivity"
											style="width:136px;">
											<c:forEach var="BusinessActivity"
												items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessActivities')}">
												<html-el:option value="${BusinessActivity.id}">${BusinessActivity.name}</html-el:option>
											</c:forEach>
										</mifos:select></td>
									</tr>
								</c:forEach>
							
							<br>	<br>		<br>	

						</c:if>
					</c:if> 
					<!--  -->
					
						
						
						<tr class="fontnormal">
							<td width="30%" align="right" class="fontnormal"><mifos:mifoslabel
								name="${ConfigurationConstants.LOAN}" mandatory="yes" /> <mifos:mifoslabel
								name="loan.amt" />:&nbsp;</td>
							<td valign="top"><mifos:mifosdecimalinput property="loanAmount"
								value="${sessionScope.loanAccountActionForm.loanAmount}" /> <mifos:mifoslabel
								name="loan.allowed_amount" />&nbsp; <c:out
								value="${BusinessKey.loanOffering.minLoanAmount}" />
							&nbsp; - &nbsp; <c:out
								value="${BusinessKey.loanOffering.maxLoanAmount}" />)
							</td>

						</tr>
						<tr class="fontnormal">
							<td width="30%" align="right" class="fontnormal"><mifos:mifoslabel
								name="${ConfigurationConstants.INTEREST}" mandatory="yes" /> <mifos:mifoslabel
								name="loan.interest_rate" />:&nbsp;</td>
							<td width="70%" valign="top"><mifos:mifosdecimalinput
								property="interestRate"
								value="${sessionScope.loanAccountActionForm.interestRate}"
								decimalFmt="10.5" /> <mifos:mifoslabel
								name="loan.allowed_interest1" /> <mifos:mifoslabel
								name="${ConfigurationConstants.INTEREST}" /> <mifos:mifoslabel
								name="loan.int_rate" />&nbsp; <c:out
								value="${BusinessKey.loanOffering.minInterestRate}" />&nbsp;
							- &nbsp; <c:out
								value="${BusinessKey.loanOffering.maxInterestRate}" />
							%)</td>
						</tr>
						<tr class="fontnormal">
							<td align="right" class="fontnormal"><span class="mandatorytext"></span>
							<mifos:mifoslabel name="loan.no_of_inst" mandatory="yes" />:&nbsp;
							</td>
							<td valign="top"><mifos:mifosnumbertext
								name="loanAccountActionForm" property="noOfInstallments"
								value="${sessionScope.loanAccountActionForm.noOfInstallments}" />
							<mifos:mifoslabel name="loan.allowed_no_of_inst" />&nbsp; <c:out
								value="${BusinessKey.loanOffering.minNoInstallments}" />&nbsp;
							- &nbsp; <c:out
								value="${BusinessKey.loanOffering.maxNoInstallments}" />)
							</td>
						</tr>

						<tr class="fontnormal">
							<td align="right" class="fontnormal"><span class="mandatorytext"></span>
							<mifos:mifoslabel name="loan.proposed_date" mandatory="yes" />:&nbsp;
							</td>
							<td valign="top"><date:datetag property="disbursementDate" /></td>
						</tr>
						<tr class="fontnormal">
							<td align="right" class="fontnormal"><mifos:mifoslabel
								name="${ConfigurationConstants.INTEREST}" /> <mifos:mifoslabel
								name="loan.interest_disb" />:&nbsp;</td>
							<td valign="top">
							<html-el:hidden property="intDedDisbursement" value="${sessionScope.loanAccountActionForm.intDedDisbursement}" />
							<input type="checkbox" name="intDedDisb" onclick="setGracePeriod()" /></td>
						</tr>
						<tr class="fontnormal">
							<td align="right" class="fontnormal"><span class="mandatorytext"></span>
							<mifos:mifoslabel name="loan.grace_period" />:&nbsp;</td>
							<td valign="top">
								<mifos:mifosnumbertext property="gracePeriod" onchange="setGracePeriodDurationValue();"/></td>
						</tr>
						<html-el:hidden property="inheritedGracePeriodDuration"
								value="${sessionScope.loanAccountActionForm.gracePeriodDuration}" />
							<html-el:hidden property="gracePeriodDuration"/>
							<html-el:hidden property="gracePeriodTypeId" value="${BusinessKey.gracePeriodType.id}" />
						<script>setIntrestAtDisb();</script>
						<tr class="fontnormal">
							<td align="right" class="fontnormal"><mifos:mifoslabel
								keyhm="Loan.PurposeOfLoan" name="Loan.PurposeOfLoan"
								bundle="loanUIResources" /><mifos:mifoslabel
								keyhm="Loan.PurposeOfLoan" name="${ConfigurationConstants.LOAN}"
								isColonRequired="yes" bundle="loanUIResources"
								isManadatoryIndicationNotRequired="yes" /></td>
							<td valign="top"><mifos:select keyhm="Loan.PurposeOfLoan"
								property="businessActivityId">
								<c:forEach var="BusinessActivity" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessActivities')}" >
									<html-el:option value="${BusinessActivity.id}">${BusinessActivity.name}</html-el:option>
								</c:forEach>
							</mifos:select></td>
						</tr>
						<c:if
							test="${BusinessKey.accountState.id != 1 || BusinessKey.accountState.id != 2}">
							<html-el:hidden
								value="${sessionScope.loanAccountActionForm.loanAmount}"
								property="loanAmount" />
							<html-el:hidden
								value="${sessionScope.loanAccountActionForm.interestRate}"
								property="interestRate" />
							<html-el:hidden
								value="${sessionScope.loanAccountActionForm.noOfInstallments}"
								property="noOfInstallments" />
							<!-- <html-el:hidden value="${requestScope.loan.businessActivityId}" property="businessActivityId"/> -->
							<html-el:hidden
								value="${sessionScope.loanAccountActionForm.gracePeriodDuration}"
								property="gracePeriodDuration" />
							<html-el:hidden
								value="${sessionScope.loanAccountActionForm.intDedDisbursement}"
								property="intDedDisbursement" />
						</c:if>
						<tr class="fontnormal">
							<td align="right" class="fontnormal"><mifos:mifoslabel
								keyhm="Loan.CollateralType" name="Loan.CollateralType"
								isColonRequired="yes" bundle="loanUIResources" /></td>
							<td valign="top"><mifos:select keyhm="Loan.CollateralType"
								property="collateralTypeId" style="width:136px;">
								<c:forEach var="CollateralType" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'CollateralTypes')}" >
									<html-el:option value="${CollateralType.id}">${CollateralType.name}</html-el:option>
								</c:forEach>
							</mifos:select></td>
						</tr>
						<tr class="fontnormal">
							<td align="right" valign="top" class="fontnormal"><mifos:mifoslabel
								keyhm="Loan.CollateralNotes" name="Loan.CollateralNotes"
								isColonRequired="yes" bundle="loanUIResources" /></td>
							<td valign="top"><mifos:textarea keyhm="Loan.CollateralNotes"
								property="collateralNote" style="width:320px; height:110px;"></mifos:textarea></td>
						</tr>
					</table>
					<br>
					<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
						  <td colspan="2" class="fontnormalbold">
						     <mifos:mifoslabel name="loan.additionalInfo" bundle="loanUIResources"/></td>
						 </tr>
		                <c:forEach var="cf" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}" varStatus="loopStatus">
		              	 <bean:define id="ctr">
		                	<c:out value="${loopStatus.index}"/>
		                </bean:define>

						<tr class="fontnormal">
			                <td width="30%" align="right">
								<mifos:mifoslabel name="${cf.lookUpEntity.entityType}" mandatory="${cf.mandatoryStringValue}" bundle="loanUIResources"></mifos:mifoslabel>:
							</td>
			                <td width="70%">
								<c:if test="${cf.fieldType == MasterConstants.CUSTOMFIELD_NUMBER}">
				                	<mifos:mifosnumbertext  name = "loanAccountActionForm" property='customField[${ctr}].fieldValue' maxlength="200"/>
				                </c:if>
				               	<c:if test="${cf.fieldType == MasterConstants.CUSTOMFIELD_ALPHANUMBER}">
				                	<mifos:mifosalphanumtext name = "loanAccountActionForm" property='customField[${ctr}].fieldValue' maxlength="200"/>
								</c:if>
				                <c:if test="${cf.fieldType == MasterConstants.CUSTOMFIELD_DATE}">
				                	<mifos:mifosalphanumtext name = "loanAccountActionForm" property='customField[${ctr}].fieldValue' maxlength="200"/>
				                </c:if>
				                <html-el:hidden property='customField[${ctr}].fieldId' value="${cf.fieldId}"></html-el:hidden>
				                <html-el:hidden property='customField[${ctr}].fieldType' value='${cf.fieldType}' />
		                	</td>
		           		</tr>
					     </c:forEach>
					</table>
					</c:if>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:submit property="editDetailsBtn"
								styleClass="buttn" >
								<mifos:mifoslabel name="loan.preview" />
							</html-el:submit> &nbsp; <html-el:button property="cancelButton"
								onclick="javascript:fun_cancel(this.form)"
								styleClass="cancelbuttn" >
								<mifos:mifoslabel name="loan.cancel" />
							</html-el:button></td>
						</tr>
					</table>
					<br>
					<br>
					</td>
				</tr>
			</table>
			</td>
			<html-el:hidden value="${BusinessKey.globalAccountNum}"
				property="globalAccountNum" />
			<html-el:hidden value="${BusinessKey.accountState.id}"
				property="accountStateId" />

		</html-el:form>
		</body>
	</tiles:put>
</tiles:insert>
