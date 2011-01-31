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

import javax.validation.constraints.NotNull;

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

	@NotNull(groups = { MandatorySavings.class, EnterAccountInfoStep.class })
	private Double depositAmount;

	private SavingsProductReferenceDto product;

	private List<PrdOfferingDto> productOfferings;

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

	public void setDepositAmount(Double depositAmount) {
		this.depositAmount = depositAmount;
	}

	public Double getDepositAmount() {
		return depositAmount;
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

	public void validateEnterAccountDetailsStep(ValidationContext context) {
		MessageContext messages = context.getMessageContext();
		validator.validate(this, messages);

		// TODO: need to get access to domain constants from controller ...
		// public static final short SAVINGS_MANDATORY= 1;
		// public static final short SAVINGS_VOLUNTARY= 2;

		// validate recommended amount
		// final Integer depositType =
		// productReference.getSavingsProductDetails()
		// .getDepositType();
		// if (depositType == 1) { // mandatory
		// result = true;
		// } else if (depositType == 2) { // voluntary
		// result = true;
		// }
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