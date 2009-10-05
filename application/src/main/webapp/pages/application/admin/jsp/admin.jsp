<%-- 
Copyright (c) 2005-2008 Grameen Foundation USA
1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
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
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>

<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.adminUIResources"/>

<!--  <%
java.util.Enumeration enn = session.getAttributeNames();

while(enn.hasMoreElements()){
	String s = enn.nextElement().toString();
	out.println(s + " " + session.getAttribute(s).toString());
}

%> -->

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
        <span id="page.id" title="admin" />
		<html-el:form action="AdminAction.do?method=load">
			<td align="left" valign="top" bgcolor="#FFFFFF"
				style="padding-left:8px; padding-top:10px;">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">

				<tr>
					<td colspan="2" align="left" valign="top"><span id="admin.label.admintasks"
						class="headingorange"><mifos:mifoslabel name="admin.admintasks"  /></span><br>
					<span class="fontnormal"><span id="admin.text.welcome"><mifos:mifoslabel name="admin.welcometomifos" /></span></span></td>
				</tr>
				<tr width="100%">
					<td align="left" valign="top" colspan="2"><font class="fontnormalRedBold"><span id="admin.error.message"><html-el:errors
						bundle="adminUIResources" /></span> </font></td>
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
							<td width="97%"><a id="admin.link.viewSysUsers"
								href="PersonAction.do?method=loadSearch&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.viewsysusers" /></a> | <a id="admin.link.defineNewUsers"
								href="PersonAction.do?method=chooseOffice&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.definenewusers" /></a></td>
						</tr>
						<tr class="fontnormal">
							<td><img src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td><html-el:link styleId="admin.link.manageRoles"
								action="/rolesPermission.do?method=viewRoles&name=aa&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.manageroles&per" /></html-el:link></td>
						</tr>
					</table>
					<br>
					<span class="fontnormalbold"> <mifos:mifoslabel name="admin.office" bundle="adminUIResources" /></span><br>
					<table width="90%" border="0" cellspacing="0" cellpadding="0">
						<tr class="fontnormal">
							<td width="3%"><img
								src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td width="97%"><html-el:link styleId="admin.link.viewOffices"
								action="/offAction.do?method=getAllOffices&randomNUm=${sessionScope.randomNUm}"><mifos:mifoslabel name="admin.viewoff" /></html-el:link>
							| <html-el:link styleId="admin.link.defineNewOffice" action="/offAction.do?method=load&randomNUm=${sessionScope.randomNUm}"><mifos:mifoslabel name="admin.defnewoff" /></html-el:link></td>
						</tr>
						<tr class="fontnormal">
							<td><img src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td><html-el:link styleId="admin.link.viewOfficeHierarchy"
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
							<td width="97%"><a id="admin.link.viewFees"
								href="feeaction.do?method=viewAll&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.viewfees" /></a> | <html-el:link styleId="admin.link.defineNewFees"
								action="feeaction.do?method=load&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}" >
								<mifos:mifoslabel name="admin.defnewfees" /></html-el:link></td>
						</tr>
						<tr class="fontnormal">
							<td><img src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td><html-el:link styleId="admin.link.viewFunds"
								action="/fundAction.do?method=viewAllFunds&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.viewfunds" /></html-el:link>
							| <html-el:link styleId="admin.link.defineNewFund"
								action="/fundAction.do?method=load&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.defnewfund" /></html-el:link></td>
						</tr>
						<tr class="fontnormal">
							<td><img src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td><html-el:link styleId="admin.link.viewChecklists"
								action="chkListAction.do?method=loadAllChecklist&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.viewcheklists" /></html-el:link>
							| <html-el:link styleId="admin.link.defineNewChecklist"
								action="chkListAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.defnewchecklist" /></html-el:link></td>
						</tr>
						<tr class="fontnormal">
							<td><img src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td><html-el:link styleId="admin.link.viewHolidays"
								action="holidayAction.do?method=get">
								<mifos:mifoslabel name="admin.viewholidays" /></html-el:link>
							| <html-el:link styleId="admin.link.defineNewHoliday"
								action="holidayAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.defnewholiday" /></html-el:link></td>
						</tr>
						<tr class="fontnormal">
							<td><img src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td>
							 <html-el:link styleId="admin.link.defineAcceptedPaymentType"
								action="acceptedPaymentTypeAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.defineacceptedpaymenttype" /></html-el:link></td>
						</tr>
						<tr class="fontnormal">
							<td><img src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td>
							 <html-el:link styleId="admin.link.viewOrganizationSettings"
								action="viewOrganizationSettingsAction.do?method=get&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.vieworganizationsettings" /></html-el:link></td>
						</tr>
						<tr class="fontnormal">
							<td><img src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td><html-el:link styleId="admin.link.viewPPI"
								action="ppiAction.do?method=get">
								<mifos:mifoslabel name="admin.viewPPI" /></html-el:link>
							| <html-el:link styleId="admin.link.configurePPI"
								action="ppiAction.do?method=configure">
								<mifos:mifoslabel name="admin.configurePPI" /></html-el:link></td>
						</tr>
					</table>
					 <br>
					<span class="fontnormalbold"><mifos:mifoslabel name="admin.datadisplayrules" /></span><br>
						<table width="90%" border="0" cellspacing="0" cellpadding="0">
							<tr class="fontnormal">
								<td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
								<td width="97%"><html-el:link styleId="admin.link.defineLabels"
								action="labelconfigurationaction.do?method=load&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.definelabels" /></html-el:link></td>
							</tr>
							<tr class="fontnormal">
								<td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
								<td width="97%"><html-el:link styleId="admin.link.defineLookupOption"
								action="lookupOptionsAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.definelookupoption" /></html-el:link></td>
							</tr>
							<tr class="fontnormal">
								<td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
								<td width="97%"><html-el:link styleId="admin.link.defineHiddenMandatoryFields"
								action="hiddenmandatoryconfigurationaction.do?method=load&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.definehiddenmandfields" /></html-el:link></td>
							</tr>
							<tr class="fontnormal">
								<td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
								<td width="97%"><html-el:link styleId="admin.link.viewAdditionalFields"
								action="customFieldsAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.view_additional_fields" /></html-el:link></a> | <html-el:link
								styleId="admin.link.defineAdditionalFields"
								action="customFieldsAction.do?method=loadDefineCustomFields&randomNUm=${sessionScope.randomNUm}" >
								<mifos:mifoslabel name="admin.defineadditionalfields" /></html-el:link></td>
							</tr>
						</table>

					<!-- spacer -->
					<table width="90%" border="0" cellspacing="0" cellpadding="0">
						<tr><td>&nbsp;</td></tr>
					</table>
					<table width="90%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td>
							<span class="headingorange"><mifos:mifoslabel name="admin.importexport.title" bundle="adminUIResources" /></span>
							<table width="90%" border="0" cellspacing="0" cellpadding="0">
								<tr class="fontnormal">
									<td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
									<td width="97%">
										<a id="admin.link.viewSystemInfo" href="manageImportAction.do?method=load">
										<mifos:mifoslabel name="admin.importexport.importtransactions" bundle="adminUIResources" /></a></td>
								</tr>
							</table>
						</td>
					</tr>
					</table>
					<table width="90%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td>
							<span class="headingorange"><mifos:mifoslabel name="admin.sysinfo.title" bundle="adminUIResources" /></span>
							<table width="90%" border="0" cellspacing="0" cellpadding="0">
								<tr class="fontnormal">
									<td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
									<td width="97%">
										<a id="admin.link.viewSystemInfo" href="systemInfoAction.do?method=load">
										<mifos:mifoslabel name="admin.View" /> <mifos:mifoslabel name="admin.sysinfo.title" bundle="adminUIResources" /></a></td>
								</tr>
							</table>
						</td>
					</tr>
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
							<td width="97%"><html-el:link styleId="admin.link.viewProductCategories"
								href="productCategoryAction.do?method=getAllCategories&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.viewprdcat" /></html-el:link> | <html-el:link styleId="admin.link.defineNewProductCategory"
								href="productCategoryAction.do?method=load&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.defnewcat" /></html-el:link></td>
						</tr>
						<tr class="fontnormal">
							<td><img src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td><html-el:link styleId="admin.link.viewLatenessDormancyDefinition"
								href="prdconfaction.do?method=load&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.viewlate/dordef" /></html-el:link></td>
						</tr>
					</table>
					<br>
					<!-- Manage products mix  -->
					<span class="fontnormalbold">
					<mifos:mifoslabel name="admin.manageProductMix" />
					</span><br>
					<table width="90%" border="0" cellspacing="0" cellpadding="0">
						<tr class="fontnormal">
							<td width="3%"><img
								src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td width="97%"><html-el:link styleId="admin.link.viewProductsMix"
								href="productMixAction.do?method=viewAllProductMix&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.viewProductsMix" />
								</html-el:link> | <html-el:link styleId="admin.link.defineProductsMix"
								href="productMixAction.do?method=load&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.defineProductsMix"/>
								</html-el:link></td>
						</tr>
					</table>
					<br>
					
					<!--  -->
					<span class="fontnormalbold">
					<fmt:message key="admin.manageProducts">
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /></fmt:param>
					</fmt:message>
					</span><br>
					<table width="90%" border="0" cellspacing="0" cellpadding="0">
						<tr class="fontnormal">
							<td width="3%"><img
								src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<!-- Bug id 28065  Added a  parameter input in the link of admin page.-->
							<td width="97%"><html-el:link styleId="admin.link.viewLoanProducts"
								href="loanproductaction.do?method=viewAllLoanProducts&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<fmt:message key="admin.viewLoanProducts">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /></fmt:param>
								</fmt:message>
								</html-el:link> | <html-el:link styleId="admin.link.defineNewLoanProduct"
								href="loanproductaction.do?method=load&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<fmt:message key="admin.defineNewLoanProduct">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /></fmt:param>
								</fmt:message>
								</html-el:link></td>
						</tr>
					</table>
					<br>
					<span class="fontnormalbold">
					<fmt:message key="admin.manageProducts">
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" /></fmt:param>
					</fmt:message>

					</span><br>
					<table width="90%" border="0" cellspacing="0" cellpadding="0">
						<tr class="fontnormal">
							<td width="3%"><img
								src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<!-- Bug id 28065  Added a  parameter input in the link of admin page.-->
							<td width="97%"><html-el:link styleId="admin.link.viewSavingsProducts"
								href="savingsproductaction.do?method=search&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<fmt:message key="admin.viewSavingsProducts">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" /></fmt:param>
								</fmt:message>
									</html-el:link> | <html-el:link styleId="admin.link.defineNewSavingsProduct"
								href="savingsproductaction.do?method=load&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<fmt:message key="admin.defineNewSavingsProduct">
									<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}" /></fmt:param>
								</fmt:message>
								</html-el:link></td>
						</tr>
					</table>
					<br>
					<span class="headingorange"><mifos:mifoslabel name="admin.Manage" /> <mifos:mifoslabel name="admin.accounts" /></span><br>
                <span class="fontnormalbold"><mifos:mifoslabel name="admin.Manage" /> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel name="admin.accounts" /></span><br>
                <table width="90%" border="0" cellspacing="0" cellpadding="0">
                  <tr class="fontnormal">
                    <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                    <td width="97%"><html-el:link styleId="admin.link.reverseLoanDisbursal"
								href="reverseloandisbaction.do?method=search&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<fmt:message key="admin.reverseLoanDisbursal">
								<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /></fmt:param>
								</fmt:message></html-el:link></td>

                  </tr>
                  <tr class="fontnormal">
                    <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                    <td width="97%"><html-el:link styleId="admin.link.redoLoanDisbursal"
								href="loanAccountAction.do?method=redoLoanBegin&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<fmt:message key="admin.redoLoanDisbursal">
								<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /></fmt:param>
								</fmt:message></html-el:link></td>

                  </tr>
                </table>
                
                <br />
				   <span class="headingorange"><mifos:mifoslabel name="admin.Manage" /> <mifos:mifoslabel name="admin.reports" /></span><br />
                   <table width="90%" border="0" cellspacing="0" cellpadding="0">
                   <tr class="fontnormal">
                   <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                   <td width="97%"><a id="admin.link.ViewAdminDocuments"
								href="birtAdminDocumentUploadAction.do?method=getViewBirtAdminDocumentPage"><mifos:mifoslabel name="admin.ViewAdminDocuments" />
								</a> | <html-el:link styleId="admin.link.uploadAdminDocuments"
								href="birtAdminDocumentUploadAction.do?method=getBirtAdminDocumentUploadPage&viewPath=administerreports_path"><mifos:mifoslabel name="admin.UploadAdminDocuments" /></html-el:link></td>
                  </tr>
                  
                  <tr class="fontnormal">
                   <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                   <td width="97%"><a id="admin.link.ViewReportsTemplates"
								href="birtReportsUploadAction.do?method=getViewReportPage"><mifos:mifoslabel name="admin.ViewReportsTemplates" />
								</a> | <html-el:link styleId="admin.link.uploadReportTemplate"
								href="birtReportsUploadAction.do?method=getBirtReportsUploadPage&viewPath=administerreports_path"><mifos:mifoslabel name="admin.uploadReportTemplate" /></html-el:link></td>
                  </tr>
                   <tr class="fontnormal">
                   <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                   <td width="97%"><a id="admin.link.ViewReportsCategory"
								href="reportsCategoryAction.do?method=viewReportsCategory"><mifos:mifoslabel name="admin.ViewReportsCategory" />
								</a> | <html-el:link styleId="admin.link.defineNewCategory"
								href="reportsCategoryAction.do?method=loadDefineNewCategoryPage"><mifos:mifoslabel name="admin.defineNewCategory" /></html-el:link></td>
                  </tr>
                </table>
                <br>

		<span class="headingorange"><mifos:mifoslabel name="admin.Manage" /> <mifos:mifoslabel name="admin.surveys" /></span><br />
                  <table width="90%" border="0" cellspacing="0" cellpadding="0">
                    <tr class="fontnormal">
                      <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                      <td width="97%"><a id="admin.link.surveys" href="surveysAction.do?method=mainpage"><mifos:mifoslabel name="admin.View" /> <mifos:mifoslabel name="admin.surveys" /></a> | <html-el:link styleId="admin.link.defineNewSurvey" href="surveysAction.do?method=create_entry"><mifos:mifoslabel name="admin.definenew" /> <mifos:mifoslabel name="admin.survey" /></html-el:link></td>
                    </tr>
                    <tr class="fontnormal">
                      <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                      <td width="97%"><a id="admin.link.viewQuestions" href="questionsAction.do?method=viewQuestions"><mifos:mifoslabel name="admin.viewquestions" /> </a> | <html-el:link styleId="admin.link.defineQuestions" href="questionsAction.do?method=defineQuestions"><mifos:mifoslabel name="admin.define" /> <mifos:mifoslabel name="admin.questions" /></html-el:link></td>
                  </tr>

                </table>
			</td>
		</html-el:form>
	</tiles:put>
</tiles:insert>
