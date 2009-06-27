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
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
<tiles:put name="body" type="string">
	<html-el:form  action="savingsApplyAdjustmentAction?method=adjustLastUserAction">
	<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'accountAction')}" var="accountAction" />
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'isLastPaymentValid')}" var="isLastPaymentValid" />
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'clientName')}" var="clientName" />
	
	<script language="javascript">
		function funCancel(form){
			form.action="savingsApplyAdjustmentAction.do?method=cancel";
			form.submit();
		}
		function fnEditApplyAdjustment(form){
			form.action="savingsApplyAdjustmentAction.do?method=previous";
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
          <td width="70%" height="24" align="left" valign="top" class="paddingL15T15">
          <table width="96%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td width="70%" class="headingorange"><span class="heading"><c:out value="${BusinessKey.savingsOffering.prdOfferingName}"/> # <c:out value="${BusinessKey.globalAccountNum}"/> - </span>
                <mifos:mifoslabel name="Savings.reviewAdjustment"/></td>
                </tr>
                <tr>
		          <td>
		    	      <font class="fontnormalRedBold"><html-el:errors	bundle="SavingsUIResources" /></font>
			      </td>
			  </tr>
            </table>
            <br>
            <table width="93%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td colspan="2" class="fontnormal">
                <mifos:mifoslabel name="Savings.confirmationMsgOnReviewAdjustment"/>
                <br><br>
                </td>
              </tr>               
              <tr>
                <td width="29%" align="right" valign="top" class="fontnormalbold">
                <mifos:mifoslabel name="savings.correct" bundle="SavingsUIResources" />
                <c:out value="${accountAction.name}"/>
                <mifos:mifoslabel name="Savings.amount" isColonRequired="yes"/><br>
                </td>
                <td width="81%" class="fontnormal">
                <c:out value="${sessionScope.savingsApplyAdjustmentActionForm.lastPaymentAmount}"/>
                <c:if test="${isLastPaymentValid == 1}">
                &nbsp;  <c:if test="${(!empty clientName) or (BusinessKey.customer.customerLevel.id!=1)}"> <mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"/> 
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
              <tr>
                <td align="right" valign="top" class="fontnormalbold">
                <mifos:mifoslabel name="Savings.notes" isColonRequired="yes"/> </td>
                <td class="fontnormal"><c:out value="${sessionScope.savingsApplyAdjustmentActionForm.note}"/></td>
              </tr>
              <tr>
                <td colspan="2">&nbsp;</td>
              </tr>
              <tr>
                <td  colspan="2">
                <html-el:button property="editButton" styleClass="insidebuttn" onclick="fnEditApplyAdjustment(this.form)">
					<mifos:mifoslabel name="Savings.Edit" />                   		
				</html-el:button>
                </td>
              </tr>
              
            </table>
            <table width="750" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center" class="blueline">&nbsp; </td>
              </tr>
            </table>
            <br>
            <table width="95%" border="0" cellspacing="0" cellpadding="1">
              <tr>
                <td align="center">
                <html-el:submit styleClass="buttn" >
						<mifos:mifoslabel name="loan.submit" />
	  		    </html-el:submit>
&nbsp;
				<html-el:button property="cancelButton" onclick="javascript:funCancel(this.form)" styleClass="cancelbuttn">
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
