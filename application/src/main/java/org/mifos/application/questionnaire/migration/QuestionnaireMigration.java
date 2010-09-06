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

package org.mifos.application.questionnaire.migration;

import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.questionnaire.migration.mappers.QuestionnaireMigrationMapper;
import org.mifos.customers.business.CustomerCustomFieldEntity;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.surveys.business.Survey;
import org.mifos.customers.surveys.business.SurveyInstance;
import org.mifos.customers.surveys.persistence.SurveysPersistence;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupInstanceDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class QuestionnaireMigration {

    @Autowired
    private QuestionnaireMigrationMapper questionnaireMigrationMapper;

    @Autowired
    private QuestionnaireServiceFacade questionnaireServiceFacade;

    @Autowired
    private SurveysPersistence surveysPersistence;

    @Autowired
    private CustomerDao customerDao;

    private MifosLogger mifosLogger;

    @SuppressWarnings({"UnusedDeclaration"})
    public QuestionnaireMigration() {
        mifosLogger = MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER);
    }

    // Intended to be used only from unit tests for injecting mocks
    public QuestionnaireMigration(QuestionnaireMigrationMapper questionnaireMigrationMapper,
                                  QuestionnaireServiceFacade questionnaireServiceFacade,
                                  SurveysPersistence surveysPersistence, CustomerDao customerDao) {
        this();
        this.questionnaireMigrationMapper = questionnaireMigrationMapper;
        this.questionnaireServiceFacade = questionnaireServiceFacade;
        this.surveysPersistence = surveysPersistence;
        this.customerDao = customerDao;
    }

    public Integer migrateAdditionalFields(List<CustomFieldDefinitionEntity> customFields) {
        Map<Short, Integer> customFieldQuestionIdMap = new HashMap<Short, Integer>();
        QuestionGroupDto questionGroupDto = questionnaireMigrationMapper.map(customFields, customFieldQuestionIdMap);
        Integer questionGroupId = questionnaireServiceFacade.createQuestionGroup(questionGroupDto);
        migrateAdditionalFieldsResponses(customFields, questionGroupId, customFieldQuestionIdMap);
        return questionGroupId;
    }

    private void migrateAdditionalFieldsResponses(List<CustomFieldDefinitionEntity> customFields, Integer questionGroupId, Map<Short, Integer> customFieldQuestionIdMap) {
        Map<Integer, List<CustomerCustomFieldEntity>> customerResponsesMap = getCustomerResponses(customFields);
        for (Integer customerId : customerResponsesMap.keySet()) {
            List<CustomerCustomFieldEntity> customerResponses = customerResponsesMap.get(customerId);
            QuestionGroupInstanceDto questionGroupInstanceDto = questionnaireMigrationMapper.map(questionGroupId, customerResponses, customFieldQuestionIdMap);
            questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto);
        }
    }

    private Map<Integer, List<CustomerCustomFieldEntity>> getCustomerResponses(List<CustomFieldDefinitionEntity> customFields) {
        Map<Integer, List<CustomerCustomFieldEntity>> customerResponsesMap = new HashMap<Integer, List<CustomerCustomFieldEntity>>();
        for (CustomFieldDefinitionEntity customField : customFields) {
            List<CustomerCustomFieldEntity> customFieldResponses = customerDao.getCustomFieldResponses(Integer.valueOf(customField.getFieldId()));
            for (CustomerCustomFieldEntity customFieldResponse : customFieldResponses) {
                addOrUpdate(customerResponsesMap, customFieldResponse);
            }
        }
        return customerResponsesMap;
    }

    private void addOrUpdate(Map<Integer, List<CustomerCustomFieldEntity>> customerResponsesMap, CustomerCustomFieldEntity customFieldResponse) {
        Integer customerId = customFieldResponse.getCustomerId();
        if (customerResponsesMap.containsKey(customerId)) {
            customerResponsesMap.get(customerId).add(customFieldResponse);
        } else {
            customerResponsesMap.put(customerId, new LinkedList<CustomerCustomFieldEntity>());
        }
    }

    public List<Integer> migrateSurveys(List<Survey> surveys) {
        List<Integer> questionGroupIds = new ArrayList<Integer>();
        for (Survey survey : surveys) {
            Integer questionGroupId = migrateSurvey(survey);
            if (questionGroupId != null) questionGroupIds.add(questionGroupId);
        }
        return questionGroupIds;
    }

    private Integer migrateSurvey(Survey survey) {
        QuestionGroupDto questionGroupDto = questionnaireMigrationMapper.map(survey);
        Integer questionGroupId = createQuestionGroup(questionGroupDto, survey);
        migrateSurveyResponses(survey, questionGroupId);
        return questionGroupId;
    }

    private Integer createQuestionGroup(QuestionGroupDto questionGroupDto, Survey survey) {
        Integer questionGroupId = null;
        try {
            questionGroupId = questionnaireServiceFacade.createQuestionGroup(questionGroupDto);
        } catch (Exception e) {
            mifosLogger.error(format("Unable to convert the survey, '%s' with ID, %s to a Question Group", survey.getName(), survey.getSurveyId()), e);
        }
        return questionGroupId;
    }

    private void migrateSurveyResponses(Survey survey, Integer questionGroupId) {
        if (questionGroupId != null) {
            List<SurveyInstance> surveyInstances = getSurveyInstances(survey);
            for (SurveyInstance surveyInstance : surveyInstances) {
                QuestionGroupInstanceDto questionGroupInstanceDto = questionnaireMigrationMapper.map(surveyInstance, questionGroupId);
                saveQuestionGroupInstance(questionGroupInstanceDto, surveyInstance);
            }
        }
    }

    private void saveQuestionGroupInstance(QuestionGroupInstanceDto questionGroupInstanceDto, SurveyInstance surveyInstance) {
        try {
            questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto);
        } catch (Exception e) {
            Survey survey = surveyInstance.getSurvey();
            mifosLogger.error(format("Unable to migrate a survey instance with ID, %s for the survey, '%s' with ID, %s", surveyInstance.getInstanceId(), survey.getName(), survey.getSurveyId()), e);
        }
    }

    private List<SurveyInstance> getSurveyInstances(Survey survey) {
        List<SurveyInstance> surveyInstances = new ArrayList<SurveyInstance>(0);
        try {
            surveyInstances = surveysPersistence.retrieveInstancesBySurvey(survey);
        } catch (Exception e) {
            mifosLogger.error(format("Unable to retrieve survey instances for survey, '%s' with ID, %s", survey.getName(), survey.getSurveyId()), e);
        }
        return surveyInstances;
    }
}