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

package org.mifos.customers.client.struts.action;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.customers.struts.actionforms.QuestionDto;
import org.mifos.customers.struts.actionforms.QuestionGroupDto;
import org.mifos.customers.struts.actionforms.SectionDto;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.contract.*;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mifos.customers.client.util.helpers.ClientConstants.EVENT_CREATE;
import static org.mifos.customers.client.util.helpers.ClientConstants.SOURCE_CLIENT;
import static org.mifos.platform.questionnaire.contract.QuestionType.DATE;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientCustActionTest {

    @Mock
    private QuestionnaireServiceFacade questionnaireServiceFacade;

    private ClientCustAction clientCustAction;

    @Before
    public void setUp() {
        clientCustAction = new ClientCustAction();
    }

    @Test
    public void shouldGetQuestionGroupsByEventSource() throws ApplicationException {
        when(questionnaireServiceFacade.getQuestionGroups(EVENT_CREATE, SOURCE_CLIENT)).thenReturn(asList(getQuestionGroupDetail()));
        List<QuestionGroupDto> questionGroups = clientCustAction.getQuestionGroups(questionnaireServiceFacade);
        assertThat(questionGroups, is(notNullValue()));
        assertThat(questionGroups.size(), is(1));
        assertThat(questionGroups.get(0).getId(), is(123));
        List<SectionDto> sections = questionGroups.get(0).getSections();
        assertThat(sections, is(notNullValue()));
        assertThat(sections.size(), is(1));
        SectionDto section1 = sections.get(0);
        assertThat(section1.getName(), is("Section1"));
        List<QuestionDto> questions1 = section1.getQuestions();
        assertThat(questions1, is(notNullValue()));
        assertThat(questions1.size(),  is(1));
        QuestionDto question1 = questions1.get(0);
        assertThat(question1.getId(), is(111));
        assertThat(question1.getText(), is("Question1"));
        assertThat(question1.getQuestionType(), is(DATE));
        assertThat(question1.isRequired(), is(true));
        verify(questionnaireServiceFacade, times(1)).getQuestionGroups(EVENT_CREATE, SOURCE_CLIENT);
    }

    private QuestionGroupDetail getQuestionGroupDetail() {
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail();
        questionGroupDetail.setId(123);
        questionGroupDetail.setSectionDetails(asList(getSectionDetail("Section1", "Question1")));
        return questionGroupDetail;
    }

    private SectionDetail getSectionDetail(String name, String title) {
        SectionDetail sectionDetail = new SectionDetail();
        sectionDetail.setName(name);
        sectionDetail.setQuestionDetails(asList(new SectionQuestionDetail(new QuestionDetail(111, title, title,  DATE), true)));
        return sectionDetail;
    }
}
