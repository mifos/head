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

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.application.servicefacade.LoanServiceFacade;
import org.mifos.clientportfolio.loan.ui.LoanAccountStatusFormBean;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.ui.core.controller.LoanAccountStatusController;
import org.mifos.ui.core.controller.EditLoanAccountStatusFormBean;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;
import org.springframework.webflow.test.execution.AbstractXmlFlowExecutionTests;
import static org.mockito.Mockito.*;

/**
 * Unit test for Spring Web Flow "editLoanAccountStatus"
 */
public class EditLoanAccountStatusTest extends AbstractXmlFlowExecutionTests {

    private LoanAccountStatusController controller;
//    private EditLoanAccountStatusFormBean formBean;
    private LoanAccountServiceFacade loanAccountServiceFacade;
    private AdminServiceFacade adminServiceFacade;
    private QuestionnaireServiceFacade questionnaireServiceFacade;
    private LoanAccountStatusFormBean loanAccountStatusFormBean;
    
    @Before
    protected void setUp() {
        controller = mock(LoanAccountStatusController.class);
//        formBean = new EditLoanAccountStatusFormBean();
        loanAccountServiceFacade = mock(LoanAccountServiceFacade.class);
        adminServiceFacade = mock(AdminServiceFacade.class);
        questionnaireServiceFacade = mock(QuestionnaireServiceFacade.class);
        loanAccountStatusFormBean = new LoanAccountStatusFormBean();
    }
    
    @Test
    public void testStartFlow() {
        MutableAttributeMap input = new LocalAttributeMap();
        MockExternalContext context = new MockExternalContext();
        startFlow(input, context);

        assertCurrentStateEquals("editStatusStep");
    }
    
    @Test
    public void testEditStatusStep_DetailsEntered_NoQuestionGroup() {
        setCurrentState("editStatusStep");

        MockExternalContext context = new MockExternalContext();
        context.setEventId("detailsEntered");
        resumeFlow(context);

        assertCurrentStateEquals("previewStep");
    }

    @Test
    public void testEditStatusStep_DetailsEntered_WithQuestionGroup() {
        setCurrentState("editStatusStep");

        MockExternalContext context = new MockExternalContext();
        context.setEventId("detailsEntered");
        resumeFlow(context);

        assertCurrentStateEquals("answerQuestionStep");
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
        context.setEventId("save");
        resumeFlow(context);

        assertCurrentStateEquals("editStatusStep");
    }    

    @Override
    protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {

        // setup bean dependencies
//        builderContext.registerBean("formBean", formBean);
        builderContext.registerBean("loanAccountServiceFacade", loanAccountServiceFacade);
        builderContext.registerBean("adminServiceFacade", adminServiceFacade);
        builderContext.registerBean("loanAccountStatusController", controller);
        builderContext.registerBean("questionnaireServiceFacade", questionnaireServiceFacade);
        builderContext.registerBean("loanAccountStatusFormBean", loanAccountStatusFormBean);
    }

    @Override
    protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {

        return resourceFactory
                .createFileResource("src/main/resources/META-INF/resources/WEB-INF/flows/editLoanAccountStatus.xml");
    }
}
