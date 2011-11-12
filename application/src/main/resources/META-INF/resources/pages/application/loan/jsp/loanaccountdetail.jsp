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
<%@ page
	import="org.mifos.config.persistence.ConfigurationPersistence"%>
<%@ page
	import="org.mifos.accounts.loan.util.helpers.LoanConstants"%>

<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>


<%
boolean isDisplay = (new ConfigurationPersistence().getConfigurationValueInteger(LoanConstants.ADMINISTRATIVE_DOCUMENT_IS_ENABLED) == 1);
%>
     	
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<span id="page.id" title="LoanAccountDetail" ></span>
	
		<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
		<fmt:setBundle basename="org.mifos.config.localizedResources.LoanUIResources"/>
		
		<html-el:form method="post" action="/loanAccountAction.do">		
			<c:set
				value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanaccountownerisagroup')}"
				var="loanaccountownerisagroup" />
			<c:set
				value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
				var="loanAccount" />
			<c:set
				value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanInformationDto')}"
				var="loanInformationDto" />
			<c:set
				value="${loanInformationDto.customerId}"
				var="customerId" />
			<c:set
				value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'accountStateNameLocalised')}"
				var="accountStateNameLocalised" />
			<c:set
				value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'gracePeriodTypeNameLocalised')}"
				var="gracePeriodTypeNameLocalised" />
			<c:set
				value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'interestTypeNameLocalised')}"
				var="interestTypeNameLocalised" />
			<c:set
				value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'accountFlagNamesLocalised')}"
				var="accountFlagNamesLocalised" />


			<html-el:hidden property="currentFlowKey"
				value="${requestScope.currentFlowKey}" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt">
					<customtags:headerLink selfLink="false" /> </span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="62%" class="headingorange"><c:out
								value="${loanInformationDto.prdOfferingName}" />&nbsp;# <span id="loanaccountdetail.text.loanid"><c:out
								value="${loanInformationDto.globalAccountNum}" /></span> <br>
							</td>
							<td width="38%" rowspan="2" align="right" valign="top"
								class="fontnormal"><c:if
								test="${loanInformationDto.accountStateId != 6 and loanInformationDto.accountStateId != 7 and loanInformationDto.accountStateId !=8 and loanInformationDto.accountStateId !=10}">
								<html-el:link styleId="loanaccountdetail.link.editAccountStatus"
									href="editStatusAction.do?method=load&accountId=${loanInformationDto.accountId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
									<mifos:mifoslabel name="loan.edit_acc_status" />
								</html-el:link>
							</c:if><br>
							</td>
						</tr>
					</table>
					<table width="100%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td><font class="fontnormalRedBold"><span id="loanaccountdetail.error.message"><html-el:errors
								bundle="loanUIResources" /></span></font></td>
						</tr>
					</table>
					<table width="96%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td class="fontnormalbold"><span class="fontnormal">
							<mifoscustom:MifosImage id="${loanInformationDto.accountStateId}"
								moduleName="org.mifos.accounts.util.resources.accountsImages" /> <span id="loanaccountdetail.text.status"><c:out
								value="${accountStateNameLocalised}" />&nbsp; 
								<c:forEach
								var="flagSet" items="${accountFlagNamesLocalised}">
								<span class="fontnormal"><c:out
									value="${flagSet}" /></span>
							</c:forEach> </span></td>
						</tr>
						<tr>
							<td class="fontnormal"><mifos:mifoslabel
								name="loan.proposed_date" />: <span id="loanaccountdetail.details.disbursaldate"><c:out
								value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,loanInformationDto.disbursementDate)}" /></span>
							<c:if test="${loanInformationDto.redone}">
                                        &nbsp(<mifos:mifoslabel
									name="loan.is_redo_loan" />)
                                    </c:if></td>
						</tr>
						<tr id="Loan.PurposeOfLoan">
							<td class="fontnormal"><mifos:mifoslabel
								name="loan.business_work_act" keyhm="Loan.PurposeOfLoan"
								isManadatoryIndicationNotRequired="yes" /> <mifos:mifoslabel
								name="${ConfigurationConstants.LOAN}" isColonRequired="yes" />
							<c:forEach var="busId"
								items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessActivities')}">
								<c:if test="${busId.id eq loanInformationDto.businessActivityId}">
									<span id="loanaccountdetail.text.purposeofloan"><c:out value="${busId.name}" /></span>
								</c:if>
							</c:forEach></td>
						</tr>
					</table>
					<table width="50%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="pages/framework/images/trans.gif" width="10"
								height="10"></td>
						</tr>
					</table>
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="33%" class="headingorange"><mifos:mifoslabel
								name="loan.acc_summary" /></td>
							<td width="33%" align="right" class="fontnormal"><html-el:link styleId="loanaccountdetail.link.viewRepaymentSchedule"
								href="loanAccountAction.do?method=getLoanRepaymentSchedule&input=reviewTransactionPage&accountId=${loanInformationDto.accountId}&prdOfferingName=${loanInformationDto.prdOfferingName}&globalAccountNum=${loanInformationDto.globalAccountNum}&accountType=${loanInformationDto.accountTypeId}&accountStateId=${loanInformationDto.accountStateId}&recordOfficeId=${loanInformationDto.officeId}&recordLoanOfficerId=${loanInformationDto.personnelId}&lastPaymentAction=${loanInformationDto.accountId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="loan.view_schd" />
							</html-el:link></td>
						</tr>
					</table>

					<c:if
						test="${loanInformationDto.accountStateId == 5 || loanInformationDto.accountStateId == 9}">
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="58%" class="fontnormal"><mifos:mifoslabel
									name="loan.totalAmtDue" /> <c:out
									value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,loanInformationDto.nextMeetingDate)}" />:
								<c:out value="${loanInformationDto.totalAmountDue}" /></td>
								<%--<span
										class="fontnormal"><a id="loanaccountdetail.link.viewInstDetails"  href="nextPayment_loanAccount.htm"><mifos:mifoslabel name="loan.view_inst_details" /></a></span><a href="#"><span
										class="fontnormalbold"> </span></a>--%>
								<%--<c:if test="${loanInformationDto.accountStateId == 5 || loanInformationDto.accountStateId == 9}">
									<td width="42%" align="right" class="fontnormal">
										<span class="fontnormal"><html-el:link styleId="loanaccountdetail.link.viewInstallmentDetails" href="loanAction.do?method=getInstallmentDetails&accountId=${loanInformationDto.accountId}&accountName=${loanInformationDto.prdOfferingName}&globalAccountNum=${loanInformationDto.globalAccountNum}
																&accountType=${loanInformationDto.accountTypeId}
																&accountStateId=${loanInformationDto.accountStateId}
																&recordOfficeId=${loanInformationDto.officeId}
																&recordLoanOfficerId=${loanInformationDto.personnelId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}"> 
											<mifos:mifoslabel name="loan.view_installment_details" /></span>
										</html-el:link>
									</td>
	              				  </c:if>--%>
							</tr>
							<tr>
								<td colspan="2" class="fontnormal"><mifos:mifoslabel
									name="loan.arrear" />: <c:out
									value="${loanInformationDto.totalAmountInArrears}" /></td>
							</tr>
						</table>
					</c:if> <c:if
						test="${loanInformationDto.accountStateId == 5 || loanInformationDto.accountStateId == 9}">
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="42%" align="right" class="fontnormal"><span
									class="fontnormal"> <html-el:link styleId="loanaccountdetail.link.viewInstallmentDetails"
									href="loanAccountAction.do?method=getInstallmentDetails&accountId=${loanInformationDto.accountId}&prdOfferingName=${loanInformationDto.prdOfferingName}&globalAccountNum=${loanInformationDto.globalAccountNum}&accountType=${loanInformationDto.accountTypeId}&accountStateId=${loanInformationDto.accountStateId}&recordOfficeId=${loanInformationDto.officeId}&recordLoanOfficerId=${loanInformationDto.personnelId}&lastPaymentAction=${loanInformationDto.accountId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
									<mifos:mifoslabel name="loan.view_installment_details" />
								</html-el:link> </span></td>
							</tr>
						</table>
					</c:if>
					<table width="96%" border="0" cellpadding="3" cellspacing="0" id="loanSummaryTable">
						<tr class="drawtablerow">
							<td width="24%">&nbsp;</td>
							<td width="20%" align="right" class="drawtablerowboldnoline">
							<fmt:message key="loan.originalLoan">
							<fmt:param><mifos:mifoslabel
								name="${ConfigurationConstants.LOAN}" /></fmt:param>
							</fmt:message></td>
							<td width="28%" align="right" class="drawtablerowboldnoline">
							<mifos:mifoslabel name="loan.amt_paid" /></td>
							<td width="28%" align="right" class="drawtablerowboldnoline">
							<fmt:message key="loan.loanBalance">
							<fmt:param><mifos:mifoslabel
								name="${ConfigurationConstants.LOAN}" /></fmt:param>
							</fmt:message></td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel
								name="loan.principal" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${loanInformationDto.loanSummary.originalPrincipal}" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${loanInformationDto.loanSummary.principalPaid}" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${loanInformationDto.loanSummary.principalDue}" /></td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel
								name="${ConfigurationConstants.INTEREST}" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${loanInformationDto.loanSummary.originalInterest}" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${loanInformationDto.loanSummary.interestPaid}" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${loanInformationDto.loanSummary.interestDue}" /></td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel name="loan.fees" /></td>
							<td align="right" class="drawtablerow" id="LoanAccountDetail.text.loanFees"><c:out
								value="${loanInformationDto.loanSummary.originalFees}" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${loanInformationDto.loanSummary.feesPaid}" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${loanInformationDto.loanSummary.feesDue}" /></td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel
								name="loan.penalty" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${loanInformationDto.loanSummary.originalPenalty}" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${loanInformationDto.loanSummary.penaltyPaid}" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${loanInformationDto.loanSummary.penaltyDue}" /></td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel name="loan.total" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${loanInformationDto.loanSummary.totalLoanAmnt}" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${loanInformationDto.loanSummary.totalAmntPaid}" /></td>
							<td align="right" class="drawtablerow"><c:out
								value="${loanInformationDto.loanSummary.totalAmntDue}" /></td>
						</tr>
						<tr>
							<td colspan="4">&nbsp;</td>
						</tr>
					</table>
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="35%" class="headingorange"><c:if
								test="${loanInformationDto.accountStateId == 3 || loanInformationDto.accountStateId == 4 || loanInformationDto.accountStateId == 5
									 || loanInformationDto.accountStateId == 6 || loanInformationDto.accountStateId == 7 || loanInformationDto.accountStateId == 8 || loanInformationDto.accountStateId == 9}">
								<mifos:mifoslabel name="loan.recentActivity" />
							</c:if></td>
							<td width="65%" align="right" class="fontnormal">&nbsp; <c:if
								test="${loanInformationDto.loanActivityDetails == true}">
								<html-el:link styleId="loanaccountdetail.link.viewAccountActivity"
									href="loanAccountAction.do?method=getAllActivity&accountId=${loanInformationDto.accountId}&prdOfferingName=${loanInformationDto.prdOfferingName}&accountStateId=${loanInformationDto.accountStateId}&globalAccountNum=${loanInformationDto.globalAccountNum}&lastPaymentAction=${loanInformationDto.accountId}&accountType=${loanInformationDto.accountTypeId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
									<mifos:mifoslabel name="loan.view_acc_activity" />
								</html-el:link>
							</c:if></td>
						</tr>
					</table>
					<c:if
						test="${loanInformationDto.accountStateId == 3 || loanInformationDto.accountStateId == 4 || loanInformationDto.accountStateId == 5
									 || loanInformationDto.accountStateId == 6 || loanInformationDto.accountStateId == 7 || loanInformationDto.accountStateId == 8 || loanInformationDto.accountStateId == 9}">
						<mifoscustom:mifostabletag source="recentAccountActivities"
							scope="session" xmlFileName="RecentAccountActivity.xml"
							moduleName="org/mifos/accounts/loan/util/resources" passLocale="true" />
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="3">&nbsp;</td>
							</tr>
						</table>
					</c:if>
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="69%" class="headingorange"><mifos:mifoslabel
								name="loan.acc_details" /></td>
							<td width="31%" align="right" valign="top" class="fontnormal"><c:if
								test="${loanInformationDto.accountStateId != 6 && loanInformationDto.accountStateId != 7 && loanInformationDto.accountStateId !=8 && loanInformationDto.accountStateId !=10}">
								<html-el:link styleId="loanaccountdetail.link.editAccountInformation"
									action="loanAccountAction.do?method=manage&customerId=${customerId}&globalAccountNum=${loanInformationDto.globalAccountNum}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
									<mifos:mifoslabel name="loan.edit_acc_info" />
								</html-el:link>
							</c:if></td>
						</tr>
						<tr>
							<td height="23" colspan="2" class="fontnormal"><span
								class="fontnormalbold"> 
								<fmt:message key="loan.interestRules">
									<fmt:param><mifos:mifoslabel
										name="${ConfigurationConstants.INTEREST}" /></fmt:param>
								</fmt:message></span> <span class="fontnormal"><br>
							<fmt:message key="loan.interestRateType">
								<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" /></fmt:param>
							</fmt:message>:&nbsp; <c:out
								value="${interestTypeNameLocalised}" /> <br>
							<fmt:message key="loan.interestRate">
								<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" /></fmt:param>
							</fmt:message>:&nbsp;<span class="fontnormal"><span id="loanaccountdetail.text.interestRate"><c:out
								value="${loanInformationDto.interestRate}" /></span>%&nbsp;<mifos:mifoslabel
								name="loan.apr" /> </span><br>
							</span> <fmt:message key="loan.interestDisbursement">
								<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" /></fmt:param>
							</fmt:message>:<c:choose>
								<c:when test="${loanInformationDto.interestDeductedAtDisbursement}">
									<mifos:mifoslabel name="loan.yes" />
								</c:when>
								<c:otherwise>
									<mifos:mifoslabel name="loan.no" />
								</c:otherwise>
							</c:choose><br>
							<br>
							<span class="fontnormalbold"> <mifos:mifoslabel
								name="loan.repaymentRules" /> </span><br>
							<mifos:mifoslabel name="loan.freq_of_inst" />:&nbsp;<c:out
								value="${loanInformationDto.recurAfter}" />
							<c:choose>
								<c:when
									test="${loanInformationDto.recurrenceId == '1'}">
									<mifos:mifoslabel name="loan.week(s)" />
								</c:when>
								<c:otherwise>
									<mifos:mifoslabel name="loan.month(s)" />
								</c:otherwise>
							</c:choose> <br>
							<mifos:mifoslabel name="loan.principle_due" />:<c:choose>
								<c:when
									test="${loanInformationDto.prinDueLastInst == true}">
									<mifos:mifoslabel name="loan.yes" />
								</c:when>
								<c:otherwise>
									<mifos:mifoslabel name="loan.no" />
								</c:otherwise>
							</c:choose> <br>
							<mifos:mifoslabel name="loan.grace_period_type" />:&nbsp; <c:out
								value="${gracePeriodTypeNameLocalised}" /><br>
							<mifos:mifoslabel name="loan.no_of_inst" />:&nbsp;<span id="loanaccountdetail.text.noOfInst"><c:out
								value="${loanInformationDto.noOfInstallments}" /></span> <mifos:mifoslabel
								name="loan.allowed_no_of_inst" />&nbsp;<c:out
								value="${loanInformationDto.minNoOfInstall}" />
							-&nbsp;<c:out
								value="${loanInformationDto.maxNoOfInstall}" />)
							<br>
							<mifos:mifoslabel name="loan.grace_period" />:&nbsp;<c:out
								value="${loanInformationDto.gracePeriodDuration}" />&nbsp;<mifos:mifoslabel
								name="loan.inst" /><br>
							<mifos:mifoslabel name="loan.source_fund" />:&nbsp; <c:out
								value="${loanInformationDto.fundName}" /><br>
							</td>
						</tr>
					</table>



				<!-- GLIM Loan Account Details -->
				<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanAccountDetailsView')}"
				var="loanAccountDetailsView" />
					<c:if test="${loanInformationDto.group == true}">
						<c:if test="${loanAccountDetailsView != null}">
							<table width="96%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td valign="top"><mifoscustom:mifostabletag
										source="loanAccountDetailsView" scope="session"
										xmlFileName="LoanAccountDetails.xml"
										moduleName="org/mifos/accounts/loan/util/resources" passLocale="true" /></td>
								</tr>
							</table>
						</c:if>
					</c:if> <!--  -->

					<table width="96%" border="0" cellpadding="0" cellspacing="0">
						<tr id="collateral">
							<td class="fontnormal"><br>
							<span class="fontnormalbold"><mifos:mifoslabel
								name="loan.collateralDetails" /></span></td>
						</tr>
						<tr id="Loan.CollateralType">
							<td class="fontnormal"><mifos:mifoslabel
								name="loan.collateral_type" keyhm="Loan.CollateralType"
								isColonRequired="yes" isManadatoryIndicationNotRequired="yes" />&nbsp;
							<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'CollateralTypes')}" var="collateralType">
								<c:if test="${collateralType.id eq loanInformationDto.collateralTypeId}">
									<span id="loanaccountdetail.text.collateraltype"><c:out value="${collateralType.name}" /></span>
								</c:if>
							</c:forEach></td>
						</tr>
						<tr id="Loan.CollateralNotes">
							<td class="fontnormal"><br>
							<mifos:mifoslabel name="loan.collateral_notes"
								keyhm="Loan.CollateralNotes" isColonRequired="yes"
								isManadatoryIndicationNotRequired="yes" />&nbsp;<br>
							<span id="loanaccountdetail.text.collateralnote"><c:out value="${loanInformationDto.collateralNote}" /></span>
                            <br /></td>
						</tr>
						<script>
							if(document.getElementById("Loan.CollateralType").style.display=="none" &&
								document.getElementById("Loan.CollateralNotes").style.display=="none")
									document.getElementById("collateral").style.display="none";
						</script>
                        <tr id="Loan.ExternalId">
                            <td class="fontnormalbold"><mifos:mifoslabel name="accounts.externalId"
                                keyhm="Loan.ExternalId" isColonRequired="yes" isManadatoryIndicationNotRequired="yes" />
                            &nbsp; <span class="fontnormal"><span id="loanaccountdetail.text.externalid"><c:out value="${loanInformationDto.externalId}" /></span> </span></td>
                        </tr>
                        
                        <!-- Administrative documents -->
	              	<%
					 if(isDisplay) {
				    %>
	   				<tr>
						<td class="fontnormal"><br>
						 <span class="fontnormalbold"> 
						<mifos:mifoslabel
							name="reports.administrativedocuments" /> 
							<br></span>
						<c:forEach var="adminDoc"
							items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'administrativeDocumentsList')}">

								<c:forEach var="adminDocMixed" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'administrativeDocumentsAccStateMixList')}">
											<c:if test="${adminDocMixed.adminDocumentID.admindocId==adminDoc.admindocId}">
												<c:if test="${adminDocMixed.accountStateID.id==loanInformationDto.accountStateId}">
												<span class="fontnormal"> 
									  <html-el:link styleId="loanaccountdetail.link.viewAdminReport"
										href="reportsUserParamsAction.do?method=loadAdminReport&admindocId=${adminDoc.admindocId}&globalAccountNum=${loanInformationDto.globalAccountNum}">
										 <c:out value="${adminDoc.adminDocumentName}" />
								      </html-el:link>
								  				</span>
												<br>
												</c:if>
											</c:if>
								</c:forEach>

						</c:forEach>
						</td>

					</tr>
			            			               		              	
	              	<%
	              	}
	              	%>  
	         						



						<tr>
							<td class="fontnormal"><br>
							<c:if
								test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
								<span class="fontnormalbold"><mifos:mifoslabel
									name="loan.additionalInfo" /><br>
								</span>
								<span class="fontnormal"> <c:forEach var="cfdef"
									items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
									<c:forEach var="cf" items="${loanInformationDto.accountCustomFields}">
										<c:if test="${cfdef.fieldId==cf.fieldId}">
											<span class="fontnormal"> <mifos:mifoslabel
												name="${cfdef.lookUpEntity.entityType}"></mifos:mifoslabel>:
                                                <c:choose>
                                                <c:when test="${cfdef.fieldType == MasterConstants.CUSTOMFIELD_DATE}">
                                                    <c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,cf.fieldValue)}" />
                                                </c:when>
                                                <c:otherwise>
                                                    <c:out value="${cf.fieldValue}" />
                                                </c:otherwise>
                                                </c:choose>
                                                 </span>
											<br>
										</c:if>
									</c:forEach>
								</c:forEach> </span>
								<br>
							</c:if> <span class="fontnormalbold"> <mifos:mifoslabel
								name="loan.recurring_acc_fees" /><br>
							</span> <c:forEach items="${loanInformationDto.accountFees}" var="feesSet">
								<c:if
									test="${feesSet.feeFrequencyTypeId == '1' && feesSet.feeStatus != '2'}">
									<c:out value="${feesSet.feeName}" />:
										<span class="fontnormal"> <c:out
										value="${feesSet.accountFeeAmount}" />&nbsp;( <mifos:mifoslabel
										name="loan.periodicityTypeRate" /> <c:out
										value="${feesSet.meetingRecurrence}" />)
									<html-el:link styleId="loanaccountdetail.link.removeFee"
										href="accountAppAction.do?method=removeFees&feeId=${feesSet.feeId}&globalAccountNum=${loanInformationDto.globalAccountNum}&accountId=${loanInformationDto.accountId}&recordOfficeId=${loanInformationDto.officeId}&recordLoanOfficerId=${loanInformationDto.personnelId}&createdDate=${loanInformationDto.createdDate}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}&input=Loan">
										<mifos:mifoslabel name="loan.remove" />
									</html-el:link> <br>
									</span>
								</c:if>
							</c:forEach><br>
							<span class="fontnormalbold"> <mifos:mifoslabel	name="loan.one_time_acc_fees" /><br>
							</span> <c:forEach items="${loanInformationDto.accountFees}" var="feesSet" varStatus="status">
								<c:if
	 								test="${feesSet.feeFrequencyTypeId == '2' && feesSet.feeStatus != '2'}">
									<span id="loanAccountDetail.text.oneTimeFeeName_<c:out value="${status.count}"/>">
										<c:out value="${feesSet.feeName}"/></span>:
										<span class="fontnormal"> <c:out
										value="${feesSet.accountFeeAmount}" />&nbsp;
										<!-- if account state is LOAN_PARTIAL_APPLICATION or LOAN_PENDING_APPROVAL then enable removal -->
									<c:if test="${loanInformationDto.accountStateId == '1' || loanInformationDto.accountStateId == '2'}">					
											<html-el:link styleId="loanAccountDetail.link.removeOneTimeFee_${status.count}"
											href="accountAppAction.do?method=removeFees&feeId=${feesSet.feeId}&globalAccountNum=${loanInformationDto.globalAccountNum}&accountId=${loanInformationDto.accountId}&recordOfficeId=${loanInformationDto.officeId}&recordLoanOfficerId=${loanInformationDto.personnelId}&createdDate=${loanInformationDto.createdDate}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}&input=Loan">
											<mifos:mifoslabel name="loan.remove" />
										</html-el:link> 
									</c:if> <br>
									</span>
								</c:if>
							</c:forEach><br>							
							<%--	<span class="fontnormal"><a id="loanaccountdetail.link.partial" href="loan_account_detail_partial.htm">Detail
										- partial/pending/cancelled</a><br>
										<a id="loanaccountdetail.link.closed" href="loan_account_detail_closed.htm">Detail - closed</a></span>
										<br> --%></td>
						</tr>
					</table>
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="66%" class="headingorange"><mifos:mifoslabel
								name="loan.more_details" /></td>
						</tr>
						<tr>
							<td class="fontnormal"><%--
									<html-el:link styleId="loanaccountdetail.link.viewTransactionHistory"  href="transaction_history_loanAccount.htm"> <mifos:mifoslabel name="loan.view_transc_history" />
									</html-el:link><br>--%> 
									<span class="fontnormal">
                                        <c:set var="questionnaireFor" scope="session" value="${loanInformationDto.prdOfferingName}"/>
                                        <c:remove var="urlMap" />
                                        <jsp:useBean id="urlMap" class="java.util.LinkedHashMap"  type="java.util.HashMap" scope="session"/>
                                        <c:set target="${urlMap}" property="${loanInformationDto.officeName}" value="custSearchAction.do?method=getOfficeHomePage&officeId=${loanInformationDto.officeId}"/>
                                        <c:set target="${urlMap}" property="${loanInformationDto.customerName}" value="clientCustAction.do?method=get&globalCustNum=${loanInformationDto.globalCustNum}"/>
                                        <c:set target="${urlMap}" property="${loanInformationDto.prdOfferingName}" value="loanAccountAction.do?method=get&globalAccountNum=${loanInformationDto.globalAccountNum}"/>
							            <a id="loanaccountdetail.link.questionGroups" href="loanAccountAction.do?method=viewAndEditAdditionalInformation&creatorId=${sessionScope.UserContext.id}&entityId=${loanInformationDto.accountId}&event=Create&source=Loan&backPageUrl=loanAccountAction.do?method%3Dget%26globalAccountNum%3D${loanInformationDto.globalAccountNum}">
							    			<mifos:mifoslabel name="client.ViewQuestionGroupResponsesLink" bundle="ClientUIResources" />
										</a>
							            <br/>
									    <html-el:link styleId="loanaccountdetail.link.viewStatusHistory"
								          href="loanAccountAction.do?method=viewStatusHistory&globalAccountNum=${loanInformationDto.globalAccountNum}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
								          <mifos:mifoslabel name="loan.view_status_history" />
							            </html-el:link>
							            <br>
										<html-el:link styleId="loanaccountdetail.link.viewChangeLog"
											href="loanAccountAction.do?method=loadChangeLog&entityType=Loan&entityId=${loanInformationDto.accountId}&currentFlowKey=${requestScope.currentFlowKey}">
											<mifos:mifoslabel name="loan.view_change_log" />
										</html-el:link>
										<br>
										<html-el:link styleId="loanaccountdetail.link.viewTransactionHistory"
											href="accountAppAction.do?method=getTrxnHistory&input=LoanDetails&globalAccountNum=${loanInformationDto.globalAccountNum}&accountId=${loanInformationDto.accountId}&prdOfferingName=${loanInformationDto.prdOfferingName}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
											<mifos:mifoslabel name="Center.TransactionHistory" />
										</html-el:link> 
									</span>
							</td>
						</tr>
					</table>
					</td>
					<td width="30%" align="left" valign="top" class="paddingleft1">
					<table width="100%" border="0" cellpadding="2" cellspacing="0"
						class="orangetableborder">
						<tr>
							<td class="orangetablehead05"><span class="fontnormalbold"><mifos:mifoslabel
								name="loan.trxn" /></span></td>
						</tr>
						<tr>
							<td class="paddingL10">
							<c:choose>
								<c:when
									test="${loanInformationDto.accountStateId=='5' ||loanInformationDto.accountStateId=='6' || loanInformationDto.accountStateId=='7' || loanInformationDto.accountStateId=='8' || loanInformationDto.accountStateId=='9'}">
									<span class="fontnormal8pt"> <c:if
										test="${(loanInformationDto.accountStateId=='5' || loanInformationDto.accountStateId=='9')}">
										
										<html-el:link styleId="loanaccountdetail.link.applyPayment"
											href="applyPaymentAction.do?method=load&input=loan&prdOfferingName=${loanInformationDto.prdOfferingName}&globalAccountNum=${loanInformationDto.globalAccountNum}&accountId=${loanInformationDto.accountId}&accountType=${loanInformationDto.accountTypeId}
																	&recordOfficeId=${loanInformationDto.officeId}&recordLoanOfficerId=${loanInformationDto.personnelId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
											<mifos:mifoslabel name="loan.apply_payment" />
										</html-el:link>
										
										<br/>
										<a href="customLoanRepayment.ftl?globalAccountNum=${loanInformationDto.globalAccountNum}">Apply Custom Payment For Demo</a>
										<br/>
									</c:if> 
									<c:if test="${loanInformationDto.accountStateId!='6' && loanInformationDto.accountStateId!='7'}">
										<html-el:link styleId="loanaccountdetail.link.applyCharges"
											href="applyChargeAction.do?method=load&accountId=${loanInformationDto.accountId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
											<mifos:mifoslabel name="loan.apply_charges" />
										</html-el:link>
									</c:if><br>

									<c:choose>

										<c:when
											test="${(loanInformationDto.accountStateId=='5' || loanInformationDto.accountStateId=='9' || loanInformationDto.accountStateId=='6') }">
											<c:if test="${loanInformationDto.accountId != '10'}">

												<c:if test="${loanInformationDto.accountStateId=='6'}">
													<html-el:link styleId="loanaccountdetail.link.applyAdjustment"
														href="applyAdjustment.do?method=loadAdjustmentWhenObligationMet&accountId=${loanInformationDto.accountId}&globalAccountNum=${loanInformationDto.globalAccountNum}&prdOfferingName=${loanInformationDto.prdOfferingName}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
														<mifos:mifoslabel name="loan.apply_adjustment" />
													</html-el:link>
												</c:if>
												<c:if test="${loanInformationDto.accountStateId!='6'}">
													<html-el:link styleId="loanaccountdetail.link.applyAdjustment"
														href="applyAdjustment.do?method=loadAdjustment&accountId=${loanInformationDto.accountId}&globalAccountNum=${loanInformationDto.globalAccountNum}&prdOfferingName=${loanInformationDto.prdOfferingName}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
														<mifos:mifoslabel name="loan.apply_adjustment" />
													</html-el:link>
												</c:if>

												<br>
											</c:if>
										</c:when>
									</c:choose> </span>


								</c:when>
							</c:choose> <c:choose>
								<c:when
									test="${loanInformationDto.accountStateId=='1' || loanInformationDto.accountStateId=='2' || loanInformationDto.accountStateId=='3' || loanInformationDto.accountStateId=='4'}">

									<span class="fontnormal8pt"> <html-el:link styleId="loanaccountdetail.link.applyCharges"
										href="applyChargeAction.do?method=load&accountId=${loanInformationDto.accountId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
										<mifos:mifoslabel name="loan.apply_charges" />
									</html-el:link><br>
									</span>
								</c:when>
							</c:choose>
							<c:choose>
								<c:when
									test="${loanInformationDto.accountStateId==3 || loanInformationDto.accountStateId==4}">
									<tr>
										<td class="paddingL10"><span class="fontnormal8pt">
										<html-el:link styleId="loanaccountdetail.link.disburseLoan"
											href="loanDisbursementAction.do?method=load&accountId=${loanInformationDto.accountId}&globalAccountNum=${loanInformationDto.globalAccountNum}&prdOfferingName=${loanInformationDto.prdOfferingName}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
											<mifos:mifoslabel name="loan.disburseloan" />
										</html-el:link> <br>
										</span></td>
									</tr>
								</c:when>
							</c:choose> <c:choose>
								<c:when
									test="${ loanInformationDto.accountStateId=='9' || loanInformationDto.accountStateId=='5'}">
									<span class="fontnormal8pt"> <html-el:link styleId="loanaccountdetail.link.repayLoan"
										href="repayLoanAction.do?method=loadRepayment&accountId=${loanInformationDto.accountId}&globalAccountNum=${loanInformationDto.globalAccountNum}&prdOfferingName=${loanInformationDto.prdOfferingName}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
										<mifos:mifoslabel name="loan.repay" />
										<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
									</html-el:link><br>
									</span>
								</c:when>
							</c:choose></td>
						<tr>
					</table>
					<table width="95%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="pages/framework/images/trans.gif" width="7"
								height="8"></td>
						</tr>
					</table>
					
					<table width="100%" border="0" cellpadding="2" cellspacing="0"
						class="bluetableborder">
						<tr>
							<td class="bluetablehead05"><span class="fontnormalbold">
							<mifos:mifoslabel name="loan.performance_history" /> </span></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"><mifos:mifoslabel
								name="loan.of_payments" /> <c:out
								value="${loanInformationDto.performanceHistory.noOfPayments}" /></span></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"><mifos:mifoslabel
								name="loan.missed_payments" /> <c:out
								value="${loanInformationDto.performanceHistory.totalNoOfMissedPayments}" /></span></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"><mifos:mifoslabel
								name="loan.days_arrears" /><c:out
								value="${loanInformationDto.performanceHistory.daysInArrears}" /> </span></td>
						</tr>
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt"><mifos:mifoslabel
								name="loan.maturity_date" /><c:out
								value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,loanInformationDto.performanceHistory.loanMaturityDate)}" />
							</span></td>
						</tr>
					</table>
					<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'questionGroupInstances')}"
			   				var="questionGroupInstances" />
					<table width="95%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="pages/framework/images/trans.gif" width="7"
								height="8"></td>
						</tr>
					</table>
						<table width="100%" border="0" cellpadding="2" cellspacing="0"
							class="bluetableborder">
							<tr>
								<td colspan="2" class="bluetablehead05"><span
									class="fontnormalbold"> <mifos:mifoslabel
									name="Surveys.Surveys" bundle="SurveysUIResources" /> </span></td>
							</tr>
							<tr>
								<td colspan="2" class="paddingL10"><img
									src="pages/framework/images/trans.gif" width="10" height="2"></td>
							</tr>
								<c:if test="${!empty questionGroupInstances}">
						            <c:forEach items="${questionGroupInstances}" var="questionGroupInstance">
						              <tr>
						                <td width="70%" class="paddingL10">
						                  <span class="fontnormal8pt">
						                    <a id="${questionGroupInstance.id}" href="viewAndEditQuestionnaire.ftl?creatorId=${sessionScope.UserContext.id}&entityId=${loanInformationDto.accountId}&instanceId=${questionGroupInstance.id}&event=View&source=Loan&backPageUrl=<c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'currentPageUrl')}"/>%26method%3Dget">
						                      <c:out value="${questionGroupInstance.questionGroupTitle}"/>
						                    </a>
						                  </span>
						                </td>
						                <td width="30%" align="left" class="paddingL10">
						                  <span class="fontnormal8pt">
						                    <label id="label.${questionGroupInstance.id}">
						                        <c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale, questionGroupInstance.dateCompleted)}" />
						                    </label>
						                  </span>
						                </td>
						              </tr>
						            </c:forEach>
								</c:if>
							<tr>
								<td colspan="2" align="right" class="paddingleft05">
								<span class="fontnormal8pt">
									<a id="loanaccountdetail.link.attachSurvey" href="questionnaire.ftl?source=Loan&event=View&entityId=${loanInformationDto.accountId}&creatorId=${sessionScope.UserContext.id}&backPageUrl=<c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'currentPageUrl')}"/>%26method%3Dget">
										<mifos:mifoslabel name="Surveys.attachasurvey" bundle="SurveysUIResources" />
									</a><br>
								</span>
						</table>
						<table width="95%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td><img src="pages/framework/images/trans.gif" width="7"
									height="8"></td>
							</tr>
						</table>
					<table width="100%" border="0" cellpadding="2" cellspacing="0"
						class="bluetableborder">
						<tr>
							<td class="bluetablehead05"><span class="fontnormalbold">
							<mifos:mifoslabel name="loan.recent_notes" /> </span></td>
						</tr>
						<tr>
							<td class="paddingL10"><img
								src="pages/framework/images/trans.gif" width="10" height="2"></td>
						</tr>
						<tr>
							<td class="paddingL10"><c:choose>
								<c:when
									test="${!empty loanAccount.recentAccountNotes}">
									<c:forEach var="note" items="${loanAccount.recentAccountNotes}">
										<span class="fontnormal8ptbold"> <c:out
											value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,note.commentDate)}" />:</span>
										<span class="fontnormal8pt"> <c:out
											value="${note.comment}" />-<em> <c:out
											value="${note.personnel.displayName}" /></em><br>
										<br>
										</span>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<span class="fontnormal"> <mifos:mifoslabel
										name="accounts.NoNotesAvailable" /> </span>
								</c:otherwise>
							</c:choose></td>
						</tr>
						<tr>
							<td align="right" class="paddingleft05"><span
								class="fontnormal8pt"> <c:if
								test="${!empty loanAccount.recentAccountNotes}">
								<html-el:link styleId="loanaccountdetail.link.seeAllNotes"
									href="notesAction.do?method=search&accountId=${loanInformationDto.accountId}&globalAccountNum=${loanInformationDto.globalAccountNum}&accountTypeId=${loanInformationDto.accountTypeId}&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
									<mifos:mifoslabel name="loan.seeallnotes" />
								</html-el:link>
							</c:if> <br>
							<html-el:link styleId="loanaccountdetail.link.addNote"
								href="notesAction.do?method=load&accountId=${loanInformationDto.accountId}&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="loan.addnote" />
							</html-el:link> </span></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			<!-- This hidden variable is being used in the next page -->
			<html-el:hidden property="accountTypeId"
				value="${loanInformationDto.accountTypeId}" />
			<html-el:hidden property="accountId" value="${loanInformationDto.accountId}" />
			<html-el:hidden property="globalAccountNum"
				value="${loanInformationDto.globalAccountNum}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
