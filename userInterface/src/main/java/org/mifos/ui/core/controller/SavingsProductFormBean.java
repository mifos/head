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

import java.math.BigDecimal;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @see SavingsProductFormValidator - manual validatior added to fix MIFOS-3915 (morzechowski@soldevelo.com)
 */
@SuppressWarnings("PMD")
public class SavingsProductFormBean {

    @Valid
    private GeneralProductBean generalDetails;

    // Deposits and Withdrawal Restrictions
    @NotEmpty
    private String selectedDepositType;
    private Map<String, String> depositTypeOptions;
    private Double amountForDeposit;
    private String selectedGroupSavingsApproach;
    private Map<String, String> groupSavingsApproachOptions;
    private Double maxWithdrawalAmount;

    // Interest rate details
    private boolean interestRateZero;
    @Min(value=0)
    @Max(value=100)
    @NotNull
    private BigDecimal interestRate;
    @NotEmpty
    private String selectedInterestCalculation;
    private Map<String, String> interestCaluclationOptions;
    @NotNull
    private Integer interestCalculationFrequency;
    @NotEmpty
    private String selectedFequencyPeriod;
    private Map<String, String> frequencyPeriodOptions;

    @Min(value=1)
    @NotNull
    private Integer interestPostingMonthlyFrequency;

    private String minBalanceRequiredForInterestCalculation;

    // accounting details
    @NotEmpty
    private String selectedInterestGlCode;
    @NotEmpty
    private String selectedPrincipalGlCode;

    private Map<String, String> interestGeneralLedgerOptions;
    private Map<String, String> principalGeneralLedgerOptions;

    private boolean notUpdateable = false;

    public void setDefaultInterestRateDetails(){
        if (this.getInterestRate() == null || this.getInterestRate().intValue() != 0){
            this.setInterestRate(BigDecimal.ZERO);
        }
        if (this.getSelectedInterestCalculation() == null || this.getSelectedInterestCalculation().isEmpty() ){
            this.setSelectedInterestCalculation("1"); //minimum balance
        }
        
        if (this.getSelectedFequencyPeriod() == null || this.getSelectedFequencyPeriod().isEmpty()){
            this.setSelectedFequencyPeriod("3");  //days
        }
        
        if (this.getInterestCalculationFrequency() == null || this.getInterestCalculationFrequency() == 0){
            this.setInterestCalculationFrequency(1);
        }
        
        if (this.getInterestPostingMonthlyFrequency() == null || this.getInterestPostingMonthlyFrequency() == 0){
            this.setInterestPostingMonthlyFrequency(1);
        }
    }
    
    public boolean isGroupSavingAccount() {
        return ("2").equals(this.generalDetails.getSelectedApplicableFor());
    }

    public boolean isMandatory() {
        return ("1").equals(this.selectedDepositType);
    }

    public Map<String, String> getInterestGeneralLedgerOptions() {
        return this.interestGeneralLedgerOptions;
    }

    public void setInterestGeneralLedgerOptions(Map<String, String> interestGeneralLedgerOptions) {
        this.interestGeneralLedgerOptions = interestGeneralLedgerOptions;
    }

    public Map<String, String> getPrincipalGeneralLedgerOptions() {
        return this.principalGeneralLedgerOptions;
    }

    public void setPrincipalGeneralLedgerOptions(Map<String, String> principalGeneralLedgerOptions) {
        this.principalGeneralLedgerOptions = principalGeneralLedgerOptions;
    }

    public String getSelectedInterestGlCode() {
        return this.selectedInterestGlCode;
    }

    public void setSelectedInterestGlCode(String selectedInterestGlCode) {
        this.selectedInterestGlCode = selectedInterestGlCode;
    }

    public String getSelectedPrincipalGlCode() {
        return this.selectedPrincipalGlCode;
    }

    public void setSelectedPrincipalGlCode(String selectedPrincipalGlCode) {
        this.selectedPrincipalGlCode = selectedPrincipalGlCode;
    }

    public String getSelectedDepositType() {
        return this.selectedDepositType;
    }

    public void setSelectedDepositType(String selectedDepositType) {
        this.selectedDepositType = selectedDepositType;
    }

    public Map<String, String> getDepositTypeOptions() {
        return this.depositTypeOptions;
    }

    public void setDepositTypeOptions(Map<String, String> depositTypeOptions) {
        this.depositTypeOptions = depositTypeOptions;
    }

    public Double getAmountForDeposit() {
        return this.amountForDeposit;
    }

    public void setAmountForDeposit(Double amountForDeposit) {
        this.amountForDeposit = amountForDeposit;
    }

    public String getSelectedGroupSavingsApproach() {
        return this.selectedGroupSavingsApproach;
    }

    public void setSelectedGroupSavingsApproach(String selectedGroupSavingsApproach) {
        this.selectedGroupSavingsApproach = selectedGroupSavingsApproach;
    }

    public Map<String, String> getGroupSavingsApproachOptions() {
        return this.groupSavingsApproachOptions;
    }

    public void setGroupSavingsApproachOptions(Map<String, String> groupSavingsApproachOptions) {
        this.groupSavingsApproachOptions = groupSavingsApproachOptions;
    }

    public Double getMaxWithdrawalAmount() {
        return this.maxWithdrawalAmount;
    }

    public void setMaxWithdrawalAmount(Double maxWithdrawalAmount) {
        this.maxWithdrawalAmount = maxWithdrawalAmount;
    }

    public BigDecimal getInterestRate() {
        return this.interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public String getSelectedInterestCalculation() {
        return this.selectedInterestCalculation;
    }

    public void setSelectedInterestCalculation(String selectedInterestCalculation) {
        this.selectedInterestCalculation = selectedInterestCalculation;
    }

    public Map<String, String> getInterestCaluclationOptions() {
        return this.interestCaluclationOptions;
    }

    public void setInterestCaluclationOptions(Map<String, String> interestCaluclationOptions) {
        this.interestCaluclationOptions = interestCaluclationOptions;
    }

    public Integer getInterestCalculationFrequency() {
        return this.interestCalculationFrequency;
    }

    public void setInterestCalculationFrequency(Integer interestCalculationFrequency) {
        this.interestCalculationFrequency = interestCalculationFrequency;
    }

    public String getSelectedFequencyPeriod() {
        return this.selectedFequencyPeriod;
    }

    public void setSelectedFequencyPeriod(String selectedFequencyPeriod) {
        this.selectedFequencyPeriod = selectedFequencyPeriod;
    }

    public Map<String, String> getFrequencyPeriodOptions() {
        return this.frequencyPeriodOptions;
    }

    public void setFrequencyPeriodOptions(Map<String, String> frequencyPeriodOptions) {
        this.frequencyPeriodOptions = frequencyPeriodOptions;
    }

    public Integer getInterestPostingMonthlyFrequency() {
        return this.interestPostingMonthlyFrequency;
    }

    public void setInterestPostingMonthlyFrequency(Integer interestPostingMonthlyFrequency) {
        this.interestPostingMonthlyFrequency = interestPostingMonthlyFrequency;
    }

    public String getMinBalanceRequiredForInterestCalculation() {
        return this.minBalanceRequiredForInterestCalculation;
    }

    public void setMinBalanceRequiredForInterestCalculation(String minBalanceRequiredForInterestCalculation) {
        this.minBalanceRequiredForInterestCalculation = minBalanceRequiredForInterestCalculation;
    }

    public GeneralProductBean getGeneralDetails() {
        return this.generalDetails;
    }

    public void setGeneralDetails(GeneralProductBean generalDetails) {
        this.generalDetails = generalDetails;
    }

    public boolean isNotUpdateable() {
        return this.notUpdateable;
    }

    public void setNotUpdateable(boolean notUpdateable) {
        this.notUpdateable = notUpdateable;
    }

    public boolean isInterestRateZero() {
        return interestRateZero;
    }

    public void setInterestRateZero(boolean interestRateZero) {
        this.interestRateZero = interestRateZero;
    }
}