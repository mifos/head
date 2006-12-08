<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/mifos/customtags" prefix="customtable"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<script language="javascript">
	function fun_return(form)
		{
			form.action="loanAccountAction.do?method=get";
			form.submit();
		}
</script>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<html-el:form action="loanAccountAction.do">
		<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />	
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <customtags:headerLink />
					</span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" height="24" align="left" valign="top"
						class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange"><span class="heading"> <c:out
								value="${BusinessKey.loanOffering.prdOfferingName}" />
							#<c:out value="${BusinessKey.globalAccountNum}" /> -
							</span> <mifos:mifoslabel name="Account.StatusHistory"
								bundle="accountsUIResources" /></td>
						</tr>

					</table>
					<br>
					<customtable:mifostabletag source="statusHistory" scope="session"
						xmlFileName="LoanStatusChangeHistory.xml" moduleName="accounts/loan"
						passLocale="true" /> <br>
					<table width="96%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:button property="btn"
								onclick="fun_return(this.form);" styleClass="buttn"
								style="width:165px;">
								<mifos:mifoslabel name="Account.returnToAccountDetails"
									bundle="accountsUIResources" />
							</html-el:button></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			<br>
			<html-el:hidden property="globalAccountNum"
				value="${BusinessKey.globalAccountNum}" />
			<html-el:hidden property="accountTypeId"
				value="${BusinessKey.accountType.accountTypeId}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
