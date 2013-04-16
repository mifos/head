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
											<script type="text/javascript" src="pages/js/datePicker.js">
											</script>
											<link rel="stylesheet" type="text/css" href="pages/css/datepicker/calendar.css" />
											<script language="javascript">

function fnSubmit(form, buttonSubmit) {
	buttonSubmit.disabled=true;
	form.method.value="process";
	form.action="processaccountingtransactionsaction.do";
	form.submit();
}

function fnCancel(form) {
	form.method.value="cancel";
	form.action="processaccountingtransactionsaction.do";
	form.submit();
}

function fnEditTransaction(form) {
	form.method.value="previous";
	form.action="processaccountingtransactionsaction.do";
	form.submit();
}

function fnloadDate(form) {
	form.method.value="loadLastUpdatedDate";
	form.action="processaccountingtransactionsaction.do";
	form.submit();
}

</script>

											<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}' />
											<fmt:setBundle basename="org.mifos.config.localizedResources.SimpleAccountingUIResources" />

											<html-el:form action="/processaccountingtransactionsaction.do">

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
																					<span id="bulkentry.heading" class="heading">
																						<mifos:mifoslabel name="simpleAccounting.head" /> - </span>
																					<mifos:mifoslabel name="simpleAccounting.processMifosAccountingTransactions" />

																				</td>
																			</tr>
																			<tr>
																				<font class="fontnormalRedBold">
																					<span id="reviewapplypayment.error.message">
																						<html-el:errors bundle="accountsUIResources" />
																					</font>
																					<td>
																						<b>
																							<font size='2' color="red">
																								<mifos:mifoslabel name="simpleAccounting.processMifosNote" />
																							</font>
																						</b>
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
																								<mifos:mifoslabel name="simpleAccounting.office" mandatory="yes" isColonRequired="Yes" />
																							</td>

																							<td align="left">

																								<c:choose>
																									<c:when test="${sessionScope.officeLevelId=='1'}">

																										<mifos:select property="office" onchange="fnloadDate(this.form)">
																											<c:forEach items="${sessionScope.DynamicOfficesOnHierarchy}" var="offices">
																												<html-el:option value="${offices.globalOfficeNumber}">${offices.displayName}</html-el:option>
																											</c:forEach>
																										</mifos:select>
																									</c:when>

																									<c:when test="${sessionScope.officeLevelId=='5'}">
																										<html-el:text property="groupBy" value="${sessionScope.DynamicOfficesOnHierarchy.displayName}" readonly="true" />
																									</c:when>
																								</c:choose>
																							</td>

																						</tr>
																						<tr class="fontnormal">
																							<td align="right">
																								<mifos:mifoslabel name="simpleAccounting.lastProcessDate" mandatory="yes" isColonRequired="Yes" />
																							</td>
																							<td align="left">
																								<date:datetag property="lastProcessDate" isDisabled="Yes" renderstyle="simple" />
																							</td>
																						</tr>

																						<tr class="fontnormal">
																							<td align="right">
																								<mifos:mifoslabel name="simpleAccounting.processTillDate" mandatory="yes" isColonRequired="Yes" />
																							</td>
																							<td align="left">
																								<mifos:mifosalphanumtext styleId="createLoanProduct.input.prdOffering" property="processTillDate" size="10" />
							 &nbsp  <img src="pages/framework/images/mainbox/calendaricon.gif" onclick="displayDatePicker('processTillDate', this);" />
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
																						<html-el:hidden property="method" value="preview" />
																						<html-el:hidden property="input" value="preview" />
																						<br>

																							<table width="93%" border="0" cellpadding="0" cellspacing="0">
																								<tr>
																									<td align="center">
																										<html-el:submit styleId="simpleaccounting.button.process" styleClass="buttn" onclick="fnSubmit(this.form, this)">
																											<mifos:mifoslabel name="simpleAccounting.process" />
																										</html-el:submit>
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