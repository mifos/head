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
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<span id="page.id" title="editsavingsaccount" />
		<script src="pages/framework/js/conversion.js"></script>
		<script src="pages/framework/js/con_en.js"></script>
		<script src="pages/framework/js/con_${sessionScope['UserContext'].currentLocale}.js"></script>
		<SCRIPT SRC="pages/application/savings/js/CreateSavingsAccount.js"></SCRIPT>
	<html-el:form method="post" action="/savingsAction.do?method=editPreview" >

      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bluetablehead05">
			  <span class="fontnormal8pt">
	          	<customtags:headerLink/> 
	          </span>               
          </td>
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
                	<mifos:mifoslabel name="Savings.Edit"/>
                	<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
                	<mifos:mifoslabel name="Savings.accountInformation"/>
                </td>
              </tr>
              <tr>
                <td class="fontnormal">
	                <mifos:mifoslabel name="Savings.editFields"/>
	                <mifos:mifoslabel name="Savings.clickPreview"/>
	                <mifos:mifoslabel name="Savings.clickCancelToReturn"/>
	                <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
	                <mifos:mifoslabel name="Savings.accountDetailsPage"/>
             		<br>
                  <mifos:mifoslabel name="Savings.fieldsRequired" mandatory="yes"/> 
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
              <tr>
                <td colspan="2" class="fontnormalbold">
                <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
                <mifos:mifoslabel name="Savings.accountDetails"/><br>
                    <br>
                </td>
              </tr>
              <tr class="fontnormal">
                <td width="30%" align="right" class="fontnormal">                
                 <c:choose>
	                  <c:when test="${sessionScope.BusinessKey.savingsOffering.savingsType.id == SavingsConstants.SAVINGS_MANDATORY}">
	                  	<mifos:mifoslabel name="Savings.mandatoryAmountForDeposit" mandatory="yes" isColonRequired="yes"/> 
	                  </c:when>
	                  <c:otherwise>
	                  <mifos:mifoslabel name="Savings.recommendedAmountForDeposit" isColonRequired="yes"/>
	                  </c:otherwise>
	                </c:choose>
                 </td>
                <td width="70%" valign="top">
               
                <html-el:text name="savingsActionForm" property="recommendedAmount"	/>
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
                </td>
              </tr>
            </table>            
            <br>           
            <c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
             <table width="95%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td colspan="2" class="fontnormalbold">
                	<mifos:mifoslabel name="Savings.additionalInformation"/>
                </td>
              </tr>
              <c:forEach var="cfdef" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}" varStatus="loopStatus">
              	 <bean:define id="ctr">
                	<c:out value="${loopStatus.index}"/>
                </bean:define>
                <c:forEach var="cf" items="${savingsActionForm.accountCustomFieldSet}" varStatus="loopStatus">
                 <c:if test="${cfdef.fieldId==cf.fieldId}">
              	  <tr class="fontnormal">
	               <td width="30%" align="right">
					<mifos:mifoslabel name="${cfdef.lookUpEntity.entityType}" mandatory="${cfdef.mandatoryStringValue}" bundle="GroupUIResources" isColonRequired="yes"></mifos:mifoslabel>
					</td>
	               <td width="70%">
	                  <c:if test="${cfdef.fieldType == MasterConstants.CUSTOMFIELD_NUMBER}">  
	                  	<mifos:mifosnumbertext  name = "savingsActionForm" property='customField[${ctr}].fieldValue' value="${cf.fieldValue}" maxlength ="200" /> 
	                  </c:if>
	               	  <c:if test="${cfdef.fieldType == MasterConstants.CUSTOMFIELD_ALPHANUMBER}">
	                 	<mifos:mifosalphanumtext name = "savingsActionForm" property='customField[${ctr}].fieldValue' value="${cf.fieldValue}" maxlength ="200"/>
		    	 	</c:if>
	                 <c:if test="${cfdef.fieldType == MasterConstants.CUSTOMFIELD_DATE}"> 
	                 	<mifos:mifosalphanumtext name = "savingsActionForm" property='customField[${ctr}].fieldValue' value="${cf.fieldValue}" maxlength ="200"/>
	                 </c:if>
	                  	<html-el:hidden property='customField[${ctr}].fieldId' value="${cfdef.fieldId}"></html-el:hidden>
	                </td>
	             </tr>
	           </c:if>
	          </c:forEach>
   	        </c:forEach>
            </table>
             </c:if>
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center" class="blueline">&nbsp;</td>
              </tr>
            </table>
            <br>
           
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center">
                <html-el:submit styleClass="buttn" >
						<mifos:mifoslabel name="loan.preview" />
				  </html-el:submit>
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
      <html-el:hidden property="accountId" value="${sessionScope.BusinessKey.accountId}"/>
      <html-el:hidden property="globalAccountNum" value="${sessionScope.BusinessKey.globalAccountNum}"/>
      <html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
</html-el:form>
</tiles:put>
</tiles:insert>        
