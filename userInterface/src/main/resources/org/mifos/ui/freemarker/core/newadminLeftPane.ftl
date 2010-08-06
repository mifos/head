[#ftl]
[#import "spring.ftl" as spring]
[#import "newblueprintmacros.ftl" as mifos]
<!-- <div class="left-pane">
	  <div class="left-pane-header">[@spring.message "administrativeTasks" /]</div>
	  <div class="left-pane-content">
   	  </div>
</div>-->

<form name="custSearchActionForm" method="post" action="custSearchAction.do?method=loadAllBranches">
    <h3>[@spring.message "administrativeTasks" /]</h3>
    <p class="form_row">
        <label for="searchString">
        [@spring.message "searchbynamesystemIDoraccountnumber"/]
        </label>
        <input class="t_box" type="text" name="searchString" maxlength="200" size="15" value="">
      </p>
     <p class="form_button">
         <input class="buttn" type="submit" name="searchButton" value="[@spring.message "search" /]" >
    </p>
</form>
