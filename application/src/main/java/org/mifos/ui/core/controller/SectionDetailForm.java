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

import org.mifos.platform.questionnaire.contract.SectionDefinition;
import org.mifos.platform.questionnaire.contract.SectionQuestionDetail;

import java.util.ArrayList;
import java.util.List;

public class SectionDetailForm {
    private SectionDefinition sectionDefinition;

    public SectionDetailForm(SectionDefinition sectionDefinition) {
        this.sectionDefinition = sectionDefinition;
    }

    public String getName() {
        return sectionDefinition.getName();
    }

    public List<SectionQuestionDetailForm> getSectionQuestions() {
        List<SectionQuestionDetailForm> questionDetailForms = new ArrayList<SectionQuestionDetailForm>();
        for (SectionQuestionDetail sectionQuestionDetail : sectionDefinition.getQuestions()) {
            questionDetailForms.add(new SectionQuestionDetailForm(sectionQuestionDetail));
        }
        return questionDetailForms;
    }
}
