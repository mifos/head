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

package org.mifos.platform.questionnaire.mappers;

import org.mifos.customers.surveys.business.Question;
import org.mifos.customers.surveys.helpers.AnswerType;
import org.mifos.customers.surveys.helpers.QuestionState;
import org.mifos.platform.questionnaire.contract.QuestionDefinition;
import org.mifos.platform.questionnaire.contract.QuestionDetail;
import org.mifos.platform.questionnaire.contract.QuestionType;

import java.util.*;

public class QuestionnaireMapperImpl implements QuestionnaireMapper {
    private Map<AnswerType, QuestionType> answerToQuestionType;
    private Map<QuestionType, AnswerType> questionToAnswerType;

    public QuestionnaireMapperImpl() {
        populateAnswerToQuestionTypeMap();
        populateQuestionToAnswerTypeMap();
    }

    @Override
    public List<QuestionDetail> mapToQuestionDetails(List<Question> questions) {
        List<QuestionDetail> questionDetails = new ArrayList<QuestionDetail>();
        for (Question question : questions) {
            questionDetails.add(mapToQuestionDetail(question));
        }
        return questionDetails;
    }

    @Override
    public QuestionDetail mapToQuestionDetail(Question question) {
        return new QuestionDetail(question.getQuestionId(),
                question.getQuestionText(),
                question.getShortName(),
                mapToQuestionType(question.getAnswerTypeAsEnum()));
    }

    @Override
    public Question mapToQuestion(QuestionDefinition questionDefinition) {
        Question question = new Question();
        question.setShortName(questionDefinition.getTitle());
        question.setQuestionText(questionDefinition.getTitle());
        question.setAnswerType(mapToAnswerType(questionDefinition.getType()));
        question.setQuestionState(QuestionState.ACTIVE);
        return question;
    }

    private QuestionType mapToQuestionType(AnswerType answerType) {
        return answerToQuestionType.get(answerType);
    }

    private AnswerType mapToAnswerType(QuestionType questionType) {
        return questionToAnswerType.get(questionType);
    }

    private void populateAnswerToQuestionTypeMap() {
        answerToQuestionType = new HashMap<AnswerType, QuestionType>();
        answerToQuestionType.put(AnswerType.INVALID, QuestionType.INVALID);
        answerToQuestionType.put(AnswerType.FREETEXT, QuestionType.FREETEXT);
        answerToQuestionType.put(AnswerType.DATE, QuestionType.DATE);
        answerToQuestionType.put(AnswerType.NUMBER, QuestionType.NUMERIC);
    }

    private void populateQuestionToAnswerTypeMap() {
        questionToAnswerType = new HashMap<QuestionType, AnswerType>();
        questionToAnswerType.put(QuestionType.INVALID, AnswerType.INVALID);
        questionToAnswerType.put(QuestionType.FREETEXT, AnswerType.FREETEXT);
        questionToAnswerType.put(QuestionType.DATE, AnswerType.DATE);
        questionToAnswerType.put(QuestionType.NUMERIC, AnswerType.NUMBER);
    }

}