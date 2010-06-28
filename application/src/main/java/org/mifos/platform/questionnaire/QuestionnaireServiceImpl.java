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

import org.mifos.customers.surveys.business.Question;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.contract.*;
import org.mifos.platform.questionnaire.domain.QuestionGroup;
import org.mifos.platform.questionnaire.mappers.QuestionnaireMapper;
import org.mifos.platform.questionnaire.mappers.QuestionnaireMapperImpl;
import org.mifos.platform.questionnaire.persistence.QuestionDao;
import org.mifos.platform.questionnaire.persistence.QuestionGroupDao;
import org.mifos.platform.questionnaire.validators.QuestionnaireValidator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class QuestionnaireServiceImpl implements QuestionnaireService {

    @Autowired
    private QuestionnaireValidator questionnaireValidator;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private QuestionGroupDao questionGroupDao;

    @Autowired
    private QuestionnaireMapper questionnaireMapper;

    @SuppressWarnings({"UnusedDeclaration"})
    private QuestionnaireServiceImpl() {
    }

    public QuestionnaireServiceImpl(QuestionnaireValidator questionnaireValidator, QuestionDao questionDao,
                                    QuestionnaireMapperImpl questionnaireMapper, QuestionGroupDao questionGroupDao) {
        this.questionnaireValidator = questionnaireValidator;
        this.questionDao = questionDao;
        this.questionnaireMapper = questionnaireMapper;
        this.questionGroupDao = questionGroupDao;
    }

    @Override
    public QuestionDetail defineQuestion(QuestionDefinition questionDefinition) throws ApplicationException {
        questionnaireValidator.validate(questionDefinition);
        Question question = questionnaireMapper.mapToQuestion(questionDefinition);
        persistQuestion(question);
        return questionnaireMapper.mapToQuestionDetail(question);
    }

    @Override
    public List<QuestionDetail> getAllQuestions() {
        List<Question> questions = questionDao.getDetailsAll();
        return questionnaireMapper.mapToQuestionDetails(questions);
    }

    @Override
    public QuestionGroupDetail defineQuestionGroup(QuestionGroupDefinition questionGroupDefinition) throws ApplicationException {
        questionnaireValidator.validate(questionGroupDefinition);
        QuestionGroup questionGroup = questionnaireMapper.mapToQuestionGroup(questionGroupDefinition);
        questionGroupDao.create(questionGroup);
        return questionnaireMapper.mapToQuestionGroupDetail(questionGroup);
    }

    @Override
    public List<QuestionGroupDetail> getAllQuestionGroups() {
        List<QuestionGroup> questionGroups = questionGroupDao.getDetailsAll();
        return questionnaireMapper.mapToQuestionGroupDetails(questionGroups);
    }

    @Override
    public boolean isDuplicateQuestion(QuestionDefinition questionDefinition) {
        List result = questionDao.retrieveCountOfQuestionsWithTitle(questionDefinition.getTitle());
        return (Long) result.get(0) > 0;
    }

    @Override
    public QuestionGroupDetail getQuestionGroup(int questionGroupId) throws ApplicationException {
        QuestionGroup questionGroup = questionGroupDao.getDetails(questionGroupId);
        if (questionGroup == null) {
            throw new ApplicationException(QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND);
        }
        return questionnaireMapper.mapToQuestionGroupDetail(questionGroup);
    }

    @Override
    public QuestionDetail getQuestion(int questionId) throws ApplicationException {
        Question question = questionDao.getDetails(questionId);
        if (question == null) {
            throw new ApplicationException(QuestionnaireConstants.QUESTION_NOT_FOUND);
        }
        return questionnaireMapper.mapToQuestionDetail(question);
    }

    private void persistQuestion(Question question) throws ApplicationException {
        try {
            questionDao.create(question);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new ApplicationException(QuestionnaireConstants.DUPLICATE_QUESTION, e);
        }
    }

}
