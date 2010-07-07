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

    private List<Question> questionPool = new ArrayList<Question>();

    private List<String> selectedQuestionIds = new ArrayList<String>();

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
        if (eventSource == null || StringUtils.isEmpty(eventSource.getEvent()) || StringUtils.isEmpty(eventSource.getSource()))
            return;
        eventSourceId = format("%s.%s", eventSource.getEvent(), eventSource.getSource());
    }

    public void addCurrentSection() {
        currentSection.trimName();
        if (StringUtils.isEmpty(getSectionName())) {
            setSectionName("Misc");
        }
        addCurrentSectionToSections();
        addSelectedQuestionsToCurrentSection();
        currentSection = new SectionForm();
        selectedQuestionIds = new ArrayList<String>();
    }

    private void addSelectedQuestionsToCurrentSection() {
        ArrayList<Question> addedQuestions = new ArrayList<Question>();
        for (Question question : questionPool) {
            if (selectedQuestionIds.contains(question.getId())) {
                currentSection.getQuestions().add(question);
                addedQuestions.add(question);
            }
        }
        questionPool.removeAll(addedQuestions);
    }

    private void addCurrentSectionToSections() {
        for (SectionForm section : sections) {
            if (StringUtils.equalsIgnoreCase(section.getName(), currentSection.getName())) {
                currentSection = section;
                return;
            }
        }
        sections.add(currentSection);
    }

    public String getSectionName() {
        return currentSection.getName();
    }

    public void setSectionName(String sectionName) {
        currentSection.setName(sectionName);
    }

    public void removeSection(String sectionName) {
        SectionForm sectionToDelete = null;
        for (SectionForm sectionForm : sections) {
            if (StringUtils.equalsIgnoreCase(sectionName,sectionForm.getName())) {
                sectionToDelete = sectionForm;
                break;
            }
        }
        if (sectionToDelete != null) {
            questionPool.addAll(sectionToDelete.getQuestions());
            sections.remove(sectionToDelete);
        }
    }

    public List<Question> getQuestionPool() {
        return questionPool;
    }

    public void setQuestionPool(List<Question> questionPool) {
        this.questionPool = questionPool;
    }

    public List<String> getSelectedQuestionIds() {
        return selectedQuestionIds;
    }

    public void setSelectedQuestionIds(List<String> selectedQuestionIds) {
        this.selectedQuestionIds = selectedQuestionIds;
    }

    public void removeQuestion(String sectionName, String questionId) {
        for (SectionForm section : sections) {
            if (StringUtils.equalsIgnoreCase(sectionName, section.getName())) {
                removeQuestionFromSection(questionId, section);
                if (sectionHasNoQuestions(section)) {
                    removeSection(sectionName);
                }
                break;
            }
        }
    }

    public boolean hasQuestionsInCurrentSection() {
        return selectedQuestionIds.size()==0;
    }

    private boolean sectionHasNoQuestions(SectionForm section) {
        return section.getQuestions().size() == 0;
    }

    private void removeQuestionFromSection(String questionId, SectionForm section) {
        Question questionToRemove = null;
        List<Question> questions = section.getQuestions();
        for (Question question : questions) {
            if (StringUtils.equals(questionId, question.getId())) {
                questionToRemove = question;
                break;
            }
        }
        if (questionToRemove != null) {
            questions.remove(questionToRemove);
            questionPool.add(questionToRemove);
        }
    }
}

