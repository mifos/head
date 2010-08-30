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
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/previewLoanProducts")
@SessionAttributes(value = {"loanProduct", "product"})
public class LoanProductPreviewController {

    private static final String REDIRECT_TO_ADMIN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String EDIT_PARAM = "EDIT";

//    @Autowired
//    private AdminServiceFacade adminServiceFacade;
//
//    public LoanProductPreviewController(final AdminServiceFacade adminServiceFacade) {
//        this.adminServiceFacade = adminServiceFacade;
//    }

    protected LoanProductPreviewController() {
        // spring auto wiring
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showPopulatedForm(@ModelAttribute("loanProduct") LoanProductFormBean loanProduct,
                                        @RequestParam(value = "editFormview", required = false) String editFormview) {
        ModelAndView modelAndView = new ModelAndView("previewLoanProducts");
        modelAndView.addObject("loanProduct", loanProduct);
        modelAndView.addObject("editFormview", editFormview);

        populateModelAndViewForPreview(loanProduct, modelAndView);

        return modelAndView;
    }

    private void populateModelAndViewForPreview(LoanProductFormBean loanProduct, ModelAndView modelAndView) {
        GeneralProductBean bean = loanProduct.getGeneralDetails();

        new ProductModelAndViewPopulator().populateProductDetails(bean, modelAndView);

        // loan product specific
        String interestRateCalculation = loanProduct.getInterestRateCalculationTypeOptions().get(loanProduct.getSelectedInterestRateCalculationType());

        // accounting
        String principalGlCode = loanProduct.getPrincipalGeneralLedgerOptions().get(loanProduct.getSelectedPrincipal());
        String interestGlCode = loanProduct.getInterestGeneralLedgerOptions().get(loanProduct.getSelectedInterest());

        modelAndView.addObject("interestRateCalculation", interestRateCalculation);

        modelAndView.addObject("principalGlCode", principalGlCode);
        modelAndView.addObject("interestGlCode", interestGlCode);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
            @RequestParam(value = EDIT_PARAM, required = false) String edit,
            @RequestParam(value = "editFormview", required = false) String editFormview,
            @ModelAttribute("loanProduct") LoanProductFormBean loanProduct,
            BindingResult result) {

        ModelAndView modelAndView = new ModelAndView(REDIRECT_TO_ADMIN);

        if (StringUtils.isNotBlank(cancel)) {
            modelAndView.setViewName(REDIRECT_TO_ADMIN);
        } else if (StringUtils.isNotBlank(edit)) {
            modelAndView.setViewName(editFormview);
            modelAndView.addObject("loanProduct", loanProduct);
        } else if (result.hasErrors()) {
            modelAndView.setViewName("previewloanProducts");
            modelAndView.addObject("loanProduct", loanProduct);
            modelAndView.addObject("editFormview", editFormview);
            populateModelAndViewForPreview(loanProduct, modelAndView);
        }

//            PrdOfferingDto product;
//            SavingsProductDto savingsProductRequest = new SavingsProductFormBeanAssembler().assembleSavingsProductRequest(loanProduct);
//            if (editFormview.equalsIgnoreCase("defineSavingsProduct")) {
//
//                try {
//                    product = this.adminServiceFacade.createSavingsProduct(savingsProductRequest);
//
//                    modelAndView.setViewName("redirect:/confirmSavingsProduct.ftl");
//                    modelAndView.addObject("product", product);
//                } catch (BusinessRuleException e) {
//                    handleBusinessRuleViolation(editFormview, loanProduct, result, modelAndView, e.getMessageKey());
//                }
//            } else if (editFormview.equalsIgnoreCase("editSavingsProduct")) {
//
//                try {
//                    product = this.adminServiceFacade.updateSavingsProduct(savingsProductRequest);
//
//                    modelAndView.setViewName("redirect:/viewEditSavingsProduct.ftl?productId="+ product.getPrdOfferingId());
//                    modelAndView.addObject("product", product);
//                } catch (BusinessRuleException e) {
//                    handleBusinessRuleViolation(editFormview, loanProduct, result, modelAndView, e.getMessageKey());
//                }
//            }
        return modelAndView;
    }

//    private void handleBusinessRuleViolation(String editFormview, LoanProductFormBean loanProduct,
//            BindingResult result, ModelAndView modelAndView, String messageKey) {
//        ObjectError error = new ObjectError("savingsProduct", new String[] { messageKey },
//                new Object[] {}, "Error: Problem persisting savings product.");
//        result.addError(error);
//        modelAndView.setViewName("previewSavingsProducts");
//        modelAndView.addObject("savingsProduct", loanProduct);
//        modelAndView.addObject("editFormview", editFormview);
//        populateModelAndViewForPreview(loanProduct, modelAndView);
//    }
}