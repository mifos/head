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
import org.mifos.dto.domain.ProductDetailsDto;
import org.mifos.dto.domain.SavingsProductDto;
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
@RequestMapping("/editSavingsProduct")
@SessionAttributes("savingsProduct")
public class EditSavingsProductsFormController {

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";

    @Autowired
    private AdminServiceFacade adminServiceFacade;
    @Autowired
    private ConfigurationServiceFacade configurationServiceFacade;
    
    private AccountingConfigurationDto configurationDto;
    
    protected EditSavingsProductsFormController(){
        //for spring autowiring
    }

    public EditSavingsProductsFormController(final AdminServiceFacade adminServicefacade){
        this.adminServiceFacade=adminServicefacade;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        LazyBindingErrorProcessor errorProcessor = new LazyBindingErrorProcessor();
        binder.setValidator(new SavingsProductFormValidator(errorProcessor, configurationServiceFacade));
        binder.setBindingErrorProcessor(errorProcessor);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showPopulatedForm(@RequestParam(value = "productId", required = true) Integer productId) {
    	ModelAndView modelAndView = new ModelAndView("editSavingsProduct");
    	
    	configurationDto = this.configurationServiceFacade.getAccountingConfiguration();
    	modelAndView.addObject("GLCodeMode", configurationDto.getGlCodeMode());
    	
        SavingsProductFormDto referenceData = this.adminServiceFacade.retrieveSavingsProductFormReferenceData();
        SavingsProductDto savingsProductDto = adminServiceFacade.retrieveSavingsProductDetails(productId);
        SavingsProductFormBean savingsProductBean = new SavingsProductFormBeanAssembler().assembleReferenceData(referenceData);
        
        ProductDetailsDto productDto = savingsProductDto.getProductDetails();
        GeneralProductBean productBean = new GeneralProductBeanAssembler().assembleFrom(savingsProductBean.getGeneralDetails(), productDto);

        savingsProductBean.setGeneralDetails(productBean);

        savingsProductBean.setSelectedDepositType(savingsProductDto.getDepositType().toString());
        savingsProductBean.setAmountForDeposit(savingsProductDto.getAmountForDeposit());
        if (savingsProductDto.getGroupMandatorySavingsType() > 0) {
            savingsProductBean.setSelectedGroupSavingsApproach(savingsProductDto.getGroupMandatorySavingsType().toString());
        } else {
            savingsProductBean.setSelectedGroupSavingsApproach("");
        }
        savingsProductBean.setMaxWithdrawalAmount(savingsProductDto.getMaxWithdrawal());

        savingsProductBean.setInterestRate(savingsProductDto.getInterestRate());
        savingsProductBean.setSelectedInterestCalculation(savingsProductDto.getInterestCalculationType().toString());
        savingsProductBean.setInterestCalculationFrequency(savingsProductDto.getInterestCalculationFrequency());
        savingsProductBean.setSelectedFequencyPeriod(savingsProductDto.getInterestCalculationFrequencyPeriod().toString());
        savingsProductBean.setInterestPostingFrequency(savingsProductDto.getInterestPostingFrequency());
        savingsProductBean.setMinBalanceRequiredForInterestCalculation(Long.toString(savingsProductDto.getMinBalanceForInterestCalculation().longValue()));

        savingsProductBean.setSelectedPrincipalGlCode(savingsProductDto.getDepositGlCode().toString());
        savingsProductBean.setSelectedInterestGlCode(savingsProductDto.getInterestGlCode().toString());
        savingsProductBean.setNotUpdateable(savingsProductDto.isOpenSavingsAccountsExist());
        
        modelAndView.addObject("savingsProduct" , savingsProductBean);
        
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
            @ModelAttribute("savingsProduct") @Valid SavingsProductFormBean savingsProductFormBean, BindingResult result, SessionStatus status) {
    	
    	ModelAndView modelAndView = new ModelAndView("redirect:/previewSavingsProducts.ftl?editFormview=editSavingsProduct");
        
    	configurationDto = this.configurationServiceFacade.getAccountingConfiguration();
    	modelAndView.addObject("GLCodeMode", configurationDto.getGlCodeMode());
    	
        if (StringUtils.isNotBlank(cancel)) {
        	modelAndView.setViewName(REDIRECT_TO_ADMIN_SCREEN);
        	status.setComplete();
        } else if (result.hasErrors()) {
            new SavingsProductValidator().validateGroup(savingsProductFormBean, result);
            modelAndView.setViewName("editSavingsProduct");
        } else {
            new SavingsProductValidator().validateGroup(savingsProductFormBean, result);
            if (result.hasErrors()) {
            	modelAndView.setViewName("editSavingsProduct");
            }
        }

        return modelAndView;
    }
}