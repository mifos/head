<!-- 

/**

 * home.jsp    version: 1.0

 

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

 */

-->

<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".homePage">
	<tiles:put name="body" type="string">
		<html-el:form action="/custSearchAction.do">
			
				<table width="95%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td width="70%" height="24" align="left" valign="top"
							class="paddingL10">
							<table width="96%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td class="fontnormalboldorange">
										<mifos:mifoslabel name="CustomerSearch.welcome"/>,&nbsp;
										<c:out value="${sessionScope.UserContext.name}" />
										
										<c:if test="${sessionScope.UserContext.lastLogin!=null && !empty sessionScope.UserContext.lastLogin}">
										<br>	<span class="fontnormal">
												<mifos:mifoslabel name="CustomerSearch.lastlog"/>
												<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,sessionScope.UserContext.lastLogin)}" /> 
											</span>
										</c:if>
									</td>
								</tr>
								<font class="fontnormalRedBold"><html-el:errors bundle="CustomerSearchUIResources"/> </font>
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
								<c:set var="isCenterHeirarchyExists"
								value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'isCenterHeirarchyExists')}" />
									<td class="fontnormalbold">
										<mifos:mifoslabel name="CustomerSearch.quicklyfind"/>
										<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"/>,
										<c:choose>
						                 	 <c:when test="${isCenterHeirarchyExists=='true'}">
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
													<mifos:mifoslabel name="CustomerSearch.searchby"/>:
												</td>
											</tr>
											<tr class="fontnormal">
												<td>
												
													<html-el:text property="searchString" maxlength="200"/>
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
													<html-el:submit property="searchButton" styleClass="buttn" style="width:70px;">
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
	</tiles:put>
</tiles:insert>
