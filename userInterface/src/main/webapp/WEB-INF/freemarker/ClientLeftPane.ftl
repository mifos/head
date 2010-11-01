[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

 <!--  Left Sidebar Begins-->
  <div class="sidebar ht750">
    <p class="orangetab">[@spring.message "ClientLeftPane.clientsAndAccountsTasks" /]</p>
    <p class="paddingLeft"><span class="fontBold">[@spring.message "ClientLeftPane.manageCollectionSheets" /]</span><br />
      <a href="#">[@spring.message "ClientLeftPane.enterCollectionSheetData" /]</a> </p>
    <p class="paddingLeft"><span class="fontBold">[@spring.message "ClientLeftPane.createnewClients" /]</span><br />
      <a href="#">[@spring.message "ClientLeftPane.createnewCenter" /]</a><br />
      <a href="#">[@spring.message "ClientLeftPane.createnewGroup" /]</a><br />
      <a href="#">[@spring.message "ClientLeftPane.createnewClient" /]</a> </p>
    <p class="paddingLeft"><span class="fontBold">[@spring.message "ClientLeftPane.createnewAccounts" /]</span><br />
      <a href="#">[@spring.message "ClientLeftPane.createSavingsaccount" /]</a><br />
      <a href="#">[@spring.message "ClientLeftPane.createLoanaccount" /]</a><br />
      <a href="#">[@spring.message "ClientLeftPane.createmultipleLoanaccounts" /]</a> </p>
    <p class="paddingLeft"><span class="fontBold">[@spring.message "ClientLeftPane.manageaccountstatus" /]</span><br />
      <a href="#">[@spring.message "ClientLeftPane.changeaccountstatus" /]</a> </p>
  </div>
  <!--  Left Sidebar Ends-->




