[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Left Sidebar Begins
  <div class="sidebar ht650">
  [#include "adminLeftPane.ftl" ]
  </div> 
  <!--  Left Sidebar Ends-->
  <!--  Main Content Begins-->  
  <div class="content leftMargin180">
  	<form method="post" action="/mifos/PersonAction.do?method=search" name="personActionForm">
  	<p class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "viewSystemUsers"/]</span></p>
  	<div class="marginLeft30">
    <div class="orangeheading">View system users</div>
    <ul></ul>
    <p class="paddingLeft">[@spring.message "searchUsersByName"/]<br />
      <input type="text" id="txtid" name="searchString"/>&nbsp;&nbsp;<input class="buttn" type="submit" name="search" value="Search"/>
    </p>
    </div>
   	</form> 
  </div><!--Main Content Ends-->
  [@mifos.footer /]