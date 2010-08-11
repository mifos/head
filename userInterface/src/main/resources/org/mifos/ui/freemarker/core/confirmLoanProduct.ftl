[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[#import "macros.ftl" as mifosmacros]
[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
 <!--  Left Sidebar Begins-->
  <div class="sidebar">
  [#include "adminLeftPane.ftl" ]
  </div> 
  <!--  Left Sidebar Ends-->
   <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
    <p class="font15 orangeheading">[@spring.message "manageLoanProduct.youhavesuccessfullyaddedanewLoanproduct"/] </p>
    <p><span class="fontBold">[@spring.message "manageLoanProduct.pleaseNoteLoanProducthasbeenassignedthesystemIDnumber"/]</span><span class="fontBold">1-002 </span></p>
    <p class="fontBold"><a href="loanProductDetails.html">[@spring.message "manageLoanProduct.viewLoanproductdetailsnow"/]</a></p>
    <p><a href="defineLoanProducts.ftl">[@spring.message "manageLoanProduct.defineanewLoanproduct"/]</a></p>
  </div>
  <!--Main Content Ends-->
  [@mifos.footer/]
  