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

import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.dto.domain.ProductTypeDto;
import org.mifos.dto.screen.ProductMixPreviewDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/defineProductMix")
@SessionAttributes("formBean")
@SuppressWarnings("PMD")
// suppressing complexity warnings for now.. keithw.
public class DefineProductMixController {

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String PREVIEW_PARAM = "preview";

    @Autowired
    private AdminServiceFacade adminServiceFacade;
    private ProductMixAssembler productMixAssembler = new ProductMixAssembler();

    protected DefineProductMixController() {
        // default contructor for spring autowiring
    }

    public DefineProductMixController(AdminServiceFacade adminServiceFacade) {
        this.adminServiceFacade = adminServiceFacade;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("formBean")
    public ProductMixFormBean showPopulatedForm() {

        List<ProductTypeDto> productTypes = this.adminServiceFacade.retrieveProductTypesApplicableToProductMix();
        return productMixAssembler.createFormBean(productTypes);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String canceBtn,
                                          @RequestParam(value = PREVIEW_PARAM, required = false) String previewBtn,
                                          @ModelAttribute("formBean") ProductMixFormBean formBean,
                                          BindingResult result,
                                          SessionStatus status) {

        ModelAndView mav = new ModelAndView("defineProductMix");
        ProductMixFormValidator formValidator = new ProductMixFormValidator();

        if (StringUtils.isNotBlank(canceBtn)) {
            mav = new ModelAndView(REDIRECT_TO_ADMIN_SCREEN);
            status.setComplete();
        }

        if (StringUtils.isBlank(formBean.getProductTypeId())) {

            List<ProductTypeDto> productTypes = this.adminServiceFacade.retrieveProductTypesApplicableToProductMix();
            mav.addObject("formBean", productMixAssembler.createFormBean(productTypes));
            validateForPreview(formValidator, formBean, result, previewBtn);

        } else if (StringUtils.isNotBlank(formBean.getProductTypeId()) && StringUtils.isBlank(formBean.getProductId())) {

            populateProductNameOptions(formBean);
            validateForPreview(formValidator, formBean, result, previewBtn);

        } else if (StringUtils.isNotBlank(formBean.getProductTypeId()) &&
                StringUtils.isNotBlank(formBean.getProductId()) && (formBean.getAllowed() == null && formBean.getNotAllowed() == null)) {

            populateAllowedNotAllowedOptions(formBean);
            validateForPreview(formValidator, formBean, result, previewBtn);

        } else {
            checkAndFixFormStringArrys(formBean);
            validateForPreview(formValidator, formBean, result, previewBtn);
            ProductMixPreviewDto preview = this.productMixAssembler.createProductMixPreview(formBean);

            mav = new ModelAndView("previewProductMix");
            mav.addObject("ref", preview);
            mav.addObject("formView", "defineProductMix");
        }

        return mav;
    }

    private void populateProductNameOptions(ProductMixFormBean formBean) {
        Map<String, String> productNameOptions = new LinkedHashMap<String, String>();

        List<PrdOfferingDto> applicableProducts = this.adminServiceFacade.retrieveLoanProductsNotMixed();
        for (PrdOfferingDto product : applicableProducts) {
            productNameOptions.put(product.getPrdOfferingId().toString(), product.getPrdOfferingName());
        }

        formBean.setProductNameOptions(productNameOptions);
    }

    private void populateAllowedNotAllowedOptions(ProductMixFormBean formBean) {
        Map<String, String> allowedProductOptions = new LinkedHashMap<String, String>();
        Map<String, String> notAllowedProductOptions = new LinkedHashMap<String, String>();

        List<PrdOfferingDto> allowedProductsInMix = this.adminServiceFacade.retrieveAllowedProductsForMix(Integer.parseInt(formBean.getProductTypeId()), Integer.parseInt(formBean.getProductId()));
        for (PrdOfferingDto allowedProduct : allowedProductsInMix) {
            allowedProductOptions.put(allowedProduct.getPrdOfferingId().toString(), allowedProduct.getPrdOfferingName());
        }

        List<PrdOfferingDto> notAllowedProductsInMix = this.adminServiceFacade.retrieveNotAllowedProductsForMix(Integer.parseInt(formBean.getProductTypeId()), Integer.parseInt(formBean.getProductId()));
        for (PrdOfferingDto notAllowedProduct : notAllowedProductsInMix) {
            notAllowedProductOptions.put(notAllowedProduct.getPrdOfferingId().toString(), notAllowedProduct.getPrdOfferingName());
        }


        formBean.setAllowedProductOptions(allowedProductOptions);
        formBean.setNotAllowedProductOptions(notAllowedProductOptions);
    }

    private void validateForPreview(ProductMixFormValidator formValidator, ProductMixFormBean formBean, BindingResult result, String previewBtn) {
        if (StringUtils.isNotBlank(previewBtn)) {
            formValidator.validate(formBean, result);
        }
    }

    private void checkAndFixFormStringArrys(ProductMixFormBean formBean) {
        if (null == formBean.getAllowed()) {
            formBean.setAllowed(new String[1]);
        }

        if (null == formBean.getNotAllowed()) {
            formBean.setNotAllowed(new String[1]);
        }
    }

    public void setProductMixAssembler(ProductMixAssembler productMixAssembler) {
        this.productMixAssembler = productMixAssembler;
    }
}