<!-- 

/**

 * vieweditoffice.jsp    version: 1.0

 

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

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<script language="javascript">
function goToCancelPage(){
	document.officeActionForm.method.value="get";
	officeActionForm.submit();
  }
 function submitViewOfficesLink(){
	document.officeActionForm.method.value="loadall";
		
		
	officeActionForm.submit();
  } 
  function editOfficeInformationLink(){
	document.officeActionForm.method.value="manage";
	document.officeActionForm.input.value="manage";	
		
	officeActionForm.submit();
  }  
  function  submitAdminLink()
{
		document.officeActionForm.method.value="load";
		document.officeActionForm.action="AdminAction.do";
		officeActionForm.submit();
}
</script>

		<html-el:form action="/OfficeAction.do" method="POST">

			
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td class="bluetablehead05"><span class="fontnormal8pt"> <!-- todo This we need to remove later by office action  -->
							<html-el:link href="javascript:submitAdminLink()"><mifos:mifoslabel
								name="office.labelLinkAdmin" bundle="OfficeResources"></mifos:mifoslabel></html-el:link> / <html-el:link
										href="javascript:submitViewOfficesLink()" > <mifos:mifoslabel
								name="office.labelLinkViewOffices" bundle="OfficeResources"></mifos:mifoslabel>

							</html-el:link> / </span><span class="fontnormal8ptbold"><c:out
								value="${requestScope.OfficeVo.officeName}"></c:out></span></td>
						</tr>
					</table>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="100%" align="left" valign="top" class="paddingL15T15">
							<table width="96%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td width="50%" height="23" class="headingorange"><c:out
										value="${requestScope.OfficeVo.officeName}"></c:out></td>
									<td width="50%" align="right">
									<html-el:link href="javascript:editOfficeInformationLink()"
										>
										<mifos:mifoslabel name="office.labelEditOfficeInfo"
											bundle="OfficeResources"></mifos:mifoslabel>
									</html-el:link>
									</td>
								</tr>
													 <tr><td colspan="2"><font class="fontnormalRedBold"><html-el:errors
								bundle="OfficeResources" /> </font>
								</td></tr>
								<tr>
									<td height="23" class="fontnormalbold"><span class="fontnormal">
									</span>
									<span class="fontnormal"> 
									<c:if test="${OfficeVo.status.statusId == 1}">
									<mifos:MifosImage id="active" moduleName="office" />
									</c:if>
									<c:if test="${OfficeVo.status.statusId == 2}">
									<mifos:MifosImage id="inactive" moduleName="office" />
									</c:if>
										<c:forEach var="status"
											items="${requestScope.OfficeStatusList}">
											<c:if test="${status.statusId==OfficeVo.status.statusId}">
												${status.statusName}
											</c:if>
										</c:forEach>

									
									</span> 
									
									<br>
									<span class="fontnormal"><mifos:mifoslabel
										name="office.labelOfficeShortName" bundle="OfficeResources"></mifos:mifoslabel>
									<c:out value="${requestScope.OfficeVo.shortName}"></c:out><br>
									 <!-- End Logic for showing the correct code--> 
									<mifos:mifoslabel name="office.labelOfficeType"
										bundle="OfficeResources"></mifos:mifoslabel> <!--  code for setting the correct value for type-->
									<c:forEach var="level" items="${OfficeLevelList}">
										<c:if test="${level.levelId == OfficeVo.level.levelId }">
											<c:out value="${level.levelName}"></c:out>
										</c:if>
									</c:forEach> <!--  END code for setting the correct value for type--><br>
									<!-- logic for showing the correct parent -->
									<mifos:mifoslabel name="office.labelParentOffice"
										bundle="OfficeResources"></mifos:mifoslabel> 
									<c:forEach var="parent" items="${Parents}">
										<c:if
											test="${parent.officeId == OfficeVo.parentOffice.officeId }">
											<c:out value="${parent.displayName}"></c:out>
										</c:if>
									</c:forEach>
									 </span><br>
									 <!-- End for showing the correct parent -->
									<span class="fontnormal"></span><span class="fontnormal"> </span><span
										class="fontnormal"></span><br>
										
									<c:if test="${not empty requestScope.OfficeVo.address.telephoneNo ||
									 not empty requestScope.OfficeVo.address.address1 ||
									 not empty requestScope.OfficeVo.address.address2 ||
									 not empty requestScope.OfficeVo.address.address3 ||
									 not empty requestScope.OfficeVo.address.city	 ||
									 not empty requestScope.OfficeVo.address.state	 ||
									 not empty requestScope.OfficeVo.address.country	 ||
									 not empty requestScope.OfficeVo.address.postalCode }">	
									<mifos:mifoslabel name="office.labelAddress"
										bundle="OfficeResources"></mifos:mifoslabel>
									
									<c:if 	test="${not empty requestScope.OfficeVo.address.address1 ||
									 not empty requestScope.OfficeVo.address.address2 ||
									 not empty requestScope.OfficeVo.address.address3 }">
									 <br>
									<span
										class="fontnormal"><c:out
										value="${requestScope.OfficeVo.address.address1}"></c:out><c:if
										test="${not empty requestScope.OfficeVo.address.address1 &&(not empty requestScope.OfficeVo.address.address2||not empty requestScope.OfficeVo.address.address3)}">, </c:if><c:if
										test="${not empty requestScope.OfficeVo.address.address2}">${requestScope.OfficeVo.address.address2}</c:if><c:if
										test="${not empty requestScope.OfficeVo.address.address3&&not empty requestScope.OfficeVo.address.address2}">, </c:if><c:if
										test="${not empty requestScope.OfficeVo.address.address3}">${requestScope.OfficeVo.address.address3}</c:if>
									</span>
									</c:if>
									
									<c:if test="${not empty requestScope.OfficeVo.address.city}">
									<br>
									<span class="fontnormal"><c:out
										value="${requestScope.OfficeVo.address.city}"></c:out>
									</span> 
									</c:if>
									<c:if test="${not empty requestScope.OfficeVo.address.state}">
									<br>
									<span class="fontnormal"><c:out
										value="${requestScope.OfficeVo.address.state}"></c:out>
									</span></c:if>
									
									<c:if test="${not empty requestScope.OfficeVo.address.country}">
									<br>
									<span class="fontnormal"><c:out
										value="${requestScope.OfficeVo.address.country}"></c:out> 
										</span>
									</c:if>
									<c:if test="${not empty requestScope.OfficeVo.address.postalCode}">
									<br>
									<span class="fontnormal"> <c:out
										value="${requestScope.OfficeVo.address.postalCode}"></c:out>
									</span>
									</c:if>
									<!-- bug 26503  -->
									<c:if test="${ not empty requestScope.OfficeVo.address.telephoneNo}">
									<br>
									<span
										class="fontnormal">
									<mifos:mifoslabel name="office.labelTelephone"
										bundle="OfficeResources"></mifos:mifoslabel></span> <span
										class="fontnormal"><c:out
										value="${requestScope.OfficeVo.address.telephoneNo}"></c:out>
									</span>
									</c:if>
									</c:if>
									
									<br>
							    <c:set var="diplaylabel" value="false" scope="page" />
									
									<c:if test="${ not empty requestScope.customFields}">
									
										<c:forEach var="cff"
											items="${requestScope.OfficeVo.customFieldSet}">
											
											  <c:if	test="${not empty cff.fieldValue}" >
											    <c:set var="diplaylabel" value="true" scope="page" />

											  </c:if>
										 </c:forEach>
									
									
									<c:if test='${diplaylabel == "true"}' >
									<br>
									<mifos:mifoslabel name="office.labelAdditionInformation"
										bundle="OfficeResources"></mifos:mifoslabel><span
										class="fontnormal"><br>
									<!-- custom field preview --> <c:forEach var="cfdef"
										items="${requestScope.customFields}">
										<c:forEach var="cf"
											items="${requestScope.OfficeVo.customFieldSet}">
											<c:if test="${cfdef.fieldId==cf.fieldId}">
											<mifos:mifoslabel name="${cfdef.lookUpEntity.entityType}" bundle="OfficeResources"></mifos:mifoslabel>:
											<!-- 	<mifos:mifoslabel name="cfdef.entityId" bundle="OfficeResources"></mifos:mifoslabel>-->
												<span class="fontnormal"><c:out value="${cf.fieldValue}" /><br>
												</span>
											</c:if>
										</c:forEach>
									</c:forEach> 
									
									
									
									</c:if>
									
									</c:if>
									
									
									
									
									
									</td>
								</tr>
							</table>
							<br>
							</td>
						</tr>
					</table>
					<br>
					
			<html-el:hidden property="method" value="manage" />
			<html-el:hidden property="input" value="manage" />
			
			 <html-el:hidden	property="formOfficeStatus" value="${requestScope.OfficeVo.status.statusId}" />  
						
			<html-el:hidden
						property="officeId"	value="${requestScope.OfficeVo.officeId}" />
			<html-el:hidden property="address.officeAdressId"
				value="${requestScope.OfficeVo.address.officeAdressId}" />
			<html-el:hidden property="versionNo" value="${requestScope.OfficeVo.versionNo}" />
			<html-el:hidden property="formOfficeStatus" value="${requestScope.OfficeVo.status.statusId}" />
			<html-el:hidden property="formOfficeType" value="${requestScope.OfficeVo.level.levelId}" />
						
			
		</html-el:form>

	</tiles:put>

</tiles:insert>
