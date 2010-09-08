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

import org.mifos.accounts.business.AccountCustomFieldEntity;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.questionnaire.migration.mappers.QuestionnaireMigrationMapper;
import org.mifos.customers.business.CustomerCustomFieldEntity;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.surveys.business.Survey;
import org.mifos.customers.surveys.business.SurveyInstance;
import org.mifos.customers.surveys.helpers.SurveyType;
import org.mifos.customers.surveys.persistence.SurveysPersistence;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
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

    @Autowired
    private LoanDao loanDao;

    private MifosLogger mifosLogger;

    @SuppressWarnings({"UnusedDeclaration"})
    public QuestionnaireMigration() {
        mifosLogger = MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER);
    }

    // Intended to be used only from unit tests for injecting mocks
    public QuestionnaireMigration(QuestionnaireMigrationMapper questionnaireMigrationMapper,
                                  QuestionnaireServiceFacade questionnaireServiceFacade,
                                  SurveysPersistence surveysPersistence, CustomerDao customerDao, LoanDao loanDao) {
        this();
        this.questionnaireMigrationMapper = questionnaireMigrationMapper;
        this.questionnaireServiceFacade = questionnaireServiceFacade;
        this.surveysPersistence = surveysPersistence;
        this.customerDao = customerDao;
        this.loanDao = loanDao;
    }

    public List<Integer> migrateAdditionalFields() {
        List<Integer> questionGroupIds = new ArrayList<Integer>();
        Integer questionGroupId = migrateAdditionalFieldsForClient();
        if (questionGroupId != null) questionGroupIds.add(questionGroupId);
        questionGroupId = migrateAdditionalFieldsForGroup();
        if (questionGroupId != null) questionGroupIds.add(questionGroupId);
        questionGroupId = migrateAdditionalFieldsForLoan();
        if (questionGroupId != null) questionGroupIds.add(questionGroupId);
        return questionGroupIds;
    }

    public List<Integer> migrateSurveys() {
        return migrateSurveysForClient();
    }

    // Made 'public' to aid unit testing
    public Integer migrateAdditionalFieldsForGroup() {
        return migrateAdditionalFieldsForCustomer(getCustomFieldsForGroup());
    }

    // Made 'public' to aid unit testing
    public Integer migrateAdditionalFieldsForClient() {
        return migrateAdditionalFieldsForCustomer(getCustomFieldsForClient());
    }

    // Made 'public' to aid unit testing
    public Integer migrateAdditionalFieldsForLoan() {
        return migrateAdditionalFieldsForLoan(getCustomFieldsForLoan());
    }

    private Integer migrateAdditionalFieldsForLoan(List<CustomFieldDefinitionEntity> customFields) {
        Integer questionGroupId = null;
        if (customFields != null && !customFields.isEmpty()) {
            Map<Short, Integer> customFieldQuestionIdMap = new HashMap<Short, Integer>();
            questionGroupId = getQuestionGroup(customFields, customFieldQuestionIdMap);
            migrateAdditionalFieldsResponsesForLoan(customFields, questionGroupId, customFieldQuestionIdMap);
        }
        return questionGroupId;
    }

    private Integer migrateAdditionalFieldsForCustomer(List<CustomFieldDefinitionEntity> customFields) {
        Integer questionGroupId = null;
        if (customFields != null && !customFields.isEmpty()) {
            Map<Short, Integer> customFieldQuestionIdMap = new HashMap<Short, Integer>();
            questionGroupId = getQuestionGroup(customFields, customFieldQuestionIdMap);
            migrateAdditionalFieldsResponsesForCustomer(customFields, questionGroupId, customFieldQuestionIdMap);
        }
        return questionGroupId;
    }

    private Integer getQuestionGroup(List<CustomFieldDefinitionEntity> customFields, Map<Short, Integer> customFieldQuestionIdMap) {
        Integer questionGroupId = null;
        if (customFields != null && !customFields.isEmpty()) {
            QuestionGroupDto questionGroupDto = mapAdditionalFieldsToQuestionGroupDto(customFields, customFieldQuestionIdMap);
            questionGroupId = createQuestionGroup(questionGroupDto);
        }
        return questionGroupId;
    }

    private Integer createQuestionGroup(QuestionGroupDto questionGroupDto) {
        Integer questionGroupId = null;
        try {
            if (questionGroupDto != null) questionGroupId = questionnaireServiceFacade.createQuestionGroup(questionGroupDto);
        } catch (Exception e) {
            mifosLogger.error("Unable to create a Question Group from custom fields", e);
        }
        return questionGroupId;
    }

    private QuestionGroupDto mapAdditionalFieldsToQuestionGroupDto(List<CustomFieldDefinitionEntity> customFields, Map<Short, Integer> customFieldQuestionIdMap) {
        QuestionGroupDto questionGroupDto = null;
        try {
            questionGroupDto = questionnaireMigrationMapper.map(customFields, customFieldQuestionIdMap);
        } catch (Exception e) {
            mifosLogger.error("Unable to migrate custom fields to a Question Group", e);
        }
        return questionGroupDto;
    }

    private List<CustomFieldDefinitionEntity> getCustomFieldsForGroup() {
        List<CustomFieldDefinitionEntity> customFields = new ArrayList<CustomFieldDefinitionEntity>(0);
        try {
            customFields = customerDao.retrieveCustomFieldEntitiesForGroup();
        } catch (Exception e) {
            mifosLogger.error("Unable to retrieve custom fields for Create Group", e);
        }
        return customFields;
    }

    private List<CustomFieldDefinitionEntity> getCustomFieldsForClient() {
        List<CustomFieldDefinitionEntity> customFields = new ArrayList<CustomFieldDefinitionEntity>(0);
        try {
            customFields = customerDao.retrieveCustomFieldEntitiesForClient();
        } catch (Exception e) {
            mifosLogger.error("Unable to retrieve custom fields for Create Client", e);
        }
        return customFields;
    }

    private List<CustomFieldDefinitionEntity> getCustomFieldsForLoan() {
        List<CustomFieldDefinitionEntity> customFields = new ArrayList<CustomFieldDefinitionEntity>(0);
        try {
            customFields = loanDao.retrieveCustomFieldEntitiesForLoan();
        } catch (Exception e) {
            mifosLogger.error("Unable to retrieve custom fields for Create Client", e);
        }
        return customFields;
    }

    private void migrateAdditionalFieldsResponsesForCustomer(List<CustomFieldDefinitionEntity> customFields, Integer questionGroupId,
                                                             Map<Short, Integer> customFieldQuestionIdMap) {
        if (questionGroupId != null) {
            Map<Integer, List<CustomerCustomFieldEntity>> customFieldResponses = getCustomFieldResponsesForCustomer(customFields);
            for (Integer entityId : customFieldResponses.keySet()) {
                List<CustomerCustomFieldEntity> customerResponses = customFieldResponses.get(entityId);
                QuestionGroupInstanceDto questionGroupInstanceDto = mapToQuestionGroupInstanceForCustomer(questionGroupId, customFieldQuestionIdMap, customerResponses);
                saveQuestionGroupInstance(questionGroupInstanceDto);
            }
        }
    }

    private void migrateAdditionalFieldsResponsesForLoan(List<CustomFieldDefinitionEntity> customFields, Integer questionGroupId,
                                                             Map<Short, Integer> customFieldQuestionIdMap) {
        if (questionGroupId != null) {
            Map<Integer, List<AccountCustomFieldEntity>> customFieldResponses = getCustomFieldResponsesForLoan(customFields);
            for (Integer entityId : customFieldResponses.keySet()) {
                List<AccountCustomFieldEntity> accountResponses = customFieldResponses.get(entityId);
                QuestionGroupInstanceDto questionGroupInstanceDto = mapToQuestionGroupInstanceForLoan(questionGroupId, customFieldQuestionIdMap, accountResponses);
                saveQuestionGroupInstance(questionGroupInstanceDto);
            }
        }
    }

    private QuestionGroupInstanceDto mapToQuestionGroupInstanceForLoan(Integer questionGroupId, Map<Short, Integer> customFieldQuestionIdMap,
                                                                List<AccountCustomFieldEntity> accountResponses) {
        QuestionGroupInstanceDto questionGroupInstanceDto = null;
        try {
            questionGroupInstanceDto = questionnaireMigrationMapper.mapForAccounts(questionGroupId, accountResponses, customFieldQuestionIdMap);
        } catch (Exception e) {
            mifosLogger.error(format("Unable to convert responses given for account with ID, %d for custom fields, to Question Group responses", accountResponses.get(0).getAccountId()), e);
        }
        return questionGroupInstanceDto;
    }

    private Map<Integer, List<AccountCustomFieldEntity>> getCustomFieldResponsesForLoan(List<CustomFieldDefinitionEntity> customFields) {
        Map<Integer, List<AccountCustomFieldEntity>> entityResponsesMap = new HashMap<Integer, List<AccountCustomFieldEntity>>();
        for (CustomFieldDefinitionEntity customField : customFields) {
            List<AccountCustomFieldEntity> customFieldResponses = getCustomFieldResponsesForLoan(customField);
            for (AccountCustomFieldEntity customFieldResponse : customFieldResponses) {
                addOrUpdateForLoan(entityResponsesMap, customFieldResponse);
            }
        }
        return entityResponsesMap;
    }

    private void addOrUpdateForLoan(Map<Integer, List<AccountCustomFieldEntity>> entityResponsesMap, AccountCustomFieldEntity customFieldResponse) {
        Integer accountId = customFieldResponse.getAccountId();
        if (entityResponsesMap.containsKey(accountId)) {
            entityResponsesMap.get(accountId).add(customFieldResponse);
        } else {
            entityResponsesMap.put(accountId, new LinkedList<AccountCustomFieldEntity>());
            entityResponsesMap.get(accountId).add(customFieldResponse);
        }
    }

    private List<AccountCustomFieldEntity> getCustomFieldResponsesForLoan(CustomFieldDefinitionEntity customField) {
        List<AccountCustomFieldEntity> customFieldResponses = new ArrayList<AccountCustomFieldEntity>(0);
        try {
            customFieldResponses = loanDao.getCustomFieldResponses(Integer.valueOf(customField.getFieldId()));
        } catch (Exception e) {
            mifosLogger.error(format("Unable to retrieve responses for custom field with ID, %s", customField.getFieldId()), e);
        }
        return customFieldResponses;
    }

    private void saveQuestionGroupInstance(QuestionGroupInstanceDto questionGroupInstanceDto) {
        if (questionGroupInstanceDto != null) {
            try {
                questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto);
            } catch (Exception e) {
                mifosLogger.error(format("Unable to migrate responses from customer with ID, %d for custom fields", questionGroupInstanceDto.getEntityId()), e);
            }
        }
    }

    private QuestionGroupInstanceDto mapToQuestionGroupInstanceForCustomer(Integer questionGroupId, Map<Short, Integer> customFieldQuestionIdMap,
                                                                List<CustomerCustomFieldEntity> customerResponses) {
        QuestionGroupInstanceDto questionGroupInstanceDto = null;
        try {
            questionGroupInstanceDto = questionnaireMigrationMapper.mapForCustomers(questionGroupId, customerResponses, customFieldQuestionIdMap);
        } catch (Exception e) {
            mifosLogger.error(format("Unable to convert responses from customer with ID, %d for custom fields, to Question Group responses", customerResponses.get(0).getCustomerId()), e);
        }
        return questionGroupInstanceDto;
    }

    private Map<Integer, List<CustomerCustomFieldEntity>> getCustomFieldResponsesForCustomer(List<CustomFieldDefinitionEntity> customFields) {
        Map<Integer, List<CustomerCustomFieldEntity>> entityResponsesMap = new HashMap<Integer, List<CustomerCustomFieldEntity>>();
        for (CustomFieldDefinitionEntity customField : customFields) {
            List<CustomerCustomFieldEntity> customFieldResponses = getCustomFieldResponsesForCustomer(customField);
            for (CustomerCustomFieldEntity customFieldResponse : customFieldResponses) {
                addOrUpdateForCustomer(entityResponsesMap, customFieldResponse);
            }
        }
        return entityResponsesMap;
    }

    private List<CustomerCustomFieldEntity> getCustomFieldResponsesForCustomer(CustomFieldDefinitionEntity customField) {
        List<CustomerCustomFieldEntity> customFieldResponses = new ArrayList<CustomerCustomFieldEntity>(0);
        try {
            customFieldResponses = customerDao.getCustomFieldResponses(Integer.valueOf(customField.getFieldId()));
        } catch (Exception e) {
            mifosLogger.error(format("Unable to retrieve responses for custom field with ID, %s", customField.getFieldId()), e);
        }
        return customFieldResponses;
    }

    private void addOrUpdateForCustomer(Map<Integer, List<CustomerCustomFieldEntity>> entityResponsesMap, CustomerCustomFieldEntity customFieldResponse) {
        Integer customerId = customFieldResponse.getCustomerId();
        if (entityResponsesMap.containsKey(customerId)) {
            entityResponsesMap.get(customerId).add(customFieldResponse);
        } else {
            entityResponsesMap.put(customerId, new LinkedList<CustomerCustomFieldEntity>());
            entityResponsesMap.get(customerId).add(customFieldResponse);
        }
    }

    private List<Integer> migrateSurveysForClient() {
        List<Survey> surveys = getSurveys(SurveyType.CLIENT);
        List<Integer> questionGroupIds = new ArrayList<Integer>();
        for (Survey survey : surveys) {
            Integer questionGroupId = migrateSurveyForClient(survey);
            if (questionGroupId != null) questionGroupIds.add(questionGroupId);
        }
        return questionGroupIds;
    }

    private List<Survey> getSurveys(SurveyType surveyType) {
        List<Survey> surveys = new ArrayList<Survey>(0);
        try {
            surveys = surveysPersistence.retrieveSurveysByType(surveyType);
        } catch (PersistenceException e) {
            mifosLogger.error(String.format("Unable to retrieve surveys of type %s", surveyType), e);
        }
        return surveys;
    }

    private Integer migrateSurveyForClient(Survey survey) {
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