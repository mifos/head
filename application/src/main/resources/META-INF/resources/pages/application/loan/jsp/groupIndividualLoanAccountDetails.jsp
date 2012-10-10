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
            <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'containsQGForCloseLoan')}"
               var="containsQGForCloseLoan" />

            <c:set value="${requestScope.backPageUrl}" var="backPageUrl"/>
            <c:if test="${ empty requestScope.backPageUrl}">
                <c:set value="viewLoanAccountDetails.ftl?globalAccountNum=${loanInformationDto.globalAccountNum}" var="backPageUrl"/>
            </c:if>

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
                                href="viewLoanAccountRepaymentSchedule.ftl?globalAccountNum=${loanInformationDto.globalAccountNum}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
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
                                <fmt:formatNumber value="${loanInformationDto.totalAmountDue}" /></td>
                            </tr>
                            <tr>
                                <td colspan="2" class="fontnormal"><mifos:mifoslabel
                                    name="loan.arrear" />: <fmt:formatNumber
                                    value="${loanInformationDto.totalAmountInArrears}" /></td>
                            </tr>
                        </table>
                    </c:if> <c:if
                        test="${loanInformationDto.accountStateId == 5 || loanInformationDto.accountStateId == 9}">
                        <table width="96%" border="0" cellpadding="3" cellspacing="0">
                            <tr>
                                 <c:url value="viewLoanAccountNextInstallmentDetails.ftl" var="viewLoanAccountNextInstallmentDetailsUrl">
                                    <c:param name="accountId" value="${loanInformationDto.accountId}"/>
                                    <c:param name="prdOfferingName" value="${loanInformationDto.prdOfferingName}"/>
                                    <c:param name="globalAccountNum" value="${loanInformationDto.globalAccountNum}"/>
                                    <c:param name="accountType" value="${loanInformationDto.accountTypeId}"/>
                                    <c:param name="accountStateId" value="${loanInformationDto.accountStateId}"/>
                                    <c:param name="recordOfficeId" value="${loanInformationDto.officeId}"/>
                                    <c:param name="recordLoanOfficerId" value="${loanInformationDto.personnelId}"/>
                                    <c:param name="lastPaymentAction" value="${loanInformationDto.accountId}"/>
                                    <c:param name="randomNUm" value="${sessionScope.randomNUm}"/>
                                    <c:param name="currentFlowKey" value="${requestScope.currentFlowKey}"/>
                                 </c:url>
                                <td width="42%" align="right" class="fontnormal"><span
                                    class="fontnormal"> <html-el:link styleId="loanaccountdetail.link.viewInstallmentDetails"
                                    href="${viewLoanAccountNextInstallmentDetailsUrl}">
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
                            <td align="right" class="drawtablerow"><fmt:formatNumber
                                value="${loanInformationDto.loanSummary.originalPrincipal}" /></td>
                            <td align="right" class="drawtablerow"><fmt:formatNumber
                                value="${loanInformationDto.loanSummary.principalPaid}" /></td>
                            <td align="right" class="drawtablerow"><fmt:formatNumber
                                value="${loanInformationDto.loanSummary.principalDue}" /></td>
                        </tr>
                        <tr>
                            <td class="drawtablerow"><mifos:mifoslabel
                                name="${ConfigurationConstants.INTEREST}" /></td>
                            <td align="right" class="drawtablerow"><fmt:formatNumber
                                value="${loanInformationDto.loanSummary.originalInterest}" /></td>
                            <td align="right" class="drawtablerow"><fmt:formatNumber
                                value="${loanInformationDto.loanSummary.interestPaid}" /></td>
                            <td align="right" class="drawtablerow"><fmt:formatNumber
                                value="${loanInformationDto.loanSummary.interestDue}" /></td>
                        </tr>
                        <tr>
                            <td class="drawtablerow"><mifos:mifoslabel name="loan.fees" /></td>
                            <td align="right" class="drawtablerow" id="LoanAccountDetail.text.loanFees"><fmt:formatNumber
                                value="${loanInformationDto.loanSummary.originalFees}" /></td>
                            <td align="right" class="drawtablerow"><fmt:formatNumber
                                value="${loanInformationDto.loanSummary.feesPaid}" /></td>
                            <td align="right" class="drawtablerow"><fmt:formatNumber
                                value="${loanInformationDto.loanSummary.feesDue}" /></td>
                        </tr>
                        <tr>
                            <td class="drawtablerow"><mifos:mifoslabel
                                name="loan.penalty" /></td>
                            <td align="right" class="drawtablerow"><fmt:formatNumber
                                value="${loanInformationDto.loanSummary.originalPenalty}" /></td>
                            <td align="right" class="drawtablerow"><fmt:formatNumber
                                value="${loanInformationDto.loanSummary.penaltyPaid}" /></td>
                            <td align="right" class="drawtablerow"><fmt:formatNumber
                                value="${loanInformationDto.loanSummary.penaltyDue}" /></td>
                        </tr>
                        <tr>
                            <td class="drawtablerow"><mifos:mifoslabel name="loan.total" /></td>
                            <td align="right" class="drawtablerow"><fmt:formatNumber
                                value="${loanInformationDto.loanSummary.totalLoanAmnt}" /></td>
                            <td align="right" class="drawtablerow"><fmt:formatNumber
                                value="${loanInformationDto.loanSummary.totalAmntPaid}" /></td>
                            <td align="right" class="drawtablerow"><fmt:formatNumber
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
                            <c:url value="viewLoanAccountAllActivity.ftl" var="viewLoanAccountAllActivityMethodUrl">
                                <c:param name="accountId" value="${loanInformationDto.accountId}"/>
                                <c:param name="prdOfferingName" value="${loanInformationDto.prdOfferingName}"/>
                                <c:param name="accountStateId" value="${loanInformationDto.accountStateId}"/>
                                <c:param name="globalAccountNum" value="${loanInformationDto.globalAccountNum}"/>
                                <c:param name="lastPaymentAction" value="${loanInformationDto.accountId}"/>
                                <c:param name="accountType" value="${loanInformationDto.accountTypeId}"/>
                                <c:param name="randomNUm" value="${sessionScope.randomNUm}"/>
                                <c:param name="currentFlowKey" value="${requestScope.currentFlowKey}"/>
                            </c:url>
                            <td width="65%" align="right" class="fontnormal">&nbsp; <c:if
                                test="${loanInformationDto.loanActivityDetails == true}">
                                <html-el:link styleId="loanaccountdetail.link.viewAccountActivity"
                                    href="${viewLoanAccountAllActivityMethodUrl}">
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
                            </fmt:message>:&nbsp;<span class="fontnormal"><span id="loanaccountdetail.text.interestRate"><fmt:formatNumber
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
                            <mifos:mifoslabel name="loan.no_of_inst" />:&nbsp;<span id="loanaccountdetail.text.noOfInst"><fmt:formatNumber
                                value="${loanInformationDto.noOfInstallments}" /></span> <mifos:mifoslabel
                                name="loan.allowed_no_of_inst" />&nbsp;<fmt:formatNumber
                                value="${loanInformationDto.minNoOfInstall}" />
                            -&nbsp;<fmt:formatNumber
                                value="${loanInformationDto.maxNoOfInstall}" />)
                            <br>
                            <mifos:mifoslabel name="loan.grace_period" />:&nbsp;<fmt:formatNumber
                                value="${loanInformationDto.gracePeriodDuration}" />&nbsp;<mifos:mifoslabel
                                name="loan.inst" /><br>
                            <mifos:mifoslabel name="loan.source_fund" />:&nbsp; <c:out
                                value="${loanInformationDto.fundName}" /><br>
                            </td>
                        </tr>
                    </table>


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
                                        href="executeAdminDocument.ftl?adminDocumentId=${adminDoc.admindocId}&entityId=${loanInformationDto.globalAccountNum}">
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
                            </c:if>
                            <span class="fontnormalbold"> <mifos:mifoslabel name="loan.recurring_acc_fees" /><br>
                            </span> <c:forEach items="${loanInformationDto.accountFees}" var="feesSet" varStatus="status">
                                <c:if
                                    test="${feesSet.feeFrequencyTypeId == '1' && feesSet.feeStatus != '2'}">
                                <span id="loanAccountDetail.text.periodicFeeName_<fmt:formatNumber value="${status.count}"/>">
                                    <c:out value="${feesSet.feeName}" /></span>:
                                        <span class="fontnormal"> <fmt:formatNumber
                                        value="${feesSet.accountFeeAmount}" />&nbsp;( <mifos:mifoslabel
                                        name="loan.periodicityTypeRate" /> <c:out
                                        value="${feesSet.meetingRecurrence}" />)
                                    <html-el:link styleId="loanAccountDetail.link.removePeriodicFee_${status.count}"
                                        href="accountAppAction.do?method=removeFees&feeId=${feesSet.feeId}&globalAccountNum=${loanInformationDto.globalAccountNum}&accountId=${loanInformationDto.accountId}&recordOfficeId=${loanInformationDto.officeId}&recordLoanOfficerId=${loanInformationDto.personnelId}&createdDate=${loanInformationDto.createdDate}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}&input=Loan">
                                        <mifos:mifoslabel name="loan.remove" />
                                    </html-el:link> <br>
                                    </span>
                                </c:if>
                            </c:forEach><br>
                            <span class="fontnormalbold"> <mifos:mifoslabel name="loan.one_time_acc_fees" /><br>
                            </span> <c:forEach items="${loanInformationDto.accountFees}" var="feesSet" varStatus="status">
                                <c:if
                                    test="${feesSet.feeFrequencyTypeId == '2' && feesSet.feeStatus != '2'}">
                                    <span id="loanAccountDetail.text.oneTimeFeeName_<fmt:formatNumber value="${status.count}"/>">
                                        <c:out value="${feesSet.feeName}"/></span>:
                                        <span class="fontnormal"> <fmt:formatNumber
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
                            <%--    <span class="fontnormal"><a id="loanaccountdetail.link.partial" href="loan_account_detail_partial.htm">Detail
                                        - partial/pending/cancelled</a><br>
                                        <a id="loanaccountdetail.link.closed" href="loan_account_detail_closed.htm">Detail - closed</a></span>
                                        <br> --%>
                            <span class="fontnormalbold">
                                <mifos:mifoslabel name="loan.recurring_acc_penalties" />
                                <br>
                            </span>
                            <c:forEach items="${loanInformationDto.accountPenalties}" var="penaltySet" varStatus="status">
                                <c:if test="${penaltySet.penaltyFrequencyId != '1' && penaltySet.penaltyStatus != '2'}">
                                    <span id="loanAccountDetail.text.periodicPenaltyName_<fmt:formatNumber value="${status.count}"/>">
                                        <c:out value="${penaltySet.penaltyName}"/>
                                    </span>:
                                    <span class="fontnormal">
                                        <fmt:formatNumber value="${penaltySet.accountPenaltyAmount}" />&nbsp;
                                        (<c:out value="${penaltySet.penaltyFrequencyName}"/>) &nbsp;
                                        <html-el:link styleId="loanAccountDetail.link.removePenalty_${status.count}"
                                            href="accountAppAction.do?method=removePenalties&penaltyId=${penaltySet.penaltyId}&globalAccountNum=${loanInformationDto.globalAccountNum}&accountId=${loanInformationDto.accountId}&recordOfficeId=${loanInformationDto.officeId}&recordLoanOfficerId=${loanInformationDto.personnelId}&createdDate=${loanInformationDto.createdDate}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}&input=Loan">
                                            <mifos:mifoslabel name="loan.remove" />
                                        </html-el:link> 
                                        <br>
                                    </span>
                                </c:if>
                            </c:forEach>
                            <br>
                            <span class="fontnormalbold">
                                <mifos:mifoslabel name="loan.one_time_acc_penalties" />
                                <br>
                            </span>
                            <c:forEach items="${loanInformationDto.accountPenalties}" var="penaltySet" varStatus="status">
                                <c:if test="${penaltySet.penaltyFrequencyId == '1' && penaltySet.penaltyStatus != '2'}">
                                    <span id="loanAccountDetail.text.oneTimePenaltyName_<fmt:formatNumber value="${status.count}"/>">
                                        <c:out value="${penaltySet.penaltyName}"/>
                                    </span>:
                                    <span class="fontnormal">
                                        <fmt:formatNumber value="${penaltySet.accountPenaltyAmount}" />&nbsp;
                                        <!-- if account state is LOAN_PARTIAL_APPLICATION or LOAN_PENDING_APPROVAL then enable removal -->
                                        <c:if test="${loanInformationDto.accountStateId == '1' || loanInformationDto.accountStateId == '2'}">
                                            <html-el:link styleId="loanAccountDetail.link.removeOneTimePenalty_${status.count}"
                                                href="accountAppAction.do?method=removePenalties&penaltyId=${penaltySet.penaltyId}&globalAccountNum=${loanInformationDto.globalAccountNum}&accountId=${loanInformationDto.accountId}&recordOfficeId=${loanInformationDto.officeId}&recordLoanOfficerId=${loanInformationDto.personnelId}&createdDate=${loanInformationDto.createdDate}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}&input=Loan">
                                                <mifos:mifoslabel name="loan.remove" />
                                            </html-el:link> 
                                        </c:if>
                                        <br>
                                    </span>
                                </c:if>
                            </c:forEach>
                            <br>
                            </td>
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
                                        <c:url value="viewLoanAccountPayments.ftl" var="viewLoanAccountPaymentsUrl" >
                                            <c:param name="globalAccountNum" value="${loanInformationDto.globalAccountNum}" />
                                        </c:url >
                                        <a id="loanaccountdetail.link.accountpayments" href="${viewLoanAccountPaymentsUrl}">
                                            <mifos:mifoslabel name="loan.ViewLoanAccountPaymentsLink" />
                                        </a> <br/>
                                        <c:set var="questionnaireFor" scope="session" value="${loanInformationDto.prdOfferingName}"/>
                                        <c:remove var="urlMap" />
                                        <jsp:useBean id="urlMap" class="java.util.LinkedHashMap"  type="java.util.HashMap" scope="session"/>
                                        <c:set target="${urlMap}" property="${loanInformationDto.officeName}" value="custSearchAction.do?method=getOfficeHomePage&officeId=${loanInformationDto.officeId}"/>
                                        <c:set target="${urlMap}" property="${loanInformationDto.customerName}" value="clientCustAction.do?method=get&globalCustNum=${loanInformationDto.globalCustNum}"/>
                                        <c:set target="${urlMap}" property="${loanInformationDto.prdOfferingName}" value="viewLoanAccountDetails.ftl?globalAccountNum=${loanInformationDto.globalAccountNum}"/>
                                        <c:url value="viewAndEditQuestionnaire.ftl" var="viewAndEditQuestionnaireMethodUrl" >
                                            <c:param name="creatorId" value="${sessionScope.UserContext.id}" />
                                            <c:param name="entityId" value="${loanInformationDto.accountId}" />
                                            <c:param name="event" value="Create" />
                                            <c:param name="source" value="Loan" />
                                            <c:param name="backPageUrl" value="${backPageUrl}" />
                                        </c:url >
                                        <c:if test="${containsQGForCloseLoan}">
                                            <a id="loanaccountdetail.link.questionGroupsClose" href="${viewAndEditQuestionnaireForClosedLoanResponsesLinkMethodUrl}&backPageUrl=${backPageUrl}">
                                            <mifos:mifoslabel name="loan.ViewQuestionGroupForClosedLoanResponsesLink" />
                                            </a> <br>
                                        </c:if>
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
                                            href="viewLoanAccountTransactionHistory.ftl?globalAccountNum=${loanInformationDto.globalAccountNum}&accountId=${loanInformationDto.accountId}&prdOfferingName=${loanInformationDto.prdOfferingName}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
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
                                        <c:url value="applyIndividualPayment.do" var="applyPaymentActionMethodUrl" >
                                            <c:param name="method" value="load" />
                                            <c:param name="input" value="loan" />
                                            <c:param name="prdOfferingName" value="${loanInformationDto.prdOfferingName}" />
                                            <c:param name="globalAccountNum" value="${loanInformationDto.globalAccountNum}" />
                                            <c:param name="accountId" value="${loanInformationDto.accountId}" />
                                            <c:param name="accountType" value="${loanInformationDto.accountTypeId}" />
                                            <c:param name="recordOfficeId" value="${loanInformationDto.officeId}" />
                                            <c:param name="recordLoanOfficerId" value="${loanInformationDto.personnelId}" />
                                            <c:param name="randomNUm" value="${sessionScope.randomNUm}" />
                                            <c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
                                            </c:url >
                                        <html-el:link styleId="loanaccountdetail.link.applyPayment"
                                            href="${applyPaymentActionMethodUrl}">
                                            <mifos:mifoslabel name="loan.apply_payment" />
                                        </html-el:link>

                                        <c:if test="${loanInformationDto.interestType == 2 || loanInformationDto.interestType == 4}">
                                            <c:url value="customLoanRepayment.ftl" var="customLoanRepaymentMethodUrl" >
                                                <c:param name="globalAccountNum" value="${loanInformationDto.globalAccountNum}" />
                                            </c:url >
                                            <br/>
                                            <html-el:link styleId="loanaccountdetail.link.applyPrincipalPrePayment"
                                                href="${customLoanRepaymentMethodUrl}">
                                                    <mifos:mifoslabel name="loan.apply_prepayment" />
                                                </html-el:link>
                                        </c:if>
                                        <br/>
                                    </c:if> 
                                    <c:url value="applyAdjustment.do" var="applyAdjustmentLoadAdjustmentMethodUrl" >
                                        <c:param name="method" value="listPossibleAdjustments" />
                                        <c:param name="accountId" value="${loanInformationDto.accountId}" />
                                        <c:param name="globalAccountNum" value="${loanInformationDto.globalAccountNum}" />
                                        <c:param name="prdOfferingName" value="${loanInformationDto.prdOfferingName}" />
                                        <c:param name="randomNUm" value="${sessionScope.randomNUm}" />
                                        <c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
                                    </c:url >
                                    <c:choose>
                                        <c:when
                                            test="${(loanInformationDto.accountStateId=='5' || loanInformationDto.accountStateId=='9' || loanInformationDto.accountStateId=='6') }">
                                            <html-el:link styleId="loanaccountdetail.link.applyAdjustment"
                                                href="${applyAdjustmentLoadAdjustmentMethodUrl}">
                                                <mifos:mifoslabel name="loan.apply_adjustment" />
                                            </html-el:link>
                                            <br>
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
                                        </span></td>
                                    </tr>
                                </c:when>
                            </c:choose> 
                        <c:url value="repayLoanAction.do" var="repayLoanActionLoadRepaymentMethodUrl" >
                            <c:param name="method" value="loadRepayment" />
                            <c:param name="accountId" value="${loanInformationDto.accountId}" />
                            <c:param name="globalAccountNum" value="${loanInformationDto.globalAccountNum}" />
                            <c:param name="prdOfferingName" value="${loanInformationDto.prdOfferingName}" />
                            <c:param name="randomNUm" value="${sessionScope.randomNUm}" />
                            <c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
                        </c:url >
                            <c:choose>
                                <c:when
                                    test="${ loanInformationDto.accountStateId=='9' || loanInformationDto.accountStateId=='5'}">
                                    <span class="fontnormal8pt"> 
                                    <html-el:link styleId="loanaccountdetail.link.repayLoan"
                                        href="${repayLoanActionLoadRepaymentMethodUrl}">
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
                                name="loan.of_payments" /> <fmt:formatNumber
                                value="${loanInformationDto.performanceHistory.noOfPayments}" /></span></td>
                        </tr>
                        <tr>
                            <td class="paddingL10"><span class="fontnormal8pt"><mifos:mifoslabel
                                name="loan.missed_payments" /> <fmt:formatNumber
                                value="${loanInformationDto.performanceHistory.totalNoOfMissedPayments}" /></span></td>
                        </tr>
                        <tr>
                            <td class="paddingL10"><span class="fontnormal8pt"><mifos:mifoslabel
                                name="loan.days_arrears" /><fmt:formatNumber
                                value="${loanInformationDto.performanceHistory.daysInArrears}" /> </span></td>
                        </tr>
                        <tr>
                            <td class="paddingL10"><span class="fontnormal8pt"><mifos:mifoslabel
                                name="loan.maturity_date" /><c:out
                                value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,loanInformationDto.performanceHistory.loanMaturityDate)}" />
                            </span></td>
                        </tr>
                    </table>
                    <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'overpayments')}"
                    var="overpayments" />
                    <c:if test="${!empty overpayments}">
                        <table width="95%" border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td><img src="pages/framework/images/trans.gif" width="7" height="8"></td>
                            </tr>
                        </table>
                        <table width="100%" border="0" cellpadding="2" cellspacing="0"
                            class="bluetableborder">
                            <tr>
                                <td colspan="2" class="bluetablehead05">
                                    <span class="fontnormalbold"> <mifos:mifoslabel name="loan.overpayments" /> </span>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2" class="paddingL10">
                                    <img src="pages/framework/images/trans.gif" width="10" height="2">
                                </td>
                            </tr>
                            <c:forEach items="${overpayments}" var="overpayment">
                                      <tr>
                                        <td width="70%" class="paddingL10">
                                          <span class="fontnormal8pt">
                                            <c:out value="${overpayment.actualOverpaymentAmount}"/>
                                          </span>
                                        </td>
                                        <td width="30%" align="left" class="paddingL10">
                                             <c:url value="clearOverpayment.ftl" var="clearOverpaymentMethodUrl" >
                                              <c:param name="globalAccountNum" value="${loanInformationDto.globalAccountNum}" />
                                              <c:param name="overpaymentId" value="${overpayment.overpaymentId}" />
                                             </c:url >
                                          <span class="fontnormal8pt">
                                              <a href="${clearOverpaymentMethodUrl}">
                                                  <mifos:mifoslabel name="loan.overpayments_clear" />
                                              </a>
                                          </span>
                                        </td>
                                      </tr>
                            </c:forEach>
                        </table>
                    </c:if>
                    <table width="95%" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td><img src="pages/framework/images/trans.gif" width="7"
                                height="8"></td>
                        </tr>
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
                            <td class="paddingL10" id="recentNotes"><c:choose>
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
