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
import org.mifos.dto.screen.SavingsProductFormDto;

public class SavingsProductFormBeanAssembler {

    public SavingsProductFormBean assembleReferenceData(SavingsProductFormDto referenceData) {

        SavingsProductFormBean formBean = new SavingsProductFormBean();

        // assembler for general product details
        populateCategoryDropdown(referenceData, formBean);
        populateApplicableForDropdown(referenceData, formBean);

        // assembler for accounting section
        populatePrincipalGlCodesDropdown(referenceData, formBean);
        populateInterestGlCodesDropdown(referenceData, formBean);

        populateDepositTypesDropdown(referenceData, formBean);
        populateAppliesToDropdown(referenceData, formBean);
        populateInterestBalanceDropdown(referenceData, formBean);
        populateFrequencyPeriodDropdown(referenceData, formBean);
        return formBean;
    }

    private void populateFrequencyPeriodDropdown(SavingsProductFormDto referenceData, SavingsProductFormBean formBean) {

        Map<String, String> frequencyPeriods = new LinkedHashMap<String, String>();
        for (ListElement option : referenceData.getInterestTimePeriodTypes()) {
            frequencyPeriods.put(option.getId().toString(), option.getName());
        }
        formBean.setFrequencyPeriodOptions(frequencyPeriods);
    }

    private void populateInterestBalanceDropdown(SavingsProductFormDto referenceData, SavingsProductFormBean formBean) {

        Map<String, String> interestBalanceTypes = new LinkedHashMap<String, String>();
        for (ListElement option : referenceData.getInterestBalanceTypes()) {
            interestBalanceTypes.put(option.getId().toString(), option.getName());
        }
        formBean.setInterestCaluclationOptions(interestBalanceTypes);
    }

    private void populateAppliesToDropdown(SavingsProductFormDto referenceData, SavingsProductFormBean formBean) {

        Map<String, String> groupSavingsApproachOptions = new LinkedHashMap<String, String>();
        for (ListElement option : referenceData.getDespositAmountAppliesTo()) {
            groupSavingsApproachOptions.put(option.getId().toString(), option.getName());
        }
        formBean.setGroupSavingsApproachOptions(groupSavingsApproachOptions);
    }

    private void populateDepositTypesDropdown(SavingsProductFormDto referenceData, SavingsProductFormBean formBean) {

        Map<String, String> depositTypes = new LinkedHashMap<String, String>();
        for (ListElement option : referenceData.getDepositTypes()) {
            depositTypes.put(option.getId().toString(), option.getName());
        }
        formBean.setDepositTypeOptions(depositTypes);
    }

    private void populateApplicableForDropdown(SavingsProductFormDto referenceData,
            SavingsProductFormBean formBean) {
        Map<String, String> applicableForOptions = new LinkedHashMap<String, String>();
        for (ListElement customerType : referenceData.getApplicableToCustomers()) {
            applicableForOptions.put(customerType.getId().toString(), customerType.getName());
        }
        formBean.setApplicableForOptions(applicableForOptions);
    }

    private void populateCategoryDropdown(SavingsProductFormDto referenceData, SavingsProductFormBean formBean) {
        Map<String, String> categoryOptions = new LinkedHashMap<String, String>();
        for (ListElement category : referenceData.getProductCategories()) {
            categoryOptions.put(category.getId().toString(), category.getName());
        }
        formBean.setCategoryOptions(categoryOptions);

        if (referenceData.getProductCategories().size() == 1) {
            formBean.setSelectedCategory(referenceData.getProductCategories().get(0).getId().toString());
        }
    }

    private void populatePrincipalGlCodesDropdown(SavingsProductFormDto referenceData, SavingsProductFormBean formBean) {
        Map<String, String> principalGeneralLedgerOptions = new LinkedHashMap<String, String>();
        for (ListElement glCode : referenceData.getPrincipalGlCodes()) {
            principalGeneralLedgerOptions.put(glCode.getId().toString(), glCode.getName());
        }
        formBean.setPrincipalGeneralLedgerOptions(principalGeneralLedgerOptions);
    }

    private void populateInterestGlCodesDropdown(SavingsProductFormDto referenceData, SavingsProductFormBean formBean) {
        Map<String, String> interestGeneralLedgerOptions = new LinkedHashMap<String, String>();
        for (ListElement glCode : referenceData.getInterestGlCodes()) {
            interestGeneralLedgerOptions.put(glCode.getId().toString(), glCode.getName());
        }
        formBean.setInterestGeneralLedgerOptions(interestGeneralLedgerOptions);
    }
}