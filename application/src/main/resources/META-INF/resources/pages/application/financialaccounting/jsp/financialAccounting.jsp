<%--
Copyright (c) 2005-2011 Grameen Foundation USA
1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
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
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>


<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.adminUIResources"/>
<body onload="fnAccountDetail(${accountingActivationStatus})">
<tiles:insert definition=".financialAccountingLayout">
	<tiles:put name="body" type="string">
        <span id="page.id" title="financialAccounting" ></span>
		<html-el:form action="/FinancialAccountingAction.do">
			<td valign="top" bgcolor="#FFFFFF"
				style="padding-left:8px; padding-top:10px;">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">

				<tr>
					<td colspan="2" valign="top"><span id="financialAccounting.label.financialacctasks"
						class="headingorange"><mifos:mifoslabel name="financialAccounting.financialaccountingtasks" bundle="FinancialAccountingUIResources" /></span><br>
					<span class="fontnormal"><span id="financialAccounting.text.welcome"><mifos:mifoslabel name="financialAccounting.welcometomifos" bundle="FinancialAccountingUIResources"/></span></span></td>
				</tr>
				<tr width="100%">
					<td valign="top" colspan="2"><font class="fontnormalRedBold"><span id="financialAccounting.error.message"><html-el:errors
						bundle="FinancialAccountingUIResources" /></span> </font></td>
				</tr>
				<tr>
					<td width="48%" height="240" valign="top"
						class="paddingleft"><span class="headingorange"><mifos:mifoslabel name="financialAccounting.accounting" bundle="FinancialAccountingUIResources" />
						</span><br>
					<span class="fontnormalbold"> <mifos:mifoslabel name="financialAccounting.master" bundle="FinancialAccountingUIResources" /></span><br>
					<table width="90%" border="0" cellspacing="0" cellpadding="0">
						<tr class="fontnormal">
                    <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                    <td width="97%"><a href="openbalanceaction.do?method=load"><mifos:mifoslabel name="financialAccounting.defineOpenBalance" bundle="FinancialAccountingUIResources"/></a></td>
                  </tr>
						<tr class="fontnormal">
                   <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                    <td width="97%"><a href="coaBranchMappingAction.do?method=load"><mifos:mifoslabel name="financialAccounting.coaBranchMapping" bundle="FinancialAccountingUIResources"/></a></td>
                 </tr>
					</table>
					<br>
					<span class="fontnormalbold"> <span class="fontnormalbold"><mifos:mifoslabel name="financialAccounting.transactions" bundle="FinancialAccountingUIResources"/>
					</span><br>
					</span>
					<table width="90%" border="0" cellspacing="0" cellpadding="0">
						 <tr class="fontnormal">
                      <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                      <td width="97%"><a href="singlegeneralledgeraction.do?method=load"><mifos:mifoslabel name="financialAccounting.singleglaction" bundle="FinancialAccountingUIResources"/></a></td>
                  </tr>
                   <tr class="fontnormal">
                      <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                      <td width="97%"><a href="multiplegeneralledgeraction.do?method=load"><mifos:mifoslabel name="financialAccounting.mulglaction" bundle="FinancialAccountingUIResources"/></a></td>
                  </tr>
                  <tr class="fontnormal">
                    <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                    <td width="97%"><a href="journalvoucheraction.do?method=load"><mifos:mifoslabel name="financialAccounting.jvaction" bundle="FinancialAccountingUIResources"/></a></td>
                  </tr>
                  <tr class="fontnormal">
                    <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                    <td width="97%"><a href="interofficetransferaction.do?method=load"><mifos:mifoslabel name="financialAccounting.interOfficeTransafer" bundle="FinancialAccountingUIResources"/></a></td>
                  </tr>
                  <tr class="fontnormal">
                   <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                    <td width="97%"><a href="voucherBranchMappingAction.do?method=load"><mifos:mifoslabel name="financialAccounting.voucherBranchMapping" bundle="FinancialAccountingUIResources"/></a></td>
                  </tr>
					</table>
					<br>
					<span class="fontnormalbold"><mifos:mifoslabel name="financialAccounting.stageandAudit" bundle="FinancialAccountingUIResources"/> </span><br>
					<table width="90%" border="0" cellspacing="0" cellpadding="0">
						<tr class="fontnormal">
                    <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                    <td width="97%"><a href="viewstagetransactionsaction.do?method=load"><mifos:mifoslabel name="financialAccounting.viewStageTransactions" bundle="FinancialAccountingUIResources"/></a></td>
                  </tr>
                  <tr class="fontnormal">
                    <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                    <td width="97%"><a href="audittransactionsaction.do?method=load"><mifos:mifoslabel name="financialAccounting.audit" bundle="FinancialAccountingUIResources"/></a></td>
                  </tr>
                  <tr class="fontnormal">
                    <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                    <td width="97%"><a href="viewgltransactionsaction.do?method=load"><mifos:mifoslabel name="financialAccounting.viewGlTransaction" bundle="FinancialAccountingUIResources"/></a></td>
                  </tr>
					</table>
					 <br>
					<span class="fontnormalbold"><mifos:mifoslabel name="financialAccounting.processes" bundle="FinancialAccountingUIResources"/> </span><br>
						<table width="90%" border="0" cellspacing="0" cellpadding="0">
						<tr class="fontnormal">
                    <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                    <td width="97%"><a href="processaccountingtransactionsaction.do?method=load"><mifos:mifoslabel name="financialAccounting.processTransactions" bundle="FinancialAccountingUIResources"/></a></td>
                  </tr>
						<tr class="fontnormal">
                   <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                    <td width="97%"><a href="consolidatedTransactionAction.do?method=load"><mifos:mifoslabel name="financialAccounting.consolidatedTransaction" bundle="FinancialAccountingUIResources"/></a></td>
                  </tr> 
                  <tr class="fontnormal">
                    <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                    <td width="97%"><a href="yearEndProcessAction.do?method=load"><mifos:mifoslabel name="financialAccounting.yearEndProcess" bundle="FinancialAccountingUIResources"/></a></td>
                  </tr>

						</table>
                    <br>
                    <table width="90%" border="0" cellspacing="0" cellpadding="0">
                        <!--<tr class="fontnormal">
			<td align="center" colspan="2">
				<div id="accountDetailsId">
				 <table width="100%" border="0" cellspacing="0" cellpadding="0">
               </table>
			</div>
			</td>
			</tr>
                    </table>

					<!-- spacer -->
					<table width="90%" border="0" cellspacing="0" cellpadding="0">
						<tr><td>&nbsp;</td></tr>
					</table>
					<table width="90%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td>
							<table width="90%" border="0" cellspacing="0" cellpadding="0">
							</table>
                            <br>
						</td>
					</tr>
					</table>
					<table width="90%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td>
													<table width="90%" border="0" cellspacing="0" cellpadding="0">
								<tr class="fontnormal">

								</tr>
							</table>
                            <table width="90%" border="0" cellspacing="0" cellpadding="0">

							</table>

							</td>
					</tr>
					</table>
					<br />
					</td>
					<td width="52%" bgcolor="#FFFFFF" valign="top" class="paddingleft">
					<table width="90%" border="0" cellspacing="0" cellpadding="0">


					</table>
					<br>










			</td>
			</tr>
			</table>
		</html-el:form>
		</body>
	</tiles:put>
</tiles:insert>
