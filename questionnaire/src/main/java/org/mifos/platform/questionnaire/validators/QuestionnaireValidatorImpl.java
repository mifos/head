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
import org.mifos.platform.questionnaire.domain.AnswerType;
import org.mifos.platform.questionnaire.domain.QuestionChoiceEntity;
import org.mifos.platform.questionnaire.domain.QuestionEntity;
import org.mifos.platform.questionnaire.exceptions.BadNumericResponseException;
import org.mifos.platform.questionnaire.exceptions.MandatoryAnswerNotFoundException;
import org.mifos.platform.questionnaire.exceptions.ValidationException;
import org.mifos.platform.questionnaire.persistence.EventSourceDao;
import org.mifos.platform.questionnaire.persistence.QuestionDao;
import org.mifos.platform.questionnaire.persistence.QuestionGroupDao;
import org.mifos.platform.questionnaire.service.dtos.ChoiceDto;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mifos.platform.questionnaire.service.dtos.QuestionDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.SectionDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.mifos.platform.questionnaire.QuestionnaireConstants.*; //NOPMD
import static org.mifos.platform.util.CollectionUtils.isEmpty;
import static org.mifos.platform.util.CollectionUtils.isNotEmpty;

@SuppressWarnings("PMD")
public class QuestionnaireValidatorImpl implements QuestionnaireValidator {

    @Autowired
    private EventSourceDao eventSourceDao;

    @Autowired
    private QuestionDao questionDao;

    @SuppressWarnings({"UnusedDeclaration"})
    private QuestionnaireValidatorImpl() {
    }

    public QuestionnaireValidatorImpl(EventSourceDao eventSourceDao, QuestionGroupDao questionGroupDao, QuestionDao questionDao) {
        this.eventSourceDao = eventSourceDao;
        this.questionDao = questionDao;
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
    public void validateForEventSource(EventSourceDto eventSourceDto) throws SystemException {
        if (eventSourceDto == null || StringUtils.isEmpty(eventSourceDto.getSource()) || StringUtils.isEmpty(eventSourceDto.getEvent()))
            throw new SystemException(INVALID_EVENT_SOURCE);
        validateEventSource(eventSourceDto);
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

    @Override
    public void validateForDefineQuestionGroup(QuestionGroupDto questionGroupDto) {
        ValidationException parentException = new ValidationException(GENERIC_VALIDATION);
        validateQuestionGroupTitle(questionGroupDto, parentException);
        validateEventSource(questionGroupDto.getEventSourceDto(), parentException);
        validateSections(questionGroupDto.getSections(), parentException);
        if (parentException.containsChildExceptions()) throw parentException;
    }

    private void validateSections(List<SectionDto> sections, ValidationException parentException) {
        if (isEmpty(sections)) {
            parentException.addChildException(new ValidationException(QUESTION_GROUP_SECTION_NOT_PROVIDED));
        } else {
            if (!sectionsHaveInvalidNames(sections, parentException) && !sectionsHaveInvalidOrders(sections, parentException)) {
                for (SectionDto section : sections) {
                    validateSection(section, parentException);
                }
            }
        }
    }

    private void validateSection(SectionDto section, ValidationException parentException) {
        validateSectionName(section, parentException);
        validateQuestions(section.getQuestions(), parentException);
    }

    private void validateSectionName(SectionDto section, ValidationException parentException) {
        String name = section.getName().trim();
        if (name.length() >= MAX_LENGTH_FOR_TITILE) {
            parentException.addChildException(new ValidationException(SECTION_NAME_TOO_BIG));
        }
    }

    private void validateQuestions(List<QuestionDto> questions, ValidationException parentException) {
        if (isEmpty(questions)) {
            parentException.addChildException(new ValidationException(NO_QUESTIONS_FOUND_IN_SECTION));
        } else {
            if (!questionsHaveInvalidNames(questions, parentException) && !questionsHaveInvalidOrders(questions, parentException)) {
                for (QuestionDto question : questions) {
                    validateQuestion(question, parentException);
                }
            }
        }
    }

    @Override
    public void validateForDefineQuestion(QuestionDto questionDto) {
        ValidationException parentException = new ValidationException(GENERIC_VALIDATION);
        validateQuestion(questionDto, parentException);
        if (parentException.containsChildExceptions()) {
            throw parentException;
        }
    }

    private void validateQuestion(QuestionDto question, ValidationException parentException) {
        if (StringUtils.isEmpty(question.getTitle())) {
            parentException.addChildException(new ValidationException(QUESTION_TITLE_NOT_PROVIDED));
        } else if (question.getTitle().length() >= MAX_LENGTH_FOR_TITILE) {
            parentException.addChildException(new ValidationException(QUESTION_TITLE_TOO_BIG));
        } else if (questionHasDuplicateTitle(question)) {
            parentException.addChildException(new ValidationException(QUESTION_TITILE_MATCHES_EXISTING_QUESTION));
        } else {
            if (QuestionType.INVALID == question.getType()) {
                parentException.addChildException(new ValidationException(QUESTION_TYPE_NOT_PROVIDED));
            } else if (question.isTypeWithChoices()) {
                validateChoices(question, parentException);
            } else if (QuestionType.NUMERIC == question.getType()) {
                validateNumericBounds(question, parentException);
            }
        }
    }

    private void validateNumericBounds(QuestionDto question, ValidationException parentException) {
        if (areInValidNumericBounds(question.getMinValue(), question.getMaxValue())) {
            parentException.addChildException(new ValidationException(INVALID_NUMERIC_BOUNDS));
        }
    }

    private void validateChoices(QuestionDto question, ValidationException parentException) {
        List<ChoiceDto> choices = question.getChoices();
        if (isEmpty(choices) || choices.size() < MAX_CHOICES_FOR_QUESTION) {
            parentException.addChildException(new ValidationException(QUESTION_CHOICES_INSUFFICIENT));
        } else if (choicesHaveInvalidValues(choices) || choicesHaveInvalidOrders(choices)) {
            parentException.addChildException(new ValidationException(QUESTION_CHOICES_INVALID));
        }
    }

    private boolean choicesHaveInvalidValues(List<ChoiceDto> choiceDtos) {
        return !allChoicesHaveValues(choiceDtos) || !allChoicesHaveUniqueValues(choiceDtos);
    }

    private boolean choicesHaveInvalidOrders(List<ChoiceDto> choiceDtos) {
        return !allChoicesHaveOrders(choiceDtos) || !allChoicesHaveUniqueOrders(choiceDtos);
    }

    private boolean allChoicesHaveUniqueOrders(List<ChoiceDto> choiceDtos) {
        boolean result = true;
        Set<Integer> choiceOrders = new HashSet<Integer>();
        for (ChoiceDto choiceDto : choiceDtos) {
            Integer order = choiceDto.getOrder();
            if (choiceOrders.contains(order)) {
                result = false;
                break;
            } else {
                choiceOrders.add(order);
            }
        }
        return result;
    }

    private boolean allChoicesHaveOrders(List<ChoiceDto> choiceDtos) {
        boolean result = true;
        for (ChoiceDto choiceDto : choiceDtos) {
            if (null == choiceDto.getOrder()) {
                result = false;
                break;
            }
        }
        return result;
    }

    private boolean allChoicesHaveValues(List<ChoiceDto> choiceDtos) {
        boolean result = true;
        for (ChoiceDto choiceDto : choiceDtos) {
            choiceDto.trimValue();
            if (StringUtils.isEmpty(choiceDto.getValue())) {
                result = false;
                break;
            }
        }
        return result;
    }

    private boolean allChoicesHaveUniqueValues(List<ChoiceDto> choiceDtos) {
        boolean result = true;
        Set<String> choiceValues = new HashSet<String>();
        for (ChoiceDto choiceDto : choiceDtos) {
            String value = choiceDto.getValue().toLowerCase(Locale.getDefault());
            if (choiceValues.contains(value)) {
                result = false;
                break;
            } else {
                choiceValues.add(value);
            }
        }
        return result;
    }

    private boolean questionHasDuplicateTitle(QuestionDto question) {
        List<QuestionEntity> questions = questionDao.retrieveByName(question.getTitle());
        boolean result = false;
        if (isNotEmpty(questions)) {
            QuestionEntity questionEntity = questions.get(0);
            result = !areSameQuestionTypes(question.getType(), questionEntity.getAnswerTypeAsEnum()) || haveIncompatibleChoices(question, questionEntity);
        }
        return result;
    }

    private boolean haveIncompatibleChoices(QuestionDto question, QuestionEntity questionEntity) {
        List<ChoiceDto> choiceDtos = question.getChoices();
        List<QuestionChoiceEntity> choiceEntities = questionEntity.getChoices();
        boolean result = false;
        if (choiceDtos != null && choiceEntities != null) {
            result = choiceDtos.size() != choiceEntities.size();
            for (int i = 0, choiceDetailsSize = choiceDtos.size(); i < choiceDetailsSize && !result; i++) {
                String choiceValue = choiceDtos.get(i).getValue();
                result = isUniqueChoice(choiceEntities, choiceValue);
            }
        }
        return result;
    }

    private boolean isUniqueChoice(List<QuestionChoiceEntity> choiceEntities, String choiceValue) {
        boolean uniqueChoice = true;
        for (QuestionChoiceEntity choiceEntity : choiceEntities) {
            if (StringUtils.equalsIgnoreCase(choiceValue, choiceEntity.getChoiceText())) {
                uniqueChoice = false;
                break;
            }
        }
        return uniqueChoice;
    }

    private boolean areSameQuestionTypes(QuestionType type, AnswerType answerType) {
        boolean result;
        switch(type) {
            case FREETEXT:
                result = AnswerType.FREETEXT == answerType;
                break;
            case SMART_SELECT:
                result = AnswerType.SMARTSELECT == answerType;
                break;
            case SINGLE_SELECT:
                result = AnswerType.CHOICE == answerType || AnswerType.SINGLESELECT == answerType;
                break;
            case DATE:
                result = AnswerType.DATE == answerType;
                break;
            case NUMERIC:
                result = AnswerType.NUMBER == answerType;
                break;
            case MULTI_SELECT:
                result = AnswerType.MULTISELECT == answerType;
                break;
            default:
                result = false;
        }
        return result;
    }

    private boolean questionsHaveInvalidOrders(List<QuestionDto> questions, ValidationException parentException) {
        boolean invalid = false;
        if (!allQuestionsHaveOrders(questions)) {
            parentException.addChildException(new ValidationException(QUESTION_ORDER_NOT_PROVIDED));
            invalid = true;
        } else if(!allQuestionsHaveUniqueOrders(questions)) {
            parentException.addChildException(new ValidationException(QUESTION_ORDER_DUPLICATE));
            invalid = true;
        }
        return invalid;
    }

    private boolean questionsHaveInvalidNames(List<QuestionDto> questions, ValidationException parentException) {
        boolean invalid = false;
        if (!allQuestionsHaveNames(questions)) {
            parentException.addChildException(new ValidationException(QUESTION_TITLE_NOT_PROVIDED));
            invalid = true;
        } else if(!allQuestionsHaveUniqueNames(questions)) {
            parentException.addChildException(new ValidationException(QUESTION_TITLE_DUPLICATE));
            invalid = true;
        }
        return invalid;
    }

    private boolean allQuestionsHaveUniqueNames(List<QuestionDto> questions) {
        boolean result = true;
        Set<String> questionNames = new HashSet<String>();
        for (QuestionDto question : questions) {
            String name = question.getTitle().toLowerCase(Locale.getDefault());
            if (questionNames.contains(name)) {
                result = false;
                break;
            } else {
                questionNames.add(name);
            }
        }
        return result;
    }

    private boolean sectionsHaveInvalidOrders(List<SectionDto> sections, ValidationException parentException) {
        boolean invalid = false;
        if (!allSectionsHaveOrders(sections)) {
            parentException.addChildException(new ValidationException(SECTION_ORDER_NOT_PROVIDED));
            invalid = true;
        } else if(!allSectionsHaveUniqueOrders(sections)) {
            parentException.addChildException(new ValidationException(SECTION_ORDER_DUPLICATE));
            invalid = true;
        }
        return invalid;
    }

    private boolean allSectionsHaveUniqueOrders(List<SectionDto> sections) {
        boolean result = true;
        Set<Integer> sectionOrders = new HashSet<Integer>();
        for (SectionDto section : sections) {
            Integer order = section.getOrder();
            if (sectionOrders.contains(order)) {
                result = false;
                break;
            } else {
                sectionOrders.add(order);
            }
        }
        return result;
    }

    private boolean allQuestionsHaveUniqueOrders(List<QuestionDto> questions) {
        boolean result = true;
        Set<Integer> sectionOrders = new HashSet<Integer>();
        for (QuestionDto question : questions) {
            Integer order = question.getOrder();
            if (sectionOrders.contains(order)) {
                result = false;
                break;
            } else {
                sectionOrders.add(order);
            }
        }
        return result;
    }

    private boolean allSectionsHaveOrders(List<SectionDto> sections) {
        boolean result = true;
        for (SectionDto section : sections) {
            if (null == section.getOrder()) {
                result = false;
                break;
            }
        }
        return result;
    }

    private boolean allQuestionsHaveOrders(List<QuestionDto> questions) {
        boolean result = true;
        for (QuestionDto question : questions) {
            if (null == question.getOrder()) {
                result = false;
                break;
            }
        }
        return result;
    }

    private boolean sectionsHaveInvalidNames(List<SectionDto> sections, ValidationException parentException) {
        boolean invalid = false;
        if (!allSectionsHaveNames(sections)) {
            parentException.addChildException(new ValidationException(SECTION_TITLE_NOT_PROVIDED));
            invalid = true;
        } else if(!allSectionsHaveUniqueNames(sections)) {
            parentException.addChildException(new ValidationException(SECTION_TITLE_DUPLICATE));
            invalid = true;
        }
        return invalid;
    }

    private boolean allSectionsHaveUniqueNames(List<SectionDto> sections) {
        boolean result = true;
        Set<String> sectionNames = new HashSet<String>();
        for (SectionDto section : sections) {
            String name = section.getName().toLowerCase(Locale.getDefault());
            if (sectionNames.contains(name)) {
                result = false;
                break;
            } else {
                sectionNames.add(name);
            }
        }
        return result;
    }

    private boolean allSectionsHaveNames(List<SectionDto> sections) {
        boolean result = true;
        for (SectionDto section : sections) {
            section.trimName();
            if (StringUtils.isEmpty(section.getName())) {
                result = false;
                break;
            }
        }
        return result;
    }

    private boolean allQuestionsHaveNames(List<QuestionDto> questions) {
        boolean result = true;
        for (QuestionDto questionDto : questions) {
            questionDto.trimTitle();
            if (StringUtils.isEmpty(questionDto.getTitle())) {
                result = false;
                break;
            }
        }
        return result;
    }

    private void validateEventSource(EventSourceDto eventSourceDto, ValidationException parentException) {
        if (eventSourceDto == null || eventSourceDto.getEvent() == null || eventSourceDto.getSource() == null) {
            parentException.addChildException(new ValidationException(INVALID_EVENT_SOURCE));
        } else {
            try {
                validateEventSource(eventSourceDto);
            } catch (SystemException e) {
                parentException.addChildException(new ValidationException(e.getKey()));
            }
        }
    }

    private void validateQuestionGroupTitle(QuestionGroupDto questionGroupDto, ValidationException parentException) {
        String title = questionGroupDto.getTitle();
        if (StringUtils.isEmpty(title)) {
            parentException.addChildException(new ValidationException(QUESTION_GROUP_TITLE_NOT_PROVIDED));
        } else {
            title = title.trim();
            if (title.length() >= MAX_LENGTH_FOR_TITILE) {
                parentException.addChildException(new ValidationException(QUESTION_GROUP_TITLE_TOO_BIG));
            }
        }
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

    private void validateEventSource(EventSourceDto eventSourceDto) throws SystemException {
        List<Long> result = eventSourceDao.retrieveCountByEventAndSource(eventSourceDto.getEvent(), eventSourceDto.getSource());
        if (isEmpty(result) || result.get(0) == 0) {
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
        for (SectionDetail sectionDetail : sectionDetails) {
            validateSectionDefinition(sectionDetail);
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
        if (QuestionType.INVALID == questionDetail.getType())
            throw new SystemException(QUESTION_TYPE_NOT_PROVIDED);
        if (QuestionType.NUMERIC == questionDetail.getType())
            validateForNumericQuestionType(questionDetail.getNumericMin(), questionDetail.getNumericMax());
    }

    private void validateForNumericQuestionType(Integer min, Integer max) {
        if (areInValidNumericBounds(min, max))
            throw new SystemException(INVALID_NUMERIC_BOUNDS);
    }

    private boolean areInValidNumericBounds(Integer min, Integer max) {
        return min != null && max != null && min > max;
    }

    private void validateQuestionTitle(QuestionDetail questionDefinition) throws SystemException {
        if (StringUtils.isEmpty(questionDefinition.getTitle()))
            throw new SystemException(QUESTION_TITLE_NOT_PROVIDED);
    }
}
