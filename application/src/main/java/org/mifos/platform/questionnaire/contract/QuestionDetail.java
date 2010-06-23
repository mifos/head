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

package org.mifos.platform.questionnaire.contract;

public class QuestionDetail {
    private Integer id;

    private String text;

    private String shortName;

    private QuestionType type;

    public QuestionDetail(Integer id, String text, String shortName, QuestionType type) {
        this.id = id;
        this.text = text;
        this.shortName = shortName;
        this.type = type;
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


    public QuestionType getType() {
        return type;
    }
}
