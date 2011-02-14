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

package org.mifos.test.acceptance.framework.questionnaire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateQuestionGroupParameters {
    private String title;
    private String appliesTo;
    private Map<String, List<CreateQuestionParameters>> newQuestions;
    private Map<String, List<String>> existingQuestions;
    private boolean answerEditable = false;

    public CreateQuestionGroupParameters() {
        super();
        newQuestions = new HashMap<String, List<CreateQuestionParameters>>();
        existingQuestions = new HashMap<String, List<String>>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAppliesTo(String appliesTo) {
        this.appliesTo = appliesTo;
    }

    public String getAppliesTo() {
        return appliesTo;
    }

    public boolean isAnswerEditable() {
        return answerEditable;
    }

    public void setAnswerEditable(boolean editable) {
        this.answerEditable = editable;
    }

    public Map<String, List<String>> getExistingQuestions() {
        return this.existingQuestions;
    }

    public void setExistingQuestions(Map<String, List<String>> existingQuestions) {
        this.existingQuestions = existingQuestions;
    }


    public Map<String, List<CreateQuestionParameters>> getNewQuestions() {
        return this.newQuestions;
    }

    public void setNewQuestions(Map<String, List<CreateQuestionParameters>> newQuestions) {
        this.newQuestions = newQuestions;
    }

    public void addExistingQuestion(String section,String question){
        if(!this.existingQuestions.containsKey(section)){
            this.existingQuestions.put(section, new ArrayList<String>());
        }
        this.existingQuestions.get(section).add(question);
    }

    public void addNewQuestion(String section, CreateQuestionParameters question){
        if(!this.newQuestions.containsKey(section)){
            this.newQuestions.put(section, new ArrayList<CreateQuestionParameters>());
        }
        this.newQuestions.get(section).add(question);
    }
}
