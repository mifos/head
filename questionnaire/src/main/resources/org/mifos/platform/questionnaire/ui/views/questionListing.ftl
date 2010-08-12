[#ftl]
<li>
    <table id="questions.table" name="questions.table">
      <tr>
        <td class="drawtablehd" style="width:35%">[@spring.message "questionnaire.question.title"/]</td>
        <td class="drawtablehd" style="width:10%">[@spring.message "questionnaire.answer.type"/]</td>
        <td class="drawtablehd" style="width:45%">[@spring.message "questionnaire.choices"/]</td>
        <td class="drawtablehd" style="width:10%">[@spring.message "questionnaire.remove"/]</td>
      </tr>
      [#list questionDefinition.questions as question]
      <tr>
        <td class="drawtablerow">${question.title}</td>
        <td class="drawtablerow">${question.type}</td>
          [#if question.commaSeparateChoices?has_content]
          <td class="drawtablerow">${question.commaSeparateChoices}</td>
          [#else]
          <td class="drawtablerow"><i>[@spring.message "questionnaire.quesiton.choices.notapplicable"/]</i></td>
          [/#if]
        <td class="drawtablerow"><a href="removeQuestion#" title="${question.title}">[@spring.message "questionnaire.remove.link"/]</a></td>
      </tr>
      [/#list]
    </table>
</li>