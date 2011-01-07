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

package org.mifos.accounts.fees.servicefacade;

import java.io.Serializable;

import org.mifos.accounts.financial.servicefacade.GLCodeDto;
import org.mifos.framework.util.helpers.Money;

/** FIXME: resolve/collapse with {@link org.mifos.accounts.fees.business.FeeDto}. */
public class FeeDto implements Serializable {

    private String id;

    private String name;

    private String status;

    private String categoryType;

    private String glCode;

    private Money amount;

    private FeeFrequencyDto feeFrequency;

    private FeeStatusDto feeStatus;

    private Short changeType;

    private String feeFrequencyType;

    private GLCodeDto glCodeDto;

    private Double rate;

    private Money feeAmount;

    private boolean rateBasedFee;

    private boolean active;

    private boolean loanCategoryType;

    private boolean customerDefaultFee;

    private boolean oneTime;

    private boolean periodic;

    private boolean timeOfDisbursement;

    private FeeFormulaDto feeFormula;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setFeeStatus(FeeStatusDto feeStatus) {
        this.feeStatus = feeStatus;
    }

    public FeeStatusDto getFeeStatus() {
        return feeStatus;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public Short getChangeType() {
        return this.changeType;
    }

    public void setChangeType(Short changeType) {
        this.changeType = changeType;
    }

    public String getFeeFrequencyType() {
        return this.feeFrequencyType;
    }

    public void setFeeFrequencyType(String feeFrequencyType) {
        this.feeFrequencyType = feeFrequencyType;
    }

    public GLCodeDto getGlCodeDto() {
        return this.glCodeDto;
    }

    public void setGlCodeDto(GLCodeDto glCodeDto) {
        this.glCodeDto = glCodeDto;
    }

    public Money getFeeAmount() {
        return this.feeAmount;
    }

    public void setFeeAmount(Money feeAmount) {
        this.feeAmount = feeAmount;
    }

    public void setLoanCategoryType(boolean loanCategoryType) {
        this.loanCategoryType = loanCategoryType;
    }

    public boolean isLoanCategoryType() {
        return loanCategoryType;
    }

    public void setCustomerDefaultFee(boolean customerDefaultFee) {
        this.customerDefaultFee = customerDefaultFee;
    }

    public boolean isCustomerDefaultFee() {
        return customerDefaultFee;
    }

    public boolean isOneTime() {
        return oneTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setGlCode(String glCode) {
        this.glCode = glCode;
    }

    public String getGlCode() {
        return glCode;
    }

    public Double getRate() {
        return rate;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public Money getAmount() {
        return amount;
    }

    public void setFeeFrequency(FeeFrequencyDto feeFrequency) {
        this.feeFrequency = feeFrequency;
    }

    public FeeFrequencyDto getFeeFrequency() {
        return feeFrequency;
    }

    public void setRateBasedFee(boolean rateBasedFee) {
        this.rateBasedFee = rateBasedFee;
    }

    /**
     * Fees are either rate-based or amount-based.
     */
    public boolean isRateBasedFee() {
        return rateBasedFee;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public void setOneTime(boolean oneTime) {
        this.oneTime = oneTime;
    }

    public void setPeriodic(boolean periodic) {
        this.periodic = periodic;
    }

    public boolean isPeriodic() {
        return this.periodic;
    }

    public void setTimeOfDisbursement(boolean timeOfDisbursement) {
        this.timeOfDisbursement = timeOfDisbursement;
    }

    public boolean isTimeOfDisbursement() {
        return this.timeOfDisbursement;
    }

    public void setFeeFormula(FeeFormulaDto feeFormulaDto) {
        this.feeFormula = feeFormulaDto;
    }

    public FeeFormulaDto getFeeFormula() {
        return feeFormula;
    }
}
