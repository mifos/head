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
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".clientsacclayoutmenu">
	<tiles:put name="body" type="string">
	<span id="page.id" title="StatusSearchResults" />

		<script>
  	function submitData(form) {
 		form.action="ChangeAccountStatus.do?method=update";
		form.submit();
 	}
	function selectAll(x) {
		for(var i=0,l=x.form.length; i<l; i++)
		{
			if(x.form[i].type == 'checkbox' && x.form[i].name != 'selectAll1'){
				x.form[i].checked=x.checked
			}
		}
	}

	function selectAllCheck(x){
		var checked = true;
		for(var i=0,l=x.form.length; i<l; i++){
			if(x.form[i].type == 'checkbox' && x.form[i].name != 'selectAll1'){
				if(x.form[i].checked == false){
					checked = false;
				}
			}
		}
		for(var i=0,l=x.form.length; i<l; i++){
			if(x.form[i].type == 'checkbox' && x.form[i].name == 'selectAll1'){
				x.form[i].checked = checked;
			}
		}
		
		
	}
 	
 	</script>

		<html-el:form action="/ChangeAccountStatus">

			<table width="95%" border="0" cellpadding="0" cellspacing="0" class="paddingleftmain">
				<tr>
					<td>
						<span class="headingorange"><mifos:mifoslabel name="accountStatus.changeaccountstatus" /> <br> <br>
					</td>
				</tr>
				<tr>
					<td>
						<span class="headingorange"> <c:set value="0" var="count" /> <c:forEach var="loan" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'searchResults')}" varStatus="loopStatus1">
								<c:set value="${loopStatus1.count}" var="count" />
							</c:forEach> <span class="headingorange">${count} <mifos:mifoslabel name="accountStatus.results" /> <mifos:mifoslabel name="accountStatus.in" /> </span> <span class="heading"> <c:forEach items="${sessionScope.allBranches}" var="office">
									<c:if test="${office.officeId == sessionScope.accountStatusActionForm.officeId}">
										<c:out value="${office.officeName}" />
									</c:if>
								</c:forEach> </span> <span class="headingorange"> <mifos:mifoslabel name="accountStatus.foundfor" /> </span> <span class="heading"> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />s </span> <span class="headingorange"> <mifos:mifoslabel
									name="accountStatus.in" /> </span> <span class="heading"> <c:set var="accountState" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanAccountStates')}" /> <c:out value="${accountState.name}" /> </span> <br>
					</td>
				</tr>
				<!--  here -->
				<tr>
					<td valign="middle">
						<font class="fontnormalRedBold"> <span id="statussearchresults.error.message"><html-el:errors bundle="loanUIResources" /></span> </font>
					</td>
				</tr>
				<tr>

					<td align="left" valign="top" class="fontnormal">
						<table width="95%" border="0" cellpadding="5" cellspacing="0" class="fontnormal">

							<tr class="bglightblue" valign="middle" height="35">
								<td width="5%" class="drawtablerowboldnolinebg">
									<input id="statussearchresults.input.selectAll" type="checkbox" onclick="selectAll(this)" name="selectAll1" />
								</td>

								<td width="15%" class="drawtablerowboldnolinebg">
									<b><mifos:mifoslabel name="accountStatus.currentstatus" /></b>
								</td>

								<td width="15%" class="drawtablerowboldnolinebg">
									<b><mifos:mifoslabel name="accountStatus.acctid" /></b>
								</td>

								<td width="15%" class="drawtablerowboldnolinebg">
									<b><mifos:mifoslabel name="accountStatus.loanamount" /></b>
								</td>

								<td width="15%" class="drawtablerowboldnolinebg">
									<b><mifos:mifoslabel name="accountStatus.disbursaldate" /></b>
								</td>

								<td class="drawtablerowboldnolinebg"">
									<b><mifos:mifoslabel name="accountStatus.accountowner" /></b>
								</td>
							</tr>


							<c:forEach var="loan" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'searchResults')}" varStatus="loopStatus1">
								<bean:define id="ctr1" toScope="request">
									<c:out value="${loopStatus1.index}" />
								</bean:define>

								<tr>
									<td width="5%" valign="top" class="drawtablerow">
										<html-el:checkbox styleId="statussearchresults.input.select" property="accountRecords[${ctr1}]" value="${loan.accountId}" onclick="selectAllCheck(this)" />
									</td>

									<td width="15%" valign="top" class="drawtablerow">
										<mifoscustom:MifosImage id="${loan.accountState.id}" moduleName="accounts.loan" />
										<c:out value="${loan.accountState.name}" />
									</td>

									<td width="15%" valign="top" class="drawtablerow">
										<html-el:link styleId="statussearchresults.link.viewLoanAccount" href="loanAccountAction.do?method=get&globalAccountNum=${loan.globalAccountNum}">
											<mifos:mifoslabel name="accountStatus.account" />
											<c:out value="${loan.globalAccountNum}" />
										</html-el:link>
									</td>
									<td width="15%" valign="top" class="drawtablerow">
										<c:out value="${loan.loanAmount}" />
									</td>
									<td width="15%" valign="top" class="drawtablerow">
										<c:out value='${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,loan.disbursementDate)}' />
									</td>


									<td valign="top" class="drawtablerow">
										<c:if test="${loan.customer.customerLevel.id==1}">
											<span class="fontnormalbold"> <mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />: </span>
										</c:if>
										<c:if test="${loan.customer.customerLevel.id==2}">
											<span class="fontnormalbold"> <mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />: </span>
										</c:if>
										<c:out value="${loan.customer.displayName}" />
										<mifos:mifoslabel name="accountStatus.id" />
										<c:out value="${loan.customer.globalCustNum}" />
										<br>
										<span class="fontnormalbold"> <mifos:mifoslabel name="accountStatus.loanofficer" /> </span>
										<c:out value="${loan.customer.personnel.displayName}" />
										<br>


										<c:out value="${loan.customer.office.officeName}" />
										<c:if test="${loan.customer.parentCustomer.parentCustomer.displayName != null}">
											<mifos:mifoslabel name="accountStatus.hierarchydelimiter" />
										</c:if>
										<c:if test="${loan.customer.customerLevel.id==1}">
											<c:out value="${loan.customer.parentCustomer.parentCustomer.displayName}" />
										</c:if>
										<c:if test="${loan.customer.parentCustomer.displayName != null}">
											<mifos:mifoslabel name="accountStatus.hierarchydelimiter" />
										</c:if>
										<c:out value="${loan.customer.parentCustomer.displayName}" />
										<br>

									</td>
								</tr>

							</c:forEach>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<br>
						<br>
						<br>


						<table width="95%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td align="right" width="11%" valign="top" class="fontnormal">
									<mifos:mifoslabel name="accountStatus.newstatus" mandatory="yes" />
								</td>
								<td align="left" width="89%" valign="top">
									<html-el:select property="newStatus" style="width:136px;">
										<html-el:option value="3"><mifos:mifoslabel name="accountStatus.approved" /></html-el:option>
									</html-el:select>
								</td>
							</tr>
							<tr>
								<td align="right" valign="top" width="11%" class="fontnormal">
									<span id="statussearchresults.label.note">
									<mifos:mifoslabel name="accountStatus.note" mandatory="yes" /></span>
								</td>
								<td>
									<html-el:textarea styleId="statussearchresults.input.note" style="width:320px; height:110px;" property="comments">
									</html-el:textarea>
								</td>
							</tr>

						</table>
					</td>
				</tr>
				<tr>
					<td>
						<table width="96%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center" class="blueline">
									&nbsp;
								</td>
							</tr>
						</table>
						<br>
					</td>
				</tr>
				<tr>

					<td align="center">
						<html-el:button styleId="statussearchresults.button.submit" property="cancel" styleClass="buttn"  onclick="submitData(this.form);">
							<mifos:mifoslabel name="accountStatus.submit" />
						</html-el:button>
					</td>
				</tr>

			</table>
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>

