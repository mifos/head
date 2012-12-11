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
<%@taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<span id="page.id" title="NextPaymentLoanAccount"></span>
		<form method="get" action="viewLoanAccountDetails.ftl">			
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'isNewGlim')}" var="isNewGlim"/>
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"> <customtags:headerLink/> </span>
					</td>
				</tr>
			</table>

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" height="24" align="left" valign="top"
						class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="70%" class="headingorange">
							<span class="heading">
								<c:out value="${param.prdOfferingName}"/>&nbsp;#
								<c:out value="${param.globalAccountNum}"/> -
							</span>
							<mifos:mifoslabel name="loan.next_install_details" bundle="loanUIResources" /></td>
						</tr>		
						<tr><td>
	              <font class="fontnormalRedBold">
	              	<span id="nextPayment_loanAccount.error.message"><html-el:errors bundle="accountsUIResources" /></span>
	              </font></td>
              </tr>				
					</table>
						<br>
					<table width="60%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td bgcolor="#F0D4A5" style="padding-left:10px; padding-bottom:3px;">
							<span class="fontnormalbold">
								<mifos:mifoslabel name="loan.apply_trans" bundle="loanUIResources" />
							</span>&nbsp;&nbsp;&nbsp;&nbsp; 		
							
							
							<c:choose>
								<c:when test="${BusinessKey.parentGroupLoanAccount || BusinessKey.groupLoanAccountMember }">
									<c:url value="applyGroupPaymentAction.do" var="applyGroupPaymentActionMethodUrl" >										<
											<c:param name="method" value="load" />
											<c:param name="input" value="loan" />
											<c:param name="prdOfferingName" value="${param.prdOfferingName}" />
											<c:param name="globalAccountNum" value="${param.globalAccountNum}" />
											<c:param name="accountId" value="${param.accountId}" />
											<c:param name="accountType" value="${BusinessKey.accountType.accountTypeId}" />
											<c:param name="recordOfficeId" value="${param.recordOfficeId}" />
											<c:param name="recordLoanOfficerId" value="${param.recordLoanOfficerId}" />
											<c:param name="randomNUm" value="${sessionScope.randomNUm}" />
											<c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
									</c:url >
									<html-el:link styleId="loanaccountdetail.link.applyPayment"
										href="${applyGroupPaymentActionMethodUrl}">
										<mifos:mifoslabel name="loan.apply_payment" />
									</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									
											<c:url value="applyAdjustment.do" var="applyAdjustmentLoadAdjustmentMethodUrl" >
												<c:param name="method" value="listPossibleAdjustments" />
												<c:param name="accountId" value="${param.accountId}" />
												<c:param name="globalAccountNum" value="${param.globalAccountNum}" />
												<c:param name="prdOfferingName" value="${param.prdOfferingName}" />
												<c:param name="randomNUm" value="${sessionScope.randomNUm}" />
												<c:param name="currentFlowKey" value="${requestScope.currentFlowKey}" />
											</c:url >
									<c:choose>
										<c:when
											test="${(BusinessKey.accountState.id=='5' || BusinessKey.accountState.id=='9' || BusinessKey.accountState.id=='6') }">
												<html-el:link styleId="loanaccountdetail.link.applyAdjustment"
												href="${applyAdjustmentLoadAdjustmentMethodUrl}">
												<mifos:mifoslabel name="loan.apply_adjustment" />
												</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										</c:when>
									</c:choose>
									<html-el:link styleId="loanaccountdetail.link.applyCharges"
											href="applyChargeAction.do?method=load&accountId=${BusinessKey.accountId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
											<mifos:mifoslabel name="loan.apply_charges" />
											</html-el:link><br>
										
								</c:when>
								<c:otherwise>
							
							
							
												
							<c:if test="${param.accountStateId==5 || param.accountStateId==9}">
							<html-el:link styleId="nextPayment_loanAccount.link.applyPayment" href="applyPaymentAction.do?method=load&input=loan&prdOfferingName=${param.prdOfferingName}&globalAccountNum=${param.globalAccountNum}&accountId=${param.accountId}&accountType=${param.accountType}&recordOfficeId=${param.recordOfficeId}&recordLoanOfficerId=${param.recordLoanOfficerId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
							<mifos:mifoslabel name="loan.apply_payment" />
							</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							</c:if>
							<c:if test="${param.lastPaymentAction != 10 && (param.accountStateId==5 || param.accountStateId==9)}">							
							<html-el:link styleId="nextPayment_loanAccount.link.applyAdjustment" href="applyAdjustment.do?method=listPossibleAdjustments&accountId=${param.accountId}&globalAccountNum=${param.globalAccountNum}&prdOfferingName=${param.prdOfferingName}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="loan.apply_adjustment"	bundle="loanUIResources" />
							</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							</c:if>
							 <html-el:link styleId="nextPayment_loanAccount.link.applyCharges" href="applyChargeAction.do?method=load&accountId=${param.accountId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="loan.apply_charges" bundle="loanUIResources" />
							</html-el:link>	
							
							
							</c:otherwise>
							</c:choose>
							
													
							</td>
						</tr>
					</table>
					<br>				
					
					<table id="loanInstallmentTable" width="60%" border="0" cellpadding="3" cellspacing="0">
						<tr class="drawtablerowboldnoline">
							<td width="60%">&nbsp;</td>
							<td width="27%" align="right">
							<mifos:mifoslabel name="loan.amount" bundle="loanUIResources" />
							</td>							
							<td width="13%" align="right">&nbsp;</td>
						</tr>
						<tr>
			                <td class="drawtablerowbold">
			                <mifos:mifoslabel name="loan.current_installment" />
			                </td>
			                <td align="right" class="drawtablerow">&nbsp;</td>
			                <td align="right" class="drawtablerow">&nbsp;</td>
			              </tr>
			              
						<tr>
							<td class="drawtablerow">
								<mifos:mifoslabel name="loan.principal"	bundle="loanUIResources" />
							</td>
							<td align="right" class="drawtablerow">
							<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'viewUpcomingInstallmentDetails')}" var="viewUpcomingInstallmentDetail" />
							<fmt:formatNumber value='${viewUpcomingInstallmentDetail.principal}'/>
							</td>
							<td align="right" class="drawtablerow">&nbsp;</td>
						</tr>
						 
						<tr>
							<td class="drawtablerow">
							<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" />
							</td>
							<td align="right" class="drawtablerow"><fmt:formatNumber value='${viewUpcomingInstallmentDetail.interest}'/></td>
							<td align="right" class="drawtablerow">&nbsp;</td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel name="loan.fees"	bundle="loanUIResources" /></td>
							<td align="right" class="drawtablerow">
							<fmt:formatNumber value='${viewUpcomingInstallmentDetail.fees}'/></td>
							<td align="right" class="drawtablerow">	&nbsp;
							<c:if test='${viewUpcomingInstallmentDetail.fees != 0.0 }'>						 
							<html-el:link styleId="nextPayment_loanAccount.link.waiveFeeDue" href="loanAccountAction.do?method=waiveChargeDue&prdOfferingName=${param.prdOfferingName}&accountId=${param.accountId}&WaiveType=fees&type=LoanAccount&input=LoanAccount&globalAccountNum=${param.globalAccountNum}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="loan.waive" bundle="loanUIResources" />
							</html-el:link>
							</c:if>
							</td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel name="loan.penalty" bundle="loanUIResources" /></td>
							<td align="right" class="drawtablerow"><fmt:formatNumber value='${viewUpcomingInstallmentDetail.penalty}'/></td>
							<td align="right" class="drawtablerow">&nbsp;
							<c:if test='${viewUpcomingInstallmentDetail.penalty != 0.0 }'>						 
							<html-el:link styleId="nextPayment_loanAccount.link.waivePenaltyDue" href="loanAccountAction.do?method=waiveChargeDue&accountId=${param.accountId}&WaiveType=penalty&type=LoanAccount&input=LoanAccount&globalAccountNum=${param.globalAccountNum}&prdOfferingName=${param.prdOfferingName}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="loan.waive" bundle="loanUIResources" />
							</html-el:link>
							</c:if>
							</td>
						</tr>
						<tr>
							<td class="drawtablerow">
							<em>
							<mifos:mifoslabel name="loan.subTotal" bundle="loanUIResources" />
							</em>
							</td>
							<td align="right" class="drawtablerow">
							<fmt:formatNumber value='${viewUpcomingInstallmentDetail.subTotal}'/>
							</td>
							<td align="right" class="drawtablerow">&nbsp;</td>
						</tr>						
											
						<tr>
							<td class="drawtablerowbold">
							<mifos:mifoslabel name="loan.overdueamount" bundle="loanUIResources" /></td>
							<td align="right" class="drawtablerow">
							&nbsp;
							</td>
							<td align="right" class="drawtablerow">&nbsp;</td>
						</tr>						
						
						<tr>
							<td class="drawtablerow">
							<mifos:mifoslabel name="loan.principal"	bundle="loanUIResources" /></td>
							<td align="right" class="drawtablerow">
							<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'viewOverDueInstallmentDetails')}" var="viewOverDueInstallmentDetail" />
							<fmt:formatNumber value='${viewOverDueInstallmentDetail.principal}'/></td>
							<td align="right" class="drawtablerow">&nbsp;</td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" /></td>
							<td align="right" class="drawtablerow"><fmt:formatNumber value='${viewOverDueInstallmentDetail.interest}'/></td>
							<td align="right" class="drawtablerow">&nbsp;</td>
						</tr>
						<tr>
							<td class="drawtablerow">
							<mifos:mifoslabel name="loan.fees" bundle="loanUIResources" /></td>
							<td align="right" class="drawtablerow"><fmt:formatNumber value='${viewOverDueInstallmentDetail.fees}'/></td>
							<td align="right" class="drawtablerow">&nbsp;
							<c:if test='${viewOverDueInstallmentDetail.fees != 0.0 }'>	
							<html-el:link styleId="nextPayment_loanAccount.link.waiveFeeOverDue" href="loanAccountAction.do?method=waiveChargeOverDue&accountId=${param.accountId}&WaiveType=fees&type=LoanAccount&input=LoanAccount&globalAccountNum=${param.globalAccountNum}&prdOfferingName=${param.prdOfferingName}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="loan.waive" bundle="loanUIResources" />
							</html-el:link>
							</c:if>
							</td>
						</tr>
						<tr>
							<td class="drawtablerow">
							<mifos:mifoslabel name="loan.penalty" bundle="loanUIResources" /></td>
							<td align="right" class="drawtablerow"><fmt:formatNumber value='${viewOverDueInstallmentDetail.penalty}'/></td>
							<td align="right" class="drawtablerow">&nbsp;
							<c:if test='${viewOverDueInstallmentDetail.penalty != 0.0 }'>
							<html-el:link styleId="nextPayment_loanAccount.link.waivePenaltyOverDue" href="loanAccountAction.do?method=waiveChargeOverDue&accountId=${param.accountId}&WaiveType=penalty&type=LoanAccount&input=LoanAccount&globalAccountNum=${param.globalAccountNum}&prdOfferingName=${param.prdOfferingName}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="loan.waive" bundle="loanUIResources" />
							</html-el:link>
							</c:if>
							</td>
						</tr>
						
						<tr>
							<td class="drawtablerow">
							<em>
							<mifos:mifoslabel name="loan.subTotal" bundle="loanUIResources" />
							</em>
							</td>
							<td align="right" class="drawtablerow">
							<fmt:formatNumber value='${viewOverDueInstallmentDetail.subTotal}'/>
							</td>
							<td align="right" class="drawtablerow">&nbsp;</td>
						</tr>	
						
						<tr>
							<td class="drawtablerowbold">
							<mifos:mifoslabel name="loan.totalDueOn" bundle="loanUIResources" />
								<c:out
									value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'nextMeetingDate'))}" />
								</td>
							<td align="right" class="drawtablerow">&nbsp;
							<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'totalAmountOverDue')}" var="totalAmountOverdue" />
							</td>
							
							<td align="right" class="drawtablerow">&nbsp;</td>
						</tr>
						<tr>
							<td colspan="3">&nbsp;</td>
						</tr>
					</table>
					<table width="96%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td>
							<input type="submit" id="nextPayment_loanAccount.button.back" name="returnToAccountDetailsbutton"	class="buttn" value="<mifos:mifoslabel name="loan.returnToAccountDetails" bundle="loanUIResources" />"/>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			<html-el:hidden property="accountId" value="${param.accountId}"/>
			<html-el:hidden property="globalAccountNum" value="${param.globalAccountNum}"/>			
			<html-el:hidden property="prdOfferingName" value="${param.prdOfferingName}"/>
			<html-el:hidden property="accountType" value="${param.accountType}" />													
			<html-el:hidden property="currentStatusId" value="${param.accountStateId}" />							
			
		</form>
	</tiles:put>
</tiles:insert>
