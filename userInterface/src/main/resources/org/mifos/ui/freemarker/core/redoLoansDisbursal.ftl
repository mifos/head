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
[@layout.header "title" /]
  [@widget.topNavigationNoSecurity currentTab="Admin" /]
   <!--  Main Content Begins-->
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-5 arrowIMG orangeheading ">[@spring.message "manageLoanAccounts.redoLoanDisbursal.selectCustomer" /]</p>
        <p class="span-5 arrowIMG1 orangeheading ">[@spring.message "manageLoanAccounts.redoLoanDisbursal.loanaccountinformation" /]</p>
        <p class="span-5 arrowIMG1 orangeheading ">[@spring.message "manageLoanAccounts.redoLoanDisbursal.review/editinstallments" /]</p>
        <p class="span-3 arrowIMG1 orangeheading last">[@spring.message "reviewAndSubmit" /]</p>
      </div>
      <div class="subcontent ">
      <form method="" action="" name="formname">
          <div class="fontBold red">[@spring.message "note"/]</div>
        <p class="font15"><span class="fontBold">[@spring.message "manageLoanAccounts.redoLoanDisbursal.redoLoansAccount" /]</span>&nbsp;-&nbsp;<span class=" orangeheading">[@spring.message "manageLoanAccounts.redoLoanDisbursal.selectacustomer" /]</span></p>
        <p>[@spring.message "manageLoanAccounts.redoLoanDisbursal.enteraClientorGroupnameandclickSearch" /]</p>
        <p>&nbsp;&nbsp;</p>
        <p class="error" id="cust_search_account.error.message"></p>
        <div class="prepend-6">
          <p>[@spring.message "name" /]&nbsp;&nbsp;
          <p>&nbsp;&nbsp;</p>
            <input id="cust_search_account.input.searchString" name="groupName" type="text" />
          </p>
          <p>&nbsp;&nbsp;</p>
          <div class="prepend-1">
            <input class="buttn " id="cust_search_account.button.search"  type="button" name="search" value="[@spring.message "admin.search"/]" onclick="#"/>
            <input class="buttn2" id="cust_search_account.button.cancel" type="button" name="cancel" value="[@spring.message "cancel"/]" onclick="#"/>
          </div>
        </div>
        </form>
      </div>
    </div>
  </div>
  <!--Main Content Ends-->
  [@layout.footer/]