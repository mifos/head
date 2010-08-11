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

public class LoanProductFormBean {

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

    private boolean includeInLoanCycleCounter;

    private String loanAmountCalculationType;
    private Map<String, String> loanAmountCalculationTypeOptions;

    // same for all loans
    private Double minLoanAmount;
    private Double maxLoanAmount;
    private Double defaultLoanAmount;

    // by last loan amount
    // row 0
    private Double lowerLastLoanAmount;
    private Double upperLastLoanAmount;
    private Double minLastLoanAmount;
    private Double maxLastLoanAmount;
    private Double defaultLastLoanAmount;

    // by loan cycle
    // row 0
    private Integer loanCycleNumber;
    private Double minLoanAmountForCycle;
    private Double maxLoanAmountForCycle;
    private Double defaultLoanAmountForCycle;

    private String selectedInterestRateCalculationType;
    private Map<String, String> interestRateCalculationTypeOptions;
    private Double maxInterestRate;
    private Double minInterestRate;
    private Double defaultInterestRate;

    private Map<String, String> installmentFrequencyPeriodOptions;
    private String installmentFrequencyPeriod;
    private Integer installmentFrequencyRecurrenceEvery;

    private String installmentsCalculationType;
    private Map<String, String> installmentsCalculationTypeOptions;

    // same for all loans
    private Integer minInstallments;
    private Integer maxInstallments;
    private Integer defaultInstallments;

    private String selectedGracePeriodType;
    private Map<String, String> gracePeriodTypeOptions;
    private Integer gracePeriodDurationInInstallments;

    private Map<String, String> applicableFeeOptions;
    private Map<String, String> selectedFeeOptions;
    private String[] selectedFees;

    private Map<String, String> applicableFundOptions;
    private Map<String, String> selectedFundOptions;
    private String[] selectedFunds;

    private Map<String, String> interestGeneralLedgerOptions;
    private String selectedInterest;
    private Map<String, String> principalGeneralLedgerOptions;
    private String selectedPrincipal;

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

    public boolean isIncludeInLoanCycleCounter() {
        return this.includeInLoanCycleCounter;
    }

    public void setIncludeInLoanCycleCounter(boolean includeInLoanCycleCounter) {
        this.includeInLoanCycleCounter = includeInLoanCycleCounter;
    }

    public String getLoanAmountCalculationType() {
        return this.loanAmountCalculationType;
    }

    public void setLoanAmountCalculationType(String loanAmountCalculationType) {
        this.loanAmountCalculationType = loanAmountCalculationType;
    }

    public Double getMinLoanAmount() {
        return this.minLoanAmount;
    }

    public void setMinLoanAmount(Double minLoanAmount) {
        this.minLoanAmount = minLoanAmount;
    }

    public Double getMaxLoanAmount() {
        return this.maxLoanAmount;
    }

    public void setMaxLoanAmount(Double maxLoanAmount) {
        this.maxLoanAmount = maxLoanAmount;
    }

    public Double getDefaultLoanAmount() {
        return this.defaultLoanAmount;
    }

    public void setDefaultLoanAmount(Double defaultLoanAmount) {
        this.defaultLoanAmount = defaultLoanAmount;
    }

    public Double getLowerLastLoanAmount() {
        return this.lowerLastLoanAmount;
    }

    public void setLowerLastLoanAmount(Double lowerLastLoanAmount) {
        this.lowerLastLoanAmount = lowerLastLoanAmount;
    }

    public Double getUpperLastLoanAmount() {
        return this.upperLastLoanAmount;
    }

    public void setUpperLastLoanAmount(Double upperLastLoanAmount) {
        this.upperLastLoanAmount = upperLastLoanAmount;
    }

    public Double getMinLastLoanAmount() {
        return this.minLastLoanAmount;
    }

    public void setMinLastLoanAmount(Double minLastLoanAmount) {
        this.minLastLoanAmount = minLastLoanAmount;
    }

    public Double getMaxLastLoanAmount() {
        return this.maxLastLoanAmount;
    }

    public void setMaxLastLoanAmount(Double maxLastLoanAmount) {
        this.maxLastLoanAmount = maxLastLoanAmount;
    }

    public Double getDefaultLastLoanAmount() {
        return this.defaultLastLoanAmount;
    }

    public void setDefaultLastLoanAmount(Double defaultLastLoanAmount) {
        this.defaultLastLoanAmount = defaultLastLoanAmount;
    }

    public Integer getLoanCycleNumber() {
        return this.loanCycleNumber;
    }

    public void setLoanCycleNumber(Integer loanCycleNumber) {
        this.loanCycleNumber = loanCycleNumber;
    }

    public Double getMinLoanAmountForCycle() {
        return this.minLoanAmountForCycle;
    }

    public void setMinLoanAmountForCycle(Double minLoanAmountForCycle) {
        this.minLoanAmountForCycle = minLoanAmountForCycle;
    }

    public Double getMaxLoanAmountForCycle() {
        return this.maxLoanAmountForCycle;
    }

    public void setMaxLoanAmountForCycle(Double maxLoanAmountForCycle) {
        this.maxLoanAmountForCycle = maxLoanAmountForCycle;
    }

    public Double getDefaultLoanAmountForCycle() {
        return this.defaultLoanAmountForCycle;
    }

    public void setDefaultLoanAmountForCycle(Double defaultLoanAmountForCycle) {
        this.defaultLoanAmountForCycle = defaultLoanAmountForCycle;
    }

    public String getSelectedInterestRateCalculationType() {
        return this.selectedInterestRateCalculationType;
    }

    public void setSelectedInterestRateCalculationType(String selectedInterestRateCalculationType) {
        this.selectedInterestRateCalculationType = selectedInterestRateCalculationType;
    }

    public Map<String, String> getInterestRateCalculationTypeOptions() {
        return this.interestRateCalculationTypeOptions;
    }

    public void setInterestRateCalculationTypeOptions(Map<String, String> interestRateCalculationTypeOptions) {
        this.interestRateCalculationTypeOptions = interestRateCalculationTypeOptions;
    }

    public Double getMaxInterestRate() {
        return this.maxInterestRate;
    }

    public void setMaxInterestRate(Double maxInterestRate) {
        this.maxInterestRate = maxInterestRate;
    }

    public Double getMinInterestRate() {
        return this.minInterestRate;
    }

    public void setMinInterestRate(Double minInterestRate) {
        this.minInterestRate = minInterestRate;
    }

    public Double getDefaultInterestRate() {
        return this.defaultInterestRate;
    }

    public void setDefaultInterestRate(Double defaultInterestRate) {
        this.defaultInterestRate = defaultInterestRate;
    }

    public String getInstallmentFrequencyPeriod() {
        return this.installmentFrequencyPeriod;
    }

    public void setInstallmentFrequencyPeriod(String installmentFrequencyPeriod) {
        this.installmentFrequencyPeriod = installmentFrequencyPeriod;
    }

    public Integer getInstallmentFrequencyRecurrenceEvery() {
        return this.installmentFrequencyRecurrenceEvery;
    }

    public void setInstallmentFrequencyRecurrenceEvery(Integer installmentFrequencyRecurrenceEvery) {
        this.installmentFrequencyRecurrenceEvery = installmentFrequencyRecurrenceEvery;
    }

    public String getInstallmentsCalculationType() {
        return this.installmentsCalculationType;
    }

    public void setInstallmentsCalculationType(String installmentsCalculationType) {
        this.installmentsCalculationType = installmentsCalculationType;
    }

    public Integer getMinInstallments() {
        return this.minInstallments;
    }

    public void setMinInstallments(Integer minInstallments) {
        this.minInstallments = minInstallments;
    }

    public Integer getMaxInstallments() {
        return this.maxInstallments;
    }

    public void setMaxInstallments(Integer maxInstallments) {
        this.maxInstallments = maxInstallments;
    }

    public Integer getDefaultInstallments() {
        return this.defaultInstallments;
    }

    public void setDefaultInstallments(Integer defaultInstallments) {
        this.defaultInstallments = defaultInstallments;
    }

    public String getSelectedGracePeriodType() {
        return this.selectedGracePeriodType;
    }

    public void setSelectedGracePeriodType(String selectedGracePeriodType) {
        this.selectedGracePeriodType = selectedGracePeriodType;
    }

    public Map<String, String> getGracePeriodTypeOptions() {
        return this.gracePeriodTypeOptions;
    }

    public void setGracePeriodTypeOptions(Map<String, String> gracePeriodTypeOptions) {
        this.gracePeriodTypeOptions = gracePeriodTypeOptions;
    }

    public Integer getGracePeriodDurationInInstallments() {
        return this.gracePeriodDurationInInstallments;
    }

    public void setGracePeriodDurationInInstallments(Integer gracePeriodDurationInInstallments) {
        this.gracePeriodDurationInInstallments = gracePeriodDurationInInstallments;
    }

    public Map<String, String> getApplicableFeeOptions() {
        return this.applicableFeeOptions;
    }

    public void setApplicableFeeOptions(Map<String, String> applicableFeeOptions) {
        this.applicableFeeOptions = applicableFeeOptions;
    }

    public Map<String, String> getSelectedFeeOptions() {
        return this.selectedFeeOptions;
    }

    public void setSelectedFeeOptions(Map<String, String> selectedFeeOptions) {
        this.selectedFeeOptions = selectedFeeOptions;
    }

    public Map<String, String> getApplicableFundOptions() {
        return this.applicableFundOptions;
    }

    public void setApplicableFundOptions(Map<String, String> applicableFundOptions) {
        this.applicableFundOptions = applicableFundOptions;
    }

    public Map<String, String> getSelectedFundOptions() {
        return this.selectedFundOptions;
    }

    public void setSelectedFundOptions(Map<String, String> selectedFundOptions) {
        this.selectedFundOptions = selectedFundOptions;
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

    public Map<String, String> getLoanAmountCalculationTypeOptions() {
        return this.loanAmountCalculationTypeOptions;
    }

    public void setLoanAmountCalculationTypeOptions(Map<String, String> loanAmountCalculationTypeOptions) {
        this.loanAmountCalculationTypeOptions = loanAmountCalculationTypeOptions;
    }

    public Map<String, String> getInstallmentFrequencyPeriodOptions() {
        return this.installmentFrequencyPeriodOptions;
    }

    public void setInstallmentFrequencyPeriodOptions(Map<String, String> installmentFrequencyPeriodOptions) {
        this.installmentFrequencyPeriodOptions = installmentFrequencyPeriodOptions;
    }

    public Map<String, String> getInstallmentsCalculationTypeOptions() {
        return this.installmentsCalculationTypeOptions;
    }

    public void setInstallmentsCalculationTypeOptions(Map<String, String> installmentsCalculationTypeOptions) {
        this.installmentsCalculationTypeOptions = installmentsCalculationTypeOptions;
    }

    public String[] getSelectedFees() {
        return this.selectedFees;
    }

    public void setSelectedFees(String[] selectedFees) {
        this.selectedFees = selectedFees;
    }

    public String[] getSelectedFunds() {
        return this.selectedFunds;
    }

    public void setSelectedFunds(String[] selectedFunds) {
        this.selectedFunds = selectedFunds;
    }

    public String getSelectedInterest() {
        return this.selectedInterest;
    }

    public void setSelectedInterest(String selectedInterest) {
        this.selectedInterest = selectedInterest;
    }

    public String getSelectedPrincipal() {
        return this.selectedPrincipal;
    }

    public void setSelectedPrincipal(String selectedPrincipal) {
        this.selectedPrincipal = selectedPrincipal;
    }
}