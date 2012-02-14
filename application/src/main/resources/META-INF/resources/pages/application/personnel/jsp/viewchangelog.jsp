<%--
Copyright (c) 2005-2011 Grameen Foundation USA
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing
permissions and limitations under the License.

See also http://www.apache.org/licenses/LICENSE-2.0.html for an
explanation of the license and how it is applied.
--%>

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom" %>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<span id="page.id" title="viewchangelog"></span>
	<script>
	function returnToDetails(){
		personActionForm.action="PersonAction.do?method=cancelChangeLog";
		personActionForm.submit();
	}
	</script>
		<html-el:form method="post" action="/PersonAction.do">
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<c:set
				value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
				var="BusinessKey" />
		   <table width="95%" border="0" cellpadding="0" cellspacing="0">
	          <tr>
	           <c:url value="AdminAction.do" var="AdminActionLoadMethodUrl" >
	            <c:param name="method" value="load" />
	           </c:url >
	            <td class="bluetablehead05">
	            <span class="fontnormal8pt"><a id="viewchangelog.link.admin" href="${AdminActionLoadMethodUrl}">
		             <mifos:mifoslabel name="Personnel.Admin" bundle="PersonnelUIResources"></mifos:mifoslabel>
	           	  </a>
			   /
			  	 <c:url value="PersonAction.do" var="PersonActionLoadSearchMethodUrl" >
			  	  <c:param name="method" value="loadSearch" />
			  	 </c:url >
			  	 
			  	  <a id="viewchangelog.link.viewUsers" href="${PersonActionLoadSearchMethodUrl}">
			  	  <mifos:mifoslabel name="Personnel.ViewUsers" bundle="PersonnelUIResources"></mifos:mifoslabel>
				 <c:url value="PersonAction.do" var="PersonActionGetMethodUrl" >
				  <c:param name="method" value="get" />
				  <c:param name="globalPersonnelNum" value="${BusinessKey.globalPersonnelNum}" />
				 </c:url >
			  	  </a> /
				  <a id="viewchangelog.link.viewUser" href="${PersonActionGetMethodUrl}">
		             <c:out value="${BusinessKey.displayName}"/>
	           	  </a>
	            </span></td>
	          </tr>
	        </table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" height="24" align="left" valign="top"
						class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
									<td>
										<font class="fontnormalRedBold">
											<span id="viewchangelog.error.message"><html-el:errors bundle="PersonnelUIResources" /></span>
										</font>
									</td>
						</tr>
						<tr>
							<td class="headingorange">
								<span class="heading">
									<c:out value="${BusinessKey.displayName}" />
								</span> -
								<mifos:mifoslabel name="Personnel.change_log"/>
							</td>
						</tr>

						<tr>
							<td class="fontnormal"><br>
								<mifos:mifoslabel name="Personnel.rec_creation_date" />:&nbsp;
								<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,BusinessKey.createdDate)}" />
							</td>
						</tr>
					</table>
					<br>
					<mifoscustom:mifostabletag moduleName="org/mifos/framework/components/audit/util/resources" scope="session" source="auditLogRecords" xmlFileName="AuditLog.xml" passLocale="true"/>
					<br>
					<table width="96%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:button styleId="viewchangelog.button.back" property="returnToAccountDetailsbutton"
								onclick="returnToDetails()" styleClass="buttn">
								<mifos:mifoslabel name="Personnel.returnToDetails"
									bundle="PersonnelUIResources" />
							</html-el:button></td>
						</tr>
					</table>
					</td>

				</tr>
			</table>
			<br>

			<html-el:hidden property="entityType" value="${param.entityType}"/>
		</html-el:form>
	</tiles:put>
</tiles:insert>
