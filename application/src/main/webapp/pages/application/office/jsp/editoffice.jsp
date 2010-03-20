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
<!-- editoffice.jsp -->

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
	<span id="page.id" title="editoffice" />
	
<script language="javascript">
	function goToCancelPage(){
		document.offActionForm.action="offAction.do?method=get";
		offActionForm.submit();
	}
  function papulateParent()
  {
	document.offActionForm.action="offAction.do?method=loadParent&input=edit";
	offActionForm.submit();
  }
</script>
		<html-el:form action="/offAction.do?method=editpreview">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<c:set var="BusinessKey" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"></c:set>
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"><html-el:link styleId="editoffice.link.admin"
						href="AdminAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
						<mifos:mifoslabel name="Office.labelLinkAdmin" />
					</html-el:link> / <html-el:link styleId="editoffice.link.viewOffices"
						href="offAction.do?method=getAllOffices&randomNUm=${sessionScope.randomNUm}">
						<mifos:mifoslabel name="Office.labelLinkViewOffices" />
					</html-el:link> / <html-el:link styleId="editoffice.link.viewOffice"
						href="offAction.do?method=get&officeId=${offActionForm.officeId}&randomNUm=${sessionScope.randomNUm}">
						<c:out value="${BusinessKey.officeName}"></c:out>
					</html-el:link> </span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange"><span class="heading"><c:out
								value="${BusinessKey.officeName}"></c:out> - </span><mifos:mifoslabel
								name="Office.labelEditOfficeInfo" /></td>
						</tr>
						<tr>
							<td class="fontnormal"><mifos:mifoslabel
								name="Office.labelMandatoryInstructions" /> <mifos:mifoslabel
								name="Office.labelFieldsMarkedWithAsterisk" mandatory="yes" /></td>
						</tr>
					</table>
					<br>
					<font class="fontnormalRedBold"><span id="editoffice.error.message"><html-el:errors
						bundle="OfficeUIResources" /></span> </font>

					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
								name="Office.labelOfficeDetails" /><br>
							<br>
							</td>
						</tr>
						<tr class="fontnormal">
							<td width="20%" align="right"><span id="editoffice.label.officeName"><mifos:mifoslabel
								name="Office.labelOfficeName" mandatory="yes" /></span></td>
							<td width="80%"><mifos:mifosalphanumtext styleId="editoffice.input.officeName" property="officeName"
								maxlength="200" name="offActionForm" ></mifos:mifosalphanumtext></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><span id="editoffice.label.shortName"><mifos:mifoslabel
								name="Office.labelOfficeShortName" mandatory="yes" /></span></td>
							<td><mifos:mifosalphanumtext styleId="editoffice.input.shortName" property="shortName" size="4"
								maxlength="4" name="offActionForm"></mifos:mifosalphanumtext></td>
						</tr>
						<tr class="fontnormal">
							<td align="right">
							<c:choose>
							<c:when test="${offActionForm.officeLevel eq OfficeLevel.HEADOFFICE.value}">
							<mifos:mifoslabel name="Office.labelOfficeType"
								 />
							</c:when>
							<c:otherwise>
							<mifos:mifoslabel name="Office.labelOfficeType"
								mandatory="yes" />
							</c:otherwise>

							</c:choose>
								</td>
							<td>

							<c:choose>
							<c:when test="${offActionForm.officeLevel eq OfficeLevel.HEADOFFICE.value or BusinessKey.level.id eq OfficeLevel.BRANCHOFFICE.value}">
							  <mifos:select name="offActionForm" property="officeLevel" disabled="true"
								size="1" >
									<html-el:option value="${BusinessKey.level.id}">${BusinessKey.level.name}</html-el:option>
							  </mifos:select>

							</c:when>
							<c:otherwise>
							<mifos:select name="offActionForm" property="officeLevel"
								size="1" onchange="papulateParent()">
								<c:forEach var="levelList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'OfficeLevelList')}" >
									<html-el:option value="${levelList.levelId}">${levelList.levelName}</html-el:option>
								</c:forEach>
							</mifos:select>
							</c:otherwise>
							</c:choose>

							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel
								name="Office.labelParentOffice" mandatory="yes" /></td>
							<td>
							<c:if test="${not empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'Parents')}">
								<mifos:select name="officeActionForm"
									property="parentOfficeId" size="1"
									value="${offActionForm.parentOfficeId}">
									<c:forEach var="parentList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'Parents')}" >
										<html-el:option value="${parentList.officeId}">${parentList.displayName}</html-el:option>
									</c:forEach>
								</mifos:select>
							</c:if>
							</td>
						</tr>
					</table>
					<br>
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
								name="Office.labelStatus" /><br>
							<br>
							</td>
						</tr>
						<tr class="fontnormal">
							<td width="20%" align="right"><mifos:mifoslabel
								name="Office.labelChangeStatus" /></td>
							<td width="80%"><c:choose>
								<c:when
									test='${offActionForm.officeLevel eq OfficeLevel.HEADOFFICE.value}'>
									<html-el:select name="offActionForm" disabled="true"
										property="officeStatus" size="1"
										value="${offActionForm.officeStatus}">
										<c:forEach var="statusList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'OfficeStatusList')}" >
											<html-el:option value="${statusList.levelId}">${statusList.levelName}</html-el:option>
										</c:forEach>
									</html-el:select>

								</c:when>
								<c:otherwise>
									<html-el:select name="officeActionForm" property="officeStatus"
										size="1" value="${offActionForm.officeStatus}">
										<c:forEach var="statusList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'OfficeStatusList')}" >
											<html-el:option value="${statusList.levelId}">${statusList.levelName}</html-el:option>
										</c:forEach>

									</html-el:select>
								</c:otherwise>
							</c:choose></td>
						</tr>
					</table>
					<br>
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
								name="Office.labelOfficeAddress" /><br>
							<br>
							</td>
						</tr>
						<tr class="fontnormal">
							<td width="20%" align="right"><span id="editoffice.label.address1"><mifos:mifoslabel
								name="${ConfigurationConstants.ADDRESS1}" keyhm="Office.Address1" isColonRequired="yes"
								bundle="OfficeUIResources"/></span>:</td>
							<td width="80%"><mifos:mifosalphanumtext styleId="editoffice.input.address1"
								property="address.line1" keyhm="Office.Address1"
								value="${offActionForm.address.line1}"
								maxlength="200"></mifos:mifosalphanumtext></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><span id="editoffice.label.address2"><mifos:mifoslabel
								name="${ConfigurationConstants.ADDRESS2}"
								bundle="OfficeUIResources" keyhm="Office.Address2" isColonRequired="yes"/></span></td>
							<td><mifos:mifosalphanumtext styleId="editoffice.input.address2" property="address.line2"  keyhm="Office.Address2"
								value="${offActionForm.address.line2}"
								maxlength="200"></mifos:mifosalphanumtext></td>
						</tr>
						<tr class="fontnormal" id="Office.Address3">
							<td align="right"><span id="editoffice.label.address3"><mifos:mifoslabel
								name="${ConfigurationConstants.ADDRESS3}"
								bundle="OfficeUIResources" keyhm="Office.Address3">
								</mifos:mifoslabel></span>:</td>
							<td><mifos:mifosalphanumtext styleId="editoffice.input.address3" property="address.line3"
								value="${offActionForm.address.line3}"
								maxlength="200" keyhm="Office.Address3">
							</mifos:mifosalphanumtext></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><span id="editoffice.label.city"><mifos:mifoslabel
								name="${ConfigurationConstants.CITY}" bundle="OfficeUIResources"/></span>:</td>
							<td><mifos:mifosalphanumtext styleId="editoffice.input.city" property="address.city"
								value="${offActionForm.address.city}" maxlength="100"/>
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><span id="editoffice.label.state"><mifos:mifoslabel
								name="${ConfigurationConstants.STATE}" bundle="OfficeUIResources"
								keyhm="Office.State" isColonRequired="yes"/></span></td>
							<td><mifos:mifosalphanumtext styleId="editoffice.input.state" property="address.state"
								value="${offActionForm.address.state}" maxlength="100"
								keyhm="Office.State"/></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><span id="editoffice.label.country"><mifos:mifoslabel name="Office.labelCountry"
								 keyhm="Office.Country"/></span></td>
							<td><mifos:mifosalphanumtext styleId="editoffice.input.country" property="address.country"
								value="${offActionForm.address.country}" maxlength="100"
								keyhm="Office.Country"/></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><span id="editoffice.label.postalCode"><mifos:mifoslabel
								name="${ConfigurationConstants.POSTAL_CODE}"
								bundle="OfficeUIResources" keyhm="Office.PostalCode"
								isColonRequired="yes"/></span></td>
							<td><mifos:mifosalphanumtext styleId="editoffice.input.postalCode" property="address.zip"
								value="${offActionForm.address.zip}"
								maxlength="20" keyhm="Office.PostalCode"/>
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><span id="editoffice.label.phoneNumber"><mifos:mifoslabel name="Office.labelTelephone"
								/></span></td>

							<td><mifos:mifosalphanumtext styleId="editoffice.input.phoneNumber" property="address.phoneNumber"
								value="${offActionForm.address.phoneNumber}"
								maxlength="20"/></td>
						</tr>
					</table>
					<br>
					<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
									name="office.labelAdditionInformation" bundle="OfficeResources"></mifos:mifoslabel>
								<br>
								<br>
								</td>
							</tr>
						<!-- For each custom field definition in the list custom field entity is passed as key to mifos label -->
						<c:forEach var="customFieldDef"
							items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}" varStatus="loopStatus">
							<bean:define id="ctr">
								<c:out value="${loopStatus.index}" />
							</bean:define>
							<c:forEach var="cf"
								items="${offActionForm.customFields}">
								<c:if test="${customFieldDef.fieldId==cf.fieldId}">
									<tr class="fontnormal">
										<td width="21%" align="right"><span id="editoffice.label.customField""><mifos:mifoslabel
											name="${customFieldDef.lookUpEntity.entityType}"
											mandatory="${customFieldDef.mandatoryStringValue}"
											bundle="OfficeUIResources"></mifos:mifoslabel></span>:
										</td>
										<td width="79%"><c:if test="${customFieldDef.fieldType == 1}">
											<mifos:mifosnumbertext styleId="editoffice.input.customField" name="offActionForm"
												property='customField[${ctr}].fieldValue'
												value="${cf.fieldValue}" maxlength="200" />
										</c:if> <c:if test="${customFieldDef.fieldType == 2}">
											<mifos:mifosalphanumtext styleId="editoffice.input.customField" name="offActionForm"
												property='customField[${ctr}].fieldValue'
												value="${cf.fieldValue}" maxlength="200" />
										</c:if> <c:if test="${customFieldDef.fieldType == 3}">
											<mifos:mifosalphanumtext styleId="editoffice.input.customField" name="offActionForm"
												property='customField[${ctr}].fieldValue'
												value="${cf.fieldValue}" maxlength="200" />

										</c:if> <html-el:hidden property='customField[${ctr}].fieldId'
											value="${cf.fieldId}"></html-el:hidden></td>
									</tr>
								</c:if>
							</c:forEach>
						</c:forEach>
					</table>
			</c:if>
					<!--Custom Fields end  -->
			</table>
			<table width="93%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="center" class="blueline">&nbsp;</td>
				</tr>
			</table>
			<br>
			<table width="93%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<!-- Next are submit and cancel button -->
					<td align="center"><html-el:submit styleId="editoffice.button.preview" styleClass="buttn">
						<mifos:mifoslabel name="office.button.preview"
							bundle="OfficeResources"></mifos:mifoslabel>
					</html-el:submit> &nbsp; <html-el:button styleId="editoffice.button.cancel"
						onclick="goToCancelPage();" property="cancelButton" 
						styleClass="cancelbuttn">
						<mifos:mifoslabel name="office.button.cancel"
							bundle="OfficeResources"></mifos:mifoslabel>
					</html-el:button></td>
				</tr>
			</table>
			<br>
			<br>
			<html-el:hidden property="officeId" value="${BusinessKey.officeId}" />
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>

	</tiles:put>

</tiles:insert>

