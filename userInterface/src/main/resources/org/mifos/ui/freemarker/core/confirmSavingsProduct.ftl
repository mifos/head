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
    <p class="font15 orangeheading">[@spring.message "manageSavingsProduct.youhavesuccessfullyaddedanewSavingsproduct"/]</p>
    <p><span class="fontBold">[@spring.message "manageSavingsProduct.pleaseNoteSavingsProducthasbeenassignedthesystemIDnumber"/]</span><span class="fontBold">1-066 </span></p>
    <p class="fontBold"><a href="savingsProductDetails.html">[@spring.message "manageSavingsProduct.viewSavingsproductdetailsnow"/]</a></p>
    <p><a href="defineSavingsProduct.ftl">[@spring.message "manageSavingsProduct.defineanewSavingsproduct"/]</a></p>
  </div><!--Main Content Ends-->
   [@mifos.footer/]