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

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.dto.domain.LoanProductRequest;
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
@RequestMapping("/editLoanProduct")
@SessionAttributes("loanProduct")
public class EditLoanProductFormController {

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    @Autowired
    private LocalValidatorFactoryBean validator;

    private LoanProductFormBeanAssembler loanProductFormBeanAssembler = new LoanProductFormBeanAssembler();
    private LoanProductFormBeanValidator formBeanValidator = new LoanProductFormBeanValidator();

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

        LoanProductFormBean loanProductFormBean = loanProductFormBeanAssembler.populateWithReferenceData(referenceData);

        loanProductFormBeanAssembler.populateWithLoanProductDetails(loanProductFormBean, loanProductRequest);

        return loanProductFormBean;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
            @ModelAttribute("loanProduct") @Valid LoanProductFormBean loanProductFormBean,
            BindingResult result, SessionStatus status) {

        String viewName = "redirect:/previewLoanProducts.ftl?editFormview=editLoanProduct";

        formBeanValidator.setValidator(validator);
        formBeanValidator.validate(loanProductFormBean, result);

        if (StringUtils.isNotBlank(cancel)) {
            viewName = REDIRECT_TO_ADMIN_SCREEN;
            status.setComplete();
        } else if (result.hasErrors()) {
            viewName = "editLoanProduct";
        }

        return viewName;
    }

    public void setLoanProductFormBeanAssembler(LoanProductFormBeanAssembler loanProductFormBeanAssembler) {
        this.loanProductFormBeanAssembler = loanProductFormBeanAssembler;
    }

    public void setFormBeanValidator(LoanProductFormBeanValidator formBeanValidator) {
        this.formBeanValidator = formBeanValidator;
    }
}