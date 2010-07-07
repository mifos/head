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
import org.mifos.customers.surveys.business.Question;
import org.mifos.customers.surveys.helpers.QuestionState;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.contract.QuestionDefinition;
import org.mifos.platform.questionnaire.contract.QuestionDetail;
import org.mifos.platform.questionnaire.contract.QuestionType;
import org.mifos.platform.questionnaire.contract.QuestionnaireService;
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
import static org.mifos.platform.questionnaire.contract.QuestionType.DATE;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/org/mifos/config/resources/QuestionnaireContext.xml", "/org/mifos/config/resources/persistenceContext.xml", "/test-dataSourceContext.xml"})
@TransactionConfiguration(transactionManager = "platformTransactionManager", defaultRollback = true)
public class QuestionDaoIntegrationTest {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private QuestionnaireService questionnaireService;

    @Test
    @Transactional
    public void testCountOfQuestionsWithTitle() throws ApplicationException {
        String questionTitle = "Title" + System.currentTimeMillis();
        List result = questionDao.retrieveCountOfQuestionsWithTitle(questionTitle);
        assertEquals((long) 0, result.get(0));
        defineQuestion(questionTitle, DATE);
        result = questionDao.retrieveCountOfQuestionsWithTitle(questionTitle);
        assertEquals((long) 1, result.get(0));
    }

    @Test
    @Transactional
    public void testRetrieveByState() throws ApplicationException {
        QuestionDetail questionDetail2 = defineQuestion("Title2" + System.currentTimeMillis(), QuestionType.NUMERIC);
        QuestionDetail questionDetail1 = defineQuestion("Title1" + System.currentTimeMillis(), QuestionType.NUMERIC);
        List list = questionDao.retrieveByState(QuestionState.ACTIVE.getValue());
        List<Integer> expectedIds = Arrays.asList(questionDetail1.getId(), questionDetail2.getId());
        List<String> expectedTitles = Arrays.asList(questionDetail1.getShortName(), questionDetail2.getShortName());
        List<Question> actualQuestions = new ArrayList<Question>();
        for (Object obj : list) {
            Question question = (Question) obj;
            if (expectedIds.contains(question.getQuestionId()))
                actualQuestions.add(question);
        }
        assertThat(actualQuestions.size(), is(2));
        assertThat(actualQuestions.get(0).getShortName(), is(expectedTitles.get(0)));
        assertThat(actualQuestions.get(1).getShortName(), is(expectedTitles.get(1)));
    }

    private QuestionDetail defineQuestion(String questionTitle, QuestionType questionType) throws ApplicationException {
        return questionnaireService.defineQuestion(new QuestionDefinition(questionTitle, questionType));
    }
}
