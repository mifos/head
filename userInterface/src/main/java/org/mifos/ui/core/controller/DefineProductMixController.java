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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.dto.domain.ProductTypeDto;
import org.mifos.dto.screen.ProductDisplayDto;
import org.mifos.dto.screen.ProductDto;
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
@RequestMapping("/defineProductMix")
@SessionAttributes("formBean")
public class DefineProductMixController {

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    protected DefineProductMixController() {
        // default contructor for spring autowiring
    }

    public DefineProductMixController(AdminServiceFacade adminServiceFacade) {
        this.adminServiceFacade = adminServiceFacade;
    }

    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink("viewHolidays", "viewHolidays.ftl").withLink("addHoliday", "defineNewHoliday.ftl").build();
    }

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("formBean")
    public ProductMixFormBean showPopulatedForm() {

        ProductMixFormBean formBean = new ProductMixFormBean();

        Map<String, String> productTypeOptions = new LinkedHashMap<String, String>();

        List<ProductTypeDto> productTypes = this.adminServiceFacade.retrieveProductTypesApplicableToProductMix();
        for (ProductTypeDto productTypeDto : productTypes) {
            productTypeOptions.put(productTypeDto.getProductTypeId().toString(), productTypeDto.getProductTypeKey());
        }

        formBean.setProductTypeOptions(productTypeOptions);

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
            mav = new ModelAndView("defineProductMix");
        } else {

            if (StringUtils.isNotBlank(formBean.getProductTypeId()) && StringUtils.isBlank(formBean.getProductId())) {

                Map<String, String> productNameOptions = new LinkedHashMap<String, String>();

                List<ProductDisplayDto> applicableProducts = this.adminServiceFacade.retrieveProductsByProductTypeId(Integer.parseInt(formBean.getProductTypeId()));
                for (ProductDisplayDto product : applicableProducts) {
                    productNameOptions.put(product.getPrdOfferingId().toString(), product.getPrdOfferingName());
                }

                formBean.setProductNameOptions(productNameOptions);

                mav = new ModelAndView("defineProductMix");
            } else if (StringUtils.isNotBlank(formBean.getProductTypeId()) &&
                    StringUtils.isNotBlank(formBean.getProductId()) &&
                    formBean.getAllowed() == null) {

                Map<String, String> allowedProductOptions = new LinkedHashMap<String, String>();
                allowedProductOptions.put("1", "allowed");
                allowedProductOptions.put("2", "allowed2");

                Map<String, String> notAllowedProductOptions = new LinkedHashMap<String, String>();
                notAllowedProductOptions.put("1", "notAllowed1");
                notAllowedProductOptions.put("2", "notAllowed2");

                formBean.setAllowedProductOptions(allowedProductOptions);
                formBean.setNotAllowedProductOptions(notAllowedProductOptions);

                mav = new ModelAndView("defineProductMix");
            }

        }

        return mav;
    }
}