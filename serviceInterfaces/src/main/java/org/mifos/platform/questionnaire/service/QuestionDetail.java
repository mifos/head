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
import org.mifos.platform.questionnaire.service.dtos.ChoiceDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("PMD")
public class QuestionDetail implements Serializable {
    private static final long serialVersionUID = -2350908554610624733L;

    private Integer id;
    private String text;
    private String nickname;
    private QuestionType type;
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_BAD_FIELD")
    private List<ChoiceDto> answerChoices;
    private Integer numericMin;
    private Integer numericMax;
    private boolean active;

    public QuestionDetail() {
        this(null, QuestionType.INVALID);
    }

    public QuestionDetail(String text, QuestionType type) {
        this(0, text, type, true);
    }

    public QuestionDetail(Integer id, String text, QuestionType type, boolean active) {
        this.id = id;
        this.text = text;
        this.type = type;
        this.answerChoices = new ArrayList<ChoiceDto>();
        this.active = active;
    }

    public Integer getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void trimText() {
        this.text = StringUtils.trim(this.text);
    }

    public List<ChoiceDto> getAnswerChoices() {
        return answerChoices;
    }

    public void setAnswerChoices(List<ChoiceDto> answerChoices) {
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

    public void setNumericMin(Integer numericMin) {
        this.numericMin = numericMin;
    }

    public void setNumericMax(Integer numericMax) {
        this.numericMax = numericMax;
    }

    public Integer getNumericMin() {
        return numericMin;
    }

    public Integer getNumericMax() {
        return numericMax;
    }

    public void addAnswerChoice(ChoiceDto answerChoice) {
        answerChoices.add(answerChoice);
    }

    public void removeAnswerChoice(int choiceIndex) {
        answerChoices.remove(choiceIndex);
    }

    public void addTag(int choiceIndex, String tag) {
        if (choiceIndex < answerChoices.size()) {
            answerChoices.get(choiceIndex).addTag(tag);
        }
    }

    public boolean isSmartSelect() {
        return QuestionType.SMART_SELECT.equals(type);
    }

    public void removeTagForChoice(int choiceIndex, int tagIndex) {
        answerChoices.get(choiceIndex).removeTag(tagIndex);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isNewQuestion() {
        return id == null || id <= 0;
    }

    public boolean isSmartChoiceDuplicated(int choiceIndex, String tag) {
        boolean result = false;

        if (choiceIndex < answerChoices.size()) {
            result = answerChoices.get(choiceIndex).isDuplicateTag(tag);
        }

        return result;
    }

    public boolean isTagsLimitReached(int choiceIndex) {
        boolean result = false;

        if (choiceIndex < answerChoices.size()) {
            result = answerChoices.get(choiceIndex).isTagsLimitReached();
        }

        return result;
    }
}
