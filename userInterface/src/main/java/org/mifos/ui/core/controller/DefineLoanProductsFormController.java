/*
 * Copyright Grameen Foundation USA
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
import org.mifos.dto.screen.LoanProductFormDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@RequestMapping("/defineLoanProducts")
@SessionAttributes("loanProduct")
public class DefineLoanProductsFormController {

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    @Autowired
    private LocalValidatorFactoryBean validator;

    private LoanProductFormBeanValidator loanProductFormBeanValidator = new LoanProductFormBeanValidator();
    private LoanProductFormBeanAssembler loanProductFormBeanAssembler = new LoanProductFormBeanAssembler();

    protected DefineLoanProductsFormController(){
        //for spring autowiring
    }

    public DefineLoanProductsFormController(final AdminServiceFacade adminServiceFacade){
        this.adminServiceFacade = adminServiceFacade;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("loanProduct")
    public LoanProductFormBean showPopulatedForm() {

        LoanProductFormDto loanProductRefData = this.adminServiceFacade.retrieveLoanProductFormReferenceData();

        LoanProductFormBean loanProductFormBean = loanProductFormBeanAssembler.populateWithReferenceData(loanProductRefData);

        loanProductFormBean.setIncludeInLoanCycleCounter(false);
        loanProductFormBean.setInstallmentFrequencyRecurrenceEvery(Integer.valueOf(1));
        loanProductFormBean.setGracePeriodDurationInInstallments(Integer.valueOf(0));

        loanProductFormBean.setLoanAmountByLastLoanAmount(loanProductFormBean.createByLastLoanAmountBeans());
        loanProductFormBean.setLoanAmountByLoanCycle(loanProductFormBean.createByLoanCycleBeans());

        SameForAllLoanBean installmentsSameForAllLoans = new SameForAllLoanBean();
        installmentsSameForAllLoans.setMin(Double.valueOf("1"));
        loanProductFormBean.setInstallmentsSameForAllLoans(installmentsSameForAllLoans);
        loanProductFormBean.setInstallmentsByLastLoanAmount(loanProductFormBean.createByLastLoanAmountBeans());
        loanProductFormBean.setInstallmentsByLoanCycle(loanProductFormBean.createByLoanCycleBeans());

        return loanProductFormBean;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
            @ModelAttribute("loanProduct") @Valid LoanProductFormBean loanProductFormBean, BindingResult result, SessionStatus status) {

        String viewName = "redirect:/previewLoanProducts.ftl?editFormview=defineLoanProducts";

        loanProductFormBeanValidator.setValidator(validator);
        loanProductFormBeanValidator.validate(loanProductFormBean, result);

        if (StringUtils.isNotBlank(cancel)) {
            viewName = REDIRECT_TO_ADMIN_SCREEN;
            status.setComplete();
        } else if (result.hasErrors()) {
            viewName = "defineLoanProducts";
            loanProductFormBean.resetMultiSelectListBoxes();
        }

        return viewName;
    }

    public void setLoanProductFormBeanAssembler(LoanProductFormBeanAssembler loanProductFormBeanAssembler) {
        this.loanProductFormBeanAssembler = loanProductFormBeanAssembler;
    }

    public void setLoanProductFormBeanValidator(LoanProductFormBeanValidator loanProductFormBeanValidator) {
        this.loanProductFormBeanValidator = loanProductFormBeanValidator;
    }
}