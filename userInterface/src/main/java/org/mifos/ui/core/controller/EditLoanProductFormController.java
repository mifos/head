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

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.dto.domain.LoanProductRequest;
import org.mifos.dto.domain.ProductDetailsDto;
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

@Controller
@RequestMapping("/editLoanProduct")
@SessionAttributes("loanProduct")
public class EditLoanProductFormController {

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    protected EditLoanProductFormController(){
        //for spring autowiring
    }

    public EditLoanProductFormController(final AdminServiceFacade adminServicefacade){
        this.adminServiceFacade=adminServicefacade;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("loanProduct")
    public LoanProductFormBean showPopulatedForm(@RequestParam(value = "productId", required = true) Integer productId) {

        LoanProductFormDto referenceData = this.adminServiceFacade.retrieveLoanProductFormReferenceData();
        LoanProductRequest loanProductRequest = adminServiceFacade.retrieveLoanProductDetails(productId);

        LoanProductFormBean loanProductFormBean = new LoanProductFormBeanAssembler().populateWithReferenceData(referenceData);

        ProductDetailsDto productDto = loanProductRequest.getProductDetails();

        GeneralProductBean productBean = new GeneralProductBeanAssembler().assembleFrom(loanProductFormBean.getGeneralDetails(), productDto);
        loanProductFormBean.setGeneralDetails(productBean);

        loanProductFormBean.setIncludeInLoanCycleCounter(loanProductRequest.isIncludeInLoanCycleCounter());
        loanProductFormBean.setWaiverInterest(loanProductRequest.isWaiverInterest());

        loanProductFormBean.setLoanAmountSameForAllLoans(new SameForAllLoanBean());
        loanProductFormBean.setLoanAmountByLastLoanAmount(populateByLastLoanAmountBeans());
        loanProductFormBean.setLoanAmountByLoanCycle(populateByLoanCycleBeans());

        loanProductFormBean.setSelectedInterestRateCalculationType(loanProductRequest.getInterestRateType().toString());
        loanProductFormBean.setMinInterestRate(loanProductRequest.getInterestRateRange().getMin().doubleValue());
        loanProductFormBean.setMaxInterestRate(loanProductRequest.getInterestRateRange().getMax().doubleValue());
        loanProductFormBean.setDefaultInterestRate(loanProductRequest.getInterestRateRange().getTheDefault().doubleValue());

        loanProductFormBean.setInstallmentFrequencyPeriod(loanProductRequest.getRepaymentDetails().getFrequencyType().toString());
        loanProductFormBean.setInstallmentFrequencyRecurrenceEvery(loanProductRequest.getRepaymentDetails().getRecurs());

        loanProductFormBean.setSelectedInstallmentsCalculationType(loanProductRequest.getRepaymentDetails().getInstallmentCalculationDetails().getCalculationType().toString());
        SameForAllLoanBean installmentsSameForAllLoans = new SameForAllLoanBean();
        installmentsSameForAllLoans.setMin(Double.valueOf("1"));
        loanProductFormBean.setInstallmentsSameForAllLoans(installmentsSameForAllLoans);
        loanProductFormBean.setInstallmentsByLastLoanAmount(populateByLastLoanAmountBeans());
        loanProductFormBean.setInstallmentsByLoanCycle(populateByLoanCycleBeans());

        loanProductFormBean.setSelectedGracePeriodType(loanProductRequest.getRepaymentDetails().getGracePeriodType().toString());
        loanProductFormBean.setGracePeriodDurationInInstallments(loanProductRequest.getRepaymentDetails().getGracePeriodDuration());

        if (!loanProductRequest.getApplicableFees().isEmpty()) {
            String[] selectedFees = new String[loanProductRequest.getApplicableFees().size()];
            int index = 0;
            for (Integer feeId : loanProductRequest.getApplicableFees()) {
                selectedFees[index] = feeId.toString();
                index++;
            }
            loanProductFormBean.setSelectedFees(selectedFees);
        }

        if (!loanProductRequest.getAccountDetails().getApplicableFunds().isEmpty()) {

            String[] selectedFunds = new String[loanProductRequest.getAccountDetails().getApplicableFunds().size()];
            int index = 0;
            for (Integer fundId : loanProductRequest.getAccountDetails().getApplicableFunds()) {
                selectedFunds[index] = fundId.toString();
                index++;
            }
            loanProductFormBean.setSelectedFunds(selectedFunds);
        }

        loanProductFormBean.setSelectedInterest(loanProductRequest.getAccountDetails().getInterestGlCodeId().toString());
        loanProductFormBean.setSelectedPrincipal(loanProductRequest.getAccountDetails().getPrincipalClCodeId().toString());

        return loanProductFormBean;
    }

    private ByLoanCycleBean[] populateByLoanCycleBeans() {

        ByLoanCycleBean zeroCycle = new ByLoanCycleBean(1);
        ByLoanCycleBean oneCycle = new ByLoanCycleBean(2);
        ByLoanCycleBean twoCycle = new ByLoanCycleBean(3);
        ByLoanCycleBean threeCycle = new ByLoanCycleBean(4);
        ByLoanCycleBean fourCycle = new ByLoanCycleBean(5);
        ByLoanCycleBean greaterThanFourCycle = new ByLoanCycleBean(6);

        return new ByLoanCycleBean[] {zeroCycle,oneCycle, twoCycle, threeCycle, fourCycle, greaterThanFourCycle};
    }

    private ByLastLoanAmountBean[] populateByLastLoanAmountBeans() {
        ByLastLoanAmountBean zero = new ByLastLoanAmountBean();
        zero.setLower(Double.valueOf("0"));
        ByLastLoanAmountBean one = new ByLastLoanAmountBean();
        ByLastLoanAmountBean two = new ByLastLoanAmountBean();
        ByLastLoanAmountBean three = new ByLastLoanAmountBean();
        ByLastLoanAmountBean four = new ByLastLoanAmountBean();
        ByLastLoanAmountBean five = new ByLastLoanAmountBean();

        return new ByLastLoanAmountBean[] {zero, one, two, three, four, five};
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
            @ModelAttribute("savingsProduct") @Valid LoanProductFormBean loanProductFormBean, BindingResult result, SessionStatus status) {

        String viewName = "redirect:/previewLoanProduct.ftl?editFormview=editLoanProduct";

        if (StringUtils.isNotBlank(cancel)) {
            viewName = REDIRECT_TO_ADMIN_SCREEN;
            status.setComplete();
        } else if (result.hasErrors()) {
//            new SavingsProductValidator().validateGroup(loanProductFormBean, result);
            viewName = "editLoanProduct";
        } else {
//            new SavingsProductValidator().validateGroup(loanProductFormBean, result);
            if (result.hasErrors()) {
                viewName = "editLoanProduct";
            }
        }

        return viewName;
    }
}