<!-- 

/**

 * viewclientchargesdetails.jsp    version: 1.0

 

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

 */

-->

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom" %>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>


<tiles:insert definition=".clientsacclayoutsearchmenu">

 <tiles:put name="body" type="string">
<html-el:hidden property="searchNode(search_name)" value="ClientChargesDetails"/>
    <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td class="bluetablehead05">
	            <span class="fontnormal8pt">
	            	<a href="CustomerSearchAction.do?method=getOfficeHomePage&officeId=<c:out value="${sessionScope.linkValues.customerOfficeId}"/>&officeName=<c:out value="${sessionScope.linkValues.customerOfficeName}"/>&loanOfficerId=<c:out value="${requestScope.Context.userContext.id}"/>">
	            	<c:out value="${sessionScope.linkValues.customerOfficeName}"/></a> /
	            </span>
	            <c:if test="${!empty sessionScope.linkValues.customerCenterName}">
	               <span class="fontnormal8pt">
	               	<a href="centerAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.customerCenterGCNum}"/>">
				       	<c:out value="${sessionScope.linkValues.customerCenterName}"/>
			       	</a>  /  </span>
		    	</c:if>
	           <c:if test="${!empty sessionScope.linkValues.customerParentName}">
	               <span class="fontnormal8pt">
	               	<a href="GroupAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.customerParentGCNum}"/>">
				       	<c:out value="${sessionScope.linkValues.customerParentName}"/>
			       	</a>  /  </span>
		    	</c:if>
	            <!-- Name of the client -->
	            <span class="fontnormal8pt">
	            	<a href="clientCreationAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.globalCustNum}"/>">
				       	<c:out value="${sessionScope.linkValues.customerName}"/>
			       	</a>/
	            	
	            </span>
	            <span class="fontnormal8ptbold">
	            <mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"/>
				<mifos:mifoslabel name="client.clientcharges" bundle="ClientUIResources"/>
				
			</span></td>
          </tr>
        </table>

		
    
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="70%" height="24" align="left" valign="top" class="paddingL15T15"><table width="96%" border="0" cellpadding="3" cellspacing="0">
              <font class="fontnormalRedBold"><html-el:errors bundle="ClientUIResources" /></font>
              <tr>
                <td width="70%" class="headingorange">
                	<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"/>
					<mifos:mifoslabel name="client.clientcharges" bundle="ClientUIResources"/>
                </td>
                </tr>
              <tr>
                <td align="right" class="headingorange"><img src="images/trans.gif" width="10" height="5"></td>
              </tr>
            </table>
            <c:if test="${param.statusId == 1 || param.statusId == 2 || param.statusId == 3 || param.statusId == 4}">
	           <table width="96%" border="0" cellpadding="0" cellspacing="0">
	              <tr>
	                <td bgcolor="#F0D4A5" style="padding-left:10px; padding-bottom:3px;">
	                <span class="fontnormalbold">
	                	<mifos:mifoslabel name="Center.ApplyTransaction" bundle="CenterUIResources"/>
	                </span>
	                <c:if test="${param.statusId == 3 || param.statusId == 4}">
	                &nbsp;&nbsp;&nbsp;&nbsp;
		                <html-el:link href="custApplyAdjustment.do?method=loadAdjustment&statusId=${param.statusId}&globalCustNum=${sessionScope.linkValues.globalCustNum}&prdOfferingName=${sessionScope.linkValues.customerName}&input=ViewClientCharges&globalAccountNum=${param.globalAccountNum}&accountType=${param.accountType}&accountId=${param.accountId}&securityParamInput=Client">
		                	<mifos:mifoslabel name="Customer.applyadjustment" bundle="CustomerUIResources"/>
		                </html-el:link>
		             </c:if>
		             <c:if test="${param.statusId == 1 || param.statusId == 2 || param.statusId == 3 || param.statusId == 4}">
			             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		                <html-el:link href="AccountsApplyChargesAction.do?method=load&statusId=${param.statusId}&globalCustNum=${sessionScope.linkValues.globalCustNum}&prdOfferingName=${sessionScope.linkValues.customerName}&input=ViewClientCharges&globalAccountNum=${param.globalAccountNum}&accountType=${param.accountType}&accountId=${param.accountId}">
		                	<mifos:mifoslabel name="Customer.apply_charges" />
		                </html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                </c:if>
	                </td>
	              </tr>
	            </table>
            	<br>
            </c:if>
            <table width="96%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td width="66%" class="headingorange">
<mifos:mifoslabel name="client.accsum" bundle="ClientUIResources"/>
                </td>
              </tr>
              <tr>
                <td class="fontnormal"><span class="fontnormalbold">
<mifos:mifoslabel name="client.amtdue" bundle="ClientUIResources"/>:
<c:out value='${requestScope.Context.businessResults["ClientFeeChargeDue"]}'/>
                  </span>
                  <html-el:link href="customerAction.do?method=waiveChargeDue&statusId=${param.statusId}&type=Client&accountId=${param.accountId}&globalAccountNum=${param.globalAccountNum}">
	              	<mifos:mifoslabel name="client.waive" bundle="ClientUIResources"/>
	              </html-el:link>
	              <br>
                          <span class="fontnormalbold">
<mifos:mifoslabel name="client.amtoverdue" bundle="ClientUIResources"/>: 
<c:out value='${requestScope.Context.businessResults["ClientFeeChargeOverDue"]}'/>
						</span>
				  <html-el:link href="customerAction.do?method=waiveChargeOverDue&statusId=${param.statusId}&type=Client&accountId=${param.accountId}&globalAccountNum=${param.globalAccountNum}">
	              	<mifos:mifoslabel name="client.waive" bundle="ClientUIResources"/>
	              </html-el:link></td>
              </tr>
            </table>
            <br>
            <table width="96%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td width="70%" class="headingorange">
<mifos:mifoslabel  name="client.upcomcharges" bundle="ClientUIResources"/>
(<c:out value='${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.Context.businessResults["UpcomingChargesDate"])}' />)
                 </td>
                <td width="70%" align="right" class="fontnormal">
                    <html-el:link href="accountAppAction.do?method=getTrxnHistory&statusId=${param.statusId}&input=ViewClientCharges&globalAccountNum=${param.globalAccountNum}&accountId=${param.accountId}&accountType=${param.accountType}&prdOfferingName=${param.prdOfferingName}&headingInput=ViewClientCharges&searchInput=ClientChargesDetails">
                    	<mifos:mifoslabel name="Center.TransactionHistory" />
                    	</html-el:link>
                
				</td>
              </tr>
            </table>
<mifoscustom:mifostabletag moduleName="customer/client" scope="request" source="ClientUpcomingFeeChargesList" xmlFileName="ClientUpcomingFeeChargesList.xml"/>                        
           <br>
            <table width="96%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td width="28%" class="headingorange">
				<mifos:mifoslabel  name="client.recaccact" bundle="ClientUIResources"/>                
                </td>
                <td width="72%" align="right" class="fontnormal">
                <html-el:link href="customerAction.do?method=getAllActivity&statusId=${param.statusId}&type=Client&globalCustNum=${sessionScope.linkValues.globalCustNum}&prdOfferingName=${sessionScope.linkValues.customerName}&input=ViewClientCharges&globalAccountNum=${param.globalAccountNum}&accountType=${param.accountType}&accountId=${param.accountId}&securityParamInput=Client">
                View all account activity</html-el:link>
                
                </td>
              </tr>
            </table>
<mifoscustom:mifostabletag moduleName="customer/client" scope="request" source="RecentAcctActivityList" xmlFileName="ClientRecentActivity.xml" passLocale="true"/>            
            <br>
            <table width="96%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td width="35%" class="headingorange">Recurring Account Fees</td>
                </tr>
            </table>
            <table width="96%" border="0" cellpadding="3" cellspacing="0">
            <c:forEach items="${requestScope.RecurrenceFeesChargesList}" var="recurrenceFees">
             <tr class="fontnormal">
                <td width="15%"><c:out value="${recurrenceFees.feeName}"/>:</td>
                <td width="30%"><c:out value="${recurrenceFees.amount}"/>&nbsp;&nbsp;(<c:out value="${recurrenceFees.meeting.simpleMeetingSchedule}"/>)</td>
                <td width="55%">
                <html-el:link href="accountAppAction.do?method=removeFees&statusId=${param.statusId}&feeId=${recurrenceFees.feeId}&accountId=${recurrenceFees.accountId}&fromPage=client&globalAccountNum=${param.globalAccountNum}"> 
                <mifos:mifoslabel name="client.remove" bundle="ClientUIResources"/>                  
                </html-el:link>
                </td>
              </tr>
            </c:forEach>
            </table><br></td>
        </tr>
      </table> 
      		<html-el:hidden property="searchNode(search_name)" value="ClientChargesDetails"/>  
			<html-el:hidden property="globalAccountNum" value="${param.globalAccountNum}" />
			<html-el:hidden property="accountId" value="${param.accountId}" />
			<html-el:hidden property="accountType" value="${param.accountType}" /> 
			<html-el:hidden property="prdOfferingName" value="${param.prdOfferingName}"/>
			<html-el:hidden property="headingInput" value="ViewClientCharges"/>
			<html-el:hidden property="searchInput" value="ClientChargesDetails"/>
			<html-el:hidden property="statusId" value="${param.statusId}"/>
			<mifos:SecurityParam property="Client" />
</tiles:put>
</tiles:insert>      