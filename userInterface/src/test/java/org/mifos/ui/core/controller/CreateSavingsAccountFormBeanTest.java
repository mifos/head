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

package org.mifos.ui.core.controller;

import junit.framework.Assert;

import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Before;
import org.junit.Test;
import org.mifos.platform.validation.MifosBeanValidator;
import org.mifos.ui.validation.StubValidationContext;
import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class CreateSavingsAccountFormBeanTest {

    private CreateSavingsAccountFormBean formBean;

    private ValidationContext validationContext;

    private MifosBeanValidator validator;

    private LocalValidatorFactoryBean targetValidator;

    @Before
    public void setUp() {
        validator = new MifosBeanValidator();
        targetValidator = new LocalValidatorFactoryBean();
        targetValidator.afterPropertiesSet();
        validator.setTargetValidator(targetValidator);

        formBean = new CreateSavingsAccountFormBean();
        formBean.setValidator(validator);

        validationContext = new StubValidationContext();
    }

    @Test
    public void validateCustomerSearchStep_EmptySearchString() {
        formBean.validateCustomerSearchStep(validationContext);

        MessageContext messageContext = validationContext.getMessageContext();
        Message[] messages = messageContext.getAllMessages();

        Assert.assertEquals(1, messages.length);
        Message message = messages[0];
        Assert.assertEquals("searchString", message.getSource());
        verifyErrorMessage(NotEmpty.class, message);
    }

    private void verifyErrorMessage(Class constraint, Message message) {
        String expected = constraint.getSimpleName() + "."
                + formBean.getClass().getSimpleName() + "."
                + message.getSource();
        Assert.assertEquals(expected, message.getText());
    }
}
