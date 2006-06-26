<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/tags/struts-bean-el" prefix="bean-el"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<script language="javascript">

function goToCancelPage(){
	clientStatusActionForm.action="clientStatusAction.do?method=cancel";
	clientStatusActionForm.submit();
  }
  function goToEditPage(){
	clientStatusActionForm.action="clientStatusAction.do?method=previous";
	clientStatusActionForm.submit();
  }

  
</script>

		<html-el:form action="clientStatusAction.do?method=update"
			onsubmit="func_disableSubmitBtn('submitButton');">

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"><a
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
								value="${requestScope.clientStatusVO.displayName}" /> -</span>
							<mifos:mifoslabel name="client.ConfirmStatusHeading"
								bundle="ClientUIResources"></mifos:mifoslabel></td>
						</tr>
						<tr>
							<td class="headingorange"><span class="fontnormalbold"> <mifos:mifoslabel
								name="client.NewStatusLabel" bundle="ClientUIResources"></mifos:mifoslabel>
							</span> <span class="fontnormal"> <c:choose>
								<%-- Partial Application --%>

								<c:when test="${requestScope.clientStatusVO.statusId == 1}">
									<mifos:MifosImage id="partial" moduleName="customer.client" />
								</c:when>
								<%-- Pending Approval --%>
								<c:when test="${requestScope.clientStatusVO.statusId == 2}">
									<mifos:MifosImage id="pending" moduleName="customer.client" />
								</c:when>
								<%-- Active --%>
								<c:when test="${requestScope.clientStatusVO.statusId == 3}">
									<mifos:MifosImage id="active" moduleName="customer.client" />
								</c:when>
								<%-- On Hold --%>
								<c:when test="${requestScope.clientStatusVO.statusId == 4}">
									<mifos:MifosImage id="hold" moduleName="customer.client" />
								</c:when>
								<%-- Cancelled --%>
								<c:when test="${requestScope.clientStatusVO.statusId == 5}">
									<mifos:MifosImage id="cancelled" moduleName="customer.client" />
								</c:when>
								<%-- Closed --%>
								<c:when test="${requestScope.clientStatusVO.statusId == 6}">
									<mifos:MifosImage id="closed" moduleName="customer.client" />
								</c:when>

								<c:otherwise>
								</c:otherwise>
							</c:choose> <c:out value="${requestScope.newStatus}" /> <c:if
								test="${!empty requestScope.newFlag}">
                     	- <c:out value="${requestScope.newFlag}" />
							</c:if> </span></td>
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
							<span class="fontnormalbold"> <c:out
								value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.clientStatusVO.customerNote.commentDate)}" />
							<!-- <c:out	value="${requestScope.clientStatusVO.customerNote.commentDate}" />-->
							</span> <span class="fontnormal"><br>
							<c:out
								value="${requestScope.clientStatusVO.customerNote.comment}" />
							-<em><c:out
								value="${requestScope.clientStatusVO.customerNote.personnelName}" /></em>
							</span> <%--	- <em><c:out value="${requestScope.context.userContext.userName}"/></em>--%>
							</td>
						</tr>
						<bean:size collection="${requestScope.checkLists}" id="listSiz" />
						<html-el:hidden property="listSize" value="${listSiz}" />
						<c:if test="${listSiz > 0}">
							<tr>
								<td class="blueline"><img src="pages/framework/images/trans.gif"
									width="10" height="12"></td>
							</tr>
							<tr>
								<td class="fontnormal">&nbsp;</td>
							</tr>
							<tr>
								<td class="fontnormal"><mifos:mifoslabel
									name="client.StatusPreviewInstruction"
									bundle="ClientUIResources"></mifos:mifoslabel> <mifos:mifoslabel
									name="client.EditPageCancelInstruction1"
									bundle="ClientUIResources"></mifos:mifoslabel> <mifos:mifoslabel
									name="${ConfigurationConstants.CLIENT}"></mifos:mifoslabel> <mifos:mifoslabel
									name="client.EditPageCancelInstruction2"
									bundle="ClientUIResources"></mifos:mifoslabel></td>
							</tr>
						</c:if>
					</table>
					<br>
					<!-- bug id 28204. Changed the spelling of the List from which data is to be displayed-->
					<table width="95%" border="0" cellpadding="3" cellspacing="0">

						<c:forEach var="chklist" items="${requestScope.checkLists}"
							varStatus="loopStatus">
							<bean:define id="ctr">
								<c:out value="${loopStatus.index}" />
							</bean:define>
							<tr class="fontnormal">
								<td width="2%" valign="top"><html-el:multibox
									property="selectedItems">
									<c:out value="${ctr}" />
								</html-el:multibox> <c:out value="${chklist.checkListName}" />
								</td>
							</tr>
						</c:forEach>
					</table>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td style="padding-top:5px;"><html-el:button property="editInfo"
								styleClass="insidebuttn" style="width:65px;"
								onclick="goToEditPage()">
								<mifos:mifoslabel name="button.previousStatus"
									bundle="ClientUIResources"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:submit property="submitButton"
								styleClass="buttn" style="width:65px;">
								<mifos:mifoslabel name="button.submit"
									bundle="CenterUIResources"></mifos:mifoslabel>
							</html-el:submit> &nbsp;&nbsp; <html-el:button
								property="cancelBtn" styleClass="cancelbuttn" style="width:70px"
								onclick="goToCancelPage()">
								<mifos:mifoslabel name="button.cancel"
									bundle="ClientUIResources"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
					</table>
					<br>
					</td>

				</tr>
			</table>


		</html-el:form>
	</tiles:put>
</tiles:insert>
