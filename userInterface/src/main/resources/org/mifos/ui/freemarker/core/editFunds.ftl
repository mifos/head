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
[@adminLeftPaneLayout]  <!--  Main Content Begins-->
<div class=" content">
<span id="page.id" title="editFund"></span>
    <form method="POST" action="editFunds.ftl" name="editFunds">
        <div class="span-24">
        [@spring.bind "formBean.oldName"/]
            [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "organizationPreferences.viewfunds":"viewFunds.ftl",spring.status.value?default(""):""}/]
        [@widget.crumbpairs breadcrumb/]
            <div class="margin20lefttop">

                <p class="font15 margin10bottom"><span name="name"
                                        class="fontBold">[@spring.bind "formBean.oldName"/]${spring.status.value?default("")}</span>&nbsp;-&nbsp;<span
                        class="orangeheading">[@spring.message "organizationPreferences.viewFunds.edit.editfundinformation"/]</span>
                </p>

                <div>[@spring.message "organizationPreferences.viewFunds.edit.completethefieldsbelow.ThenclickPreview.ClickCanceltoreturntoViewfundspagewithoutsubmittinginformation."/]</div>
                <div><span class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired"/] </div>
            [@form.showAllErrors "formBean.*"/]
                <p>&nbsp;</p>

                <p class="fontBold">[@spring.message "organizationPreferences.viewFunds.edit.funddetails"/]</p>

                <p>&nbsp;</p>

                <div class="prepend-3 span-22 last">
                    <input type="hidden" name="PREVIEWVIEW" id="previewview" value="${previewView}"/>
                [@spring.bind "formBean.id"/]
                    <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                [@spring.bind "formBean.oldName"/]
                    <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                [@spring.bind "formBean.codeId"/]
                    <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                [@spring.bind "formBean.name"/]
                    <span class="span-4 rightAlign"><span
                            class="red"> * </span>[@spring.message "organizationPreferences.viewFunds.name"/]: </span>
                    <span class="span-4"><input type="text" name="${spring.status.expression}"
                                                value="${spring.status.value?default("")}"/></span>
                </div>
                <div class="prepend-3 span-22 last">
                [@spring.bind "formBean.codeValue"/]
                    <span class="span-4 rightAlign"><span
                            class="red"> * </span>[@spring.message "organizationPreferences.viewFunds.fundCode"/]: </span><span
                        class="span-4" name="${spring.status.expression}">${spring.status.value?default("")}</span>
                </div>
                <div class="clear">&nbsp;</div>
                <div class="buttonsSubmitCancel">
                    <input class="buttn" type="submit" name="preview" value="[@spring.message "preview"/]"/>
                    <input class="buttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "cancel"/]"/>
                </div>
            </div>
        </div>
    </form>
</div><!--Main Content Ends-->
[/@adminLeftPaneLayout]