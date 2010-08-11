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
[#include "layout.ftl"]
[@adminLeftPaneLayout]
    <STYLE TYPE="text/css"><!-- @import url(pages/questionnaire/css/questionnaire.css); --></STYLE>
    <script type="text/javascript" src="pages/questionnaire/js/viewQuestionDetail.js"></script>
    <span id="page.id" title="view_question_details"></span>
        [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "questionnaire.view.questions":"viewQuestions.ftl",Request.questionDetail.title:""}/]
        [@mifos.crumbpairs breadcrumb/]
        <div class="content_panel">
            <h1>
                ${Request.questionDetail.title}
            </h1>
            <form name="viewQuestionDetailsForm" action="viewAndEditQuestion.ftl?execution=${flowExecutionKey}" method="POST" class="marginLeft30">
                <fieldset>
                    <ol>
                        <li>
                            <a href="editQuestion#" class="topRight">[@spring.message "questionnaire.edit"/]</a>
                            <input type="submit" id="_eventId_editQuestion" name="_eventId_editQuestion" value="${Request.questionDetail.id}" style="display:none"/>
                            [@spring.message "questionnaire.question"/]: ${Request.questionDetail.title}
                        </li>
                        <li>
                            [@spring.message "questionnaire.answer.type"/]: ${Request.questionDetail.type}
                        </li>
                        <li>
                            [#if Request.questionDetail.smartSelect]
                                <table id="choices.table" name="choices.table">
                                 <tr>
                                     <td class="drawtablehd" width="50%">[@spring.message "questionnaire.choice"/]</td>
                                     <td class="drawtablehd" width="50%">[@spring.message "questionnaire.tags"/]</td>
                                 </tr>
                                [#list Request.questionDetail.choices as choice]
                                     <tr>
                                         <td class="drawtablerow" width="50%">${choice.choiceText}</td>
                                         [#if choice.commaSeparatedTags?has_content]
                                            <td class="drawtablerow" width="50%">${choice.commaSeparatedTags}</td>
                                         [#else]
                                            <td class="drawtablerow" width="50%">&nbsp;</td>
                                         [/#if]
                                     </tr>
                                [/#list]
                                </table>
                            [#elseif Request.questionDetail.commaSeparateChoices?has_content]
                                <td class="drawtablerow">[@spring.message "questionnaire.quesiton.choices"/]: ${Request.questionDetail.commaSeparateChoices}</td>
                            [/#if]
                        </li>
                        <li>
                            [#if Request.questionDetail.numericMin?exists]
                                [@spring.message "questionnaire.quesiton.numeric.min"/]: ${Request.questionDetail.numericMin}
                            [/#if]
                            [#if Request.questionDetail.numericMax?exists]
                                [@spring.message "questionnaire.quesiton.numeric.max"/]: ${Request.questionDetail.numericMax}
                            [/#if]
                        </li>
                    </ol>
                </fieldset>
            </form>
        </div>
[/@adminLeftPaneLayout]
