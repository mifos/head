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
<script type="text/javascript" src="pages/js/separator.js"></script>
<script type="text/javascript" src="pages/js/datePicker.js"></script>
<link rel="stylesheet" type="text/css" href="pages/css/datepicker/calendar.css" />
<script language="javascript">
function fnloadOffices(form) {

	form.method.value="loadOffices";
	form.action="viewstagetransactionsaction.do";
	form.submit();
}

function fnloadMainAccounts(form) {
	form.method.value="loadMainAccounts";
	form.action="viewstagetransactionsaction.do";
	form.submit();

}

function fnloadAccountHeads(form) {
	form.method.value="loadAccountHeads";
	form.action="viewstagetransactionsaction.do";
	form.submit();
}

function fnSubmit(form, buttonSubmit) {
	buttonSubmit.disabled=true;
	form.method.value="preview";
	form.action="viewstagetransactionsaction.do";
	form.submit();
}

function fnCancel(form) {
	form.method.value="cancel";
	form.action="viewstagetransactionsaction.do";
	form.submit();
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
<span id="page.id" title="simpleaccounting"></span>
<mifos:NumberFormattingInfo />
<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.SimpleAccountingUIResources"/>
<body onload="fnBankDetail()">
<html-el:form action="/viewstagetransactionsaction.do">

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


					<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
							<tr>
								<td align="left" valign="top" class="paddingleftCreates">

									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr>
											<td class="headingorange">
												<span id="generalLedger.heading" class="heading"> <mifos:mifoslabel name="simpleAccounting.head" bundle="simpleAccountingUIResources"/> - </span>
												<mifos:mifoslabel name="simpleAccounting.glaction" bundle="simpleAccountingUIResources" />

											</td>
										</tr>
										<tr>
											<td class="fontnormal">
												<br>
												<span class="mandatorytext"> <font color="#FF0000">*</font> </span>

												<mifos:mifoslabel name="simpleAccounting.mandatory" bundle="simpleAccountingUIResources"/>
											</td>
										</tr>
									</table>

									<logic:messagesPresent>
										<font class="fontnormalRedBold"><span id="BulkEntry.error.message"> <html-el:errors bundle="simpleAccountingUIResources" /> </span> </font>
										<br>
									</logic:messagesPresent>
									<br>



	<c:set var="transactionDetailIDabc" value="${sessionScope.transactionDetailID}" scope="session"/>



				<table width="93%" border="0" cellpadding="3" cellspacing="0">
					<tr class="fontnormal">
							<td align="right">
			                   <mifos:mifoslabel name="simpleAccounting.trxnDate" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources"/>
			                </td>
			                <td align="left">
			                     <mifos:mifosalphanumtext styleId= "createLoanProduct.input.prdOffering" property="stageTrxnDate" name="viewtransactionstageactionform" size="8"/>
							 &nbsp  <img src="pages/framework/images/mainbox/calendaricon.gif" onclick="displayDatePicker('stageTrxnDate', this);"/>
			                </td>

					</tr>
					<tr class="fontnormal">
						<td align="right"><mifos:mifoslabel name="simpleAccounting.officeHeirarchy" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
						<td align="left">
							<mifos:select name="viewtransactionstageactionform" property="stageOfficeHierarchy" onchange="fnloadOffices(this.form)">
							<html-el:option value="1"><mifos:mifoslabel name="simpleAccounting.headOffice" bundle="simpleAccountingUIResources"/></html-el:option>
							<html-el:option value="2"><mifos:mifoslabel name="simpleAccounting.regionalOffice" bundle="simpleAccountingUIResources"/></html-el:option>
							<html-el:option value="3"><mifos:mifoslabel name="simpleAccounting.divisionalOffice" bundle="simpleAccountingUIResources"/></html-el:option>
							<html-el:option value="4"><mifos:mifoslabel name="simpleAccounting.areaOffice" bundle="simpleAccountingUIResources"/></html-el:option>
							<html-el:option value="5"><mifos:mifoslabel name="simpleAccounting.branchOffice" bundle="simpleAccountingUIResources"/></html-el:option>
							<html-el:option value="6"><mifos:mifoslabel name="simpleAccounting.center" bundle="simpleAccountingUIResources"/></html-el:option>
							<html-el:option value="7"><mifos:mifoslabel name="simpleAccounting.group" bundle="simpleAccountingUIResources"/></html-el:option>
						</mifos:select>
					   </td>
					   <td align="right"><mifos:mifoslabel name="simpleAccounting.office" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
					   <td align="left">
						<mifos:select name="viewtransactionstageactionform" property="stageOffice">
						<c:forEach items="${sessionScope.OfficesOnHierarchy}" var="offices">
						<html-el:option value="${offices.globalOfficeNum}">${offices.displayName}</html-el:option>
						</c:forEach>
						</mifos:select>
					</td>
			 </tr>

			<tr class="fontnormal">
			     <td width="15%" align="right"><mifos:mifoslabel name="simpleAccounting.trxnType" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
							<td width="25%" align="left">


								<mifos:select name="viewtransactionstageactionform" styleId="trxnTypeId" property="stageTrxnType" onchange="fnloadMainAccounts(this.form)">
								<c:if test="${viewtransactionstageactionform.stageTrxnType !='JV'}" >
								<html-el:option value="CR"><mifos:mifoslabel name="simpleAccounting.cashReceipt" bundle="simpleAccountingUIResources" /></html-el:option>
								<html-el:option value="CP"><mifos:mifoslabel name="simpleAccounting.cashPayment" bundle="simpleAccountingUIResources" /></html-el:option>
								<html-el:option value="BR"><mifos:mifoslabel name="simpleAccounting.bankReceipt" bundle="simpleAccountingUIResources" /></html-el:option>
								<html-el:option value="BP"><mifos:mifoslabel name="simpleAccounting.bankPayment" bundle="simpleAccountingUIResources" /></html-el:option>
								</c:if>
							    <c:if test="${viewtransactionstageactionform.stageTrxnType=='JV'}" >
								<html-el:option value="JV" ><mifos:mifoslabel name="simpleAccounting.journalVoucher" bundle="simpleAccountingUIResources" /></html-el:option>
								</c:if>
								</mifos:select>

							</td>
			 <td width="15%" align="right"><mifos:mifoslabel name="simpleAccounting.mainAccount" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
					<td width="25%" align="left">
						<mifos:select name="viewtransactionstageactionform"  property="stageMainAccount" onchange="fnloadAccountHeads(this.form)">
				        <c:forEach items="${sessionScope.MainAccountGlCodes}" var="mainAccount">
				            <html-el:option value="${mainAccount.glcode}">${mainAccount.glname}</html-el:option>
				        </c:forEach>
						</mifos:select>
				</td>
			</tr>

			<tr class="fontnormal">

			<td align="right"><mifos:mifoslabel name="simpleAccounting.accountHead" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
				<td align="left">
					<mifos:select name="viewtransactionstageactionform" property="stageAccountHead">
				        <c:forEach items="${sessionScope.AccountHeadGlCodes}" var="accountHead">
				            <html-el:option value="${accountHead.glcode}">${accountHead.glname}</html-el:option>
				        </c:forEach>
					</mifos:select>
				</td>

				<td align="right"><mifos:mifoslabel name="simpleAccounting.amount" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
				<td align="left">
					  <html-el:text name="viewtransactionstageactionform" property="stageAmount" styleClass="separatedNumber" styleId="simpleaccounting.input.amount"/>
				</td>

			</tr>

		  <tr class="fontnormal">
			<td align="center" colspan="4">
				<div id="bankDetailsId">
				<br>
				  <table width="93%" border="0" cellpadding="3" cellspacing="0" class="burlywoodborder">
				    <tr class="fontnormal">
				      <td align="right"><mifos:mifoslabel name="simpleAccounting.chequeNo" mandatory="no" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
				<td align="left">
					<mifos:mifosnumbertext property="stageChequeNo" style="width:150px" maxlength="60" /></td>
				<td align="right"><mifos:mifoslabel name="simpleAccounting.chequeDate" mandatory="no" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
			                <td align="left">
			                     <mifos:mifosalphanumtext styleId= "createLoanProduct.input.prdOffering" property="chequeDate" name="viewtransactionstageactionform" size="8"/>
							 &nbsp  <img src="pages/framework/images/mainbox/calendaricon.gif" onclick="displayDatePicker('chequeDate', this);"/>
			                </td>

			 </tr>

				<tr class="fontnormal">
			      <td align="right"><mifos:mifoslabel name="simpleAccounting.bankName" mandatory="no" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
					<td align="left">
						<mifos:mifosalphanumtext property="stageBankName" style="width:150px" maxlength="60" />
					</td>
			 <td align="right"><mifos:mifoslabel name="simpleAccounting.bankBranch" mandatory="no" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
				 <td align="left">
					<mifos:mifosalphanumtext property="stageankBranch" style="width:150px" maxlength="60" />
				</td>

			       </tr>

			  </table>
		     <br>
			  </div>
			</td>
			</tr>
			<tr class="fontnormal">
		   <td align="right"><mifos:mifoslabel name="simpleAccounting.trxnNotes" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
		 <td align="left" colspan="3">
		 <mifos:textarea name="viewtransactionstageactionform" property="stageNotes" cols='68'></mifos:textarea>
             </td>
		</tr>
		</table>
			<br>
			<table width="93%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center" class="blueline">
												&nbsp;
											</td>
										</tr>
			</table>

					<html-el:hidden property="method" value="load" />
					<html-el:hidden property="input" value="load" />
				<br>

					<table width="93%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center">
												 <html-el:submit styleId="simpleaccounting.button.submit" styleClass="buttn submit"  onclick="fnSubmit(this.form, this)">
													<mifos:mifoslabel name="simpleAccounting.preview" bundle="simpleAccountingUIResources"/>
											    </html-el:submit>
												&nbsp;
												<html-el:button  styleId="bulkentry.button.cancel" property="cancel" styleClass="cancelbuttn"  onclick="fnCancel(this.form);">
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
