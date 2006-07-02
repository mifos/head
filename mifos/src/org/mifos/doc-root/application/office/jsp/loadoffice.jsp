<!-- 

/**

 * loadoffice.jsp    version: 1.0

 

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
<tiles:insert definition=".create">
	<tiles:put name="body" type="string">

		<script language="javascript">
/*
 * This function is called when user press the cancel button 
 */
function goToCancelPage(){
	document.officeActionForm.method.value="cancel";
	officeActionForm.submit();
	
	
  }
/*
 * This function is called when user press the officeType in office Type
 * select box
 */
  
  function papulateParent(selectBox)
  {
  		if(selectBox.selectedIndex > 0)
  		{
		  document.officeActionForm.method.value="loadParent";
		  officeActionForm.submit();		
		}
		else
		{
		   alert("Please select some value");
		}
  }
</script>


		<%--<html-el:form action="/OfficeAction.do"
			onsubmit="return validateOfficeActionForm(this)">
			<html-el:javascript formName="/OfficeAction" bundle="OfficeResources" />
		--%>
		<html-el:form action="/OfficeAction.do" focus="officeName">
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
												name="office.labelOfficeInformation"
												bundle="OfficeResources"></mifos:mifoslabel></td>
										</tr>
									</table>
									</td>
									<td width="73%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17px" height="17px"></td>
											<td class="timelineboldorangelight"><mifos:mifoslabel
												name="office.labelReviewAndSubmit" bundle="OfficeResources"></mifos:mifoslabel></td>
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
										name="office.labelAddNewOffice" bundle="OfficeResources"></mifos:mifoslabel></span>
									<mifos:mifoslabel name="office.labelEnterOfficeInformation"
										bundle="OfficeResources"></mifos:mifoslabel></td>

								</tr>
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="office.labelCompleteTheFields" bundle="OfficeResources"></mifos:mifoslabel>
										<br>
									<mifos:mifoslabel name="office.labelFieldsMarkedWithAsterisk"
										mandatory="yes" bundle="OfficeResources"></mifos:mifoslabel></td>
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
										maxlength="200"></mifos:mifosalphanumtext></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="office.labelOfficeShortName" mandatory="yes"
										bundle="OfficeResources"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext property="shortName" size="4"
										maxlength="4"></mifos:mifosalphanumtext></td>
								</tr>

								<%-- office code removed from the 
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="office.labelOfficeCode" bundle="OfficeResources"></mifos:mifoslabel></td>
								
									<td>
									
									
									 <mifos:select property="formOfficeCode" size="1">
										<html-el:options collection="OfficeCodeList" property="codeId"
											labelProperty="lookUpName" />
									</mifos:select></td>
								</tr>
								
								 --%>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="office.labelOfficeType" mandatory="yes"
										bundle="OfficeResources"></mifos:mifoslabel></td>
									<td><mifos:select property="formOfficeType" size="1"
										onchange="papulateParent(this)">
										<html-el:options collection="OfficeLevelList"
											property="levelId" labelProperty="levelName" />
									</mifos:select></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="office.labelParentOffice" mandatory="yes"
										bundle="OfficeResources"></mifos:mifoslabel></td>
									<td><mifos:select name="officeActionForm"
										property="formParentOffice" size="1">
										<c:if test="${not empty Parents}">
											<html-el:options collection="Parents" property="officeId"
												labelProperty="displayName" />
										</c:if>

									</mifos:select></td>
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
										property="address.address1" maxlength="200"></mifos:mifosalphanumtext></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="${ConfigurationConstants.ADDRESS2}"
										bundle="OfficeResources"></mifos:mifoslabel>:</td>
									<td><mifos:mifosalphanumtext property="address.address2"
										maxlength="200"></mifos:mifosalphanumtext></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="${ConfigurationConstants.ADDRESS3}"
										bundle="OfficeResources" keyhm="Office.Address3" isColonRequired="yes"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext property="address.address3"
										maxlength="200" keyhm="Office.Address3"></mifos:mifosalphanumtext></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="${ConfigurationConstants.CITY}"
										 bundle="OfficeResources"></mifos:mifoslabel>:</td>
									<td><mifos:mifosalphanumtext property="address.city"
										maxlength="100"></mifos:mifosalphanumtext></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="${ConfigurationConstants.STATE}"
										 bundle="OfficeResources" keyhm="Office.State" isColonRequired="yes"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext property="address.state" 
										maxlength="100" keyhm="Office.State"></mifos:mifosalphanumtext></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="office.labelCountry"
										 bundle="OfficeResources" keyhm="Office.Country"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext property="address.country" 
										maxlength="100" keyhm="Office.Country"></mifos:mifosalphanumtext></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="${ConfigurationConstants.POSTAL_CODE}"
										bundle="OfficeResources" keyhm="Office.PostalCode" isColonRequired="yes"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext property="address.postalCode"
										maxlength="20" keyhm="Office.PostalCode"></mifos:mifosalphanumtext></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="office.labelTelephone" bundle="OfficeResources"></mifos:mifoslabel>

									</td>

									<td><mifos:mifosalphanumtext property="address.telephoneNo"
										maxlength="20"></mifos:mifosalphanumtext></td>
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
								<c:forEach var="cf" items="${requestScope.customFields}"
									varStatus="loopStatus">
									<bean:define id="ctr">
										<c:out value="${loopStatus.index}" />
									</bean:define>
									<tr class="fontnormal">
										<td width="21%" align="right">
			<mifos:mifoslabel name="${cf.lookUpEntity.entityType}" mandatory="${cf.mandatoryStringValue}" bundle="OfficeResources"></mifos:mifoslabel>:
						<!-- 	<mifos:mifoslabel		name="cf.lookUpEntity.entityId" bundle="OfficeResources"></mifos:mifoslabel>-->
					</td>
										<td width="79%"><c:if test="${cf.fieldType == 1}">
											<mifos:mifosnumbertext name="officeActionForm"
												property='customField[${ctr}].fieldValue' value="" maxlength="200"/>
										</c:if> <c:if test="${cf.fieldType == 2}">
											<mifos:mifosalphanumtext name="officeActionForm"
												property='customField[${ctr}].fieldValue' value="" maxlength="200"/>
										</c:if> <c:if test="${cf.fieldType == 3}">
											<mifos:mifosalphanumtext name="officeActionForm"
												property='customField[${ctr}].fieldValue' value="" maxlength="200"/>

										</c:if> <html-el:hidden property='customField[${ctr}].fieldId'
											value="${cf.fieldId}"></html-el:hidden></td>
									</tr>
								</c:forEach>

							</table>
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
							
					</table>
				</tr>

				<br>

			</table>
			<br>
			<br>
			<!-- hidden veriable which will be set to method -->
			<html-el:hidden property="method" value="preview" />
			<!-- hidden veriable which will set input veriable -->
			<html-el:hidden property="input" value="create" />
			<!-- hidden veriable which will set input veriable -->
			<html-el:hidden property="formOfficeStatus" value="1" />


		</html-el:form>
	</tiles:put>

</tiles:insert>
