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
										<tiles:put name="body" type="string">
											<!-- <span id="page.id" title="CustomerList"></span> -->
											<script src="pages/js/jquery/jquery-1.4.2.min.js">
											</script>
											<script type="text/javascript" src="pages/js/separator.js">
											</script>
											<script type="text/javascript" src="pages/js/datePicker.js">
											</script>
											<link rel="stylesheet" type="text/css" href="pages/css/datepicker/calendar.css" />
											<script language="javascript">
function fnloadFromOffices(form) {

	form.method.value="loadFromOffices";
	form.action="interofficetransferaction.do";
	form.submit();
}

function fnloadToOffices(form) {

	form.method.value="loadToOffices";
	form.action="interofficetransferaction.do";
	form.submit();
}

function fnloadCreditAccounts(form) {
	form.method.value="loadCreditAccount";
	form.action="interofficetransferaction.do";
	form.submit();
}


function fnSubmit(form, buttonSubmit) {
	buttonSubmit.disabled=true;
	form.method.value="preview";
	form.action="interofficetransferaction.do";
	form.submit();
}

function fnCancel(form) {
	form.method.value="cancel";
	form.action="interofficetransferaction.do";
	form.submit();
}

function fnloadCreditAccounts(form) {
	form.method.value="loadCreditAccount";
	form.action="interofficetransferaction.do";
	form.submit();
}



</script>
											<span id="page.id" title="simpleaccounting">
											</span>
											<mifos:NumberFormattingInfo />
											<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}' />
											<fmt:setBundle basename="org.mifos.config.localizedResources.SimpleAccountingUIResources" />
											<body onload="fnBankDetail()">
												<html-el:form action="/interofficetransferaction.do">

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
																						<span id="generalLedger.heading" class="heading">
																							<mifos:mifoslabel name="simpleAccounting.head" bundle="simpleAccountingUIResources" /> - </span>
																						<mifos:mifoslabel name="simpleAccounting.glaction" bundle="simpleAccountingUIResources" />

																					</td>
																				</tr>
																				<tr>
																					<td class="fontnormal">
																						<br>
																							<span class="mandatorytext">
																								<font color="#FF0000">*</font>
																							</span>
																							<mifos:mifoslabel name="simpleAccounting.mandatory" bundle="simpleAccountingUIResources" />
																						</td>
																					</tr>
																				</table>

																				<logic:messagesPresent>
																					<font class="fontnormalRedBold">
																						<span id="BulkEntry.error.message">
																							<html-el:errors bundle="simpleAccountingUIResources" />
																						</span>
																					</font>
																					<br>
																					</logic:messagesPresent>
																					<br>


																						<table width="93%" border="0" cellpadding="3" cellspacing="0">
																							<tr class="fontnormal">
																								<td align="right">
																									<mifos:mifoslabel name="simpleAccounting.trxnDate" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources" />
																								</td>
																								<td align="left">
																									<mifos:mifosalphanumtext styleId="createLoanProduct.input.prdOffering" property="trxnDate" size="8" />
							 &nbsp  <img src="pages/framework/images/mainbox/calendaricon.gif" onclick="displayDatePicker('trxnDate', this);" />
																								</td>
																							</tr>

																							<tr class="boldFont">

																								<td align="left">
																									<mifos:mifoslabel name="simpleAccounting.fromInterOfficeTransfer" bundle="simpleAccountingUIResources" />
																								</td>
																							</tr>

																							<tr class="fontnormal">
																								<td align="right">
																									<mifos:mifoslabel name="simpleAccounting.officeHeirarchy" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources" />
																								</td>
																								<td align="left">
																									<mifos:select property="fromOfficeHierarchy" onchange="fnloadFromOffices(this.form)">
																										<html-el:option value="1">
																											<mifos:mifoslabel name="simpleAccounting.headOffice" bundle="simpleAccountingUIResources" />
																										</html-el:option>
																										<html-el:option value="2">
																											<mifos:mifoslabel name="simpleAccounting.regionalOffice" bundle="simpleAccountingUIResources" />
																										</html-el:option>
																										<html-el:option value="3">
																											<mifos:mifoslabel name="simpleAccounting.divisionalOffice" bundle="simpleAccountingUIResources" />
																										</html-el:option>
																										<html-el:option value="4">
																											<mifos:mifoslabel name="simpleAccounting.areaOffice" bundle="simpleAccountingUIResources" />
																										</html-el:option>
																										<html-el:option value="5">
																											<mifos:mifoslabel name="simpleAccounting.branchOffice" bundle="simpleAccountingUIResources" />
																										</html-el:option>
																										<html-el:option value="6">
																											<mifos:mifoslabel name="simpleAccounting.center" bundle="simpleAccountingUIResources" />
																										</html-el:option>
																										<html-el:option value="7">
																											<mifos:mifoslabel name="simpleAccounting.group" bundle="simpleAccountingUIResources" />
																										</html-el:option>
																									</mifos:select>
																								</td>
																								<td align="right">
																									<mifos:mifoslabel name="simpleAccounting.office" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources" />
																								</td>
																								<td align="left">
																									<mifos:select property="fromOffice">
																										<c:forEach items="${sessionScope.IOFromOfficeHierarchy}" var="fromOffices">
																											<html-el:option value="${fromOffices.globalOfficeNum}">${fromOffices.displayName}</html-el:option>
																										</c:forEach>
																									</mifos:select>
																								</td>
																							</tr>

																							<tr class="boldFont">

																								<td align="left">
																									<mifos:mifoslabel name="simpleAccounting.toInterOfficeTransfer" bundle="simpleAccountingUIResources" />
																								</td>
																							</tr>

																							<tr class="fontnormal">
																								<td align="right">
																									<mifos:mifoslabel name="simpleAccounting.officeHeirarchy" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources" />
																								</td>
																								<td align="left">
																									<mifos:select property="toOfficeHierarchy" onchange="fnloadToOffices(this.form)">
																										<html-el:option value="1">
																											<mifos:mifoslabel name="simpleAccounting.headOffice" bundle="simpleAccountingUIResources" />
																										</html-el:option>
																										<html-el:option value="2">
																											<mifos:mifoslabel name="simpleAccounting.regionalOffice" bundle="simpleAccountingUIResources" />
																										</html-el:option>
																										<html-el:option value="3">
																											<mifos:mifoslabel name="simpleAccounting.divisionalOffice" bundle="simpleAccountingUIResources" />
																										</html-el:option>
																										<html-el:option value="4">
																											<mifos:mifoslabel name="simpleAccounting.areaOffice" bundle="simpleAccountingUIResources" />
																										</html-el:option>
																										<html-el:option value="5">
																											<mifos:mifoslabel name="simpleAccounting.branchOffice" bundle="simpleAccountingUIResources" />
																										</html-el:option>
																										<html-el:option value="6">
																											<mifos:mifoslabel name="simpleAccounting.center" bundle="simpleAccountingUIResources" />
																										</html-el:option>
																										<html-el:option value="7">
																											<mifos:mifoslabel name="simpleAccounting.group" bundle="simpleAccountingUIResources" />
																										</html-el:option>
																									</mifos:select>
																								</td>
																								<td align="right">
																									<mifos:mifoslabel name="simpleAccounting.office" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources" />
																								</td>
																								<td align="left">
																									<mifos:select property="toOffice">
																										<c:forEach items="${sessionScope.IOToOfficeHierarchy}" var="toOffices">
																											<html-el:option value="${toOffices.globalOfficeNum}">${toOffices.displayName}</html-el:option>
																										</c:forEach>
																									</mifos:select>
																								</td>
																							</tr>

																							<tr class="fontnormal">


																								<td width="15%" align="right">
																									<mifos:mifoslabel name="simpleAccounting.debitAccountHead" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources" />
																								</td>
																								<td width="25%" align="left">
																									<mifos:select property="debitAccountHead" onchange="fnloadCreditAccounts(this.form)">
																										<c:forEach items="${sessionScope.InterOfficeDebitAccountGlCodes}" var="mainAccount">
																											<html-el:option value="${mainAccount.glcode}">${mainAccount.glname}</html-el:option>
																										</c:forEach>
																									</mifos:select>
																								</td>

																								<td align="right">
																									<mifos:mifoslabel name="simpleAccounting.creditAccountHead" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources" />
																								</td>
																								<td align="left">
																									<mifos:select property="creditAccountHead">
																										<c:forEach items="${sessionScope.InterOfficeDebitAccountGlCodes}" var="accountHead">
																											<html-el:option value="${accountHead.glcode}">${accountHead.glname}</html-el:option>
																										</c:forEach>
																									</mifos:select>
																								</td>
																							</tr>

																							<tr class="fontnormal">



																								<td align="right">
																									<mifos:mifoslabel name="simpleAccounting.amount" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources" />
																								</td>
																								<td align="left">
																									<html-el:text property="amount" styleClass="separatedNumber" styleId="simpleaccounting.input.amount" />
																								</td>

																							</tr>

																							<tr class="fontnormal">
																								<td align="">
																									<mifos:mifoslabel name="simpleAccounting.trxnNotes" mandatory="yes" isColonRequired="Yes" bundle="simpleAccountingUIResources" />
																								</td>
																								<td align="left" colspan="3">
																									<mifos:textarea property="notes" cols='68'>
																									</mifos:textarea>
																								</td>
																							</tr>


																						</table>
																						<br>
																						</div>
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
																								<html-el:submit styleId="simpleaccounting.button.submit" styleClass="buttn submit" onclick="fnSubmit(this.form, this)">
																									<mifos:mifoslabel name="simpleAccounting.preview" bundle="simpleAccountingUIResources" />
																								</html-el:submit>
												&nbsp;
												<html-el:button styleId="bulkentry.button.cancel" property="cancel" styleClass="cancelbuttn" onclick="fnCancel(this.form);">
																									<mifos:mifoslabel name="simpleAccounting.cancel" bundle="simpleAccountingUIResources" />
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
