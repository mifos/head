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
import org.mifos.dto.domain.ProductDetailsDto;
import org.mifos.dto.domain.SavingsProductDto;
import org.mifos.dto.screen.SavingsProductFormDto;
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
@RequestMapping("/editSavingsProduct")
@SessionAttributes("savingsProduct")
public class EditSavingsProductsFormController {

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    protected EditSavingsProductsFormController(){
        //for spring autowiring
    }

    public EditSavingsProductsFormController(final AdminServiceFacade adminServicefacade){
        this.adminServiceFacade=adminServicefacade;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("savingsProduct")
    public SavingsProductFormBean showPopulatedForm(@RequestParam(value = "productId", required = true) Integer productId) {

        SavingsProductFormDto referenceData = this.adminServiceFacade.retrieveSavingsProductFormReferenceData();
        SavingsProductDto savingsProductDto = adminServiceFacade.retrieveSavingsProductDetails(productId);
        SavingsProductFormBean savingsProductBean = new SavingsProductFormBeanAssembler().assembleReferenceData(referenceData);

        GeneralProductBean productBean = savingsProductBean.getGeneralDetails();
        ProductDetailsDto productDto = savingsProductDto.getProductDetails();

        productBean.setId(productDto.getId());
        productBean.setName(productDto.getName());
        productBean.setShortName(productDto.getShortName());
        productBean.setDescription(productDto.getDescription());
        productBean.setSelectedCategory(productDto.getCategory().toString());
        productBean.setStartDateDay(productDto.getStartDate().getDayOfMonth());
        productBean.setStartDateMonth(productDto.getStartDate().getMonthOfYear());
        productBean.setStartDateYear(Integer.valueOf(productDto.getStartDate().getYearOfEra()).toString());

        if (productDto.getEndDate() != null) {
            productBean.setEndDateDay(productDto.getEndDate().getDayOfMonth());
            productBean.setEndDateMonth(productDto.getEndDate().getMonthOfYear());
            productBean.setEndDateYear(Integer.valueOf(productDto.getEndDate().getYearOfEra()).toString());
        }

        productBean.setSelectedApplicableFor(productDto.getApplicableFor().toString());

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
        savingsProductBean.setInterestPostingMonthlyFrequency(savingsProductDto.getInterestPostingMonthlyFrequency());
        savingsProductBean.setMinBalanceRequiredForInterestCalculation(savingsProductDto.getMinBalanceForInterestCalculation().toString());

        savingsProductBean.setSelectedPrincipalGlCode(savingsProductDto.getDepositGlCode().toString());
        savingsProductBean.setSelectedInterestGlCode(savingsProductDto.getInterestGlCode().toString());

        return savingsProductBean;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
            @ModelAttribute("savingsProduct") @Valid SavingsProductFormBean savingsProductFormBean, BindingResult result, SessionStatus status) {

        String viewName = "redirect:/previewSavingsProducts.ftl?editFormview=editSavingsProduct";

        if (StringUtils.isNotBlank(cancel)) {
            viewName = REDIRECT_TO_ADMIN_SCREEN;
            status.setComplete();
        } else if (result.hasErrors()) {
            new SavingsProductValidator().validateGroup(savingsProductFormBean, result);
            viewName = "editSavingsProduct";
        } else {
            new SavingsProductValidator().validateGroup(savingsProductFormBean, result);
            if (result.hasErrors()) {
                viewName = "editSavingsProduct";
            }
        }

        return viewName;
    }
}