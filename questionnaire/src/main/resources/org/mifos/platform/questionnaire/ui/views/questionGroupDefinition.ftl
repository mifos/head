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
[#assign addNewQuestion][@spring.message "questionnaire.addNewQuestion"/][/#assign]
[#assign selectQuestions][@spring.message "questionnaire.selectQuestions"/][/#assign]
[#assign active][@spring.message "questionnaire.active"/][/#assign]
[#assign inActive][@spring.message "questionnaire.inactive"/][/#assign]
<script type="text/javascript">

    var updateShowOnPageColumn = function() {
        var hideColumns = true;
        $("#eventSourceIds option:selected").each(function() {
            if ($(this).val().toLowerCase().indexOf("loan") >= 0 
                    || $(this).val().toLowerCase().indexOf("client") >= 0) {
                $(".showOnPage").css("display", "table-cell");
                hideColumns = false;
            }
        });    
        if (hideColumns) {
            $(".showOnPage").css("display", "none");
            $(".showOnPage input").attr('checked', false);
        }
    };
    
    $(document).ready(function() {
        $("#eventSourceIds").change(updateShowOnPageColumn);
        updateShowOnPageColumn();
    });
</script>
<div class="content">
<div class="allErrorsDiv">
    [@form.showAllErrors "questionGroupForm.*"/]
</div>
<form name="createquestiongroupform"
      action="createQuestionGroup.ftl?execution=${flowExecutionKey}" method="POST">

     <ul class="form_content">
            <li id="questionGroupStatus" style="display: none;">
              <label for="active"><span class="red">*</span>[@spring.message "questionnaire.status"/]: </label>
              [@form.boolRadioButtons "questionGroupForm.active", {"true":active, "false":inActive},'','' /]
            </li>
            <li class="long_t_box">
                <label for="title"><span class="red">*</span>[@spring.message
                    "questionnaire.questionGroupTitle"/]:</label>
                    [#if questionGroupForm.questionGroupDetail.ppi]
                        [@spring.formInput "questionGroupForm.title",
                        'maxlength="50" disabled="true"
                        onkeypress="return FnCheckNumCharsOnPress(event,this);"
                        onblur="return FnCheckNumChars(event,this);return FnEscape(event,this)"'/]
                    [#else]
                        [@spring.formInput "questionGroupForm.title",
                        'maxlength="50"
                        onkeypress="return FnCheckNumCharsOnPress(event,this);"
                        onblur="return FnCheckNumChars(event,this);return FnEscape(event,this)"'/]
                    [/#if]
            </li>
            <li>
                <label for="eventSourceId"><span class="red">*</span>[@spring.message
                    "questionnaire.questionGroupAppliesTo"/]:</label>
                [@form.formMultiSelect "questionGroupForm.eventSourceIds", EventSources, '' /]
            </li>
            <li id="applyToAllLoansDiv">
                <label for="applyToAllLoanProducts">[@spring.message "questionnaire.applyToAllLoanProducts"/]:</label>
                [@form.formCheckbox "questionGroupForm.applyToAllLoanProducts", ""/]
            </li>
            <li>
                <label for="editable">[@spring.message "questionnaire.editable"/]:</label>
                [@form.formCheckbox "questionGroupForm.editable", ""/]
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
              [@form.boolRadioButtons "questionGroupForm.addQuestionFlag", {"false":selectQuestions, "true":addNewQuestion},'','' /]
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
        [#list questionGroupForm.sections as section]

        <div style="width: 100%">
            <b>${section.name}:&nbsp;&nbsp;</b>
            [#assign sectionRemovable = false]
            [#list questionGroupForm.sectionsToAdd as sectionToAdd]
                [#if section.name == sectionToAdd]
                    [#assign sectionRemovable = true]
                    [#break]
                [/#if]
            [/#list]
            [#if sectionRemovable == true]
                <a href="javascript:CreateQuestionGroup.removeSection('${section.name}')">[@spring.message "questionnaire.remove.link"/]</a>
            [#else]
                <a href="javascript:CreateQuestionGroup.removeSection('${section.name}')" style="visibility:hidden">[@spring.message "questionnaire.remove.link"/]</a>
            [/#if]
            <span class="move orderCenter">
                <a href="javascript:CreateQuestionGroup.moveSectionUp('${section.name}')" id="moveSectionUp_${section.name?replace(" ","")}">
                    <img src="pages/framework/images/smallarrowtop.gif" width="11" height="11">
                </a>&nbsp
                <a href="javascript:CreateQuestionGroup.moveSectionDown('${section.name}')" id="moveSectionDown_${section.name?replace(" ","")}">
                    <img src="pages/framework/images/smallarrowdown.gif" width="11" height="11">
                </a>
            </span>
        </div>
        <table class="table_common" id="sections.table" name="sections.table">
            <thead>
            <tr>
                <th class="name" >[@spring.message "questionnaire.question.name"/]</th>
                <th class="showOnPage orderCenter">[@spring.message "questionnaire.question.showOnPage"/]</th>
                <th class="isMandatory orderCenter">[@spring.message "questionnaire.question.mandatory"/]</th>
                <th class="remove orderCenter">[@spring.message "questionnaire.remove"/]</th>
                <th class="order orderCenter">[@spring.message "questionnaire.question.order"/]</th>
            </tr>
            </thead>
            <tbody>
            [#list section.sectionQuestions as sectionQuestion]
            <tr>
                <td class="name">${sectionQuestion.text}</td>
                <td align="right" valign="center" class="showOnPage">
                    [@form.formCheckbox "questionGroupForm.sections[${section_index}].sectionQuestions[${sectionQuestion_index}].showOnPage", ""/]
                </td>
                <td align="center" valign="center" class="mandatory orderCenter">
                    [@form.formCheckbox "questionGroupForm.sections[${section_index}].sectionQuestions[${sectionQuestion_index}].mandatory", ""/]
                </td>
                <td class="remove orderCenter">
                [#if sectionQuestion_index gte section.initialCountOfQuestions]
                    <a href="javascript:CreateQuestionGroup.removeQuestion('${section.name}','${sectionQuestion.questionId}')">[@spring.message "questionnaire.remove.link"/]</a>
                [#else]
                    <a href="javascript:CreateQuestionGroup.removeQuestion('${section.name}','${sectionQuestion.questionId}')" style="visibility:hidden">[@spring.message "questionnaire.remove.link"/]</a>
                [/#if]
                </td>
                <td class="order orderCenter">
                    <a href="javascript:CreateQuestionGroup.moveQuestionUp('${section.name}','${sectionQuestion.questionId}')" id="moveQuestionUp_${sectionQuestion.questionId}">
                        <img src="pages/framework/images/smallarrowtop.gif" width="11" height="11">
                    </a>&nbsp
                    <a href="javascript:CreateQuestionGroup.moveQuestionDown('${section.name}','${sectionQuestion.questionId}')" id="moveQuestionDown_${sectionQuestion.questionId}">
                        <img src="pages/framework/images/smallarrowdown.gif" width="11" height="11">
                    </a>
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
        <input type="submit" id="_eventId_moveQuestionDown" name="_eventId_moveQuestionDown" value="" style="visibility:hidden"/>
        <input type="submit" id="_eventId_moveSectionUp" name="_eventId_moveSectionUp" value="" style="visibility:hidden"/>
        <input type="submit" id="_eventId_moveSectionDown" name="_eventId_moveSectionDown" value="" style="visibility:hidden"/>
    </div>
    <div class="button_footer">
        <div class="button_container buttonsSubmitCancel">
             <input type="submit" name="_eventId_defineQuestionGroup" id="_eventId_defineQuestionGroup" value="[@spring.message "questionnaire.submit"/]" class="buttn"/>
             &nbsp;
             <input type="submit" name="_eventId_cancel" id="_eventId_cancel" value="[@spring.message "questionnaire.cancel"/]" class="cancelbuttn"/>
        </div>
    </div>
</div>
</form>
