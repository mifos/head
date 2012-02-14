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
<tiles:insert definition=".homePage">
	<tiles:put name="body" type="string">
	<span id="page.id" title="Home"></span>
		<html-el:form action="/custSearchAction.do">
			
				<table width="95%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td width="70%" height="24" align="left" valign="top"
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
												
													<html-el:text styleId="home.input.search" property="searchString" maxlength="200"/>
													<c:choose>
													<c:when test='${sessionScope.UserContext.officeLevelId==5}'>
													<html-el:hidden property="officeId" value="${sessionScope.UserContext.branchId}"/> 
													</c:when>
													<c:otherwise>
													<html-el:hidden property="officeId" value="0"/> 
													</c:otherwise>
													</c:choose>												
												</td>
											</tr>
													<c:set var="recordLoanOfficerId" value="0"/>
													<c:set var="recordOfficeId" value="0"/>
												
											<tr class="fontnormal8pt">
												<td width="88%">
													<html-el:submit styleId="home.button.search" property="searchButton" styleClass="buttn">
														<mifos:mifoslabel name="CustomerSearch.search"/>
													</html-el:submit>
												</td>
											</tr>											
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
				<html-el:hidden property="officeId" value="0"/>
				<html-el:hidden property="method" value="mainSearch" />	
				<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />				
		</html-el:form>
		<!-- task-list MIFOS-5177 -->
		<c:if test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'isLoanOfficer')}">
			<html-el:form action="custSearchAction.do?method=getHomePage">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" height="24" align="left" valign="middle" class="paddingL10">
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td class="fontnormalbold">		
									<mifos:mifoslabel name="CustomerSearch.upcomingMeetings"/>:
									<html-el:select property="selectedDateOption" onchange="this.form.submit();">
										<c:forEach var="date"
											items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'nearestDates')}">
											<html-el:option value="${date}">
												${date}
											</html-el:option>
										</c:forEach>
									</html-el:select>
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
										<c:url value="centerCustAction.do" var="centerCustActionGetMethodUrl" >
											<c:param name="method" value="get" />
											<c:param name="globalCustNum" value="${center.globalCustNum}" />
										</c:url >
										<td width="99%">
											<a href="${centerCustActionGetMethodUrl}">
												<c:out value="${center.displayName}" />
											</a>
										</td>
									</tr>
									<tr>
										<td></td>
										<td>
											<ul>
												<c:forEach var="group" items="${center.groups}">
													<c:url value="groupCustAction.do" var="groupCustActionGetMethodUrl" >
														<c:param name="method" value="get" />
														<c:param name="globalCustNum" value="${group.globalCustNum}" />
													</c:url >
													<li class="fontnormal" style="margin-bottom:3px;">
														<a href="${groupCustActionGetMethodUrl}">
															<c:out value="${group.displayName}" />
														</a>
													</li>
													<ul>
														<c:forEach var="client" items="${group.clients}">
															<c:url value="clientCustAction.do" var="clientCustActionGetMethodUrl" >
																<c:param name="method" value="get" />
																<c:param name="globalCustNum" value="${client.globalCustNum}" />
															</c:url >
															<li class="fontnormal" style="margin-bottom:3px;">
																<a href="${clientCustActionGetMethodUrl}">
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
										<c:url value="groupCustAction.do" var="groupCustActionGetMethodUrl" >
											<c:param name="method" value="get" />
											<c:param name="globalCustNum" value="${group.globalCustNum}" />
										</c:url >
										<td width="99%">
											<a href="${groupCustActionGetMethodUrl}">
												<c:out value="${group.displayName}" />
											</a>
										</td>
									</tr>
									<tr>
										<td></td>
										<td>
											<ul>
												<c:forEach var="client" items="${group.clients}">
													<c:url value="clientCustAction.do" var="clientCustActionGetMethodUrl" >
														<c:param name="method" value="get" />
														<c:param name="globalCustNum" value="${client.globalCustNum}" />
													</c:url >
													<li class="fontnormal" style="margin-bottom:3px;">
														<a href="${clientCustActionGetMethodUrl}">
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
			</html-el:form>
		</c:if>
	</tiles:put>
</tiles:insert>
