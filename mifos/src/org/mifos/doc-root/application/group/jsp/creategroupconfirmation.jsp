<!-- /**
 
 * creategroupconfirmation.jsp    version: 1.0
 
 
 
 * Copyright © 2005-2006 Grameen Foundation USA
 
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

<tiles:insert definition=".clientsacclayoutsearchmenu">
 <tiles:put name="body" type="string">
<html-el:form action="GroupAction.do?method=get">

    
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td align="left" valign="top" class="paddingL15T15" >
          <table width="98%" border="0" cellspacing="0" cellpadding="3">
              <tr>
                <td valign="top"><span class="headingorange">
                <mifos:mifoslabel name="Group.createsuccess" bundle="GroupUIResources"> </mifos:mifoslabel>
				<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" ></mifos:mifoslabel>                
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
                
                <c:out value="${requestScope.GroupVO.displayName}"/>
				<mifos:mifoslabel name="Group.createconfirmhead1" bundle="GroupUIResources"></mifos:mifoslabel>
                    </span> <c:out value="${requestScope.GroupVO.globalCustNum}"/>
                    <span class="fontnormal"><br>
      <mifos:mifoslabel name="Group.createconfirmhead2" bundle="GroupUIResources"></mifos:mifoslabel>
	  <mifos:mifoslabel name="${ConfigurationConstants.GROUP}" ></mifos:mifoslabel>                 
	  <mifos:mifoslabel name="Group.details" ></mifos:mifoslabel>                
      <mifos:mifoslabel name="Group.createconfirmhead3" bundle="GroupUIResources"></mifos:mifoslabel>
      <mifos:mifoslabel name="${ConfigurationConstants.GROUP}" ></mifos:mifoslabel><mifos:mifoslabel name="Group.createconfirmhead4" bundle="GroupUIResources"></mifos:mifoslabel>
      
      <br>
                            <br>
                            </span>
                     <c:choose>
                        <c:when test="${!empty requestScope.GroupVO.personnel and !empty requestScope.GroupVO.personnel.personnelId}">
                            <a href="GroupAction.do?method=get&globalCustNum=${requestScope.GroupVO.globalCustNum}&recordOfficeId=${requestScope.GroupVO.office.officeId}&recordLoanOfficerId=${requestScope.GroupVO.personnel.personnelId}">
                            <mifos:mifoslabel name="Group.view" bundle="GroupUIResources"></mifos:mifoslabel>
						    <mifos:mifoslabel name="${ConfigurationConstants.GROUP}" ></mifos:mifoslabel>
                            <mifos:mifoslabel name="Group.details" bundle="GroupUIResources"></mifos:mifoslabel>
                            <mifos:mifoslabel name="Group.now" bundle="GroupUIResources"></mifos:mifoslabel>
                            
                            </a>
                       </c:when>
                       <c:otherwise>
                       		<a href="GroupAction.do?method=get&globalCustNum=${requestScope.GroupVO.globalCustNum}&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}">
                            <mifos:mifoslabel name="Group.view" bundle="GroupUIResources"></mifos:mifoslabel>
						    <mifos:mifoslabel name="${ConfigurationConstants.GROUP}" ></mifos:mifoslabel>
                            <mifos:mifoslabel name="Group.details" bundle="GroupUIResources"></mifos:mifoslabel>
                            <mifos:mifoslabel name="Group.now" bundle="GroupUIResources"></mifos:mifoslabel>

                            </a>
                       </c:otherwise>
                    </c:choose>
                            <span class="fontnormal"><br>
                            <br>
                            </span><span class="fontnormalboldorange">
                            <mifos:mifoslabel name="Group.suggestednextsteps" bundle="GroupUIResources"></mifos:mifoslabel>
                            
                            </span>
                            <br>
                            <!--  linkto create account will be shown only if group is in active state -->
                      <c:if test="${requestScope.GroupVO.statusId == 9}">
                            	<mifos:mifoslabel name="Group.createaccount" bundle="GroupUIResources"></mifos:mifoslabel>
                            	<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" ></mifos:mifoslabel>
                         <span class="fontnormal"><br>
                            <html-el:link href="savingsAction.do?method=getPrdOfferings&customerId=${requestScope.GroupVO.customerId}&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}">
                            	<mifos:mifoslabel name="Group.createa" bundle="GroupUIResources"></mifos:mifoslabel>
                            	<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" bundle="GroupUIResources"></mifos:mifoslabel>
                            	<mifos:mifoslabel name="Group.account" bundle="GroupUIResources"></mifos:mifoslabel>
                             </html-el:link><br>
                             <c:if test="${sessionScope.isGroupLoanAllowed == true}">
                            <html-el:link href="loanAction.do?method=getPrdOfferings&customer.customerId=${requestScope.GroupVO.customerId}&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}">
                            	<mifos:mifoslabel name="Group.createa" bundle="GroupUIResources"></mifos:mifoslabel>
                            	<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" bundle="GroupUIResources"></mifos:mifoslabel>
                            	<mifos:mifoslabel name="Group.account" bundle="GroupUIResources"></mifos:mifoslabel>
                            </html-el:link><br>
                            </c:if>
                            <br>
                         </span>
                      </c:if>
                      <span class="fontnormal">
                          <a href="GroupAction.do?method=hierarchyCheck&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}">
                            	<mifos:mifoslabel name="Group.createa" bundle="GroupUIResources"> </mifos:mifoslabel>
                            	<mifos:mifoslabel name="Group.new" bundle="GroupUIResources"></mifos:mifoslabel>
                            	<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" bundle="GroupUIResources"></mifos:mifoslabel>
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

