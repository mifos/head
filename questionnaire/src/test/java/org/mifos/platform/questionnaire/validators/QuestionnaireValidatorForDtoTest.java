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
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.builders.ChoiceDetailBuilder;
import org.mifos.platform.questionnaire.builders.QuestionDtoBuilder;
import org.mifos.platform.questionnaire.builders.QuestionGroupDtoBuilder;
import org.mifos.platform.questionnaire.builders.SectionDtoBuilder;
import org.mifos.platform.questionnaire.domain.AnswerType;
import org.mifos.platform.questionnaire.domain.QuestionChoiceEntity;
import org.mifos.platform.questionnaire.domain.QuestionEntity;
import org.mifos.platform.exceptions.ValidationException;
import org.mifos.platform.questionnaire.persistence.EventSourceDao;
import org.mifos.platform.questionnaire.persistence.QuestionDao;
import org.mifos.platform.questionnaire.persistence.QuestionGroupDao;
import org.mifos.platform.questionnaire.service.dtos.ChoiceDto;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.dtos.QuestionDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.SectionDto;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.*;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.QUESTION_TITLE_TOO_BIG;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.SECTION_NAME_TOO_BIG;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuestionnaireValidatorForDtoTest {
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
    public void shouldNotValidateForValidQuestionGroupDto() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
        } catch (ValidationException e) {
            fail("Should not have thrown validationException");
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_MissingTitle() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        questionGroupDto.setTitle(null);
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(QUESTION_GROUP_TITLE_NOT_PROVIDED));
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_QuestionGroupTitleExceedsMaxChars() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        questionGroupDto.setTitle("this is more than fifty characters string for question group title");
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(QUESTION_GROUP_TITLE_TOO_BIG));
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_SectionNameExceedsMaxChars() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        questionGroupDto.getSections().get(0).setName("this is more than fifty characters string for question group title");
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(SECTION_NAME_TOO_BIG));
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_QuestionTitleExceedsMaxChars() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        questionGroupDto.getSections().get(0).getQuestions().get(0).setTitle(
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "too much");
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(QUESTION_TITLE_TOO_BIG));
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionDto_QuestionTitleExceedsMaxChars() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        QuestionDto questionDto = questionGroupDto.getSections().get(0).getQuestions().get(0);
        questionDto.setTitle(
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "there are exactly fifty characters in this string " +
                "too much");
        try {
            questionnaireValidator.validateForDefineQuestion(questionDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(QUESTION_TITLE_TOO_BIG));
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_InvalidEventSorce() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(0L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(INVALID_EVENT_SOURCE));
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_MissingSections() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        questionGroupDto.setSections(Collections.<SectionDto>emptyList());
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(QUESTION_GROUP_SECTION_NOT_PROVIDED));
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_MissingQuestionTitle() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        questionGroupDto.getSections().get(0).getQuestions().get(0).setTitle(null);
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(QUESTION_TITLE_NOT_PROVIDED));
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionDto_MissingQuestionTitle() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        QuestionDto questionDto = questionGroupDto.getSections().get(0).getQuestions().get(0);
        questionDto.setTitle(null);
        try {
            questionnaireValidator.validateForDefineQuestion(questionDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(QUESTION_TITLE_NOT_PROVIDED));
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_DuplicateQuestionTitle() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        questionGroupDto.getSections().get(0).getQuestions().get(0).setTitle(" Ques ");
        questionGroupDto.getSections().get(0).getQuestions().get(1).setTitle("quEs");
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(QUESTION_TITLE_DUPLICATE));
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_MissingQuestionOrder() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        questionGroupDto.getSections().get(0).getQuestions().get(0).setOrder(null);
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(QUESTION_ORDER_NOT_PROVIDED));
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_DuplicateQuestionOrder() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        questionGroupDto.getSections().get(0).getQuestions().get(0).setOrder(123);
        questionGroupDto.getSections().get(0).getQuestions().get(1).setOrder(123);
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(QUESTION_ORDER_DUPLICATE));
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_MissingQuestionsInSection() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        questionGroupDto.getSections().get(0).setQuestions(Collections.<QuestionDto>emptyList());
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(NO_QUESTIONS_FOUND_IN_SECTION));
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_MissingSectionName() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        questionGroupDto.getSections().get(0).setName(null);
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(QuestionnaireConstants.SECTION_TITLE_NOT_PROVIDED));
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_DuplicateSectionName() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        questionGroupDto.getSections().get(0).setName(" Sec ");
        questionGroupDto.getSections().get(1).setName("sEc");
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(QuestionnaireConstants.SECTION_TITLE_DUPLICATE));
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_MissingSectionOrder() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        questionGroupDto.getSections().get(0).setOrder(null);
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(SECTION_ORDER_NOT_PROVIDED));
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_DuplicateSectionOrder() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        questionGroupDto.getSections().get(0).setOrder(21);
        questionGroupDto.getSections().get(1).setOrder(21);
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(SECTION_ORDER_DUPLICATE));
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_ExistingQuestionTitle_DifferentQuestionType_NoChoices() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        String questionTitle = questionGroupDto.getSections().get(0).getQuestions().get(0).getTitle();
        when(questionDao.retrieveByName(questionTitle)).thenReturn(asList(getQuestionEntity(questionTitle, AnswerType.DATE, null)));
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(QUESTION_TITILE_MATCHES_EXISTING_QUESTION));
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_ExistingQuestionTitle_SameQuestionType_NoChoices() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        String questionTitle = questionGroupDto.getSections().get(0).getQuestions().get(0).getTitle();
        when(questionDao.retrieveByName(questionTitle)).thenReturn(asList(getQuestionEntity(questionTitle, AnswerType.FREETEXT, null)));
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
        } catch (ValidationException e) {
            fail("Should not have thrown validationException");
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_ExistingQuestionTitle_SameQuestionType_WithDifferentNumberOfChoices() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        String questionTitle = questionGroupDto.getSections().get(0).getQuestions().get(1).getTitle();
        List<QuestionChoiceEntity> choices = asList(getChoice("Ch2"), getChoice("Ch5"));
        when(questionDao.retrieveByName(questionTitle)).thenReturn(asList(getQuestionEntity(questionTitle, AnswerType.SINGLESELECT, choices)));
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(QUESTION_TITILE_MATCHES_EXISTING_QUESTION));
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_ExistingQuestionTitle_SameQuestionType_WithDifferentChoices() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        String questionTitle = questionGroupDto.getSections().get(0).getQuestions().get(1).getTitle();
        List<QuestionChoiceEntity> choices = asList(getChoice("Ch2"), getChoice("Ch3"), getChoice("Ch0"));
        when(questionDao.retrieveByName(questionTitle)).thenReturn(asList(getQuestionEntity(questionTitle, AnswerType.SINGLESELECT, choices)));
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(QUESTION_TITILE_MATCHES_EXISTING_QUESTION));
        }
    }

    @Test
    public void shouldNotValidateForValidQuestionGroupDto_ExistingQuestionTitle_SameQuestionType_WithSimilarChoices() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        String questionTitle = questionGroupDto.getSections().get(0).getQuestions().get(1).getTitle();
        List<QuestionChoiceEntity> choices = asList(getChoice("Ch2"), getChoice("Ch3"), getChoice("cH1"));
        when(questionDao.retrieveByName(questionTitle)).thenReturn(asList(getQuestionEntity(questionTitle, AnswerType.SINGLESELECT, choices)));
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
        } catch (ValidationException e) {
            fail("Should not have thrown validationException");
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_MissingQuestionType() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        questionGroupDto.getSections().get(0).getQuestions().get(0).setType(QuestionType.INVALID);
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(ANSWER_TYPE_NOT_PROVIDED));
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_MissingRequiredNumberOfChoicesForSingleSelect() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        questionGroupDto.getSections().get(0).getQuestions().get(1).setChoices(asList(new ChoiceDto("Ch1")));
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(QUESTION_CHOICES_INSUFFICIENT));
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_MissingValueForChoice() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        questionGroupDto.getSections().get(0).getQuestions().get(1).getChoices().get(0).setValue(null);
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(QUESTION_CHOICES_INVALID));
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_DuplicateValueForChoice() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        questionGroupDto.getSections().get(0).getQuestions().get(1).getChoices().get(0).setValue(" Choice");
        questionGroupDto.getSections().get(0).getQuestions().get(1).getChoices().get(1).setValue("ChoICe ");
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(QUESTION_CHOICES_INVALID));
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_MissingOrderForChoice() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        questionGroupDto.getSections().get(0).getQuestions().get(1).getChoices().get(0).setOrder(null);
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(QUESTION_CHOICES_INVALID));
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_DuplicateOrderForChoice() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        questionGroupDto.getSections().get(0).getQuestions().get(1).getChoices().get(0).setOrder(987);
        questionGroupDto.getSections().get(0).getQuestions().get(1).getChoices().get(1).setOrder(987);
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(QUESTION_CHOICES_INVALID));
        }
    }

    @Test
    public void shouldValidateForInvalidQuestionGroupDto_InvalidNumericBounds() {
        when(eventSourceDao.retrieveCountByEventAndSource("Create", "Client")).thenReturn(asList(1L));
        QuestionGroupDto questionGroupDto = getQuestionGroupDto();
        questionGroupDto.getSections().get(1).getQuestions().get(1).setMinValue(100);
        questionGroupDto.getSections().get(1).getQuestions().get(1).setMaxValue(10);
        try {
            questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
            fail("Should have thrown validationException");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(GENERIC_VALIDATION));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            assertThat(childExceptions, is(notNullValue()));
            assertThat(childExceptions.size(), is(1));
            assertThat(childExceptions.get(0).getKey(), is(INVALID_NUMERIC_BOUNDS));
        }
    }

    private QuestionChoiceEntity getChoice(String text) {
        return new QuestionChoiceEntity(text);
    }

    private QuestionEntity getQuestionEntity(String questionTitle, AnswerType answerType, List<QuestionChoiceEntity> choices) {
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setShortName(questionTitle);
        questionEntity.setAnswerType(answerType);
        questionEntity.setChoices(choices);
        return questionEntity;
    }

    private QuestionGroupDto getQuestionGroupDto() {
        String ques1Title = "Ques1" + currentTimeMillis();
        String ques2Title = "Ques2" + currentTimeMillis();
        String ques3Title = "Ques3" + currentTimeMillis();
        String ques4Title = "Ques4" + currentTimeMillis();
        String qgTitle = "QG1" + currentTimeMillis();
        QuestionDto question1 = new QuestionDtoBuilder().withTitle(ques1Title).withMandatory(true).withType(QuestionType.FREETEXT).withOrder(1).build();
        ChoiceDto choice1 = new ChoiceDetailBuilder().withValue("Ch1").withOrder(1).build();
        ChoiceDto choice2 = new ChoiceDetailBuilder().withValue("Ch2").withOrder(2).build();
        ChoiceDto choice3 = new ChoiceDetailBuilder().withValue("Ch3").withOrder(3).build();
        QuestionDto question2 = new QuestionDtoBuilder().withTitle(ques2Title).withType(QuestionType.SINGLE_SELECT).addChoices(choice1, choice2, choice3).withOrder(2).build();
        SectionDto section1 = new SectionDtoBuilder().withName("Sec1").withOrder(1).addQuestions(question1, question2).build();
        QuestionDto question3 = new QuestionDtoBuilder().withTitle(ques3Title).withMandatory(false).withType(QuestionType.DATE).withOrder(1).build();
        QuestionDto question4 = new QuestionDtoBuilder().withTitle(ques4Title).withMandatory(true).withType(QuestionType.NUMERIC).withOrder(2).build();
        SectionDto section2 = new SectionDtoBuilder().withName("Sec2").withOrder(2).addQuestions(question3, question4).build();
        return new QuestionGroupDtoBuilder().withTitle(qgTitle).withEventSource("Create", "Client").addSections(section1, section2).build();
    }
}
