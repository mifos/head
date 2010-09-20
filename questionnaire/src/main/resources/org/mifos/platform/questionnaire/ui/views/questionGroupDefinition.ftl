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
[#assign addNewQuestion][@spring.message "questionnaire.addNewQuestion"/][/#assign]
[#assign selectQuestions][@spring.message "questionnaire.selectQuestions"/][/#assign]
[#assign active][@spring.message "questionnaire.active"/][/#assign]
[#assign inActive][@spring.message "questionnaire.inactive"/][/#assign]
<div class="allErrorsDiv">
    [@mifosmacros.showAllErrors "questionGroupForm.*"/]
</div>
<form name="createquestiongroupform"
      action="createQuestionGroup.ftl?execution=${flowExecutionKey}" method="POST">

     <ul class="form_content">
            <li id="questionGroupStatus" style="display: none;">
              <label for="active"><span class="red">*</span>[@spring.message "questionnaire.status"/]: </label>
              [@mifosmacros.boolRadioButtons "questionGroupForm.active", {"true":active, "false":inActive},'','' /]
            </li>
            <li class="long_t_box">
                <label for="title"><span class="red">*</span>[@spring.message
                    "questionnaire.questionGroupTitle"/]:</label>

                    [@spring.formInput "questionGroupForm.title",
                    'maxlength="50"
                    onkeypress="return FnCheckNumCharsOnPress(event,this);"
                    onblur="return FnCheckNumChars(event,this);return FnEscape(event,this)"'/]

            </li>
            <li>
                <label for="eventSourceId"><span class="red">*</span>[@spring.message
                    "questionnaire.questionGroupAppliesTo"/]:</label>
                [@mifosmacros.formSingleSelectWithPrompt "questionGroupForm.eventSourceId", EventSources,
                "--select one--" /]
            </li>
            <li>
                <label for="editable">[@spring.message "questionnaire.editable"/]:</label>
                [@mifosmacros.formCheckbox "questionGroupForm.editable", ""/]
            </li>
            <li class="long_t_box">
                <label for="sectionName">[@spring.message "questionnaire.currentSectionTitle"/]:</label>
                [@spring.formInput "questionGroupForm.sectionName",
                'maxlength="50"
                onkeypress="return FnCheckNumCharsOnPress(event,this);"
                onblur="return FnCheckNumChars(event,this);return FnEscape(event,this)"'/]
            </li>
            <li>
              <label for="addQuestionFlag">&nbsp;</label>
              [@mifosmacros.boolRadioButtons "questionGroupForm.addQuestionFlag", {"false":selectQuestions, "true":addNewQuestion},'','' /]
            </li>
    </ul>

    [#if questionGroupForm.addQuestionFlag]
    <div  id="addQuestionDiv" name="addQuestionDiv">
    [#else]
    <div  id="addQuestionDiv" name="addQuestionDiv" style="display: none;">
    [/#if]
        [#include "addQuestion.ftl"]
    </div>


    [#if questionGroupForm.addQuestionFlag]
    <div class="form_content" id="selectQuestionsDiv" name="selectQuestionsDiv" style="display: none;">
    [#else]
    <div class="form_content" id="selectQuestionsDiv" name="selectQuestionsDiv">
    [/#if]
            [#include "selectQuestions.ftl"]
    </div>


    <div id="divSections">
        [#assign sectionsSize = questionGroupForm.sections?size]
        [#assign reversedSections = questionGroupForm.sections?reverse]
        [#list reversedSections as section]
        [#assign reversed_section_index = sectionsSize - section_index - 1]
        <b>${section.name}:&nbsp;&nbsp;</b>
        [#if reversed_section_index gte questionGroupForm.initialCountOfSections]
            <a href="javascript:CreateQuestionGroup.removeSection('${section.name}')">[@spring.message "questionnaire.remove.link"/]</a>
        [#else]
            <a href="javascript:CreateQuestionGroup.removeSection('${section.name}')" style="visibility:hidden">[@spring.message "questionnaire.remove.link"/]</a>
        [/#if]
        <br/>
        <table class="table_common" id="sections.table" name="sections.table">
            <thead>
            <tr>
                <th class="name" >[@spring.message "questionnaire.question.name"/]</th>
                <th class="mandatory" style="text-align:center">[@spring.message "questionnaire.question.mandatory"/]</th>
                <th class="remove">[@spring.message "questionnaire.question.delete"/]</th>
                <th class="move">[@spring.message "questionnaire.question.move"/]</th>
            </tr>
            </thead>
            <tbody>
            [#list section.sectionQuestions as sectionQuestion]
            <tr>
                <td class="name">${sectionQuestion.title}</td>
                <td align="center" valign="center" class="mandatory" style="text-align:center">
                    [@mifosmacros.formCheckbox "questionGroupForm.sections[${reversed_section_index}].sectionQuestions[${sectionQuestion_index}].mandatory", ""/]
                </td>
                <td class="remove">
                    [#if sectionQuestion_index gte section.initialCountOfQuestions]
                        <a href="javascript:CreateQuestionGroup.removeQuestion('${section.name}','${sectionQuestion.questionId}')">[@spring.message "questionnaire.remove.link"/]</a>
                    [#else]
                        <a href="javascript:CreateQuestionGroup.removeQuestion('${section.name}','${sectionQuestion.questionId}')" style="visibility:hidden">[@spring.message "questionnaire.remove.link"/]</a>
                    [/#if]
                </td>
                <td>
                    <a href="javascript:CreateQuestionGroup.moveQuestionUp('${section.name}','${sectionQuestion.questionId}')">up</a>&nbsp;&nbsp;
                    <a href="javascript:CreateQuestionGroup.moveQuestionUp('${section.name}','${sectionQuestion.questionId}')">down</a>
                </td>
            </tr>
            [/#list]
            </tbody>
        </table>
        [/#list]
        <input type="submit" id="_eventId_deleteSection" name="_eventId_deleteSection" value="" style="visibility:hidden"/>
        <input type="submit" id="_eventId_deleteQuestion" name="_eventId_deleteQuestion" value="" style="visibility:hidden"/>
        <input type="hidden" id="questionSection" name="questionSection" value=""/>
        <input type="submit" id="_eventId_moveQuestionUp" name="_eventId_moveQuestionUp" value="" style="visibility:hidden"/>
    </div>
    <div class="button_footer">
        <div class="button_container">
             <input type="submit" name="_eventId_defineQuestionGroup" id="_eventId_defineQuestionGroup" value="[@spring.message "questionnaire.submit"/]" class="buttn"/>
             &nbsp;
             <input type="submit" name="_eventId_cancel" id="_eventId_cancel" value="[@spring.message "questionnaire.cancel"/]" class="cancelbuttn"/>
        </div>
    </div>
</form>
