<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/mifos/customtags" prefix="customtable"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>

<script language="javascript">
function goToCancelPage(){
	loanStatusActionForm.action="LoanStatusAction.do?method=cancel";
	loanStatusActionForm.submit();
  }
</script>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<html-el:form action="LoanStatusAction.do">
			
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt">
							<mifoscustom:getLoanHeader loanHeader='${sessionScope.header_get}'/>
								<html-el:link action="loanAction.do?method=get&globalAccountNum=${param.globalAccountNum}">
									<c:out value="${sessionScope.loanStatusActionForm.accountName}" />
								</html-el:link> </span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" height="24" align="left" valign="top"
						class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange">
							<span class="heading">
							<c:out value="${sessionScope.loanStatusActionForm.accountName}"/>
							#<c:out value="${sessionScope.loanStatusActionForm.globalAccountNum}"/> - 
							</span>
							<mifos:mifoslabel name="Account.StatusHistory"	bundle="accountsUIResources" /></td>
						</tr>

					</table>
					<br>
			<customtable:mifostabletag  source="statusHistory" scope="request"  xmlFileName="AccountStatusChangeHistory.xml" moduleName="accounts" passLocale="true"/> 
					

					<br>
					<table width="96%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center">
							<html-el:button property="btn" onclick="goToCancelPage()" styleClass="buttn" style="width:165px;">
								<mifos:mifoslabel name="Account.returnToAccountDetails"	bundle="accountsUIResources" />
							</html-el:button>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			<br>
			

		</html-el:form>
	</tiles:put>
</tiles:insert>
