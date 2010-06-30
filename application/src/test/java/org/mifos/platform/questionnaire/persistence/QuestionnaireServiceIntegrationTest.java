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
import org.mifos.platform.questionnaire.domain.QuestionGroup;
import org.mifos.platform.questionnaire.domain.QuestionGroupState;
import org.mifos.test.matchers.HasThisKindOfEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
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

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldDefineQuestion() throws ApplicationException {
        String questionTitle = "Title" + System.currentTimeMillis();
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
        String questionTitle = "Title" + System.currentTimeMillis();
        QuestionGroupDetail questionGroupDetail = defineQuestionGroup(questionTitle);
        assertNotNull(questionGroupDetail);
        Integer questionGroupId = questionGroupDetail.getId();
        assertNotNull(questionGroupId);
        QuestionGroup questionGroup = questionGroupDao.getDetails(questionGroupId);
        assertNotNull(questionGroup);
        assertEquals(questionTitle, questionGroup.getTitle());
        assertEquals(QuestionGroupState.ACTIVE, questionGroup.getState());
        verifyCreationDate(questionGroup);
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetAllQuestions() throws ApplicationException {
        int initialCountOfQuestions = questionnaireService.getAllQuestions().size();
        defineQuestion("Q1" + System.currentTimeMillis(), NUMERIC);
        defineQuestion("Q2" + System.currentTimeMillis(), FREETEXT);
        int finalCountOfQuestions = questionnaireService.getAllQuestions().size();
        assertThat(finalCountOfQuestions - initialCountOfQuestions, is(2));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetAllQuestionGroups() throws ApplicationException {
        int initialCount = questionnaireService.getAllQuestionGroups().size();
        defineQuestionGroup("QG1" + System.currentTimeMillis());
        defineQuestionGroup("QG2" + System.currentTimeMillis());
        int finalCount = questionnaireService.getAllQuestionGroups().size();
        assertThat(finalCount - initialCount, is(2));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetQuestionGroupById() throws ApplicationException {
        String title = "QG1" + System.currentTimeMillis();
        QuestionGroupDetail createdQuestionGroupDetail = defineQuestionGroup(title);
        QuestionGroupDetail retrievedQuestionGroupDetail = questionnaireService.getQuestionGroup(createdQuestionGroupDetail.getId());
        assertNotSame(createdQuestionGroupDetail, retrievedQuestionGroupDetail);
        assertThat(retrievedQuestionGroupDetail.getTitle(), is(title));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void testGetQuestionGroupByIdFailure() throws ApplicationException {
        String title = "QG1" + System.currentTimeMillis();
        QuestionGroupDetail createdQuestionGroupDetail = defineQuestionGroup(title);
        Integer maxQuestionGroupId = createdQuestionGroupDetail.getId();
        try {
            questionnaireService.getQuestionGroup(maxQuestionGroupId+1);
        } catch (ApplicationException e) {
            assertThat(e.getKey(), is(QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND));
        }
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetQuestionById() throws ApplicationException {
        String title = "Q1" + System.currentTimeMillis();
        QuestionDetail createdQuestionDetail = defineQuestion(title, QuestionType.FREETEXT);
        QuestionDetail retrievedQuestionDetail = questionnaireService.getQuestion(createdQuestionDetail .getId());
        assertNotSame(createdQuestionDetail , retrievedQuestionDetail);
        assertThat(retrievedQuestionDetail.getText(), is(title));
        assertThat(retrievedQuestionDetail.getShortName(), is(title));
        assertThat(retrievedQuestionDetail.getType(), is(QuestionType.FREETEXT));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void testGetQuestionByIdFailure() throws ApplicationException {
        String title = "Q1" + System.currentTimeMillis();
        QuestionDetail createdQuestionDetail = defineQuestion(title, QuestionType.DATE);
        Integer maxQuestionId = createdQuestionDetail.getId();
        try {
            questionnaireService.getQuestion(maxQuestionId+1);
        } catch (ApplicationException e) {
            assertThat(e.getKey(), is(QuestionnaireConstants.QUESTION_NOT_FOUND));
        }
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldThrowExceptionForDuplicateQuestion() throws ApplicationException {
        long offset = System.currentTimeMillis();
        String questionTitle = "Title" + offset;
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
        String questionTitle = "Title" + System.currentTimeMillis();
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
        assertThat(eventSources, new HasThisKindOfEvent("Create", "Client", "Create Client"));
        assertThat(eventSources, new HasThisKindOfEvent("View", "Client", "View Client"));
    }

    private QuestionDetail defineQuestion(String questionTitle, QuestionType questionType) throws ApplicationException {
        return questionnaireService.defineQuestion(new QuestionDefinition(questionTitle, questionType));
    }

    private QuestionGroupDetail defineQuestionGroup(String title) throws ApplicationException {
        return questionnaireService.defineQuestionGroup(new QuestionGroupDefinition(title));
    }

    private void verifyCreationDate(QuestionGroup questionGroup) {
        Calendar creationDate = Calendar.getInstance();
        creationDate.setTime(questionGroup.getDateOfCreation());
        Calendar currentDate = Calendar.getInstance();
        assertThat(creationDate.get(Calendar.DATE), is(currentDate.get(Calendar.DATE)));
        assertThat(creationDate.get(Calendar.MONTH), is(currentDate.get(Calendar.MONTH)));
        assertThat(creationDate.get(Calendar.YEAR), is(currentDate.get(Calendar.YEAR)));
    }
}
