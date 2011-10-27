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
<span id="page.id" title="create_officeHoliday"></span>
<div class="content">
    <div class="span-24">
    [@widget.crumbs breadcrumbs /]
        <div class="margin20lefttop">
            <p class="font15 margin10topbottom"><span
                    class="orangeheading">[@spring.message "organizationPreferences.definenewholiday.addHoliday" /]</span>
            </p>

            <div class="margin5topbottom"><span
                    class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired" /] </div>

            <form method="post" action="defineNewHoliday.ftl" name="formname">
            [@spring.bind "formBean" /]
            [@form.showAllErrors "formBean.*"/]
                <div style="margin-left:100px;margin-top:25px">
                    <div class="prepend-3 span-22 last margin5bottom">
                    <span class="span-4 rightAlign" style="margin-top:6px;" ><span
                            class="red"> * </span>[@spring.message "organizationPreferences.definenewholiday.holidayName" /]</span>
                <span class="span-4 margin5topbottom">
                [@spring.bind "formBean.name" /]
                    <input type="text" id="holiday.input.name" name="${spring.status.expression}"
                           value="${spring.status.value?default("")}" size="45" maxlength="25"/>
                </span>
                    </div>
                    <div class="prepend-3 span-22 last margin5bottom">
                    <span class="span-4 rightAlign"><span
                            class="red"> * </span>[@spring.message "organizationPreferences.definenewholiday.fromDat" /] </span>
                    [@spring.bind "formBean.fromDay" /]
                        <span class="span-2"><input type="text" id="holidayFromDateDD" size="2" maxlength="2"
                                                    name="${spring.status.expression}"
                                                    value="${spring.status.value?default("")}"/>&nbsp;[@spring.message "organizationPreferences.definenewholiday.DD" /]</span>
                    [@spring.bind "formBean.fromMonth" /]
                        <span class="span-2"><input type="text" id="holidayFromDateMM" size="2" maxlength="2"
                                                    name="${spring.status.expression}"
                                                    value="${spring.status.value?default("")}"/>&nbsp;[@spring.message "organizationPreferences.definenewholiday.MM" /]</span>
                    [@spring.bind "formBean.fromYear" /]
                        <span class="span-3"><input type="text" id="holidayFromDateYY" size="4" maxlength="4"
                                                    name="${spring.status.expression}"
                                                    value="${spring.status.value?default("")}"/>&nbsp;[@spring.message "organizationPreferences.definenewholiday.YYYY" /]</span>
                    </div>
                    <div class="prepend-3 span-22 last margin5bottom">
                        <span class="span-4 rightAlign">[@spring.message "organizationPreferences.definenewholiday.toDate"/]</span>
                    [@spring.bind "formBean.toDay" /]
                        <span class="span-2"><input type="text" id="holidayThruDateDD" size="2" maxlength="2"
                                                    name="${spring.status.expression}"
                                                    value="${spring.status.value?default("")}"/>&nbsp;[@spring.message "organizationPreferences.definenewholiday.DD" /]</span>
                    [@spring.bind "formBean.toMonth" /]
                        <span class="span-2"><input type="text" id="holidayThruDateMM" size="2" maxlength="2"
                                                    name="${spring.status.expression}"
                                                    value="${spring.status.value?default("")}"/>&nbsp;[@spring.message "organizationPreferences.definenewholiday.MM" /]</span>
                    [@spring.bind "formBean.toYear" /]
                        <span class="span-3"><input type="text" id="holidayThruDateYY" size="4" maxlength="4"
                                                    name="${spring.status.expression}"
                                                    value="${spring.status.value?default("")}"/>&nbsp;[@spring.message "organizationPreferences.definenewholiday.YYYY" /]</span>
                    </div>
                    <div class="prepend-3 span-22 last margin5bottom">
                        <span class="span-4 rightAlign"><span
                                class="red"> * </span>[@spring.message "organizationPreferences.definenewholiday.repaymentRule" /]</span>
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
                    <div class="prepend-3 span-22 last margin5bottom">
                    <span class="span-4 rightAlign"><span
                            class="red"> * </span>[@spring.message "organizationPreferences.definenewholiday.appliesto" /]
                        &nbsp;:</span>
                    <span class="span-5">
                        <div id="officeTree">
                        </div>
                    </span>
                    [@spring.bind "formBean.selectedOfficeIds" /]
                        <input type="hidden" id="selectedOfficeIds" name="${spring.status.expression}"
                               value="${spring.status.value?default("")}"/>
                    </div>
                    <div class="clear">&nbsp;</div>
                </div>
                <div class="buttonsSubmitCancel">
                    <input class="buttn" type="submit" id="holiday.button.preview" name="preview"
                           value="[@spring.message "preview"/]"/>
                    <input class="buttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "cancel"/]"/>
                </div>
            </form>
        </div>
    </div>
</div>
[/@adminLeftPaneLayout]
<script type="text/javascript" src="pages/js/jstree/jquery.jstree.js"></script>
<script type="text/javascript" src="pages/application/holiday/js/createHolidays.js"></script>
