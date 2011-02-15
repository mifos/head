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

package org.mifos.platform.questionnaire.ui.model;

import org.apache.commons.lang.StringUtils;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.validation.ScreenObject;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("PMD")
public class QuestionForm extends ScreenObject {
    private static final long serialVersionUID = 2010225942240327677L;

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_BAD_FIELD")
    private List<Question> questions = new ArrayList<Question>();

    @javax.validation.Valid
    private Question currentQuestion = new Question(new QuestionDetail());

    public Question getCurrentQuestion() {
        return this.currentQuestion;
    }

    public void setCurrentQuestion(Question currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void addCurrentQuestion() {
        currentQuestion.setChoices();
        questions.add(currentQuestion);
        currentQuestion = new Question(new QuestionDetail());
    }

    public boolean isDuplicateTitle(String questionTitle) {
        return (findQuestionByText(questionTitle) != null);
    }

    private Question findQuestionByText(String text) {
        if(!StringUtils.isBlank(text)){
            for (Question qt : questions){
                if(StringUtils.equalsIgnoreCase(text.trim(), qt.getText())){
                    return qt;
                }
            }
        }
        return null;
    }

    public void removeQuestion(String questionTitle) {
        Question question = findQuestionByText(questionTitle);
        if (question != null) {
            questions.remove(question);
        }
    }

    public boolean answerChoicesAreInvalid() {
        return currentQuestion.answerChoicesAreInvalid();
    }

    public boolean numericBoundsAreInvalid() {
        return currentQuestion.numericBoundsAreInvalid();
    }

    public boolean textHasChanged() {
        return currentQuestion.textHasChanged();
    }
}
