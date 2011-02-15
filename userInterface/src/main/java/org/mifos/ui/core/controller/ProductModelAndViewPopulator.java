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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.web.servlet.ModelAndView;

public class ProductModelAndViewPopulator {

    public void populateProductDetails(GeneralProductBean bean, ModelAndView modelAndView) {
        String categoryName = bean.getCategoryOptions().get(bean.getSelectedCategory());

        DateTime startDate = new DateTime().withDate(Integer.parseInt(bean.getStartDateYear()), bean.getStartDateMonth(), bean.getStartDateDay());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String startDateFormatted = format.format(startDate.toDate());

        String endDateFormatted = "";
        if (StringUtils.isNotBlank(bean.getEndDateYear())) {
            DateTime endDate = new DateTime().withDate(Integer.parseInt(bean.getEndDateYear()), bean.getEndDateMonth(), bean.getEndDateDay());
            endDateFormatted = format.format(endDate.toDate());
        }

        String applicableTo = bean.getApplicableForOptions().get(bean.getSelectedApplicableFor());

        modelAndView.addObject("categoryName", categoryName);
        modelAndView.addObject("startDateFormatted", startDateFormatted);
        modelAndView.addObject("endDateFormatted", endDateFormatted);
        modelAndView.addObject("applicableTo", applicableTo);
    }

    public void populateModelAndViewForPreview(LoanProductFormBean loanProduct, ModelAndView modelAndView) {
        GeneralProductBean bean = loanProduct.getGeneralDetails();

        populateProductDetails(bean, modelAndView);

        // loan product specific
        if (loanProduct.isMultiCurrencyEnabled()) {
            String currencyCode = loanProduct.getCurrencyOptions().get(loanProduct.getSelectedCurrency());
            modelAndView.addObject("currencyCode", currencyCode);
        }
        String interestRateCalculation = loanProduct.getInterestRateCalculationTypeOptions().get(loanProduct.getSelectedInterestRateCalculationType());

        List<String> fees = populateFees(loanProduct);

        List<String> funds = populateFunds(loanProduct);

        // accounting
        String principalGlCode = loanProduct.getPrincipalGeneralLedgerOptions().get(loanProduct.getSelectedPrincipal());
        String interestGlCode = loanProduct.getInterestGeneralLedgerOptions().get(loanProduct.getSelectedInterest());

        modelAndView.addObject("interestRateCalculation", interestRateCalculation);
        modelAndView.addObject("fees", fees);
        modelAndView.addObject("funds", funds);

        modelAndView.addObject("principalGlCode", principalGlCode);
        modelAndView.addObject("interestGlCode", interestGlCode);
    }

    private List<String> populateFunds(LoanProductFormBean loanProduct) {

        List<String> funds = new ArrayList<String>();

        if (loanProduct.getSelectedFunds() != null) {
            for (String selectedFund : loanProduct.getSelectedFunds()) {
                if (loanProduct.getApplicableFundOptions().containsKey(selectedFund)) {
                    funds.add(loanProduct.getApplicableFundOptions().get(selectedFund));
                } else if (loanProduct.getSelectedFundOptions().containsKey(selectedFund)) {
                    funds.add(loanProduct.getSelectedFundOptions().get(selectedFund));
                }
            }
        }
        return funds;
    }

    private List<String> populateFees(LoanProductFormBean loanProduct) {
        List<String> fees = new ArrayList<String>();
        if (loanProduct.getSelectedFees() != null) {
            for (String selectedFee : loanProduct.getSelectedFees()) {
                if (loanProduct.getApplicableFeeOptions().containsKey(selectedFee)) {
                    fees.add(loanProduct.getApplicableFeeOptions().get(selectedFee));
                } else if (loanProduct.getSelectedFeeOptions().containsKey(selectedFee)) {
                    fees.add(loanProduct.getSelectedFeeOptions().get(selectedFee));
                }
            }
        }
        return fees;
    }

}
