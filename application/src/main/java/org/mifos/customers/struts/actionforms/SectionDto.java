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
import org.mifos.platform.questionnaire.contract.SectionDetail;
import org.mifos.platform.questionnaire.contract.SectionQuestionDetail;

import java.util.ArrayList;
import java.util.List;

public class SectionDto implements DataTransferObject {
    private static final long serialVersionUID = 5714435976562961102L;

    private SectionDetail sectionDetail;

    public SectionDto(SectionDetail sectionDetail) {
        this.sectionDetail = sectionDetail;
    }

    public String getName() {
        return sectionDetail.getName();
    }

    public List<QuestionDto> getQuestions() {
        List<QuestionDto> questions = new ArrayList<QuestionDto>();
        for (SectionQuestionDetail sectionQuestionDetail : sectionDetail.getQuestions()) {
            questions.add(new QuestionDto(sectionQuestionDetail));
        }
        return questions;
    }

    public QuestionDto getQuestion(int i) {
        List<QuestionDto> questions = getQuestions();
        return i < questions.size() ? questions.get(i) : new QuestionDto(sectionDetail.getQuestionDetail(i));
    }
}
