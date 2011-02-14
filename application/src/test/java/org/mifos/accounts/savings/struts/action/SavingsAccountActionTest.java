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

package org.mifos.accounts.savings.struts.action;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupInstanceDetail;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mifos.platform.questionnaire.service.dtos.ChoiceDto;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class SavingsAccountActionTest {

    @Mock
    private QuestionnaireServiceFacade questionnaireServiceFacade;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private FlowManager flowManager;

    private SavingsAction savingsAccountAction;
    public static final String FLOW_KEY = "FlowKey";

    @Before
    public void setUp() throws Exception {
        savingsAccountAction = new SavingsAction();
    }

    @Test
    public void shouldSetQuestionGroupInstanceDetailsInSession() throws PageExpiredException {
        List<QuestionGroupInstanceDetail> instanceDetails = asList(getQuestionGroupInstanceDetail("QG1"), getQuestionGroupInstanceDetail("QG2"));
        when(questionnaireServiceFacade.getQuestionGroupInstances(101, "View", "Savings")).thenReturn(instanceDetails);
        when(request.getAttribute(Constants.CURRENTFLOWKEY)).thenReturn(FLOW_KEY);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(Constants.FLOWMANAGER)).thenReturn(flowManager);
        Flow flow = new Flow();
        when(flowManager.getFlowWithValidation(FLOW_KEY)).thenReturn(flow);
        savingsAccountAction.setQuestionGroupInstances(questionnaireServiceFacade, request, 101);
        assertThat((List<QuestionGroupInstanceDetail>) flow.getObjectFromSession("questionGroupInstances"), is(instanceDetails));
        verify(questionnaireServiceFacade, times(1)).getQuestionGroupInstances(101, "View", "Savings");
        verify(request, times(1)).getAttribute(Constants.CURRENTFLOWKEY);
        verify(request, times(1)).getSession();
        verify(session, times(1)).getAttribute(Constants.FLOWMANAGER);
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
        QuestionDetail questionDetail = new QuestionDetail(111, title, QuestionType.SINGLE_SELECT, true, true);
        List<ChoiceDto> choiceDtos = new ArrayList<ChoiceDto>();
        for (String answerChoice : answerChoices) {
            choiceDtos.add(new ChoiceDto(answerChoice));
        }
        questionDetail.setAnswerChoices(choiceDtos);
        sectionDetail.setQuestionDetails(asList(new SectionQuestionDetail(questionDetail, true)));
        return sectionDetail;
    }
}