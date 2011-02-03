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
import org.mifos.platform.validation.MifosBeanValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;

/**
 * An object to hold information collected in create savings account process.
 */
public class CreateSavingsAccountFormBean implements Serializable {

    private CustomerDto customer;

    @NotEmpty(groups = CustomerSearchStep.class)
    private String searchString;

    @Pattern(regexp = "^[0-9]+(\\.[0-9]{1,2})?$?", groups = { MandatorySavings.class })
    private String mandatoryDepositAmount;

    @Pattern(regexp = "^[0-9]+(\\.[0-9]{1,2})?$", groups = { VoluntarySavings.class })
    private String voluntaryDepositAmount;

    private SavingsProductReferenceDto product;

    @NotNull(groups = { SelectProductStep.class })
    private Integer productId;

    private List<PrdOfferingDto> productOfferings;

    private Map<String, String> productOfferingOptions;

    private Map<String, String> savingsTypes;

    private Map<String, String> recurrenceTypes;

    private Map<String, String> recurrenceFrequencies;

    @Autowired
    private transient MifosBeanValidator validator;

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
        // TODO: need to get access to domain constants from controller ...
        // public static final short SAVINGS_MANDATORY= 1;
        // public static final short SAVINGS_VOLUNTARY= 2;
        MessageContext messages = context.getMessageContext();
        Class[] validationGroups = {};
        if (this.product.getSavingsProductDetails().getDepositType() == 1) {
            Class[] groups = { MandatorySavings.class };
            validationGroups = groups;
        } else if (this.product.getSavingsProductDetails().getDepositType() == 2
                && !"".equals(this.voluntaryDepositAmount)) {
            Class[] groups = { VoluntarySavings.class };
            validationGroups = groups;
        }
        validator.validate(this, messages, validationGroups);
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