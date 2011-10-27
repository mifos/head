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
<span id="page.id" title="view_lateness_and_dormancy_definition"></span>
<div class=" content">
    <style type="text/css">
        .error {
            margin-bottom: 0;
            margin-top: 15px;
        }
    </style>
    <form method="post" name="latenessanddormancy" action="editLatenessDormancy.ftl">
    [@widget.crumbs breadcrumbs /]
    [@spring.bind "formBean" /]
        <div class="marginLeft30">
            <div class="span-24">
            [@form.showAllErrors "formBean.*"/]
                <div class="span-23 borderbtm width95prc">
                    <p class="font15 margin10topbottom">
                        <span class="orangeheading">
                            [@spring.message "manageProducts.editLatenessDormancy.setLatenessDefinition" /]
                        </span>
                    </p>

                    <div class="fontBold">
                        [@spring.message "ftlDefinedLabels.manageProducts.editLatenessDormancy.loan"  /]
                    </div>
                    <div class="span-23 width95prc">
                        <span class="span-11 width50prc">
                            [@spring.message "ftlDefinedLabels.manageProducts.editLatenessDormancy.specifyTheNumberOfDaysOfNonPayment"  /]
                        </span>
                    <span class="span-8">
                        [@spring.bind "formBean.latenessDays" /]
                            <input size="4" maxlength="4" type="text" id="lateness" name="${spring.status.expression}"
                                   value="${formBean.latenessDays?c}">&nbsp;[@spring.message "manageProducts.editLatenessDormancy.days" /]
                    </span>
                    </div>
                    <div class="clear">&nbsp;</div>
                </div>
                <div class="clear">&nbsp;</div>
                <div class="span-23 width95prc">
                    <p class="font15 orangeheading margin10topbottom">[@spring.message "manageProducts.editLatenessDormancy.setDormancyDefinition" /] </p>

                    <div class="fontBold">
                        [@spring.message "ftlDefinedLabels.manageProducts.editLatenessDormancy.savings"  /]
                    </div>
                    <div class="span-23 width95prc">
                        <span class="span-11 width50prc">
                            [@spring.message "ftlDefinedLabels.manageProducts.editLatenessDormancy.specifyTheNumberOfDaysToDefineDormancy"  /]
                        </span>
                <span class="span-8">
                    [@spring.bind "formBean.dormancyDays" /]
                        <input size="4" maxlength="4" type="text" id="dormancy" name="${spring.status.expression}"
                               value="${formBean.dormancyDays?c}">&nbsp;[@spring.message "manageProducts.editLatenessDormancy.days"/]
                </span>
                    </div>
                    <div class="clear">&nbsp;</div>
                </div>
                <div class="clear">&nbsp;</div>
                <div class="buttonsSubmitCancel">
                    <input class="buttn" type="submit" name="submit" value="[@spring.message "submit"/]"/>
                    <input class="buttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "cancel"/]"/>
                </div>
            </div>
        </div>
    </form>
</div><!--Main Content Ends-->
[/@adminLeftPaneLayout]