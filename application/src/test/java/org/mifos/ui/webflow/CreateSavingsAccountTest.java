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

package org.mifos.ui.webflow;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.mifos.platform.validation.MifosBeanValidator;
import org.mifos.ui.core.controller.CreateSavingsAccountController;
import org.mifos.ui.core.controller.CreateSavingsAccountFormBean;
import org.mockito.Mock;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
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

    MifosBeanValidator validator = new MifosBeanValidator();
    LocalValidatorFactoryBean targetValidator = new LocalValidatorFactoryBean();

    @Mock
    CreateSavingsAccountController controller = mock(CreateSavingsAccountController.class);

    CreateSavingsAccountFormBean formBean = new CreateSavingsAccountFormBean();

    @Test
    public void testStartFlow() {

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

        assertCurrentStateEquals("selectCustomerStep");
    }

    @Test
    public void testSelectCustomerStep_CustomerSelected() {

        setCurrentState("selectCustomerStep");

        doNothing().when(controller).getProductOfferings(formBean);

        MockExternalContext context = new MockExternalContext();
        MockParameterMap requestParameterMap = new MockParameterMap();
        requestParameterMap.put("customerId", "1");
        context.setRequestParameterMap(requestParameterMap);
        context.setEventId("customerSelected");
        resumeFlow(context);

        assertCurrentStateEquals("selectProductOfferingStep");
    }

    @Test
    public void testSelectCustomerStep_SearchTermEntered() {

        setCurrentState("selectCustomerStep");

        MockExternalContext context = new MockExternalContext();
        context.setEventId("searchTermEntered");
        resumeFlow(context);

        assertCurrentStateEquals("selectCustomerStep");
    }

    public void testSelectProductOfferingStep_ProductSelected() {

        setCurrentState("selectProductOfferingStep");

        doNothing().when(controller).loadProduct(1, formBean);

        MockExternalContext context = new MockExternalContext();
        MockParameterMap requestParameterMap = new MockParameterMap();
        requestParameterMap.put("productId", "1");
        context.setRequestParameterMap(requestParameterMap);
        context.setEventId("productSelected");
        resumeFlow(context);

        assertCurrentStateEquals("enterAccountDetailsStep");
    }

    @Test
    public void testEnterAccountDetailsStep_DetailsEntered() {

        setCurrentState("enterAccountDetailsStep");

        MockExternalContext context = new MockExternalContext();
        context.setEventId("detailsEntered");
        resumeFlow(context);

        assertCurrentStateEquals("previewStep");
    }

    @Test
    public void testEnterAccountDetailsStep_NewProductSelected() {

        setCurrentState("enterAccountDetailsStep");

        MockExternalContext context = new MockExternalContext();
        MockParameterMap requestParameterMap = new MockParameterMap();
        requestParameterMap.put("productId", "1");
        context.setRequestParameterMap(requestParameterMap);
        context.setEventId("newProductSelected");
        resumeFlow(context);

        assertCurrentStateEquals("enterAccountDetailsStep");
    }

    @Test
    public void testPreviewStep_Edit() {

        setCurrentState("previewStep");

        MockExternalContext context = new MockExternalContext();
        context.setEventId("edit");
        resumeFlow(context);

        assertCurrentStateEquals("enterAccountDetailsStep");
    }

    @Test
    public void testPreviewStep_SaveForLater() {

        setCurrentState("previewStep");

        MockExternalContext context = new MockExternalContext();
        context.setEventId("saveForLater");
        resumeFlow(context);

        assertFlowExecutionEnded();
        assertFlowExecutionOutcomeEquals("complete");
    }

    @Test
    public void testPreviewStep_SaveForApproval() {

        setCurrentState("previewStep");

        MockExternalContext context = new MockExternalContext();
        context.setEventId("saveForApproval");
        resumeFlow(context);

        assertFlowExecutionEnded();
        assertFlowExecutionOutcomeEquals("complete");
    }

    @Override
    protected void configureFlowBuilderContext(
            MockFlowBuilderContext builderContext) {

        builderContext.registerBean("validator", validator);
        builderContext.registerBean("createSavingsAccountController",
                controller);
        builderContext.registerBean("savingsAccountFormBean", formBean);
    }

    @Override
    protected FlowDefinitionResource getResource(
            FlowDefinitionResourceFactory resourceFactory) {

        FlowDefinitionResource resource = resourceFactory
                .createFileResource("src/main/resources/META-INF/resources/WEB-INF/flows/createSavingsAccount.xml");
        return resource;
    }
}
