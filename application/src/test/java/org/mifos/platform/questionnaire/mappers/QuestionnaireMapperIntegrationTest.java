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

package org.mifos.platform.questionnaire.mappers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.platform.questionnaire.contract.EventSource;
import org.mifos.platform.questionnaire.contract.QuestionGroupDefinition;
import org.mifos.platform.questionnaire.contract.SectionDefinition;
import org.mifos.platform.questionnaire.domain.EventSourceEntity;
import org.mifos.platform.questionnaire.domain.QuestionGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/org/mifos/config/resources/QuestionnaireContext.xml", "/org/mifos/config/resources/persistenceContext.xml", "/test-dataSourceContext.xml"})
@TransactionConfiguration(transactionManager = "platformTransactionManager", defaultRollback = true)
public class QuestionnaireMapperIntegrationTest {

    @Autowired
    private QuestionnaireMapper questionnaireMapper;
    
    @Test
    @Transactional(rollbackFor = DataAccessException.class)
    public void shouldMapEventSources() {
        EventSource eventSource = new EventSource("Create", "Client", "Create Client");
        List<SectionDefinition> sectionDefinitions = getSectionDefinitions();
        QuestionGroup questionGroup = questionnaireMapper.mapToQuestionGroup(new QuestionGroupDefinition("Title", eventSource, sectionDefinitions));
        Set<EventSourceEntity> eventSources = questionGroup.getEventSources();
        assertThat(eventSources, is(not(nullValue())));
        assertThat(eventSources.size(), is(1));
        EventSourceEntity eventSourceEntity = eventSources.toArray(new EventSourceEntity[eventSources.size()])[0];
        assertThat(eventSourceEntity.getEvent().getName(), is("Create"));
        assertThat(eventSourceEntity.getSource().getEntityType(), is("Client"));
        assertThat(eventSourceEntity.getDescription(), is("Create Client"));
    }

    private List<SectionDefinition> getSectionDefinitions() {
        List<SectionDefinition> sectionDefinitions = new ArrayList<SectionDefinition>();
        SectionDefinition sectionDefinition = new SectionDefinition();
        sectionDefinition.setName("Name");
        sectionDefinitions.add(sectionDefinition);
        return sectionDefinitions;
    }
}
