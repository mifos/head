[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[#-- <div class="left-pane">
	  <div class="left-pane-header">[@spring.message "administrativeTasks" /]</div>
	  <div class="left-pane-content">
   	  </div>
</div>--]
 <div  class="sidebar ht550">
  <div>
  <form name="custSearchActionForm" method="post" action="custSearchAction.do?method=loadAllBranches">
    <p class="orangetab">[@spring.message "admin.administrativeTasks" /]</p>
    <p class="paddingLeft marginTop20 marginLeft30 fontBold">[@spring.message "admin.searchbynamesystemIDoraccountnumber"/]<br />
    <input type="text" name="searchString" maxlength="200" size="20" value="">
      <br />
      <input type="submit" name="searchButton" value="[@spring.message "admin.search" /]" class="buttn floatRight">
    </p>
    </form>
  </div>
</div>