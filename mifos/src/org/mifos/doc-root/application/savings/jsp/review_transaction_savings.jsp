<!--
 
 * review_transaction_savings.jsp  version: 1.0
 
 
 
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
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<script language="javascript">
		function funCancel(form){
			form.action="savingsDepositWithdrawalAction.do?method=cancel";
			form.submit();
		}
		function funMakePayment(form){
			func_disableSubmitBtn("submitButton");
			form.action="savingsDepositWithdrawalAction.do?method=makePayment";
			form.submit();
		}
		function fnEditPayment(form){
			form.action="savingsDepositWithdrawalAction.do?method=previous";
			form.submit();
		}
	</script>
<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
<html-el:form method="post" action="savingsDepositWithdrawalAction.do?method=makePayment" >
    <html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
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
              <td width="100%" colspan="2" class="headingorange">
              	<span class="heading"><c:out value="${BusinessKey.savingsOffering.prdOfferingName}"/> # <c:out value="${BusinessKey.globalAccountNum}"/>
                  - </span><mifos:mifoslabel name="Savings.reviewTransaction" /></td>
            </tr>
            <tr>
              <td colspan="2" class="fontnormal">
              	<mifos:mifoslabel name="Savings.reviewInformation" />
              	<mifos:mifoslabel name="Savings.clickSubmitIfSatisfied" />.
              	<mifos:mifoslabel name="Savings.clickCancelToReturn" />
              	<mifos:mifoslabel name="Savings.accountDetailsPage" />
            </tr>
            <tr>
              <td colspan="2" class="blueline"><img src="images/trans.gif" width="10" height="5"></td>
            </tr>
          </table>
            <br>            
            <table width="96%" border="0" cellspacing="0" cellpadding="1">
	            <tr>
		          <td colspan="2">
		    	      <font class="fontnormalRedBold"><html-el:errors	bundle="SavingsUIResources" /></font>
			      </td>
			  </tr>
			  <c:set var="customerLevel" value="${BusinessKey.customer.customerLevel.id}" />
			  <c:if test="${customerLevel==CustomerLevel.CENTER.value or (customerLevel==CustomerLevel.GROUP.value and 
				  				BusinessKey.recommendedAmntUnit.id==RecommendedAmountUnit.PERINDIVIDUAL.value)}">
	              <tr>
	                	<td align="right" class="fontnormalbold"><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"/>
		              	  <mifos:mifoslabel name="Savings.clientName" isColonRequired="yes"/></td>
		                <td class="fontnormal">
			              <c:choose>
				              <c:when test="${BusinessKey.customer.customerId == sessionScope.savingsDepositWithdrawalForm.customerId}">
					              <mifos:mifoslabel name="Savings.nonSpecified" />
				              </c:when>
				              <c:otherwise>
					             <c:forEach var="client" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'clientList')}">
					                	<c:if test="${client.customerId == sessionScope.savingsDepositWithdrawalForm.customerId}">
					                		<c:out value ="${client.displayName}"/>
					                	</c:if>
					              </c:forEach>
				              </c:otherwise>
			              </c:choose>
		              </td>
		            </tr>
              </c:if>
              <tr>
                <td align="right" class="fontnormalbold">
                	<mifos:mifoslabel name="Savings.dateOfTrxn"  isColonRequired="Yes"/> 
                </td>
                <td class="fontnormal">
                	<c:out value="${sessionScope.savingsDepositWithdrawalForm.trxnDate}"/>
                </td>
              </tr>
              
              <tr>
                <td align="right" class="fontnormalbold">
                	<mifos:mifoslabel name="Savings.paymentType" isColonRequired="Yes"/>
                </td>
                <td class="fontnormal">
                	<c:forEach var="trxnType" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'trxnTypes')}" >
		             	<c:if test="${trxnType.id == sessionScope.savingsDepositWithdrawalForm.trxnTypeId}">
		             		<c:out value="${trxnType.name}"/>
		             	</c:if>
		             </c:forEach>
		        </td>
              </tr>
              <tr>
                <td width="22%" align="right" class="fontnormalbold">
	                <mifos:mifoslabel name="Savings.amount" isColonRequired="Yes"/>
                </td>
                <td width="78%" class="fontnormal">
	                <c:out value="${sessionScope.savingsDepositWithdrawalForm.amount}"/>
                </td>
              </tr>
              
              <tr>
                <td align="right" class="fontnormalbold">
               	 <mifos:mifoslabel name="Savings.modeOfPayment" isColonRequired="Yes"/>
                </td>
                <td class="fontnormal">
		           <c:forEach var="Payment" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'PaymentType')}" >
		             	<c:if test="${payment.id == sessionScope.savingsDepositWithdrawalForm.paymentTypeId}">
		             		<c:out value="${payment.name}"/>
		             	</c:if>
		             </c:forEach>
                </td>
              </tr>
              <tr>
                <td align="right" class="fontnormalbold">
	                <mifos:mifoslabel name="Savings.receiptId" keyhm="Savings.ReceiptId" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/>
                </td>
                <td class="fontnormal">
	                <c:out value="${sessionScope.savingsDepositWithdrawalForm.receiptId}"/>
                </td>
              </tr>
              <tr>
                <td align="right" class="fontnormalbold">
                	<mifos:mifoslabel name="Savings.receiptDate" keyhm="Savings.ReceiptDate" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/>
                </td>
                <td class="fontnormal">
                	<c:out value="${sessionScope.savingsDepositWithdrawalForm.receiptDate}"/>
                </td>
              </tr>
              
              <tr>
                <td height="3" colspan="2" align="center">&nbsp;</td>
              </tr>
              <tr>
                <td height="3" colspan="2">
                <html-el:button property="editButton" styleClass="insidebuttn" 	style="width:115px;" onclick="fnEditPayment(this.form)">
					<mifos:mifoslabel name="Savings.editTransaction" />                   		
				</html-el:button>
                </td>
              </tr>
              <tr>
                <td height="3" colspan="2" align="center" class="blueline">&nbsp;</td>
              </tr>
              <tr>
                <td height="3" colspan="2" align="center" class="fontnormal">&nbsp;</td>
              </tr>
            </table>
            <table width="96%" border="0" cellspacing="0" cellpadding="1">
              <tr>
                <td align="center">
                <html-el:button property="submitButton" styleClass="buttn" style="width:70px;" onclick="javascript:funMakePayment(this.form)">
						<mifos:mifoslabel name="savings.Submit" bundle="SavingsUIResources"/>
	  		    </html-el:button>
&nbsp;
		      <html-el:button property="cancelButton" onclick="javascript:funCancel(this.form)" styleClass="cancelbuttn" style="width:70px;">
						<mifos:mifoslabel name="savings.Cancel" bundle="SavingsUIResources"/>
			   </html-el:button>
                </td>
              </tr>
            </table>            <br>
            </td>
        </tr>
      </table>
<html-el:hidden property="accountId" value="${BusinessKey.accountId}"/>
<html-el:hidden property="globalAccountNum" value="${BusinessKey.globalAccountNum}"/>      
</html-el:form>
</tiles:put>
</tiles:insert>        