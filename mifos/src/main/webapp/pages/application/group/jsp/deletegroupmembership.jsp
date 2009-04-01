<!--
/**

* deletegroupmembership.jsp    version: 1.0



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

<%@taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="/customer/customerfunctions" prefix="customerfn"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>


<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
	<input type="hidden" id="page.id" value="DeleteGroupMembership"/>

		<script>
		function goToCancelPage(){
		groupTransferActionForm.action="clientTransferAction.do?method=cancel";
		groupTransferActionForm.submit();
	    }
	</script>
	<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
	<fmt:setBundle basename="org.mifos.config.localizedResources.GroupUIResources"/>
		<html-el:form action="groupTransferAction.do?method=removeGroupMemberShip">
			<c:set
				value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
				var="BusinessKey" />
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
					<td width="70%" align="left" valign="top" class="paddingL15T15">
				    <table width="96%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td width="62%" class="headingorange">
					<span class="heading">
					<c:out value="${BusinessKey.displayName}" /> </span> - 
						<fmt:message key="Group.removeMembership">
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
						</fmt:message>
				  </td>
				</tr>
			</table>

			<table width="96%" border="0" cellpadding="3" cellspacing="0">
				<tr>
					<td><span class="fontnormal"> 
					<mifos:mifoslabel
						name="Group.sureToDelete" bundle="GroupUIResources" /> 
					<mifos:mifoslabel
						name="Group.dot" bundle="GroupUIResources" /> </span></td>
						
					</tr>
				<tr>
					<logic:messagesPresent>
					<td><br><font class="fontnormalRedBold"><span id="deletegroupmembership.error.message"><html-el:errors
							bundle="GroupUIResources" /></span></font></td>
					</logic:messagesPresent>
				</tr>
				
			</table>

			<%-- Start of information --%>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr class="fontnormal">
							<td align="left" valign="top" class="fontnormal"><mifos:mifoslabel
								name="Group.AssignedloanOfficer" bundle="GroupUIResources"></mifos:mifoslabel>
							<mifos:select 
								property="assignedLoanOfficerId" size="0">
								<c:forEach var="loanOfficer" 
									items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanOfficers')}">
									<html-el:option value="${loanOfficer.personnelId}" >
										<c:out value="${loanOfficer.displayName}" />
									</html-el:option>
								</c:forEach>
							</mifos:select> <br>
							</td>
						</tr>
					</table>

					<table width="95%" border="0" cellpadding="3" cellspacing="0">

						<tr>
							<td width="7%" align="left" valign="top" class="fontnormalbold">
							<span id="deletegroupmembership.label.note">
							<mifos:mifoslabel name="Group.note"
								bundle="GroupUIResources"></mifos:mifoslabel></span></td>
							<td width="93%" align="left" valign="top"><html-el:textarea styleId="deletegroupmembership.input.note"
								property="comment" cols="37" style="width:320px; height:110px;">
							</html-el:textarea></td>
						</tr>
					</table>

					<!-- End of information -->
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="fontnormalbold"><br><span class="fontnormal">
							<mifos:mifoslabel name="Group.transferbranchMsg3" /> 
								<fmt:message key="Group.editMag2ReturnToEdit">
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
						</fmt:message>
								</span></td>
						</tr>
					</table>
					
					<table width="93%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="93%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:submit styleId="deletegroupmembership.button.submit" styleClass="buttn">
								<mifos:mifoslabel name="button.submit"
									bundle="ClientUIResources"></mifos:mifoslabel>
							</html-el:submit> &nbsp; &nbsp; <html-el:button styleId="deletegroupmembership.button.cancel" onclick="goToCancelPage();"
								property="cancelButton" styleClass="cancelbuttn">
								<mifos:mifoslabel name="button.cancel"
									bundle="ClientUIResources"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
					</table>
				<br>			
			  </td>
             </tr>
            </table> 
        	</td>
          </tr>
    </table> 
			<html-el:hidden property="currentFlowKey"
				value="${requestScope.currentFlowKey}" />

		</html-el:form>
	</tiles:put>
</tiles:insert>
