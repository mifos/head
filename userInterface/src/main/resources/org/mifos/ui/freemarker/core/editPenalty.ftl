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
<div class="content">
<span id="page.id" title="editPenalty"></span>
<form method="POST" action="editPenalty.ftl" name="editPenalty">
    <div class="span-24">
        [@spring.bind "formBean.oldName"/]
            [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "penalty.viewPenalties":"viewPenalties.ftl",spring.status.value?default(""):""}/]
        [@widget.crumbpairs breadcrumb/]
        <div class="margin20lefttop">
            <p class="font15 margin10bottom">
                <span name="name" class="fontBold">[@spring.bind "formBean.oldName"/]${spring.status.value?default("")}</span>
                &nbsp;-&nbsp;
                <span class="orangeheading">[@spring.message "organizationPreferences.viewPenalty.edit.editpenaltyinformation"/]</span>
            </p>
            <div class="font9pt">[@spring.message "organizationPreferences.viewPenalty.edit.completethefieldsbelow.ThenclickPreview.ClickCanceltoreturntoViewpenaltiespagewithoutsubmittinginformation"/]</div>
            <div class="font9pt"><span class="red">*</span>[@spring.message "fieldsmarkedwithanasteriskarerequired"/]</div>
            [@form.showAllErrors "formBean.*"/]
            
            <div class="clear">&nbsp;</div>
            <p class="fontBold">[@spring.message "organizationPreferences.definenewpenalty.penaltydetails"/]</p>
            
            <div class="prepend-3 span-20 last">
                [@spring.bind "formBean.id"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                [@spring.bind "formBean.categoryTypeId"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                [@spring.bind "formBean.showAmount"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                
                <div class="span-20 ">
                    <span class="span-7 rightAlign">
                        <span class="red">* </span>[@spring.message "organizationPreferences.definenewpenalty.name"/]
                    </span>
                    <span class="span-5">
                        &nbsp;[@spring.bind "formBean.name"/]
                        <input type="text" name="${spring.status.expression}" id="${spring.status.expression}" value="${spring.status.value?if_exists}"/>
                    </span>
                </div>
                
                <div class="span-20">
                    <span class="span-7 rightAlign">
                        <span class="red">* </span>[@spring.message "organizationPreferences.definenewpenalty.graceperiodtype"/]
                    </span>
                    <span class="span-5">
                        &nbsp;[@form.formSingleSelectWithPrompt "formBean.periodTypeId", param.periodType, "--Select--" /]
                    </span>
                </div>
                <div class="span-20 ">
                    <span class="span-7 rightAlign">[@spring.message "organizationPreferences.definenewpenalty.graceperiodduration"/]</span>
                    <span class="span-5">
                        &nbsp;[@spring.bind "formBean.duration"/]
                        <input type="text" name="${spring.status.expression}" id="${spring.status.expression}" value="${spring.status.value?if_exists}"/>
                    </span>
                </div>
                <div class="span-20 ">
                    <span class="span-7 rightAlign">
                        <span class="red">* </span>[@spring.message "organizationPreferences.definenewpenalty.mincumulativepenaltylimit"/]
                    </span>
                    <span class="span-5">
                        &nbsp;[@spring.bind "formBean.min"/]
                        <input type="text" name="${spring.status.expression}" id="${spring.status.expression}" value="${spring.status.value?if_exists}"/>
                    </span>
                 </div>
                 <div class="span-20 ">
                    <span class="span-7 rightAlign">
                        <span class="red">* </span>[@spring.message "organizationPreferences.definenewpenalty.maxcumulativepenaltylimit"/]
                    </span>
                    <span class="span-5">
                        &nbsp;[@spring.bind "formBean.max"/]
                        <input type="text" name="${spring.status.expression}" id="${spring.status.expression}" value="${spring.status.value?if_exists}"/>
                    </span>
                 </div>
            </div>
            
            <div class="clear">&nbsp;</div>
            <p class="fontBold">[@spring.message "organizationPreferences.definenewpenalty.interestcalculation"/]</p>
            
            <div class="prepend-3 span-20 last">
            [#if formBean.showAmount]
                <div class="span-20 ">
                    <span class="span-7 rightAlign">
                        <span class="red">* </span>[@spring.message "organizationPreferences.definenewpenalty.fixedamount"/]
                    </span>
                    <span class="span-5">
                        &nbsp;[@spring.bind "formBean.amount"/]
                        <input type="text" name="${spring.status.expression}" id="${spring.status.expression}" value="${spring.status.value?if_exists}"/>
                    </span>
                </div>
            [#else]
                <div class="span-20">
                    <span class="span-7 rightAlign">
                        <span class="red">* </span>[@spring.message "organizationPreferences.definenewpenalty.calculatepenaltyas"/]
                    </span>
                    <span class="span-5">
                        &nbsp;[@spring.bind "formBean.rate"/]
                        <input type="text" name="${spring.status.expression}" id="${spring.status.expression}" value="${spring.status.value?if_exists}"/>
                        [@spring.message "organizationPreferences.definenewpenalty.percentof"/]
                        &nbsp;[@form.formSingleSelectWithPrompt "formBean.formulaId", param.formulaType, "--Select--" /]
                    </span>
                </div>
            [/#if]
                <div class="span-20 ">
                    <span class="span-7 rightAlign">
                        <span class="red">* </span>[@spring.message "organizationPreferences.definenewpenalty.penaltyapplicationfrequency"/]
                    </span>
                    <span class="span-5">
                        &nbsp;[@form.formSingleSelectWithPrompt "formBean.frequencyId", param.frequencyType, "--Select--" /]
                    </span>
                </div>
            </div>
            
            <div class="clear">&nbsp;</div>
            <p class="fontBold">[@spring.message "organizationPreferences.definenewpenalty.accountingdetails"/]</p>
            
            <div class="prepend-3 span-20 last">
                <div class="span-20 ">
                    <span class="span-7 rightAlign">
                        <span class="red">* </span>[@spring.message "organizationPreferences.definenewpenalty.glcode"/]
                    </span>
                    <span class="span-5">
                        &nbsp;[@form.formSingleSelectWithPrompt "formBean.glCodeId", param.glCodes, "--Select--" /]
                    </span>
                </div>
                <div class="span-20 ">
                    <span class="span-7 rightAlign">
                        <span class="red">* </span>[@spring.message "organizationPreferences.viewPenalty.edit.status"/]
                    </span>
                    <span class="span-5">
                        &nbsp;[@form.formSingleSelectWithPrompt "formBean.statusId", param.statusType, "--Select--" /]
                    </span>
                </div>
            </div>
            
            <div class="clear">&nbsp;</div>
            <div class="buttonsSubmitCancel">
                <input class="buttn" type="submit" name="preview" value="[@spring.message "preview"/]"/>
                <input class="buttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "cancel"/]"/>
            </div>
        </div>
    </div>
</form>
</div>
<!--Main Content Ends-->
[/@adminLeftPaneLayout]