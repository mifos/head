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
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.contract.*;
import org.mifos.platform.questionnaire.persistence.EventSourceDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mifos.framework.util.CollectionUtils.isEmpty;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.*;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.NO_QUESTIONS_FOUND_IN_SECTION;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.DUPLICATE_QUESTION_FOUND_IN_SECTION;
import static org.mifos.platform.questionnaire.contract.QuestionType.INVALID;

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
    public void validate(QuestionDefinition questionDefinition) throws ApplicationException {
        validateQuestionTitle(questionDefinition);
        validateQuestionType(questionDefinition);
    }

    @Override
    public void validate(QuestionGroupDetail questionGroupDetail) throws ApplicationException {
        validateQuestionGroupTitle(questionGroupDetail);
        validateQuestionGroupSections(questionGroupDetail);
        validateQuestionGroupEventSource(questionGroupDetail);
    }

    private void validateQuestionGroupEventSource(QuestionGroupDetail questionGroupDetail) throws ApplicationException {
        EventSource eventSource = questionGroupDetail.getEventSource();
        if (eventSource == null || StringUtils.isEmpty(eventSource.getSource()) || StringUtils.isEmpty(eventSource.getEvent()))
            throw new ApplicationException(QuestionnaireConstants.INVALID_EVENT_SOURCE);
        validateEventSource(eventSource);
    }

    private void validateEventSource(EventSource eventSource) throws ApplicationException {
        List result = eventSourceDao.retrieveCountByEventAndSource(eventSource.getEvent(), eventSource.getSource());
        if (isEmpty(result) || ((Long) result.get(0) == 0)) {
            throw new ApplicationException(QuestionnaireConstants.INVALID_EVENT_SOURCE);
        }
    }

    private void validateQuestionGroupSections(QuestionGroupDetail questionGroupDetail) throws ApplicationException {
        List<SectionDetail> sectionDetails = questionGroupDetail.getSectionDetails();
        if(isEmpty(sectionDetails)) {
            throw new ApplicationException(QuestionnaireConstants.QUESTION_GROUP_SECTION_NOT_PROVIDED);
        }
        validateSectionDefinitions(sectionDetails);
    }

    private void validateSectionDefinitions(List<SectionDetail> sectionDetails) throws ApplicationException {
        Set<SectionQuestionDetail> questions = new HashSet<SectionQuestionDetail>();
        for (SectionDetail sectionDetail : sectionDetails) {
            validateSectionDefinition(sectionDetail);
            for (SectionQuestionDetail questionDetail : sectionDetail.getQuestions()) {
                if (!questions.add(questionDetail)) {
                    throw new ApplicationException(DUPLICATE_QUESTION_FOUND_IN_SECTION);
                }
            }
        }
    }

    private void validateSectionDefinition(SectionDetail sectionDetail) throws ApplicationException {
        if (isEmpty(sectionDetail.getQuestions())) {
            throw new ApplicationException(NO_QUESTIONS_FOUND_IN_SECTION);
        }
    }

    private void validateQuestionGroupTitle(QuestionGroupDetail questionGroupDetail) throws ApplicationException {
        if (StringUtils.isEmpty(questionGroupDetail.getTitle()))
            throw new ApplicationException(QUESTION_GROUP_TITLE_NOT_PROVIDED);
    }

    private void validateQuestionType(QuestionDefinition questionDefinition) throws ApplicationException {
        if (INVALID == questionDefinition.getType())
            throw new ApplicationException(QUESTION_TYPE_NOT_PROVIDED);
    }

    private void validateQuestionTitle(QuestionDefinition questionDefinition) throws ApplicationException {
        if (StringUtils.isEmpty(questionDefinition.getTitle()))
            throw new ApplicationException(QUESTION_TITLE_NOT_PROVIDED);
    }
}
