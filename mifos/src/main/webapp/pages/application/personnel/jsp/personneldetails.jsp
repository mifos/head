<%--
Copyright (c) 2005-2009 Grameen Foundation USA
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
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/personnel/personnelfunctions" prefix="personnelfn"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<script language="javascript">

  function goToCancelPage(){
	personActionForm.action="PersonAction.do?method=cancel";
	personActionForm.submit();
  }
  function viewChangeLog(){
	  personActionForm.action="PersonnelAction.do?method=search&input=UserChangeLog";
	  personActionForm.submit();
  }
</script>
		<html-el:form action="PersonAction.do?method=preview">

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <a id="personneldetails.link.admin"
						href="AdminAction.do?method=load"> <mifos:mifoslabel
						name="Personnel.Admin" bundle="PersonnelUIResources"></mifos:mifoslabel>
					</a> / <a id="personneldetails.link.viewUsers" href="PersonAction.do?method=loadSearch"> <mifos:mifoslabel
						name="Personnel.ViewUsers" bundle="PersonnelUIResources"></mifos:mifoslabel>
					</a> / </span> <c:set var="personnelBO" scope="request"
						value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" />
					<span class="fontnormal8ptbold"><c:out
						value="${personnelBO.displayName}" /></span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">

				<td width="70%" align="left" valign="top" class="paddingL15T15">
				<table width="96%" border="0" cellpadding="3" cellspacing="0">
					<tr>
						<td width="50%" height="23" class="headingorange"><span id="personneldetails.text.fullName"><c:out
							value="${personnelBO.displayName}" /></span></td>
						<td width="50%" align="right"><a id="personneldetails.link.editUser"
							href="PersonAction.do?method=manage&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}"> <mifos:mifoslabel
							name="Personnel.EditUserInformation"
							bundle="PersonnelUIResources"></mifos:mifoslabel> </a></td>
					</tr>
					<tr>
						<td colspan="2"><font class="fontnormalRedBold"> <span id="personneldetails.error.message"><html-el:errors
							bundle="PersonnelUIResources" /></span> </font></td>
					</tr>
					<tr>
						<td height="23" class="fontnormalbold"><span class="fontnormal"> <c:choose>
							<%-- Active State --%>
							<c:when test="${personnelBO.status.id == 1}">
								<mifos:MifosImage id="active" moduleName="personnel" />
							</c:when>
							<c:when test="${personnelBO.status.id  == 2}">
								<mifos:MifosImage id="inactive" moduleName="personnel" />
							</c:when>
							<c:otherwise>
							</c:otherwise>
						</c:choose> <span id="personneldetails.text.status"><c:out value="${personnelBO.status.name}" /></span> </span>
						<c:if test="${personnelBO.locked == 'true'}">
							<span class="fontnormalRed"> <mifos:mifoslabel
								name="Personnel.Locked" bundle="PersonnelUIResources"></mifos:mifoslabel>
							</span>
						</c:if> &nbsp;<br>
						</td>
					</tr>

					<tr id="Personnel.GovernmentId">
						<td class="fontnormalbold"><span class="fontnormal"> <mifos:mifoslabel
							name="${ConfigurationConstants.GOVERNMENT_ID}"
							bundle="PersonnelUIResources" keyhm="Personnel.GovernmentId"
							isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
						<c:out value="${personnelBO.personnelDetails.governmentIdNumber}" />
						<br></td>
					</tr>

					<tr>
						<td class="fontnormalbold"><span class="fontnormal"> <mifos:mifoslabel
							name="Personnel.Email" bundle="PersonnelUIResources"></mifos:mifoslabel>
						<span id="personneldetails.text.email"><c:out value="${personnelBO.emailId}" /></span> <br>

						<mifos:mifoslabel name="Personnel.DOB"
							bundle="PersonnelUIResources"></mifos:mifoslabel> <c:out
							value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,personnelBO.personnelDetails.dob)}" />;
						<c:out value="${personnelBO.age}" /> <mifos:mifoslabel
							name="Personnel.YearsOld" bundle="PersonnelUIResources"></mifos:mifoslabel>
						<br>

						<c:forEach
							items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'genderList')}"
							var="item">
							<c:if test="${personnelBO.personnelDetails.gender == item.id}">
											${item.name}
								</c:if>
						</c:forEach> <c:if
							test="${!empty personnelBO.personnelDetails.maritalStatus}">
							<c:out value=";" />
							<c:forEach
								items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'maritalStatusList')}"
								var="item">
								<c:if
									test="${personnelBO.personnelDetails.maritalStatus == item.id}">
											${item.name}
								</c:if>
							</c:forEach>
						</c:if> <br>
						<mifos:mifoslabel name="Personnel.LanguagePreferred"
							bundle="PersonnelUIResources"></mifos:mifoslabel> <c:if
							test="${!empty personnelBO.preferredLocale}">
							<%--<c:forEach
								items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'languageList')}"
								var="item">

								<c:if test="${personnelBO.preferredLocale.localeId == item.id}">
											${item.name}
								</c:if>
							</c:forEach> --%>
							${personnelBO.preferredLocale.language.languageName}
						</c:if> <br>
						<mifos:mifoslabel name="Personnel.DOJMFI"
							bundle="PersonnelUIResources"></mifos:mifoslabel> <c:out
							value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,personnelBO.personnelDetails.dateOfJoiningMFI)}" />
						<br>
						<mifos:mifoslabel name="Personnel.DOJBranch"
							bundle="PersonnelUIResources"></mifos:mifoslabel> <c:out
							value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,personnelBO.personnelDetails.dateOfJoiningBranch)}" />
						</span><br>
						<br>
						<mifos:mifoslabel name="Personnel.Address"
							bundle="PersonnelUIResources"></mifos:mifoslabel><br>
							<c:if test="${ empty personnelBO.personnelDetails.address.displayAddress
									&&  empty personnelBO.personnelDetails.address.city
									&&  empty personnelBO.personnelDetails.address.state
									&&  empty personnelBO.personnelDetails.address.country
									&&  empty personnelBO.personnelDetails.address.zip
									&&  empty personnelBO.personnelDetails.address.phoneNumber}">
									<br>
									<span
										class="fontnormal"><mifos:mifoslabel name="Personnel.addressnotentered"
							bundle="PersonnelUIResources"></mifos:mifoslabel></span>
										<br>
										</c:if>
						<span class="fontnormal"> <c:if
							test="${!empty personnelBO.personnelDetails.address.displayAddress}">
							<c:out
								value="${personnelBO.personnelDetails.address.displayAddress}" />
							<br>
						</c:if> <c:if
							test="${!empty personnelBO.personnelDetails.address.city}">
							<c:out value="${personnelBO.personnelDetails.address.city}" />
							<br>
						</c:if> <c:if
							test="${!empty personnelBO.personnelDetails.address.state}">
							<c:out value="${personnelBO.personnelDetails.address.state}" />
							<br>
						</c:if> <c:if
							test="${!empty personnelBO.personnelDetails.address.country}">
							<c:out value="${personnelBO.personnelDetails.address.country}" />
							<br>
						</c:if> <c:if
							test="${!empty personnelBO.personnelDetails.address.zip}">
							<c:out value="${personnelBO.personnelDetails.address.zip}" />
							<br>
						</c:if> <br>
						</span> <c:if
							test="${!empty personnelBO.personnelDetails.address.phoneNumber}">
							<span class="fontnormal"> <mifos:mifoslabel
								name="Personnel.Telephone" bundle="PersonnelUIResources"
								isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <c:out
								value="${personnelBO.personnelDetails.address.phoneNumber}" />
							</span>
							<br>
							<br>
						</c:if>
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td width="71%" class="fontnormalbold"><mifos:mifoslabel
									name="Personnel.OfficePermissions"
									bundle="PersonnelUIResources"></mifos:mifoslabel> <br>
								<span class="fontnormal"> <mifos:mifoslabel
									name="Personnel.Office" bundle="PersonnelUIResources"></mifos:mifoslabel>
								<c:out value="${personnelBO.office.officeName}" /> <br>
								<mifos:mifoslabel name="Personnel.UserTitle"
									bundle="PersonnelUIResources"></mifos:mifoslabel> <c:forEach
									items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'titleList')}"
									var="item">

									<c:if test="${personnelBO.title == item.id}">
											${item.name}
								</c:if>
								</c:forEach> <br>
								<mifos:mifoslabel name="Personnel.UserHierarchy"
									bundle="PersonnelUIResources"></mifos:mifoslabel> <c:forEach
									items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'personnelLevelList')}"
									var="item">

									<c:if test="${personnelBO.level.id == item.id}">
											${item.name}
								</c:if>
								</c:forEach> <br>
								<mifos:mifoslabel name="Personnel.Roles"
									bundle="PersonnelUIResources"></mifos:mifoslabel> <c:forEach
									var="personnelRole" items="${personnelBO.personnelRoles}"
									varStatus="loopStatus">
									<bean:define id="ctr">
										<c:out value="${loopStatus.index}" />
									</bean:define>
									<c:choose>
										<c:when test="${ctr == 0}">
											<c:out value="${personnelRole.role.name}" />
										</c:when>
										<c:otherwise>
					 						, <c:out value="${personnelRole.role.name}" />
										</c:otherwise>
									</c:choose>
								</c:forEach> </span></td>
							</tr>
						</table>
						<br>
						<mifos:mifoslabel name="Personnel.LoginInformation"
							bundle="PersonnelUIResources"></mifos:mifoslabel><br>
						<span class="fontnormal"> <mifos:mifoslabel
							name="Personnel.UserName" bundle="PersonnelUIResources"></mifos:mifoslabel>
						<c:out value="${personnelBO.userName}" /> </span><br>
						<br>
						<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
						<c:if test="${!empty personnelBO.customFields}">
							<mifos:mifoslabel name="Personnel.AdditionalInfo"
								bundle="PersonnelUIResources"></mifos:mifoslabel>
							<br>
						</c:if> <c:forEach var="cfdef"
							items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
							<c:forEach var="cf"
								items="${personnelBO.customFields}">
								<c:if test="${cfdef.fieldId==cf.fieldId}">
									<span class="fontnormal"> <mifos:mifoslabel
										name="${cfdef.lookUpEntity.entityType}"
										bundle="PersonnelUIResources"></mifos:mifoslabel>:
									<c:out value="${cf.fieldValue}" /> </span>
									<br>
								</c:if>
							</c:forEach>
						</c:forEach> <br>
						</c:if>
						<span class="fontnormal"> <html-el:link styleId="personneldetails.link.viewChangeLog"
							href="PersonAction.do?method=loadChangeLog&entityType=Personnel&entityId=${personnelBO.personnelId}&currentFlowKey=${requestScope.currentFlowKey}">
							<mifos:mifoslabel name="Personnel.ViewChangeLog"
								bundle="PersonnelUIResources"></mifos:mifoslabel>
						</html-el:link> </span>
						</td>
						<td height="23" align="right" valign="top" class="fontnormal"><c:if
							test="${personnelBO.locked == 'true'}">
							<a id="personneldetails.link.unlockUser" href="PersonAction.do?method=loadUnLockUser&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
							 <mifos:mifoslabel
								name="Personnel.UnlockUser" bundle="PersonnelUIResources"></mifos:mifoslabel>
							</a>
						</c:if></td>
					</tr>
				</table>
				<br>
				</td>
				<td width="30%" align="left" valign="top" class="paddingleft1">
				<table width="100%" border="0" cellpadding="2" cellspacing="0"
					class="bluetableborder">
					<tr>
						<td class="bluetablehead05"><span class="fontnormalbold"> <mifos:mifoslabel
							name="Personnel.RecentNotes" bundle="PersonnelUIResources"></mifos:mifoslabel>
						</span></td>
					</tr>
					<tr>
						<td class="paddingL10"><img src="pages/framework/images/trans.gif" width="10"
							height="2"></td>
					</tr>
					<tr>
						<td class="paddingL10"><c:choose>
							<c:when test="${!empty personnelBO.recentPersonnelNotes}">
								<c:forEach var="note" items="${personnelBO.recentPersonnelNotes}">
									<span class="fontnormal8ptbold"> <c:out
										value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,note.commentDate)}" />:
									</span>
									<span class="fontnormal8pt"> <c:out value="${note.comment}" />
									-<em><c:out value="${note.personnelName}" /></em><br>
									<br>
									</span>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<span class="fontnormal"> <mifos:mifoslabel
									name="Personnel.NoNotesAvailable" bundle="PersonnelUIResources" />
								</span>
							</c:otherwise>
						</c:choose></td>

					</tr>
					<tr>
						<td align="right" class="paddingleft05"><span
							class="fontnormal8pt"> <c:if test="${!empty personnelBO.recentPersonnelNotes}">
							<a id="personneldetails.link.seeAllNotes" href="personnelNoteAction.do?method=search&personnelId=<c:out value="${personnelBO.personnelId}"/>&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}&personnelName=<c:out value="${personnelBO.displayName}"/>"> <mifos:mifoslabel
								name="Personnel.SeeAllNotes" bundle="PersonnelUIResources"></mifos:mifoslabel>
							</a>
							<br>
						</c:if> <a id="personneldetails.link.addNote" href="personnelNoteAction.do?method=load&personnelId=<c:out value="${personnelBO.personnelId}"/>&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}"> <mifos:mifoslabel
							name="Personnel.AddNote" bundle="PersonnelUIResources"></mifos:mifoslabel>
						</a> </span></td>
					</tr>
				</table>
				<p class="paddingleft1">&nbsp;</p>
				</td>

			</table>
			<br>

		</html-el:form>
	</tiles:put>
</tiles:insert>
