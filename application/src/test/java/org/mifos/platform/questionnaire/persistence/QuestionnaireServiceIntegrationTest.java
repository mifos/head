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

package org.mifos.platform.questionnaire.persistence;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.customers.surveys.business.Question;
import org.mifos.customers.surveys.helpers.AnswerType;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.contract.*;
import org.mifos.platform.questionnaire.domain.EventSourceEntity;
import org.mifos.platform.questionnaire.domain.QuestionGroup;
import org.mifos.platform.questionnaire.domain.QuestionGroupState;
import org.mifos.platform.questionnaire.domain.Section;
import org.mifos.platform.questionnaire.matchers.QuestionGroupDetailMatcher;
import org.mifos.test.matchers.EventSourceMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.mifos.platform.questionnaire.contract.QuestionType.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/org/mifos/config/resources/QuestionnaireContext.xml", "/org/mifos/config/resources/persistenceContext.xml", "/test-dataSourceContext.xml"})
@TransactionConfiguration(transactionManager = "platformTransactionManager", defaultRollback = true)
public class QuestionnaireServiceIntegrationTest {

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private QuestionGroupDao questionGroupDao;
    public static final String TITLE = "Title";

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldDefineQuestion() throws ApplicationException {
        String questionTitle = TITLE + System.currentTimeMillis();
        QuestionDetail questionDetail = defineQuestion(questionTitle, DATE);
        assertNotNull(questionDetail);
        Integer questionId = questionDetail.getId();
        assertNotNull(questionId);
        Question questionEntity = questionDao.getDetails(questionId);
        assertNotNull(questionEntity);
        assertEquals(questionTitle, questionEntity.getShortName());
        assertEquals(questionTitle, questionEntity.getQuestionText());
        assertEquals(AnswerType.DATE, questionEntity.getAnswerTypeAsEnum());
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldDefineQuestionGroup() throws ApplicationException {
        String title = TITLE + System.currentTimeMillis();
        QuestionDetail questionDetail1 = defineQuestion(title + 1, NUMERIC);
        QuestionDetail questionDetail2 = defineQuestion(title + 2, FREETEXT);
        SectionDefinition section1 = getSectionWithQuestionId("S1", questionDetail1.getId());
        SectionDefinition section2 = getSectionWithQuestionId("S2", questionDetail2.getId());
        QuestionGroupDetail questionGroupDetail = defineQuestionGroup(title, "Create", "Client", asList(section1, section2));
        assertNotNull(questionGroupDetail);
        Integer questionGroupId = questionGroupDetail.getId();
        assertNotNull(questionGroupId);
        QuestionGroup questionGroup = questionGroupDao.getDetails(questionGroupId);
        assertNotNull(questionGroup);
        assertEquals(title, questionGroup.getTitle());
        assertEquals(QuestionGroupState.ACTIVE, questionGroup.getState());
        List<Section> sections = questionGroup.getSections();
        assertEquals(2, sections.size());
        assertEquals("S1", sections.get(0).getName());
        assertEquals(title + 1, sections.get(0).getQuestions().get(0).getQuestion().getShortName());
        assertEquals("S2", sections.get(1).getName());
        assertEquals(title + 2, sections.get(1).getQuestions().get(0).getQuestion().getShortName());
        verifyCreationDate(questionGroup);
        verifyEventSources(questionGroup);
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetAllQuestions() throws ApplicationException {
        int initialCountOfQuestions = questionnaireService.getAllQuestions().size();
        QuestionDetail questionDetail2 = defineQuestion("Q2" + System.currentTimeMillis(), FREETEXT);
        QuestionDetail questionDetail1 = defineQuestion("Q1" + System.currentTimeMillis(), NUMERIC);
        List<String> expectedOrderTitles = Arrays.asList(questionDetail1.getShortName(), questionDetail2.getShortName());
        List<Integer> expectedOrderIds = Arrays.asList(questionDetail1.getId(), questionDetail2.getId());
        List<QuestionDetail> questionDetails = questionnaireService.getAllQuestions();
        int finalCountOfQuestions = questionDetails.size();
        assertThat(finalCountOfQuestions - initialCountOfQuestions, is(2));
        List<QuestionDetail> actualQuestions = new ArrayList<QuestionDetail>();
        for (QuestionDetail questionDetail : questionDetails) {
            if (expectedOrderIds.contains(questionDetail.getId()))
                actualQuestions.add(questionDetail);
        }
        assertThat(actualQuestions.get(0).getShortName(), is(expectedOrderTitles.get(0)));
        assertThat(actualQuestions.get(1).getShortName(), is(expectedOrderTitles.get(1)));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetAllQuestionGroups() throws ApplicationException {
        int initialCount = questionnaireService.getAllQuestionGroups().size();
        String questionGroupTitle1 = "QG1" + System.currentTimeMillis();
        String questionGroupTitle2 = "QG2" + System.currentTimeMillis();
        List<SectionDefinition> sectionsForQG1 = asList(getSection("Section1"));
        defineQuestionGroup(questionGroupTitle1, "Create", "Client", sectionsForQG1);
        List<SectionDefinition> sectionsForQG2 = asList(getSection("S2"), getSection("Section2"));
        defineQuestionGroup(questionGroupTitle2, "Create", "Client", sectionsForQG2);
        List<QuestionGroupDetail> questionGroups = questionnaireService.getAllQuestionGroups();
        int finalCount = questionGroups.size();
        assertThat(finalCount - initialCount, is(2));
        assertThat(questionGroups, hasItems(getQuestionGroupDetailMatcher(questionGroupTitle1, sectionsForQG1),
                getQuestionGroupDetailMatcher(questionGroupTitle2, sectionsForQG2)));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetQuestionGroupById() throws ApplicationException {
        String title = "QG1" + System.currentTimeMillis();
        List<SectionDefinition> definitions = asList(getSection("S1"), getSection("S2"));
        QuestionGroupDetail createdQuestionGroupDetail = defineQuestionGroup(title, "Create", "Client", definitions);
        QuestionGroupDetail retrievedQuestionGroupDetail = questionnaireService.getQuestionGroup(createdQuestionGroupDetail.getId());
        assertNotSame(createdQuestionGroupDetail, retrievedQuestionGroupDetail);
        assertThat(retrievedQuestionGroupDetail.getTitle(), is(title));
        List<SectionDefinition> sectionDefinitions = retrievedQuestionGroupDetail.getSectionDefinitions();
        assertThat(sectionDefinitions, is(not(nullValue())));
        assertThat(sectionDefinitions.size(), is(2));
        List<SectionDefinition> sectionDefinitionList = retrievedQuestionGroupDetail.getSectionDefinitions();
        assertThat(sectionDefinitionList.get(0).getName(), is("S1"));
        assertThat(sectionDefinitionList.get(1).getName(), is("S2"));
        EventSource eventSource = retrievedQuestionGroupDetail.getEventSource();
        assertThat(eventSource, is(not(nullValue())));
        assertThat(eventSource.getEvent(), is("Create"));
        assertThat(eventSource.getSource(), is("Client"));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetQuestionGroupByIdOrdersQuestionsWithinEverySection() throws ApplicationException {
        String qgTitle = "QG1" + System.currentTimeMillis();

        SectionDefinition sectionDefinition1 = new SectionDefinition();
        sectionDefinition1.setName("Section1");
        String section1Question1 = "Q2_" + System.currentTimeMillis();
        sectionDefinition1.addQuestion(new SectionQuestionDetail(defineQuestion(section1Question1, FREETEXT).getId(), false));
        String section1Question2 = "Q1_" + System.currentTimeMillis();
        sectionDefinition1.addQuestion(new SectionQuestionDetail(defineQuestion(section1Question2, NUMERIC).getId(), true));
        String section1Question3 = "Q3_" + System.currentTimeMillis();
        sectionDefinition1.addQuestion(new SectionQuestionDetail(defineQuestion(section1Question3, DATE).getId(), true));

        SectionDefinition sectionDefinition2 = new SectionDefinition();
        sectionDefinition2.setName("Section2");
        String section2Question1 = "S2_" + System.currentTimeMillis();
        sectionDefinition2.addQuestion(new SectionQuestionDetail(defineQuestion(section2Question1, FREETEXT).getId(), false));
        String section2Question2 = "S3_" + System.currentTimeMillis();
        sectionDefinition2.addQuestion(new SectionQuestionDetail(defineQuestion(section2Question2, DATE).getId(), true));
        String section2Question3 = "S1_" + System.currentTimeMillis();
        sectionDefinition2.addQuestion(new SectionQuestionDetail(defineQuestion(section2Question3, NUMERIC).getId(), true));

        int questionGroupId = defineQuestionGroup(qgTitle, "Create", "Client", asList(sectionDefinition1, sectionDefinition2)).getId();
        QuestionGroupDetail questionGroupDetail = questionnaireService.getQuestionGroup(questionGroupId);
        assertThat(questionGroupDetail, notNullValue());
        List<SectionDefinition> sectionDefinitions = questionGroupDetail.getSectionDefinitions();
        assertThat(sectionDefinitions, notNullValue());
        assertThat(sectionDefinitions.size(), is(2));

        SectionDefinition section1 = sectionDefinitions.get(0);
        assertThat(section1.getName(), is("Section1"));
        List<SectionQuestionDetail> questions1 = section1.getQuestions();
        assertThat(questions1, notNullValue());
        assertThat(questions1.size(), is(3));
        assertThat(questions1.get(0).getTitle(), is(section1Question1));
        assertThat(questions1.get(1).getTitle(), is(section1Question2));
        assertThat(questions1.get(2).getTitle(), is(section1Question3));

        SectionDefinition section2 = sectionDefinitions.get(1);
        assertThat(section2.getName(), is("Section2"));
        List<SectionQuestionDetail> questions2 = section2.getQuestions();
        assertThat(questions2, notNullValue());
        assertThat(questions2.size(), is(3));
        assertThat(questions2.get(0).getTitle(), is(section2Question1));
        assertThat(questions2.get(1).getTitle(), is(section2Question2));
        assertThat(questions2.get(2).getTitle(), is(section2Question3));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void testGetQuestionGroupByIdFailure() throws ApplicationException {
        String title = "QG1" + System.currentTimeMillis();
        QuestionGroupDetail createdQuestionGroupDetail = defineQuestionGroup(title, "Create", "Client", asList(getSection("S1")));
        Integer maxQuestionGroupId = createdQuestionGroupDetail.getId();
        try {
            questionnaireService.getQuestionGroup(maxQuestionGroupId + 1);
        } catch (ApplicationException e) {
            assertThat(e.getKey(), is(QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND));
        }
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetQuestionById() throws ApplicationException {
        String title = "Q1" + System.currentTimeMillis();
        QuestionDetail createdQuestionDetail = defineQuestion(title, QuestionType.FREETEXT);
        QuestionDetail retrievedQuestionDetail = questionnaireService.getQuestion(createdQuestionDetail.getId());
        assertNotSame(createdQuestionDetail, retrievedQuestionDetail);
        assertThat(retrievedQuestionDetail.getText(), is(title));
        assertThat(retrievedQuestionDetail.getShortName(), is(title));
        assertThat(retrievedQuestionDetail.getType(), is(QuestionType.FREETEXT));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void testGetQuestionByIdFailure() throws ApplicationException {
        String title = "Q1" + System.currentTimeMillis();
        QuestionDetail createdQuestionDetail = defineQuestion(title, DATE);
        Integer maxQuestionId = createdQuestionDetail.getId();
        try {
            questionnaireService.getQuestion(maxQuestionId + 1);
        } catch (ApplicationException e) {
            assertThat(e.getKey(), is(QuestionnaireConstants.QUESTION_NOT_FOUND));
        }
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldThrowExceptionForDuplicateQuestion() throws ApplicationException {
        long offset = System.currentTimeMillis();
        String questionTitle = TITLE + offset;
        defineQuestion(questionTitle, DATE);
        try {
            defineQuestion(questionTitle, FREETEXT);
            Assert.fail("Exception should have been thrown for duplicate question title");
        } catch (ApplicationException e) {
            assertEquals(QuestionnaireConstants.DUPLICATE_QUESTION, e.getKey());
        }
    }

    @Test
    @Transactional
    public void testIsDuplicateQuestion() throws ApplicationException {
        String questionTitle = TITLE + System.currentTimeMillis();
        boolean result = questionnaireService.isDuplicateQuestion(new QuestionDefinition(questionTitle, FREETEXT));
        assertThat(result, is(false));
        defineQuestion(questionTitle, DATE);
        result = questionnaireService.isDuplicateQuestion(new QuestionDefinition(questionTitle, FREETEXT));
        assertThat(result, is(true));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldRetrieveAllEventSources() {
        List<EventSource> eventSources = questionnaireService.getAllEventSources();
        assertNotNull(eventSources);
        assertThat(eventSources, new EventSourceMatcher("Create", "Client", "Create Client"));
    }

    private QuestionDetail defineQuestion(String questionTitle, QuestionType questionType) throws ApplicationException {
        return questionnaireService.defineQuestion(new QuestionDefinition(questionTitle, questionType));
    }

    private QuestionGroupDetail defineQuestionGroup(String title, String event, String source, List<SectionDefinition> sectionDefinitions) throws ApplicationException {
        return questionnaireService.defineQuestionGroup(new QuestionGroupDefinition(title, new EventSource(event, source, null), sectionDefinitions));
    }

    private SectionDefinition getSection(String name) throws ApplicationException {
        SectionDefinition section = new SectionDefinition();
        section.setName(name);
        String questionTitle = "Question" + name + System.currentTimeMillis();
        section.addQuestion(new SectionQuestionDetail(defineQuestion(questionTitle, NUMERIC).getId(), questionTitle, true));
        return section;
    }

    private SectionDefinition getSectionWithQuestionId(String name, int questionId) throws ApplicationException {
        SectionDefinition section = new SectionDefinition();
        section.setName(name);
        section.addQuestion(new SectionQuestionDetail(questionId, true));
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
        assertEquals(1, eventSources.size());
        EventSourceEntity eventSourceEntity = eventSources.toArray(new EventSourceEntity[eventSources.size()])[0];
        assertEquals("Create", eventSourceEntity.getEvent().getName());
        assertEquals("Client", eventSourceEntity.getSource().getEntityType());
        assertEquals("Create Client", eventSourceEntity.getDescription());
    }

    private QuestionGroupDetailMatcher getQuestionGroupDetailMatcher(String questionGroupTitle, List<SectionDefinition> sectionDefinitions) {
        return new QuestionGroupDetailMatcher(new QuestionGroupDetail(0, questionGroupTitle, sectionDefinitions));
    }
}


