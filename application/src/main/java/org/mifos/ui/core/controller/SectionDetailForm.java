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

import org.mifos.platform.questionnaire.contract.SectionDetail;
import org.mifos.platform.questionnaire.contract.SectionQuestionDetail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.trim;

public class SectionDetailForm implements Serializable {
    private static final long serialVersionUID = 900851228874986225L;
    private SectionDetail sectionDetail;

    public SectionDetailForm() {
        this(new SectionDetail());
    }

    public SectionDetailForm(SectionDetail sectionDetail) {
        this.sectionDetail = sectionDetail;
    }

    public String getName() {
        return sectionDetail.getName();
    }

    public List<SectionQuestionDetailForm> getSectionQuestions() {
        List<SectionQuestionDetailForm> questionDetailForms = new ArrayList<SectionQuestionDetailForm>();
        for (SectionQuestionDetail sectionQuestionDetail : sectionDetail.getQuestions()) {
            questionDetailForms.add(new SectionQuestionDetailForm(sectionQuestionDetail));
        }
        return questionDetailForms;
    }

    public List<SectionQuestionDetail> getSectionQuestionDetails() {
        return sectionDetail.getQuestions();
    }

    public SectionDetail getSectionDetail() {
        return sectionDetail;
    }

    public void trimName() {
        sectionDetail.setName(trim(getName()));
    }

    public void setName(String name) {
        sectionDetail.setName(name);
    }

    public void addSectionQuestion(SectionQuestionDetail sectionQuestionDetail) {
        sectionDetail.addQuestion(sectionQuestionDetail);
    }

    public void setQuestionDetails(List<SectionQuestionDetail> sectionQuestionDetails) {
        sectionDetail.setQuestionDetails(sectionQuestionDetails);
    }
}
