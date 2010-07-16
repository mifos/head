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

package org.mifos.platform.questionnaire.ui.model;

import org.apache.commons.lang.StringUtils;
import org.mifos.platform.questionnaire.service.QuestionDetail;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("PMD")
public class QuestionForm extends ScreenObject {
    private static final long serialVersionUID = 2010225942240327677L;

    private List<Question> questions = new ArrayList<Question>();

    @javax.validation.Valid
    private Question currentQuestion = new Question(new QuestionDetail());

    public Question getCurrentQuestion() {
        return this.currentQuestion;
    }

    public void setCurrentQuestion(Question currentQuestion) {
        this.currentQuestion = currentQuestion;
    }


    /*@org.hibernate.validator.constraints.NotEmpty
    @javax.validation.constraints.Size(max=50)
    public String getTitle() {
        return currentQuestion.getTitle();
    }

    public void setTitle(String title) {
        currentQuestion.setTitle(title);
    }*/

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void addCurrentQuestion() {
        currentQuestion.trimTitle();
        currentQuestion.setChoicesIfApplicable();
        questions.add(currentQuestion);
        currentQuestion = new Question(new QuestionDetail());
    }

    public boolean isDuplicateTitle(String questionTitle) {
        return (findQuestionByTitle(questionTitle) != null);
    }

    public void removeQuestion(String questionTitle) {
       Question question = findQuestionByTitle(questionTitle);
       if (question != null) {
         questions.remove(question);
       }
    }

    private Question findQuestionByTitle(String title) {
        if(!StringUtils.isBlank(title)){
            for (Question qt : questions){
                if(StringUtils.equalsIgnoreCase(title.trim(), qt.getTitle())){
                    return qt;
                }
            }
        }
        return null;
    }

    public boolean answerChoicesAreInvalid() {
        return currentQuestion.answerChoicesAreInvalid();
    }
}
