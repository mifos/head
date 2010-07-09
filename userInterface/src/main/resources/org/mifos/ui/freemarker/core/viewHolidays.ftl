[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Left Sidebar Begins
  <div class="sidebar ht650">
  [#include "adminLeftPane.ftl" ]
  </div> 
  <!--  Left Sidebar Ends-->
  <!--  Main Content Begins-->
  <div class=" content leftMargin180">
  	<form method="" action="" name="formname">
  	<div class="bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "admin"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "viewHolidaysOrganizationWide" /]</span></div>
      <div class="span-24">        
        <div class="clear">&nbsp;</div>
        <div class="marginLeft30">
        <p class="font15"><span class="fontBold">[@spring.message "viewHolidays"/]</span>&nbsp;-&nbsp;<span class="orangeheading">[@spring.message "organizationWide" /]</span></p>
        <p>[@spring.message "belowIsTheListOfOrganizationWideHolidaysClickHereTo"/]<a href="holidayAction.do?method=addHoliday">[@spring.message "defineNewHoliday"/]</a> </p>
        <p class="fontBold">Holidays for year 2008</p>
        <div class="span-22"> 
        	<div class="bluedivs span-22 fontBold paddingLeft">
        		<span class="span-3 ">From Date</span>
            	<span class="span-3">To Date</span>
            	<span class="span-7 ">Holiday Name</span>
            	<span class="span-8 ">Repayment Rule</span>
        	</div>
        	<div class="span-22 borderbtm paddingLeft ">
        		<span class="span-3 ">2008-12-24</span>
            	<span class="span-3">2008-12-25</span>
            	<span class="span-7 ">Christmas Holiday</span>
            	<span class="span-8 ">Next Meeting/Repayment</span>
    	    </div>
        	
    	</div>
        <div class="clear">&nbsp;</div>
        <p class="span-22 fontBold">Holidays for year 2010</p>
        <div class="span-22"> 
        	<div class="bluedivs span-22 fontBold paddingLeft">
        		<span class="span-3 ">From Date</span>
            	<span class="span-3">To Date</span>
            	<span class="span-7 ">Holiday Name</span>
            	<span class="span-8 ">Repayment Rule</span>
        	</div>
        	<div class="span-22 borderbtm paddingLeft ">
        		<span class="span-3 ">2010-01-01</span>
            	<span class="span-3">2010-01-01</span>
            	<span class="span-7 ">New Year</span>
            	<span class="span-8 ">Next Working Day</span>
    	    </div>                  	
    	</div>
    	</div>
      </div>
    </form>
   </div>  
  <!--Main Content Ends-->
  [@mifos.footer/]