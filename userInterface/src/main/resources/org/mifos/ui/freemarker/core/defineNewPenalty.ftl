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
<script>
function showRateOrAmount() {
    if (document.getElementsByName("categoryTypeId")[0].value == 1) {
        document.getElementById("rateDiv").style.display = "block";
    } else {
        document.getElementsByName("rate")[0].value = "";
        document.getElementsByName("formulaId")[0].selectedIndex = 0;
        document.getElementById("rateDiv").style.display = "none";
    }
}

function fnGracePeriod() {
    if(document.getElementsByName("periodTypeId")[0].selectedIndex==0 ||
        document.getElementsByName("periodTypeId")[0].value==3) {
        document.getElementsByName("duration")[0].value="";
        document.getElementsByName("duration")[0].disabled=true;
    }else {
        document.getElementsByName("duration")[0].disabled=false;
    }
}
</script>
[@layout.header "title" /]
[@widget.topNavigationNoSecurity currentTab="Admin" /]
<!--  Main Content Begins-->
<span id="page.id" title="newPenalty"></span>
[@i18n.formattingInfo /]
<div class="content definePageMargin">
    <div class="borders margin20lefttop width90prc">
        <div class="borderbtm width100prc height25px">
            <p class="span-17 timelineboldorange arrowIMG  padding20left" style="width:50%">[@spring.message "organizationPreferences.definenewpenalty.penaltyinformation"/]</p>

            <p class="span-3 timelineboldorange arrowIMG1 last padding20left10right width130px" style="float:right">[@spring.message "reviewAndSubmit"/]</p>
        </div>
        <div>
            <form method="POST" action="defineNewPenalty.ftl" name="organizationPreferences.definenewpenalty.defineNewPenalty">
                <div class="margin20lefttop">
                    <p class="font11pt">
                        <span class="fontBold">[@spring.message "organizationPreferences.definenewpenalty.defineanewpenalty"/]&nbsp;-</span>
                        <span class="orangeheading">[@spring.message "organizationPreferences.definenewpenalty.enterpenaltyinformation"/]</span>
                    </p>

                    <div class="font9pt">[@spring.message "organizationPreferences.definenewpenalty.completethefieldsbelow.ThenclickPreview.ClickCanceltoreturntoAdminpagewithoutsubmittinginformation"/]</div>
                    <div class="font9pt"><span class="red">*</span>[@spring.message "fieldsmarkedwithanasteriskarerequired"/] </div>
                    [@form.showAllErrors "formBean.*"/]
                    <br/>

                    <p class="fontBold margin10topbottom">[@spring.message "organizationPreferences.definenewpenalty.penaltydetails"/]</p>

                    <div class="prepend-3  span-20 last">
                        <div class="span-20 ">
                            <span class="span-7 rightAlign">
                                <span class="red">* </span>[@spring.message "organizationPreferences.definenewpenalty.name"/]
                            </span>
                            <span class="span-5">
                                &nbsp;[@form.input path="formBean.name" id="name" attributes="" /]
                            </span>
                        </div>
                        <div class="span-20 ">
                            <span class="span-7 rightAlign">
                                <span class="red">* </span>[@spring.message "organizationPreferences.definenewpenalty.appliesto"/]
                            </span>
                            <span class="span-5">
                                &nbsp;[@form.formSingleSelectWithPrompt "formBean.categoryTypeId", param.categoryType, "--Select--", "onchange=showRateOrAmount()"/]
                            </span>
                        </div>
                        <div class="span-20 ">
                            <span class="span-7 rightAlign">
                                [@spring.message "organizationPreferences.definenewpenalty.graceperiodtype"/]
                            </span>
                            <span class="span-5">
                                &nbsp;[@form.formSingleSelectWithPrompt "formBean.periodTypeId", param.periodType, "--Select--", "onchange=fnGracePeriod()" /]
                            </span>
                        </div>
                        <div class="span-20 ">
                            <span class="span-7 rightAlign">[@spring.message "organizationPreferences.definenewpenalty.graceperiodduration"/]
                            </span>
                            <span class="span-5">
                                &nbsp;[@form.input path="formBean.duration" id="duration" attributes="class=separatedNumber" /]
                            </span>
                        </div>
                        <div class="span-20 ">
                            <span class="span-7 rightAlign">
                                <span class="red">* </span>[@spring.message "organizationPreferences.definenewpenalty.mincumulativepenaltylimit"/]
                            </span>
                            <span class="span-5">
                                &nbsp;[@form.input path="formBean.min" id="min" attributes="class=separatedNumber" /]
                            </span>
                        </div>
                        <div class="span-20 ">
                            <span class="span-7 rightAlign">
                                <span class="red">* </span>[@spring.message "organizationPreferences.definenewpenalty.maxcumulativepenaltylimit"/]
                            </span>
                            <span class="span-5">
                                &nbsp;[@form.input path="formBean.max" id="max" attributes="class=separatedNumber" /]
                            </span>
                        </div>
                    </div>
                    <div class="clear">&nbsp;</div>
                    
                    <p class="fontBold margin10topbottom">[@spring.message "organizationPreferences.definenewpenalty.interestcalculation"/]</p>

                    <div class="prepend-3  span-20 last">
                        <div class="span-20 ">
                            <span class="span-7 rightAlign">
                                <span class="red">* </span>[@spring.message "organizationPreferences.definenewpenalty.fixedamount"/]
                            </span>
                            <span class="span-5">
                                &nbsp;[@form.input path="formBean.amount" id="amount" attributes="class=separatedNumber" /]
                            </span>
                        </div>
                        <div id="rateDiv" class="span-20" style="display: none;">
                            <span class="span-7 rightAlign">
                                <br/>
                                <span class="red">* </span>[@spring.message "organizationPreferences.definenewpenalty.calculatepenaltyas"/]
                            </span>
                            <span class="span-5">
                                &nbsp;&nbsp;&nbsp;[@spring.message "organizationPreferences.definenewpenalty.or"/]
                                <br/>
                                &nbsp;[@form.input path="formBean.rate" id="rate" attributes="class=separatedNumber" /]
                                [@spring.message "organizationPreferences.definenewpenalty.percentof"/]
                                &nbsp;[@form.formSingleSelectWithPrompt "formBean.formulaId", param.formulaType, "--Select--" /]
                            </span>
                        </div>
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
                    
                    <p class="fontBold margin10topbottom">[@spring.message "organizationPreferences.definenewpenalty.accountingdetails"/]</p>

                    <div class="prepend-3  span-20 last">
                        <div class="span-20 ">
                            <span class="span-7 rightAlign">
                                <span class="red">* </span>[@spring.message "organizationPreferences.definenewpenalty.glcode"/]
                            </span>
                            <span class="span-5">
                                &nbsp;[@form.formSingleSelectWithPrompt "formBean.glCodeId", param.glCodes, "--Select--" /]
                            </span>
                        </div>
                    </div>
                </div>
                <div class="clear">&nbsp;</div>
                <div class="buttonsSubmitCancel margin20leftright" >
                    <input class="buttn submit" type="submit" name="preview" value="[@spring.message "preview"/]"/>
                    <input class="buttn2" type="submit" name="CANCEL" value="[@spring.message "cancel"/]"/>
                </div>
                <div class="clear">&nbsp;</div>
            </form>
        </div>
        <!--Subcontent Ends-->
    </div>
</div>
<script>
showRateOrAmount();
fnGracePeriod();
</script>
<!--Main Content Ends-->
[@layout.footer/]