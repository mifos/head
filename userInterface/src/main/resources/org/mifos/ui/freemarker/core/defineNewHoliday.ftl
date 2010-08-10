[#ftl]	
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[#import "macros.ftl" as mifosMacros]

[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <div class="sidebar htTotal">
  [#include "adminLeftPane.ftl" /]
  </div> 
 <!--  Main Content Begins-->
 <script type="text/javascript" src="pages/js/jquery/jquery-1.4.2.min.js"></script>
 <script type="text/javascript" src="pages/js/jstree/jquery.jstree.js"></script>
 <script type="text/javascript" src="pages/application/holiday/js/createHolidays.js"></script>
 <span id="page.id" title="create_officeHoliday" />  
 <div class=" content leftMargin180">
    <div class="span-24">
    	[@mifos.crumbs breadcrumbs /]
    	<p class="font15"><span class="orangeheading">[@spring.message "organizationPreferences.definenewholiday.addHoliday" /]</span></p>
    	<p>&nbsp;&nbsp;</p>
        <div><span class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired" /] </div>
        
        <form method="post" action="defineNewHoliday.ftl" name="formname">
        <div id="allErrorsDiv" class="allErrorsDiv">
        	[@spring.bind "formBean" /]
  			[@spring.showErrors "<br />" /]
        </div>
        <p>&nbsp;&nbsp;</p>
                <div class="prepend-3 span-22 last">
        	<span class="span-4 rightAlign"><span class="red"> * </span>[@spring.message "organizationPreferences.definenewholiday.holidayName" /]</span>
        	<span class="span-4">
        	[@spring.bind "formBean.name" /]
		    <input type="text" id="holiday.input.name" name="${spring.status.expression}" value="${spring.status.value?default("")}" size="45" />
        	</span>
        </div>
        <p>&nbsp;&nbsp;</p>
        <div class="prepend-3 span-22 last">
            <span class="span-4 rightAlign"><span class="red"> * </span>[@spring.message "organizationPreferences.definenewholiday.fromDat" /] </span>
            [@spring.bind "formBean.fromDay" /]
            <span class="span-2"><input type="text" id="holidayFromDateDD" size="1" maxlength="2" name="${spring.status.expression}" value="${spring.status.value?default("")}" />[@spring.message "organizationPreferences.definenewholiday.DD" /]</span>
            [@spring.bind "formBean.fromMonth" /]
            <span class="span-2"><input type="text" id="holidayFromDateMM" size="1" maxlength="2" name="${spring.status.expression}" value="${spring.status.value?default("")}" />[@spring.message "organizationPreferences.definenewholiday.MM" /]</span>
            [@spring.bind "formBean.fromYear" /]
            <span class="span-3"><input type="text" id="holidayFromDateYY" size="2" maxlength="4" name="${spring.status.expression}" value="${spring.status.value?default("")}" />[@spring.message "organizationPreferences.definenewholiday.YYYY" /]</span>
        </div>
        <p>&nbsp;&nbsp;</p>
        <div class="prepend-3 span-22 last">
            <span class="span-4 rightAlign">[@spring.message "organizationPreferences.definenewholiday.toDate"/]</span>
            [@spring.bind "formBean.toDay" /]
            <span class="span-2"><input type="text" id="holidayThruDateDD" size="1" maxlength="2" name="${spring.status.expression}" value="${spring.status.value?default("")}" />[@spring.message "organizationPreferences.definenewholiday.DD" /]</span>
            [@spring.bind "formBean.toMonth" /]
            <span class="span-2"><input type="text" id="holidayThruDateMM" size="1" maxlength="2" name="${spring.status.expression}" value="${spring.status.value?default("")}" />[@spring.message "organizationPreferences.definenewholiday.MM" /]</span>
            [@spring.bind "formBean.toYear" /]
            <span class="span-3"><input type="text" id="holidayThruDateYY" size="2" maxlength="4" name="${spring.status.expression}" value="${spring.status.value?default("")}" />[@spring.message "organizationPreferences.definenewholiday.YYYY" /]</span>
        </div>
        <p>&nbsp;&nbsp;</p>
        <div class="prepend-3 span-22 last">
            <span class="span-4 rightAlign">
            	<span class="red"> * </span>[@spring.message "organizationPreferences.definenewholiday.repaymentRule" /]</span>
            	<span class="span-5">
            	    [@spring.bind "formBean.repaymentRuleId" /]
				    <select id="holiday.input.repaymentrule" name="${spring.status.expression}">
				        <option value="-1" [@spring.checkSelected ""/]>${springMacroRequestContext.getMessage("--Select--")}</option>
				        [#if formBean.repaymentRuleOptions?is_hash]
				            [#list formBean.repaymentRuleOptions?keys as value]
				            <option value="${value?html}"[@spring.checkSelected value/]>${springMacroRequestContext.getMessage(formBean.repaymentRuleOptions[value]?html)}</option>
				            [/#list]
				        [#else]
				            [#list formBean.repaymentRuleOptions as value]
				            <option value="${value?html}"[@spring.checkSelected value/]>${springMacroRequestContext.getMessage(value?html)}</option>
				            [/#list]
				        [/#if]
				    </select>
				</span>
        </div>
        <p>&nbsp;&nbsp;</p>
        <div class="prepend-3 span-22 last">
            <span class="span-4 rightAlign"><span class="red"> * </span>[@spring.message "organizationPreferences.definenewholiday.appliesto" /] &nbsp;:</span>
            <span class="span-5">
            	    <div id="officeTree">
					</div>
            </span>
            [@spring.bind "formBean.selectedOfficeIds" /]
            <input type="hidden" id="selectedOfficeIds" name="${spring.status.expression}" value="${spring.status.value?default("")}" />
        </div>
        <div class="clear">&nbsp;</div>
        <hr />
        <div class="prepend-10">
            <input class="buttn" type="submit" id="holiday.button.preview" name="preview"  value="Preview" />
            <input class="buttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "cancel"/]"/>
        </div>
        </form> 
	</div>
 </div>
 [@mifos.footer/]