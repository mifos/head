[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

 <!--  Left Sidebar Begins-->
  <div class="sidebar ht750">
    <p class="orangetab">[@spring.message "clients&AccountsTasks" /]</p>
    <p class="paddingLeft"><span class="fontBold">[@spring.message "manageCollectionSheets" /]</span><br />
      <a href="collectionsheetaction.do?method=load&amp;recordOfficeId=0&amp;recordLoanOfficerId=0">[@spring.message "enterCollectionSheetData" /]</a> </p>
    <p class="paddingLeft"><span class="fontBold">[@spring.message "createnewClients" /]</span><br />
      <a href="centerCustAction.do?method=chooseOffice&amp;recordOfficeId=0&amp;recordLoanOfficerId=0">[@spring.message "createnewCenter" /]</a><br />
      <a href="groupCustAction.do?method=hierarchyCheck&amp;recordOfficeId=0&amp;recordLoanOfficerId=0&amp;input=createGroup">[@spring.message "createnewGroup" /]</a><br />
      <a href="groupCustAction.do?method=loadSearch&amp;recordOfficeId=0&amp;recordLoanOfficerId=0&amp;input=createClient">[@spring.message "createnewClient" /]</a> </p>
    <p class="paddingLeft"><span class="fontBold">[@spring.message "createnewAccounts" /]</span><br />
      <a href="custSearchAction.do?method=loadSearch&amp;recordOfficeId=0&amp;recordLoanOfficerId=0&amp;input=savings">[@spring.message "createSavingsaccount" /]</a><br />
      <a href="custSearchAction.do?method=loadSearch&amp;recordOfficeId=0&amp;recordLoanOfficerId=0&amp;input=loan">[@spring.message "createLoanaccount" /]</a><br />
      <a href="multipleloansaction.do?method=load&amp;recordOfficeId=0&amp;recordLoanOfficerId=0">[@spring.message "createmultipleLoanaccounts" /]</a> </p>
    <p class="paddingLeft"><span class="fontBold">[@spring.message "manageaccountstatus" /]</span><br />
      <a href="ChangeAccountStatus.do?method=load&amp;recordOfficeId=0&amp;recordLoanOfficerId=0">[@spring.message "changeaccountstatus" /]</a> </p>
  </div>
  <!--  Left Sidebar Ends-->




