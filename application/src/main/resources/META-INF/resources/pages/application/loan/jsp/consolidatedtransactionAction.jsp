<%--
Copyright (c) 2005-2011 Grameen Foundation USA
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


<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>


<head>

<style>
tr.even {
  background-color: #ddd;
}
tr.odd {
  background-color: #eee;
}

div.scroll {
height: 80%;
width: 100%;
overflow: auto;
float:left;
padding: 0px;
position:relative;
}
table.pkgtable td
{
border-top: 1px dotted #6699CC;
}
table.pkgtable tr.pkgtableheaderrow
{
background-color: #BEC8D1;
text-align: center;
font-family: Verdana;
font-weight: bold;
font-size: 13px;
}

table.burlywoodborder {
border-right: solid 1px #EAEBF4;
border-left : solid 1px #EAEBF4;
border-top : solid 1px #EAEBF4;
border-bottom : solid 1px #EAEBF4;
}

</style>
</head>
<tiles:insert definition=".financialAccountingLayout">
<tiles:put name="body" type="string" >
<!-- <span id="page.id" title="CustomerList"></span> -->
<script src="pages/js/jquery/jquery-1.4.2.min.js"></script>
<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}' />
<fmt:setBundle basename="org.mifos.config.localizedResources.ProductDefinitionResources" />
<script type="text/javascript" src="pages/js/jquery/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="pages/js/singleitem.js"></script>
<script type="text/javascript" src="pages/js/datePicker.js"></script>
<link rel="stylesheet" type="text/css" href="pages/css/datepicker/calendar.css" />
<script language="javascript">
function fnSubmit(form, buttonSubmit) {
	buttonSubmit.disabled=true;
	form.method.value="approve";
	form.action="consolidatedTransactionAction.do";
	form.submit();
}


function fnloadConsolidatedTransactions(form)
{
form.method.value="loadConsolidatedTransaction";
	form.action="consolidatedTransactionAction.do";
	form.submit()
}

function fnReject(form) {
	form.method.value="reject";
	form.action="consolidatedTransactionAction.do";
	form.submit();
}

function addNumbers(id)
                {

                      var tot=document.getElementById("answer").value;

                        var val1 = parseInt(document.getElementById(id).value);
                        var ansD = document.getElementById("answer");

                        val1=Number(tot)+Number(val1);
                        ansD.value =val1;
                }
function fnBankDetail(){

	var trxnType = $('#trxnTypeId option:selected').val();
	if(trxnType == 'BR' || trxnType == 'BP'){
		$("#bankDetailsId").show();
	}else{
		$("#bankDetailsId").hide();
	}
}
</script>

<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.SimpleAccountingUIResources"/>
<body onload="fnBankDetail()">
<html-el:form action="/consolidatedTransactionAction.do">

		<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="350" align="left" valign="top" bgcolor="#FFFFFF">

							<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
									<tr>
										<td align="center" class="heading">
											&nbsp;
										</td>
									</tr>
							</table>

					<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="burlywoodborder">
							<tr>
								<td align="left" valign="top" class="paddingleftCreates">

									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td class="headingorange">
												<span id="bulkentry.heading" class="heading"> <mifos:mifoslabel name="simpleAccounting.head" /> - </span>
												<mifos:mifoslabel name="simpleAccounting.viewglaction" />

											</td>
										</tr>
									</table>

									<logic:messagesPresent>
										<font class="fontnormalRedBold"><span id="BulkEntry.error.message"> <html-el:errors bundle="simpleAccountingUIResources" /> </span> </font>
										<br>
									</logic:messagesPresent>
									<br>
				<table width="93%" border="0" cellpadding="3" cellspacing="0">



				<tr class="fontnormal">

				 <td align="right"><mifos:mifoslabel name="simpleAccounting.branchOffice" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
					   <td align="left">
						<mifos:select property="branch" name="consolidatedtransactionactionform"  onchange="fnloadConsolidatedTransactions(this.form)">
						<c:forEach items="${sessionScope.OfficesOnHierarchy}" var="offices">
						<html-el:option value="${offices.globalOfficeNum}">${offices.displayName}</html-el:option>
						</c:forEach>
						</mifos:select>
					</td>


							<td align="right">
			                   <mifos:mifoslabel name="simpleAccounting.trxnDate" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources"/>
			                </td>
			                <td align="left" >
			                     <mifos:mifosalphanumtext styleId= "createLoanProduct.input.prdOffering" name="consolidatedtransactionactionform" property="transactiondate" size="8"/>
							 &nbsp  <img src="pages/framework/images/mainbox/calendaricon.gif" onclick="displayDatePicker('transactiondate', this);"/>
			                </td>

					<tr class="fontnormal">
			<td align="center" colspan="4">
				<div id="bankDetailsId">
				<br>
			<table width="100%" border="0" cellpadding="0" cellspacing="0" class="burlywoodborder">





			</table>
</tr>
				   <table width="100%" border="0" cellpadding="2" cellspacing="0" class="burlywoodborder">
<tr><td>
				   <table cellpadding="1" cellspacing="1" border="1">
            <tr>
                <th>
                   Voucher.NO
                </th>
                <th>
                    COA Description
                </th>
                <th>
                    Amount
                </th>
            </tr>
            <tr>
                <th>
                 ......
                </th>
                <th>
                    Reciepts
                </th>
                <th>
                 ......
                </th>
            </tr>
             <c:forEach items="${sessionScope.viewStageTransactionsDtoInterOfficeList}" var="accountHeadInterOffice">
                <tr>
                <td>
                 <html-el:text property="transactionNo" name="consolidatedtransactionactionform" styleClass="separatedNumber" readonly="true" value="${accountHeadInterOffice.transactionNo}" styleId="${accountHead.sno}" />
                 </td>

                    <td>
                        <html-el:text property="subAccountType" name="consolidatedtransactionactionform" styleClass="separatedNumber" styleId="simpleaccounting.input.coaname" readonly="true" value="${accountHeadInterOffice.mainAccount}"/>
                    </td>

                    <td>
                        <html-el:text property="amount" name="consolidatedtransactionactionform"  styleClass="separatedNumber" styleId="simpleaccounting.input.coaname" readonly="true" value="${accountHeadInterOffice.transactionAmount}"/>
                    </td>
                </tr>
           </c:forEach>


             <c:forEach items="${sessionScope.viewStageTransactionsDtoCRBRListValues}" var="accountHeadCRBR">
                <tr>
                <td>
                 <html-el:text property="transactionNo" name="consolidatedtransactionactionform" styleClass="separatedNumber" readonly="true" value="${accountHeadCRBR.transactionNo}" styleId="${accountHead.sno}" />
                 </td>

                    <td>
                        <html-el:text property="subAccountType" name="consolidatedtransactionactionform" styleClass="separatedNumber" styleId="simpleaccounting.input.coaname" readonly="true" value="${accountHeadCRBR.subAccount}"/>
                    </td>

                    <td>
                        <html-el:text property="amount" name="consolidatedtransactionactionform"  styleClass="separatedNumber" styleId="simpleaccounting.input.coaname" readonly="true" value="${accountHeadCRBR.transactionAmount}"/>
                    </td>
                </tr>
           </c:forEach>

            <tr>
                <th>
                 ......
                </th>
                <th>
                    Reciepts Operations
                </th>
                <th>
                .......
                </th>
            </tr>

            <c:forEach items="${sessionScope.viewStageTransactionsDtoCRMisListValues}" var="accountHeadCRMis">
                <tr>
                <td>
                 <html-el:text property="transactionNo" name="consolidatedtransactionactionform" styleClass="separatedNumber" readonly="true" value="${accountHeadCRMis.transactionNo}" styleId="${accountHead.sno}" />
                 </td>

                    <td>
                        <html-el:text property="subAccountType" name="consolidatedtransactionactionform" styleClass="separatedNumber" styleId="simpleaccounting.input.coaname" readonly="true" value="${accountHeadCRMis.subAccount}"/>
                    </td>

                    <td>
                        <html-el:text property="amount" name="consolidatedtransactionactionform"  styleClass="separatedNumber" styleId="simpleaccounting.input.coaname" readonly="true" value="${accountHeadCRMis.transactionAmount}"/>
                    </td>
                </tr>
           </c:forEach>
             <tr class="fontnormal">
                <th>
                  Total
                </th>
                <th>
         =
                </th>
                <th>
                     <html-el:text  property="crtotal" styleId="answer" readonly="true"  value='${ConsolidatedTransactionActionForm.crtotal}'/>
                </th>
                <th>

                </th>
            </tr>
        </table>
        </td>
        <td>
         <table cellpadding="1" cellspacing="1" border="1">
            <tr>
                <th>
                   Voucher.NO
                </th>
                <th>
                    Transaction Notes
                </th>
                <th>
                    Amount
                </th>
            </tr>
            <tr>
                <th>
                 ......
                </th>
                <th>
                    Payments
                </th>
                <th>
                .......
                </th>
            </tr>
             <c:forEach items="${sessionScope.viewStageTransactionsDtoCPBPListValues}" var="accountHeadCPBP">
                <tr>
                <td>
                        <html-el:text property="transactionCpBpNo" name="consolidatedtransactionactionform" styleClass="separatedNumber" styleId="simpleaccounting.input.coaname" readonly="true" value="${accountHeadCPBP.transactionNo}"/>
                    </td>
                    <td>
                        <html-el:text property="subAccountCpBpType" name="consolidatedtransactionactionform" styleClass="separatedNumber" styleId="simpleaccounting.input.coaname" readonly="true" value="${accountHeadCPBP.subAccount}"/>
                    </td>
                    <td>
                        <html-el:text property="CpBpamount" name="consolidatedtransactionactionform" styleClass="separatedNumber" styleId="${accountHead.sno}"   readonly="true" value="${accountHeadCPBP.transactionAmount}" />
                    </td>

                </tr>
           </c:forEach>
            <tr>
                <th>
                 ......
                </th>
                <th>
                    Payment Operations
                </th>
                <th>
                .......
                </th>
            </tr>
             <c:forEach items="${sessionScope.viewStageTransactionsDtoCPMisListValues}" var="accountHeadCpMis">
                <tr>
                <td>
                 <html-el:text property="transactionCpBpNo" name="consolidatedtransactionactionform" styleClass="separatedNumber" readonly="true" value="${accountHeadCpMis.transactionNo}" styleId="${accountHead.sno}" />
                 </td>

                    <td>
                        <html-el:text property="subAccountType" name="consolidatedtransactionactionform" styleClass="separatedNumber" styleId="simpleaccounting.input.coaname" readonly="true" value="${accountHeadCpMis.subAccount}"/>
                    </td>

                    <td>
                        <html-el:text property="amount" name="consolidatedtransactionactionform"  styleClass="separatedNumber" styleId="simpleaccounting.input.coaname" readonly="true" value="${accountHeadCpMis.transactionAmount}"/>
                    </td>
                </tr>
           </c:forEach>

             <tr class="fontnormal">
                <th>
                  Total
                </th>
                <th>
         =
                </th>
                <th>
                     <html-el:text  property="cptotal" styleId="answer" readonly="true" value='${ConsolidatedTransactionActionForm.cptotal}'/>
                </th>
                <th>

                </th>
            </tr>
        </table>
        </td>
        <tr>
		     </table>
			<br>
		</tr>
					<html-el:hidden property="method" value="get" />
					<%-- <html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" /> --%>
				<br>

					<table width="93%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center">
												 <html-el:submit styleId="simpleaccounting.button.approve" styleClass="buttn submit" onclick="fnSubmit(this.form,this)" >
													<mifos:mifoslabel name="simpleAccounting.approve" bundle="simpleAccountingUIResources"/>
											    </html-el:submit>
												&nbsp;
												<html-el:button  styleId="bulkentry.button.reject" property="cancel" styleClass="cancelbuttn" onclick="fnReject(this.form);" >
													<mifos:mifoslabel name="simpleAccounting.reject" bundle="simpleAccountingUIResources"/>
												</html-el:button>
											</td>
										</tr>
									</table>
									<br>
								</td>
							</tr>
						</table>
						<br>
					</td>
				</tr>
			</table>
		</html-el:form>
		</body>
	</tiles:put>
</tiles:insert>
