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

import java.io.Serializable;

import org.mifos.platform.validation.MifosBeanValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class LoanAccountFormBean implements Serializable {

    @Autowired
    private transient MifosBeanValidator validator;

    private Integer productId;
    
    private Number amount;
    private Number interestRate;
    private Number numberOfInstallments;
    private Number disbursalDateDay;
    private Number disbursalDateMonth;
    private Number disbursalDateYear;
    
    private Number graceDuration = Integer.valueOf(0);
    
    private Integer fundId;
    private Integer loanPurposeId;
    private Integer collateralTypeId;
    private String collateralNotes;
    private String externalId;
    
    private String[] selectedFeeId;
    private String[] selectedFeeAmount;
    
    
    public String[] getSelectedFeeId() {
		return selectedFeeId;
	}

	public void setSelectedFeeId(String[] selectedFeeId) {
		this.selectedFeeId = selectedFeeId;
	}

	public String[] getSelectedFeeAmount() {
		return selectedFeeAmount;
	}

	public void setSelectedFeeAmount(String[] selectedFeeAmount) {
		this.selectedFeeAmount = selectedFeeAmount;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getCollateralNotes() {
		return collateralNotes;
	}

	public void setCollateralNotes(String collateralNotes) {
		this.collateralNotes = collateralNotes;
	}

	public Integer getLoanPurposeId() {
		return loanPurposeId;
	}

	public void setLoanPurposeId(Integer loanPurposeId) {
		this.loanPurposeId = loanPurposeId;
	}

	public Integer getCollateralTypeId() {
		return collateralTypeId;
	}

	public void setCollateralTypeId(Integer collateralTypeId) {
		this.collateralTypeId = collateralTypeId;
	}

	public Integer getFundId() {
		return fundId;
	}

	public void setFundId(Integer fundId) {
		this.fundId = fundId;
	}

	public Number getGraceDuration() {
		return graceDuration;
	}

	public void setGraceDuration(Number graceDuration) {
		this.graceDuration = graceDuration;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Number getAmount() {
		return amount;
	}

	public void setAmount(Number amount) {
		this.amount = amount;
	}

	public Number getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(Number interestRate) {
		this.interestRate = interestRate;
	}

	public Number getNumberOfInstallments() {
		return numberOfInstallments;
	}

	public void setNumberOfInstallments(Number numberOfInstallments) {
		this.numberOfInstallments = numberOfInstallments;
	}

	public Number getDisbursalDateDay() {
		return disbursalDateDay;
	}

	public void setDisbursalDateDay(Number disbursalDateDay) {
		this.disbursalDateDay = disbursalDateDay;
	}

	public Number getDisbursalDateMonth() {
		return disbursalDateMonth;
	}

	public void setDisbursalDateMonth(Number disbursalDateMonth) {
		this.disbursalDateMonth = disbursalDateMonth;
	}

	public Number getDisbursalDateYear() {
		return disbursalDateYear;
	}

	public void setDisbursalDateYear(Number disbursalDateYear) {
		this.disbursalDateYear = disbursalDateYear;
	}

	public void validateEnterAccountDetailsStep(ValidationContext context) {
        MessageContext messages = context.getMessageContext();
        validator.validate(this, messages);
    }
}