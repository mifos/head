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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.persistence.EventSourceDao;
import org.mifos.platform.questionnaire.service.*;//NOPMD
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.Arrays; //NOPMD
import static org.junit.Assert.fail;      //NOPMD
import static org.mifos.platform.questionnaire.QuestionnaireConstants.*; //NOPMD
import static org.mockito.Matchers.anyString;   //NOPMD
import org.mockito.Mockito;   //NOPMD

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD")
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
            questionnaireValidator.validate(new QuestionDetail("Title", QuestionType.FREETEXT));
        } catch (SystemException e) {
            fail("Should not have thrown the exception");
        }
    }

    @Test
    public void shouldThrowExceptionWhenQuestionTitleIsProvided() {
        try {
            questionnaireValidator.validate(new QuestionDetail(null, QuestionType.FREETEXT));
            fail("Should have thrown the application exception");
        } catch (SystemException e) {
            Assert.assertEquals(QUESTION_TITLE_NOT_PROVIDED, e.getKey());
        }
    }

    @Test
    public void shouldThrowExceptionWhenQuestionTypeNotProvided() {
        try {
            questionnaireValidator.validate(new QuestionDetail("Title 123", QuestionType.INVALID));
            fail("Should have thrown the application exception");
        } catch (SystemException e) {
            Assert.assertEquals(QUESTION_TYPE_NOT_PROVIDED, e.getKey());
        }
    }

    @Test
    public void shouldNotThrowExceptionWhenQuestionGroupTitleIsProvided() {
        Mockito.when(eventSourceDao.retrieveCountByEventAndSource(anyString(), anyString())).thenReturn(Arrays.asList((long) 1));
        try {
            questionnaireValidator.validate(new QuestionGroupDetail(0, "Title", getEventSource("Create", "Client"), Arrays.asList(getSection("S1"))));
        } catch (SystemException e) {
            fail("shouldNotThrowExceptionWhenQuestionGroupTitleIsProvided:Should not have thrown the exception");
        }
        Mockito.verify(eventSourceDao, Mockito.times(1)).retrieveCountByEventAndSource(anyString(), anyString());
    }

    @Test
    public void shouldThrowExceptionWhenQuestionGroupTitleIsProvided() {
        try {
            questionnaireValidator.validate(new QuestionGroupDetail(0, null, getEventSource("Create", "Client"), Arrays.asList(getSection("S1"))));
            fail("Should have thrown the application exception");
        } catch (SystemException e) {
            Assert.assertEquals(QUESTION_GROUP_TITLE_NOT_PROVIDED, e.getKey());
        }
    }

    @Test
    public void shouldNotThrowExceptionWhenQuestionGroupHasAtLeastOneSection() {
        Mockito.when(eventSourceDao.retrieveCountByEventAndSource(anyString(), anyString())).thenReturn(Arrays.asList((long) 1));
        try {
            questionnaireValidator.validate(new QuestionGroupDetail(0, "Title", getEventSource("Create", "Client"), Arrays.asList(getSection("S1"))));
        } catch (SystemException e) {
            fail("shouldNotThrowExceptionWhenQuestionGroupHasAtLeastOneSection:Should not have thrown the exception");
        }
        Mockito.verify(eventSourceDao, Mockito.times(1)).retrieveCountByEventAndSource(anyString(), anyString());
    }

    @Test
    public void shouldThrowExceptionWhenQuestionGroupHasNoSections() {
        try {
            questionnaireValidator.validate(new QuestionGroupDetail(0, "Title", getEventSource("Create", "Client"), new ArrayList<SectionDetail>()));
            fail("Should have thrown the application exception");
        } catch (SystemException e) {
            Assert.assertEquals(QUESTION_GROUP_SECTION_NOT_PROVIDED, e.getKey());
        }
    }

    @Test
    public void shouldThrowExceptionWhenEventIsNotProvided() {
        try {
            questionnaireValidator.validate(new QuestionGroupDetail(0, "Title", getEventSource(null, "Client"), Arrays.asList(getSection("S1"))));
            fail("Should have thrown the application exception");
        } catch (SystemException e) {
            Assert.assertEquals(INVALID_EVENT_SOURCE, e.getKey());
        }
        Mockito.verify(eventSourceDao, Mockito.never()).retrieveCountByEventAndSource(anyString(), anyString());
    }

    @Test
    public void shouldThrowExceptionWhenSourceIsNotProvided() {
        try {
            questionnaireValidator.validate(new QuestionGroupDetail(0, "Title", getEventSource("Create", null), Arrays.asList(getSection("S1"))));
            fail("Should have thrown the application exception");
        } catch (SystemException e) {
            Assert.assertEquals(INVALID_EVENT_SOURCE, e.getKey());
        }
        Mockito.verify(eventSourceDao, Mockito.never()).retrieveCountByEventAndSource(anyString(), anyString());
    }

    @Test
    public void shouldThrowExceptionWhenEventSourceIsNotProvided() {
        try {
            questionnaireValidator.validate(new QuestionGroupDetail(0, "Title", null, Arrays.asList(getSection("S1"))));
            fail("Should have thrown the application exception");
        } catch (SystemException e) {
            Assert.assertEquals(INVALID_EVENT_SOURCE, e.getKey());
        }
        Mockito.verify(eventSourceDao, Mockito.never()).retrieveCountByEventAndSource(anyString(), anyString());
    }

    @Test
    public void shouldThrowExceptionWhenAGivenSectionHasNoQuestions() {
        try {
            questionnaireValidator.validate(new QuestionGroupDetail(0, "Title", getEventSource("Create", "Client"), Arrays.asList(getSectionWithQuestions("S1"))));
            fail("Should have thrown the application exception");
        } catch (SystemException e) {
            Assert.assertEquals(NO_QUESTIONS_FOUND_IN_SECTION, e.getKey());
        }
        Mockito.verify(eventSourceDao, Mockito.never()).retrieveCountByEventAndSource(anyString(), anyString());
    }

    @Test
    public void shouldThrowExceptionWhenAGivenQuestionAppearsInMoreThanOneSection() {
        try {
            SectionDetail sectionDefinition1 = getSectionWithQuestions("S1", 1, 3);
            SectionDetail sectionDefinition2 = getSectionWithQuestions("S2", 3, 2);
            List<SectionDetail> sectionDetails = Arrays.asList(sectionDefinition1, sectionDefinition2);
            questionnaireValidator.validate(new QuestionGroupDetail(0, "Title", getEventSource("Create", "Client"), sectionDetails));
            fail("Should have thrown the application exception");
        } catch (SystemException e) {
            Assert.assertEquals(DUPLICATE_QUESTION_FOUND_IN_SECTION, e.getKey());
        }
        Mockito.verify(eventSourceDao, Mockito.never()).retrieveCountByEventAndSource(anyString(), anyString());
    }

    private SectionDetail getSection(String name) {
        SectionDetail section = new SectionDetail();
        section.setName(name);
        section.addQuestion(new SectionQuestionDetail(new QuestionDetail(12, null, null, QuestionType.INVALID), true));
        return section;
    }

    private SectionDetail getSectionWithQuestions(String name, int... questionIds) {
        SectionDetail section = new SectionDetail();
        section.setName(name);
        if (questionIds != null) {
            for (int questionId : questionIds) {
                section.addQuestion(new SectionQuestionDetail(new QuestionDetail(questionId, null, null, QuestionType.INVALID), true));
            }
        }
        return section;
    }

    private EventSource getEventSource(String event, String source) {
        return new EventSource(event, source, null);
    }

}
