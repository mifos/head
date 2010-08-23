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

import java.math.BigDecimal;

@SuppressWarnings("PMD")
public class SavingsProductRequest {

    private final ProductDetailsDto productDetails;
    private final Integer depositType;
    private final Integer interestCalculationType;
    private final Integer groupMandatorySavingsType;
    private final Double amountForDeposit;
    private final Double maxWithdrawal;
    private final boolean groupMandatorySavingsAccount;
    private final BigDecimal interestRate;
    private final Integer interestCalculationFrequency;
    private final Integer interestCalculationFrequencyPeriod;
    private final Integer interestPostingMonthlyFrequency;
    private final BigDecimal minBalanceForInterestCalculation;
    private final Integer depositGlCode;
    private final Integer interestGlCode;

    public SavingsProductRequest(ProductDetailsDto productDetailsDto, boolean groupMandatorySavingsAccount,
            Integer depositType, Integer groupMandatorySavingsType, Double amountForDeposit, Double maxWithdrawal,
            BigDecimal interestRate, Integer interestCalculationType, Integer interestCalculationFrequency,
            Integer interestCalculationFrequencyPeriod, Integer interestPostingMonthlyFrequency,
            BigDecimal minBalanceForInterestCalculation, Integer depositGlCode, Integer interestGlCode) {
        this.productDetails = productDetailsDto;
        this.groupMandatorySavingsAccount = groupMandatorySavingsAccount;
        this.depositType = depositType;
        this.interestCalculationType = interestCalculationType;
        this.groupMandatorySavingsType = groupMandatorySavingsType;
        this.amountForDeposit = amountForDeposit;
        this.maxWithdrawal = maxWithdrawal;
        this.interestRate = interestRate;
        this.interestCalculationFrequency = interestCalculationFrequency;
        this.interestCalculationFrequencyPeriod = interestCalculationFrequencyPeriod;
        this.interestPostingMonthlyFrequency = interestPostingMonthlyFrequency;
        this.minBalanceForInterestCalculation = minBalanceForInterestCalculation;
        this.depositGlCode = depositGlCode;
        this.interestGlCode = interestGlCode;
    }

    public ProductDetailsDto getProductDetails() {
        return this.productDetails;
    }

    public Integer getDepositType() {
        return this.depositType;
    }

    public Integer getInterestCalculationType() {
        return this.interestCalculationType;
    }

    public Integer getGroupMandatorySavingsType() {
        return this.groupMandatorySavingsType;
    }

    public Double getAmountForDeposit() {
        return this.amountForDeposit;
    }

    public Double getMaxWithdrawal() {
        return this.maxWithdrawal;
    }

    public boolean isGroupMandatorySavingsAccount() {
        return this.groupMandatorySavingsAccount;
    }

    public BigDecimal getInterestRate() {
        return this.interestRate;
    }

    public Integer getInterestCalculationFrequency() {
        return this.interestCalculationFrequency;
    }

    public Integer getInterestCalculationFrequencyPeriod() {
        return this.interestCalculationFrequencyPeriod;
    }

    public Integer getInterestPostingMonthlyFrequency() {
        return this.interestPostingMonthlyFrequency;
    }

    public BigDecimal getMinBalanceForInterestCalculation() {
        return this.minBalanceForInterestCalculation;
    }

    public Integer getDepositGlCode() {
        return this.depositGlCode;
    }

    public Integer getInterestGlCode() {
        return this.interestGlCode;
    }
}