<!--
 
 * continuecreatesavingsaccount.jsp  version: 1.0
 
 
 
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

<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
	<script src="pages/framework/js/conversion.js"></script>
	<script src="pages/framework/js/con_en.js"></script>
	<script src="pages/framework/js/con_${sessionScope["UserContext"].currentLocale}.js"></script>
	<SCRIPT SRC="pages/application/savings/js/CreateSavingsAccount.js"></SCRIPT>
	<html-el:form method="post" action="/savingsAction.do?method=preview" >
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
                <td class="timelineboldgray"><mifos:mifoslabel name="Savings.Select"/><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"/></td>
              </tr>
            </table></td>
            <td width="34%" align="center">
			  <table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td><img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17"></td>
                <td class="timelineboldorange">
                    <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
                    <mifos:mifoslabel name="Savings.accountInformation"/></td>
              </tr>
            </table>
			</td>
            <td width="33%" align="right">
			  <table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td><img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17"></td>
                <td class="timelineboldorangelight"><mifos:mifoslabel name="Savings.review&Submit"/></td>
              </tr>
            </table>
			</td>
            </tr>
        </table>
        </td>
      </tr>
    </table>
      <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
        <tr>
          <td width="70%" height="24" align="left" valign="top" class="paddingleftCreates">
          <table width="98%" border="0" cellspacing="0" cellpadding="3">
              <tr>
                <td class="headingorange">
                <span class="heading">
                	<mifos:mifoslabel name="Savings.Create"/>
	                <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
	                <mifos:mifoslabel name="Savings.account"/> - </span>
	                <mifos:mifoslabel name="Savings.Enter"/>
	                <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
	                <mifos:mifoslabel name="Savings.accountInformation"/></td>
              </tr>
              <tr>
                <td class="fontnormal"> 
                <mifos:mifoslabel name="Savings.completeTheFieldsBelow"/>
                <mifos:mifoslabel name="Savings.clickContinue"/>
				<mifos:mifoslabel name="Savings.clickCancel"/>
                <br><mifos:mifoslabel name="Savings.fieldsRequired" mandatory="yes"/>
                </td>
              </tr>
              <tr>
    	          <td>
	        	      <font class="fontnormalRedBold"><html-el:errors	bundle="SavingsUIResources" /></font>
        	      </td>
              </tr>
              <tr>
                <td class="fontnormal">
                <br>
                  <span class="fontnormalbold">
                  <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'client')}" var="client" />
                  <mifos:mifoslabel name="Savings.accountOwner" isColonRequired="yes"/></span>
                  <c:out value="${client.displayName}" />
                 </td>
              </tr>
            </table>
            
              <br>
              <table width="93%" border="0" cellpadding="3" cellspacing="0">
                <tr class="fontnormal">
                  <td width="35%" align="right" class="fontnormal">
                  <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" mandatory="yes"/>
                  <mifos:mifoslabel name="Savings.instanceName" isColonRequired="yes"/></td>
                  <td width="65%">
                  <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'prd')}" var="savingsOffering" />
                  	<mifos:select name="savingsActionForm" property="selectedPrdOfferingId" onchange="javascript:fun_refresh(this.form)">
						<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'savingsPrdOfferings')}" 
											var="savingsPrdOfferings">
											<html-el:option value="${savingsPrdOfferings.prdOfferingId}">${savingsPrdOfferings.prdOfferingName}</html-el:option>
					</c:forEach>
					</mifos:select>
                  </td>
                </tr>
              </table>
              
              <table width="93%" border="0" cellpadding="3" cellspacing="0">
                <tr class="fontnormal">
                  <td colspan="2" valign="top" class="fontnormalbold">
                  <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
                  <mifos:mifoslabel name="Savings.productSummary"/><br>
                  <br></td>
                </tr>
                <tr class="fontnormal">
                  <td align="right" valign="top">
                  	<mifos:mifoslabel name="Savings.description" isColonRequired="yes"/>
                  </td>
                  
                  <td valign="top">
                  	<c:out value="${savingsOffering.description}" />
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td align="right"><mifos:mifoslabel name="Savings.typeOfDeposits" isColonRequired="yes"/></td>
                  <td valign="top">
                  
							 <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'SavingsType')}" 
											var="item">
							<c:if test="${savingsOffering.savingsType.id == item.id}">
							    <c:out value="${item.name}"></c:out>
							</c:if>				
											
					</c:forEach>   
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td width="35%" align="right"><mifos:mifoslabel name="Savings.maxAmountPerWithdrawl" isColonRequired="yes"/><br></td>
                  <td width="65%" valign="top">
	                  <c:out value="${savingsOffering.maxAmntWithdrawl}" />
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td align="right">
                  <mifos:mifoslabel name="Savings.balanceUsedFor"/>
                  <mifos:mifoslabel name="${ConfigurationConstants.INTEREST}"/>
                  <mifos:mifoslabel name="Savings.rateCalculation"/>:                  </td>
                  <td valign="top">
                  
					 <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'IntCalTypes')}" 
											var="IntCalTypes">
							<c:if test="${savingsOffering.interestCalcType.id == IntCalTypes.id}">
							    <c:out value="${IntCalTypes.name}"></c:out>
							</c:if>				
											
					</c:forEach> 
				  </td>
                </tr>
                <tr class="fontnormal">
                  <td align="right">
                  <mifos:mifoslabel name="Savings.timePeriodFor"/>
                  <mifos:mifoslabel name="${ConfigurationConstants.INTEREST}"/>
                  <mifos:mifoslabel name="Savings.rateCalculation" isColonRequired="yes"/>
                  </td>
                  <td valign="top">
                        <c:out value="${savingsOffering.timePerForInstcalc.meeting.meetingDetails.recurAfter}"/>
                  	  	<c:if test="${savingsOffering.timePerForInstcalc.meeting.meetingDetails.recurrenceType.recurrenceId == 2}">
                  	  		<mifos:mifoslabel name="meeting.labelMonths" bundle="MeetingResources"/>
                  	  	</c:if>
                  	  	<c:if test="${savingsOffering.timePerForInstcalc.meeting.meetingDetails.recurrenceType.recurrenceId == 3}">
	                  	  	<mifos:mifoslabel name="meeting.labelDays" bundle="MeetingResources"/>
                  	  	</c:if>
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td align="right">
                  <mifos:mifoslabel name="Savings.frequencyOf"/>
                  <mifos:mifoslabel name="${ConfigurationConstants.INTEREST}"/>
                  <mifos:mifoslabel name="Savings.postingToAccounts" isColonRequired="yes"/>
                  </td>
                  <td valign="top">
	                  <c:out value="${savingsOffering.freqOfPostIntcalc.meeting.meetingDetails.recurAfter}"/>
                  	    <c:if test="${savingsOffering.freqOfPostIntcalc.meeting.meetingDetails.recurrenceType.recurrenceId == 2}">
                  	  		<mifos:mifoslabel name="meeting.labelMonths" bundle="MeetingResources"/>
						</c:if>
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td align="right">
                  <mifos:mifoslabel name="Savings.minBalanceRequired"/>
                  <mifos:mifoslabel name="${ConfigurationConstants.INTEREST}"/>
                  <mifos:mifoslabel name="Savings.rateCalculation" isColonRequired="yes"/>
                  </td>
                  <td valign="top">
	                  <c:out value="${savingsOffering.minAmntForInt}" />
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td align="right">
                  <mifos:mifoslabel name="${ConfigurationConstants.INTEREST}"/>
                  <mifos:mifoslabel name="Savings.rate" isColonRequired="yes"/>
                  </td>
                  <td valign="top">
                  	<c:out value="${savingsOffering.interestRate}" /> <mifos:mifoslabel name="Savings.perc"/>
                  </td>
                </tr>
              </table>
              
              <br>    
               <table width="93%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td colspan="2" class="fontnormalbold">
                  <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
                  <mifos:mifoslabel name="Savings.accountDetails"/><br>
                      <br>
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td width="35%" align="right" class="fontnormal">
	                <c:choose>
	                  <c:when test="${savingsOffering.savingsType.id == SavingsConstants.SAVINGS_MANDATORY}">
	                  	<mifos:mifoslabel name="Savings.mandatoryAmountForDeposit" mandatory="yes" isColonRequired="yes"/>
	                  </c:when>
	                  <c:otherwise>
	                  <mifos:mifoslabel name="Savings.recommendedAmountForDeposit" isColonRequired="yes"/>
	                  </c:otherwise>
	                </c:choose>
                  </td>
                  <td width="65%" valign="top">                  
                  	<mifos:mifosdecimalinput name="savingsActionForm" property="recommendedAmount"	
		                  />
		                   
	                  <c:choose>
	                    <c:when test="${client.customerLevel.id==CustomerConstants.GROUP_LEVEL_ID}">
	                    
	                    (
						   <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'RecommendedAmtUnit')}" 
											var="item">
							<c:if test="${savingsOffering.recommendedAmntUnit.id == item.id}">
							    <c:out value="${item.name}"></c:out>
							</c:if>				
											
					</c:forEach>   
						  )
	                    </c:when>
	                    <c:otherwise>
	                     <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'RecommendedAmtUnit')}" 
											var="item">
							<c:if test="${savingsOffering.recommendedAmntUnit.id == item.id}">
							    <c:out value="${item.name}"></c:out>
							</c:if>				
											
					</c:forEach> 
	                    </c:otherwise>
                    </c:choose>
                    </td>
                </tr>
              </table>
              <br>
             <c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
              <table width="93%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td colspan="2" class="fontnormalbold">
                  <mifos:mifoslabel name="Savings.additionalInformation"/></td>
                </tr>
                <c:forEach var="cf" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}" varStatus="loopStatus">
              	 <bean:define id="ctr">
                	<c:out value="${loopStatus.index}"/>
                </bean:define>

			<tr class="fontnormal">
                <td width="35%" align="right">
					<mifos:mifoslabel name="${cf.lookUpEntity.entityType}" mandatory="${cf.mandatoryStringValue}" bundle="GroupUIResources"></mifos:mifoslabel>:
				</td>
                <td width="65%">          
					<c:if test="${cf.fieldType == MasterConstants.CUSTOMFIELD_NUMBER}">  
	                	<mifos:mifosnumbertext  name = "savingsActionForm" property='customField[${ctr}].fieldValue' maxlength="200"/>
	                </c:if>
	               	<c:if test="${cf.fieldType == MasterConstants.CUSTOMFIELD_ALPHANUMBER}">
	                	<mifos:mifosalphanumtext name = "savingsActionForm" property='customField[${ctr}].fieldValue' maxlength="200"/>
					</c:if>
	                <c:if test="${cf.fieldType == MasterConstants.CUSTOMFIELD_DATE}"> 
	                	<mifos:mifosalphanumtext name = "savingsActionForm" property='customField[${ctr}].fieldValue' maxlength="200"/>
	                </c:if>
	                <html-el:hidden property='customField[${ctr}].fieldId' value="${cf.fieldId}"></html-el:hidden>
                </td>
           </tr>
         </c:forEach>
              </table>
              </c:if>
              <table width="93%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center" class="blueline">&nbsp;</td>
                </tr>
              </table>
              <br>
              
              <table width="93%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center">
                  <html-el:submit styleClass="buttn" >
						<mifos:mifoslabel name="loan.preview" />
				  </html-el:submit>
&nbsp;
    			  <html-el:button property="cancelButton" onclick="javascript:fun_createCancel(this.form)" styleClass="cancelbuttn" >
						<mifos:mifoslabel name="loan.cancel" />
				  </html-el:button>
                  </td>
                </tr>
              </table>
              <br>
          </td>
        </tr>
      </table>
      <html-el:hidden property="input" value="preview"/>
           <html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
 </html-el:form>
</tiles:put>
</tiles:insert>
