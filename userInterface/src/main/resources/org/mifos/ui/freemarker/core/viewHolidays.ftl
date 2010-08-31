[#ftl]
[#--
* Copyright (c) 2005-2010 Grameen Foundation USA
*  All rights reserved.
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
*  implied. See the License for the specific language governing
*  permissions and limitations under the License.
*
*  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
*  explanation of the license and how it is applied.
--]
[#include "layout.ftl"]
[@adminLeftPaneLayout]
  <!--  Main Content Begins-->
  <span id="page.id" title="view_organizational_holidays" />
  <div class=" content">
  
    [@mifos.crumbs breadcrumbs /]
  	
      <div class="span-24">        
        <div class="clear">&nbsp;</div>
        <div class="marginLeft30">
        <p class="font15"><span class="fontBold">[@spring.message "organizationPreferences.viewholidays"/]</span>&nbsp;-&nbsp;<span class="orangeheading">[@spring.message "organizationPreferences.viewholidays.organizationWide" /]</span></p>
        <p>[@spring.message "organizationPreferences.viewholidays.belowIsTheListOfOrganizationWideHolidaysClickHereTo"/]<a id="holiday.link.defineNewHoliday" href="defineNewHoliday.ftl">[@spring.message "organizationPreferences.viewholidays.defineNewHoliday"/]</a> </p>
        
      [#list holidaysMap?keys as key]
      	<p class="fontBold">Holidays for year ${key}</p>
      	<div class="span-22"> 
        	<div class="bluedivs span-20 fontBold paddingLeft">
        		<span class="span-3">[@spring.message "organizationPreferences.definenewholiday.fromDat"/]</span>
            	<span class="span-3">[@spring.message "organizationPreferences.definenewholiday.toDate"/]</span>
            	<span class="span-5">[@spring.message "organizationPreferences.definenewholiday.holidayName"/]</span>
            	<span class="span-5">[@spring.message "organizationPreferences.definenewholiday.repaymentRule"/]</span>
            	<span class="span-5">[@spring.message "organizationPreferences.definenewholiday.appliesto"/]</span>
        	</div>
      		[#list holidaysMap[key] as officeHoliday]
	        	<div class="span-20 borderbtm paddingLeft ">
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
[/@adminLeftPaneLayout]