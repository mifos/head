<!-- 

/**

 * viewallclosedaccounts.jsp    version: 1.0

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

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
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<script language="javascript">
function fnCancel(form){
	form.action="closedaccsearchaction.do?method=cancel";
	form.submit();
}
</script>
		<html-el:form action="closedaccsearchaction.do?method=search">
			  <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td class="bluetablehead05">
	            <span class="fontnormal8pt">
	            	<a href="CustomerSearchAction.do?method=getOfficeHomePage&officeId=<c:out value="${sessionScope.linkValues.customerOfficeId}"/>&officeName=<c:out value="${sessionScope.linkValues.customerOfficeName}"/>&loanOfficerId=<c:out value="${requestScope.Context.userContext.id}"/>">
	            	<c:out value="${sessionScope.linkValues.customerOfficeName}"/></a> /
	            </span>
	            <!-- Name of the client -->
	            <span class="fontnormal8pt">
	            <a href="centerAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.globalCustNum}"/>">
	           				<c:out value="${sessionScope.linkValues.customerName}"/>            	
           			</a>
	            </span>
	                        </td>
          </tr>
        </table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" height="24" align="left" valign="top"
						class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange"><span class="heading"> <c:out
								value="${sessionScope.linkValues.customerName}" /> - </span><mifos:mifoslabel
								name="client.closedacc" bundle="ClientUIResources" /></td>
						</tr>
					</table>
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="blueline"><img src="images/trans.gif" width="5"
								height="3"></td>
						</tr>
						<c:if test="${!empty requestScope.ClosedSavingsAccountsList}">
							<tr class="fontnormal">
	                			<td height="30">
	                				<span class="heading">
	                					<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" />
	                				</span>
	                			</td>
	              			</tr>
						</c:if>
						<c:forEach items="${requestScope.ClosedSavingsAccountsList}" var="closedSavingsAccount">
							<tr class="fontnormal">
								<td width="97%" height="30">
								<table width="90%" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td width="63%" align="left" valign="top"><span
											class="fontnormal"> </span>
										<table width="100%" border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td width="50%"><span class="fontnormal"> <html-el:link
													href="savingsAction.do?method=get&globalAccountNum=${closedSavingsAccount.globalAccountNum}">
													<c:out value="${closedSavingsAccount.savingsOffering.prdOfferingName}" />,&nbsp;
													<c:out value="Acct #" />
													<c:out value="${closedSavingsAccount.globalAccountNum}" />
												</html-el:link> </span></td>
												<td width="50%"><span class="fontnormal"> <img
													src="pages/framework/images/status_closedblack.gif"
													width="8" height="9">
													<c:out	value="${closedSavingsAccount.accountState.name}" /> </span></td>
											</tr>
										</table>
										<span class="fontnormal">
										<mifos:mifoslabel name="client.date"
											bundle="ClientUIResources" /> &nbsp;<c:out
											value="${closedSavingsAccount.accountState.name}" />:&nbsp;
										<c:out
											value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,closedSavingsAccount.closedDate)}" />
										</span></td>
									</tr>
								</table>
								<table width="50%" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td><img src="pages/framework/images/trans.gif" width="10"
											height="10"></td>
									</tr>
								</table>
								</td>
							</tr>
						</c:forEach>
					</table>
					<c:if test="${!empty requestScope.ClosedSavingsAccountsList || !empty requestScope.ClosedAccountsList}">
						<table width="96%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center" class="blueline">&nbsp;</td>
							</tr>
						</table>
					</c:if>
					<br>
					<table width="96%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:button property="cancel"
								styleClass="buttn" style="width:135px;"
								onclick="javascript:fnCancel(this.form)">
								<mifos:mifoslabel name="client.butbachdetpage"
									bundle="ClientUIResources" />
							</html-el:button></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
		</html-el:form>
	</tiles:put>
</tiles:insert>
