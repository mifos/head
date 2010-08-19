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

import java.util.Map;

public class SavingsProductFormBean {

    // general product details
    private String name;
    private String shortName;
    private String description;

    private String selectedCategory;
    private Map<String, String> categoryOptions;

    private Integer startDateDay;
    private Integer startDateMonth;
    private String startDateYear;

    private Integer endDateDay;
    private Integer endDateMonth;
    private String endDateYear;

    private String selectedApplicableFor;
    private Map<String, String> applicableForOptions;

    // Deposits and Withdrawal Restrictions
    private String selectedDepositType;
    private Map<String, String> depositTypeOptions;
    private Double amountForDeposit;
    private String selectedGroupSavingsApproach;
    private Map<String, String> groupSavingsApproachOptions;
    private Double maxWithdrawalAmount;

    // Interest rate details
    private Double interestRate;
    private String selectedInterestCalculation;
    private Map<String, String> interestCaluclationOptions;

    private Integer interestCalculationFrequency;
    private String selectedFequencyPeriod;
    private Map<String, String> frequencyPeriodOptions;

    private Integer interestPostingMonthlyFrequency;
    private Double minBalanceRequiredForInterestCalculation;

    // accounting details
    private Map<String, String> interestGeneralLedgerOptions;
    private String selectedInterestGlCode;
    private Map<String, String> principalGeneralLedgerOptions;
    private String selectedPrincipalGlCode;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSelectedCategory() {
        return this.selectedCategory;
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public Map<String, String> getCategoryOptions() {
        return this.categoryOptions;
    }

    public void setCategoryOptions(Map<String, String> categoryOptions) {
        this.categoryOptions = categoryOptions;
    }

    public String getStartDateYear() {
        return this.startDateYear;
    }

    public void setStartDateYear(String startDateYear) {
        this.startDateYear = startDateYear;
    }

    public String getEndDateYear() {
        return this.endDateYear;
    }

    public void setEndDateYear(String endDateYear) {
        this.endDateYear = endDateYear;
    }

    public String getSelectedApplicableFor() {
        return this.selectedApplicableFor;
    }

    public void setSelectedApplicableFor(String selectedApplicableFor) {
        this.selectedApplicableFor = selectedApplicableFor;
    }

    public Map<String, String> getApplicableForOptions() {
        return this.applicableForOptions;
    }

    public void setApplicableForOptions(Map<String, String> applicableForOptions) {
        this.applicableForOptions = applicableForOptions;
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

    public Integer getStartDateDay() {
        return this.startDateDay;
    }

    public void setStartDateDay(Integer startDateDay) {
        this.startDateDay = startDateDay;
    }

    public Integer getStartDateMonth() {
        return this.startDateMonth;
    }

    public void setStartDateMonth(Integer startDateMonth) {
        this.startDateMonth = startDateMonth;
    }

    public Integer getEndDateDay() {
        return this.endDateDay;
    }

    public void setEndDateDay(Integer endDateDay) {
        this.endDateDay = endDateDay;
    }

    public Integer getEndDateMonth() {
        return this.endDateMonth;
    }

    public void setEndDateMonth(Integer endDateMonth) {
        this.endDateMonth = endDateMonth;
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

    public Double getInterestRate() {
        return this.interestRate;
    }

    public void setInterestRate(Double interestRate) {
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

    public Double getMinBalanceRequiredForInterestCalculation() {
        return this.minBalanceRequiredForInterestCalculation;
    }

    public void setMinBalanceRequiredForInterestCalculation(Double minBalanceRequiredForInterestCalculation) {
        this.minBalanceRequiredForInterestCalculation = minBalanceRequiredForInterestCalculation;
    }
}