<!--
/**

* previewCenterStatus    version: 1.0



* Copyright (c) 2005-2006 Grameen Foundation USA

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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<script>
</script>
		<body>
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT> 
		<script language="javascript">
  
  function goToEditPage(){
		centerActionForm.action="centerAction.do?method=previous";
		centerActionForm.submit();
  }
   function goToCancelPage(){
		centerActionForm.action="centerAction.do?method=cancel";
		centerActionForm.submit();
  }
  function goToGetPage(){
		centerActionForm.action="centerAction.do?method=updateStatus";
		centerActionForm.submit();
  }
  
  /*function checkSelectedChoices(){
	
	var checkCounter = 0;
		var dummy = document.getElementById("listSize");
		if(dummy.value == 1){
			if(centerActionForm.selectedItems.checked){
			  checkCounter = checkCounter + 1;
			}
		}
		else{
			for (i = 0;i < dummy.value;  i++)
			{
				if (centerActionForm.selectedItems[i].checked)
					checkCounter = checkCounter + 1;
			}
		}
		if (checkCounter < dummy.value)
		{
			alert("Please select all the checklists to change the status");
			return (false);
		}
		else{
			centerActionForm.action="centerAction.do?method=updateStatus";
			centerActionForm.submit();
		}
	
	
  }*/
  
  
  
  
</script>
		<html-el:form method="post"
			action="centerAction.do?method=updateStatus" onsubmit="func_disableSubmitBtn('submitButton');">
			<html-el:hidden property="input" value="status" />
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
								value="${requestScope.centerVO.displayName}" />-</span> Confirm
							status change</td>
						</tr>
						<tr>
							<td class="headingorange"><span class="fontnormalbold"> <mifos:mifoslabel
								name="Center.NewStatus" bundle="CenterUIResources"></mifos:mifoslabel></span>

							<c:set var="statusI" scope="request"
								value="${requestScope.centerVO.statusId}" /> <c:choose>
								<c:when test="${statusI == 14}">
									<span class="fontnormal"><img
										src="pages/framework/images/status_closedblack.gif" width="8"
										height="9"></span>
								</c:when>
								<c:otherwise>
									<span class="fontnormal"><img
										src="pages/framework/images/status_activegreen.gif" width="8"
										height="9"></span>
								</c:otherwise>
							</c:choose> <span class="fontnormal"><c:out value="${requestScope.newStatus}" /></span>
							</td>
						</tr>
						<tr><logic:messagesPresent>
							<td><br><font class="fontnormalRedBold"><html-el:errors bundle="CenterUIResources" /></font></td>
							</logic:messagesPresent>
						</tr>
						<tr>
							<td class="fontnormal"><br>
							<span class="fontnormalbold">
							<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.centerVO.customerNote.commentDate)}" />
							<!--<c:out value="${requestScope.centerVO.customerNote.commentDate}" />-->
							</span>
							<span class="fontnormal"><br>
							</span> <c:out
								value="${requestScope.centerVO.customerNote.comment}" /> -<em><c:out
								value="${requestScope.centerVO.customerNote.personnelName}" /></em></td>
						</tr>
						<c:if test="${!empty requestScope.checkLists}">
							<tr>
								<td class="blueline"><img src="pages/framework/images/trans.gif"
									width="10" height="12"></td>
							</tr>
							<tr>
								<td class="fontnormal">&nbsp;</td>
							</tr>
							<tr>
								<td class="fontnormal">Complete and check off the following
								tasks. Then click Submit. Click Cancel to return to center
								details without submitting information.</td>
							</tr>
						</c:if>
					</table>
					<br>

					<table width="95%" border="0" cellpadding="3" cellspacing="0">
						<bean:size collection="${requestScope.checkLists}" id="listSiz" />
						<c:forEach var="checklist" items="${requestScope.checkLists}" varStatus="loopStatus">
						<bean:define id="ctr">
                		    <c:out value="${loopStatus.index}"/>
                		   </bean:define>
							<bean-el:size collection="${requestScope.checkLists}"
								id="listSiz" />
							<html-el:hidden property="listSize" value="${listSiz}" />
							<tr class="fontnormal">
								<html-el:multibox name="centerActionForm"
									property="selectedItems">
									<td width="2%" valign="top"><c:out
										value="${ctr}" /></td>
								</html-el:multibox>
								<c:out value="${checklist.checkListName}" />

							</tr>
						</c:forEach>



					</table>

					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>

							<td style="padding-top:5px;"><html-el:button
								onclick="goToEditPage()" property="editButton"
								styleClass="insidebuttn" style="width:65px;">
								<mifos:mifoslabel name="button.previousStatus"
									bundle="CenterUIResources"></mifos:mifoslabel>
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
							</html-el:submit> &nbsp; &nbsp; <html-el:button
								onclick="goToCancelPage();" property="cancelButton"
								styleClass="cancelbuttn" style="width:70px">
								<mifos:mifoslabel name="button.cancel"
									bundle="CenterUIResources"></mifos:mifoslabel>
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
