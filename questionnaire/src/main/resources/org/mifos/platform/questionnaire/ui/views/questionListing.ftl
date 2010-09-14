[#ftl]
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
            <td class="title">${question.title}</td>
            <td class="ans_type">${question.type}</td>
            <td class="choices">
              [#if question.commaSeparateChoices?has_content]
                ${question.commaSeparateChoices}
              [#else]
                <i>[@spring.message "questionnaire.quesiton.choices.notapplicable"/]</i>
              [/#if]
            </td>
            <td class="remove"><a href="removeQuestion#" title="${question.title}">[@spring.message "questionnaire.remove.link"/]</a></td>
       </tr>
      [/#list]
      </tbody>
    </table>
</div>