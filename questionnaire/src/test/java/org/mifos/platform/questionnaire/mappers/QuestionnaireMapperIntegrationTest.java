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

package org.mifos.platform.questionnaire.mappers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.platform.questionnaire.domain.EventSourceEntity;
import org.mifos.platform.questionnaire.domain.QuestionGroup;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test-questionnaire-dbContext.xml", "/test-questionnaire-persistenceContext.xml", "/META-INF/spring/QuestionnaireContext.xml"})
@TransactionConfiguration(transactionManager = "platformTransactionManager", defaultRollback = true)
public class QuestionnaireMapperIntegrationTest {

    @Autowired
    private QuestionnaireMapper questionnaireMapper;

    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldMapEventSources() throws Exception {
        testMapEventSource("Create", "Client", "Create Client");
        testMapEventSource("View", "Client", "View Client");
        testMapEventSource("Create", "Loan", "Create Loan");
    }

    private void testMapEventSource(String event, String source, String description) throws Exception {
        EventSourceDto eventSourceDto = new EventSourceDto(event, source, description);
        List<SectionDetail> sectionDetails = getSectionDefinitions();
        QuestionGroup questionGroup = questionnaireMapper.mapToQuestionGroup(new QuestionGroupDetail(0, "Title", Arrays.asList(eventSourceDto), sectionDetails, false));
        Set<EventSourceEntity> eventSources = questionGroup.getEventSources();
        assertThat(eventSources, is(not(nullValue())));
        assertThat(eventSources.size(), is(1));
        EventSourceEntity eventSourceEntity = eventSources.toArray(new EventSourceEntity[eventSources.size()])[0];
        assertThat(eventSourceEntity.getEvent().getName(), is(event));
        assertThat(eventSourceEntity.getSource().getEntityType(), is(source));
        assertThat(eventSourceEntity.getDescription(), is(description));
        assertThat(eventSourceEntity.getEvent().getName(), is(event));
        assertThat(eventSourceEntity.getSource().getEntityType(), is(source));
        assertThat(eventSourceEntity.getDescription(), is(description));
    }

    private List<SectionDetail> getSectionDefinitions() {
        List<SectionDetail> sectionDetails = new ArrayList<SectionDetail>();
        SectionDetail sectionDetail = new SectionDetail();
        sectionDetail.setName("Name");
        sectionDetails.add(sectionDetail);
        return sectionDetails;
    }
}
