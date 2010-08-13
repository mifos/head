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
    <span id="page.id" title="view_question_groups_details"></span>
    [#if error_message_code??]
        [@spring.message error_message_code/]
    [#else]
        [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "questionnaire.view.question.groups":"viewQuestionGroups.ftl",Request.questionGroupDetail.title:""}/]
        [@mifos.crumbpairs breadcrumb/]
    <div class="content_panel">
        <h1 id="questionGroup.title">
            ${Request.questionGroupDetail.title}
        </h1>
        <form name="viewQuestionDetailsForm" action="editQuestion.ftl" method="POST" class="marginLeft30">
            [#assign boolean_text_yes][@spring.message "questionnaire.yes"/][/#assign]
            [#assign boolean_text_no][@spring.message "questionnaire.no"/][/#assign]
            <fieldset>
                <ol>
                    <li id="questionGroup.appliesTo">
                        [@spring.message "questionnaire.questionGroupAppliesTo"/]: ${Request.eventSources[Request.questionGroupDetail.eventSourceId]}
                    </li>
                    <li id="questionGroup.editable">
                        [@spring.message "questionnaire.editable"/]: ${Request.questionGroupDetail.editable?string(boolean_text_yes, boolean_text_no)}
                    </li>
                    <li id="questionGroup.sections">
                        [#list Request.questionGroupDetail.sections as section]
                            <b>${section.name}</b><br/>
                            <table id="sections.table.${section.name}" name="sections.table.${section.name}">
                                 <tr>
                                     <td class="drawtablehd" width="50%">[@spring.message "questionnaire.question.name"/]</td>
                                     <td class="drawtablehd" width="25%">[@spring.message "questionnaire.question.mandatory"/]</td>
                                     <td class="drawtablehd" width="25%">[@spring.message "questionnaire.status"/]</td>
                                 </tr>
                                [#list section.sectionQuestions as sectionQuestion]
                                     <tr>
                                         <td class="drawtablerow" width="50%"><a href="viewAndEditQuestion.ftl?questionId=${sectionQuestion.questionId}">${sectionQuestion.title}</a></td>
                                         <td class="drawtablerow" width="25%">
                                             [#if sectionQuestion.mandatory]
                                                 [@spring.message "questionnaire.yes"/]
                                             [#else]
                                                 [@spring.message "questionnaire.no"/]
                                             [/#if]
                                         </td>
                                         <td class="drawtablerow" width="25%">
                                             [#if sectionQuestion.active]
                                                 [@spring.message "questionnaire.active"/]
                                             [#else]
                                                 [@spring.message "questionnaire.inactive"/]
                                             [/#if]
                                         </td>
                                     </tr>
                                [/#list]
                            </table>
                        [/#list]
                    </li>
                </ol>
            </fieldset>
        </form>
    [/#if]
    </div>
[/@adminLeftPaneLayout]
