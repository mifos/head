[#ftl]
[#import "spring.ftl" as spring]
[#import "newblueprintmacros.ftl" as mifos]
<form name="custSearchActionForm" method="post" action="custSearchAction.do?method=loadAllBranches">
    <h3>[@spring.message "admin.administrativeTasks" /]</h3>
    <p class="form_row">
        <label for="searchString">
        [@spring.message "admin.searchbynamesystemIDoraccountnumber"/]
        </label>
        <input class="t_box" type="text" name="searchString" maxlength="200" size="15" value="">
      </p>
     <p class="form_button">
         <input class="buttn" type="submit" name="searchButton" value="[@spring.message "admin.search" /]" >
    </p>
</form>
