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
[#assign freeText][@spring.message "questionnaire.quesiton.choices.freetext"/][/#assign]
[#assign date][@spring.message "questionnaire.quesiton.choices.date"/][/#assign]
[#assign number][@spring.message "questionnaire.quesiton.choices.number"/][/#assign]
[#assign multiSelect][@spring.message "questionnaire.quesiton.choices.multiselect"/][/#assign]
[#assign singleSelect][@spring.message "questionnaire.quesiton.choices.singleselect"/][/#assign]
[#assign smartSelect][@spring.message "questionnaire.quesiton.choices.smartselect"/][/#assign]
[@adminLeftPaneLayout]
    <STYLE TYPE="text/css"><!-- @import url(pages/questionnaire/css/questionnaire.css); --></STYLE>
    <script type="text/javascript" src="pages/questionnaire/js/viewQuestionDetail.js"></script>
<div class=" content">
    <span id="page.id" title="view_question_details"></span>
        [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "questionnaire.view.questions":"viewQuestions.ftl",question.text:""}/]
        [@widget.crumbpairs breadcrumb/]
        <div class="content_panel">
            <table>
                <tr>
                    <td>
                        <div id="questionGroup.title" class="headingorange" style="padding: 3px;">
                            ${question.text}
                        </div>
                    </td>
                    <td>
                        [#if question.editable]
                            <a href="editQuestion#" class="topRight">[@spring.message "questionnaire.editquestion"/]</a>
                        [/#if]
                    </td>
                </tr>
            </table>
            <form name="viewQuestionDetailsForm" action="viewAndEditQuestion.ftl?execution=${flowExecutionKey}" method="POST" class="marginLeft30">
                <fieldset>
                    <ol>
                        <li>
                            <input type="submit" id="_eventId_editQuestion" name="_eventId_editQuestion" value="${question.id}" style="display:none"/>
                            [@spring.message "questionnaire.question"/]: ${question.text}
                        </li>
                        <li>
                            [@spring.message "questionnaire.answer.type"/]: ${{"freeText":freeText, "date":date, "number":number, "multiSelect":multiSelect, "singleSelect":singleSelect, "smartSelect":smartSelect}[question.type]}
                        </li>
                        [#if question.smartSelect]
                        <li>
                            <table id="choices.table" name="choices.table">
                                     <tr>
                                         <td class="drawtablehd" width="50%">[@spring.message "questionnaire.choice"/]</td>
                                         <td class="drawtablehd" width="50%">[@spring.message "questionnaire.tags"/]</td>
                                     </tr>
                                    [#list question.choices as choice]
                                         <tr>
                                             <td class="drawtablerow" width="50%">${choice.value}</td>
                                             [#if choice.commaSeparatedTags?has_content]
                                                <td class="drawtablerow" width="50%">${choice.commaSeparatedTags}</td>
                                             [#else]
                                                <td class="drawtablerow" width="50%">&nbsp;</td>
                                             [/#if]
                                         </tr>
                                    [/#list]
                                    </table>
                                [#elseif question.commaSeparateChoices?has_content]
                                    <td class="drawtablerow">[@spring.message "questionnaire.quesiton.choices"/]: ${question.commaSeparateChoices}</td>
                        </li>
                        [/#if]
                        [#if question.numericMin?exists]
                        <li>
                            [@spring.message "questionnaire.quesiton.numeric.min"/]: ${question.numericMin}
                        </li>
                        [/#if]
                        [#if question.numericMax?exists]
                        <li>
                            [@spring.message "questionnaire.quesiton.numeric.max"/]: ${question.numericMax}
                        </li>
                        [/#if]
                    </ol>
                </fieldset>
            </form>
        </div>
</div>
[/@adminLeftPaneLayout]
