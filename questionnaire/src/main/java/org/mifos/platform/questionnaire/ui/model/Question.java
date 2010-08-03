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

package org.mifos.platform.questionnaire.ui.model;

import org.mifos.platform.questionnaire.mappers.QuestionTypeDetailFactory;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.util.CollectionUtils;
import org.mifos.platform.util.MapEntry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@SuppressWarnings("PMD")
public class Question implements Serializable {
    private static final long serialVersionUID = -2584259958410679795L;
    private static Map<String, QuestionType> stringToQuestionTypeMap;
    private static Map<QuestionType, String> questionTypeToStringMap;
    private QuestionDetail questionDetail;
    private String currentChoice;

    static {
        populateStringToQuestionTypeMap();
        populateQuestionTypeToStringMap();
    }

    public Question() {
        //TODO: spring autowiring requires default constructor
    }
    public Question(QuestionDetail questionDetail) {
        this.questionDetail = questionDetail;
    }

    @javax.validation.constraints.NotNull
    @javax.validation.constraints.Pattern(regexp="^.*[^\\s]+.*$")
    @javax.validation.constraints.Size(max = 200)
    public String getTitle() {
        return questionDetail.getTitle();
    }

    public void setTitle(String title) {
        questionDetail.setTitle(title);
        trimTitle();
    }

    public void trimTitle() {
        questionDetail.trimTitle();
    }

    @javax.validation.constraints.Pattern(regexp="^.*[^\\s]+.*$")
    @javax.validation.constraints.NotNull
    public String getType() {
        return questionTypeToStringMap.get(questionDetail.getQuestionTypeDetail().getQuestionType());
    }

    public void setType(String type) {
        QuestionType questionType = stringToQuestionTypeMap.get(type);
        if (questionType == null) {
            questionType = QuestionType.INVALID;
        }
        QuestionTypeDetailFactory questionTypeDetailFactory = QuestionTypeDetailFactory.getInstance();
        questionDetail.setQuestionTypeDetail(questionTypeDetailFactory.getQuestionTypeDetail(questionType));
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

    public List<String> getChoices() {
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
        questionDetail.addAnswerChoice(getCurrentChoice());
        setCurrentChoice(null);
    }

    public void removeChoice(int choiceIndex) {
        questionDetail.removeAnswerChoice(choiceIndex);
    }

    public void setChoicesIfApplicable() {
        QuestionType type = questionDetail.getQuestionTypeDetail().getQuestionType();
        if (!answerChoicesApplicableFor(type)) {
            questionDetail.setAnswerChoices(new ArrayList<String>());
        }
    }

    public boolean answerChoicesAreInvalid() {
        return answerChoicesApplicableFor(questionDetail.getQuestionTypeDetail().getQuestionType()) && getChoices().size() < 2;
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

    private boolean answerChoicesApplicableFor(QuestionType type) {
        return QuestionType.MULTI_SELECT.equals(type) || QuestionType.SINGLE_SELECT.equals(questionDetail.getQuestionTypeDetail().getQuestionType());
    }

    private static void populateStringToQuestionTypeMap() {
        stringToQuestionTypeMap = CollectionUtils.asMap(
                MapEntry.makeEntry(getResource("questionnaire.quesiton.choices.freetext"), QuestionType.FREETEXT),
                MapEntry.makeEntry(getResource("questionnaire.quesiton.choices.date"), QuestionType.DATE),
                MapEntry.makeEntry(getResource("questionnaire.quesiton.choices.multiselect"), QuestionType.MULTI_SELECT),
                MapEntry.makeEntry(getResource("questionnaire.quesiton.choices.singleselect"), QuestionType.SINGLE_SELECT),
                MapEntry.makeEntry(getResource("questionnaire.quesiton.choices.number"), QuestionType.NUMERIC));
    }

    private static void populateQuestionTypeToStringMap() {
        questionTypeToStringMap = CollectionUtils.asMap(MapEntry.makeEntry(QuestionType.FREETEXT, getResource("questionnaire.quesiton.choices.freetext")),
                MapEntry.makeEntry(QuestionType.DATE, getResource("questionnaire.quesiton.choices.date")),
                MapEntry.makeEntry(QuestionType.NUMERIC, getResource("questionnaire.quesiton.choices.number")),
                MapEntry.makeEntry(QuestionType.MULTI_SELECT, getResource("questionnaire.quesiton.choices.multiselect")),
                MapEntry.makeEntry(QuestionType.SINGLE_SELECT, getResource("questionnaire.quesiton.choices.singleselect"))
        );
    }

    private static String getResource(String key) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("org.mifos.platform.questionnaire.ui.localizedProperties.questionnaire_messages");
        return resourceBundle.getString(key);
    }

    public boolean numericBoundsAreInvalid() {
        boolean result = false;
        if (QuestionType.NUMERIC.equals(questionDetail.getQuestionTypeDetail().getQuestionType())) {
            Integer min = getNumericMin();
            Integer max = getNumericMax();
            result = min != null && max != null && min > max;
        }
        return result;
    }
}
