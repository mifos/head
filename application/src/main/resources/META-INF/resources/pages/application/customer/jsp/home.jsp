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

<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<script>
function disableSubmitButtonAndSubmit(button_id, form_id) {
    document.getElementById(button_id).disabled = true;
    document.getElementById(form_id).submit();
}
</script>
<tiles:insert definition=".homePage">
	<tiles:put name="body" type="string">
	<span id="page.id" title="Home"></span>
		<form id="search.form" action="searchResult.ftl">
			
				<table width="95%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td width="40%" height="24" align="left" valign="top"
							class="paddingL10">
							<table width="96%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td class="fontnormalboldorange">
										<span id="home.text.welcome"><mifos:mifoslabel name="CustomerSearch.welcome"/>,&nbsp;
										<c:out value="${sessionScope.UserContext.name}" /></span>
										<c:if test="${sessionScope.UserContext.lastLogin!=null && !empty sessionScope.UserContext.lastLogin}">
										<br>	<span class="fontnormal" id="home.text.lastLogin">
												<mifos:mifoslabel name="CustomerSearch.lastlog"/>
												<c:out value="${userdatefn:getUserLocaleDateObject(sessionScope.UserContext.preferredLocale,sessionScope.UserContext.lastLogin)}" /> 
											</span>
										</c:if>
									</td>
								</tr>
								<font class="fontnormalRedBold"><span id="home.error.message"><html-el:errors bundle="CustomerSearchUIResources"/></span> </font>
								<tr>
									<td class="fontnormalbold">&nbsp;</td>
								</tr>
								<tr>
									<td class="fontnormalbold">
										<mifos:mifoslabel name="CustomerSearch.navigate"/>:
									</td>
								</tr>
								<tr>
									<td class="fontnormal">
									<table width="80%" border="0" cellspacing="0" cellpadding="3">
										<tr class="fontnormal">
											<td><img src="pages/framework/images/smallarrowleft.gif" width="11" height="11">
												<mifos:mifoslabel name="CustomerSearch.linksleft"/>
											</td>
											<td><img src="pages/framework/images/smallarrowdown.gif" width="11" height="11">
												<mifos:mifoslabel name="CustomerSearch.searchbelow"/>
											</td>
											<td><img src="pages/framework/images/smallarrowtop.gif" width="11" height="11">
												<mifos:mifoslabel name="CustomerSearch.tabsattop"/>
											</td>
										</tr>
									</table>
									</td>
								</tr>
								<tr>
									<td class="fontnormal">&nbsp;</td>
								</tr>
								<tr>
								<c:set var="isCenterHierarchyExists"
								value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'isCenterHierarchyExists')}" />
									<td class="fontnormalbold">
										<mifos:mifoslabel name="CustomerSearch.quicklyfind"/>
										<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"/>,
										<c:choose>
						                 	 <c:when test="${isCenterHierarchyExists=='true'}">
						                 	 	<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />,&nbsp;
						                  		<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />,
						                  	</c:when>
						                  	<c:otherwise>
	                  							<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />,
	                  						</c:otherwise>
	                  					</c:choose>
										<mifos:mifoslabel name="CustomerSearch.or"/>
										<mifos:mifoslabel name="CustomerSearch.youCan"/>...
									</td>
								</tr>
								<tr>
									<td class="fontnormal">
										<table width="100%" border="0" cellspacing="0" cellpadding="2">
											<tr class="fontnormal">
												<td>
													<span id="home.label.search"><mifos:mifoslabel name="CustomerSearch.searchby"/></span>:
												</td>
											</tr>
											<tr class="fontnormal">
												<td>    
												
													<input type="text" id="home.input.search" name="searchString" maxlength="200"/>
													<c:choose>
													<c:when test='${sessionScope.UserContext.officeLevelId==5}'>
													<html-el:hidden property="officeId" value="${sessionScope.UserContext.branchId}"/> 
													</c:when>
													<c:otherwise>
													<html-el:hidden property="officeId" value="0"/> 
													</c:otherwise>
													</c:choose>												
                                                    <html-el:submit styleId="home.button.search" property="searchButton" styleClass="buttn" onclick="disableSubmitButtonAndSubmit('home.button.search', 'search.form')">
                                                        <mifos:mifoslabel name="CustomerSearch.search"/>
                                                    </html-el:submit>
												</td>
											</tr>
                                            <tiles:insert definition=".searchFilters" flush="false">
                                            </tiles:insert>
													<c:set var="recordLoanOfficerId" value="0"/>
													<c:set var="recordOfficeId" value="0"/>										
										</table>
									</td>
								</tr>
							</table>
						</td>
      
                        <!-- MIFOS- 5756 Dashboard with statistics -->
                        <td width="40%" align="right" height="25" valign="top" class="paddingL10"> 
                        <c:set var="dashboard" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'dashboard')}" />
                        <table width="60%" border="0" cellpadding="2" cellspacing="1" 
                        class="bluetableborder">
                        <tr>
                            <td class="bluetablehead05"><span class="fontnormalbold">
                                <mifos:mifoslabel name="CustomerSearch.dashboardTitle"/>:</span>
                            </td>
                        </tr>
                        <tr>
                            <td class="paddingL10"><span class="fontnormal">
                            <c:url value="viewTotalBorrowersDBDetails.ftl" var="viewTotalBorrowersDBDetailsUrl" />
                            <a href="${viewTotalBorrowersDBDetailsUrl}">
                            <mifos:mifoslabel name="CustomerSearch.dashboardBorrowers"/>:</a>
                             <c:out
                                value="${dashboard.borrowersCount}" />
                            </span></td>
                        </tr>
                         <tr>
                            <td class="paddingL10"><span class="fontnormal">
                            <c:url value="viewTotalBorrowersGroupDBDetails.ftl" var="viewTotalBorrowersGroupDBDetailsUrl" />
                            <a href="${viewTotalBorrowersGroupDBDetailsUrl}">
                            <mifos:mifoslabel name="CustomerSearch.dashboardBorrowersGroup"/>:</a>
                             <c:out
                                value="${dashboard.borrowersGroupCount}" />
                            </span></td>
                         </tr>
                         <tr>
                            <td class="paddingL10"><span class="fontnormal">
                            <c:url value="viewActiveCentersDBDetails.ftl" var="viewActiveCentersDBDetailsUrl" />
                            <a href="${viewActiveCentersDBDetailsUrl}">
                            <mifos:mifoslabel name="CustomerSearch.dashboardActiveCenters"/>:</a>
                             <c:out 
                             value="${dashboard.activeCentersCount}" />
                            </span></td>
                         </tr>
                         <tr>
                            <td class="paddingL10"><span class="fontnormal">
                            <c:url value="viewActiveGroupsDBDetails.ftl" var="viewActiveGroupsDBDetailsUrl" />
                            <a href="${viewActiveGroupsDBDetailsUrl}">
                            <mifos:mifoslabel name="CustomerSearch.dashboardActiveGroups"/>:</a>
                             <c:out
                                value="${dashboard.activeGroupsCount}" />
                            </span></td>
                         </tr>
                         <tr>
                            <td class="paddingL10"><span class="fontnormal">
                            <c:url value="viewActiveClientsDBDetails.ftl" var="viewActiveClientsDBDetailsUrl" />
                            <a href="${viewActiveClientsDBDetailsUrl}">
                            <mifos:mifoslabel name="CustomerSearch.dashboardActiveClients"/>:</a>
                             <c:out
                                value="${dashboard.activeClientsCount}" />
                            </span></td>
                         </tr>
                         <tr>
                            <td class="paddingL10"><span class="fontnormal">
                            <c:url value="viewWaitingForApprovalLoansDBDetails.ftl" var="viewWaitingForApprovalLoansDBDetailsUrl" />
                            <a href="${viewWaitingForApprovalLoansDBDetailsUrl}">
                            <mifos:mifoslabel name="CustomerSearch.dashboardLoansWaitingForApproval"/>:</a>
                            <c:out value="${dashboard.waitingForApprovalLoansCount}"/>
                            </span></td>
                         </tr>
                         <tr>
                            <td class="paddingL10"><span class="fontnormal">
                            <c:url value="viewBadStandingLoansDBDetails.ftl" var="viewBadStandingLoansDBDetailsUrl" />
                            <a href="${viewBadStandingLoansDBDetailsUrl}">
                            <mifos:mifoslabel name="CustomerSearch.dashboardLoansInArrears"/>:</a>
                             <c:out
                                value="${dashboard.loansInArrearsCount}" />
                            </span></td>
                         </tr>
                         <tr>
                            <td class="paddingL10"><span class="fontnormal">
                            <c:url value="viewLoansToBePaidCurrWeekDBDetails.ftl" var="viewLoansToBePaidCurrWeekDBDetailsUrl" />
                            <a href="${viewLoansToBePaidCurrWeekDBDetailsUrl}" ><mifos:mifoslabel name="CustomerSearch.dashboardCurrWeekPaidLoans"/>:</a>
                             <c:out
                                value="${dashboard.loansToBePaidCurrentWeek}" />
                                </span></td>
                        </tr>

                    </table>
                        </td>
					</tr>
				</table>
				<html-el:hidden property="officeId" value="0"/>
				<html-el:hidden property="method" value="mainSearch" />	
				<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />				
		</form>
		<!-- task-list MIFOS-5177 -->
		<c:if test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'isLoanOfficer')}">
			<form action="home.ftl">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" height="24" align="left" valign="middle" class="paddingL10">
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td class="fontnormalbold">		
									<mifos:mifoslabel name="CustomerSearch.upcomingMeetings"/>:
									<select name="selectedDateOption" onchange="this.form.submit();">
										<c:forEach var="date"
											items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'nearestDates')}">
											<c:choose> 
												<c:when test="${date == custSearchActionForm.selectedDateOption}">
													<option value="${date}" selected="selected">
														${date}
													</option>
												</c:when>
												<c:otherwise>
													<option value="${date}">
														${date}
													</option>												
												</c:otherwise>
											</c:choose>	
										</c:forEach>
									</select>
								</td>
							<tr>
						</table>
					</td>
				</tr>
				<tr>
					<c:set var="hierarchy" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'hierarchy')}" />
					<c:choose>
	             		<c:when test="${isCenterHierarchyExists=='true'}">
			             	<td width="70%" height="24" align="left" valign="top" class="paddingL10">	 
			             	 	<c:forEach var="center" items="${hierarchy.centers}">
								<table width="90%" border="0" cellspacing="3" cellpadding="0" class="paddingL10">
									<tr class="fontnormal">
										<td width="1%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
										<td width="99%">
											<a href="viewCenterDetails.ftl?globalCustNum=${center.globalCustNum}">
												<c:out value="${center.displayName}" />
											</a>
										</td>
									</tr>
									<tr>
										<td></td>
										<td>
											<ul>
												<c:forEach var="group" items="${center.groups}">
													<li class="fontnormal" style="margin-bottom:3px;">
														<a href="viewGroupDetails.ftl?globalCustNum=${group.globalCustNum}">
															<c:out value="${group.displayName}" />
														</a>
													</li>
													<ul>
														<c:forEach var="client" items="${group.clients}">
															<li class="fontnormal" style="margin-bottom:3px;">
																<a href="viewClientDetails.ftl?globalCustNum=${client.globalCustNum}">
																	<c:out value="${client.displayName}" />
																</a>
															</li>
														</c:forEach>	
													</ul>
												</c:forEach>
											</ul>	
										<td>
									</tr>
								</table>
			              		</c:forEach>
		              		</td>
              		 	</c:when>
	              		<c:otherwise>
	              			<td width="70%" height="24" align="left" valign="top" class="paddingL10">
							<c:forEach var="group" items="${hierarchy.groups}">
								<table width="90%" border="0" cellspacing="3" cellpadding="0" class="paddingL10">
									<tr class="fontnormal">
										<td width="1%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
										<td width="99%">
											<a href="viewGroupDetails.ftl?globalCustNum=${group.globalCustNum}">
												<c:out value="${group.displayName}" />
											</a>
										</td>
									</tr>
									<tr>
										<td></td>
										<td>
											<ul>
												<c:forEach var="client" items="${group.clients}">
													<li class="fontnormal" style="margin-bottom:3px;">
														<a href="viewClientDetails.ftl?globalCustNum=${client.globalCustNum}">
															<c:out value="${client.displayName}" />
														</a>
													</li>
												</c:forEach>
											</ul>	
										<td>
									</tr>
								</table>
		              		</c:forEach>
		              		</td>
			   			</c:otherwise>
					</c:choose>
				</tr>
			</table>
			</form>
		</c:if>
	</tiles:put>
</tiles:insert>
