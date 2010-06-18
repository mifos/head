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

package org.mifos.ui.core.controller;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuestionForm implements Serializable{
    private List<Question> questions = new ArrayList<Question>();
    private Question currentQuestion = new Question();

    public String getTitle() {
        return currentQuestion.getTitle();
    }

    public void setTitle(String title) {
        currentQuestion.setTitle(title);
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void addCurrentQuestion() {
        currentQuestion.trimTitle();
        questions.add(currentQuestion);
        currentQuestion = new Question();
    }

    public boolean isDuplicateTitle(String questionTitle) {
        if(StringUtils.isEmpty(questionTitle)){
            return false;
        }
        for (Question question: questions){
            if(StringUtils.equalsIgnoreCase(questionTitle.trim(), question.getTitle())){
                return true;
            }
        }
        return false;
    }

}
