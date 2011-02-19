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

import java.io.Serializable;

import org.mifos.dto.screen.SavingsProductReferenceDto;
import org.mifos.platform.validation.MifosBeanValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;

/**
 * @deprecated by CreateSavingsAccountFormBean.
 */
@Deprecated
@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class AccountDetailFormBean implements Serializable {

	private Float depositAmount;

	private SavingsProductReferenceDto product;

	@Autowired
	private transient MifosBeanValidator validator;

	public void setValidator(MifosBeanValidator validator) {
		this.validator = validator;
	}

	public void setDepositAmount(Float depositAmount) {
		this.depositAmount = depositAmount;
	}

	public Float getDepositAmount() {
		return depositAmount;
	}

	/**
	 * Sets the savings product associated with the (new) savings account. The
	 * product can be referenced in form validation.
	 * 
	 * @param product An instance of {@link SavingsProductReferenceDto}
	 */
	public void setProduct(SavingsProductReferenceDto product) {
		this.product = product;
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
