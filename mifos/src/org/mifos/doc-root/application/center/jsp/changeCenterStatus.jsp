<!--
/**

* changeCenterStatus    version: 1.0



* Copyright © 2005-2006 Grameen Foundation USA

* 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

* All rights reserved.



* Apache License
* Copyright (c) 2005-2006 Grameen Foundation USA
*

* Licensed under the Apache License, Version 2.0 (the "License"); you may
* not use this file except in compliance with the License. You may obtain
* a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*

* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and limitations under the

* License.
*
* See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

* and how it is applied.

*

*/
 -->

<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://struts.apache.org/tags-bean-el" prefix="bean-el"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<script>
 function goToCancelPage(){
	centerActionForm.action="centerAction.do?method=cancel";
	centerActionForm.submit();
  }
</script>

		<html-el:form method="post" action="centerAction.do?method=preview">
			<html-el:hidden property="input" value="status" />
			<html-el:hidden property="customerId"
				value="${requestScope.centerVO.customerId}" />
			<html-el:hidden property="globalCustNum"
				value="${requestScope.centerVO.globalCustNum}" />
			<html-el:hidden property="versionNo"
				value="${requestScope.centerVO.versionNo}" />
			<html-el:hidden property="office.versionNo"
				value="${requestScope.centerVO.office.versionNo}" />
			<html-el:hidden property="displayName"
				value="${requestScope.centerVO.displayName}" />
			<html-el:hidden property="customerMeeting.meetingPlace" value="dsfsf" />
			<html-el:hidden property="office.officeId"
				value="${requestScope.centerVO.office.officeId}" />
			<!-- body begins -->
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <a href="CustomerSearchAction.do?method=getOfficeHomePage&officeId=<c:out value="${sessionScope.linkValues.customerOfficeId}"/>&officeName=<c:out value="${sessionScope.linkValues.customerOfficeName}"/>&loanOfficerId=<c:out value="${requestScope.Context.userContext.id}"/>">
	            	<c:out value="${sessionScope.linkValues.customerOfficeName}"/></a> 
					/ </span> <!-- Name of the client --> <span class="fontnormal8pt">
					<a
						href="centerAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.globalCustNum}"/>">
					<c:out value="${sessionScope.linkValues.customerName}" /> </a> </span>

					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="0" cellspacing="0">

						<tr>
							<td width="83%" class="headingorange"><span class="heading"><c:out
								value="${requestScope.centerVO.displayName}" /> - </span> <mifos:mifoslabel
								name="Center.ChangeStatusHeading" bundle="CenterUIResources"></mifos:mifoslabel>
							</td>
						</tr>
						<tr>
							<td class="headingorange"><span class="fontnormalbold"><mifos:mifoslabel
								name="Center.CurrentStatusHeading"/>:</span>
							<c:set var="status" scope="request"
								value="${sessionScope.oldStatus}" /> <c:choose>
								<c:when test="${status == 13}">
									<span class="fontnormal"><img
										src="pages/framework/images/status_activegreen.gif" width="8"
										height="9"></span>
								</c:when>
								<c:otherwise>
									<span class="fontnormal"><img
										src="pages/framework/images/status_closedblack.gif" width="8"
										height="9"></span>
								</c:otherwise>
							</c:choose> <span class="fontnormal"><c:out
								value="${requestScope.currentStatus}" /> </span>
							</td>
						</tr>
						<tr><logic:messagesPresent>
							<td><br><font class="fontnormalRedBold"><html-el:errors bundle="CenterUIResources" /></font></td>
							</logic:messagesPresent>
						</tr>

						<tr>
							<td class="fontnormal"><br>
							<mifos:mifoslabel name="Center.ChangeStatusPageInstruction"
								bundle="CenterUIResources"></mifos:mifoslabel> 
								<mifos:mifoslabel name="Center.EditPageCancelInstruction1"	bundle="CenterUIResources"/>
								<mifos:mifoslabel name="${ConfigurationConstants.CENTER}"/>
								<mifos:mifoslabel name="Center.EditPageCancelInstruction2"	bundle="CenterUIResources"/>
								</td>
						</tr>
						<tr>
							<td class="blueline"><img src="pages/framework/images/trans.gif"
								width="10" height="12"></td>
						</tr>

					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<!-- Error Display if any -->


						<tr>

							<td align="left" valign="top" class="fontnormalbold"><mifos:mifoslabel
								name="Center.Status" mandatory="yes" bundle="CenterUIResources"></mifos:mifoslabel>
							</td>
							<td align="left" valign="top">
							<table width="95%" border="0" cellpadding="0" cellspacing="0">
								<c:forEach var="status" items="${requestScope.statusList}"
									varStatus="loopStatus">
									<tr class="fontnormal">

										<td width="2%" align="center"><html-el:radio
											property="statusId" value="${status.statusId}" /></td>
										<td width="98%"><c:out value="${status.statusName}" /></td>
									</tr>
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
							<mifos:mifoslabel name="Center.Note" mandatory="yes"
								bundle="CenterUIResources"></mifos:mifoslabel></td>
							<td width="93%" align="left" valign="top"
								style="padding-left:5px;"><html-el:textarea
								name="centerActionForm" property="customerNote.comment"
								style="width:320px; height:110px;"></html-el:textarea></td>
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
									bundle="CenterUIResources"></mifos:mifoslabel>
							</html-el:submit> &nbsp; &nbsp; <html-el:button
								onclick="goToCancelPage();" property="cancelButton"
								styleClass="cancelbuttn" style="width:70px">
								<mifos:mifoslabel name="button.cancel"
									bundle="CenterUIResources"></mifos:mifoslabel>
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
