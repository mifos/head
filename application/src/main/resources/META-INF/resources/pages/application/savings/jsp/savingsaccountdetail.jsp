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
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<span id="page.id" title="savingsaccountdetail"></span>
		<html-el:form method="post" action="/savingsAction.do">

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"> <customtags:headerLink selfLink="false" /> </span>
					</td>
				</tr>
			</table>
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
            <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'containsQGForCloseSavings')}"
			   var="containsQGForCloseSavings" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'administrativeDocumentsList')}" var="adminDoc" />
			<c:set var="questionnaireFor" scope="session" value="${BusinessKey.savingsOffering.prdOfferingName}" />
            <c:remove var="urlMap" />
            <jsp:useBean id="urlMap" class="java.util.LinkedHashMap"  type="java.util.HashMap" scope="session"/>
            <c:set target="${urlMap}" property="${BusinessKey.office.officeName}" value="clientsAndAccounts.ftl?officeId=${BusinessKey.office.officeId}"/>
            <c:set target="${urlMap}" property="${BusinessKey.customer.displayName}" value="viewClientDetails.ftl?globalCustNum=${BusinessKey.customer.globalCustNum}"/>
            <c:set target="${urlMap}" property="${BusinessKey.savingsOffering.prdOfferingName}" value="viewSavingsAccountDetails.ftl?globalAccountNum=${BusinessKey.globalAccountNum}"/>
		 	<c:set value="${requestScope.backPageUrl}" var="backPageUrl"/>
            <c:if test="${ empty requestScope.backPageUrl}">
           		<c:set value="viewSavingsAccountDetails.ftl?globalAccountNum=${BusinessKey.globalAccountNum}" var="backPageUrl"/>
            </c:if>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="62%" class="headingorange">
									<c:out value="${BusinessKey.savingsOffering.prdOfferingName}" />
									#
									<span id="savingsaccountdetail.text.savingsId"><c:out value="${BusinessKey.globalAccountNum}" /></span>
									<br>
								</td>
								<td width="38%" rowspan="2" align="right" valign="top" class="fontnormal">
									<c:if test="${BusinessKey.accountState.id != AccountStates.SAVINGS_ACC_CANCEL && BusinessKey.accountState.id != AccountStates.SAVINGS_ACC_CLOSED}">
										<html-el:link styleId="savingsaccountdetail.link.editAccountStatus" 
											href="editStatusAction.do?method=load&accountId=${BusinessKey.accountId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
											<mifos:mifoslabel name="Savings.editAccountStatus" />
										</html-el:link>
									</c:if>
									<br>
								</td>
							</tr>
						</table>
						<table width="100%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td>
									<font class="fontnormalRedBold"><html-el:errors bundle="SavingsUIResources" /></font>
								</td>
							</tr>
						</table>
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td class="fontnormalbold">
									<span class="fontnormal">
										<mifoscustom:MifosImage	id="${BusinessKey.accountState.id}" moduleName="org.mifos.accounts.util.resources.accountsImages" />
										<span id="savingsaccountdetail.status.text" ><c:out value="${BusinessKey.accountState.name}" /></span>&nbsp; 
										<c:forEach var="flagSet" items="${BusinessKey.accountFlags}">
											<span class="fontnormal">
												<c:if test="${flagSet.flag.id == 6}">
													<mifos:MifosImage id="blackflag" moduleName="org.mifos.accounts.util.resources.accountsImages" />
												</c:if>
												<c:out value="${flagSet.flag.name}" />
											</span>
										</c:forEach> 
									</span>
								</td>
							</tr>
							<tr>
								<td class="fontnormal">
									<mifos:mifoslabel name="Savings.accountBalance" />:
									<fmt:formatNumber value="${BusinessKey.savingsBalance.amount}" />
									<br>
								</td>
								<td align="right" valign="top" class="fontnormal">
									<c:if
										test="${BusinessKey.savingsOffering.savingsType.id == SavingsConstants.SAVINGS_MANDATORY
												&& (BusinessKey.accountState.id == AccountStates.SAVINGS_ACC_APPROVED ||
												BusinessKey.accountState.id == AccountStates.SAVINGS_ACC_INACTIVE)}">
										<html-el:link styleId="savingsaccountdetail.link.viewDepositDueDetails" 
											href="viewSavingsAccountDepositDueDetails.ftl?globalAccountNum=${BusinessKey.globalAccountNum}&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
 											<mifos:mifoslabel name="Savings.viewDepositDueDetails" />
										</html-el:link>
									</c:if>
								</td>
							</tr>
							<c:if test="${BusinessKey.accountState.id != AccountStates.SAVINGS_ACC_CLOSED}">
								<tr>
									<td class="fontnormal" colspan="2">
										<c:choose>
											<c:when test="${empty BusinessKey.nextMeetingDate}">
												<mifos:mifoslabel name="Savings.totalAmountDue" isColonRequired="yes"/>
											</c:when>
											<c:otherwise>
												<mifos:mifoslabel name="Savings.totalAmountDueOn"/>
												<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,BusinessKey.nextMeetingDate)}" />:
											</c:otherwise>
										</c:choose>
										<span id="savingsaccountdetail.text.totalAmountDue">
											<c:if test="${BusinessKey.savingsOffering.savingsType.id == SavingsConstants.SAVINGS_MANDATORY}">
												<fmt:formatNumber value="${BusinessKey.totalAmountDue.amount}" />
											</c:if>
											<c:if test="${BusinessKey.savingsOffering.savingsType.id == SavingsConstants.SAVINGS_VOLUNTARY}">
												<fmt:formatNumber value="${BusinessKey.totalAmountDueForNextInstallment.amount}" />
											</c:if>
										</span>										
									</td>
								</tr>
							</c:if>
						</table>
						<table width="50%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									<img src="pages/framework/images/trans.gif" width="10" height="10">
								</td>
							</tr>
						</table>
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="35%" class="headingorange">
									<mifos:mifoslabel name="Savings.recentActivity" />
								</td>
								<td width="65%" align="right" class="fontnormal">
									&nbsp;
									<html-el:link styleId="savingsaccountdetail.link.viewAllAccountActivity" 
										href="viewSavingsAccountRecentActivity.ftl?globalAccountNum=${BusinessKey.globalAccountNum}&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
										<mifos:mifoslabel name="Savings.viewAllAccountActivity" />
									</html-el:link>
								</td>
							</tr>
						</table>
						<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'recentActivityForDetailPage')}" var="recentActivityForDetailPage" scope="session" />

						<mifoscustom:mifostabletag source="recentActivityForDetailPage" scope="session" xmlFileName="SavingsAccountRecentActivity.xml" moduleName="org/mifos/accounts/savings/util/resources" passLocale="true" />
						<br>
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="69%" height="23" class="headingorange">
									<mifos:mifoslabel name="Savings.AccountDetails" />
								</td>
								<td width="31%" align="right" valign="top" class="fontnormal">
									&nbsp;
								</td>
							</tr>
							<tr>
								<td height="23" class="fontnormal">
									<p>
										<span class="fontnormal"> <c:choose>
												<c:when test="${BusinessKey.savingsOffering.savingsType.id == SavingsConstants.SAVINGS_MANDATORY}">
													<mifos:mifoslabel name="Savings.mandatoryAmountForDeposit" isColonRequired="yes"/>
	                  </c:when>
												<c:otherwise>
													<mifos:mifoslabel name="Savings.recommendedAmountForDeposit" isColonRequired="yes"/>
	                  </c:otherwise>
											</c:choose> <fmt:formatNumber value="${BusinessKey.recommendedAmount.amount}" /> <c:choose>
												<c:when test="${BusinessKey.customer.customerLevel.id==CustomerConstants.GROUP_LEVEL_ID}">
	                    ( <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'RecommendedAmtUnit')}"
											var="item">
							<c:if test="${BusinessKey.savingsOffering.recommendedAmntUnit.id == item.id}">
							    <c:out value="${item.name}"></c:out>
							</c:if>

					</c:forEach> )
	                    </c:when>
												<c:otherwise>
													 <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'RecommendedAmtUnit')}"
											var="item">
							<c:if test="${BusinessKey.savingsOffering.recommendedAmntUnit.id == item.id}">
							    <c:out value="${item.name}"></c:out>
							</c:if>

					</c:forEach>
												</c:otherwise>
											</c:choose> <br> <br> <mifos:mifoslabel name="Savings.typeOfDeposits" />:<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'SavingsType')}"
											var="item">
							<c:if test="${BusinessKey.savingsOffering.savingsType.id == item.id}">
							    <c:out value="${item.name}"></c:out>
							</c:if>

					</c:forEach>

											 </span>
										<br>
										<mifos:mifoslabel name="Savings.maxAmountPerWithdrawl" isColonRequired="yes"/>
										<fmt:formatNumber value="${BusinessKey.savingsOffering.maxAmntWithdrawl.amount}" />
										<br>
										<span class="fontnormal"> <mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" /> <mifos:mifoslabel name="Savings.rate" />: <fmt:formatNumber value="${BusinessKey.savingsOffering.interestRate}" /><mifos:mifoslabel name="Savings.perc" />
										</span>
										<br>
										
										<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
										<br>
										<span class="fontnormalbold"> <mifos:mifoslabel name="Savings.additionalInformation" /> </span>
										<br>
										<c:forEach var="cfdef" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
											<c:forEach var="cf" items="${BusinessKey.accountCustomFields}">
												<c:if test="${cfdef.fieldId==cf.fieldId}">
												<c:choose>
													<c:when test="${cfdef.fieldType == 3}">
														<span class="fontnormal"> <mifos:mifoslabel name="${cfdef.lookUpEntity.entityType}" isColonRequired="yes"/>
														<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,cf.fieldValue)}" />
													</c:when>
													<c:otherwise>
														<span class="fontnormal"> <mifos:mifoslabel name="${cfdef.lookUpEntity.entityType}" isColonRequired="yes"/> 
														<c:out value="${cf.fieldValue}" /> </span>
													</c:otherwise>
													</c:choose>
													<br>
												</c:if>
											</c:forEach>
										</c:forEach>
										<br>
										</c:if>
									</p>
								</td>
								<td align="right" valign="top" class="fontnormal">
									<c:if test="${BusinessKey.accountState.id != AccountStates.SAVINGS_ACC_CANCEL && BusinessKey.accountState.id != AccountStates.SAVINGS_ACC_CLOSED}">
										<html-el:link styleId="savingsaccountdetail.link.editAccountInformation" 
											action="savingsAction.do?method=edit&currentFlowKey=${requestScope.currentFlowKey}">
											<mifos:mifoslabel name="Savings.EditAccountInformation" />
										</html-el:link>
									</c:if>
								</td>
							</tr>
						</table>
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
						    <tr>
								<td class="fontnormal"><br>
									 <span class="fontnormalbold"> 
									<mifos:mifoslabel
								name="reports.administrativedocuments" /> 
								<br></span>	
									<c:forEach var="adminDoc" items="${adminDoc}">
										<span class="fontnormal"> 
									  		<html-el:link styleId="loanaccountdetail.link.viewAdminReport"
												href="executeAdminDocument.ftl?adminDocumentId=${adminDoc.admindocId}&entityId=${BusinessKey.globalAccountNum}">
										 		<c:out value="${adminDoc.adminDocumentName}" />
								      		</html-el:link>
								  		</span>
										<br>
					                </c:forEach>
					                <br>
					                </td>
					            </tr>
								
						</table>
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="66%" class="headingorange">
									<mifos:mifoslabel name="Savings.moreAccountAndTransactionDetails" />
								</td>
							</tr>
							<tr>
                            	<c:url value="viewAndEditQuestionnaire.ftl" var="viewAndEditQuestionnaireMethodUrl" >
                            		<c:param name="creatorId" value="${sessionScope.UserContext.id}" />
                            		<c:param name="entityId" value="${BusinessKey.accountId}" />
                            		<c:param name="event" value="Create" />
                            		<c:param name="source" value="Savings" />
                            		<c:param name="backPageUrl" value="${backPageUrl}" />
                            	</c:url >
								<td class="fontnormal">
                            		<a id="savingsaccountdetail.link.questionGroups" href="${viewAndEditQuestionnaireMethodUrl}">
                                		<mifos:mifoslabel name="client.ViewQuestionGroupResponsesLink" bundle="ClientUIResources" />
                            		</a> <br/>
                                       <c:url value="viewAndEditQuestionnaire.ftl" var="viewQuestionGroupForClosedSavingsResponsesLinkMethodUrl" >
                                        <c:param name="creatorId" value="${sessionScope.UserContext.id}" />
                                        <c:param name="entityId" value="${BusinessKey.accountId}" />
                                        <c:param name="event" value="Close" />
                                        <c:param name="source" value="Savings" />
                                        <c:param name="backPageUrl" value="${backPageUrl}" />
                                       </c:url >
								    <c:if test="${containsQGForCloseSavings}">
                                        <a id="savingsaccountdetail.link.questionGroupsClose" href="${viewQuestionGroupForClosedSavingsResponsesLinkMethodUrl}">
                                        <mifos:mifoslabel name="Savings.ViewQuestionGroupForClosedSavingsResponsesLink" />
                                        </a> <br>
                                    </c:if>
									<html-el:link styleId="savingsaccountdetail.link.viewTransactionHistory" 
										href="viewSavingsAccountTransactionHistory.ftl?globalAccountNum=${BusinessKey.globalAccountNum}&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
 										<mifos:mifoslabel name="Savings.viewTransactionHistory" />
									</html-el:link>
									<br>
									<span class="fontnormal"> 
									<html-el:link styleId="savingsaccountdetail.link.viewChangeLog" 
											href="savingsAction.do?method=loadChangeLog&entityType=Savings&entityId=${BusinessKey.accountId}&currentFlowKey=${requestScope.currentFlowKey}">
											<mifos:mifoslabel name="Savings.viewChangeLog" />
										</html-el:link> <br>
									 <html-el:link styleId="savingsaccountdetail.link.viewStatusHistory" 
									 		href="savingsAction.do?method=getStatusHistory&globalAccountNum=${BusinessKey.globalAccountNum}&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
											<mifos:mifoslabel name="Savings.viewStatusHistory" />
										</html-el:link> </span>
								</td>
							</tr>
						</table>
					</td>
					<td width="30%" align="left" valign="top" class="paddingleft1">
						<table width="100%" border="0" cellpadding="2" cellspacing="0" class="orangetableborder">
							<tr>
								<td class="orangetablehead05">
									<span class="fontnormalbold"> <mifos:mifoslabel name="Savings.transactions" /> </span>
								</td>
							</tr>
							<tr>
								<td class="paddingL10">
									<span class="fontnormal8pt">
				<c:if test="${BusinessKey.accountState.id == AccountStates.SAVINGS_ACC_APPROVED || BusinessKey.accountState.id == AccountStates.SAVINGS_ACC_INACTIVE}">
		           <html-el:link styleId="savingsaccountdetail.link.makeDepositWithdrawal"  
		           		href="savingsDepositWithdrawalAction.do?method=load&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
		          	    <mifos:mifoslabel name="Savings.makeDepositWithdrawl"/>
		           </html-el:link><br>

					<html-el:link styleId="savingsaccountdetail.link.applyAdjustment" 
						href="savingsApplyAdjustmentAction.do?method=list&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
						<mifos:mifoslabel name="Savings.applyAdjustment" />
					</html-el:link>
					<br>

                    <html-el:link styleId="savingsaccountdetail.link.applyTransfer"
                        href="fundTransfer.ftl?accountId=${BusinessKey.accountId}&globalAccNum=${BusinessKey.globalAccountNum}">
                        <mifos:mifoslabel name="Savings.applyTransfer" />
                    </html-el:link>
                    <br />
				</c:if>
				<c:if test="${BusinessKey.accountState.id == AccountStates.SAVINGS_ACC_APPROVED || BusinessKey.accountState.id == AccountStates.SAVINGS_ACC_INACTIVE}">
											<html-el:link styleId="savingsaccountdetail.link.closeAccount" 
												href="savingsClosureAction.do?method=load&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
												<mifos:mifoslabel name="Savings.closeAccount" />
											</html-el:link>
										</c:if> </span>
								</td>
							</tr>
						</table>
						<table width="95%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									<img src="pages/framework/images/trans.gif" width="7" height="8">
								</td>
							</tr>
						</table>

						<table id="performanceHistoryTable" name="performanceHistoryTable" width="100%" border="0" cellpadding="2" cellspacing="0" class="bluetableborder">
							<tr>
								<td class="bluetablehead05">
									<span class="fontnormalbold"> <mifos:mifoslabel name="Savings.performanceHistory" /> </span>
								</td>
							</tr>
							 <tr>
                <td class="paddingL10"><img src="pages/framework/images/trans.gif" width="10" height="2"></td>
              </tr>
              <tr>
                <td class="paddingL10">
                <span class="fontnormal8pt">
               		<mifos:mifoslabel name="Savings.dateAccountOpened" isColonRequired="yes"/> <c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,BusinessKey.activationDate)}" />
					 </span></td>
			  </tr>
              <tr>
                <td class="paddingL10">
                <span class="fontnormal8pt">
                	<mifos:mifoslabel name="Savings.totalDeposits" isColonRequired="yes"/>
                	<fmt:formatNumber value="${BusinessKey.savingsPerformance.totalDeposits.amount}" />

                </span></td>
              </tr>
              <tr>
                <td class="paddingL10">
                <span class="fontnormal8pt">
                <mifos:mifoslabel name="Savings.totalInterestEarned" isColonRequired="yes"/> <fmt:formatNumber value="${BusinessKey.savingsPerformance.totalInterestEarned.amount}" /></span></td>
              </tr>
              <tr>
                <td class="paddingL10">
                <span class="fontnormal8pt">
                <mifos:mifoslabel name="Savings.totalWithdrawls" isColonRequired="yes"/> <fmt:formatNumber value="${BusinessKey.savingsPerformance.totalWithdrawals.amount}" /> </span></td>
              </tr>
               <c:if test="${BusinessKey.savingsOffering.savingsType.id == SavingsConstants.SAVINGS_MANDATORY}">
                   <tr>
	                <td class="paddingL10">
	                <span class="fontnormal8pt">
	                <mifos:mifoslabel name="Savings.missedDeposits" isColonRequired="yes"/> <fmt:formatNumber value="${BusinessKey.savingsPerformance.missedDeposits}" /> </span></td>
	  			  </tr>
	  			</c:if>
						</table>
						<table width="95%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									<img src="pages/framework/images/trans.gif" width="7" height="8">
								</td>
							</tr>
						</table>		
			
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'questionGroupInstances')}"
			   	var="questionGroupInstances" />
			
			<table width="100%" border="0" cellpadding="2" cellspacing="0" class="bluetableborder">
            <tr>
              <td colspan="2" class="bluetablehead05">
                <span class="fontnormalbold">
                  <mifos:mifoslabel name="Surveys.Surveys" bundle="SurveysUIResources"/>
                </span>
              </td>
            </tr>
            <tr>
              <td colspan="2" class="paddingL10"><img src="pages/framework/images/trans.gif" width="10" height="2"></td>
            </tr>
								<c:if test="${!empty questionGroupInstances}">
						            <c:forEach items="${questionGroupInstances}" var="questionGroupInstance">
						              <tr>
						                <td width="70%" class="paddingL10">
						                   <c:url value="viewAndEditQuestionnaire.ftl" var="viewAndEditQuestionnaireQuestionMethodUrl" >
						                    <c:param name="creatorId" value="${sessionScope.UserContext.id}" />
						                    <c:param name="entityId" value="${BusinessKey.accountId}" />
						                    <c:param name="instanceId" value="${questionGroupInstance.id}" />
						                    <c:param name="event" value="View" />
						                    <c:param name="source" value="Savings" />
						                    <c:param name="backPageUrl" value="${backPageUrl}" />
						                   </c:url >
						                  <span class="fontnormal8pt">
						                    <a id="${questionGroupInstance.id}" href="${viewAndEditQuestionnaireQuestionMethodUrl}">
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
                	<c:url value="questionnaire.ftl" var="questionnaireAttachASurveyMethodUrl">
                	 <c:param name="source" value="Savings"/>
                	 <c:param name="event" value="View"/>
                	 <c:param name="entityId" value="${BusinessKey.accountId}"/>
                	 <c:param name="creatorId" value="${sessionScope.UserContext.id}"/>
                	 <c:param name="backPageUrl" value="${backPageUrl}" />
                	</c:url>
                  <a href="${questionnaireAttachASurveyMethodUrl}">
                    <mifos:mifoslabel name="Surveys.attachasurvey" bundle="SurveysUIResources"/>
                  </a><br>
				</span>
          </table>
					<table width="95%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td><img src="pages/framework/images/trans.gif" width="7"
								height="8"></td>
						</tr>
					</table>
						<table width="100%" border="0" cellpadding="2" cellspacing="0" class="bluetableborder">
							<tr>
								<td class="bluetablehead05">
									<span class="fontnormalbold"><mifos:mifoslabel name="Savings.recentNotes" /></span>
								</td>
							</tr>
							     <tr>
                  <td class="paddingL10"><img src="pages/framework/images/trans.gif" width="10" height="2"></td>
                </tr>
              <tr>
                  <td class="paddingL10">
                  		<c:choose>
              				<c:when test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'notes')}">
								<c:forEach var="note" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'notes')}">
									<span class="fontnormal8ptbold"> <c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,note.commentDate)}"/>:</span>
									<span class="fontnormal8pt">
			                				<c:out value="${note.comment}"/>-<em>
											<c:out value="${note.personnel.displayName}"/></em><br><br>
			                	     </span>
			                	</c:forEach>
			                </c:when>
             				<c:otherwise>
	         					<span class="fontnormal">
	              	 				<mifos:mifoslabel name="accounts.NoNotesAvailable" />
	         					</span>
	     					</c:otherwise>
	 					</c:choose>
                  </td>
                </tr>
				<tr>
                	<td align="right" class="paddingleft05">
						<span class="fontnormal8pt">
							<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'notes')}">
								<html-el:link styleId="savingsaccountdetail.link.seeAllNotes"
									href="notesAction.do?method=search&accountId=${BusinessKey.accountId}&globalAccountNum=${BusinessKey.globalAccountNum}&accountTypeId=${BusinessKey.accountType.accountTypeId}&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
									<mifos:mifoslabel name="Savings.seeAllNotes" />
								</html-el:link>
							</c:if>

								<br>
							<html-el:link  styleId="savingsaccountdetail.link.addANotes" 
									href="notesAction.do?method=load&accountId=${BusinessKey.accountId}&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
									<mifos:mifoslabel name="Savings.addANote" />
							</html-el:link>

						</span>
					</td>
                </tr>
						</table>
					</td>
				</tr>
			</table>
			<html-el:hidden property="accountId" value="${BusinessKey.accountId}" />
			<html-el:hidden property="globalAccountNum" value="${BusinessKey.globalAccountNum}" />
			 <html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
