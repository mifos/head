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
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>


<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<input type="hidden" id="page.id" value="EditGroupDetails"/>
	
		<script language="javascript">
	function chkForValidDates(){		
	  		if(! chkForDateTrainedDate())
				return false; 	
	  }
  function chkForDateTrainedDate(){
	 var trainedDate = document.getElementById("trainedDate");	  
  	 if (trainedDate!=undefined && trainedDate!=null){
  	 	var trainedDateFormat = document.getElementById("trainedDateFormat");	  
  	 	var trainedDateYY = document.getElementById("trainedDateYY");	  
	    return (validateMyForm(trainedDate,trainedDateFormat,trainedDateYY))
 	 }
 	 return true;
  }
</script>
		<SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>
		<SCRIPT SRC="pages/application/group/js/groupcommon.js"></SCRIPT>
		<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
		<fmt:setBundle basename="org.mifos.config.localizedResources.GroupUIResources"/>
		<html-el:form action="groupCustAction.do?method=previewManage">
		<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <customtags:headerLink/> </span>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
						<table width="95%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td class="headingorange">
									<span class="heading"> <c:out value="${BusinessKey.displayName}" /> - </span>
									<fmt:message key="Group.editInformation">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
									</fmt:message>
								</td>
							</tr>
							<tr>
								<td class="fontnormal">
									<mifos:mifoslabel name="Group.editMsg1" bundle="GroupUIResources"></mifos:mifoslabel>
									<mifos:mifoslabel name="Group.createpagehead2" bundle="GroupUIResources"></mifos:mifoslabel>
									<fmt:message key="Group.editMag2ReturnToDetail">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
									</fmt:message>
									
									<span class="mandatorytext"><font color="#FF0000"><br> *</font></span>
									<mifos:mifoslabel name="Group.createpagehead4" bundle="GroupUIResources"></mifos:mifoslabel>
								</td>
							</tr>
							<tr>
								<td>
									<font class="fontnormalRedBold"><span id="edit_groupdetails.error.message"><html-el:errors bundle="GroupUIResources" /></span></font>
								</td>
							</tr>
						</table>
						<br>
						<table width="95%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold">
									<fmt:message key="Group.groupDetail">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
									</fmt:message>
									<br>
									<br>
								</td>
							</tr>
							<tr class="fontnormal">
								<td width="25%" align="right">
									<span id="edit_groupdetails.label.displayName">
									<mifos:mifoslabel name="Group.name" mandatory="yes" bundle="GroupUIResources"></mifos:mifoslabel>
									</span>
								</td>
								<td width="75%">
									<mifos:mifosalphanumtext styleId="edit_groupdetails.input.displayName" property="displayName" />
								</td>
							</tr>
							<c:set var ="CenterHierarchy" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'CenterHierarchyExist')}" />
							<c:if test="${CenterHierarchy eq 'No'}">
								<tr class="fontnormal">
									<td align="right" class="fontnormal">
										<mifos:mifoslabel name="Group.loanofficer" bundle="GroupUIResources"></mifos:mifoslabel>
									</td>
									<td>
										<mifos:select name="groupCustActionForm" property="loanOfficerId" size="1">
											<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanOfficers')}" var="loanOfficer">
												<html-el:option value="${loanOfficer.personnelId}">${loanOfficer.displayName}</html-el:option>
											</c:forEach>
										</mifos:select>
									</td>
								</tr>
							</c:if>
							<tr class="fontnormal">
								<td align="right" class="fontnormal">
									<mifos:mifoslabel keyhm="Group.ExternalId" isColonRequired="Yes" name="${ConfigurationConstants.EXTERNALID}" />
								</td>
								<td>
									<table width="95%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="28%">
												<span id="edit_groupdetails.label.externalId">
												<mifos:mifosalphanumtext styleId="edit_groupdetails.input.externalId" property="externalId" keyhm="Group.ExternalId" />
												</span>
											</td>
											<td width="72%" class="fontnormal8pt">
												<mifos:mifoslabel keyhm="Group.ExternalId" name="Center.ExternalIdInfo" />
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr class="fontnormal">
								<td width="17%" height="23" align="right" class="fontnormal">
									<mifos:mifoslabel name="Group.FormedBy" bundle="GroupUIResources"></mifos:mifoslabel>
								</td>
								<td width="83%">
									<c:out value="${BusinessKey.customerFormedByPersonnel.displayName}" />
								</td>
							</tr>
						</table>
						<br>
						<table width="95%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold">
									<mifos:mifoslabel name="Group.trainingstatus" bundle="GroupUIResources"></mifos:mifoslabel>
									<br>
									<br>
								</td>
							</tr>
							<c:choose>
								<c:when test="${BusinessKey.trained == true}">
									<tr class="fontnormal">
										<td width="25%" align="right" class="fontnormal">
											<mifos:mifoslabel keyhm="Group.Trained" name="Group.grouptrained" bundle="GroupUIResources"></mifos:mifoslabel>
										</td>
										<td width="75%">
											<mifos:checkbox keyhm="Group.Trained" property="trained" value="1" disabled="true" />
											<html-el:hidden property="trained" value="1"/>
										</td>
									</tr>
									<tr class="fontnormal">
										<td align="right" class="fontnormal">
											<mifos:mifoslabel name="Group.grouptrainedon" bundle="GroupUIResources"></mifos:mifoslabel>
										</td>
										<td>
											<c:out value="${sessionScope.groupCustActionForm.trainedDate}" />
											<html-el:hidden property="trainedDate"
											value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,BusinessKey.trainedDate)}" />
											<html-el:hidden property="trainedDateFormat" value=""/>
											<html-el:hidden property="trainedDateYY" value=""/>

										</td>
									</tr>
								</c:when>
								<c:otherwise>
									<tr class="fontnormal">
										<td width="25%" align="right" class="fontnormal">
											<mifos:mifoslabel keyhm="Group.Trained" name="Group.grouptrained" bundle="GroupUIResources"></mifos:mifoslabel>
										</td>
										<td width="75%">
											<mifos:checkbox keyhm="Group.Trained" property="trained" value="1" />
										</td>
									</tr>
									<tr class="fontnormal">
										<td align="right" class="fontnormal">
											<mifos:mifoslabel keyhm="Group.TrainedDate" name="Group.grouptrainedon" bundle="GroupUIResources"></mifos:mifoslabel>
										</td>
										<td>
											<date:datetag keyhm="Group.TrainedDate" property="trainedDate"/>
										</td>
									</tr>
								</c:otherwise>
							</c:choose>
						</table>
						<br>
						<br>
						<table width="95%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold">
									<mifos:mifoslabel name="Group.officialstatus" bundle="GroupUIResources"></mifos:mifoslabel>
									<br>
									<br>
								</td>
							</tr>
							<tr class="fontnormal">
								<td width="25%" align="right" valign="top" class="fontnormal" style="padding-top:8px;">
									<fmt:message key="Group.assignclientstogrouptitles">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
									</fmt:message>

								</td>
								<td width="75%">
									<table width="80%" border="0" cellspacing="0" cellpadding="3">
										<c:forEach var="pos" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'positions')}" varStatus="loopStatus">
												<bean:define id="ctr">
													<c:out value="${loopStatus.index}" />
												</bean:define>
												<tr class="fontnormal">
													<td width="17%">
														<c:out value="${pos.name}" />
														:
													</td>
													<td width="83%">
														<c:set var="clientcol" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'clients')}" />
														<mifos:select name="groupCustActionForm" property='customerPosition[${ctr}].customerId' size="1">
															<html-el:options collection="clientcol" property="customerId" labelProperty="displayName" />
														</mifos:select>
														<html-el:hidden property='customerPosition[${ctr}].positionId' value="${pos.id}"></html-el:hidden>
													</td>
												</tr>
											</c:forEach>
									</table>

								</td>
							</tr>
						</table>
						<br>
						<table width="95%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold">
									<mifos:mifoslabel name="Group.address" bundle="GroupUIResources"  keyhm="Group.Address"></mifos:mifoslabel>
									<br>
									<br>
								</td>
							</tr>

							<tr class="fontnormal">
								<td width="25%" align="right">
									<span id="edit_groupdetails.label.address1">
									<mifos:mifoslabel keyhm="Group.Address1" isColonRequired="Yes" name="${ConfigurationConstants.ADDRESS1}" />
									</span>
								</td>
								<td width="75%">
									<mifos:mifosalphanumtext styleId="edit_groupdetails.input.address1" keyhm="Group.Address1" name="groupCustActionForm" property="address.line1" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<span id="edit_groupdetails.label.address2">
									<mifos:mifoslabel keyhm="Group.Address2" isColonRequired="Yes" name="${ConfigurationConstants.ADDRESS2}" />
									</span>
								</td>
								<td>
									<mifos:mifosalphanumtext styleId="edit_groupdetails.input.address2" keyhm="Group.Address2" name="groupCustActionForm" property="address.line2"  />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<span id="edit_groupdetails.label.address3">
									<mifos:mifoslabel keyhm="Group.Address3" isColonRequired="Yes" name="${ConfigurationConstants.ADDRESS3}" />
									</span>
								</td>
								<td>
									<mifos:mifosalphanumtext styleId="edit_groupdetails.input.address3" keyhm="Group.Address3" name="groupCustActionForm" property="address.line3" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<span id="edit_groupdetails.label.city">
									<mifos:mifoslabel keyhm="Group.City" isColonRequired="Yes" name="${ConfigurationConstants.CITY}" />
									</span>
								</td>
								<td>
									<mifos:mifosalphanumtext styleId="edit_groupdetails.input.city" keyhm="Group.City" name="groupCustActionForm" property="address.city"/>
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<span id="edit_groupdetails.label.state">
									<mifos:mifoslabel keyhm="Group.State" isColonRequired="Yes" name="${ConfigurationConstants.STATE}" />
									</span>
								</td>
								<td>
									<mifos:mifosalphanumtext styleId="edit_groupdetails.input.state" keyhm="Group.State" name="groupCustActionForm" property="address.state" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<span id="edit_groupdetails.label.country">
									<mifos:mifoslabel keyhm="Group.Country" name="Group.country" bundle="GroupUIResources" />
									</span>
								</td>
								<td>
									<mifos:mifosalphanumtext styleId="edit_groupdetails.input.country" keyhm="Group.Country" name="groupCustActionForm" property="address.country" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<span id="edit_groupdetails.label.postalCode">
									<mifos:mifoslabel keyhm="Group.PostalCode" isColonRequired="Yes" name="${ConfigurationConstants.POSTAL_CODE}" />
									</span>
								</td>
								<td>
									<mifos:mifosalphanumtext styleId="edit_groupdetails.input.postalCode" keyhm="Group.PostalCode" name="groupCustActionForm" property="address.zip" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<span id="edit_groupdetails.label.telephone">
									<mifos:mifoslabel keyhm="Group.PhoneNumber" name="Group.telephone" bundle="GroupUIResources" />
									</span>
								</td>
								<td>
									<mifos:mifosalphanumtext styleId="edit_groupdetails.input.telephone" keyhm="Group.PhoneNumber" name="groupCustActionForm" property="address.phoneNumber"/>
								</td>
							</tr>

						</table>

				<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
				<br>
						<%--Custom Fields start  --%>
						<table width="95%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold">
									<mifos:mifoslabel name="Group.additionalinformation" bundle="GroupUIResources"></mifos:mifoslabel>
									<br>
									<br>
								</td>
							</tr>

							<!-- For each custom field definition in the list custom field entity is passed as key to mifos label -->
							<c:forEach var="cfdef" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}" varStatus="loopStatus">
								<bean:define id="ctr">
									<c:out value="${loopStatus.index}" />
								</bean:define>
								<c:forEach var="cf" items="${sessionScope.groupCustActionForm.customFields}" varStatus="loopStatus">
									<c:if test="${cfdef.fieldId==cf.fieldId}">
										<tr class="fontnormal">
											<td width="25%" align="right">
												<span id="edit_groupdetails.label.customField"><mifos:mifoslabel name="${cfdef.lookUpEntity.entityType}" mandatory="${cfdef.mandatoryStringValue}" bundle="GroupUIResources" isColonRequired="Yes"></mifos:mifoslabel></span>
												
											</td>
											<td width="75%">
												<c:if test="${cfdef.fieldType == 1}">
													<mifos:mifosnumbertext styleId="edit_groupdetails.input.customField" name="groupCustActionForm" property='customField[${ctr}].fieldValue' value="${cf.fieldValue}" maxlength="200" />
												</c:if>
												<c:if test="${cfdef.fieldType == 2}">
													<mifos:mifosalphanumtext styleId="edit_groupdetails.input.customField" name="groupCustActionForm" property='customField[${ctr}].fieldValue' value="${cf.fieldValue}" maxlength="200" />
												</c:if>
												<c:if test="${cfdef.fieldType == 3}">
													<date:datetag property="customField[${ctr}].fieldValue" />
												</c:if>
												<html-el:hidden property='customField[${ctr}].fieldId' value="${cfdef.fieldId}"></html-el:hidden>
												<html-el:hidden property='fieldTypeList' value='${cfdef.fieldType}' />
											</td>
										</tr>
									</c:if>
								</c:forEach>
							</c:forEach>

						</table>
					</c:if>
						<br>
						<%--Custom Fields end  --%>
						<table width="95%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center" class="blueline">
									&nbsp;
								</td>
							</tr>
						</table>
						<br>
						<table width="95%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center">
									<html-el:submit styleId="edit_groupdetails.button.preview" styleClass="buttn" >
										<mifos:mifoslabel name="button.preview" bundle="GroupUIResources"></mifos:mifoslabel>
									</html-el:submit>
									&nbsp; &nbsp;
									<html-el:button styleId="edit_groupdetails.button.cancel" property="cancelBtn" styleClass="cancelbuttn" onclick="goToCancelPage(this.form)">
										<mifos:mifoslabel name="button.cancel" bundle="GroupUIResources"></mifos:mifoslabel>
									</html-el:button>
								</td>
							</tr>
						</table>
						<br>
					</td>
				</tr>
			</table>

			<html-el:hidden property="input" value="ManageGroup" />
		</html-el:form>
	</tiles:put>
</tiles:insert>

