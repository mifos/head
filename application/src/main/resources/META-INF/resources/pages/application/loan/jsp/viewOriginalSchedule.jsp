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
<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<span id="page.id" title="ViewOriginalSchedule"></span>
		<script type="text/javascript" src="pages/application/loan/js/loanRepayment.js"></script>

		<html-el:form method="post" action="/loanAccountAction.do">
		<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
        <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'disbursementDate')}" var="disbursementDate" />

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"> <customtags:headerLink/> </span>
					</td>
				</tr>
			</table>

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="83%" class="headingorange"><span class="heading">
								<c:out value="${BusinessKey.loanOffering.prdOfferingName}"/>&nbsp;#
								<c:out value="${BusinessKey.globalAccountNum}"/>-
								</span>
                                <mifos:mifoslabel name="loan.original.schedule" bundle="loanUIResources" />
                            </td>
						</tr>
					</table>
                    <br>
                    <span class="fontnormalbold">
                        <mifos:mifoslabel name="loan.amt" />:&nbsp;
                    </span>
                    <span class="fontnormal">
                        <c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanAmount')}"/>
                        <br>
                    </span>
                    <span class="fontnormalbold">
                        <mifos:mifoslabel name="loan.proposed_date" />:&nbsp;
                    </span>
                    <span class="fontnormal">
                        <c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,disbursementDate)}"/>
                        <br>
                    </span>
                    <br>
                    <span class="headingOrange">
                        <mifos:mifoslabel name="loan.originalScheduleInstallments" />&nbsp;
                    </span>
                    <br/>
                    <mifoscustom:mifostabletag source="originalInstallments" scope="session"
                        xmlFileName="OriginalSchedule.xml"
                        moduleName="org/mifos/accounts/loan/util/resources" passLocale="true"/>
                    <br>
                    					                    
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center">
                                <html-el:button styleId="loanRepayment.button.return" property="returnToRepaymentScheduleButton"
                                    onclick="LoanRepayment.submit(this.form,'getLoanRepaymentSchedule&accountId=${param.accountId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}');" styleClass="buttn" >
                                    <mifos:mifoslabel name="loan.returnToRepaymentSchedule" bundle="loanUIResources" />
                                </html-el:button>
							</td>
						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>

		<html-el:hidden property="input" value="viewOriginalSchedule"/>
		<html-el:hidden property="accountId" value="${BusinessKey.accountId}"/>
		<html-el:hidden property="accountStateId" value="${BusinessKey.accountState.id}"/>
		<html-el:hidden property="accountTypeId" value="${BusinessKey.accountType.accountTypeId}"/>
		<html-el:hidden property="recordOfficeId" value="${param.recordOfficeId}"/>
		<html-el:hidden property="recordLoanOfficerId" value="${param.recordLoanOfficerId}"/>
		<html-el:hidden property="globalAccountNum" value="${BusinessKey.globalAccountNum}"/>
		<html-el:hidden property="prdOfferingName" value="${BusinessKey.loanOffering.prdOfferingName}"/>
	</html-el:form>
	</tiles:put>
</tiles:insert>
