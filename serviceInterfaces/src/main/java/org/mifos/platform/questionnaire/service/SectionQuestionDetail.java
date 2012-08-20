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

package org.mifos.platform.questionnaire.service;

import org.mifos.platform.questionnaire.service.dtos.ChoiceDto;
import org.mifos.platform.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isEmpty;

@SuppressWarnings("PMD")
public class SectionQuestionDetail implements Serializable {
    private static final long serialVersionUID = -6756173977268032788L;

    private boolean mandatory;
    private QuestionDetail questionDetail;
    private String value;
    private Integer sequenceNumber;

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "SE_BAD_FIELD")
    private List<SelectionDetail> selections = new ArrayList<SelectionDetail>();

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

    public SectionQuestionDetail(int id, QuestionDetail questionDetail, boolean required, int sequenceNumber) {
        this(id, questionDetail, required, null);
        this.sequenceNumber = sequenceNumber;
    }

    public SectionQuestionDetail(int id, QuestionDetail questionDetail, boolean required) {
        this(id, questionDetail, required, null);
    }

    public SectionQuestionDetail(int id, QuestionDetail questionDetail, boolean mandatory, String value) {
        this(id, questionDetail, mandatory, value, new LinkedList<SelectionDetail>());
    }

    public SectionQuestionDetail(int id, QuestionDetail questionDetail, boolean mandatory, String value, List<SelectionDetail> selections) {
        this.id = id;
        this.questionDetail = questionDetail;
        this.mandatory = mandatory;
        this.value = value;
        this.selections = selections;
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

    public String getText() {
        return questionDetail.getText();
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public QuestionType getQuestionType() {
        return questionDetail.getType();
    }

    public List<ChoiceDto> getAnswerChoices() {
        return questionDetail.getAnswerChoices();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<SelectionDetail> getSelections() {
        return selections;
    }

    public void setSelections(List<SelectionDetail> selections) {
        this.selections = selections;
    }

    public boolean hasNoAnswer() {
        return !hasAnswer();
    }

    public boolean hasAnswer() {
        return !isEmpty(this.value) || !CollectionUtils.isEmpty(this.selections);
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
        QuestionType questionType = getQuestionType();
        return QuestionType.MULTI_SELECT.equals(questionType) ||
                QuestionType.SMART_SELECT.equals(questionType) ||
                QuestionType.SMART_SINGLE_SELECT.equals(questionType);
    }

    public boolean isNumeric() {
        return QuestionType.NUMERIC.equals(getQuestionType());
    }

    public Integer getNumericMin() {
        return questionDetail.getNumericMin();
    }

    public Integer getNumericMax() {
        return questionDetail.getNumericMax();
    }

    public boolean isActive() {
        return questionDetail.isActive();
    }

    public boolean isInactive() {
        return !isActive();
    }

    //Used for legacy Struts binding support.
    @Deprecated
    public String[] getValuesAsArray() {
        return getValues();
    }

    //Used for legacy Struts binding support
    @Deprecated
    public void setValuesAsArray(String[] valuesArr) {
        setValues(valuesArr);
        removeDefaultSelection();
    }

    public String[] getValues() {
        String[] values = new String[selections.size()];
        for (int i = 0, selectionsSize = selections.size(); i < selectionsSize; i++) {
            values[i] = selections.get(i).toString();
        }
        return values;
    }

    public void setValues(String[] valuesArr) {
        this.selections = getSelections(valuesArr);
    }

    private List<SelectionDetail> getSelections(String[] valuesArr) {
        List<SelectionDetail> selectionDetails = new ArrayList<SelectionDetail>();
        for (String value : valuesArr) {
            selectionDetails.add(new SelectionDetail(value));
        }
        return selectionDetails;
    }

    /*  This is a work around for the bug in Struts 1 tag html:multibox
        The array binding does not happen if none of the values are selected
        i.e the setter won't be called at all, this can create issues
        when user un selects all values which were selected before
        The work around introduces a hidden value which is always selected,
        which ensures the binding always happen
    */
    @Deprecated
    private void removeDefaultSelection() {
        if (CollectionUtils.isNotEmpty(selections)) {
            selections.remove(0);
        }
    }

    public QuestionDetail getQuestionDetail() {
        return questionDetail;
    }

    public void setQuestionDetail(QuestionDetail questionDetail) {
        this.questionDetail = questionDetail;
    }

    public String getMultiSelectValue() {
        return CollectionUtils.toString(this.selections);
    }

    public String getAnswer() {
        return isMultiSelectQuestion()? getMultiSelectValue(): (isEmpty(this.value)? EMPTY: this.value);
    }

    public Integer getSequenceNumber() {
        return this.sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public boolean isNotActive() {
        return questionDetail.isNotActive();
    }
}
