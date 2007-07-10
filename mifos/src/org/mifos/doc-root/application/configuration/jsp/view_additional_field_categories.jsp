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
									<a href="customFieldsAction.do?method=add&amp;currentFlowKey=${requestScope.currentFlowKey}"><mifos:mifoslabel name="configuration.add_new_field" /></a> <br> </span><span class="fontnormalbold"><span
										class="fontnormalbold"><br> </span>
									</span>
									<span class="fontnormalbold"> </span>
									<table width="90%" border="0" cellspacing="0" cellpadding="0">

										<tr class="fontnormal">
											<td>
												<img src="pages/framework/images/bullet_circle.gif" width="9" height="11"
													alt="">
											</td>
											<td>
												<a href="VIewCustomFields.htm">Personnel</a>
											</td>
										</tr>
										<tr class="fontnormal">
											<td>
												<img src="pages/framework/images/bullet_circle.gif" width="9" height="11"
													alt="">
											</td>
											<td>
												<a href="VIewCustomFields.htm">Office</a>
											</td>
										</tr>

										<tr class="fontnormal">
											<td width="1%">
												<img src="pages/framework/images/bullet_circle.gif" width="9" height="11"
													alt="">
											</td>
											<td width="99%">
												<a href="VIewCustomFields.htm">Client</a>
											</td>
										</tr>
										<tr class="fontnormal">
											<td>
												<img src="pages/framework/images/bullet_circle.gif" width="9" height="11"
													alt="">
											</td>
											<td>
												<a href="VIewCustomFields.htm">Group</a>
											</td>
										</tr>

										<tr class="fontnormal">
											<td width="1%">
												<img src="pages/framework/images/bullet_circle.gif" width="9" height="11"
													alt="">
											</td>
											<td width="99%">
												<a href="VIewCustomFields.htm">Center</a>
											</td>
										</tr>
										<tr class="fontnormal">
											<td>
												<img src="pages/framework/images/bullet_circle.gif" width="9" height="11"
													alt="">
											</td>
											<td>
												<a href="VIewCustomFields.htm">Loan</a>
											</td>
										</tr>

										<tr class="fontnormal">
											<td width="1%">
												<img src="pages/framework/images/bullet_circle.gif" width="9" height="11"
													alt="">
											</td>
											<td width="99%">
												<a href="VIewCustomFields.htm">Savings</a>
											</td>
										</tr>
									</table>
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
