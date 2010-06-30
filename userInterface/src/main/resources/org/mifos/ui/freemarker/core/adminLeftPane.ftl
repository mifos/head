[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
<!-- <div class="left-pane">
	  <div class="left-pane-header">[@spring.message "administrativeTasks" /]</div>
	  <div class="left-pane-content">
   	  </div>
</div>-->
 <div  class="sidebar ht600">
  <div>
  <form name="custSearchActionForm" method="post"
                              action="/mifos/custSearchAction.do?method=loadAllBranches">
    <p class="orangetab">[@spring.message "administrativeTasks" /]</p>
    <p class="paddingLeft marginTop10 fontBold">[@spring.message "searchbynamesystemIDoraccountnumber"/]<br />
    <input type="text" name="searchString" maxlength="200" size="15" value="">
      <br />
      <input type="submit" name="searchButton" value="[@spring.message "search" /]" class="buttn floatRight">
    </p>
    </form>
  </div>
</div>