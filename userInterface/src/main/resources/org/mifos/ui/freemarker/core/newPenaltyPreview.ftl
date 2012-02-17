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
[@layout.header "title" /]
[@widget.topNavigationNoSecurity currentTab="Admin" /]
<!--  Container Begins-->
<span id="page.id" title="newpenaltyPreview"></span>
<div class="content definePageMargin">
    <div class="borders margin20lefttop width90prc">
        <div class="borderbtm width100prc height25px">
            <p class="span-17 completeIMG silverheading padding20left"
               style="width:50%">[@spring.message "organizationPreferences.definenewpenalty.penaltyinformation"/]</p>

            <p class="span-3 timelineboldorange arrowIMG last padding20left10right width130px"
               style="float:right">[@spring.message "reviewAndSubmit"/]</p>
        </div>
        <div>
            <form method="post" action="newPenaltyPreview.ftl" name="newpenaltyPreview">
                <div class="margin30left20top">
                    <p class="font15"><span
                            class="fontBold">[@spring.message "organizationPreferences.definenewpenalty.defineanewpenalty"/]</span>&nbsp;-&nbsp;<span
                            class="orangeheading">[@spring.message "organizationPreferences.definenewpenalty.enterpenaltyinformation"/]</span></p>

                    <div>[@spring.message "reviewtheinformationbelow.ClickSubmitifyouaresatisfiedorclickEdittomakechanges.ClickCanceltoreturntoAdminpagewithoutsubmittinginformation"/]</div>
                [@spring.bind "formBean"/]
                [@form.showAllErrors "formBean.*"/]
                    <div class="marginLeft30">
                        <ul class="error">
                        [#if error?exists]
                            <li><b>[@spring.message error?default("")/] </b></li>
                        [/#if]
                        </ul>
                    </div>
                    <p class="fontBold margin10topbottom">[@spring.message "organizationPreferences.definenewpenalty.penaltydetails" /]</p>

                    <div class="span-21 last">
                        <div class="span-20">
                            <span class="span-6 fontBold">[@spring.message "organizationPreferences.definenewpenalty.name" /]</span>
                            <span class="span-4">${formBean.name}</span>
                        </div>
                        <div class="span-20 ">
                            <span class="span-6 fontBold">[@spring.message "organizationPreferences.definenewpenalty.appliesto" /]</span>
                            <span class="span-4">${applies}</span>
                            [@spring.bind "formBean.categoryTypeId"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                        </div>
                        [#if period?has_content]
                            <div class="span-20">
                                <span class="span-6 fontBold">[@spring.message "organizationPreferences.definenewpenalty.graceperiodtype" /]</span>
                                <span class="span-4">${period}</span>
                                [@spring.bind "formBean.periodTypeId"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                            </div>
                        [/#if]
                        [#if formBean.duration?has_content]
                            <div class="span-20 ">
                                <span class="span-6 fontBold">[@spring.message "organizationPreferences.definenewpenalty.graceperiodduration" /]</span>
                                <span class="span-4">${formBean.duration?number}</span>
                            </div>
                        [/#if]
                        <div class="span-20 last">
                            <span class="span-6 fontBold">[@spring.message "organizationPreferences.definenewpenalty.mincumulativepenaltylimit" /]</span>
                            <span class="span-4">${formBean.min?number}</span>
                        </div>
                        <div class="span-20 last">
                            <span class="span-6 fontBold">[@spring.message "organizationPreferences.definenewpenalty.maxcumulativepenaltylimit" /]</span>
                            <span class="span-4">${formBean.max?number}</span>
                        </div>
                    </div>
                    <div class="clear">&nbsp;</div>
                    <p class="fontBold margin10topbottom">[@spring.message "organizationPreferences.definenewpenalty.interestcalculation" /] </p>

                    <div class="span-21 last">
                        [#if formBean.amount?has_content]
                            <div class="span-20">
                                <span class="span-6 fontBold">[@spring.message "organizationPreferences.definenewpenalty.fixedamount" /]</span>
                                <span class="span-4">${formBean.amount?number}</span>
                            </div>
                        [#else]
                            <div class="span-20 ">
                                <span class="span-6 fontBold">[@spring.message "organizationPreferences.definenewpenalty.calculatepenaltyas" /]</span>
                                <span class="span-8">
                                    ${formBean.rate?number}
                                    [@spring.message "organizationPreferences.definenewpenalty.percentof" /]
                                    ${formula}
                                    [@spring.bind "formBean.formulaId"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                                </span>
                            </div>
                        [/#if]
                        <div class="span-20 ">
                            <span class="span-6 fontBold">[@spring.message "organizationPreferences.definenewpenalty.penaltyapplicationfrequency" /]</span>
                            <span class="span-4">${frequency}</span>
                            [@spring.bind "formBean.frequencyId"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                        </div>
                    </div>
                    <div class="clear">&nbsp;</div>
                    
                    <p class="fontBold margin10topbottom">[@spring.message "organizationPreferences.definenewpenalty.accountingdetails" /] </p>

                    <div class="span-21 last">
                        <div class="span-20">
                            <span class="span-6 fontBold">[@spring.message "organizationPreferences.definenewpenalty.glcode" /]</span>
                            <span class="span-4">${glCode}</span>
                            [@spring.bind "formBean.glCodeId"/]<input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                        </div>
                        <div class="clear">&nbsp;</div>
                        <div class="span-20">
                            <input class="insidebuttn margin30top" type="submit" name="EDIT"
                                 value="[@spring.message "organizationPreferences.viewPenalty.edit.editpenaltyinformation"/]"/>
                        </div>
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
[@layout.footer/]