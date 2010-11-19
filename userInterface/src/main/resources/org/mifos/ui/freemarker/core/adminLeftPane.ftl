[#ftl]
[#import "spring.ftl" as spring]
[#import "newblueprintmacros.ftl" as mifos]
[#-- <div class="left-pane">
	  <div class="left-pane-header">[@spring.message "administrativeTasks" /]</div>
	  <div class="left-pane-content">
   	  </div>
</div>--]
 <div  class="sidebar ht550">
  <div>
  <form name="custSearchActionForm" method="post" action="custSearchAction.do?method=loadAllBranches">
    <p class="orangetab">[@spring.message "admin.administrativeTasks" /]</p>
    <p class="leftpanelform fontnormal8ptbold">[@spring.message "admin.searchbynamesystemIDoraccountnumber"/]<br />
    <input type="text" class="t_box" name="searchString" maxlength="200" size="20" value="" style="margin-left:0px;margin-top:4px;">
      <br />
      <input type="submit" name="searchButton" value="[@spring.message "admin.search" /]" class="buttn floatRight" style="margin-right:16px;
margin-top:5px;">
    </p>
    </form>
  </div>
</div>