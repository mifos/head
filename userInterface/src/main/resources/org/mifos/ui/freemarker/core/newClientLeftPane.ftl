[#ftl]
[#import "spring.ftl" as spring]
[#import "newblueprintmacros.ftl" as mifos]

    <h3>[@spring.message "clients&AccountsTasks" /]</h3>
    <ul class="side_nav">
        <li class="title">
            [@spring.message "manageCollectionSheets" /]
        </li>
        <li>
            <a href="collectionsheetaction.do?method=load&amp;recordOfficeId=0&amp;recordLoanOfficerId=0">[@spring.message "enterCollectionSheetData" /]</a>
        </li>
         <li class="title">
            [@spring.message "createnewClients" /]
         </li>
        <li>
           <a href="centerCustAction.do?method=chooseOffice&amp;recordOfficeId=0&amp;recordLoanOfficerId=0">[@spring.message "createnewCenter" /]</a>
        </li>
        <li>
            <a href="groupCustAction.do?method=hierarchyCheck&amp;recordOfficeId=0&amp;recordLoanOfficerId=0&amp;input=createGroup">[@spring.message "createnewGroup" /]</a>
        </li>
        <li>
            <a href="groupCustAction.do?method=loadSearch&amp;recordOfficeId=0&amp;recordLoanOfficerId=0&amp;input=createClient">[@spring.message "createnewClient" /]</a>
        </li>

        <li class="title">
                [@spring.message "createnewAccounts" /]
        </li>
        <li>
            <a href="custSearchAction.do?method=loadSearch&amp;recordOfficeId=0&amp;recordLoanOfficerId=0&amp;input=savings">[@spring.message "createSavingsaccount" /]</a>
        </li>
        <li>
            <a href="custSearchAction.do?method=loadSearch&amp;recordOfficeId=0&amp;recordLoanOfficerId=0&amp;input=loan">[@spring.message "createLoanaccount" /]</a>
        </li>
        <li>
            <a href="multipleloansaction.do?method=load&amp;recordOfficeId=0&amp;recordLoanOfficerId=0">[@spring.message "createmultipleLoanaccounts" /]</a>
        </li>
         <li class="title">
                [@spring.message "manageaccountstatus" /]
        </li>
        <li>
            <a href="ChangeAccountStatus.do?method=load&amp;recordOfficeId=0&amp;recordLoanOfficerId=0">[@spring.message "changeaccountstatus" /]</a>
        </li>
    </ul>
