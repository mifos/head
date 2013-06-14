/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import org.apache.commons.lang.StringUtils;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QuestionGroupDetail implements Serializable {
    private static final long serialVersionUID = 5240884292277900071L;

    private Integer id;
    private String title;
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "SE_BAD_FIELD")
    private List<SectionDetail> sectionDetails;
    private List<EventSourceDto> eventSourceDtos;
    private boolean editable;
    private boolean active;
    private boolean ppi;
    private Short activityId;
    
    private List<QuestionLinkDetail> questionLinks = new ArrayList<QuestionLinkDetail>();
    private List<SectionLinkDetail> sectionLinks = new ArrayList<SectionLinkDetail>();

    public QuestionGroupDetail() {
        this(0, null, new ArrayList<EventSourceDto>(), new ArrayList<SectionDetail>(), false);
    }

    public QuestionGroupDetail(int id, String title, List<SectionDetail> sectionDetails) {
        this(id, title, null, sectionDetails, false);
    }

    public QuestionGroupDetail(int id, String title, List<EventSourceDto> eventSourceDtos, List<SectionDetail> sectionDetails, boolean editable) {
        this(id, title, eventSourceDtos, sectionDetails, editable, false);
    }

    public QuestionGroupDetail(int id, String title, List<EventSourceDto> eventSourceDtos, List<SectionDetail> sectionDetails, boolean editable, boolean active) {
        this.id = id;
        this.title = title;
        this.sectionDetails = sectionDetails;
        this.eventSourceDtos = eventSourceDtos;
        this.editable = editable;
        this.active = active;
    }

    public QuestionGroupDetail(int id, String title, List<EventSourceDto> eventSourceDtos, List<SectionDetail> sectionDetails, boolean editable, boolean active, boolean ppi) {
        this.id = id;
        this.title = title;
        this.sectionDetails = sectionDetails;
        this.eventSourceDtos = eventSourceDtos;
        this.editable = editable;
        this.active = active;
        this.ppi = ppi;
    }
    public QuestionGroupDetail(int id, String title, List<EventSourceDto> eventSourceDtos, List<SectionDetail> sectionDetails, boolean editable, boolean active, boolean ppi, 
            List<QuestionLinkDetail> questionLinks,List<SectionLinkDetail> sectionLinks) {
        this.id = id;
        this.title = title;
        this.sectionDetails = sectionDetails;
        this.eventSourceDtos = eventSourceDtos;
        this.editable = editable;
        this.active = active;
        this.ppi = ppi;
        this.questionLinks = questionLinks;
        this.sectionLinks = sectionLinks;
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

    public List<EventSourceDto> getEventSources() {
        return eventSourceDtos;
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

    public void setEventSources(List<EventSourceDto> eventSourceDtos) {
        this.eventSourceDtos = eventSourceDtos;
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

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isPpi() {
        return ppi;
    }

    public void setPpi(boolean ppi) {
        this.ppi = ppi;
    }

    public boolean isNewQuestionGroup() {
        return id == 0;
    }

    public List<Integer> getAllQuestionIds() {
        List<Integer> questionIds = new ArrayList<Integer>();
        for (SectionDetail sectionDetail : sectionDetails) {
            for (SectionQuestionDetail sectionQuestionDetail : sectionDetail.getQuestions()) {
                questionIds.add(sectionQuestionDetail.getQuestionId());
            }
        }
        return questionIds;
    }

    public void addSection(SectionDetail sectionDetail) {
        this.sectionDetails.add(sectionDetail);
    }

    public void removeSection(SectionDetail sectionToDelete) {
        for (Iterator<SectionDetail> iterator = sectionDetails.iterator(); iterator.hasNext();) {
            SectionDetail sectionDetail = iterator.next();
            if (StringUtils.equalsIgnoreCase(sectionToDelete.getName(), sectionDetail.getName())) {
                iterator.remove();
                break;
            }
        }
    }

    public boolean hasNoActiveSectionsAndQuestions() {
        boolean result = true;
        for (SectionDetail sectionDetail : sectionDetails) {
            if (sectionDetail.hasActiveQuestions()) {
                result = false;
                break;
            }
        }
        return result;
    }

    public Short getActivityId() {
        return activityId;
    }

    public void setActivityId(Short activityId) {
        this.activityId = activityId;
    }

    public List<QuestionLinkDetail> getQuestionLinks() {
        return questionLinks;
    }

    public void setQuestionLinks(List<QuestionLinkDetail> questionLinks) {
        this.questionLinks = questionLinks;
    }

    public List<SectionLinkDetail> getSectionLinks() {
        return sectionLinks;
    }

    public void setSectionLinks(List<SectionLinkDetail> sectionLinks) {
        this.sectionLinks = sectionLinks;
    }
    
}
