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

package org.mifos.dto.domain;

public class FeeCreateDto {

    private Short categoryType;
    private Short feeFrequencyType;
    private Short glCode;
    private Short feePaymentType;
    private Short feeFormula;
    private String feeName;
    private boolean rateFee;
    private boolean customerDefaultFee;
    private Short currencyId;
    private Double rate;
    private String amount;
    private Short feeRecurrenceType;
    private Short monthRecurAfter;
    private Short weekRecurAfter;

    @SuppressWarnings("PMD")
    public FeeCreateDto(
            Short feeCategory, Short feeFrequencyType, Short glCode,
            Short feePayment, Short feeFormula, String feeName,
            boolean isRateFee, boolean isCustomerDefaultFee, Double rate,
            Short currencyId, String amount, Short feeRecurrenceType,
            Short monthRecurAfter, Short weekRecurAfter) {

                this.categoryType = feeCategory;
                this.feeFrequencyType = feeFrequencyType;
                this.glCode = glCode;
                this.feePaymentType = feePayment;
                this.feeFormula = feeFormula;
                this.feeName = feeName;
                this.rateFee = isRateFee;
                this.customerDefaultFee = isCustomerDefaultFee;
                this.rate = rate;
                this.currencyId = currencyId;
                this.amount = amount;
                this.feeRecurrenceType = feeRecurrenceType;
                this.monthRecurAfter = monthRecurAfter;
                this.weekRecurAfter = weekRecurAfter;

    }

    public Short getCategoryType() {
        return this.categoryType;
    }

    public void setCategoryType(Short categoryType) {
        this.categoryType = categoryType;
    }

    public Short getFeeFrequencyType() {
        return this.feeFrequencyType;
    }

    public void setFeeFrequencyType(Short feeFrequencyType) {
        this.feeFrequencyType = feeFrequencyType;
    }

    public Short getGlCode() {
        return this.glCode;
    }

    public void setGlCode(Short glCode) {
        this.glCode = glCode;
    }

    public Short getFeePaymentType() {
        return this.feePaymentType;
    }

    public void setFeePaymentType(Short feePaymentType) {
        this.feePaymentType = feePaymentType;
    }

    public Short getFeeFormula() {
        return this.feeFormula;
    }

    public void setFeeFormula(Short feeFormula) {
        this.feeFormula = feeFormula;
    }

    public String getFeeName() {
        return this.feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }

    public boolean isRateFee() {
        return this.rateFee;
    }

    public void setRateFee(boolean rateFee) {
        this.rateFee = rateFee;
    }

    public boolean isCustomerDefaultFee() {
        return this.customerDefaultFee;
    }

    public void setCustomerDefaultFee(boolean customerDefaultFee) {
        this.customerDefaultFee = customerDefaultFee;
    }

    public Short getCurrencyId() {
        return this.currencyId;
    }

    public void setCurrencyId(Short currencyId) {
        this.currencyId = currencyId;
    }

    public Double getRate() {
        return this.rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getAmount() {
        return this.amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Short getFeeRecurrenceType() {
        return this.feeRecurrenceType;
    }

    public void setFeeRecurrenceType(Short feeRecurrenceType) {
        this.feeRecurrenceType = feeRecurrenceType;
    }

    public Short getMonthRecurAfter() {
        return this.monthRecurAfter;
    }

    public void setMonthRecurAfter(Short monthRecurAfter) {
        this.monthRecurAfter = monthRecurAfter;
    }

    public Short getWeekRecurAfter() {
        return this.weekRecurAfter;
    }

    public void setWeekRecurAfter(Short weekRecurAfter) {
        this.weekRecurAfter = weekRecurAfter;
    }
}
