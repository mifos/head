<!--

/**

 * view_additional_field_categories.jsp



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

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/date" prefix="date"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>

<script language="javascript" type="text/javascript">

</script>
<tiles:insert definition=".view">
	<tiles:put name="body" type="string">

		<html-el:form action="customFieldsAction.do">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"> <html-el:link
								href="customFieldsAction.do?method=cancel&amp;currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="configuration.admin" />
							</html-el:link> / </span><span class="fontnormal8ptbold"><mifos:mifoslabel
								name="configuration.view_additional_fields" /> </span>
					</td>
				</tr>
			</table>
			<table></table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
						<table width="95%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td class="headingorange">
									<mifos:mifoslabel name="configuration.view_additional_fields" />
								</td>

							</tr>
							<tr>
								<td class="fontnormalbold">
									<span class="fontnormal"><mifos:mifoslabel name="configuration.select_category" /> 
										<html-el:link href="customFieldsAction.do?method=loadDefineCustomFields&randomNUm=${sessionScope.randomNUm}">
											<mifos:mifoslabel name="configuration.add_new_field" >
										</mifos:mifoslabel>
										</html-el:link>
										<br> 
									</span>
									<br>
									<mifos:CustomFieldCategoryList actionName="customFieldsAction.do" 
										methodName="viewCategory" flowKey="${requestScope.currentFlowKey}">
									</mifos:CustomFieldCategoryList>
								</td>
							</tr>
						</table>
						<br>
					</td>
				</tr>
			</table>
			<br>
			<td></td>
			<tr></tr>
			<table></table>
			<html-el:hidden property="currentFlowKey"
				value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
