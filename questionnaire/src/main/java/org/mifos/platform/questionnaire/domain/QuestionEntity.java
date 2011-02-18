/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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
package org.mifos.platform.questionnaire.domain;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("PMD")
public class QuestionEntity implements Serializable, Comparable<QuestionEntity> {
    private static final long serialVersionUID = -2L;

    private int questionId;

    private AnswerType answerType;

    private QuestionState questionState;

    private String questionText;

    private String nickname;

    private Integer numericMin;

    private Integer numericMax;

    @SuppressWarnings(value="SE_BAD_FIELD")
    private List<QuestionChoiceEntity> choices;

    public QuestionEntity() {
        this(null);
    }

    public QuestionEntity(String questionText) {
        this(questionText, AnswerType.FREETEXT);
    }

    public QuestionEntity(String questionText, AnswerType answerType) {
        this(questionText, answerType, null);
    }

    public QuestionEntity(String questionText, AnswerType answerType,List<QuestionChoiceEntity> choices) {
        this.questionText = questionText;
        this.answerType = answerType;
        this.choices = choices;
        this.questionState = QuestionState.ACTIVE;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public List<QuestionChoiceEntity> getChoices() {
        return choices;
    }

    public void setChoices(List<QuestionChoiceEntity> choices) {
        this.choices = choices;
    }

    public void addChoice(QuestionChoiceEntity choice) {
        getChoices().add(choice);
    }

    @Override
    @SuppressWarnings("PMD.OnlyOneReturn")
    public final boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof QuestionEntity)) {
            return false;
        }

        QuestionEntity question = (QuestionEntity) o;
        return question.getQuestionId() == getQuestionId();
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(getQuestionId()).hashCode();
    }

    @Override
	public int compareTo(QuestionEntity other) {
        return getQuestionText().compareTo(other.getQuestionText());
    }

    public boolean isActive() {
        return QuestionState.ACTIVE.equals(questionState) || QuestionState.ACTIVE_NOT_EDITABLE.equals(questionState);
    }

    public boolean isEditable() {
        return QuestionState.ACTIVE.equals(questionState) || QuestionState.INACTIVE.equals(questionState);
    }
}

