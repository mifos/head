<!--

/**

 * admin.jsp    version: 1.0



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
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<html-el:form action="AdminAction.do?method=load">
			<td align="left" valign="top" bgcolor="#FFFFFF"
				style="padding-left:8px; padding-top:10px;">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">

				<tr>
					<td colspan="2" align="left" valign="top"><span
						class="headingorange"><mifos:mifoslabel name="admin.admintasks" /></span><br>
					<span class="fontnormal"><mifos:mifoslabel name="admin.welcometomifos" /></span></td>
				</tr>
				<tr width="100%">
					<td align="left" valign="top" colspan="2"><font class="fontnormalRedBold"><html-el:errors
						bundle="adminUIResources" /> </font></td>
				</tr>

				<tr>
					<td width="48%" height="240" align="left" valign="top"
						class="paddingleft"><span class="headingorange"><mifos:mifoslabel name="admin.manageorg" />
						</span><br>
					<span class="fontnormalbold"> <span class="fontnormalbold"><mifos:mifoslabel name="admin.sysusers" />
					</span><br>
					</span>
					<table width="90%" border="0" cellspacing="0" cellpadding="0">
						<tr class="fontnormal">
							<td width="3%"><img
								src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td width="97%"><a
								href="PersonAction.do?method=loadSearch&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.viewsysusers" /></a> | <a
								href="PersonAction.do?method=chooseOffice&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.definenewusers" /></a></td>
						</tr>
						<tr class="fontnormal">
							<td><img src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td><html-el:link
								action="/rolesPermission.do?method=viewRoles&name=aa&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.manageroles&per" /></html-el:link></td>
						</tr>
					</table>
					<br>
					<span class="fontnormalbold"> Offices</span><br>
					<table width="90%" border="0" cellspacing="0" cellpadding="0">
						<tr class="fontnormal">
							<td width="3%"><img
								src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td width="97%"><html-el:link
								action="/offAction.do?method=getAllOffices&randomNUm=${sessionScope.randomNUm}"><mifos:mifoslabel name="admin.viewoff" /></html-el:link>
							| <html-el:link action="/offAction.do?method=load&randomNUm=${sessionScope.randomNUm}"><mifos:mifoslabel name="admin.defnewoff" /></html-el:link></td>
						</tr>
						<tr class="fontnormal">
							<td><img src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td><html-el:link
								action="/offhierarchyaction.do?method=load&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.viewoffhier" /></html-el:link></td>
						</tr>
					</table>
					<br>
					<span class="fontnormalbold"><mifos:mifoslabel name="admin.orgpref" /> </span><br>
					<table width="90%" border="0" cellspacing="0" cellpadding="0">
						<tr class="fontnormal">
							<td width="3%"><img
								src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td width="97%"><a
								href="feeaction.do?method=viewAll&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.viewfees" /></a> | <html-el:link
								action="feeaction.do?method=load&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}" >
								<mifos:mifoslabel name="admin.defnewfees" /></html-el:link></td>
						</tr>
						<tr class="fontnormal">
							<td><img src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td><html-el:link
								action="/fundAction.do?method=viewAllFunds&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.viewfunds" /></html-el:link>
							| <html-el:link
								action="/fundAction.do?method=load&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.defnewfund" /></html-el:link></td>
						</tr>
						<tr class="fontnormal">
							<td><img src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td><html-el:link
								action="chkListAction.do?method=loadAllChecklist&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.viewcheklists" /></html-el:link>
							| <html-el:link
								action="chkListAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.defnewchecklist" /></html-el:link></td>
						</tr>
						<tr class="fontnormal">
							<td><img src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td><html-el:link
								action="holidayAction.do?method=get">
								<mifos:mifoslabel name="admin.viewholidays" /></html-el:link>
							| <html-el:link
								action="holidayAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.defnewholiday" /></html-el:link></td>
						</tr>
						<tr class="fontnormal">
							<td><img src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td><html-el:link
								action="ppiAction.do?method=get">
								<mifos:mifoslabel name="admin.viewPPI" /></html-el:link>
							| <html-el:link
								action="ppiAction.do?method=configure">
								<mifos:mifoslabel name="admin.configurePPI" /></html-el:link></td>
						</tr>

					</table>
					 <br>
					<span class="fontnormalbold"><mifos:mifoslabel name="admin.datadisplayrules" /></span><br>
						<table width="90%" border="0" cellspacing="0" cellpadding="0">
							<tr class="fontnormal">
								<td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
								<td width="97%"><html-el:link
								action="labelconfigurationaction.do?method=load&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.definelabels" /></html-el:link></td>
							</tr>
							<tr class="fontnormal">
								<td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
								<td width="97%"><html-el:link
								action="lookupOptionsAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.definelookupoption" /></html-el:link></td>
							</tr>
							<tr class="fontnormal">
								<td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
								<td width="97%"><html-el:link
								action="hiddenmandatoryconfigurationaction.do?method=load&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.definehiddenmandfields" /></html-el:link></td>
							</tr>
						</table>

					<table width="90%" border="0" cellspacing="0" cellpadding="0">


					</table>
					</td>
					<td width="52%" align="left" valign="top" class="paddingleft"><span
						class="headingorange"><mifos:mifoslabel name="admin.manageprd" /></span><br>
					<span class="fontnormalbold"><mifos:mifoslabel name="admin.prdrules" /></span><br>
					<table width="90%" border="0" cellspacing="0" cellpadding="0">
						<tr class="fontnormal">
							<td width="3%"><img
								src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td width="97%"><html-el:link
								href="productCategoryAction.do?method=getAllCategories&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.viewprdcat" /></html-el:link> | <html-el:link
								href="productCategoryAction.do?method=load&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.defnewcat" /></html-el:link></td>
						</tr>
						<tr class="fontnormal">
							<td><img src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td><html-el:link
								href="prdconfaction.do?method=load&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.viewlate/dordef" /></html-el:link></td>
						</tr>
					</table>
					<br>
					<!-- Manage products mix  -->
					<span class="fontnormalbold">
					<mifos:mifoslabel name="admin.Manage" />
					<mifos:mifoslabel name="admin.product" />
					<mifos:mifoslabel name="admin.mix" />
					</span><br>
					<table width="90%" border="0" cellspacing="0" cellpadding="0">
						<tr class="fontnormal">
							<td width="3%"><img
								src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td width="97%"><html-el:link
								href="productMixAction.do?method=viewAllProductMix&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.View" />
								<mifos:mifoslabel name="admin.products" />
								<mifos:mifoslabel name="admin.mix" />
								</html-el:link> | <html-el:link
								href="productMixAction.do?method=load&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.define" />
								<mifos:mifoslabel name="admin.products"/>
								<mifos:mifoslabel name="admin.mix"/>
								</html-el:link></td>
						</tr>
					</table>
					<br>
					
					<!--  -->
					<span class="fontnormalbold">
					<mifos:mifoslabel name="admin.Manage" />

					<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
					<mifos:mifoslabel name="admin.products" />
					</span><br>
					<table width="90%" border="0" cellspacing="0" cellpadding="0">
						<tr class="fontnormal">
							<td width="3%"><img
								src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<!-- Bug id 28065  Added a  parameter input in the link of admin page.-->
							<td width="97%"><html-el:link
								href="loanproductaction.do?method=viewAllLoanProducts&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.View" />
								<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
								<mifos:mifoslabel name="admin.products" />
								</html-el:link> | <html-el:link
								href="loanproductaction.do?method=load&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.definenew" />
								<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
								<mifos:mifoslabel name="admin.product" />
								</html-el:link></td>
						</tr>
					</table>
					<br>
					<span class="fontnormalbold">
					<mifos:mifoslabel name="admin.Manage" />
					<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" />
					<mifos:mifoslabel name="admin.products" />

					</span><br>
					<table width="90%" border="0" cellspacing="0" cellpadding="0">
						<tr class="fontnormal">
							<td width="3%"><img
								src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<!-- Bug id 28065  Added a  parameter input in the link of admin page.-->
							<td width="97%"><html-el:link
								href="savingsproductaction.do?method=search&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.View" />
								<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" />
								<mifos:mifoslabel name="admin.products" />
									</html-el:link> | <html-el:link
								href="savingsproductaction.do?method=load&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.definenew" />
								<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" />
								<mifos:mifoslabel name="admin.product" /></html-el:link></td>
						</tr>
					</table>
					<br>
					<span class="headingorange"><mifos:mifoslabel name="admin.Manage" /> <mifos:mifoslabel name="admin.accounts" /></span><br>
                <span class="fontnormalbold"><mifos:mifoslabel name="admin.Manage" /> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel name="admin.accounts" /></span><br>
                <table width="90%" border="0" cellspacing="0" cellpadding="0">
                  <tr class="fontnormal">
                    <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                    <td width="97%"><html-el:link
								href="reverseloandisbaction.do?method=search&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}"><mifos:mifoslabel name="admin.reverse" /> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel name="admin.disbursal" /></html-el:link></td>

                  </tr>
                </table>
                
                <br />
				   <span class="headingorange"><mifos:mifoslabel name="admin.Manage" /> <mifos:mifoslabel name="admin.reports" /></span><br />
                   <table width="90%" border="0" cellspacing="0" cellpadding="0">
                   <tr class="fontnormal">
                   <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                   <td width="97%"><a
								href="birtReportsUploadAction.do?method=getViewReportPage"><mifos:mifoslabel name="admin.View" /> <mifos:mifoslabel name="admin.reports" /> <mifos:mifoslabel name="admin.templates" />
								</a> | <html-el:link
								href="birtReportsUploadAction.do?method=getBirtReportsUploadPage&viewPath=administerreports_path"><mifos:mifoslabel name="admin.uploadReportTemplate" /></html-el:link></td>
                  </tr>
                </table>
                <br>

		<span class="headingorange"><mifos:mifoslabel name="admin.Manage" /> <mifos:mifoslabel name="admin.surveys" /></span><br />
                  <table width="90%" border="0" cellspacing="0" cellpadding="0">
                    <tr class="fontnormal">
                      <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                      <td width="97%"><a href="surveysAction.do?method=mainpage"><mifos:mifoslabel name="admin.View" /> <mifos:mifoslabel name="admin.surveys" /></a> | <html-el:link href="surveysAction.do?method=create_entry"><mifos:mifoslabel name="admin.definenew" /> <mifos:mifoslabel name="admin.survey" /></html-el:link></td>
                    </tr>
                    <tr class="fontnormal">
                      <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                      <td width="97%"><a href="questionsAction.do?method=viewQuestions"><mifos:mifoslabel name="admin.viewquestions" /> </a> | <html-el:link href="questionsAction.do?method=defineQuestions"><mifos:mifoslabel name="admin.define" /> <mifos:mifoslabel name="admin.questions" /></html-el:link></td>
                  </tr>

                </table>
			</td>
		</html-el:form>
	</tiles:put>
</tiles:insert>
