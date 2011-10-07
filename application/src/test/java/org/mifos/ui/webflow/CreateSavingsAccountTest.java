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

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mifos.config.servicefacade.ConfigurationServiceFacade;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.validation.MifosBeanValidator;
import org.mifos.ui.core.controller.CreateSavingsAccountController;
import org.mifos.ui.core.controller.CreateSavingsAccountFormBean;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;
import org.springframework.webflow.test.MockParameterMap;
import org.springframework.webflow.test.execution.AbstractXmlFlowExecutionTests;

/**
 * Tests Create Savings Account webflow.
 * 
 * Methods use the following naming convention:
 * 
 * test<state id>_<event id>
 * 
 * @see http 
 *      ://static.springsource.org/spring-webflow/docs/2.0.x/reference/htmlsingle
 *      /spring-webflow-reference.html#testing
 */
public class CreateSavingsAccountTest extends AbstractXmlFlowExecutionTests {

    private CreateSavingsAccountController controller;
    private CreateSavingsAccountFormBean formBean;
    private QuestionnaireServiceFacade questionnaireServiceFacade;
    private ConfigurationServiceFacade configurationServiceFacade;

    @Override
	@Before
    public void setUp() {
        controller = mock(CreateSavingsAccountController.class);
        formBean = mock(CreateSavingsAccountFormBean.class);
        questionnaireServiceFacade = mock(QuestionnaireServiceFacade.class);
        configurationServiceFacade = mock(ConfigurationServiceFacade.class);
    }

    @Test
    public void testStartFlow_CustomerIdProvided() {

        Integer customerId = 1;

        MutableAttributeMap input = new LocalAttributeMap();
        MockParameterMap requestParameterMap = new MockParameterMap();
        requestParameterMap.put("customerId", customerId.toString());

        MockExternalContext context = new MockExternalContext();
        context.setRequestParameterMap(requestParameterMap);
        startFlow(input, context);

        assertCurrentStateEquals("selectProductOfferingStep");
        verify(controller).customerSelected(eq(customerId), any(CreateSavingsAccountFormBean.class));
        verify(controller).getProductOfferings(any(CreateSavingsAccountFormBean.class));
    }

    @Test
    public void testStartFlow_CustomerIdNotProvided() {

        MutableAttributeMap input = new LocalAttributeMap();
        MockExternalContext context = new MockExternalContext();
        startFlow(input, context);

        assertCurrentStateEquals("customerSearchStep");
    }

    @Test
    public void testCustomerSearchStep_SearchTermEntered() {

        setCurrentState("customerSearchStep");

        MockExternalContext context = new MockExternalContext();
        context.setEventId("searchTermEntered");
        resumeFlow(context);

        verify(controller).searchCustomers(formBean);
        assertCurrentStateEquals("selectCustomerStep");
    }

    @Test
    public void testCustomerSearchStep_Cancel() {
        setCurrentState("customerSearchStep");
        
        MockExternalContext context = new MockExternalContext();
        context.setEventId("cancel");
        resumeFlow(context);
        
        assertFlowExecutionEnded();
    }

    @Test
    public void testSelectCustomerStep_CustomerSelected() {

        Integer customerId = 1;
        setCurrentState("selectCustomerStep");

        doNothing().when(controller).getProductOfferings(formBean);

        MockExternalContext context = new MockExternalContext();
        MockParameterMap requestParameterMap = new MockParameterMap();
        requestParameterMap.put("customerId", customerId.toString());
        context.setRequestParameterMap(requestParameterMap);
        context.setEventId("customerSelected");
        resumeFlow(context);

        verify(controller).customerSelected(customerId, formBean);
        verify(controller).getProductOfferings(formBean);
        assertCurrentStateEquals("selectProductOfferingStep");
    }

    @Test
    public void testSelectCustomerStep_SearchTermEntered() {

        setCurrentState("selectCustomerStep");

        MockExternalContext context = new MockExternalContext();
        context.setEventId("searchTermEntered");
        resumeFlow(context);

        verify(controller).searchCustomers(formBean);
        assertCurrentStateEquals("selectCustomerStep");
    }

    @Test
    public void testSelectCustomerStep_Cancel() {
        setCurrentState("selectCustomerStep");
        
        MockExternalContext context = new MockExternalContext();
        context.setEventId("cancel");
        resumeFlow(context);
        
        assertFlowExecutionEnded();
    }

    @Test
    public void testSelectProductOfferingStep_ProductSelected() {

        Integer productId = 1;
        setCurrentState("selectProductOfferingStep");

        doNothing().when(controller).loadProduct(1, formBean);

        MockExternalContext context = new MockExternalContext();
        MockParameterMap requestParameterMap = new MockParameterMap();
        requestParameterMap.put("productId", productId.toString());
        context.setRequestParameterMap(requestParameterMap);
        context.setEventId("productSelected");
        resumeFlow(context);

        verify(controller).loadProduct(productId, formBean);
        assertCurrentStateEquals("enterAccountDetailsStep");
    }

    @Test
    public void testSelectProductOfferingStep_Cancel() {
        setCurrentState("selectProductOfferingStep");
        
        MockExternalContext context = new MockExternalContext();
        context.setEventId("cancel");
        resumeFlow(context);
        
        assertFlowExecutionEnded();
    }
    
    /**
     * Account info entry step is complete. There is a question group defined
     * for "Create Savings" work flow.
     */
    @Test
    public void testEnterAccountDetailsStep_DetailsEntered_WithQuestionGroup() {

        List<QuestionGroupDetail> groups = new ArrayList<QuestionGroupDetail>();
        groups.add(new QuestionGroupDetail());
        when(formBean.getQuestionGroups()).thenReturn(groups);

        setCurrentState("enterAccountDetailsStep");

        MockExternalContext context = new MockExternalContext();
        context.setEventId("detailsEntered");
        resumeFlow(context);

        assertCurrentStateEquals("answerQuestionGroupStep");
    }

    /**
     * Account info entry step is complete. There is NO question group defined
     * for "Create Savings" work flow.
     */
    @Test
    public void testEnterAccountDetailsStep_DetailsEntered_NoQuestionGroup() {

        List<QuestionGroupDetail> groups = new ArrayList<QuestionGroupDetail>();
        when(formBean.getQuestionGroups()).thenReturn(groups);

        setCurrentState("enterAccountDetailsStep");

        MockExternalContext context = new MockExternalContext();
        context.setEventId("detailsEntered");
        resumeFlow(context);

        assertCurrentStateEquals("previewStep");
    }

    @Test
    public void testEnterAccountDetailsStep_NewProductSelected() {

        Integer productId = 1;
        setCurrentState("enterAccountDetailsStep");

        MockExternalContext context = new MockExternalContext();
        MockParameterMap requestParameterMap = new MockParameterMap();
        requestParameterMap.put("productId", productId.toString());
        context.setRequestParameterMap(requestParameterMap);
        context.setEventId("newProductSelected");
        resumeFlow(context);

        verify(controller).loadProduct(productId, formBean);
        assertCurrentStateEquals("enterAccountDetailsStep");
    }

    @Test
    public void testEnterAccountDetailsStep_Cancel() {
        setCurrentState("enterAccountDetailsStep");
        
        MockExternalContext context = new MockExternalContext();
        context.setEventId("cancel");
        resumeFlow(context);
        
        assertFlowExecutionEnded();
    }
    
    @Test
    public void testAnswerQuestionGroupStep_QuestionsAnswered() {

        List<QuestionGroupDetail> questionGroups = new ArrayList<QuestionGroupDetail>();
        doNothing().when(questionnaireServiceFacade).validateResponses(
                questionGroups);

        setCurrentState("answerQuestionGroupStep");

        MockExternalContext context = new MockExternalContext();
        context.setEventId("questionsAnswered");
        resumeFlow(context);

        assertCurrentStateEquals("previewStep");
    }

    @Test
    public void testAnswerQuestionGroupStep_Cancel() {
        setCurrentState("answerQuestionGroupStep");
        
        MockExternalContext context = new MockExternalContext();
        context.setEventId("cancel");
        resumeFlow(context);
        
        assertFlowExecutionEnded();
    }
    
    @Test
    public void testPreviewStep_EditAccountDetails() {

        setCurrentState("previewStep");

        MockExternalContext context = new MockExternalContext();
        context.setEventId("editAccountDetails");
        resumeFlow(context);

        verify(controller, never()).loadQuestionGroups(formBean);
        assertCurrentStateEquals("editAccountDetailsStep");
    }

    @Test
    public void testPreviewStep_EditQuestionGroup() {
        setCurrentState("previewStep");

        MockExternalContext context = new MockExternalContext();
        context.setEventId("editQuestionGroup");
        resumeFlow(context);

        verify(controller, never()).loadQuestionGroups(formBean);
        assertCurrentStateEquals("editQuestionGroupStep");
    }

    @Test
    public void testPreviewStep_SaveForLater() {

        setCurrentState("previewStep");

        MockExternalContext context = new MockExternalContext();
        context.setEventId("saveForLater");
        resumeFlow(context);

        verify(controller).createAccountInPartialApplicationState(formBean);
        assertFlowExecutionEnded();
        assertFlowExecutionOutcomeEquals("complete");
    }

    @Test
    public void testPreviewStep_SaveForApproval() {

        setCurrentState("previewStep");

        MockExternalContext context = new MockExternalContext();
        context.setEventId("saveForApproval");
        resumeFlow(context);

        verify(controller).createAccountInPendingApprovalState(formBean);
        assertFlowExecutionEnded();
        assertFlowExecutionOutcomeEquals("complete");
    }

    @Test
    public void testPreviewStep_SaveForApproved() {

        setCurrentState("previewStep");

        MockExternalContext context = new MockExternalContext();
        context.setEventId("approve");
        resumeFlow(context);

        verify(controller).createAccountInActiveState(formBean);
        assertFlowExecutionEnded();
        assertFlowExecutionOutcomeEquals("complete");
    }

    @Test
    public void testPreviewStep_Cancel() {
        setCurrentState("previewStep");
        
        MockExternalContext context = new MockExternalContext();
        context.setEventId("cancel");
        resumeFlow(context);
        
        assertFlowExecutionEnded();
    }
    
    @Override
    protected void configureFlowBuilderContext(
            MockFlowBuilderContext builderContext) {

        // setup bean dependencies
        builderContext.registerBean("createSavingsAccountController",
                controller);
        builderContext.registerBean("savingsAccountFormBean", formBean);
        builderContext.registerBean("questionnaireServiceFacade",
                questionnaireServiceFacade);
        builderContext.registerBean("configurationServiceFacade", configurationServiceFacade);
        builderContext
                .registerBean("validator", mock(MifosBeanValidator.class));
    }

    @Override
    protected FlowDefinitionResource getResource(
            FlowDefinitionResourceFactory resourceFactory) {

        FlowDefinitionResource resource = resourceFactory
                .createFileResource("src/main/resources/META-INF/resources/WEB-INF/flows/createSavingsAccount.xml");
        return resource;
    }
}
