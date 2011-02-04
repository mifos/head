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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import junit.framework.Assert;

import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Before;
import org.junit.Test;
import org.mifos.dto.domain.SavingsProductDto;
import org.mifos.dto.screen.SavingsProductReferenceDto;
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
    public void validateCustomerSearchStep_EmptySearchStringShouldFail() {
        formBean.validateCustomerSearchStep(validationContext);

        MessageContext messageContext = validationContext.getMessageContext();
        Message[] messages = messageContext.getAllMessages();

        Assert.assertEquals(1, messages.length);
        Message message = messages[0];
        Assert.assertEquals("searchString", message.getSource());
        verifyErrorMessage(NotEmpty.class, message);
    }

    @Test
    public void validateCustomerSearchStep_NonEmptySearchStringShouldPass() {
        formBean.setSearchString("foo");
        formBean.validateCustomerSearchStep(validationContext);

        MessageContext messageContext = validationContext.getMessageContext();
        Message[] messages = messageContext.getAllMessages();

        Assert.assertEquals(0, messages.length);
    }

    @Test
    public void validateSelectCustomerStep_EmptySearchStringShouldFail() {
        // new search term entered in select customer step. this transitions
        // back to customer search step.
        validateCustomerSearchStep_EmptySearchStringShouldFail();
    }

    @Test
    public void validateSelectCustomerStep_NonEmptySearchStringShouldPass() {
        // new search term entered in select customer step. this transitions
        // back to customer search step.
        validateCustomerSearchStep_NonEmptySearchStringShouldPass();
    }

    @Test
    public void validateSelectProductOfferingStep_EmptyProductIdShouldFail() {
        formBean.validateSelectProductOfferingStep(validationContext);

        MessageContext messageContext = validationContext.getMessageContext();
        Message[] messages = messageContext.getAllMessages();

        Assert.assertEquals(1, messages.length);
        Message message = messages[0];
        Assert.assertEquals("productId", message.getSource());
        verifyErrorMessage(NotNull.class, message);
    }

    @Test
    public void validateSelectProductOfferingStep_NonEmptyProductIdShouldPass() {
        formBean.setProductId(1);
        formBean.validateSelectProductOfferingStep(validationContext);

        MessageContext messageContext = validationContext.getMessageContext();
        Message[] messages = messageContext.getAllMessages();

        Assert.assertEquals(0, messages.length);
    }

    @Test
    public void validateEnterAccountDetailsStep_MandatoryDeposit_EmptyAmountShouldFail() {
        validateEnterAccountDetailsStep_MandatoryDeposit("", Pattern.class,
                false);
    }

    @Test
    public void validateEnterAccountDetailsStep_MandatoryDeposit_NullAmountShouldFail() {
        validateEnterAccountDetailsStep_MandatoryDeposit(null, NotNull.class,
                false);
    }

    @Test
    public void validateEnterAccountDetailsStep_MandatoryDeposit_InvalidFormatAmountShouldFail() {
        validateEnterAccountDetailsStep_MandatoryDeposit("xyz", Pattern.class,
                false);
    }

    @Test
    public void validateEnterAccountDetailsStep_MandatoryDeposit_NumericAmountShouldPass() {
        validateEnterAccountDetailsStep_MandatoryDeposit("123.0", null, true);
    }

    private void validateEnterAccountDetailsStep_MandatoryDeposit(
            String amount, Class expectedViolation, boolean passExpected) {
        setDepositType(formBean, CreateSavingsAccountFormBean.MANDATORY_DEPOSIT);
        formBean.setMandatoryDepositAmount(amount);
        formBean.validateEnterAccountDetailsStep(validationContext);

        MessageContext messageContext = validationContext.getMessageContext();
        Message[] messages = messageContext.getAllMessages();

        if (passExpected) {
            Assert.assertEquals(0, messages.length);
            return;
        } else {
            Assert.assertEquals(1, messages.length);
            Message message = messages[0];
            Assert.assertEquals("mandatoryDepositAmount", message.getSource());
            verifyErrorMessage(expectedViolation, message);
        }
    }

    /**
     * Sets deposit type in the form bean.
     */
    private void setDepositType(CreateSavingsAccountFormBean formBean,
            int depositType) {
        SavingsProductDto productDetails = mock(SavingsProductDto.class);
        when(productDetails.getDepositType()).thenReturn(depositType);
        SavingsProductReferenceDto productRef = mock(SavingsProductReferenceDto.class);
        when(productRef.getSavingsProductDetails()).thenReturn(productDetails);
        formBean.setProduct(productRef);
    }

    private void verifyErrorMessage(Class constraint, Message message) {
        String expected = constraint.getSimpleName() + "."
                + formBean.getClass().getSimpleName() + "."
                + message.getSource();
        Assert.assertEquals(expected, message.getText());
    }
}
