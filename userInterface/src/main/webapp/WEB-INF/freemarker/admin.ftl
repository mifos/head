[#ftl]
[#--
* Copyright (c) 2005-2011 Grameen Foundation USA
*  All rights reserved.
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
*  implied. See the License for the specific language governing
*  permissions and limitations under the License.
*
*  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
*  explanation of the license and how it is applied.
--]
[#include "layout.ftl"]
[@adminLeftPaneLayout]
  <!--  Main Content Begins-->
   <div>
  <div class="content marginLeft30">
    <div class="orangeheading font14 marginLeft50">[@spring.message "admin.administrativeTasks" /]</div>
    <p class="marginLeft30">[@spring.message "admin.administrativeTasksWelcome" /] </p>
    <div class="marginTop20"></div>
    <div class="span-9 floatLT">
      <p class="orangeheading">[@spring.message "admin.manageOrganization" /]</p>
      <p class="fontBold">[@spring.message "admin.systemusers"/]</p>
      <ul>
        <li type="circle"><a id="admin.link.viewSysUsers" href="viewSystemUsers.ftl">[@spring.message "admin.viewsystemusers"/]</a>&nbsp;|&nbsp;<a id="admin.link.defineNewUsers" href="user.ftl">[@spring.message "admin.definenewsystemuser" /]</a></li>
        <li type="circle"><a id="admin.link.manageRoles" href="manageRolesAndPermissions.ftl">[@spring.message "admin.managerolesandpermissions"/]</a></li>
      </ul>
      <p><span class="fontBold">[@spring.message "admin.offices"/]</span>
      <ul>
        <li type="circle"><a id="admin.link.viewOffices" href="viewOffices.ftl">[@spring.message "admin.viewOffices" /]</a>&nbsp;|&nbsp;<a Id="admin.link.defineNewOffice" href="defineNewOffice.ftl">[@spring.message "admin.defineNewOffice"/]</a></li>
        <li type="circle"><a id="admin.link.viewOfficeHierarchy" href="viewOfficeHierarchy.ftl">[@spring.message "admin.viewofficehierarchy" /]</a></li>
      </ul>
      </p>
      <p><span class="fontBold">[@spring.message "admin.organizationpreferences"/]</span>
      <ul>
        <li type="circle"><a id="admin.link.defineNewFees" href="feeaction.do?method=load&recordOfficeId=${model.request.getSession().getAttribute("UserContext").branchId}&recordLoanOfficerId=${model.request.getSession().getAttribute("UserContext").id}" >[@spring.message "admin.viewfees"/]</a>&nbsp;|&nbsp;<a eId="admin.link.defineNewFees" href="feeaction.do?method=load&recordOfficeId=${model.request.getSession().getAttribute("UserContext").branchId}&recordLoanOfficerId=${model.request.getSession().getAttribute("UserContext").id}">[@spring.message "admin.definenewfees"/]</a></li>
        <li type="circle"><a id="admin.link.viewFunds" href="viewFunds.ftl" >[@spring.message "admin.viewfunds"/]</a>&nbsp;|&nbsp;<a Id="admin.link.defineNewFund"    href="defineNewFund.ftl">[@spring.message "admin.definenewfund"/]</a></li>
        <li type="circle"><a id="admin.link.viewChecklists" href="viewChecklists.ftl" >[@spring.message "admin.viewchecklists"/]</a>&nbsp;|&nbsp;<a Id="admin.link.defineNewChecklist" href="defineNewChecklist.ftl">[@spring.message "admin.definenewchecklist"/]</a></li>
        <li type="circle"><a id="admin.link.viewHolidays" href="viewHolidays.ftl" >[@spring.message "admin.viewholidays"/]</a>&nbsp;|&nbsp;<a Id="admin.link.defineNewHoliday" href="defineNewHoliday.ftl">[@spring.message "admin.definenewholidays"/]</a></li>
        <li type="circle"><a id="admin.link.defineAcceptedPaymentType" href="defineAcceptedPaymentTypes.ftl" >[@spring.message "admin.defineacceptedpayments"/]</a></li>
        <li type="circle"><a id="admin.link.viewOrganizationSettings" href="viewOrganizationSettings.ftl" >[@spring.message "admin.viewOrganizationSettings"/]</a></li>
      </ul>
      </p>
      <p><span class="fontBold">[@spring.message "admin.datadisplayandrules" /] </span>
      <ul>
        <li type="circle"><a id="admin.link.defineLabels" href="defineLabels.ftl">[@spring.message "admin.defineLabels"/]</a></li>
        <li type="circle"><a id="admin.link.defineLookupOption"    href="defineLookupOptions.ftl">[@spring.message "admin.defineLookupOptions"/]</a></li>
        <li type="circle"><a id="admin.link.defineHiddenMandatoryFields" href="defineMandatoryHiddenFields.ftl">[@spring.message "admin.definemandatory/hiddenfields"/]</a></li>
        <li type="circle"><a id="admin.link.viewAdditionalFields" href="viewAdditionalFields.ftl">[@spring.message "admin.viewAdditionalFields"/]</a>&nbsp;|&nbsp;<a Id="admin.link.defineAdditionalFields"    href="defineAdditionalFields.ftl">[@spring.message "admin.defineAdditionalFields"/]</a></li>
      </ul>
      </p>
      <p class="orangeheading">[@spring.message "admin.manageimports"/]</p>
      <ul>
        <li type="circle"><a id="admin.link.manageImports" href="manageImportAction.do?method=load">[@spring.message "admin.importtransactions"/]</a></li>
      </ul>
      <p class="orangeheading">[@spring.message "admin.systemadministration"/]</p>
      <ul>
        <li type="circle"><a id="admin.link.viewSystemInfo" href="systemInformation.ftl">[@spring.message "admin.viewSystemInformation"/]</a></li>
       <li type="circle"> <a id="admin.link.shutdownMifos" href="shutdownAction.do?method=load">[@spring.message "admin.shutdown"/]</a></li>
        <li type="circle"><a id="admin.link.batchjobs" href="batchjobs.ftl">[@spring.message "admin.batchjobs"/]</a></li>
      </ul>
    </div>
    <div class="span-9 last">
      <p class="orangeheading">[@spring.message "admin.manageProducts"/]</p>
      <p><span class="fontBold">[@spring.message "admin.productrulesattributes"/]</span>
      <ul>
        <li type="circle"><a id="admin.link.viewProductCategories" href="viewProductCategories.ftl">[@spring.message "admin.viewproductcategories"/]</a>&nbsp;|&nbsp;<a id="admin.link.defineNewProductCategory" href="defineNewCategory.ftl">[@spring.message "admin.definenewcategory"/]</a></li>
        <li type="circle"><a id="admin.link.viewLatenessDormancyDefinition" href="editLatenessDormancy.ftl">[@spring.message "admin.viewlatenessdormancydefinition"/]</a></li>
      </ul>
      </p>
      <p><span class="fontBold">[@spring.message "admin.manageproductmix"/]</span>
      <ul>
        <li type="circle"><a id="admin.link.viewProductsMix" href="viewProductMix.ftl">[@spring.message "admin.viewproductsmix"/]</a>&nbsp;|&nbsp;<a id="admin.link.defineProductsMix" href="productMixAction.do?method=load&recordOfficeId=${model.request.getSession().getAttribute("UserContext").branchId}&recordLoanOfficerId=${model.request.getSession().getAttribute("UserContext").id}">[@spring.message "admin.defineproductsmix"/]</a></li>
      </ul>
      </p>
      <p><span class="fontBold">[@spring.message "admin.manageLoanProducts"/]</span>
      <ul>
        <li type="circle"><a id="admin.link.viewLoanProducts" href="viewLoanProducts.ftl">[@spring.message "admin.viewLoanProducts"/]</a>&nbsp;|&nbsp;<a Id="admin.link.defineNewLoanProduct" href="loanproductaction.do?method=load&recordOfficeId=${model.request.getSession().getAttribute("UserContext").branchId}&recordLoanOfficerId=${model.request.getSession().getAttribute("UserContext").id}">[@spring.message "admin.definenewLoanproduct"/]</a></li>
      </ul>
      </p>
      <p><span class="fontBold">[@spring.message "admin.manageSavingsproducts"/]</span>
      <ul>
        <li type="circle"><a id="admin.link.viewSavingsProducts" href="viewSavingsProducts.ftl">[@spring.message "admin.viewSavingsproducts"/]</a>&nbsp;|&nbsp;<a id="admin.link.defineNewSavingsProduct" href="savingsproductaction.do?method=load&recordOfficeId=${model.request.getSession().getAttribute("UserContext").branchId}&recordLoanOfficerId=${model.request.getSession().getAttribute("UserContext").id}">[@spring.message "admin.definenewSavingsproduct"/]</a></li>
      </ul>
      </p>
      <p class="orangeheading">[@spring.message "admin.manageAccounts"/]</p>
      <p><span class="fontBold">[@spring.message "admin.manageLoanaccounts"/]</span>
      <ul>
        <li type="circle"><a id="admin.link.reverseLoanDisbursal" href="reverseloandisbaction.do?method=search&recordOfficeId=${model.request.getSession().getAttribute("UserContext").branchId}&recordLoanOfficerId=${model.request.getSession().getAttribute("UserContext").id}">[@spring.message "admin.reverseLoanDisbursal"/] </a></li>
        <li type="circle"><a id="admin.link.redoLoanDisbursal" href="loanAccountAction.do?method=redoLoanBegin&recordOfficeId=${model.request.getSession().getAttribute("UserContext").branchId}&recordLoanOfficerId=${model.request.getSession().getAttribute("UserContext").id}">[@spring.message "admin.redoLoanDisbursal"/] </a></li>
      </ul>
      </p>
      <p class="orangeheading">[@spring.message "admin.managereports"/]</p>
      <ul>
        <li type="circle"><a id="admin.link.ViewAdminDocuments" href="birtAdminDocumentUploadAction.do?method=getViewBirtAdminDocumentPage">[@spring.message "admin.viewadmindocuments"/]</a>&nbsp;|&nbsp;<a id="admin.link.uploadAdminDocuments"    href="birtAdminDocumentUploadAction.do?method=getBirtAdminDocumentUploadPage&viewPath=administerreports_path">[@spring.message "admin.uploadadmindocuments"/]</a></li>
        <li type="circle"><a id="admin.link.ViewReportsTemplates" href="birtReportsUploadAction.do?method=getViewReportPage">[@spring.message "admin.viewreportstemplates"/]</a>&nbsp;|&nbsp;<a id="admin.link.uploadReportTemplate" href="birtReportsUploadAction.do?method=getBirtReportsUploadPage&viewPath=administerreports_path">[@spring.message "admin.uploadreporttemplate"/]</a></li>
        <li type="circle"><a id="admin.link.ViewReportsCategory" href="reportsCategoryAction.do?method=viewReportsCategory">[@spring.message "admin.viewreportscategory"/]</a>&nbsp;|&nbsp;<a id="admin.link.defineNewCategory" href="reportsCategoryAction.do?method=loadDefineNewCategoryPage">[@spring.message "admin.definenewreportcategory"/]</a></li>
      </ul>
      <p class="orangeheading">[@spring.message "admin.managesurveys"/]</p>
      <ul>
        <li type="circle"><a id="admin.link.surveys" href="surveysAction.do?method=mainpage">[@spring.message "admin.viewsurveys"/]</a>&nbsp;|&nbsp;<a id="admin.link.defineNewSurvey" href="surveysAction.do?method=create_entry">[@spring.message "admin.definenewsurvey"/]</a></li>
        <li type="circle"><a id="admin.link.viewQuestions" href="questionsAction.do?method=viewQuestions">[@spring.message "admin.viewquestionbank"/]</a>&nbsp;|&nbsp;<a id="admin.link.defineQuestions" href="questionsAction.do?method=defineQuestions">[@spring.message "admin.definequestions"/]</a></li>
      </ul>
      <p class="orangeheading">[@spring.message "admin.managequestions" /]</p>
      <ul>
        <li type="circle"><a id="admin.link.questions" href="viewQuestions.ftl">[@spring.message "admin.viewquestions" /]</a> | <a id="admin.link.defineNewQuestion" href="createQuestion.ftl">[@spring.message "admin.definenewquestion" /]</a></li>
        <li type="circle"><a id="admin.link.questiongroups" href="viewQuestionGroups.ftl">[@spring.message "admin.viewquestiongroups" /]</a> | <a id="admin.link.defineNewQuestionGroup" href="createQuestionGroup.ftl">[@spring.message "admin.definenewquestiongroup" /]</a></li>
        <li type="circle"><a id="admin.link.uploadPPI" href="uploadQuestionGroup.ftl" >[@spring.message "admin.uploadPPI"/]</a></li>
      </ul>
    </div>
  </div>
  </div>
 <!--Main Content Ends -->
[/@adminLeftPaneLayout]