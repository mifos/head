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
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.struts.actionforms.QuestionDto;
import org.mifos.customers.struts.actionforms.QuestionGroupDto;
import org.mifos.customers.struts.actionforms.SectionDto;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.platform.questionnaire.service.ChoiceDetail;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupInstanceDetail;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mifos.customers.client.util.helpers.ClientConstants.EVENT_CREATE;
import static org.mifos.customers.client.util.helpers.ClientConstants.SOURCE_CLIENT;
import static org.mifos.framework.struts.tags.MifosTagUtils.xmlEscape;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientCustActionTest {

    @Mock
    private QuestionnaireServiceFacade questionnaireServiceFacade;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private FlowManager flowManager;

    @Mock
    private ClientBO clientBO;

    private ClientCustAction clientCustAction;
    public static final String FLOW_KEY = "FlowKey";

    @Before
    public void setUp() {
        clientCustAction = new ClientCustAction();
    }

    @Test
    public void shouldGetQuestionGroupsByEventSource() throws ApplicationException {
        when(questionnaireServiceFacade.getQuestionGroups(EVENT_CREATE, SOURCE_CLIENT)).thenReturn(asList(getQuestionGroupDetail("QG1", asList("red", "green", "blue"))));
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
        assertThat(questions1.size(), is(1));
        QuestionDto question1 = questions1.get(0);
        assertThat(question1.getId(), is(111));
        assertThat(question1.getText(), is("Question1"));
        assertThat(question1.getQuestionType(), is(QuestionType.SINGLE_SELECT));
        assertThat(question1.getAnswerChoices().get(0).getChoiceText(), is("red"));
        assertThat(question1.getAnswerChoices().get(1).getChoiceText(), is("green"));
        assertThat(question1.getAnswerChoices().get(2).getChoiceText(), is("blue"));
        assertThat(question1.isRequired(), is(true));
        verify(questionnaireServiceFacade, times(1)).getQuestionGroups(EVENT_CREATE, SOURCE_CLIENT);
    }

    @Test
    public void shouldSetQuestionGroupInstanceDetailsInSession() throws PageExpiredException {
        List<QuestionGroupInstanceDetail> instanceDetails = asList(getQuestionGroupInstanceDetail("QG1"), getQuestionGroupInstanceDetail("QG2"));
        when(questionnaireServiceFacade.getQuestionGroupInstances(101, "View", "Client")).thenReturn(instanceDetails);
        when(request.getAttribute(Constants.CURRENTFLOWKEY)).thenReturn(FLOW_KEY);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(Constants.FLOWMANAGER)).thenReturn(flowManager);
        Flow flow = new Flow();
        when(flowManager.getFlowWithValidation(FLOW_KEY)).thenReturn(flow);
        clientCustAction.setQuestionGroupInstances(questionnaireServiceFacade, request, 101);
        assertThat((List<QuestionGroupInstanceDetail>) flow.getObjectFromSession("questionGroupInstances"), is(instanceDetails));
        verify(questionnaireServiceFacade, times(1)).getQuestionGroupInstances(101, "View", "Client");
        verify(request, times(1)).getAttribute(Constants.CURRENTFLOWKEY);
        verify(request, times(1)).getSession();
        verify(session, times(1)).getAttribute(Constants.FLOWMANAGER);
    }

    @Test
    public void testPrepareSurveySelection() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(Constants.RANDOMNUM)).thenReturn("123");
        when(clientBO.getDisplayName()).thenReturn("displayName");
        when(clientBO.getCustomerId()).thenReturn(1);
        when(clientBO.getOffice()).thenReturn(getOffice());
        when(clientBO.getOfficeId()).thenReturn((short) 1);
        when(clientBO.getGlobalCustNum()).thenReturn("globalCustomerNumber");

        clientCustAction.prepareSurveySelection(request, clientBO, (short)2);

        verify(session, times(1)).setAttribute("source", "Client");
        verify(session, times(1)).setAttribute("event", "View");
        verify(session, times(1)).setAttribute("questionnaireFor", xmlEscape("displayName"));
        verify(session, times(1)).setAttribute("entityId", 1);
        verify(session, times(1)).setAttribute("creatorId", new Short("2"));
        verify(session, times(1)).setAttribute(Mockito.eq("urlMap"), Mockito.anyMap());
    }

    private OfficeBO getOffice() {
        OfficeBO office = new OfficeBO();
        office.setOfficeName("officeName");
        return office;
    }

    private QuestionGroupInstanceDetail getQuestionGroupInstanceDetail(String questionGroupTitle) {
        QuestionGroupInstanceDetail detail = new QuestionGroupInstanceDetail();
        detail.setDateCompleted(Calendar.getInstance().getTime());
        detail.setQuestionGroupDetail(getQuestionGroupDetail(questionGroupTitle, asList("red", "green", "blue")));
        return detail;
    }

    private QuestionGroupDetail getQuestionGroupDetail(String title, List<String> answerChoices) {
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail();
        questionGroupDetail.setTitle(title);
        questionGroupDetail.setId(123);
        questionGroupDetail.setSectionDetails(asList(getSectionDetail("Section1", "Question1", answerChoices)));
        return questionGroupDetail;
    }

    private SectionDetail getSectionDetail(String name, String title, List<String> answerChoices) {
        SectionDetail sectionDetail = new SectionDetail();
        sectionDetail.setName(name);
        QuestionDetail questionDetail = new QuestionDetail(111, title, title, QuestionType.SINGLE_SELECT);
        List<ChoiceDetail> choiceDetails = new ArrayList<ChoiceDetail>();
        for (String answerChoice : answerChoices) {
            choiceDetails.add(new ChoiceDetail(answerChoice));
        }
        questionDetail.setAnswerChoices(choiceDetails);
        sectionDetail.setQuestionDetails(asList(new SectionQuestionDetail(questionDetail, true)));
        return sectionDetail;
    }
}
