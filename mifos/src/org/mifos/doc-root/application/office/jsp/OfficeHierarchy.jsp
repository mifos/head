<!-- 

/**

 * OfficeHierarchy.jsp    version: 1.0

 

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
	document.officeHierarchyActionForm.method.value="cancel";
	officeHierarchyActionForm.submit();
  }
   function  submitAdminLink()
{
		document.officeHierarchyActionForm.method.value="load";
		document.officeHierarchyActionForm.action="AdminAction.do";
		officeHierarchyActionForm.submit();
}
</script>

		<html-el:form action="/OfficeHierarchyAction.do" method="POST">

			
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td class="bluetablehead05"><span class="fontnormal8pt"><html-el:link href="javascript:submitAdminLink()"><mifos:mifoslabel
								name="office.labelLinkAdmin" bundle="OfficeResources"></mifos:mifoslabel></html-el:link>
							/ </span><span class="fontnormal8ptbold"><mifos:mifoslabel
								name="office.labelViewOfficeHierarchy" bundle="OfficeResources"></mifos:mifoslabel></span></td>
						</tr>
					</table>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="70%" align="left" valign="top" class="paddingL15T15">
							<table width="98%" border="0" cellspacing="0" cellpadding="3">
								<tr>
									<td width="35%" class="headingorange"><mifos:mifoslabel
										name="office.labelViewOfficeHierarchy"
										bundle="OfficeResources"></mifos:mifoslabel></td>
								</tr>
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="office.labelMaxMinLevel" bundle="OfficeResources"></mifos:mifoslabel>

									</td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="2" class="fontnormal"><mifos:mifoslabel
										name="office.labelCheckLevelIncluded" bundle="OfficeResources"></mifos:mifoslabel>

									<br>
									<br>
									<span class="fontnormalbold"><mifos:mifoslabel
										name="office.labelNote" bundle="OfficeResources"></mifos:mifoslabel></span>
									<mifos:mifoslabel name="office.labelNoteInstruction"
										bundle="OfficeResources"></mifos:mifoslabel></td>
								</tr>
								<tr>
								<td colspan="2" class="fontnormal">
															<font class="fontnormalRedBold"><html-el:errors
							bundle="OfficeResources" /> </font>
								
								</td>
								</tr>

								<!--  start office display logic-->
								<c:forEach var="ol" items="${requestScope.OfficeLevelList}">
									<tr  class="fontnormal">

										<td width="8%" align="right"><c:choose>
											<c:when test='${ol.levelId == 1}'>
												<input type="checkbox" checked="checked" disabled="disabled">
											</c:when>
											<c:when test='${ol.levelId == 2}'>

												<!--st -->

												<c:forEach var="ohl"
													items="${requestScope.OldHierarchyList}">
													<c:if test="${ohl.levelId == 2}">
														<c:choose>
															<c:when test='${ohl.configured ==0}'>
																<input name="regionalOffice" type="checkbox">
															</c:when>
															<c:otherwise>
																<input name="regionalOffice" type="checkbox"
																	checked="checked">

															</c:otherwise>

														</c:choose>
													</c:if>
												</c:forEach>

												<!--end -->
											</c:when>
											<c:when test='${ol.levelId == 3}'>
												<!--st -->
												<c:forEach var="ohl"
													items="${requestScope.OldHierarchyList}">
													<c:if test="${ohl.levelId == 3}">
														<c:choose>
															<c:when test='${ohl.configured ==0}'>
																<input name="subRegionalOffice" type="checkbox">
															</c:when>
															<c:otherwise>
																<input name="subRegionalOffice" type="checkbox"
																	checked="checked">
															</c:otherwise>

														</c:choose>
													</c:if>
												</c:forEach>


												<!--end -->
											</c:when>
											<c:when test='${ol.levelId == 4}'>

												<c:forEach var="ohl"
													items="${requestScope.OldHierarchyList}">
													<c:if test="${ohl.levelId == 4}">
														<c:choose>
															<c:when test='${ohl.configured ==0}'>
																<input name="areaOffice" type="checkbox">
															</c:when>
															<c:otherwise>
																<input name="areaOffice" type="checkbox"
																	checked="checked">
															</c:otherwise>

														</c:choose>
													</c:if>
												</c:forEach>

											</c:when>
											<c:when test='${ol.levelId == 5}'>
												<input type="checkbox" checked="checked" disabled="disabled">
											</c:when>
											<c:otherwise>
											</c:otherwise>
										</c:choose></td>




										<td width="92%"><c:out value="${ol.levelName}" /></td>






									</tr>

								</c:forEach>
							</table>



							<table width="98%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td class="blueline">&nbsp;</td>
								</tr>
							</table>
							<br>
							<table width="98%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center">&nbsp; <html-el:submit styleClass="buttn"
										style="width:70px;"></html-el:submit> &nbsp; <html-el:button
										onclick="goToCancelPage();" property="cancelButton"
										value="Cancel" styleClass="cancelbuttn" style="width:70px">
										<mifos:mifoslabel name="office.button.cancel"
											bundle="OfficeResources"></mifos:mifoslabel>
									</html-el:button></td>
								</tr>
							</table>
							<br>
							</td>
						</tr>
					</table>
					<br>
					
			<!-- hidden veriable which will be set to method -->
			<html-el:hidden property="method" value="update" />
			<!-- hidden veriable which will set input veriable -->
			<html-el:hidden property="input" value="create" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
