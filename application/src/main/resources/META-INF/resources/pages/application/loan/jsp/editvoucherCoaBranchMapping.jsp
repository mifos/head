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
	form.method.value="preview";
	form.action="voucherBranchMappingAction.do";
	form.submit();
}


function fnloadCoaNames(form)
{
form.method.value="loadCoaNames";
	form.action="voucherBranchMappingAction.do";
	form.submit()
}
function fnloadMainAccounts(form)
{
form.method.value="loadMainAccounts";
	form.action="voucherBranchMappingAction.do";
	form.submit()
}
function fnCancel(form) {
	form.method.value="cancel";
	form.action="generalledgeraction.do";
	form.submit();
}
function addNumbers(styleId)
                {


                      var tot=document.getElementById("answer").value;

                        var val1 = parseInt(document.getElementById(styleId).value);
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
<html-el:form action="/voucherBranchMappingAction.do">

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
												<span id="bulkentry.heading" class="heading"> <mifos:mifoslabel name="financialAccounting.head"  bundle="FinancialAccountingUIResources"/> - </span>
												<mifos:mifoslabel name="financialAccounting.voucherbranchheader"  bundle="FinancialAccountingUIResources"/>

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
						<mifos:select property="branch" name="voucherbranchmappingform"  onchange="fnloadCoaNames(this.form)">
						<c:forEach items="${sessionScope.OfficesOnHierarchy}" var="offices">
						<html-el:option value="${offices.globalOfficeNum}">${offices.displayName}</html-el:option>
						</c:forEach>
						</mifos:select>
					</td>
					 <td  align="right"><mifos:mifoslabel name="simpleAccounting.trxnType" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
							<td  align="left">
								<mifos:select styleId="trxnTypeId" property="transactiontype" name="voucherbranchmappingform" onchange="fnloadMainAccounts(this.form)">
								<html-el:option value="CP"><mifos:mifoslabel name="simpleAccounting.cashPayment" bundle="simpleAccountingUIResources" /></html-el:option>
								<html-el:option value="BP"><mifos:mifoslabel name="simpleAccounting.bankPayment" bundle="simpleAccountingUIResources" /></html-el:option>
								</mifos:select>
							</td></tr>
							<tr class="fontnormal">
							 <td  align="right"><mifos:mifoslabel name="simpleAccounting.mainAccount" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
					<td align="left">
						<mifos:select property="mainAccount" name="voucherbranchmappingform" >
				        <c:forEach items="${sessionScope.MainAccountGlCodes}" var="mainAccount">
				            <html-el:option value="${mainAccount.glcode}">${mainAccount.glname}</html-el:option>
				        </c:forEach>
						</mifos:select>
				</td>
							<td align="right">
			                   <mifos:mifoslabel name="simpleAccounting.trxnDate" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources"/>
			                </td>
			                <td align="left" >
			                     <mifos:mifosalphanumtext styleId= "createLoanProduct.input.prdOffering" name="voucherbranchmappingform" property="transactiondate" size="8"/>
							 &nbsp  <img src="pages/framework/images/mainbox/calendaricon.gif" onclick="displayDatePicker('transactiondate', this);"/>
			                </td></tr>

					<tr class="fontnormal">
			<td align="center" colspan="4">
				<div id="bankDetailsId">
				<br>
			<table width="100%" border="0" cellpadding="0" cellspacing="0" class="burlywoodborder">

				    <tr class="fontnormal">
				      <td align="right"><mifos:mifoslabel name="simpleAccounting.chequeNo" mandatory="no" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
				<td align="left">
					<mifos:mifosnumbertext property="chequeNo"  name="voucherbranchmappingform" style="width:150px" maxlength="60" /></td>
				<td align="right"><mifos:mifoslabel name="simpleAccounting.chequeDate" mandatory="no" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
					<td align="left">
						<date:datetag property="chequeDate" name="voucherbranchmappingform" isDisabled="No" renderstyle="simple"/></td>
			 </tr>

				<tr class="fontnormal">
			      <td align="right"><mifos:mifoslabel name="simpleAccounting.bankName" mandatory="no" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
					<td align="left">
						<mifos:mifosalphanumtext property="bankName" name="voucherbranchmappingform" style="width:150px" maxlength="60" />
					</td>
			 <td align="right"><mifos:mifoslabel name="simpleAccounting.bankBranch" mandatory="no" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
				 <td align="left">
					<mifos:mifosalphanumtext property="bankBranch" name="voucherbranchmappingform" style="width:150px" maxlength="60" />
				</td>

			       </tr>

			</table>

</tr>
<tr><td></td></tr>
<tr><td></td></tr><tr><td></td></tr><tr><td></td></tr>

				  <table width="100%" border="0" cellpadding="2" cellspacing="0" class="burlywoodborder">

				   <table cellpadding="1" cellspacing="1" border="1">
            <tr class="fontnormal" >
                <th>
                   S.NO
                </th>
                <th>
                    COA Description
                </th>
                <th>
                    Amount
                </th>
                <th>
                    Transaction Notes
                </th>
            </tr>
             <c:forEach items="${sessionScope.coaNamesList}" var="accountHead">

                <tr class="fontnormal">
                    <td>
                        <html-el:text property="sno" name="voucherbranchmappingform" styleClass="separatedNumber"  styleId="simpleaccounting.input.sno" readonly="true" value="${accountHead.sno}"/>
                    </td>
                    <td>
                        <html-el:text property="coaname" name="voucherbranchmappingform" styleClass="separatedNumber" styleId="simpleaccounting.input.coaname" readonly="true" value="${accountHead.coaName}"/>
                    </td>
                    <td>
                        <html-el:text property="amount" name="voucherbranchmappingform" styleClass="separatedNumber" value="${accountHead.amounts}" styleId="${accountHead.sno}" onchange="javascript:addNumbers('${accountHead.sno}')"/>
                    </td>
                    <td>
                        <html-el:text property="transactionnotes" name="voucherbranchmappingform" styleClass="separatedNumber" value="${accountHead.trannotes}"  styleId="simpleaccounting.input.transactionnotes" />
                    </td>
                    <td>

                    </td>
                </tr>

           </c:forEach>

        </table>

		     </table>
			<br>
		</tr>

					<html-el:hidden property="method" value="get" />
					<%-- <html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" /> --%>
				<br>

					<table width="93%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center">
												 <html-el:submit styleId="simpleaccounting.button.preview" styleClass="buttn"  onclick="fnSubmit(this.form, this)">
													<mifos:mifoslabel name="simpleAccounting.preview"/>
											    </html-el:submit>
												&nbsp;
												<html-el:button  styleId="bulkentry.button.cancel" property="cancel" styleClass="cancelbuttn" onclick="fnCancel(this.form);" >
													<mifos:mifoslabel name="simpleAccounting.cancel" bundle="simpleAccountingUIResources"/>
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
