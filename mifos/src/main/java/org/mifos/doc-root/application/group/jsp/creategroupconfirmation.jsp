<!-- /**
 
 * creategroupconfirmation.jsp    version: 1.0
 
 
 
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
 
 */-->
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
<tiles:put name="body" type="string">
<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.GroupUIResources"/>
<html-el:form action="groupCustAction.do?method=get">

    
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td align="left" valign="top" class="paddingL15T15" >
          <table width="98%" border="0" cellspacing="0" cellpadding="3">
              <tr>
                <td valign="top"><span class="headingorange">
				<fmt:message key="Group.createsuccess">
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
						</fmt:message>                
                <br><br>
                <font class="fontnormalRedBold">
   					<html-el:errors bundle="PersonnelUIResources"/>
   				</font>
                  
                </span></td>
              </tr>
              <tr>
                <td class="fontnormalbold"> 
	                <mifos:mifoslabel name="Group.pleasenote" bundle="GroupUIResources"></mifos:mifoslabel>
                <span class="fontnormal"> 
                
                <c:out value="${sessionScope.groupCustActionForm.displayName}"/>
				<mifos:mifoslabel name="Group.createconfirmhead1" bundle="GroupUIResources"></mifos:mifoslabel>
                    </span> <c:out value="${sessionScope.groupCustActionForm.globalCustNum}"/>
                    <span class="fontnormal"><br>
      			<fmt:message key="Group.createconfirm">
				<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
				</fmt:message>
      
      <br>
                            <br>
                            </span>
                    <c:choose>
                        <c:when test="${!empty sessionScope.groupCustActionForm.loanOfficerId}">
                        	<c:set var = "userId" value = "${sessionScope.groupCustActionForm.loanOfficerId}"/>
		                   	<c:set var = "branchId" value = "${sessionScope.groupCustActionForm.officeId}"/>
                        </c:when>
                        <c:otherwise>
			               	<c:set var = "userId" value = "${UserContext.id}"/>
			               	<c:set var = "branchId" value = "${UserContext.branchId}"/>			               	
                        </c:otherwise>
                     </c:choose>
                   
	           		<a href="groupCustAction.do?method=get&globalCustNum=${sessionScope.groupCustActionForm.globalCustNum}&recordOfficeId=${branchId}&recordLoanOfficerId=${userId}&randomNUm=${sessionScope.randomNUm}">
	                    <fmt:message key="Group.viewGroupDetail">
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
						</fmt:message>
                     </a>
                     
                            <span class="fontnormal"><br>
                            <br>
                            </span><span class="fontnormalboldorange">
                            <mifos:mifoslabel name="Group.suggestednextsteps" bundle="GroupUIResources"></mifos:mifoslabel>
                            
                            </span>
                            <br>
                            <!--  linkto create account will be shown only if group is in active state -->
                      <c:if test="${sessionScope.groupCustActionForm.status == CustomerStatus.GROUP_ACTIVE.value}">
                            	<fmt:message key="Group.createaccount">
								<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
								</fmt:message>
                         <span class="fontnormal"><br>
                            <html-el:link href="savingsAction.do?method=getPrdOfferings&customerId=${sessionScope.groupCustActionForm.customerId}&recordOfficeId=${branchId}&recordLoanOfficerId=${userId}&randomNUm=${sessionScope.randomNUm}">
                            	
                            	<fmt:message key="Group.createAnAccount">
								<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" /></fmt:param>
								</fmt:message>
                             </html-el:link><br>                             
                             <c:if test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'isGroupLoanAllowed') == true}">
	                            <html-el:link href="loanAction.do?method=getPrdOfferings&customer.customerId=${sessionScope.groupCustActionForm.customerId}&recordOfficeId=${branchId}&recordLoanOfficerId=${userId}&randomNUm=${sessionScope.randomNUm}">
	                            	
	                            	<fmt:message key="Group.createAnAccount">
								<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /></fmt:param>
								</fmt:message>
	                            </html-el:link><br>
                            </c:if>
                            <br>
                         </span>
                      </c:if>
                      <span class="fontnormal">
                          <a href="groupCustAction.do?method=hierarchyCheck&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
                     
                            	<fmt:message key="Group.createNewAccount">
								<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
								</fmt:message>
                          </a>
                      </span>
                      
                          </td>
              </tr>
            </table>
            <br>
            <br>
          </td>
        </tr>
      </table>
      <br>

</html-el:form>
</tiles:put>
</tiles:insert>

