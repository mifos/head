[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
<div class="sidebar htTotal">
[#include "adminLeftPane.ftl" /]
</div> 
<!--  Main Content Begins-->  
  <div class=" content leftMargin180">
  	<form method="" action="" name="formname">
  	<p class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "viewreportscategory"/]</span></p>
    <p class="font15 orangeheading">[@spring.message "viewreportscategory"/]</p>
    <p><span>[@spring.message "clickonEdit/Deletetomakechangestoareportcategoryor"/] </span><a href="#">[@spring.message "addanewreportcategory"/] </a></p>
    <div>&nbsp;</div>
    <div class="span-18">
    	<div class="span-22  borderbtm paddingLeft">
        	<span class="span-17 fontBold ">[@spring.message "categoryName"/]</span>
            <span class="span-3 ">&nbsp;</span>
        </div>
        <div class="span-22  borderbtm paddingLeft ">
            <span class="span-17 fontBold ">[@spring.message "clientDetail"/]</span>
            <span class="span-2 rightAlign"><a href="#">[@spring.message "edit"/]</a>&nbsp;|&nbsp;<a href="#">[@spring.message "delete"/]</a></span>
        </div>
        <div class="span-22  borderbtm paddingLeft ">
            <span class="span-17 fontBold">[@spring.message "performance"/]</span>
            <span class="span-2 rightAlign"><a href="#">[@spring.message "edit"/]</a>&nbsp;|&nbsp;<a href="#">[@spring.message "delete"/]</a></span>
        </div>
        <div class="span-22  borderbtm paddingLeft ">
            <span class="span-17 fontBold">[@spring.message "center"/]</span>
            <span class="span-2 rightAlign"><a href="#">[@spring.message "edit"/]</a>&nbsp;|&nbsp;<a href="#">[@spring.message "delete"/]</a></span>
        </div>
        <div class="span-22  borderbtm paddingLeft ">
            <span class="span-17 fontBold">[@spring.message "loanProductDetail"/]</span>
            <span class="span-2 rightAlign"><a href="#">[@spring.message "edit"/]</a>&nbsp;|&nbsp;<a href="#">[@spring.message "delete"/]</a></span>
        </div>
        <div class="span-22  borderbtm paddingLeft ">
            <span class="span-17 fontBold">[@spring.message "status"/]</span>
            <span class="span-2 rightAlign"><a href="#">[@spring.message "edit"/]</a>&nbsp;|&nbsp;<a href="#">[@spring.message "delete"/]</a></span>
        </div>
        <div class="span-22  borderbtm paddingLeft ">
            <span class="span-17 fontBold">[@spring.message "analysis"/]</span>
            <span class="span-2 rightAlign"><a href="#">[@spring.message "edit"/]</a>&nbsp;|&nbsp;<a href="#">[@spring.message "delete"/]</a></span>
        </div>
        <div class="span-22  borderbtm paddingLeft ">
            <span class="span-17 fontBold">[@spring.message "miscellaneous"/]</span>
            <span class="span-2 rightAlign"><a href="#">[@spring.message "edit"/]</a>&nbsp;|&nbsp;<a href="#">[@spring.message "delete"/]</a></span>
        </div>
        
    </div>
   	</form> 
  </div><!--Main Content Ends-->
  [@mifos.footer/]