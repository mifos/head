/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.accounts.productdefinition.struts.actionforms;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoanPrdActionFormTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private FlowManager flowManager;

    private LoanPrdActionForm loanPrdActionForm;
    private static final String FLOW_KEY = "FlowKey";

    @Before
    public void setUp() {
        loanPrdActionForm = new LoanPrdActionForm();
    }

    @Test
    public void testApplicableMaster() throws Exception {
        loanPrdActionForm.setPrdApplicableMaster("" + ApplicableTo.CLIENTS.getValue());
        Assert.assertEquals(ApplicableTo.CLIENTS, loanPrdActionForm.getPrdApplicableMasterEnum());
    }

    @Test
    public void testSetFromEnum() throws Exception {
        loanPrdActionForm.setPrdApplicableMaster(ApplicableTo.ALLCUSTOMERS);
        Assert.assertEquals(ApplicableTo.ALLCUSTOMERS, loanPrdActionForm.getPrdApplicableMasterEnum());
    }

    @Test
    public void shouldSetSelectedQuestionGroupsOnSession() throws PageExpiredException {
        when(request.getAttribute(Constants.CURRENTFLOWKEY)).thenReturn(FLOW_KEY);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(Constants.FLOWMANAGER)).thenReturn(flowManager);
        Flow flow = new Flow();
        when(flowManager.getFlowWithValidation(FLOW_KEY)).thenReturn(flow);
        List<QuestionGroupDetail> questionGroupDetails = asList(getQuestionGroupDetail(1, "QG1"), getQuestionGroupDetail(2, "QG2"),
                                                            getQuestionGroupDetail(3, "QG3"), getQuestionGroupDetail(4, "QG4"));
        when(flowManager.getFromFlow(FLOW_KEY, ProductDefinitionConstants.SRCQGLIST)).thenReturn(questionGroupDetails);
        loanPrdActionForm.setLoanOfferingQGs(new String[] {"1", "4"});
        loanPrdActionForm.setSelectedQuestionGroups(request);
        List<QuestionGroupDetail> selectedQGDetails = (List<QuestionGroupDetail>) flow.getObjectFromSession(ProductDefinitionConstants.SELECTEDQGLIST);
        assertThat(selectedQGDetails, is(notNullValue()));
        assertThat(selectedQGDetails.get(0).getId(), is(1));
        assertThat(selectedQGDetails.get(0).getTitle(), is("QG1"));
        assertThat(selectedQGDetails.get(1).getId(), is(4));
        assertThat(selectedQGDetails.get(1).getTitle(), is("QG4"));
    }

    @Test
    public void shouldSetSelectedQuestionGroupsOnSessionForNoQGs() throws PageExpiredException {
        when(request.getAttribute(Constants.CURRENTFLOWKEY)).thenReturn(FLOW_KEY);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(Constants.FLOWMANAGER)).thenReturn(flowManager);
        Flow flow = new Flow();
        when(flowManager.getFlowWithValidation(FLOW_KEY)).thenReturn(flow);
        List<QuestionGroupDetail> questionGroupDetails = asList(getQuestionGroupDetail(1, "QG1"), getQuestionGroupDetail(2, "QG2"),
                                                            getQuestionGroupDetail(3, "QG3"), getQuestionGroupDetail(4, "QG4"));
        when(flowManager.getFromFlow(FLOW_KEY, ProductDefinitionConstants.SRCQGLIST)).thenReturn(questionGroupDetails);
        loanPrdActionForm.setLoanOfferingQGs(null);
        loanPrdActionForm.setSelectedQuestionGroups(request);
        List<QuestionGroupDetail> selectedQGDetails = (List<QuestionGroupDetail>) flow.getObjectFromSession(ProductDefinitionConstants.SELECTEDQGLIST);
        assertThat(selectedQGDetails, is(notNullValue()));
        assertThat(selectedQGDetails.isEmpty(), is(true));
    }

    private QuestionGroupDetail getQuestionGroupDetail(int id, String title) {
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail();
        questionGroupDetail.setId(id);
        questionGroupDetail.setTitle(title);
        return questionGroupDetail;
    }
}
