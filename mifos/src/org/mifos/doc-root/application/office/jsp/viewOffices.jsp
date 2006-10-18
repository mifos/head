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
		<html-el:form action="/offAction.do?method=preview">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"><html-el:link
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
								bundle="OfficeResources"></mifos:mifoslabel> <html-el:link
								href="offAction.do?method=load&officeLevel=&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="office.labelViewOfficeAddNewOffice"
									bundle="OfficeResources"></mifos:mifoslabel>
							</html-el:link><br>
							<br>
							<font class="fontnormalRedBold"><html-el:errors
								bundle="OfficeResources" /></font> </span> <c:set
								var="regional" /> <c:set var="divisional" /> <c:set var="area" />
							<!-- start main loop from here --> <c:forEach var="office"
								items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'headOfficeList')}">
								<!-- for head office -->
								<c:if test="${office.level.id == OfficeLevel.HEADOFFICE.value }">
									<span class="fontnormalbold"> <html-el:link
										href="offAction.do?method=get&officeId=${office.officeId}&randomNUm=${sessionScope.randomNUm}">
										<c:out value="${office.officeName}" />
									</html-el:link> <br>
									</span>
								</c:if>
							</c:forEach>

							<c:set var="regionalConfig" scope="request" value="false" ></c:set>
							<c:set var="subRegionalConfig" scope="request" value="false" ></c:set>
							<c:set var="areaConfig" scope="request" value="false" ></c:set>
							<c:forEach var="level" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'OfficeLevelList')}">
							   <c:if test="${OfficeLevel.REGIONALOFFICE.value == level.levelId}">
							     <c:set var="regionalConfig" scope="request" value="true" ></c:set>
							   </c:if>
							   <c:if test="${OfficeLevel.SUBREGIONALOFFICE.value == level.levelId}">
							     <c:set var="subRegionalConfig" scope="request" value="true" ></c:set>
							   </c:if>
							   <c:if test="${OfficeLevel.AREAOFFICE.value == level.levelId}">
							     <c:set var="areaConfig" scope="request" value="true" ></c:set>
							   </c:if>
							</c:forEach>

							<c:choose>
								<c:when test="${empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'regionalOfficeList')
									&& regionalConfig == 'true' }">
									<br>
									<table width="95%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="61%"><span class="fontnormalbold"> <mifos:mifoslabel
												name="Office.labelRegionalOffice" bundle="OfficeUIResources" />
											</span></td>
											<td width="39%" align="right"><html-el:link
												href="offAction.do?method=load&officeLevel=${OfficeLevel.REGIONALOFFICE.value}&randomNUm=${sessionScope.randomNUm}">
												<mifos:mifoslabel name="office.labelAddNew"
													bundle="OfficeResources" />
												<mifos:mifoslabel name="Office.labelRegionalOffice"
													bundle="OfficeUIResources" />
											</html-el:link></td>
										</tr>
									</table>
								</c:when>
								<c:otherwise>
									<c:forEach var="office" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'regionalOfficeList')}">
										<!-- for regional  office -->
										<c:if
											test="${office.level.id == OfficeLevel.REGIONALOFFICE.value && empty regional}">
											<c:set var="regional" value="level" />
											<br>
											<table width="95%" border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td width="61%"><span class="fontnormalbold"><span
														class="fontnormalbold"><c:out value="${office.level.name}" /></span></span></td>
													<td width="39%" align="right"><html-el:link
														href="offAction.do?method=load&officeLevel=${office.level.id}&randomNUm=${sessionScope.randomNUm}">
														<mifos:mifoslabel name="office.labelAddNew"
															bundle="OfficeResources"></mifos:mifoslabel>
														<c:out value="${office.level.name}" />
													</html-el:link></td>
												</tr>
											</table>
										</c:if>
										<span class="fontnormalbold"> </span>
										<!--  now the childern loop for regional office -->
										<table width="90%" border="0" cellspacing="0" cellpadding="0">
											<tr class="fontnormal">
												<td width="1%"><img
													src="pages/framework/images/bullet_circle.gif" width="9"
													height="11"></td>
												<td width="99%"><html-el:link
													href="offAction.do?method=get&officeId=${office.officeId}&randomNUm=${sessionScope.randomNUm}">
													<c:out value="${office.officeName}" />
												</html-el:link>&nbsp;&nbsp;&nbsp; <c:if
													test="${office.status.id == OfficeStatus.INACTIVE.value}">
													<mifos:MifosImage id="inactive" moduleName="office" />
													<c:out value="${office.status.name}" />
												</c:if></td>
											</tr>
										</table>
										<!--  end childern loop for regional office -->
									</c:forEach>
								</c:otherwise>
							</c:choose>



							 <c:choose>
								<c:when test="${empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'divisionalOfficeList') && subRegionalConfig == 'true' }">
									<br>
									<table width="95%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="61%"><span class="fontnormalbold"> <mifos:mifoslabel
												name="Office.labelDivisionalOffice"
												bundle="OfficeUIResources" /> </span></td>
											<td width="39%" align="right"><html-el:link
												href="offAction.do?method=load&officeLevel=${OfficeLevel.SUBREGIONALOFFICE.value}&randomNUm=${sessionScope.randomNUm}">
												<mifos:mifoslabel name="office.labelAddNew"
													bundle="OfficeResources" />
												<mifos:mifoslabel name="Office.labelDivisionalOffice"
													bundle="OfficeUIResources" />
											</html-el:link></td>
										</tr>
									</table>
								</c:when>
								<c:otherwise>
									<c:forEach var="office" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'divisionalOfficeList')}">
										<!-- for regional  office -->
										<c:if
											test="${office.level.id == OfficeLevel.SUBREGIONALOFFICE.value &&  empty divisional}">
											<c:set var="divisional" value="level" />
											<br>
											<table width="95%" border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td width="61%"><span class="fontnormalbold"><span
														class="fontnormalbold"><c:out value="${office.level.name}" /></span></span></td>
													<td width="39%" align="right"><html-el:link
														href="offAction.do?method=load&officeLevel=${office.level.id}&randomNUm=${sessionScope.randomNUm}">
														<mifos:mifoslabel name="office.labelAddNew"
															bundle="OfficeResources"></mifos:mifoslabel>
														<c:out value="${office.level.name}" />
													</html-el:link></td>
												</tr>
											</table>
										</c:if>
										<span class="fontnormalbold"> </span>
										<!--  now the childern loop for regional office -->
										<table width="90%" border="0" cellspacing="0" cellpadding="0">
											<tr class="fontnormal">
												<td width="1%"><img
													src="pages/framework/images/bullet_circle.gif" width="9"
													height="11"></td>
												<td width="99%"><html-el:link
													href="offAction.do?method=get&officeId=${office.officeId}&randomNUm=${sessionScope.randomNUm}">
													<c:out value="${office.officeName}" />
												</html-el:link>&nbsp;&nbsp;&nbsp; <c:if
													test="${office.status.id == OfficeStatus.INACTIVE.value}">
													<mifos:MifosImage id="inactive" moduleName="office" />
													<c:out value="${office.status.name}" />
												</c:if></td>
											</tr>
										</table>
										<!--  end childern loop for regional office -->
									</c:forEach>
								</c:otherwise>
							</c:choose> <c:choose>
								<c:when test="${empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'areaOfficeList') && areaConfig == 'true'}">
									<br>
									<table width="95%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="61%"><span class="fontnormalbold"> <mifos:mifoslabel
												name="Office.labelAreaOffice" bundle="OfficeUIResources" />
											</span></td>
											<td width="39%" align="right"><html-el:link
												href="offAction.do?method=load&officeLevel=${OfficeLevel.AREAOFFICE.value}&randomNUm=${sessionScope.randomNUm}">
												<mifos:mifoslabel name="office.labelAddNew"
													bundle="OfficeResources" />
												<mifos:mifoslabel name="Office.labelAreaOffice"
													bundle="OfficeUIResources" />
											</html-el:link></td>
										</tr>
									</table>
								</c:when>
								<c:otherwise>
									<c:forEach var="office" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'areaOfficeList')}">
										<!-- for regional  office -->
										<c:if
											test="${office.level.id == OfficeLevel.AREAOFFICE.value && empty area }">
											<c:set var="area" value="level" />
											<br>
											<table width="95%" border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td width="61%"><span class="fontnormalbold"><span
														class="fontnormalbold"><c:out value="${office.level.name}" /></span></span></td>
													<td width="39%" align="right"><html-el:link
														href="offAction.do?method=load&officeLevel=${office.level.id}&randomNUm=${sessionScope.randomNUm}">
														<mifos:mifoslabel name="office.labelAddNew"
															bundle="OfficeResources"></mifos:mifoslabel>
														<c:out value="${office.level.name}" />
													</html-el:link></td>
												</tr>
											</table>
										</c:if>
										<span class="fontnormalbold"> </span>
										<!--  now the childern loop for regional office -->
										<table width="90%" border="0" cellspacing="0" cellpadding="0">
											<tr class="fontnormal">
												<td width="1%"><img
													src="pages/framework/images/bullet_circle.gif" width="9"
													height="11"></td>
												<td width="99%"><html-el:link
													href="offAction.do?method=get&officeId=${office.officeId}&randomNUm=${sessionScope.randomNUm}">
													<c:out value="${office.officeName}" />
												</html-el:link>&nbsp;&nbsp;&nbsp; <c:if
													test="${office.status.id == OfficeStatus.INACTIVE.value}">
													<mifos:MifosImage id="inactive" moduleName="office" />
													<c:out value="${office.status.name}" />
												</c:if></td>
											</tr>
										</table>
										<!--  end childern loop for regional office -->
									</c:forEach>
								</c:otherwise>
							</c:choose> <c:set var="parentOffice" /> <c:set var="branch" />
							<c:choose>
								<c:when test="${empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'branchOfficeList')}">
									<c:set var="branch" value="level" />
									<br>
									<table width="95%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="61%"><span class="fontnormalbold"> <mifos:mifoslabel
												name="Office.labelBranchOffice" bundle="OfficeUIResources" />
											</span></td>
											<td width="39%" align="right"><html-el:link
												href="offAction.do?method=load&officeLevel=${OfficeLevel.BRANCHOFFICE.value}&randomNUm=${sessionScope.randomNUm}">
												<mifos:mifoslabel name="office.labelAddNew"
													bundle="OfficeResources" />
												<mifos:mifoslabel name="Office.labelBranchOffice"
													bundle="OfficeUIResources" />
											</html-el:link></td>
										</tr>
									</table>
								</c:when>
								<c:otherwise>
									<c:forEach var="office" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'branchOfficeList')}"
										varStatus="counter">
										<c:if
											test="${office.level.id == OfficeLevel.BRANCHOFFICE.value && empty branch}">
											<c:set var="branch" value="level" />
											<br>
											<table width="95%" border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td width="61%"><span class="fontnormalbold"> <c:out
														value="${office.level.name}"></c:out> </span></td>
													<td width="39%" align="right"><html-el:link
														href="offAction.do?method=load&officeLevel=5&randomNUm=${sessionScope.randomNUm}">
														<mifos:mifoslabel name="office.labelAddNew"
															bundle="OfficeResources" />
														<c:out value="${office.level.name}"></c:out>
													</html-el:link></td>
												</tr>
											</table>
										</c:if>
										<c:if
											test="${ !empty parentOffice  && parentOffice != office.parentOffice.officeName}">
											<br>
											<span class="fontnormal"><c:out
												value="${office.parentOffice.officeName}" /></span>
										</c:if>
										<c:if test="${empty parentOffice}">
											<span class="fontnormal"><c:out
												value="${office.parentOffice.officeName}" /></span>
											<c:set var="parentOffice"
												value="${office.parentOffice.officeName}" />
											<c:set var="branch" value="${office.parentOffice.officeName}" />
										</c:if>
										<table width="90%" border="0" cellspacing="0" cellpadding="0">
											<tr class="fontnormal">
												<td width="1%"><img
													src="pages/framework/images/bullet_circle.gif" width="9"
													height="11"></td>
												<td width="99%"><html-el:link
													href="offAction.do?method=get&officeId=${office.officeId}&randomNUm=${sessionScope.randomNUm}">
													<c:out value="${office.officeName}" />
												</html-el:link> &nbsp;&nbsp;&nbsp; <c:if
													test="${office.status.id == OfficeStatus.INACTIVE.value}">
													<mifos:MifosImage id="inactive" moduleName="office" />
													<c:out value="${office.status.name}" />
												</c:if></td>
											</tr>
										</table>
									</c:forEach>
								</c:otherwise>
							</c:choose></td>
						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>
			<br>
			<br>
			<!-- hidden veriable which will set input veriable -->
			<html-el:hidden property="input" value="search" />
			<!-- hidden varible for office type -->
			<html-el:hidden property="officeLevel" value=""/>
			<html-el:hidden property="officeId" value="" />
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>

	</tiles:put>

</tiles:insert>

