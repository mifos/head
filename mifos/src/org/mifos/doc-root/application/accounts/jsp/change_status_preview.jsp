<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<script language="javascript">
			  function goToCancelPage(form){
				form.action="editStatusAction.do?method=cancel";
				form.submit();
			  }
			  function GoToEditPage(form){
				form.action="editStatusAction.do?method=previous";
				form.submit();
			  }
		</script>
		<html-el:form action="editStatusAction.do?method=update">
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />	
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
								value="${sessionScope.editStatusActionForm.globalAccountNum}" />&nbsp;-</span>
							<mifos:mifoslabel name="accounts.ConfirmStatusChange"	bundle="accountsUIResources" /></td>
						</tr>
						<tr>
							<td class="headingorange"><span class="fontnormalbold"> <mifos:mifoslabel
								name="accounts.NewStatus" bundle="accountsUIResources"></mifos:mifoslabel>:
							</span> <span class="fontnormal"> <mifoscustom:MifosImage
								id="${sessionScope.editStatusActionForm.newStatusId}"
								moduleName="accounts" /><c:out
								value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'newStatusName')}" /> <c:if
								test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'flagName')}">
                     	- <c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'flagName')}" />
							</c:if></span></td>
						</tr>
						<tr><logic:messagesPresent>
							<td><br><font class="fontnormalRedBold"><html-el:errors bundle="accountsUIResources" /></font></td>
							</logic:messagesPresent>
						</tr>
						<tr>
							<td class="fontnormal"><br>
							<span class="fontnormalbold"> <c:out
								value="${sessionScope.editStatusActionForm.commentDate}" />
							</span> <span class="fontnormal"><br>
							<c:out value="${sessionScope.editStatusActionForm.notes}"/>&nbsp;-</span> 
							<em><c:out
								value="${sessionScope.UserContext.name}" /></em>
							</td>
						</tr>
						<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'checklist')}">
							<tr>
								<td class="blueline"><img src="pages/framework/images/trans.gif"
									width="10" height="12"></td>
							</tr>
							<tr>
								<td class="fontnormal">&nbsp;</td>
							</tr>
							<tr>
								<td class="fontnormal"><mifos:mifoslabel
									name="accounts.ChecklistMsg" bundle="accountsUIResources"></mifos:mifoslabel>
								<mifos:mifoslabel name="accounts.ClickSubmit" bundle="accountsUIResources" />
								<mifos:mifoslabel name="accounts.ClickCancel1"	bundle="accountsUIResources" />
								<c:if test="${sessionScope.editStatusActionForm.accountTypeId == '1'}">
									<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
								</c:if>
								<c:if test="${sessionScope.editStatusActionForm.accountTypeId == '2'}">
									<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" />
								</c:if>
							<mifos:mifoslabel name="accounts.ClickCancel2"
								bundle="accountsUIResources"></mifos:mifoslabel>
								</td>
							</tr>
						</c:if>
					</table>
					<br>
					<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'checklist')}">
						<table width="95%" border="0" cellpadding="3" cellspacing="0">
							<c:forEach var="chklist" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'checklist')}">
								<c:forEach var="chklistDetail" items="${chklist.checklistDetails}">
									<bean:size collection="${chklist.checklistDetails}" id="listSize" />
									<html-el:hidden property="chklistSize"	value="${pageScope.listSize}" />
								<tr class="fontnormal">
									<html-el:multibox name="editStatusActionForm"
										property="selectedItems">
										<td width="2%" valign="top"><c:out
											value="${chklistDetail.detailId}" /></td>
									</html-el:multibox>
									<c:out value="${chklistDetail.detailText}" />
								</tr>
								</c:forEach>
							</c:forEach>
						</table>
					</c:if>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td style="padding-top:5px;"><html-el:button property="editInfo"
								value="Edit Status" styleClass="insidebuttn" style="width:65px;"
								onclick="GoToEditPage(this.form)">
								<mifos:mifoslabel name="accounts.EditStatus"
									bundle="accountsUIResources"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:submit property="btn"
								styleClass="buttn" style="width:70px;">
								<mifos:mifoslabel name="accounts.submit" bundle="accountsUIResources"></mifos:mifoslabel>
							</html-el:submit> &nbsp;&nbsp; <html-el:button property="btn"
								styleClass="cancelbuttn" style="width:70px"
								onclick="goToCancelPage(this.form)">
								<mifos:mifoslabel name="accounts.cancel" bundle="accountsUIResources"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
					</table>
					<br>
					</td>
			</table>
			<html-el:hidden property="globalAccountNum" value="${sessionScope.editStatusActionForm.globalAccountNum}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
