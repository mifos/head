<%--
Copyright (c) 2005-2009 Grameen Foundation USA
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
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
	<span style="display: none" id="page.id">offhierarchy</span>
		<script language="javascript">
			function cancel() {
				document.offhierarchyactionform.action="offhierarchyaction.do?method=cancel";
				offhierarchyactionform.submit();
			}
		</script>
		<script src="pages/framework/js/CommonUtilities.js"></script>
		<html-el:form action="/offhierarchyaction.do?method=update" onsubmit="return func_disableSubmitBtn('submitBtn');">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"><html-el:link styleId="offhierarchy.link.admin" href="offhierarchyaction.do?method=cancel&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="Office.labelLinkAdmin" ></mifos:mifoslabel>
							</html-el:link> / </span><span class="fontnormal8ptbold"><mifos:mifoslabel name="Office.labelViewOfficeHierarchy" /></span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
						<table width="98%" border="0" cellspacing="0" cellpadding="3">
							<tr>
								<td width="35%" class="headingorange">
									<mifos:mifoslabel name="Office.labelViewOfficeHierarchy"/>
								</td>
							</tr>
							<tr>
								<td class="fontnormal">
									<mifos:mifoslabel name="Office.labelMaxMinLevel"/>
								</td>
							</tr>
							<tr >

								<logic:messagesPresent>
								<td>
								<font class="fontnormalRedBold"><span id="offhierarchy.error.message"><html-el:errors
									bundle="OfficeUIResources" /></span></font>
									</td>
								</logic:messagesPresent>
							</tr>
						</table>
						<br>
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormal">
									<mifos:mifoslabel name="Office.labelCheckLevelIncluded" />
									<br>
									<br>
									<span class="fontnormalbold"><mifos:mifoslabel name="Office.labelNote" /></span>
									<mifos:mifoslabel name="Office.labelNoteInstruction" />
								</td>
							</tr>
							<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'officelevels')}" var="ol">
								<tr class="fontnormal">
									<td width="8%" align="right">
										<c:if test='${ol.id == 1}'>
											<html-el:checkbox styleId="offhierarchy.input.officeLevel" property="headOffice" value="1" disabled="true" />
										</c:if>
										<c:if test='${ol.id == 2}'>
											<html-el:checkbox styleId="offhierarchy.input.officeLevel" property="regionalOffice" value="1" />
										</c:if>
										<c:if test='${ol.id == 3}'>
											<html-el:checkbox styleId="offhierarchy.input.officeLevel" property="subRegionalOffice" value="1" />
										</c:if>
										<c:if test='${ol.id == 4}'>
											<html-el:checkbox styleId="offhierarchy.input.officeLevel" property="areaOffice" value="1" />
										</c:if>
										<c:if test='${ol.id == 5}'>
											<html-el:checkbox styleId="offhierarchy.input.officeLevel" property="branchOffice" value="1" disabled="true" />
										</c:if>
									</td>
									<td width="92%">
										<span id="offhierarchy.label.officeLevel"><c:out value="${ol.name}" /></span>
									</td>
								</tr>
							</c:forEach>
						</table>
						<table width="98%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td class="blueline">
									&nbsp;
								</td>
							</tr>
						</table>
						<br>
						<table width="98%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center">
									&nbsp;
									<html-el:submit styleId="offhierarchy.button.submit" property="submitBtn" styleClass="buttn">
									<mifos:mifoslabel name="Office.submit"/>
									</html-el:submit>
									&nbsp;
									<html-el:button styleId="offhierarchy.button.cancel" onclick="cancel()" property="cancelButton"  styleClass="cancelbuttn">
										<mifos:mifoslabel name="Office.cancel" />
									</html-el:button>
								</td>
							</tr>
						</table>
						<br>
					</td>
				</tr>
			</table>
			<br>
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
