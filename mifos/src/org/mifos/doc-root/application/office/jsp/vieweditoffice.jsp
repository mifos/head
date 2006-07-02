<!-- 

/**

 * vieweditoffice.jsp    version: 1.0

 

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


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<script language="javascript">
function goToCancelPage(){
	document.officeActionForm.method.value="get";
	officeActionForm.submit();

	}
  function papulateParent()
  {
		
		document.officeActionForm.method.value="loadParent";
		
		officeActionForm.submit();		
  }	
   function submitViewOfficesLink(){
	document.officeActionForm.method.value="loadall";
		
		
	officeActionForm.submit();
  }
function getOffice(officeid){
	document.officeActionForm.method.value="get";
	document.officeActionForm.officeId.value=officeid;	
	officeActionForm.submit();
  }  
   function  submitAdminLink()
{
		document.officeActionForm.method.value="load";
		document.officeActionForm.action="AdminAction.do";
		officeActionForm.submit();
}
</script>

	<%--	<html-el:form action="/OfficeAction.do" method="post" onsubmit="return validateOfficeActionForm(this)">
<html-el:javascript formName="/OfficeAction" bundle="OfficeResources" />
	--%>
		<html-el:form action="/OfficeAction.do">	
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td class="bluetablehead05"><span class="fontnormal8pt"><html-el:link href="javascript:submitAdminLink()"><mifos:mifoslabel
								name="office.labelLinkAdmin" bundle="OfficeResources"></mifos:mifoslabel></html-el:link>
							/ 
							
							
							<html-el:link
										href="javascript:submitViewOfficesLink()" > <mifos:mifoslabel
								name="office.labelLinkViewOffices" bundle="OfficeResources"></mifos:mifoslabel>

							</html-el:link>
							
								
							/ 
							
							<html-el:link href="javascript:getOffice(${requestScope.OfficeVo.officeId})"> 
													<c:out
								value="${requestScope.OfficeVo.officeName}"></c:out></html-el:link>
							
							
								
								
								</span></td>
						</tr>
					</table>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="70%" align="left" valign="top" class="paddingL15T15">
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td class="headingorange"><span class="heading"><c:out
										value="${requestScope.OfficeVo.officeName}"></c:out> - </span><mifos:mifoslabel
										name="office.labelEditOfficeInfo" bundle="OfficeResources"></mifos:mifoslabel></td>
								</tr>
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="office.labelMandatoryInstructions"
										bundle="OfficeResources"></mifos:mifoslabel> <mifos:mifoslabel
										name="office.labelFieldsMarkedWithAsterisk" mandatory="yes"
										bundle="OfficeResources"></mifos:mifoslabel></td>
								</tr>
							</table>
							<br>
														<font class="fontnormalRedBold"><html-el:errors
							bundle="OfficeResources" /> </font>
							
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="office.labelOfficeDetails" bundle="OfficeResources"></mifos:mifoslabel><br>
									<br>
									</td>
								</tr>
								<tr class="fontnormal">
									<td width="20%" align="right"><mifos:mifoslabel
										name="office.labelOfficeName" mandatory="yes"
										bundle="OfficeResources">
									</mifos:mifoslabel></td>
									<td width="80%"><mifos:mifosalphanumtext property="officeName"
										maxlength="200" value="${requestScope.OfficeVo.officeName}"></mifos:mifosalphanumtext></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="office.labelOfficeShortName" mandatory="yes"
										bundle="OfficeResources"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext property="shortName" size="4"
										maxlength="4" value="${requestScope.OfficeVo.shortName}"></mifos:mifosalphanumtext></td>
								</tr>
								
								
								<%-- office code not required any more 
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="office.labelOfficeCode" bundle="OfficeResources"></mifos:mifoslabel></td>
									<td><!--  code for setting the correct value for code--> <c:forEach
										var="code" items="${OfficeCodeList}">
										<c:if test="${code.codeId == OfficeVo.officeCode.codeId }">
											<c:out value="${code.lookUpName}"></c:out>

											<html-el:hidden property="formOfficeCode"
												value="${OfficeVo.officeCode.codeId}" />
										</c:if>
									</c:forEach> <!--  END code for setting the correct value for code-->
									</td>
								</tr>
								
								--%>
								
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="office.labelOfficeType" mandatory="yes"
										bundle="OfficeResources"></mifos:mifoslabel></td>
									<td><!--  code for setting the correct value for type-->
									
									
									 <mifos:select
										name="officeActionForm" property="formOfficeType" size="1"
										onchange="papulateParent()">
										<html-el:options collection="OfficeLevelList"
											property="levelId" labelProperty="levelName" />
									</mifos:select> 
									
									
									
									
									
									<!--  END code for setting the correct value for type-->
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="office.labelParentOffice" mandatory="yes"
										bundle="OfficeResources"></mifos:mifoslabel></td>
									<td>
									
									
									
									<mifos:select name="officeActionForm"
										property="formParentOffice" size="1"
										value="${requestScope.OfficeVo.parentOffice.officeId}">
										
										<c:if test="${not empty Parents}">
											<html-el:options collection="Parents" property="officeId"
												labelProperty="displayName" />

										</c:if>
									</mifos:select>
									
									
									
									</td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="office.labelStatus" bundle="OfficeResources"></mifos:mifoslabel><br>
									<br>
									</td>
								</tr>
								<tr class="fontnormal">
									<td width="20%" align="right"><mifos:mifoslabel
										name="office.labelChangeStatus" bundle="OfficeResources"></mifos:mifoslabel></td>
									<td width="80%">
									
									
									
									<c:choose>
        								<c:when test='${OfficeVo.level.levelId == 1}'>
        							 <html-el:select name="officeActionForm" disabled="true"
										property="formOfficeStatus" size="1"
										value="${requestScope.OfficeVo.status.statusId}">
										<html-el:options collection="OfficeStatusList"
											property="statusId" labelProperty="statusName" />
									</html-el:select>
						
        								</c:when>
        								<c:otherwise>
        							 <html-el:select name="officeActionForm" 
										property="formOfficeStatus" size="1"
										value="${requestScope.OfficeVo.status.statusId}">
										<html-el:options collection="OfficeStatusList"
											property="statusId" labelProperty="statusName" />
									</html-el:select>

       									 </c:otherwise>
    								</c:choose>
									
									
									
									
									
									</td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="office.labelOfficeAddress" bundle="OfficeResources"></mifos:mifoslabel><br>
									<br>
									</td>
								</tr>
								<tr class="fontnormal">
									<td width="20%" align="right"><mifos:mifoslabel
										name="${ConfigurationConstants.ADDRESS1}" 
										bundle="OfficeResources"></mifos:mifoslabel>:</td>
									<td width="80%"><mifos:mifosalphanumtext
										property="address.address1"
										value="${requestScope.OfficeVo.address.address1}" maxlength="200"></mifos:mifosalphanumtext></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="${ConfigurationConstants.ADDRESS2}"
										bundle="OfficeResources"></mifos:mifoslabel>:</td>
									<td><mifos:mifosalphanumtext property="address.address2"
										value="${requestScope.OfficeVo.address.address2}" maxlength="200"></mifos:mifosalphanumtext>

									</td>
								</tr>
								<tr class="fontnormal" id="Office.Address3">
									<td align="right"><mifos:mifoslabel name="${ConfigurationConstants.ADDRESS3}"
										bundle="OfficeResources" keyhm="Office.Address3" isColonRequired="yes"></mifos:mifoslabel>:</td>
									<td><mifos:mifosalphanumtext property="address.address3"
										value="${requestScope.OfficeVo.address.address3}" maxlength="200" keyhm="Office.Address3"> </mifos:mifosalphanumtext>
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="${ConfigurationConstants.CITY}"
										bundle="OfficeResources"></mifos:mifoslabel>:</td>
									<td><mifos:mifosalphanumtext property="address.city"
										value="${requestScope.OfficeVo.address.city}" maxlength="100"></mifos:mifosalphanumtext>
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="${ConfigurationConstants.STATE}"
										bundle="OfficeResources" keyhm="Office.State" isColonRequired="yes"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext property="address.state"
										value="${requestScope.OfficeVo.address.state}" maxlength="100" keyhm="Office.State"></mifos:mifosalphanumtext>
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="office.labelCountry"
										bundle="OfficeResources" keyhm="Office.Country"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext property="address.country"
										value="${requestScope.OfficeVo.address.country}" maxlength="100" keyhm="Office.Country"></mifos:mifosalphanumtext>
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="${ConfigurationConstants.POSTAL_CODE}" 
										bundle="OfficeResources" keyhm="Office.PostalCode" isColonRequired="yes"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext property="address.postalCode"
										value="${requestScope.OfficeVo.address.postalCode}" maxlength="20" keyhm="Office.PostalCode"></mifos:mifosalphanumtext>
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="office.labelTelephone" bundle="OfficeResources"></mifos:mifoslabel>

									</td>

									<td><mifos:mifosalphanumtext property="address.telephoneNo"
										value="${requestScope.OfficeVo.address.telephoneNo}" maxlength="20"></mifos:mifosalphanumtext>
									</td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
					       <c:if test="${!empty requestScope.customFields}">
								<tr>
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="office.labelAdditionInformation"
										bundle="OfficeResources"></mifos:mifoslabel> <br>
									<br>
									</td>
								</tr>
							</c:if>
								<!-- For each custom field definition in the list custom field entity is passed as key to mifos label -->




								<c:forEach var="customFieldDef"
									items="${requestScope.customFields}" varStatus="loopStatus">
									<bean:define id="ctr">
										<c:out value="${loopStatus.index}" />
									</bean:define>
									<c:forEach var="cf"
										items="${requestScope.OfficeVo.customFieldSet}">
										<c:if test="${customFieldDef.fieldId==cf.fieldId}">
											<tr class="fontnormal">
												<td width="21%" align="right">
			<mifos:mifoslabel name="${customFieldDef.lookUpEntity.entityType}" mandatory="${customFieldDef.mandatoryStringValue}" bundle="OfficeResources"></mifos:mifoslabel>:												
						<!-- <mifos:mifoslabel
													name="cf.lookUpEntity.entityId" bundle="OfficeResources"></mifos:mifoslabel>-->
													</td>
												<td width="79%"><c:if
													test="${customFieldDef.fieldType == 1}">
													<mifos:mifosnumbertext name="officeActionForm"
														property='customField[${ctr}].fieldValue'
														value="${cf.fieldValue}" maxlength="200"/>
												</c:if> <c:if test="${customFieldDef.fieldType == 2}">
													<mifos:mifosalphanumtext name="officeActionForm"
														property='customField[${ctr}].fieldValue'
														value="${cf.fieldValue}" maxlength="200"/>
												</c:if> <c:if test="${customFieldDef.fieldType == 3}">
													<mifos:mifosalphanumtext name="officeActionForm"
														property='customField[${ctr}].fieldValue'
														value="${cf.fieldValue}" maxlength="200"/>

												</c:if> <html-el:hidden
													property='customField[${ctr}].fieldId'
													value="${cf.fieldId}"></html-el:hidden></td>
											</tr>
										</c:if>
									</c:forEach>
								</c:forEach>

							</table>

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
							<td align="center"><html-el:submit styleClass="buttn"
								style="width:70px;">
								<mifos:mifoslabel name="office.button.preview"
									bundle="OfficeResources"></mifos:mifoslabel>
							</html-el:submit> &nbsp; <html-el:button
								onclick="goToCancelPage();" property="cancelButton"
								value="Cancel" styleClass="cancelbuttn" style="width:70px">
								<mifos:mifoslabel name="office.button.cancel"
									bundle="OfficeResources"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
					</table>
					<br>
					



			<br>

			<html-el:hidden property="method" value="preview" />
			<html-el:hidden property="input" value="manageedit" />
			<html-el:hidden property="officeId"
				value="${requestScope.OfficeVo.officeId}" />
			<html-el:hidden property="address.officeAdressId"
				value="${requestScope.OfficeVo.address.officeAdressId}" />
			<html-el:hidden property="versionNo" value="${requestScope.OfficeVo.versionNo}" />
			
		</html-el:form>

	</tiles:put>

</tiles:insert>

