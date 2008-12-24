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
<!-- createNewOfficeSuccessful.jsp -->

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<html-el:form action="/offAction.do?method=get" >

					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="70%" align="left" valign="top" class="paddingL15T15">
							<table width="98%" border="0" cellspacing="0" cellpadding="3">
								<tr>
									<td class="headingorange"><mifos:mifoslabel
										name="Office.labelCreatedSuccessfully"
										/> <br>
									<br>
									</td>
								</tr>
								<tr>
									<td class="fontnormalbold"><mifos:mifoslabel
										name="Office.labelPleaseNote" />
									<span class="fontnormal"><c:out	value="${sessionScope.offActionForm.officeName}"></c:out> <mifos:mifoslabel
										name="Office.labelOfficeAssignedNumber"
										/> </span> <c:out
										value="${sessionScope.offActionForm.globalOfficeNum}"></c:out> <br>
									<br>



									<html-el:link action="/offAction.do?method=get&officeId=${sessionScope.offActionForm.officeId}&randomNUm=${sessionScope.randomNUm}">
									 <mifos:mifoslabel
										name="Office.labelViewOfficeDetails" />
									</html-el:link>
									<span class="fontnormal"><br>
									<br>
									</span><span class="fontnormal">
									<html-el:link action="/offAction.do?method=load&randomNUm=${sessionScope.randomNUm}" >

									 <mifos:mifoslabel
										name="Office.labelAddNewOfficeNow" /></html-el:link> </span></td>
								</tr>
							</table>
							<br>
							</td>
						</tr>
					</table>
					<br>
					<!-- </td>
				</tr>
			</table> -->
			<html-el:hidden property="input" value="createSuccess" />
			<html-el:hidden property="officeId"  />
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>

	</tiles:put>

</tiles:insert>
