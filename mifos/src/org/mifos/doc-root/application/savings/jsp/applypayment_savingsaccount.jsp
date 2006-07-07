<!--
 
 * applypayment_savingsaccount.jsp  version: 1.0
 
 
 
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
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>
		<script language="javascript">
		function funCancel(form){
			form.action="savingsDepositWithdrawalAction.do?method=cancel";
			form.submit();
		}
		function reLoad(form){
			form.action="savingsDepositWithdrawalAction.do?method=reLoad";
			form.submit();
		}
	</script>

<html-el:form method="post" action="/savingsDepositWithdrawalAction.do?method=preview" onsubmit="return (validateMyForm(receiptDate,receiptDateFormat,receiptDateYY) && validateMyForm(trxnDate,trxnDateFormat,trxnDateYY))">
   <table width="95%" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td class="bluetablehead05">
        	<span class="fontnormal8pt">
        		<customtags:headerLink /> 
        	</span>
        </td>
      </tr>
    </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="70%" height="24" align="left" valign="top" class="paddingL15T15">
          <table width="96%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td width="70%" class="headingorange">
                <span class="heading">
                <c:out value="${sessionScope.BusinessKey.savingsOffering.prdOfferingName}" /> # <c:out value="${sessionScope.BusinessKey.globalAccountNum}" /> - 
                </span>
                <mifos:mifoslabel name="Savings.makeDepositWithdrawal" />
                </td>
                </tr>
              <tr>
                <td class="fontnormal"><br>
                  <span class="fontnormalRed">*</span> <mifos:mifoslabel name="Savings.fieldsRequired" /> </td>
              </tr>
            </table>
            
            <table width="95%" border="0" cellspacing="0" cellpadding="3">
	            <tr>
		          <td colspan="2">
		    	      <font class="fontnormalRedBold"><html-el:errors	bundle="SavingsUIResources" /></font>
			      </td>
			  </tr>
              <tr>
                <td align="right" class="fontnormal">
                <mifos:mifoslabel name="Savings.paymentType" mandatory="Yes" isColonRequired="Yes"/>
                </td>
                <td>
                <mifos:select name="savingsDepositWithdrawalForm" property="trxnTypeId" onchange="javascript:reLoad(this.form)">
						<html-el:options collection="trxnTypes" property="id" labelProperty="name"></html-el:options>
				</mifos:select>
                </td>
              </tr>
              <tr>
                <td width="24%" align="right" class="fontnormal">
                	<mifos:mifoslabel name="Savings.enterAmount" mandatory="Yes" isColonRequired="Yes"/>
                </td>
                <td width="76%">
	                <mifos:mifosdecimalinput property="amount"/>
                </td>
              </tr>
              <tr>
                <td align="right" class="fontnormal">
                	<mifos:mifoslabel name="Savings.dateOfTrxn" mandatory="Yes" isColonRequired="Yes"/>
                </td>
                <td class="fontnormal">
	                <date:datetag property="trxnDate" />
                </td>
              </tr>
              <tr>
                <td align="right" class="fontnormal">
	                <mifos:mifoslabel name="Savings.modeOfPayment" mandatory="yes" isColonRequired="Yes"/>
                </td>
                <td>
                	<mifos:select name="savingsDepositWithdrawalForm" property="paymentTypeId">
						<html-el:options collection="PaymentType" property="id" labelProperty="name"></html-el:options>
					</mifos:select>
                </td>
              </tr>
              <tr>
                <td align="right" class="fontnormal">
                	<mifos:mifoslabel keyhm="Savings.ReceiptId" isColonRequired="Yes" name="Savings.receiptId" />
                </td>
                <td>
                	<mifos:mifosalphanumtext keyhm="Savings.ReceiptId" property="receiptId" />
                </td>
              </tr>
              <tr>
                <td align="right" class="fontnormal">
                <mifos:mifoslabel keyhm="Savings.ReceiptDate" isColonRequired="Yes"  name="Savings.receiptDate" />
                </td>
                <td class="fontnormal">
	                <date:datetag keyhm="Savings.ReceiptDate" property="receiptDate" />
                </td>
              </tr>
        <c:choose>
			<c:when test="${!empty clientList}">
              <tr>
                <td align="right" class="fontnormal"><span class="fontnormalRed">*</span>
                <mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />
				<mifos:mifoslabel name="Savings.clientName" isColonRequired="yes"/>
				</td>
                <td>
	               	 <mifos:select name="savingsDepositWithdrawalForm" property="customerId">
						<c:forEach var="client" items="${sessionScope.clientList}">
							<html-el:option value="${client.customerId}">
								<c:out value="${client.displayName}" />
							</html-el:option>
						</c:forEach>
						<html-el:option value="${sessionScope.BusinessKey.customer.customerId}">
							<mifos:mifoslabel name="Savings.nonSpecified" />
						</html-el:option>
					</mifos:select>
                </td>
              </tr>
            </c:when>
			<c:otherwise>
					<html-el:hidden property="customerId" value="${sessionScope.BusinessKey.customer.customerId}" />
			</c:otherwise>
		</c:choose>
            </table>
            <table width="96%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center" class="blueline">&nbsp;</td>
              </tr>
              <tr>
                <td align="center">&nbsp;</td>
              </tr>
              <tr>
                <td align="center">
					<html-el:submit styleClass="buttn" style="width:130px;">
						<mifos:mifoslabel name="Savings.reviewTransaction" />
					</html-el:submit>
                  &nbsp;
                  <html-el:button property="cancelButton" onclick="javascript:funCancel(this.form)" styleClass="cancelbuttn" style="width:70px;">
							<mifos:mifoslabel name="savings.Cancel" bundle="SavingsUIResources"/>
				  </html-el:button>
                </td>
              </tr>
            </table></td>
        </tr>
      </table>
		<html-el:hidden property="accountId" value="${sessionScope.BusinessKey.accountId}" />
		<html-el:hidden property="globalAccountNum" value="${sessionScope.BusinessKey.globalAccountNum}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>      
