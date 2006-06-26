<!-- 

/**

 * viewChangeLog_loanAccount.jsp    version: 1.0

 

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

 */

-->

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom" %>
<%@taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<script>
		function fun_return(form)
					{
						form.action="loanAction.do?method=get&globalAccountNum=${requestScope.loan.globalAccountNum}";
						form.submit();
					}
					
	</script>
		<html-el:form method="post" action="/loanAction.do">
			
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt">
						<mifoscustom:getLoanHeader loanHeader='${sessionScope.header_get}'/>
							<html-el:link action="loanAction.do?method=get&globalAccountNum=${requestScope.loan.globalAccountNum}">
								<c:out value="${requestScope.loan.loanOffering.prdOfferingName}" />
							</html-el:link></span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" height="24" align="left" valign="top"
						class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
									<td>
										<font class="fontnormalRedBold">
											<html-el:errors bundle="loanUIResources" /> 
										</font>
									</td>
						</tr>
						<tr>
							<td class="headingorange">
								<span class="heading">
									<c:out value="${requestScope.loan.loanOffering.prdOfferingName}" />&nbsp;#
											<c:out value="${requestScope.loan.globalAccountNum}" />-&nbsp;  
								</span>
								<mifos:mifoslabel name="loan.change_log"/>
							</td>
						</tr>

						<tr>
							<td class="fontnormal"><br>
								<mifos:mifoslabel name="loan.rec_creation_date" />:&nbsp;
								<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.loan.createdDate)}" />
							</td>
						</tr>
					</table>
					<br>
					<mifoscustom:mifostabletag moduleName="accounts/loan" scope="request" source="LoanChangeLogList" xmlFileName="LoanChangeLog.xml" passLocale="true"/>
					<br>
					<table width="96%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:button property="returnToAccountDetailsbutton"
								onclick="javascript:fun_return(this.form)"
								styleClass="buttn" style="width:165px;">
								<mifos:mifoslabel name="loan.returnToAccountDetails"
									bundle="loanUIResources" />
							</html-el:button></td>
						</tr>
					</table>
					</td>

				</tr>
			</table>
			<br>
			

		</html-el:form>
	</tiles:put>
</tiles:insert>
