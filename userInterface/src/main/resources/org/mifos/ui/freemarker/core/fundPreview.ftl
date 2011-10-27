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
    <span id="page.id" title="fundPreview"></span>
    <!--  Main Content Begins-->
    <div class="content">
        <form method="POST" action="fundPreview.ftl" name="fundPreview">

            <div class="span-24">
            [@spring.bind "formBean.oldName"/]
                [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "organizationPreferences.viewfunds":"viewFunds.ftl",spring.status.value?default(""):""}/]
            [@widget.crumbpairs breadcrumb/]
                <div class="margin20lefttop">
                    <p class="font15">[@spring.bind "formBean.oldName"/]<span class="fontBold" name="name">
                            ${spring.status.value?default("")}[@spring.showErrors "<br />"/]</span>&nbsp;-&nbsp;<span
                            class="orangeheading">[@spring.message "organizationPreferences.fundpreview.previewfundinformation"/]</span>
                    </p>

                    <div>[@spring.message "organizationPreferences.viewFunds.edit.previewTheFieldsBelowThenClickSubmitClickCancelToReturnToFundDetailsWithoutSubmittingInformation"/]</div>
                    <div class="clear">&nbsp;</div>
                    <div class="allErrorsDiv">
                    [@form.showAllErrors "formBean.*"/]
                    </div>

                    <div class="fontBold">[@spring.message "organizationPreferences.viewFunds.edit.funddetails"/]</div>
                    <div class="">
                    [@spring.bind "formBean.id"/]
                        <input type="hidden" name="${spring.status.expression}"
                               value="${spring.status.value?default("")}"/>
                        <span class="fontBold">[@spring.message "organizationPreferences.viewFunds.name"/]
                            &nbsp;:</span><span class="">&nbsp;[@spring.bind "formBean.name"/]<label
                            name="${spring.status.expression}">${spring.status.value?default("")}</label>[@spring.showErrors "<br />"/]</span><br/>
                        <span class="fontBold">[@spring.message "organizationPreferences.viewFunds.fundCode"/]
                            &nbsp;:</span><span class="">&nbsp;[@spring.bind "formBean.codeValue"/]<label
                            name="${spring.status.expression}">${spring.status.value?default("")}</label>[@spring.showErrors "<br />"/]</span>
                    </div>
                    <div class="clear">&nbsp;</div>
                [@spring.bind "formBean.name"/]<input type="hidden" name="${spring.status.expression}"
                                                      value="${spring.status.value?default("")}"/>
                [@spring.bind "formBean.oldName"/]<input type="hidden" name="${spring.status.expression}"
                                                      value="${spring.status.value?default("")}"/>
                [@spring.bind "formBean.codeId"/]<input type="hidden" name="${spring.status.expression}"
                                                        value="${spring.status.value?default("")}"/>
                [@spring.bind "formBean.codeValue"/]<input type="hidden" name="${spring.status.expression}"
                                                           value="${spring.status.value?default("")}"/>

                    <div class=""><input class="insidebuttn" type="submit" name="EDIT"
                                                  value="[@spring.message "organizationPreferences.fundpreview.editFundInformation"/]"/>
                    </div>
                    <div class="clear">&nbsp;</div>
                    <div class="buttonsSubmitCancel">
                        <input class="buttn" type="submit" name="submit" value="[@spring.message "submit"/]"/>
                        <input class="buttn2" type="submit" id="CANCEL" name="CANCEL"
                               value="[@spring.message "cancel"/]"/>
                    </div>
                </div>
        </form>
    </div>
</div><!--Main Content Ends-->
</div><!--Container Ends-->
[/@adminLeftPaneLayout]