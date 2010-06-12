[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]
<!-- <div class="left-pane">
	  <div class="left-pane-header">[@spring.message "administrativeTasks" /]</div>
	  <div class="left-pane-content">
   	  </div>
</div>-->
 <div>
  <div class="sidebar ht600">
    <p class="orangetab">[@spring.message "administrativeTasks" /]</p>
    <p class="paddingLeft">[@spring.message "searchbynamesystemIDoraccountnumber"/]<br />
      <input type="text" id="txt" />
      <br />
      <input class="buttn" type="button" name="search" value="[@spring.message "search" /]" onclick="#" />
    </p>
  </div>
</div>