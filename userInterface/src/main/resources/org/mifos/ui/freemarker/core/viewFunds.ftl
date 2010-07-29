[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <div class="sidebar ht600">
  [#include "adminLeftPane.ftl" ]
  </div>  
   <!--  Main Content Begins-->  
  <div class=" content leftMargin180">
    <span id="page.id" title="viewFunds" />
    [@mifos.crumb "viewFunds"/]
    
    <p class="font15 orangeheading">[@spring.message "viewfunds"/]</p>
    <p>&nbsp;&nbsp;</p> 
    <div class="span-15" id="fundDetailsList">
    
    	<div class="span-15 fontBold borderbtm paddingLeft">
     		<span class="span-5 ">[@spring.message "name"/]</span>
           	<span class="span-8">[@spring.message "fundCode"/]</span>
           	<span class="span-1 ">&nbsp;</span>
        </div>

    	[#list fundsList as fund]
		<div class="span-15 borderbtm paddingLeft">
        	<span class="span-5 ">${fund.name}</span>
           	<span class="span-8">${fund.code.value}</span>
           	<span class="span-1 "><a class="floatRT" href="editFunds.ftl?fundId=${fund.id}">[@spring.message "edit"/]</a></span>
        </div>    		
        [/#list] 
    </div>
 </div><!--Main Content Ends-->  
[@mifos.footer/]