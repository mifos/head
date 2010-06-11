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

package org.mifos.platform.questionnaire;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.customers.surveys.business.Question;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.contract.QuestionDefinition;
import org.mifos.platform.questionnaire.contract.QuestionDetail;
import org.mifos.platform.questionnaire.contract.QuestionnaireService;
import org.mifos.platform.questionnaire.mappers.QuestionnaireMapperImpl;
import org.mifos.platform.questionnaire.persistence.QuestionnaireDao;
import org.mifos.platform.questionnaire.validators.QuestionValidator;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.QUESTION_TITLE_NOT_PROVIDED;
import static org.mifos.platform.questionnaire.contract.QuestionType.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QuestionnaireServiceTest {

    private QuestionnaireService questionnaireService;

    @Mock
    private QuestionValidator questionValidator;

    @Mock
    private QuestionnaireDao questionnaireDao;

    private QuestionnaireMapperImpl questionnaireMapper = new QuestionnaireMapperImpl();

    private static final String QUESTION_TITLE = "Test QuestionDetail Title";

    @Before
    public void setUp() {
        questionnaireService = new QuestionnaireServiceImpl(questionValidator, questionnaireDao, questionnaireMapper);
    }

    @Test
    public void shouldDefineQuestion() throws ApplicationException {
        QuestionDefinition questionDefinition = new QuestionDefinition(QUESTION_TITLE, FREETEXT);
        try {
            QuestionDetail questionDetail = questionnaireService.defineQuestion(questionDefinition);
            verify(questionnaireDao, times(1)).create(any(Question.class));
            assertNotNull(questionDetail);
            assertEquals(QUESTION_TITLE, questionDetail.getText());
            assertEquals(QUESTION_TITLE, questionDetail.getShortName());
            assertEquals(FREETEXT, questionDetail.getType());
        } catch (ApplicationException e) {
            fail("Should not have thrown the validation exception");
        }
        verify(questionValidator).validate(questionDefinition);
        verify(questionnaireDao).create(any(org.mifos.customers.surveys.business.Question.class));
    }

    @Test(expected = ApplicationException.class)
    public void shouldThrowValidationExceptionWhenQuestionTitleIsNull() throws ApplicationException {
        QuestionDefinition questionDefinition = new QuestionDefinition(null, INVALID);
        doThrow(new ApplicationException(QUESTION_TITLE_NOT_PROVIDED)).when(questionValidator).validate(questionDefinition);
        questionnaireService.defineQuestion(questionDefinition);
        verify(questionValidator).validate(questionDefinition);
    }

    @Test
    public void shouldGetAllQuestions() {
        List<QuestionDetail> questionDetails = questionnaireService.getAllQuestions();
        assertNotNull("getAllQuestions should not return null", questionDetails);
        verify(questionnaireDao, times(1)).getDetailsAll();
    }
    
}

