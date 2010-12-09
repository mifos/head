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

import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.savings.persistence.SavingsDao;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.questionnaire.migration.mappers.QuestionnaireMigrationMapper;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.surveys.business.Survey;
import org.mifos.customers.surveys.business.SurveyInstance;
import org.mifos.customers.surveys.helpers.SurveyType;
import org.mifos.customers.surveys.persistence.SurveysPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupInstanceDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.hibernate.SQLQuery;
import org.hibernate.HibernateException;

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

    public void migrateAdditionalFields() {
        System.out.println("migrating Additional Fields for Client");
        migrateAdditionalFieldsForClient();
        System.out.println("migrating Additional Fields for Group");
        migrateAdditionalFieldsForGroup();
        System.out.println("migrating Additional Fields for Center");
        migrateAdditionalFieldsForCenter();
        System.out.println("migrating Additional Fields for Loan");
        migrateAdditionalFieldsForLoan();
        System.out.println("migrating Additional Fields for Savings");
        migrateAdditionalFieldsForSavings();
        System.out.println("migrating Additional Fields for Office");
        migrateAdditionalFieldsForOffice();
        System.out.println("migrating Additional Fields for Personnel");
        migrateAdditionalFieldsForPersonnel();

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
            Integer eventSourceId = getEventSourceId("Create", "Group");
            if (migrateAdditionalFieldsResponsesForCustomer(customFields, questionGroupId, eventSourceId, customFieldQuestionIdMap)) {
                removeCustomFields(getCustomFieldsForGroup());
            }
        }
        return questionGroupId;
    }

    private void removeCustomFields(Iterator<CustomFieldDefinitionEntity> customFields) {
        boolean result = false;
        if (customFields != null) {
            result = true;
            while (customFields.hasNext()) {
                CustomFieldDefinitionEntity customField = customFields.next();
                try {
                    surveysPersistence.delete(customField);
                } catch (PersistenceException e) {
                    logger.error(format("Unable to remove custom field with ID %d", customField.getFieldId()), e);
                    result = false;
                    surveysPersistence.rollbackTransaction();
                    break;
                }
            }
        }
        if (result) {
            surveysPersistence.commitTransaction();
        }
    }

    // Made 'public' to aid unit testing
    public Integer migrateAdditionalFieldsForClient() {
        Iterator<CustomFieldDefinitionEntity> customFields = getCustomFieldsForClient();
        Integer questionGroupId = null;
        if (customFields != null) {
            Map<Short, Integer> customFieldQuestionIdMap = new HashMap<Short, Integer>();
            questionGroupId = getQuestionGroup(customFields, customFieldQuestionIdMap, EntityType.CLIENT);
            customFields = getCustomFieldsForClient();
            Integer eventSourceId = getEventSourceId("Create", "Client");
            if (migrateAdditionalFieldsResponsesForCustomer(customFields, questionGroupId, eventSourceId, customFieldQuestionIdMap)) {
                removeCustomFields(getCustomFieldsForClient());
            }
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
            if (migrateAdditionalFieldsResponsesForLoan(customFields, questionGroupId, customFieldQuestionIdMap)) {
                removeCustomFields(getCustomFieldsForLoan());
            }
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
            if (migrateAdditionalFieldsResponsesForPersonnel(customFields, questionGroupId, customFieldQuestionIdMap)) {
                removeCustomFields(getCustomFieldsForPersonnel());
            }
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
            if (migrateAdditionalFieldsResponsesForOffice(customFields, questionGroupId, customFieldQuestionIdMap)) {
                removeCustomFields(getCustomFieldsForOffice());
            }
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
            Integer eventSourceId = getEventSourceId("Create", "Center");
            if (migrateAdditionalFieldsResponsesForCustomer(customFields, questionGroupId, eventSourceId, customFieldQuestionIdMap)) {
                removeCustomFields(getCustomFieldsForCenter());
            }
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
            if (migrateAdditionalFieldsResponsesForSavings(customFields, questionGroupId, customFieldQuestionIdMap)) {
                removeCustomFields(getCustomFieldsForSavings());
            }
        }
        return questionGroupId;
    }

    private Integer getQuestionGroup(Iterator<CustomFieldDefinitionEntity> customFields, Map<Short, Integer> customFieldQuestionIdMap, EntityType entityType) {
        Integer questionGroupId = null;
        if (customFields != null) {
            QuestionGroupDto mapped = mapAdditionalFieldsToQuestionGroupDto(customFields, customFieldQuestionIdMap, entityType);
            if (mapped != null) {
                questionGroupId = createQuestionGroup(mapped);
            }
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

    private boolean migrateAdditionalFieldsResponsesForCustomer(Iterator<CustomFieldDefinitionEntity> customFields, Integer questionGroupId,
                                                             Integer eventSourceId, Map<Short, Integer> customFieldQuestionIdMap) {
        boolean result = false;
        if (questionGroupId != null && eventSourceId != null) {
            result = true;
            Map<Integer, List<CustomFieldForMigrationDto>> customFieldResponses = getCustomFieldResponsesForCustomer(customFields);
            for (Integer entityId : customFieldResponses.keySet()) {
                List<CustomFieldForMigrationDto> customerResponses = customFieldResponses.get(entityId);
                QuestionGroupInstanceDto questionGroupInstanceDto = mapToQuestionGroupInstance("customer", questionGroupId, eventSourceId, customFieldQuestionIdMap, customerResponses);
                if (saveQuestionGroupInstance(questionGroupInstanceDto)) {
                    SQLQuery update = StaticHibernateUtil.getSessionTL().createSQLQuery("delete from customer_custom_field where customer_customfield_id = :customFieldId");
                    StaticHibernateUtil.startTransaction();
                    for (CustomFieldForMigrationDto response : customerResponses) {
                        try {
                            update.setInteger("customFieldId", response.getCustomFieldId());
                            update.executeUpdate();
                        } catch (HibernateException e) {
                            logger.error(format("Unable to remove responses given for account with ID %d for custom fields", response.getEntityId()), e);
                            result = false;
                            StaticHibernateUtil.rollbackTransaction();
                            break;
                        }
                    }
                }
                else {
                    result = false;
                }
            }
        }
        return result;
    }

    private boolean migrateAdditionalFieldsResponsesForLoan(Iterator<CustomFieldDefinitionEntity> customFields, Integer questionGroupId,
                                                             Map<Short, Integer> customFieldQuestionIdMap) {
        boolean result = false;
        if (questionGroupId != null) {
            result = true;
            Map<Integer, List<CustomFieldForMigrationDto>> customFieldResponses = getCustomFieldResponsesForLoan(customFields);
            for (Integer entityId : customFieldResponses.keySet()) {
                List<CustomFieldForMigrationDto> accountResponses = customFieldResponses.get(entityId);
                Integer eventSourceId = getEventSourceId("Create", "Loan");
                QuestionGroupInstanceDto questionGroupInstanceDto = mapToQuestionGroupInstance("loan", questionGroupId, eventSourceId, customFieldQuestionIdMap, accountResponses);
                if (saveQuestionGroupInstance(questionGroupInstanceDto)) {
                    SQLQuery update = StaticHibernateUtil.getSessionTL().createSQLQuery("delete from account_custom_field where account_custom_field_id = :customFieldId");
                    StaticHibernateUtil.startTransaction();
                    for (CustomFieldForMigrationDto response : accountResponses) {
                        try {
                            update.setInteger("customFieldId", response.getCustomFieldId());
                            update.executeUpdate();
                        } catch (HibernateException e) {
                            logger.error(format("Unable to remove responses given for loan with ID %d for custom fields", response.getEntityId()), e);
                            result = false;
                            StaticHibernateUtil.rollbackTransaction();
                            break;
                        }
                    }
                }
                else {
                    result = false;
                }
            }
        }
        return result;
    }

    private boolean migrateAdditionalFieldsResponsesForPersonnel(Iterator<CustomFieldDefinitionEntity> customFields, Integer questionGroupId,
            Map<Short, Integer> customFieldQuestionIdMap) {
        boolean result = false;
        if (questionGroupId != null) {
            result = true;
            Map<Integer, List<CustomFieldForMigrationDto>> customFieldResponses = getCustomFieldResponsesForPersonnel(customFields);
            for (Integer entityId : customFieldResponses.keySet()) {
                List<CustomFieldForMigrationDto> accountResponses = customFieldResponses.get(entityId);
                Integer eventSourceId = getEventSourceId("Create", "Personnel");
                QuestionGroupInstanceDto questionGroupInstanceDto = mapToQuestionGroupInstance("personnel", questionGroupId, eventSourceId, customFieldQuestionIdMap, accountResponses);
                if (saveQuestionGroupInstance(questionGroupInstanceDto)) {
                    SQLQuery update = StaticHibernateUtil.getSessionTL().createSQLQuery("delete from personnel_custom_field where personnel_custom_field_id = :customFieldId");
                    StaticHibernateUtil.startTransaction();
                    for (CustomFieldForMigrationDto response : accountResponses) {
                        try {
                            update.setInteger("customFieldId", response.getCustomFieldId());
                            update.executeUpdate();
                        } catch (HibernateException e) {
                            logger.error(format("Unable to remove responses given for personnel with ID %d for custom fields", response.getEntityId()), e);
                            result = false;
                            StaticHibernateUtil.rollbackTransaction();
                            break;
                        }
                    }
                }
                else {
                    result = false;
                }
            }
        }
        return result;
    }

    private boolean migrateAdditionalFieldsResponsesForOffice(Iterator<CustomFieldDefinitionEntity> customFields, Integer questionGroupId,
            Map<Short, Integer> customFieldQuestionIdMap) {
        boolean result = false;
        if (questionGroupId != null) {
            result = true;
            Map<Integer, List<CustomFieldForMigrationDto>> customFieldResponses = getCustomFieldResponsesForOffice(customFields);
            for (Integer entityId : customFieldResponses.keySet()) {
                List<CustomFieldForMigrationDto> accountResponses = customFieldResponses.get(entityId);
                Integer eventSourceId = getEventSourceId("Create", "Office");
                QuestionGroupInstanceDto questionGroupInstanceDto = mapToQuestionGroupInstance("office", questionGroupId, eventSourceId, customFieldQuestionIdMap, accountResponses);
                if (saveQuestionGroupInstance(questionGroupInstanceDto)) {
                    SQLQuery update = StaticHibernateUtil.getSessionTL().createSQLQuery("delete from office_custom_field where office_custom_field_id = :customFieldId");
                    StaticHibernateUtil.startTransaction();
                    for (CustomFieldForMigrationDto response : accountResponses) {
                        try {
                            update.setInteger("customFieldId", response.getCustomFieldId());
                            update.executeUpdate();
                        } catch (HibernateException e) {
                            logger.error(format("Unable to remove responses given for office with ID %d for custom fields", response.getEntityId()), e);
                            result = false;
                            surveysPersistence.rollbackTransaction();
                            break;
                        }
                    }
                }
                else {
                    result = false;
                }
            }
        }
        return result;
    }

    private boolean migrateAdditionalFieldsResponsesForSavings(Iterator<CustomFieldDefinitionEntity> customFields,
            Integer questionGroupId, Map<Short, Integer> customFieldQuestionIdMap) {
        boolean result = false;
        if (questionGroupId != null) {
            result = true;
            Map<Integer, List<CustomFieldForMigrationDto>> customFieldResponses = getCustomFieldResponsesForSavings(customFields);
            for (Integer entityId : customFieldResponses.keySet()) {
                List<CustomFieldForMigrationDto> accountResponses = customFieldResponses.get(entityId);
                Integer eventSourceId = getEventSourceId("Create", "Savings");
                QuestionGroupInstanceDto questionGroupInstanceDto = mapToQuestionGroupInstance("savings", questionGroupId,
                        eventSourceId, customFieldQuestionIdMap, accountResponses);
                if (saveQuestionGroupInstance(questionGroupInstanceDto)) {
                    SQLQuery update = StaticHibernateUtil.getSessionTL().createSQLQuery("delete from account_custom_field where account_custom_field_id = :customFieldId");
                    StaticHibernateUtil.startTransaction();
                    for (CustomFieldForMigrationDto response : accountResponses) {
                        try {
                            update.setInteger("customFieldId", response.getCustomFieldId());
                            update.executeUpdate();
                        } catch (HibernateException e) {
                            logger.error(format("Unable to remove responses given for savings with ID %d for custom fields", response.getEntityId()), e);
                            result = false;
                            StaticHibernateUtil.rollbackTransaction();
                            break;
                        }
                    }
                }
                else {
                    result = false;
                }
            }
        }
        return result;
    }

    private Map<Integer, List<CustomFieldForMigrationDto>> getCustomFieldResponsesForSavings(Iterator<CustomFieldDefinitionEntity> customFields) {
        Map<Integer, List<CustomFieldForMigrationDto>> entityResponsesMap = new HashMap<Integer, List<CustomFieldForMigrationDto>>();
        if (customFields != null) {
            while (customFields.hasNext()) {
                List<CustomFieldForMigrationDto> customFieldResponses = getCustomFieldResponsesForSavings(customFields.next());
                if (customFieldResponses != null) {
                    for (CustomFieldForMigrationDto customFieldDto : customFieldResponses) {
                        addOrUpdate(entityResponsesMap, customFieldDto);
                    }
                }
            }
        }
        return entityResponsesMap;
    }

    private List<CustomFieldForMigrationDto> getCustomFieldResponsesForSavings(CustomFieldDefinitionEntity customField) {
        List<CustomFieldForMigrationDto> customFieldResponses = null;
        try {
            customFieldResponses = savingsDao.getCustomFieldResponses(customField.getFieldId());
        } catch (Exception e) {
            logger.error(format("Unable to retrieve responses for custom field with ID, %s", customField.getFieldId()), e);
        }
        return customFieldResponses;
    }

    private Map<Integer, List<CustomFieldForMigrationDto>> getCustomFieldResponsesForOffice(Iterator<CustomFieldDefinitionEntity> customFields) {
        Map<Integer, List<CustomFieldForMigrationDto>> entityResponsesMap = new HashMap<Integer, List<CustomFieldForMigrationDto>>();
        if (customFields != null) {
            while (customFields.hasNext()) {
                List<CustomFieldForMigrationDto> customFieldResponses = getCustomFieldResponsesForOffice(customFields.next());
                if (customFieldResponses != null) {
                    for (CustomFieldForMigrationDto customFieldDto : customFieldResponses) {
                        addOrUpdate(entityResponsesMap, customFieldDto);
                    }
                }
            }
        }
        return entityResponsesMap;
    }

    private List<CustomFieldForMigrationDto> getCustomFieldResponsesForOffice(CustomFieldDefinitionEntity customField) {
        List<CustomFieldForMigrationDto> customFieldResponses = null;
        try {
            customFieldResponses = officeDao.getCustomFieldResponses(customField.getFieldId());
        } catch (Exception e) {
            logger.error(format("Unable to retrieve responses for custom field with ID, %s", customField.getFieldId()), e);
        }
        return customFieldResponses;
    }

    private Map<Integer, List<CustomFieldForMigrationDto>> getCustomFieldResponsesForPersonnel(Iterator<CustomFieldDefinitionEntity> customFields) {
        Map<Integer, List<CustomFieldForMigrationDto>> entityResponsesMap = new HashMap<Integer, List<CustomFieldForMigrationDto>>();
        if (customFields != null) {
            while (customFields.hasNext()) {
                List<CustomFieldForMigrationDto> customFieldResponses = getCustomFieldResponsesForPersonnel(customFields.next());
                if (customFieldResponses != null) {
                    for (CustomFieldForMigrationDto customFieldDto : customFieldResponses) {
                        addOrUpdate(entityResponsesMap, customFieldDto);
                    }
                }
            }
        }
        return entityResponsesMap;
    }

    private List<CustomFieldForMigrationDto> getCustomFieldResponsesForPersonnel(CustomFieldDefinitionEntity customField) {
        List<CustomFieldForMigrationDto> customFieldResponses = null;
        try {
            customFieldResponses = personnelDao.getCustomFieldResponses(customField.getFieldId());
        } catch (Exception e) {
            logger.error(format("Unable to retrieve responses for custom field with ID, %s", customField.getFieldId()), e);
        }
        return customFieldResponses;
    }

    private QuestionGroupInstanceDto mapToQuestionGroupInstance(String type, Integer questionGroupId,
                                                                            Integer eventSourceId, Map<Short, Integer> customFieldQuestionIdMap, List<CustomFieldForMigrationDto> responses) {
        QuestionGroupInstanceDto questionGroupInstanceDto = null;
        try {
            questionGroupInstanceDto = questionnaireMigrationMapper.map(questionGroupId, eventSourceId, responses, customFieldQuestionIdMap);
        } catch (Exception e) {
            logger.error(format("Unable to convert responses given for %s with ID, %d for custom fields, to Question Group responses", type, responses.get(0).getEntityId()), e);
        }
        return questionGroupInstanceDto;
    }

    private Map<Integer, List<CustomFieldForMigrationDto>> getCustomFieldResponsesForLoan(Iterator<CustomFieldDefinitionEntity> customFields) {
        Map<Integer, List<CustomFieldForMigrationDto>> entityResponsesMap = new HashMap<Integer, List<CustomFieldForMigrationDto>>();
        if (customFields != null) {
            while (customFields.hasNext()) {
                List<CustomFieldForMigrationDto> customFieldResponses = getCustomFieldResponsesForLoan(customFields.next());
                if (customFieldResponses != null) {
                    for (CustomFieldForMigrationDto customFieldDto : customFieldResponses) {
                        addOrUpdate(entityResponsesMap, customFieldDto);
                    }
                }
            }
        }
        return entityResponsesMap;
    }

    private List<CustomFieldForMigrationDto> getCustomFieldResponsesForLoan(CustomFieldDefinitionEntity customField) {
        List<CustomFieldForMigrationDto> customFieldResponses = null;
        try {
            customFieldResponses = loanDao.getCustomFieldResponses(customField.getFieldId());
        } catch (Exception e) {
            logger.error(format("Unable to retrieve responses for custom field with ID, %s", customField.getFieldId()), e);
        }
        return customFieldResponses;
    }

    private static int migratedQgInstances = 0;

    private boolean saveQuestionGroupInstance(QuestionGroupInstanceDto questionGroupInstanceDto) {
        if (questionGroupInstanceDto != null) {
            try {
                questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto);
                ++migratedQgInstances;
                if (migratedQgInstances % 1000 == 0) {
                    System.out.printf("%d Saved instances: %d\n", System.currentTimeMillis(), migratedQgInstances);
                    StaticHibernateUtil.getSessionTL().flush();
                    StaticHibernateUtil.getSessionTL().clear();
                }
                return true;
            } catch (Exception e) {
                logger.error(format("Unable to migrate responses from customer with ID, %d for custom fields", questionGroupInstanceDto.getEntityId()), e);
            }
        }
        return false;
    }

    private Map<Integer, List<CustomFieldForMigrationDto>> getCustomFieldResponsesForCustomer(Iterator<CustomFieldDefinitionEntity> customFields) {
        Map<Integer, List<CustomFieldForMigrationDto>> entityResponsesMap = new HashMap<Integer, List<CustomFieldForMigrationDto>>();
        if (customFields != null) {
            while (customFields.hasNext()) {
                List<CustomFieldForMigrationDto> customFieldResponses = getCustomFieldResponsesForCustomer(customFields.next());
                if (customFieldResponses != null) {
                    for (CustomFieldForMigrationDto customFieldDto : customFieldResponses) {
                        addOrUpdate(entityResponsesMap, customFieldDto);
                    }
                }
            }
        }
        return entityResponsesMap;
    }

    private List<CustomFieldForMigrationDto> getCustomFieldResponsesForCustomer(CustomFieldDefinitionEntity customField) {
        List<CustomFieldForMigrationDto> customFieldResponses = null;
        try {
            customFieldResponses = customerDao.getCustomFieldResponses(customField.getFieldId());
        } catch (Exception e) {
            logger.error(format("Unable to retrieve responses for custom field with ID, %s", customField.getFieldId()), e);
        }
        return customFieldResponses;
    }

    private void addOrUpdate(Map<Integer, List<CustomFieldForMigrationDto>> entityResponsesMap, CustomFieldForMigrationDto customFieldResponse) {
        Integer entityId = customFieldResponse.getEntityId();
        if (entityResponsesMap.containsKey(entityId)) {
            entityResponsesMap.get(entityId).add(customFieldResponse);
        } else {
            entityResponsesMap.put(entityId, new ArrayList<CustomFieldForMigrationDto>());
            entityResponsesMap.get(entityId).add(customFieldResponse);
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
            // todo - we migrate only non PPI surveys in release E
            surveys = surveysPersistence.retrieveNonPPISurveysByTypeIterator(surveyType);
        } catch (PersistenceException e) {
            logger.error(String.format("Unable to retrieve surveys of type %s", surveyType), e);
        }
        return surveys;
    }

    private Integer migrateSurvey(Survey survey) {
        QuestionGroupDto questionGroupDto = mapSurveyToQuestionGroupDto(survey);
        Integer questionGroupId = createQuestionGroup(questionGroupDto, survey);
        Integer eventSourceId = getEventSourceId(questionGroupDto);
        if (migrateSurveyResponses(survey, questionGroupId, eventSourceId)) {
            /* todo - we don't want to remove surveys in release E
            surveysPersistence.commitTransaction();

            try {
                surveysPersistence.delete(survey);
            } catch (PersistenceException e) {
                logger.error(format("Unable to remove survey '%s' with ID %s", survey.getName(), survey.getSurveyId()), e);
            }*/
        }
        return questionGroupId;
    }

    private Integer getEventSourceId(QuestionGroupDto questionGroupDto) {
        Integer eventSourceId = null;
        if (questionGroupDto != null) {
            EventSourceDto eventSourceDto = questionGroupDto.getEventSourceDtos().get(0);
            eventSourceId = getEventSourceId(eventSourceDto.getEvent(), eventSourceDto.getSource());
        }
        return eventSourceId;
    }

    private Integer getEventSourceId(String event, String source) {
        Integer eventSourceId = null;
        try {
            eventSourceId = questionnaireServiceFacade.getEventSourceId(event, source);
        } catch (Exception e) {
            logger.error(format("Unable to obtain the event source ID for event %s and source %s'", event, source), e);
        }
        return eventSourceId;
    }

    private QuestionGroupDto mapSurveyToQuestionGroupDto(Survey survey) {
        QuestionGroupDto questionGroupDto = null;
        try {
            questionGroupDto = questionnaireMigrationMapper.map(survey);
        } catch (Exception e) {
            logger.error(format("Unable to convert the survey, '%s' with ID, %s to a Question Group", survey.getName(), survey.getSurveyId()), e);
        }
        return questionGroupDto;
    }

    private Integer createQuestionGroup(QuestionGroupDto questionGroupDto, Survey survey) {
        Integer questionGroupId = null;
        if (questionGroupDto != null) {
            try {
                questionGroupId = questionnaireServiceFacade.createQuestionGroup(questionGroupDto);
            } catch (Exception e) {
                logger.error(format("Unable to convert the survey, '%s' with ID, %s to a Question Group", survey.getName(), survey.getSurveyId()), e);
            }
        }
        return questionGroupId;
    }

    private static int surveysCount = 0;

    private boolean migrateSurveyResponses(Survey survey, Integer questionGroupId, Integer eventSourceId) {
        boolean result = false;
        if (questionGroupId != null && eventSourceId != null) {
            result = true;
            Iterator<SurveyInstance> surveyInstanceIterator = getSurveyInstances(survey);
            if (surveyInstanceIterator != null) {
                while (surveyInstanceIterator.hasNext()) {
                    SurveyInstance surveyInstance = surveyInstanceIterator.next();
                    ++surveysCount;
                    if (surveysCount % 100 == 0) {
                        System.out.printf("%d Migrated survey no %d\n", System.currentTimeMillis(), surveysCount);
                    }
                    if (saveQuestionGroupInstance(mapToQuestionGroupInstance(questionGroupId, eventSourceId, surveyInstance), surveyInstance)) {
                        /* todo - we don't want to remove surveys in release E
                        try {
                            surveysPersistence.delete(surveyInstance);
                        } catch (PersistenceException e) {
                            logger.error(format("Unable to remove survey instance '%s' (survey id: %d, survey instance id: %d)",
                                    survey.getName(), survey.getSurveyId(), surveyInstance.getInstanceId()), e);
                            result = false;
                            surveysPersistence.rollbackTransaction();
                        }*/
                    }
                    else {
                        result = false;
                    }
                }
            }
        }
        return result;
    }

    private QuestionGroupInstanceDto mapToQuestionGroupInstance(Integer questionGroupId, Integer eventSourceId, SurveyInstance surveyInstance) {
        QuestionGroupInstanceDto questionGroupInstanceDto = null;
        try {
            questionGroupInstanceDto = questionnaireMigrationMapper.map(surveyInstance, questionGroupId, eventSourceId);
        } catch (Exception e) {
            Survey survey = surveyInstance.getSurvey();
            logger.error(format("Unable to migrate a survey instance with ID, %s for the survey, '%s' with ID, %s", surveyInstance.getInstanceId(), survey.getName(), survey.getSurveyId()), e);
        }
        return questionGroupInstanceDto;
    }

    private boolean saveQuestionGroupInstance(QuestionGroupInstanceDto questionGroupInstanceDto, SurveyInstance surveyInstance) {
        if (questionGroupInstanceDto != null) {
            try {
                questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto);
                return true;
            } catch (Exception e) {
                Survey survey = surveyInstance.getSurvey();
                logger.error(format("Unable to migrate a survey instance with ID, %s for the survey, '%s' with ID, %s", surveyInstance.getInstanceId(), survey.getName(), survey.getSurveyId()), e);
            }
        }
        return false;
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