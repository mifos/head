<%--
Copyright (c) 2005-2009 Grameen Foundation USA
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="/customer/customerfunctions" prefix="customerfn"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>


<tiles:insert definition=".withoutmenu">
 <tiles:put name="body" type="string">
 <span style="display: none" id="page.id">PreviewGroup</span>
 <SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
<script language="javascript">
	 function setGroupStatus(statusValue){	
		disableButtons();
	 	document.getElementsByName("status")[0].value=statusValue;
	 	groupCustActionForm.submit();
	 }
	function disableButtons(){
		func_disableSubmitBtn("submitBtn1");
		func_disableSubmitBtn("submitBtn2");
	}  
  function GoToEditPage(){
	groupCustActionForm.action="groupCustAction.do?method=previous";
	groupCustActionForm.submit();
  }
  function goToCancelPage(){
	groupCustActionForm.action="groupCustAction.do?method=cancel";
	groupCustActionForm.submit();
  }
</script>
<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.GroupUIResources"/>
<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'CenterHierarchyExist')}" var="CenterHierarchyExist" />
<html-el:form action="groupCustAction.do?method=create" > 
 <html-el:hidden property="status" value="" /> 
 <html-el:hidden property="input" value="createGroup"/> 
 <html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td height="350" align="left" valign="top" bgcolor="#FFFFFF"> 
    <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <td align="center" class="heading">&nbsp;</td>
        </tr>
    </table>              
      <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bluetablehead"><table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td width="33%"><table border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td><img src="pages/framework/images/timeline/tick.gif" width="17" height="17"></td>
                    <td class="timelineboldgray">
                    <c:choose>
						<c:when test="${CenterHierarchyExist == true}">
							<fmt:message key="Group.select">
							<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /></fmt:param>
						    </fmt:message>
						</c:when>
						<c:otherwise>
		                    <fmt:message key="Group.choosebranch">
							<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}" /></fmt:param>
							</fmt:message>
	                   </c:otherwise>
	                 </c:choose>
                    </td>
                  </tr>
                </table>
              </td>
              <td width="34%" align="center">
                <table border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td><img src="pages/framework/images/timeline/tick.gif" width="17" height="17"></td>
                    <td class="timelineboldgray">
                    	<fmt:message key="Group.groupinformation">
				<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
				</fmt:message>
                    </td>
                  </tr>
                </table>
              </td>
              <td width="33%" align="right">
                <table border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td><img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17"></td>
                    <td class="timelineboldorange">
                    	<mifos:mifoslabel name="Group.reviewandsubmit" bundle="GroupUIResources"></mifos:mifoslabel>
                    </td>
                  </tr>
                </table>
              </td>
              </tr>
          </table></td>
        </tr>
      </table>
      <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
             
          <tr>
            <td align="left" valign="top" class="paddingleftCreates"> 
                         <table width="93%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td class="headingorange"><span class="heading">
                  	<fmt:message key="Group.createNew">
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
					</fmt:message>
                  - </span>
                 	<mifos:mifoslabel name="Group.reviewandsubmit" bundle="GroupUIResources"></mifos:mifoslabel>
                 </td>
                </tr>
                <tr>
                  <td class="fontnormal"> 
                  <mifos:mifoslabel name="Group.previewcreatepagehead1" bundle="GroupUIResources"></mifos:mifoslabel>
                  <mifos:mifoslabel name="Group.previewcreatepagehead2" bundle="GroupUIResources"></mifos:mifoslabel>
                  <mifos:mifoslabel name="Group.createpagehead3" bundle="GroupUIResources"></mifos:mifoslabel>
                  </td>
                </tr>
                <tr>
   					<td>
   					<font class="fontnormalRedBold"><span id="previewgroup.error.message"><html-el:errors bundle="GroupUIResources"/></span></font>
					</td>
				</tr>
              </table>
              
		    
		   
		   	<table width="93%" border="0" cellspacing="0" cellpadding="3">
		   	<c:choose>
			   	<c:when test="${CenterHierarchyExist == true}">
			   	 <br>
                <tr>
                  <td>
                  	<span class="fontnormalbold">
	                   <mifos:mifoslabel name="Group.loanofficerassigned" bundle="GroupUIResources"></mifos:mifoslabel>
                    </span>
                    <span class="fontnormal">
                  		<c:out value="${sessionScope.groupCustActionForm.parentCustomer.personnel.displayName}"/>  <br>
              		</span>
              		<span class="fontnormalbold">
						 <fmt:message key="Group.centerAssign" >
				  			<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /></fmt:param>
				  		 </fmt:message>
              		</span>
              		<span class="fontnormal">
              			<c:out value="${sessionScope.groupCustActionForm.parentCustomer.displayName}"/><br>
              		</span>
              		<span class="fontnormalbold">
              			<mifos:mifoslabel name="Group.meetingschedule" bundle="GroupUIResources"></mifos:mifoslabel>
              		</span>
              		<span class="fontnormal">
	              		<c:out value="${customerfn:getMeetingSchedule(sessionScope.groupCustActionForm.parentCustomer.customerMeeting.meeting,sessionScope.UserContext)}"/>
              			<br>
              		</span>
              		<span class="fontnormalbold">
              			<mifos:mifoslabel name="Group.locationofthemeeting" bundle="GroupUIResources"></mifos:mifoslabel>
              		</span>
              		<span class="fontnormal">
              			<c:out value="${sessionScope.groupCustActionForm.parentCustomer.customerMeeting.meeting.meetingPlace}"/>
              			<br>
              		</span>
              		<br>
              		</td>
                </tr>
               </c:when>
               <c:otherwise>
                <tr>
                  <td class="fontnormal">
                  	<span class="fontnormalbold">
                  	   
                  		<fmt:message key="Group.Selected">
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}" /></fmt:param>
					</fmt:message>
					</span>
                  	 <c:out value="${sessionScope.groupCustActionForm.officeName}"/></td>
                </tr>
               </c:otherwise>
               </c:choose>
              </table>              


              <table width="93%" border="0" cellpadding="0" cellspacing="0">
              
                <tr>
                  <td width="100%" height="23" class="fontnormalboldorange">
                  <fmt:message key="Group.groupinformation">
				<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
				</fmt:message>
                  </td>
                </tr>
                
                <tr>
                  <td class="fontnormalbold">
                  <fmt:message key="Group.groupname">
                  <fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
                  </fmt:message>
                  <span class="fontnormal"> 
	               <c:out value="${sessionScope.groupCustActionForm.displayName}"/>  
                   <br>
                   </span>
                  </td>
                  </tr>
                  
                  <tr>
	                  <td class="fontnormalbold">
		                  <mifos:mifoslabel name="Group.FormedBy" bundle="GroupUIResources"></mifos:mifoslabel>
				          <span class="fontnormal">
					          <c:forEach var="formedBy" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'formedByLoanOfficers')}">
									<c:if test = "${formedBy.personnelId == sessionScope.groupCustActionForm.formedByPersonnel}">
										<c:out value="${formedBy.displayName}"/><br>
									</c:if>
							  </c:forEach>
				          </span>
	                  </td>
                  </tr>
                 <%-- Show following only if center hierarchy does not exists --%>
                 
                 <tr>
                 <td class="fontnormalbold">
              	 <c:if test="${CenterHierarchyExist == false}">
                  <span class="fontnormalbold">
                  <mifos:mifoslabel name="Group.loanofficerassigned" bundle="GroupUIResources"></mifos:mifoslabel>
                  </span>
                  <span class="fontnormal"> 
                  	<c:forEach var="LO" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanOfficers')}">
						<c:if test = "${LO.personnelId == sessionScope.groupCustActionForm.loanOfficerId}">
							<c:out value="${LO.displayName}"/><br>
						</c:if>
					</c:forEach>
					<br>
                  </span>
                  <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customerMeeting')}" var="customerMeeting" />
                   <span class="fontnormalbold">
	                    <mifos:mifoslabel name="Group.meetingschedule" bundle="GroupUIResources"></mifos:mifoslabel>
                   </span>
                   <span class="fontnormal">
						<c:out value="${customerfn:getMeetingSchedule(customerMeeting,sessionScope.UserContext)}"/><br> 
					</span>
				   <span class="fontnormalbold">
                    	<mifos:mifoslabel name="Group.locationofthemeeting" bundle="GroupUIResources"></mifos:mifoslabel>
                   </span>
                   
                   <span class="fontnormal">
                    	<c:out value="${customerMeeting.meetingPlace}"/><br>
                   </span>
					
              </c:if>
              	</td>
              	</tr>
                  <%-- Show following always --%>
                  
                  <tr id="Group.Trained">
                  <td class="fontnormalbold">
                    <mifos:mifoslabel name="Group.grouptrained" bundle="GroupUIResources" keyhm="Group.Trained" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
                    <span class="fontnormal"> 
	                	<c:choose>
							<c:when test="${sessionScope.groupCustActionForm.customerTrained == true}">                    	
		                    		<mifos:mifoslabel name="Group.YesLabel" bundle="GroupUIResources"/>                    	
		                    </c:when>
						    <c:otherwise>
			                    <mifos:mifoslabel name="Group.NoLabel" bundle="GroupUIResources"/>
						    </c:otherwise>
				        </c:choose>
			        </span>
                    <br>
				</td>
				</tr>
				
				<tr id="Group.TrainedDate">
                  <td class="fontnormalbold">
					 <mifos:mifoslabel name="Group.grouptrainedon" bundle="GroupUIResources" keyhm="Group.TrainedDate" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
					<span class="fontnormal">
						<c:out value="${sessionScope.groupCustActionForm.trainedDate}" />
					</span><br>
				  </td>
				</tr>
				
				<tr id="Group.ExternalId">
                  <td class="fontnormalbold">
					<mifos:mifoslabel name="${ConfigurationConstants.EXTERNALID}" keyhm="Group.ExternalId" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/>
					<span class="fontnormal">
						 <c:out value="${sessionScope.groupCustActionForm.externalId}"/>
					</span><br><br> 
				  </td>
				</tr>
					
				  <tr id="Group.Address">
                  <td class="fontnormalbold">
						<mifos:mifoslabel name="Group.address" bundle="GroupUIResources" keyhm="Group.Address" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel><br>
					<span class="fontnormal"> 
						<c:out value="${sessionScope.groupCustActionForm.address.displayAddress}"/><br>
					</span>
				  </td>
				  </tr>
				  
				  <tr id="Group.City">
                  <td class="fontnormalbold">
					<mifos:mifoslabel name="${ConfigurationConstants.CITY}" isColonRequired="yes" keyhm="Group.City" isManadatoryIndicationNotRequired="yes"/>
					<span class="fontnormal">
						<c:out value="${sessionScope.groupCustActionForm.address.city}"/><br>
					</span>
				</td>
				</tr>
				
				<tr id="Group.State">
                  <td class="fontnormalbold">
					<mifos:mifoslabel name="${ConfigurationConstants.STATE}" isColonRequired="yes" keyhm="Group.State" isManadatoryIndicationNotRequired="yes"/>
				<span class="fontnormal"> 
					<c:out value="${sessionScope.groupCustActionForm.address.state}"/><br>
				</span>
				</td>
				</tr>
				
				<tr id="Group.Country">
                  <td class="fontnormalbold">
					<mifos:mifoslabel name="Group.country" bundle="GroupUIResources" keyhm="Group.Country" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
					<span class="fontnormal">
						<c:out value="${sessionScope.groupCustActionForm.address.country}"/><br>
					</span>
				</td>
				</tr>
				
				<tr id="Group.PostalCode">
                  <td class="fontnormalbold">
					<mifos:mifoslabel name="${ConfigurationConstants.POSTAL_CODE}" isColonRequired="yes" keyhm="Group.PostalCode" isManadatoryIndicationNotRequired="yes"/>
					<span class="fontnormal">
						<c:out value="${sessionScope.groupCustActionForm.address.zip}"/><br>
					<br>
					</span>
					</td>
					</tr>
					
				<tr id="Group.PhoneNumber">
                  <td class="fontnormalbold">
					<mifos:mifoslabel name="Group.telephone" bundle="GroupUIResources" keyhm="Group.PhoneNumber" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
					<span class="fontnormal">
						<c:out value="${sessionScope.groupCustActionForm.address.phoneNumber}"/></span><br>
											
				</td>
				</tr>	
			<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
			<tr>
                  <td class="fontnormalbold">
                  <br>
					<mifos:mifoslabel name="Group.additionalinformation" bundle="GroupUIResources"></mifos:mifoslabel>
					<br>					
					<c:forEach var="cfdef" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
						 <c:forEach var="cf" items="${sessionScope.groupCustActionForm.customFields}">
							<c:if test="${cfdef.fieldId==cf.fieldId}">
								<mifos:mifoslabel name="${cfdef.lookUpEntity.entityType}" bundle="GroupUIResources"></mifos:mifoslabel>: 
				           	 	<span class="fontnormal"><c:out value="${cf.fieldValue}"/></span><br>
							</c:if>
						</c:forEach>
		  		  </c:forEach>    		 			
  		 	</td>
  		 	</tr>
  		 	</c:if>
  		 	
  		 	<tr>
                  <td class="fontnormalbold">
                   <br>
					<c:if test="${!empty sessionScope.groupCustActionForm.defaultFees}">
					<mifos:mifoslabel name="Group.adminsetfess" bundle="GroupUIResources"></mifos:mifoslabel><br>
						<c:forEach var="adminFee" items="${sessionScope.groupCustActionForm.defaultFees}">
								<c:if test="${adminFee.removed == false}">
							  		 <c:out value="${adminFee.feeName}"/>:
							   		<span class="fontnormal">
							   			<c:out value="${adminFee.amount}"/> 
							   			<mifos:mifoslabel name="Group.periodicity" bundle="GroupUIResources"/>
								   		<c:choose>
											<c:when test="${adminFee.periodic}">
												<c:out value="${adminFee.feeSchedule}"/>
											</c:when>
											<c:otherwise>
												<mifos:mifoslabel name="Fees.onetime"/>
											</c:otherwise>
										</c:choose>
									</span><br>
								</c:if> 
							</c:forEach>
							<br>
							</c:if>
				</td>
			</tr>
			<tr>
              <td class="fontnormalbold">		
					<mifos:mifoslabel name="Group.additionalfees" bundle="GroupUIResources"></mifos:mifoslabel><br>
					<c:forEach var="fee" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'additionalFeeList')}" >
						<c:forEach var="selectedFee" items="${sessionScope.groupCustActionForm.additionalFees}" >
							<c:if test="${fee.feeId == selectedFee.feeId}">
			           	  		<c:out value="${fee.feeName}"/>:
								<span class="fontnormal"><span class="fontnormal">
								<c:out value="${selectedFee.amount}"/> 
								<mifos:mifoslabel name="Group.periodicity" bundle="GroupUIResources"/>
								<c:choose>
									<c:when test="${fee.periodic == true}">
										<c:out value="${fee.feeSchedule}"/>
									</c:when>
									<c:otherwise>
										<mifos:mifoslabel name="Fees.onetime"/>
									</c:otherwise>
								</c:choose></span><br></span>
							</c:if>	
						</c:forEach>
					</c:forEach>
				<br>  <br>                  
                      
              <html-el:button styleId="previewgroup.button.edit" property="editInfo" styleClass="insidebuttn"  onclick="GoToEditPage()">
					<fmt:message key="Group.editInformation">
				<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
				</fmt:message>
              </html-el:button>
              </td>
            </tr>
              </table>
              <table width="93%" border="0" cellpadding="0" cellspacing="0">
              	<tr>
                  <td align="center" class="blueline">&nbsp;</td>
                </tr>
                <tr>
                  <td align="center">&nbsp;</td>
                </tr>
                <tr>
                 <td align="center">
                  <html-el:submit  styleId="previewgroup.button.saveForLater" property="submitBtn1" onclick="setGroupStatus('${CustomerStatus.GROUP_PARTIAL.value}')"  styleClass="buttn">
                  	<mifos:mifoslabel name="button.saveforlater" bundle="GroupUIResources"></mifos:mifoslabel>
                  </html-el:submit>
&nbsp;
			<c:choose>
		    	<c:when test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'pendingApprovalDefined') == true}">
				 <html-el:button styleId="previewgroup.button.submitForApproval" property="submitBtn2" styleClass="buttn"   onclick="setGroupStatus('${CustomerStatus.GROUP_PENDING.value}')" >
				 	<mifos:mifoslabel name="button.submitforapproval" bundle="GroupUIResources"></mifos:mifoslabel>
				 </html-el:button>
				</c:when>
				<c:otherwise>	 
				 <html-el:button styleId="previewgroup.button.approve" property="submitBtn2" styleClass="buttn"  onclick="setGroupStatus('${CustomerStatus.GROUP_ACTIVE.value}')" >
				 	<mifos:mifoslabel name="button.approved" bundle="GroupUIResources"></mifos:mifoslabel>
				 </html-el:button>
				</c:otherwise>		
			</c:choose>					 
				 &nbsp;
                 <html-el:button styleId="previewgroup.button.cancel" property="cancelBtn"  styleClass="cancelbuttn"  onclick="goToCancelPage()">
	                    <mifos:mifoslabel name="button.cancel" bundle="GroupUIResources"></mifos:mifoslabel>
                 </html-el:button>
                  </td></tr>
            </table>
            <br></td>
          </tr>
        </table>
      <br></td>
  </tr>
</table>
</html-el:form>
</tiles:put>
</tiles:insert>

