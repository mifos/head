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
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.persistence.EventSourceDao;
import org.mifos.platform.questionnaire.service.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mifos.platform.questionnaire.QuestionnaireConstants.*;
import static org.mifos.platform.questionnaire.service.QuestionType.INVALID;
import static org.mifos.platform.util.CollectionUtils.isEmpty;

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
    public void validate(QuestionDetail questionDetail) throws SystemException {
        validateQuestionTitle(questionDetail);
        validateQuestionType(questionDetail);
    }

    @Override
    public void validate(QuestionGroupDetail questionGroupDetail) throws SystemException {
        validateQuestionGroupTitle(questionGroupDetail);
        validateQuestionGroupSections(questionGroupDetail);
        validateQuestionGroupEventSource(questionGroupDetail);
    }

    private void validateQuestionGroupEventSource(QuestionGroupDetail questionGroupDetail) throws SystemException {
        EventSource eventSource = questionGroupDetail.getEventSource();
        if (eventSource == null || StringUtils.isEmpty(eventSource.getSource()) || StringUtils.isEmpty(eventSource.getEvent()))
            throw new SystemException(QuestionnaireConstants.INVALID_EVENT_SOURCE);
        validateEventSource(eventSource);
    }

    private void validateEventSource(EventSource eventSource) throws SystemException {
        List result = eventSourceDao.retrieveCountByEventAndSource(eventSource.getEvent(), eventSource.getSource());
        if (isEmpty(result) || ((Long) result.get(0) == 0)) {
            throw new SystemException(QuestionnaireConstants.INVALID_EVENT_SOURCE);
        }
    }

    private void validateQuestionGroupSections(QuestionGroupDetail questionGroupDetail) throws SystemException {
        List<SectionDetail> sectionDetails = questionGroupDetail.getSectionDetails();
        if(isEmpty(sectionDetails)) {
            throw new SystemException(QuestionnaireConstants.QUESTION_GROUP_SECTION_NOT_PROVIDED);
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

    private void validateQuestionType(QuestionDetail questionDefinition) throws SystemException {
        if (INVALID == questionDefinition.getType())
            throw new SystemException(QUESTION_TYPE_NOT_PROVIDED);
    }

    private void validateQuestionTitle(QuestionDetail questionDefinition) throws SystemException {
        if (StringUtils.isEmpty(questionDefinition.getTitle()))
            throw new SystemException(QUESTION_TITLE_NOT_PROVIDED);
    }
}
