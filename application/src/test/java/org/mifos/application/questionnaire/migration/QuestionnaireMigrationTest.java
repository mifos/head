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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountCustomFieldEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.questionnaire.migration.mappers.QuestionnaireMigrationMapper;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.customers.business.CustomerCustomFieldEntity;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.surveys.business.CustomFieldUtils;
import org.mifos.customers.surveys.business.Survey;
import org.mifos.customers.surveys.business.SurveyInstance;
import org.mifos.customers.surveys.business.SurveyUtils;
import org.mifos.customers.surveys.helpers.SurveyType;
import org.mifos.customers.surveys.persistence.SurveysPersistence;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.builders.QuestionDtoBuilder;
import org.mifos.platform.questionnaire.builders.QuestionGroupDtoBuilder;
import org.mifos.platform.questionnaire.builders.QuestionGroupInstanceDtoBuilder;
import org.mifos.platform.questionnaire.builders.QuestionGroupResponseDtoBuilder;
import org.mifos.platform.questionnaire.builders.SectionDtoBuilder;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.service.dtos.QuestionDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupInstanceDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupResponseDto;
import org.mifos.platform.questionnaire.service.dtos.SectionDto;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mifos.customers.surveys.business.CustomFieldUtils.getCustomFieldDef;
import static org.mifos.customers.surveys.business.SurveyUtils.getGroupBO;
import static org.mifos.customers.surveys.business.SurveyUtils.getLoanBO;
import static org.mifos.customers.surveys.business.SurveyUtils.getSurvey;
import static org.mifos.customers.surveys.business.SurveyUtils.getSurveyInstance;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuestionnaireMigrationTest {

    @Mock
    private QuestionnaireMigrationMapper questionnaireMigrationMapper;

    @Mock
    private QuestionnaireServiceFacade questionnaireServiceFacade;

    @Mock
    private SurveysPersistence surveysPersistence;

    @Mock
    private CustomerDao customerDao;

    @Mock
    private LoanDao loanDao;

    private QuestionnaireMigration questionnaireMigration;

    private static final int QUESTION_GROUP_ID = 123;

    private Calendar calendar;

    @Before
    public void setUp() {
        questionnaireMigration = new QuestionnaireMigration(questionnaireMigrationMapper, questionnaireServiceFacade, surveysPersistence, customerDao, loanDao);
        calendar = Calendar.getInstance();
    }

    @Test
    public void shouldMigrateSurveys() throws ApplicationException {
        Survey survey1 = getSurvey("Sur1", "Ques1", calendar.getTime());
        Survey survey2 = getSurvey("Sur2", "Ques2", calendar.getTime());
        List<Survey> surveys = asList(survey1, survey2);
        when(surveysPersistence.retrieveSurveysByTypeIterator(SurveyType.CLIENT)).thenReturn(surveys.iterator());
        QuestionGroupDto questionGroupDto1 = getQuestionGroupDto("Sur1", "Ques1", "View", "Client");
        QuestionGroupDto questionGroupDto2 = getQuestionGroupDto("Sur2", "Ques2", "View", "Client");
        SurveyInstance surveyInstance1 = getSurveyInstance(survey1, 12, 101, "Answer1");
        QuestionGroupInstanceDto questionGroupInstanceDto1 = getQuestionGroupInstanceDto("Answer1", 12, 101);
        SurveyInstance surveyInstance2 = getSurveyInstance(survey1, 13, 102, "Answer2");
        QuestionGroupInstanceDto questionGroupInstanceDto2 = getQuestionGroupInstanceDto("Answer2", 13, 102);
        SurveyInstance surveyInstance3 = getSurveyInstance(survey2, 12, 101, "Answer3");
        QuestionGroupInstanceDto questionGroupInstanceDto3 = getQuestionGroupInstanceDto("Answer3", 12, 101);
        SurveyInstance surveyInstance4 = getSurveyInstance(survey2, 13, 102, "Answer4");
        QuestionGroupInstanceDto questionGroupInstanceDto4 = getQuestionGroupInstanceDto("Answer4", 13, 102);
        when(questionnaireMigrationMapper.map(survey1)).thenReturn(questionGroupDto1);
        when(questionnaireMigrationMapper.map(survey2)).thenReturn(questionGroupDto2);
        when(questionnaireMigrationMapper.map(eq(surveyInstance1), anyInt())).thenReturn(questionGroupInstanceDto1);
        when(questionnaireMigrationMapper.map(eq(surveyInstance2), anyInt())).thenReturn(questionGroupInstanceDto2);
        when(questionnaireMigrationMapper.map(eq(surveyInstance3), anyInt())).thenReturn(questionGroupInstanceDto3);
        when(questionnaireMigrationMapper.map(eq(surveyInstance4), anyInt())).thenReturn(questionGroupInstanceDto4);
        when(questionnaireServiceFacade.createQuestionGroup(questionGroupDto1)).thenReturn(121);
        when(questionnaireServiceFacade.createQuestionGroup(questionGroupDto2)).thenReturn(122);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto1)).thenReturn(1111);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto2)).thenReturn(2222);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto3)).thenReturn(3333);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto4)).thenReturn(4444);
        List<SurveyInstance> surveyInstances1 = asList(surveyInstance1, surveyInstance2);
        when(surveysPersistence.retrieveInstancesBySurveyIterator(survey1)).thenReturn(surveyInstances1.iterator());
        List<SurveyInstance> surveyInstances2 = asList(surveyInstance3, surveyInstance4);
        when(surveysPersistence.retrieveInstancesBySurveyIterator(survey2)).thenReturn(surveyInstances2.iterator());
        List<Integer> questionGroupIds = questionnaireMigration.migrateSurveys();
        assertThat(questionGroupIds, is(notNullValue()));
        assertThat(questionGroupIds.size(), is(2));
        assertThat(questionGroupIds.get(0), is(121));
        assertThat(questionGroupIds.get(1), is(122));
        verify(questionnaireMigrationMapper, times(2)).map(any(Survey.class));
        verify(questionnaireMigrationMapper, times(4)).map(any(SurveyInstance.class), anyInt());
        verify(questionnaireServiceFacade, times(2)).createQuestionGroup(any(QuestionGroupDto.class));
        verify(questionnaireServiceFacade, times(4)).saveQuestionGroupInstance(any(QuestionGroupInstanceDto.class));
        verify(surveysPersistence, times(1)).retrieveSurveysByTypeIterator(SurveyType.CLIENT);
        verify(surveysPersistence, times(2)).retrieveInstancesBySurveyIterator(any(Survey.class));
    }

    @Test
    public void shouldMigrateAdditionalFieldsForClient() {
        QuestionGroupDto questionGroupDto = new QuestionGroupDto();
        QuestionGroupInstanceDto questionGroupInstanceDto1 = new QuestionGroupInstanceDto();
        QuestionGroupInstanceDto questionGroupInstanceDto2 = new QuestionGroupInstanceDto();
        CustomFieldDefinitionEntity customFieldDef1 = getCustomFieldDef(1, "CustomField1", CustomerLevel.CLIENT, CustomFieldType.ALPHA_NUMERIC, EntityType.CLIENT);
        CustomFieldDefinitionEntity customFieldDef2 = getCustomFieldDef(2, "CustomField2", CustomerLevel.CLIENT, CustomFieldType.DATE, EntityType.CLIENT);
        List<CustomFieldDefinitionEntity> customFields = asList(customFieldDef1, customFieldDef2);
        Map<Short,Integer> customFieldQuestionIdMap = new HashMap<Short, Integer>();
        Iterator<CustomFieldDefinitionEntity> customFieldIterator = customFields.iterator();
        when(customerDao.retrieveCustomFieldEntitiesForClientIterator()).thenReturn(customFieldIterator);
        when(questionnaireMigrationMapper.map(customFieldIterator, customFieldQuestionIdMap, EntityType.CLIENT)).thenReturn(questionGroupDto);
        when(questionnaireServiceFacade.createQuestionGroup(questionGroupDto)).thenReturn(QUESTION_GROUP_ID);
        ClientBO clientBO1 = SurveyUtils.getClientBO(11);
        CustomerCustomFieldEntity customField1 = CustomFieldUtils.getCustomerCustomField(1, "Ans1", clientBO1);
        CustomerCustomFieldEntity customField2 = CustomFieldUtils.getCustomerCustomField(1, "Ans2", clientBO1);
        CustomerCustomFieldEntity customField3 = CustomFieldUtils.getCustomerCustomField(1, "Ans3", clientBO1);
        List<CustomerCustomFieldEntity> customerResponses1 = asList(customField1, customField2, customField3);
        when(customerDao.getCustomFieldResponses(Short.valueOf("1"))).thenReturn(customerResponses1.iterator());
        when(questionnaireMigrationMapper.mapForCustomers(QUESTION_GROUP_ID, customerResponses1, customFieldQuestionIdMap)).thenReturn(questionGroupInstanceDto1);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto1)).thenReturn(0);
        ClientBO clientBO2 = SurveyUtils.getClientBO(22);
        CustomerCustomFieldEntity customField4 = CustomFieldUtils.getCustomerCustomField(2, "Ans11", clientBO2);
        CustomerCustomFieldEntity customField5 = CustomFieldUtils.getCustomerCustomField(2, "Ans22", clientBO2);
        CustomerCustomFieldEntity customField6 = CustomFieldUtils.getCustomerCustomField(2, "Ans33", clientBO2);
        List<CustomerCustomFieldEntity> customerResponses2 = asList(customField4, customField5, customField6);
        when(customerDao.getCustomFieldResponses(Short.valueOf("2"))).thenReturn(customerResponses2.iterator());
        when(questionnaireMigrationMapper.mapForCustomers(QUESTION_GROUP_ID, customerResponses2, customFieldQuestionIdMap)).thenReturn(questionGroupInstanceDto2);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto2)).thenReturn(0);
        Integer questionGroupId = questionnaireMigration.migrateAdditionalFieldsForClient();
        assertThat(questionGroupId, is(QUESTION_GROUP_ID));
        verify(questionnaireMigrationMapper).map(customFieldIterator, customFieldQuestionIdMap, EntityType.CLIENT);
        verify(questionnaireServiceFacade).createQuestionGroup(questionGroupDto);
        verify(customerDao, times(2)).getCustomFieldResponses(any(Short.class));
        verify(customerDao, times(2)).retrieveCustomFieldEntitiesForClientIterator();
        verify(questionnaireMigrationMapper, times(2)).mapForCustomers(eq(QUESTION_GROUP_ID), Matchers.<List<CustomerCustomFieldEntity>>any(), eq(customFieldQuestionIdMap));
        verify(questionnaireServiceFacade).saveQuestionGroupInstance(questionGroupInstanceDto1);
        verify(questionnaireServiceFacade).saveQuestionGroupInstance(questionGroupInstanceDto2);
    }

    @Test
    public void shouldMigrateAdditionalFieldsForGroup() {
        QuestionGroupDto questionGroupDto = new QuestionGroupDto();
        QuestionGroupInstanceDto questionGroupInstanceDto1 = new QuestionGroupInstanceDto();
        QuestionGroupInstanceDto questionGroupInstanceDto2 = new QuestionGroupInstanceDto();
        CustomFieldDefinitionEntity customFieldDef1 = getCustomFieldDef(1, "CustomField1", CustomerLevel.GROUP, CustomFieldType.ALPHA_NUMERIC, EntityType.GROUP);
        CustomFieldDefinitionEntity customFieldDef2 = getCustomFieldDef(2, "CustomField2", CustomerLevel.GROUP, CustomFieldType.DATE, EntityType.GROUP);
        List<CustomFieldDefinitionEntity> customFields = asList(customFieldDef1, customFieldDef2);
        Map<Short,Integer> customFieldQuestionIdMap = new HashMap<Short, Integer>();
        Iterator<CustomFieldDefinitionEntity> customFieldIterator = customFields.iterator();
        when(customerDao.retrieveCustomFieldEntitiesForGroupIterator()).thenReturn(customFieldIterator);
        when(questionnaireMigrationMapper.map(customFieldIterator, customFieldQuestionIdMap, EntityType.GROUP)).thenReturn(questionGroupDto);
        when(questionnaireServiceFacade.createQuestionGroup(questionGroupDto)).thenReturn(QUESTION_GROUP_ID);
        GroupBO groupBO1 = getGroupBO(11);
        CustomerCustomFieldEntity customField1 = CustomFieldUtils.getCustomerCustomField(1, "Ans1", groupBO1);
        CustomerCustomFieldEntity customField2 = CustomFieldUtils.getCustomerCustomField(1, "Ans2", groupBO1);
        CustomerCustomFieldEntity customField3 = CustomFieldUtils.getCustomerCustomField(1, "Ans3", groupBO1);
        List<CustomerCustomFieldEntity> customerResponses1 = asList(customField1, customField2, customField3);
        when(customerDao.getCustomFieldResponses(Short.valueOf("1"))).thenReturn(customerResponses1.iterator());
        when(questionnaireMigrationMapper.mapForCustomers(QUESTION_GROUP_ID, customerResponses1, customFieldQuestionIdMap)).thenReturn(questionGroupInstanceDto1);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto1)).thenReturn(0);
        GroupBO groupBO2 = getGroupBO(22);
        CustomerCustomFieldEntity customField4 = CustomFieldUtils.getCustomerCustomField(2, "Ans11", groupBO2);
        CustomerCustomFieldEntity customField5 = CustomFieldUtils.getCustomerCustomField(2, "Ans22", groupBO2);
        CustomerCustomFieldEntity customField6 = CustomFieldUtils.getCustomerCustomField(2, "Ans33", groupBO2);
        List<CustomerCustomFieldEntity> customerResponses2 = asList(customField4, customField5, customField6);
        when(customerDao.getCustomFieldResponses(Short.valueOf("2"))).thenReturn(customerResponses2.iterator());
        when(questionnaireMigrationMapper.mapForCustomers(QUESTION_GROUP_ID, customerResponses2, customFieldQuestionIdMap)).thenReturn(questionGroupInstanceDto2);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto2)).thenReturn(0);
        Integer questionGroupId = questionnaireMigration.migrateAdditionalFieldsForGroup();
        assertThat(questionGroupId, is(QUESTION_GROUP_ID));
        verify(questionnaireMigrationMapper).map(customFieldIterator, customFieldQuestionIdMap, EntityType.GROUP);
        verify(questionnaireServiceFacade).createQuestionGroup(questionGroupDto);
        verify(customerDao, times(2)).getCustomFieldResponses(any(Short.class));
        verify(customerDao, times(2)).retrieveCustomFieldEntitiesForGroupIterator();
        verify(questionnaireMigrationMapper, times(2)).mapForCustomers(eq(QUESTION_GROUP_ID), Matchers.<List<CustomerCustomFieldEntity>>any(), eq(customFieldQuestionIdMap));
        verify(questionnaireServiceFacade).saveQuestionGroupInstance(questionGroupInstanceDto1);
        verify(questionnaireServiceFacade).saveQuestionGroupInstance(questionGroupInstanceDto2);
    }

    @Test
    public void shouldMigrateAdditionalFieldsForLoan() {
        QuestionGroupDto questionGroupDto = new QuestionGroupDto();
        QuestionGroupInstanceDto questionGroupInstanceDto1 = new QuestionGroupInstanceDto();
        QuestionGroupInstanceDto questionGroupInstanceDto2 = new QuestionGroupInstanceDto();
        CustomFieldDefinitionEntity customFieldDef1 = getCustomFieldDef(1, "CustomField1", CustomerLevel.CLIENT, CustomFieldType.ALPHA_NUMERIC, EntityType.LOAN);
        CustomFieldDefinitionEntity customFieldDef2 = getCustomFieldDef(2, "CustomField2", CustomerLevel.CLIENT, CustomFieldType.DATE, EntityType.LOAN);
        List<CustomFieldDefinitionEntity> customFields = asList(customFieldDef1, customFieldDef2);
        Map<Short,Integer> customFieldQuestionIdMap = new HashMap<Short, Integer>();
        Iterator<CustomFieldDefinitionEntity> customFieldIterator = customFields.iterator();
        when(loanDao.retrieveCustomFieldEntitiesForLoan()).thenReturn(customFieldIterator);
        when(questionnaireMigrationMapper.map(customFieldIterator, customFieldQuestionIdMap, EntityType.LOAN)).thenReturn(questionGroupDto);
        when(questionnaireServiceFacade.createQuestionGroup(questionGroupDto)).thenReturn(QUESTION_GROUP_ID);
        LoanBO loanBO1 = getLoanBO(11);
        AccountCustomFieldEntity customField1 = CustomFieldUtils.getLoanCustomField(1, "Ans1", loanBO1);
        AccountCustomFieldEntity customField2 = CustomFieldUtils.getLoanCustomField(1, "Ans2", loanBO1);
        AccountCustomFieldEntity customField3 = CustomFieldUtils.getLoanCustomField(1, "Ans3", loanBO1);
        List<AccountCustomFieldEntity> loanResponses1 = asList(customField1, customField2, customField3);
        when(loanDao.getCustomFieldResponses(Short.valueOf("1"))).thenReturn(loanResponses1.iterator());
        when(questionnaireMigrationMapper.mapForAccounts(QUESTION_GROUP_ID, loanResponses1, customFieldQuestionIdMap)).thenReturn(questionGroupInstanceDto1);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto1)).thenReturn(0);
        LoanBO loanBO2 = getLoanBO(22);
        AccountCustomFieldEntity customField4 = CustomFieldUtils.getLoanCustomField(2, "Ans11", loanBO2);
        AccountCustomFieldEntity customField5 = CustomFieldUtils.getLoanCustomField(2, "Ans22", loanBO2);
        AccountCustomFieldEntity customField6 = CustomFieldUtils.getLoanCustomField(2, "Ans33", loanBO2);
        List<AccountCustomFieldEntity> customerResponses2 = asList(customField4, customField5, customField6);
        when(loanDao.getCustomFieldResponses(Short.valueOf("2"))).thenReturn(customerResponses2.iterator());
        when(questionnaireMigrationMapper.mapForAccounts(QUESTION_GROUP_ID, customerResponses2, customFieldQuestionIdMap)).thenReturn(questionGroupInstanceDto2);
        when(questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto2)).thenReturn(0);
        Integer questionGroupId = questionnaireMigration.migrateAdditionalFieldsForLoan();
        assertThat(questionGroupId, is(QUESTION_GROUP_ID));
        verify(questionnaireMigrationMapper).map(customFieldIterator, customFieldQuestionIdMap, EntityType.LOAN);
        verify(questionnaireServiceFacade).createQuestionGroup(questionGroupDto);
        verify(loanDao, times(2)).getCustomFieldResponses(any(Short.class));
        verify(loanDao, times(2)).retrieveCustomFieldEntitiesForLoan();
        verify(questionnaireMigrationMapper, times(2)).mapForAccounts(eq(QUESTION_GROUP_ID), Matchers.<List<AccountCustomFieldEntity>>any(), eq(customFieldQuestionIdMap));
        verify(questionnaireServiceFacade).saveQuestionGroupInstance(questionGroupInstanceDto1);
        verify(questionnaireServiceFacade).saveQuestionGroupInstance(questionGroupInstanceDto2);
    }

    private QuestionGroupDto getQuestionGroupDto(String questionGroupTitle, String questionTitle, String event, String source) {
        QuestionGroupDtoBuilder questionGroupDtoBuilder = new QuestionGroupDtoBuilder();
        QuestionDto questionDto = new QuestionDtoBuilder().withTitle(questionTitle).withType(QuestionType.FREETEXT).build();
        SectionDto sectionDto = new SectionDtoBuilder().withName("Misc").withOrder(0).withQuestions(asList(questionDto)).build();
        questionGroupDtoBuilder.withTitle(questionGroupTitle).withEventSource(event, source).withSections(asList(sectionDto));
        return questionGroupDtoBuilder.build();
    }

    private QuestionGroupInstanceDto getQuestionGroupInstanceDto(String response, Integer creatorId, Integer entityId) {
        QuestionGroupInstanceDtoBuilder instanceBuilder = new QuestionGroupInstanceDtoBuilder();
        QuestionGroupResponseDtoBuilder responseBuilder = new QuestionGroupResponseDtoBuilder();
        responseBuilder.withResponse(response).withSectionQuestion(999);
        QuestionGroupResponseDto questionGroupResponseDto = responseBuilder.build();
        instanceBuilder.withQuestionGroup(123).withCompleted(true).withCreator(creatorId).withEntity(entityId).withVersion(1).addResponses(questionGroupResponseDto);
        return instanceBuilder.build();
    }

}