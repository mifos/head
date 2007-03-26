<!--
 
 * reviewclosesavings.jsp  version: 1.0
 
 
 
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
			form.action="savingsClosureAction.do?method=cancel";
			form.submit();
		}
		function funCloseAccount(form){
			func_disableSubmitBtn("closeButton");
			form.action="savingsClosureAction.do?method=close";
			form.submit();
		}
		function fnEditCloseAccount(form){
			form.action="savingsClosureAction.do?method=previous";
			form.submit();
		}
	</script>
	<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
	<html-el:form method="post" action="savingsClosureAction.do?method=close" >
    <html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
						<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'accountPayment')}" var="accountPayment" />
    
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
          <td width="70%" height="24" align="left" valign="top" class="paddingL15T15"><table width="96%" border="0" cellpadding="3" cellspacing="0">
            <tr>
              <td width="100%" colspan="2" class="headingorange">
              <span class="heading">
              		<c:out value="${BusinessKey.savingsOffering.prdOfferingName}"/> # <c:out value="${BusinessKey.globalAccountNum}"/> - 
			  </span><mifos:mifoslabel name="Savings.review"/></td>
            </tr>
            <tr>
              <td colspan="2" class="fontnormal"><mifos:mifoslabel name="Savings.reviewDetails"/>
              <mifos:mifoslabel name="Savings.clickSubmitIfSatisfied"/>
              <mifos:mifoslabel name="Savings.clickCancelToReturn"/>
              <mifos:mifoslabel name="Savings.detailsWithOutClosing"/>
			</td>
            </tr>
             <tr>
		          <td>
		    	      <font class="fontnormalRedBold"><html-el:errors	bundle="SavingsUIResources" /></font>
			      </td>
			  </tr>
            <tr>
              <td colspan="2" class="blueline"><img src="images/trans.gif" width="10" height="5"></td>
            </tr>
          </table>
            <br>            <table width="96%" border="0" cellspacing="0" cellpadding="3">
              <tr>
                <td align="right" class="fontnormalbold"><mifos:mifoslabel name="Savings.dateOfTrxn"/>:</td>
                <td class="fontnormal">
               <%-- <c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,sessionScope.accountPayment.paymentDate)}" />--%>
                <c:out value="${sessionScope.savingsClosureForm.trxnDate}"/>
                </td>
              </tr>
              <tr>
                <td width="22%" align="right" class="fontnormalbold"><mifos:mifoslabel name="Savings.amount"/>: </td>
                <td width="78%" class="fontnormal">
                   	<c:out value="${accountPayment.amount.amountDoubleValue}"/>
                </td>
              </tr>
              <tr>
                <td align="right" class="fontnormalbold"><mifos:mifoslabel name="Savings.modeOfPayment"/>: </td>
                <td class="fontnormal">
                <c:forEach var="payment" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'PaymentType')}" >
                 	<c:if test="${payment.id == sessionScope.savingsClosureForm.paymentTypeId}">
                 		<c:out value="${payment.name}"/>
                 	</c:if>
                 </c:forEach>
                </td>
              </tr>
              <tr id="Savings.ReceiptId">
                <td align="right" class="fontnormalbold"><mifos:mifoslabel name="Savings.receiptId" keyhm="Savings.ReceiptId" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/></td>
                <td class="fontnormal">
                 <c:out value="${sessionScope.savingsClosureForm.receiptId}"/>
                </td>
              </tr>
              <tr id="Savings.ReceiptDate">
                <td align="right" class="fontnormalbold"><mifos:mifoslabel name="Savings.receiptDate" keyhm="Savings.ReceiptDate" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/></td>
	            <td class="fontnormal">
                 <c:out value="${sessionScope.savingsClosureForm.receiptDate}"/>
				</td>
              </tr>
              <c:set var="customerLevel" value="${BusinessKey.customer.customerLevel.id}" />
			  <c:if test="${customerLevel==CustomerLevel.CENTER.value or (customerLevel==CustomerLevel.GROUP.value and 
				  				BusinessKey.recommendedAmntUnit.id==RecommendedAmountUnit.PERINDIVIDUAL.value)}">
              <tr>
                <td align="right" class="fontnormalbold"><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"/>
                <mifos:mifoslabel name="Savings.clientName"/>:</td>
                <td class="fontnormal">
	                <c:choose>
			              <c:when test="${BusinessKey.customer.customerId == sessionScope.savingsClosureForm.customerId}">
				              <mifos:mifoslabel name="Savings.nonSpecified" />
			              </c:when>
			              <c:otherwise>
				             <c:forEach var="client" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'clientList')}">
				                	<c:if test="${client.customerId == sessionScope.savingsClosureForm.customerId}">
				                		<c:out value ="${client.displayName}"/>
				                	</c:if>
				              </c:forEach>
			              </c:otherwise>
			        </c:choose>
                </td>
              </tr>
             </c:if>
              <tr>
                <td align="right" valign="top" class="fontnormalbold"><mifos:mifoslabel name="Savings.notes"/>:</td>
                <td class="fontnormal">
	                <c:out value="${sessionScope.savingsClosureForm.notes}"/>
                 </td>
              </tr>
              <tr>
                <td height="3" colspan="2">&nbsp;</td>
              </tr>
              <tr>
                <td height="3" colspan="2">
                <html-el:button property="editButton" styleClass="insidebuttn" 	style="width:115px;" onclick="fnEditCloseAccount(this.form)">
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
                <html-el:button property="closeButton" styleClass="buttn" style="width:70px;" onclick="javascript:funCloseAccount(this.form)">
						<mifos:mifoslabel name="loan.submit" />
	  		    </html-el:button>
                  &nbsp;
                <html-el:button property="cancelButton" onclick="javascript:funCancel(this.form)" styleClass="cancelbuttn" style="width:70px;">
						<mifos:mifoslabel name="loan.cancel" />
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
