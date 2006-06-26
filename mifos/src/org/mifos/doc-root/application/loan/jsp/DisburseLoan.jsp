<!--
 
 * DisburseLoan.jsp  version: xxx
 
 
 
 * Copyright © 2005-2006 Grameen Foundation USA
 
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
 
 * All rights reserved.
 
 
 
 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 
 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *
 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 
 
 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 
 
 * and how it is applied. 
 
 *
 
 -->

<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	
	<script>
			function fun_return(form)
					{
						form.action="loanAction.do";
						form.method.value="get";
						form.submit();
					}
	</script>
	<SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>
		<html-el:form  action="loanDisbursmentAction.do?method=preview&globalAccountNum=${param.globalAccountNum}" 
				onsubmit="return (validateMyForm(disbursmentDate,disbursmentDateFormat,disbursmentDateYY) && validateMyForm(receiptDate,receiptDateFormat,receiptDateYY))"	focus="disbursmentDate">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">  
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt">
							<mifoscustom:getLoanHeader loanHeader='${sessionScope.header_get}'/>
						</span>
						<span class="fontnormal8pt">
							<html-el:link href="loanAction.do?method=get&globalAccountNum=${param.globalAccountNum}">
									<c:out value="${param.prdOfferingName}" />
							</html-el:link>
						</span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" height="24" align="left" valign="top" class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="70%" class="headingorange">
								<span class="heading">
										<c:out value="${param.prdOfferingName}" />&nbsp;#&nbsp;
										<c:out value="${param.globalAccountNum}" />
									&nbsp;-&nbsp;
								</span> 
								<mifos:mifoslabel name="loan.repay" /><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
							</td>
						</tr>
						<tr>
                			<td class="fontnormal"> 
                				<span class="mandatorytext">
                					<font color="#FF0000">*</font>
                				</span>
                				<mifos:mifoslabel name="loan.asterisk" />
                			</td>
              			</tr>
					</table>
					<br>
					<table width="95%" border="0" cellspacing="0" cellpadding="3">
						
								<font class="fontnormalRedBold">
									<html-el:errors bundle="LoanUIResources" /> 
								</font>
						<tr>
							<td colspan="2" align="right" class="fontnormal"><img src="pages/framework/images/trans.gif" width="10" height="2"></td>
						</tr>
						<tr>
                			<td align="right" class="fontnormal">
                				<mifos:mifoslabel name="loan.dateofdisb/payment" mandatory="true"/>:&nbsp;
                			</td>
                			<td class="fontnormal">
                				<date:datetag property="disbursmentDate" name="LoanDisbursment"/>
                			</td>
              			</tr> 
              			<tr>
			                <td align="right" class="fontnormal">
			                	<mifos:mifoslabel name="loan.receiptId" />:&nbsp;
			                </td>
			                <td class="fontnormal">
								<mifos:mifosalphanumtext property="receiptId" />
							</td>
              			</tr>
              			<tr>
			                <td align="right" class="fontnormal">
			                	<mifos:mifoslabel name="loan.receiptdate" />:&nbsp;
			                </td>
			                <td class="fontnormal">
								<date:datetag property="receiptDate" name="LoanDisbursment"/>
							</td>
			            </tr>
			            <tr>
                			<td colspan="2" class="fontnormalbold">
                				<mifos:mifoslabel name="loan.disbdetails"  />
                			</td>
                		</tr>
                		<tr>
			                <td width="29%" align="right" class="fontnormal">
			                	<mifos:mifoslabel name="${ConfigurationConstants.LOAN}"  /><mifos:mifoslabel name="loan.amt"  />:&nbsp;
							</td>
			                <td width="71%">
			                	<mifos:mifosdecimalinput  property="loanAmount" name="LoanDisbursment" />
                			</td>
              			</tr>
              			<tr>
	              			<td align="right" class="fontnormal">
	              				<mifos:mifoslabel name="loan.mode_of_payment" mandatory="yes" />:&nbsp;
	                  		</td>
	                  		<td>
	                  		    <c:set var="paymentModeList" scope="request" value="${requestScope.paymentType.lookUpMaster}" />
								<mifos:select property="disbursmentModeOfPayment" style="width:136px;">
									<html-el:options collection="paymentModeList" property="id" labelProperty="lookUpValue" />
								</mifos:select>
							</td>
                  		</tr>
                  	</table>
                  	<table width="95%" border="0" cellspacing="0" cellpadding="3">
		                <tr>
		                	<td colspan="2" align="right" class="fontnormal">
		                		<img src="pages/framework/images/trans.gif" width="10" height="2">
		                	</td>
		                </tr>
		                <tr>
		                	<td colspan="2" class="fontnormalbold">
		                		<mifos:mifoslabel name="loan.paymentdetails" />
		                	</td>
		                </tr>
		                <tr>
		                	<td width="29%" align="right" class="fontnormal">
		                		<mifos:mifoslabel name="loan.amount" />:&nbsp;
		                	</td>
		                	<td width="71%">
		                		<mifos:mifosdecimalinput property="interestAmount"  name="LoanDisbursment" />
		                	</td>
		                </tr>
                  		<tr>
	              			<td align="right" class="fontnormal">
	              				<mifos:mifoslabel name="loan.mode_of_payment" mandatory="yes" />:&nbsp;
	                  		</td>
	                  		<td>
								<mifos:select property="paymentModeOfPayment" style="width:136px;">
									<html-el:options collection="paymentModeList" property="id" labelProperty="lookUpValue" />
								</mifos:select>
							</td>
                  		</tr>
					</table>
					<table width="96%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
						<tr>
							<td align="center">&nbsp;</td>
						</tr>
						<tr>
							<td align="center">
								<html-el:submit styleClass="buttn" style="width:130px;" >
									<mifos:mifoslabel name="loan.reviewtransaction" />
								</html-el:submit> &nbsp;
								<html-el:button property="cancelButton" styleClass="cancelbuttn" style="width:65px;" 
									onclick="javascript:fun_return(this.form)">
									<mifos:mifoslabel name="loan.cancel" />
								</html-el:button>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			<br>
				<html-el:hidden property="prdOfferingName" value="${param.prdOfferingName}"/> 
				<html-el:hidden property="globalAccountNum" value="${param.globalAccountNum}"/> 
				<html-el:hidden property="accountId" value="${param.accountId}"/>
				<html-el:hidden property="method" value=""/>
</html-el:form>

	</tiles:put>
</tiles:insert>

