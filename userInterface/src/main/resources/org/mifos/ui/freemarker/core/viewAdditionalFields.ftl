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
  	<p class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "admin.viewAdditionalFields"/]</span></p>
  	<p>&nbsp;&nbsp;</p>
    <div class="orangeheading">[@spring.message "admin.viewAdditionalFields"/]</div>
    <p>[@spring.message "datadisplayandrules.viewadditionalfields.clickonacategorybelowtoviewandedittheadditionalfieldsdefinedforthatcategoryor"/]&nbsp;<a href="#">[@spring.message "datadisplayandrules.viewadditionalfields.addanewadditionalfield"/] </a></p>
    <div class="span-22">
    <p>&nbsp;&nbsp;</p>
    	<ul>
        	<li type="circle"><a href="#">[@spring.message "datadisplayandrules.viewadditionalfields.personnel"/]</a></li>
            <li type="circle"><a href="#">[@spring.message "datadisplayandrules.viewadditionalfields.office"/]</a></li>
            <li type="circle"><a href="#">[@spring.message "datadisplayandrules.viewadditionalfields.client"/]</a></li>
            <li type="circle"><a href="#">[@spring.message "datadisplayandrules.viewadditionalfields.group"/]</a></li>
            <li type="circle"><a href="#">[@spring.message "datadisplayandrules.viewadditionalfields.center"/]</a></li>
            <li type="circle"><a href="#">[@spring.message "datadisplayandrules.viewadditionalfields.loan"/]</a></li>
            <li type="circle"><a href="#">[@spring.message "datadisplayandrules.viewadditionalfields.savings"/]</a></li>
        </ul>	
    </div>
   	</form> 
  </div><!--Main Content Ends-->
  [@mifos.footer/] 