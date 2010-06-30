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
import org.mifos.platform.questionnaire.contract.EventSource;
import org.mifos.platform.questionnaire.contract.QuestionDefinition;
import org.mifos.platform.questionnaire.contract.QuestionGroupDefinition;
import org.mifos.platform.questionnaire.contract.SectionDefinition;
import org.mifos.platform.questionnaire.persistence.EventSourceDao;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.*;
import static org.mifos.platform.questionnaire.contract.QuestionType.FREETEXT;
import static org.mifos.platform.questionnaire.contract.QuestionType.INVALID;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QuestionValidatorTest {

    private QuestionnaireValidator questionnaireValidator;

    @Mock
    private EventSourceDao eventSourceDao;

    @Before
    public void setUp() {
        questionnaireValidator = new QuestionnaireValidatorImpl(eventSourceDao);
    }

    @Test
    public void shouldNotThrowExceptionWhenQuestionTitleIsProvided() {
        try {
            questionnaireValidator.validate(new QuestionDefinition("Title", FREETEXT));
        } catch (ApplicationException e) {
            fail("Should not have thrown the exception");
        }
    }

    @Test
    public void shouldThrowExceptionWhenQuestionTitleIsProvided() {
        try {
            questionnaireValidator.validate(new QuestionDefinition(null, FREETEXT));
            fail("Should have thrown the application exception");
        } catch (ApplicationException e) {
            assertEquals(QUESTION_TITLE_NOT_PROVIDED, e.getKey());
        }
    }

    @Test
    public void shouldThrowExceptionWhenQuestionTypeNotProvided() {
        try {
            questionnaireValidator.validate(new QuestionDefinition("Title 123", INVALID));
            fail("Should have thrown the application exception");
        } catch (ApplicationException e) {
            assertEquals(QUESTION_TYPE_NOT_PROVIDED, e.getKey());
        }
    }

    @Test
    public void shouldNotThrowExceptionWhenQuestionGroupTitleIsProvided() {
        when(eventSourceDao.retrieveCountByEventAndSource(anyString(), anyString())).thenReturn(Arrays.asList((long) 1));
        try {
            questionnaireValidator.validate(new QuestionGroupDefinition("Title", getEventSource("Create", "Client"), asList(getSection("S1"))));
        } catch (ApplicationException e) {
            fail("Should not have thrown the exception");
        }
        verify(eventSourceDao, times(1)).retrieveCountByEventAndSource(anyString(), anyString());
    }

    @Test
    public void shouldThrowExceptionWhenQuestionGroupTitleIsProvided() {
        try {
            questionnaireValidator.validate(new QuestionGroupDefinition(null, getEventSource("Create", "Client"), asList(getSection("S1"))));
            fail("Should have thrown the application exception");
        } catch (ApplicationException e) {
            assertEquals(QUESTION_GROUP_TITLE_NOT_PROVIDED, e.getKey());
        }
    }

    @Test
    public void shouldNotThrowExceptionWhenQuestionGroupHasAtLeastOneSection() {
        when(eventSourceDao.retrieveCountByEventAndSource(anyString(), anyString())).thenReturn(Arrays.asList((long) 1));
        try {
            questionnaireValidator.validate(new QuestionGroupDefinition("Title", getEventSource("Create", "Client"), asList(getSection("S1"))));
        } catch (ApplicationException e) {
            fail("Should not have thrown the exception");
        }
        verify(eventSourceDao, times(1)).retrieveCountByEventAndSource(anyString(), anyString());
    }

    @Test
    public void shouldThrowExceptionWhenQuestionGroupHasNoSections() {
        try {
            questionnaireValidator.validate(new QuestionGroupDefinition("Title", getEventSource("Create", "Client"), new ArrayList<SectionDefinition>()));
            fail("Should have thrown the application exception");
        } catch (ApplicationException e) {
            assertEquals(QUESTION_GROUP_SECTION_NOT_PROVIDED, e.getKey());
        }
    }

    @Test
    public void shouldThrowExceptionWhenEventIsNotProvided() {
        try {
            questionnaireValidator.validate(new QuestionGroupDefinition("Title", getEventSource(null, "Client"), asList(getSection("S1"))));
            fail("Should have thrown the application exception");
        } catch (ApplicationException e) {
            assertEquals(INVALID_EVENT_SOURCE, e.getKey());
        }
        verify(eventSourceDao, never()).retrieveCountByEventAndSource(anyString(), anyString());
    }

    @Test
    public void shouldThrowExceptionWhenSourceIsNotProvided() {
        try {
            questionnaireValidator.validate(new QuestionGroupDefinition("Title", getEventSource("Create", null), asList(getSection("S1"))));
            fail("Should have thrown the application exception");
        } catch (ApplicationException e) {
            assertEquals(INVALID_EVENT_SOURCE, e.getKey());
        }
        verify(eventSourceDao, never()).retrieveCountByEventAndSource(anyString(), anyString());
    }

    @Test
    public void shouldThrowExceptionWhenEventSourceIsNotProvided() {
        try {
            questionnaireValidator.validate(new QuestionGroupDefinition("Title", null, asList(getSection("S1"))));
            fail("Should have thrown the application exception");
        } catch (ApplicationException e) {
            assertEquals(INVALID_EVENT_SOURCE, e.getKey());
        }
        verify(eventSourceDao, never()).retrieveCountByEventAndSource(anyString(), anyString());
    }

    private SectionDefinition getSection(String name) {
        SectionDefinition section = new SectionDefinition();
        section.setName(name);
        return section;
    }

    private EventSource getEventSource(String event, String source) {
        return new EventSource(source, event, null);
    }

}
