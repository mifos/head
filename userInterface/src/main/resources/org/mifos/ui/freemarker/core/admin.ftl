[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  [#include "adminLeftPane.ftl" ] 
    <!--  Left Sidebar Begins
  <div class="sidebar ht750">
    <p class="orangetab">Administrative Tasks</p>
    <p class="paddingLeft"><span class="fontBold">Search by client name, system ID or account number</span><br />
      <input type="text" id="txt" />
      <br />
      <input class="buttn" type="button" name="search" value="Search" onclick="location.href='searched.html'" />
    </p>
  </div>-->
  <!--  Left Sidebar Ends-->
   <!--  Main Content Begins-->  
   <div>
  <div class=" content leftMargin180">
    <div class="orangeheading font14">[@spring.message "administrativeTasks" /]</div>
    <p>[@spring.message "administrativeTasksWelcome" /] </p>
    <div class="span-10 floatLT">
      <p class="orangeheading">[@spring.message "manageOrganization" /]</p>
      <p class="fontBold">[@spring.message "systemusers"/]</p>
      <ul>
        <li type="circle"><a href="viewUsers.html">[@spring.message "viewsystemusers"/]</a>&nbsp;|&nbsp;<a href="newUser.html">[@spring.message "definenewsystemuser" /]</a></li>
        <li type="circle"><a href="roles&permissions.html">[@spring.message "managerolesandpermissions"/]</a></li>
      </ul>
      <p><span class="fontBold">[@spring.message "offices"/]</span>
      <ul>
        <li type="circle"><a href="viewOffices.html">[@spring.message "viewOffices" /]</a>&nbsp;|&nbsp;<a href="newOffice.html">[@spring.message "defineNewOffice"/]</a></li>
        <li type="circle"><a href="officeHierarchy.html">[@spring.message "viewofficehierarchy" /]</a></li>
      </ul>
      </p>
      <p><span class="fontBold">Organization Preferences</span>
      <ul>
        <li type="circle"><a href="viewFees.html">[@spring.message "viewfees"/]</a>&nbsp;|&nbsp;<a href="newFee.html">[@spring.message "definenewfees"/]</a></li>
        <li type="circle"><a href="viewFunds.html">[@spring.message "viewfunds"/]</a>&nbsp;|&nbsp;<a href="newFund.html">[@spring.message "definenewfund"/]</a></li>
        <li type="circle"><a href="viewChecklists.html">[@spring.message "viewchecklists"/]</a>&nbsp;|&nbsp;<a href="newChecklist.html">[@spring.message "definenewchecklist"/]</a></li>
        <li type="circle"><a href="viewHolidays.html">[@spring.message "viewholidays"/]</a>&nbsp;|&nbsp;<a href="addHoliday.html">[@spring.message "definenewholidays"/]</a></li>
        <li type="circle"><a href="paymentTypes.html">[@spring.message "defineacceptedpayments"/]</a></li>
        <li type="circle"><a href="organizationalSettings.html">[@spring.message "vieworganizationsettings"/]</a></li>
        <li type="circle"><a href="viewPPI.html">[@spring.message "viewPPIsettings" /]</a>&nbsp;|&nbsp;<a href="configPPI.html">[@spring.message "ConfigurePPIsettings"/]</a></li>
      </ul>
      </p>
      <p><span class="fontBold">[@spring.message "datadisplayandrules" /] </span>
      <ul>
        <li type="circle"><a href="defineLabels.html">[@spring.message "defineLabels"/]</a></li>
        <li type="circle"><a href="defineLookUps.html">[@spring.message "defineLookupOptions"/]</a></li>
        <li type="circle"><a href="mandatory.html">[@spring.message "definemandatory/hiddenfields"/]</a></li>
        <li type="circle"><a href="viewAdditionalFields.html">[@spring.message "viewAdditionalFields"/]</a>&nbsp;|&nbsp;<a href="newField.html">[@spring.message "defineAdditionalFields"/]</a></li>
      </ul>
      </p>
      <p class="orangeheading">[@spring.message "manageimports"/]</p>
      <ul>
        <li type="circle"><a href="import.html">[@spring.message "importtransactions"/]</a></li>
      </ul>
      <p class="orangeheading">[@spring.message "manageinformation"/]</p>
      <ul>
        <li type="circle"><a id="admin.link.viewSystemInfo" href="systemInfoAction.do?method=load">[@spring.message "viewSystemInformation"/]</a></li>
      </ul>
    </div>
    <div class="span-10 last">
      <p class="orangeheading">[@spring.message "manageProducts"/]</p>
      <p><span class="fontBold">[@spring.message "productrulesattributes"/]</span>
      <ul>
        <li type="circle"><a Id="admin.link.viewProductCategories" href="productCategoryAction.do?method=getAllCategories&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">[@spring.message "viewproductcategories"/]</a>&nbsp;|&nbsp;<a Id="admin.link.defineNewProductCategory" href="productCategoryAction.do?method=load&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">[@spring.message "definenewcategory"/]</a></li>
        <li type="circle"><a Id="admin.link.viewLatenessDormancyDefinition" href="prdconfaction.do?method=load&randomNUm=${sessionScope.randomNUm}">[@spring.message "viewlatenessdormancydefinition"/]</a></li>
      </ul>
      </p>
      <p><span class="fontBold">[@spring.message "manageproductmix"/]</span>
      <ul>
        <li type="circle"><a Id="admin.link.viewProductsMix" href="productMixAction.do?method=viewAllProductMix&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">[@spring.message "viewproductsmix"/]</a>&nbsp;|&nbsp;<a Id="admin.link.defineProductsMix" href="productMixAction.do?method=load&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">[@spring.message "defineproductsmix"/]</a></li>
      </ul>
      </p>
      <p><span class="fontBold">[@spring.message "manageLoanProducts"/]</span>
      <ul>
        <li type="circle"><a href="viewLoanProducts.html">[@spring.message "viewLoanProducts"/]</a>&nbsp;|&nbsp;<a href="loanProduct.html">[@spring.message "definenewLoanproduct"/]</a></li>
      </ul>
      </p>
      <p><span class="fontBold">[@spring.message "manageSavingsproducts"/]</span>
      <ul>
        <li type="circle"><a id="admin.link.viewSavingsProducts" href="savingsproductaction.do?method=search&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">[@spring.message "viewSavingsproducts"/]</a>&nbsp;|&nbsp;<a id="admin.link.defineNewSavingsProduct" href="savingsproductaction.do?method=load&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">>[@spring.message "definenewSavingsproduct"/]</a></li>
      </ul>
      </p>
      <p class="orangeheading">[@spring.message "manageAccounts"/]</p>
      <p><span class="fontBold">[@spring.message "manageLoanaccounts"/]</span>
      <ul>
        <li type="circle"><a Id="admin.link.reverseLoanDisbursal" href="reverseloandisbaction.do?method=search&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">[@spring.message "reverseLoanDisbursal"/] </a></li>
        <li type="circle"><a Id="admin.link.redoLoanDisbursal" href="loanAccountAction.do?method=redoLoanBegin&recordOfficeId=${UserContext.branchId}&recordLoanOfficerId=${UserContext.id}&randomNUm=${sessionScope.randomNUm}">[@spring.message "redoLoanDisbursal"/] </a></li>
      </ul>
      </p>
      <p class="orangeheading">[@spring.message "managereports"/]</p>
      <ul>
        <li type="circle"><a id="admin.link.ViewAdminDocuments" href="birtAdminDocumentUploadAction.do?method=getViewBirtAdminDocumentPage">[@spring.message "viewadmindocuments"/]</a>&nbsp;|&nbsp;<a Id="admin.link.uploadAdminDocuments"	href="birtAdminDocumentUploadAction.do?method=getBirtAdminDocumentUploadPage&viewPath=administerreports_path">[@spring.message "uploadadmindocuments"/]</a></li>
        <li type="circle"><a id="admin.link.ViewReportsTemplates" href="birtReportsUploadAction.do?method=getViewReportPage">[@spring.message "viewreportstemplates"/]</a>&nbsp;|&nbsp;<a id="admin.link.uploadReportTemplate" href="birtReportsUploadAction.do?method=getBirtReportsUploadPage&viewPath=administerreports_path">[@spring.message "uploadreporttemplate"/]</a></li>
        <li type="circle"><a id="admin.link.ViewReportsCategory" href="reportsCategoryAction.do?method=viewReportsCategory">[@spring.message "viewreportscategory"/]</a>&nbsp;|&nbsp;<a id="admin.link.defineNewCategory" href="reportsCategoryAction.do?method=loadDefineNewCategoryPage">[@spring.message "definenewreportcategory"/]</a></li>
      </ul>
      <p class="orangeheading">[@spring.message "managesurveys"/]</p>
      <ul>
        <li type="circle"><a id="admin.link.surveys" href="surveysAction.do?method=mainpage">[@spring.message "viewsurveys"/]</a>&nbsp;|&nbsp;<a Id="admin.link.defineNewSurvey" href="surveysAction.do?method=create_entry">[@spring.message "definenewsurvey"/]</a></li>
        <li type="circle"><a id="admin.link.viewQuestions" href="questionsAction.do?method=viewQuestions">[@spring.message "viewquestionbank"/]</a>&nbsp;|&nbsp;<a id="admin.link.defineQuestions" href="questionsAction.do?method=defineQuestions">[@spring.message "definequestions"/]</a></li>
      </ul>
    </div>
  </div>
</div><!--Main Content Ends-->


[@mifos.footer/]