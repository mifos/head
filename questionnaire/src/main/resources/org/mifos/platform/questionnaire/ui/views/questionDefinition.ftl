[#ftl]
[#assign freeText][@spring.message "questionnaire.quesiton.choices.freetext"/][/#assign]
[#assign date][@spring.message "questionnaire.quesiton.choices.date"/][/#assign]
[#assign number][@spring.message "questionnaire.quesiton.choices.number"/][/#assign]
[#assign multiSelect][@spring.message "questionnaire.quesiton.choices.multiselect"/][/#assign]
[#assign singleSelect][@spring.message "questionnaire.quesiton.choices.singleselect"/][/#assign]
[#assign smartSelect][@spring.message "questionnaire.quesiton.choices.smartselect"/][/#assign]
<input type="hidden" id="number" name="number" value="${number}"/>
<input type="hidden" id="multiSelect" name="multiSelect" value="${multiSelect}"/>
<input type="hidden" id="singleSelect" name="singleSelect" value="${singleSelect}" />
<input type="hidden" id="smartSelect" name="smartSelect" value="${smartSelect}" />
<input type="submit" id="_eventId_removeChoice" name="_eventId_removeChoice" value="" style="visibility:hidden"/>
<input type="submit" id="_eventId_removeChoiceTag" name="_eventId_removeChoiceTag" value="" style="visibility:hidden"/>
<input type="submit" id="_eventId_addSmartChoiceTag" name="_eventId_addSmartChoiceTag" value="" style="visibility:hidden"/>
<li>
  <label for="currentQuestion.title"><span class="red">*</span>[@spring.message "questionnaire.question.title"/]: </label>
  [@spring.formInput "questionDefinition.currentQuestion.title", 'maxlength="50"' /]
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
<li id="choiceDiv">
    <label for="currentQuestion.currentChoice"><span class="red">*</span>[@spring.message "questionnaire.quesiton.choice"/]: </label>
    [@spring.formInput "questionDefinition.currentQuestion.currentChoice", 'maxlength="200"'/]
    <input type="submit" id="_eventId_addChoice" name="_eventId_addChoice" class="buttn"
           value="[@spring.message "questionnaire.quesiton.add"/] >>">
    <fieldset>
        <ol class="choiceOlStyle">
            <li class="choiceHeaderStyle">
                <span class="choiceStyle">[@spring.message "questionnaire.choice"/]</span>Remove
            </li>
            [#list questionDefinition.currentQuestion.choices as choice]
            <li>
                <span class="choiceStyle">${choice}&nbsp;</span>
                [#if choice_index gte questionDefinition.currentQuestion.initialNumberOfChoices]
                    <a href="removeChoice#" choiceIndex="${choice_index}">[@spring.message "questionnaire.remove.link"/]</a>
                [#else]
                    <a href="removeChoice#" choiceIndex="${choice_index}" style="visibility:hidden">[@spring.message "questionnaire.remove.link"/]</a>
                [/#if]
            </li>
            [/#list]
        </ol>
    </fieldset>
</li>
<li id="choiceTagsDiv">
    <label for="currentQuestion.currentSmartChoice"><span class="red">*</span>[@spring.message "questionnaire.quesiton.choice"/]: </label>
    [@spring.formInput "questionDefinition.currentQuestion.currentSmartChoice", 'maxlength="200"'/]
    <input type="submit" id="_eventId_addSmartChoice" name="_eventId_addSmartChoice" class="buttn"
           value="[@spring.message "questionnaire.quesiton.add"/] >>">
    <fieldset>
        <ol>
            <li class="choiceHeaderStyle">
                <span class="choiceStyle">[@spring.message "questionnaire.choice"/]</span>
                <span class="tagStyle">[@spring.message "questionnaire.tags"/]</span>
                <span class="removeStyle">[@spring.message "questionnaire.remove"/]</span>
                <span class="addTagStyle">&nbsp;</span>
            </li>
            [#list questionDefinition.currentQuestion.choices as choice]
            <li>
                <span class="choiceStyle">${choice.choiceText}&nbsp;</span>
                <span class="tagStyle">
                    [#if choice.tags?size > 0]
                        [#list choice.tags as tag]
                            ${tag}<a href="removeSmartChoiceTag#" choiceTagIndex="${choice_index}_${tag_index}" style="text-decoration: none;">
                                    <img src="pages/framework/images/icon_remove.gif" class="removeIMG" alt="remove"/>
                                  </a> &nbsp;
                        [/#list]
                    [#else]
                        &nbsp;
                    [/#if]
                </span>
                <span class="removeStyle">
                    [#if choice_index gte questionDefinition.currentQuestion.initialNumberOfChoices]
                        <a href="removeSmartChoice#" choiceIndex="${choice_index}">[@spring.message "questionnaire.remove.link"/]</a>
                    [#else]
                        <a href="removeSmartChoice#" choiceIndex="${choice_index}" style="visibility:hidden">[@spring.message "questionnaire.remove.link"/]</a>
                    [/#if]
                </span>
                [#if questionDefinition.currentQuestion.currentSmartChoiceTags?size > 0]
                    <span class="addTagStyle">
                        [@spring.formInput "questionDefinition.currentQuestion.currentSmartChoiceTags[${choice_index}]", 'maxlength="50"'/]
                        <input type="submit" id="addSmartChoiceTag_${choice_index}" name="addSmartChoiceTag_${choice_index}" disabled="disabled"
                               class="disabledbuttn" value="[@spring.message "questionnaire.question.addtag"/] >>" choiceIndex="${choice_index}">
                    </span>
                [/#if]
            </li>
            [/#list]
        </ol>
    </fieldset>
</li>