[#ftl]
[#assign freeText][@spring.message "questionnaire.quesiton.choices.freetext"/][/#assign]
[#assign date][@spring.message "questionnaire.quesiton.choices.date"/][/#assign]
[#assign number][@spring.message "questionnaire.quesiton.choices.number"/][/#assign]
[#assign multiSelect][@spring.message "questionnaire.quesiton.choices.multiselect"/][/#assign]
[#assign singleSelect][@spring.message "questionnaire.quesiton.choices.singleselect"/][/#assign]
[#assign smartSelect][@spring.message "questionnaire.quesiton.choices.smartselect"/][/#assign]
[#assign active][@spring.message "questionnaire.active"/][/#assign]
[#assign inActive][@spring.message "questionnaire.inactive"/][/#assign]
<input type="hidden" id="number" name="number" value="${number}"/>
<input type="hidden" id="multiSelect" name="multiSelect" value="${multiSelect}"/>
<input type="hidden" id="singleSelect" name="singleSelect" value="${singleSelect}" />
<input type="hidden" id="smartSelect" name="smartSelect" value="${smartSelect}" />
<input type="submit" id="_eventId_removeChoice" name="_eventId_removeChoice" value="" style="display: none;"/>
<input type="submit" id="_eventId_removeChoiceTag" name="_eventId_removeChoiceTag" value="" style="display: none;"/>
<input type="submit" id="_eventId_addSmartChoiceTag" name="_eventId_addSmartChoiceTag" value="" style="display: none;"/>
<ul class="form_content">
    <li id="status">
      <label for="currentQuestion.active"><span class="red">*</span>[@spring.message "questionnaire.status"/]: </label>
      [@mifosmacros.boolRadioButtons "questionDefinition.currentQuestion.active", {"true":active, "false":inActive},'','' /]
    </li>
    <li id="question" class="long_t_box">
      <label for="currentQuestion.title"><span class="red">*</span>[@spring.message "questionnaire.question.title"/]: </label>
      [@spring.formInput "questionDefinition.currentQuestion.title", 'maxlength="200"' /]
    </li>
    <li>
      <label for="currentQuestion.type"><span class="red">*</span>[@spring.message "questionnaire.answer.type"/]: </label>
      [@spring.formSingleSelect "questionDefinition.currentQuestion.type", [freeText, date, number, multiSelect, singleSelect, smartSelect], ''/]
    </li>
    <li id="numericDiv">
        <label for="currentQuestion.numericMin">[@spring.message "questionnaire.quesiton.numeric.min"/]: </label>
        [@spring.formInput "questionDefinition.currentQuestion.numericMin", 'maxlength="5" class="numeric"'/]
        <br>
        <label for="currentQuestion.numericMax">[@spring.message "questionnaire.quesiton.numeric.max"/]: </label>
        [@spring.formInput "questionDefinition.currentQuestion.numericMax", 'maxlength="5" class="numeric"'/]
    </li>
    <li id="choiceDiv" class="long_t_box">
        <label for="currentQuestion.currentChoice"><span class="red">*</span>[@spring.message "questionnaire.quesiton.choice"/]: </label>
        [@spring.formInput "questionDefinition.currentQuestion.currentChoice", 'maxlength="200"'/]
        <input type="submit" id="_eventId_addChoice" name="_eventId_addChoice" class="buttn"
               value="[@spring.message "questionnaire.quesiton.add"/] >>">
        
        <table class="table_common" border="0" >
            <thead>
               <tr>
                    <th class="choice">[@spring.message "questionnaire.choice"/]</th>
                    <th class="remove">Remove</th>
                </tr>
            </thead>
            <tbody>
            [#list questionDefinition.currentQuestion.choices as choice]
            <tr>
                <td class="choice">${choice}&nbsp;</td>
                <td class="remove">
                [#if choice_index gte questionDefinition.currentQuestion.initialNumberOfChoices]
                    <a href="removeChoice#" choiceIndex="${choice_index}">[@spring.message "questionnaire.remove.link"/]</a>
                [#else]
                    <a href="removeChoice#" choiceIndex="${choice_index}" style="visibility:hidden">[@spring.message "questionnaire.remove.link"/]</a>
                [/#if]
                </td>
            </tr>
            [/#list]
            </tbody>
        </table>
    </li>
</ul>


<div id="choiceTagsDiv">
    <ul class="form_content">
        <li class="long_t_box">
            <label for="currentQuestion.currentSmartChoice"><span class="red">*</span>[@spring.message "questionnaire.quesiton.choice"/]: </label>
            [@spring.formInput "questionDefinition.currentQuestion.currentSmartChoice", 'maxlength="200"'/]
            <input type="submit" id="_eventId_addSmartChoice" name="_eventId_addSmartChoice" class="buttn"
                   value="[@spring.message "questionnaire.quesiton.add"/] >>">
        </li>
    </ul>
    <table class="table_common" border="0" >
        <thead class="col4">
            <tr>
                <th class="choice">[@spring.message "questionnaire.choice"/]</th>
                <th class="tag">[@spring.message "questionnaire.tags"/]</th>
                <th class="remove">[@spring.message "questionnaire.remove"/]</th>
                <th class="addTags">&nbsp;</th>
            </tr>
        </thead>
        <tbody>
        [#list questionDefinition.currentQuestion.choices as choice]
         <tr>
                <td class="choice">${choice.value}&nbsp;</td>
                <td class="tag">
                    [#if choice.tags?size > 0]
                        [#list choice.tags as tag]
                            ${tag}<a href="removeSmartChoiceTag#" choiceTagIndex="${choice_index}_${tag_index}" style="text-decoration: none;">
                                    <img src="pages/framework/images/icon_remove.gif" class="removeIMG" alt="remove"/>
                                  </a> &nbsp;
                        [/#list]
                    [#else]
                        &nbsp;
                    [/#if]
                </td>
                <td class="remove">
                    [#if choice_index gte questionDefinition.currentQuestion.initialNumberOfChoices]
                        <a href="removeSmartChoice#" choiceIndex="${choice_index}">[@spring.message "questionnaire.remove.link"/]</a>
                    [#else]
                        <a href="removeSmartChoice#" choiceIndex="${choice_index}" style="visibility:hidden">[@spring.message "questionnaire.remove.link"/]</a>
                    [/#if]
                </td>
              <td class="addTags">
                [#if questionDefinition.currentQuestion.currentSmartChoiceTags?size > 0]

                        [@spring.formInput "questionDefinition.currentQuestion.currentSmartChoiceTags[${choice_index}]", 'maxlength="50"'/]
                        <input type="submit" id="addSmartChoiceTag_${choice_index}" name="addSmartChoiceTag_${choice_index}" disabled="disabled"
                               class="disabledbuttn" value="[@spring.message "questionnaire.question.addtag"/] >>" choiceIndex="${choice_index}">
                [/#if]
               </td>
          </tr>
        [/#list]
        </tbody>
    </table>

</div>