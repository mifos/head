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

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP", justification = "..")
public class LoanProductFormBean {

    @Valid
    private GeneralProductBean generalDetails;

    private boolean multiCurrencyEnabled;
    private String selectedCurrency;
    private Map<String, String> currencyOptions;

    private boolean includeInLoanCycleCounter;

    private boolean waiverInterest;

    @NotEmpty
    private String selectedLoanAmountCalculationType;
    private Map<String, String> loanAmountCalculationTypeOptions;

    private SameForAllLoanBean loanAmountSameForAllLoans;
    private ByLastLoanAmountBean[] loanAmountByLastLoanAmount;
    private ByLoanCycleBean[] loanAmountByLoanCycle;

    @NotEmpty
    private String selectedInterestRateCalculationType;
    private Map<String, String> interestRateCalculationTypeOptions;

    @Min(value = 0)
    @Max(value = 999)
    @NotNull
    private Double maxInterestRate;
    @Min(value = 0)
    @Max(value = 999)
    @NotNull
    private Double minInterestRate;
    @Min(value = 0)
    @Max(value = 99)
    @NotNull
    private Double defaultInterestRate;

    @NotEmpty
    private String installmentFrequencyPeriod;
    private Map<String, String> installmentFrequencyPeriodOptions;

    @Min(value = 0)
    @Max(value = 100)
    @NotNull
    private Integer installmentFrequencyRecurrenceEvery;

    @NotEmpty
    private String selectedInstallmentsCalculationType;
    private Map<String, String> installmentsCalculationTypeOptions;

    private SameForAllLoanBean installmentsSameForAllLoans;
    private ByLastLoanAmountBean[] installmentsByLastLoanAmount;
    private ByLoanCycleBean[] installmentsByLoanCycle;

    private String selectedGracePeriodType;
    private Map<String, String> gracePeriodTypeOptions;
    private Integer gracePeriodDurationInInstallments;

    private Map<String, String> applicableFeeOptions;
    private Map<String, String> selectedFeeOptions;
    private String[] applicableFees;
    private String[] selectedFees;

    private Map<String, String> applicablePenaltyOptions;
    private Map<String, String> selectedPenaltyOptions;
    private String[] applicablePenalties;
    private String[] selectedPenalties;

    private Map<String, String> applicableFundOptions;
    private Map<String, String> selectedFundOptions;
    private String[] applicableFunds;

    private String[] selectedFunds;
    @NotEmpty
    private String selectedInterest;
    private Map<String, String> interestGeneralLedgerOptions;

    @NotEmpty
    private String selectedPrincipal;
    private Map<String, String> principalGeneralLedgerOptions;

    public boolean isIncludeInLoanCycleCounter() {
        return this.includeInLoanCycleCounter;
    }

    public boolean isWaiverInterest() {
        return waiverInterest;
    }

    public void setWaiverInterest(boolean waiverInterest) {
        this.waiverInterest = waiverInterest;
    }

    public void setIncludeInLoanCycleCounter(boolean includeInLoanCycleCounter) {
        this.includeInLoanCycleCounter = includeInLoanCycleCounter;
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

    public String getSelectedLoanAmountCalculationType() {
        return this.selectedLoanAmountCalculationType;
    }

    public void setSelectedLoanAmountCalculationType(String selectedLoanAmountCalculationType) {
        this.selectedLoanAmountCalculationType = selectedLoanAmountCalculationType;
    }

    public SameForAllLoanBean getLoanAmountSameForAllLoans() {
        return this.loanAmountSameForAllLoans;
    }

    public void setLoanAmountSameForAllLoans(SameForAllLoanBean loanAmountSameForAllLoans) {
        this.loanAmountSameForAllLoans = loanAmountSameForAllLoans;
    }

    public ByLastLoanAmountBean[] getLoanAmountByLastLoanAmount() {
        return this.loanAmountByLastLoanAmount;
    }

    public void setLoanAmountByLastLoanAmount(ByLastLoanAmountBean[] loanAmountByLastLoanAmount) {
        this.loanAmountByLastLoanAmount = loanAmountByLastLoanAmount;
    }

    public ByLoanCycleBean[] getLoanAmountByLoanCycle() {
        return this.loanAmountByLoanCycle;
    }

    public void setLoanAmountByLoanCycle(ByLoanCycleBean[] loanAmountByLoanCycle) {
        this.loanAmountByLoanCycle = loanAmountByLoanCycle;
    }

    public String getSelectedInstallmentsCalculationType() {
        return this.selectedInstallmentsCalculationType;
    }

    public void setSelectedInstallmentsCalculationType(String selectedInstallmentsCalculationType) {
        this.selectedInstallmentsCalculationType = selectedInstallmentsCalculationType;
    }

    public SameForAllLoanBean getInstallmentsSameForAllLoans() {
        return this.installmentsSameForAllLoans;
    }

    public void setInstallmentsSameForAllLoans(SameForAllLoanBean installmentsSameForAllLoans) {
        this.installmentsSameForAllLoans = installmentsSameForAllLoans;
    }

    public ByLastLoanAmountBean[] getInstallmentsByLastLoanAmount() {
        return this.installmentsByLastLoanAmount;
    }

    public void setInstallmentsByLastLoanAmount(ByLastLoanAmountBean[] installmentsByLastLoanAmount) {
        this.installmentsByLastLoanAmount = installmentsByLastLoanAmount;
    }

    public ByLoanCycleBean[] getInstallmentsByLoanCycle() {
        return this.installmentsByLoanCycle;
    }

    public void setInstallmentsByLoanCycle(ByLoanCycleBean[] installmentsByLoanCycle) {
        this.installmentsByLoanCycle = installmentsByLoanCycle;
    }

    public String[] getApplicableFees() {
        return this.applicableFees;
    }

    public void setApplicableFees(String[] applicableFees) {
        this.applicableFees = applicableFees;
    }

    public String[] getApplicableFunds() {
        return this.applicableFunds;
    }

    public void setApplicableFunds(String[] applicableFunds) {
        this.applicableFunds = applicableFunds;
    }

    public GeneralProductBean getGeneralDetails() {
        return this.generalDetails;
    }

    public void setGeneralDetails(GeneralProductBean generalDetails) {
        this.generalDetails = generalDetails;
    }

    public String[] getApplicablePenalties() {
        return applicablePenalties;
    }

    public void setApplicablePenalties(String[] applicablePenalties) {
        this.applicablePenalties = applicablePenalties;
    }

    public String[] getSelectedPenalties() {
        return selectedPenalties;
    }

    public void setSelectedPenalties(String[] selectedPenalties) {
        this.selectedPenalties = selectedPenalties;
    }
    
    public Map<String, String> getApplicablePenaltyOptions() {
        return applicablePenaltyOptions;
    }

    public void setApplicablePenaltyOptions(Map<String, String> applicablePenaltyOptions) {
        this.applicablePenaltyOptions = applicablePenaltyOptions;
    }

    public Map<String, String> getSelectedPenaltyOptions() {
        return selectedPenaltyOptions;
    }

    public void setSelectedPenaltyOptions(Map<String, String> selectedPenaltyOptions) {
        this.selectedPenaltyOptions = selectedPenaltyOptions;
    }

    public void resetMultiSelectListBoxes() {
        if (this.selectedFees != null) {
            for (String selectedFee : this.selectedFees) {
                if (this.applicableFeeOptions.containsKey(selectedFee)) {
                    String value = this.applicableFeeOptions.remove(selectedFee);
                    this.selectedFeeOptions.put(selectedFee, value);
                }
            }
        }

        if (this.selectedFunds != null) {
            for (String selectedFund : this.selectedFunds) {
                if (this.applicableFundOptions.containsKey(selectedFund)) {
                    String value = this.applicableFundOptions.remove(selectedFund);
                    this.selectedFundOptions.put(selectedFund, value);
                }
            }
        }
        
        if (this.selectedPenalties != null) {
            for (String selectedPenalty : this.selectedPenalties) {
                if (this.applicablePenaltyOptions.containsKey(selectedPenalty)) {
                    String value = this.applicablePenaltyOptions.remove(selectedPenalty);
                    this.selectedPenaltyOptions.put(selectedPenalty, value);
                }
            }
        }
    }

    public void removeMultiSelectItems() {

        if (this.selectedFees != null && this.selectedFees.length == 1) {
            for (String selectedFee : this.selectedFees) {
                if (containsKey(this.applicableFees, selectedFee)) {
                    this.selectedFees = null;
                }
            }
        }

        if (this.selectedFunds != null && this.selectedFunds.length == 1) {
            for (String selectedFund : this.selectedFunds) {
                if (containsKey(this.applicableFunds, selectedFund)) {
                    this.selectedFunds = null;
                }
            }
        }
        
        if (this.selectedPenalties != null) {
            for (String selectedPenalty : this.selectedPenalties) {
                if (containsKey(this.applicablePenalties, selectedPenalty)) {
                    this.selectedPenalties = null;
                }
            }
        }
    }

    private boolean containsKey(String[] unselectedKey, String key) {
        boolean found = false;
        for (String item : unselectedKey) {
            if (item.equals(key)) {
                found = true;
            }
        }
        return found;
    }

    public String getSelectedCurrency() {
        return this.selectedCurrency;
    }

    public void setSelectedCurrency(String selectedCurrency) {
        this.selectedCurrency = selectedCurrency;
    }

    public Map<String, String> getCurrencyOptions() {
        return this.currencyOptions;
    }

    public void setCurrencyOptions(Map<String, String> currencyOptions) {
        this.currencyOptions = currencyOptions;
    }

    public boolean isMultiCurrencyEnabled() {
        return this.multiCurrencyEnabled;
    }

    public void setMultiCurrencyEnabled(boolean multiCurrencyEnabled) {
        this.multiCurrencyEnabled = multiCurrencyEnabled;
    }

    public ByLoanCycleBean[] createByLoanCycleBeans() {

        ByLoanCycleBean zeroCycle = new ByLoanCycleBean(1);
        ByLoanCycleBean oneCycle = new ByLoanCycleBean(2);
        ByLoanCycleBean twoCycle = new ByLoanCycleBean(3);
        ByLoanCycleBean threeCycle = new ByLoanCycleBean(4);
        ByLoanCycleBean fourCycle = new ByLoanCycleBean(5);
        ByLoanCycleBean greaterThanFourCycle = new ByLoanCycleBean(6);

        return new ByLoanCycleBean[] { zeroCycle, oneCycle, twoCycle, threeCycle, fourCycle, greaterThanFourCycle };
    }

    public ByLastLoanAmountBean[] createByLastLoanAmountBeans() {
        ByLastLoanAmountBean zero = new ByLastLoanAmountBean();
        zero.setLower(Double.valueOf("0"));
        ByLastLoanAmountBean one = new ByLastLoanAmountBean();
        ByLastLoanAmountBean two = new ByLastLoanAmountBean();
        ByLastLoanAmountBean three = new ByLastLoanAmountBean();
        ByLastLoanAmountBean four = new ByLastLoanAmountBean();
        ByLastLoanAmountBean five = new ByLastLoanAmountBean();

        return new ByLastLoanAmountBean[] { zero, one, two, three, four, five };
    }
}