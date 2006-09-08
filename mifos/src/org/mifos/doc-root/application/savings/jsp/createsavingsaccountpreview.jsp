<!--
 
 * createsavingsaccountpreview.jsp  version: 1.0
 
 
 
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
 
 -->

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>

<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
	<html-el:form method="post" action="/savingsAction.do?method=create" >

<SCRIPT SRC="pages/application/savings/js/CreateSavingsAccount.js"></SCRIPT>
<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td height="470" align="left" valign="top" bgcolor="#FFFFFF">
    <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <td align="center" class="heading">&nbsp;</td>
      </tr>
    </table>      
      <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <td class="bluetablehead">  <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td width="33%"><table border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td><img src="pages/framework/images/timeline/tick.gif" width="17" height="17"></td>
                    <td class="timelineboldgray"><mifos:mifoslabel name="Savings.Select"/><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"/></td>
                  </tr>
                </table>
              </td>
              <td width="34%" align="center">
                <table border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td><img src="pages/framework/images/timeline/tick.gif" width="17" height="17"></td>
                    <td class="timelineboldgray">
                    <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
                    <mifos:mifoslabel name="Savings.accountInformation"/></td>
                  </tr>
                </table></td>
              <td width="33%" align="right">
                <table border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td><img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17"></td>
                    <td class="timelineboldorange"><mifos:mifoslabel name="Savings.review&Submit"/>t</td>
                  </tr>
                </table></td>
            </tr>
          </table></td>
      </tr>
    </table>
      <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
        <tr>
          <td width="70%" height="24" align="left" valign="top" class="paddingleftCreates">
          <table width="98%" border="0" cellspacing="0" cellpadding="3">
              <tr>
                <td class="headingorange"><span class="heading">
               		<mifos:mifoslabel name="Savings.Create"/>
	                <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
	                <mifos:mifoslabel name="Savings.account"/> - </span>
	                <mifos:mifoslabel name="Savings.Preview"/>
	                <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
	                <mifos:mifoslabel name="Savings.accountInformation"/></td>
              </tr>
              <tr>
                <td class="fontnormal">
                <mifos:mifoslabel name="Savings.reviewInformation"/>
                <mifos:mifoslabel name="Savings.clickSubmit"/>
                <mifos:mifoslabel name="Savings.clickCancel"/></td>
              </tr>
              <tr>
                <td class="fontnormal">
                <font class="fontnormalRedBold"><html-el:errors	bundle="SavingsUIResources" /></font>
                <br>
                  <span class="fontnormalbold">
                  	<mifos:mifoslabel name="Savings.accountOwner"/>: 
                  	</span><c:out value="${sessionScope.BusinessKey.customer.displayName}" /></td>
              </tr>
            </table>
              
              <table width="93%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td width="100%" height="23" class="fontnormalboldorange">
				<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
				<mifos:mifoslabel name="Savings.accountInformation"/>
</td>
                </tr>
                <tr>
                  <td height="23" class="fontnormal"> 
                     <span class="fontnormalbold">
                     <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
                     <mifos:mifoslabel name="Savings.instanceName"/>:</span>
                     <span class="fontnormal">
                     	<c:out value="${sessionScope.BusinessKey.savingsOffering.prdOfferingName}"/><br><br>
                     </span>
                     <span class="fontnormalbold">
                     	<mifos:mifoslabel name="Savings.instanceInformation"/>
                     </span>
                     <br>
                      <mifos:mifoslabel name="Savings.description"/>: 
                      <c:out value="${sessionScope.BusinessKey.savingsOffering.description}"/><br> <br>                    
                      <span class="fontnormalbold">
                        <mifos:mifoslabel name="Savings.typeOfDeposits"/>:
                      </span>
                      <span class="fontnormal">
	                      <customtags:lookUpValue	id="${sessionScope.BusinessKey.savingsOffering.savingsType.id}"
							searchResultName="SavingsType" mapToSeperateMasterTable="true">
						  </customtags:lookUpValue>
                      </span><br>
                      <span class="fontnormalbold">
                        <mifos:mifoslabel name="Savings.maxAmountPerWithdrawl"/>: 
                      </span><c:out value="${sessionScope.BusinessKey.savingsOffering.maxAmntWithdrawl}"/><br>                        
                      <span class="fontnormalbold">
                        <mifos:mifoslabel name="Savings.balanceUsedFor"/>
		                <mifos:mifoslabel name="${ConfigurationConstants.INTEREST}"/>
		                <mifos:mifoslabel name="Savings.rateCalculation"/>:
                      </span> 
		                  <customtags:lookUpValue	id="${sessionScope.BusinessKey.savingsOffering.interestCalcType.id}"
							searchResultName="IntCalTypes" mapToSeperateMasterTable="true">
						  </customtags:lookUpValue>
                       <br>
                      <span class="fontnormalbold">
                        <mifos:mifoslabel name="Savings.timePeriodFor"/>
                  		<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}"/>
                  		<mifos:mifoslabel name="Savings.rateCalculation"/>: 
                      </span>
                      	<c:out value="${sessionScope.BusinessKey.savingsOffering.timePerForInstcalc.meeting.meetingDetails.recurAfter}"/>
                  	  	<c:if test="${sessionScope.BusinessKey.savingsOffering.timePerForInstcalc.meeting.meetingDetails.recurrenceType.recurrenceId == 2}">
                  	  		<mifos:mifoslabel name="meeting.labelMonths" bundle="MeetingResources"/>
                  	  	</c:if>
                  	  	<c:if test="${sessionScope.BusinessKey.savingsOffering.timePerForInstcalc.meeting.meetingDetails.recurrenceType.recurrenceId == 3}">
	                  	  	<mifos:mifoslabel name="meeting.labelDays" bundle="MeetingResources"/>
                  	  	</c:if><br>
                      <span class="fontnormalbold">
                        <mifos:mifoslabel name="Savings.frequencyOf"/>
                 		<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}"/>
                 		<mifos:mifoslabel name="Savings.postingToAccounts"/>: 
                      </span>
                        <c:out value="${sessionScope.BusinessKey.savingsOffering.freqOfPostIntcalc.meeting.meetingDetails.recurAfter}"/>
                  	    <c:if test="${sessionScope.BusinessKey.savingsOffering.freqOfPostIntcalc.meeting.meetingDetails.recurrenceType.recurrenceId == 2}">
                  	  		<mifos:mifoslabel name="meeting.labelMonths" bundle="MeetingResources"/>
						</c:if> <br>
                      <span class="fontnormalbold">
                        <mifos:mifoslabel name="Savings.minBalanceRequired"/>
                  		<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}"/>
                 		<mifos:mifoslabel name="Savings.rateCalculation"/>: 
                      </span><c:out value="${sessionScope.BusinessKey.savingsOffering.minAmntForInt}" /><br>
                      <span class="fontnormalbold">
                        <mifos:mifoslabel name="${ConfigurationConstants.INTEREST}"/>
                  		<mifos:mifoslabel name="Savings.rate"/>:
                      </span>
                      <span class="fontnormal">
	                        <c:out value="${sessionScope.BusinessKey.savingsOffering.interestRate}" /> <mifos:mifoslabel name="Savings.perc"/>
                      </span><br><br>
                   
                    <span class="fontnormalbold">
                    <c:choose>
	                  <c:when test="${sessionScope.BusinessKey.savingsOffering.savingsType.id == SavingsConstants.SAVINGS_MANDATORY}">
	                  	<mifos:mifoslabel name="Savings.mandatoryAmountForDeposit"/>: 
	                  </c:when>
	                  <c:otherwise>
	                  <mifos:mifoslabel name="Savings.recommendedAmountForDeposit"/>: 
	                  </c:otherwise>
	                </c:choose>
                    </span>
                    <span class="fontnormal"> <c:out value="${sessionScope.BusinessKey.recommendedAmount.amountDoubleValue}"/>                    
                    <c:choose>
	                    <c:when test="${sessionScope.BusinessKey.customer.customerLevel.id==CustomerConstants.GROUP_LEVEL_ID}">
	                    (<customtags:lookUpValue	id="${sessionScope.BusinessKey.savingsOffering.recommendedAmntUnit.id}"
							searchResultName="RecommendedAmtUnit" mapToSeperateMasterTable="true">
						  </customtags:lookUpValue>)
	                    </c:when>
	                    <c:otherwise>
	                      <customtags:lookUpValue	id="${sessionScope.BusinessKey.savingsOffering.recommendedAmntUnit.id}"
							searchResultName="RecommendedAmtUnit" mapToSeperateMasterTable="true">
						  </customtags:lookUpValue>
	                    </c:otherwise>
                    </c:choose>

                    <br><br>
                    </span>
                  <span class="fontnormalbold">
                    <mifos:mifoslabel name="Savings.additionalInformation"/><br>
                    <c:forEach var="cfdef" items="${sessionScope.customFields}">
						<c:forEach var="cf" items="${sessionScope.BusinessKey.accountCustomFields}">
							<c:if test="${cfdef.fieldId==cf.fieldId}">
								<mifos:mifoslabel name="${cfdef.lookUpEntity.entityType}" bundle="GroupUIResources"></mifos:mifoslabel>: 
		        		  	 	<span class="fontnormal">
									<c:out value="${cf.fieldValue}"/>
								</span><br>
							</c:if>
						</c:forEach>
  					</c:forEach>
                  </span>
                    
                    <br>
                    <span class="fontnormal">
                    <html-el:button property="editButton" styleClass="insidebuttn" style="width:220px;"	onclick="fnCreateEdit(this.form)">
                   		<mifos:mifoslabel name="Savings.Edit"/>&nbsp;<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>&nbsp;<mifos:mifoslabel name="Savings.accountInformation"/>
					</html-el:button> 
     			 </span> </td>
                </tr>
              </table>
              <table width="93%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center" class="blueline">&nbsp;</td>
                </tr>
              </table>
              <br>

              <table width="93%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center">
                  <html-el:button property="saveForLaterButton" styleClass="buttn"	style="width:100px;" onclick="javascript:setAccountState(this.form,13)">
						<mifos:mifoslabel name="loan.saveForLater" />
				 </html-el:button>
&nbsp;
      		<c:choose >
				<c:when test="${sessionScope.isPendingApproval == true}">
					<html-el:button
						property="approvedButton" styleClass="buttn"
						style="width:130px;" onclick="javascript:setAccountState(this.form,14)">
						<mifos:mifoslabel name="loan.submitForApproval" />
					</html-el:button>
				</c:when>
				<c:otherwise>
					<html-el:button
						property="approvedButton" styleClass="buttn"
						style="width:130px;" onclick="javascript:setAccountState(this.form,16)">
						<mifos:mifoslabel name="loan.approved" />
					</html-el:button>
				</c:otherwise>
			</c:choose>
&nbsp;
      
				<html-el:button property="cancelButton" styleClass="cancelbuttn" style="width:70px;" onclick="javascript:fun_createCancel(this.form)">
						<mifos:mifoslabel name="loan.cancel" />
				</html-el:button>
                  </td>
                </tr>
              </table>
              <br>
          </td>
        </tr>
      </table>
      <html-el:hidden  property="stateSelected"/>
      </html-el:form>
</tiles:put>
</tiles:insert>
