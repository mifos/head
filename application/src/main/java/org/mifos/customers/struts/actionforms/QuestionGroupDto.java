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
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.SectionDetail;

import java.util.ArrayList;
import java.util.List;

public class QuestionGroupDto implements DataTransferObject {
    private static final long serialVersionUID = -3740125391034068655L;

    private QuestionGroupDetail questionGroupDetail;

    public QuestionGroupDto() {
        this(new QuestionGroupDetail());
    }

    public QuestionGroupDto(QuestionGroupDetail questionGroupDetail) {
        this.questionGroupDetail = questionGroupDetail;
    }

    public List<SectionDto> getSections() {
        List<SectionDto> sections = new ArrayList<SectionDto>();
        for (SectionDetail sectionDetail : questionGroupDetail.getSectionDetails()) {
            sections.add(new SectionDto(sectionDetail));
        }
        return sections;
    }

    public SectionDto getSection(int i) {
        List<SectionDto> sections = getSections();
        return i < sections.size() ? sections.get(i) : new SectionDto(questionGroupDetail.getSectionDetail(i));
    }

    public int getId() {
        return questionGroupDetail.getId();
    }

    public QuestionGroupDetail getQuestionGroupDetail() {
        return questionGroupDetail;
    }
}
