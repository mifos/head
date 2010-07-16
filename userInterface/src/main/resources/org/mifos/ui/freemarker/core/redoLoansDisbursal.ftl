[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
   <!--  Main Content Begins-->
  <div class="content marginAuto">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-5 arrowIMG orangeheading ">[@spring.message "selectCustomer" /]</p>
        <p class="span-5 arrowIMG1 orangeheading ">[@spring.message "loanaccountinformation" /]</p>
        <p class="span-5 arrowIMG1 orangeheading ">[@spring.message "review/editinstallments" /]</p>
        <p class="span-3 arrowIMG1 orangeheading last">[@spring.message "review&Submit" /]</p>
      </div>
      <div class="subcontent ">
      <form method="" action="" name="formname">
      	<div class="fontBold red">[@spring.message "note"/]</div>
        <p class="font15"><span class="fontBold">[@spring.message "redoLoansAccount" /]</span>&nbsp;-&nbsp;<span class=" orangeheading">[@spring.message "selectacustomer" /]</span></p>
        <p>[@spring.message "enteraClientorGroupnameandclickSearch" /]</p>
        <p>&nbsp;&nbsp;</p>
        <p class="error" id="cust_search_account.error.message"></p>
        <div class="prepend-6">
          <p>[@spring.message "name" /]&nbsp;&nbsp;
          <p>&nbsp;&nbsp;</p>
            <input id="cust_search_account.input.searchString" name="groupName" type="text" />
          </p>
          <p>&nbsp;&nbsp;</p>
          <div class="prepend-1">
            <input class="buttn " id="cust_search_account.button.search"  type="button" name="search" value="Search" onclick="#"/>
            <input class="buttn2" id="cust_search_account.button.cancel" type="button" name="cancel" value="Cancel" onclick="#"/>
          </div>
        </div>
        </form>
      </div>
    </div>
  </div>
  <!--Main Content Ends-->
  [@mifos.footer/]