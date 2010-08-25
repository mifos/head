[#ftl]
<li style="padding-bottom: 0pt;">
    <label for="txtListSearch"><span class="red">*</span>[@spring.message "questionnaire.select.questions"/]:</label>
    <input type="text" autocomplete="off" id="txtListSearch" name="txtListSearch" style="width:21em;"/>
</li>
<li style="padding-top: 0pt;">
    <label for="questionList">&nbsp;</label>
    <ol class="questionList" id="questionList" style="overflow:auto; width:19em; height:180px; border:1px solid #336699; padding-left:5px">
        [#list questionGroupForm.questionPool as sectionQuestion]
        <li style="padding-bottom: 0pt;">
           <input type="checkbox" id="${sectionQuestion.questionId?c}" name="selectedQuestionIds" value="${sectionQuestion.questionId?c}"/>
            &nbsp;<label for="${sectionQuestion.questionId?c}">${sectionQuestion.title}</label>
        </li>
        [/#list]
    </ol>
</li>
<li>
    <div class="marginLeft15em">
            <input type="submit" name="_eventId_addSection" id="_eventId_addSection" value='[@spring.message "questionnaire.add.questions"/]' class="buttn"/>
    </div>
</li>