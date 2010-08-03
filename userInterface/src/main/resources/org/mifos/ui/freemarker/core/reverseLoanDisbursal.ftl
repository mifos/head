[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Left Sidebar Begins-->
  <div class="sidebar ht600">
  [#include "adminLeftPane.ftl" ]
  </div> 
  <!--  Left Sidebar Ends-->
  <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  	<form method="" action="" name="formname">
  	<p class="bluedivs paddingLeft"><a href="admin.html">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "manageLoanAccounts.reverseLoansdisbursal"/]</span></p>
    <p class="orangeheading font15">[@spring.message "manageLoanAccounts.reverseLoansdisbursal"/]</p>
    <ul><li class="error" > </li></ul>
    <p class="paddingLeft">[@spring.message "manageLoanAccounts.reverseLoansdisbursal.searchLoansaccountbyID"/]<br />
      <input type="text" id="txtid"/>&nbsp;&nbsp;<input class="buttn" type="button" name="search" value="Search" onclick="#" />
    </p>
	
   	</form> 
  </div><!--Main Content Ends-->
   [@mifos.footer/]