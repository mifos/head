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

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.dto.domain.SavingsProductDto;
import org.mifos.service.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/previewSavingsProducts")
@SessionAttributes(value = {"savingsProduct", "product"})
public class SavingsProductPreviewController {

    private static final String REDIRECT_TO_ADMIN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String EDIT_PARAM = "EDIT";

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    public SavingsProductPreviewController(final AdminServiceFacade adminServiceFacade) {
        this.adminServiceFacade = adminServiceFacade;
    }

    protected SavingsProductPreviewController() {
        // spring auto wiring
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showPopulatedForm(@ModelAttribute("savingsProduct") SavingsProductFormBean savingsProduct,
                                        @RequestParam(value = "editFormview", required = false) String editFormview) {
        ModelAndView modelAndView = new ModelAndView("previewSavingsProducts");
        modelAndView.addObject("savingsProduct", savingsProduct);
        modelAndView.addObject("editFormview", editFormview);

        GeneralProductBean bean = savingsProduct.getGeneralDetails();
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

        String depositType = savingsProduct.getDepositTypeOptions().get(savingsProduct.getSelectedDepositType());

        String appliesTo = "";
        if (StringUtils.isNotBlank(savingsProduct.getSelectedGroupSavingsApproach())) {
            appliesTo = savingsProduct.getGroupSavingsApproachOptions().get(savingsProduct.getSelectedGroupSavingsApproach());
        }

        String interestCalculationUsed = savingsProduct.getInterestCaluclationOptions().get(savingsProduct.getSelectedInterestCalculation());

        String interestCalculationTimePeriod = savingsProduct.getFrequencyPeriodOptions().get(savingsProduct.getSelectedFequencyPeriod());
        String depositGlCode = savingsProduct.getPrincipalGeneralLedgerOptions().get(savingsProduct.getSelectedPrincipalGlCode());
        String interestGlCode = savingsProduct.getInterestGeneralLedgerOptions().get(savingsProduct.getSelectedInterestGlCode());

        modelAndView.addObject("categoryName", categoryName);
        modelAndView.addObject("startDateFormatted", startDateFormatted);
        modelAndView.addObject("endDateFormatted", endDateFormatted);
        modelAndView.addObject("applicableTo", applicableTo);
        modelAndView.addObject("depositType", depositType);
        modelAndView.addObject("appliesTo", appliesTo);
        modelAndView.addObject("interestCalculationUsed", interestCalculationUsed);
        modelAndView.addObject("interestCalculationTimePeriod", interestCalculationTimePeriod);
        modelAndView.addObject("depositGlCode", depositGlCode);
        modelAndView.addObject("interestGlCode", interestGlCode);

        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
            @RequestParam(value = EDIT_PARAM, required = false) String edit,
            @RequestParam(value = "editFormview", required = false) String editFormview,
            @ModelAttribute("savingsProduct") SavingsProductFormBean savingsProduct,
            BindingResult result) {

        ModelAndView modelAndView = new ModelAndView(REDIRECT_TO_ADMIN);

        if (StringUtils.isNotBlank(cancel)) {
            modelAndView.setViewName(REDIRECT_TO_ADMIN);
        } else if (StringUtils.isNotBlank(edit)) {
            modelAndView.setViewName(editFormview);
            modelAndView.addObject("savingsProduct", savingsProduct);
        } else if (result.hasErrors()) {
            modelAndView.setViewName("previewSavingsProducts");
            modelAndView.addObject("savingsProduct", savingsProduct);
        } else {

            PrdOfferingDto product;

            if (editFormview.equalsIgnoreCase("defineSavingsProduct")) {

                try {
                    SavingsProductDto savingsProductRequest = new SavingsProductFormBeanAssembler()
                            .assembleSavingsProductRequest(savingsProduct);
                    product = this.adminServiceFacade.createSavingsProduct(savingsProductRequest);

                    modelAndView.setViewName("redirect:/confirmSavingsProduct.ftl");
                    modelAndView.addObject("product", product);
                } catch (BusinessRuleException e) {
                    ObjectError error = new ObjectError("savingsProduct", new String[] { e.getMessageKey() },
                            new Object[] {}, "Error: Problem persisting savings product.");
                    result.addError(error);
                    modelAndView.setViewName("previewSavingsProducts");
                    modelAndView.addObject("savingsProduct", savingsProduct);
                }
            } else if (editFormview.equalsIgnoreCase("editSavingsProduct")) {

                SavingsProductDto savingsProductRequest = new SavingsProductFormBeanAssembler().assembleSavingsProductRequest(savingsProduct);
                product = this.adminServiceFacade.updateSavingsProduct(savingsProductRequest);

                modelAndView.setViewName("redirect:/viewEditSavingsProduct.ftl?productId=" + product.getPrdOfferingId());
                modelAndView.addObject("product", product);
            }

        }
        return modelAndView;
    }
}