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
<tiles:insert definition=".create">
<tiles:put name="body" type="string" >
<!-- <span id="page.id" title="CustomerList"></span> -->
<script src="pages/js/jquery/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="pages/js/separator.js"></script>
<script type="text/javascript" src="pages/js/datePicker.js"></script>
<link rel="stylesheet" type="text/css" href="pages/css/datepicker/calendar.css" />
<script language="javascript">
function fnloadOffices(form) {

	form.method.value="loadOffices";
	form.action="generalledgeraction.do";
	form.submit();
}

function fnloadMainAccounts(form) {
	form.method.value="loadMainAccounts";
	form.action="generalledgeraction.do";
	form.submit();

}

function fnloadAccountHeads(form) {
	form.method.value="loadAccountHeads";
	form.action="generalledgeraction.do";
	form.submit();
}

function fnSubmit(form, buttonSubmit) {
	buttonSubmit.disabled=true;
	form.method.value="preview";
	form.action="generalledgeraction.do";
	form.submit();
}

function fnCancel(form) {
	form.method.value="cancel";
	form.action="generalledgeraction.do";
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
<html-el:form action="/generalledgeraction.do">

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
										<tr class="fontnormal">
										    <td>
										        <c:choose>
								                     <c:when test="${status=='success'}">
										                <font color="#FF0000" ><mifos:mifoslabel name="simpleAccounting.status" bundle="simpleAccountingUIResources"/></font>
										             </c:when>
										         </c:choose>
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
							<td align="right">
			                   <mifos:mifoslabel name="simpleAccounting.trxnDate" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources"/>
			                </td>
			                <td align="left">
			                     <mifos:mifosalphanumtext styleId= "createLoanProduct.input.prdOffering" property="trxnDate" size="8"/>
							 &nbsp  <img src="pages/framework/images/mainbox/calendaricon.gif" onclick="displayDatePicker('trxnDate', this);"/>
			                </td>

					</tr>
					<tr class="fontnormal">
						<td align="right"><mifos:mifoslabel name="simpleAccounting.officeHeirarchy" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
						<td align="left">
							<mifos:select property="officeHierarchy" onchange="fnloadOffices(this.form)">
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
						<mifos:select property="office">
						<c:forEach items="${sessionScope.OfficesOnHierarchy}" var="offices">
						<html-el:option value="${offices.globalOfficeNum}">${offices.displayName}</html-el:option>
						</c:forEach>
						</mifos:select>
					</td>
			 </tr>

			<tr class="fontnormal">
			     <td width="15%" align="right"><mifos:mifoslabel name="simpleAccounting.trxnType" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
							<td width="25%" align="left">
								<mifos:select styleId="trxnTypeId" property="trxnType" onchange="fnloadMainAccounts(this.form)">
								<html-el:option value="CR"><mifos:mifoslabel name="simpleAccounting.cashReceipt" bundle="simpleAccountingUIResources" /></html-el:option>
								<html-el:option value="CP"><mifos:mifoslabel name="simpleAccounting.cashPayment" bundle="simpleAccountingUIResources" /></html-el:option>
								<html-el:option value="BR"><mifos:mifoslabel name="simpleAccounting.bankReceipt" bundle="simpleAccountingUIResources" /></html-el:option>
								<html-el:option value="BP"><mifos:mifoslabel name="simpleAccounting.bankPayment" bundle="simpleAccountingUIResources" /></html-el:option>
								</mifos:select>
							</td>
			 <td width="15%" align="right"><mifos:mifoslabel name="simpleAccounting.mainAccount" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
					<td width="25%" align="left">
						<mifos:select property="mainAccount" onchange="fnloadAccountHeads(this.form)">
				        <c:forEach items="${sessionScope.MainAccountGlCodes}" var="mainAccount">
				            <html-el:option value="${mainAccount.glCode}">${mainAccount.glDesc}</html-el:option>
				        </c:forEach>
						</mifos:select>
				</td>
			</tr>

			<tr class="fontnormal">

			<td align="right"><mifos:mifoslabel name="simpleAccounting.accountHead" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
				<td align="left">
					<mifos:select property="accountHead">
				        <c:forEach items="${sessionScope.AccountHeadGlCodes}" var="accountHead">
				            <html-el:option value="${accountHead.glCode}">${accountHead.glDesc}</html-el:option>
				        </c:forEach>
					</mifos:select>
				</td>

				<td align="right"><mifos:mifoslabel name="simpleAccounting.amount" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
				<td align="left">
					  <html-el:text property="amount" styleClass="separatedNumber" styleId="simpleaccounting.input.amount"/>
				</td>

			</tr>
			<!-- start Woriking Zone -->

		  <tr class="fontnormal">
			<td align="center" colspan="4">
				<div id="bankDetailsId">
				<br>
				  <table width="93%" border="0" cellpadding="3" cellspacing="0" class="burlywoodborder">
				    <tr class="fontnormal">
				      <td align="right"><mifos:mifoslabel name="simpleAccounting.chequeNo" mandatory="no" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
				<td align="left">
					<mifos:mifosnumbertext property="chequeNo" style="width:150px" maxlength="60" /></td>
				<td align="right"><mifos:mifoslabel name="simpleAccounting.chequeDate" mandatory="no" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
					<td align="left">
						<date:datetag property="chequeDate" isDisabled="No" renderstyle="simple"/></td>
			 </tr>

				<tr class="fontnormal">
			      <td align="right"><mifos:mifoslabel name="simpleAccounting.bankName" mandatory="no" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
					<td align="left">
						<mifos:mifosalphanumtext property="bankName" style="width:150px" maxlength="60" />
					</td>
			 <td align="right"><mifos:mifoslabel name="simpleAccounting.bankBranch" mandatory="no" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
				 <td align="left">
					<mifos:mifosalphanumtext property="bankBranch" style="width:150px" maxlength="60" />
				</td>

			       </tr>
			  </table>
		     <br>
			  </div>
			</td>
			</tr>
			<tr class="fontnormal">
		   <td align="right" colspan="1"><mifos:mifoslabel name="simpleAccounting.trxnNotes" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources"/></td>
		 <td align="left">
		 <mifos:textarea property="notes"></mifos:textarea>
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
					<%-- <html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" /> --%>
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
