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

package org.mifos.ui.core.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import junit.framework.Assert;

import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Before;
import org.junit.Test;
import org.mifos.dto.domain.SavingsProductDto;
import org.mifos.dto.screen.SavingsProductReferenceDto;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.validation.MifosBeanValidator;
import org.mifos.platform.validations.ValidationException;
import org.mifos.ui.validation.StubValidationContext;
import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class CreateSavingsAccountFormBeanTest {

    private CreateSavingsAccountFormBean formBean;
    private ValidationContext validationContext;
    private QuestionnaireServiceFacade questionnaireServiceFacade;
    private ValidationException validationException;

    @Before
    public void setUp() {
        MifosBeanValidator validator = new MifosBeanValidator();
        LocalValidatorFactoryBean targetValidator = new LocalValidatorFactoryBean();
        targetValidator.afterPropertiesSet();
        validator.setTargetValidator(targetValidator);

        formBean = new CreateSavingsAccountFormBean();
        formBean.setValidator(validator);

        questionnaireServiceFacade = mock(QuestionnaireServiceFacade.class);
        formBean.setQuestionnaireServiceFascade(questionnaireServiceFacade);

        validationContext = new StubValidationContext();

        validationException = new ValidationException("Root");
        validationException.addChildException(new ValidationException("Child"));
    }

    @Test
    public void validateCustomerSearchStepEmptySearchStringShouldFail() {
        formBean.validateCustomerSearchStep(validationContext);

        MessageContext messageContext = validationContext.getMessageContext();
        Message[] messages = messageContext.getAllMessages();

        Assert.assertEquals(1, messages.length);
        Message message = messages[0];
        Assert.assertEquals("searchString", message.getSource());
        verifyErrorMessage(NotEmpty.class, message);
    }

    @Test
    public void validateCustomerSearchStepNonEmptySearchStringShouldPass() {
        formBean.setSearchString("foo");
        formBean.validateCustomerSearchStep(validationContext);

        MessageContext messageContext = validationContext.getMessageContext();
        Message[] messages = messageContext.getAllMessages();

        Assert.assertEquals(0, messages.length);
    }

    @Test
    public void validateSelectCustomerStepEmptySearchStringShouldFail() {
        // new search term entered in select customer step. this transitions
        // back to customer search step.
        validateCustomerSearchStepEmptySearchStringShouldFail();
    }

    @Test
    public void validateSelectCustomerStepNonEmptySearchStringShouldPass() {
        // new search term entered in select customer step. this transitions
        // back to customer search step.
        validateCustomerSearchStepNonEmptySearchStringShouldPass();
    }

    @Test
    public void validateSelectProductOfferingStepEmptyProductIdShouldFail() {
        formBean.validateSelectProductOfferingStep(validationContext);

        MessageContext messageContext = validationContext.getMessageContext();
        Message[] messages = messageContext.getAllMessages();

        Assert.assertEquals(1, messages.length);
        Message message = messages[0];
        Assert.assertEquals("productId", message.getSource());
        verifyErrorMessage(NotNull.class, message);
    }

    @Test
    public void validateSelectProductOfferingStepNonEmptyProductIdShouldPass() {
        formBean.setProductId(1);
        formBean.validateSelectProductOfferingStep(validationContext);

        MessageContext messageContext = validationContext.getMessageContext();
        Message[] messages = messageContext.getAllMessages();

        Assert.assertEquals(0, messages.length);
    }

    @Test
    public void validateEnterAccountDetailsStepMandatoryDepositEmptyAmountShouldFail() {
        Map<String, Class> expectedViolations = new HashMap<String, Class>();
        expectedViolations.put("Pattern", Pattern.class);
        expectedViolations.put("DecimalMax", DecimalMax.class);
        validateEnterAccountDetailsStepMandatoryDeposit("", expectedViolations,
                false);
    }

    @Test
    public void validateEnterAccountDetailsStepMandatoryDepositNullAmountShouldFail() {
        Map<String, Class> expectedViolations = new HashMap<String, Class>();
        expectedViolations.put("NotNull", NotNull.class);
        validateEnterAccountDetailsStepMandatoryDeposit(null, expectedViolations,
                false);
    }

    @Test
    public void validateEnterAccountDetailsStepMandatoryDepositInvalidAmountShouldFail() {
        Map<String, Class> expectedViolations = new HashMap<String, Class>();
        expectedViolations.put("Pattern", Pattern.class);
        expectedViolations.put("DecimalMax", DecimalMax.class);
        validateEnterAccountDetailsStepMandatoryDeposit("xyz", expectedViolations,
                false);
    }

    @Test
    public void validateEnterAccountDetailsStepMandatoryDepositTooHighAmountShouldFail() {
        Map<String, Class> expectedViolations = new HashMap<String, Class>();
        expectedViolations.put("DecimalMax", DecimalMax.class);
        validateEnterAccountDetailsStepMandatoryDeposit("10000000000000000000.00", expectedViolations,
                false);
    }

    @Test
    public void validateEnterAccountDetailsStepMandatoryDepositNumericAmountShouldPass() {
        validateEnterAccountDetailsStepMandatoryDeposit("123.0", null, true);
    }

    @Test
    public void validateAnswerQuestionGroupStepEmptyQuestionGroupShouldPass() {
        List<QuestionGroupDetail> questionGroups = new ArrayList<QuestionGroupDetail>();
        formBean.setQuestionGroups(questionGroups);
        formBean.validateAnswerQuestionGroupStep(validationContext);

        MessageContext messageContext = validationContext.getMessageContext();
        Message[] messages = messageContext.getAllMessages();
        Assert.assertEquals(0, messages.length);
    }

    @Test
    public void validateAnswerQuestionGroupStepMissingMandatoryResponseShouldFail() {

        List<QuestionGroupDetail> questionGroups = new ArrayList<QuestionGroupDetail>();
        formBean.setQuestionGroups(questionGroups);

        doThrow(validationException).when(questionnaireServiceFacade)
                .validateResponses(formBean.getQuestionGroups());

        formBean.validateAnswerQuestionGroupStep(validationContext);

        MessageContext messageContext = validationContext.getMessageContext();
        Message[] messages = messageContext.getAllMessages();
        Assert.assertEquals(1, messages.length);
    }

    private void validateEnterAccountDetailsStepMandatoryDeposit(
            String amount, @SuppressWarnings("rawtypes") Map<String, Class> expectedViolations, boolean expectingPass) {
        setDepositType(formBean, CreateSavingsAccountFormBean.MANDATORY_DEPOSIT);
        formBean.setMandatoryDepositAmount(amount);
        formBean.validateEnterAccountDetailsStep(validationContext);

        MessageContext messageContext = validationContext.getMessageContext();
        Message[] messages = messageContext.getAllMessages();

        if (expectingPass) {
            Assert.assertEquals(0, messages.length);
            return;
        } else {
            Assert.assertEquals(expectedViolations.size(), messages.length);
            for(Message message : messages) {
                Assert.assertEquals("mandatoryDepositAmount", message.getSource());
                String reason = message.getText().substring(0, message.getText().indexOf("."));
                Assert.assertTrue(expectedViolations.containsKey(reason));
                verifyErrorMessage(expectedViolations.get(reason), message);
            }
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

    private void verifyErrorMessage(@SuppressWarnings("rawtypes") Class constraint, Message message) {
        String expected = constraint.getSimpleName() + "."
                + formBean.getClass().getSimpleName() + "."
                + message.getSource();
        Assert.assertEquals(expected, message.getText());
    }
}