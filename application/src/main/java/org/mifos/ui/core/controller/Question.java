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

package org.mifos.ui.core.controller;

import org.mifos.framework.util.CollectionUtils;
import org.mifos.platform.questionnaire.contract.QuestionDetail;
import org.mifos.platform.questionnaire.contract.QuestionType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static org.mifos.framework.util.MapEntry.makeEntry;
import static org.mifos.platform.questionnaire.contract.QuestionType.*;

public class Question implements Serializable {
    private static final long serialVersionUID = -2584259958410679795L;
    private static Map<String, QuestionType> stringToQuestionTypeMap;
    private static Map<QuestionType, String> questionTypeToStringMap;
    private QuestionDetail questionDetail;
    private String choice;

    static {
        populateStringToQuestionTypeMap();
        populateQuestionTypeToStringMap();
    }

    public Question() {
        this(new QuestionDetail());
    }

    public Question(QuestionDetail questionDetail) {
        this.questionDetail = questionDetail;
    }

    @org.hibernate.validator.constraints.NotEmpty
    @javax.validation.constraints.Size(min = 1, max = 50)
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

    @org.hibernate.validator.constraints.NotEmpty
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

    public List<String> getChoices() {
        return this.questionDetail.getAnswerChoices();
    }

    public String getCommaSeparateChoices() {
        String listAsString = this.questionDetail.getAnswerChoices().toString();
        return listAsString.substring(1, listAsString.length() - 1);
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public void addAnswerChoice() {
        getChoices().add(getChoice());
        setChoice(null);
    }

    public void removeChoice(int choiceIndex) {
        getChoices().remove(choiceIndex);
    }

    public void setChoicesIfApplicable() {
        QuestionType type = questionDetail.getType();
        if (!answerChoicesApplicableFor(type)) {
            questionDetail.setAnswerChoices(new ArrayList<String>());
        }
    }

    public boolean answerChoicesAreInvalid() {
        return answerChoicesApplicableFor(questionDetail.getType()) && getChoices().size() < 2;
    }

    private boolean answerChoicesApplicableFor(QuestionType type) {
        return MULTI_SELECT.equals(type) || SINGLE_SELECT.equals(questionDetail.getType());
    }

    private static void populateStringToQuestionTypeMap() {
        stringToQuestionTypeMap = CollectionUtils.asMap(
                makeEntry(getResource("questionnaire.quesiton.choices.freetext"), FREETEXT),
                makeEntry(getResource("questionnaire.quesiton.choices.date"), DATE),
                makeEntry(getResource("questionnaire.quesiton.choices.multiselect"), MULTI_SELECT),
                makeEntry(getResource("questionnaire.quesiton.choices.singleselect"), SINGLE_SELECT),
                makeEntry(getResource("questionnaire.quesiton.choices.number"), NUMERIC));
    }

    private static void populateQuestionTypeToStringMap() {
        questionTypeToStringMap = CollectionUtils.asMap(makeEntry(FREETEXT, getResource("questionnaire.quesiton.choices.freetext")),
                makeEntry(DATE, getResource("questionnaire.quesiton.choices.date")),
                makeEntry(NUMERIC, getResource("questionnaire.quesiton.choices.number")),
                makeEntry(MULTI_SELECT, getResource("questionnaire.quesiton.choices.multiselect")),
                makeEntry(SINGLE_SELECT, getResource("questionnaire.quesiton.choices.singleselect"))
        );
    }

    private static String getResource(String key) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("org.mifos.ui.localizedProperties.questionnaire_messages");
        return resourceBundle.getString(key);
    }
}
