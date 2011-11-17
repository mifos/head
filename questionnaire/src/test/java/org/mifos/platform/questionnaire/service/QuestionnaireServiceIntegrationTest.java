/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.platform.questionnaire.service;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.builders.ChoiceDetailBuilder;
import org.mifos.platform.questionnaire.builders.QuestionDtoBuilder;
import org.mifos.platform.questionnaire.builders.QuestionGroupDtoBuilder;
import org.mifos.platform.questionnaire.builders.QuestionGroupInstanceDtoBuilder;
import org.mifos.platform.questionnaire.builders.QuestionGroupResponseDtoBuilder;
import org.mifos.platform.questionnaire.builders.SectionDtoBuilder;
import org.mifos.platform.questionnaire.domain.AnswerType;
import org.mifos.platform.questionnaire.domain.ChoiceTagEntity;
import org.mifos.platform.questionnaire.domain.EventSourceEntity;
import org.mifos.platform.questionnaire.domain.QuestionChoiceEntity;
import org.mifos.platform.questionnaire.domain.QuestionEntity;
import org.mifos.platform.questionnaire.domain.QuestionGroup;
import org.mifos.platform.questionnaire.domain.QuestionGroupInstance;
import org.mifos.platform.questionnaire.domain.QuestionGroupResponse;
import org.mifos.platform.questionnaire.domain.QuestionGroupState;
import org.mifos.platform.questionnaire.domain.QuestionState;
import org.mifos.platform.questionnaire.domain.QuestionnaireService;
import org.mifos.platform.questionnaire.domain.Section;
import org.mifos.platform.questionnaire.domain.SectionQuestion;
import org.mifos.platform.questionnaire.matchers.EventSourcesMatcher;
import org.mifos.platform.questionnaire.matchers.QuestionChoicesMatcher;
import org.mifos.platform.questionnaire.matchers.QuestionGroupDetailMatcher;
import org.mifos.platform.questionnaire.persistence.QuestionDao;
import org.mifos.platform.questionnaire.persistence.QuestionGroupDao;
import org.mifos.platform.questionnaire.persistence.QuestionGroupInstanceDao;
import org.mifos.platform.questionnaire.service.dtos.ChoiceDto;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupInstanceDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupResponseDto;
import org.mifos.platform.questionnaire.service.dtos.SectionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test-questionnaire-dbContext.xml", "/test-questionnaire-persistenceContext.xml", "/META-INF/spring/QuestionnaireContext.xml"})
@TransactionConfiguration(transactionManager = "platformTransactionManager", defaultRollback = true)
public class QuestionnaireServiceIntegrationTest {

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private QuestionGroupDao questionGroupDao;

    @Autowired
    private QuestionGroupInstanceDao questionGroupInstanceDao;

    @Autowired
    private AnnotationSessionFactoryBean sessionFactory;

    public static final String TITLE = "Title";

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldDefineQuestion() throws SystemException {
        String questionTitle = TITLE + currentTimeMillis();
        QuestionDetail questionDetail = defineQuestion(questionTitle, QuestionType.DATE, true);
        assertNotNull(questionDetail);
        Integer questionId = questionDetail.getId();
        assertNotNull(questionId);
        QuestionEntity questionEntity = questionDao.getDetails(questionId);
        assertNotNull(questionEntity);
        Assert.assertEquals(questionTitle, questionEntity.getQuestionText());
        Assert.assertEquals(AnswerType.DATE, questionEntity.getAnswerTypeAsEnum());
        Assert.assertEquals(questionDetail.getAnswerChoices(), asList());
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldUpdateQuestion() throws SystemException {
        String questionTitle = TITLE + currentTimeMillis();
        QuestionDetail questionDetail = defineQuestion(questionTitle, QuestionType.DATE, true);
        assertNotNull(questionDetail);
        Integer questionId = questionDetail.getId();
        assertNotNull(questionId);
        QuestionEntity questionEntity = questionDao.getDetails(questionId);
        assertNotNull(questionEntity);
        Assert.assertEquals(questionTitle, questionEntity.getQuestionText());
        Assert.assertEquals(AnswerType.DATE, questionEntity.getAnswerTypeAsEnum());
        Assert.assertEquals(questionDetail.getAnswerChoices(), asList());

        questionDetail.setActive(false);
        questionDetail.setText(questionTitle + 1);
        Assert.assertThat(questionDao.retrieveCountOfQuestionsWithText(questionTitle).get(0).intValue(), is(1));

        // See http://forum.springsource.org/showthread.php?t=18951, integration test practice recommended by Rod Johnson
        SessionFactoryUtils.getSession(sessionFactory.getObject(), false).clear();

        questionnaireService.defineQuestion(questionDetail);
        Assert.assertThat(questionDao.retrieveCountOfQuestionsWithText(questionTitle).get(0).intValue(), is(0));
        questionEntity = questionDao.getDetails(questionId);
        assertNotNull(questionEntity);
        Assert.assertEquals(questionTitle + 1, questionEntity.getQuestionText());
        Assert.assertEquals(questionTitle + 1, questionEntity.getQuestionText());
        Assert.assertEquals(AnswerType.DATE, questionEntity.getAnswerTypeAsEnum());
        Assert.assertEquals(questionDetail.getAnswerChoices(), asList());
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldDefineMultiSelectQuestion() throws SystemException {
        String questionTitle = TITLE + currentTimeMillis();
        QuestionDetail questionDetail = defineQuestion(questionTitle, QuestionType.MULTI_SELECT, asList("choice1", "choice2"));
        Assert.assertNotNull(questionDetail);
        Integer questionId = questionDetail.getId();
        assertNotNull(questionId);
        QuestionEntity questionEntity = questionDao.getDetails(questionId);
        assertNotNull(questionEntity);
        Assert.assertEquals(questionTitle, questionEntity.getQuestionText());
        Assert.assertEquals(AnswerType.MULTISELECT, questionEntity.getAnswerTypeAsEnum());
        assertThat(questionEntity.getChoices(), new QuestionChoicesMatcher(asList(new QuestionChoiceEntity("choice1"), new QuestionChoiceEntity("choice2"))));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldDefineSingleSelectQuestion() throws SystemException {
        String questionTitle = TITLE + currentTimeMillis();
        QuestionDetail questionDetail = defineQuestion(questionTitle, QuestionType.SINGLE_SELECT, asList("choice1", "choice2"));
        assertNotNull(questionDetail);
        Integer questionId = questionDetail.getId();
        assertNotNull(questionId);
        QuestionEntity questionEntity = questionDao.getDetails(questionId);
        assertNotNull(questionEntity);
        Assert.assertEquals(questionTitle, questionEntity.getQuestionText());
        Assert.assertEquals(AnswerType.SINGLESELECT, questionEntity.getAnswerTypeAsEnum());
        assertThat(questionEntity.getChoices(), new QuestionChoicesMatcher(asList(new QuestionChoiceEntity("choice1"), new QuestionChoiceEntity("choice2"))));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldDefineQuestionGroup() throws SystemException {
        String title = TITLE + currentTimeMillis();
        QuestionDetail questionDetail1 = defineQuestion(title + 1, QuestionType.NUMERIC, true);
        QuestionDetail questionDetail2 = defineQuestion(title + 2, QuestionType.FREETEXT, true);
        SectionDetail section1 = getSectionWithQuestionId("S1", questionDetail1);
        SectionDetail section2 = getSectionWithQuestionId("S2", questionDetail2);
        QuestionGroupDetail questionGroupDetail = defineQuestionGroup(title, "Create", "Client", asList(section1, section2), true);
        assertNotNull(questionGroupDetail);
        Integer questionGroupId = questionGroupDetail.getId();
        assertNotNull(questionGroupId);
        QuestionGroup questionGroup = questionGroupDao.getDetails(questionGroupId);
        assertNotNull(questionGroup);
        Assert.assertEquals(title, questionGroup.getTitle());
        Assert.assertEquals(QuestionGroupState.ACTIVE, questionGroup.getState());
        Assert.assertEquals(true, questionGroup.isEditable());
        List<Section> sections = questionGroup.getSections();
        Assert.assertEquals(2, sections.size());
        Assert.assertEquals("S1", sections.get(0).getName());
        Assert.assertEquals(title + 1, sections.get(0).getQuestions().get(0).getQuestion().getQuestionText());
        Assert.assertEquals("S2", sections.get(1).getName());
        Assert.assertEquals(title + 2, sections.get(1).getQuestions().get(0).getQuestion().getQuestionText());
        verifyCreationDate(questionGroup);
        verifyEventSources(questionGroup);
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetAllActiveQuestions() throws SystemException {
        int initialCountOfQuestions = questionnaireService.getAllActiveQuestions(null).size();
        QuestionDetail questionDetail2 = defineQuestion("Q2" + currentTimeMillis(), QuestionType.FREETEXT, true);
        QuestionDetail questionDetail1 = defineQuestion("Q1" + currentTimeMillis(), QuestionType.NUMERIC, true);
        defineQuestion("Q0" + currentTimeMillis(), QuestionType.NUMERIC, false);
        List<String> expectedOrderTitles = asList(questionDetail1.getText(), questionDetail2.getText());
        List<Integer> expectedOrderIds = asList(questionDetail1.getId(), questionDetail2.getId());
        List<QuestionDetail> questionDetails = questionnaireService.getAllActiveQuestions(null);
        int finalCountOfQuestions = questionDetails.size();
        assertThat(finalCountOfQuestions - initialCountOfQuestions, is(2));
        List<QuestionDetail> actualQuestions = new ArrayList<QuestionDetail>();
        for (QuestionDetail questionDetail : questionDetails) {
            if (expectedOrderIds.contains(questionDetail.getId())) {
                actualQuestions.add(questionDetail);
            }
        }
        assertThat(actualQuestions.get(0).getText(), is(expectedOrderTitles.get(0)));
        assertThat(actualQuestions.get(1).getText(), is(expectedOrderTitles.get(1)));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetAllActiveQuestionsWithoutExcludedQuestions() throws SystemException {
        int initialCountOfQuestions = questionnaireService.getAllActiveQuestions(null).size();
        QuestionDetail questionDetail3 = defineQuestion("Q3" + currentTimeMillis(), QuestionType.DATE, true);
        QuestionDetail questionDetail2 = defineQuestion("Q2" + currentTimeMillis(), QuestionType.FREETEXT, true);
        QuestionDetail questionDetail1 = defineQuestion("Q1" + currentTimeMillis(), QuestionType.NUMERIC, true);
        defineQuestion("Q0" + currentTimeMillis(), QuestionType.NUMERIC, false);
        List<String> expectedOrderTitles = asList(questionDetail1.getText(), questionDetail2.getText());
        List<Integer> expectedOrderIds = asList(questionDetail1.getId(), questionDetail2.getId());
        List<QuestionDetail> questionDetails = questionnaireService.getAllActiveQuestions(asList(questionDetail3.getId()));
        int finalCountOfQuestions = questionDetails.size();
        assertThat(finalCountOfQuestions - initialCountOfQuestions, is(2));
        List<QuestionDetail> actualQuestions = new ArrayList<QuestionDetail>();
        for (QuestionDetail questionDetail : questionDetails) {
            if (expectedOrderIds.contains(questionDetail.getId())) {
                actualQuestions.add(questionDetail);
            }
        }
        assertThat(actualQuestions.get(0).getText(), is(expectedOrderTitles.get(0)));
        assertThat(actualQuestions.get(1).getText(), is(expectedOrderTitles.get(1)));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetAllQuestions() throws SystemException {
        int initialCountOfQuestions = questionnaireService.getAllQuestions().size();
        QuestionDetail questionDetail2 = defineQuestion("Q2" + currentTimeMillis(), QuestionType.FREETEXT, true);
        QuestionDetail questionDetail1 = defineQuestion("Q1" + currentTimeMillis(), QuestionType.NUMERIC, true);
        QuestionDetail questionDetail0 = defineQuestion("Q0" + currentTimeMillis(), QuestionType.NUMERIC, false);
        List<String> expectedOrderTitles = asList(questionDetail0.getText(), questionDetail1.getText(), questionDetail2.getText());
        List<Integer> expectedOrderIds = asList(questionDetail0.getId(), questionDetail1.getId(), questionDetail2.getId());
        List<QuestionDetail> questionDetails = questionnaireService.getAllQuestions();
        int finalCountOfQuestions = questionDetails.size();
        assertThat(finalCountOfQuestions - initialCountOfQuestions, is(3));
        List<QuestionDetail> actualQuestions = new ArrayList<QuestionDetail>();
        for (QuestionDetail questionDetail : questionDetails) {
            if (expectedOrderIds.contains(questionDetail.getId())) {
                actualQuestions.add(questionDetail);
            }
        }
        assertThat(actualQuestions.get(0).getText(), is(expectedOrderTitles.get(0)));
        assertThat(actualQuestions.get(1).getText(), is(expectedOrderTitles.get(1)));
        assertThat(actualQuestions.get(2).getText(), is(expectedOrderTitles.get(2)));
    }

    @SuppressWarnings("unchecked")
    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetAllQuestionGroups() throws SystemException {
        int initialCount = questionnaireService.getAllQuestionGroups().size();
        String questionGroupTitle1 = "QG1" + currentTimeMillis();
        String questionGroupTitle2 = "QG2" + currentTimeMillis();
        List<SectionDetail> sectionsForQG1 = asList(getSection("Section1"));
        defineQuestionGroup(questionGroupTitle1, "Create", "Client", sectionsForQG1, false);
        List<SectionDetail> sectionsForQG2 = asList(getSection("S2"), getSection("Section2"));
        defineQuestionGroup(questionGroupTitle2, "Create", "Client", sectionsForQG2, false);
        List<QuestionGroupDetail> questionGroups = questionnaireService.getAllQuestionGroups();
        int finalCount = questionGroups.size();
        assertThat(finalCount - initialCount, is(2));
        assertThat(questionGroups, Matchers.hasItems(getQuestionGroupDetailMatcher(questionGroupTitle1, "Create", "Client", sectionsForQG1, false), getQuestionGroupDetailMatcher(questionGroupTitle2, "Create", "Client", sectionsForQG2, false)));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetQuestionGroupById() throws SystemException {
        String title = "QG1" + currentTimeMillis();
        List<SectionDetail> details = asList(getSection("S1"), getSection("S2"));
        QuestionGroupDetail createdQuestionGroupDetail = defineQuestionGroup(title, "Create", "Client", details, true);
        QuestionGroupDetail retrievedQuestionGroupDetail = questionnaireService.getQuestionGroup(createdQuestionGroupDetail.getId());
        Assert.assertNotSame(createdQuestionGroupDetail, retrievedQuestionGroupDetail);
        assertThat(retrievedQuestionGroupDetail.getTitle(), is(title));
        List<SectionDetail> sectionDetails = retrievedQuestionGroupDetail.getSectionDetails();
        assertThat(sectionDetails, is(notNullValue()));
        assertThat(sectionDetails.size(), is(2));
        List<SectionDetail> sectionDetailList = retrievedQuestionGroupDetail.getSectionDetails();
        assertThat(sectionDetailList.get(0).getName(), is("S1"));
        assertThat(sectionDetailList.get(1).getName(), is("S2"));
        EventSourceDto eventSourceDto = retrievedQuestionGroupDetail.getEventSources().get(0);
        assertThat(eventSourceDto, is(notNullValue()));
        assertThat(eventSourceDto.getEvent(), is("Create"));
        assertThat(eventSourceDto.getSource(), is("Client"));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetQuestionGroupByIdOrdersQuestionsWithinEverySection() throws SystemException {
        String qgTitle = "QG1" + currentTimeMillis();

        SectionDetail sectionDefinition1 = new SectionDetail();
        sectionDefinition1.setName("Section1");
        String section1Question1 = "Q2_" + currentTimeMillis();
        sectionDefinition1.addQuestion(new SectionQuestionDetail(defineQuestion(section1Question1, QuestionType.FREETEXT, true), false));
        String section1Question2 = "Q1_" + currentTimeMillis();
        sectionDefinition1.addQuestion(new SectionQuestionDetail(defineQuestion(section1Question2, QuestionType.NUMERIC, true), true));
        String section1Question3 = "Q3_" + currentTimeMillis();
        sectionDefinition1.addQuestion(new SectionQuestionDetail(defineQuestion(section1Question3, QuestionType.DATE, true), true));

        SectionDetail sectionDefinition2 = new SectionDetail();
        sectionDefinition2.setName("Section2");
        String section2Question1 = "S2_" + currentTimeMillis();
        sectionDefinition2.addQuestion(new SectionQuestionDetail(defineQuestion(section2Question1, QuestionType.FREETEXT, true), false));
        String section2Question2 = "S3_" + currentTimeMillis();
        sectionDefinition2.addQuestion(new SectionQuestionDetail(defineQuestion(section2Question2, QuestionType.DATE, true), true));
        String section2Question3 = "S1_" + currentTimeMillis();
        sectionDefinition2.addQuestion(new SectionQuestionDetail(defineQuestion(section2Question3, QuestionType.NUMERIC, true), true));

        int questionGroupId = defineQuestionGroup(qgTitle, "Create", "Client", asList(sectionDefinition1, sectionDefinition2), false).getId();
        QuestionGroupDetail questionGroupDetail = questionnaireService.getQuestionGroup(questionGroupId);
        assertThat(questionGroupDetail, Matchers.notNullValue());
        List<SectionDetail> sectionDetails = questionGroupDetail.getSectionDetails();
        assertThat(sectionDetails, Matchers.notNullValue());
        assertThat(sectionDetails.size(), is(2));

        SectionDetail section1 = sectionDetails.get(0);
        assertThat(section1.getName(), is("Section1"));
        List<SectionQuestionDetail> questions1 = section1.getQuestions();
        assertThat(questions1, Matchers.notNullValue());
        assertThat(questions1.size(), is(3));
        assertThat(questions1.get(0).getText(), is(section1Question1));
        assertThat(questions1.get(1).getText(), is(section1Question2));
        assertThat(questions1.get(2).getText(), is(section1Question3));

        SectionDetail section2 = sectionDetails.get(1);
        assertThat(section2.getName(), is("Section2"));
        List<SectionQuestionDetail> questions2 = section2.getQuestions();
        assertThat(questions2, Matchers.notNullValue());
        assertThat(questions2.size(), is(3));
        assertThat(questions2.get(0).getText(), is(section2Question1));
        assertThat(questions2.get(1).getText(), is(section2Question2));
        assertThat(questions2.get(2).getText(), is(section2Question3));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void testGetQuestionGroupByIdFailure() throws SystemException {
        String title = "QG1" + currentTimeMillis();
        QuestionGroupDetail createdQuestionGroupDetail = defineQuestionGroup(title, "Create", "Client", asList(getSection("S1")), true);
        Integer maxQuestionGroupId = createdQuestionGroupDetail.getId();
        try {
            questionnaireService.getQuestionGroup(maxQuestionGroupId + 1);
        } catch (SystemException e) {
            assertThat(e.getKey(), is(QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND));
        }
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetQuestionById() throws SystemException {
        String title = "Q1" + currentTimeMillis();
        QuestionDetail createdQuestionDetail = defineQuestion(title, QuestionType.FREETEXT, true);
        QuestionDetail retrievedQuestionDetail = questionnaireService.getQuestion(createdQuestionDetail.getId());
        Assert.assertNotSame(createdQuestionDetail, retrievedQuestionDetail);
        assertThat(retrievedQuestionDetail.getText(), is(title));
        assertThat(retrievedQuestionDetail.getType(), is(QuestionType.FREETEXT));
    }

    public void shouldGetQuestionWithAnswerChoicesById() throws SystemException {
        String title = "Q1" + currentTimeMillis();
        QuestionDetail createdQuestionDetail = defineQuestion(title, QuestionType.MULTI_SELECT, asList("choice1", "choice2"));
        QuestionDetail retrievedQuestionDetail = questionnaireService.getQuestion(createdQuestionDetail.getId());
        Assert.assertNotSame(createdQuestionDetail, retrievedQuestionDetail);
        assertThat(retrievedQuestionDetail.getText(), is(title));
        assertThat(retrievedQuestionDetail.getType(), is(QuestionType.MULTI_SELECT));
        Assert.assertEquals(retrievedQuestionDetail.getAnswerChoices(), asList("choice1", "choice2"));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void testGetQuestionByIdFailure() throws SystemException {
        String title = "Q1" + currentTimeMillis();
        QuestionDetail createdQuestionDetail = defineQuestion(title, QuestionType.DATE, true);
        Integer maxQuestionId = createdQuestionDetail.getId();
        try {
            questionnaireService.getQuestion(maxQuestionId + 1);
        } catch (SystemException e) {
            assertThat(e.getKey(), is(QuestionnaireConstants.QUESTION_NOT_FOUND));
        }
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldThrowExceptionForDuplicateQuestion() throws SystemException {
        long offset = currentTimeMillis();
        String questionTitle = TITLE + offset;
        defineQuestion(questionTitle, QuestionType.DATE, true);
        try {
            defineQuestion(questionTitle, QuestionType.FREETEXT, true);
            Assert.fail("Exception should have been thrown for duplicate question title");
        } catch (SystemException e) {
            Assert.assertEquals(QuestionnaireConstants.QUESTION_TITLE_DUPLICATE, e.getKey());
        }
    }

    @Test
    @Transactional
    public void testIsDuplicateQuestion() throws SystemException {
        String questionTitle = TITLE + currentTimeMillis();
        boolean result = questionnaireService.isDuplicateQuestionText(questionTitle);
        assertThat(result, is(false));
        defineQuestion(questionTitle, QuestionType.DATE, true);
        result = questionnaireService.isDuplicateQuestionText(questionTitle);
        assertThat(result, is(true));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldRetrieveAllEventSources() {
        List<EventSourceDto> eventSourceDtos = questionnaireService.getAllEventSources();
        assertNotNull(eventSourceDtos);
        assertThat(eventSourceDtos, new EventSourcesMatcher(
                asList(new EventSourceDto("Create", "Client", "Create Client"),
                        new EventSourceDto("View", "Client", "View Client"),
                        new EventSourceDto("Create", "Group", "Create Group"),
                        new EventSourceDto("Approve", "Loan", "Approve Loan"),
                        new EventSourceDto("Close", "Client", "Close Client"),
                        new EventSourceDto("Create", "Loan", "Create Loan"),
                        new EventSourceDto("View", "Loan", "View Loan"),
                        new EventSourceDto("View", "Group", "View Group"),
                        new EventSourceDto("Create", "Center", "Create Center"),
                        new EventSourceDto("View", "Center", "View Center"),
                        new EventSourceDto("Disburse", "Loan", "Disburse Loan"),
                        new EventSourceDto("Create", "Savings", "Create Savings"),
                        new EventSourceDto("View", "Savings", "View Savings"),
                        new EventSourceDto("Create", "Office", "Create Office"),
                        new EventSourceDto("Create", "Personnel", "Create Personnel"),
                        new EventSourceDto("Close", "Loan", "Close Loan"),
                        new EventSourceDto("Close", "Savings", "Close Savings")
                        )));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetQuestionGroupsByEventAndSource() throws SystemException {
        String title = "QG1" + currentTimeMillis();
        List<SectionDetail> details = asList(getSection("S1"), getSection("S2"));
        QuestionGroupDetail expectedQGDetail = defineQuestionGroup(title, "Create", "Client", details, false);
        List<QuestionGroupDetail> questionGroups = questionnaireService.getQuestionGroups(new EventSourceDto("Create", "Client", "Create.Client"));
        assertThat(questionGroups, is(notNullValue()));
        QuestionGroupDetail actualQGDetail = getMatchingQGDetailById(expectedQGDetail.getId(), questionGroups);
        assertThat(actualQGDetail, is(notNullValue()));
        assertThat(actualQGDetail.getTitle(), is(title));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldSaveResponse() throws SystemException {
        String title = "QG1" + currentTimeMillis();
        List<SectionDetail> details = asList(getSection("S1"), getSectionWithSmartSelectQuestion("S2"));
        QuestionGroupDetail questionGroupDetail = defineQuestionGroup(title, "Create", "Client", details, true);
        questionGroupDetail.getSectionDetail(0).getQuestionDetail(0).setValue("1");
        SelectionDetail selectionDetail1 = getSelectionDetail("Ch1", "Tag2");
        SelectionDetail selectionDetail2 = getSelectionDetail("Ch3", "Tag1");
        questionGroupDetail.getSectionDetail(1).getQuestionDetail(0).setSelections(asList(selectionDetail1, selectionDetail2));
        questionnaireService.saveResponses(new QuestionGroupDetails(1, 1, 1, asList(questionGroupDetail)));
        List<QuestionGroupInstance> instances = questionGroupInstanceDao.getDetailsAll();
        assertThat(instances.size(), is(1));
        QuestionGroupInstance instance = getMatchingQuestionGroupInstance(questionGroupDetail.getId(), 1, instances, 0);
        assertThat(instance, is(notNullValue()));
        List<QuestionGroupResponse> groupResponses = instance.getQuestionGroupResponses();
        assertThat(groupResponses.size(), is(3));
        Assert.assertEquals(groupResponses.get(0).getResponse(), "1");
        Assert.assertEquals(groupResponses.get(1).getResponse(), "Ch1");
        Assert.assertEquals(groupResponses.get(1).getTag(), "Tag2");
        Assert.assertEquals(groupResponses.get(2).getResponse(), "Ch3");
        Assert.assertEquals(groupResponses.get(2).getTag(), "Tag1");

        questionGroupDetail.getSectionDetail(0).getQuestionDetail(0).setValue("3");
        questionnaireService.saveResponses(new QuestionGroupDetails(1, 1, 1, asList(questionGroupDetail)));
        instances = questionGroupInstanceDao.getDetailsAll();
        assertThat(instances.size(), is(2));
        instance = getMatchingQuestionGroupInstance(questionGroupDetail.getId(), 1, instances, 1);
        assertThat(instance, is(notNullValue()));
        groupResponses = instance.getQuestionGroupResponses();
        Assert.assertEquals(groupResponses.get(0).getResponse(), "3");
    }

    private SelectionDetail getSelectionDetail(String selectedChoice, String selectedTag) {
        SelectionDetail selectionDetail1 = new SelectionDetail();
        selectionDetail1.setSelectedChoice(selectedChoice);
        selectionDetail1.setSelectedTag(selectedTag);
        return selectionDetail1;
    }

    private QuestionDetail defineQuestionWithSmartSelect(String questionTitle, List<ChoiceDto> choiceDtos) throws SystemException {
        QuestionDetail questionDetail = new QuestionDetail(questionTitle, QuestionType.SMART_SELECT);
        questionDetail.setActive(true);
        questionDetail.setAnswerChoices(choiceDtos);
        return questionnaireService.defineQuestion(questionDetail);
    }

    private SectionDetail getSectionWithSmartSelectQuestion(String name) throws SystemException {
        SectionDetail section = new SectionDetail();
        section.setName(name);
        String questionTitle = "Question" + name + currentTimeMillis();
        ChoiceDto choiceDto1 = getChoiceDto("Ch1", "Tag1", "Tag2", "Tag3");
        ChoiceDto choiceDto2 = getChoiceDto("Ch2", "Tag11", "Tag22", "Tag33");
        ChoiceDto choiceDto3 = getChoiceDto("Ch3", "Tag111", "Tag222", "Tag333");
        QuestionDetail questionDetail = defineQuestionWithSmartSelect(questionTitle, asList(choiceDto1, choiceDto2, choiceDto3));
        section.addQuestion(new SectionQuestionDetail(questionDetail, true));
        return section;
    }

    private ChoiceDto getChoiceDto(String choiceText, String... tags) {
        ChoiceDto choiceDto = new ChoiceDto(choiceText);
        for (String tag : tags) {
            choiceDto.addTag(tag);
        }
        return choiceDto;
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldNotGetInActiveQuestionGroupsByEventAndSource() throws SystemException {
        String title = "QG1" + currentTimeMillis();
        List<SectionDetail> details = asList(getSection("S1"), getSection("S2"));
        QuestionGroupDetail expectedQGDetail = defineQuestionGroup(title, "Create", "Client", details, true);
        setState(expectedQGDetail.getId(), QuestionGroupState.INACTIVE);
        List<QuestionGroupDetail> questionGroups = questionnaireService.getQuestionGroups(new EventSourceDto("Create", "Client", "Create.Client"));
        assertThat(questionGroups, is(notNullValue()));
        QuestionGroupDetail actualQGDetail = getMatchingQGDetailById(expectedQGDetail.getId(), questionGroups);
        assertThat(actualQGDetail, is(Matchers.nullValue()));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldNotGetInQuestionGroupsWithAllInactiveQuestions() throws SystemException {
        String title = "QG1" + currentTimeMillis();
        List<SectionDetail> sectionDetails = asList(getSectionWithAllInactiveQuestions("S1"), getSectionWithAllInactiveQuestions("S2"));
        defineQuestionGroup(title, "Create", "Client", sectionDetails, true);
        List<QuestionGroupDetail> questionGroups = questionnaireService.getQuestionGroups(new EventSourceDto("Create", "Client", "Create.Client"));
        assertThat(questionGroups, is(notNullValue()));
        assertThat(questionGroups.size(), is(0));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldSaveQuestionWithSmartSelectOptionType() {
        String quesTitle = "Ques" + currentTimeMillis();
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setAnswerType(AnswerType.SMARTSELECT);
        questionEntity.setQuestionState(QuestionState.ACTIVE);
        questionEntity.setNickname(quesTitle);
        questionEntity.setQuestionText(quesTitle);
        questionEntity.setChoices(asList(getChoice("Choice1", 0, "Tag1", "Tag2"), getChoice("Choice2", 1, "Tag4")));
        Integer quesId = questionDao.create(questionEntity);
        QuestionEntity newQuestionEntity = questionDao.getDetails(quesId);
        assertThat(newQuestionEntity, is(notNullValue()));
        assertThat(newQuestionEntity.getAnswerTypeAsEnum(), is(AnswerType.SMARTSELECT));
        assertThat(newQuestionEntity.getChoices(), is(notNullValue()));
        assertThat(newQuestionEntity.getChoices().size(), is(2));
        assertThat(newQuestionEntity.getChoices().get(0).getChoiceText(), is("Choice1"));
        assertThat(newQuestionEntity.getChoices().get(0).getTags(), is(notNullValue()));
        assertThat(newQuestionEntity.getChoices().get(0).getTags().size(), is(2));

        assertThat(newQuestionEntity.getChoices().get(1).getChoiceText(), is("Choice2"));
        assertThat(newQuestionEntity.getChoices().get(1).getTags(), is(notNullValue()));
        assertThat(newQuestionEntity.getChoices().get(1).getTags().size(), is(1));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldSaveQuestionGroupFromDto() {
        String ques1Title = "Ques1" + currentTimeMillis();
        String ques2Title = "Ques2" + currentTimeMillis();
        String qgTitle = "QG1" + currentTimeMillis();
        createSingleSelectQuestion(ques2Title);
        QuestionDto question1 = new QuestionDtoBuilder().withText(ques1Title).withMandatory(true).withType(QuestionType.FREETEXT).withOrder(1).build();
        ChoiceDto choice1 = new ChoiceDetailBuilder().withValue("Ch1").withOrder(1).build();
        ChoiceDto choice2 = new ChoiceDetailBuilder().withValue("Ch2").withOrder(2).build();
        ChoiceDto choice3 = new ChoiceDetailBuilder().withValue("Ch3").withOrder(3).build();
        QuestionDto question2 = new QuestionDtoBuilder().withText(ques2Title).withType(QuestionType.SINGLE_SELECT).addChoices(choice1, choice2, choice3).withOrder(2).build();
        SectionDto section1 = new SectionDtoBuilder().withName("Sec1").withOrder(1).addQuestions(question1, question2).build();
        QuestionGroupDto questionGroupDto = new QuestionGroupDtoBuilder().withTitle(qgTitle).withEventSource("Create", "Client").addSections(section1).build();
        Integer questionGroupId = questionnaireService.defineQuestionGroup(questionGroupDto);
        assertQuestionGroup(questionGroupDao.getDetails(questionGroupId), qgTitle, ques1Title, ques2Title);
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldSaveQuestionGroupInstance() {
        String title = "QG1" + currentTimeMillis();
        QuestionGroupDetail qgDetail = defineQuestionGroup(title, "Create", "Client", asList(getSection("S1")), true);
        Integer sectionQuestionId = qgDetail.getSectionDetail(0).getQuestionDetail(0).getId();
        QuestionGroupInstanceDto qgInstanceDto = getQuestionGroupInstanceDto("1234", 121, 567, qgDetail.getId(), sectionQuestionId);
        Integer qgInstanceId = questionnaireService.saveQuestionGroupInstance(qgInstanceDto);
        assertThat(qgInstanceId, is(notNullValue()));
        QuestionGroupInstance instance = questionGroupInstanceDao.getDetails(qgInstanceId);
        assertThat(instance, is(notNullValue()));
        assertThat(instance.getCreatorId(), is(121));
        assertThat(instance.getEntityId(), is(567));
        List<QuestionGroupResponse> questionGroupResponses = instance.getQuestionGroupResponses();
        assertThat(questionGroupResponses, is(notNullValue()));
        assertThat(questionGroupResponses.size(), is(1));
        assertThat(questionGroupResponses.get(0).getResponse(), is("1234"));
    }

    private QuestionGroupInstanceDto getQuestionGroupInstanceDto(String response, Integer creatorId, Integer entityId, Integer questionGroupId, Integer sectionQuestionId) {
        QuestionGroupInstanceDtoBuilder instanceBuilder = new QuestionGroupInstanceDtoBuilder();
        QuestionGroupResponseDtoBuilder responseBuilder = new QuestionGroupResponseDtoBuilder();
        responseBuilder.withResponse(response).withSectionQuestion(sectionQuestionId);
        QuestionGroupResponseDto questionGroupResponseDto = responseBuilder.build();
        instanceBuilder.withQuestionGroup(questionGroupId).withCompleted(true).withCreator(creatorId).withEventSource(1).withEntity(entityId).withVersion(1).addResponses(questionGroupResponseDto);
        return instanceBuilder.build();
    }

    private void createSingleSelectQuestion(String ques1Title) {
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setAnswerType(AnswerType.SINGLESELECT);
        questionEntity.setQuestionState(QuestionState.INACTIVE);
        questionEntity.setNickname(ques1Title);
        questionEntity.setQuestionText(ques1Title);
        questionEntity.setChoices(asList(getChoice("Ch2", 1), getChoice("Ch1", 2), getChoice("Ch3", 3)));
        questionDao.create(questionEntity);
    }

    private void assertQuestionGroup(QuestionGroup questionGroup, String questionGroupTitle, String firstQuestionTitle, String secondQuestionTitle) {
        assertThat(questionGroup, is(notNullValue()));
        assertThat(questionGroup.getTitle(), is(questionGroupTitle));
        Set<EventSourceEntity> eventSources = questionGroup.getEventSources();
        assertThat(eventSources, is(notNullValue()));
        assertThat(eventSources.size(), is(1));
        EventSourceEntity eventSourceEntity = eventSources.toArray(new EventSourceEntity[eventSources.size()])[0];
        assertThat(eventSourceEntity.getEvent().getName(), is("Create"));
        assertThat(eventSourceEntity.getSource().getEntityType(), is("Client"));
        assertThat(questionGroup.getState(), is(QuestionGroupState.ACTIVE));
        List<Section> sections = questionGroup.getSections();
        assertThat(sections, is(notNullValue()));
        assertThat(sections.size(), is(1));
        Section section = sections.get(0);
        assertThat(section.getName(), is("Sec1"));
        List<SectionQuestion> questions = section.getQuestions();
        assertThat(questions, is(notNullValue()));
        assertThat(questions.size(), is(2));

        SectionQuestion sectionQuestion1 = questions.get(0);
        assertThat(sectionQuestion1.getSequenceNumber(), is(1));
        assertThat(sectionQuestion1.getSection(), is(notNullValue()));
        assertThat(sectionQuestion1.getSection().getName(), is("Sec1"));
        assertThat(sectionQuestion1.getSection().getSequenceNumber(), is(1));
        assertThat(sectionQuestion1.getQuestion(), is(notNullValue()));
        assertThat(sectionQuestion1.getQuestion().getQuestionStateAsEnum(), is(QuestionState.ACTIVE));
        assertThat(sectionQuestion1.getQuestion().getQuestionText(), is(firstQuestionTitle));
        assertThat(sectionQuestion1.getQuestion().getAnswerTypeAsEnum(), is(AnswerType.FREETEXT));

        SectionQuestion sectionQuestion2 = questions.get(1);
        assertThat(sectionQuestion2.getSequenceNumber(), is(2));
        assertThat(sectionQuestion2.getSection(), is(notNullValue()));
        assertThat(sectionQuestion2.getSection().getName(), is("Sec1"));
        assertThat(sectionQuestion2.getSection().getSequenceNumber(), is(1));
        assertThat(sectionQuestion2.getQuestion(), is(notNullValue()));
        assertThat(sectionQuestion2.getQuestion().getQuestionStateAsEnum(), is(QuestionState.ACTIVE));
        assertThat(sectionQuestion2.getQuestion().getQuestionText(), is(secondQuestionTitle));
        assertThat(sectionQuestion2.getQuestion().getAnswerTypeAsEnum(), is(AnswerType.SINGLESELECT));
        assertThat(sectionQuestion2.getQuestion().getChoices(), is(notNullValue()));
        assertThat(sectionQuestion2.getQuestion().getChoices().size(), is(3));
        assertThat(sectionQuestion2.getQuestion().getChoices().get(0).getChoiceText(), is("Ch2"));
        assertThat(sectionQuestion2.getQuestion().getChoices().get(0).getChoiceOrder(), is(1));
        assertThat(sectionQuestion2.getQuestion().getChoices().get(1).getChoiceText(), is("Ch1"));
        assertThat(sectionQuestion2.getQuestion().getChoices().get(1).getChoiceOrder(), is(2));
        assertThat(sectionQuestion2.getQuestion().getChoices().get(2).getChoiceText(), is("Ch3"));
        assertThat(sectionQuestion2.getQuestion().getChoices().get(2).getChoiceOrder(), is(3));
    }

    private QuestionChoiceEntity getChoice(String choiceText, int choiceOrder, String... tagTexts) {
        QuestionChoiceEntity questionChoiceEntity = new QuestionChoiceEntity();
        questionChoiceEntity.setChoiceText(choiceText);
        questionChoiceEntity.setChoiceOrder(choiceOrder);
        Set<ChoiceTagEntity> tags = new HashSet<ChoiceTagEntity>();
        for (String tagText : tagTexts) {
            tags.add(getTag(tagText));
        }
        questionChoiceEntity.setTags(tags);
        return questionChoiceEntity;
    }

    private ChoiceTagEntity getTag(String tagText) {
        ChoiceTagEntity choiceTagEntity = new ChoiceTagEntity();
        choiceTagEntity.setTagText(tagText);
        return choiceTagEntity;
    }

    private QuestionGroupInstance getMatchingQuestionGroupInstance(Integer questionGroupId, Integer entityId, List<QuestionGroupInstance> instances, int version) {
        for (QuestionGroupInstance questionGroupInstance : instances) {
            if (questionGroupInstance.getQuestionGroup().getId() == questionGroupId &&
                    questionGroupInstance.getEntityId() == entityId && questionGroupInstance.getVersionNum() == version) {
                return questionGroupInstance;
            }
        }
        return null;
    }

    private void setState(Integer id, QuestionGroupState questionGroupState) {
        QuestionGroup questionGroup = questionGroupDao.getDetails(id);
        questionGroup.setState(questionGroupState);
        questionGroupDao.update(questionGroup);
    }

    private QuestionDetail defineQuestion(String questionTitle, QuestionType questionType, boolean active) throws SystemException {
        QuestionDetail questionDetail = new QuestionDetail(questionTitle, questionType);
        questionDetail.setActive(active);
        return questionnaireService.defineQuestion(questionDetail);
    }

    private QuestionDetail defineQuestion(String questionTitle, QuestionType type, List<String> choices) throws SystemException {
        QuestionDetail questionDetail = new QuestionDetail(questionTitle, type);
        List<ChoiceDto> choiceDtos = getChoiceDetails(choices);
        questionDetail.setAnswerChoices(choiceDtos);
        return questionnaireService.defineQuestion(questionDetail);
    }

    private List<ChoiceDto> getChoiceDetails(List<String> choices) {
        List<ChoiceDto> choiceDtos = new ArrayList<ChoiceDto>();
        for (String choice : choices) {
            choiceDtos.add(new ChoiceDto(choice));
        }
        return choiceDtos;
    }

    private QuestionGroupDetail defineQuestionGroup(String title, String event, String source, List<SectionDetail> sectionDetails, boolean editable) throws SystemException {
        return questionnaireService.defineQuestionGroup(new QuestionGroupDetail(0, title, Arrays.asList(new EventSourceDto(event, source, null)), sectionDetails, editable, true));
    }

    private SectionDetail getSection(String name) throws SystemException {
        SectionDetail section = new SectionDetail();
        section.setName(name);
        String questionTitle = "Question" + name + currentTimeMillis();
        QuestionDetail questionDetail = defineQuestion(questionTitle, QuestionType.NUMERIC, true);
        section.addQuestion(new SectionQuestionDetail(questionDetail, true));
        return section;
    }

    private SectionDetail getSectionWithAllInactiveQuestions(String name) throws SystemException {
        SectionDetail section = new SectionDetail();
        section.setName(name);
        String questionTitle = "Question" + name + currentTimeMillis();
        QuestionDetail questionDetail = defineQuestion(questionTitle, QuestionType.NUMERIC, false);
        section.addQuestion(new SectionQuestionDetail(questionDetail, true));
        return section;
    }

    private SectionDetail getSectionWithQuestionId(String name, QuestionDetail questionDetail) throws SystemException {
        SectionDetail section = new SectionDetail();
        section.setName(name);
        section.addQuestion(new SectionQuestionDetail(questionDetail, true));
        return section;
    }

    private void verifyCreationDate(QuestionGroup questionGroup) {
        Calendar creationDate = Calendar.getInstance();
        creationDate.setTime(questionGroup.getDateOfCreation());
        Calendar currentDate = Calendar.getInstance();
        assertThat(creationDate.get(Calendar.DATE), is(currentDate.get(Calendar.DATE)));
        assertThat(creationDate.get(Calendar.MONTH), is(currentDate.get(Calendar.MONTH)));
        assertThat(creationDate.get(Calendar.YEAR), is(currentDate.get(Calendar.YEAR)));
    }

    private void verifyEventSources(QuestionGroup questionGroup) {
        Set<EventSourceEntity> eventSources = questionGroup.getEventSources();
        assertNotNull(eventSources);
        Assert.assertEquals(1, eventSources.size());
        EventSourceEntity eventSourceEntity = eventSources.toArray(new EventSourceEntity[eventSources.size()])[0];
        Assert.assertEquals("Create", eventSourceEntity.getEvent().getName());
        Assert.assertEquals("Client", eventSourceEntity.getSource().getEntityType());
        Assert.assertEquals("Create Client", eventSourceEntity.getDescription());
    }

    private QuestionGroupDetailMatcher getQuestionGroupDetailMatcher(String questionGroupTitle,  String event, String source, List<SectionDetail> sectionDetails, boolean editable) {
        return new QuestionGroupDetailMatcher(new QuestionGroupDetail(0, questionGroupTitle, Arrays.asList(new EventSourceDto(event, source, null)), sectionDetails, editable, true));
    }

    private QuestionGroupDetail getMatchingQGDetailById(Integer expectedId, List<QuestionGroupDetail> questionGroups) {
        QuestionGroupDetail actualQGDetail = null;
        for (QuestionGroupDetail questionGroupDetail : questionGroups) {
            if (questionGroupDetail.getId().equals(expectedId)) {
                actualQGDetail = questionGroupDetail;
            }
        }
        return actualQGDetail;
    }
}
