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

import org.apache.commons.lang.StringUtils;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.exceptions.BadNumericResponseException;
import org.mifos.platform.questionnaire.exceptions.MandatoryAnswerNotFoundException;
import org.mifos.platform.questionnaire.exceptions.ValidationException;
import org.mifos.platform.questionnaire.persistence.EventSourceDao;
import org.mifos.platform.questionnaire.service.*; //NOPMD
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mifos.platform.questionnaire.QuestionnaireConstants.*; //NOPMD
import static org.mifos.platform.questionnaire.service.QuestionType.INVALID;
import static org.mifos.platform.questionnaire.service.QuestionType.NUMERIC;
import static org.mifos.platform.util.CollectionUtils.isEmpty;

@SuppressWarnings("PMD")
public class QuestionnaireValidatorImpl implements QuestionnaireValidator {

    @Autowired
    private EventSourceDao eventSourceDao;

    @SuppressWarnings({"UnusedDeclaration"})
    private QuestionnaireValidatorImpl() {
    }

    public QuestionnaireValidatorImpl(EventSourceDao eventSourceDao) {
        this.eventSourceDao = eventSourceDao;
    }

    @Override
    public void validateForDefineQuestion(QuestionDetail questionDetail) throws SystemException {
        validateQuestionTitle(questionDetail);
        validateQuestionType(questionDetail);
    }

    @Override
    public void validateForDefineQuestionGroup(QuestionGroupDetail questionGroupDetail) throws SystemException {
        validateQuestionGroupTitle(questionGroupDetail);
        validateQuestionGroupSections(questionGroupDetail.getSectionDetails());
        validateForEventSource(questionGroupDetail.getEventSource());
    }

    @Override
    public void validateForEventSource(EventSource eventSource) throws SystemException {
        if (eventSource == null || StringUtils.isEmpty(eventSource.getSource()) || StringUtils.isEmpty(eventSource.getEvent()))
            throw new SystemException(INVALID_EVENT_SOURCE);
        validateEventSource(eventSource);
    }

    @Override
    public void validateForQuestionGroupResponses(List<QuestionGroupDetail> questionGroupDetails) {
        if (isEmpty(questionGroupDetails)) throw new SystemException(NO_ANSWERS_PROVIDED);
        ValidationException validationException = new ValidationException(GENERIC_VALIDATION);
        for (QuestionGroupDetail questionGroupDetail : questionGroupDetails) {
            validateResponsesInQuestionGroup(questionGroupDetail, validationException);
        }
        if (validationException.containsChildExceptions()) throw validationException;
    }

    private void validateResponsesInQuestionGroup(QuestionGroupDetail questionGroupDetail, ValidationException validationException) {
        for (SectionDetail sectionDetail : questionGroupDetail.getSectionDetails()) {
            for (SectionQuestionDetail sectionQuestionDetail : sectionDetail.getQuestions()) {
                validateSectionQuestionDetail(validationException, sectionQuestionDetail);
            }
        }
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    private void validateSectionQuestionDetail(ValidationException validationException, SectionQuestionDetail sectionQuestionDetail) {
        // TODO: When there are more such validations, use a chain of validators
        String questionTitle = sectionQuestionDetail.getTitle();
        if (sectionQuestionDetail.isMandatory() && sectionQuestionDetail.hasNoAnswer()) {
            validationException.addChildException(new MandatoryAnswerNotFoundException(questionTitle));
        } else if (sectionQuestionDetail.hasAnswer() && sectionQuestionDetail.isNumeric()) {
            Integer allowedMinValue = sectionQuestionDetail.getNumericMin();
            Integer allowedMaxValue = sectionQuestionDetail.getNumericMax();
            if (invalidNumericAnswer(sectionQuestionDetail.getAnswer(), allowedMinValue, allowedMaxValue)) {
                validationException.addChildException(new BadNumericResponseException(questionTitle, allowedMinValue, allowedMaxValue));
            }
        }
    }

    private boolean invalidNumericAnswer(String answer, Integer allowedMin, Integer allowedMax) {
        boolean result;
        try {
            Integer answerAsInt = Integer.parseInt(answer, 10);
            result = (allowedMin != null && answerAsInt < allowedMin) || (allowedMax != null && answerAsInt > allowedMax);
        } catch (NumberFormatException e) {
            result = true;
        }
        return result;
    }

    private void validateEventSource(EventSource eventSource) throws SystemException {
        List result = eventSourceDao.retrieveCountByEventAndSource(eventSource.getEvent(), eventSource.getSource());
        if (isEmpty(result) || ((Long) result.get(0) == 0)) {
            throw new SystemException(INVALID_EVENT_SOURCE);
        }
    }

    private void validateQuestionGroupSections(List<SectionDetail> sectionDetails) throws SystemException {
        if(isEmpty(sectionDetails)) {
            throw new SystemException(QUESTION_GROUP_SECTION_NOT_PROVIDED);
        }
        validateSectionDefinitions(sectionDetails);
    }

    private void validateSectionDefinitions(List<SectionDetail> sectionDetails) throws SystemException {
        Set<SectionQuestionDetail> questions = new HashSet<SectionQuestionDetail>();
        for (SectionDetail sectionDetail : sectionDetails) {
            validateSectionDefinition(sectionDetail);
            for (SectionQuestionDetail questionDetail : sectionDetail.getQuestions()) {
                if (!questions.add(questionDetail)) {
                    throw new SystemException(DUPLICATE_QUESTION_FOUND_IN_SECTION);
                }
            }
        }
    }

    private void validateSectionDefinition(SectionDetail sectionDetail) throws SystemException {
        if (isEmpty(sectionDetail.getQuestions())) {
            throw new SystemException(NO_QUESTIONS_FOUND_IN_SECTION);
        }
    }

    private void validateQuestionGroupTitle(QuestionGroupDetail questionGroupDetail) throws SystemException {
        if (StringUtils.isEmpty(questionGroupDetail.getTitle()))
            throw new SystemException(QUESTION_GROUP_TITLE_NOT_PROVIDED);
    }

    private void validateQuestionType(QuestionDetail questionDetail) throws SystemException {
        if (INVALID == questionDetail.getQuestionTypeDetail().getQuestionType())
            throw new SystemException(QUESTION_TYPE_NOT_PROVIDED);
        if (NUMERIC == questionDetail.getQuestionTypeDetail().getQuestionType())
            validateForNumericQuestionType(questionDetail);
    }

    private void validateForNumericQuestionType(QuestionDetail questionDetail) {
        Integer min = questionDetail.getNumericMin();
        Integer max = questionDetail.getNumericMax();
        if (min != null && max != null && min > max)
            throw new SystemException(INVALID_NUMERIC_BOUNDS);
    }

    private void validateQuestionTitle(QuestionDetail questionDefinition) throws SystemException {
        if (StringUtils.isEmpty(questionDefinition.getTitle()))
            throw new SystemException(QUESTION_TITLE_NOT_PROVIDED);
    }
}
