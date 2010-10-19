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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mifos.accounts.business.AccountCustomFieldEntity;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.savings.persistence.SavingsDao;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.questionnaire.migration.mappers.QuestionnaireMigrationMapper;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.customers.business.CustomerCustomFieldEntity;
import org.mifos.customers.office.business.OfficeCustomFieldEntity;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelCustomFieldEntity;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.surveys.business.Survey;
import org.mifos.customers.surveys.business.SurveyInstance;
import org.mifos.customers.surveys.helpers.SurveyType;
import org.mifos.customers.surveys.persistence.SurveysPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupInstanceDto;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class QuestionnaireMigration {

    private static final Logger logger = LoggerFactory.getLogger(QuestionnaireMigration.class);

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

    @Autowired
    private SavingsDao savingsDao;

    @Autowired
    private OfficeDao officeDao;

    @Autowired
    private PersonnelDao personnelDao;

    @SuppressWarnings({"UnusedDeclaration"})

    public QuestionnaireMigration() {
    }

    // Intended to be used only from unit tests for injecting mocks
    public QuestionnaireMigration(QuestionnaireMigrationMapper questionnaireMigrationMapper,
                                  QuestionnaireServiceFacade questionnaireServiceFacade,
                                  SurveysPersistence surveysPersistence, CustomerDao customerDao, LoanDao loanDao, SavingsDao savingsDao, OfficeDao officeDao, PersonnelDao personnelDao) {
        this.questionnaireMigrationMapper = questionnaireMigrationMapper;
        this.questionnaireServiceFacade = questionnaireServiceFacade;
        this.surveysPersistence = surveysPersistence;
        this.customerDao = customerDao;
        this.loanDao = loanDao;
        this.personnelDao=personnelDao;
        this.savingsDao=savingsDao;
        this.officeDao=officeDao;
    }

    public List<Integer> migrateAdditionalFields() {
        List<Integer> questionGroupIds = new ArrayList<Integer>();
        Integer questionGroupId = migrateAdditionalFieldsForClient();
        if (questionGroupId != null) {
            questionGroupIds.add(questionGroupId);
        }
        questionGroupId = migrateAdditionalFieldsForGroup();
        if (questionGroupId != null) {
            questionGroupIds.add(questionGroupId);
        }
        questionGroupId = migrateAdditionalFieldsForLoan();
        if (questionGroupId != null) {
            questionGroupIds.add(questionGroupId);
        }
        questionGroupId = migrateAdditionalFieldsForSavings();
        if (questionGroupId != null) {
            questionGroupIds.add(questionGroupId);
        }
        questionGroupId = migrateAdditionalFieldsForCenter();
        if (questionGroupId != null) {
            questionGroupIds.add(questionGroupId);
        }
        questionGroupId = migrateAdditionalFieldsForOffice();
        if (questionGroupId != null) {
            questionGroupIds.add(questionGroupId);
        }
        questionGroupId = migrateAdditionalFieldsForPersonnel();
        if (questionGroupId != null) {
            questionGroupIds.add(questionGroupId);
        }
        return questionGroupIds;
    }

    public List<Integer> migrateSurveys() {
        List<Integer> questionGroupIds = new ArrayList<Integer>();
        for (SurveyType surveyType : SurveyType.values()){
            if(surveyType!=SurveyType.ALL) {
                questionGroupIds.addAll(migrateSurveys(surveyType));
            }
        }
        return questionGroupIds;
    }

    // Made 'public' to aid unit testing
    public Integer migrateAdditionalFieldsForGroup() {
        Iterator<CustomFieldDefinitionEntity> customFields = getCustomFieldsForGroup();
        Integer questionGroupId = null;
        if (customFields != null) {
            Map<Short, Integer> customFieldQuestionIdMap = new HashMap<Short, Integer>();
            questionGroupId = getQuestionGroup(customFields, customFieldQuestionIdMap, EntityType.GROUP);
            customFields = getCustomFieldsForGroup();
            Integer eventSourceId = questionnaireServiceFacade.getEventSourceId("Create", "Group");
            migrateAdditionalFieldsResponsesForCustomer(customFields, questionGroupId, eventSourceId, customFieldQuestionIdMap);
        }
        return questionGroupId;
    }

    // Made 'public' to aid unit testing
    public Integer migrateAdditionalFieldsForClient() {
        Iterator<CustomFieldDefinitionEntity> customFields = getCustomFieldsForClient();
        Integer questionGroupId = null;
        if (customFields != null) {
            Map<Short, Integer> customFieldQuestionIdMap = new HashMap<Short, Integer>();
            questionGroupId = getQuestionGroup(customFields, customFieldQuestionIdMap, EntityType.CLIENT);
            customFields = getCustomFieldsForClient();
            Integer eventSourceId = questionnaireServiceFacade.getEventSourceId("Create", "Client");
            migrateAdditionalFieldsResponsesForCustomer(customFields, questionGroupId, eventSourceId, customFieldQuestionIdMap);
        }
        return questionGroupId;
    }

    // Made 'public' to aid unit testing
    public Integer migrateAdditionalFieldsForLoan() {
        Iterator<CustomFieldDefinitionEntity> customFields = getCustomFieldsForLoan();
        Integer questionGroupId = null;
        if (customFields != null) {
            Map<Short, Integer> customFieldQuestionIdMap = new HashMap<Short, Integer>();
            questionGroupId = getQuestionGroup(customFields, customFieldQuestionIdMap, EntityType.LOAN);
            customFields = getCustomFieldsForLoan();
            migrateAdditionalFieldsResponsesForLoan(customFields, questionGroupId, customFieldQuestionIdMap);
        }
        return questionGroupId;
    }

    // Made 'public' to aid unit testing
    public Integer migrateAdditionalFieldsForPersonnel() {
        Iterator<CustomFieldDefinitionEntity> customFields = getCustomFieldsForPersonnel();
        Integer questionGroupId = null;
        if (customFields != null) {
            Map<Short, Integer> customFieldQuestionIdMap = new HashMap<Short, Integer>();
            questionGroupId = getQuestionGroup(customFields, customFieldQuestionIdMap, EntityType.PERSONNEL);
            customFields = getCustomFieldsForPersonnel();
            migrateAdditionalFieldsResponsesForPersonnel(customFields, questionGroupId, customFieldQuestionIdMap);
        }
        return questionGroupId;
    }

    // Made 'public' to aid unit testing
    public Integer migrateAdditionalFieldsForOffice() {
        Iterator<CustomFieldDefinitionEntity> customFields = getCustomFieldsForOffice();
        Integer questionGroupId = null;
        if (customFields != null) {
            Map<Short, Integer> customFieldQuestionIdMap = new HashMap<Short, Integer>();
            questionGroupId = getQuestionGroup(customFields, customFieldQuestionIdMap, EntityType.OFFICE);
            customFields = getCustomFieldsForOffice();
            migrateAdditionalFieldsResponsesForOffice(customFields, questionGroupId, customFieldQuestionIdMap);
        }
        return questionGroupId;
    }

    // Made 'public' to aid unit testing
    public Integer migrateAdditionalFieldsForCenter() {
        Iterator<CustomFieldDefinitionEntity> customFields = getCustomFieldsForCenter();
        Integer questionGroupId = null;
        if (customFields != null) {
            Map<Short, Integer> customFieldQuestionIdMap = new HashMap<Short, Integer>();
            questionGroupId = getQuestionGroup(customFields, customFieldQuestionIdMap, EntityType.CENTER);
            customFields = getCustomFieldsForCenter();
            Integer eventSourceId = questionnaireServiceFacade.getEventSourceId("Create", "Center");
            migrateAdditionalFieldsResponsesForCustomer(customFields, questionGroupId, eventSourceId, customFieldQuestionIdMap);
        }
        return questionGroupId;
    }

    // Made 'public' to aid unit testing
    public Integer migrateAdditionalFieldsForSavings() {
        Iterator<CustomFieldDefinitionEntity> customFields = getCustomFieldsForSavings();
        Integer questionGroupId = null;
        if (customFields != null) {
            Map<Short, Integer> customFieldQuestionIdMap = new HashMap<Short, Integer>();
            questionGroupId = getQuestionGroup(customFields, customFieldQuestionIdMap, EntityType.SAVINGS);
            customFields = getCustomFieldsForSavings();
            migrateAdditionalFieldsResponsesForSavings(customFields, questionGroupId, customFieldQuestionIdMap);
        }
        return questionGroupId;
    }

    private Integer getQuestionGroup(Iterator<CustomFieldDefinitionEntity> customFields, Map<Short, Integer> customFieldQuestionIdMap, EntityType entityType) {
        Integer questionGroupId = null;
        if (customFields != null) {
            questionGroupId = createQuestionGroup(mapAdditionalFieldsToQuestionGroupDto(customFields, customFieldQuestionIdMap, entityType));
        }
        return questionGroupId;
    }

    private Integer createQuestionGroup(QuestionGroupDto questionGroupDto) {
        Integer questionGroupId = null;
        try {
            if (questionGroupDto != null) {
                questionGroupId = questionnaireServiceFacade.createQuestionGroup(questionGroupDto);
            }
        } catch (Exception e) {
            logger.error("Unable to create a Question Group from custom fields", e);
        }
        return questionGroupId;
    }

    private QuestionGroupDto mapAdditionalFieldsToQuestionGroupDto(Iterator<CustomFieldDefinitionEntity> customFields,
                                                                   Map<Short, Integer> customFieldQuestionIdMap, EntityType entityType) {
        QuestionGroupDto questionGroupDto = null;
        try {
            questionGroupDto = questionnaireMigrationMapper.map(customFields, customFieldQuestionIdMap, entityType);
        } catch (Exception e) {
            logger.error("Unable to migrate custom fields to a Question Group", e);
        }
        return questionGroupDto;
    }

    private Iterator<CustomFieldDefinitionEntity> getCustomFieldsForGroup() {
        Iterator<CustomFieldDefinitionEntity> customFields = null;
        try {
            customFields = customerDao.retrieveCustomFieldEntitiesForGroupIterator();
        } catch (Exception e) {
            logger.error("Unable to retrieve custom fields for Create Group", e);
        }
        return customFields;
    }

    private Iterator<CustomFieldDefinitionEntity> getCustomFieldsForClient() {
        Iterator<CustomFieldDefinitionEntity> customFields = null;
        try {
            customFields = customerDao.retrieveCustomFieldEntitiesForClientIterator();
        } catch (Exception e) {
            logger.error("Unable to retrieve custom fields for Create Client", e);
        }
        return customFields;
    }

    private Iterator<CustomFieldDefinitionEntity> getCustomFieldsForLoan() {
        Iterator<CustomFieldDefinitionEntity> customFields = null;
        try {
            customFields = loanDao.retrieveCustomFieldEntitiesForLoan();
        } catch (Exception e) {
            logger.error("Unable to retrieve custom fields for Create Loan", e);
        }
        return customFields;
    }

    private Iterator<CustomFieldDefinitionEntity> getCustomFieldsForPersonnel() {
        Iterator<CustomFieldDefinitionEntity> customFields = null;
        try {
            customFields = personnelDao.retrieveCustomFieldEntitiesForPersonnel();
        } catch (Exception e) {
            logger.error("Unable to retrieve custom fields for Create Personnel", e);
        }
        return customFields;
    }

    private Iterator<CustomFieldDefinitionEntity> getCustomFieldsForOffice() {
        Iterator<CustomFieldDefinitionEntity> customFields = null;
        try {
            customFields = officeDao.retrieveCustomFieldEntitiesForOffice();
        } catch (Exception e) {
            logger.error("Unable to retrieve custom fields for Create Office", e);
        }
        return customFields;
    }

    private Iterator<CustomFieldDefinitionEntity> getCustomFieldsForCenter() {
        Iterator<CustomFieldDefinitionEntity> customFields = null;
        try {
            customFields = customerDao.retrieveCustomFieldEntitiesForCenterIterator();
        } catch (Exception e) {
            logger.error("Unable to retrieve custom fields for Create Center", e);
        }
        return customFields;
    }

    private Iterator<CustomFieldDefinitionEntity> getCustomFieldsForSavings() {
        Iterator<CustomFieldDefinitionEntity> customFields = null;
        try {
            customFields = savingsDao.retrieveCustomFieldEntitiesForSavings();
        } catch (Exception e) {
            logger.error("Unable to retrieve custom fields for Create Savings", e);
        }
        return customFields;
    }

    private void migrateAdditionalFieldsResponsesForCustomer(Iterator<CustomFieldDefinitionEntity> customFields, Integer questionGroupId,
                                                             Integer eventSourceId, Map<Short, Integer> customFieldQuestionIdMap) {
        if (questionGroupId != null) {
            Map<Integer, List<CustomerCustomFieldEntity>> customFieldResponses = getCustomFieldResponsesForCustomer(customFields);
            for (Integer entityId : customFieldResponses.keySet()) {
                List<CustomerCustomFieldEntity> customerResponses = customFieldResponses.get(entityId);
                QuestionGroupInstanceDto questionGroupInstanceDto = mapToQuestionGroupInstanceForCustomer(questionGroupId, eventSourceId, customFieldQuestionIdMap, customerResponses);
                saveQuestionGroupInstance(questionGroupInstanceDto);
            }
        }
    }

    private void migrateAdditionalFieldsResponsesForLoan(Iterator<CustomFieldDefinitionEntity> customFields, Integer questionGroupId,
                                                             Map<Short, Integer> customFieldQuestionIdMap) {
        if (questionGroupId != null) {
            Map<Integer, List<AccountCustomFieldEntity>> customFieldResponses = getCustomFieldResponsesForLoan(customFields);
            for (Integer entityId : customFieldResponses.keySet()) {
                List<AccountCustomFieldEntity> accountResponses = customFieldResponses.get(entityId);
                Integer eventSourceId = questionnaireServiceFacade.getEventSourceId("Create", "Loan");
                QuestionGroupInstanceDto questionGroupInstanceDto = mapToQuestionGroupInstanceForAccount(questionGroupId, eventSourceId, customFieldQuestionIdMap, accountResponses);
                saveQuestionGroupInstance(questionGroupInstanceDto);
            }
        }
    }

    private void migrateAdditionalFieldsResponsesForPersonnel(Iterator<CustomFieldDefinitionEntity> customFields, Integer questionGroupId,
            Map<Short, Integer> customFieldQuestionIdMap) {
        if (questionGroupId != null) {
            Map<Integer, List<PersonnelCustomFieldEntity>> customFieldResponses = getCustomFieldResponsesForPersonnel(customFields);
            for (Integer entityId : customFieldResponses.keySet()) {
                List<PersonnelCustomFieldEntity> accountResponses = customFieldResponses.get(entityId);
                Integer eventSourceId = questionnaireServiceFacade.getEventSourceId("Create", "Personnel");
                QuestionGroupInstanceDto questionGroupInstanceDto = mapToQuestionGroupInstanceForPersonnel(questionGroupId, eventSourceId, customFieldQuestionIdMap, accountResponses);
                saveQuestionGroupInstance(questionGroupInstanceDto);
            }
        }
    }

    private void migrateAdditionalFieldsResponsesForOffice(Iterator<CustomFieldDefinitionEntity> customFields, Integer questionGroupId,
            Map<Short, Integer> customFieldQuestionIdMap) {
        if (questionGroupId != null) {
            Map<Integer, List<OfficeCustomFieldEntity>> customFieldResponses = getCustomFieldResponsesForOffice(customFields);
            for (Integer entityId : customFieldResponses.keySet()) {
                List<OfficeCustomFieldEntity> accountResponses = customFieldResponses.get(entityId);
                Integer eventSourceId = questionnaireServiceFacade.getEventSourceId("Create", "Office");
                QuestionGroupInstanceDto questionGroupInstanceDto = mapToQuestionGroupInstanceForOffice(questionGroupId, eventSourceId, customFieldQuestionIdMap, accountResponses);
                saveQuestionGroupInstance(questionGroupInstanceDto);
            }
        }
    }

    private void migrateAdditionalFieldsResponsesForSavings(Iterator<CustomFieldDefinitionEntity> customFields,
            Integer questionGroupId, Map<Short, Integer> customFieldQuestionIdMap) {
        if (questionGroupId != null) {
            Map<Integer, List<AccountCustomFieldEntity>> customFieldResponses = getCustomFieldResponsesForSavings(customFields);
            for (Integer entityId : customFieldResponses.keySet()) {
                List<AccountCustomFieldEntity> accountResponses = customFieldResponses.get(entityId);
                Integer eventSourceId = questionnaireServiceFacade.getEventSourceId("Create", "Savings");
                QuestionGroupInstanceDto questionGroupInstanceDto = mapToQuestionGroupInstanceForAccount(questionGroupId,
                        eventSourceId, customFieldQuestionIdMap, accountResponses);
                saveQuestionGroupInstance(questionGroupInstanceDto);
            }
        }
    }

    private Map<Integer, List<AccountCustomFieldEntity>> getCustomFieldResponsesForSavings(Iterator<CustomFieldDefinitionEntity> customFields) {
        Map<Integer, List<AccountCustomFieldEntity>> entityResponsesMap = new HashMap<Integer, List<AccountCustomFieldEntity>>();
        if (customFields != null) {
            while (customFields.hasNext()) {
                Iterator<AccountCustomFieldEntity> customFieldResponses = getCustomFieldResponsesForSavings(customFields.next());
                if (customFieldResponses != null) {
                    while (customFieldResponses.hasNext()) {
                        addOrUpdateForAccount(entityResponsesMap, customFieldResponses.next());
                    }
                }
            }
        }
        return entityResponsesMap;
    }

    private Iterator<AccountCustomFieldEntity> getCustomFieldResponsesForSavings(CustomFieldDefinitionEntity customField) {
        Iterator<AccountCustomFieldEntity> customFieldResponses = null;
        try {
            customFieldResponses = savingsDao.getCustomFieldResponses(customField.getFieldId());
        } catch (Exception e) {
            logger.error(format("Unable to retrieve responses for custom field with ID, %s", customField.getFieldId()), e);
        }
        return customFieldResponses;
    }

    private Map<Integer, List<OfficeCustomFieldEntity>> getCustomFieldResponsesForOffice(Iterator<CustomFieldDefinitionEntity> customFields) {
        Map<Integer, List<OfficeCustomFieldEntity>> entityResponsesMap = new HashMap<Integer, List<OfficeCustomFieldEntity>>();
        if (customFields != null) {
            while (customFields.hasNext()) {
                Iterator<OfficeCustomFieldEntity> customFieldResponses = getCustomFieldResponsesForOffice(customFields.next());
                if (customFieldResponses != null) {
                    while (customFieldResponses.hasNext()) {
                        addOrUpdateForOffice(entityResponsesMap, customFieldResponses.next());
                    }
                }
            }
        }
        return entityResponsesMap;
    }

    private Iterator<OfficeCustomFieldEntity> getCustomFieldResponsesForOffice(CustomFieldDefinitionEntity customField) {
        Iterator<OfficeCustomFieldEntity> customFieldResponses = null;
        try {
            customFieldResponses = officeDao.getCustomFieldResponses(customField.getFieldId());
        } catch (Exception e) {
            logger.error(format("Unable to retrieve responses for custom field with ID, %s", customField.getFieldId()), e);
        }
        return customFieldResponses;
    }

    private void addOrUpdateForOffice(Map<Integer, List<OfficeCustomFieldEntity>> entityResponsesMap, OfficeCustomFieldEntity customFieldResponse) {
        Integer officeId = customFieldResponse.getOffice().getOfficeId().intValue();
        if (entityResponsesMap.containsKey(officeId)) {
            entityResponsesMap.get(officeId).add(customFieldResponse);
        } else {
            entityResponsesMap.put(officeId, new LinkedList<OfficeCustomFieldEntity>());
            entityResponsesMap.get(officeId).add(customFieldResponse);
        }
    }

    private QuestionGroupInstanceDto mapToQuestionGroupInstanceForOffice(Integer questionGroupId,
                                                                         Integer eventSourceId, Map<Short, Integer> customFieldQuestionIdMap, List<OfficeCustomFieldEntity> officeResponses) {
        QuestionGroupInstanceDto questionGroupInstanceDto = null;
        try {
            questionGroupInstanceDto = questionnaireMigrationMapper.mapForOffice(questionGroupId, eventSourceId, officeResponses,
                    customFieldQuestionIdMap);
        } catch (Exception e) {
            logger
                    .error(format(
                            "Unable to convert responses given for account with ID, %d for custom fields, to Question Group responses",
                            officeResponses.get(0).getOffice().getOfficeId().intValue()), e);
        }
        return questionGroupInstanceDto;
    }

    private Map<Integer, List<PersonnelCustomFieldEntity>> getCustomFieldResponsesForPersonnel(Iterator<CustomFieldDefinitionEntity> customFields) {
        Map<Integer, List<PersonnelCustomFieldEntity>> entityResponsesMap = new HashMap<Integer, List<PersonnelCustomFieldEntity>>();
        if (customFields != null) {
            while (customFields.hasNext()) {
                Iterator<PersonnelCustomFieldEntity> customFieldResponses = getCustomFieldResponsesForPersonnel(customFields.next());
                if (customFieldResponses != null) {
                    while (customFieldResponses.hasNext()) {
                        addOrUpdateForPersonnel(entityResponsesMap, customFieldResponses.next());
                    }
                }
            }
        }
        return entityResponsesMap;
    }

    private Iterator<PersonnelCustomFieldEntity> getCustomFieldResponsesForPersonnel(CustomFieldDefinitionEntity customField) {
        Iterator<PersonnelCustomFieldEntity> customFieldResponses = null;
        try {
            customFieldResponses = personnelDao.getCustomFieldResponses(customField.getFieldId());
        } catch (Exception e) {
            logger.error(format("Unable to retrieve responses for custom field with ID, %s", customField.getFieldId()), e);
        }
        return customFieldResponses;
    }

    private void addOrUpdateForPersonnel(Map<Integer, List<PersonnelCustomFieldEntity>> entityResponsesMap, PersonnelCustomFieldEntity customFieldResponse) {
        Integer personnelId = customFieldResponse.getPersonnel().getPersonnelId().intValue();
        if (entityResponsesMap.containsKey(personnelId)) {
            entityResponsesMap.get(personnelId).add(customFieldResponse);
        } else {
            entityResponsesMap.put(personnelId, new LinkedList<PersonnelCustomFieldEntity>());
            entityResponsesMap.get(personnelId).add(customFieldResponse);
        }
    }

    private QuestionGroupInstanceDto mapToQuestionGroupInstanceForPersonnel(Integer questionGroupId,
                                                                            Integer eventSourceId, Map<Short, Integer> customFieldQuestionIdMap, List<PersonnelCustomFieldEntity> personnelResponses) {
        QuestionGroupInstanceDto questionGroupInstanceDto = null;
        try {
            questionGroupInstanceDto = questionnaireMigrationMapper.mapForPersonnel(questionGroupId, eventSourceId, personnelResponses,
                    customFieldQuestionIdMap);
        } catch (Exception e) {
            logger
                    .error(format(
                            "Unable to convert responses given for account with ID, %d for custom fields, to Question Group responses",
                            personnelResponses.get(0).getPersonnel().getPersonnelId().intValue()), e);
        }
        return questionGroupInstanceDto;
    }

    private QuestionGroupInstanceDto mapToQuestionGroupInstanceForAccount(Integer questionGroupId, Integer eventSourceId, Map<Short, Integer> customFieldQuestionIdMap,
                                                                          List<AccountCustomFieldEntity> accountResponses) {
        QuestionGroupInstanceDto questionGroupInstanceDto = null;
        try {
            questionGroupInstanceDto = questionnaireMigrationMapper.mapForAccounts(questionGroupId, eventSourceId, accountResponses, customFieldQuestionIdMap);
        } catch (Exception e) {
            logger.error(format("Unable to convert responses given for account with ID, %d for custom fields, to Question Group responses", accountResponses.get(0).getAccountId()), e);
        }
        return questionGroupInstanceDto;
    }

    private Map<Integer, List<AccountCustomFieldEntity>> getCustomFieldResponsesForLoan(Iterator<CustomFieldDefinitionEntity> customFields) {
        Map<Integer, List<AccountCustomFieldEntity>> entityResponsesMap = new HashMap<Integer, List<AccountCustomFieldEntity>>();
        if (customFields != null) {
            while (customFields.hasNext()) {
                Iterator<AccountCustomFieldEntity> customFieldResponses = getCustomFieldResponsesForLoan(customFields.next());
                if (customFieldResponses != null) {
                    while (customFieldResponses.hasNext()) {
                        addOrUpdateForAccount(entityResponsesMap, customFieldResponses.next());
                    }
                }
            }
        }
        return entityResponsesMap;
    }

    private void addOrUpdateForAccount(Map<Integer, List<AccountCustomFieldEntity>> entityResponsesMap, AccountCustomFieldEntity customFieldResponse) {
        Integer accountId = customFieldResponse.getAccountId();
        if (entityResponsesMap.containsKey(accountId)) {
            entityResponsesMap.get(accountId).add(customFieldResponse);
        } else {
            entityResponsesMap.put(accountId, new LinkedList<AccountCustomFieldEntity>());
            entityResponsesMap.get(accountId).add(customFieldResponse);
        }
    }

    private Iterator<AccountCustomFieldEntity> getCustomFieldResponsesForLoan(CustomFieldDefinitionEntity customField) {
        Iterator<AccountCustomFieldEntity> customFieldResponses = null;
        try {
            customFieldResponses = loanDao.getCustomFieldResponses(customField.getFieldId());
        } catch (Exception e) {
            logger.error(format("Unable to retrieve responses for custom field with ID, %s", customField.getFieldId()), e);
        }
        return customFieldResponses;
    }

    private void saveQuestionGroupInstance(QuestionGroupInstanceDto questionGroupInstanceDto) {
        if (questionGroupInstanceDto != null) {
            try {
                questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto);
            } catch (Exception e) {
                logger.error(format("Unable to migrate responses from customer with ID, %d for custom fields", questionGroupInstanceDto.getEntityId()), e);
            }
        }
    }

    private QuestionGroupInstanceDto mapToQuestionGroupInstanceForCustomer(Integer questionGroupId, Integer eventSourceId, Map<Short, Integer> customFieldQuestionIdMap,
                                                                           List<CustomerCustomFieldEntity> customerResponses) {
        QuestionGroupInstanceDto questionGroupInstanceDto = null;
        try {
            questionGroupInstanceDto = questionnaireMigrationMapper.mapForCustomers(questionGroupId, eventSourceId, customerResponses, customFieldQuestionIdMap);
        } catch (Exception e) {
            logger.error(format("Unable to convert responses from customer with ID, %d for custom fields, to Question Group responses", customerResponses.get(0).getCustomerId()), e);
        }
        return questionGroupInstanceDto;
    }

    private Map<Integer, List<CustomerCustomFieldEntity>> getCustomFieldResponsesForCustomer(Iterator<CustomFieldDefinitionEntity> customFields) {
        Map<Integer, List<CustomerCustomFieldEntity>> entityResponsesMap = new HashMap<Integer, List<CustomerCustomFieldEntity>>();
        if (customFields != null) {
            while (customFields.hasNext()) {
                Iterator<CustomerCustomFieldEntity> customFieldResponses = getCustomFieldResponsesForCustomer(customFields.next());
                if (customFieldResponses != null) {
                    while (customFieldResponses.hasNext()) {
                        addOrUpdateForCustomer(entityResponsesMap, customFieldResponses.next());
                    }
                }
            }
        }
        return entityResponsesMap;
    }

    private Iterator<CustomerCustomFieldEntity> getCustomFieldResponsesForCustomer(CustomFieldDefinitionEntity customField) {
        Iterator<CustomerCustomFieldEntity> customFieldResponses = null;
        try {
            customFieldResponses = customerDao.getCustomFieldResponses(customField.getFieldId());
        } catch (Exception e) {
            logger.error(format("Unable to retrieve responses for custom field with ID, %s", customField.getFieldId()), e);
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

    private List<Integer> migrateSurveys(SurveyType surveyType) {
        List<Integer> questionGroupIds = new ArrayList<Integer>();
        Iterator<Survey> surveyIterator = getSurveys(surveyType);
        if (surveyIterator != null) {
            while (surveyIterator.hasNext()) {
                Survey sur = surveyIterator.next();
                if(sur.getAppliesToAsEnum()!=surveyType) {
                    sur.setAppliesTo(surveyType);
                }
                Integer questionGroupId = migrateSurvey(sur);
                if (questionGroupId != null) {
                    questionGroupIds.add(questionGroupId);
                }
            }
        }
        return questionGroupIds;
    }

    private Iterator<Survey> getSurveys(SurveyType surveyType) {
        Iterator<Survey> surveys = null;
        try {
            surveys = surveysPersistence.retrieveSurveysByTypeIterator(surveyType);
        } catch (PersistenceException e) {
            logger.error(String.format("Unable to retrieve surveys of type %s", surveyType), e);
        }
        return surveys;
    }

    private Integer migrateSurvey(Survey survey) {
        QuestionGroupDto questionGroupDto = questionnaireMigrationMapper.map(survey);
        Integer questionGroupId = createQuestionGroup(questionGroupDto, survey);
        EventSourceDto eventSourceDto = questionGroupDto.getEventSourceDtos().get(0);
        Integer eventSourceId = questionnaireServiceFacade.getEventSourceId(eventSourceDto.getEvent(), eventSourceDto.getSource());
        migrateSurveyResponses(survey, questionGroupId, eventSourceId);
        return questionGroupId;
    }

    private Integer createQuestionGroup(QuestionGroupDto questionGroupDto, Survey survey) {
        Integer questionGroupId = null;
        try {
            questionGroupId = questionnaireServiceFacade.createQuestionGroup(questionGroupDto);
        } catch (Exception e) {
            logger.error(format("Unable to convert the survey, '%s' with ID, %s to a Question Group", survey.getName(), survey.getSurveyId()), e);
        }
        return questionGroupId;
    }

    private void migrateSurveyResponses(Survey survey, Integer questionGroupId, Integer eventSourceId) {
        if (questionGroupId != null) {
            Iterator<SurveyInstance> surveyInstanceIterator = getSurveyInstances(survey);
            if (surveyInstanceIterator != null) {
                while (surveyInstanceIterator.hasNext()) {
                    SurveyInstance surveyInstance = surveyInstanceIterator.next();
                    QuestionGroupInstanceDto questionGroupInstanceDto = questionnaireMigrationMapper.map(surveyInstance, questionGroupId, eventSourceId);
                    saveQuestionGroupInstance(questionGroupInstanceDto, surveyInstance);
                }
            }
        }
    }

    private void saveQuestionGroupInstance(QuestionGroupInstanceDto questionGroupInstanceDto, SurveyInstance surveyInstance) {
        try {
            questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto);
        } catch (Exception e) {
            Survey survey = surveyInstance.getSurvey();
            logger.error(format("Unable to migrate a survey instance with ID, %s for the survey, '%s' with ID, %s", surveyInstance.getInstanceId(), survey.getName(), survey.getSurveyId()), e);
        }
    }

    private Iterator<SurveyInstance> getSurveyInstances(Survey survey) {
        Iterator<SurveyInstance> surveyInstances = null;
        try {
            surveyInstances = surveysPersistence.retrieveInstancesBySurveyIterator(survey);
        } catch (Exception e) {
            logger.error(format("Unable to retrieve survey instances for survey, '%s' with ID, %s", survey.getName(), survey.getSurveyId()), e);
        }
        return surveyInstances;
    }
}