<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/mifos/customtags" prefix="customtable"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<SCRIPT SRC="pages/application/savings/js/CreateSavingsAccount.js"></SCRIPT>
		<html-el:form method="post" action="/savingsAction.do?method=editPreview" >
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
			
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt">
	          				<customtags:headerLink/> 
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
							<td class="headingorange">
							<span class="heading">
		                	<c:out value="${BusinessKey.savingsOffering.prdOfferingName}"/> # <c:out value="${BusinessKey.globalAccountNum}"/> - 
		                	</span> 
							<mifos:mifoslabel name="Account.StatusHistory"	bundle="accountsUIResources" /></td>
						</tr>

					</table>
					<br>
					 <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'statusChangeHistoryList')}" var="statusChangeHistoryList" scope="session" />
					<customtable:mifostabletag  source="statusChangeHistoryList" scope="session"  xmlFileName="SavingsStatusChangeHistory.xml" moduleName="accounts\\savings" passLocale="true"/> 
					

					<br>
					<table width="96%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center">
							<html-el:button property="returnToAccountDetailsbutton"
					       onclick="javascript:fun_editCancel(this.form)"
						     styleClass="buttn" style="width:165px;">
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
