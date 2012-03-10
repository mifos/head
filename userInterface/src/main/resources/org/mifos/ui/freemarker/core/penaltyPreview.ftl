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
<div>
    <span id="page.id" title="penaltyPreview"></span>
    <div class="content">
        <form method="POST" action="penaltyPreview.ftl" name="penaltyPreview">
            <div class="span-24">
                [@spring.bind "formBean.oldName"/]
                    [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "penalty.viewPenalties":"viewPenalties.ftl",spring.status.value?default(""):""}/]
                [@widget.crumbpairs breadcrumb/]
            <div class="margin20lefttop">
                <p class="font15 margin10bottom">
                    <span name="name" class="fontBold">[@spring.bind "formBean.oldName"/]${spring.status.value?default("")}</span>
                    &nbsp;-&nbsp;
                    <span class="orangeheading">[@spring.message "organizationPreferences.viewPenalty.edit.previewpenaltyinformation"/]</span>
                </p>
                <div class="font9pt">[@spring.message "organizationPreferences.viewPenalty.edit.previewTheFieldsBelowThenClickSubmitClickCancelToReturnToPenaltyDetailsWithoutSubmittingInformation"/]</div>
                [@form.showAllErrors "formBean.*"/]
            
                <div class="clear">&nbsp;</div>
                <p class="fontBold">[@spring.message "organizationPreferences.definenewpenalty.penaltydetails"/]</p>
                
                <div class="span-20">
                    [@spring.bind "formBean.oldName"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                    [@spring.bind "formBean.id"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                    [@spring.bind "formBean.categoryTypeId"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                    [@spring.bind "formBean.showAmount"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                    
                    <div class="span-20">
                        <span class="fontBold">[@spring.message "organizationPreferences.definenewpenalty.name" /]</span>
                        ${formBean.name}
                        [@spring.bind "formBean.name"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                    </div>
                    [#if period?has_content]
                        <div class="span-20">
                            <span class="fontBold">[@spring.message "organizationPreferences.definenewpenalty.graceperiodtype" /]</span>
                            ${period}
                            [@spring.bind "formBean.periodTypeId"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                        </div>
                    [/#if]
                    [#if formBean.duration?has_content]
                        <div class="span-20">
                            <span class="fontBold">[@spring.message "organizationPreferences.definenewpenalty.graceperiodduration" /]</span>
                            ${formBean.duration?number}
                            [@spring.bind "formBean.duration"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                        </div>
                    [/#if]
                    <div class="span-20">
                        <span class="fontBold">[@spring.message "organizationPreferences.definenewpenalty.mincumulativepenaltylimit" /]</span>
                        ${formBean.min?number}
                        [@spring.bind "formBean.min"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                    </div>
                    <div class="span-20">
                        <span class="fontBold">[@spring.message "organizationPreferences.definenewpenalty.maxcumulativepenaltylimit" /]</span>
                        ${formBean.max?number}
                        [@spring.bind "formBean.max"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                    </div>
                </div>
                
                <div class="clear">&nbsp;</div>
                <p class="fontBold">[@spring.message "organizationPreferences.definenewpenalty.interestcalculation" /]</p>
                
                <div class="span-20">
                    [#if formBean.amount?has_content]
                        <div class="span-20">
                            <span class="fontBold">[@spring.message "organizationPreferences.definenewpenalty.fixedamount" /]</span>
                            ${formBean.amount?number}
                            [@spring.bind "formBean.amount"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                        </div>
                    [#else]
                        <div class="span-20">
                            <span class="fontBold">[@spring.message "organizationPreferences.definenewpenalty.calculatepenaltyas" /]</span>
                            ${formBean.rate?number}
                            [@spring.message "organizationPreferences.definenewpenalty.percentof" /]
                            ${formula}
                            [@spring.bind "formBean.rate"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                            [@spring.bind "formBean.formulaId"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                        </div>
                    [/#if]
                    <div class="span-20">
                        <span class="fontBold">[@spring.message "organizationPreferences.definenewpenalty.penaltyapplicationfrequency" /]</span>
                        ${frequency}
                        [@spring.bind "formBean.frequencyId"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                    </div>
                </div>
                
                <div class="clear">&nbsp;</div>
                <p class="fontBold">[@spring.message "organizationPreferences.definenewpenalty.accountingdetails" /]</p>

                <div class="span-20">
                    <div class="span-20">
                        <span class="fontBold">[@spring.message "organizationPreferences.definenewpenalty.glcode" /]</span>
                           	[#if GLCodeMode == 1]
                           		${glCode.glcode} - ${glCode.glname}
	                       	[#elseif GLCodeMode == 2]
	                        	${glCode.glname} (${glCode.glcode})
	                       	[#elseif GLCodeMode == 3]
	                        	${glCode.glname}
	                       	[#elseif GLCodeMode == 4]
	                        	${glCode.glcode}
	                       	[/#if]
                        
                        [@spring.bind "formBean.glCodeId"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                    </div>
                    <div class="span-20">
                        <span class="fontBold">[@spring.message "organizationPreferences.viewPenalty.edit.status" /]</span>
                        ${status}
                        [@spring.bind "formBean.statusId"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                    </div>
                    <div class="clear">&nbsp;</div>
                    <div class="span-20">
                        <input class="insidebuttn margin30top" type="submit" name="EDIT"
                             value="[@spring.message "organizationPreferences.viewPenalty.edit.editpenaltyinformation"/]"/>
                    </div>
                </div>
                <div class="clear">&nbsp;</div>
                <div class="buttonsSubmitCancel margin20leftright">
                    <input class="buttn" type="submit" name="SUBMIT" value="[@spring.message "submit"/]"/>
                    <input class="buttn2" type="submit" name="CANCEL" value="[@spring.message "cancel"/]"/>
                </div>
                <div class="clear">&nbsp;</div>
            </form>
        </div>
        <!--Subcontent Ends-->
    </div>
</div>
<!--Main Content Ends-->
</div>
<!--Container Ends-->
[/@adminLeftPaneLayout]