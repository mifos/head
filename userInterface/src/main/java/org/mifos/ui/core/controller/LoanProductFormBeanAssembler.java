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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.mifos.dto.domain.LoanProductRequest;
import org.mifos.dto.domain.LowerUpperMinMaxDefaultDto;
import org.mifos.dto.domain.MinMaxDefaultDto;
import org.mifos.dto.domain.RepaymentDetailsDto;
import org.mifos.dto.screen.AccountingDetailsDto;
import org.mifos.dto.screen.ListElement;
import org.mifos.dto.screen.LoanAmountDetailsDto;
import org.mifos.dto.screen.LoanProductDetails;
import org.mifos.dto.screen.LoanProductFormDto;

public class LoanProductFormBeanAssembler {

    private GeneralProductBeanAssembler productBeanAssembler = new GeneralProductBeanAssembler();

    public LoanProductFormBean populateWithReferenceData(LoanProductFormDto loanProductRefData) {

        LoanProductFormBean loanProductFormBean = new LoanProductFormBean();

        GeneralProductBean productDetails = productBeanAssembler.assembleFromRefData(loanProductRefData);
        loanProductFormBean.setGeneralDetails(productDetails);

        populateCurrenciesDropdown(loanProductRefData, loanProductFormBean);

        Map<String, String> loanAmountCalculationTypeOptions = populateLoanAmountCalculationRadioButtons(loanProductFormBean);

        populateInterestRateDropdown(loanProductRefData, loanProductFormBean);
        populateFrequencyRadioButtons(loanProductFormBean);
        populateInstallmentCalculationRadioButtons(loanProductFormBean, loanAmountCalculationTypeOptions);
        populateGracePeriodDrowdown(loanProductRefData, loanProductFormBean);

        populateApplicableFeeMultiSelectList(loanProductRefData, loanProductFormBean);
        populateSelectedFeeMultiSelectList(loanProductFormBean);

        populateApplicableFundsMultiSelectList(loanProductRefData, loanProductFormBean);
        populateSelectedFundMultiSelectList(loanProductFormBean);

        populateInterestGlCodesDropdown(loanProductRefData, loanProductFormBean);
        populatePrincipalGlCodesDropdown(loanProductRefData, loanProductFormBean);

        return loanProductFormBean;
    }

    private void populateCurrenciesDropdown(LoanProductFormDto loanProductRefData,
            LoanProductFormBean loanProductFormBean) {
        Map<String, String> currencyOptions = new LinkedHashMap<String, String>();
        for (ListElement currency : loanProductRefData.getCurrencyOptions()) {
            currencyOptions.put(currency.getId().toString(), currency.getName());
        }
        loanProductFormBean.setCurrencyOptions(currencyOptions);
        loanProductFormBean.setMultiCurrencyEnabled(loanProductRefData.isMultiCurrencyEnabled());
    }

    private void populatePrincipalGlCodesDropdown(LoanProductFormDto loanProductRefData,
            LoanProductFormBean loanProductFormBean) {
        Map<String, String> principalGeneralLedgerOptions = new LinkedHashMap<String, String>();
        for (ListElement glCode : loanProductRefData.getPrincipalGlCodes()) {
            principalGeneralLedgerOptions.put(glCode.getId().toString(), glCode.getName());
        }
        loanProductFormBean.setPrincipalGeneralLedgerOptions(principalGeneralLedgerOptions);
    }

    private void populateInterestGlCodesDropdown(LoanProductFormDto loanProductRefData,
            LoanProductFormBean loanProductFormBean) {
        Map<String, String> interestGeneralLedgerOptions = new LinkedHashMap<String, String>();
        for (ListElement glCode : loanProductRefData.getInterestGlCodes()) {
            interestGeneralLedgerOptions.put(glCode.getId().toString(), glCode.getName());
        }
        loanProductFormBean.setInterestGeneralLedgerOptions(interestGeneralLedgerOptions);
    }

    private void populateSelectedFundMultiSelectList(LoanProductFormBean loanProductFormBean) {
        Map<String, String> selectedFundOptions = new LinkedHashMap<String, String>();
        loanProductFormBean.setSelectedFundOptions(selectedFundOptions);
    }

    private void populateApplicableFundsMultiSelectList(LoanProductFormDto loanProductRefData,
            LoanProductFormBean loanProductFormBean) {
        Map<String, String> applicableFundOptions = new LinkedHashMap<String, String>();
        for (ListElement fund : loanProductRefData.getSourceOfFunds()) {
            applicableFundOptions.put(fund.getId().toString(), fund.getName());
        }
        loanProductFormBean.setApplicableFundOptions(applicableFundOptions);
    }

    private void populateSelectedFeeMultiSelectList(LoanProductFormBean loanProductFormBean) {
        Map<String, String> selectedFeeOptions = new LinkedHashMap<String, String>();
        loanProductFormBean.setSelectedFeeOptions(selectedFeeOptions);
    }

    private void populateApplicableFeeMultiSelectList(LoanProductFormDto loanProductRefData,
            LoanProductFormBean loanProductFormBean) {
        Map<String, String> applicableFeeOptions = new LinkedHashMap<String, String>();
        for (ListElement fee : loanProductRefData.getLoanFees()) {
            applicableFeeOptions.put(fee.getId().toString(), fee.getName());
        }
        loanProductFormBean.setApplicableFeeOptions(applicableFeeOptions);
    }

    private void populateGracePeriodDrowdown(LoanProductFormDto loanProductRefData,
            LoanProductFormBean loanProductFormBean) {
        Map<String, String> gracePeriodTypeOptions = new LinkedHashMap<String, String>();
        for (ListElement gracePeriod : loanProductRefData.getGracePeriodTypes()) {
            gracePeriodTypeOptions.put(gracePeriod.getId().toString(), gracePeriod.getName());
        }
        loanProductFormBean.setGracePeriodTypeOptions(gracePeriodTypeOptions);
        loanProductFormBean.setSelectedGracePeriodType("1");
    }

    private void populateInstallmentCalculationRadioButtons(LoanProductFormBean loanProductFormBean,
            Map<String, String> loanAmountCalculationTypeOptions) {
        loanProductFormBean.setInstallmentsCalculationTypeOptions(loanAmountCalculationTypeOptions);
        loanProductFormBean.setSelectedInstallmentsCalculationType("1");
    }

    private void populateFrequencyRadioButtons(LoanProductFormBean loanProductFormBean) {
        Map<String, String> installmentFrequencyPeriodOptions = new LinkedHashMap<String, String>();
        installmentFrequencyPeriodOptions.put("1", "frequency.Weekly");
        installmentFrequencyPeriodOptions.put("2", "frequency.Monthly");
        loanProductFormBean.setInstallmentFrequencyPeriodOptions(installmentFrequencyPeriodOptions);
        loanProductFormBean.setInstallmentFrequencyPeriod("1");
    }

    private void populateInterestRateDropdown(LoanProductFormDto loanProductRefData,
            LoanProductFormBean loanProductFormBean) {
        Map<String, String> interestRateCalculationTypeOptions = new LinkedHashMap<String, String>();
        for (ListElement interestCalculationType : loanProductRefData.getInterestCalculationTypes()) {
            interestRateCalculationTypeOptions.put(interestCalculationType.getId().toString(), interestCalculationType.getName());
        }
        loanProductFormBean.setInterestRateCalculationTypeOptions(interestRateCalculationTypeOptions);
    }

    private Map<String, String> populateLoanAmountCalculationRadioButtons(LoanProductFormBean loanProductFormBean) {
        Map<String, String> loanAmountCalculationTypeOptions = new LinkedHashMap<String, String>();
        loanAmountCalculationTypeOptions.put("1", "product.sameforallloans");
        loanAmountCalculationTypeOptions.put("2", "product.bylastloanamount");
        loanAmountCalculationTypeOptions.put("3", "product.byloancycle");
        loanProductFormBean.setLoanAmountCalculationTypeOptions(loanAmountCalculationTypeOptions);
        loanProductFormBean.setSelectedLoanAmountCalculationType("1");
        return loanAmountCalculationTypeOptions;
    }

    public void setProductBeanAssembler(GeneralProductBeanAssembler productBeanAssembler) {
        this.productBeanAssembler = productBeanAssembler;
    }

    public LoanProductRequest toLoanProductDto(LoanProductFormBean loanProductFormBean) {
        LoanProductDetails loanProductDetails = translateToLoanProductDetails(loanProductFormBean);

        LoanAmountDetailsDto loanAmountDetails = translateToLoanAmountDetails(loanProductFormBean);

        Integer interestRateType = Integer.valueOf(loanProductFormBean.getSelectedInterestRateCalculationType());
        Double maxInterest = Double.valueOf(loanProductFormBean.getMaxInterestRate());
        Double minInterest = Double.valueOf(loanProductFormBean.getMinInterestRate());
        Double defaultInterest = Double.valueOf(loanProductFormBean.getDefaultInterestRate());
        MinMaxDefaultDto interestRateRange = MinMaxDefaultDto.create(minInterest, maxInterest, defaultInterest);

        RepaymentDetailsDto repaymentDetails = translateToRepaymentDetails(loanProductFormBean);

        List<Integer> applicableFees = new ArrayList<Integer>();
        if (loanProductFormBean.getSelectedFees() != null) {
            for (String feeId : loanProductFormBean.getSelectedFees()) {
                applicableFees.add(Integer.valueOf(feeId));
            }
        }

        AccountingDetailsDto accountDetails = translateToAccountingDetails(loanProductFormBean);

        return new LoanProductRequest(loanProductDetails, loanAmountDetails, interestRateType, interestRateRange, repaymentDetails, applicableFees, accountDetails);
    }

    private AccountingDetailsDto translateToAccountingDetails(LoanProductFormBean loanProductFormBean) {

        List<Integer> applicableFunds = new ArrayList<Integer>();
        if (loanProductFormBean.getSelectedFunds() != null) {
            for (String fundId : loanProductFormBean.getSelectedFunds()) {
                applicableFunds.add(Integer.valueOf(fundId));
            }
        }
        Integer interestGlCodeId = Integer.valueOf(loanProductFormBean.getSelectedInterest());
        Integer principalClCodeId = Integer.valueOf(loanProductFormBean.getSelectedPrincipal());

        return new AccountingDetailsDto(applicableFunds, interestGlCodeId, principalClCodeId);
    }

    private LoanProductDetails translateToLoanProductDetails(LoanProductFormBean loanProductFormBean) {

        Integer category = Integer.valueOf(loanProductFormBean.getGeneralDetails().getSelectedCategory());
        Integer applicableFor = Integer.valueOf(loanProductFormBean.getGeneralDetails().getSelectedApplicableFor());
        DateTime startDate = new DateTime().withDate(Integer.valueOf(loanProductFormBean.getGeneralDetails().getStartDateYear()), loanProductFormBean.getGeneralDetails().getStartDateMonth(), loanProductFormBean.getGeneralDetails().getStartDateDay());
        DateTime endDate = null;
        if (StringUtils.isNotBlank(loanProductFormBean.getGeneralDetails().getEndDateYear())) {
            endDate = new DateTime().withDate(Integer.valueOf(loanProductFormBean.getGeneralDetails().getEndDateYear()), loanProductFormBean.getGeneralDetails().getEndDateMonth(), loanProductFormBean.getGeneralDetails().getEndDateDay());
        }

        Integer currencyId = null;
        if (StringUtils.isNotBlank(loanProductFormBean.getSelectedCurrency())) {
            currencyId = Integer.valueOf(loanProductFormBean.getSelectedCurrency());
        }

        return new LoanProductDetails(loanProductFormBean.getGeneralDetails().getName(), loanProductFormBean.getGeneralDetails().getShortName(), loanProductFormBean.getGeneralDetails().getDescription(), category,
                startDate, endDate, applicableFor, loanProductFormBean.isIncludeInLoanCycleCounter(),loanProductFormBean.isWaiverInterest(), currencyId);
    }

    private LoanAmountDetailsDto translateToLoanAmountDetails(LoanProductFormBean loanProductFormBean) {
        Integer calculationType = Integer.valueOf(loanProductFormBean.getSelectedLoanAmountCalculationType());

        MinMaxDefaultDto sameForAllLoanRange = null;
        List<LowerUpperMinMaxDefaultDto>  byLastLoanAmountList = new ArrayList<LowerUpperMinMaxDefaultDto>();
        List<MinMaxDefaultDto> byLoanCycleList = new ArrayList<MinMaxDefaultDto>();
        if (Integer.valueOf(1).equals(calculationType)) {
            sameForAllLoanRange = translateSameForAllLoanBeanToMinMaxDefaultDto(loanProductFormBean.getLoanAmountSameForAllLoans());
        }

        if (Integer.valueOf(2).equals(calculationType)) {
            byLastLoanAmountList = translateByLastLoanAmountBeanToLowerUpperMinMaxDefaultDto(loanProductFormBean.getLoanAmountByLastLoanAmount());
        }

        if (Integer.valueOf(3).equals(calculationType)) {
            byLoanCycleList = translateLoanCycleBeanToMinMaxDefaultDto(loanProductFormBean.getLoanAmountByLoanCycle());
        }

        return new LoanAmountDetailsDto(calculationType, sameForAllLoanRange, byLastLoanAmountList, byLoanCycleList);
    }

    private LoanAmountDetailsDto translateToInstallmentDetails(LoanProductFormBean loanProductFormBean) {
        Integer calculationType = Integer.valueOf(loanProductFormBean.getSelectedInstallmentsCalculationType());

        MinMaxDefaultDto sameForAllLoanRange = null;
        List<LowerUpperMinMaxDefaultDto>  byLastLoanAmountList = new ArrayList<LowerUpperMinMaxDefaultDto>();
        List<MinMaxDefaultDto> byLoanCycleList = new ArrayList<MinMaxDefaultDto>();
        if (Integer.valueOf(1).equals(calculationType)) {
            sameForAllLoanRange = translateSameForAllLoanBeanToMinMaxDefaultDto(loanProductFormBean.getInstallmentsSameForAllLoans());
        }

        if (Integer.valueOf(2).equals(calculationType)) {
            byLastLoanAmountList = translateByLastLoanAmountBeanToLowerUpperMinMaxDefaultDto(loanProductFormBean.getInstallmentsByLastLoanAmount());
        }

        if (Integer.valueOf(3).equals(calculationType)) {
            byLoanCycleList = translateLoanCycleBeanToMinMaxDefaultDto(loanProductFormBean.getLoanAmountByLoanCycle());
        }

        return new LoanAmountDetailsDto(calculationType, sameForAllLoanRange, byLastLoanAmountList, byLoanCycleList);
    }

    private RepaymentDetailsDto translateToRepaymentDetails(LoanProductFormBean loanProductFormBean) {

        Integer frequencyType = Integer.valueOf(loanProductFormBean.getInstallmentFrequencyPeriod());
        Integer recurs = Integer.valueOf(loanProductFormBean.getInstallmentFrequencyRecurrenceEvery());

        LoanAmountDetailsDto installmentCalculationDetails = translateToInstallmentDetails(loanProductFormBean);

        Integer gracePeriodType = Integer.valueOf(loanProductFormBean.getSelectedGracePeriodType());
        Integer gracePeriodDuration = loanProductFormBean.getGracePeriodDurationInInstallments();

        RepaymentDetailsDto details = new RepaymentDetailsDto(frequencyType, recurs, installmentCalculationDetails, gracePeriodType, gracePeriodDuration);

        return details;
    }

    private MinMaxDefaultDto translateSameForAllLoanBeanToMinMaxDefaultDto(SameForAllLoanBean bean) {
        return MinMaxDefaultDto.create(bean.getMin(), bean.getMax(), bean.getTheDefault());
    }

    private List<MinMaxDefaultDto> translateLoanCycleBeanToMinMaxDefaultDto(ByLoanCycleBean[] loanAmountByLoanCycle) {
        List<MinMaxDefaultDto> byLoanCycleList = new ArrayList<MinMaxDefaultDto>();
        for (ByLoanCycleBean bean : loanAmountByLoanCycle) {
            byLoanCycleList.add(MinMaxDefaultDto.create(bean.getMin(), bean.getMax(), bean.getTheDefault()));
        }
        return byLoanCycleList;
    }

    private List<LowerUpperMinMaxDefaultDto> translateByLastLoanAmountBeanToLowerUpperMinMaxDefaultDto(ByLastLoanAmountBean[] loanAmountByLastLoanAmount) {
        List<LowerUpperMinMaxDefaultDto> list = new ArrayList<LowerUpperMinMaxDefaultDto>();
        for (ByLastLoanAmountBean bean : loanAmountByLastLoanAmount) {
            list.add(LowerUpperMinMaxDefaultDto.create(bean.getLower(), bean.getUpper(), bean.getMin(), bean.getMax(), bean.getTheDefault()));
        }
        return list;
    }
}