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

<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<span id="page.id" title="EditPreviewLoanAccount" />	
		<SCRIPT SRC="pages/application/loan/js/PreviewLoanAccount.js"></SCRIPT>
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<html-el:form method="post" action="/loanAccountAction.do?method=update"
			onsubmit="func_disableSubmitBtn('previewDetailsBtn')">
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<c:set
				value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
				var="BusinessKey" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanIndividualMonitoringIsEnabled')}"	
																													var="loanIndividualMonitoringIsEnabled" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanaccountownerisagroup')}"
																													 var="loanaccountownerisagroup" />
				
				
			<td height="822" align="left" valign="top" bgcolor="#FFFFFF"
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
								value="${sessionScope.loanAccountActionForm.prdOfferingName}" />&nbsp;#
							<c:out
								value="${sessionScope.loanAccountActionForm.globalAccountNum}" />-&nbsp;
							</span> <mifos:mifoslabel name="loan.preview" /> <mifos:mifoslabel
								name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel
								name="loan.acc_info" /></td>
						</tr>
						<tr>
							<td class="fontnormal"><mifos:mifoslabel
								name="loan.reviewaccountinformation1" /> <mifos:mifoslabel
								name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel
								name="loan.reviewaccountinformation2" /> <mifos:mifoslabel
								name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel
								name="loan.reviewaccountinformation3" /></td>
						</tr>
						<tr>
							<td><font class="fontnormalRedBold"> <span id="editPreviewLoanAccount.error.message"><html-el:errors
								bundle="loanUIResources" /></span> </font></td>
						</tr>
					</table>
					<br>

					<table width="95%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td colspan="2"><span class="fontnormalbold"> <mifos:mifoslabel
								name="loan.acc_owner" />:&nbsp; </span><span class="fontnormal"> <c:out
								value='${BusinessKey.customer.displayName}' /></span></td>
						</tr>
						<tr>
							<td width="100%" colspan="2" class="fontnormalbold"><mifos:mifoslabel
								name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel
								name="loan.loan_acc_details" /> <br>
							<br>
							
							
					</tr>
					<!-- Loan Account Details -->
					 <c:if test="${loanIndividualMonitoringIsEnabled == '1'}">
							 <c:if test="${loanaccountownerisagroup == 'yes'}">
					    <tr>
							<td valign="top">
	                          	  <mifoscustom:mifostabletag source="loanAccountDetailsView" scope="session" xmlFileName="LoanAccountDetails.xml" moduleName="accounts/loan" passLocale="true" />	                       
							</td>
						</tr>
						<br>						<br>
						     </c:if>
	                 </c:if>
						<!--  -->
						<tr>						
								
							<span class="fontnormal"> <mifos:mifoslabel
								name="${ConfigurationConstants.LOAN}" /><mifos:mifoslabel
								name="loan.amt" />:&nbsp; <span class="fontnormal"> <c:out
								value="${sessionScope.loanAccountActionForm.loanAmount}" /> </span><br>
							<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" /> <mifos:mifoslabel
								name="loan.int_rate" />:&nbsp; <span class="fontnormal"> <c:out
								value="${sessionScope.loanAccountActionForm.interestRate}" /> </span><br>
							<mifos:mifoslabel name="loan.definst" />:&nbsp; <span
								class="fontnormal"> <c:out
								value="${sessionScope.loanAccountActionForm.noOfInstallments}" />
							</span><br>
							<mifos:mifoslabel name="loan.proposed_date" />: <span
								class="fontnormal"> <c:out value="${sessionScope.loanAccountActionForm.disbursementDate}" />
							<br>
							</span> <mifos:mifoslabel name="loan.grace_period" />:&nbsp; <span
								class="fontnormal"> <c:out
								value="${sessionScope.loanAccountActionForm.gracePeriodDuration}" />&nbsp;
							<mifos:mifoslabel name="loan.inst" /><br>
							</span> <mifos:mifoslabel
								name="${ConfigurationConstants.INTEREST}" /> <mifos:mifoslabel
								name="loan.interest_disb" />:&nbsp; <span class="fontnormal">
								<c:choose>
								<c:when
									test="${sessionScope.loanAccountActionForm.intDedDisbursement==1}">
									<mifos:mifoslabel name="loan.yes" />
								</c:when>
								<c:otherwise>
									<mifos:mifoslabel name="loan.no" />
								</c:otherwise>
							</c:choose> </span></td>
						</tr>
						<c:if test="${loanIndividualMonitoringIsEnabled != '1'}">
							<tr id="Loan.PurposeOfLoan">
								<td class="fontnormal"><mifos:mifoslabel
									name="loan.business_work_act" keyhm="Loan.PurposeOfLoan"
									isManadatoryIndicationNotRequired="yes" /><mifos:mifoslabel
									name="${ConfigurationConstants.LOAN}" isColonRequired="yes" />&nbsp;
								<span class="fontnormal">
	
								 <c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessActivitieName')}" /></span></td>
							</tr>
						</c:if>
						<tr id="Loan.CollateralType">
							<td class="fontnormal"><mifos:mifoslabel
								name="loan.collateral_type" keyhm="Loan.CollateralType"
								isColonRequired="yes" isManadatoryIndicationNotRequired="yes" />&nbsp;
							<span class="fontnormal"> <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'CollateralTypes')}" var="collateralType">
								<c:if test="${collateralType.id eq sessionScope.loanAccountActionForm.collateralTypeId}">
									<c:out value="${collateralType.name}" />
								</c:if>
							</c:forEach></span></td>
						</tr>
						<tr id="Loan.CollateralNotes">
							<td class="fontnormal"><br>
							<mifos:mifoslabel name="loan.collateral_notes"
								keyhm="Loan.CollateralNotes" isColonRequired="yes"
								isManadatoryIndicationNotRequired="yes" />&nbsp; <span
								class="fontnormal"><br>
							<c:out
								value="${sessionScope.loanAccountActionForm.collateralNote}" />
							<br>
						</tr>
                        <tr id="accounts.externalId">
                            <td class="fontnormalbold"><mifos:mifoslabel name="accounts.externalId"
                                keyhm="accounts.externalId" isColonRequired="yes" isManadatoryIndicationNotRequired="no" />
                            &nbsp; <span class="fontnormal"><c:out value="${BusinessKey.externalId}" /> </span></td>
                        </tr>
                        <tr>
							<td class="fontnormal">
							<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
							<span class="fontnormalbold"><mifos:mifoslabel name="loan.additionalInfo" bundle="loanUIResources"/></span><br>
			                    	<c:forEach var="cfdef" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
										<c:forEach var="cf" items="${sessionScope.loanAccountActionForm.customFields}">
											<c:if test="${cfdef.fieldId==cf.fieldId}">
												<mifos:mifoslabel name="${cfdef.lookUpEntity.entityType}" bundle="loanUIResources"></mifos:mifoslabel>:
						        		  	 	<span class="fontnormal">
												<c:out value="${cf.fieldValue}"/>
											</span><br>
										</c:if>
									</c:forEach>
					  			</c:forEach>
							<br>
							</c:if>
							<html-el:button styleId="editPreviewLoanAccount.button.edit" property="editButton" styleClass="insidebuttn"
								onclick="fnEdit(this.form)">
								<mifos:mifoslabel name="loan.edit_loan_acc" />
							</html-el:button> </td>
						</tr>
					</table>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:submit styleId="editPreviewLoanAccount.button.submit" property="previewDetailsBtn"
								styleClass="buttn" >
								<mifos:mifoslabel name="loan.submit" />
							</html-el:submit> &nbsp; <html-el:button styleId="editPreviewLoanAccount.button.cancel" property="cancelButton"
								styleClass="cancelbuttn" 
								onclick="javascript:fun_cancel(this.form)">
								<mifos:mifoslabel name="loan.cancel" />
							</html-el:button></td>
						</tr>
					</table>
					<br>
					<br>
					</td>
				</tr>
			</table>
			<html-el:hidden value="${sessionScope.loanAccountActionForm.globalAccountNum}"
				property="globalAccountNum" />
				</html-el:form>
	</tiles:put>
</tiles:insert>
