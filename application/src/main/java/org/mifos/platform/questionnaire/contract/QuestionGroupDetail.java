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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuestionGroupDetail implements Serializable {
    private static final long serialVersionUID = 5240884292277900071L;

    private Integer id;
    private String title;
    private List<SectionDetail> sectionDetails;
    private EventSource eventSource;

    public QuestionGroupDetail() {
        this(0, null, null, new ArrayList<SectionDetail>());
    }

    public QuestionGroupDetail(int id, String title, List<SectionDetail> sectionDetails) {
        this(id, title, null, sectionDetails);
    }

    public QuestionGroupDetail(int id, String title, EventSource eventSource, List<SectionDetail> sectionDetails) {
        this.id = id;
        this.title = title;
        this.sectionDetails = sectionDetails;
        this.eventSource = eventSource;
    }

    public String getTitle() {
        return title;
    }

    public Integer getId() {
        return id;
    }

    public List<SectionDetail> getSectionDetails() {
        return sectionDetails;
    }

    public EventSource getEventSource() {
        return eventSource;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSectionDetails(List<SectionDetail> sectionDetails) {
        this.sectionDetails = sectionDetails;
    }

    public void setEventSource(EventSource eventSource) {
        this.eventSource = eventSource;
    }
}
