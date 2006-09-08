<!--

 * savingsaccountdetail.jsp  version: 1.0



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
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<html-el:form method="post" action="/savingsAction.do">

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"> <customtags:headerLink selfLink="false" /> </span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="62%" class="headingorange">
									<c:out value="${sessionScope.BusinessKey.savingsOffering.prdOfferingName}" />
									#
									<c:out value="${sessionScope.BusinessKey.globalAccountNum}" />
									<br>
								</td>
								<td width="38%" rowspan="2" align="right" valign="top" class="fontnormal">
									<c:if test="${sessionScope.BusinessKey.accountState.id != AccountStates.SAVINGS_ACC_CANCEL && sessionScope.BusinessKey.accountState.id != AccountStates.SAVINGS_ACC_CLOSED}">
										<html-el:link href="editStatusAction.do?method=load&accountId=${sessionScope.BusinessKey.accountId}">
											<mifos:mifoslabel name="Savings.Edit" />
											<mifos:mifoslabel name="Savings.account" />
											<mifos:mifoslabel name="Savings.status" />
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
									<%--<span class="fontnormal"><img src="pages/framework/images/status_activegreen.gif" width="8" height="9"> Active</span>--%>
									<span class="fontnormal"> <mifoscustom:MifosImage id="${sessionScope.BusinessKey.accountState.id}" moduleName="accounts.savings" /> <c:out value="${sessionScope.BusinessKey.accountState.name}" />&nbsp; <c:forEach var="flagSet"
											items="${sessionScope.BusinessKey.accountFlags}">
											<span class="fontnormal"> <c:if test="${flagSet.flag.id == 6}">
													<mifos:MifosImage id="blackflag" moduleName="accounts.savings" />
												</c:if><c:out value="${flagSet.flag.name}" /> </span>
										</c:forEach> </span>
								</td>
							</tr>
							<tr>
								<td class="fontnormal">
									<mifos:mifoslabel name="Savings.accountBalance" />
									:
									<c:out value="${sessionScope.BusinessKey.savingsBalance}" />
									<br>
								</td>
								<td align="right" valign="top" class="fontnormal">
									<c:if
										test="${sessionScope.BusinessKey.savingsOffering.savingsType.id == SavingsConstants.SAVINGS_MANDATORY
												&& (sessionScope.BusinessKey.accountState.id == AccountStates.SAVINGS_ACC_APPROVED ||
												sessionScope.BusinessKey.accountState.id == AccountStates.SAVINGS_ACC_INACTIVE)}">
										<html-el:link href="savingsAction.do?method=getDepositDueDetails&globalAccountNum=${sessionScope.BusinessKey.globalAccountNum}">
											<mifos:mifoslabel name="Savings.viewDepositDueDetails" />
										</html-el:link>
									</c:if>
								</td>
							</tr>
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
									<html-el:link href="savingsAction.do?method=getRecentActivity&globalAccountNum=${sessionScope.BusinessKey.globalAccountNum}">
										<mifos:mifoslabel name="Savings.viewAllAccountActivity" />
									</html-el:link>
								</td>
							</tr>
						</table>
						<mifoscustom:mifostabletag source="recentActivityForDetailPage" scope="session" xmlFileName="SavingsAccountRecentActivity.xml" moduleName="accounts\\savings" passLocale="true" />
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
												<c:when test="${sessionScope.BusinessKey.savingsOffering.savingsType.id == SavingsConstants.SAVINGS_MANDATORY}">
													<mifos:mifoslabel name="Savings.mandatoryAmountForDeposit" />:
	                  </c:when>
												<c:otherwise>
													<mifos:mifoslabel name="Savings.recommendedAmountForDeposit" />:
	                  </c:otherwise>
											</c:choose> <c:out value="${sessionScope.BusinessKey.recommendedAmount.amountDoubleValue}" /> <c:choose>
												<c:when test="${sessionScope.BusinessKey.customer.customerLevel.id==CustomerConstants.GROUP_LEVEL_ID}">
	                    (<customtags:lookUpValue id="${sessionScope.BusinessKey.recommendedAmntUnit.id}" searchResultName="RecommendedAmtUnit" mapToSeperateMasterTable="true">
													</customtags:lookUpValue>)
	                    </c:when>
												<c:otherwise>
													<customtags:lookUpValue id="${sessionScope.BusinessKey.recommendedAmntUnit.id}" searchResultName="RecommendedAmtUnit" mapToSeperateMasterTable="true">
													</customtags:lookUpValue>
												</c:otherwise>
											</c:choose> <br> <br> <mifos:mifoslabel name="Savings.typeOfDeposits" />: <customtags:lookUpValue id="${sessionScope.BusinessKey.savingsOffering.savingsType.id}" searchResultName="SavingsType" mapToSeperateMasterTable="true">
											</customtags:lookUpValue> </span>
										<br>
										<mifos:mifoslabel name="Savings.maxAmountPerWithdrawl" />:
										<c:out value="${sessionScope.BusinessKey.savingsOffering.maxAmntWithdrawl}" />
										<br>
										<span class="fontnormal"> <mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" /> <mifos:mifoslabel name="Savings.rate" />: <c:out value="${sessionScope.BusinessKey.savingsOffering.interestRate}" /> <mifos:mifoslabel name="Savings.perc" />
										</span>
										<br>
										<br>
										<span class="fontnormalbold"> <mifos:mifoslabel name="Savings.additionalInformation" /> </span>
										<br>
										<c:forEach var="cfdef" items="${sessionScope.customFields}">
											<c:forEach var="cf" items="${sessionScope.BusinessKey.accountCustomFields}">
												<c:if test="${cfdef.fieldId==cf.fieldId}">
													<span class="fontnormal"> <mifos:mifoslabel name="${cfdef.lookUpEntity.entityType}"></mifos:mifoslabel>: <c:out value="${cf.fieldValue}" /> </span>
													<br>
												</c:if>
											</c:forEach>
										</c:forEach>
										<br>
									</p>
								</td>
								<td align="right" valign="top" class="fontnormal">
									<c:if test="${sessionScope.BusinessKey.accountState.id != AccountStates.SAVINGS_ACC_CANCEL && sessionScope.BusinessKey.accountState.id != AccountStates.SAVINGS_ACC_CLOSED}">
										<html-el:link action="savingsAction.do?method=edit">
											<mifos:mifoslabel name="Savings.EditAccountInformation" />
										</html-el:link>
									</c:if>
								</td>
							</tr>
						</table>
						<br>
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="66%" class="headingorange">
									<mifos:mifoslabel name="Savings.moreAccountAndTransactionDetails" />
								</td>
							</tr>
							<tr>
								<td class="fontnormal">
									<html-el:link href="savingsAction.do?method=getTransactionHistory&globalAccountNum=${sessionScope.BusinessKey.globalAccountNum}">
										<mifos:mifoslabel name="Savings.viewTransactionHistory" />
									</html-el:link>
									<br>
									<span class="fontnormal"> <html-el:link href="#">
											<mifos:mifoslabel name="Savings.viewChangeLog" />
										</html-el:link> <br>
									 <html-el:link href="savingsAction.do?method=getStatusHistory&globalAccountNum=${sessionScope.BusinessKey.globalAccountNum}">
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
				<c:if test="${sessionScope.BusinessKey.accountState.id == AccountStates.SAVINGS_ACC_APPROVED || sessionScope.BusinessKey.accountState.id == AccountStates.SAVINGS_ACC_INACTIVE}">
		           <html-el:link  href="savingsDepositWithdrawalAction.do?method=load">
		          	    <mifos:mifoslabel name="Savings.makeDepositWithdrawl"/>
		           </html-el:link><br>

					<html-el:link href="savingsApplyAdjustmentAction.do?method=load">
						<mifos:mifoslabel name="Savings.applyAdjustment" />
					</html-el:link>
					<br>
				</c:if>
				<c:if test="${sessionScope.BusinessKey.accountState.id == AccountStates.SAVINGS_ACC_APPROVED || sessionScope.BusinessKey.accountState.id == AccountStates.SAVINGS_ACC_INACTIVE}">
											<html-el:link href="savingsClosureAction.do?method=load">
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

						<table width="100%" border="0" cellpadding="2" cellspacing="0" class="bluetableborder">
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
               		<mifos:mifoslabel name="Savings.dateAccountOpened"/>: <c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,sessionScope.BusinessKey.activationDate)}" />
					 </span></td>
			  </tr>
              <tr>
                <td class="paddingL10">
                <span class="fontnormal8pt">
                	<mifos:mifoslabel name="Savings.totalDeposits"/>:
                	<c:out value="${sessionScope.BusinessKey.savingsPerformance.totalDeposits}" />
                	
                </span></td>
              </tr>
              <tr>
                <td class="paddingL10">
                <span class="fontnormal8pt">
                <mifos:mifoslabel name="Savings.totalInterestEarned"/>: <c:out value="${sessionScope.BusinessKey.savingsPerformance.totalInterestEarned}" /></span></td>
              </tr>
              <tr>
                <td class="paddingL10">
                <span class="fontnormal8pt">
                <mifos:mifoslabel name="Savings.totalWithdrawls"/>: <c:out value="${sessionScope.BusinessKey.savingsPerformance.totalWithdrawals}" /> </span></td>
              </tr>  
               <c:if test="${sessionScope.BusinessKey.savingsOffering.savingsType.id == SavingsConstants.SAVINGS_MANDATORY}">
                   <tr>
	                <td class="paddingL10">
	                <span class="fontnormal8pt">
	                <mifos:mifoslabel name="Savings.missedDeposits"/>: <c:out value="${sessionScope.BusinessKey.savingsPerformance.missedDeposits}" /> </span></td>
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
              				<c:when test="${!empty sessionScope.notes}">
								<c:forEach var="note" items="${sessionScope.notes}">
									<span class="fontnormal8ptbold"> <c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,note.commentDate)}"/>:</span>
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
							<c:if test="${!empty sessionScope.notes}">
								<html-el:link href="notesAction.do?method=search&accountId=${sessionScope.BusinessKey.accountId}&globalAccountNum=${sessionScope.BusinessKey.globalAccountNum}&prdOfferingName=${sessionScope.BusinessKey.savingsOffering.prdOfferingName}&securityParamInput=Savings&accountTypeId=${sessionScope.BusinessKey.accountType.accountTypeId}">
									<mifos:mifoslabel name="Savings.seeAllNotes" />
								</html-el:link>
							</c:if>
							
								<br>
							<html-el:link href="notesAction.do?method=load&accountId=${sessionScope.BusinessKey.accountId}">
									<mifos:mifoslabel name="Savings.addANote" />
							</html-el:link>
							
						</span>
					</td>
                </tr>
						</table>
					</td>
				</tr>
			</table>
			<mifos:SecurityParam property="Savings" />
			<html-el:hidden property="accountId" value="${sessionScope.BusinessKey.accountId}" />
			<html-el:hidden property="globalAccountNum" value="${sessionScope.BusinessKey.globalAccountNum}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
