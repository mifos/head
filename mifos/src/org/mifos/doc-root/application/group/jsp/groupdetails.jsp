<!-- /**
 
 * groupdetails.jsp    version: 1.0
 
 
 
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
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/customer/customerfunctions" prefix="customerfn"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
 <tiles:put name="body" type="string">

<!--<script>
function meetingpopup(){
	window.open("schedulemeetingpopup.htm",null,"height=400,width=800,status=yes,scrollbars=yes,toolbar=no,menubar=no,location=no");
}
</script>-->
<script language="javascript">
	function ViewDetails(){
		closedaccsearchactionform.submit();
	}
</script>
<html-el:form action="GroupAction.do?method=get" >

   <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td class="bluetablehead05">
            <span class="fontnormal8pt">
            <a href="CustomerSearchAction.do?method=getOfficeHomePage&officeId=<c:out value="${sessionScope.linkValues.customerOfficeId}"/>&officeName=<c:out value="${sessionScope.linkValues.customerOfficeName}"/>&loanOfficerId=<c:out value="${requestScope.Context.userContext.id}"/>">
	           <c:out value="${sessionScope.linkValues.customerOfficeName}"/>            	
           	</a> /
           	<c:if test="${!empty sessionScope.linkValues.customerParentName}">
               	<a href="centerAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.customerParentGCNum}"/>">
			       	<c:out value="${sessionScope.linkValues.customerParentName}"/>
		       	</a> /  
		    </c:if>
            </span>
            <span class="fontnormal8ptbold">
            	<c:out value="${requestScope.GroupVO.displayName}"/>
            </span>
            </td>
          </tr>
        </table>
        <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td width="70%" align="left" valign="top" class="paddingL15T15"><table width="96%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td width="73%" class="headingorange"><c:out value="${requestScope.GroupVO.displayName}"/> <br></td>
                  <td width="27%" rowspan="2" align="right" valign="top" class="fontnormal">
	                  <c:if test="${requestScope.GroupVO.statusId != 12}">
		                  <html-el:link action="GroupAction.do?method=loadStatus">
		                  	<mifos:mifoslabel name="Group.edit"  bundle="GroupUIResources"></mifos:mifoslabel>
		                  	<mifos:mifoslabel name="${ConfigurationConstants.GROUP}"></mifos:mifoslabel>		                  	
		                  	<mifos:mifoslabel name="Group.status1"  bundle="GroupUIResources"></mifos:mifoslabel>		                  	
		                  </html-el:link>
		              </c:if>
		          </td>
                </tr>
                <tr>
   				<td colspan="2">
   				<font class="fontnormalRedBold"><html-el:errors bundle="GroupUIResources"/></font>
				</td>
				</tr>
                <tr>
                  <td class="fontnormalbold">
                  	<span class="fontnormal">
		                  <c:choose>
					<%-- Partial Application --%>
					<c:when test="${requestScope.GroupVO.statusId == 7}">
						<mifos:MifosImage id="partial" moduleName="customer.group"/>
					</c:when>
					<%-- Pending Approval --%>
					<c:when test="${requestScope.GroupVO.statusId == 8}">
						<mifos:MifosImage id="pending" moduleName="customer.group"/>
					</c:when>
					<%-- Active --%>
					<c:when test="${requestScope.GroupVO.statusId == 9}">
						<mifos:MifosImage id="active" moduleName="customer.group"/>
					</c:when>
					<%-- On Hold --%>
					<c:when test="${requestScope.GroupVO.statusId == 10}">
						<mifos:MifosImage id="hold" moduleName="customer.group"/>
					</c:when>
					<%-- Cancelled --%>
					<c:when test="${requestScope.GroupVO.statusId == 11}">
						<mifos:MifosImage id="cancelled" moduleName="customer.group"/>
					</c:when>
					<%-- Closed --%>
					<c:when test="${requestScope.GroupVO.statusId == 12}">
						<mifos:MifosImage id="closed" moduleName="customer.group"/>
					</c:when>

				<c:otherwise>	  </c:otherwise>
				</c:choose>
       			          <c:out value="${requestScope.currentStatus}"/> 
       			          <c:if test="${!empty requestScope.currentFlag}">
        					- <c:out value="${requestScope.currentFlag}"/>
        				 </c:if>
        			</span>
        			

					<c:if test="${requestScope.isBlacklisted == true}">
        			<span class="fontnormal">
        				 <mifos:MifosImage id="blackflag" moduleName="customer.group"/>
       			 		  <c:out value="${requestScope.blacklistedFlagName}"/>
        			 </span>
        			 </c:if>
        			 
                    <span class="fontnormal"><br>
                    	<mifos:mifoslabel name="Group.systemId"  bundle="GroupUIResources"/>
                    <c:out value="${requestScope.GroupVO.globalCustNum}"/>
                    </span><br>
                    <span class="fontnormal">
                    	<mifos:mifoslabel name="Group.loanofficer"  bundle="GroupUIResources"></mifos:mifoslabel>
                    <c:out value="${requestScope.GroupVO.personnel.displayName}"/>
                    </span><br>
<br>
 <mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"/><mifos:mifoslabel name="Group.s"  bundle="GroupUIResources"/>
 <mifos:mifoslabel name="Group.assigned1"  bundle="GroupUIResources"></mifos:mifoslabel>  
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 <c:if test="${requestScope.GroupVO.statusId != 11 and requestScope.GroupVO.statusId != 12}">
<span class="fontnormal">
	  <a href="clientCreationAction.do?method=load&isClientUnderGrp=1&parentGroupId=${requestScope.GroupVO.customerId}&recordOfficeId=${requestScope.GroupVO.office.officeId}&recordLoanOfficerId=${requestScope.GroupVO.personnel.personnelId}">
	  <mifos:mifoslabel name="Group.Add"  bundle="GroupUIResources" />
	  <mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></a>
</span>
</c:if>
<br>
<span class="fontnormal">
 <mifos:mifoslabel name="Group.groupdetailMsg1"  bundle="GroupUIResources"></mifos:mifoslabel>
 <mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"></mifos:mifoslabel>
 <mifos:mifoslabel name="Group.groupdetailMsg2"  bundle="GroupUIResources"></mifos:mifoslabel>
 <mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"></mifos:mifoslabel>  
 <mifos:mifoslabel name="Group.details"  bundle="GroupUIResources"></mifos:mifoslabel>
 <br>
</span>
	<div id="Layer2" style="border: 1px solid #CECECE; height:100px; width:250px; overflow: auto; padding:6px; margin-top:5px;"> 
		<span class="fontnormal">
		<!-- Display all client under this group -->
		<c:choose>
		<c:when test="${!empty requestScope.clients}">
			<c:choose>
               <c:when test="${!empty requestScope.GroupVO.personnel and !empty requestScope.GroupVO.personnel.personnelId}">
	               <c:forEach var="client" items="${requestScope.clients}">
						<a href="clientCreationAction.do?method=getDetails&globalCustNum=${client.globalCustNum}&recordOfficeId=${requestScope.GroupVO.office.officeId}&recordLoanOfficerId=${requestScope.GroupVO.personnel.personnelId}">
							<c:out value="${client.displayName}"/>
							<c:out value="${customerfn:getClientPositions(requestScope.customerPositions,client)}" />
							<br>
						</a>
				   </c:forEach> 
	           </c:when>
	           <c:otherwise>
	            	<c:forEach var="client" items="${requestScope.clients}">
						<a href="clientCreationAction.do?method=getDetails&globalCustNum=${client.globalCustNum}&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}">
							<c:out value="${client.displayName}"/>
							<c:out value="${customerfn:getClientPositions(requestScope.customerPositions,client)}" />
							<br>
						</a>
				   </c:forEach> 
	           </c:otherwise>
	        </c:choose>
			
		</c:when>
		<c:otherwise>
			<mifos:mifoslabel name="Group.noclientsavailable1"  bundle="GroupUIResources"/>  
			<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"/><mifos:mifoslabel name="Group.noclientsavailable2"  bundle="GroupUIResources"/>			
		</c:otherwise>
		</c:choose>
	     </span><br>
	</div>
<table width="50%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><img src="pages/framework/images/trans.gif" width="10" height="10"></td>
  </tr>
</table>
</td>
                </tr>
              </table>
              <table width="96%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td width="36%" class="headingorange"> 
                  	<mifos:mifoslabel name="Group.accountinformation"  bundle="GroupUIResources"></mifos:mifoslabel>
                  </td>
                </tr>
                <!-- bug id 28004. Added if condition to show link to open accounts if only client is active-->
                <c:if test="${requestScope.GroupVO.statusId == 9}">
		            <tr align="right">
		              <td class="headingorange"><span class="fontnormal">
		              
		              <mifos:mifoslabel name="Group.opennewaccount"  bundle="GroupUIResources"></mifos:mifoslabel>
		            
		              &nbsp;
		              <c:if test="${sessionScope.isGroupLoanAllowed == true}">
		              <html-el:link href="loanAccountAction.do?method=getPrdOfferings&customerId=${requestScope.GroupVO.customerId}&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}">
		           	   <mifos:mifoslabel name="${ConfigurationConstants.LOAN}"></mifos:mifoslabel>
		              </html-el:link> &nbsp;|&nbsp; 
		              </c:if>
		              <html-el:link href="savingsAction.do?method=getPrdOfferings&customerId=${requestScope.GroupVO.customerId}&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}">
			              <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"></mifos:mifoslabel>
		              </html-el:link> </span></td>
		            </tr>
                </c:if>
              </table>
              
                           
             <c:if test="${!empty requestScope.CustomerActiveLoanAccounts}">	
				<table width="96%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td width="63%" align="left" valign="top" class="tableContentLightBlue">
                  	<table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td width="63%">
                      	<span class="fontnormalbold">
                      		<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
                      	</span>
                      	<span class="fontnormal"></span>
                      </td>
                    </tr>
                  	</table>                    
                  <span class="fontnormal"></span>
                  <table width="95%" border="0" align="center" cellpadding="0" cellspacing="0">
	                  <c:forEach items="${requestScope.CustomerActiveLoanAccounts}" var="loan">
	                  	<tr>
	                      <td>
	                      	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	                        <tr>
	                          <td width="65%">
	                          	<span class="fontnormal">
	                          		<html-el:link href="loanAccountAction.do?globalAccountNum=${loan.globalAccountNum}&method=get&recordOfficeId=${param.recordOfficeId}&recordLoanOfficerId=${param.recordLoanOfficerId}">
	                          			<c:out value="${loan.loanOffering.prdOfferingName}"/>,Acct #<c:out value="${loan.globalAccountNum}"/>
	                          		</html-el:link>
	                          	</span>
	                          </td>
	                          <td width="35%">
	                          	<span class="fontnormal">
	                          		<mifoscustom:MifosImage id="${loan.accountState.id}" moduleName="accounts.loan" />
	                          		<c:out value="${loan.accountState.name}"/>
	                          	</span>
	                          </td>
	                        </tr>
	                      	</table>
	                      	<c:if test="${loan.accountState.id==5 || loan.accountState.id==9}">
	                  		<span class="fontnormal">
	                  			<mifos:mifoslabel name="loan.outstandingbalance"/>: 
	                  			<c:out value="${loan.loanSummary.oustandingBalance.amountDoubleValue}" /><br>
								<mifos:mifoslabel name="loan.amount_due" />: 
								<c:out value="${loan.totalAmountDue.amountDoubleValue}"/>
							</span>
							</c:if>
						</td>
	                    </tr>
	                    <tr>
	                      <td><img src="pages/framework/images/trans.gif" width="5" height="20"></td>
	                 	</tr>
	                  </c:forEach>
              	  </table>
              </table>
              	
              <table width="50%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td><img src="pages/framework/images/trans.gif" width="10" height="10"></td>
                </tr>
              </table>
              </c:if>
              
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
                  <mifos:mifoslabel name="${ConfigurationConstants.GROUP}"></mifos:mifoslabel>                  
                  <mifos:mifoslabel name="Group.charges"  bundle="GroupUIResources"></mifos:mifoslabel>
                  </span>
                      <table width="95%" border="0" align="center" cellpadding="0" cellspacing="0">
                        <tr>
                          <td><table width="100%" border="0" cellspacing="0" cellpadding="0">
                              <tr>
                                <td width="53%"><span class="fontnormal">
		<html-el:link href="javascript:ViewDetails()">
			<mifos:mifoslabel name="Group.viewdetails"  bundle="GroupUIResources"></mifos:mifoslabel>
		</html-el:link>
                                </span></td>
                              </tr>
                              <tr>
                              <td>
                               <span class="fontnormal"><mifos:mifoslabel name="Group.amountdue"  bundle="GroupUIResources"/>
                              	<c:out value="${requestScope.totalAmountDue}"/>
                              	</span>
                              	</td>
                              </tr>
                            </table>
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
                  <html-el:link href="closedaccsearchaction.do?method=search&searchNode(search_name)=AllClosedAccounts&customerId=${requestScope.GroupVO.customerId}&input=ViewGroupClosedAccounts">
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
            <table width="96%" border="0" cellpadding="3" cellspacing="0">
            <%-- Surveys
              <tr>
                <td width="47%" height="23" class="headingorange">
                <mifos:mifoslabel name="Group.surveys"  bundle="GroupUIResources"></mifos:mifoslabel>
                </td>
                <td width="53%" align="right" class="fontnormal8pt">
                <a href="#">
	                <mifos:mifoslabel name="Group.attachsurvey"  bundle="GroupUIResources"></mifos:mifoslabel>
                </a></td>
              </tr>
              --%>
              <tr>
               
              </tr>
            </table>
            <table width="50%" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td><img src="pages/framework/images/trans.gif" width="10" height="10"></td>
              </tr>
            </table>     
            
            <table width="96%" border="0" cellpadding="0" cellspacing="0">
              
              <tr>
                <td width="63%" height="23" class="headingorange"> 
                	<mifos:mifoslabel name="${ConfigurationConstants.GROUP}"></mifos:mifoslabel>
                	<mifos:mifoslabel name="Group.groupinformation"  bundle="GroupUIResources"></mifos:mifoslabel>
                	</td>
                <td width="37%" align="right" class="fontnormal">
                <html-el:link action="GroupAction.do?method=manage">
                 <mifos:mifoslabel name="Group.edit"  bundle="GroupUIResources"></mifos:mifoslabel>
               	<mifos:mifoslabel name="${ConfigurationConstants.GROUP}"></mifos:mifoslabel>
                 <mifos:mifoslabel name="Group.groupinformation"  bundle="GroupUIResources"></mifos:mifoslabel>
                </html-el:link></td>
              </tr>
              
              <tr>
                <td colspan="2" class="fontnormalbold">  
                <span class="fontnormal">
               	<mifos:mifoslabel name="${ConfigurationConstants.GROUP}"></mifos:mifoslabel>  
                <mifos:mifoslabel name="Group.approvaldate" bundle="GroupUIResources"></mifos:mifoslabel>
                <c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.GroupVO.customerActivationDate)}" />
               <%-- <c:out value="${requestScope.GroupVO.customerActivationDate}"/> --%>
               <br>
               </td>
               </tr>
               
               <tr id="Group.ExternalId">
                <td class="fontnormalbold">  
                <span class="fontnormal">                 
                  <mifos:mifoslabel name="${ConfigurationConstants.EXTERNALID}" isColonRequired="Yes" keyhm="Group.ExternalId" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
                   <c:out value="${requestScope.GroupVO.externalId}"/>
                   <br>
              </td>
               </tr>
                    
                   
                   
                   
                <tr>
                <td class="fontnormalbold">  
                <span class="fontnormal"> 
                   <mifos:mifoslabel name="Group.FormedBy" bundle="GroupUIResources"></mifos:mifoslabel>
	  			   <c:out value="${requestScope.GroupVO.customerFormedByPersonnel.displayName}"/><br>
                   
                   <%-- Collection Sheet
				  <mifos:mifoslabel name="Group.collectionsheettype" bundle="GroupUIResources"></mifos:mifoslabel> Collection Sheet Type 1
				  <c:out value="${requestScope.GroupVO.collectionSheet.collectionSheetName}"/> --%>
				</span>
				<br>
				<span class="fontnormal"></span>
				</td>
				</tr>
				
				<tr id="Group.TrainedDate">
                <td class="fontnormalbold">                  
					<mifos:mifoslabel name="Group.trainingstatus" bundle="GroupUIResources" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>					
					<br><span class="fontnormal">
					<mifos:mifoslabel name="Group.trainedon" bundle="GroupUIResources" keyhm="Group.TrainedDate" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
								<c:if test="${requestScope.GroupVO.trained == 1}">
									<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.GroupVO.trainedDate)}" />
									<%--<c:out value="${requestScope.GroupVO.trainedDate}"/>--%>
					            </c:if>
					</span><br>
					<br>
					<%--  programs
					<mifos:mifoslabel name="Group.programs" bundle="GroupUIResources"></mifos:mifoslabel>
						<span class="fontnormal"><span class="fontnormal"><br>
								<c:forEach var="program" items="${requestScope.customerPrograms}">
									<c:out value="${program.programName}"/><br>
								</c:forEach> 
					 </span></span><br>
					<br> --%>
				</td>
				</tr>
				
				<tr>
                <td class="fontnormalbold">                  
					<mifos:mifoslabel name="Group.officialtitlesassigned" bundle="GroupUIResources"></mifos:mifoslabel>
					<span class="fontnormal"><br>
					</span> 
					<span class="fontnormal">
							<%-- <c:forEach var="pos" items="${requestScope.positions}">
								<c:forEach var="custpos" items="${requestScope.GroupVO.customerPositions}">
									<c:if test="${pos.positionId==custpos.positionId}">
										<c:if test="${! empty custpos.customerId}">
											<c:out value="${pos.positionName}"/>: <c:out value="${custpos.customerName}"/><br>
										</c:if>
									</c:if>
								</c:forEach> 
							</c:forEach>--%>
							<c:forEach var="pos" items="${requestScope.customerPositions}">
								<c:if test="${! empty pos.customerId}">
									<c:out value="${pos.positionName}"/>: <c:out value="${pos.customerName}"/><br>
								</c:if>
							</c:forEach>
					</span><br>
				</td>
				</tr>
				
				
				<%--Address --%>
				<tr id="Group.Address">
                <td class="fontnormalbold">                  
				<mifos:mifoslabel name="Group.address"  bundle="GroupUIResources" keyhm="Group.Address" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
					<br>
					<c:if test="${!empty requestScope.GroupVO.displayAddress}">
						<span class="fontnormal">
							<c:out value="${requestScope.GroupVO.displayAddress}"/><br>
						</span>
					</c:if>
					
					<c:if test="${!empty requestScope.GroupVO.customerAddressDetail.city}">
						<span class="fontnormal">
							<c:out value="${requestScope.GroupVO.customerAddressDetail.city}"/><br>
						</span>
					</c:if>
					<c:if test="${!empty requestScope.GroupVO.customerAddressDetail.state}">
						<span class="fontnormal">
							<c:out value="${requestScope.GroupVO.customerAddressDetail.state}"/><br>
						</span>
					</c:if>
					<c:if test="${!empty requestScope.GroupVO.customerAddressDetail.country}">
						<span class="fontnormal">
							<c:out value="${requestScope.GroupVO.customerAddressDetail.country}"/>
							<br>
						</span>
					</c:if>
				
					<c:if test="${!empty requestScope.GroupVO.customerAddressDetail.zip}">
					<span class="fontnormal">
					<c:out value="${requestScope.GroupVO.customerAddressDetail.zip}"/><br>
					</span>
					</c:if>
		</td>
		</tr>
		
		<tr id="Group.PhoneNumber">
            <td class="fontnormalbold">  
            <span class="fontnormal">
				<c:if test="${!empty requestScope.GroupVO.customerAddressDetail.phoneNumber}">
					<br><span class="fontnormal">
					<mifos:mifoslabel name="Group.telephone"  bundle="GroupUIResources" keyhm="Group.PhoneNumber" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
							
							<c:out value="${requestScope.GroupVO.customerAddressDetail.phoneNumber}"/>
						</span><br>
				</c:if>    
				<br> 
		</td>
		</tr>
		      
	<%--Additional information custom fields--%>  
	<tr>
        <td class="fontnormalbold">          
		  <mifos:mifoslabel name="Group.additionalinformation" bundle="GroupUIResources"></mifos:mifoslabel>
		  <span class="fontnormal"><br>
		       <c:forEach var="cfdef" items="${requestScope.customFields}">
					<c:forEach var="cf" items="${requestScope.GroupVO.customFieldSet}">
						<c:if test="${cfdef.fieldId==cf.fieldId and !empty cf.fieldValue}">
							<c:choose>
								<c:when test="${cfdef.fieldType == 3}"> 
									<mifos:mifoslabel name="${cfdef.lookUpEntity.entityType}" bundle="CenterUIResources"></mifos:mifoslabel>: 
							    	<span class="fontnormal"><c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,cf.fieldValue)}" />
							    </c:when>
								<c:otherwise>
									<mifos:mifoslabel name="${cfdef.lookUpEntity.entityType}" bundle="CenterUIResources"></mifos:mifoslabel>: 
							    	<span class="fontnormal"><c:out value="${cf.fieldValue}"/>
						        </c:otherwise>
					        </c:choose><br>
						</c:if>
					</c:forEach>
		  		</c:forEach> 
		                <br></span>
		  </td>
		  </tr>
		  
		  <tr>
                <td colspan="2" class="fontnormal">  
                <span class="fontnormal">
		                
		                
<!-- Dynamic links -->
 <c:choose>
  	<c:when test="${sessionScope.CenterHierarchyExist eq 'Yes'}"> 
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	  <tr>
    	<td width="59%" class="fontnormalbold">
	    <mifos:mifoslabel name="Group.centermembership&meetingdetails" bundle="GroupUIResources"></mifos:mifoslabel>
	    <mifos:mifoslabel name="${ConfigurationConstants.CENTER}"></mifos:mifoslabel>  
	    <mifos:mifoslabel name="Group.membership" bundle="GroupUIResources"></mifos:mifoslabel>	    	    
    	<span class="fontnormalRed"><br>
    	<mifos:mifoslabel name="Group.meetings" bundle="GroupUIResources"/>&nbsp;
    	<c:out value="${requestScope.GroupVO.customerMeeting.meeting.meetingSchedule}"/><br>
	    </span>
	    <span class="fontnormal">
	        <c:out value="${requestScope.GroupVO.customerMeeting.meeting.meetingPlace}"/><br>
	        <c:out value="${sessionScope.linkValues.customerParentName}"/><br>
	    </span>
	      
        </td>
	    <td width="41%" align="right" valign="top" class="fontnormal">
	    	<a href="GroupAction.do?method=loadParentTransfer">
				<mifos:mifoslabel name="Group.edit" bundle="GroupUIResources"></mifos:mifoslabel>	    	
				<mifos:mifoslabel name="${ConfigurationConstants.CENTER}">  </mifos:mifoslabel>    				
	    		<mifos:mifoslabel name="Group.membership" bundle="GroupUIResources"></mifos:mifoslabel>
	    	</a>
		</td>
		  </tr>
	</table>
 </c:when> 
	<c:otherwise>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
	 	 <tr>
    		<td width="59%" class="fontnormalbold">
	  	  <mifos:mifoslabel name="Group.meetingdetails"  bundle="GroupUIResources"></mifos:mifoslabel>
    		<span class="fontnormalRed"><br>
		   	<mifos:mifoslabel name="Group.meetings" bundle="GroupUIResources"/>
	    	     <c:out value="${requestScope.GroupVO.customerMeeting.meeting.meetingSchedule}"/>
  	       </span>
	       <br>	 
	       	<span class="fontnormal">
		       	<c:out value="${requestScope.GroupVO.customerMeeting.meeting.meetingPlace}"/>
	    	</span>
	    	</td>
	    	<td width="41%" align="right" valign="top" class="fontnormal"><br>
		    	<c:choose>
				  <c:when test="${ empty requestScope.GroupVO.customerMeeting.meeting}">
				  		<%-- Meeting for group has not been created yet, therefore create new meeting--%>
			          	<html-el:link action="MeetingAction.do?method=loadMeeting&input=GroupDetails&customerId=${requestScope.GroupVO.customerId}">
	                     	<mifos:mifoslabel name="Group.editmeetingchedule" bundle="GroupUIResources"/><br>
	             		</html-el:link>		
	               </c:when>
				   <c:otherwise>
				  		<%-- Meeting for group is already  created , therefore edit existing meeting--%>				   
						<html-el:link action="MeetingAction.do?method=get&input=GroupDetails&meetingId=${requestScope.GroupVO.customerMeeting.meeting.meetingId}">
			           		<mifos:mifoslabel name="Group.editmeetingchedule" bundle="GroupUIResources"/><br>
			     		</html-el:link>			          
                  </c:otherwise>
          	   </c:choose>
          	   
    		  <a href="GroupAction.do?method=loadTransfer">
    		  	<mifos:mifoslabel name="Group.editOfficeMembership" bundle="GroupUIResources"/>
    		  </a>
    		</td>
		</tr>
		</table>
	
	</c:otherwise>
  </c:choose>	
  
<br>
                
            <%--Historical data link--%>
                <span class="fontnormal">  

                <a href="CustomerHistoricalDataAction.do?method=get&input=Group">
                <mifos:mifoslabel name="Group.viewhistoricaldata" bundle="GroupUIResources"></mifos:mifoslabel>
                </a>
            
                <br>
<html-el:link href="closedaccsearchaction.do?method=search&searchNode(search_name)=ChangeLogDetails&input=ViewGroupChangelog&customerId=${requestScope.GroupVO.customerId}&entityTypeId=12&createdDate=${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.GroupVO.createdDate)}">
	<mifos:mifoslabel name="Group.viewchangelog" bundle="GroupUIResources"></mifos:mifoslabel>
</html-el:link>
                 </span></td>
              </tr>
            </table>   
            
            </td>
            <td width="30%" align="left" valign="top" class="paddingleft1"><table width="100%" border="0" cellpadding="2" cellspacing="0" class="bluetableborder">
              <tr>
                <td class="bluetablehead05"><span class="fontnormalbold">
                   <mifos:mifoslabel name="Group.performancehistory" bundle="GroupUIResources"></mifos:mifoslabel>
                </span></td>
              </tr>
              
              <tr>
                <td class="paddingL10"><img src="pages/framework/images/trans.gif" width="10" height="2"></td>
              </tr>
              <tr>
					<td class="paddingL10"><span class="fontnormal8pt"> <mifos:mifoslabel
						name="Group.hashof" bundle="GroupUIResources" /> <mifos:mifoslabel
						name="${ConfigurationConstants.CLIENT}" /><mifos:mifoslabel
						name="Group.s" bundle="GroupUIResources"/> <c:out
						value="${sessionScope.GroupPerformanceHistory.activeClientCount}" /> </span>
					</td>
				</tr>
				<tr>
					<td class="paddingL10"><span class="fontnormal8pt"> <mifos:mifoslabel
						name="Group.Amountoflast" bundle="GroupUIResources" /> <mifos:mifoslabel
						name="${ConfigurationConstants.GROUP}" /> <mifos:mifoslabel
						name="${ConfigurationConstants.LOAN}" isColonRequired="yes"/> <c:out
						value="${sessionScope.GroupPerformanceHistory.lastGroupLoanAmount}" /></span></td>
				</tr>
				<tr>
					<td class="paddingL10"><span class="fontnormal8pt"> <mifos:mifoslabel
						name="Group.Avgindividual" bundle="GroupUIResources" /> <mifos:mifoslabel
						name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel
						name="Group.size" bundle="GroupUIResources" /> <c:out
						value="${sessionScope.GroupPerformanceHistory.avgLoanAmountForMember}" /></span></td>
				</tr>
				<tr>
					<td class="paddingL10"><span class="fontnormal8pt"> <mifos:mifoslabel
						name="Group.Total" bundle="GroupUIResources" /> <mifos:mifoslabel
						name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel
						name="Group.portfolio" bundle="GroupUIResources" /> <c:out
						value="${sessionScope.GroupPerformanceHistory.totalOutStandingLoanAmount}" /></span></td>
				</tr>
				<tr>
					<td class="paddingL10"><span class="fontnormal8pt"> <mifos:mifoslabel
						name="Group.PortfolioAtRisk" bundle="GroupUIResources" /> <c:out
						value="${sessionScope.GroupPerformanceHistory.portfolioAtRisk}" /></span></td>
				</tr>
				<tr>
					<td class="paddingL10"><span class="fontnormal8pt"> <mifos:mifoslabel
						name="Group.Total" bundle="GroupUIResources" /> <mifos:mifoslabel
						name="${ConfigurationConstants.SAVINGS}" isColonRequired="yes" />
					<c:out value="${sessionScope.GroupPerformanceHistory.totalSavingsAmount}" /></span></td>
				</tr>
				<c:if test="${sessionScope.isGroupLoanAllowed == true}">
					<tr>
						<td class="paddingL10"><span class="fontnormal8pt"> <mifos:mifoslabel
							name="${ConfigurationConstants.LOAN}"></mifos:mifoslabel> <mifos:mifoslabel
							name="label.loancyclecounter" bundle="CustomerUIResources"></mifos:mifoslabel>
						-</span></td>
					</tr>

					<c:forEach
						items='${sessionScope.GroupAction_Context.businessResults["loanCycleCounter"]}'
						var="loanCycleCounter">
						<tr>
							<td class="paddingL10"><span class="fontnormal8pt">&nbsp;&nbsp;&nbsp;<c:out
								value="${loanCycleCounter.offeringName}" />: <c:out
								value="${loanCycleCounter.counter}" /></span></td>
						</tr>
					</c:forEach>
				</c:if>
            </table>
              <table width="95%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td><img src="pages/framework/images/trans.gif" width="7" height="8"></td>
                  </tr>
              </table>                
                <table width="100%" border="0" cellpadding="2" cellspacing="0" class="bluetableborder">
                  <tr>
                    <td class="bluetablehead05">
                    <span class="fontnormalbold">
                    	 <mifos:mifoslabel name="Group.recentnotes" bundle="GroupUIResources"></mifos:mifoslabel>
 					</span></td>
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
	                    		<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,note.commentDate)}"/>:
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
	                    <a href="CustomerNoteAction.do?method=get&input=Group&security_param=Group">
	                    	<mifos:mifoslabel name="Group.seeallnotes" bundle="GroupUIResources"></mifos:mifoslabel>
	                    </a><br>
	                </c:if>
            	    <a href="CustomerNoteAction.do?method=load&input=Group&security_param=Group">
			             <mifos:mifoslabel name="Group.addanote" bundle="GroupUIResources"></mifos:mifoslabel>
            	    </a>
                    </span></td>
                  </tr>
              </table>
            </td>
          </tr>
        </table>        
      <br>
<br> 
<mifos:SecurityParam property="Group" />
<html-el:hidden property="customerId" value="${requestScope.GroupVO.customerId}"/> 
<html-el:hidden property="globalCustNum" value="${requestScope.GroupVO.globalCustNum}"/>
<html-el:hidden property="globalAccountNum" value="${requestScope.GroupVO.customerAccount.globalAccountNum}"/> 
<html-el:hidden property="versionNo" value="${requestScope.GroupVO.versionNo}"/> 
<html-el:hidden property="displayName" value="${requestScope.GroupVO.displayName}"/> 
</html-el:form>
<html-el:form  action="closedaccsearchaction.do?method=search">
<html-el:hidden property="searchNode(search_name)" value="ClientChargesDetails"/>
<html-el:hidden property="prdOfferingName" value="${requestScope.GroupVO.displayName}"/> 
<html-el:hidden property="globalAccountNum" value="${requestScope.GroupVO.customerAccount.globalAccountNum}"/> 
<html-el:hidden property="accountId" value="${requestScope.GroupVO.customerAccount.accountId}"/> 
<html-el:hidden property="accountType" value="${requestScope.GroupVO.customerAccount.accountTypeId}"/> 
<html-el:hidden property="customerId" value="${requestScope.GroupVO.customerId}"/> 
<html-el:hidden property="input" value="ViewGroupCharges"/> 
<html-el:hidden property="statusId" value="${requestScope.GroupVO.statusId}"/> 
</html-el:form>
</tiles:put>
</tiles:insert>

