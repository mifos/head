[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Left Sidebar Begins-->
  <div class="sidebar">
  [#include "adminLeftPane.ftl" ]
  </div> 
  <!--  Left Sidebar Ends-->
  <!--  Main Content Begins-->  
  <div class="content leftMargin180">
    <p class="orangeheading font15">[@spring.message "youhavesuccessfullyaddedanewproductcategory"/]</p>
    <p><span class="fontBold">[@spring.message "pleaseNoteProductcategoryhasbeenassignedthesystemIDnumber"/]</span><span class="fontBold">1-038 </span></p>
    <p class="fontBold"><a href="categoryDetails.html">[@spring.message "viewcategorydetailsnow"/]</a></p>
    <p><a href="defineNewCategory.ftl">[@spring.message "defineanewproductcategory"/]</a></p>
    
    
  </div><!--Main Content Ends-->
  [@mifos.footer/]