<!--
 
 * createLoanAccount.jsp  version: xxx
 
 
 
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

<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/tags/date" prefix="date"%>

<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
			
		<SCRIPT SRC="pages/application/loan/js/CreateLoanAccount.js"></SCRIPT>
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>		
		<html-el:form method="get" action="/loanAction.do" onsubmit="return fun_submit();">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="470" align="left" valign="top" bgcolor="#FFFFFF">
					<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="heading">&nbsp;</td>
						</tr>
					</table>
					<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
						<tr>
							<td class="bluetablehead">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td width="25%">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/tick.gif" width="17" height="17"></td>
											<td class="timelineboldgray">
												<mifos:mifoslabel name="loan.Select" />
												<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />
												<mifos:mifoslabel name="loan.Slash" />
												<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />

											</td>
										</tr>
									</table>
									</td>
									<td width="25%" align="center">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17"></td>
											<td class="timelineboldorange">
												<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
												<mifos:mifoslabel name="loan.acc_info" />
											</td>
										</tr>
									</table>
									</td>
									<td width="25%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17"></td>
											<td class="timelineboldorangelight">
												<mifos:mifoslabel name="loan.review/edit_ins" />
											</td>
										</tr>
									</table>
									</td>
									<td width="25%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17"></td>
											<td class="timelineboldorangelight">
												<mifos:mifoslabel name="loan.review&submit" /></td>
										</tr>
									</table>
									</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					<html-el:hidden property="input" value="createPage" />
					<html-el:hidden property="method" value="" />
					<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
						<tr>
							<td width="70%" height="24" align="left" valign="top" class="paddingleftCreates">
									<table width="98%" border="0" cellspacing="0" cellpadding="3">
										<tr>
											<td class="headingorange">
												<span class="heading">
													<mifos:mifoslabel name="accounts.create" /><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel name="accounts.account" />&nbsp;-&nbsp;
												</span>
												<mifos:mifoslabel name="loan.Enter" /> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel name="loan.acc_info" />
											</td>
										</tr>
										<tr>
											<td class="fontnormal">
											<mifos:mifoslabel name="loan.Select" />
											<mifos:mifoslabel name="loan.a" />
											<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
												<mifos:mifoslabel name="loan.select_loan_inst" /> <br>
												<font color="#FF0000">*</font>
												<mifos:mifoslabel name="loan.asterisk" />
											</td>
											
										</tr>
										<tr>
											<td>
												<font class="fontnormalRedBold">
													<html-el:errors bundle="loanUIResources" /> 
												</font>
											</td>
										</tr>	
										<tr>
											<td class="fontnormal"><br>
												<span class="fontnormalbold">
													<mifos:mifoslabel name="loan.acc_owner" />:
												</span> 
												<c:out value="${sessionScope.loanAccounts_customerMaster.displayName}" />
											</td>
										</tr>
									</table><br>
									<table width="93%" border="0" cellpadding="3" cellspacing="0">
										<tr class="fontnormal">
											<td width="30%" align="right" class="fontnormal">
												<span class="mandatorytext"></span>
												<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" mandatory="yes" />
												<mifos:mifoslabel name="loan.instancename"  />:
											</td>
											<td width="70%">
												<mifos:select name="loanActionForm" property="selectedPrdOfferingId" style="width:136px;">
													<html-el:options collection="loanPrdOfferings" 
													property="prdOfferingId"
													labelProperty="prdOfferingName" />
												</mifos:select>
											</td>
										</tr>
									</table>
									<table width="93%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center" class="blueline">&nbsp;</td>
										</tr>
									</table>
									<br>

									<table width="93%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center">
												<html-el:submit property="continueBtn" styleClass="buttn" style="width:70px;" >
													<mifos:mifoslabel name="loan.continue" />
												</html-el:submit> &nbsp; 
												
												<html-el:button property="cancelButton" onclick="javascript:fun_cancel(this.form)" styleClass="cancelbuttn" style="width:70px;">
													<mifos:mifoslabel name="loan.cancel" />
												</html-el:button>
											</td>
										</tr>
									</table>
								 <br>
							</td>
						</tr>
					</table><br>
				</td>
				</tr>
			</table>
		</html-el:form>
	</tiles:put>
</tiles:insert>
