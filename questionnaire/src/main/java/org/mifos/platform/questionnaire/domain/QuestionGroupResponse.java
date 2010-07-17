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

package org.mifos.platform.questionnaire.domain;


import java.io.Serializable;

public class QuestionGroupResponse implements Serializable {
    private static final long serialVersionUID = 3649495389577852936L;

    private int id;
    private SectionQuestion sectionQuestion;
    private String response;
    private QuestionGroupInstance questionGroupInstance;

    @SuppressWarnings({"PMD.UnnecessaryConstructor","PMD.UncommentedEmptyConstructor"})
    public QuestionGroupResponse() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public SectionQuestion getSectionQuestion() {
        return sectionQuestion;
    }

    public void setSectionQuestion(SectionQuestion sectionQuestion) {
        this.sectionQuestion = sectionQuestion;
    }

    public QuestionGroupInstance getQuestionGroupInstance() {
        return questionGroupInstance;
    }

    public void setQuestionGroupInstance(QuestionGroupInstance questionGroupInstance) {
        this.questionGroupInstance = questionGroupInstance;
    }
}
