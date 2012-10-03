<%--
Copyright (c) 2005-2011 Grameen Foundation USA
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
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<script language="javascript">
 function fnAccountDetail(accountingActivationStatus){
	if(accountingActivationStatus == true){
		$("#accountDetailsId").show();
	}else{
		$("#accountDetailsId").hide();
	}
  }
</script>

<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.adminUIResources"/>
<body onload="fnAccountDetail(${accountingActivationStatus})">
<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
        <span id="page.id" title="admin" ></span>
		<html-el:form action="AdminAction.do?method=load">
			<td valign="top" bgcolor="#FFFFFF"
				style="padding-left:8px; padding-top:10px;">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">

				<tr>
					<td colspan="2" valign="top"><span id="admin.label.admintasks"
						class="headingorange"><mifos:mifoslabel name="admin.admintasks"  /></span><br>
					<span class="fontnormal"><span id="admin.text.welcome"><mifos:mifoslabel name="admin.welcometomifos" /></span></span></td>
				</tr>
				<tr width="100%">
					<td valign="top" colspan="2"><font class="fontnormalRedBold"><span id="admin.error.message"><html-el:errors
						bundle="adminUIResources" /></span> </font></td>
				</tr>

				<tr>
					<td width="48%" height="240" valign="top"
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
							<td><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
							<td>
							<a id="admin.link.viewOfficeHierarchy" href="viewOfficeHierarchy.ftl"><mifos:mifoslabel name="admin.viewoffhier" /></a>
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
							<td>
							<a id="admin.link.viewFunds" href="viewFunds.ftl"><mifos:mifoslabel name="admin.viewfunds" /></a>
							| <a href="defineNewFund.ftl" id="admin.link.defineNewFund"><mifos:mifoslabel name="admin.defnewfund" /></a>
							</td>
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
							<td>
							<a id="admin.link.viewHolidays" href="viewHolidays.ftl"><mifos:mifoslabel name="admin.viewholidays" /></a>
							| <a id="admin.link.defineNewHoliday" href="defineNewHoliday.ftl"><mifos:mifoslabel name="admin.defnewholiday" /></a>
							</td>
						</tr>
						<tr class="fontnormal">
							<td><img src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td>
							<a id="admin.link.defineAcceptedPaymentType" href="defineAcceptedPaymentTypes.ftl"><mifos:mifoslabel name="admin.defineacceptedpaymenttype" /></a>
							</td>
						</tr>
						<tr class="fontnormal">
							<td><img src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td>
							<a id="admin.link.viewOrganizationSettings" href="viewOrganizationSettings.ftl"><mifos:mifoslabel name="admin.vieworganizationsettings" /></a>
							</td>
						</tr>
                        <tr class="fontnormal">
                            <td><img src="pages/framework/images/bullet_circle.gif" width="9"
                                height="11"></td>
                            <td>
                            <a id="admin.link.viewPenalties" href="viewPenalties.ftl"><mifos:mifoslabel name="admin.viewpenalties" /></a>
                            | <a href="defineNewPenalty.ftl" id="admin.link.defineNewPenalty"><mifos:mifoslabel name="admin.defnewpenalties" /></a>
                            </td>
                        </tr>
					</table>
					 <br>
					<span class="fontnormalbold"><mifos:mifoslabel name="admin.datadisplayrules" /></span><br>
						<table width="90%" border="0" cellspacing="0" cellpadding="0">
							<tr class="fontnormal">
								<td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
								<td width="97%">
								<a id="admin.link.customizeText" href="customizeText.ftl"><mifos:mifoslabel name="admin.customizeText" /></a>								
								</td>
							</tr>
							<tr class="fontnormal">
								<td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
								<td width="97%"><html-el:link styleId="admin.link.defineLookupOption"
								action="lookupOptionsAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.definelookupoption" /></html-el:link></td>
							</tr>
							<tr class="fontnormal">
								<td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
								<td width="97%">
								<a href="defineMandatoryHiddenFields.ftl" id="admin.link.defineHiddenMandatoryFields"><mifos:mifoslabel name="admin.definehiddenmandfields" /></a>
								</td>
							</tr>
						</table>
                    <br>
                    <span class="fontnormalbold"><mifos:mifoslabel name="admin.accounting" /></span><br>
                    <table width="90%" border="0" cellspacing="0" cellpadding="0">
                        <tr class="fontnormal">
                            <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                            <td width="97%">
                                <a id="admin.link.monthClosing" href="monthClosing.ftl"><mifos:mifoslabel name="admin.monthClosing" /></a>
                            </td>
                        </tr>
                        <!--<tr class="fontnormal">
			<td align="center" colspan="2">
			 	<div id="accountDetailsId">
				 <table width="100%" border="0" cellspacing="0" cellpadding="0">
                   <tr class="fontnormal">
                      <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                      <td width="97%"><a href="generalledgeraction.do?method=load"><mifos:mifoslabel name="admin.glaction" bundle="adminUIResources"/></a></td>
                  </tr>
                  <tr class="fontnormal">
                    <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                    <td width="97%"><a href="journalvoucheraction.do?method=load"><mifos:mifoslabel name="admin.jvaction" bundle="adminUIResources"/></a></td>
                  </tr>
                   <tr class="fontnormal">
                    <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                    <td width="97%"><a href="openbalanceaction.do?method=load"><mifos:mifoslabel name="admin.defineOpenBalance" bundle="adminUIResources"/></a></td>
                  </tr>
                  <tr class="fontnormal">
                    <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                    <td width="97%"><a href="viewgltransactionsaction.do?method=load"><mifos:mifoslabel name="admin.viewGlTransaction" bundle="adminUIResources"/></a></td>
                  </tr>
                  <tr class="fontnormal">
                    <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                    <td width="97%"><a href="processaccountingtransactionsaction.do?method=load"><mifos:mifoslabel name="admin.processTransactions" bundle="adminUIResources"/></a></td>
                  </tr>
                  <tr class="fontnormal">
                    <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                    <td width="97%"><a href="yearEndProcessAction.do?method=load"><mifos:mifoslabel name="admin.yearEndProcess" bundle="adminUIResources"/></a></td>
                  </tr>-->
               </table>
			</div>
			</td>
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
									<a id="admin.link.viewimportedfiles" href="viewImportedTransactions.ftl">
                                        <mifos:mifoslabel name="admin.importexport.undoImportedFiles" bundle="adminUIResources" /></a>
										| <a id="admin.link.manageImports" href="manageImportAction.do?method=load">
										<mifos:mifoslabel name="admin.importexport.importtransactions" bundle="adminUIResources" /></a>
                                        </td>
								</tr>
                                <tr class="fontnormal">
                                        <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                                        <td width="97%">
                                            <a id="admin.link.importClients" href="importClients.ftl">
                                            <mifos:mifoslabel name="admin.importexport.clients" bundle="adminUIResources" /></a>
                                        </td>
                                </tr>
                                <tr class="fontnormal">
                                        <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                                        <td width="97%">
                                            <a id="admin.link.importLoans" href="importLoans.ftl">
                                            <mifos:mifoslabel name="admin.importexport.loans.importLoans" bundle="adminUIResources" /></a>
                                        </td>
                                </tr>
                                <tr class="fontnormal">
                                        <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                                        <td width="97%">
                                            <a id="admin.link.importSavings" href="importSavings.ftl">
                                            <mifos:mifoslabel name="admin.importexport.savings.importSavings" bundle="adminUIResources" /></a>
                                        </td>
                                </tr>
								<tr class="fontnormal">
									<td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
									<td width="97%">
										<a id="admin.link.viewaccountingexports" href="renderAccountingDataCacheInfo.ftl">
										<mifos:mifoslabel name="admin.importexport.viewaccountingexports" bundle="adminUIResources" /></a>
                                        </td>
								</tr>
								<tr class="fontnormal">
									<td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
									<td width="97%">
										<a id="admin.link.viewaccountingexports" href="restApprovalList.ftl">
										REST API Approval</a>
                                        </td>
								</tr>
							</table>
                            <br>
						</td>
					</tr>
					</table>
					<table width="90%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td>
							<span class="headingorange"><mifos:mifoslabel name="admin.sysadmin.title" bundle="adminUIResources" /></span>
							<table width="90%" border="0" cellspacing="0" cellpadding="0">
								<tr class="fontnormal">
									<td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
									<td width="97%">
										<a id="admin.link.viewSystemInfo" href="systemInformation.ftl">
                                        <mifos:mifoslabel name="admin.view.sysinfo.title" bundle="adminUIResources"/></a></td>
								</tr>
							</table>
                            <table width="90%" border="0" cellspacing="0" cellpadding="0">
								<tr class="fontnormal">
									<td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
									<td width="97%">
										<a id="admin.link.shutdownMifos" href="shutdown.ftl">
										<mifos:mifoslabel name="admin.shutdown.link" bundle="adminUIResources" /></a></td>
								</tr>
							</table>
							<table width="90%" border="0" cellspacing="0" cellpadding="0">
								<tr class="fontnormal">
									<td width="3%"><img
										src="pages/framework/images/bullet_circle.gif" width="9"
										height="11"></td>
									<td width="97%"><a id="admin.link.batchjobs"
										href="batchjobs.ftl"> <mifos:mifoslabel
										name="admin.batchjobs.link" bundle="adminUIResources" /></a></td>
								</tr>
							</table>
							</td>
					</tr>
					</table>
					<br />
					</td>
					<td width="52%" bgcolor="#FFFFFF" valign="top" class="paddingleft"><span
						class="headingorange"><mifos:mifoslabel name="admin.manageprd" /></span><br>
					<span class="fontnormalbold"><mifos:mifoslabel name="admin.prdrules" /></span><br>
					<table width="90%" border="0" cellspacing="0" cellpadding="0">
						<tr class="fontnormal">
							<td width="3%"><img
								src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td width="97%">
								<!--
								<html-el:link styleId="admin.link.viewProductCategories" href="productCategoryAction.do?method=getAllCategories&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.viewprdcat" />
								</html-el:link> | <html-el:link styleId="admin.link.defineNewProductCategory" href="productCategoryAction.do?method=load&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.defnewcat" />
								</html-el:link>
								-->
								<a href="viewProductCategories.ftl" id="admin.link.viewProductCategories"><mifos:mifoslabel name="admin.viewprdcat" /></a> | <a href="defineNewCategory.ftl" id="admin.link.defineNewProductCategory"><mifos:mifoslabel name="admin.defnewcat" /></a>								
							</td>
						</tr>
						<tr class="fontnormal">
							<td><img src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<td>
							<a id="admin.link.viewLatenessDormancyDefinition" href="editLatenessDormancy.ftl"><mifos:mifoslabel name="admin.viewlate/dordef" /></a>
						</tr>
					</table>
					<br>
					<!-- Manage products mix  -->
					<span class="fontnormalbold">
					<mifos:mifoslabel name="admin.manageProductMix" />
					</span><br>
					<table width="90%" border="0" cellspacing="0" cellpadding="0">
						<tr class="fontnormal">
							<td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
							<td width="97%">
								<a href="viewProductMix.ftl"><mifos:mifoslabel name="admin.viewProductsMix" /></a> | <a id="admin.link.defineProductsMix" href="defineProductMix.ftl"><mifos:mifoslabel name="admin.defineProductsMix"/></a>
							</td>
						</tr>
					</table>
					<br>
					
					<!--  -->
					<span class="fontnormalbold">
					<mifos:mifoslabel name="admin.manageloanprd" />
					</span><br>
					<table width="90%" border="0" cellspacing="0" cellpadding="0">
						<tr class="fontnormal">
							<td width="3%"><img
								src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<!-- Bug id 28065  Added a  parameter input in the link of admin page.-->
							<td width="97%"><html-el:link styleId="admin.link.viewLoanProducts"
								href="loanproductaction.do?method=viewAllLoanProducts&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.viewLoanProducts" />
								</html-el:link> | <html-el:link styleId="admin.link.defineNewLoanProduct"
								href="loanproductaction.do?method=load&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.defineNewLoanProduct" />
								</html-el:link></td>
						</tr>
					</table>
					<br>
					<span class="fontnormalbold">
					<mifos:mifoslabel name="admin.managesavprd" />
					</span><br>
					<table width="90%" border="0" cellspacing="0" cellpadding="0">
						<tr class="fontnormal">
							<td width="3%"><img
								src="pages/framework/images/bullet_circle.gif" width="9"
								height="11"></td>
							<!-- Bug id 28065  Added a  parameter input in the link of admin page.-->
							<td width="97%">
								<!-- 
								<html-el:link styleId="admin.link.viewSavingsProducts" href="savingsproductaction.do?method=search&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								</html-el:link>
								-->
								<a id="admin.link.viewSavingsProducts" href="viewSavingsProducts.ftl">
									<mifos:mifoslabel name="admin.viewSavingsProducts" />
								</a> | <a id="admin.link.defineNewSavingsProduct" href="defineSavingsProduct.ftl">
									<mifos:mifoslabel name="admin.defineNewSavingsProduct" />
									</a>
									<!--
									<html-el:link styleId="admin.link.defineNewSavingsProduct" href="savingsproductaction.do?method=load&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
									</html-el:link>
									 -->
								</td>
						</tr>
					</table>
					<br>
					<span class="headingorange"><mifos:mifoslabel name="admin.manage.accounts" bundle="adminUIResources"/> </span><br>
                <span class="fontnormalbold"><mifos:mifoslabel name="admin.manageLoanAccounts" /></span><br>
                <table width="90%" border="0" cellspacing="0" cellpadding="0">
                  <tr class="fontnormal">
                    <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                    <td width="97%"><html-el:link styleId="admin.link.reverseLoanDisbursal"
								href="reverseloandisbaction.do?method=search&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
								<mifos:mifoslabel name="admin.reverseLoanDisbursal" />
								</html-el:link></td>

                  </tr>
                  <tr class="fontnormal">
                    <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                    <td width="97%"><html-el:link styleId="admin.link.redoLoanDisbursal"
                    	href="redoLoanAccount.ftl">
						<!-- 
						href="loanAccountAction.do?method=redoLoanBegin&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">
						 -->
								<mifos:mifoslabel name="admin.redoLoanDisbursal" />
								</html-el:link></td>
                  </tr>
                </table>
                
                <br />
				   <span class="headingorange"><mifos:mifoslabel name="admin.manage.reports" bundle="adminUIResources"/> </span><br />
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

<!-- 
		<span class="headingorange"><mifos:mifoslabel name="admin.manage.surveys" bundle="adminUIResources"/></span><br />
                  <table width="90%" border="0" cellspacing="0" cellpadding="0">
                    <tr class="fontnormal">
                      <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                      <td width="97%"><a id="admin.link.surveys" href="surveysAction.do?method=mainpage"><mifos:mifoslabel name="admin.view.surveys" bundle="adminUIResources"/> </a></td>
                    </tr>
                    <tr class="fontnormal">
                      <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                      <td width="97%"><a id="admin.link.viewQuestions" href="questionsAction.do?method=viewQuestions"><mifos:mifoslabel name="admin.viewquestions" bundle="adminUIResources"/> </a></td>
                  </tr>
                </table>
-->
                <br>
                <span class="headingorange"><mifos:mifoslabel name="admin.Manage" /> <mifos:mifoslabel name="admin.questions" /></span><br />
                <table width="90%" border="0" cellspacing="0" cellpadding="0">
                <tr class="fontnormal">
                  <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                  <td width="97%"><a id="admin.link.questions" href="viewQuestions.ftl"><mifos:mifoslabel name="admin.View" /> <mifos:mifoslabel name="admin.questions" /></a> | <html-el:link styleId="admin.link.defineNewQuestion" href="createQuestion.ftl"><mifos:mifoslabel name="admin.definenew" /> <mifos:mifoslabel name="admin.question" /></html-el:link></td>
                </tr>
                <tr class="fontnormal">
                  <td width="3%"><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></td>
                  <td width="97%"><a id="admin.link.questiongroups" href="viewQuestionGroups.ftl"><mifos:mifoslabel name="admin.View" /> <mifos:mifoslabel name="admin.questiongroups" /></a> | <html-el:link styleId="admin.link.defineNewQuestionGroup" href="createQuestionGroup.ftl"><mifos:mifoslabel name="admin.definenew" /> <mifos:mifoslabel name="admin.questiongroup" /></html-el:link></td>
                </tr>
                   <tr class="fontnormal">
                     <td><img src="pages/framework/images/bullet_circle.gif" width="9"  height="11"></td>
                     <td><a id="admin.link.uploadPPI" href="uploadQuestionGroup.ftl"><mifos:mifoslabel name="admin.uploadPPI" /></a></td>
                  </tr>
                </table>
			</td>
			</tr>
			</table>
		</html-el:form>
		</body>
	</tiles:put>
</tiles:insert>
