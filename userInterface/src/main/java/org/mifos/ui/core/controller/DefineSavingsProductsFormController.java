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
import org.mifos.config.servicefacade.ConfigurationServiceFacade;
import org.mifos.config.servicefacade.dto.AccountingConfigurationDto;
import org.mifos.dto.screen.SavingsProductFormDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/defineSavingsProduct")
@SessionAttributes("savingsProduct")

public class DefineSavingsProductsFormController {

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";

    @Autowired
    private AdminServiceFacade adminServiceFacade;
    @Autowired
    private ConfigurationServiceFacade configurationServiceFacade;
    
    private AccountingConfigurationDto configurationDto;
    
    protected DefineSavingsProductsFormController() {
        //for spring autowiring
    }

    public DefineSavingsProductsFormController(final AdminServiceFacade adminServicefacade) {
        this.adminServiceFacade = adminServicefacade;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showPopulatedForm() {
    	ModelAndView modelAndView = new ModelAndView("defineSavingsProduct");
        SavingsProductFormDto referenceData = this.adminServiceFacade.retrieveSavingsProductFormReferenceData();
        SavingsProductFormBean savingsProduct = new SavingsProductFormBeanAssembler().assembleReferenceData(referenceData);

        configurationDto = this.configurationServiceFacade.getAccountingConfiguration();
        
        Double zero = Double.valueOf("0");
        savingsProduct.setAmountForDeposit(zero);
        savingsProduct.setMaxWithdrawalAmount(zero);
        savingsProduct.setMinBalanceRequiredForInterestCalculation("0");
        modelAndView.addObject("savingsProduct", savingsProduct);
        modelAndView.addObject("GLCodeMode", configurationDto.getGlCodeMode());
        return modelAndView;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        LazyBindingErrorProcessor errorProcessor = new LazyBindingErrorProcessor();
        binder.setValidator(new SavingsProductFormValidator(errorProcessor,configurationServiceFacade));
        binder.setBindingErrorProcessor(errorProcessor);
    }

    @RequestMapping(value ="procesImportedTransactionUndo", method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
                                    @ModelAttribute("savingsProduct") @Valid SavingsProductFormBean savingsProductFormBean, BindingResult result, SessionStatus status) {
    	ModelAndView modelAndView = new ModelAndView("redirect:/previewSavingsProducts.ftl?editFormview=defineSavingsProduct");
    	configurationDto = this.configurationServiceFacade.getAccountingConfiguration();
    	modelAndView.addObject("GLCodeMode", configurationDto.getGlCodeMode());
        if (StringUtils.isNotBlank(cancel)) {
        	modelAndView.setViewName(REDIRECT_TO_ADMIN_SCREEN);
        	status.setComplete();
        } else if (result.hasErrors()) {
        	modelAndView.setViewName("defineSavingsProduct");
        }

        return modelAndView;
    }
}