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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.dto.screen.SavingsProductReferenceDto;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.validation.MifosBeanValidator;
import org.mifos.platform.validations.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;

/**
 * An object to hold information collected in create savings account process.
 */
public class CreateSavingsAccountFormBean implements Serializable {

    // FIXME code smell! copied from SavingsType. SavingsType should be made
    // publicly available
    static final int MANDATORY_DEPOSIT = 1;
    static final int VOLUNTARY_DEPOSIT = 2;

    private CustomerDto customer;

    @NotEmpty(groups = CustomerSearchStep.class)
    private String searchString;

    // TODO grab format in mifos code base. LocalizationConverter.
    // parseDoubleForMoney
    @NotNull(groups = { MandatorySavings.class })
    @Pattern(regexp = "^[0-9]+(\\.[0-9]{1,2})?$?", groups = { MandatorySavings.class })
    private String mandatoryDepositAmount;

    @Pattern(regexp = "^[0-9]+(\\.[0-9]{1,2})?$", groups = { VoluntarySavings.class })
    private String voluntaryDepositAmount;

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

    public void setValidator(MifosBeanValidator validator) {
        this.validator = validator;
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
        MessageContext messages = context.getMessageContext();
        Class[] validationGroups = {};
        if (this.product.getSavingsProductDetails().getDepositType() == MANDATORY_DEPOSIT) {
            Class[] groups = { MandatorySavings.class };
            validationGroups = groups;
        } else if (this.product.getSavingsProductDetails().getDepositType() == VOLUNTARY_DEPOSIT
                && !"".equals(this.voluntaryDepositAmount)) {
            Class[] groups = { VoluntarySavings.class };
            validationGroups = groups;
        }
        validator.validate(this, messages, validationGroups);
    }

    public void validateAnswerQuestionGroupStep(ValidationContext context) {
        MessageContext messages = context.getMessageContext();
        try {
            questionnaireServiceFacade.validateResponses(this
                    .getQuestionGroups());
        } catch (ValidationException e) {
            if (e.hasChildExceptions()) {
                List<ValidationException> children = e.getChildExceptions();
                for (ValidationException child : children) {
                    messages.addMessage(new MessageBuilder().error()
                            .source(child.getIdentifier()).code(child.getKey())
                            .arg(child.getIdentifier()).build());
                }
            }
        }
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

    public void setVoluntaryDepositAmount(String voluntaryDepositAmount) {
        this.voluntaryDepositAmount = voluntaryDepositAmount;
    }

    public String getVoluntaryDepositAmount() {
        return voluntaryDepositAmount;
    }

    public void setQuestionGroups(List<QuestionGroupDetail> questionGroups) {
        this.questionGroups = questionGroups;
    }

    public List<QuestionGroupDetail> getQuestionGroups() {
        return questionGroups;
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

interface VoluntarySavings {
}
