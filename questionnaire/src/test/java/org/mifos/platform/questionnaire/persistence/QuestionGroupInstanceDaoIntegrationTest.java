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

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.domain.QuestionGroup;
import org.mifos.platform.questionnaire.domain.QuestionGroupInstance;
import org.mifos.platform.questionnaire.domain.QuestionGroupResponse;
import org.mifos.platform.questionnaire.domain.QuestionnaireService;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test-questionnaire-dbContext.xml", "/test-questionnaire-persistenceContext.xml", "/META-INF/spring/QuestionnaireContext.xml"})
@TransactionConfiguration(transactionManager = "platformTransactionManager", defaultRollback = true)
public class QuestionGroupInstanceDaoIntegrationTest {

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private QuestionGroupDao questionGroupDao;

    @Autowired
    private QuestionGroupInstanceDao questionGroupInstanceDao;

    @Autowired
    private EventSourceDao eventSourceDao;

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldPersistQuestionGroupInstance() throws SystemException {
        String title = "QG1" + currentTimeMillis();
        List<SectionDetail> details = asList(getSection("S1"), getSection("S2"));
        QuestionGroupDetail questionGroupDetail = defineQuestionGroup(title, "Create", "Client", details, false);
        QuestionGroup questionGroup = questionGroupDao.getDetails(questionGroupDetail.getId());
        QuestionGroupInstance questionGroupInstance = getQuestionGroupInstance(1, questionGroup, 101, "Foo Bar1", "Foo Bar2");
        questionGroupInstanceDao.saveOrUpdateAll(asList(questionGroupInstance));
        QuestionGroupInstance groupInstance = questionGroupInstanceDao.getDetails(questionGroupInstance.getId());
        assertThat(groupInstance, is(notNullValue()));
        assertThat(groupInstance.getQuestionGroupResponses(), is(notNullValue()));
        assertThat(groupInstance.getQuestionGroupResponses().size(), is(2));
        assertThat(groupInstance.getQuestionGroupResponses().get(0).getSectionQuestion(), is(notNullValue()));
        assertThat(groupInstance.getQuestionGroupResponses().get(0).getSectionQuestion(), is(questionGroup.getSections().get(0).getQuestions().get(0)));
        assertThat(groupInstance.getQuestionGroupResponses().get(0).getResponse(), is("Foo Bar1"));
        assertThat(groupInstance.getQuestionGroupResponses().get(1).getSectionQuestion(), is(notNullValue()));
        assertThat(groupInstance.getQuestionGroupResponses().get(1).getSectionQuestion(), is(questionGroup.getSections().get(1).getQuestions().get(0)));
        assertThat(groupInstance.getQuestionGroupResponses().get(1).getResponse(), is("Foo Bar2"));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetLatestQuestionGroupInstancesBasedOnQuestionGroupAndEntity() {
        String title = "QG1" + currentTimeMillis();
        List<SectionDetail> details = asList(getSection("S1"), getSection("S2"));
        QuestionGroupDetail questionGroupDetail = defineQuestionGroup(title, "Create", "Client", details, false);
        QuestionGroup questionGroup = questionGroupDao.getDetails(questionGroupDetail.getId());
        QuestionGroupInstance instance1 = getQuestionGroupInstance(1, questionGroup, 101, "Foo Bar1", "Foo Bar2");
        QuestionGroupInstance instance2 = getQuestionGroupInstance(2, questionGroup, 101, "Hello World1", "Hello World2");
        questionGroupInstanceDao.saveOrUpdateAll(asList(instance1, instance2));
        List<QuestionGroupInstance> instances = questionGroupInstanceDao.retrieveLatestQuestionGroupInstanceByQuestionGroupAndEntity(101, questionGroupDetail.getId());
        assertThat(instances, is(notNullValue()));
        assertThat(instances.size(), is(1));
        QuestionGroupInstance qGInst = instances.get(0);
        assertThat(qGInst.getVersionNum(), is(2));
        assertThat(qGInst.getEntityId(), is(101));
        assertThat(qGInst.getQuestionGroup().getId(), is(questionGroupDetail.getId()));
        List<QuestionGroupResponse> qGResponses = qGInst.getQuestionGroupResponses();
        assertThat(qGResponses, is(notNullValue()));
        assertThat(qGResponses.size(), is(2));
        assertThat(qGResponses.get(0).getResponse(), is("Hello World1"));
        assertThat(qGResponses.get(1).getResponse(), is("Hello World2"));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetAllQuestionGroupInstancesByEntityAndEventSourceId() {
        List<SectionDetail> details = asList(getSection("S1"), getSection("S2"));
        QuestionGroup questionGroup1 = questionGroupDao.getDetails(defineQuestionGroup("QG2" + currentTimeMillis(), "View", "Client", details, false).getId());
        QuestionGroup questionGroup2 = questionGroupDao.getDetails(defineQuestionGroup("QG1" + currentTimeMillis(), "View", "Client", details, false).getId());
        QuestionGroup questionGroup3 = questionGroupDao.getDetails(defineQuestionGroup("QG3" + currentTimeMillis(), "View", "Client", details, false).getId());
        List<QuestionGroupInstance> questionGroupInstances = asList(
                getQuestionGroupInstance(2010, 7, 24, questionGroup1, 101, 1),
                getQuestionGroupInstance(2010, 7, 25, questionGroup1, 101, 1),
                getQuestionGroupInstance(2010, 7, 24, questionGroup2, 101, 1),
                getQuestionGroupInstance(2010, 7, 25, questionGroup2, 101, 1),
                getQuestionGroupInstance(2010, 7, 26, questionGroup2, 101, 1),
                getQuestionGroupInstance(2010, 7, 24, questionGroup3, 101, 1),
                getQuestionGroupInstance(2010, 7, 26, questionGroup3, 101, 1)
        );
        questionGroupInstanceDao.saveOrUpdateAll(questionGroupInstances);
        int eventSourceId = (eventSourceDao.retrieveByEventAndSource("View", "Client").get(0)).getId();
        List<QuestionGroupInstance> list = questionGroupInstanceDao.retrieveQuestionGroupInstancesByEntityIdAndEventSourceId(101, eventSourceId);
        assertThat(list, is(notNullValue()));
        assertThat(list.size(), is(7));
        assertThat((list.get(0)).getId(), is(questionGroupInstances.get(4).getId()));
        assertThat((list.get(1)).getId(), is(questionGroupInstances.get(6).getId()));
        assertThat((list.get(2)).getId(), is(questionGroupInstances.get(1).getId()));
        assertThat((list.get(3)).getId(), is(questionGroupInstances.get(3).getId()));
        assertThat((list.get(4)).getId(), is(questionGroupInstances.get(0).getId()));
        assertThat((list.get(5)).getId(), is(questionGroupInstances.get(2).getId()));
        assertThat((list.get(6)).getId(), is(questionGroupInstances.get(5).getId()));
    }

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldGetAllLatestQuestionGroupInstancesByEntityAndEventSourceId() {
        List<SectionDetail> details = asList(getSection("S1"), getSection("S2"));
        QuestionGroup questionGroup1 = questionGroupDao.getDetails(defineQuestionGroup("QG2" + currentTimeMillis(), "View", "Client", details, false).getId());
        QuestionGroup questionGroup2 = questionGroupDao.getDetails(defineQuestionGroup("QG1" + currentTimeMillis(), "View", "Client", details, false).getId());
        QuestionGroup questionGroup3 = questionGroupDao.getDetails(defineQuestionGroup("QG3" + currentTimeMillis(), "View", "Client", details, false).getId());
        List<QuestionGroupInstance> questionGroupInstances = asList(
                getQuestionGroupInstance(2010, 7, 24, questionGroup1, 101, 1),
                getQuestionGroupInstance(2010, 7, 25, questionGroup1, 101, 2),
                getQuestionGroupInstance(2010, 7, 24, questionGroup2, 101, 1),
                getQuestionGroupInstance(2010, 7, 25, questionGroup2, 101, 2),
                getQuestionGroupInstance(2010, 7, 26, questionGroup2, 101, 3),
                getQuestionGroupInstance(2010, 7, 24, questionGroup3, 101, 1),
                getQuestionGroupInstance(2010, 7, 26, questionGroup3, 101, 2)
        );
        questionGroupInstanceDao.saveOrUpdateAll(questionGroupInstances);
        int eventSourceId = eventSourceDao.retrieveByEventAndSource("View", "Client").get(0).getId();
        //  questionGroupInstanceDao.retrieveQuestionGroupInstancesByEntityIdAndEventSourceId(101, eventSourceId)
        //  is a hack to flush the entities as method under test is in SQL and not HQL
        questionGroupInstanceDao.retrieveQuestionGroupInstancesByEntityIdAndEventSourceId(101, eventSourceId);
        List<QuestionGroupInstance> list = questionGroupInstanceDao.retrieveLatestQuestionGroupInstancesByEntityIdAndEventSourceId(101, eventSourceId);
        assertThat(list, is(notNullValue()));
        assertThat(list.size(), is(3));
        assertThat((list.get(0)).getId(), is(questionGroupInstances.get(4).getId()));
        assertThat((list.get(1)).getId(), is(questionGroupInstances.get(6).getId()));
        assertThat((list.get(2)).getId(), is(questionGroupInstances.get(1).getId()));
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
        String questionTitle = "Question" + name + currentTimeMillis();
        QuestionDetail questionDetail = defineQuestion(questionTitle, QuestionType.NUMERIC);
        section.addQuestion(new SectionQuestionDetail(questionDetail, true));
        return section;
    }

    private QuestionGroupInstance getQuestionGroupInstance(int version, QuestionGroup questionGroup, int entityId, String... responses) {
        QuestionGroupInstance questionGroupInstance = new QuestionGroupInstance();
        questionGroupInstance.setCompletedStatus(1);
        questionGroupInstance.setQuestionGroup(questionGroup);
        questionGroupInstance.setDateConducted(Calendar.getInstance().getTime());
        questionGroupInstance.setCreatorId(122);
        questionGroupInstance.setVersionNum(version);
        questionGroupInstance.setEntityId(entityId);
        List<QuestionGroupResponse> questionGroupResponses = new ArrayList<QuestionGroupResponse>();
        for (int index = 0; index < responses.length; index++) {
            SectionQuestion sectionQuestion = questionGroup.getSections().get(index).getQuestions().get(0);
            questionGroupResponses.add(getQuestionGroupResponse(responses[index], questionGroupInstance, sectionQuestion));
        }
        questionGroupInstance.setQuestionGroupResponses(questionGroupResponses);
        return questionGroupInstance;
    }

    private QuestionGroupInstance getQuestionGroupInstance(int year, int month, int date, QuestionGroup questionGroup, int entityId, int versionNum, String... responses) {
        QuestionGroupInstance questionGroupInstance = new QuestionGroupInstance();
        questionGroupInstance.setCompletedStatus(1);
        questionGroupInstance.setQuestionGroup(questionGroup);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date);
        questionGroupInstance.setDateConducted(calendar.getTime());
        questionGroupInstance.setCreatorId(122);
        questionGroupInstance.setEventSourceId(questionGroup.getEventSources().iterator().next().getId());
        questionGroupInstance.setVersionNum(versionNum);
        questionGroupInstance.setEntityId(entityId);
        List<QuestionGroupResponse> questionGroupResponses = new ArrayList<QuestionGroupResponse>();
        for (int index = 0; index < responses.length; index++) {
            SectionQuestion sectionQuestion = questionGroup.getSections().get(index).getQuestions().get(0);
            questionGroupResponses.add(getQuestionGroupResponse(responses[index], questionGroupInstance, sectionQuestion));
        }
        questionGroupInstance.setQuestionGroupResponses(questionGroupResponses);
        return questionGroupInstance;
    }

    private QuestionGroupResponse getQuestionGroupResponse(String responses, QuestionGroupInstance questionGroupInstance, SectionQuestion sectionQuestion) {
        QuestionGroupResponse response = new QuestionGroupResponse();
        response.setResponse(responses);
        response.setQuestionGroupInstance(questionGroupInstance);
        response.setSectionQuestion(sectionQuestion);
        return response;
    }
}
