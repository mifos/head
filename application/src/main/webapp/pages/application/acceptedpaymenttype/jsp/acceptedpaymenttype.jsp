<%--
Copyright (c) 2005-2009 Grameen Foundation USA
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

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/date" prefix="date"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>

<script language="javascript">
  
 
</script>


<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<span style="display: none" id="page.id">AcceptedPaymentType</span>
		<SCRIPT SRC="pages/framework/js/logic.js"></SCRIPT>
		<html-el:form action="acceptedPaymentTypeAction"  >
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt">
							<html-el:link styleId="acceptedpaymenttype.link.admin" href="acceptedPaymentTypeAction.do?method=cancel&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="acceptedPaymentType.admin" />
							</html-el:link> 
						</span> / <span class="fontnormal8ptbold"><mifos:mifoslabel name="acceptedPaymentType.defineacceptedpaymenttype" /> </span>
					</td>
				</tr>
			</table>
			
			<table cellspacing="0" cellpadding="0" width="95%" border="0">
        	<tbody>
            <tr>
                <td class="paddingL15T15" valign="top" align="left" width="70%">
                    <table cellspacing="0" cellpadding="3" width="98%" border="0">
                        <tbody>
                            <tr>
                                <td class="headingorange" colspan="2">
                                    <mifos:mifoslabel name="acceptedPaymentType.defineacceptedpaymenttype" /><br />
                                </td>
                            </tr>
                            <tr>
                                <td class="fontnormal" valign="top" colspan="2">
                                    <mifos:mifoslabel name="acceptedPaymentType.instruction1" /><br />
                                    <mifos:mifoslabel name="acceptedPaymentType.instruction2" /></td>
                            </tr>
                            
                            <tr>
                                <td class="fontnormal" valign="top" colspan="2">&nbsp;</td>
                                
                            </tr>
                            <tr>
                                <td class="fontnormalbold" valign="top" colspan="2">
                                    <mifos:mifoslabel name="acceptedPaymentType.clientgroupcenter" /></td>
                            </tr>
                            <tr>
                                <td valign="top" align="right" width="35%">
                                    <span class="fontnormal"><mifos:mifoslabel name="acceptedPaymentType.fee"  isColonRequired="Yes"/> </span>
                                </td>
                                <td valign="top" width="65%">
                                    <table cellspacing="0" cellpadding="0" width="86%" border="0">
                                        <tbody>
                                            <tr>
                                                <td width="28%">
                                                <c:set var="inFeeList" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'inFeeList')}"/> 
												<c:set var="outFeeList" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'outFeeList')}"/> 
                                                    <mifos:MifosSelect property="fees" leftListName="feeLeftList" addButtonName="feeAddButton" removeButtonName="feeRemoveButton"
														input="inFeeList" output="outFeeList" property1="id"
														property2="name" multiple="true" spacedOut="true">
													</mifos:MifosSelect> 
                                                </td>
                                               
                                            </tr>
                                        </tbody>
                                    </table>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    <br />
                    <table cellspacing="0" cellpadding="3" width="98%" border="0">
                        <tbody>
                            <tr>
                                <td class="fontnormalbold" valign="top" colspan="2">
                                    <mifos:mifoslabel name="acceptedPaymentType.loan"  isColonRequired="Yes"/></td>
                            </tr>
                            <tr>
                                <td valign="top" align="right" width="35%">
                                    <span class="fontnormal"><mifos:mifoslabel name="acceptedPaymentType.disbursement"  isColonRequired="Yes"/> </span>
                                </td>
                                <td valign="top" width="65%">
                                    <table cellspacing="0" cellpadding="0" width="86%" border="0">
                                        <tbody>
                                            <tr>
                                                <td width="28%">
                                                <c:set var="inDisbursementList" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'inDisbursementList')}"/> 
												<c:set var="outDisbursementList" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'outDisbursementList')}"/> 
                                                    <mifos:MifosSelect property="disbursements" leftListName="disbursementLeftList" addButtonName="disbursementAddButton" removeButtonName="disbursementRemoveButton"
														input="inDisbursementList" output="outDisbursementList" property1="id"
														property2="name" multiple="true" spacedOut="true">
													</mifos:MifosSelect> 
                                                </td>
                                               
               
                                            </tr>
                                        </tbody>
                                    </table>
                                </td>
                            </tr>
                            <tr>
                                <td class="fontnormal" valign="top" align="right">
                                    <mifos:mifoslabel name="acceptedPaymentType.repayment"  isColonRequired="Yes"/></td>
                                <td valign="top">
                                    <table cellspacing="0" cellpadding="0" width="86%" border="0">
                                        <tbody>
                                            <tr>
                                            <td width="28%">
                                                <c:set var="inRepaymentList" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'inRepaymentList')}"/> 
												<c:set var="outRepaymentList" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'outRepaymentList')}"/> 
                                                    <mifos:MifosSelect property="repayments" leftListName="repaymentLeftList" addButtonName="repaymentAddButton" removeButtonName="repaymentRemoveButton"
														input="inRepaymentList" output="outRepaymentList" property1="id"
														property2="name" multiple="true" spacedOut="true">
													</mifos:MifosSelect> 
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    <br />
                    <table cellspacing="0" cellpadding="3" width="98%" border="0">
                        <tbody>
                            <tr>
                                <td class="fontnormalbold" valign="top" colspan="2">
                                    <mifos:mifoslabel name="acceptedPaymentType.saving"  isColonRequired="Yes"/></td>
                            </tr>
                            <tr>
                                <td class="fontnormal" valign="top" align="right" width="35%">
                                     <mifos:mifoslabel name="acceptedPaymentType.withdrawal"  isColonRequired="Yes"/></td>
                                <td valign="top" width="65%">
                                    <table cellspacing="0" cellpadding="0" width="86%" border="0">
                                        <tbody>
                                            <tr>
                                                <td width="28%">
                                                <c:set var="inWithdrawalList" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'inWithdrawalList')}"/> 
												<c:set var="outWithdrawalList" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'outWithdrawalList')}"/> 
                                                    <mifos:MifosSelect property="withdrawals" leftListName="withdrawalLeftList" addButtonName="withdrawalAddButton" removeButtonName="withdrawalRemoveButton"
														input="inWithdrawalList" output="outWithdrawalList" property1="id"
														property2="name" multiple="true" spacedOut="true">
													</mifos:MifosSelect> 
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </td>
                            </tr>
                            <tr>
                                <td class="fontnormal" valign="top" align="right">
                                    <mifos:mifoslabel name="acceptedPaymentType.deposit"  isColonRequired="Yes"/></td>
                                <td valign="top">
                                    <table cellspacing="0" cellpadding="0" width="86%" border="0">
                                        <tbody>
                                            <tr>
                                                <td width="28%">
                                                <c:set var="inDepositList" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'inDepositList')}"/> 
												<c:set var="outDepositList" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'outDepositList')}"/> 
                                                    <mifos:MifosSelect property="deposits" leftListName="depositLeftList" addButtonName="depositAddButton" removeButtonName="depositRemoveButton"
														input="inDepositList" output="outDepositList" property1="id"
														property2="name" multiple="true" spacedOut="true">
													</mifos:MifosSelect> 
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    <table width="98%" border="0" cellpadding="0" cellspacing="0">
		              <tr>
		                <td class="blueline">&nbsp;                </td>
		              </tr>
		            </table>
		            <br>
                    <table cellspacing="0" cellpadding="0" width="98%" border="0">
                        <tbody>
                            <tr>
                                <td align="center">&nbsp; <html-el:submit styleId="acceptedpaymenttype.button.submit" property="submitBtn"
										styleClass="buttn" onclick="transferData(this.form.fees);transferData(this.form.disbursements);transferData(this.form.repayments);transferData(this.form.deposits);transferData(this.form.withdrawals);">
										<mifos:mifoslabel name="acceptedPaymentType.Submit">
											</mifos:mifoslabel>
									</html-el:submit> &nbsp; <html-el:button styleId="acceptedpaymenttype.button.cancel" property="cancelBtn"
										styleClass="cancelbuttn" 
										onclick="location.href='acceptedPaymentTypeAction.do?method=cancel&currentFlowKey=${requestScope.currentFlowKey}'">
										<mifos:mifoslabel name="acceptedPaymentType.Cancel">
											</mifos:mifoslabel>
									</html-el:button></td>
                            </tr>
                        </tbody>
                    </table>
                    <br />
                </td>
            </tr>
        </tbody>
    </table>
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<html-el:hidden property="method" value="update" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
