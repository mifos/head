<!--

/**

 * define_additional_fields.jsp    version: 1.0



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


<tiles:insert definition=".create">
	<tiles:put name="body" type="string">
		<html-el:form action="/customFieldsAction.do?method=preview" focus="categoryType">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="450" align="left" valign="top" bgcolor="#FFFFFF">

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
									<td width="27%">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/bigarrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorange"><mifos:mifoslabel
												name="configuration.additional_fields_information" /></td>
										</tr>
									</table>
									</td>
									<td width="73%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorangelight"><mifos:mifoslabel
												name="configuration.review"  /> &amp; <mifos:mifoslabel
												name="configuration.submit"  /></td>
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
									<td class="headingorange"><span class="heading"> <mifos:mifoslabel
										name="configuration.define_additional_fields"  /> - </span> <mifos:mifoslabel
										name="configuration.enter_additional_fields_information"  /></td>
								</tr>
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="configuration.instruction1"  />
									<mifos:mifoslabel name="configuration.instruction2"
										mandatory="yes" /></td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<font class="fontnormalRedBold"> <html-el:errors
										bundle="configurationUIResources" /> </font>
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="configuration.additional_fields_detail" /> <br>
									<br>
									</td>
								</tr>
								<tr class="fontnormal">
									<td width="27%" align="right"><mifos:mifoslabel
										name="configuration.category" mandatory="yes"
										/>:</td>
									<td valign="top"><mifos:select property="categoryType"
										style="width:136px;">
										<c:forEach
											items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'allCategories')}"
											var="category">
											<html-el:option value="${category.id}">${category.name}</html-el:option>
										</c:forEach>
									</mifos:select></td>
								</tr>
								<tr class="fontnormal">
									<td width="27%" align="right"><mifos:mifoslabel
										name="configuration.label" mandatory="yes" />:</td>
									<td width="73%" valign="top"><mifos:mifosalphanumtext
										property="labelName" maxlength="200" /></td>
								</tr>
								<tr class="fontnormal">
                  					<td align="right"><mifos:mifoslabel
										name="configuration.mandatory"  />:</td>
                  					<td><html-el:checkbox property="mandatoryField"  value="1" /> </td>
                				</tr>
								<tr class="fontnormal">
									<td align="right" valign="top"><mifos:mifoslabel
										name="configuration.data_type" mandatory="yes" />:</td>
									<td valign="top"><mifos:select property="dataType"
										style="width:136px;">
										<c:forEach
											items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'allDataTypes')}"
											var="oneDataType">
											<html-el:option value="${oneDataType.id}">${oneDataType.name}</html-el:option>
										</c:forEach>
									</mifos:select></td>
								</tr>
								<tr class="fontnormal">
									<td width="27%" align="right"><mifos:mifoslabel
										name="configuration.default_value" />:</td>
									<td width="73%" valign="top"><mifos:mifosalphanumtext
										property="defaultValue" maxlength="200" /></td>
								</tr>
							</table>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center" class="blueline">&nbsp;</td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center"><html-el:submit style="width:65px;"
										property="btnPreview" styleClass="buttn" >
										<mifos:mifoslabel name="configuration.preview" />
									</html-el:submit>&nbsp; <html-el:button property="cancelButton" style="width:65px;"
										styleClass="cancelbuttn"
										onclick="location.href='customFieldsAction.do?method=cancel&currentFlowKey=${requestScope.currentFlowKey}'">
										<mifos:mifoslabel name="configuration.cancel"  />
									</html-el:button></td>
								</tr>
							</table>
							<br>
						</tr>
					</table>
					<br>
					<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
					<html-el:hidden property="method" value="preview" />
					</html-el:form> 
			</tiles:put> 
		</tiles:insert>
