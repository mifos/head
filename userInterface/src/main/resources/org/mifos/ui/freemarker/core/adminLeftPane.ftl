[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
<!-- <div class="left-pane">
	  <div class="left-pane-header">[@spring.message "administrativeTasks" /]</div>
	  <div class="left-pane-content">
   	  </div>
</div>-->
 <div  class="sidebar ht750">
  <div>
    <p class="orangetab">[@spring.message "administrativeTasks" /]</p>
    <p class="paddingLeft marginTop10">[@spring.message "searchbynamesystemIDoraccountnumber"/]<br />
      <input type="text" id="txt" size="15"/>
      <br />
      <input class="buttn floatRight" type="button" name="search" value="[@spring.message "search" /]" onclick="#" />
    </p>
  </div>
</div>