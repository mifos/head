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
  	<p class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "viewAdditionalFields"/]</span></p>
  	<p>&nbsp;&nbsp;</p>
    <div class="orangeheading">[@spring.message "viewAdditionalFields"/]</div>
    <p>[@spring.message "clickonacategorybelowtoviewandedittheadditionalfieldsdefinedforthatcategoryor"/]&nbsp;<a href="#">[@spring.message "addanewadditionalfield"/] </a></p>
    <div class="span-22">
    <p>&nbsp;&nbsp;</p>
    	<ul>
        	<li type="circle"><a href="#">[@spring.message "personnel"/]</a></li>
            <li type="circle"><a href="#">[@spring.message "office"/]</a></li>
            <li type="circle"><a href="#">[@spring.message "client"/]</a></li>
            <li type="circle"><a href="#">[@spring.message "group"/]</a></li>
            <li type="circle"><a href="#">[@spring.message "center"/]</a></li>
            <li type="circle"><a href="#">[@spring.message "loan"/]</a></li>
            <li type="circle"><a href="#">[@spring.message "savings"/]</a></li>
        </ul>	
    </div>
   	</form> 
  </div><!--Main Content Ends-->
  [@mifos.footer/] 