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
[@adminLeftPaneLayout] <!--  Main Content Begins-->
 <span id="page.id" title="Review_holidayCreation" />  
 <div class=" content leftMargin180">
    <div class="span-24">
    	[@mifos.crumbs breadcrumbs /]
    	<p class="font15"><span class="orangeheading">[@spring.message "organizationPreferences.previewHoliday" /]</span></p>
    	<p>&nbsp;&nbsp;</p>
    	<div class="bluedivs span-24 fontBold paddingLeft">
    		<span class="span-3">[@spring.message "organizationPreferences.definenewholiday.fromDat"/]</span>
        	<span class="span-3">[@spring.message "organizationPreferences.definenewholiday.toDate"/]</span>
        	<span class="span-5">[@spring.message "organizationPreferences.definenewholiday.holidayName"/]</span>
        	<span class="span-5">[@spring.message "organizationPreferences.definenewholiday.repaymentRule"/]</span>
        	<span class="span-5">[@spring.message "organizationPreferences.definenewholiday.appliesto"/]</span>
    	</div>
		<div class="span-24 borderbtm paddingLeft ">
    		<span class="span-3">${officeHoliday.holidayDetails.fromDate}</span>
        	<span class="span-3">${officeHoliday.holidayDetails.thruDate}</span>
        	<span class="span-5">${officeHoliday.holidayDetails.name}</span>
        	<span class="span-5">${officeHoliday.holidayDetails.repaymentRuleName}</span>
        	<span class="span-5">${officeHoliday.officeNamesAsString}</span>
	    </div>

        <form method="post" action="previewHoliday.ftl" name="formname">
        [@spring.bind "formBean.name" /]
		<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}" />
        [@spring.bind "formBean.fromDay" /]
        <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}" />
        [@spring.bind "formBean.fromMonth" /]
        <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}" />
        [@spring.bind "formBean.fromYear" /]
        <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}" />
        
        [@spring.bind "formBean.toDay" /]
        <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}" />
        [@spring.bind "formBean.toMonth" /]
        <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}" />
        [@spring.bind "formBean.toYear" /]
        <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}" />

		[@spring.bind "formBean.repaymentRuleId" /]
		<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}" />
					
        [@spring.bind "formBean.selectedOfficeIds" /]
        <input type="hidden" id="selectedOfficeIds" name="${spring.status.expression}" value="${spring.status.value?default("")}" />
        
        <br />
        <div class="prepend-1">
			<input type="submit" class="buttn2" name="EDIT" value="[@spring.message "organizationPreferences.editHolidayInformation"/]"/>
		</div>
        <hr />
        <div class="prepend-10">
            <input class="buttn" type="submit" id="holiday.button.submit" name="submit" value="[@spring.message "submit"/]" />
            <input class="buttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "cancel"/]"/>
        </div>
        </form> 
	</div>
 </div>
 [/@adminLeftPaneLayout]