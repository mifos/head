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

import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.util.CollectionUtils;
import org.mifos.platform.util.MapEntry;

import java.io.Serializable;
import java.util.Map;


public class Question implements Serializable{
    private static final long serialVersionUID = -2584259958410679795L;
    private QuestionDetail questionDetail;
    private Map<String, QuestionType> stringToQuestionTypeMap;
    private Map<QuestionType, String> questionTypeToStringMap;

    public Question() {
        this(new QuestionDetail());
    }

    public Question(QuestionDetail questionDetail) {
        this.questionDetail = questionDetail;
        populateStringToQuestionTypeMap();
        populateQuestionTypeToStringMap();
    }

    @org.hibernate.validator.constraints.NotEmpty
    @javax.validation.constraints.Size(min=1,max=50)
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

    private void populateStringToQuestionTypeMap() {
        stringToQuestionTypeMap = CollectionUtils.asMap(MapEntry.makeEntry("Free text", QuestionType.FREETEXT),
                MapEntry.makeEntry("Date", QuestionType.DATE),
                MapEntry.makeEntry("Number", QuestionType.NUMERIC));
    }

    private void populateQuestionTypeToStringMap() {
        questionTypeToStringMap = CollectionUtils.asMap(MapEntry.makeEntry(QuestionType.FREETEXT, "Free text"),
                MapEntry.makeEntry(QuestionType.DATE, "Date"),
                MapEntry.makeEntry(QuestionType.NUMERIC, "Number"));
    }
}
