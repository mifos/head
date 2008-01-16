<!--

/**

 * birtAdminDocumentUpload.jsp    version: 1.0



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

<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".create">
  <tiles:put name="body" type="string">
  
  	<script type="text/javascript">
	function goToCancelPage(form){
		form.action = "AdminAction.do?method=load";
		form.submit();
  	}
  	
  				
	function populateParent(selectBox){
		if(selectBox.selectedIndex > 0)
		{
		  document.birtAdminDocumentUploadActionForm.action="birtAdminDocumentUploadAction.do?method=loadProductInstance";
		  birtAdminDocumentUploadActionForm.submit();
		}
	 }
	 
			 
    </script>
		<html-el:form method="post"
			action="/birtAdminDocumentUploadAction.do?method=preview"
			 enctype="multipart/form-data">
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
									<td width="33%">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/bigarrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorange"><mifos:mifoslabel
												name="reports.documentInformation"/>
												</td>
										</tr>
									</table>
									</td>
									<td width="33%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorangelight"><mifos:mifoslabel
												name="reports.review&submit" /></td>
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
											<mifos:mifoslabel name="reports.uploadAdministrativeDocument" /> -
										</span>  
									<mifos:mifoslabel
												name="reports.enterDocumentInformation" /></td>
								</tr>
								<tr>
									<td class="fontnormalbold"><span class="fontnormal">
									    <mifos:mifoslabel name="reports.statement" />
										<font color="#ff0000">*</font>
										<mifos:mifoslabel name="reports.markedFieldStatement" />
										</span>
									</td>
								</tr>
								<tr>
                                    <td class="fontnormalbold"/>
                                </tr>
								<tr>
								    <td class="fontnormalbold"> 
								       <mifos:mifoslabel name="reports.administrativeDocumentDetails" />
								    </td>
								</tr>
								<tr>
									<td>
										<font class="fontnormalRedBold">
											<html-el:errors	bundle="reportsUIResources" />	
										</font>
									</td>
								</tr>
							</table>
							<br>
							<table width="90%" border="0" cellspacing="0" cellpadding="3">
								<tr class="fontnormal">
									<td align="right">
									    <mifos:mifoslabel
										name="reports.admindocumenttitle" mandatory="yes" />:</td>
									<td><html-el:text styleId="111"
										property="adminiDocumentTitle" maxlength="200"/> </td>
								</tr>
							    <tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="reports.accountType" mandatory="yes"/>
									</td>
									<td>
										<mifos:select property="accountTypeId"
											onchange="return populateParent(this)" >
												<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'ProductTypeList')}" var="producttype">
													<html-el:option value="${producttype.productTypeID}">${producttype.name}</html-el:option>
												</c:forEach>
										</mifos:select>
									</td>
								</tr>
										
									<tr class="fontnormal">
										<td align="right" valign="top">
											<mifos:mifoslabel name="reports.ShowWhenStatus"  mandatory="yes"/>
											:
										</td>
										<td valign="top">
											<table width="80%" border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td>
														<img src="pages/framework/images/trans.gif" width="1" height="1">
													</td>
												</tr>
											</table>
	
											<c:set var="SelectedAccountStatus" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'SelectedAccountStatus')}" />
											<c:set var="NotSelectedAccountStatus" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'NotSelectedAccountStatus')}" />
											<mifos:MifosSelect  property="statusList" input="SelectedAccountStatus" output="NotSelectedAccountStatus" property1="id" property2="description" multiple="true">
											</mifos:MifosSelect>
										</td>
									</tr>
								
	
							<tr class="fontnormal">
								    <td align="right">
									   <mifos:mifoslabel
										name="reports.labelAdministrativeDocument" mandatory="yes"/>:</td>
								    <td>
                                        <input type="file" value="" name="file"/>
                                    </td>
								</tr>
								<input type="hidden" name="isActive"  value="1" />
						  </table><br>	
						 <table width="90%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center" class="blueline">
												&nbsp;
											</td>
										</tr>
						  </table><br>	
						<table width="90%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center"><html-el:submit styleClass="buttn"
										style="width:70px;" onclick="transferData(this.form.statusList);">
										<mifos:mifoslabel name="reports.preview"></mifos:mifoslabel>
									</html-el:submit> &nbsp; <html-el:button
										onclick="goToCancelPage(this.form);" property="cancelButton"
										value="Cancel" styleClass="cancelbuttn" style="width:70px">
										<mifos:mifoslabel name="reports.cancel"/>
									</html-el:button></td>
								</tr>
						</table><br>
							</td>
						</tr>
					</table><br>
					</td>
				</tr>
			</table>
			<html:hidden property="method" value="search" />
			
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
