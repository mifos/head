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

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<html-el:form action="/savingsApplyAdjustmentAction.do?method=preview" >
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
	          	<customtags:headerLink selfLink="true"/> 
	        </span>
        </td>
      </tr>
    </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="100%" height="24" align="left" valign="top" class="paddingL15T15"><table width="96%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td width="70%" class="headingorange">
                <span class="heading"><c:out value="${sessionScope.BusinessKey.savingsOffering.prdOfferingName}"/> # <c:out value="${sessionScope.BusinessKey.globalAccountNum}"/> - </span>
                <mifos:mifoslabel name="Savings.applyAdjustment"/></td>
                </tr>
              <tr>
                <td class="fontnormal"><br>
                <mifos:mifoslabel name="Savings.msgOnTopLastPaymentModifiedAdjustment"/>
                 </td>
              </tr>
            </table>
            <br>
            <table width="100%" border="0" cellspacing="0" cellpadding="2">
              <tr>
		          <td colspan="2">
		    	      <font class="fontnormalRedBold"><html-el:errors	bundle="SavingsUIResources" /></font>
			      </td>
			  </tr>
              <tr>
                <td width="25%" class="fontnormal">
				<html-el:radio property="lastPaymentAmountOption" value="1" /><mifos:mifoslabel name="Savings.modifylastPayment"/></td>
                <td width="75%" class="fontnormal"><mifos:mifoslabel name="Savings.amount"/>:
                    <mifos:mifosdecimalinput name="savingsApplyAdjustmentActionForm" property="lastPaymentAmount"/> 
                <c:if test="${sessionScope.isLastPaymentValid == 1}">
                    &nbsp;( <mifos:mifoslabel name="Savings.paymentType"/>: 
                  	<c:out value="${sessionScope.accountAction.name}"/><c:if test="${(!empty sessionScope.clientName) or (sessionScope.BusinessKey.customer.customerLevel.levelId!=1)}">; <mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"/>
                  	<mifos:mifoslabel name="Savings.clientName"/>:</c:if>
                  	<c:choose>
	              		<c:when test="${!empty sessionScope.clientName}">
							<c:out value="${sessionScope.clientName}"/>
	                	</c:when>
                  	<c:otherwise>
	                  	<c:if test="${sessionScope.BusinessKey.customer.customerLevel.levelId!=1}">
		                  	<mifos:mifoslabel name="Savings.nonSpecified"/>
	                  	</c:if>
                  	</c:otherwise>
                  </c:choose>)
                </c:if>
                </td>
              </tr>
             <%-- <tr>
                <td class="fontnormal"><input type="radio" name="radiobutton" value="radiobutton">
                  Adjust account balance</td>
                <td class="fontnormal">Amount:
                    <input name="textfield24103423222" type="text" >
                </td>
              </tr>
              <tr>
                <td class="fontnormal"><input type="radio" name="radiobutton" value="radiobutton">
      Adjust interest earned</td>
                <td class="fontnormal">Amount:
                    <input name="textfield24103423223" type="text" >
                </td>
              </tr>--%>
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
<html-el:hidden property="accountId" value="${sessionScope.BusinessKey.accountId}"/>
<html-el:hidden property="globalAccountNum" value="${sessionScope.BusinessKey.globalAccountNum}"/> 
      
</html-el:form>
</tiles:put>
</tiles:insert>
