<!-- 

/**

 * configureClientMoratorium.jsp    version: 1.0

 

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
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>

<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
		<script language="javascript">
		<!--
			function fnCancel(form) {
				form.action="AdminAction.do?method=load";
				form.submit();
			}
		//-->
		</script>
		<html-el:form method="post"
			action="/custSearchAction.do?method=search">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="470" align="left" valign="top" bgcolor="#FFFFFF">
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
									<td width="25%">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/bigarrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorange"><mifos:mifoslabel
												name="moratorium.categoryinfo" bundle="moratoriumUIResources" /></td>
										</tr>
									</table>
									</td>									
									<td width="25%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorangelight"><mifos:mifoslabel
												name="moratorium.info" bundle="moratoriumUIResources" /></td>
										</tr>
									</table>
									</td>
									<td width="25%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17" height="17"></td>
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
							<td width="70%" height="24" align="left" valign="top"
								class="paddingleftCreates">
							<table width="98%" border="0" cellspacing="0" cellpadding="3">                                
                                <tr>
									<td class="headingorange">								
										<span class="heading"> 
											<mifos:mifoslabel name="moratorium.define" /> -
										</span>
									<mifos:mifoslabel name="moratorium.categoryinfo" /></td>
								</tr>
								<tr>
									<td class="fontnormalbold">
										<span class="fontnormal">
											<mifos:mifoslabel name="moratorium.enter_search_criteria" />
										</span>
									</td>
								</tr>
								<tr>
									<td>
										<font class="fontnormalRedBold">
											<html-el:errors bundle="CustomerSearchUIResources" /> 
										</font>
									</td>
								</tr>
							</table>
							<br>
							<table width="90%" border="0" cellspacing="0" cellpadding="3">
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="moratorium.client_type" />:</td>
									<td><html-el:text styleId="111"
										property="searchString" maxlength="200"/> </td>
								</tr>
								<tr class="fontnormal">
									<td width="30%">&nbsp;</td>
									<td width="70%"><br>
									<html-el:submit property="searchButton" styleClass="buttn"
										style="width:70px;">
										<mifos:mifoslabel name="accounts.search" />
									</html-el:submit>&nbsp;
									<html-el:button property="cancelButton"
											onclick="javascript:fnCancel(this.form)"
											styleClass="cancelbuttn" style="width:70px;">
											<mifos:mifoslabel name="accounts.cancel" />
										</html-el:button>
									</td>
								</tr>
							</table><br>
							</td>
						</tr>
					</table><br>
					</td>
				</tr>
			</table>
			<html:hidden property="method" value="search" />
			<html:hidden property="perspective" value="${custSearchActionForm.perspective}"/>
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<html:hidden property="input" value="moratorium" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
