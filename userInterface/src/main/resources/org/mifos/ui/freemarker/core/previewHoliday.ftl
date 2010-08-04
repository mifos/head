[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <div class="sidebar htTotal">
  [#include "adminLeftPane.ftl" /]
  </div> 
 <!--  Main Content Begins-->
 <span id="page.id" title="Review_holidayCreation" />  
 <div class=" content leftMargin180">
    <div class="span-24">
    	[@mifos.crumbs breadcrumbs /]
    	<p class="font15"><span class="orangeheading">[@spring.message "previewHoliday" /]</span></p>
    	<p>&nbsp;&nbsp;</p>
    	<div class="bluedivs span-24 fontBold paddingLeft">
    		<span class="span-3">From Date</span>
        	<span class="span-3">To Date</span>
        	<span class="span-5">Holiday Name</span>
        	<span class="span-5">Repayment Rule</span>
        	<span class="span-5">Applies To</span>
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
			<input type="submit" class="buttn2" name="EDIT" value="[@spring.message "editHolidayInformation"/]"/>
		</div>
        <hr />
        <div class="prepend-10">
            <input class="buttn" type="submit" id="holiday.button.submit" name="submit" value="[@spring.message "submit"/]" />
            <input class="buttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "cancel"/]"/>
        </div>
        </form> 
	</div>
 </div>
 [@mifos.footer/]