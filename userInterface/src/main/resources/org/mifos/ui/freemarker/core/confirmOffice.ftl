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
    <p class="orangeheading">[@spring.message "offices.youhavesuccessfullyaddedanewoffice"/] </p>
    <p><span class="fontBold">[@spring.message "offices.pleasenoteOffice1officehasbeenassignedthesystemIDnumber"/]</span><span class="fontBold">0005 </span></p>
	<p><a href="officeDetails.html" class="fontBold">[@spring.message "offices.viewofficedetailsnow"/]</a></p>

    <div><a href="defineNewOffice.ftl">[@spring.message "offices.addanewoffice"/]</a></div>
  </div>
  <!--Main Content Ends-->
  [@mifos.footer/]