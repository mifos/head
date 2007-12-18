<!--

/**

 * configureMoratorium.jsp    version: 1.0



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
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".create">
	<tiles:put name="body" type="string">
		<script language="javascript">
		<!--
			function fnCancel(form) {
				form.action="AdminAction.do?method=load";
				form.submit();
			}
		//-->
		</script>
		<html-el:form action="/moratoriumAction.do?method=loadSearch">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="350" align="left" valign="top" bgcolor="#FFFFFF">
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td align="center" class="heading">&nbsp;</td>
						</tr>
					</table>
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td class="bluetablehead">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td width="27%" align="left">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><html-el:img
												src="pages/framework/images/timeline/bigarrow.gif"
												width="17" height="17" /></td>
											<td class="timelineboldorange"><mifos:mifoslabel
												name="moratorium.moratoriumcatinfo" bundle="moratoriumUIResources" /></td>
										</tr>
									</table>
									</td>
									<td width="73%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><html-el:img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17" height="17" /></td>
											<td class="timelineboldorangelight"><mifos:mifoslabel
												name="moratorium.review" bundle="moratoriumUIResources" /></td>
										</tr>
									</table>
									</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0" class="bluetableborder">
						<tr>
							<td align="left" valign="top" class="paddingleftCreates">
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td class="headingorange"><span class="heading"><mifos:mifoslabel
										name="moratorium.define" bundle="moratoriumUIResources" /> - </span><mifos:mifoslabel
										name="moratorium.categoryinfo" bundle="moratoriumUIResources" /></td>
								</tr>
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="moratorium.compfields" bundle="moratoriumUIResources" />
									<mifos:mifoslabel name="moratorium.clickcanc"
										bundle="moratoriumUIResources" /><br>
									
								</tr>
								<tr>
									<td>
										<table width="100%">
											<tr>
												<td class="fontnormal" halign="center">
													<html-el:radio property="searchString" value="1"/>
													<mifos:mifoslabel name="moratorium.applyForOffice" bundle="moratoriumUIResources" />
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td>
										<table width="100%">	
											<tr>
												<td class="fontnormal" halign="center">
													<html-el:radio property="searchString" value="2"/>
													<mifos:mifoslabel name="moratorium.applyForClient" bundle="moratoriumUIResources" />
												</td>
											</tr>
										</table>
									</td>
								</tr>								
							</table>
							<br>							
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center"><html-el:submit styleClass="buttn"
										style="width:70px">
										<mifos:mifoslabel name="moratorium.preview"
											bundle="moratoriumUIResources" />
									</html-el:submit> &nbsp; <html-el:button property="cancel"
										styleClass="cancelbuttn" style="width:70px"
										onclick="javascript:fnCancel(this.form)">
										<mifos:mifoslabel name="moratorium.cancel"
											bundle="moratoriumUIResources" />
									</html-el:button></td>
								</tr>
							</table>
							<br>
							</td>
						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
