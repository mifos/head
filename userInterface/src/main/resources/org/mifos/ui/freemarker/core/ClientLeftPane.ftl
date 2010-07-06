[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

 <!--  Left Sidebar Begins-->
  <div class="sidebar ht750">
    <p class="orangetab">[@spring.message "clients&AccountsTasks" /]</p>
    <p class="paddingLeft"><span class="fontBold">[@spring.message "manageCollectionSheets" /]</span><br />
      <a href="collectionData.html">[@spring.message "enterCollectionSheetData" /]</a> </p>
    <p class="paddingLeft"><span class="fontBold">[@spring.message "createnewClients" /]</span><br />
      <a href="newCenter.html">[@spring.message "createnewCenter" /]</a><br />
      <a href="newGroup.html">[@spring.message "createnewGroup" /]</a><br />
      <a href="newClient.html">[@spring.message "createnewClient" /]</a> </p>
    <p class="paddingLeft"><span class="fontBold">[@spring.message "createnewAccounts" /]</span><br />
      <a href="newSavings.html">[@spring.message "createSavingsaccount" /]</a><br />
      <a href="newLoan.html">[@spring.message "createLoanaccount" /]</a><br />
      <a href="multipleLoan.html">[@spring.message "createmultipleLoanaccounts" /]</a> </p>
    <p class="paddingLeft"><span class="fontBold">[@spring.message "manageaccountstatus" /]</span><br />
      <a href="status.html">[@spring.message "changeaccountstatus" /]</a> </p>
  </div>
  <!--  Left Sidebar Ends-->




