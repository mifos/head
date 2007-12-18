<!-- 

/**

 * applyMoratoriumConfirm.jsp    version: 1.0

 

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
<%@ taglib uri="/tags/date" prefix="date"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
		<script>
				function fun_cancel(form) {
				form.action="AdminAction.do?method=load";
				form.submit();
			}
			
			function goToEditPage(form){
			form.action="moratoriumAction.do?method=previous";
			form.submit();
		  }
		</script>

		<html-el:form method="post" action="/moratoriumAction.do?method=createConfirmed">
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
			<html-el:hidden property="customerId" value="${BusinessKey.customerId}" />
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
													<td><img src="pages/framework/images/timeline/tick.gif"
														width="17" height="17"></td>
													<td class="timelineboldgray"><mifos:mifoslabel
														name="moratorium.categoryinfo" bundle="moratoriumUIResources" /></td>
												</tr>
											</table>
											</td>
											<td width="25%" align="center">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td><img
														src="pages/framework/images/timeline/tick.gif"
														width="17" height="17"></td>
													<td class="timelineboldgray"><mifos:mifoslabel
												name="moratorium.info" bundle="moratoriumUIResources" /></td>
												</tr>
											</table>
											</td>											
											<td width="25%" align="right">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td><img
														src="pages/framework/images/timeline/bigarrow.gif"
														width="17" height="17"></td>
													<td class="timelineboldorange"><mifos:mifoslabel
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
									<mifos:mifoslabel name="moratorium.reviewAndSubmit" /></td>
								</tr>
								<tr>
									<td>
										<span class="fontnormal">
											<mifos:mifoslabel name="moratorium.confirmDetailInfo" />											
										</span>
									</td>
								</tr>
								<tr>
									<td>
										<span class="fontnormalbold">
											<mifos:mifoslabel name="moratorium.clientName" isColonRequired="Yes"/>
										</span>&nbsp;
										<span class="fontnormal">
											<c:out value="${BusinessKey.displayName}"/>
										</span>
									</td>
								</tr>								
							</table>
							<br>
							<table width="96%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td>
										<span class="fontnormalbold">
											<mifos:mifoslabel isColonRequired="Yes" name="moratorium.StartDate" bundle="moratoriumUIResources"/>
										</span>&nbsp;
										<span class="fontnormal">
										<c:out value="${sessionScope.moratoriumActionForm.moratoriumFromDateDD}"/>/<c:out value="${sessionScope.moratoriumActionForm.moratoriumFromDateMM}"/>/<c:out value="${sessionScope.moratoriumActionForm.moratoriumFromDateYY}"/>
										</span>
									</td>									
								</tr>
								<tr>
									<td>
										<span class="fontnormalbold">
											<mifos:mifoslabel isColonRequired="Yes" name="moratorium.EndDate" bundle="moratoriumUIResources"/>
										</span>&nbsp;
										<span class="fontnormal">
										<c:out value="${sessionScope.moratoriumActionForm.moratoriumEndDateDD}"/>/<c:out value="${sessionScope.moratoriumActionForm.moratoriumEndDateMM}"/>/<c:out value="${sessionScope.moratoriumActionForm.moratoriumEndDateYY}"/>
										</span>
									</td>									
								</tr>
								<tr>
									<td>
										<span class="fontnormalbold">
											<mifos:mifoslabel isColonRequired="Yes" name="moratorium.notes" bundle="moratoriumUIResources"/>
										</span>
									</td>									
								</tr>
								<tr>
									<td class="fontnormal">
										<span class="fontnormal">
											<c:out value="${sessionScope.moratoriumActionForm.moratoriumNotes}"/>
										</span>
									</td>
								</tr>
								<tr></tr><tr></tr>
								<tr></tr><tr></tr>
								<tr></tr><tr></tr>
								<tr>
									<td align="left" valign="top">
					                    <html-el:button property="btn" style="width:200px;" styleClass="insidebuttn" onclick="goToEditPage(this.form);">
					                    	<mifos:mifoslabel name="moratorium.editLabel" bundle="moratoriumUIResources"></mifos:mifoslabel>
					                    </html-el:button>
				                    </td>
				                </tr>
							</table><br>
							<table width="96%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td align="center">
										<html-el:submit styleClass="buttn" style="width:70px;">
											<mifos:mifoslabel name="moratorium.submit" />
										</html-el:submit>
									    &nbsp;&nbsp;
										<html-el:button property="cancelButton" styleClass="cancelbuttn" style="width:70px;" onclick="fun_cancel(this.form);">
											<mifos:mifoslabel name="accounts.cancel" />
										</html-el:button>
									</td>
								</tr>
							</table>
							<br>
					</table>
					</td>
				</tr>
			</table>			
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />			
		</html-el:form>
	</tiles:put>
</tiles:insert>
