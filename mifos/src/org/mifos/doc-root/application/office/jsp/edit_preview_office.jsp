<%-- 
Copyright (c) 2005-2008 Grameen Foundation USA
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
<!-- edit_preview_office.jsp -->

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<!-- Next is code for setting the hidden veriable to cancel -->
		<script language="javascript" type="text/javascript">
function goToCancelPage(id){
	document.offActionForm.action="offAction.do?method=get&officeId="+id;
	document.offActionForm.submit();
  }
  function goToPreviousPage()
  {
	document.offActionForm.action="offAction.do?method=editprevious"
	offActionForm.submit();

  }
</script>
		<html-el:form action="/offAction.do?method=update">
			<c:set var="BusinessKey" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"></c:set>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"><html-el:link
						href="AdminAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
						<mifos:mifoslabel name="office.labelLinkAdmin"
							bundle="OfficeResources"></mifos:mifoslabel>
					</html-el:link> / <html-el:link
						href="offAction.do?method=getAllOffices&randomNUm=${sessionScope.randomNUm}">
						<mifos:mifoslabel name="office.labelLinkViewOffices"
							bundle="OfficeResources"></mifos:mifoslabel>

					</html-el:link> / <html-el:link
						href="offAction.do?method=get&officeId=${BusinessKey.officeId}&randomNUm=${sessionScope.randomNUm}">
						<c:out value="${BusinessKey.officeName}"></c:out>
					</html-el:link> </span></td>
				</tr>
			</table>
			<table table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">

					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange"><span class="heading"> <c:out
								value="${BusinessKey.officeName}"></c:out> </span> - <mifos:mifoslabel
								name="Office.labelPreviewOfficeInformation" /></td>
						</tr>
						<tr>
							<td class="fontnormal"><mifos:mifoslabel
								name="Office.labelMandatoryInstructions" /></td>
						</tr>
					</table>
					<br>

					<table width="93%" border="0" cellpadding="0" cellspacing="0">
						<tr >

								<logic:messagesPresent>
								<td>
								<font class="fontnormalRedBold"><html-el:errors
									bundle="OfficeUIResources" /></font>
									<br><br></td>
								</logic:messagesPresent>
								</tr>
						<tr>

							<td height="23" class="fontnormalbold"><mifos:mifoslabel
								name="Office.labelOfficeName" /> <span class="fontnormal"> <c:out
								value="${offActionForm.officeName}"></c:out> <br>
							</span> <mifos:mifoslabel name="Office.labelOfficeShortName" /><span
								class="fontnormal"> <c:out value="${offActionForm.shortName}"></c:out><br>
							</span> <mifos:mifoslabel name="Office.labelOfficeType" /> <span
								class="fontnormal"> <c:forEach var="level"
								items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'OfficeLevelList')}">
								<c:if test="${level.levelId == offActionForm.officeLevel }">
									<c:out value="${level.levelName}"></c:out>
								</c:if>
							</c:forEach> </span> <br>
							<mifos:mifoslabel name="Office.labelParentOffice" /><span
								class="fontnormal"> <c:forEach var="parent" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'Parents')}">
								<c:if test="${parent.officeId == offActionForm.parentOfficeId }">
									<c:out value="${parent.displayName}"></c:out>
								</c:if>
							</c:forEach> </span><br>
							<mifos:mifoslabel name="Office.labelStatus" />:<span
								class="fontnormal"> <c:forEach var="status"
								items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'OfficeStatusList')}">
								<c:if test="${status.levelId  ==offActionForm.officeStatus}">
									<c:out value="${status.levelName}"></c:out>
								</c:if>
							</c:forEach> </span> <br>
							<br>
							<mifos:mifoslabel name="Office.labelAddress" /><span
								class="fontnormal"><br> <c:out
								value="${offActionForm.address.displayAddress}"></c:out> </span>
							</td>
						</tr>
						<tr>
							<td class="fontnormalbold"><mifos:mifoslabel
								name="${ConfigurationConstants.CITY}" bundle="OfficeResources"></mifos:mifoslabel>:<span
								class="fontnormal"> <c:out value="${offActionForm.address.city}"></c:out>
							</span>
						</tr>
						<tr id="Office.State">
							<td class="fontnormalbold"><mifos:mifoslabel
								name="${ConfigurationConstants.STATE}"
								bundle="OfficeUIResources" keyhm="Office.State"
								isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel><span
								class="fontnormal"> <c:out
								value="${offActionForm.address.state}"></c:out> </span>
						</tr>
						<tr id="Office.Country">
							<td class="fontnormalbold"><mifos:mifoslabel
								name="Office.labelCountry" keyhm="Office.Country"
								isManadatoryIndicationNotRequired="yes" /><span
								class="fontnormal"> <c:out
								value="${offActionForm.address.country}"></c:out> </span>
						</tr>
						<tr id="Office.PostalCode">
							<td class="fontnormalbold"><mifos:mifoslabel
								name="${ConfigurationConstants.POSTAL_CODE}"
								bundle="OfficeResources" keyhm="Office.PostalCode"
								isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel><span
								class="fontnormal"> <c:out value="${offActionForm.address.zip}"></c:out>

							</span></td>
						</tr>
						<tr>
							<td class="fontnormalbold"><mifos:mifoslabel
								name="Office.labelTelephone" /><span class="fontnormal"> <c:out
								value="${offActionForm.address.phoneNumber}"></c:out></span><br><br>
						<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
							<c:if test="${!empty offActionForm.customFields}">
								<mifos:mifoslabel name="Office.labelAdditionInformation" />
							</c:if> <span class="fontnormal"><br>
							<c:forEach var="cfdef" items="${offActionForm.customFields}">
								<c:forEach var="cf" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
									<c:if test="${cfdef.fieldId==cf.fieldId}">

										<font class="fontnormalBold"> <mifos:mifoslabel
											name="${cf.lookUpEntity.entityType}" bundle="OfficeResources"></mifos:mifoslabel>:</font>
										<span class="fontnormal"><c:out value="${cfdef.fieldValue}" /><br>
										</span>
									</c:if>
								</c:forEach>
							</c:forEach> </span></c:if> <br>
							<br>
							<span class="fontnormal">
							 <html-el:button
								onclick="goToPreviousPage();" property="cancelButton"
								styleClass="insidebuttn">
								<mifos:mifoslabel name="Office.edit" />
							</html-el:button></span></td>
						</tr>
					</table>


					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>

					<table width="93%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center">&nbsp; <!-- Next are submit and cancel button -->

							<html-el:submit styleClass="buttn"><mifos:mifoslabel name="Office.submit" /></html-el:submit>
							&nbsp; <html-el:button
								onclick="goToCancelPage(${BusinessKey.officeId});"
								property="cancelButton"  styleClass="cancelbuttn">
								<mifos:mifoslabel name="Office.cancel" />
							</html-el:button></td>
						</tr>
					</table>

					<br>

					<br>
					</td>
				</tr>
			</table>
			<html-el:hidden property="officeId" value="${offActionForm.officeId}" />
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
