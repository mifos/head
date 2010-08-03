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
  <span id="page.id" title="view_organizational_holidays" />
  <div class=" content leftMargin180">
  
    [@mifos.crumbs breadcrumbs /]
  	
      <div class="span-24">        
        <div class="clear">&nbsp;</div>
        <div class="marginLeft30">
        <p class="font15"><span class="fontBold">[@spring.message "viewHolidays"/]</span>&nbsp;-&nbsp;<span class="orangeheading">[@spring.message "organizationWide" /]</span></p>
        <p>[@spring.message "belowIsTheListOfOrganizationWideHolidaysClickHereTo"/]<a href="defineNewHoliday.ftl">[@spring.message "defineNewHoliday"/]</a> </p>
        
      [#list holidaysMap?keys as key]
      	<p class="fontBold">Holidays for year ${key}</p>
      	<div class="span-22"> 
        	<div class="bluedivs span-22 fontBold paddingLeft">
        		<span class="span-3">From Date</span>
            	<span class="span-3">To Date</span>
            	<span class="span-5">Holiday Name</span>
            	<span class="span-5">Repayment Rule</span>
            	<span class="span-5">Applies To</span>
        	</div>
      		[#list holidaysMap[key] as officeHoliday]
	        	<div class="span-22 borderbtm paddingLeft ">
	        		<span class="span-3">${officeHoliday.holidayDetails.fromDate}</span>
	            	<span class="span-3">${officeHoliday.holidayDetails.thruDate}</span>
	            	<span class="span-5">${officeHoliday.holidayDetails.name}</span>
	            	<span class="span-5">${officeHoliday.holidayDetails.repaymentRuleName}</span>
	            	<span class="span-5">${officeHoliday.officeNamesAsString}</span>
	    	    </div>
      		[/#list]
      	</div>
		<div class="clear">&nbsp;</div>	    
      [/#list]
    	</div>
      </div>
   </div>  
  <!--Main Content Ends-->
  [@mifos.footer/]