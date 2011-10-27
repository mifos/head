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
<span id="page.id" title="newFundPreview"></span>
<div class="content definePageMargin">
    <div class="borders margin20lefttop width90prc">
        <div class="borderbtm width100prc height25px">
            <p class="span-17 completeIMG silverheading padding20left"
               style="width:50%">[@spring.message "fundinformation"/]</p>

            <p class="span-3 timelineboldorange arrowIMG last padding20left10right width130px"
               style="float:right">[@spring.message "reviewAndSubmit"/]</p>
        </div>
        <div>
            <form method="post" action="newFundPreview.ftl" name="newFundPreview">
                <div class="margin30left20top">
                    <p class="font15"><span
                            class="fontBold">[@spring.message "defineanewfund"/]</span>&nbsp;-&nbsp;<span
                            class="orangeheading">[@spring.message "reviewAndSubmit"/]</span></p>

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
                    <p class="fontBold">[@spring.message "funddetails"/] </p>

                    <p class="">
                    [@spring.bind "formBean.id"/]
                        <input type="hidden" name="${spring.status.expression}"
                               value="${spring.status.value?default("")}"/>
                    [@spring.bind "formBean.name"/]
                        <input type="hidden" name="${spring.status.expression}"
                               value="${spring.status.value?default("")}"/>
                    [@spring.bind "formBean.codeValue"/]
                        <input type="hidden" name="${spring.status.expression}"
                               value="${spring.status.value?default("")}"/>
                    [@spring.bind "formBean.codeId"/]
                        <input type="hidden" name="${spring.status.expression}"
                               value="${spring.status.value?default("")}"/>

                    [@spring.bind "formBean.name"/]
                        <span class="fontBold">[@spring.message "name"/] </span><span>&nbsp;${spring.status.value?default("")}</span><br/>

                    [@spring.bind "formBean.codeValue"/]
                        <span class="fontBold">[@spring.message "fundcode"/] </span><span>&nbsp;${spring.status.value?default("")}</span><br/>
                    </p>

                    <p class="">
                    <span><input class="insidebuttn margin30top" type="submit" name="EDIT"
                                 value="[@spring.message "organizationPreferences.fundpreview.editFundInformation"/]"/></span>
                    </p>
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