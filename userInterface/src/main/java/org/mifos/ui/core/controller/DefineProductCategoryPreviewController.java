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
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.dto.domain.CreateOrUpdateProductCategory;
import org.mifos.dto.screen.ProductCategoryTypeDto;
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
@RequestMapping("/newProductCategoryPreview")
@SessionAttributes("formBean")
public class DefineProductCategoryPreviewController {

    private static final String REDIRECT_TO_ADMIN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String EDIT_PARAM = "EDIT";

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    public DefineProductCategoryPreviewController(final AdminServiceFacade adminServiceFacade) {
        this.adminServiceFacade = adminServiceFacade;
    }

    protected DefineProductCategoryPreviewController() {
        // spring auto wiring
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showPopulatedForm(ProductCategoryFormBean formBean) {
        ModelAndView modelAndView = new ModelAndView("defineNewCategory");
        modelAndView.addObject("formbean", formBean);
        modelAndView.addObject("typeList", this.getProductCategoryTypes());
        return modelAndView;
    }

    private Map<String, String> getProductCategoryTypes() {
        String pType = "";
        Map<String, String> categoryTypes = new LinkedHashMap<String, String>();
        for (ProductCategoryTypeDto productCategoryTypeDto : this.adminServiceFacade.retrieveProductCategoryTypes()) {
            if (!pType.equals(productCategoryTypeDto.getProductName())) {
                categoryTypes.put(productCategoryTypeDto.getProductTypeID().toString(), productCategoryTypeDto
                        .getProductName());
            }
            pType = productCategoryTypeDto.getProductName();
        }
        return categoryTypes;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
            @RequestParam(value = EDIT_PARAM, required = false) String edit,
            @ModelAttribute("formBean") ProductCategoryFormBean bean,
            BindingResult result) {
        ModelAndView modelAndView = new ModelAndView("defineNewCategory");
        if (StringUtils.isNotBlank(cancel)) {
            modelAndView.setViewName(REDIRECT_TO_ADMIN);
        } else if (StringUtils.isNotBlank(edit)) {
            modelAndView.setViewName("defineNewCategory");
            modelAndView.addObject("formBean", bean);
            modelAndView.addObject("typeList", this.getProductCategoryTypes());
        } else if (result.hasErrors()) {
            modelAndView.setViewName("newProductCategoryPreview");
            modelAndView.addObject("formBean", bean);
        } else {
            CreateOrUpdateProductCategory productCategory = new CreateOrUpdateProductCategory(Short.parseShort(bean.getProductTypeId()), bean.getProductCategoryName(),
                    bean.getProductCategoryDesc(), Short.parseShort(bean.getProductCategoryStatusId()), bean.getGlobalPrdCategoryNum());

            try {
                this.adminServiceFacade.createProductCategory(productCategory);
                modelAndView.setViewName(REDIRECT_TO_ADMIN);
            } catch (BusinessRuleException e) {
                ObjectError error = new ObjectError("formBean", new String[] { e.getMessageKey() }, new Object[] {}, "default: ");
                result.addError(error);
                modelAndView.setViewName("newProductCategoryPreview");
                modelAndView.addObject("formBean", bean);
            }
        }
        return modelAndView;
    }
}