[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
<div class="sidebar htTotal">
[#include "adminLeftPane.ftl" ]
</div>
<!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  	<form method="" action="" name="formname">
  	<div class="marginLeft10">
  	<div class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "systemusers.managerolesandpermissions.rolesandpermissions"/]</span></div>
    <div class="span-18">  		
        <div class="clear">&nbsp;</div>
        <p class="font15"><span class=" orangeheading">[@spring.message "systemusers.managerolesandpermissions.rolesandpermissions"/] </span></p>
        <div>[@spring.message "systemusers.managerolesandpermissions.aroleisthename"/]<br /> [@spring.message "systemusers.managerolesandpermissions.nameandtaskpermissions"/] <a href="#">[@spring.message "systemusers.managerolesandpermissions.newRole"/]</a></div>
        <div class="clear">&nbsp;</div>
        <div class="span-22">
            <div class="borderbtm span-22 last"><span class="span-9 fontBold"><a href="#" >[@spring.message "admin"/]</a></span><span class="span-4 last"><a href="#">[@spring.message "systemusers.managerolesandpermissions.deleterole"/]</a></span></div>
        </div>
	</div>
	</div>
   	</form> 
  </div><!--Main Content Ends-->
  [@mifos.footer/]