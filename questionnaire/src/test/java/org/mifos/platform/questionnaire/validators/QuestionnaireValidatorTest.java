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
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.validations.ValidationException;
import org.mifos.platform.questionnaire.exceptions.BadNumericResponseException;
import org.mifos.platform.questionnaire.exceptions.MandatoryAnswerNotFoundException;
import org.mifos.platform.questionnaire.persistence.EventSourceDao;
import org.mifos.platform.questionnaire.persistence.QuestionDao;
import org.mifos.platform.questionnaire.persistence.QuestionGroupDao;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.GENERIC_VALIDATION;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.INVALID_EVENT_SOURCE;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.INVALID_NUMERIC_BOUNDS;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.NO_ANSWERS_PROVIDED;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.NO_QUESTIONS_FOUND_IN_SECTION;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.QUESTION_GROUP_SECTION_NOT_PROVIDED;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.QUESTION_GROUP_TITLE_NOT_PROVIDED;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.QUESTION_TEXT_NOT_PROVIDED;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.ANSWER_TYPE_NOT_PROVIDED;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuestionnaireValidatorTest {

    private QuestionnaireValidator questionnaireValidator;

    @Mock
    private EventSourceDao eventSourceDao;

    @Mock
    private QuestionGroupDao questionGroupDao;

    @Mock
    private QuestionDao questionDao;

    @Before
    public void setUp() {
        questionnaireValidator = new QuestionnaireValidatorImpl(eventSourceDao, questionGroupDao, questionDao);
    }

    @Test
    public void shouldNotThrowExceptionWhenQuestionTitleIsProvided() {
        try {
            questionnaireValidator.validateForDefineQuestion(new QuestionDetail("Title", QuestionType.FREETEXT));
        } catch (SystemException e) {
            fail("Should not have thrown the exception");
        }
    }

    @Test
    public void shouldThrowExceptionWhenQuestionTitleIsProvided() {
        try {
            questionnaireValidator.validateForDefineQuestion(new QuestionDetail(null, QuestionType.FREETEXT));
            fail("Should have thrown the validation exception");
        } catch (SystemException e) {
            assertEquals(QUESTION_TEXT_NOT_PROVIDED, e.getKey());
        }
    }

    @Test
    public void shouldThrowExceptionWhenQuestionTypeNotProvided() {
        try {
            questionnaireValidator.validateForDefineQuestion(new QuestionDetail("Title 123", QuestionType.INVALID));
            fail("Should have thrown the validation exception");
        } catch (SystemException e) {
            assertEquals(ANSWER_TYPE_NOT_PROVIDED, e.getKey());
        }
    }

    @Test
    public void shouldNotThrowExceptionForNumericQuestionType() {
        try {
            QuestionDetail questionDetail = new QuestionDetail("Title", QuestionType.NUMERIC);
            questionnaireValidator.validateForDefineQuestion(questionDetail);
        } catch (SystemException e) {
            fail("Should not have thrown the exception");
        }
    }

    @Test
    public void shouldThrowExceptionForNumericQuestionTypeWhenInvalidBoundsGiven() {
        try {
            QuestionDetail questionDetail = new QuestionDetail("Title", QuestionType.NUMERIC);
            questionDetail.setNumericMin(100);
            questionDetail.setNumericMax(10);
            questionnaireValidator.validateForDefineQuestion(questionDetail);
            fail("Should have thrown the exception");
        } catch (SystemException e) {
            assertEquals(INVALID_NUMERIC_BOUNDS, e.getKey());
        }
    }

    @Test
    public void shouldNotThrowExceptionForNumericQuestionTypeWhenOnlyMinBoundGiven() {
        try {
            QuestionDetail questionDetail = new QuestionDetail("Title", QuestionType.NUMERIC);
            questionDetail.setNumericMin(10);
            questionnaireValidator.validateForDefineQuestion(questionDetail);
        } catch (SystemException e) {
            fail("Should not have thrown the exception");
        }
    }

    @Test
    public void shouldNotThrowExceptionForNumericQuestionTypeWhenOnlyMaxBoundGiven() {
        try {
            QuestionDetail questionDetail = new QuestionDetail("Title", QuestionType.NUMERIC);
            questionDetail.setNumericMax(-100);
            questionnaireValidator.validateForDefineQuestion(questionDetail);
        } catch (SystemException e) {
            fail("Should not have thrown the exception");
        }
    }

    @Test
    public void shouldNotThrowExceptionWhenQuestionGroupTitleIsProvided() {
        when(eventSourceDao.retrieveCountByEventAndSource(anyString(), anyString())).thenReturn(asList((long) 1));
        try {
            questionnaireValidator.validateForDefineQuestionGroup(getQuestionGroupDetail(0, "Title", "Create", "Client"));
        } catch (SystemException e) {
            fail("shouldNotThrowExceptionWhenQuestionGroupTitleIsProvided:Should not have thrown the exception");
        }
        verify(eventSourceDao, times(1)).retrieveCountByEventAndSource(anyString(), anyString());
    }

    @Test
    public void shouldThrowExceptionWhenQuestionGroupTitleIsProvided() {
        try {
            questionnaireValidator.validateForDefineQuestionGroup(getQuestionGroupDetail(0, null, "Create", "Client"));
            fail("Should have thrown the validation exception");
        } catch (SystemException e) {
            assertEquals(QUESTION_GROUP_TITLE_NOT_PROVIDED, e.getKey());
        }
    }

    @Test
    public void shouldNotThrowExceptionWhenQuestionGroupHasAtLeastOneSection() {
        when(eventSourceDao.retrieveCountByEventAndSource(anyString(), anyString())).thenReturn(asList((long) 1));
        try {
            questionnaireValidator.validateForDefineQuestionGroup(getQuestionGroupDetail(0, "Title", "Create", "Client"));
        } catch (SystemException e) {
            fail("shouldNotThrowExceptionWhenQuestionGroupHasAtLeastOneSection:Should not have thrown the exception");
        }
        verify(eventSourceDao, times(1)).retrieveCountByEventAndSource(anyString(), anyString());
    }

    @Test
    public void shouldThrowExceptionWhenQuestionGroupHasNoSections() {
        try {
            questionnaireValidator.validateForDefineQuestionGroup(getQuestionGroupDetail(0, "Title", "Create", "Client", new ArrayList<SectionDetail>()));
            fail("Should have thrown the validation exception");
        } catch (SystemException e) {
            assertEquals(QUESTION_GROUP_SECTION_NOT_PROVIDED, e.getKey());
        }
    }

    @Test
    public void shouldThrowExceptionWhenEventIsNotProvided() {
        try {
            questionnaireValidator.validateForDefineQuestionGroup(getQuestionGroupDetail(0, "Title", null, "Client"));
            fail("Should have thrown the validation exception");
        } catch (SystemException e) {
            assertEquals(INVALID_EVENT_SOURCE, e.getKey());
        }
        verify(eventSourceDao, never()).retrieveCountByEventAndSource(anyString(), anyString());
    }

    @Test
    public void shouldThrowExceptionWhenSourceIsNotProvided() {
        try {
            questionnaireValidator.validateForDefineQuestionGroup(getQuestionGroupDetail(0, "Title", "Create", null));
            fail("Should have thrown the validation exception");
        } catch (SystemException e) {
            assertEquals(INVALID_EVENT_SOURCE, e.getKey());
        }
        verify(eventSourceDao, never()).retrieveCountByEventAndSource(anyString(), anyString());
    }

    @Test
    public void shouldThrowExceptionWhenEventSourceIsNotProvided() {
        try {
            questionnaireValidator.validateForDefineQuestionGroup(new QuestionGroupDetail(0, "Title", null, asList(getSection("S1")), false));
            fail("Should have thrown the validation exception");
        } catch (SystemException e) {
            assertEquals(INVALID_EVENT_SOURCE, e.getKey());
        }
        verify(eventSourceDao, never()).retrieveCountByEventAndSource(anyString(), anyString());
    }

    @Test
    public void shouldThrowExceptionWhenAGivenSectionHasNoQuestions() {
        try {
            questionnaireValidator.validateForDefineQuestionGroup(getQuestionGroupDetail(0, "Title", "Create", "Client", asList(getSectionWithQuestions("S1"))));
            fail("Should have thrown the validation exception");
        } catch (SystemException e) {
            assertEquals(NO_QUESTIONS_FOUND_IN_SECTION, e.getKey());
        }
        verify(eventSourceDao, never()).retrieveCountByEventAndSource(anyString(), anyString());
    }

    @Test
    public void shouldThrowExceptionWhenNoAnswersProvided() {
        try {
            questionnaireValidator.validateForQuestionGroupResponses(null);
            fail("Should have thrown the validation exception");
        } catch (SystemException e) {
            assertEquals(NO_ANSWERS_PROVIDED, e.getKey());
        }
    }

    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
    @Test
    public void shouldThrowExceptionWhenAMandatoryQuestionHasNoAnswer() {
        QuestionGroupDetail questionGroupDetail = getQuestionGroupDetail(0, "Title", "Create", "Client");
        try {
            questionnaireValidator.validateForQuestionGroupResponses(asList(questionGroupDetail));
            fail("Should have thrown the validation exception");
        } catch (ValidationException e) {
            assertEquals(GENERIC_VALIDATION, e.getKey());
            assertEquals(true, e.hasChildExceptions());
            assertEquals(1, e.getChildExceptions().size());
            ValidationException childException = e.getChildExceptions().get(0);
            assertTrue(childException instanceof MandatoryAnswerNotFoundException);
            assertEquals("Q1", childException.getIdentifier());
        }
    }

    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
    @Test
    public void shouldThrowExceptionWhenANumericQuestionHasInvalidAnswer() {
        QuestionDetail questionDetail = getNumericQuestionDetail("Numeric Question", 10, 100);
        SectionDetail sectionWithOneQuestion = getSectionWithOneQuestion("Sec1", questionDetail, "123ab");
        QuestionGroupDetail questionGroupDetail = getQuestionGroupDetail(0, "Title", "Create", "Client", asList(sectionWithOneQuestion));
        try {
            questionnaireValidator.validateForQuestionGroupResponses(asList(questionGroupDetail));
            fail("Should have thrown the validation exception");
        } catch (ValidationException e) {
            assertEquals(GENERIC_VALIDATION, e.getKey());
            assertEquals(true, e.hasChildExceptions());
            assertEquals(1, e.getChildExceptions().size());
            ValidationException childException = e.getChildExceptions().get(0);
            assertTrue(childException instanceof BadNumericResponseException);
            assertEquals("Numeric Question", childException.getIdentifier());
            assertEquals(Integer.valueOf(10), ((BadNumericResponseException)childException).getAllowedMinValue());
            assertEquals(Integer.valueOf(100), ((BadNumericResponseException)childException).getAllowedMaxValue());
        }
    }

    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
    @Test
    public void shouldThrowExceptionWhenANumericQuestionHasAnswerLessThanMin() {
        QuestionDetail questionDetail = getNumericQuestionDetail("Numeric Question", 10, null);
        SectionDetail sectionWithOneQuestion = getSectionWithOneQuestion("Sec1", questionDetail, "9");
        QuestionGroupDetail questionGroupDetail = getQuestionGroupDetail(0, "Title", "Create", "Client", asList(sectionWithOneQuestion));
        try {
            questionnaireValidator.validateForQuestionGroupResponses(asList(questionGroupDetail));
            fail("Should have thrown the validation exception");
        } catch (ValidationException e) {
            assertEquals(GENERIC_VALIDATION, e.getKey());
            assertEquals(true, e.hasChildExceptions());
            assertEquals(1, e.getChildExceptions().size());
            ValidationException childException = e.getChildExceptions().get(0);
            assertTrue(childException instanceof BadNumericResponseException);
            assertEquals("Numeric Question", childException.getIdentifier());
            assertEquals(Integer.valueOf(10), ((BadNumericResponseException)childException).getAllowedMinValue());
            assertNull(((BadNumericResponseException)childException).getAllowedMaxValue());
        }
    }

    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
    @Test
    public void shouldThrowExceptionWhenANumericQuestionHasAnswerMoreThanMax() {
        QuestionDetail questionDetail = getNumericQuestionDetail("Numeric Question", null, 100);
        SectionDetail sectionWithOneQuestion = getSectionWithOneQuestion("Sec1", questionDetail, "101");
        QuestionGroupDetail questionGroupDetail = getQuestionGroupDetail(0, "Title", "Create", "Client", asList(sectionWithOneQuestion));
        try {
            questionnaireValidator.validateForQuestionGroupResponses(asList(questionGroupDetail));
            fail("Should have thrown the validation exception");
        } catch (ValidationException e) {
            assertEquals(GENERIC_VALIDATION, e.getKey());
            assertEquals(true, e.hasChildExceptions());
            assertEquals(1, e.getChildExceptions().size());
            ValidationException childException = e.getChildExceptions().get(0);
            assertTrue(childException instanceof BadNumericResponseException);
            assertEquals("Numeric Question", childException.getIdentifier());
            assertNull(((BadNumericResponseException)childException).getAllowedMinValue());
            assertEquals(Integer.valueOf(100), ((BadNumericResponseException)childException).getAllowedMaxValue());
        }
    }

    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
    @Test
    public void shouldNotThrowExceptionWhenANumericQuestionWithNoBoundsHasAnswer() {
        QuestionDetail questionDetail = getNumericQuestionDetail("Numeric Question", null, null);
        SectionDetail sectionWithOneQuestion = getSectionWithOneQuestion("Sec1", questionDetail, "121");
        QuestionGroupDetail questionGroupDetail = getQuestionGroupDetail(0, "Title", "Create", "Client", asList(sectionWithOneQuestion));
        try {
            questionnaireValidator.validateForQuestionGroupResponses(asList(questionGroupDetail));
        } catch (ValidationException e) {
            fail("Should not have thrown the validation exception");
        }
    }

    private QuestionDetail getNumericQuestionDetail(String title, Integer numericMin, Integer numericMax) {
        QuestionDetail questionDetail = new QuestionDetail(title, QuestionType.NUMERIC);
        questionDetail.setNumericMin(numericMin);
        questionDetail.setNumericMax(numericMax);
        return questionDetail;
    }

    private SectionDetail getSectionWithOneQuestion(String name, QuestionDetail questionDetail, String response) {
        SectionDetail sectionDetail = new SectionDetail();
        sectionDetail.setName(name);
        sectionDetail.setQuestionDetails(asList(new SectionQuestionDetail(questionDetail, true, response)));
        return sectionDetail;
    }

    private QuestionGroupDetail getQuestionGroupDetail(int id, String title, String event, String source) {
        return getQuestionGroupDetail(id, title, event, source, asList(getSection("S1")));
    }

    private QuestionGroupDetail getQuestionGroupDetail(int id, String title, String event, String source, List<SectionDetail> sectionDetails) {
        return new QuestionGroupDetail(id, title, Arrays.asList(getEventSource(event, source)), sectionDetails, false);
    }

    private SectionDetail getSection(String name) {
        SectionDetail section = new SectionDetail();
        section.setName(name);
        section.addQuestion(new SectionQuestionDetail(new QuestionDetail(12, "Q1", QuestionType.INVALID, true, true), true, null));
        return section;
    }

    private SectionDetail getSectionWithQuestions(String name, int... questionIds) {
        SectionDetail section = new SectionDetail();
        section.setName(name);
        if (questionIds != null) {
            for (int questionId : questionIds) {
                section.addQuestion(new SectionQuestionDetail(new QuestionDetail(questionId, null, QuestionType.INVALID, true, true), true, null));
            }
        }
        return section;
    }

    private EventSourceDto getEventSource(String event, String source) {
        return new EventSourceDto(event, source, null);
    }

}
