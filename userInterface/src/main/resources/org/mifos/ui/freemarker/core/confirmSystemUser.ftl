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
    <p class="orangeheading">[@spring.message "systemUser.youhavesuccessfullyaddedanewuser"/] </p>
    <div><span class="fontBold">[@spring.message "systemUser.pleaseNoteSystemUserhasbeenassignedthesystemIDnumber"/]</span><span class="fontBold">0003-00004 </span></div><br />
	<p><a href="systemUserProfile.html" class="fontBold">[@spring.message "systemUser.viewuserdetailsnow"/]</a></p>
    <div><a href="defineNewSystemUser.ftl">[@spring.message "systemUser.addanewuser"/]</a></div>
  </div>
  <!--Main Content Ends-->
  [@mifos.footer/]