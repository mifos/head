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

package org.mifos.platform.questionnaire.ui.controller;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.platform.questionnaire.service.EventSource;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.ui.model.QuestionGroupForm;
import org.mifos.platform.questionnaire.ui.model.SectionDetailForm;
import org.mifos.platform.questionnaire.ui.model.SectionQuestionDetailForm;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import java.util.Arrays;
import static org.hamcrest.CoreMatchers.not;   // NOPMD
import static org.hamcrest.CoreMatchers.nullValue;   // NOPMD
import static org.junit.Assert.assertNotSame;    // NOPMD
import static org.junit.Assert.assertThat;     // NOPMD

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD")
public class QuestionGroupFormTest {

    @Test
    public void shouldGetEventSourceId() {
        EventSource eventSource = new EventSource("Create", "Client", "Create Client");
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail(123, "Title", eventSource, new ArrayList<SectionDetail>());
        QuestionGroupForm questionGroupForm = new QuestionGroupForm(questionGroupDetail);
        assertThat(questionGroupForm.getEventSourceId(), Matchers.is("Create.Client"));
    }

    @Test
    public void shouldGetSections() {
        SectionDetail sectionDetail = new SectionDetail();
        sectionDetail.setName("Section1");
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail(123, "Title", null, Arrays.asList(sectionDetail));
        QuestionGroupForm questionGroupForm = new QuestionGroupForm(questionGroupDetail);
        List<SectionDetailForm> sections = questionGroupForm.getSections();
        assertThat(sections, Matchers.notNullValue());
        assertThat(sections.size(), Matchers.is(1));
        assertThat(sections.get(0).getName(), Matchers.is("Section1"));
    }

    @Test
    public void shouldGetEventSource() {
        QuestionGroupForm questionGroupForm;

        questionGroupForm = new QuestionGroupForm();
        questionGroupForm.setEventSourceId("event.source");
        assertEventSource(questionGroupForm.getEventSource(), "event", "source");

        questionGroupForm = new QuestionGroupForm();
        questionGroupForm.setEventSourceId(null);
        assertThat(questionGroupForm.getEventSource().getDescription(), CoreMatchers.is(nullValue()));
        assertThat(questionGroupForm.getEventSource().getSource(), CoreMatchers.is(nullValue()));
        assertThat(questionGroupForm.getEventSource().getEvent(), CoreMatchers.is(nullValue()));

        questionGroupForm = new QuestionGroupForm();
        questionGroupForm.setEventSourceId("");
        assertThat(questionGroupForm.getEventSource().getDescription(), CoreMatchers.is(nullValue()));
        assertThat(questionGroupForm.getEventSource().getSource(), CoreMatchers.is(nullValue()));
        assertThat(questionGroupForm.getEventSource().getEvent(), CoreMatchers.is(nullValue()));
    }

    @Test
    public void shouldSetEventSource() {
        QuestionGroupForm questionGroupForm;

        questionGroupForm = new QuestionGroupForm();
        questionGroupForm.setEventSource(new EventSource("Create", "Client", null));
        assertThat(questionGroupForm.getEventSourceId(), CoreMatchers.is("Create.Client"));

        questionGroupForm = new QuestionGroupForm();
        questionGroupForm.setEventSource(null);
        assertThat(questionGroupForm.getEventSourceId(), CoreMatchers.is(nullValue()));

        questionGroupForm = new QuestionGroupForm();
        questionGroupForm.setEventSource(new EventSource("", null, null));
        assertThat(questionGroupForm.getEventSourceId(), CoreMatchers.is(nullValue()));
    }

    @Test
    public void testAddCurrentSection() {
        QuestionGroupForm questionGroupForm = new QuestionGroupForm();
        questionGroupForm.setQuestionPool(new ArrayList<SectionQuestionDetail>(Arrays.asList(getSectionQuestionDetail(1, "Q1", true), getSectionQuestionDetail(2, "Q2", false))));
        questionGroupForm.setSelectedQuestionIds(Arrays.asList("1"));
        String title = "title";
        questionGroupForm.setTitle(title);
        String sectionName = "sectionName";
        questionGroupForm.setSectionName(sectionName);

        questionGroupForm.addCurrentSection();

        List<SectionDetailForm> sectionDetailForms = questionGroupForm.getSections();
        assertThat(sectionDetailForms.size(), CoreMatchers.is(1));
        String nameOfAddedSection = questionGroupForm.getSections().get(0).getName();
        assertThat(nameOfAddedSection, CoreMatchers.is(sectionName));
        List<SectionQuestionDetailForm> questions = sectionDetailForms.get(0).getSectionQuestions();
        assertThat(questions.size(), CoreMatchers.is(1));
        assertThat(questions.get(0).getTitle(), CoreMatchers.is("Q1"));
        assertThat(questions.get(0).isMandatory(), CoreMatchers.is(true));
        assertNotSame(nameOfAddedSection, questionGroupForm.getSectionName());
        assertNotSame(questionGroupForm.getSelectedQuestionIds().size(), CoreMatchers.is(0));

        questionGroupForm.setSelectedQuestionIds(Arrays.asList("2"));
        questionGroupForm.setSectionName(sectionName);
        questionGroupForm.addCurrentSection();

        sectionDetailForms = questionGroupForm.getSections();
        assertThat(sectionDetailForms.size(), CoreMatchers.is(1));
        nameOfAddedSection = questionGroupForm.getSections().get(0).getName();
        assertThat(nameOfAddedSection, CoreMatchers.is(sectionName));
        questions = sectionDetailForms.get(0).getSectionQuestions();
        assertThat(questions.size(), CoreMatchers.is(2));
        assertThat(questions.get(0).getTitle(), CoreMatchers.is("Q1"));
        assertThat(questions.get(0).isMandatory(), CoreMatchers.is(true));
        assertThat(questions.get(1).getTitle(), CoreMatchers.is("Q2"));
        assertThat(questions.get(1).isMandatory(), CoreMatchers.is(false));
        assertNotSame(nameOfAddedSection, questionGroupForm.getSectionName());
        assertNotSame(questionGroupForm.getSelectedQuestionIds().size(), CoreMatchers.is(0));
    }

    @Test
    public void testAddCurrentSectionWhenSectionNameIsNotProvided() {
        QuestionGroupForm questionGroupForm = new QuestionGroupForm();
        String title = "title";
        questionGroupForm.setTitle(title);
        questionGroupForm.addCurrentSection();
        assertThat(questionGroupForm.getSections().size(), CoreMatchers.is(1));
        String nameOfAddedSection = questionGroupForm.getSections().get(0).getName();
        assertThat(nameOfAddedSection, CoreMatchers.is("Misc"));
        assertNotSame(nameOfAddedSection, questionGroupForm.getSectionName());
    }

    @Test
    public void testAddCurrentSectionWhenSectionNameIsBlank() {
        QuestionGroupForm questionGroupForm = new QuestionGroupForm();
        String title = "title";
        questionGroupForm.setTitle(title);
        String sectionName = "   ";
        questionGroupForm.setSectionName(sectionName);
        questionGroupForm.addCurrentSection();
        assertThat(questionGroupForm.getSections().size(), CoreMatchers.is(1));
        String nameOfAddedSection = questionGroupForm.getSections().get(0).getName();
        assertThat(nameOfAddedSection, CoreMatchers.is("Misc"));
        assertNotSame(nameOfAddedSection, questionGroupForm.getSectionName());
    }

    @Test
    public void testRemoveQuestionFromSection() {
        QuestionGroupForm questionGroupForm = new QuestionGroupForm();
        List<SectionDetailForm> sections = new ArrayList<SectionDetailForm>();
        setupSection(questionGroupForm, sections, "sectionName");

        questionGroupForm.removeQuestion("sectionName", "1");

        assertThat(questionGroupForm.getSections().size(), CoreMatchers.is(1));
        assertThat(questionGroupForm.getSections().get(0).getSectionQuestions().size(), CoreMatchers.is(1));
        assertThat(questionGroupForm.getSections().get(0).getSectionQuestions().get(0).getTitle(), CoreMatchers.is("Q2"));
        assertThat(questionGroupForm.getSections().get(0).getSectionQuestions().get(0).isMandatory(), CoreMatchers.is(true));
        assertThat(questionGroupForm.getQuestionPool().size(), Matchers.is(1));
        assertThat(questionGroupForm.getQuestionPool().get(0).getTitle(), Matchers.is("Q1"));
        assertThat(questionGroupForm.getQuestionPool().get(0).isMandatory(), Matchers.is(false));

        questionGroupForm.removeQuestion("sectionName", "2");

        assertThatQuestionFormHasNoSection(questionGroupForm);
    }

    private void setupSection(QuestionGroupForm questionGroupForm, List<SectionDetailForm> sections, String sectionName) {
        sections.add(getSectionSectionDetailForm(sectionName,
                new ArrayList<SectionQuestionDetail>(Arrays.asList(getSectionQuestionDetail(1, "Q1", false), getSectionQuestionDetail(2, "Q2", true)))));
        questionGroupForm.setSections(sections);

        assertThat(questionGroupForm.getSections().size(), CoreMatchers.is(1));
        assertThat(questionGroupForm.getSections().get(0).getSectionQuestions().size(), CoreMatchers.is(2));
        assertThat(questionGroupForm.getSections().get(0).getSectionQuestions().get(0).getTitle(), CoreMatchers.is("Q1"));
        assertThat(questionGroupForm.getSections().get(0).getSectionQuestions().get(0).isMandatory(), CoreMatchers.is(false));
        assertThat(questionGroupForm.getSections().get(0).getSectionQuestions().get(1).getTitle(), CoreMatchers.is("Q2"));
        assertThat(questionGroupForm.getSections().get(0).getSectionQuestions().get(1).isMandatory(), CoreMatchers.is(true));
        assertThat(questionGroupForm.getQuestionPool().size(), Matchers.is(0));
    }

    @Test
    public void removeSection() {
        QuestionGroupForm questionGroupForm = new QuestionGroupForm();
        List<SectionDetailForm> sections = new ArrayList<SectionDetailForm>();
        String sectionName = "sectionName";
        setupSection(questionGroupForm, sections, sectionName);
        questionGroupForm.removeSection(sectionName);
        assertThatQuestionFormHasNoSection(questionGroupForm);
    }

    private void assertThatQuestionFormHasNoSection(QuestionGroupForm questionGroupForm) {
        assertThat(questionGroupForm.getSections().size(), CoreMatchers.is(0));
        assertThat(questionGroupForm.getQuestionPool().size(), Matchers.is(2));
        assertThat(questionGroupForm.getQuestionPool().get(0).getTitle(), Matchers.is("Q1"));
        assertThat(questionGroupForm.getQuestionPool().get(0).isMandatory(), Matchers.is(false));
        assertThat(questionGroupForm.getQuestionPool().get(1).getTitle(), Matchers.is("Q2"));
        assertThat(questionGroupForm.getQuestionPool().get(1).isMandatory(), Matchers.is(false));
    }

    private SectionDetailForm getSectionSectionDetailForm(String sectionName, List<SectionQuestionDetail> questions) {
        SectionDetailForm section = new SectionDetailForm();
        section.setName(sectionName);
        section.setQuestionDetails(questions);
        return section;
    }

    private SectionQuestionDetail getSectionQuestionDetail(int id, String title, boolean mandatory) {
        return new SectionQuestionDetail(new QuestionDetail(id, title, title, QuestionType.FREETEXT), mandatory);
    }

    private void assertEventSource(EventSource eventSource, String event, String source) {
        assertThat(eventSource, CoreMatchers.is(not(nullValue())));
        assertThat(eventSource.getEvent(), CoreMatchers.is(event));
        assertThat(eventSource.getSource(), CoreMatchers.is(source));
    }
}
