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
<script src="pages/js/jquery/jquery-1.4.2.min.js"></script>
<script language="javascript">


function fnSubmit(form, buttonSubmit) {
	buttonSubmit.disabled=true;
	form.method.value="submit";
	form.action="voucherBranchMappingAction.do";
	form.submit();
}

function fnCancel(form) {
	form.method.value="cancel";
	form.action="generalledgeraction.do";
	form.submit();
}

function fnEditTransaction(form) {
	form.method.value="previous";
	form.action="voucherBranchMappingAction.do";
	form.submit();
}

</script>

<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.SimpleAccountingUIResources"/>

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

												<mifos:mifoslabel name="simpleAccounting.review" />

											</td>
										</tr>
										<tr>
											<td class="fontnormal">
												<br>
												<mifos:mifoslabel name="simpleAccounting.reviewNotes" />
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
							<th align="right">
			                   <mifos:mifoslabel name="simpleAccounting.trxnDate" mandatory="yes" isColonRequired="Yes"/>
			                </th>
			                <td align="left">
			                    ${VoucherBranchMappingActionForm.transactiondate}
			                </td>

					</tr>
					<tr class="fontnormal">
						<th align="right"><mifos:mifoslabel name="simpleAccounting.empty"    /></th>
						<td align="left">

					   </td>
					   <th align="right"><mifos:mifoslabel name="simpleAccounting.office" mandatory="yes" isColonRequired="Yes"/></th>
					   <td align="left">
				    <c:forEach items="${sessionScope.OfficesOnHierarchy}" var="offices">
							<c:if test="${offices.globalOfficeNum==VoucherBranchMappingActionForm.branch}">
				                          ${offices.displayName}
							</c:if>
					 </c:forEach>

					</td>
			 </tr>


			<tr class="fontnormal">
			     <th width="15%" align="right"><mifos:mifoslabel name="simpleAccounting.trxnType" mandatory="yes" isColonRequired="Yes"/></th>
							<td width="25%" align="left">
								<c:if test="${VoucherBranchMappingActionForm.transactiontype=='CR'}">
								  <mifos:mifoslabel name="simpleAccounting.cashReceipt" />
								</c:if>
						                <c:if test="${VoucherBranchMappingActionForm.transactiontype=='CP'}">
								  <mifos:mifoslabel name="simpleAccounting.cashPayment" />
								</c:if>
						                <c:if test="${VoucherBranchMappingActionForm.transactiontype=='BR'}">
								  <mifos:mifoslabel name="simpleAccounting.bankReceipt" />
								</c:if>
						                <c:if test="${VoucherBranchMappingActionForm.transactiontype=='BP'}">
								  <mifos:mifoslabel name="simpleAccounting.bankPayment" />
								</c:if>
							</td>
			 <th width="15%" align="right"><mifos:mifoslabel name="simpleAccounting.mainAccount" mandatory="yes" isColonRequired="Yes"/></th>
					<td width="25%" align="left">
				  <c:forEach items="${sessionScope.MainAccountGlCodes}" var="mainaccount">
							<c:if test="${mainaccount.glcode==VoucherBranchMappingActionForm.mainAccount}">
				                          ${mainaccount.glname}
							</c:if>
				  </c:forEach>
				    </td>
			</tr>


		  <tr class="fontnormal">
			<td align="center" colspan="4">
				<div id="bankDetailsId">
				<br>
				  <table width="93%" border="0" cellpadding="3" cellspacing="0" class="burlywoodborder">
				    <tr class="fontnormal">
				      <th align="center"><mifos:mifoslabel name="simpleAccounting.chequeNo" mandatory="no" isColonRequired="Yes"/></th>
					<td align="left">
					${VoucherBranchMappingActionForm.chequeNo}
				<th align="center"><mifos:mifoslabel name="simpleAccounting.chequeDate" mandatory="no" isColonRequired="Yes"/></th>
					<td align="left">
						${VoucherBranchMappingActionForm.chequeDate}
					</td>
			 </tr>

				<tr class="fontnormal">
			      <th align="center"><mifos:mifoslabel name="simpleAccounting.bankName" mandatory="no" isColonRequired="Yes"/></td>
					<td align="left">
						${VoucherBranchMappingActionForm.bankName}
					</td>
			 <th align="center"><mifos:mifoslabel name="simpleAccounting.bankBranch" mandatory="no" isColonRequired="Yes"/></th>
				 <td align="left">
					${VoucherBranchMappingActionForm.bankBranch}
				</td>
			</tr>
			  </table>
		     <br>
			  </div>
			</td>
			</tr>

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
                        <html-el:text property="amount" name="voucherbranchmappingform" styleClass="separatedNumber" value="${accountHead.amounts}" readonly="true" styleId="${accountHead.sno}" onchange="javascript:addNumbers('${accountHead.sno}')"/>
                    </td>
                    <td>
                        <html-el:text property="transactionnotes" name="voucherbranchmappingform" styleClass="separatedNumber" value="${accountHead.trannotes}" styleId="simpleaccounting.input.transactionnotes" />
                    </td>
                    <td>

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
                     <html-el:text  property="total" styleId="answer"  name="voucherbranchmappingform" value='${VoucherBranchMappingActionForm.total}'/>
                </th>
                <th>

                </th>
            </tr>
        </table>

		     </table>
		<tr class="fontnormal">
		 <td align="left" colspan='2'>
			 <html-el:button property="editButton" styleClass="insidebuttn" onclick="fnEditTransaction(this.form)">
					<mifos:mifoslabel name="simpleAccounting.editTransaction" />
			 </html-el:button>
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
					<%-- <html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" /> --%>
				<br>

					<table width="93%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center">
												 <html-el:submit styleId="simpleaccounting.button.preview" styleClass="buttn"  onclick="fnSubmit(this.form, this)">
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
