/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.customers.surveys.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.mifos.customers.ppi.business.PPIChoice;
import org.mifos.customers.surveys.SurveysConstants;
import org.mifos.customers.surveys.helpers.AnswerType;
import org.mifos.customers.surveys.helpers.QuestionState;
import org.mifos.framework.formulaic.DateValidator;
import org.mifos.framework.formulaic.NumberValidator;
import org.mifos.framework.formulaic.OneOfValidator;
import org.mifos.framework.formulaic.ValidationError;

public class Question implements Serializable, Comparable<Question> {
    private int questionId;

    private AnswerType answerType;

    private QuestionState questionState;

    private String shortName;

    private String questionText;

    private Integer numericMin;

    private Integer numericMax;

    private List<QuestionChoice> choices = new LinkedList<QuestionChoice>();

    public Object validate(Object objectData) throws ValidationError {
        String data = (String) objectData;
        if (answerType == AnswerType.FREETEXT) {
            return null;
        } else if (answerType == AnswerType.NUMBER) {
            BigDecimal min = numericMin == null ? null : new BigDecimal(numericMin);
            BigDecimal max = numericMax == null ? null : new BigDecimal(numericMax);
            return new NumberValidator(min, max).validate(data);
        } else if (answerType == AnswerType.CHOICE) {
            List<String> choicesStrings = new LinkedList<String>();
            for (QuestionChoice choice : choices) {
                choicesStrings.add(Integer.toString(choice.getChoiceId()));
            }
            return new OneOfValidator(choicesStrings).validate(objectData);
        } else if (answerType == AnswerType.MULTISELECT) {
            return objectData;
        } else if (answerType == AnswerType.DATE) {
            DateValidator d = new DateValidator();
            return d.validate(objectData);
        }
        return data;
    }

    public Question() {
        this(null);
    }

    public Question(String questionText) {
        this(questionText, AnswerType.FREETEXT);
    }

    public Question(String questionText, AnswerType answerType) {
        this(null, questionText, answerType);
    }

    public Question(String shortName, String questionText, AnswerType answerType) {
        setShortName(shortName);
        setQuestionText(questionText);
        setAnswerType(answerType);
        questionState = QuestionState.ACTIVE;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }

    public Integer getNumericMax() {
        return numericMax;
    }

    public void setNumericMax(Integer numericMax) {
        this.numericMax = numericMax;
    }

    public Integer getNumericMin() {
        return numericMin;
    }

    public void setNumericMin(Integer numericMin) {
        this.numericMin = numericMin;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public QuestionState getQuestionStateAsEnum() {
        return questionState;
    }

    public int getQuestionState() {
        return questionState.getValue();
    }

    public void setQuestionState(int state) {
        this.questionState = QuestionState.fromInt(state);
    }

    public void setQuestionState(QuestionState questionState) {
        this.questionState = questionState;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public AnswerType getAnswerTypeAsEnum() {
        return answerType;
    }

    public int getAnswerType() {
        return answerType.getValue();
    }

    public void setAnswerType(AnswerType answerType) {
        this.answerType = answerType;
    }

    public void setAnswerType(int answerType) {
        this.answerType = AnswerType.fromInt(answerType);
    }

    @Override
    public String toString() {
        return "<Question " + questionId + " \"" + questionText + "\">";
    }

    public List<QuestionChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<QuestionChoice> choices) {
        this.choices = choices;
    }

    public void addChoice(QuestionChoice choice) {
        getChoices().add(choice);
    }

    public int getQuestionType() {
        if (answerType == AnswerType.CHOICE) {
            for (QuestionChoice choice : choices) {
                if (choice instanceof PPIChoice)
                    return SurveysConstants.QUESTION_TYPE_PPI;
            }
        }
        return SurveysConstants.QUESTION_TYPE_GENERAL;
    }

    @Override
    public final boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof Question)) {
            return false;
        }

        Question question = (Question) o;
        return question.getQuestionId() == getQuestionId();
    }

    @Override
    public int hashCode() {
        return new Integer(getQuestionId()).hashCode();
    }

    public int compareTo(Question other) {
        return getShortName().compareTo(other.getShortName());
    }

}
