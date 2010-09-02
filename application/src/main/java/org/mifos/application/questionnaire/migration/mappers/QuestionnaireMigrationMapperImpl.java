/*
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
 */

package org.mifos.application.questionnaire.migration.mappers;

import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.customers.surveys.business.Question;
import org.mifos.customers.surveys.business.QuestionChoice;
import org.mifos.customers.surveys.business.Survey;
import org.mifos.customers.surveys.business.SurveyInstance;
import org.mifos.customers.surveys.business.SurveyQuestion;
import org.mifos.customers.surveys.helpers.AnswerType;
import org.mifos.customers.surveys.helpers.SurveyType;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.dtos.ChoiceDto;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupInstanceDto;
import org.mifos.platform.questionnaire.service.dtos.SectionDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.DEFAULT_EVENT_FOR_CUSTOM_FIELDS;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.DEFAULT_EVENT_FOR_SURVEYS;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.DEFAULT_SECTION_NAME;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.QUESTION_GROUP_TITLE_FOR_ADDITIONAL_FIELDS;
import static org.mifos.platform.util.CollectionUtils.asMap;
import static org.mifos.platform.util.MapEntry.makeEntry;

public class QuestionnaireMigrationMapperImpl implements QuestionnaireMigrationMapper {

    private Map<CustomFieldType, QuestionType> customFieldTypeToQuestionTypeMap;
    private Map<EntityType, String> entityTypeToSourceMap;
    private Map<SurveyType, String> surveyTypeToSourceMap;
    private Map<AnswerType, QuestionType> answerToQuestionType;
    private static final int DEFAULT_ORDER = 0;

    public QuestionnaireMigrationMapperImpl() {
        populateTypeMappings();
        populateEntityTypeToSourceMappings();
        populateSurveyTypeToSourceMappings();
        populateAnswerToQuestionTypeMappings();
    }

    @Override
    public QuestionDto map(CustomFieldDefinitionEntity customField, Integer questionOrder) {
        QuestionDto questionDto = new QuestionDto();
        questionDto.setTitle(customField.getLabel());
        questionDto.setType(mapToQuestionType(customField.getFieldTypeAsEnum()));
        questionDto.setMandatory(customField.isMandatory());
        questionDto.setOrder(questionOrder);
        return questionDto;
    }

    @Override
    public QuestionGroupDto map(List<CustomFieldDefinitionEntity> customFields) {
        SectionDto sectionDto = getDefaultSection();
        for (int i = 0, customFieldsSize = customFields.size(); i < customFieldsSize; i++) {
            CustomFieldDefinitionEntity customField = customFields.get(i);
            sectionDto.addQuestion(map(customField, i));
        }
        return getQuestionGroup(sectionDto, customFields.get(0).getEntityType());
    }

    @Override
    public QuestionGroupDto map(Survey survey) {
        QuestionGroupDto questionGroupDto = new QuestionGroupDto();
        questionGroupDto.setTitle(survey.getName());
        questionGroupDto.setEventSourceDto(mapEventSourceForSurvey(survey));
        questionGroupDto.addSection(mapToSectionForSurvey(survey.getQuestions()));
        return questionGroupDto;
    }

    @Override
    public QuestionGroupInstanceDto map(SurveyInstance surveyInstance) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private SectionDto mapToSectionForSurvey(List<SurveyQuestion> questions) {
        SectionDto sectionDto = getDefaultSection();
        for (SurveyQuestion question : questions) {
            sectionDto.addQuestion(mapToQuestionDto(question));
        }
        return sectionDto;
    }

    private QuestionDto mapToQuestionDto(SurveyQuestion surveyQuestion) {
        QuestionDto questionDto = new QuestionDto();
        Question question = surveyQuestion.getQuestion();
        questionDto.setTitle(question.getShortName());
        questionDto.setMandatory(surveyQuestion.getMandatory() == 1);
        questionDto.setOrder(surveyQuestion.getOrder());
        AnswerType answerType = question.getAnswerTypeAsEnum();
        questionDto.setType(answerToQuestionType.get(answerType));
        if (answerType == AnswerType.NUMBER)
            mapNumberQuestion(questionDto, question);
        else if (answerType == AnswerType.SINGLESELECT || answerType == AnswerType.MULTISELECT || answerType == AnswerType.CHOICE)
            mapChoiceBasedQuestion(questionDto, question.getChoices());
        return questionDto;
    }

    private void mapChoiceBasedQuestion(QuestionDto questionDto, List<QuestionChoice> questionChoices) {
        List<ChoiceDto> choices = new ArrayList<ChoiceDto>();
        for (int i = 0, choicesSize = questionChoices.size(); i < choicesSize; i++) {
            QuestionChoice questionChoice = questionChoices.get(i);
            choices.add(mapToChoiceDto(i, questionChoice));
        }
        questionDto.setChoices(choices);
    }

    private ChoiceDto mapToChoiceDto(int i, QuestionChoice questionChoice) {
        ChoiceDto choiceDto = new ChoiceDto();
        choiceDto.setOrder(i);
        choiceDto.setValue(questionChoice.getChoiceText());
        return choiceDto;
    }

    private void mapNumberQuestion(QuestionDto questionDto, Question question) {
        questionDto.setMinValue(question.getNumericMin());
        questionDto.setMaxValue(question.getNumericMax());
    }

    private QuestionGroupDto getQuestionGroup(SectionDto sectionDto, Short entityType) {
        QuestionGroupDto questionGroupDto = new QuestionGroupDto();
        questionGroupDto.addSection(sectionDto);
        questionGroupDto.setEditable(false);
        questionGroupDto.setPpi(false);
        EventSourceDto eventSourceDto = mapEventSourceForCustomField(entityType);
        questionGroupDto.setEventSourceDto(eventSourceDto);
        questionGroupDto.setTitle(format(QUESTION_GROUP_TITLE_FOR_ADDITIONAL_FIELDS, eventSourceDto));
        return questionGroupDto;
    }

    private EventSourceDto mapEventSourceForSurvey(Survey survey) {
        SurveyType surveyType = survey.getAppliesToAsEnum();
        String event = DEFAULT_EVENT_FOR_SURVEYS;
        String source = surveyTypeToSourceMap.get(surveyType);
        return new EventSourceDto(event, source, getEventSourceDescription(event, source));
    }

    private EventSourceDto mapEventSourceForCustomField(Short entityTypeId) {
        EntityType entityType = EntityType.fromInt(entityTypeId);
        String event = DEFAULT_EVENT_FOR_CUSTOM_FIELDS;
        String source = entityTypeToSourceMap.get(entityType);
        return new EventSourceDto(event, source, getEventSourceDescription(event, source));
    }

    private String getEventSourceDescription(String event, String source) {
        return format("%s %s", event, source);
    }

    private SectionDto getDefaultSection() {
        SectionDto sectionDto = new SectionDto();
        sectionDto.setName(DEFAULT_SECTION_NAME);
        sectionDto.setOrder(DEFAULT_ORDER);
        return sectionDto;
    }

    private QuestionType mapToQuestionType(CustomFieldType customFieldType) {
        return customFieldTypeToQuestionTypeMap.get(customFieldType);
    }

    private void populateEntityTypeToSourceMappings() {
        entityTypeToSourceMap = asMap(
                makeEntry(EntityType.CLIENT, "Client"),
                makeEntry(EntityType.GROUP, "Group"),
                makeEntry(EntityType.CENTER, "Center")
        );
    }

    private void populateSurveyTypeToSourceMappings() {
        surveyTypeToSourceMap = asMap(
                makeEntry(SurveyType.CLIENT, "Client"),
                makeEntry(SurveyType.GROUP, "Group"),
                makeEntry(SurveyType.CENTER, "Center")
        );
    }

    private void populateTypeMappings() {
        customFieldTypeToQuestionTypeMap = asMap(makeEntry(CustomFieldType.NONE, QuestionType.INVALID),
                makeEntry(CustomFieldType.ALPHA_NUMERIC, QuestionType.FREETEXT),
                makeEntry(CustomFieldType.NUMERIC, QuestionType.NUMERIC),
                makeEntry(CustomFieldType.DATE, QuestionType.DATE));
    }

    private void populateAnswerToQuestionTypeMappings() {
        answerToQuestionType = asMap(makeEntry(AnswerType.INVALID, QuestionType.INVALID),
                makeEntry(AnswerType.FREETEXT, QuestionType.FREETEXT),
                makeEntry(AnswerType.DATE, QuestionType.DATE),
                makeEntry(AnswerType.NUMBER, QuestionType.NUMERIC),
                makeEntry(AnswerType.SINGLESELECT, QuestionType.SINGLE_SELECT),
                makeEntry(AnswerType.CHOICE, QuestionType.SINGLE_SELECT),
                makeEntry(AnswerType.MULTISELECT, QuestionType.MULTI_SELECT));
    }
}