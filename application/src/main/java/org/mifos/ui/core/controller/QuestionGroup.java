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

import org.apache.commons.lang.StringUtils;
import org.mifos.platform.questionnaire.contract.EventSource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class QuestionGroup implements Serializable {
    private static final long serialVersionUID = 9142463851744584305L;
    private String title;
    private String eventSourceId;

    private String id;

    private List<SectionForm> sections = new ArrayList<SectionForm>();

    private SectionForm currentSection = new SectionForm();

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void trimTitle() {
        this.title = StringUtils.trim(this.title);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<SectionForm> getSections() {
        return sections;
    }

    public void setSections(List<SectionForm> sections) {
        this.sections = sections;
    }

    public void setEventSourceId(String eventSourceId) {
        this.eventSourceId = eventSourceId;
    }

    public String getEventSourceId() {
        return eventSourceId;
    }

    public EventSource getEventSource() {
        if (StringUtils.isNotEmpty(eventSourceId)) {
            String[] parts = eventSourceId.split("\\.");
            return new EventSource(parts[0], parts[1], eventSourceId);
        }
        return null;
    }

    public void setEventSource(EventSource eventSource) {
        if (eventSource == null || StringUtils.isEmpty(eventSource.getEvent()) || StringUtils.isEmpty(eventSource.getSource())) return;
        eventSourceId = format("%s.%s", eventSource.getEvent(), eventSource.getSource());
    }

    public void addCurrentSection() {
        currentSection.trimName();
        if(StringUtils.isEmpty(getSectionName())){
            setSectionName("Misc");
        }
        sections.add(currentSection);
        currentSection = new SectionForm();
    }

    public String getSectionName(){
        return currentSection.getName();
    }

    public void setSectionName(String sectionName) {
        currentSection.setName(sectionName);
    }

    public void removeSection(String sectionName) {
        SectionForm sectionToDelete = null;
        for (SectionForm sectionForm: sections){
            if(sectionName.equals(sectionForm.getName())){
                sectionToDelete = sectionForm;
                break;
            }
        }
        if (sectionToDelete != null) {
            sections.remove(sectionToDelete);
        }
    }
}

