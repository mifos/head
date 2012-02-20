[#ftl]
[#--
* Copyright (c) 2005-2011 Grameen Foundation USA
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
[@adminLeftPaneLayout]<!--Container Begins-->

<script>
$(document).ready(function() {

    $(":regex(id, monthClosingDate)").datepicker({
    	[#if monthClosingFormBean.locale.language?lower_case == "zh"]
			dateFormat: 'y-m-d',
		[/#if]
		[#if monthClosingFormBean.locale.language?lower_case == "en"]
			[#if monthClosingFormBean.locale.country?lower_case == "us"]
			dateFormat: 'mm/dd/y',
			[#else]
			dateFormat: 'dd/mm/y',
			[/#if]
		[/#if]
        showOn: "button",
        buttonImage: "pages/framework/images/mainbox/calendaricon.gif",
		buttonImageOnly: true
    });
  }
);
	$(function() {
		[#if monthClosingFormBean.locale.language == "en"]
			$.datepicker.setDefaults($.datepicker.regional['']);
		[#else]
			$.datepicker.setDefaults($.datepicker.regional['${monthClosingFormBean.locale.language?lower_case}']);
		[/#if]
	});
</script>

<span id="page.id" title="monthClosing"></span>
<!--  Main Content Begins-->
<div class="content">
    <form method="post" name="monthClosing" action="monthClosing.ftl">
        [@widget.crumbs breadcrumbs /]
        [@spring.bind "monthClosingFormBean" /]
        <div class="marginLeft30">
            <div class="span-24">
                <div class="clear">&nbsp;</div>
                <div class="span-23 borderbtm">
                    <p class="font15"><span class="orangeheading">[@spring.message "systemAdministration.monthClosing.monthClosingInformation" /]</span></p>
                    <div class="span-23">
                        <span class="span-11">[@spring.message "systemAdministration.monthClosing.welcometotheMifosMonthClosingManagementArea" /].</span>
                    </div>
                    [@form.showAllErrors "monthClosingFormBean.*"/]
                    <div class="clear">&nbsp;</div>
                    <div class="span-23">
                        <span class="span-7">[@spring.message "systemAdministration.monthClosing.currentMonthClosingDate" /]</span>
                    <span class="span-11" id="monthClosingCurrentDate">${model.currentDate}</span>
                    </div>
                    <div class="span-23">
                        <span class="span-7">[@spring.message "systemAdministration.monthClosing.changeTo" /]</span>
                    <span class="span-11">
                        [@spring.bind "monthClosingFormBean.date" /]
                        <input type="text" name="${spring.status.expression}" size="10" value="${monthClosingFormBean.getFormattedDate()}" id="monthClosingDate" class="date-pick" />
                    </span>
                    </div>
                    <div class="clear">&nbsp;</div>
                </div>
                <div class="clear">&nbsp;</div>
                <div class="prepend-9">
                    <input class="buttn"  type="submit" name="CHANGE" value="[@spring.message "systemAdministration.monthClosing.change"/]"/>
                </div>
            </div>
        </div>
    </form>
</div><!--Main Content Ends-->
[/@adminLeftPaneLayout]