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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.dto.domain.ProductTypeDto;
import org.mifos.dto.screen.ProductMixDetailsDto;
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

@Controller
@RequestMapping("/editProductMix")
@SessionAttributes("formBean")
public class EditProductMixController {

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";

    @Autowired
    private AdminServiceFacade adminServiceFacade;
    private ProductMixAssembler productMixAssembler = new ProductMixAssembler();

    protected EditProductMixController() {
        // default contructor for spring autowiring
    }

    public EditProductMixController(AdminServiceFacade adminServiceFacade) {
        this.adminServiceFacade = adminServiceFacade;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("formBean")
    public ProductMixFormBean showEditForm(@RequestParam(value = "productId", required = true) Integer productId) {

        List<ProductTypeDto> productTypes = this.adminServiceFacade.retrieveProductTypesApplicableToProductMix();
        ProductMixFormBean formBean = productMixAssembler.createFormBean(productTypes);
        formBean.setProductId(productId.toString());
        formBean.setProductTypeId("1");

        ProductMixDetailsDto productMixDetails = adminServiceFacade.retrieveProductMixDetails(productId.shortValue(), formBean.getProductTypeId());

        Map<String, String> productNameOptions = new LinkedHashMap<String, String>();
        productNameOptions.put(productMixDetails.getPrdOfferingId().toString(), productMixDetails.getPrdOfferingName());
        formBean.setProductNameOptions(productNameOptions);

        populateAllowedNotAllowedOptions(formBean, productMixDetails);

        return formBean;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
                                    @ModelAttribute("formBean") ProductMixFormBean formBean,
                                    BindingResult result,
                                    SessionStatus status) {

        ModelAndView mav = new ModelAndView(REDIRECT_TO_ADMIN_SCREEN);

        if (StringUtils.isNotBlank(cancel)) {
            status.setComplete();
        } else if (result.hasErrors()) {
            mav = new ModelAndView("editProductMix");
        } else {
            ProductMixPreviewDto preview = this.productMixAssembler.createProductMixPreview(formBean);

            mav = new ModelAndView("previewProductMix");
            mav.addObject("ref", preview);
            mav.addObject("formView", "editProductMix");
        }

        return mav;
    }

    private void populateAllowedNotAllowedOptions(ProductMixFormBean formBean, ProductMixDetailsDto productMixDetails) {
        Map<String, String> allowedProductOptions = new LinkedHashMap<String, String>();
        Map<String, String> notAllowedProductOptions = new LinkedHashMap<String, String>();

        List<PrdOfferingDto> allowedProductsInMix = productMixDetails.getAllowedPrdOfferingNames();
        for (PrdOfferingDto allowedProduct : allowedProductsInMix) {
            allowedProductOptions.put(allowedProduct.getPrdOfferingId().toString(), allowedProduct.getPrdOfferingName());
        }

        List<PrdOfferingDto> notAllowedProductsInMix = productMixDetails.getNotAllowedPrdOfferingNames();
        for (PrdOfferingDto notAllowedProduct : notAllowedProductsInMix) {
            notAllowedProductOptions.put(notAllowedProduct.getPrdOfferingId().toString(), notAllowedProduct.getPrdOfferingName());
        }

        formBean.setAllowedProductOptions(allowedProductOptions);
        formBean.setNotAllowedProductOptions(notAllowedProductOptions);
    }

    public void setProductMixAssembler(ProductMixAssembler productMixAssembler) {
        this.productMixAssembler = productMixAssembler;
    }
}