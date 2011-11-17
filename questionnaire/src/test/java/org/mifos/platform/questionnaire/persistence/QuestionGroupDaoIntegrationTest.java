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

package org.mifos.platform.questionnaire.persistence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.domain.QuestionnaireService;
import org.mifos.platform.questionnaire.domain.Section;
import org.mifos.platform.questionnaire.domain.SectionQuestion;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Arrays;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test-questionnaire-dbContext.xml", "/test-questionnaire-persistenceContext.xml", "/META-INF/spring/QuestionnaireContext.xml"})
@TransactionConfiguration(transactionManager = "platformTransactionManager", defaultRollback = true)
public class QuestionGroupDaoIntegrationTest {

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private QuestionGroupDao questionGroupDao;

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldRetrieveSectionQuestionIdByQuestionGroupNameSectionNameQuestionId() {
        String title = "QG1" + currentTimeMillis();
        SectionDetail sectionDetail1 = getSection("S1");
        SectionDetail sectionDetail2 = getSection("S2");
        List<SectionDetail> details = asList(sectionDetail1, sectionDetail2);
        QuestionGroupDetail questionGroupDetail = defineQuestionGroup(title, "Create", "Client", details, false);
        List<Section> sections = questionGroupDao.retrieveSectionByNameAndQuestionGroupId("S2", questionGroupDetail.getId());
        assertThat(sections, is(notNullValue()));
        assertThat(sections.size(), is(1));
        Section section2 = sections.get(0);
        assertThat(section2.getName(), is("S2"));
        List<SectionQuestion> sections2Questions = section2.getQuestions();
        assertThat(sections2Questions, is(notNullValue()));
        assertThat(sections2Questions.size(), is(3));
        assertThat(sections2Questions.get(0).getQuestionText(), is(sectionDetail2.getQuestionDetail(0).getText()));
        assertThat(sections2Questions.get(1).getQuestionText(), is(sectionDetail2.getQuestionDetail(1).getText()));
        assertThat(sections2Questions.get(2).getQuestionText(), is(sectionDetail2.getQuestionDetail(2).getText()));
    }

    @Test
    public void shouldRetrieveEventSourcesInOrder() {
        List<EventSourceDto> eventSources = questionnaireService.getAllEventSources();
        assertThat(eventSources, is(notNullValue()));
        assertThat(eventSources.size(), is(17));
        assertThat(eventSources.get(0).getDescription(), is("Create Client"));
        assertThat(eventSources.get(1).getDescription(), is("View Client"));
        assertThat(eventSources.get(2).getDescription(), is("Close Client"));
        assertThat(eventSources.get(3).getDescription(), is("Create Group"));
        assertThat(eventSources.get(4).getDescription(), is("View Group"));
        assertThat(eventSources.get(5).getDescription(), is("Create Office"));
        assertThat(eventSources.get(6).getDescription(), is("Create Personnel"));
        assertThat(eventSources.get(7).getDescription(), is("Create Center"));
        assertThat(eventSources.get(8).getDescription(), is("View Center"));
        assertThat(eventSources.get(9).getDescription(), is("Create Savings"));
        assertThat(eventSources.get(10).getDescription(), is("View Savings"));
        assertThat(eventSources.get(11).getDescription(), is("Close Savings"));
        assertThat(eventSources.get(12).getDescription(), is("Create Loan"));
        assertThat(eventSources.get(13).getDescription(), is("View Loan"));
        assertThat(eventSources.get(14).getDescription(), is("Approve Loan"));
        assertThat(eventSources.get(15).getDescription(), is("Close Loan"));
        assertThat(eventSources.get(16).getDescription(), is("Disburse Loan"));
    }

    private QuestionDetail defineQuestion(String questionTitle, QuestionType questionType) throws SystemException {
        return questionnaireService.defineQuestion(new QuestionDetail(questionTitle, questionType));
    }

    private QuestionGroupDetail defineQuestionGroup(String title, String event, String source, List<SectionDetail> sectionDetails, boolean editable) throws SystemException {
        return questionnaireService.defineQuestionGroup(new QuestionGroupDetail(0, title, Arrays.asList(new EventSourceDto(event, source, null)), sectionDetails, editable));
    }

    private SectionDetail getSection(String name) throws SystemException {
        SectionDetail section = new SectionDetail();
        section.setName(name);
        section.addQuestion(new SectionQuestionDetail(defineQuestion("Question1" + name + currentTimeMillis(), QuestionType.NUMERIC), true));
        section.addQuestion(new SectionQuestionDetail(defineQuestion("Question2" + name + currentTimeMillis(), QuestionType.DATE), true));
        section.addQuestion(new SectionQuestionDetail(defineQuestion("Question3" + name + currentTimeMillis(), QuestionType.FREETEXT), false));
        return section;
    }
}
