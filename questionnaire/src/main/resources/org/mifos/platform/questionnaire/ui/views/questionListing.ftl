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
[#assign freeText][@spring.message "questionnaire.quesiton.choices.freetext"/][/#assign]
[#assign date][@spring.message "questionnaire.quesiton.choices.date"/][/#assign]
[#assign number][@spring.message "questionnaire.quesiton.choices.number"/][/#assign]
[#assign multiSelect][@spring.message "questionnaire.quesiton.choices.multiselect"/][/#assign]
[#assign singleSelect][@spring.message "questionnaire.quesiton.choices.singleselect"/][/#assign]
[#assign smartSelect][@spring.message "questionnaire.quesiton.choices.smartselect"/][/#assign]
[#assign smartSingleSelect][@spring.message "questionnaire.quesiton.choices.smartsingleselect"/][/#assign]
<div class="question_list">
    <table class="table_common " id="questions.table" name="questions.table">
      <thead>
        <tr>
        <th class="title" >[@spring.message "questionnaire.question.title"/]</th>
        <th class="ans_type" >[@spring.message "questionnaire.answer.type"/]</th>
        <th class="choices" >[@spring.message "questionnaire.choices"/]</th>
        <th class="remove" >[@spring.message "questionnaire.remove"/]</th>
      </tr>
      </thead>
      <tbody>
      [#list questionDefinition.questions as question]
        <tr>
            <td class="title">${question.text}</td>
            <td class="ans_type">${{"freeText":freeText, "date":date, "number":number, "multiSelect":multiSelect, "singleSelect":singleSelect, "smartSelect":smartSelect, "smartSingleSelect":smartSingleSelect}[question.type]}</td>
            <td class="choices">
              [#if question.commaSeparateChoices?has_content]
                ${question.commaSeparateChoices}
              [#else]
                <i>[@spring.message "questionnaire.quesiton.choices.notapplicable"/]</i>
              [/#if]
            </td>
            <td class="remove"><a href="removeQuestion#" title="${question.text}">[@spring.message "questionnaire.remove.link"/]</a></td>
       </tr>
      [/#list]
      </tbody>
    </table>
</div>