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

import org.mifos.platform.questionnaire.contract.EventSource;
import org.mifos.platform.questionnaire.contract.QuestionGroupDetail;
import org.mifos.platform.questionnaire.contract.SectionDefinition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isEmpty;

public class QuestionGroupDetailForm implements Serializable {
    private static final long serialVersionUID = -7545625058942409636L;
    private QuestionGroupDetail questionGroupDetail;
    private String eventSourceId;

    public QuestionGroupDetailForm(QuestionGroupDetail questionGroupDetail) {
        this.questionGroupDetail = questionGroupDetail;
        setEventSourceId();
    }

    public String getTitle() {
        return questionGroupDetail.getTitle();
    }

    public String getEventSourceId() {
        return eventSourceId;
    }

    private void setEventSourceId() {
        EventSource eventSource = this.questionGroupDetail.getEventSource();
        if (eventSource == null || isEmpty(eventSource.getEvent()) || isEmpty(eventSource.getSource())) return;
        eventSourceId = format("%s.%s", eventSource.getEvent(), eventSource.getSource());
    }

    public List<SectionDetailForm> getSections() {
        List<SectionDetailForm> sectionDetails = new ArrayList<SectionDetailForm>();
        for (SectionDefinition sectionDefinition : questionGroupDetail.getSectionDefinitions()) {
            sectionDetails.add(new SectionDetailForm(sectionDefinition));
        }
        return sectionDetails;
    }
}
