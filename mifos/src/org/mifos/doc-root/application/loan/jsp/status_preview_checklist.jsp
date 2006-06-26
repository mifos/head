<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">

		<script language="javascript">

  function goToCancelPage(){
	loanStatusActionForm.action="LoanStatusAction.do?method=cancel";
	loanStatusActionForm.submit();
  } 
  <%--
  function checkSelectedChoices(){
  	var checkCounter = 0;
  	if(loanStatusActionForm.selectedItems != undefined){
	for (i=0;i<loanStatusActionForm.selectedItems.length; i++)
	{
		if (loanStatusActionForm.selectedItems[i].checked)
			checkCounter = checkCounter + 1;
	}
	if (checkCounter != loanStatusActionForm.chklistSize.value)
	{
		alert("Please select all options");
			return (false);
	}	
		else
			loanStatusActionForm.submit();
	}
	else{
		loanStatusActionForm.submit();
		}
  }	--%>
  function GoToEditPage(){
	loanStatusActionForm.action="LoanStatusAction.do?method=previous";
	loanStatusActionForm.submit();
  }
  function submitFn(){
  	if(loanStatusActionForm.newStatusId.value != 7) {
		loanStatusActionForm.action="LoanStatusAction.do?method=update"; 
		loanStatusActionForm.submit();
		func_disableSubmitBtn("submitbtn");
	}
	else {
		loanStatusActionForm.action="LoanStatusAction.do?method=writeOff"; 
		loanStatusActionForm.submit();
		func_disableSubmitBtn("submitbtn");
	}
  }

  
</script>
<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<html-el:form action="LoanStatusAction.do">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <mifoscustom:getLoanHeader
						loanHeader='${sessionScope.header_get}' /> <html-el:link
						action="loanAction.do?method=get&globalAccountNum=${sessionScope.loanStatusActionForm.globalAccountNum}">
						<c:out value="${sessionScope.loanStatusActionForm.accountName}" />
					</html-el:link> </span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="83%" class="headingorange"><span class="heading"> <c:out
								value="${sessionScope.loanStatusActionForm.accountName}" /> #<c:out
								value="${sessionScope.loanStatusActionForm.globalAccountNum}" /> -</span>
							<mifos:mifoslabel name="loan.ConfirmStatusChange"
								bundle="loanUIResources"></mifos:mifoslabel></td>
						</tr>
						<tr>
							<td class="headingorange"><span class="fontnormalbold"> <mifos:mifoslabel
								name="loan.NewStatus" bundle="loanUIResources"></mifos:mifoslabel>
							</span> <span class="fontnormal"> <mifoscustom:MifosImage
								id="${sessionScope.loanStatusActionForm.newStatusId}"
								moduleName="accounts.loan" /><c:out
								value="${requestScope.newStatusName}" /> <c:if
								test="${!empty requestScope.flagName}">
                     	- <c:out value="${requestScope.flagName}" />
							</c:if> </span></td>
						</tr>
						<tr><logic:messagesPresent>
							<td><br><font class="fontnormalRedBold"><html-el:errors bundle="loanUIResources" /></font></td>
							</logic:messagesPresent>
						</tr>
						<tr>
							<td class="fontnormal"><br>
							<span class="fontnormalbold"> <c:out
								value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.LoanStatusVO.notes.commentDate)}" />
							</span> <span class="fontnormal"><br>
							<c:out value="${requestScope.LoanStatusVO.notes.comment}" />&nbsp;
							- &nbsp; </span> <em><c:out
								value="${requestScope.LoanStatusVO.notes.officer.displayName}" /></em>
							</td>
						</tr>
						<c:if test="${!empty requestScope.checklist}">
							<tr>
								<td class="blueline"><img src="pages/framework/images/trans.gif"
									width="10" height="12"></td>
							</tr>
							<tr>
								<td class="fontnormal">&nbsp;</td>
							</tr>
							<tr>
								<td class="fontnormal"><mifos:mifoslabel
									name="loan.ChecklistMsg" bundle="loanUIResources"></mifos:mifoslabel>
								<mifos:mifoslabel name="loan.ClickSubmit"
									bundle="loanUIResources"></mifos:mifoslabel> <mifos:mifoslabel
									name="loan.ClickCancel" bundle="loanUIResources"></mifos:mifoslabel>
								</td>
							</tr>
						</c:if>
					</table>
					<br>
					<c:if test="${!empty requestScope.checklist}">
						<table width="95%" border="0" cellpadding="3" cellspacing="0">
							<bean:size collection="${requestScope.checklist}" id="listSize" />
							<html-el:hidden property="chklistSize"
								value="${pageScope.listSize}" />
							<c:forEach var="chklist" items="${requestScope.checklist}">
								<tr class="fontnormal">
									<html-el:multibox name="loanStatusActionForm"
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
								onclick="GoToEditPage()">
								<mifos:mifoslabel name="loan.EditStatus"
									bundle="loanUIResources"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:button property="submitbtn"
								styleClass="buttn" style="width:70px;" onclick="submitFn()">
								<mifos:mifoslabel name="loan.submit" bundle="loanUIResources"></mifos:mifoslabel>
							</html-el:button> &nbsp;&nbsp; <html-el:button property="btn"
								styleClass="cancelbuttn" style="width:70px"
								onclick="goToCancelPage()">
								<mifos:mifoslabel name="loan.cancel" bundle="loanUIResources"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
					</table>
					<br>
					</td>
			</table>
<html-el:hidden property="newStatusId" value="${sessionScope.loanStatusActionForm.newStatusId}"/> 
<html-el:hidden property="globalAccountNum" value="${sessionScope.loanStatusActionForm.globalAccountNum}"/> 
			<%--<html-el:hidden property="input" value="PreviewChangeGroupStatus"/> 
<html-el:hidden property="customerId" value="${requestScope.GroupVO.customerId}"/> 
<html-el:hidden property="globalCustNum" value="${requestScope.GroupVO.globalCustNum}"/> 
<html-el:hidden property="versionNo" value="${requestScope.GroupVO.versionNo}"/> 
<html-el:hidden property="displayName" value="${requestScope.GroupVO.displayName}"/> --%>
		</html-el:form>
	</tiles:put>
</tiles:insert>
