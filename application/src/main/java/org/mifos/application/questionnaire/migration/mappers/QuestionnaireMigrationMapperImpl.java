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

import org.apache.commons.lang.StringUtils;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountCustomFieldEntity;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerCustomFieldEntity;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficeCustomFieldEntity;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelCustomFieldEntity;
import org.mifos.customers.surveys.business.Question;
import org.mifos.customers.surveys.business.QuestionChoice;
import org.mifos.customers.surveys.business.Survey;
import org.mifos.customers.surveys.business.SurveyInstance;
import org.mifos.customers.surveys.business.SurveyQuestion;
import org.mifos.customers.surveys.business.SurveyResponse;
import org.mifos.customers.surveys.helpers.AnswerType;
import org.mifos.customers.surveys.helpers.SurveyType;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.service.dtos.ChoiceDto;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupInstanceDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupResponseDto;
import org.mifos.platform.questionnaire.service.dtos.SectionDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import static java.lang.String.format;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.DEFAULT_EVENT_FOR_CUSTOM_FIELDS;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.DEFAULT_EVENT_FOR_SURVEYS;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.DEFAULT_ORDER;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.DEFAULT_SECTION_NAME;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.DEFAULT_VERSION;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.MULTI_SELECT_DELIMITER;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.QUESTION_GROUP_TITLE_FOR_ADDITIONAL_FIELDS;
import static org.mifos.platform.util.CollectionUtils.asMap;
import static org.mifos.platform.util.MapEntry.makeEntry;

public class QuestionnaireMigrationMapperImpl implements QuestionnaireMigrationMapper {

    private static final Logger logger = LoggerFactory.getLogger(QuestionnaireMigrationMapperImpl.class);

    private Map<CustomFieldType, QuestionType> customFieldTypeToQuestionTypeMap;
    private Map<EntityType, String> entityTypeToSourceMap;
    private Map<SurveyType, String> surveyTypeToSourceMap;
    private Map<AnswerType, QuestionType> answerToQuestionType;

    @Autowired
    private QuestionnaireServiceFacade questionnaireServiceFacade;

    public QuestionnaireMigrationMapperImpl() {
        populateTypeMappings();
        populateEntityTypeToSourceMappings();
        populateSurveyTypeToSourceMappings();
        populateAnswerToQuestionTypeMappings();
    }

    // Intended to be used from unit tests for injecting mocks
    public QuestionnaireMigrationMapperImpl(QuestionnaireServiceFacade questionnaireServiceFacade) {
        this();
        this.questionnaireServiceFacade = questionnaireServiceFacade;
    }

    @Override
    public QuestionDto map(CustomFieldDefinitionEntity customField, Integer questionOrder) {
        QuestionDto questionDto = new QuestionDto();
        questionDto.setText(customField.getLabel());
        questionDto.setType(mapToQuestionType(customField.getFieldTypeAsEnum()));
        questionDto.setMandatory(customField.isMandatory());
        questionDto.setOrder(questionOrder);
        return questionDto;
    }

    @Override
    public QuestionGroupDto map(Iterator<CustomFieldDefinitionEntity> customFields, Map<Short, Integer> customFieldQuestionIdMap, EntityType entityType) {
        SectionDto sectionDto = getDefaultSection();
        if (customFields != null) {
            for (int i = 0; customFields.hasNext(); i++) {
                CustomFieldDefinitionEntity customField = customFields.next();
                QuestionDto questionDto = map(customField, i);
                Integer questionId = createQuestion(questionDto, customField.getFieldId());
                if (questionId != null) {
                    sectionDto.addQuestion(questionDto);
                    customFieldQuestionIdMap.put(customField.getFieldId(), questionId);
                }
            }
        }
        return getQuestionGroup(sectionDto, entityType.getValue());
    }

    private Integer createQuestion(QuestionDto questionDto, Short fieldId) {
        Integer questionId = null;
        try {
            questionId = questionnaireServiceFacade.createQuestion(questionDto);
        } catch (Exception e) {
            logger.error(format("Unable to migrate a Custom Field with ID, %s to a Question", fieldId), e);
        }
        return questionId;
    }

    @Override
    public QuestionGroupDto map(Survey survey) {
        QuestionGroupDto questionGroupDto = new QuestionGroupDto();
        questionGroupDto.setTitle(survey.getName());
        questionGroupDto.setEditable(false);
        questionGroupDto.setPpi(false);
        questionGroupDto.setEventSourceDtos(Arrays.asList(mapEventSourceForSurvey(survey)));
        questionGroupDto.addSection(mapToSectionForSurvey(survey.getQuestions()));
        return questionGroupDto;
    }

    @Override
    public QuestionGroupInstanceDto map(SurveyInstance surveyInstance, Integer questionGroupId, Integer eventSourceId) {
        QuestionGroupInstanceDto questionGroupInstanceDto = new QuestionGroupInstanceDto();
        questionGroupInstanceDto.setDateConducted(surveyInstance.getDateConducted());
        questionGroupInstanceDto.setCompleted(surveyInstance.getCompletedStatus());
        questionGroupInstanceDto.setCreatorId(Integer.valueOf(surveyInstance.getCreator().getPersonnelId()));
        questionGroupInstanceDto.setEventSourceId(eventSourceId);
        questionGroupInstanceDto.setEntityId(mapToEntityId(surveyInstance));
        questionGroupInstanceDto.setQuestionGroupId(questionGroupId);
        questionGroupInstanceDto.setVersion(DEFAULT_VERSION);
        questionGroupInstanceDto.setQuestionGroupResponseDtos(mapToQuestionGroupResponseDtos(surveyInstance, questionGroupId));
        return questionGroupInstanceDto;
    }

    @Override
    public QuestionGroupInstanceDto mapForCustomers(Integer questionGroupId, Integer eventSourceId, List<CustomerCustomFieldEntity> customerResponses, Map<Short, Integer> customFieldQuestionIdMap) {
        QuestionGroupInstanceDto questionGroupInstanceDto = new QuestionGroupInstanceDto();
        CustomerBO customer = customerResponses.get(0).getCustomer();
        questionGroupInstanceDto.setDateConducted(mapToDateConducted(customer.getCreatedDate(), customer.getUpdatedDate()));
        questionGroupInstanceDto.setCompleted(true);
        questionGroupInstanceDto.setCreatorId(mapToCreatorId(customer.getCreatedBy(), customer.getUpdatedBy()));
        questionGroupInstanceDto.setEventSourceId(eventSourceId);
        questionGroupInstanceDto.setEntityId(customer.getCustomerId());
        questionGroupInstanceDto.setQuestionGroupId(questionGroupId);
        questionGroupInstanceDto.setVersion(DEFAULT_VERSION);
        questionGroupInstanceDto.setQuestionGroupResponseDtos(mapToQuestionGroupResponseDtosForCustomer(questionGroupId, customerResponses, customFieldQuestionIdMap));
        return questionGroupInstanceDto;
    }

    @Override
    public QuestionGroupInstanceDto mapForAccounts(Integer questionGroupId, Integer eventSourceId, List<AccountCustomFieldEntity> accountResponses, Map<Short, Integer> customFieldQuestionIdMap) {
        QuestionGroupInstanceDto questionGroupInstanceDto = new QuestionGroupInstanceDto();
        AccountBO account = accountResponses.get(0).getAccount();
        questionGroupInstanceDto.setDateConducted(mapToDateConducted(account.getCreatedDate(), account.getUpdatedDate()));
        questionGroupInstanceDto.setCompleted(true);
        questionGroupInstanceDto.setCreatorId(mapToCreatorId(account.getCreatedBy(), account.getUpdatedBy()));
        questionGroupInstanceDto.setEventSourceId(eventSourceId);
        questionGroupInstanceDto.setEntityId(account.getAccountId());
        questionGroupInstanceDto.setQuestionGroupId(questionGroupId);
        questionGroupInstanceDto.setVersion(DEFAULT_VERSION);
        questionGroupInstanceDto.setQuestionGroupResponseDtos(mapToQuestionGroupResponseDtosForAccount(questionGroupId, accountResponses, customFieldQuestionIdMap));
        return questionGroupInstanceDto;
    }

    @Override
    public QuestionGroupInstanceDto mapForOffice(Integer questionGroupId, Integer eventSourceId, List<OfficeCustomFieldEntity> officeResponses, Map<Short, Integer> customFieldQuestionIdMap) {
        QuestionGroupInstanceDto questionGroupInstanceDto = new QuestionGroupInstanceDto();
        OfficeBO office = officeResponses.get(0).getOffice();
        questionGroupInstanceDto.setDateConducted(mapToDateConducted(office.getCreatedDate(), office.getUpdatedDate()));
        questionGroupInstanceDto.setCompleted(true);
        questionGroupInstanceDto.setCreatorId(mapToCreatorId(office.getCreatedBy(), office.getUpdatedBy()));
        questionGroupInstanceDto.setEventSourceId(eventSourceId);
        questionGroupInstanceDto.setEntityId(office.getOfficeId().intValue());
        questionGroupInstanceDto.setQuestionGroupId(questionGroupId);
        questionGroupInstanceDto.setVersion(DEFAULT_VERSION);
        questionGroupInstanceDto.setQuestionGroupResponseDtos(mapToQuestionGroupResponseDtosForOffice(questionGroupId, officeResponses, customFieldQuestionIdMap));
        return questionGroupInstanceDto;
    }

    @Override
    public QuestionGroupInstanceDto mapForPersonnel(Integer questionGroupId, Integer eventSourceId, List<PersonnelCustomFieldEntity> personnelResponses, Map<Short, Integer> customFieldQuestionIdMap) {
        QuestionGroupInstanceDto questionGroupInstanceDto = new QuestionGroupInstanceDto();
        PersonnelBO personnel = personnelResponses.get(0).getPersonnel();
        questionGroupInstanceDto.setDateConducted(mapToDateConducted(personnel.getCreatedDate(), personnel.getUpdatedDate()));
        questionGroupInstanceDto.setCompleted(true);
        questionGroupInstanceDto.setCreatorId(mapToCreatorId(personnel.getCreatedBy(), personnel.getUpdatedBy()));
        questionGroupInstanceDto.setEventSourceId(eventSourceId);
        questionGroupInstanceDto.setEntityId(personnel.getPersonnelId().intValue());
        questionGroupInstanceDto.setQuestionGroupId(questionGroupId);
        questionGroupInstanceDto.setVersion(DEFAULT_VERSION);
        questionGroupInstanceDto.setQuestionGroupResponseDtos(mapToQuestionGroupResponseDtosForPersonnel(questionGroupId, personnelResponses, customFieldQuestionIdMap));
        return questionGroupInstanceDto;
    }

    private Date mapToDateConducted(Date createdDate, Date updatedDate) {
        if (createdDate != null) {
            return createdDate;
        } else if (updatedDate != null) {
            return updatedDate;
        }
        return Calendar.getInstance().getTime();
    }

    private Integer mapToCreatorId(Short createdBy, Short updatedBy) {
        if (createdBy != null) {
            return Integer.valueOf(createdBy);
        } else if (updatedBy != null) {
            return Integer.valueOf(updatedBy);
        } else {
            return 0;
        }
    }

    private List<QuestionGroupResponseDto> mapToQuestionGroupResponseDtosForCustomer(Integer questionGroupId, List<CustomerCustomFieldEntity> customerResponses, Map<Short, Integer> customFieldQuestionIdMap) {
        List<QuestionGroupResponseDto> questionGroupResponseDtos = new ArrayList<QuestionGroupResponseDto>();
        for (CustomerCustomFieldEntity customerResponse : customerResponses) {
            Short fieldId = customerResponse.getFieldId();
            String fieldValue = customerResponse.getFieldValue();
            QuestionGroupResponseDto questionGroupResponseDto = mapToQuestionGroupResponseDto(questionGroupId, customFieldQuestionIdMap, fieldId, fieldValue);
            if (questionGroupResponseDto != null) {
                questionGroupResponseDtos.add(questionGroupResponseDto);
            }
        }
        return questionGroupResponseDtos;
    }

    private List<QuestionGroupResponseDto> mapToQuestionGroupResponseDtosForAccount(Integer questionGroupId, List<AccountCustomFieldEntity> accountResponses, Map<Short, Integer> customFieldQuestionIdMap) {
        List<QuestionGroupResponseDto> questionGroupResponseDtos = new ArrayList<QuestionGroupResponseDto>();
        for (AccountCustomFieldEntity accountResponse : accountResponses) {
            Short fieldId = accountResponse.getFieldId();
            String fieldValue = accountResponse.getFieldValue();
            QuestionGroupResponseDto questionGroupResponseDto = mapToQuestionGroupResponseDto(questionGroupId, customFieldQuestionIdMap, fieldId, fieldValue);
            if (questionGroupResponseDto != null) {
                questionGroupResponseDtos.add(questionGroupResponseDto);
            }
        }
        return questionGroupResponseDtos;
    }

    private List<QuestionGroupResponseDto> mapToQuestionGroupResponseDtosForOffice(Integer questionGroupId, List<OfficeCustomFieldEntity> officeResponses, Map<Short, Integer> customFieldQuestionIdMap) {
        List<QuestionGroupResponseDto> questionGroupResponseDtos = new ArrayList<QuestionGroupResponseDto>();
        for (OfficeCustomFieldEntity officeResponse : officeResponses) {
            Short fieldId = officeResponse.getFieldId();
            String fieldValue = officeResponse.getFieldValue();
            QuestionGroupResponseDto questionGroupResponseDto = mapToQuestionGroupResponseDto(questionGroupId, customFieldQuestionIdMap, fieldId, fieldValue);
            if (questionGroupResponseDto != null) {
                questionGroupResponseDtos.add(questionGroupResponseDto);
            }
        }
        return questionGroupResponseDtos;
    }

    private List<QuestionGroupResponseDto> mapToQuestionGroupResponseDtosForPersonnel(Integer questionGroupId, List<PersonnelCustomFieldEntity> personnelResponses, Map<Short, Integer> customFieldQuestionIdMap) {
        List<QuestionGroupResponseDto> questionGroupResponseDtos = new ArrayList<QuestionGroupResponseDto>();
        for (PersonnelCustomFieldEntity personnelResponse : personnelResponses) {
            Short fieldId = personnelResponse.getFieldId();
            String fieldValue = personnelResponse.getFieldValue();
            QuestionGroupResponseDto questionGroupResponseDto = mapToQuestionGroupResponseDto(questionGroupId, customFieldQuestionIdMap, fieldId, fieldValue);
            if (questionGroupResponseDto != null) {
                questionGroupResponseDtos.add(questionGroupResponseDto);
            }
        }
        return questionGroupResponseDtos;
    }

    private QuestionGroupResponseDto mapToQuestionGroupResponseDto(Integer questionGroupId, Map<Short, Integer> customFieldQuestionIdMap, Short fieldId, String fieldValue) {
        QuestionGroupResponseDto questionGroupResponseDto = null;
        Integer questionId = customFieldQuestionIdMap.get(fieldId);
        if (questionId != null && questionId != 0) {
            Integer sectionQuestionId = questionnaireServiceFacade.getSectionQuestionId(DEFAULT_SECTION_NAME, questionId, questionGroupId);
            if (sectionQuestionId != null && sectionQuestionId != 0) {
                questionGroupResponseDto = new QuestionGroupResponseDto();
                questionGroupResponseDto.setResponse(fieldValue);
                questionGroupResponseDto.setSectionQuestionId(sectionQuestionId);
            }
        }
        return questionGroupResponseDto;
    }

    private List<QuestionGroupResponseDto> mapToQuestionGroupResponseDtos(SurveyInstance surveyInstance, Integer questionGroupId) {
        List<QuestionGroupResponseDto> questionGroupResponseDtos = new ArrayList<QuestionGroupResponseDto>();
        for (SurveyResponse surveyResponse : surveyInstance.getSurveyResponses()) {
            if (surveyResponse.getQuestion().getAnswerTypeAsEnum() == AnswerType.MULTISELECT) {
                questionGroupResponseDtos.addAll(mapToMultiSelectQuestionGroupResponses(questionGroupId, surveyResponse));
            } else {
                questionGroupResponseDtos.add(mapToQuestionGroupResponse(questionGroupId, surveyResponse));
            }
        }
        return questionGroupResponseDtos;
    }

    private List<QuestionGroupResponseDto> mapToMultiSelectQuestionGroupResponses(Integer questionGroupId, SurveyResponse surveyResponse) {
        List<QuestionGroupResponseDto> questionGroupResponseDtos = new ArrayList<QuestionGroupResponseDto>();
        String multiSelectValue = surveyResponse.getMultiSelectValue();
        if (StringUtils.isNotEmpty(multiSelectValue)) {
            Map<Integer, QuestionChoice> choiceLookup = getChoiceLookup(surveyResponse);
            Integer questionId = surveyResponse.getQuestion().getQuestionId();
            Integer sectionQuestionId = getSectionQuestionId(questionGroupId, questionId);
            String[] answers = StringUtils.split(multiSelectValue, MULTI_SELECT_DELIMITER);
            for (int ansIndex = 0; ansIndex < answers.length; ansIndex++) {
                if (isChoiceSelected(answers[ansIndex])) {
                    String answer = choiceLookup.get(ansIndex).getChoiceText();
                    questionGroupResponseDtos.add(mapToQuestionGroupResponse(sectionQuestionId, answer));
                }
            }
        }
        return questionGroupResponseDtos;
    }

    private boolean isChoiceSelected(String answer) {
        return StringUtils.isNotEmpty(answer) && QuestionnaireConstants.CHOICE_SELECTED.equals(answer);
    }

    private Map<Integer, QuestionChoice> getChoiceLookup(SurveyResponse surveyResponse) {
        Map<Integer, QuestionChoice> questionChoiceLookup = new HashMap<Integer, QuestionChoice>();
        for (QuestionChoice questionChoice : surveyResponse.getSurveyQuestion().getQuestion().getChoices()) {
            questionChoiceLookup.put(questionChoice.getChoiceOrder(), questionChoice);
        }
        return questionChoiceLookup;
    }

    private QuestionGroupResponseDto mapToQuestionGroupResponse(Integer sectionQuestionId, String answer) {
        QuestionGroupResponseDto questionGroupResponseDto = new QuestionGroupResponseDto();
        questionGroupResponseDto.setResponse(answer);
        questionGroupResponseDto.setSectionQuestionId(sectionQuestionId);
        return questionGroupResponseDto;
    }

    private QuestionGroupResponseDto mapToQuestionGroupResponse(Integer questionGroupId, SurveyResponse surveyResponse) {
        Integer questionId = surveyResponse.getQuestion().getQuestionId();
        Integer sectionQuestionId = getSectionQuestionId(questionGroupId, questionId);
        return mapToQuestionGroupResponse(sectionQuestionId, surveyResponse.toString());
    }

    private Integer getSectionQuestionId(Integer questionGroupId, Integer questionId) {
        return questionnaireServiceFacade.getSectionQuestionId(DEFAULT_SECTION_NAME, questionId, questionGroupId);
    }

    private Integer mapToEntityId(SurveyInstance surveyInstance) {
        Integer result = 0;
        if (surveyInstance.isForCustomer()) {
            result = surveyInstance.getCustomer().getCustomerId();
        } else if (surveyInstance.isForAccount()) {
            result = surveyInstance.getAccount().getAccountId();
        }
        return result;
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
        questionDto.setText(question.getQuestionText());
        questionDto.setMandatory(surveyQuestion.getMandatory() == 1);
        questionDto.setOrder(surveyQuestion.getOrder());
        AnswerType answerType = question.getAnswerTypeAsEnum();
        questionDto.setType(answerToQuestionType.get(answerType));
        if (answerType == AnswerType.NUMBER) {
            mapNumberQuestion(questionDto, question);
        } else if (answerType == AnswerType.SINGLESELECT || answerType == AnswerType.MULTISELECT || answerType == AnswerType.CHOICE) {
            mapChoiceBasedQuestion(questionDto, question.getChoices());
        }
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
        questionGroupDto.setEventSourceDtos(Arrays.asList(eventSourceDto));
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
                makeEntry(EntityType.CENTER, "Center"),
                makeEntry(EntityType.LOAN, "Loan"),
                makeEntry(EntityType.SAVINGS, "Savings"),
                makeEntry(EntityType.OFFICE, "Office"),
                makeEntry(EntityType.PERSONNEL, "Personnel")
        );
    }

    private void populateSurveyTypeToSourceMappings() {
        surveyTypeToSourceMap = asMap(
                makeEntry(SurveyType.CLIENT, "Client"),
                makeEntry(SurveyType.GROUP, "Group"),
                makeEntry(SurveyType.CENTER, "Center"),
                makeEntry(SurveyType.LOAN, "Loan"),
                makeEntry(SurveyType.SAVINGS, "Savings"),
                makeEntry(SurveyType.ALL, "All")
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