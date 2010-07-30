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

import org.mifos.platform.util.CollectionUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.mifos.platform.util.CollectionUtils.isNotEmpty;

@SuppressWarnings("PMD")
public class SectionQuestionDetail implements Serializable {
    private static final long serialVersionUID = -6756173977268032788L;

    private boolean mandatory;
    private QuestionDetail questionDetail;
    private String value;

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_BAD_FIELD")
    private List<String> values;

    private int id;

    public SectionQuestionDetail() {
        this(new QuestionDetail(), false);
    }

    public SectionQuestionDetail(QuestionDetail questionDetail, boolean mandatory) {
        this(questionDetail, mandatory, null);
    }

    public SectionQuestionDetail(QuestionDetail questionDetail, boolean mandatory, String value) {
        this(0, questionDetail, mandatory, value);
    }

    public SectionQuestionDetail(int id, QuestionDetail questionDetail, boolean required) {
        this(id, questionDetail, required, null);
    }

    public SectionQuestionDetail(int id, QuestionDetail questionDetail, boolean mandatory, String value) {
        this(id, questionDetail, mandatory, value, new LinkedList<String>());
    }

    public SectionQuestionDetail(int id, QuestionDetail questionDetail, boolean mandatory, String value, List<String> values) {
        this.id = id;
        this.questionDetail = questionDetail;
        this.mandatory = mandatory;
        this.value = value;
        this.values = values;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestionId() {
        return questionDetail.getId();
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public String getTitle() {
        return questionDetail.getTitle();
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public QuestionType getQuestionType() {
        return questionDetail.getType();
    }

    public List<String> getAnswerChoices() {
        return questionDetail.getAnswerChoices();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public boolean hasNoAnswer() {
        return !hasAnswer();
    }

    public boolean hasAnswer() {
        return !isEmpty(this.value) || !CollectionUtils.isEmpty(this.values);
    }

    @Override
    @SuppressWarnings("PMD.OnlyOneReturn")
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SectionQuestionDetail that = (SectionQuestionDetail) o;

        if (questionDetail != null ? !questionDetail.equals(that.questionDetail) : that.questionDetail != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return questionDetail != null ? questionDetail.hashCode() : 0;
    }

    public boolean isMultiSelectQuestion() {
        return QuestionType.MULTI_SELECT.equals(getQuestionType());
    }

    public List<String> getAnswers() {
        List<String> answers = new LinkedList<String>();
        if (hasAnswer()) {
            if (isMultiSelectQuestion()) {
                answers.addAll(getValues());
            } else {
                answers.add(getValue());
            }
        }
        return answers;
    }

    public String getAnswer() {
        List<String> answers = getAnswers();
        return isNotEmpty(answers) ? CollectionUtils.toString(answers) : EMPTY;
    }

    //Used for legacy Struts binding support.
    @Deprecated
    public String[] getValuesAsArray() {
        return values.toArray(new String[values.size()]);
    }

    //Used for legacy Struts binding support
    @Deprecated
    public void setValuesAsArray(String[] valuesArr) {
        this.values = Arrays.asList(valuesArr);
    }
}
