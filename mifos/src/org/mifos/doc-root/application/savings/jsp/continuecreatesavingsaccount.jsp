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

<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
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
                <br> <font color="#FF0000">*</font><mifos:mifoslabel name="Savings.fieldsRequired"/>
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
                  <mifos:mifoslabel name="Savings.accountOwner"/>: </span>
                  <c:out value="${sessionScope.BusinessKey.customer.displayName}" />
                 </td>
              </tr>
            </table>
              <br>
              <table width="93%" border="0" cellpadding="3" cellspacing="0">
                <tr class="fontnormal">
                  <td width="35%" align="right" class="fontnormal">
                  <span class="mandatorytext"><font color="#FF0000">*</font></span>
                  <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
                  <mifos:mifoslabel name="Savings.instanceName"/>:</td>
                  <td width="65%">
                  	<mifos:select name="savingsActionForm" property="selectedPrdOfferingId" onchange="javascript:fun_refresh(this.form)" style="width:136px;">
						<html-el:options collection="savingsPrdOfferings" property="prdOfferingId" labelProperty="prdOfferingName" />
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
                  	<mifos:mifoslabel name="Savings.description"/>:
                  </td>
                  <td valign="top">
                  	<c:out value="${sessionScope.BusinessKey.savingsOffering.description}" />
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td align="right"><mifos:mifoslabel name="Savings.typeOfDeposits"/>:</td>
                  <td valign="top">
                  	  <customtags:lookUpValue	id="${sessionScope.BusinessKey.savingsOffering.savingsType.savingsTypeId}"
						searchResultName="SavingsType" mapToSeperateMasterTable="true">
					  </customtags:lookUpValue>
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td width="35%" align="right"><mifos:mifoslabel name="Savings.maxAmountPerWithdrawl"/>:<br></td>
                  <td width="65%" valign="top">
	                  <c:out value="${sessionScope.BusinessKey.savingsOffering.maxAmntWithdrawl}" />
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td align="right">
                  <mifos:mifoslabel name="Savings.balanceUsedFor"/>
                  <mifos:mifoslabel name="${ConfigurationConstants.INTEREST}"/>
                  <mifos:mifoslabel name="Savings.rateCalculation"/>:                  </td>
                  <td valign="top">
	                  <customtags:lookUpValue	id="${sessionScope.BusinessKey.savingsOffering.interestCalcType.interestCalculationTypeID}"
						searchResultName="IntCalTypes" mapToSeperateMasterTable="true">
					  </customtags:lookUpValue>
				  </td>
                </tr>
                <tr class="fontnormal">
                  <td align="right">
                  <mifos:mifoslabel name="Savings.timePeriodFor"/>
                  <mifos:mifoslabel name="${ConfigurationConstants.INTEREST}"/>
                  <mifos:mifoslabel name="Savings.rateCalculation"/>:
                  </td>
                  <td valign="top">
	                  <c:out value="${sessionScope.BusinessKey.savingsOffering.timePerForInstcalc.meeting.feeMeetingSchedule}" />
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td align="right">
                  <mifos:mifoslabel name="Savings.frequencyOf"/>
                  <mifos:mifoslabel name="${ConfigurationConstants.INTEREST}"/>
                  <mifos:mifoslabel name="Savings.postingToAccounts"/>:
                  </td>
                  <td valign="top">
                  	<c:out value="${sessionScope.BusinessKey.savingsOffering.freqOfPostIntcalc.meeting.feeMeetingSchedule}" />
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td align="right">
                  <mifos:mifoslabel name="Savings.minBalanceRequired"/>
                  <mifos:mifoslabel name="${ConfigurationConstants.INTEREST}"/>
                  <mifos:mifoslabel name="Savings.rateCalculation"/>: 
                  </td>
                  <td valign="top">
	                  <c:out value="${sessionScope.BusinessKey.savingsOffering.minAmntForInt}" />
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td align="right">
                  <mifos:mifoslabel name="${ConfigurationConstants.INTEREST}"/>
                  <mifos:mifoslabel name="Savings.rate"/>: 
                  </td>
                  <td valign="top">
                  	<c:out value="${sessionScope.BusinessKey.savingsOffering.interestRate}" /> <mifos:mifoslabel name="Savings.perc"/>
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
	                  <c:when test="${sessionScope.BusinessKey.savingsOffering.savingsType.savingsTypeId == SavingsConstants.SAVINGS_MANDATORY}">
	                  	<mifos:mifoslabel name="Savings.mandatoryAmountForDeposit" mandatory="yes"/>: 
	                  </c:when>
	                  <c:otherwise>
	                  <mifos:mifoslabel name="Savings.recommendedAmountForDeposit"/>: 
	                  </c:otherwise>
	                </c:choose>
                  </td>
                  <td width="65%" valign="top">                  
                  	<mifos:mifosdecimalinput name="savingsActionForm" property="recommendedAmount"	
		                  value="${sessionScope.BusinessKey.recommendedAmount.amountDoubleValue}"/>
	                  <c:choose>
	                    <c:when test="${sessionScope.BusinessKey.customer.customerLevel.levelId==CustomerConstants.GROUP_LEVEL_ID}">
	                    (<customtags:lookUpValue	id="${sessionScope.BusinessKey.savingsOffering.recommendedAmntUnit.recommendedAmntUnitId}"
							searchResultName="RecommendedAmtUnit" mapToSeperateMasterTable="true">
						  </customtags:lookUpValue>)
	                    </c:when>
	                    <c:otherwise>
	                      <customtags:lookUpValue	id="${sessionScope.BusinessKey.savingsOffering.recommendedAmntUnit.recommendedAmntUnitId}"
							searchResultName="RecommendedAmtUnit" mapToSeperateMasterTable="true">
						  </customtags:lookUpValue>
	                    </c:otherwise>
                    </c:choose>
                    </td>
                </tr>
              </table>
              <br>
              <table width="93%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td colspan="2" class="fontnormalbold">
                  <mifos:mifoslabel name="Savings.additionalInformation"/></td>
                </tr>
                <c:forEach var="cf" items="${sessionScope.customFields}" varStatus="loopStatus">
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
                <%--
                <tr class="fontnormal">
                  <td width="35%" align="right">Custom Field 1:</td>
                  <td width="65%"><input name="textfield242222" type="text" value="xyz">
                  </td>
                </tr>--%>
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
                  <html-el:submit styleClass="buttn" style="width:70px;" >
						<mifos:mifoslabel name="loan.preview" />
				  </html-el:submit>
&nbsp;
    			  <html-el:button property="cancelButton" onclick="javascript:fun_createCancel(this.form)" styleClass="cancelbuttn" style="width:70px;">
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
 </html-el:form>
</tiles:put>
</tiles:insert>
