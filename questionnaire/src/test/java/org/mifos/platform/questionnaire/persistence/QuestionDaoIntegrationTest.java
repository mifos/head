/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.platform.questionnaire.persistence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.domain.QuestionEntity;
import org.mifos.platform.questionnaire.domain.QuestionState;
import org.mifos.platform.questionnaire.domain.QuestionnaireService;
import org.mifos.platform.questionnaire.service.DateQuestionTypeDto;
import org.mifos.platform.questionnaire.service.NumericQuestionTypeDto;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionTypeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test-questionnaire-dbContext.xml", "/test-questionnaire-persistenceContext.xml", "/META-INF/spring/QuestionnaireContext.xml"})
@TransactionConfiguration(transactionManager = "platformTransactionManager", defaultRollback = true)
@SuppressWarnings("PMD")
public class QuestionDaoIntegrationTest {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private QuestionnaireService questionnaireService;

    @Test
    @Transactional
    public void testCountOfQuestionsWithTitle() throws SystemException {
        String questionTitle = "Title" + System.currentTimeMillis();
        List result = questionDao.retrieveCountOfQuestionsWithTitle(questionTitle);
        assertEquals((long) 0, result.get(0));
        defineQuestion(questionTitle, new DateQuestionTypeDto());
        result = questionDao.retrieveCountOfQuestionsWithTitle(questionTitle);
        assertEquals((long) 1, result.get(0));
    }

    @Test
    @Transactional
    public void testRetrieveByState() throws SystemException {
        NumericQuestionTypeDto numeric = new NumericQuestionTypeDto();
        QuestionDetail questionDetail2 = defineQuestion("Title2" + System.currentTimeMillis(), numeric);
        QuestionDetail questionDetail1 = defineQuestion("Title1" + System.currentTimeMillis(), numeric);
        List<QuestionEntity> list = questionDao.retrieveByState(QuestionState.ACTIVE.getValue());
        List<Integer> expectedIds = Arrays.asList(questionDetail1.getId(), questionDetail2.getId());
        List<String> expectedTitles = Arrays.asList(questionDetail1.getShortName(), questionDetail2.getShortName());
        List<QuestionEntity> actualQuestions = new ArrayList<QuestionEntity>();
        for (QuestionEntity question : list) {
            if (expectedIds.contains(question.getQuestionId()))
                actualQuestions.add(question);
        }
        assertThat(actualQuestions.size(), is(2));
        assertThat(actualQuestions.get(0).getShortName(), is(expectedTitles.get(0)));
        assertThat(actualQuestions.get(1).getShortName(), is(expectedTitles.get(1)));
    }

    private QuestionDetail defineQuestion(String questionTitle, QuestionTypeDto questionTypeDto) throws SystemException {
        return questionnaireService.defineQuestion(new QuestionDetail(questionTitle, questionTypeDto));
    }
}
