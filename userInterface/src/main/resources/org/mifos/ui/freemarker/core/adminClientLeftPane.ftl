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
<form name="custSearchActionForm" method="post" action="custSearchAction.do?method=loadAllBranches">
    <h3>[@spring.message "admin.administrativeTasks" /]</h3>
    <p class="form_row">
        <label for="searchString">
        [@spring.message "admin.searchbynamesystemIDoraccountnumber"/]
        </label>
        <input class="t_box" type="text" id="searchString" name="searchString" maxlength="200" size="15" value="">
      </p>
     <p class="form_button">
         <input class="buttn" type="submit" name="searchButton" value="[@spring.message "admin.search" /]" >
    </p>
</form>
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
            <a href="createSavingsAccount.ftl">[@spring.message "ClientLeftPane.createSavingsaccount" /]</a>
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
            <a href="ChangeAccountStatus.do?method=load&amp;recordOfficeId=0&amp;recordLoanOfficerId=0">[@spring.message "ClientLeftPane.approveMultipleLoans" /]</a>
        </li>
    </ul>
