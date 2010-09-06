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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.mifos.platform.questionnaire.ui.model.Question;
import org.mifos.platform.questionnaire.ui.model.QuestionGroupForm;
import org.mifos.platform.questionnaire.ui.model.SectionDetailForm;
import org.mifos.platform.questionnaire.ui.model.SectionQuestionDetailForm;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;     // NOPMD

@RunWith(MockitoJUnitRunner.class)
public class QuestionGroupFormTest {

    @Test
    public void shouldGetEventSourceId() {
        EventSourceDto eventSourceDto = new EventSourceDto("Create", "Client", "Create Client");
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail(123, "Title", eventSourceDto, new ArrayList<SectionDetail>(), false);
        QuestionGroupForm questionGroupForm = new QuestionGroupForm(questionGroupDetail);
        assertThat(questionGroupForm.getEventSourceId(), is("Create.Client"));
    }

    @Test
    public void shouldGetSections() {
        SectionDetail sectionDetail = new SectionDetail();
        sectionDetail.setName("Section1");
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail(123, "Title", null, asList(sectionDetail), false);
        QuestionGroupForm questionGroupForm = new QuestionGroupForm(questionGroupDetail);
        List<SectionDetailForm> sections = questionGroupForm.getSections();
        assertThat(questionGroupForm.getInitialCountOfSections(), is(1));
        assertThat(sections, notNullValue());
        assertThat(sections.size(), is(1));
        assertThat(sections.get(0).getName(), is("Section1"));
        assertThat(sections.get(0).getInitialCountOfQuestions(), is(0));
    }

    @Test
    public void shouldGetEventSource() {
        QuestionGroupForm questionGroupForm;

        questionGroupForm = new QuestionGroupForm();
        questionGroupForm.setEventSourceId("event.source");
        assertEventSource(questionGroupForm.getEventSource(), "event", "source");

        questionGroupForm = new QuestionGroupForm();
        questionGroupForm.setEventSourceId(null);
        assertThat(questionGroupForm.getEventSource().getDescription(), is(nullValue()));
        assertThat(questionGroupForm.getEventSource().getSource(), is(nullValue()));
        assertThat(questionGroupForm.getEventSource().getEvent(), is(nullValue()));

        questionGroupForm = new QuestionGroupForm();
        questionGroupForm.setEventSourceId("");
        assertThat(questionGroupForm.getEventSource().getDescription(), is(nullValue()));
        assertThat(questionGroupForm.getEventSource().getSource(), is(nullValue()));
        assertThat(questionGroupForm.getEventSource().getEvent(), is(nullValue()));
    }

    @Test
    public void shouldSetEventSource() {
        QuestionGroupForm questionGroupForm;

        questionGroupForm = new QuestionGroupForm();
        questionGroupForm.setEventSource(new EventSourceDto("Create", "Client", null));
        assertThat(questionGroupForm.getEventSourceId(), is("Create.Client"));

        questionGroupForm = new QuestionGroupForm();
        questionGroupForm.setEventSource(null);
        assertThat(questionGroupForm.getEventSourceId(), is(nullValue()));

        questionGroupForm = new QuestionGroupForm();
        questionGroupForm.setEventSource(new EventSourceDto("", null, null));
        assertThat(questionGroupForm.getEventSourceId(), is(nullValue()));
    }

    @Test
    public void testAddCurrentSection() {
        QuestionGroupForm questionGroupForm = new QuestionGroupForm();
        questionGroupForm.setQuestionPool(new ArrayList<SectionQuestionDetail>(asList(getSectionQuestionDetail(1, "Q1", true), getSectionQuestionDetail(2, "Q2", false))));
        questionGroupForm.setSelectedQuestionIds(asList("1"));
        String title = "title";
        questionGroupForm.setTitle(title);
        String sectionName = "sectionName";
        questionGroupForm.setSectionName(sectionName);

        questionGroupForm.addCurrentSection();

        List<SectionDetailForm> sectionDetailForms = questionGroupForm.getSections();
        assertThat(sectionDetailForms.size(), is(1));
        String nameOfAddedSection = questionGroupForm.getSections().get(0).getName();
        assertThat(nameOfAddedSection, is(sectionName));
        List<SectionQuestionDetailForm> questions = sectionDetailForms.get(0).getSectionQuestions();
        assertThat(questions.size(), is(1));
        assertThat(questions.get(0).getTitle(), is("Q1"));
        assertThat(questions.get(0).isMandatory(), is(true));
        assertThat(questionGroupForm.getSectionName(), is(nameOfAddedSection));
        assertNotSame(questionGroupForm.getSelectedQuestionIds().size(), is(0));

        questionGroupForm.setSelectedQuestionIds(asList("2"));
        questionGroupForm.setSectionName(sectionName);
        questionGroupForm.addCurrentSection();

        sectionDetailForms = questionGroupForm.getSections();
        assertThat(sectionDetailForms.size(), is(1));
        nameOfAddedSection = questionGroupForm.getSections().get(0).getName();
        assertThat(nameOfAddedSection, is(sectionName));
        questions = sectionDetailForms.get(0).getSectionQuestions();
        assertThat(questions.size(), is(2));
        assertThat(questions.get(0).getTitle(), is("Q1"));
        assertThat(questions.get(0).isMandatory(), is(true));
        assertThat(questions.get(1).getTitle(), is("Q2"));
        assertThat(questions.get(1).isMandatory(), is(false));
        assertThat(questionGroupForm.getSectionName(), is(nameOfAddedSection));
        assertNotSame(questionGroupForm.getSelectedQuestionIds().size(), is(0));
    }

    @Test
    public void testAddCurrentSectionWhenSectionNameIsNotProvided() {
        QuestionGroupForm questionGroupForm = new QuestionGroupForm();
        String title = "title";
        questionGroupForm.setTitle(title);
        questionGroupForm.addCurrentSection();
        assertThat(questionGroupForm.getSections().size(), is(1));
        String nameOfAddedSection = questionGroupForm.getSections().get(0).getName();
        assertThat(nameOfAddedSection, is("Misc"));
        assertThat(questionGroupForm.getSectionName(), is(nameOfAddedSection));
    }

    @Test
    public void testAddCurrentSectionWhenSectionNameIsBlank() {
        QuestionGroupForm questionGroupForm = new QuestionGroupForm();
        String title = "title";
        questionGroupForm.setTitle(title);
        String sectionName = "   ";
        questionGroupForm.setSectionName(sectionName);
        questionGroupForm.addCurrentSection();
        assertThat(questionGroupForm.getSections().size(), is(1));
        String nameOfAddedSection = questionGroupForm.getSections().get(0).getName();
        assertThat(nameOfAddedSection, is("Misc"));
        assertThat(questionGroupForm.getSectionName(), is(nameOfAddedSection));
    }

    @Test
    public void testAddCurrentSectionForAddQuestion() {
        QuestionGroupForm questionGroupForm = new QuestionGroupForm();
        String title = "title";
        questionGroupForm.setTitle(title);
        String sectionName = "SectionWithNewQuestion";
        Question currentQuestion = new Question(new QuestionDetail());
        currentQuestion.setTitle(" Question1 ");
        currentQuestion.setType("Free Text");
        questionGroupForm.setCurrentQuestion(currentQuestion);
        questionGroupForm.setAddQuestionFlag(true);
        questionGroupForm.setSectionName(sectionName);
        questionGroupForm.addCurrentSection();
        List<SectionDetailForm> sections = questionGroupForm.getSections();
        assertThat(sections.size(), is(1));
        SectionDetailForm section1 = sections.get(0);
        assertThat(section1.getName(), is(sectionName));
        assertThat(section1.getSectionQuestionDetails().get(0).getTitle(), is("Question1"));
        assertThat(questionGroupForm.getSectionName(), is(section1.getName()));
    }

    @Test
    public void testRemoveQuestionFromSection() {
        QuestionGroupForm questionGroupForm = new QuestionGroupForm();
        List<SectionDetailForm> sections = new ArrayList<SectionDetailForm>();
        setupSection(questionGroupForm, sections, "sectionName");

        questionGroupForm.removeQuestion("sectionName", "1");

        assertThat(questionGroupForm.getSections().size(), is(1));
        assertThat(questionGroupForm.getSections().get(0).getSectionQuestions().size(), is(1));
        assertThat(questionGroupForm.getSections().get(0).getSectionQuestions().get(0).getTitle(), is("Q2"));
        assertThat(questionGroupForm.getSections().get(0).getSectionQuestions().get(0).isMandatory(), is(true));
        assertThat(questionGroupForm.getQuestionPool().size(), is(1));
        assertThat(questionGroupForm.getQuestionPool().get(0).getTitle(), is("Q1"));
        assertThat(questionGroupForm.getQuestionPool().get(0).isMandatory(), is(false));

        questionGroupForm.removeQuestion("sectionName", "2");

        assertThatQuestionFormHasNoSection(questionGroupForm);
    }

    private void setupSection(QuestionGroupForm questionGroupForm, List<SectionDetailForm> sections, String sectionName) {
        sections.add(getSectionSectionDetailForm(sectionName,
                new ArrayList<SectionQuestionDetail>(asList(getSectionQuestionDetail(1, "Q1", false), getSectionQuestionDetail(2, "Q2", true)))));
        questionGroupForm.setSections(sections);

        assertThat(questionGroupForm.getSections().size(), is(1));
        assertThat(questionGroupForm.getSections().get(0).getSectionQuestions().size(), is(2));
        assertThat(questionGroupForm.getSections().get(0).getSectionQuestions().get(0).getTitle(), is("Q1"));
        assertThat(questionGroupForm.getSections().get(0).getSectionQuestions().get(0).isMandatory(), is(false));
        assertThat(questionGroupForm.getSections().get(0).getSectionQuestions().get(1).getTitle(), is("Q2"));
        assertThat(questionGroupForm.getSections().get(0).getSectionQuestions().get(1).isMandatory(), is(true));
        assertThat(questionGroupForm.getQuestionPool().size(), is(0));
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
    
    @Test
    public void testIsDuplicateTitle() {
        QuestionGroupForm questionGroupForm = new QuestionGroupForm();
        String title = "title";
        questionGroupForm.setTitle(title);
        String sectionName = "SectionWithNewQuestion";
        Question currentQuestion = new Question(new QuestionDetail());
        currentQuestion.setTitle(" Question1 ");
        currentQuestion.setType("Free Text");
        questionGroupForm.setCurrentQuestion(currentQuestion);
        questionGroupForm.setAddQuestionFlag(true);
        questionGroupForm.setSectionName(sectionName);
        questionGroupForm.addCurrentSection();
        questionGroupForm.getCurrentQuestion().setTitle("Question2 ");
        questionGroupForm.addCurrentSection();
        assertThat(questionGroupForm.isDuplicateTitle("Question1"), is(true));
    }

    private void assertThatQuestionFormHasNoSection(QuestionGroupForm questionGroupForm) {
        assertThat(questionGroupForm.getSections().size(), is(0));
        assertThat(questionGroupForm.getQuestionPool().size(), is(2));
        assertThat(questionGroupForm.getQuestionPool().get(0).getTitle(), is("Q1"));
        assertThat(questionGroupForm.getQuestionPool().get(0).isMandatory(), is(false));
        assertThat(questionGroupForm.getQuestionPool().get(1).getTitle(), is("Q2"));
        assertThat(questionGroupForm.getQuestionPool().get(1).isMandatory(), is(false));
    }

    private SectionDetailForm getSectionSectionDetailForm(String sectionName, List<SectionQuestionDetail> questions) {
        SectionDetailForm section = new SectionDetailForm();
        section.setName(sectionName);
        section.setQuestionDetails(questions);
        return section;
    }

    private SectionQuestionDetail getSectionQuestionDetail(int id, String title, boolean mandatory) {
        return new SectionQuestionDetail(new QuestionDetail(id, title, title, QuestionType.FREETEXT, true), mandatory);
    }

    private void assertEventSource(EventSourceDto eventSourceDto, String event, String source) {
        assertThat(eventSourceDto, is(not(nullValue())));
        assertThat(eventSourceDto.getEvent(), is(event));
        assertThat(eventSourceDto.getSource(), is(source));
    }
}
