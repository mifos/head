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

import org.hibernate.validator.constraints.NotEmpty;
import org.mifos.config.servicefacade.ConfigurationServiceFacade;
import org.mifos.config.servicefacade.dto.AccountingConfigurationDto;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.dto.screen.SavingsProductReferenceDto;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.validation.MifosBeanValidator;
import org.mifos.platform.validations.ValidationException;
import org.mifos.ui.core.controller.util.ValidationExceptionMessageExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * An object to hold information collected in create savings account process.
 */
@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID"}, justification="should disable at filter level and also for pmd - not important for us")
public class CreateSavingsAccountFormBean implements Serializable {

    // FIXME - keithw - there is no need to differentiate between madatory and voluntary deposit amount
    // 		 - there is only one field on the form - deposit amount and at creation we use the 
    //       - product definition to determine if it is mandatory or voluntary?
    static final int MANDATORY_DEPOSIT = 1;
    static final int VOLUNTARY_DEPOSIT = 2;

    private CustomerDto customer;

    @NotEmpty(groups = CustomerSearchStep.class)
    private String searchString;

    @NotNull(groups = { MandatorySavings.class })
    @Pattern(regexp = "^[0-9]+(\\.[0-9]+)?$?", groups = { MandatorySavings.class })
    private String mandatoryDepositAmount;

    private SavingsProductReferenceDto product;

    @NotNull(groups = { SelectProductStep.class })
    private Integer productId;

    private List<QuestionGroupDetail> questionGroups;

    private List<PrdOfferingDto> productOfferings;

    private Map<String, String> productOfferingOptions;

    private Map<String, String> savingsTypes;

    private Map<String, String> recurrenceTypes;

    private Map<String, String> recurrenceFrequencies;

    @Autowired
    private transient MifosBeanValidator validator;

    @Autowired
    private transient QuestionnaireServiceFacade questionnaireServiceFacade;

    private transient ConfigurationServiceFacade configurationServiceFacade;

    public void setValidator(MifosBeanValidator validator) {
        this.validator = validator;
    }

    public void setQuestionnaireServiceFascade(QuestionnaireServiceFacade questionnaireServiceFacade) {
        this.questionnaireServiceFacade = questionnaireServiceFacade;
    }

    @Autowired
    public void setConfigurationServiceFacade(ConfigurationServiceFacade configurationServiceFacade) {
        this.configurationServiceFacade = configurationServiceFacade;
    }

    public void setCustomer(CustomerDto customer) {
        this.customer = customer;
    }

    public CustomerDto getCustomer() {
        return customer;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getSearchString() {
        return searchString;
    }

    /**
     * Sets the savings product associated with the (new) savings account. The
     * product can be referenced in form validation.
     * 
     * @param product
     *            An instance of {@link SavingsProductReferenceDto}
     */
    public void setProduct(SavingsProductReferenceDto product) {
        this.product = product;
    }

    public SavingsProductReferenceDto getProduct() {
        return product;
    }

    public void setProductOfferings(List<PrdOfferingDto> productOfferings) {
        this.productOfferings = productOfferings;
    }

    public List<PrdOfferingDto> getProductOfferings() {
        return productOfferings;
    }

    public void setProductOfferingOptions(
            Map<String, String> productOfferingOptions) {
        this.productOfferingOptions = productOfferingOptions;
    }

    public Map<String, String> getProductOfferingOptions() {
        return productOfferingOptions;
    }

    /**
     * Validation method that Spring webflow calls on state transition out of
     * customerSearchStep.
     */
    public void validateCustomerSearchStep(ValidationContext context) {
        MessageContext messages = context.getMessageContext();
        validator.validate(this, messages, CustomerSearchStep.class);
    }

    /**
     * Validation method that Spring webflow calls on state transition out of
     * selectCustomerStep.
     */
    public void validateSelectCustomerStep(ValidationContext context) {
        MessageContext messages = context.getMessageContext();
        validator.validate(this, messages, CustomerSearchStep.class);
    }

    /**
     * Validation method that Spring webflow calls on state transition out of
     * selectProductOfferingStep.
     */
    public void validateSelectProductOfferingStep(ValidationContext context) {
        MessageContext messages = context.getMessageContext();
        validator.validate(this, messages, SelectProductStep.class);
    }

    public void validateEnterAccountDetailsStep(ValidationContext context) {
        validateAccountDetails(context);
    }

    public void validateEditAccountDetailsStep(ValidationContext context) {
        validateAccountDetails(context);
    }

    public void validateAnswerQuestionGroupStep(ValidationContext context) {
        validateQuestionGroupAnswers(context);
    }

    public void validateEditQuestionGroupStep(ValidationContext context) {
        validateQuestionGroupAnswers(context);
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setSavingsTypes(Map<String, String> savingsTypes) {
        this.savingsTypes = savingsTypes;
    }

    public Map<String, String> getSavingsTypes() {
        return savingsTypes;
    }

    public void setRecurrenceTypes(Map<String, String> recurrenceTypes) {
        this.recurrenceTypes = recurrenceTypes;
    }

    public Map<String, String> getRecurrenceTypes() {
        return recurrenceTypes;
    }

    public void setRecurrenceFrequencies(
            Map<String, String> recurrenceFrequencies) {
        this.recurrenceFrequencies = recurrenceFrequencies;
    }

    public Map<String, String> getRecurrenceFrequencies() {
        return recurrenceFrequencies;
    }

    public void setMandatoryDepositAmount(String mandatoryDepositAmount) {
        this.mandatoryDepositAmount = mandatoryDepositAmount;
    }

    public String getMandatoryDepositAmount() {
        return mandatoryDepositAmount;
    }

    public void setQuestionGroups(List<QuestionGroupDetail> questionGroups) {
        this.questionGroups = questionGroups;
    }

    public List<QuestionGroupDetail> getQuestionGroups() {
        return questionGroups;
    }

    private void validateAccountDetails(ValidationContext context) {
        MessageContext messages = context.getMessageContext();
        validator.validate(this, messages, MandatorySavings.class);
        validateMandatoryDepositAmountDigits(context);
    }

    private void validateQuestionGroupAnswers(ValidationContext context) {
        MessageContext messages = context.getMessageContext();
        try {
            questionnaireServiceFacade.validateResponses(this
                    .getQuestionGroups());
        } catch (ValidationException e) {
            ValidationExceptionMessageExtractor extractor = new ValidationExceptionMessageExtractor();
            extractor.extract(messages, e);
        }
    }

    private void validateMandatoryDepositAmountDigits(ValidationContext context) {
        if (mandatoryDepositAmount == null) {
            return;
        }
        AccountingConfigurationDto accountingConfiguration = configurationServiceFacade.getAccountingConfiguration();
        BigDecimal decimalAmount;
        try {
            decimalAmount = new BigDecimal(mandatoryDepositAmount);
        } catch (NumberFormatException e) {
            return;
        }
        if ((decimalAmount.precision() - decimalAmount.scale()) > accountingConfiguration.getDigitsBeforeDecimal()) {
            context.getMessageContext().addMessage(new MessageBuilder()
                .error()
                .source("mandatoryDepositAmount")
                .code("DigitsBefore.CreateSavingsAccountFormBean.mandatoryDepositAmount")
                .arg(accountingConfiguration.getDigitsBeforeDecimal())
                .build()
            );
        }
        if (decimalAmount.scale() > accountingConfiguration.getDigitsAfterDecimal()) {
            context.getMessageContext().addMessage(new MessageBuilder()
                .error()
                .source("mandatoryDepositAmount")
                .code("DigitsAfter.CreateSavingsAccountFormBean.mandatoryDepositAmount")
                .arg(accountingConfiguration.getDigitsAfterDecimal())
                .build()
            );
        }
    }

}

/**
 * Marker interfaces for JSR-303 validation groups.
 */
interface CustomerSearchStep {
}

interface SelectProductStep {
}

interface EnterAccountInfoStep {
}

interface MandatorySavings {
}
