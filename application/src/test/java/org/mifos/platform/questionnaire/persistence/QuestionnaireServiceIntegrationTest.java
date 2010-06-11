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
import org.mifos.platform.questionnaire.contract.QuestionDefinition;
import org.mifos.platform.questionnaire.contract.QuestionDetail;
import org.mifos.platform.questionnaire.contract.QuestionType;
import org.mifos.platform.questionnaire.contract.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mifos.platform.questionnaire.contract.QuestionType.FREETEXT;
import static org.mifos.platform.questionnaire.contract.QuestionType.NUMERIC;
import static org.mifos.platform.questionnaire.contract.QuestionType.DATE;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/org/mifos/config/resources/QuestionnaireContext.xml", "/test-persistenceContext.xml"})
@TransactionConfiguration(transactionManager = "platformTransactionManager", defaultRollback = true)
public class QuestionnaireServiceIntegrationTest {

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private QuestionnaireDao questionnaireDao;

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldDefineQuestion() throws ApplicationException {
        String questionTitle = "Title" + System.currentTimeMillis();
        QuestionDetail questionDetail = defineQuestion(questionTitle, DATE);
        assertNotNull(questionDetail);
        Integer questionId = questionDetail.getId();
        assertNotNull(questionId);
        Question questionEntity = questionnaireDao.getDetails(questionId);
        assertNotNull(questionEntity);
        assertEquals(questionTitle, questionEntity.getShortName());
        assertEquals(questionTitle, questionEntity.getQuestionText());
        assertEquals(AnswerType.DATE, questionEntity.getAnswerTypeAsEnum());
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetAllQuestions() throws ApplicationException {
        int initialCountOfQuestions = questionnaireService.getAllQuestions().size();
        defineQuestion("Q2" + System.currentTimeMillis(), FREETEXT);
        defineQuestion("Q1" + System.currentTimeMillis(), NUMERIC);
        int finalCountOfQuestions = questionnaireService.getAllQuestions().size();
        assertThat(finalCountOfQuestions - initialCountOfQuestions, is(2));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldThrowExceptionForDupliacateQuestion() throws ApplicationException {
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

    private QuestionDetail defineQuestion(String questionTitle, QuestionType questionType) throws ApplicationException {
        QuestionDefinition questionDefinition = new QuestionDefinition(questionTitle, questionType);
        QuestionDetail questionDetail = questionnaireService.defineQuestion(questionDefinition);
        return questionDetail;
    }
}
