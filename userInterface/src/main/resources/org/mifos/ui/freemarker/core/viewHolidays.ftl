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
[@adminLeftPaneLayout]
<!--  Main Content Begins-->
<span id="page.id" title="view_organizational_holidays"></span>
<div class=" content">

[@widget.crumbs breadcrumbs /]

    <div class="span-24">
        <div class="clear">&nbsp;</div>
        <div class="marginLeft15">
            <p class="font15"><span class="fontBold">[@spring.message "organizationPreferences.viewholidays"/]</span>&nbsp;-&nbsp;<span
                    class="orangeheading">[@spring.message "organizationPreferences.viewholidays.organizationWide" /]</span>
            </p>

            <p class="padding5topbottom">[@spring.message "organizationPreferences.viewholidays.belowIsTheListOfOrganizationWideHolidaysClickHereTo"/]
                <a id="holiday.link.defineNewHoliday"
                   href="defineNewHoliday.ftl">[@spring.message "organizationPreferences.viewholidays.defineNewHoliday"/]</a>
            </p>

            [#list holidaysMap?keys as key]
            <p class="fontBold padding10topbottom">Holidays for year ${key}</p>
            <table id="organizational_holidays_${key}" style="line-height:1.2;margin-bottom:5px; border-bottom:1px solid #D7DEEE;" cellspacing="0" cellpadding="3" border="0" width="98%">
                <tbody>
                <tr>
                    <td width="11%" class="drawtablehd">[@spring.message "organizationPreferences.definenewholiday.fromDat"/]</td>
                    <td width="11%" class="drawtablehd">[@spring.message "organizationPreferences.definenewholiday.toDate"/]</td>
                    <td width="20%" class="drawtablehd">[@spring.message "organizationPreferences.definenewholiday.holidayName"/]</td>
                    <td width="15%" class="drawtablehd">[@spring.message "organizationPreferences.definenewholiday.repaymentRule"/]</td>
                    <td width="43%" class="drawtablehd">[@spring.message "organizationPreferences.definenewholiday.appliesto"/]</td>
                </tr>
                [#list holidaysMap[key] as officeHoliday]
                <tr>
                    <td width="11%" class="drawtablerow">${officeHoliday.holidayDetails.fromDate}</td>
                    <td width="11%" class="drawtablerow">${officeHoliday.holidayDetails.thruDate}</td>
                    <td width="20%" class="drawtablerow">${officeHoliday.holidayDetails.name}</td>
                    <td width="15%" class="drawtablerow">${officeHoliday.holidayDetails.repaymentRuleName}</td>
                    <td width="43%" class="drawtablerow">${officeHoliday.officeNamesAsString}</td>
                </tr>
                [/#list]
                </tbody>
            </table>
           [/#list]
        </div>
    </div>
    [@form.returnToPage  "AdminAction.do?method=load" "button.back" "viewholidays.button.back"/]
</div>
<!--Main Content Ends-->
[/@adminLeftPaneLayout]