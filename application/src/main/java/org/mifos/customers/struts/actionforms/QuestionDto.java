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

package org.mifos.customers.struts.actionforms;

import org.mifos.framework.business.service.DataTransferObject;
import org.mifos.platform.questionnaire.service.ChoiceDetail;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;

import java.util.List;

public class QuestionDto implements DataTransferObject {
    private static final long serialVersionUID = 1759235897826098794L;

    private final SectionQuestionDetail sectionQuestionDetail;

    public QuestionDto(SectionQuestionDetail sectionQuestionDetail) {
        this.sectionQuestionDetail = sectionQuestionDetail;
    }

    public int getId() {
        return sectionQuestionDetail.getQuestionId();
    }

    public String getText() {
        return sectionQuestionDetail.getTitle();
    }

    public boolean isRequired() {
        return sectionQuestionDetail.isMandatory();
    }

    public QuestionType getQuestionType() {
        return sectionQuestionDetail.getQuestionType();
    }

    public int getQuestionTypeAsNum() {
        return sectionQuestionDetail.getQuestionType().ordinal();
    }

    public List<ChoiceDetail> getAnswerChoices() {
        return sectionQuestionDetail.getAnswerChoices();
    }

    public String getRequiredString() {
        return String.valueOf(sectionQuestionDetail.isMandatory());
    }

    public String getValue() {
        return sectionQuestionDetail.getValue();
    }

    public void setValue(String value) {
        this.sectionQuestionDetail.setValue(value);
    }
}
