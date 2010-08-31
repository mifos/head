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
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.mifos.dto.screen.ListElement;
import org.mifos.dto.screen.LoanProductFormDto;
import org.mifos.dto.screen.SavingsProductFormDto;

public class GeneralProductBeanAssembler {

    public GeneralProductBean assembleFromRefData(SavingsProductFormDto referenceData) {
        GeneralProductBean formBean = new GeneralProductBean();

        populateCategoryDropdown(referenceData.getProductCategories(), formBean);
        populateApplicableForDropdown(referenceData.getApplicableToCustomers(), formBean);
        populateStatusDropdown(referenceData.getStatusOptions(), formBean);
        defaultStartDateToToday(formBean);

        return formBean;
    }

    public GeneralProductBean assembleFromRefData(LoanProductFormDto referenceData) {
        GeneralProductBean formBean = new GeneralProductBean();

        populateCategoryDropdown(referenceData.getProductCategories(), formBean);
        populateApplicableForDropdown(referenceData.getApplicableCustomerTypes(), formBean);
        populateStatusDropdown(referenceData.getStatusOptions(), formBean);
        defaultStartDateToToday(formBean);

        return formBean;
    }

    private void defaultStartDateToToday(GeneralProductBean formBean) {
        DateTime today = new DateTime();
        formBean.setStartDateDay(today.getDayOfMonth());
        formBean.setStartDateMonth(today.getMonthOfYear());
        formBean.setStartDateYear(Integer.valueOf(today.getYearOfEra()).toString());
    }

    private void populateStatusDropdown(List<ListElement> options, GeneralProductBean formBean) {
        Map<String, String> statusOptions = new LinkedHashMap<String, String>();
        for (ListElement status : options) {
            statusOptions.put(status.getId().toString(), status.getName());
        }
        formBean.setStatusOptions(statusOptions);
    }

    private void populateApplicableForDropdown(List<ListElement> options, GeneralProductBean formBean) {
        Map<String, String> applicableForOptions = new LinkedHashMap<String, String>();
        for (ListElement customerType : options) {
            applicableForOptions.put(customerType.getId().toString(), customerType.getName());
        }
        formBean.setApplicableForOptions(applicableForOptions);
    }

    private void populateCategoryDropdown(List<ListElement> options, GeneralProductBean formBean) {
        Map<String, String> categoryOptions = new LinkedHashMap<String, String>();
        for (ListElement category : options) {
            categoryOptions.put(category.getId().toString(), category.getName());
        }
        formBean.setCategoryOptions(categoryOptions);

        if (options.size() == 1) {
            formBean.setSelectedCategory(options.get(0).getId().toString());
        }
    }
}
