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

import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuestionGroupDetail implements Serializable {
    private static final long serialVersionUID = 5240884292277900071L;

    private Integer id;
    private String title;
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "SE_BAD_FIELD")
    private List<SectionDetail> sectionDetails;
    private EventSourceDto eventSourceDto;
    private boolean editable;
    private boolean active;

    public QuestionGroupDetail() {
        this(0, null, null, new ArrayList<SectionDetail>(), false);
    }

    public QuestionGroupDetail(int id, String title, List<SectionDetail> sectionDetails) {
        this(id, title, null, sectionDetails, false);
    }

    public QuestionGroupDetail(int id, String title, EventSourceDto eventSourceDto, List<SectionDetail> sectionDetails, boolean editable) {
        this(id, title, eventSourceDto, sectionDetails, editable, false);
    }

    public QuestionGroupDetail(int id, String title, EventSourceDto eventSourceDto, List<SectionDetail> sectionDetails, boolean editable, boolean active) {
        this.id = id;
        this.title = title;
        this.sectionDetails = sectionDetails;
        this.eventSourceDto = eventSourceDto;
        this.editable = editable;
        this.active = active;
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

    public EventSourceDto getEventSource() {
        return eventSourceDto;
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

    public void setEventSource(EventSourceDto eventSourceDto) {
        this.eventSourceDto = eventSourceDto;
    }

    public SectionDetail getSectionDetail(int i) {
        if (i >= this.sectionDetails.size()) {
            this.sectionDetails.add(new SectionDetail());
        }
        return this.sectionDetails.get(i);
    }

    public boolean isEditable() {
        return this.editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isActive() {
        return active;
    }
}
