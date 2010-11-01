[#ftl]
[#import "spring.ftl" as spring]
[#import "newblueprintmacros.ftl" as mifos]

    <h3>[@spring.message "ClientLeftPane.clientsAndAccountsTasks" /]</h3>
    <ul class="side_nav">
        <li class="title">
            [@spring.message "ClientLeftPane.manageCollectionSheets" /]
        </li>
        <li>
            <a href="collectionsheetaction.do?method=load&amp;recordOfficeId=0&amp;recordLoanOfficerId=0">[@spring.message "ClientLeftPane.enterCollectionSheetData" /]</a>
        </li>
         <li class="title">
            [@spring.message "ClientLeftPane.createnewClients" /]
         </li>
        <li>
           <a href="centerCustAction.do?method=chooseOffice&amp;recordOfficeId=0&amp;recordLoanOfficerId=0">[@spring.message "ClientLeftPane.createnewCenter" /]</a>
        </li>
        <li>
            <a href="groupCustAction.do?method=hierarchyCheck&amp;recordOfficeId=0&amp;recordLoanOfficerId=0&amp;input=createGroup">[@spring.message "ClientLeftPane.createnewGroup" /]</a>
        </li>
        <li>
            <a href="groupCustAction.do?method=loadSearch&amp;recordOfficeId=0&amp;recordLoanOfficerId=0&amp;input=createClient">[@spring.message "ClientLeftPane.createnewClient" /]</a>
        </li>

        <li class="title">
                [@spring.message "ClientLeftPane.createnewAccounts" /]
        </li>
        <li>
            <a href="custSearchAction.do?method=loadSearch&amp;recordOfficeId=0&amp;recordLoanOfficerId=0&amp;input=savings">[@spring.message "ClientLeftPane.createSavingsaccount" /]</a>
        </li>
        <li>
            <a href="custSearchAction.do?method=loadSearch&amp;recordOfficeId=0&amp;recordLoanOfficerId=0&amp;input=loan">[@spring.message "ClientLeftPane.createLoanaccount" /]</a>
        </li>
        <li>
            <a href="multipleloansaction.do?method=load&amp;recordOfficeId=0&amp;recordLoanOfficerId=0">[@spring.message "ClientLeftPane.createmultipleLoanaccounts" /]</a>
        </li>
         <li class="title">
                [@spring.message "ClientLeftPane.manageaccountstatus" /]
        </li>
        <li>
            <a href="ChangeAccountStatus.do?method=load&amp;recordOfficeId=0&amp;recordLoanOfficerId=0">[@spring.message "ClientLeftPane.changeaccountstatus" /]</a>
        </li>
    </ul>
