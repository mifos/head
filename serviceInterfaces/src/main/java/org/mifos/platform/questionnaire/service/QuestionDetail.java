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

package org.mifos.platform.questionnaire.service;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("PMD")
public class QuestionDetail implements Serializable {
    private static final long serialVersionUID = -2350908554610624733L;

    private Integer id;
    private String text;
    private String shortName;
    private QuestionType type;
    private List<String> answerChoices;

    public QuestionDetail() {
        this(null, QuestionType.INVALID);
    }

    public QuestionDetail(String text, QuestionType type) {
        this(0, text, text, type);
    }

    public QuestionDetail(Integer id, String text, String shortName, QuestionType type) {
        this(id, text, shortName, type, new ArrayList<String>());
    }

    public QuestionDetail(String title, QuestionType type, List<String> answerChoices) {
        this(0, title, title, type, answerChoices);
    }

    public QuestionDetail(Integer id, String text, String shortName, QuestionType type, List<String> answerChoices) {
        this.id = id;
        this.text = text;
        this.shortName = shortName;
        this.type = type;
        this.answerChoices = answerChoices;
    }

    public Integer getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getShortName() {
        return shortName;
    }

    public String getTitle() {
        return shortName;
    }

    public QuestionType getType() {
        return type;
    }

    public void setTitle(String title) {
        this.shortName = title;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void trimTitle() {
        this.shortName = StringUtils.trim(this.shortName);
    }

    public List<String> getAnswerChoices() {
        return answerChoices;
    }

    public void setAnswerChoices(List<String> answerChoices) {
        this.answerChoices = answerChoices;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuestionDetail that = (QuestionDetail) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
