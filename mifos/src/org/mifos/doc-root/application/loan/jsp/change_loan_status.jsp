<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">

		<script language="javascript">

function goToCancelPage(){
	loanActionForm.submit();
  }
  
  function manageFlag(i) {
   if(loanStatusActionForm.flagId!=undefined){
		if(i==10){
			loanStatusActionForm.flagId.disabled=false;
		}else{
			loanStatusActionForm.flagId.selectedIndex=0;
			loanStatusActionForm.flagId.disabled=true;
		}
	}
  }
 
</script>

		<html-el:form action="LoanStatusAction.do?method=preview">
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
								value="${sessionScope.loanStatusActionForm.globalAccountNum}" />-&nbsp;
							</span><mifos:mifoslabel name="loan.changestatus"
								bundle="loanUIResources" /></td>
						</tr>
						<tr>
							<td class="headingorange"><span class="fontnormalbold"> <mifos:mifoslabel
								name="loan.currentstatus" bundle="loanUIResources" />:<mifoscustom:MifosImage
								id="${sessionScope.loanStatusActionForm.currentStatusId}"
								moduleName="accounts.loan" /></span> <span class="fontnormal"> <c:out
								value="${requestScope.oldStatusName}" /> </span></td>
						</tr>
						<tr><logic:messagesPresent>
							<td><br><font class="fontnormalRedBold"><html-el:errors bundle="loanUIResources" /></font></td>
							</logic:messagesPresent>
						</tr>
						<tr>
							<td class="fontnormal"><br>
							<mifos:mifoslabel name="loan.SelectStatus"
								bundle="loanUIResources"></mifos:mifoslabel> <mifos:mifoslabel
								name="loan.ClickContinue" bundle="loanUIResources"></mifos:mifoslabel>
							<mifos:mifoslabel name="loan.ClickCancel"
								bundle="loanUIResources"></mifos:mifoslabel></td>
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
								name="loan.status" mandatory="yes" bundle="loanUIResources"></mifos:mifoslabel>
							</td>
							<td align="left" valign="top">
							<table width="95%" border="0" cellpadding="0" cellspacing="0">

								<c:forEach var="status" items="${requestScope.statusList}"
									varStatus="loopStatus">
									<bean:define id="ctr">
										<c:out value="${loopStatus.index}" />
									</bean:define>
									<tr class="fontnormal">
										<td width="2%" align="center"><html-el:radio
											property="newStatusId" value="${status.statusId}"
											onclick="manageFlag(${status.statusId})" /></td>
										<td width="98%"><c:out value="${status.statusName}" /></td>
									</tr>
								</c:forEach>
								<c:forEach var="status" items="${requestScope.statusList}">
									<c:if test="${status.statusId == 10}">
										<tr class="fontnormal">
											<td align="center">&nbsp;</td>
											<td><mifos:mifoslabel name="loan.SelectExplaination"
												bundle="loanUIResources"></mifos:mifoslabel></td>
										</tr>
										<tr class="fontnormal">
											<td align="center">&nbsp;</td>
											<td><c:set var="flags" scope="request"
												value="${status.flagList}" /> <mifos:select
												name="loanStatusActionForm" property="flagId" size="1"
												disabled="true">
												<html-el:options collection="flags" property="flagId"
													labelProperty="flagName" />
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

							<mifos:mifoslabel name="loan.note" mandatory="yes"
								bundle="loanUIResources"></mifos:mifoslabel></td>
							<td width="93%" align="left" valign="top"
								style="padding-left:4px;"><html-el:textarea
								property="notes.comment" style="width:320px; height:110px;" /></td>
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
							<td align="center"><html-el:submit styleClass="buttn"
								style="width:70px;">
								<mifos:mifoslabel name="loan.preview" bundle="loanUIResources"></mifos:mifoslabel>
							</html-el:submit> &nbsp;&nbsp; <html-el:button property="btn"
								styleClass="cancelbuttn" style="width:70px"
								onclick="goToCancelPage()">
								<mifos:mifoslabel name="loan.cancel" bundle="loanUIResources"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
					</table>
					<br>
					<br>
					</td>
				</tr>
			</table>
		</html-el:form>
		<html-el:form action="loanAction.do?method=get">
			<html-el:hidden property="globalAccountNum"
				value="${sessionScope.loanStatusActionForm.globalAccountNum}" />
		</html-el:form>
		<script language="javascript">
	if(loanStatusActionForm.newStatusId.length != undefined){
		for(j=0;j<loanStatusActionForm.newStatusId.length;j++){
			if (loanStatusActionForm.newStatusId[j].checked) {
				manageFlag(loanStatusActionForm.newStatusId[j].value);
			}
		}
	}else{
		manageFlag(loanStatusActionForm.newStatusId.value);
	}
</script>

	</tiles:put>
</tiles:insert>
