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

package org.mifos.platform.questionnaire.ui.model;

import org.apache.commons.lang.StringUtils;
import org.mifos.platform.questionnaire.service.dtos.ChoiceDto;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.mifos.platform.util.CollectionUtils.isEmpty;
import static org.mifos.platform.util.MapEntry.makeEntry;

@SuppressWarnings("PMD")
public class Question implements Serializable {
    private static final long serialVersionUID = -2584259958410679795L;
    private static Map<String, QuestionType> stringToQuestionTypeMap;
    private static Map<QuestionType, String> questionTypeToStringMap;
    private QuestionDetail questionDetail;
    private String currentChoice;
    private String currentSmartChoice;
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_BAD_FIELD")
    private List<String> currentSmartChoiceTags;
    private int initialNumberOfChoices;
    private String originalText;

    static {
        populateStringToQuestionTypeMap();
        populateQuestionTypeToStringMap();
    }

    public Question() {
        this(null);
    }

    public Question(QuestionDetail questionDetail) {
        this.questionDetail = questionDetail;
        this.currentSmartChoiceTags = new ArrayList<String>();
    }

    @javax.validation.constraints.NotNull
    @javax.validation.constraints.Pattern(regexp="^.*[^\\s]+.*$")
    @javax.validation.constraints.Size(max = 1000)
    public String getText() {
        return questionDetail.getText();
    }

    public void setText(String text) {
        questionDetail.setText(text);
        questionDetail.trimText();
    }

    @javax.validation.constraints.Pattern(regexp="^.*[^\\s]+.*$")
    @javax.validation.constraints.NotNull
    public String getType() {
        return questionTypeToStringMap.get(questionDetail.getType());
    }

    public void setType(String type) {
        questionDetail.setType(stringToQuestionTypeMap.get(type));
    }

    public String getId() {
        return questionDetail.getId().toString();
    }

    public void setId(String id) {
        questionDetail.setId(Integer.valueOf(id));
    }

    public QuestionDetail getQuestionDetail() {
        return questionDetail;
    }

    public void setQuestionDetail(QuestionDetail questionDetail) {
        this.questionDetail=questionDetail;
        initializeSmartChoiceTags();
    }

    public List<ChoiceDto> getChoices() {
        return this.questionDetail.getAnswerChoices();
    }

    public String getCommaSeparateChoices() {
        return CollectionUtils.toString(this.questionDetail.getAnswerChoices());
    }

    public String getCurrentChoice() {
        return currentChoice;
    }

    public void setCurrentChoice(String currentChoice) {
        this.currentChoice = currentChoice;
    }

    public void addAnswerChoice() {
        questionDetail.addAnswerChoice(new ChoiceDto(getCurrentChoice()));
        setCurrentChoice(null);
        currentSmartChoiceTags.add(EMPTY);
    }

    public void removeChoice(int choiceIndex) {
        questionDetail.removeAnswerChoice(choiceIndex);
        currentSmartChoiceTags.remove(choiceIndex);
    }

    public void removeChoiceTag(String choiceTagIndex) {
        String[] indices = choiceTagIndex.split("_");
        int choiceIndex = Integer.valueOf(indices[0]);
        int tagIndex = Integer.valueOf(indices[1]);
        questionDetail.removeTagForChoice(choiceIndex, tagIndex);
    }

    public void addAnswerSmartChoice() {
        questionDetail.addAnswerChoice(new ChoiceDto(getCurrentSmartChoice()));
        currentSmartChoiceTags.add(EMPTY);
        setCurrentSmartChoice(null);
    }

    public void addSmartChoiceTag(int choiceIndex) {
        String currentSmartChoiceTag = getCurrentSmartChoiceTag(choiceIndex);
        if (isNotEmpty(currentSmartChoiceTag)) {
            questionDetail.addTag(choiceIndex, currentSmartChoiceTag);
            currentSmartChoiceTags.set(choiceIndex, EMPTY);
        }
    }

    public void setChoicesIfApplicable() {
        QuestionType type = questionDetail.getType();
        if (!answerChoicesApplicableFor(type)) {
            resetChoices();
        }
    }

    public boolean answerChoicesAreInvalid() {
        return answerChoicesApplicableFor(questionDetail.getType()) && getChoices().size() < 2;
    }

    public Integer getNumericMin() {
        return questionDetail.getNumericMin();
    }

    public Integer getNumericMax() {
        return questionDetail.getNumericMax();
    }

    public void setNumericMin(Integer numericMin) {
        questionDetail.setNumericMin(numericMin);
    }

    public void setNumericMax(Integer numericMax) {
        questionDetail.setNumericMax(numericMax);
    }

    public boolean numericBoundsAreInvalid() {
        boolean result = false;
        if (QuestionType.NUMERIC.equals(questionDetail.getType())) {
            Integer min = getNumericMin();
            Integer max = getNumericMax();
            result = min != null && max != null && min > max;
        }
        return result;
    }

    public String getCurrentSmartChoice() {
        return currentSmartChoice;
    }

    public void setCurrentSmartChoice(String currentSmartChoice) {
        this.currentSmartChoice = currentSmartChoice;
    }

    public List<String> getCurrentSmartChoiceTags() {
        return currentSmartChoiceTags;
    }

    public String getCurrentSmartChoiceTag(int index) {
        if (index >= currentSmartChoiceTags.size()) {
            currentSmartChoiceTags.add(EMPTY);
        }
        return currentSmartChoiceTags.get(index);
    }

    public boolean isSmartSelect() {
        return questionDetail.isSmartSelect();
    }

    public int getInitialNumberOfChoices() {
        return initialNumberOfChoices;
    }

    public boolean isActive() {
        return questionDetail.isActive();
    }

    public boolean isEditable() {
        return questionDetail.isEditable();
    }

    public void setActive(boolean active) {
        questionDetail.setActive(active);
    }

    private boolean answerChoicesApplicableFor(QuestionType type) {
        return QuestionType.MULTI_SELECT.equals(type) || QuestionType.SINGLE_SELECT.equals(type) || QuestionType.SMART_SELECT.equals(type) 
                || QuestionType.SMART_SINGLE_SELECT.equals(type);
    }

    private void resetChoices() {
        questionDetail.setAnswerChoices(new ArrayList<ChoiceDto>());
    }

    private void initializeSmartChoiceTags() {
        List<ChoiceDto> choices = getChoices();
        if (isEmpty(choices)) {
            choices = new ArrayList<ChoiceDto>();
        }
        this.currentSmartChoiceTags = new ArrayList<String>();
        this.initialNumberOfChoices = choices.size();
        this.originalText = getText();
        for (int i = 0; i < initialNumberOfChoices; i++) {
            this.currentSmartChoiceTags.add(EMPTY);
        }
    }

    private static void populateStringToQuestionTypeMap() {
        stringToQuestionTypeMap = CollectionUtils.asMap(
                makeEntry("freeText", QuestionType.FREETEXT),
                makeEntry("date", QuestionType.DATE),
                makeEntry("multiSelect", QuestionType.MULTI_SELECT),
                makeEntry("singleSelect", QuestionType.SINGLE_SELECT),
                makeEntry("smartSelect", QuestionType.SMART_SELECT),
                makeEntry("number", QuestionType.NUMERIC),
                makeEntry("smartSingleSelect", QuestionType.SMART_SINGLE_SELECT)
        );
    }

    private static void populateQuestionTypeToStringMap() {
        questionTypeToStringMap = CollectionUtils.asMap(
                makeEntry(QuestionType.FREETEXT, "freeText"),
                makeEntry(QuestionType.DATE, "date"),
                makeEntry(QuestionType.NUMERIC, "number"),
                makeEntry(QuestionType.MULTI_SELECT, "multiSelect"),
                makeEntry(QuestionType.SMART_SELECT, "smartSelect"),
                makeEntry(QuestionType.SINGLE_SELECT, "singleSelect"),
                makeEntry(QuestionType.SMART_SINGLE_SELECT, "smartSingleSelect")
        );
    }

    public boolean textHasChanged() {
        return !StringUtils.equals(originalText, getText());
    }

    void setChoices() {
        setChoicesIfApplicable();
    }

    public boolean isSmartChoiceDuplicated(int choiceIndex) {
        boolean result = false;

        String currentSmartChoiceTag = getCurrentSmartChoiceTag(choiceIndex);
        if (isNotEmpty(currentSmartChoiceTag)) {
            result = questionDetail.isSmartChoiceDuplicated(choiceIndex, currentSmartChoiceTag);
        }

        return result;
    }

    public boolean isTagsLimitReached(int choiceIndex) {
        return questionDetail.isTagsLimitReached(choiceIndex);
    }
}
