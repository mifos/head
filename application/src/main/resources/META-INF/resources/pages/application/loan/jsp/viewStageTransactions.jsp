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
											<script language="javascript">

											</script>

											<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}' />
											<fmt:setBundle basename="org.mifos.config.localizedResources.SimpleAccountingUIResources" />
											<body>
												<html-el:form action="/viewstagetransactionsaction.do?method=approve" onsubmit="refreshpage();">

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

																			<table width="93%" border="0" cellpadding="2" cellspacing="0">
																				<tr>
																					<td class="headingorange">
																						<span id="bulkentry.heading" class="heading">
																							<mifos:mifoslabel name="simpleAccounting.head" /> - </span>
																						<mifos:mifoslabel name="simpleAccounting.viewTransactions" />

																					</td>
																				</tr>
																			</table>
																			<logic:messagesPresent>
																				<font class="fontnormalRedBold">
																					<span id="SimpleAccounting.error.message">
																						<html-el:errors bundle="simpleAccountingUIResources" />
																					</span>
																				</font>
																				<br>
																				</logic:messagesPresent>


																				<br>
																					<table width="97%" cellpadding="0" cellspacing="0" border="0">

																						<c:choose>
																							<c:when test="${ViewGlTransactionPaginaitonVariablesDto.totalNoOfRowsForPagination != 0}">
																								<input type="hidden" name="iPageNo" value="${ViewGlTransactionPaginaitonVariablesDto.iPageNo}">
																									<input type="hidden" name="cPageNo" value="${ViewGlTransactionPaginaitonVariablesDto.cPageNo}">
																										<input type="hidden" name="noOfRecordsPerPage" value="${ViewGlTransactionPaginaitonVariablesDto.noOfRecordsPerPage}">
																											<tr class="fontnormal" bgcolor="#F2A23A">
																												<th>Transaction No</th>
																												<th>Transaction Date</th>
																												<th>Transaction Type</th>
																												<th>officeLevel</th>
																												<th>Display Name</th>
																												<th>Main Account</th>
																												<th>Amount Action</th>
																												<th>Sub Account</th>
																												<th>Transaction Amount</th>



																											</tr>
																											<c:forEach items="${sessionScope.ViewStageTransactionsDtos}" var="viewStageTransactionsDto" >
																											<c:set var="transactionID" value="${viewStageTransactionsDto.transactionID}" scope="session"/>


																												<tr class="fontnormal">
																													<td align="center">${viewStageTransactionsDto.transactionNo}</td>
																													<td align="center">${viewStageTransactionsDto.transactionDate}</td>
																													<td align="center">${viewStageTransactionsDto.transactionType}</td>
																													<td align="center">${viewStageTransactionsDto.officeLevel}</td>
																													<td align="center">${viewStageTransactionsDto.displayName}</td>
																													<td align="center">${viewStageTransactionsDto.mainAccount}</td>
																													<td align="center">${viewStageTransactionsDto.amountAction}</td>
																													<td align="center">${viewStageTransactionsDto.subAccount}</td>

																													<td align="center">
																														<fmt:formatNumber value="${viewStageTransactionsDto.transactionAmount}" />
																													</td>
																													<!--
			      <td>${viewStageTransactionsDto.narration}</td>
			      -->
																													<td>
																														<a href="approvetransactionsaction.do?method=approve&txnNo=${viewStageTransactionsDto.transactionNo}">Approve</a>
																														<a href="viewstagetransactionsaction.do?method=reject&txnNo=${viewStageTransactionsDto.transactionNo}">Reject</a>
																														<a href="viewstagetransactionsaction.do?method=edit&txnNo=${viewStageTransactionsDto.transactionNo}">Edit</a>
																														<!--
																														<html-el:button value="Approve" property="" onclick="doApprove(${viewStageTransactionsDto.transactionNo});"/>
																														<html-el:button value="Reject" property="" onclick="doReject()"/>
																														<html-el:button value="Edit" property="" onclick="doEdit()"/>
-->
																														<td>
																														</tr>
																													</c:forEach>

																													<tr class="fontnormal">
																														<td colspan="10">
																															<div>
																																<c:if test="${(ViewGlTransactionPaginaitonVariablesDto.cPage * ViewGlTransactionPaginaitonVariablesDto.noOfPagesIndex)-(ViewGlTransactionPaginaitonVariablesDto.noOfPagesIndex)>0}">
																																	<a href="./viewstagetransactionsaction.do?method=load&iPageNo=${ViewGlTransactionPaginaitonVariablesDto.prePageNo}&cPageNo=${ViewGlTransactionPaginaitonVariablesDto.prePageNo}">
																																		<< Previous</a>&nbsp &nbsp
				</c:if>
																																		<c:forEach var="page" begin="${((ViewGlTransactionPaginaitonVariablesDto.cPage*ViewGlTransactionPaginaitonVariablesDto.noOfPagesIndex)-(ViewGlTransactionPaginaitonVariablesDto.noOfPagesIndex-1))}" end="${(ViewGlTransactionPaginaitonVariablesDto.cPage*ViewGlTransactionPaginaitonVariablesDto.noOfPagesIndex)}">
																																			<c:choose>
																																				<c:when test="${page ==((ViewGlTransactionPaginaitonVariablesDto.iPageNo/ViewGlTransactionPaginaitonVariablesDto.noOfRecordsPerPage)+1)}">
																																					<a href="./viewstagetransactionsaction.do?method=load&iPageNo=${page}" style="cursor:pointer;color: red">
																																						<b>${page}</b>
																																					</a>
																																				</c:when>
																																				<c:when test="${page <= ViewGlTransactionPaginaitonVariablesDto.iTotalPages}">
																																					<a href="./viewstagetransactionsaction.do?method=load&iPageNo=${page}">
																																						<b>${page}</b>
																																					</a>
																																				</c:when>
																																			</c:choose>
																																		</c:forEach>
																																		<c:if test="${ViewGlTransactionPaginaitonVariablesDto.iTotalPages > ViewGlTransactionPaginaitonVariablesDto.noOfPagesIndex && ViewGlTransactionPaginaitonVariablesDto.i< ViewGlTransactionPaginaitonVariablesDto.iTotalPages}">
				    &nbsp &nbsp<a href="./viewstagetransactionsaction.do?method=load&iPageNo=${ViewGlTransactionPaginaitonVariablesDto.i}&cPageNo=${ViewGlTransactionPaginaitonVariablesDto.i}"> Next</a>
																																		</c:if>
																																		<br>
																																			<b>Rows Showing:${ViewGlTransactionPaginaitonVariablesDto.startRecordCurrentPage} - ${ViewGlTransactionPaginaitonVariablesDto.endRecordCurrentPage} ,Total Results:${ViewGlTransactionPaginaitonVariablesDto.totalNoOfRowsForPagination} </b>
																																		</div>
																																	</td>
																																</tr>
																															</c:when>
																															<c:otherwise>
																																<tr>
																																	<td colspan=10>
																																		<p>
																																			<mifos:mifoslabel name="simpleAccounting.viewTransactionsNoRecords" />
																																		</p>
																																	</td>
																																</tr>
																															</c:otherwise>
																														</c:choose>
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


																														<br>

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
