
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".clientsacclayoutmenu">
	<tiles:put name="body" type="string">

		<script>

	
  	function submitData(form) {
		form.action="ChangeAccountStatus.do?method=searchResults";
		form.submit();
 	}
 	function getLoanOfficers(form){
 		if(document.getElementsByName("officeId")[0].selectedIndex!=0) {
	 		form.action="ChangeAccountStatus.do?method=getLoanOfficers";
 			form.submit();
 		}
 	}
 	
	</script>



		<html-el:form action="/ChangeAccountStatus">
			<table width="100%" border="0" cellpadding="2" cellspacing="0" class="paddingleftmain">
				<tr>
					<td>
						<span class="headingorange"> <mifos:mifoslabel name="accountStatus.changeaccountstatus" /> <br> <br>
					</td>
				</tr>
			</table>




			<table width="95%" border="0" cellpadding="7" cellspacing="0">
				<tr>
					<td valign="top">
						<table width="100%" border="0" cellspacing="0" cellpadding="4">
							<tr class="fontnormal">
								<td width="100%" colspan="2" class="bglightblue">
									<span class="heading"><mifos:mifoslabel name="accountStatus.search" /></span>
								</td>
							</tr>

						</table>
				<tr>
					<td>
						<font class="fontnormalRedBold"> <html-el:errors bundle="loanUIResources" /> </font>
					</td>
				</tr>

				<tr class="bluetablehead">

					<td align="left" valign="top" class="fontnormal">
						<table width="50%" border="0" cellpadding="4" cellspacing="0">
							<tr>
								<td align="right" valign="top" class="fontnormal" width="36%">
									<mifos:mifoslabel name="accountStatus.branch" mandatory="yes" />
								</td>
								<td>
									<mifos:select style="width:136px;" property="officeId" onchange="getLoanOfficers(this.form)">
										<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'OfficesBranchOffices')}" var="branchOffice">
											<html-el:option value="${branchOffice.officeId}">${branchOffice.officeName}</html-el:option>
										</c:forEach>
									</mifos:select>
								</td>
							</tr>
							<tr>
								<td align="right" class="fontnormal">
									<mifos:mifoslabel name="accountStatus.loanOfficer" mandatory="yes" />
								</td>
								<td>
									<mifos:select style="width:136px;" property="personnelId">
										<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanOfficers')}" var="loanOfficer">
											<html-el:option value="${loanOfficer.personnelId}">${loanOfficer.displayName}</html-el:option>
										</c:forEach>
									</mifos:select>
								</td>
							</tr>
							<tr>
								<td align="right" class="fontnormal">
									<mifos:mifoslabel name="accountStatus.type" mandatory="yes" />
								</td>
								<td>
									<html-el:select property="type" style="width:136px;">
										<html-el:option value="0">
											<mifos:mifoslabel name="accountStatus.loan" />
										</html-el:option>
									</html-el:select>
								</td>
							</tr>
							<tr>
								<td align="right" class="fontnormal">
									<mifos:mifoslabel name="accountStatus.currentstatus" mandatory="yes" />
								</td>
								<td>
									<c:set var="accountState" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanAccountStates')}" />
									<html-el:select property="currentStatus" style="width:136px;">
										<c:choose>
											<c:when test="${accountState != null}">
												<html-el:option value="${accountState.id}">
													<c:out value="${accountState.name}" />
												</html-el:option>
											</c:when>
											<c:otherwise>
												<html-el:option value=""><mifos:mifoslabel name="accountStatus.select" /></html-el:option>
											</c:otherwise>
										</c:choose>
									</html-el:select>
								</td>
							</tr>

							<tr>
								<td></td>
								<td align="left">
									<html-el:button property="cancel" styleClass="buttn"  onclick="submitData(this.form);">
										<mifos:mifoslabel name="accountStatus.search" />
									</html-el:button>
								</td>
							</tr>

						</table>

					</td>
				</tr>
			</table>
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
