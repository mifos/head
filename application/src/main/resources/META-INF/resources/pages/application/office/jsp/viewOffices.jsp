<%-- 
Copyright (c) 2005-2011 Grameen Foundation USA
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
<!-- viewOffices.jsp -->

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
        <span id="page.id" title="viewOffices"></span>
		<html-el:form action="/offAction.do?method=preview">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"><html-el:link styleId="viewOffices.link.admin"
						href="AdminAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
						<mifos:mifoslabel name="office.labelLinkAdmin"
							bundle="OfficeResources"></mifos:mifoslabel>
					</html-el:link> / </span> <span class="fontnormal8ptbold"><mifos:mifoslabel
						name="office.labelLinkViewOffices" bundle="OfficeResources"></mifos:mifoslabel></span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange"><span class="headingorange"><mifos:mifoslabel
								name="office.labelLinkViewOffices" bundle="OfficeResources"></mifos:mifoslabel></span></td>
						</tr>
						<tr>
							<td class="fontnormalbold"><span class="fontnormal"><mifos:mifoslabel
								name="office.labelViewOfficeInstruction"
								bundle="OfficeResources"></mifos:mifoslabel> <html-el:link styleId="viewOffices.link.newOffice"
								href="offAction.do?method=load&officeLevel=&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="office.labelViewOfficeAddNewOffice"
									bundle="OfficeResources"></mifos:mifoslabel>
							</html-el:link><br>
							<br>
							<font class="fontnormalRedBold"><span id="viewOffices.error.message"><html-el:errors bundle="OfficeResources" /></span></font>
							 
							<c:forEach var="headOffice" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'headOfficeList')}">
									<span class="fontnormalbold">
									<html-el:link styleId="viewOffices.link.viewHeadOffice" href="offAction.do?method=get&officeId=${headOffice.id}&randomNUm=${sessionScope.randomNUm}">
									<c:out value="${headOffice.name}" /></html-el:link> <br>
									</span>
							</c:forEach>

							<c:set var="regionalConfig" scope="request" value="false" ></c:set>
							<c:set var="subRegionalConfig" scope="request" value="false" ></c:set>
							<c:set var="areaConfig" scope="request" value="false" ></c:set>

							<c:if test="${ !empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'regionalOfficeList')}">
								<c:set var="regionalConfig" scope="request" value="true" ></c:set>
							</c:if>
       
                            <c:set var="regional" />
                            <c:set var="regionalParentOffice" />

							<c:choose>
								<c:when test="${empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'regionalOfficeList') && regionalConfig == 'true' }">
									<br>
									<table width="95%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="61%"><span class="fontnormalbold"> <mifos:mifoslabel
												name="Office.labelRegionalOffice" bundle="OfficeUIResources" />
											</span></td>
											<td width="39%" align="right">
											<html-el:link styleId="viewOffices.link.newRegionalOffice" href="offAction.do?method=load&officeLevel=${OfficeLevel.REGIONALOFFICE.value}&randomNUm=${sessionScope.randomNUm}">
												<mifos:mifoslabel name="office.labelAddNew" bundle="OfficeResources" />
												<mifos:mifoslabel name="Office.labelRegionalOffice" bundle="OfficeUIResources" />
											</html-el:link>
											</td>
										</tr>
									</table>
								</c:when>
								<c:otherwise>
									<br>
									<table width="95%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="61%"><span class="fontnormalbold">
												<span class="fontnormalbold"><mifos:mifoslabel name="Office.labelRegionalOffice" bundle="OfficeUIResources" /></span></span></td>
											<td width="39%" align="right">
												<html-el:link styleId="viewOffices.link.newRegionalOffice" 
													href="offAction.do?method=load&officeLevel=${OfficeLevel.REGIONALOFFICE.value}&randomNUm=${sessionScope.randomNUm}">
													<mifos:mifoslabel name="office.labelAddNew" bundle="OfficeResources" />
													<mifos:mifoslabel name="Office.labelRegionalOffice" bundle="OfficeUIResources" />
												</html-el:link>
											</td>
										</tr>
									</table>
                                    <br/>
									<c:forEach var="regionalOffice" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'regionalOfficeList')}">
                                        <c:if test="${ !empty regionalParentOffice  && regionalParentOffice != regionalOffice.parentOfficeName}">
                                            <br/>
                                            <c:set var="regionalParentOffice" value="${regionalOffice.parentOfficeName}" />
                                            <span class="fontnormal"><c:out value="${regionalOffice.parentOfficeName}" /></span>
                                        </c:if>
                                        <c:if test="${empty regionalParentOffice}">
                                            <span class="fontnormal"><c:out value="${regionalOffice.parentOfficeName}" /></span>
                                            <c:set var="regionalParentOffice" value="${regionalOffice.parentOfficeName}" />
                                            <c:set var="regional" value="${regionalOffice.parentOfficeName}" />
                                        </c:if>
										<span class="fontnormalbold"> </span>
										<table width="90%" border="0" cellspacing="0" cellpadding="0">
											<tr class="fontnormal">
												<td width="1%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
												<td width="99%"><html-el:link styleId="viewOffices.link.viewRegionalOffice"
													href="offAction.do?method=get&officeId=${regionalOffice.id}&randomNUm=${sessionScope.randomNUm}">
													<c:out value="${regionalOffice.name}" />
												</html-el:link>&nbsp;&nbsp;&nbsp;
												<c:if test="${office.statusId == OfficeStatus.INACTIVE.value}">
													<mifos:MifosImage id="inactive" moduleName="org.mifos.customers.office.util.resources.officeImages" />
													<mifos:mifoslabel name="OfficeStatus-Inactive" bundle="LookupValueMessages" />
												</c:if></td>
											</tr>
										</table>
									</c:forEach>
								</c:otherwise>
							</c:choose>
                            <br/>

							<c:if test="${ !empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'divisionalOfficeList')}">
							     <c:set var="subRegionalConfig" scope="request" value="true" ></c:set>
							</c:if>
       
                            <c:set var="divisional" />
                            <c:set var="divisionalParentOffice" /> 

							 <c:choose>
								<c:when test="${empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'divisionalOfficeList') && subRegionalConfig == 'true' }">
									<br>
									<table width="95%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="61%">
												<span class="fontnormalbold"><mifos:mifoslabel name="Office.labelDivisionalOffice" bundle="OfficeUIResources" /></span>
											</td>
											<td width="39%" align="right">
												<html-el:link styleId="viewOffices.link.newDivisionalOffice" href="offAction.do?method=load&officeLevel=${OfficeLevel.SUBREGIONALOFFICE.value}&randomNUm=${sessionScope.randomNUm}">
													<mifos:mifoslabel name="office.labelAddNew" bundle="OfficeResources" />
													<mifos:mifoslabel name="Office.labelDivisionalOffice" bundle="OfficeUIResources" />
												</html-el:link>
											</td>
										</tr>
									</table>
								</c:when>
								<c:otherwise>
									<br>
									<table width="95%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="61%">
												<span class="fontnormalbold">
													<span class="fontnormalbold"><mifos:mifoslabel name="Office.labelDivisionalOffice" bundle="OfficeUIResources" /></span>
												</span>
											</td>
											<td width="39%" align="right"><html-el:link styleId="viewOffices.link.newDivisionalOffice"
												href="offAction.do?method=load&officeLevel=${OfficeLevel.SUBREGIONALOFFICE.value}&randomNUm=${sessionScope.randomNUm}">
												<mifos:mifoslabel name="office.labelAddNew" bundle="OfficeResources"></mifos:mifoslabel>
												<mifos:mifoslabel name="Office.labelDivisionalOffice" bundle="OfficeUIResources" />
											</html-el:link></td>
										</tr>
									</table>
									<br/>	
									<c:forEach var="divisionalOffice" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'divisionalOfficeList')}">
                                        <c:if test="${ !empty divisionalParentOffice  && divisionalParentOffice != divisionalOffice.parentOfficeName}">
                                            <br/>
                                            <c:set var="divisionalParentOffice" value="${divisionalOffice.parentOfficeName}" />
                                            <span class="fontnormal"><c:out value="${divisionalOffice.parentOfficeName}" /></span>
                                        </c:if>
                                        <c:if test="${empty divisionalParentOffice}">
                                            <span class="fontnormal"><c:out value="${divisionalOffice.parentOfficeName}" /></span>
                                            <c:set var="divisionalParentOffice" value="${divisionalOffice.parentOfficeName}" />
                                            <c:set var="divisional" value="${divisionalOffice.parentOfficeName}" />
                                        </c:if>
										<span class="fontnormalbold"> </span>
										<table width="90%" border="0" cellspacing="0" cellpadding="0">
											<tr class="fontnormal">
												<td width="1%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
												<td width="99%">
													<html-el:link styleId="viewOffices.link.viewDivisionalOffice" 
														href="offAction.do?method=get&officeId=${divisionalOffice.id}&randomNUm=${sessionScope.randomNUm}">
														<c:out value="${divisionalOffice.name}" />
													</html-el:link>&nbsp;&nbsp;&nbsp;
												
												<c:if test="${office.statusId == OfficeStatus.INACTIVE.value}">
													<mifos:MifosImage id="inactive" moduleName="org.mifos.customers.office.util.resources.officeImages" />
													<mifos:mifoslabel name="OfficeStatus-Inactive" bundle="LookupValueMessages" />
												</c:if>
												</td>
											</tr>
										</table>
									</c:forEach>
								</c:otherwise>
							</c:choose> 
                            <br/>
		
							<c:if test="${ !empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'areaOfficeList')}">
							     <c:set var="areaConfig" scope="request" value="true" />
							</c:if>
       
                            <c:set var="area" />
                            <c:set var="areaParentOffice" /> 
												
							<c:choose>
								<c:when test="${empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'areaOfficeList') && areaConfig == 'true'}">
									<br>
									<table width="95%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="61%">
												<span class="fontnormalbold"><mifos:mifoslabel name="Office.labelAreaOffice" bundle="OfficeUIResources" /></span>
											</td>
											<td width="39%" align="right">
												<html-el:link styleId="viewOffices.link.newAreaOffice"
													href="offAction.do?method=load&officeLevel=${OfficeLevel.AREAOFFICE.value}&randomNUm=${sessionScope.randomNUm}">
													<mifos:mifoslabel name="office.labelAddNew" bundle="OfficeResources" />
													<mifos:mifoslabel name="Office.labelAreaOffice" bundle="OfficeUIResources" />
												</html-el:link>
											</td>
										</tr>
									</table>
								</c:when>
								<c:otherwise>
									<br>
									<table width="95%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="61%">
												<span class="fontnormalbold">
													<span class="fontnormalbold"><mifos:mifoslabel name="Office.labelAreaOffice" bundle="OfficeUIResources" /></span>
												</span>
											</td>
											<td width="39%" align="right">
												<html-el:link styleId="viewOffices.link.newAreaOffice"
													href="offAction.do?method=load&officeLevel=${OfficeLevel.AREAOFFICE.value}&randomNUm=${sessionScope.randomNUm}">
													<mifos:mifoslabel name="office.labelAddNew" bundle="OfficeResources"></mifos:mifoslabel>
													<mifos:mifoslabel name="Office.labelAreaOffice" bundle="OfficeUIResources" />
												</html-el:link>
											</td>
										</tr>
									</table>
									<br/>
									<c:forEach var="areaOffice" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'areaOfficeList')}">
                                        <c:if test="${ !empty areaParentOffice  && areaParentOffice != areaOffice.parentOfficeName}">
                                            <br/>
                                            <c:set var="areaParentOffice" value="${areaOffice.parentOfficeName}" />
                                            <span class="fontnormal"><c:out value="${areaOffice.parentOfficeName}" /></span>
                                        </c:if>
                                        <c:if test="${empty areaParentOffice}">
                                            <span class="fontnormal"><c:out value="${areaOffice.parentOfficeName}" /></span>
                                            <c:set var="areaParentOffice" value="${areaOffice.parentOfficeName}" />
                                            <c:set var="area" value="${areaOffice.parentOfficeName}" />
                                        </c:if>
										<span class="fontnormalbold"> </span>
										<table width="90%" border="0" cellspacing="0" cellpadding="0">
											<tr class="fontnormal">
												<td width="1%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
												<td width="99%">
													<html-el:link styleId="viewOffices.link.viewAreaOffice" 
														href="offAction.do?method=get&officeId=${areaOffice.id}&randomNUm=${sessionScope.randomNUm}">
														<c:out value="${areaOffice.name}" />
													</html-el:link>&nbsp;&nbsp;&nbsp;
													<c:if test="${areaOffice.statusId == OfficeStatus.INACTIVE.value}">
														<mifos:MifosImage id="inactive" moduleName="org.mifos.customers.office.util.resources.officeImages" />
														<mifos:mifoslabel name="OfficeStatus-Inactive" bundle="LookupValueMessages" />
													</c:if>
												</td>
											</tr>
										</table>
									</c:forEach>
								</c:otherwise>
							</c:choose> 
							<br/>
							
							<c:set var="branch" />
                            <c:set var="branchParentOffice" /> 
							
							<c:choose>
								<c:when test="${empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'branchOfficeList')}">
									<br>
									<table width="95%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="61%">
												<span class="fontnormalbold">
													<mifos:mifoslabel name="Office.labelBranchOffice" bundle="OfficeUIResources" />
												</span>
											</td>
											<td width="39%" align="right">
												<html-el:link styleId="viewOffices.link.newBranchOffice" 
													href="offAction.do?method=load&officeLevel=${OfficeLevel.BRANCHOFFICE.value}&randomNUm=${sessionScope.randomNUm}">
													<mifos:mifoslabel name="office.labelAddNew" bundle="OfficeResources" />
													<mifos:mifoslabel name="Office.labelBranchOffice" bundle="OfficeUIResources" />
												</html-el:link>
											</td>
										</tr>
									</table>
								</c:when>
								<c:otherwise>
									<br>
									<table width="95%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="61%">
												<span class="fontnormalbold"><mifos:mifoslabel name="Office.labelBranchOffice" bundle="OfficeUIResources" /></span>
											</td>
											<td width="39%" align="right">
												<html-el:link styleId="viewOffices.link.newBranchOffice" href="offAction.do?method=load&officeLevel=5&randomNUm=${sessionScope.randomNUm}">
													<mifos:mifoslabel name="office.labelAddNew" bundle="OfficeResources" />
													<mifos:mifoslabel name="Office.labelBranchOffice" bundle="OfficeUIResources" />
												</html-el:link>
											</td>
										</tr>
									</table>
                                    <br/>
									<c:forEach var="branchOffice" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'branchOfficeList')}" varStatus="counter">
										<c:if test="${ !empty branchParentOffice  && branchParentOffice != branchOffice.parentOfficeName}">
                                            <br/>
                                            <c:set var="branchParentOffice" value="${branchOffice.parentOfficeName}" />
                                            <span class="fontnormal"><c:out value="${branchOffice.parentOfficeName}" /></span>
                                        </c:if>
                                        <c:if test="${empty branchParentOffice}">
                                            <span class="fontnormal"><c:out value="${branchOffice.parentOfficeName}" /></span>
                                            <c:set var="branchParentOffice" value="${branchOffice.parentOfficeName}" />
                                            <c:set var="branch" value="${branchOffice.parentOfficeName}" />
                                        </c:if>
										<table width="90%" border="0" cellspacing="0" cellpadding="0">
											<tr class="fontnormal">
												<td width="1%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
												<td width="99%">
													<html-el:link styleId="viewOffices.link.viewBranchOffice" href="offAction.do?method=get&officeId=${branchOffice.id}&randomNUm=${sessionScope.randomNUm}">
													<c:out value="${branchOffice.name}" /></html-el:link>&nbsp;&nbsp;&nbsp; 
													<c:if test="${branchOffice.statusId == OfficeStatus.INACTIVE.value}">
														<mifos:MifosImage id="inactive" moduleName="org.mifos.customers.office.util.resources.officeImages" />
														<mifos:mifoslabel name="OfficeStatus-Inactive" bundle="LookupValueMessages" />
													</c:if>
												</td>
											</tr>
										</table>
									</c:forEach>
								</c:otherwise>
							</c:choose>
							</td>
						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>
			<html-el:submit property="backButton" styleId="viewoffices.button.back" styleClass="buttn" onclick="form.action='AdminAction.do?method=load'">
				<mifos:mifoslabel name="button.back" bundle ="PersonnelUIResources"></mifos:mifoslabel>
			</html-el:submit>
			<br>
			<br>
			<html-el:hidden property="input" value="search" />
			<html-el:hidden property="officeLevel" value=""/>
			<html-el:hidden property="officeId" value="" />
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>

	</tiles:put>

</tiles:insert>

