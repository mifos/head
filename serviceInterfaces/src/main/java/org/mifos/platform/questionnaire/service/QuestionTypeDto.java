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

import java.io.Serializable;

public class QuestionTypeDto implements Serializable {
    private static final long serialVersionUID = 2992814510112708328L;

    private final QuestionType questionType;

    public QuestionTypeDto() {
        this(QuestionType.INVALID);
    }

    protected QuestionTypeDto(QuestionType questionType) {
        this.questionType = questionType;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    @SuppressWarnings("PMD")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuestionTypeDto that = (QuestionTypeDto) o;

        if (questionType != that.questionType) return false;

        return true;
    }

    @SuppressWarnings("PMD")
    @Override
    public int hashCode() {
        return questionType != null ? questionType.hashCode() : 0;
    }

    public boolean isMultiSelectQuestion() {
        return QuestionType.MULTI_SELECT.equals(getQuestionType());
    }

    public boolean isNumeric() {
        return QuestionType.NUMERIC.equals(questionType);
    }
}
