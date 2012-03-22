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

package org.mifos.dto.domain;

import java.io.Serializable;
import java.math.BigDecimal;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class SavingsProductDto implements Serializable {

    private boolean openSavingsAccountsExist = true;
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
    private String depositGlCodeValue;
    private String depositGlCodeName;
    private final Integer interestGlCode;
    private String interestGlCodeValue;
    private String interestGlCodeName;

    /**
     * minimal legal constructor for create savings products request
     */
    public SavingsProductDto(ProductDetailsDto productDetailsDto, boolean groupSavingsAccount,
            Integer depositType, Integer groupSavingsType, Double amountForDeposit, Double maxWithdrawal,
            BigDecimal interestRate, Integer interestCalculationType, Integer interestCalculationFrequency,
            Integer interestCalculationFrequencyPeriod, Integer interestPostingMonthlyFrequency,
            BigDecimal minBalanceForInterestCalculation, Integer depositGlCode, Integer interestGlCode) {
        this.productDetails = productDetailsDto;
        this.groupMandatorySavingsAccount = groupSavingsAccount;
        this.depositType = depositType;
        this.groupMandatorySavingsType = groupSavingsType;
        this.amountForDeposit = amountForDeposit;
        this.maxWithdrawal = maxWithdrawal;
        this.interestRate = interestRate;
        this.interestCalculationType = interestCalculationType;
        this.interestCalculationFrequency = interestCalculationFrequency;
        this.interestCalculationFrequencyPeriod = interestCalculationFrequencyPeriod;
        this.interestPostingMonthlyFrequency = interestPostingMonthlyFrequency;
        this.minBalanceForInterestCalculation = minBalanceForInterestCalculation;
        this.depositGlCode = depositGlCode;
        this.interestGlCode = interestGlCode;
    }

    public String getDepositGlCodeName() {
		return depositGlCodeName;
	}

	public void setDepositGlCodeName(String depositGlCodeName) {
		this.depositGlCodeName = depositGlCodeName;
	}

	public String getInterestGlCodeName() {
		return interestGlCodeName;
	}

	public void setInterestGlCodeName(String interestGlCodeName) {
		this.interestGlCodeName = interestGlCodeName;
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

    public String getDepositGlCodeValue() {
        return this.depositGlCodeValue;
    }

    public void setDepositGlCodeValue(String depositGlCodeValue) {
        this.depositGlCodeValue = depositGlCodeValue;
    }

    public String getInterestGlCodeValue() {
        return this.interestGlCodeValue;
    }

    public void setInterestGlCodeValue(String interestGlCodeValue) {
        this.interestGlCodeValue = interestGlCodeValue;
    }

    public boolean isOpenSavingsAccountsExist() {
        return this.openSavingsAccountsExist;
    }

    public void setOpenSavingsAccountsExist(boolean openSavingsAccountsExist) {
        this.openSavingsAccountsExist = openSavingsAccountsExist;
    }
}