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
[@adminLeftPaneLayout]<!--  Main Content Begins-->
<div class=" content">
    <span id="page.id" title="viewPenalty"></span>
    <div class="span-24">
        [@spring.bind "penalty.penaltyName"/]
            [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "penalty.viewPenalties":"viewPenalties.ftl",spring.status.value?default(""):""}/]
        [@widget.crumbpairs breadcrumb/]
        
        <div class="clear">&nbsp;</div>
        <div class="marginLeft30" style="line-height:1.2">
            <div class="span-18 width90prc" style="margin-bottom:7px;">
                <span class="orangeheading">${penalty.penaltyName}</span>
                <div style="position:relative;top:-17px; text-align:right;">
                    <a id="Penalty.link.editPenalty" href="editPenalty.ftl?penaltyId=${penalty.penaltyId}">
                        [@spring.message "penalty.editPenalty" /]
                    </a>
                </div>
            </div>
            <div style="height:5px;" class="clear">&nbsp;</div>
            <p class="span-24 ">
                <div class="fontBold black-subheading">
                    [@spring.message "organizationPreferences.definenewpenalty.penaltydetails"  /]
                </div>
                <div>
                    <span>[@spring.message "organizationPreferences.definenewpenalty.name" /]</span>
                    <span>${penalty.penaltyName}</span>
                </div>
                <div>
                    <span>[@spring.message "organizationPreferences.definenewpenalty.appliesto" /]</span>
                    <span>${penalty.categoryType.name}</span>
                </div>
                <div>
                    <span>[@spring.message "organizationPreferences.definenewpenalty.graceperiodtype" /]</span>
                    <span>${penalty.periodType.name}</span>
                </div>
                [#if penalty.periodDuration?has_content]
                    <div>
                        <span>[@spring.message "organizationPreferences.definenewpenalty.graceperiodduration" /]</span>
                        <span>${penalty.periodDuration?number}</span>
                    </div>
                [/#if]
                <div>
                    <span>[@spring.message "organizationPreferences.definenewpenalty.mincumulativepenaltylimit" /]</span>
                    <span>${penalty.minimumLimit?number}</span>
                </div>
                <div>
                    <span>[@spring.message "organizationPreferences.definenewpenalty.maxcumulativepenaltylimit" /]</span>
                    <span>${penalty.maximumLimit?number}</span>
                </div>
            </p>
            <div class="clear">&nbsp;</div>
            
            <p class="span-24 ">
                <div class="fontBold black-subheading">[@spring.message "organizationPreferences.definenewpenalty.interestcalculation" /]</div>
                [#if penalty.amount?has_content]
                    <div>
                        <span>[@spring.message "organizationPreferences.definenewpenalty.fixedamount" /]</span>
                        <span>${penalty.amount?number}</span>
                    </div>
                [#else]
                    <div>
                        <span>[@spring.message "organizationPreferences.definenewpenalty.calculatepenaltyas" /]</span>
                        <span>
                            ${penalty.rate?number}
                            [@spring.message "organizationPreferences.definenewpenalty.percentof" /]
                            ${penalty.penaltyFormula.name}
                        </span>
                    </div>
                [/#if]
                <div>
                    <span>[@spring.message "organizationPreferences.definenewpenalty.penaltyapplicationfrequency" /]</span>
                    <span>${penalty.penaltyFrequency.name}</span>
                </div>
            </p>
            <div class="clear">&nbsp;</div>
            
            <p class="span-24 ">
                <div class="fontBold black-subheading">[@spring.message "organizationPreferences.definenewpenalty.accountingdetails" /]</div>
                <div>
                    <span>[@spring.message "organizationPreferences.definenewpenalty.glcode" /]</span>
                    
                    [#if GLCodeMode == 1]
                   		<span>${penalty.glCodeDto.glcode} - ${penalty.glCodeDto.glname}</span>
                   	[#elseif GLCodeMode == 2]
                    	<span>${penalty.glCodeDto.glname} (${penalty.glCodeDto.glcode})</span>
                   	[#elseif GLCodeMode == 3]
                    	<span>${penalty.glCodeDto.glname}</span>
                   	[#elseif GLCodeMode == 4]
                    	<span>${penalty.glCodeDto.glcode}</span>
                   	[/#if]
                </div>
                <div>
                    <span>[@spring.message "organizationPreferences.viewPenalty.edit.status" /]</span>
                    <span>${penalty.status.name}</span>
                </div>
            </p>
        </div>
    </div>
</div>
<!--Main Content Ends-->
[/@adminLeftPaneLayout]