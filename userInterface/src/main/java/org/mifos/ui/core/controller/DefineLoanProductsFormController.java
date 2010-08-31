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

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.dto.domain.LoanProductRequest;
import org.mifos.dto.domain.MinMaxDefaultDto;
import org.mifos.dto.domain.RepaymentDetailsDto;
import org.mifos.dto.screen.AccountingDetailsDto;
import org.mifos.dto.screen.LoanAmountDetails;
import org.mifos.dto.screen.LoanProductDetails;
import org.mifos.dto.screen.LoanProductFormDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/defineLoanProducts")
@SessionAttributes("loanProduct")
public class DefineLoanProductsFormController {

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";

    @Autowired
    private AdminServiceFacade adminServiceFacade;
    private LoanProductAssembler loanProductAssembler = new LoanProductAssembler();

    protected DefineLoanProductsFormController(){
        //for spring autowiring
    }

    public DefineLoanProductsFormController(final AdminServiceFacade adminServicefacade){
        this.adminServiceFacade=adminServicefacade;
    }

    @SuppressWarnings("PMD")
    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("loanProduct")
    public LoanProductFormBean showPopulatedForm() {

        LoanProductFormDto loanProductRefData = this.adminServiceFacade.retrieveLoanProductFormReferenceData();

        LoanProductFormBean loanProductFormBean = loanProductAssembler.populateWithReferenceData(loanProductRefData);

        DateTime startDate = new DateTime();
        loanProductFormBean.setStartDateDay(startDate.getDayOfMonth());
        loanProductFormBean.setStartDateMonth(startDate.getMonthOfYear());
        loanProductFormBean.setStartDateYear(Integer.valueOf(startDate.getYearOfEra()).toString());

        SameForAllLoanBean installmentsSameForAllLoans = new SameForAllLoanBean();
        installmentsSameForAllLoans.setMin(Double.valueOf("1"));
        loanProductFormBean.setInstallmentsSameForAllLoans(installmentsSameForAllLoans);

        loanProductFormBean.setIncludeInLoanCycleCounter(false);
        loanProductFormBean.setInstallmentFrequencyRecurrenceEvery(Integer.valueOf(1));
        loanProductFormBean.setGracePeriodDurationInInstallments(Integer.valueOf(0));


        return loanProductFormBean;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
            @ModelAttribute("loanProduct") @Valid LoanProductFormBean loanProductFormBean, BindingResult result, SessionStatus status) {

        String viewName = REDIRECT_TO_ADMIN_SCREEN;

        if (StringUtils.isNotBlank(cancel)) {
            viewName = REDIRECT_TO_ADMIN_SCREEN;
            status.setComplete();
        } else if (result.hasErrors()) {
            viewName = "defineLoanProducts";
        } else {

            LoanProductRequest loanProduct = translateFrom(loanProductFormBean);

            adminServiceFacade.createLoanProduct(loanProduct);
            status.setComplete();
        }

        return viewName;
    }

    private LoanProductRequest translateFrom(LoanProductFormBean loanProductFormBean) {

        LoanProductDetails loanProductDetails = translateToLoanProductDetails(loanProductFormBean);

        LoanAmountDetails loanAmountDetails = translateToLoanAmountDetails(loanProductFormBean);

        Integer interestRateType = Integer.valueOf(loanProductFormBean.getSelectedInterestRateCalculationType());
        Double maxInterest = Double.valueOf(loanProductFormBean.getMaxInterestRate());
        Double minInterest = Double.valueOf(loanProductFormBean.getMinInterestRate());
        Double defaultInterest = Double.valueOf(loanProductFormBean.getDefaultInterestRate());
        MinMaxDefaultDto<Double> interestRateRange = new MinMaxDefaultDto<Double>(minInterest, maxInterest, defaultInterest);

        RepaymentDetailsDto repaymentDetails = translateToRepaymentDetails(loanProductFormBean);

        List<Integer> applicableFees = new ArrayList<Integer>();
        if (loanProductFormBean.getSelectedFees() != null) {
            for (String feeId : loanProductFormBean.getSelectedFees()) {
                applicableFees.add(Integer.valueOf(feeId));
            }
        }

        AccountingDetailsDto accountDetails = translateToAccountDetails(loanProductFormBean);

        return new LoanProductRequest(loanProductDetails, loanAmountDetails, interestRateType, interestRateRange, repaymentDetails, applicableFees, accountDetails);
    }

    private AccountingDetailsDto translateToAccountDetails(LoanProductFormBean loanProductFormBean) {

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

    private LoanAmountDetails translateToLoanAmountDetails(LoanProductFormBean loanProductFormBean) {
        Integer calculationType = Integer.valueOf(loanProductFormBean.getSelectedLoanAmountCalculationType());
        MinMaxDefaultDto<Double> sameForAllLoanRange = translate(loanProductFormBean.getLoanAmountSameForAllLoans());
        return new LoanAmountDetails(calculationType, sameForAllLoanRange);
    }

    private LoanProductDetails translateToLoanProductDetails(LoanProductFormBean loanProductFormBean) {
        Integer category = Integer.valueOf(loanProductFormBean.getSelectedCategory());
        Integer applicableFor = Integer.valueOf(loanProductFormBean.getSelectedApplicableFor());
        DateTime startDate = new DateTime().withDate(Integer.valueOf(loanProductFormBean.getStartDateYear()), loanProductFormBean.getStartDateMonth(), loanProductFormBean.getStartDateDay());
        DateTime endDate = null;
        if (StringUtils.isNotBlank(loanProductFormBean.getEndDateYear())) {
            endDate = new DateTime().withDate(Integer.valueOf(loanProductFormBean.getEndDateYear()), loanProductFormBean.getEndDateMonth(), loanProductFormBean.getEndDateDay());
        }

        return new LoanProductDetails(loanProductFormBean.getName(), loanProductFormBean.getShortName(), loanProductFormBean.getDescription(), category,
                startDate, endDate, applicableFor, loanProductFormBean.isIncludeInLoanCycleCounter(), loanProductFormBean.isWaiverInterest());
    }

    private RepaymentDetailsDto translateToRepaymentDetails(LoanProductFormBean loanProductFormBean) {

        Integer frequencyType = Integer.valueOf(loanProductFormBean.getInstallmentFrequencyPeriod());
        Integer recurs = Integer.valueOf(loanProductFormBean.getInstallmentFrequencyRecurrenceEvery());
        Integer calculationType = Integer.valueOf(loanProductFormBean.getSelectedInstallmentsCalculationType());
        MinMaxDefaultDto<Integer> sameForAllLoanRange = translateToMinMaxDefault(loanProductFormBean.getInstallmentsSameForAllLoans());

        Integer gracePeriodType = Integer.valueOf(loanProductFormBean.getSelectedGracePeriodType());
        Integer gracePeriodDuration = loanProductFormBean.getGracePeriodDurationInInstallments();

        return new RepaymentDetailsDto(frequencyType, recurs, calculationType, sameForAllLoanRange, gracePeriodType, gracePeriodDuration);
    }

    private MinMaxDefaultDto<Integer> translateToMinMaxDefault(SameForAllLoanBean bean) {
        return new MinMaxDefaultDto<Integer>(bean.getMin().intValue(), bean.getMax().intValue(), bean.getTheDefault().intValue());
    }

    private MinMaxDefaultDto<Double> translate(SameForAllLoanBean bean) {
        return new MinMaxDefaultDto<Double>(bean.getMin(), bean.getMax(), bean.getTheDefault());
    }

    public void setLoanProductAssembler(LoanProductAssembler loanProductAssembler) {
        this.loanProductAssembler = loanProductAssembler;
    }
}