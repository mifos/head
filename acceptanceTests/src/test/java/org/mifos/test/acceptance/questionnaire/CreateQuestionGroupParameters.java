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

package org.mifos.test.acceptance.questionnaire;

import java.util.List;

public class CreateQuestionGroupParameters {
    private String title = "";
    private String appliesTo = "--select one--";
    private String sectionName = "";
    private List<String> sectionQuestions;

    public boolean isAnswerEditable() {
        return answerEditable;
    }

    private boolean answerEditable = false;

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

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public void setQuestions(List<String> sectionQuestions) {
        this.sectionQuestions = sectionQuestions;
    }

    public List<String> getSectionQuestions() {
        return sectionQuestions;
    }

    public void setAnswerEditable(boolean editable) {
        this.answerEditable = editable;
    }
}
