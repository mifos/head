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
import org.mifos.platform.questionnaire.contract.QuestionDefinition;
import org.mifos.platform.questionnaire.contract.QuestionDetail;
import org.mifos.platform.questionnaire.contract.QuestionnaireService;
import org.mifos.platform.questionnaire.mappers.QuestionnaireMapper;
import org.mifos.platform.questionnaire.mappers.QuestionnaireMapperImpl;
import org.mifos.platform.questionnaire.persistence.QuestionnaireDao;
import org.mifos.platform.questionnaire.validators.QuestionValidator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class QuestionnaireServiceImpl implements QuestionnaireService {

    @Autowired
    private QuestionValidator questionValidator;

    @Autowired
    private QuestionnaireDao questionnaireDao;
    
    @Autowired
    private QuestionnaireMapper questionnaireMapper;

    @SuppressWarnings({"UnusedDeclaration"})
    private QuestionnaireServiceImpl() {
    }

    public QuestionnaireServiceImpl(QuestionValidator questionValidator, QuestionnaireDao questionnaireDao,
                                    QuestionnaireMapperImpl questionnaireMapper) {
        this.questionValidator = questionValidator;
        this.questionnaireDao = questionnaireDao;
        this.questionnaireMapper = questionnaireMapper;
    }

    @Override
    public QuestionDetail defineQuestion(QuestionDefinition questionDefinition) throws ApplicationException {
        questionValidator.validate(questionDefinition);
        Question question = questionnaireMapper.mapToQuestion(questionDefinition);
        persistQuestion(question);
        return questionnaireMapper.mapToQuestionDetail(question);
    }

    private void persistQuestion(Question question) throws ApplicationException {
        try {
            questionnaireDao.create(question);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new ApplicationException(QuestionnaireConstants.DUPLICATE_QUESTION, e);
        }
    }

    @Override
    public List<QuestionDetail> getAllQuestions() {
        List<Question> questions = questionnaireDao.getDetailsAll();
        return questionnaireMapper.mapToQuestionDetails(questions);
    }
    
}
