[#ftl]
[#--
* Copyright Grameen Foundation USA
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
<ul class="form_content">
    <li style="padding-bottom: 0pt;" class="long_t_box">
        <label for="txtListSearch"><span class="red">*</span>[@spring.message "questionnaire.select.questions"/]:</label>
        <input type="text" autocomplete="off" id="txtListSearch" name="txtListSearch" />
    </li>
    <li style="padding-top: 0pt;">
        <label for="questionList">&nbsp;</label>
        <ul class="questionList_box" id="questionList" style="">
            [#list questionGroupForm.questionPool as sectionQuestion]
            <li >
               <input type="checkbox" id="${sectionQuestion.questionId?c}" name="selectedQuestionIds" value="${sectionQuestion.questionId?c}"/>
                &nbsp;<label for="${sectionQuestion.questionId?c}">${sectionQuestion.text}</label>
            </li>
            [/#list]
        </ul>
    </li>
</ul>
<div class="add_question">
    <div class="button_container">
            <input type="submit" name="_eventId_addSection" id="_eventId_addSection" value='[@spring.message "questionnaire.add.questions"/]' class="buttn"/>
    </div>
</div>
