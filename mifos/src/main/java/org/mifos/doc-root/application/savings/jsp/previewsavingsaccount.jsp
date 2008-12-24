<!--
 
 * previewsavingsaccount.jsp  version: 1.0
 
 
 
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
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<SCRIPT SRC="pages/application/savings/js/CreateSavingsAccount.js"></SCRIPT>
	 <SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
	<html-el:form method="post" action="/savingsAction.do?method=update" >

      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bluetablehead05">
          <span class="fontnormal8pt">
          	<customtags:headerLink/>
          </span></td>
        </tr>
      </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td align="left" valign="top" class="paddingL15T15" >
          <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" scope="session" />
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td width="83%" class="headingorange">
                <span class="heading">
	                <c:out value="${sessionScope.BusinessKey.savingsOffering.prdOfferingName}"/> # <c:out value="${sessionScope.BusinessKey.globalAccountNum}"/> - 
	            </span> 
	                <mifos:mifoslabel name="Savings.Preview"/>
	                <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
	                <mifos:mifoslabel name="Savings.accountInformation"/>                               
                </td>
              </tr>
              <tr>
                <td class="fontnormal">
                    <mifos:mifoslabel name="Savings.reviewInformation"/>
                    <mifos:mifoslabel name="Savings.clickSubmitOnPreview"/>
                    <mifos:mifoslabel name="Savings.Edit"/>
                    <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
                    <mifos:mifoslabel name="Savings.accountInformation"/>
                    <mifos:mifoslabel name="Savings.toMakeChanges"/>
                    <mifos:mifoslabel name="Savings.clickCancelToReturn"/>
                    <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
                    <mifos:mifoslabel name="Savings.accountDetailsPage"/>
                 </td>
              </tr>
              <tr>
    	          <td>
	        	      <font class="fontnormalRedBold"><html-el:errors	bundle="SavingsUIResources" /></font>
        	      </td>
              </tr>
            </table>
            <br>  
               <table width="95%" border="0" cellpadding="3" cellspacing="0">
              <tr class="fontnormal">
                <td width="30%" class="fontnormal">
                <span class="fontnormalbold">
                	<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
                    <mifos:mifoslabel name="Savings.accountDetails"/><br>                    
                    <c:choose>
	                  <c:when test="${sessionScope.BusinessKey.savingsOffering.savingsType.id == SavingsConstants.SAVINGS_MANDATORY}">
	                  	<mifos:mifoslabel name="Savings.mandatoryAmountForDeposit" isColonRequired="yes"/>
	                  </c:when>
	                  <c:otherwise>
	                  <mifos:mifoslabel name="Savings.recommendedAmountForDeposit" isColonRequired="yes"/>
	                  </c:otherwise>
	                </c:choose>
                </span> 
                
                <c:out value="${savingsActionForm.recommendedAmount}"/>
                    <c:choose>
	                    <c:when test="${sessionScope.BusinessKey.customer.customerLevel.id==CustomerConstants.GROUP_LEVEL_ID}">
	                    (<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'RecommendedAmtUnit')}" 
											var="item">
							<c:if test="${sessionScope.BusinessKey.savingsOffering.recommendedAmntUnit.id == item.id}">
							    <c:out value="${item.name}"></c:out>
							</c:if>				
											
					</c:forEach> )
	                    </c:when>
	                    <c:otherwise>
	                      <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'RecommendedAmtUnit')}" 
											var="item">
							<c:if test="${sessionScope.BusinessKey.savingsOffering.recommendedAmntUnit.id == item.id}">
							    <c:out value="${item.name}"></c:out>
							</c:if>				
											
					</c:forEach> 
	                    </c:otherwise>
                    </c:choose>
                <br>  <br>  
                
                 <c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
                <span class="fontnormalbold"><mifos:mifoslabel name="Savings.additionalInformation"/></span>
               <br>
                  <c:forEach var="cfdef" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
						<c:forEach var="cf" items="${savingsActionForm.accountCustomFieldSet}">
							<c:if test="${cfdef.fieldId==cf.fieldId}">
								<span class="fontnormalbold"><mifos:mifoslabel name="${cfdef.lookUpEntity.entityType}" bundle="GroupUIResources" isColonRequired="yes"></mifos:mifoslabel></span> 
		        		  	 	<span class="fontnormal">
									<c:out value="${cf.fieldValue}"/>
								</span><br>
							</c:if>
						</c:forEach>
  					</c:forEach>  					
                  <br>
                  </c:if>
                  <span class="fontnormal">
	                  <html-el:button property="editButton" styleClass="insidebuttn" onclick="fnEdit(this.form)">
							<mifos:mifoslabel name="Savings.Edit" />
							<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" />
							<mifos:mifoslabel name="Savings.accountInformation" />
						</html-el:button> 
                  </span></td>
                </tr>
              <tr class="fontnormal">
                <td class="fontnormal">&nbsp;</td>
              </tr>
            </table>            
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center" class="blueline">&nbsp;</td>
              </tr>
            </table>
            <br>
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center">
				<html-el:button property="submitButton" styleClass="buttn" onclick="javascript:fnUpdate(this.form)">
						<mifos:mifoslabel name="loan.submit" />
				  </html-el:button>
				  &nbsp;
				 <html-el:button property="cancelButton" onclick="javascript:fun_editCancel(this.form)" styleClass="cancelbuttn">
						<mifos:mifoslabel name="loan.cancel" />
				  </html-el:button>
                </td>
              </tr>
            </table>
            <br>
            <br>
          </td>
        </tr>
      </table>
      <html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
</html-el:form>
</tiles:put>
</tiles:insert>
