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
        migrateAdditionalFields(EntityType.CLIENT);
        migrateAdditionalFields(EntityType.GROUP);
        migrateAdditionalFields(EntityType.CENTER);
        migrateAdditionalFields(EntityType.LOAN);
        migrateAdditionalFields(EntityType.SAVINGS);
        migrateAdditionalFields(EntityType.OFFICE);
        migrateAdditionalFields(EntityType.PERSONNEL);
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

    private void removeCustomFields(EntityType entityType, boolean fullyMigrated, List<CustomFieldDefinitionEntity> customFields, List<Integer> responses) {
        System.out.printf("%d removing Additional Fields for %s\n", System.currentTimeMillis(), entityType);
        SQLQuery update = null;
        switch (entityType) {
            case CLIENT:
            case GROUP:
            case CENTER: update = StaticHibernateUtil.getSessionTL().createSQLQuery("delete from customer_custom_field where customer_customfield_id = :customFieldId"); break;
            case LOAN:
            case SAVINGS: update = StaticHibernateUtil.getSessionTL().createSQLQuery("delete from account_custom_field where account_custom_field_id = :customFieldId"); break;
            case OFFICE: update = StaticHibernateUtil.getSessionTL().createSQLQuery("delete from office_custom_field where office_custom_field_id = :customFieldId"); break;
            case PERSONNEL: update = StaticHibernateUtil.getSessionTL().createSQLQuery("delete from personnel_custom_field where personnel_custom_field_id = :customFieldId"); break;
        }

        final int COMMIT_EVERY = 1000;
        final int PRINT_EVERY = 10000;

        StaticHibernateUtil.startTransaction();
        int modifyCount = 0;
        for (Integer responseId : responses) {
            ++modifyCount;
            try {
                update.setInteger("customFieldId", responseId);
                update.executeUpdate();
                if (modifyCount % COMMIT_EVERY == 0) {
					StaticHibernateUtil.commitTransaction();
                    StaticHibernateUtil.startTransaction();
				}
                if (modifyCount % PRINT_EVERY == 0) {
                    System.out.print(".");
                    System.out.flush();
                }
            } catch (HibernateException e) {
                logger.error(format("Unable to remove response given for %s with ID %d for custom fields", entityType, responseId), e);
                StaticHibernateUtil.rollbackTransaction();
                return;
            }
        }
        if (fullyMigrated && customFields != null) {
            for (CustomFieldDefinitionEntity customField : customFields) {
                try {
                    surveysPersistence.delete(customField);
                } catch (PersistenceException e) {
                    logger.error(format("Unable to remove custom field with ID %d", customField.getFieldId()), e);
                    StaticHibernateUtil.rollbackTransaction();
                    return;
                }
            }
        }
        StaticHibernateUtil.commitTransaction();
    }

    // Made 'public' to aid unit testing
    public Integer migrateAdditionalFields(EntityType entityType) {
        System.out.printf("%d migrating Additional Fields for %s\n", System.currentTimeMillis(), entityType);
        List<CustomFieldDefinitionEntity> customFields = getCustomFields(entityType);
        Integer questionGroupId = null;
        if (customFields != null) {
            Map<Short, Integer> customFieldQuestionIdMap = new HashMap<Short, Integer>();
            questionGroupId = getQuestionGroup(customFields.iterator(), customFieldQuestionIdMap, entityType);
            List<Integer> responses = new ArrayList<Integer>();
            boolean fullyMigrated = migrateAdditionalFieldsResponses(entityType, customFields, questionGroupId, customFieldQuestionIdMap, responses);
            removeCustomFields(entityType, fullyMigrated, customFields, responses);
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

    private List<CustomFieldDefinitionEntity> getCustomFields(EntityType entityType) {
        List<CustomFieldDefinitionEntity> customFields = null;
        try {
            switch (entityType) {
                case CLIENT: customFields = customerDao.retrieveCustomFieldEntitiesForClient(); break;
                case GROUP: customFields = customerDao.retrieveCustomFieldEntitiesForGroup(); break;
                case CENTER: customFields = customerDao.retrieveCustomFieldEntitiesForCenter(); break;
                case LOAN: customFields = loanDao.retrieveCustomFieldEntitiesForLoan(); break;
                case SAVINGS: customFields = savingsDao.retrieveCustomFieldEntitiesForSavings(); break;
                case OFFICE: customFields = officeDao.retrieveCustomFieldEntitiesForOffice(); break;
                case PERSONNEL: customFields = personnelDao.retrieveCustomFieldEntitiesForPersonnel(); break;
            }
        } catch (Exception e) {
            logger.error("Unable to retrieve custom fields for " + entityType, e);
        }
        return customFields;
    }

    private boolean migrateAdditionalFieldsResponses(EntityType entityType, List<CustomFieldDefinitionEntity> customFields, Integer questionGroupId,
                                                     Map<Short, Integer> customFieldQuestionIdMap, List<Integer> responsesIds) {
        boolean result = false;
        Integer eventSourceId = getEventSourceId(entityType);
        if (questionGroupId != null && eventSourceId != null) {
            result = true;
            Iterator<CustomFieldDefinitionEntity> cfIter = customFields.iterator();
            while (cfIter.hasNext()) {
                CustomFieldDefinitionEntity cfDefinition = cfIter.next();
                if (customFieldQuestionIdMap.get(cfDefinition.getFieldId()) == null) { // this field hasn't been migrated
                    cfIter.remove();
                }
            }
            List<Object[]> customFieldResponses = getCustomFieldResponses(entityType, customFields);
            if (customFieldResponses != null) {
                Integer previousCustomer = null;
                List<CustomFieldForMigrationDto> migrationDtos = null;
                for (Object[] customFieldResponse : customFieldResponses) {
                    CustomFieldForMigrationDto customField = new CustomFieldForMigrationDto(customFieldResponse);
                    if (customField.getEntityId().equals(previousCustomer)) {
                        //noinspection ConstantConditions
                        migrationDtos.add(customField);
                        continue;
                    }
                    result = saveQuestionGroupInstance(entityType, questionGroupId, customFieldQuestionIdMap, responsesIds, result, eventSourceId, migrationDtos);
                    migrationDtos = new ArrayList<CustomFieldForMigrationDto>();
                    migrationDtos.add(customField);
                    previousCustomer = customField.getEntityId();
                }
                result = saveQuestionGroupInstance(entityType, questionGroupId, customFieldQuestionIdMap, responsesIds, result, eventSourceId, migrationDtos);
            }
        }
        return result;
    }

    private boolean saveQuestionGroupInstance(EntityType entityType, Integer questionGroupId, Map<Short, Integer> customFieldQuestionIdMap, List<Integer> responsesIds, boolean result, Integer eventSourceId, List<CustomFieldForMigrationDto> customerResponses) {
        if (customerResponses != null) {
            QuestionGroupInstanceDto questionGroupInstanceDto = mapToQuestionGroupInstance(entityType, questionGroupId, eventSourceId, customFieldQuestionIdMap, customerResponses);
            if (saveQuestionGroupInstance(questionGroupInstanceDto)) {
                for (CustomFieldForMigrationDto response : customerResponses) {
                    responsesIds.add(response.getCustomFieldId());
                }
            }
            else {
                result = false;
            }
        }
        return result;
    }

    private QuestionGroupInstanceDto mapToQuestionGroupInstance(EntityType type, Integer questionGroupId,
                                                                            Integer eventSourceId, Map<Short, Integer> customFieldQuestionIdMap, List<CustomFieldForMigrationDto> responses) {
        QuestionGroupInstanceDto questionGroupInstanceDto = null;
        try {
            questionGroupInstanceDto = questionnaireMigrationMapper.map(questionGroupId, eventSourceId, responses, customFieldQuestionIdMap);
        } catch (Exception e) {
            logger.error(format("Unable to convert responses given for %s with ID, %d for custom fields, to Question Group responses", type, responses.get(0).getEntityId()), e);
        }
        return questionGroupInstanceDto;
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

    private List<Object[]> getCustomFieldResponses(EntityType entityType, List<CustomFieldDefinitionEntity> customFields) {
        if (customFields != null) {
            List<Short> ids = new ArrayList<Short>();
            for (CustomFieldDefinitionEntity customField : customFields) {
                ids.add(customField.getFieldId());
            }
            if (ids.size() > 0) {
                return getCustomFieldResponsesList(entityType, ids);
            }
        }
        return null;
    }

    private List<Object[]> getCustomFieldResponsesList(EntityType entityType, List<Short> customFields) {
        List<Object[]> customFieldResponses = null;
        try {
            switch (entityType) {
                case CLIENT:
                case GROUP:
                case CENTER: customFieldResponses = customerDao.getCustomFieldResponses(customFields); break;
                case LOAN: customFieldResponses = loanDao.getCustomFieldResponses(customFields); break;
                case SAVINGS: customFieldResponses = savingsDao.getCustomFieldResponses(customFields); break;
                case OFFICE: customFieldResponses = officeDao.getCustomFieldResponses(customFields); break;
                case PERSONNEL: customFieldResponses = personnelDao.getCustomFieldResponses(customFields); break;
            }
        } catch (Exception e) {
            logger.error(format("Unable to retrieve responses for custom field with IDs, %s", customFields), e);
        }
        return customFieldResponses;
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

    private Integer getEventSourceId(EntityType entityType) {
        Integer eventSourceId = null;
        String event = "Create";
        String source = null;
        switch (entityType) {
                case CLIENT: source = "Client"; break;
                case GROUP: source = "Group"; break;
                case CENTER: source = "Center"; break;
                case LOAN: source = "Loan"; break;
                case SAVINGS: source = "Savings"; break;
                case OFFICE: source = "Office"; break;
                case PERSONNEL: source = "Personnel"; break;
            }
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