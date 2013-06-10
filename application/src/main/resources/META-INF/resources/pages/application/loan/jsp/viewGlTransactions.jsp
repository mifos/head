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
<script type="text/javascript" src="pages/js/datePicker.js"></script>
<link rel="stylesheet" type="text/css" href="pages/css/datepicker/calendar.css" />
<script language="javascript">
function fnSubmit(form, buttonSubmit) {
	buttonSubmit.disabled=true;
	form.method.value="submit";
	form.action="viewgltransactionsaction.do";
	form.submit();
}

function fnCancel(form) {
	form.method.value="cancel";
	form.action="viewgltransactionsaction.do";
	form.submit();
}

</script>

<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.SimpleAccountingUIResources"/>
<body>
<html-el:form action="/viewgltransactionsaction.do">

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
							<td align="right">
			                   <mifos:mifoslabel name="simpleAccounting.fromTrxnDate" mandatory="yes" isColonRequired="Yes"/>
			                </td>
			                <td align="left">
			                 <mifos:mifosalphanumtext styleId= "createLoanProduct.input.prdOffering" property="fromTrxnDate" size="10"/>
							 &nbsp  <img src="pages/framework/images/mainbox/calendaricon.gif" onclick="displayDatePicker('fromTrxnDate', this);"/>
			                </td>
					</tr>
					<tr class="fontnormal">
							<td align="right">
			                   <mifos:mifoslabel name="simpleAccounting.toTrxnDate" mandatory="yes" isColonRequired="Yes"/>
			                </td>
			                <td align="left">
			                 <mifos:mifosalphanumtext styleId= "createLoanProduct.input.prdOffering" property="toTrxnDate" size="10"/>
							 &nbsp  <img src="pages/framework/images/mainbox/calendaricon.gif" onclick="displayDatePicker('toTrxnDate', this);"/>
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

					<html-el:hidden property="method" value="get" />
					<%-- <html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" /> --%>
				<br>

					<table width="93%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center">
												 <html-el:submit styleId="simpleaccounting.button.submit" styleClass="buttn"  onclick="fnSubmit(this.form, this)">
													<mifos:mifoslabel name="simpleAccounting.submit"/>
											    </html-el:submit>
												&nbsp;
												<html-el:button  styleId="bulkentry.button.cancel" property="cancel" styleClass="cancelbuttn"  onclick="fnCancel(this.form);">
													<mifos:mifoslabel name="simpleAccounting.cancel"/>
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
