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

public class SectionQuestion implements Serializable {
    //TODO: For the time being to resolve dependencies
    // should extend AbstractEntity? move AbstractEntity to common module first
    private static final long serialVersionUID = -439615520685608649L;

    private int id;
    private Integer sequenceNumber;
    private boolean required;
    private QuestionEntity question;
    private Section section;
    private boolean showOnPage;

    public boolean isShowOnPage() {
		return showOnPage;
	}

	public void setShowOnPage(boolean showOnPage) {
		this.showOnPage = showOnPage;
	}

	public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public QuestionEntity getQuestion() {
        return question;
    }

    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getQuestionText() {
        return question.getQuestionText();
    }

    public boolean isNewSectionQuestion() {
        return id <= 0;
    }
}
