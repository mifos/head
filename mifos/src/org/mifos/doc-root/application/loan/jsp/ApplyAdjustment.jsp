<!--
 
 * ApplyAdjustment.jsp  version: xxx
 
 
 
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
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">

		<script>
			function fun_cancel(form)
						{
						form.action="loanAction.do";
						form.method.value="get";
						form.submit();
						}
		</script>
		<html-el:form action="/applyAdjustmentAction.do?method=update">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt">
							<mifoscustom:getLoanHeader loanHeader='${sessionScope.header_get}'/>
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
										<c:out value="${param.globalAccountNum}" />&nbsp;-&nbsp;
									</span>
									<mifos:mifoslabel name="loan.apply_adjustment" />
								</td>
							</tr>
						</table>
						<br>
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormal">
									<mifos:mifoslabel name="loan.lastpaymentdescription" /><br><br>
									<mifos:mifoslabel name="loan.lastpaymentamount" />:&nbsp;
									<c:out value="${requestScope.Adjustment.amount}" /><br><br>
									<html-el:checkbox property="amount" name="Adjustment">
	          							<mifos:mifoslabel name="loan.checkboxpaymentvalue"  />
	          						</html-el:checkbox><br><br>
								</td>
							</tr>
							<tr>
								<td width="5%" valign="top" class="fontnormal">
									<mifos:mifoslabel name="loan.notes" />:<br>
								</td>
								<td width="95%" class="fontnormal">
									<html-el:textarea property="note" name="Adjustment" style="width:320px; height:110px;" />
								</td>
							</tr>
						</table>
						<table width="750" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center" class="blueline">&nbsp;</td>
							</tr>
						</table>
						<br>
						<table width="95%" border="0" cellspacing="0" cellpadding="1">
							<tr>
								<td align="center">
									<html-el:submit styleClass="buttn" 
										style="width:75px;" >
										<mifos:mifoslabel name="loan.submit" />
									</html-el:submit> &nbsp; 
									<html-el:button property="cancelBtn" styleClass="cancelbuttn" 
										style="width:70px;" onclick="javascript:fun_cancel(this.form)">
										<mifos:mifoslabel name="loan.cancel" />
									</html-el:button>
								</td>
							</tr>
						</table>
						<html-el:hidden property="accountId" value="${requestScope.Adjustment.accountId}"/>
						<html-el:hidden property="paymentId" value="${requestScope.Adjustment.paymentId}"/>
						<html-el:hidden property="amount" value="${requestScope.Adjustment.amount}"/>
						<html-el:hidden property="personnelId" value="${requestScope.Adjustment.personnelId}"/>
						<html-el:hidden property="method" value=""/>
					</td>
				</tr>
			</table>
			<br>
		</html-el:form>
	</tiles:put>
</tiles:insert>
