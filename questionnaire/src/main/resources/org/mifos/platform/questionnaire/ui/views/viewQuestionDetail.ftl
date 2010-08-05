[#ftl]
[#--
* Copyright (c) 2005-2010 Grameen Foundation USA
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
[#import "spring.ftl" as spring]
[#import "newblueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]

<div class="colmask leftmenu">
    <div class="colleft">
        <div class="col1wrap">
            <div class="col1">
            <div class="main_content">
            <span id="page.id" title="view_question_details"/></span>
                [#if error_message_code??]
                    [@spring.message error_message_code/]
                [#else]
                    [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "questionnaire.view.questions":"viewQuestions.ftl",Request.questionDetail.title:""}/]
                    [@mifos.crumbpairs breadcrumb/]
                    <div class="content_panel">
                    <div class="marginLeft30">
                            <h1>
                                ${Request.questionDetail.title}
                            </h1>
                            <div class="marginTop15">
                                [@spring.message "questionnaire.question"/]: ${Request.questionDetail.title}<br/>
                                [@spring.message "questionnaire.answer.type"/]: ${Request.questionDetail.type}<br/>
                                [#if Request.questionDetail.smartSelect]
                                    <table id="choices.table" name="choices.table">
                                     <tr>
                                         <td class="drawtablehd" width="50%">[@spring.message "questionnaire.choice"/]</td>
                                         <td class="drawtablehd" width="50%">[@spring.message "questionnaire.tags"/]</td>
                                     </tr>
                                    [#list Request.questionDetail.choices as choice]
                                         <tr>
                                             <td class="drawtablerow" width="50%">${choice.choiceText}</td>
                                             [#if choice.commaSeparatedTags?exists]
                                                <td class="drawtablerow" width="50%">${choice.commaSeparatedTags}</td>
                                             [#else]
                                                <td class="drawtablerow" width="50%">&nbsp;</td>
                                             [/#if]
                                         </tr>
                                    [/#list]
                                    </table>
                                [#elseif Request.questionDetail.commaSeparateChoices?exists]
                                    <td class="drawtablerow">[@spring.message "questionnaire.quesiton.choices"/]: ${Request.questionDetail.commaSeparateChoices}</td>
                                [/#if]
                                [#if Request.questionDetail.numericMin?exists]
                                    [@spring.message "questionnaire.quesiton.numeric.min"/]: ${Request.questionDetail.numericMin}<br/>
                                [/#if]
                                [#if Request.questionDetail.numericMax?exists]
                                    [@spring.message "questionnaire.quesiton.numeric.max"/]: ${Request.questionDetail.numericMax}<br/>
                                [/#if]
                            </div>
                        </div>
                [/#if]
            </div>
            </div>
         </div>
        </div>    
        <div class="col2">
            <div class="side_bar">
                [#include "newadminLeftPane.ftl" /]
            </div>
        </div>
    </div>
</div>
[@mifos.footer/]