/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.ui.webflow;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.clientportfolio.loan.ui.LoanAccountController;
import org.mifos.clientportfolio.loan.ui.LoanAccountQuestionGroupFormBean;
import org.mifos.clientportfolio.loan.ui.LoanAccountStatusFormBean;
import org.mifos.clientportfolio.newloan.applicationservice.LoanApplicationStateDto;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.ui.core.controller.LoanAccountStatusController;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;
import org.springframework.webflow.test.execution.AbstractXmlFlowExecutionTests;

/**
 * Unit test for Spring Web Flow "editLoanAccountStatus"
 */
public class EditLoanAccountStatusTest extends AbstractXmlFlowExecutionTests {

    private LoanAccountStatusController controller;
    private LoanAccountController loanAccountController;
    private LoanAccountServiceFacade loanAccountServiceFacade;
    private QuestionnaireServiceFacade questionnaireServiceFacade;
    private LoanAccountStatusFormBean loanAccountStatusFormBean;
    private LoanAccountQuestionGroupFormBean loanAccountQuestionGroupFormBean;
    
    // see AccountState enum
    private static final int STATE_PENDING = 1;
    private static final int STATE_PARTIAL = 2;
    private static final int STATE_APPROVED = 3;
    private static final int STATE_CANCELLED = 10;
    
    @Before
    protected void setUp() {
        controller = mock(LoanAccountStatusController.class);
        loanAccountController = mock(LoanAccountController.class);
        loanAccountServiceFacade = mock(LoanAccountServiceFacade.class);
        questionnaireServiceFacade = mock(QuestionnaireServiceFacade.class);
        loanAccountQuestionGroupFormBean = new LoanAccountQuestionGroupFormBean();
        loanAccountStatusFormBean = new LoanAccountStatusFormBean();
        loanAccountStatusFormBean.setLoanApplicationState(new LoanApplicationStateDto(STATE_PENDING, STATE_PARTIAL, STATE_APPROVED, STATE_CANCELLED));
    }
    
    @Test
    public void testStartFlow() {
        MutableAttributeMap input = new LocalAttributeMap();
        MockExternalContext context = new MockExternalContext();
        startFlow(input, context);

        assertCurrentStateEquals("editStatusStep");
    }

    @Test
    public void testEditStatusStep_DetailsEntered_AccountApproved() {
        loanAccountStatusFormBean.setStatus(STATE_APPROVED);
        List<QuestionGroupDetail> questionGroups = new ArrayList<QuestionGroupDetail>();
        questionGroups.add(new QuestionGroupDetail());
        loanAccountQuestionGroupFormBean.setQuestionGroups(questionGroups);

        setCurrentState("editStatusStep");
        
        MockExternalContext context = new MockExternalContext();
        context.setEventId("detailsEntered");
        resumeFlow(context);

        assertCurrentStateEquals("answerQuestionGroupStep");
    }

    @Test
    public void testEditStatusStep_DetailsEntered_AccountApproved_NoQuestionGroup() {
        loanAccountStatusFormBean.setStatus(STATE_APPROVED);
        List<QuestionGroupDetail> questionGroups = new ArrayList<QuestionGroupDetail>();
        loanAccountQuestionGroupFormBean.setQuestionGroups(questionGroups);
        
        setCurrentState("editStatusStep");

        MockExternalContext context = new MockExternalContext();
        context.setEventId("detailsEntered");
        resumeFlow(context);

        assertCurrentStateEquals("previewStep");
    }
    
    @Test
    public void testEditStatusStep_DetailsEntered_PartialApplication() {
        setCurrentState("editStatusStep");
        loanAccountStatusFormBean.setStatus(STATE_PARTIAL);

        MockExternalContext context = new MockExternalContext();
        context.setEventId("detailsEntered");
        resumeFlow(context);

        assertCurrentStateEquals("previewStep");
    }

    @Test
    public void testPreviewStep_Save() {
        setCurrentState("previewStep");

        MockExternalContext context = new MockExternalContext();
        context.setEventId("save");
        resumeFlow(context);

        // TODO verify mock interaction
        assertFlowExecutionEnded();
    }
    
    @Test
    public void testPreviewStep_EditStatus() {
        setCurrentState("previewStep");

        MockExternalContext context = new MockExternalContext();
        context.setEventId("edit");
        resumeFlow(context);

        assertCurrentStateEquals("editStatusStep");
    }    

    @Override
    protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {

        // setup bean dependencies
        builderContext.registerBean("loanAccountServiceFacade", loanAccountServiceFacade);
        builderContext.registerBean("loanAccountStatusController", controller);
        builderContext.registerBean("loanAccountController", loanAccountController);
        builderContext.registerBean("questionnaireServiceFacade", questionnaireServiceFacade);
        builderContext.registerBean("loanAccountStatusFormBean", loanAccountStatusFormBean);
        builderContext.registerBean("loanAccountQuestionGroupFormBean", loanAccountQuestionGroupFormBean);
    }

    @Override
    protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {

        return resourceFactory
                .createFileResource("src/main/resources/META-INF/resources/WEB-INF/flows/editLoanAccountStatus.xml");
    }
}
