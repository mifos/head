<!-- 

/**

 * viewclientchargesdetails.jsp    version: 1.0

 

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

    <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td class="bluetablehead05">
	            <span class="fontnormal8pt">
	            	<a href="CustomerSearchAction.do?method=getOfficeHomePage&officeId=<c:out value="${sessionScope.linkValues.customerOfficeId}"/>&officeName=<c:out value="${sessionScope.linkValues.customerOfficeName}"/>&loanOfficerId=<c:out value="${requestScope.Context.userContext.id}"/>">
	            	<c:out value="${sessionScope.linkValues.customerOfficeName}"/></a>   /
	            </span>
	            <!-- Name of the client -->
	            <span class="fontnormal8pt">
	            <a href="centerAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.globalCustNum}"/>">
	           				<c:out value="${sessionScope.linkValues.customerName}"/>            	
           			</a>/
	            </span>
	            <!-- Center Charges -->
	            <span class="fontnormal8ptbold">
	            
	            <mifos:mifoslabel name="${ConfigurationConstants.CENTER}"/>
	                 <mifos:mifoslabel name="Center.Charges" bundle="CenterUIResources"/>
	            </span>
	         </td>
          </tr>
        </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="70%" height="24" align="left" valign="top" class="paddingL15T15"><table width="96%" border="0" cellpadding="3" cellspacing="0">
              <font class="fontnormalRedBold"><html-el:errors bundle="CenterUIResources" /></font>
              <tr>
                <td width="70%" class="headingorange">
				<mifos:mifoslabel name="${ConfigurationConstants.CENTER}"/>
	                 <mifos:mifoslabel name="Center.Charges" bundle="CenterUIResources"/>
                </td>
                </tr>
              <tr>
                <td align="right" class="headingorange"><img src="images/trans.gif" width="10" height="5"></td>
              </tr>
            </table>
            <c:if test="${param.statusId == 13}">
	           <table width="96%" border="0" cellpadding="0" cellspacing="0">
	              <tr>
	                <td bgcolor="#F0D4A5" style="padding-left:10px; padding-bottom:3px;">
	                	<span class="fontnormalbold">
	                	<mifos:mifoslabel name="Center.ApplyTransaction" bundle="CenterUIResources"/></span>
	                	&nbsp;&nbsp;&nbsp;&nbsp;
	                	<html-el:link href="applyPaymentAction.do?method=load&searchInput=ClientChargesDetails&statusId=${param.statusId}&globalCustNum=${sessionScope.linkValues.globalCustNum}&prdOfferingName=${sessionScope.linkValues.customerName}&input=ViewCenterCharges&globalAccountNum=${param.globalAccountNum}&accountType=${param.accountType}&accountId=${param.accountId}&securityParamInput=Center">
	                    	<mifos:mifoslabel name="accounts.apply_payment" />
	                    </html-el:link>
	                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    <html-el:link href="custApplyAdjustment.do?method=loadAdjustment&statusId=${param.statusId}&globalCustNum=${sessionScope.linkValues.globalCustNum}&prdOfferingName=${sessionScope.linkValues.customerName}&input=ViewCenterCharges&globalAccountNum=${param.globalAccountNum}&accountType=${param.accountType}&accountId=${param.accountId}&securityParamInput=Center">
	                    	<mifos:mifoslabel name="Center.ApplyAdjustment" bundle="CenterUIResources"/>
	                    </html-el:link>
	                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    <html-el:link href="AccountsApplyChargesAction.do?method=load&statusId=${param.statusId}&globalCustNum=${sessionScope.linkValues.globalCustNum}&prdOfferingName=${sessionScope.linkValues.customerName}&input=ViewCenterCharges&globalAccountNum=${param.globalAccountNum}&accountType=${param.accountType}&accountId=${param.accountId}">
	                    	<mifos:mifoslabel name="Center.ApplyCharges" />
						</html-el:link>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	              </tr>
	            </table> 
	            <br>
	         </c:if>
            <table width="96%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td width="66%" class="headingorange">
					<mifos:mifoslabel name="Center.AccountSummary" bundle="CenterUIResources"/>
                </td>
              </tr>
              <tr>
                <td class="fontnormal">
                	<span class="fontnormal">
						<mifos:mifoslabel name="Center.AmountDue" bundle="CenterUIResources"/>:
						<c:out value='${requestScope.Context.businessResults["ClientFeeChargeDue"].amountDoubleValue}'/>
                    </span>
 					<c:if test='${requestScope.Context.businessResults["ClientFeeChargeDue"].amountDoubleValue != 0.0}'>
                  <html-el:link href="customerAction.do?method=waiveChargeDue&statusId=${param.statusId}&type=Center&accountId=${param.accountId}&globalAccountNum=${param.globalAccountNum}">
	              	<mifos:mifoslabel name="client.waive" bundle="ClientUIResources"/>
	              </html-el:link>
	              </c:if>
	              <br>
                    <span class="fontnormal">
						<mifos:mifoslabel name="Center.AmountOverdue" bundle="CenterUIResources"/>: 
						<c:out value='${requestScope.Context.businessResults["ClientFeeChargeOverDue"].amountDoubleValue}'/>
					</span>
					<c:if test='${requestScope.Context.businessResults["ClientFeeChargeOverDue"].amountDoubleValue != 0.0}'>
				  <html-el:link href="customerAction.do?method=waiveChargeOverDue&statusId=${param.statusId}&type=Center&accountId=${param.accountId}&globalAccountNum=${param.globalAccountNum}">
					<mifos:mifoslabel name="client.waive" bundle="ClientUIResources"/>
	              </html-el:link>
	              </c:if>
	              <BR>
	              <span class="fontnormalbold">
	               <mifos:mifoslabel name="accounts.total" isColonRequired="Yes"></mifos:mifoslabel>
	               <c:out value='${requestScope.Context.businessResults["ClientFeeChargeOverDue"].amountDoubleValue + requestScope.Context.businessResults["ClientFeeChargeDue"].amountDoubleValue}'></c:out>
	              </span>
				</td>
              </tr>
            </table>
            <br>
            <table width="96%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td width="70%" class="headingorange">
					<mifos:mifoslabel  name="Center.UpcomingCharges" bundle="CenterUIResources"/>
					(<c:out value='${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.Context.businessResults["UpcomingChargesDate"])}' />)
                 </td>
                <td width="70%" align="right" class="fontnormal">
                    <html-el:link href="accountAppAction.do?method=getTrxnHistory&statusId=${param.statusId}&input=ViewCenterCharges&globalAccountNum=${param.globalAccountNum}&accountId=${param.accountId}&accountType=${param.accountType}&prdOfferingName=${sessionScope.linkValues.customerName}&headingInput=ViewCenterCharges&searchInput=ClientChargesDetails">
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
					<mifos:mifoslabel  name="Center.RecentAccountActivity" bundle="CenterUIResources"/>                
                </td>
                <td width="72%" align="right" class="fontnormal">
                <html-el:link href="customerAction.do?method=getAllActivity&statusId=${param.statusId}&type=Center&globalCustNum=${sessionScope.linkValues.globalCustNum}&prdOfferingName=${sessionScope.linkValues.customerName}&input=ViewCenterCharges&globalAccountNum=${param.globalAccountNum}&accountType=${param.accountType}&accountId=${param.accountId}&securityParamInput=Center">
                <mifos:mifoslabel  name="Center.AccountActivity" bundle="CenterUIResources"/> </html-el:link>
                </td>
              </tr>
            </table>
			<mifoscustom:mifostabletag moduleName="customer" scope="request" source="RecentAcctActivityList" xmlFileName="CustomerRecentActivity.xml" passLocale="true"/>            
            <br>
            <table width="96%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td width="35%" class="headingorange">
                <mifos:mifoslabel  name="Center.RecurringAccountFees" bundle="CenterUIResources"/></td>
                </tr>
            </table>
            <table width="96%" border="0" cellpadding="3" cellspacing="0">            
              <c:forEach items="${requestScope.RecurrenceFeesChargesList}" var="recurrenceFees">
             <tr class="fontnormal">
                <td width="15%"><c:out value="${recurrenceFees.feeName}"/>:</td>
                <td width="30%"><c:out value="${recurrenceFees.amount}"/>
                &nbsp;&nbsp;(<c:out value="${recurrenceFees.meeting.simpleMeetingSchedule}"/>)</td>
                <td width="55%">               
               <html-el:link href="accountAppAction.do?method=removeFees&statusId=${param.statusId}&feeId=${recurrenceFees.feeId}&accountId=${recurrenceFees.accountId}&fromPage=center&globalAccountNum=${param.globalAccountNum}"> 
				<mifos:mifoslabel name="Center.remove" bundle="CenterUIResources"/></td>
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
			<html-el:hidden property="headingInput" value="ViewCenterCharges"/>
			<html-el:hidden property="searchInput" value="ClientChargesDetails"/>
			<mifos:SecurityParam property="Center" />
			<html-el:hidden property="statusId" value="${param.statusId}"/>
</tiles:put>
</tiles:insert>      
