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
<!-- viewOfficeDetails.jsp -->

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
	<span id="page.id" title="viewOfficeDetails" />
		<html-el:form action="/offAction.do">
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
			   var="BusinessKey" />

					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td class="bluetablehead05"><span class="fontnormal8pt">
							<html-el:link styleId="viewOfficeDetails.link.admin" href="AdminAction.do?method=load&randomNUm=${sessionScope.randomNUm}"><mifos:mifoslabel
								name="Office.labelLinkAdmin" /></html-el:link> / <html-el:link styleId="viewOfficeDetails.link.viewOffices"
										href="offAction.do?method=getAllOffices&randomNUm=${sessionScope.randomNUm}" > <mifos:mifoslabel
								name="Office.labelLinkViewOffices" />

							</html-el:link> / </span><span class="fontnormal8ptbold"><c:out
								value="${BusinessKey.officeName}"></c:out></span></td>
						</tr>
					</table>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="100%" align="left" valign="top" class="paddingL15T15">
							<table width="96%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td width="50%" height="23" class="headingorange"><span id="viewOfficeDetails.text.officeName"><c:out
										value="${BusinessKey.officeName}"></c:out></span></td>
									<td width="50%" align="right">
									<html-el:link styleId="viewOfficeDetails.link.editOfficeInformation" href="offAction.do?method=edit&officeLevel=${BusinessKey.level.id}&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}"
										>
										<mifos:mifoslabel name="Office.labelEditOfficeInfo"
											/>
									</html-el:link>
									</td>
								</tr>
								 <tr><td colspan="2"><font class="fontnormalRedBold"><span id="viewOfficeDetails.error.message"><html-el:errors
								bundle="OfficeUIResources" /></span> </font>
								</td></tr>
								<tr>
									<td height="23" class="fontnormalbold"><span class="fontnormal">
									</span>
									<span class="fontnormal">
									<c:if test="${BusinessKey.officeStatus == OfficeStatus.ACTIVE}">
									<mifos:MifosImage id="active" moduleName="org.mifos.application.office.util.resources.officeImages" />
									</c:if>
									<c:if test="${BusinessKey.officeStatus == OfficeStatus.INACTIVE}">
									<mifos:MifosImage id="inactive" moduleName="org.mifos.application.office.util.resources.officeImages" />
									</c:if>
									<c:out value="${BusinessKey.status.name}"></c:out>
									</span>

									<br>
									<span class="fontnormal"><mifos:mifoslabel
										name="Office.labelOfficeShortName" />
									<span id="viewOfficeDetails.text.shortName"><c:out value="${BusinessKey.shortName}"></c:out></span><br>
									 <!-- End Logic for showing the correct code-->
									<mifos:mifoslabel name="Office.labelOfficeType"/>
									<span id="viewOfficeDetails.text.officeLevel"><c:out value="${BusinessKey.level.name}"></c:out></span>
									 <br>
									<!-- logic for showing the correct parent -->
									<mifos:mifoslabel name="Office.labelParentOffice"	/>
										<c:if test="${not empty BusinessKey.parentOffice}">
										<c:out value="${BusinessKey.parentOffice.officeName}"></c:out>
										</c:if>
										<br>

									 </span><br>
									 <!-- End for showing the correct parent -->

									<c:if test="${not empty BusinessKey.address.address}">
	
									<mifos:mifoslabel name="office.labelAddress"
										bundle="OfficeResources"></mifos:mifoslabel>
										<br>
										<c:if test="${ empty BusinessKey.address.address.displayAddress
									&&  empty BusinessKey.address.address.city
									&&  empty BusinessKey.address.address.state
									&&  empty BusinessKey.address.address.country
									&&  empty BusinessKey.address.address.zip
									&&  empty BusinessKey.address.address.phoneNumber}">
									<br>
									<span
										class="fontnormal"><mifos:mifoslabel name="Office.addressnotentered"/></span>
										<br>
										</c:if>
										<span
										class="fontnormal">
										<c:out value="${BusinessKey.address.address.displayAddress}"></c:out>
										</span>
									</c:if>
									<c:if test="${not empty BusinessKey.address.address.city}">
									<br>
									<span class="fontnormal"><c:out
										value="${BusinessKey.address.address.city}"></c:out>
									</span>
									</c:if>
									<c:if test="${not empty BusinessKey.address.address.state}">
									<br>
									<span class="fontnormal"><c:out
										value="${BusinessKey.address.address.state}"></c:out>
									</span></c:if>

									<c:if test="${not empty BusinessKey.address.address.country}">
									<br>
									<span class="fontnormal"><c:out
										value="${BusinessKey.address.address.country}"></c:out>
										</span>
									</c:if>
									<c:if test="${not empty BusinessKey.address.address.zip}">
									<br>
									<span class="fontnormal"> <c:out
										value="${BusinessKey.address.address.zip}"></c:out>
									</span>
									</c:if>
									<c:if test="${ not empty BusinessKey.address.address.phoneNumber}">
									<br>
									<br>
									<span
										class="fontnormal">
									<mifos:mifoslabel name="Office.labelTelephone"
										/></span> <span
										class="fontnormal"><c:out
										value="${BusinessKey.address.address.phoneNumber}"></c:out>
									</span>
									<br>
									</c:if>
									<br>

									<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
										<mifos:mifoslabel name="Office.labelAdditionInformation" />
									 <span class="fontnormal"><br>
									 <c:forEach var="cfdef"
										items="${BusinessKey.customFields}">
										<c:forEach var="cf" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
											<c:if test="${cfdef.fieldId==cf.fieldId}">
												<font class="fontnormal">
												<mifos:mifoslabel
											name="${cf.lookUpEntity.entityType}"
											bundle="OfficeResources"></mifos:mifoslabel>:
												</font>
												<span class="fontnormal"><c:out value="${cfdef.fieldValue}" /><br>
												</span>
											</c:if>
										</c:forEach>
									</c:forEach>
									</c:if>								</td>
								</tr>
							</table>
							<br>
							</td>
						</tr>
					</table>
					<br>
			<html-el:hidden	property="officeId"	value="${BusinessKey.officeId}" />
			<html-el:hidden property="officeLevel" value="${BusinessKey.level.id}"/>
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>

	</tiles:put>

</tiles:insert>
