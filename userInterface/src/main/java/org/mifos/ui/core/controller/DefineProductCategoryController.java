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

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.dto.screen.ProductCategoryDetailsDto;
import org.mifos.dto.screen.ProductCategoryTypeDto;
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
@RequestMapping("/editProductCategory")
@SessionAttributes("formBean")
public class DefineProductCategoryController {

    private static final String REDIRECT_TO_VIEW_PRODUCT_CATEGORY = "redirect:/editProductCategory.ftl";
    private static final String TO_PRODUCT_CATEGORY_PREVIEW = "productCategoryPreview";
    private static final String CANCEL_PARAM = "CANCEL";

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    protected DefineProductCategoryController() {
        // spring autowiring
    }

    public DefineProductCategoryController(final AdminServiceFacade adminServiceFacade) {
        this.adminServiceFacade = adminServiceFacade;
    }

    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink("editProductCategory", "editProductCategory.ftl").build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView handleRequestInternal(HttpServletRequest request) {
        boolean editMode = Boolean.parseBoolean(request.getParameter("editMode"));
        ModelAndView modelAndView = new ModelAndView("editFunds");
        List<ProductCategoryTypeDto> typeList = adminServiceFacade.retrieveProductCategoryTypes();
        modelAndView.addObject("typeList", typeList);
        ProductCategoryFormBean formBean= new ProductCategoryFormBean();
        if(editMode) {
            String globalProductCategoryNumber = request.getParameter("globalProductCategoryNumber");
            ProductCategoryDetailsDto dto = adminServiceFacade.retrieveProductCateogry(globalProductCategoryNumber);
            formBean.setGlobalPrdCategoryNum(globalProductCategoryNumber);
            formBean.setProductCategoryDesc(dto.getProductCategoryDesc());
            formBean.setProductCategoryName(dto.getProductCategoryName());
            formBean.setProductCategoryStatusId(dto.getProductCategoryStatusId());
            formBean.setProductType(dto.getProductTypeName());
            formBean.setProductTypeId(dto.getProductTypeId());
        }
        modelAndView.addObject("formBean", formBean);
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
            @ModelAttribute("formBean") ProductCategoryFormBean formBean, BindingResult result, SessionStatus status) {
        ModelAndView modelAndView = new ModelAndView(REDIRECT_TO_VIEW_PRODUCT_CATEGORY);
        if (StringUtils.isNotBlank(cancel)) {
            modelAndView.setViewName(REDIRECT_TO_VIEW_PRODUCT_CATEGORY);
            status.setComplete();
        } else if (result.hasErrors()) {
            modelAndView.setViewName("editProductCategory");
        } else {
            modelAndView.setViewName(TO_PRODUCT_CATEGORY_PREVIEW);
            modelAndView.addObject("formBean", formBean);
        }
        return modelAndView;
    }
}