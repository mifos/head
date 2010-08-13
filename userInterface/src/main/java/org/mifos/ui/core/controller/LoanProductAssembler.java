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

import java.util.LinkedHashMap;
import java.util.Map;

import org.mifos.dto.screen.ListElement;
import org.mifos.dto.screen.LoanProductFormDto;

public class LoanProductAssembler {

    public LoanProductFormBean populateWithReferenceData(LoanProductFormDto loanProductRefData) {

        LoanProductFormBean loanProductFormBean = new LoanProductFormBean();

        populateCategoryDropdown(loanProductRefData, loanProductFormBean);
        populateApplicableForDropdown(loanProductRefData, loanProductFormBean);

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

    private void populateApplicableForDropdown(LoanProductFormDto loanProductRefData,
            LoanProductFormBean loanProductFormBean) {
        Map<String, String> applicableForOptions = new LinkedHashMap<String, String>();
        for (ListElement customerType : loanProductRefData.getApplicableCustomerTypes()) {
            applicableForOptions.put(customerType.getId().toString(), customerType.getName());
        }
        loanProductFormBean.setApplicableForOptions(applicableForOptions);
    }

    private void populateCategoryDropdown(LoanProductFormDto loanProductRefData, LoanProductFormBean loanProductFormBean) {
        Map<String, String> categoryOptions = new LinkedHashMap<String, String>();
        for (ListElement category : loanProductRefData.getProductCategories()) {
            categoryOptions.put(category.getId().toString(), category.getName());
        }
        loanProductFormBean.setCategoryOptions(categoryOptions);

        if (loanProductRefData.getProductCategories().size() == 1) {
            loanProductFormBean.setSelectedCategory(loanProductRefData.getProductCategories().get(0).getId().toString());
        }
    }
}