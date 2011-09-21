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

package org.mifos.clientportfolio.loan.ui;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.clientportfolio.newloan.applicationservice.LoanDisbursementDateValidationServiceFacade;
import org.mifos.dto.domain.FeeDto;
import org.mifos.platform.validation.MifosBeanValidator;
import org.mifos.platform.validations.Errors;
import org.mifos.ui.validation.StubValidationContext;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LoanAccountFormBeanTest {

    private LoanAccountFormBean loanAccountFormBean;
    
    private ValidationContext context;
    
    @Mock private LoanDisbursementDateValidationServiceFacade loanDisbursementDateValidationServiceFacade; 
    
    @Before
    public void setUp() {
        MifosBeanValidator validator = new MifosBeanValidator();
        LocalValidatorFactoryBean targetValidator = new LocalValidatorFactoryBean();
        targetValidator.afterPropertiesSet();
        validator.setTargetValidator(targetValidator);

        loanAccountFormBean = new LoanAccountFormBean();
        Integer productId = Integer.valueOf(1);
        Integer customerId = Integer.valueOf(1);
        loanAccountFormBean.setProductId(productId);
        loanAccountFormBean.setCustomerId(customerId);
        
        loanAccountFormBean.setPurposeOfLoanMandatory(false);
        loanAccountFormBean.setSourceOfFundsMandatory(false);
        loanAccountFormBean.setExternalIdMandatory(false);
        loanAccountFormBean.setCollateralTypeAndNotesHidden(false);
        
        loanAccountFormBean.setAmount(Double.valueOf("1000.0"));
        loanAccountFormBean.setMinAllowedAmount(Integer.valueOf(400));
        loanAccountFormBean.setMaxAllowedAmount(Integer.valueOf(20000));
        
        loanAccountFormBean.setInterestRate(Double.valueOf(10.0));
        loanAccountFormBean.setMinAllowedInterestRate(Double.valueOf(1.0));
        loanAccountFormBean.setMaxAllowedInterestRate(Double.valueOf(20.0));
        loanAccountFormBean.setDigitsBeforeDecimalForInterest(10);
        loanAccountFormBean.setDigitsAfterDecimalForInterest(5);
        loanAccountFormBean.setDigitsBeforeDecimalForMonetaryAmounts(14);
        loanAccountFormBean.setDigitsAfterDecimalForMonetaryAmounts(1);

        loanAccountFormBean.setAdditionalFees(createAdditionalFeesMocks());
        
        loanAccountFormBean.setDisbursementDateDD(24);
        loanAccountFormBean.setDisbursementDateMM(02);
        loanAccountFormBean.setDisbursementDateYY(2011);
        
        loanAccountFormBean.setNumberOfInstallments(12);
        loanAccountFormBean.setMinNumberOfInstallments(1);
        loanAccountFormBean.setMaxNumberOfInstallments(12);
        
        loanAccountFormBean.setValidator(validator);
        loanAccountFormBean.setLoanDisbursementDateValidationServiceFacade(loanDisbursementDateValidationServiceFacade);
        
        context = new StubValidationContext();
        
        when(loanDisbursementDateValidationServiceFacade.validateLoanDisbursementDate((LocalDate)anyObject(), anyInt(), anyInt())).thenReturn(new Errors());
    }
    
    @Test
    public void shouldContainValidationMessageOnAmountFieldWhenAmountViolatesAllowedRange() {

        // setup
        loanAccountFormBean.setAmount(Double.valueOf("0.0"));
        
        // exercise test
        loanAccountFormBean.validateEnterAccountDetailsStep(context);
        
        // verification
        MessageContext messageContext = context.getMessageContext();
        Message[] messages = messageContext.getAllMessages();

        assertThat(messages.length, is(1));
        Message message = messages[0];
        assertThat(message.getSource().toString(), is("amount"));
        assertThat(message.getText().toString(), containsString("loanAccountFormBean.Amount.invalid"));
    }
    
    @Test
    public void shouldContainValidationMessageOnInterestRateFieldWhenInterestRateViolatesAllowedRange() {

        // setup
        loanAccountFormBean.setInterestRate(200);
        
        // exercise test
        loanAccountFormBean.validateEnterAccountDetailsStep(context);
        
        // verification
        MessageContext messageContext = context.getMessageContext();
        Message[] messages = messageContext.getAllMessages();

        assertThat(messages.length, is(1));
        Message message = messages[0];
        assertThat(message.getSource().toString(), is("interestRate"));
        assertThat(message.getText().toString(), containsString("loanAccountFormBean.InterestRate.invalid"));
    }
    
    @Test
    public void shouldContainValidationMessageOnNumberOfInstallmentsFieldWhenNumberOfInstallmentsViolatesAllowedRange() {

        // setup
        loanAccountFormBean.setNumberOfInstallments(100);
        
        // exercise test
        loanAccountFormBean.validateEnterAccountDetailsStep(context);
        
        // verification
        MessageContext messageContext = context.getMessageContext();
        Message[] messages = messageContext.getAllMessages();

        assertThat(messages.length, is(1));
        Message message = messages[0];
        assertThat(message.getSource().toString(), is("numberOfInstallments"));
        assertThat(message.getText().toString(), containsString("loanAccountFormBean.NumberOfInstallments.invalid"));
    }
    
    @Test
    public void shouldContainValidationMessageOnDisbursementDateDayFieldWhenDateIsInvalid() {

        // setup
        loanAccountFormBean.setDisbursementDateDD(32);
        loanAccountFormBean.setDisbursementDateMM(1);
        loanAccountFormBean.setDisbursementDateYY(2011);
        
        // exercise test
        loanAccountFormBean.validateEnterAccountDetailsStep(context);
        
        // verification
        MessageContext messageContext = context.getMessageContext();
        Message[] messages = messageContext.getAllMessages();

        assertThat(messages.length, is(1));
        Message message = messages[0];
        assertThat(message.getSource().toString(), is("disbursementDateDD"));
        assertThat(message.getText().toString(), containsString("loanAccountFormBean.DisbursalDate.invalid"));
    }
    
    @Test
    public void shouldContainValidationMessageOnSourceOfFundsFieldWhenFieldIsMandatoryAndNotSelected() {

        // setup
        loanAccountFormBean.setSourceOfFundsMandatory(true);
        loanAccountFormBean.setFundId(null);
        
        // exercise test
        loanAccountFormBean.validateEnterAccountDetailsStep(context);
        
        // verification
        MessageContext messageContext = context.getMessageContext();
        Message[] messages = messageContext.getAllMessages();

        assertThat(messages.length, is(1));
        Message message = messages[0];
        assertThat(message.getSource().toString(), is("fundId"));
        assertThat(message.getText().toString(), containsString("loanAccountFormBean.SourceOfFunds.invalid"));
    }
    
    @Test
    public void shouldContainValidationMessageOnLoanPurposeFieldWhenFieldIsMandatoryAndNotSelected() {

        // setup
        loanAccountFormBean.setPurposeOfLoanMandatory(true);
        loanAccountFormBean.setLoanPurposeId(null);
        
        // exercise test
        loanAccountFormBean.validateEnterAccountDetailsStep(context);
        
        // verification
        MessageContext messageContext = context.getMessageContext();
        Message[] messages = messageContext.getAllMessages();

        assertThat(messages.length, is(1));
        Message message = messages[0];
        assertThat(message.getSource().toString(), is("loanPurposeId"));
        assertThat(message.getText().toString(), containsString("loanAccountFormBean.PurposeOfLoan.invalid"));
    }

    @Test
    public void shouldContainValidationMessageOnAdditionalInterestFeeWhenDigitsBeforeAreOutOfRange() {
        // setup
        loanAccountFormBean.setSelectedFeeId(new Number[]{ 1 });
        loanAccountFormBean.setSelectedFeeAmount(new Number [] { 10000000000L });

        // exercise test
        loanAccountFormBean.validateEnterAccountDetailsStep(context);

        // verification
        Message[] messages = context.getMessageContext().getAllMessages();

        assertThat(messages.length, is(1));
        Message message = messages[0];
        assertThat(message.getSource().toString(), is("selectedFeeId"));
        assertThat(message.getText().toString(), containsString("loanAccountFormBean.additionalfees.amountOrRate.digits.before.decimal.invalid"));
    }

    @Test
    public void shouldContainValidationMessageOnAdditionalInterestFeeWhenDigitsAfterSeparatorAreOutOfRange() {
        // setup
        loanAccountFormBean.setSelectedFeeId(new Number[]{ 1 });
        loanAccountFormBean.setSelectedFeeAmount(new Number [] { 10.000001 });

        // exercise test
        loanAccountFormBean.validateEnterAccountDetailsStep(context);

        // verification
        Message[] messages = context.getMessageContext().getAllMessages();

        assertThat(messages.length, is(1));
        Message message = messages[0];
        assertThat(message.getSource().toString(), is("selectedFeeId"));
        assertThat(message.getText().toString(), containsString("loanAccountFormBean.additionalfees.amountOrRate.digits.after.decimal.invalid"));
    }

    private List<FeeDto> createAdditionalFeesMocks() {
        FeeDto interestFee = mock(FeeDto.class);
        when(interestFee.getId()).thenReturn("1");
        when(interestFee.isRateBasedFee()).thenReturn(true);
        FeeDto monetaryFee = mock(FeeDto.class);
        when(monetaryFee.getId()).thenReturn("2");
        when(monetaryFee.isRateBasedFee()).thenReturn(false);
        List<FeeDto> additionalFees = new ArrayList<FeeDto>();
        additionalFees.add(interestFee);
        additionalFees.add(monetaryFee);
        return additionalFees;
    }
}
