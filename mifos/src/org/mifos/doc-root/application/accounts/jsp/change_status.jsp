<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<script language="javascript">
			function goToCancelPage(form){
				form.action="editStatusAction.do?method=cancel";
				form.submit();
 			 }
			function manageFlag(i) {
   				if(editStatusActionForm.flagId!=undefined){
					if(i==15){
						editStatusActionForm.flagId.disabled=false;
					}else if(i==10){
						editStatusActionForm.flagId.disabled=false;
					}else{
						editStatusActionForm.flagId.selectedIndex=0;
						editStatusActionForm.flagId.disabled=true;
					}
				}
 			 }
		</script>
		<html-el:form action="editStatusAction.do?method=preview">
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
					<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="83%" class="headingorange"><span class="heading"> <c:out
								value="${sessionScope.editStatusActionForm.accountName}" />&nbsp;#&nbsp;<c:out
								value="${sessionScope.editStatusActionForm.globalAccountNum}" />&nbsp;-&nbsp;
							</span><mifos:mifoslabel name="accounts.changestatus"
								bundle="accountsUIResources" /></td>
						</tr>
						<tr>
							<td class="headingorange"><span class="fontnormalbold"> <mifos:mifoslabel
								name="accounts.currentstatus" bundle="accountsUIResources" />:<mifoscustom:MifosImage
								id="${sessionScope.editStatusActionForm.currentStatusId}"
								moduleName="accounts" /></span> <span class="fontnormal"><c:out value="${BusinessKey.accountState.name}" />
								</span></td>
						</tr>
						<tr><logic:messagesPresent>
							<td><br><font class="fontnormalRedBold"><html-el:errors bundle="accountsUIResources" /></font></td>
							</logic:messagesPresent>
						</tr>
						<tr>
							<td class="fontnormal"><br>
								<c:if test="${sessionScope.editStatusActionForm.accountTypeId == '1'}">
									<mifos:mifoslabel name="accounts.SelectLoanStatusComplete"
									    bundle="accountsUIResources" />
								</c:if>
								<c:if test="${sessionScope.editStatusActionForm.accountTypeId == '2'}">
									<mifos:mifoslabel name="accounts.SelectSavingsStatusComplete"
									    bundle="accountsUIResources" />
								</c:if>
								
								</td>
						</tr>
						<tr>
							<td class="blueline"><img src="pages/framework/images/trans.gif"
								width="10" height="12"></td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="left" valign="top" class="fontnormalbold"><mifos:mifoslabel
								name="accounts.status" mandatory="yes" bundle="accountsUIResources"></mifos:mifoslabel>
							</td>
							<td align="left" valign="top">
							<table width="95%" border="0" cellpadding="0" cellspacing="0">

								<c:forEach var="status" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'statusList')}"	varStatus="loopStatus">
									<bean:define id="ctr">
										<c:out value="${loopStatus.index}" />
									</bean:define>
									<tr class="fontnormal">
										<td width="2%" align="center"><html-el:radio
											property="newStatusId" value="${status.id}"
											onclick="manageFlag(${status.id})" /></td>
										<td width="98%"><c:out value="${status.name}" /></td>
									</tr>
								</c:forEach>
								<c:forEach var="status" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'statusList')}">
									<c:if test="${status.id == '10' || status.id == '15'}">
										<tr class="fontnormal">
											<td align="center">&nbsp;</td>
											<td><c:if test="${sessionScope.editStatusActionForm.accountTypeId == '1'}">
													<mifos:mifoslabel name="accounts.SelectExplainationLoan" bundle="accountsUIResources" />
												</c:if>
												<c:if test="${sessionScope.editStatusActionForm.accountTypeId == '2'}">
													<mifos:mifoslabel name="accounts.SelectExplainationSavings" bundle="accountsUIResources" />
												</c:if>											
												</td>
										</tr>
										<tr class="fontnormal">
											<td align="center">&nbsp;</td>
											<td><c:set var="flags" scope="request"
												value="${status.flagSet}" /> <mifos:select
												name="editStatusActionForm" property="flagId" size="1" disabled="true">
												<html-el:options collection="flags" property="id"
													labelProperty="flagDescription" />
											</mifos:select></td>
										</tr>
									</c:if>
								</c:forEach>
								<tr class="fontnormal">
									<td align="center">&nbsp;</td>
									<td>&nbsp;</td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td width="7%" align="left" valign="top" class="fontnormalbold">

							<mifos:mifoslabel name="accounts.note" mandatory="yes"
								bundle="accountsUIResources"></mifos:mifoslabel></td>
							<td width="93%" align="left" valign="top"
								style="padding-left:4px;"><html-el:textarea
								property="notes" style="width:320px; height:110px;" /></td>
						</tr>
					</table>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td height="25" align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:submit styleClass="buttn">
								<mifos:mifoslabel name="accounts.preview" />
							</html-el:submit> &nbsp;&nbsp; <html-el:button property="btn"
								styleClass="cancelbuttn"
								onclick="goToCancelPage(this.form)">
								<mifos:mifoslabel name="accounts.cancel" />
							</html-el:button></td>
						</tr>
					</table>
					<br>
					<br>
					</td>
				</tr>
			</table>
			<html-el:hidden property="globalAccountNum" value="${sessionScope.editStatusActionForm.globalAccountNum}" />
		</html-el:form>
		<script language="javascript">
				if(editStatusActionForm.newStatusId.length != undefined){
					for(j=0;j<editStatusActionForm.newStatusId.length;j++){ 
						if (editStatusActionForm.newStatusId[j].checked) {
							manageFlag(editStatusActionForm.newStatusId[j].value);
						}
					}
				}else{
					manageFlag(editStatusActionForm.newStatusId.value);
				}
			</script>
	</tiles:put>
</tiles:insert>
