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
<!-- CreateNewOffice.jsp -->

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".create">
	<tiles:put name="body" type="string">
	<span style="display: none" id="page.id">CreateNewOffice</span>
	

		<script language="javascript">
	function goToCancelPage(){
		document.offActionForm.action="AdminAction.do?method=load";
		offActionForm.submit();
  	}

  function papulateParent(selectBox){
		if(selectBox.selectedIndex > 0)
		{
	  document.offActionForm.action="offAction.do?method=loadParent";
	  offActionForm.submit();
	}
  }
</script>
		<html-el:form action="/offAction.do?method=preview">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="350" align="left" valign="top" bgcolor="#FFFFFF">
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td class="bluetablehead">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td width="27%">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/bigarrow.gif"
												width="17px" height="17px"></td>
											<td class="timelineboldorange"><mifos:mifoslabel
												name="Office.labelOfficeInformation" /></td>
										</tr>
									</table>
									</td>
									<td width="73%" align="right">
									<table border="0" cellspacing="0" cellpadding="0" align="right">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17px" height="17px"></td>
											<td class="timelineboldorangelight"><mifos:mifoslabel
												name="Office.labelReviewAndSubmit" /></td>
										</tr>
									</table>
									</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0" class="bluetableborder">
						<tr>
							<td align="left" valign="top" class="paddingleftCreates">
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td class="headingorange"><span class="heading"><mifos:mifoslabel
										name="Office.labelAddNewOffice" /></span>
									<mifos:mifoslabel name="Office.labelEnterOfficeInformation"
										/></td>

								</tr>
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="Office.labelCompleteTheFields" />
									<br>
									<mifos:mifoslabel name="Office.labelFieldsMarkedWithAsterisk"
										mandatory="yes" /></td>
								</tr>
							</table>
							<br>
							<font class="fontnormalRedBold"><span id="CreateNewOffice.error.message"><html-el:errors
								bundle="OfficeUIResources" /></span> </font>
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="Office.labelOfficeDetails" /><br>
									<br>
									</td>
								</tr>
								<tr class="fontnormal">
									<td width="20%" align="right"><span id="CreateNewOffice.label.officeName"><mifos:mifoslabel
										name="Office.labelOfficeName" mandatory="yes"
										/></span></td>
									<td width="80%"><mifos:mifosalphanumtext styleId="CreateNewOffice.input.officeName" property="officeName"
										maxlength="200" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="CreateNewOffice.label.shortName"><mifos:mifoslabel
										name="Office.labelOfficeShortName" mandatory="yes"
										/></span></td>
									<td><mifos:mifosalphanumtext styleId="CreateNewOffice.input.shortName" property="shortName" size="4"
										maxlength="4"/></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="CreateNewOffice.label.officeLevel"><mifos:mifoslabel
										name="Office.labelOfficeType" mandatory="yes"
										/></span></td>
									<td><span id="CreateNewOffice.input.officeLevel"><mifos:select property="officeLevel"
										onchange="return papulateParent(this)" >
										<c:forEach var="levelList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'OfficeLevelList')}" >
											<html-el:option value="${levelList.levelId}">${levelList.levelName}</html-el:option>
										</c:forEach>
									</mifos:select></span></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="CreateNewOffice.label.parentOffice"><mifos:mifoslabel
										name="Office.labelParentOffice" mandatory="yes"
										/></span></td>
									<td><span id="CreateNewOffice.input.parentOffice"><mifos:select property="parentOfficeId">
										<c:if test="${not empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'Parents')}">
										<c:forEach var="parentList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'Parents')}" >
											<html-el:option value="${parentList.officeId}">${parentList.displayName}</html-el:option>
										</c:forEach>
										</c:if>

									</mifos:select></span></td>
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
									<td width="20%" align="right"><span id="CreateNewOffice.label.address1"><mifos:mifoslabel
										name="${ConfigurationConstants.ADDRESS1}"
										keyhm="Office.Address1" isColonRequired="yes"
										bundle="OfficeUIResources"/></span></td>
									<td width="80%"><mifos:mifosalphanumtext styleId="CreateNewOffice.input.address1" keyhm="Office.Address1"
										property="address.line1" maxlength="200" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="CreateNewOffice.label.address2"><mifos:mifoslabel
										name="${ConfigurationConstants.ADDRESS2}"
										bundle="OfficeUIResources" keyhm="Office.Address2"
										isColonRequired="yes"/></span></td>
									<td><mifos:mifosalphanumtext styleId="CreateNewOffice.input.address2" property="address.line2"  keyhm="Office.Address2"
										maxlength="200"/></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="CreateNewOffice.label.address3"><mifos:mifoslabel
										name="${ConfigurationConstants.ADDRESS3}"
										bundle="OfficeUIResources" 
										keyhm="Office.Address3"
										isColonRequired="yes"></mifos:mifoslabel></span></td>
									<td><mifos:mifosalphanumtext styleId="CreateNewOffice.input.address3" property="address.line3"
										maxlength="200" keyhm="Office.Address3"></mifos:mifosalphanumtext></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="CreateNewOffice.label.city"><mifos:mifoslabel
										name="${ConfigurationConstants.CITY}" bundle="OfficeResources"></mifos:mifoslabel></span>:</td>
									<td><mifos:mifosalphanumtext styleId="CreateNewOffice.input.city" property="address.city"
										maxlength="100"></mifos:mifosalphanumtext></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="CreateNewOffice.label.state"><mifos:mifoslabel
										name="${ConfigurationConstants.STATE}"
										bundle="OfficeResources" keyhm="Office.State"
										isColonRequired="yes"></mifos:mifoslabel></span></td>
									<td><mifos:mifosalphanumtext styleId="CreateNewOffice.input.state" property="address.state"
										maxlength="100" keyhm="Office.State"></mifos:mifosalphanumtext></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="CreateNewOffice.label.country"><mifos:mifoslabel name="Office.labelCountry"
										 keyhm="Office.Country"/></span></td>
									<td><mifos:mifosalphanumtext styleId="CreateNewOffice.input.country" property="address.country"
										maxlength="100" keyhm="Office.Country"></mifos:mifosalphanumtext></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="CreateNewOffice.label.postalCode"><mifos:mifoslabel
										name="${ConfigurationConstants.POSTAL_CODE}"
										bundle="OfficeResources" keyhm="Office.PostalCode"
										isColonRequired="yes"></mifos:mifoslabel></span></td>
									<td><mifos:mifosalphanumtext styleId="CreateNewOffice.input.postalCode" property="address.zip"
										maxlength="20" keyhm="Office.PostalCode"></mifos:mifosalphanumtext></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span id="CreateNewOffice.label.phoneNumber"><mifos:mifoslabel
										name="Office.labelTelephone" /></span>

									</td>

									<td><mifos:mifosalphanumtext styleId="CreateNewOffice.input.phoneNumber" property="address.phoneNumber"
										maxlength="20"></mifos:mifosalphanumtext></td>
								</tr>
							</table>
							<br>
							<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
									<tr>
										<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
											name="Office.labelAdditionInformation"
											/> <br>
										<br>
										</td>
									</tr>
								</c:if>
								<c:forEach var="cf" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}"
									varStatus="loopStatus">
									<bean:define id="ctr">
										<c:out value="${loopStatus.index}" />
									</bean:define>
									<tr class="fontnormal">
										<td width="21%" align="right"><span id="CreateNewOffice.label.customField"><mifos:mifoslabel
											name="${cf.lookUpEntity.entityType}"
											mandatory="${cf.mandatoryStringValue}"
											bundle="OfficeResources"></mifos:mifoslabel></span>:
										</td>
										<td width="79%"><c:if test="${cf.fieldType == 1}">
											<mifos:mifosnumbertext styleId="CreateNewOffice.input.customField" name="offActionForm"
												property='customField[${ctr}].fieldValue' maxlength="200" />
										</c:if> <c:if test="${cf.fieldType == 2}">
											<mifos:mifosalphanumtext styleId="CreateNewOffice.input.customField" name="offActionForm"
												property='customField[${ctr}].fieldValue' maxlength="200" />
										</c:if> <c:if test="${cf.fieldType == 3}">
											<mifos:mifosalphanumtext styleId="CreateNewOffice.input.customField" name="offActionForm"
												property='customField[${ctr}].fieldValue' maxlength="200" />

										</c:if></td>
									</tr>
								</c:forEach>

							</table>
						</c:if>
							<!--Custom Fields end  -->

							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center" class="blueline">&nbsp;</td>
								</tr>
							</table>

							<br>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<!-- Next are submit and cancel button -->
									<td align="center"><html-el:submit styleId="CreateNewOffice.button.preview" styleClass="buttn">
										<mifos:mifoslabel name="Office.preview"/>
									</html-el:submit> &nbsp; <html-el:button styleId="CreateNewOffice.button.cancel"
										onclick="goToCancelPage();" property="cancelButton"
										 styleClass="cancelbuttn">
										<mifos:mifoslabel name="Office.cancel"/>
									</html-el:button></td>
								</tr>
							</table>
							<br>
					</table>
				</tr>

				<br>

			</table>
			<!-- hidden veriable which will set input veriable -->
			<html-el:hidden property="input" value="create" />
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<br>
			<br>

		</html-el:form>
	</tiles:put>

</tiles:insert>
