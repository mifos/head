<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">

		<script language="javascript">

  function goToCancelPage(form){
	form.action="editSavingsAction.do?method=cancel";
	form.submit();
  }
  function GoToEditPage(form){
	form.action="editSavingsAction.do?method=previous";
	form.submit();
  }

  
</script>

		<html-el:form action="editSavingsAction.do?method=update">
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
								value="${sessionScope.editSavingsStatusActionForm.accountName}" />&nbsp;#&nbsp;<c:out
								value="${sessionScope.editSavingsStatusActionForm.globalAccountNum}" />&nbsp;-</span>
							<mifos:mifoslabel name="savings.ConfirmStatusChange"
								bundle="SavingsUIResources"></mifos:mifoslabel></td>
						</tr>
						<tr>
							<td class="headingorange"><span class="fontnormalbold"> <mifos:mifoslabel
								name="savings.NewStatus" bundle="SavingsUIResources"></mifos:mifoslabel>:
							</span> <span class="fontnormal"> <mifoscustom:MifosImage
								id="${sessionScope.editSavingsStatusActionForm.newStatusId}"
								moduleName="accounts.savings" /><c:out
								value="${sessionScope.newStatusName}" /> <c:if
								test="${!empty sessionScope.flagName}">
                     	- <c:out value="${sessionScope.flagName}" />
							</c:if></span></td>
						</tr>
						<tr><logic:messagesPresent>
							<td><br><font class="fontnormalRedBold"><html-el:errors bundle="SavingsUIResources" /></font></td>
							</logic:messagesPresent>
						</tr>
						<tr>
							<td class="fontnormal"><br>
							<span class="fontnormalbold"> <c:out
								value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,sessionScope.accountNotes.commentDate)}" />
							</span> <span class="fontnormal"><br>
							<c:out value="${sessionScope.accountNotes.comment}"/>&nbsp;-</span> 
							<em><c:out
								value="${sessionScope.accountNotes.personnel.displayName}" /></em>
							</td>
						</tr>
						<c:if test="${!empty sessionScope.checklist}">
							<tr>
								<td class="blueline"><img src="pages/framework/images/trans.gif"
									width="10" height="12"></td>
							</tr>
							<tr>
								<td class="fontnormal">&nbsp;</td>
							</tr>
							<tr>
								<td class="fontnormal"><mifos:mifoslabel
									name="savings.ChecklistMsg" bundle="SavingsUIResources"></mifos:mifoslabel>
								<mifos:mifoslabel name="savings.ClickSubmit"
									bundle="SavingsUIResources"></mifos:mifoslabel> 							<mifos:mifoslabel name="savings.ClickCancel1"
								bundle="SavingsUIResources"></mifos:mifoslabel>
							<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"></mifos:mifoslabel>
							<mifos:mifoslabel name="savings.ClickCancel2"
								bundle="SavingsUIResources"></mifos:mifoslabel>
								</td>
							</tr>
						</c:if>
					</table>
					<br>
					<c:if test="${!empty sessionScope.checklist}">
						<table width="95%" border="0" cellpadding="3" cellspacing="0">
							<bean:size collection="${sessionScope.checklist}" id="listSize" />
							<html-el:hidden property="chklistSize"
								value="${pageScope.listSize}" />
							<c:forEach var="chklist" items="${sessionScope.checklist}">
								<tr class="fontnormal">
									<html-el:multibox name="editSavingsStatusActionForm"
										property="selectedItems">
										<td width="2%" valign="top"><c:out
											value="${chklist.checkListId}" /></td>
									</html-el:multibox>
									<c:out value="${chklist.checkListName}" />
								</tr>
							</c:forEach>
						</table>
					</c:if> 
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td style="padding-top:5px;"><html-el:button property="editInfo"
								value="Edit Status" styleClass="insidebuttn" style="width:65px;"
								onclick="GoToEditPage(this.form)">
								<mifos:mifoslabel name="savings.EditStatus"
									bundle="SavingsUIResources"></mifos:mifoslabel>
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
								<mifos:mifoslabel name="savings.Submit" bundle="SavingsUIResources"></mifos:mifoslabel>
							</html-el:submit> &nbsp;&nbsp; <html-el:button property="btn"
								styleClass="cancelbuttn" style="width:70px"
								onclick="goToCancelPage(this.form)">
								<mifos:mifoslabel name="savings.Cancel" bundle="SavingsUIResources"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
					</table>
					<br>
					</td>
			</table>
			<html-el:hidden property="accountId" value="${sessionScope.BusinessKey.accountId}"/>
		</html-el:form>
	</tiles:put>
</tiles:insert>
