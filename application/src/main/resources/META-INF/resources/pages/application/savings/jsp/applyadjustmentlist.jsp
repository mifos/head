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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
    <tiles:put name="body" type="string">
        <span id="page.id" title="listSavingsPossibleAdjustments"></span>
        <SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>

        <c:set
            value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'savingsAdjustablePayments')}"
            var="possibleAdjustments" />
        <c:set
            value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
            var="BusinessKey" />

        <table width="95%" border="0" cellpadding="0" cellspacing="0">
            <tr>
                <td class="bluetablehead05"><span
                    class="fontnormal8pt"> <customtags:headerLink />
                </span></td>
            </tr>
        </table>
        <c:choose>
            <c:when test="${empty possibleAdjustments}">
                <p>
                    <span class="fontnormal"><mifos:mifoslabel
                            name="account.no_adjustments"
                            bundle="accountsUIResources" /></span>
                </p>
            </c:when>
            <c:otherwise>
                <!--  Header text -->
                <p>
                    <span class="heading"> <c:out
                            value="${BusinessKey.savingsOffering.prdOfferingName}" />&nbsp;#&nbsp;
                        <c:out
                            value="${BusinessKey.globalAccountNum}" />
                        &nbsp; - &nbsp; <span class="headingorange"><mifos:mifoslabel
                                name="accounts.list_adjustments"
                                bundle="accountsUIResources" /></span>
                    </span>
                </p>
                <!-- Adjustments table -->
                <table id="possibleAdjustments2" width="100%"
                    cellspacing="0" cellpadding="3" border="0">
                    <tbody>
                        <tr class="drawtablerowbold">
                            <td width="8%" align="left"><b><mifos:mifoslabel
                                        name="accounts.date_of_trxn"
                                        bundle="accountsUIResources" /></b></td>
                            <td width="8%" align="left"><b><mifos:mifoslabel
                                        name="Description"
                                        bundle="SavingsUIResources" /></b></td>
                            <td width="8%" align="left"><b><mifos:mifoslabel
                                        name="Amount"
                                        bundle="accountsUIResources" /></b></td>
                            <td width="8%" align="left"><b><mifos:mifoslabel
                                        name="accounts.mode_of_payment"
                                        bundle="accountsUIResources" /></b></td>
                            <td width="8%" align="left"><b><mifos:mifoslabel
                                        name="accounts.receiptdate"
                                        bundle="accountsUIResources" /></b></td>
                            <td width="8%" align="left"><b><mifos:mifoslabel
                                        name="accounts.receiptid"
                                        bundle="accountsUIResources" /></b></td>
                            <td width="8%" align="left"><b><mifos:mifoslabel
                                        name="accounts.apply_adjustment"
                                        bundle="accountsUIResources" /></b></td>
                        </tr>
                        <c:forEach var="possibleAdjustment"
                            items="${possibleAdjustments}">
                            <tr>
                                <td class="drawtablerow" align="left">${possibleAdjustment.paymentDate}</td>
                                <td class="drawtablerow" align="left">
                                    <c:choose>
                                        <c:when
                                            test="${possibleAdjustment.deposit}">
                                            <mifos:mifoslabel
                                                name="AccountAction-Deposit"
                                                bundle="LookupValueMessages" />
                                        </c:when>
                                        <c:otherwise>
                                            <mifos:mifoslabel
                                                name="AccountAction-Withdrawal"
                                                bundle="LookupValueMessages" />
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="drawtablerow" align="left"><fmt:formatNumber
                                        value="${possibleAdjustment.amount}" /></td>
                                <td class="drawtablerow" align="left">${possibleAdjustment.paymentType}</td>
                                <td class="drawtablerow" align="left">
                                    <c:choose>
                                        <c:when
                                            test="${possibleAdjustment.receiptDate == null}">
                                            -
                                        </c:when>
                                        <c:otherwise>
                                            ${possibleAdjustment.receiptDate}
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="drawtablerow" align="left">
                                    <c:choose>
                                        <c:when
                                            test="${empty possibleAdjustment.receiptId}">
                                            -
                                        </c:when>
                                        <c:otherwise>
                                            ${possibleAdjustment.receiptId}
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="drawtablerow" align="left">
                                    <html-el:link
                                        styleId="savingsaccountdetail.link.applyAdjustment"
                                        href="savingsApplyAdjustmentAction.do?method=load&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}&paymentId=${possibleAdjustment.paymentId}"
                                        styleClass="listAdjustments.link.applyAdjustment">
                                        <mifos:mifoslabel
                                            name="Savings.applyAdjustment" />
                                    </html-el:link>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </tiles:put>
</tiles:insert>
