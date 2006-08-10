<!--
/**

* centerDetails.jsp    version: 1.0



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
 <%@taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>

<!-- Tils definition for the header and menu -->
<tiles:insert definition=".clientsacclayoutsearchmenu">
<tiles:put name="body" type="string">
<script language="javascript">
	function ViewDetails(){
		closedaccsearchactionform.submit();
	}
	
  function GoToEditPage(){
	centerActionForm.action="centerAction.do?method=manage";
	centerActionForm.submit();
  }

</script>

<html-el:form action="centerAction.do">
<html-el:hidden property="input" value="CenterDetails}"/> 
<!-- Hidden properties set for customer id, version, system id and version number -->
<html-el:hidden property="customerId" value="${requestScope.centerVO.customerId}"/> 
<html-el:hidden property="globalCustNum" value="${requestScope.centerVO.globalCustNum}"/> 
<html-el:hidden property="versionNo" value="${requestScope.centerVO.versionNo}"/> 
<html-el:hidden property="statusId" value="${requestScope.centerVO.statusId}"/> 
<html-el:hidden property="office.officeId" value="${requestScope.centerVO.office.officeId}"/> 
     <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td class="bluetablehead05">
	            <span class="fontnormal8pt">
	            	<a href="CustomerSearchAction.do?method=getOfficeHomePage&officeId=<c:out value="${sessionScope.linkValues.customerOfficeId}"/>&officeName=<c:out value="${sessionScope.linkValues.customerOfficeName}"/>&loanOfficerId=<c:out value="${requestScope.Context.userContext.id}"/>">
	            	<c:out value="${sessionScope.linkValues.customerOfficeName}"/></a> 
					 /
	            </span>
	            <!-- Name of the client -->
	            <span class="fontnormal8ptbold">
	            	<c:out value="${requestScope.centerVO.displayName}"/>
	            </span>
            </td>
          </tr>
        </table>
        <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <!-- Name of the center -->
          <tr>
            <td width="70%" align="left" valign="top" class="paddingL15T15">
            	<table width="96%" border="0" cellpadding="3" cellspacing="0">
					<tr>
	                  <td class="headingorange"><c:out value="${requestScope.centerVO.displayName}"/></td>
	                  <td rowspan="2" align="right" valign="top" class="headingorange">
	                  	<span class="fontnormal">
						<!-- Edit center status link -->
	                  	  				   
	                  	  <a href= "editCustomerStatusAction.do?method=load&customerId=<c:out value="${requestScope.centerVO.customerId}"/>&securityParamInput=Center">
	                  	  <mifos:mifoslabel name="Center.Edit" bundle="CenterUIResources" />
	                  	  <mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
	                  	  <mifos:mifoslabel name="Center.Status1" bundle="CenterUIResources" />	                  	  
	                  	  </a><br>
	                  	  </span></td>
	                </tr>
	                <tr>
		   				<td colspan="2">
		   				<font class="fontnormalRedBold"><html-el:errors bundle="CenterUIResources" /></font>
						</td>
					</tr>
	                <tr>
	                  <td class="fontnormalbold">
	                  <c:set var="statusID" scope="request" value="${requestScope.centerVO.statusId}"/> 
	                 <!-- Status of the center. Depending on the status id active or inactive is displayed -->
	                  
	
	                  <c:if test="${statusID == 13}">   
		                  <span class="fontnormal"><img src="pages/framework/images/status_activegreen.gif" width="8" height="9">
		              </c:if>
		              <c:if test="${statusID == 14}">   
		               <span class="fontnormal"><img src="pages/framework/images/status_closedblack.gif" width="8" height="9">
		              </c:if>
		              <c:out value="${requestScope.currentStatus}"/></span><br>
		              <!-- System Id of the center -->
	                  <span class="fontnormal"><mifos:mifoslabel name="Center.SystemId" bundle="CenterUIResources"></mifos:mifoslabel>:</span> <span class="fontnormal"><c:out value="${requestScope.centerVO.globalCustNum}"/><br>
	                 	<!-- Loan Officer of the center -->
	                 	<mifos:mifoslabel name="Center.LoanOff" bundle="CenterUIResources" ></mifos:mifoslabel> <c:out value="${requestScope.centerVO.personnel.displayName}"/></span><br>
	                  <br>                  
	                  <!-- List of groups under the center -->
	                  <mifos:mifoslabel name="${ConfigurationConstants.GROUP}"/><mifos:mifoslabel name="Center.s" bundle="CenterUIResources" />
	                  <mifos:mifoslabel name="Center.Assigned" bundle="CenterUIResources" />
	                  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                  <c:if test="${statusID != 14}">
		                  <span class="fontnormal">
		                  	  <a href="GroupAction.do?method=load&centerSystemId=<c:out value="${requestScope.centerVO.globalCustNum}"/>&parentOfficeId=${requestScope.centerVO.office.officeId}&recordOfficeId=${requestScope.centerVO.office.officeId}&recordLoanOfficerId=${requestScope.centerVO.personnel.personnelId}">
		                  	  <mifos:mifoslabel name="Center.Add"  bundle="CenterUIResources" />
		                  	  <mifos:mifoslabel name="${ConfigurationConstants.GROUP}"  /></a>
						  </span>
					  </c:if>
	                  <br>
	                  <span class="fontnormal">
	                  <mifos:mifoslabel name="Center.GroupsLink1" bundle="CenterUIResources"></mifos:mifoslabel>
	                  <mifos:mifoslabel name="${ConfigurationConstants.GROUP}"  />
	                  <mifos:mifoslabel name="Center.GroupsLink2" bundle="CenterUIResources"></mifos:mifoslabel>
	                  <mifos:mifoslabel name="${ConfigurationConstants.GROUP}"  />	                  
	                  <mifos:mifoslabel name="Center.GroupsLink3" bundle="CenterUIResources"></mifos:mifoslabel>
	                  </span><br>
	                  <div id="Layer2" style="border: 1px solid #CECECE; height:100px; width:250px; overflow: auto; padding:6px; margin-top:5px;">
	                   <span class="fontnormal">
	                  	<c:choose>
		                  	<c:when test="${!empty requestScope.groups}">
			                  	<c:forEach var="group" items="${requestScope.groups}">
								   <span class="fontnormal">
				                  	  <a href="GroupAction.do?method=get&globalCustNum=<c:out value="${group.globalCustNum}"/>&recordOfficeId=${requestScope.centerVO.office.officeId}&recordLoanOfficerId=${requestScope.centerVO.personnel.personnelId}">
				                  	  <c:out value="${group.displayName}"/></a><br>
							       </span>
							    </c:forEach>
						    </c:when>
						    <c:otherwise>
							    <!-- <c:if test="${sessionScope.performanceHistory.numberOfGroups==0}">-->
							    <mifos:mifoslabel name="Center.No" bundle="CenterUIResources" />
							    <mifos:mifoslabel name="${ConfigurationConstants.GROUP}"/><mifos:mifoslabel name="Center.s" bundle="CenterUIResources" />
							    <mifos:mifoslabel name="Center.Available" bundle="CenterUIResources" />
							    <!-- </c:if> -->
							</c:otherwise>    
						</c:choose>
					  </div>
					  <br></td>
	                </tr>
                </table>
                
              	<table width="96%" border="0" cellpadding="3" cellspacing="0">
                <!-- Account table -->
                <tr>
                  <td width="36%" class="headingorange"><mifos:mifoslabel name="Center.AccountHeading" bundle="CenterUIResources"></mifos:mifoslabel></td>
                </tr>
                <tr align="right">
                  <td class="headingorange"><span class="fontnormal">
		              <c:if test="${statusID == 13}">
		              <mifos:mifoslabel name="Center.AccountsLink" bundle="CenterUIResources"></mifos:mifoslabel>&nbsp;  
		               <html-el:link href="savingsAction.do?method=getPrdOfferings&customerId=${requestScope.centerVO.customerId}&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}">
		               <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" />
		               </html-el:link>
		                </span>
                  	  </c:if>	
                  </td>
                </tr>
              </table>
              
              
              <c:if test="${!empty requestScope.CustomerActiveSavingsAccounts}">
              <table width="96%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td width="63%" align="left" valign="top" class="tableContentLightBlue">
                  	<table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td width="63%">
                      	<span class="fontnormalbold">
                      		<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" />
                      	</span>
                      	<span class="fontnormal"></span>
                      </td>
                    </tr>
                  	</table>                    
                  <span class="fontnormal"></span>               
                    <table width="95%" border="0" align="center" cellpadding="0" cellspacing="0">
	                    <c:forEach items="${requestScope.CustomerActiveSavingsAccounts}" var="savings">
	                    	 <tr>
		                        <td>
		                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
		                          <tr>
		                            <td width="65%">
		                            <span class="fontnormal"> 
		                            	<html-el:link href="savingsAction.do?globalAccountNum=${savings.globalAccountNum}&method=get&recordOfficeId=${param.recordOfficeId}&recordLoanOfficerId=${param.recordLoanOfficerId}">
		                            		<c:out value="${savings.savingsOffering.prdOfferingName}"/>,Acct #<c:out value="${savings.globalAccountNum}"/>
		                            	</html-el:link>
									</span>
									</td>
		                            <td width="35%">
		                            	<span class="fontnormal">
		                            		<mifoscustom:MifosImage id="${savings.accountState.id}" moduleName="accounts.savings" />
		                            		<c:out value="${savings.accountState.name}"/>
		                            	</span></td>
		                          </tr>
		                        </table>
		                        <span class="fontnormal"><mifos:mifoslabel name="Savings.balance"/>: 
		                        <c:out value="${savings.savingsBalance.amountDoubleValue}"/>
		                        </span></td>
		                      </tr>
		                      <tr>
		                       <td><img src="pages/framework/images/trans.gif" width="5" height="20"></td>
		                      </tr>
		                      </c:forEach>
		                    </table>                    
		                    </td>
		               		 </tr>
	                    
              		</table>
              <table width="50%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td><img src="pages/framework/images/trans.gif" width="10" height="10"></td>
                </tr>
              </table>
              </c:if>          
              
              <table width="96%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td width="65%" align="left" valign="top" class="tableContentLightBlue"> 
                  <span class="fontnormalbold">
                     <mifos:mifoslabel name="${ConfigurationConstants.CENTER}"/>
	                 <mifos:mifoslabel name="Center.Charges" bundle="CenterUIResources"/>
                  </span>
                      <table width="95%" border="0" align="center" cellpadding="0" cellspacing="0">
                        <tr>
                          <td><table width="100%" border="0" cellspacing="0" cellpadding="0">
                              <tr>
                                <td width="53%"><span class="fontnormal"> 
                               <%-- <c:forEach var="cp" items="${requestScope.centerVO.customerAccounts}">
									<c:if test="${cp.accountId==3}">
										<c:set var="accID" scope="request" value="${cp.accountId}"/> 
										
									</c:if>
								</c:forEach> --%>
                                <html-el:link href="javascript:ViewDetails()">
                                View details</html-el:link> </span></td>
                              </tr>
                            </table>
                              <span class="fontnormal">Amount Due: 
                              <c:out value='${requestScope.Context.businessResults["totalFeeDue"]}'/>  </span>
                              </td>
                        </tr>
                        <tr>
                          <td><img src="pages/framework/images/trans.gif" width="5" height="5"></td>
                        </tr>
                      </table>
                  </td>
                </tr>
              </table>
              <table width="50%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td><img src="pages/framework/images/trans.gif" width="10" height="5"></td>
                </tr>
              </table>
                
              <table width="96%" border="0" cellpadding="3" cellspacing="0">
              
                <tr>
                  <td width="38%" align="right" class="fontnormal">
                  <span class="fontnormal">
                  <html-el:link href="closedaccsearchaction.do?method=search&searchNode(search_name)=AllClosedAccounts&customerId=${requestScope.centerVO.customerId}&input=ViewCenterClosedAccounts">
                  <mifos:mifoslabel name="Group.viewallclosedaccounts"  bundle="GroupUIResources"></mifos:mifoslabel>
                  </html-el:link>
                  </span></td>
                </tr>
              </table>              
              <table width="50%" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td><img src="pages/framework/images/trans.gif" width="10" height="10"></td>
              </tr>
            </table> 
                                  
              <table width="96%" border="0" cellpadding="0" cellspacing="0">
              
              <tr>
                <td width="50%" height="23" class="headingorange"><mifos:mifoslabel name="${ConfigurationConstants.CENTER}"/><c:out value=" "/><mifos:mifoslabel name="Center.Information" bundle="CenterUIResources"/></td>
                <td width="50%" align="right" class="fontnormal">
                	<html-el:link action="centerCustAction.do?method=manage"><mifos:mifoslabel name="Center.Edit" bundle="CenterUIResources" /><c:out value=" "/><mifos:mifoslabel name="${ConfigurationConstants.CENTER}"/><c:out value=" "/><mifos:mifoslabel name="Center.details" bundle="CenterUIResources" /></html-el:link>
                </td>
              </tr>
              
               <tr>
                <td height="23" colspan="2" class="fontnormalbold">                  
                <span class="fontnormal">
	               <!-- MFI Joining date -->
	               <mifos:mifoslabel name="Center.MfiJoiningDate" bundle="CenterUIResources"></mifos:mifoslabel>: 
 					<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.centerVO.mfiJoiningDate)}" />
					<br>
					<mifos:mifoslabel name="${ConfigurationConstants.CENTER}"/>  
					<mifos:mifoslabel name="Center.CenterStartDate" bundle="CenterUIResources"></mifos:mifoslabel>:  
					<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.centerVO.createdDate)}" />
				</td>
				</tr>
				
				<!-- External Id -->
	      		<tr id="Center.ExternalId">
                <td height="23" colspan="2" class="fontnormalbold">                  
                <span class="fontnormal">
	      			<mifos:mifoslabel name="${ConfigurationConstants.EXTERNALID}" bundle="CenterUIResources" keyhm="Center.ExternalId" isManadatoryIndicationNotRequired="yes" isColonRequired="yes"></mifos:mifoslabel> 
	      			<c:out value="${requestScope.centerVO.externalId}"/><br>	       			
                </span>
                </td>
                </tr>
                
                <!-- Client positions -->
                <tr >
                <td height="23" colspan="2" class="fontnormalbold"> 
                <br>                                 
                <mifos:mifoslabel name="Center.OfficialTitlesHeading" bundle="CenterUIResources"></mifos:mifoslabel><br>
               
				 <c:forEach var="position" items="${requestScope.positions}">
				 	<c:forEach var="cp" items="${requestScope.centerVO.customerPositions}">
						<c:if test="${position.positionId==cp.positionId}">
						<span class="fontnormal">
							<c:if test="${! empty cp.customerId}">
							<c:out value="${position.positionName}"/>:
							<c:out value="${cp.customerName}"/><br>
							</c:if>
								
							
		                </span>
						</c:if>
					</c:forEach>
			  	</c:forEach>     				   
                <br>
                </td>
                </tr>
                
           <tr>
           <td height="23" colspan="2" class="fontnormalbold">                  
           <span class="fontnormal">     
          <table width="100%" border="0" cellspacing="0" cellpadding="0">
        	<tr>
	          <td class="fontnormalbold">
	          <mifos:mifoslabel name="Center.MeetingsHeading" bundle="CenterUIResources"></mifos:mifoslabel> </td>
				  <td align="right">
				       	<html-el:link action="MeetingAction.do?method=get&input=CenterDetails&meetingId=${requestScope.centerVO.customerMeeting.meeting.meetingId}">
			          	<mifos:mifoslabel name="Center.MeetingsLink" bundle="CenterUIResources"></mifos:mifoslabel></html-el:link>
			      </td>
	        </tr>
	      </table>      
	      <span class="fontnormalRed">
	      <mifos:mifoslabel name="Center.MeetingsSubHeading" bundle="CenterUIResources"></mifos:mifoslabel>:&nbsp;
	      <c:out value="${requestScope.centerVO.customerMeeting.meeting.meetingSchedule}"/></span><span class="fontnormal"><br>
	      <span class="fontnormal"><c:out value="${requestScope.centerVO.customerMeeting.meeting.meetingPlace}"/></span> </span><span class="fontnormal"><br>
	      </span><br>      <span class="fontnormal">      </span><span class="fontnormal">
	      </span>
	      <!-- Address Fields -->
	      
	      </td>
	      </tr>
	      
	      <tr id="Center.Address">
           <td height="23" colspan="2" class="fontnormalbold">                              
	      <c:if test="${not empty requestScope.centerVO.customerAddressDetail.phoneNumber ||
					    not empty requestScope.centerVO.customerAddressDetail.line1 ||
						not empty requestScope.centerVO.customerAddressDetail.line2 ||
						not empty requestScope.centerVO.customerAddressDetail.line3 ||
						not empty requestScope.centerVO.customerAddressDetail.city	 ||
						not empty requestScope.centerVO.customerAddressDetail.state	 ||
						not empty requestScope.centerVO.customerAddressDetail.country	 ||
						not empty requestScope.centerVO.customerAddressDetail.zip }">	
	      <mifos:mifoslabel name="Center.Address" bundle="CenterUIResources" keyhm="Center.Address" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
	      <span class="fontnormal"><br></span> 
	      <c:if test="${!empty requestScope.centerVO.displayAddress}">
	      	<span class="fontnormal">
	      		<c:out value="${requestScope.centerVO.displayAddress}"/><br>
	      	</span>
	      </c:if>
	      <c:if test="${!empty requestScope.centerVO.customerAddressDetail.city}">
	      	<span class="fontnormal">
	      		<c:out value="${requestScope.centerVO.customerAddressDetail.city}"/><br>
	      	</span>
	      </c:if>
	      <c:if test="${!empty requestScope.centerVO.customerAddressDetail.state}">
	      	<span class="fontnormal"> <c:out value="${requestScope.centerVO.customerAddressDetail.state}"/><br></span>
	      </c:if>
	      <c:if test="${!empty requestScope.centerVO.customerAddressDetail.country}">
	      	<span class="fontnormal"><c:out value="${requestScope.centerVO.customerAddressDetail.country}"/><br></span>
	      </c:if>
	      <c:if test="${!empty requestScope.centerVO.customerAddressDetail.zip}">
			<span class="fontnormal"><c:out	value="${requestScope.centerVO.customerAddressDetail.zip}" /> <br>
			</span> 
			</c:if>
	      <span class="fontnormal">
	      <span class="fontnormal"></span>
	      </td>
	      </tr>
	      
	      <tr id="Center.PhoneNumber">
           <td height="23" colspan="2" class="fontnormalbold">                  
           <span class="fontnormal"> 
          <!-- Telephone Number -->
	      <span class="fontnormal">
	      <c:set var="phoneNumber" value="${requestScope.centerVO.customerAddressDetail.phoneNumber}"/>
	      <c:if test="${!empty phoneNumber}"><br>
	      	<mifos:mifoslabel name="Center.Telephone" bundle="CenterUIResources" keyhm="Center.PhoneNumber" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> 
	      	<c:out value="${requestScope.centerVO.customerAddressDetail.phoneNumber}"/><br></span>
      	  </c:if>	<br>
      	  </td>
      	  </tr>
      	  
      	  <tr>
           <td height="23" colspan="2" class="fontnormalbold">                  
           <span class="fontnormal"> 
	      <!-- Additional Information -->
	     </c:if>
	     <c:if test="${!empty requestScope.customFields}">
	     <span class="fontnormalbold">
	      <mifos:mifoslabel name="Center.AdditionalInformationHeading" bundle="CenterUIResources"></mifos:mifoslabel> 
	      	<br></span>
	      </c:if>
	      <span class="fontnormal">
	      	<c:forEach var="cf" items="${requestScope.customFields}">
				   <c:forEach var="customField" items="${requestScope.centerVO.customFieldSet}">
					 <c:if test="${cf.fieldId==customField.fieldId}">
						<c:choose>
								<c:when test="${cf.fieldType == 3}"> 
									<mifos:mifoslabel name="${cf.lookUpEntity.entityType}" bundle="CenterUIResources"></mifos:mifoslabel>: 
					         		<span class="fontnormal"><c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,customField.fieldValue)}" /><br></span>
					         	</c:when>
								<c:otherwise>
									<mifos:mifoslabel name="${cf.lookUpEntity.entityType}" bundle="CenterUIResources"></mifos:mifoslabel>: 
					         		<span class="fontnormal"><c:out value="${customField.fieldValue}"/><br></span>
				         		</c:otherwise>
			         		</c:choose>
			         	</c:if>
					</c:forEach>
	    		</c:forEach>
	    		<br> 
	    		<html-el:link href="closedaccsearchaction.do?method=search&searchNode(search_name)=ChangeLogDetails&input=ViewCenterChangelog&customerId=${requestScope.centerVO.customerId}&entityTypeId=20&createdDate=${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.centerVO.createdDate)}">
	    		<mifos:mifoslabel name="center.ChangeLogLink" bundle="CenterUIResources"></mifos:mifoslabel>
	    		 </html-el:link>
	      	</span>
	      	</td>
	        </tr>
	         </table> 
	                       
	            </td>
	            <!-- Performance History -->
	            <td width="30%" align="left" valign="top" class="paddingleft1"><table width="100%" border="0" cellpadding="2" cellspacing="0" class="bluetableborder">
	              <tr>
	                <td class="bluetablehead05"><span class="fontnormalbold">
	                <mifos:mifoslabel name="Center.PerformanceHistoryHeading" bundle="CenterUIResources"></mifos:mifoslabel>
	                </span></td>
	              </tr>
	              <tr>
	                <td class="paddingL10"><img src="pages/framework/images/trans.gif" width="10" height="2"></td>
	              </tr>
	              <tr>
	                <td class="paddingL10"><span class="fontnormal8pt">
	                <mifos:mifoslabel name="Group.hashof" bundle="GroupUIResources"/>
	                <mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"/><mifos:mifoslabel name="Center.s" bundle="CenterUIResources" isColonRequired="yes"/>
	                 <c:out value="${sessionScope.performanceHistory.numberOfClients}"/></span></td>
	              </tr>
	              <tr>
	                <td class="paddingL10"><span class="fontnormal8pt">
	                <mifos:mifoslabel name="Group.hashof" bundle="GroupUIResources"/>
	                <mifos:mifoslabel name="${ConfigurationConstants.GROUP}"/><mifos:mifoslabel name="Center.s" bundle="CenterUIResources" isColonRequired="yes"/>
	                 <c:out value="${sessionScope.performanceHistory.numberOfGroups}"/></span></td>
	              </tr>
	              <tr>
	                <td class="paddingL10"><span class="fontnormal8pt">
	                <mifos:mifoslabel name="Center.Total" bundle="CenterUIResources"/>
	                <mifos:mifoslabel name="${ConfigurationConstants.LOAN}"/>
	                <mifos:mifoslabel name="Center.portfolio" bundle="CenterUIResources"  isColonRequired="yes"/>
	                 <c:out value="${sessionScope.performanceHistory.totalOutstandingPortfolio}"/></span></td>
	              </tr>
	              <tr>
	                <td class="paddingL10"><span class="fontnormal8pt">
	                <mifos:mifoslabel name="Center.PortfolioAtRisk" bundle="CenterUIResources" isColonRequired="yes"/>
	                 <c:out value="${sessionScope.performanceHistory.portfolioAtRisk}"/></span></td>
	              </tr>
	              <tr>
	                <td class="paddingL10"><span class="fontnormal8pt">
	                <mifos:mifoslabel name="Center.Total" bundle="CenterUIResources"/>
	                <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" isColonRequired="yes"/>
	                 <c:out value="${sessionScope.performanceHistory.totalSavings}"/></span></td>
	              </tr>
	              
	            </table>
	              <table width="95%" border="0" cellspacing="0" cellpadding="0">
	                <tr>
	                  <td><img src="pages/framework/images/trans.gif" width="7" height="8"></td>
	                </tr>
	              </table>                
	              <table width="100%" border="0" cellpadding="2" cellspacing="0" class="bluetableborder">
	                <tr>
	                  <td class="bluetablehead05"><span class="fontnormalbold"><mifos:mifoslabel name="Center.RecentNotes" bundle="CenterUIResources" /></span></td>
	                </tr>
	                <tr>
	                  <td class="paddingL10"><img src="pages/framework/images/trans.gif" width="10" height="2"></td>
	                </tr>
	                <tr>
	                    <td class="paddingL10">
	                    <c:choose>
                    <c:when test="${!empty requestScope.notes}">
	                    <c:forEach var="note" items="${requestScope.notes}">
	                    	<span class="fontnormal8ptbold"> 
 								<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,note.commentDate)}" />
	                    	</span>
	                    	<span class="fontnormal8pt"> 
	                    	<c:out value="${note.comment}"/>
	                    	 -<em><c:out value="${note.personnelName}"/></em><br><br>
	                      	</span>
	                     </c:forEach> 
	                     </c:when>
                     <c:otherwise>
	                     <span class="fontnormal"> 
	                    	 <mifos:mifoslabel name="Group.nonotesavailable" bundle="GroupUIResources"/>
	                     </span>
                     </c:otherwise>
                     </c:choose>
						</td>
	                  </tr>
	                <tr>
	                  <td align="right" class="paddingleft05">
                    <span class="fontnormal8pt">
                     <c:if test="${!empty requestScope.notes}">
            		<html-el:link href="customerNotesAction.do?method=search&customerId=${requestScope.centerVO.customerId}&globalAccountNum=${requestScope.centerVO.globalCustNum}&customerName=${requestScope.centerVO.displayName}&securityParamInput=Center&levelId=${requestScope.centerVO.customerLevel.levelId}">
            		  	<mifos:mifoslabel name="center.SeeAllNotesLink" bundle="CenterUIResources" ></mifos:mifoslabel>
                    </html-el:link><br>
                    </c:if>
            	   <a href="customerNotesAction.do?method=load&customerId=<c:out value="${requestScope.centerVO.customerId}"/>">
			             <mifos:mifoslabel name="Center.NotesLink" bundle="CenterUIResources"></mifos:mifoslabel>
            	    </a>
                    </span></td>
	                </tr>
	              </table>
	            </td>
	          </tr>
	        </table>        
	        <br></td>
	  </tr>
	</table>
<br>
<mifos:SecurityParam property="Center" />
</html-el:form>
<html-el:form  action="closedaccsearchaction.do?method=search">
<html-el:hidden property="searchNode(search_name)" value="ClientChargesDetails"/>
<html-el:hidden property="prdOfferingName" value="${requestScope.centerVO.displayName}"/> 
<html-el:hidden property="globalAccountNum" value="${requestScope.centerVO.customerAccount.globalAccountNum}"/> 
<html-el:hidden property="accountId" value="${requestScope.centerVO.customerAccount.accountId}"/> 
<html-el:hidden property="accountType" value="${requestScope.centerVO.customerAccount.accountTypeId}"/> 
<html-el:hidden property="input" value="ViewCenterCharges"/>
<html-el:hidden property="customerId" value="${requestScope.centerVO.customerId}"/> 
<html-el:hidden property="statusId" value="${statusID}"/>
</html-el:form>
</tiles:put>
</tiles:insert>
