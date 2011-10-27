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
[@adminLeftPaneLayout] <!--  Main Content Begins-->
<span id="page.id" title="Review_holidayCreation"></span>
<div class="content">
    <div class="span-24">
    [@widget.crumbs breadcrumbs /]
        <div class="margin20lefttop">
            <p class="font15 margin15bottom"><span
                    class="orangeheading">[@spring.message "organizationPreferences.previewHoliday" /]</span></p>
            [#if otherHolidays?size > 0]
                <p class="error">[@spring.message "organizationPreferences.otherHolidaysWithTheSameDate" /]: [#list otherHolidays as holiday][#if holiday_index != 0], [/#if]<strong>${holiday}</strong>[/#list]</p>
            [/#if]

            <table class="lineheight1p2" cellspacing="0" cellpadding="3" border="0" width="98%">
                <tbody>
                <tr>
                    <td width="11%" class="drawtablehd">[@spring.message "organizationPreferences.definenewholiday.fromDat"/]</td>
                    <td width="11%" class="drawtablehd">[@spring.message "organizationPreferences.definenewholiday.toDate"/]</td>
                    <td width="20%" class="drawtablehd">[@spring.message "organizationPreferences.definenewholiday.holidayName"/]</td>
                    <td width="20%" class="drawtablehd">[@spring.message "organizationPreferences.definenewholiday.repaymentRule"/]</td>
                    <td width="38%" class="drawtablehd">[@spring.message "organizationPreferences.definenewholiday.appliesto"/]</td>
                </tr>
                <tr>
                    <td width="11%" class="drawtablerow">${officeHoliday.holidayDetails.fromDate}</td>
                    <td width="11%" class="drawtablerow">${officeHoliday.holidayDetails.thruDate}</td>
                    <td width="20%" class="drawtablerow">${officeHoliday.holidayDetails.name}</td>
                    <td width="20%" class="drawtablerow">${officeHoliday.holidayDetails.repaymentRuleName}</td>
                    <td width="38%" class="drawtablerow">${officeHoliday.officeNamesAsString}</td>
                </tr>
                <tr>
                    <td width="11%" class="drawtablerow">&nbsp;</td>
                    <td width="11%" class="drawtablerow">&nbsp;</td>
                    <td width="20%" class="drawtablerow">&nbsp;</td>
                    <td width="20%" class="drawtablerow">&nbsp;</td>
                    <td width="38%" class="drawtablerow">&nbsp;</td>
                </tr>

                </tbody>
            </table>

            <form method="post" action="previewHoliday.ftl" name="formname">
            [@spring.bind "formBean.name" /]
                <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
            [@spring.bind "formBean.fromDay" /]
                <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
            [@spring.bind "formBean.fromMonth" /]
                <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
            [@spring.bind "formBean.fromYear" /]
                <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>

            [@spring.bind "formBean.toDay" /]
                <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
            [@spring.bind "formBean.toMonth" /]
                <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
            [@spring.bind "formBean.toYear" /]
                <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>

            [@spring.bind "formBean.repaymentRuleId" /]
                <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>

            [@spring.bind "formBean.selectedOfficeIds" /]
                <input type="hidden" id="selectedOfficeIds" name="${spring.status.expression}"
                       value="${spring.status.value?default("")}"/>


                 <input type="submit" class="buttn2" style="height:15px; font-size:8pt;" name="EDIT"
                           value="[@spring.message "organizationPreferences.editHolidayInformation"/]"/>

                <div class="buttonsSubmitCancel marginTop15 width95prc">
                    <input class="buttn" type="submit" id="holiday.button.submit" name="submit"
                           value="[@spring.message "submit"/]"/>
                    <input class="buttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "cancel"/]"/>
                </div>
            </form>
        </div>
    </div>
</div>
[/@adminLeftPaneLayout]