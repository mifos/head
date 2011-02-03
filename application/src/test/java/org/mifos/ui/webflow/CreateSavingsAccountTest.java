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

import org.junit.Before;
import org.junit.Test;
import org.mifos.platform.validation.MifosBeanValidator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;
import org.springframework.webflow.test.execution.AbstractXmlFlowExecutionTests;

/**
 * Tests Create Savings Account webflow.
 * 
 * @see http
 *      ://static.springsource.org/spring-webflow/docs/2.0.x/reference/htmlsingle
 *      /spring-webflow-reference.html#testing
 */
public class CreateSavingsAccountTest extends AbstractXmlFlowExecutionTests {

    MifosBeanValidator validator = new MifosBeanValidator();
    LocalValidatorFactoryBean targetValidator = new LocalValidatorFactoryBean();

    @Before
    public void setUp() {
        targetValidator.afterPropertiesSet();
        validator.setTargetValidator(targetValidator);
    }

    @Test
    public void testStartFlow() {

        MutableAttributeMap input = new LocalAttributeMap();
        MockExternalContext context = new MockExternalContext();
        startFlow(input, context);

        assertCurrentStateEquals("customerSearchStep");
    }

    @Override
    protected void configureFlowBuilderContext(
            MockFlowBuilderContext builderContext) {
        builderContext.registerBean("validator", validator);
    }

    @Override
    protected FlowDefinitionResource getResource(
            FlowDefinitionResourceFactory resourceFactory) {
        FlowDefinitionResource resource = resourceFactory
                .createFileResource("src/main/resources/META-INF/resources/WEB-INF/flows/createSavingsAccount.xml");
        return resource;
    }
}
