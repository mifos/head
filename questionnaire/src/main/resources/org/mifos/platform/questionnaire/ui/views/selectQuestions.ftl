[#ftl]
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
                &nbsp;<label for="${sectionQuestion.questionId?c}">${sectionQuestion.title}</label>
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
