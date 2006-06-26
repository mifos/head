<!-- 

/**

 * previewoffice.jsp    version: 1.0

 

 * Copyright © 2005-2006 Grameen Foundation USA

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
		<!-- Next is code for setting the hidden veriable to cancel -->
		<script language="javascript" type="text/javascript">
function goToCancelPage(){
	document.officeActionForm.method.value="cancel";
	officeActionForm.submit();
  }
  function goToPreviousPage()
  {
	document.officeActionForm.method.value="previous";
	officeActionForm.submit();
  
  }
  function goToCreate()
  {
  	document.officeActionForm.method.value="create";
	officeActionForm.submit();
 
  }
</script>
		<html-el:form action="/OfficeAction.do" method="POST">
			<br>
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
											<td><img src="pages/framework/images/timeline/tick.gif"
												width="17" height="17" alt=""></td>
											<td class="timelineboldgray"><mifos:mifoslabel
												name="office.labelOfficeInformation"
												bundle="OfficeResources"></mifos:mifoslabel></td>
										</tr>
									</table>
									</td>
									<td width="73%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/bigarrow.gif"
												width="17" height="17" alt=""></td>
											<td class="timelineboldorange"><mifos:mifoslabel
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
						cellspacing="0" valign="top" class="bluetableborder">
						<tr>
							<td align="left" valign="top" class="paddingleftCreates">
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td class="headingorange"><span class="heading"><mifos:mifoslabel
										name="office.labelAddNewOffice" bundle="OfficeResources"></mifos:mifoslabel></span><mifos:mifoslabel
										name="office.labelReviewAndSubmit" bundle="OfficeResources"></mifos:mifoslabel></td>
								</tr>
								<!-- 	<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="office.labelCompleteTheFields" bundle="OfficeResources"></mifos:mifoslabel>

									<mifos:mifoslabel name="office.labelFieldsMarkedWithAsterisk"
										mandatory="yes" bundle="OfficeResources"></mifos:mifoslabel></td>
								</tr>
								-->
							</table>
							<br>

							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr class="fontnormalRedBold">
									<td><font class="fontnormalRedBold"> <html-el:errors
										bundle="OfficeResources" /> </font></td>
								</tr>
								<tr>
									<td width="100%" height="23" class="fontnormalboldorange"><mifos:mifoslabel
										name="office.labelOfficeInformation" bundle="OfficeResources"></mifos:mifoslabel></td>
								</tr>
								<tr>

									<!-- actual information starts from here -->
									<td height="23" class="fontnormalbold"><mifos:mifoslabel
										name="office.labelOfficeName" bundle="OfficeResources">
									</mifos:mifoslabel> <span class="fontnormal"> <c:out
										value="${requestScope.OfficeVo.officeName}"></c:out> <br>
									</span> <mifos:mifoslabel name="office.labelOfficeShortName"
										bundle="OfficeResources"></mifos:mifoslabel><span
										class="fontnormal"> <c:out
										value="${requestScope.OfficeVo.shortName}"></c:out><br>
									</span> <%-- office code is not required any more 		
									<mifos:mifoslabel name="office.labelOfficeCode"
										bundle="OfficeResources"></mifos:mifoslabel><span
										class="fontnormal"> <!--  code for setting the correct value for code-->
							
									<c:forEach var="code" items="${OfficeCodeList}">
										<c:if test="${code.codeId == OfficeVo.officeCode.codeId }">
											<c:out value="${code.lookUpName}"></c:out>
										</c:if>
									</c:forEach> <!--  END code for setting the correct value for code-->


									</span>
									
							--%> <mifos:mifoslabel name="office.labelOfficeType"
										bundle="OfficeResources"></mifos:mifoslabel> <span
										class="fontnormal"> <!--  code for setting the correct value for type-->

									<c:forEach var="level" items="${OfficeLevelList}">
										<c:if test="${level.levelId == OfficeVo.level.levelId }">
											<c:out value="${level.levelName}"></c:out>
										</c:if>
									</c:forEach> <!--  END code for setting the correct value for type-->

									</span> <br>




									<span class="fontnormal"> </span><mifos:mifoslabel
										name="office.labelParentOffice" bundle="OfficeResources"></mifos:mifoslabel><span
										class="fontnormal"> <!-- logic for showing the correct parent -->
									<c:forEach var="parent" items="${Parents}">
										<c:if
											test="${parent.officeId == OfficeVo.parentOffice.officeId }">
											<c:out value="${parent.displayName}"></c:out>
										</c:if>
									</c:forEach> <!-- End for showing the correct parent --> </span><br>
									<span class="fontnormal"></span><span class="fontnormal"> </span><span
										class="fontnormal"></span><br>
									<mifos:mifoslabel name="office.labelAddress"
										bundle="OfficeResources"></mifos:mifoslabel><span
										class="fontnormal"><br>
									</span> <span
										class="fontnormal"><c:out
										value="${requestScope.OfficeVo.address.address1}"></c:out><c:if
										test="${not empty requestScope.OfficeVo.address.address1 &&(not empty requestScope.OfficeVo.address.address2||not empty requestScope.OfficeVo.address.address3)}">, </c:if><c:if
										test="${not empty requestScope.OfficeVo.address.address2}">${requestScope.OfficeVo.address.address2}</c:if><c:if
										test="${not empty requestScope.OfficeVo.address.address3&&not empty requestScope.OfficeVo.address.address2}">, </c:if><c:if
										test="${not empty requestScope.OfficeVo.address.address3}">${requestScope.OfficeVo.address.address3}</c:if>
									</span>
								</tr>
								<tr>
									<td class="fontnormalbold"><br><mifos:mifoslabel name="${ConfigurationConstants.CITY}"
										bundle="OfficeResources"></mifos:mifoslabel>:<span
										class="fontnormal"> <c:out
										value="${requestScope.OfficeVo.address.city}"></c:out><br>
									</span>
								</tr>
								<tr id="Office.State">
									<td class="fontnormalbold"><mifos:mifoslabel name="${ConfigurationConstants.STATE}"
										bundle="OfficeResources" keyhm="Office.State" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel><span
										class="fontnormal"> <c:out
										value="${requestScope.OfficeVo.address.state}"></c:out><br>
									</span>
								</tr>
								<tr id="Office.Country">
									<td class="fontnormalbold"><mifos:mifoslabel name="office.labelCountry"
										bundle="OfficeResources" keyhm="Office.Country" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel><span
										class="fontnormal"> <c:out
										value="${requestScope.OfficeVo.address.country}"></c:out><br>
									</span>
								</tr>
								<tr id="Office.PostalCode">
									<td class="fontnormalbold"><mifos:mifoslabel name="${ConfigurationConstants.POSTAL_CODE}"
										bundle="OfficeResources" keyhm="Office.PostalCode" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel><span
										class="fontnormal"> <c:out
										value="${requestScope.OfficeVo.address.postalCode}"></c:out>
									</span>
								</tr>
								<tr>
									<td class="fontnormalbold"><br><mifos:mifoslabel name="office.labelTelephone"
										bundle="OfficeResources"></mifos:mifoslabel><span
										class="fontnormal"> <c:out
										value="${requestScope.OfficeVo.address.telephoneNo}"></c:out></span><br>
									<br>
								     <c:if test="${!empty requestScope.customFields}">
									<mifos:mifoslabel name="office.labelAdditionInformation"
										bundle="OfficeResources"></mifos:mifoslabel>
									</c:if>
										<span
										class="fontnormal"><br>
									<!-- custom field preview --> <c:forEach var="cfdef"
										items="${requestScope.customFields}">
										<c:forEach var="cf"
											items="${requestScope.OfficeVo.customFieldSet}">
											<c:if test="${cfdef.fieldId==cf.fieldId}">
												<font class="fontnormalBold">
												<mifos:mifoslabel name="${cfdef.lookUpEntity.entityType}" bundle="OfficeResources"></mifos:mifoslabel>:
											<!-- 	<mifos:mifoslabel
													name="cfdef.entityId" bundle="OfficeResources"></mifos:mifoslabel>-->
												</font>
												<span class="fontnormal"><c:out value="${cf.fieldValue}" /><br>
												</span>
											</c:if>
										</c:forEach>
									</c:forEach> <span class="fontnormal"></span> <br>
									<br>
									<span class="fontnormal"></span> <html-el:button
										onclick="goToPreviousPage();" property="cancelButton"
										styleClass="insidebuttn" style="width:150px">
										<mifos:mifoslabel name="office.button.edit"
											bundle="OfficeResources"></mifos:mifoslabel>
									</html-el:button></span></td>
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
									<td align="center">&nbsp; <!-- Next are submit and cancel button -->

									<html-el:submit styleClass="buttn" style="width:70px;"></html-el:submit>
									&nbsp; <html-el:button onclick="goToCancelPage();"
										property="cancelButton" styleClass="cancelbuttn"
										style="width:70px">
										<mifos:mifoslabel name="office.button.cancel"
											bundle="OfficeResources"></mifos:mifoslabel>
									</html-el:button></td>
								</tr>

							</table>

							</td>


						</tr>
						<tr>
							<td>&nbsp;</td>
						</tr>

					</table>

					</td>
				</tr>
			</table>


			<!-- hidden veriable which will be set to method -->
			<html-el:hidden property="method" value="create" />
			<!-- hidden veriable which will set input veriable -->
			<html-el:hidden property="input" value="create" />
			<html-el:hidden property="formOfficeStatus" value="1" />
			<html-el:hidden property="officeId" value="" />
		</html-el:form>
	</tiles:put>

</tiles:insert>
