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

import org.joda.time.DateTime;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@RequestMapping("/defineLoanProducts")
@SessionAttributes("loanProduct")
public class DefineLoanProductsController {

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    protected DefineLoanProductsController(){
        //for spring autowiring
    }
    public DefineLoanProductsController(final AdminServiceFacade adminServicefacade){
        this.adminServiceFacade = adminServicefacade;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("loanProduct")
    public LoanProductFormBean showPopulatedForm() {

        LoanProductFormBean loanProductFormBean = new LoanProductFormBean();
        loanProductFormBean.setName("bindedName");
        loanProductFormBean.setShortName("1234");
        loanProductFormBean.setDescription("basic description of the product");

        Map<String, String> categoryOptions = new LinkedHashMap<String, String>();
        categoryOptions.put("1", "Other");

        loanProductFormBean.setCategoryOptions(categoryOptions);
        loanProductFormBean.setSelectedCategory("1");

        DateTime today = new DateTime();
        loanProductFormBean.setStartDateDay(today.getDayOfMonth());
        loanProductFormBean.setStartDateMonth(today.getMonthOfYear());
        loanProductFormBean.setStartDateYear(""+today.getYearOfEra());

        DateTime aYearFromNow = new DateTime();
        loanProductFormBean.setEndDateDay(aYearFromNow.getDayOfMonth());
        loanProductFormBean.setEndDateMonth(aYearFromNow.getMonthOfYear());
        loanProductFormBean.setEndDateYear(""+aYearFromNow.getYearOfEra());

        Map<String, String> applicableForOptions = new LinkedHashMap<String, String>();
        applicableForOptions.put("1", "Clients");
        applicableForOptions.put("2", "Groups");

        loanProductFormBean.setApplicableForOptions(applicableForOptions);
        loanProductFormBean.setSelectedApplicableFor("2");

        loanProductFormBean.setIncludeInLoanCycleCounter(true);

        Map<String, String> calculationTypeOptions = new LinkedHashMap<String, String>();
        calculationTypeOptions.put("1", "Same for all loans");
        calculationTypeOptions.put("2", "By last loan amount");
        calculationTypeOptions.put("3", "By loan cycle");

        loanProductFormBean.setLoanAmountCalculationTypeOptions(calculationTypeOptions);
        loanProductFormBean.setLoanAmountCalculationType("1");

        loanProductFormBean.setMinLoanAmount(Double.valueOf("25.5"));
        loanProductFormBean.setMaxLoanAmount(Double.valueOf("250.0"));
        loanProductFormBean.setDefaultLoanAmount(Double.valueOf("101.5"));

        Map<String, String> interestRateCalculationTypeOptions = new LinkedHashMap<String, String>();
        interestRateCalculationTypeOptions.put("1", "Flat");
        interestRateCalculationTypeOptions.put("2", "Declining Balance");
        interestRateCalculationTypeOptions.put("3", "Declining Balance-Equal Principal Installment");

        loanProductFormBean.setInterestRateCalculationTypeOptions(interestRateCalculationTypeOptions);
        loanProductFormBean.setSelectedInterestRateCalculationType("2");

        loanProductFormBean.setMinInterestRate(Double.valueOf("1.0"));
        loanProductFormBean.setMaxInterestRate(Double.valueOf("10.0"));
        loanProductFormBean.setDefaultInterestRate(Double.valueOf("5.0"));

        Map<String, String> installmentFrequencyPeriodOptions = new LinkedHashMap<String, String>();
        installmentFrequencyPeriodOptions.put("1", "Weekly");
        installmentFrequencyPeriodOptions.put("2", "Monthly");
        loanProductFormBean.setInstallmentFrequencyPeriodOptions(installmentFrequencyPeriodOptions);
        loanProductFormBean.setInstallmentFrequencyPeriod("1");
        loanProductFormBean.setInstallmentFrequencyRecurrenceEvery(2);

        loanProductFormBean.setInstallmentsCalculationTypeOptions(calculationTypeOptions);
        loanProductFormBean.setInstallmentsCalculationType("1");

        loanProductFormBean.setMinInstallments(1);
        loanProductFormBean.setMaxInstallments(12);
        loanProductFormBean.setDefaultInstallments(12);


        Map<String, String> gracePeriodTypeOptions = new LinkedHashMap<String, String>();
        gracePeriodTypeOptions.put("1", "None");
        gracePeriodTypeOptions.put("2", "Grace on all repayments");
        gracePeriodTypeOptions.put("3", "Principal only grace");
        loanProductFormBean.setGracePeriodTypeOptions(gracePeriodTypeOptions);

        loanProductFormBean.setSelectedGracePeriodType("2");

        loanProductFormBean.setGracePeriodDurationInInstallments(0);

        Map<String, String> applicableFeeOptions = new LinkedHashMap<String, String>();
        applicableFeeOptions.put("1", "fee1");
        applicableFeeOptions.put("2", "fee2");
        loanProductFormBean.setApplicableFeeOptions(applicableFeeOptions);

        Map<String, String> selectedFeeOptions = new LinkedHashMap<String, String>();
        selectedFeeOptions.put("3", "selectedfee");
        loanProductFormBean.setSelectedFeeOptions(selectedFeeOptions);

        Map<String, String> applicableFundOptions = new LinkedHashMap<String, String>();
        applicableFundOptions.put("1", "fund1");
        applicableFundOptions.put("2", "fund2");
        loanProductFormBean.setApplicableFundOptions(applicableFundOptions);

        Map<String, String> selectedFundOptions = new LinkedHashMap<String, String>();
        selectedFundOptions.put("3", "selectedfund");
        loanProductFormBean.setSelectedFundOptions(selectedFundOptions);

        Map<String, String> interestGeneralLedgerOptions = new LinkedHashMap<String, String>();
        interestGeneralLedgerOptions.put("5123", "5123");
        loanProductFormBean.setInterestGeneralLedgerOptions(interestGeneralLedgerOptions);
        loanProductFormBean.setSelectedInterest("5123");

        Map<String, String> principalGeneralLedgerOptions = new LinkedHashMap<String, String>();
        principalGeneralLedgerOptions.put("5199", "5199");
        loanProductFormBean.setPrincipalGeneralLedgerOptions(principalGeneralLedgerOptions);
        loanProductFormBean.setSelectedPrincipal("5199");

        return loanProductFormBean;
    }
}