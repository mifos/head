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

package org.mifos.platform.questionnaire.validators;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.contract.QuestionDefinition;
import org.mifos.platform.questionnaire.validators.QuestionValidator;
import org.mifos.platform.questionnaire.validators.QuestionValidatorImpl;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.QUESTION_TITLE_NOT_PROVIDED;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.QUESTION_TYPE_NOT_PROVIDED;
import static org.mifos.platform.questionnaire.contract.QuestionType.*;

@RunWith(MockitoJUnitRunner.class)
public class QuestionValidatorTest {

    private QuestionValidator questionValidator;

    @Before
    public void setUp(){
        questionValidator = new QuestionValidatorImpl();
    }

    @Test
    public void shouldNotThrowExceptionWhenTitleIsProvided(){
        try {
            questionValidator.validate(new QuestionDefinition("Title", FREETEXT));
        } catch (ApplicationException e) {
            fail("Should not have thrown the exception");
        }
    }

    @Test
    public void shouldThrowExceptionWhenTitleIsProvided(){
        try {
            questionValidator.validate(new QuestionDefinition(null, FREETEXT));
            fail("Should have thrown the application exception");
        } catch (ApplicationException e) {
            assertEquals(QUESTION_TITLE_NOT_PROVIDED, e.getKey());
        }
    }

    @Test
    public void shouldThrowExceptionWhenQuestionTypeNotProvided(){
        try {
            questionValidator.validate(new QuestionDefinition("Title 123", INVALID));
            fail("Should have thrown the application exception");
        } catch (ApplicationException e) {
            assertEquals(QUESTION_TYPE_NOT_PROVIDED, e.getKey());
        }
    }
}
