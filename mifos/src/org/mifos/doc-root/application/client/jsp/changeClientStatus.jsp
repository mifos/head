<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://struts.apache.org/tags-bean-el" prefix="bean-el"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">

		<script language="javascript">

function goToCancelPage(){
	clientStatusActionForm.action="clientStatusAction.do?method=cancel";
	clientStatusActionForm.submit();
  }
  
function manageFlag(i) {
		if(clientStatusActionForm.flagId!=undefined){
		if(i==5 || i==6){
			clientStatusActionForm.flagId.disabled=false;
		}else{
			clientStatusActionForm.flagId.selectedIndex=0;
			clientStatusActionForm.flagId.disabled=true;
		}
	}
	
}
 
  function fn(){
  	clientStatusActionForm.flagId[0].style.display="";
	clientStatusActionForm.flagId[0].disabled=false;
	clientStatusActionForm.statusId[0].checked=true;
  }
</script>

		<html-el:form action="clientStatusAction.do?method=preview">
			<html-el:hidden property="currentStatusId"
				value="${sessionScope.oldClient.statusId}" />
			<html-el:hidden property="displayName"
				value="${sessionScope.oldClient.displayName}" />
			<html-el:hidden property="customerId"
				value="${sessionScope.oldClient.customerId}" />


			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <a
						href="CustomerSearchAction.do?method=getOfficeHomePage&officeId=<c:out value="${sessionScope.linkValues.customerOfficeId}"/>&officeName=<c:out value="${sessionScope.linkValues.customerOfficeName}"/>&loanOfficerId=<c:out value="${requestScope.Context.userContext.id}"/>">
					<c:out value="${sessionScope.linkValues.customerOfficeName}" /></a>
					/ </span> <c:if
						test="${!empty sessionScope.linkValues.customerCenterName}">
						<span class="fontnormal8pt"> <a
							href="centerAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.customerCenterGCNum}"/>">
						<c:out value="${sessionScope.linkValues.customerCenterName}" /> </a>
						/ </span>
					</c:if> <c:if
						test="${!empty sessionScope.linkValues.customerParentName}">
						<span class="fontnormal8pt"> <a
							href="GroupAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.customerParentGCNum}"/>">
						<c:out value="${sessionScope.linkValues.customerParentName}" /> </a>
						/ </span>
					</c:if> <!-- Name of the client --> <span class="fontnormal8pt"> <a
						href="clientCreationAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.globalCustNum}"/>">
					<c:out value="${sessionScope.linkValues.customerName}" /> </a> </span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="83%" class="headingorange"><span class="heading"> <c:out
								value="${sessionScope.linkValues.customerName}" /> </span> - <mifos:mifoslabel
								name="client.ChangeStatusHeading" bundle="ClientUIResources"></mifos:mifoslabel>
							</td>
						</tr>
						<tr>
							<td class="headingorange"><span class="fontnormalbold"> <mifos:mifoslabel
								name="client.CurrentStatusHeading" bundle="ClientUIResources"></mifos:mifoslabel>:

							</span> <span class="fontnormal"> <c:choose>
								<%-- Partial Application --%>

								<c:when
									test="${requestScope.clientStatusVO.currentStatusId == 1}">
									<mifos:MifosImage id="partial" moduleName="customer.client" />
								</c:when>
								<%-- Pending Approval --%>
								<c:when
									test="${requestScope.clientStatusVO.currentStatusId == 2}">
									<mifos:MifosImage id="pending" moduleName="customer.client" />
								</c:when>
								<%-- Active --%>
								<c:when
									test="${requestScope.clientStatusVO.currentStatusId == 3}">
									<mifos:MifosImage id="active" moduleName="customer.client" />
								</c:when>
								<%-- On Hold --%>
								<c:when
									test="${requestScope.clientStatusVO.currentStatusId == 4}">
									<mifos:MifosImage id="hold" moduleName="customer.client" />
								</c:when>
								<%-- Cancelled --%>
								<c:when
									test="${requestScope.clientStatusVO.currentStatusId == 5}">
									<mifos:MifosImage id="cancelled" moduleName="customer.client" />
								</c:when>
								<%-- Closed --%>
								<c:when
									test="${requestScope.clientStatusVO.currentStatusId == 6}">
									<mifos:MifosImage id="closed" moduleName="customer.client" />
								</c:when>

								<c:otherwise>
								</c:otherwise>
							</c:choose> <c:out value="${requestScope.currentStatus}" /> </span>
							</td>
						</tr>
						<tr>
							<logic:messagesPresent>
								<td><br>
								<font class="fontnormalRedBold"><html-el:errors
									bundle="ClientUIResources" /></font></td>
							</logic:messagesPresent>
						</tr>
						<tr>
							<td class="fontnormal"><br>
							<mifos:mifoslabel name="client.ChangeStatusPageInstruction"
								bundle="ClientUIResources"></mifos:mifoslabel> <mifos:mifoslabel
								name="client.EditPageCancelInstruction1"
								bundle="ClientUIResources"></mifos:mifoslabel> <mifos:mifoslabel
								name="${ConfigurationConstants.CLIENT}"></mifos:mifoslabel> <mifos:mifoslabel
								name="client.EditPageCancelInstruction2"
								bundle="ClientUIResources"></mifos:mifoslabel></td>
						</tr>
						<tr>

						</tr>
						<tr>
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
								name="client.Status" mandatory="yes" bundle="ClientUIResources"></mifos:mifoslabel>:
							</td>
							<td align="left" valign="top">
							<table width="95%" border="0" cellpadding="0" cellspacing="0">
								<c:forEach var="status" items="${requestScope.statusList}"
									varStatus="loopStatus">
									<tr class="fontnormal">
										<td width="2%" align="center"><html-el:radio
											property="statusId" value="${status.statusId}"
											onclick="manageFlag(${status.statusId})" /></td>
										<td width="98%"><c:out value="${status.statusName}" /></td>
									</tr>
								</c:forEach>
								<c:forEach var="status" items="${requestScope.statusList}">
									<c:if test="${status.statusId == 5 or status.statusId==6}">
										<tr class="fontnormal">
											<td align="center">&nbsp;</td>
											<td><mifos:mifoslabel name="client.ClosedStatusInstruction1"
												bundle="ClientUIResources"></mifos:mifoslabel> <mifos:mifoslabel
												name="${ConfigurationConstants.CLIENT}"></mifos:mifoslabel>
											<mifos:mifoslabel name="client.ClosedStatusInstruction2"
												bundle="ClientUIResources"></mifos:mifoslabel></td>
										</tr>
										<tr class="fontnormal">
											<td align="center">&nbsp;</td>
											<td><c:set var="flags" scope="request"
												value="${status.flagList}" /> <mifos:select
												name="clientStatusActionForm" property="flagId" size="1"
												disabled="true">
												<html-el:options collection="flags" property="flagId"
													labelProperty="flagName" />
											</mifos:select></td>
										</tr>
									</c:if>
								</c:forEach>

								<!-- Bug id 28188. Changed the script to below the flagId field -->
								<script language="javascript">
						if(clientStatusActionForm.statusId.length != undefined){
							for(j=0;j<clientStatusActionForm.statusId.length;j++){
								if (clientStatusActionForm.statusId[j].checked) {
									manageFlag(clientStatusActionForm.statusId[j].value);
								}
							}
						}else{
							manageFlag(clientStatusActionForm.statusId.value);
						}
					</script>
								<tr class="fontnormal">
									<td align="center">&nbsp;</td>
									<td>&nbsp;</td>
								</tr>
							</table>
							</td>
						</tr>

						<tr>
							<td width="7%" align="left" valign="top" class="fontnormalbold">

							<mifos:mifoslabel name="client.Note" mandatory="yes"
								bundle="ClientUIResources"></mifos:mifoslabel>:</td>
							<td width="93%" align="left" valign="top"
								style="padding-left:4px;"><html-el:textarea
								property="customerNote.comment"
								style="width:320px; height:110px;" /></td>
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
								<mifos:mifoslabel name="button.preview"
									bundle="ClientUIResources"></mifos:mifoslabel>
							</html-el:submit> &nbsp;&nbsp; <html-el:button
								property="cancelBtn" styleClass="cancelbuttn" style="width:70px"
								onclick="goToCancelPage()">
								<mifos:mifoslabel name="button.cancel"
									bundle="ClientUIResources"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
					</table>
					<br>
					<br>
					</td>
				</tr>
			</table>

		</html-el:form>
	</tiles:put>
</tiles:insert>
