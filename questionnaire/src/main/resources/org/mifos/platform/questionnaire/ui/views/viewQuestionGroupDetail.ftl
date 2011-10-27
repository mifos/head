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
    <STYLE TYPE="text/css"><!-- @import url(pages/questionnaire/css/questionnaire.css); --></STYLE>
    <script type="text/javascript" src="pages/questionnaire/js/viewQuestionGroupDetail.js"></script>
<div class=" content">
    <span id="page.id" title="view_question_groups_details"></span>
    [#if error_message_code??]
        [@spring.message error_message_code/]
    [#else]
        [#assign breadcrumb = {"admin":"AdminAction.do?method=load", "questionnaire.view.question.groups":"viewQuestionGroups.ftl",questionGroupForm.questionGroupDetail.title:""}/]
        [@widget.crumbpairs breadcrumb/]
    <div class="content_panel">
        <table>
            <tr>
                <td>
                    <div id="questionGroup.title" class="headingorange" style="padding: 3px;">
                        ${questionGroupForm.questionGroupDetail.title}
                    </div>
                </td>
                <td>
                    <a href="editQuestionGroup#" class="topRight">[@spring.message "questionnaire.editQuestionGroup"/]</a>
                </td>
            </tr>
        </table>
        <form name="viewQuestionGroupDetailsForm" action="viewAndEditQuestionGroup.ftl?execution=${flowExecutionKey}" method="POST" class="marginLeft30">
            [#assign boolean_text_yes][@spring.message "questionnaire.yes"/][/#assign]
            [#assign boolean_text_no][@spring.message "questionnaire.no"/][/#assign]
            <ul class="no_bullet">
                    <li>
                        <input type="submit" id="_eventId_editQuestionGroup" name="_eventId_editQuestionGroup" value="${questionGroupForm.questionGroupDetail.id}" style="display:none"/>
                        [@spring.message "questionnaire.questionGroup"/]: ${questionGroupForm.questionGroupDetail.title}
                    </li>
                    <li id="questionGroup.appliesTo">
                        [@spring.message "questionnaire.questionGroupAppliesTo"/]: [#list questionGroupForm.questionGroupDetail.eventSources as eventSource][#if eventSource_index != 0], [/#if]${eventSource.description}[/#list]

                    </li>
                    <li id="questionGroup.editable">
                        [@spring.message "questionnaire.editable"/]: ${questionGroupForm.questionGroupDetail.editable?string(boolean_text_yes, boolean_text_no)}
                    </li>
                    <li id="questionGroup.sections" class="question_list_table">
                        [#list questionGroupForm.sections as section]
                            <b>${section.name}</b><br/>
                            <table class="table_common" id="sections.table.${section.name}" name="sections.table.${section.name}">
                                <thead>
                                 <tr>
                                     <th class="name" width="60%">[@spring.message "questionnaire.question.name"/]</th>
                                     <th class="isMandatory" width="20%">[@spring.message "questionnaire.question.mandatory"/]</th>
                                     <th class="status" width="20%">[@spring.message "questionnaire.status"/]</th>
                                 </tr>
                                 </thead>
                                <tbody>
                                [#list section.sectionQuestions as sectionQuestion]
                                     <tr>
                                         <td class="name" ><a href="viewAndEditQuestion.ftl?questionId=${sectionQuestion.questionId}">${sectionQuestion.text}</a></td>
                                         <td class="isMandatory" >
                                             [#if sectionQuestion.mandatory]
                                                 [@spring.message "questionnaire.yes"/]
                                             [#else]
                                                 [@spring.message "questionnaire.no"/]
                                             [/#if]
                                         </td>
                                         <td class="status" >
                                             [#if sectionQuestion.active]
                                                 [@spring.message "questionnaire.active"/]
                                             [#else]
                                                 [@spring.message "questionnaire.inactive"/]
                                             [/#if]
                                         </td>
                                     </tr>
                                [/#list]
                                </tbody>
                            </table>
                        [/#list]
                    </li>
              </ul>
        </form>
    [/#if]
    </div>
</div>
[/@adminLeftPaneLayout]
