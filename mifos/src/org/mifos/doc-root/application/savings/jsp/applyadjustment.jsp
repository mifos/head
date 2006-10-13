<!--
 
 * applyadjustment.jsp  version: 1.0
 
 
 
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
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<html-el:form action="/savingsApplyAdjustmentAction.do?method=preview" >
	 <html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'accountAction')}" var="accountActionValue" />
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'isLastPaymentValid')}" var="isLastPaymentValid" />
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'clientName')}" var="clientName" />

	<script language="javascript">
		function funCancel(form){
			form.action="savingsApplyAdjustmentAction.do?method=cancel";
			form.submit();
		}
	</script>
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
          <td width="100%" height="24" align="left" valign="top" class="paddingL15T15"><table width="96%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td width="70%" class="headingorange">
                <span class="heading"><c:out value="${BusinessKey.savingsOffering.prdOfferingName}"/> # <c:out value="${BusinessKey.globalAccountNum}"/> - </span>
                <mifos:mifoslabel name="Savings.applyAdjustment"/></td>
                </tr>
                <tr>
		          <td>
		    	      <font class="fontnormalRedBold"><html-el:errors	bundle="SavingsUIResources" /></font>
			      </td>
			  </tr>
              <tr>
                <td class="fontnormal">
                <mifos:mifoslabel name="Savings.msgOnTopLastPaymentModifiedAdjustment"/>
                 </td>
              </tr>
            </table>
			<br>
			<table width="95%" border="0" cellspacing="0" cellpadding="2">
              <tr>
                <td width="34%" class="fontnormal">
                <mifos:mifoslabel name="savings.Last" bundle="SavingsUIResources" />
                <c:out value="${accountActionValue.name}"/>
                <mifos:mifoslabel name="savings.made" bundle="SavingsUIResources" />:
                  <c:out value="${BusinessKey.lastPmnt.amount}"/></td>
                </tr>
              <tr>
                <td class="fontnormal">
                <mifos:mifoslabel name="savings.correct" bundle="SavingsUIResources" />
                <c:out value="${accountActionValue.name}"/>
                <mifos:mifoslabel name="Savings.amount" />:
                   <c:if test="${param.method == 'load'}">
						<mifos:mifosdecimalinput name="savingsApplyAdjustmentActionForm" property="lastPaymentAmount" value=""/>
					</c:if>
					<c:if test="${param.method != 'load'}">
						<mifos:mifosdecimalinput name="savingsApplyAdjustmentActionForm" property="lastPaymentAmount"/>
					</c:if>
				   	<c:if test="${isLastPaymentValid == 1}">
                    &nbsp; <c:if test="${(!empty clientName) or (BusinessKey.customer.customerLevel.id!=1)}"><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"/>
                  	<mifos:mifoslabel name="Savings.clientName"/>:</c:if>
                  	<c:choose>
	              		<c:when test="${!empty clientName}">
							(<c:out value="${clientName}"/>)
	                	</c:when>
                  	<c:otherwise>
	                  	<c:if test="${BusinessKey.customer.customerLevel.id!=1}">
		                  	(<mifos:mifoslabel name="Savings.nonSpecified"/>)
	                  	</c:if>
                  	</c:otherwise>
                  </c:choose>
                </c:if>
				</td>
                </tr>
            </table>
            <br>
           
            
            <table width="93%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td width="5%" valign="top" class="fontnormal">
                <mifos:mifoslabel name="Savings.notes" mandatory="yes"/>: <br>
                </td>
                <td width="95%" class="fontnormal">
                	<html-el:textarea property="note" style="width:320px; height:110px;"/>
                </td>
              </tr>
            </table>
            <table width="750" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center" class="blueline">&nbsp;                </td>
              </tr>
            </table>            <br>
            <table width="95%" border="0" cellspacing="0" cellpadding="1">
              <tr>
                <td align="center">
                <html-el:submit styleClass="buttn" style="width:125px;" >
						<mifos:mifoslabel name="Savings.reviewAdjustment" />
	  		    </html-el:submit>
&nbsp;
			    <html-el:button property="cancelButton" onclick="javascript:funCancel(this.form)" styleClass="cancelbuttn" style="width:70px;">
						<mifos:mifoslabel name="loan.cancel" />
			    </html-el:button>
                </td>
              </tr>
            </table></td>
        </tr>
      </table>
<html-el:hidden property="accountId" value="${BusinessKey.accountId}"/>
<html-el:hidden property="globalAccountNum" value="${BusinessKey.globalAccountNum}"/> 
      
</html-el:form>
</tiles:put>
</tiles:insert>
