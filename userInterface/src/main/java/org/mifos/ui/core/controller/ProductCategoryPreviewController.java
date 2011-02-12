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
import org.mifos.dto.domain.CreateOrUpdateProductCategory;
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
@RequestMapping("/categoryPreview")
@SessionAttributes("formBean")
public class ProductCategoryPreviewController {
    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String EDIT_PARAM = "EDIT";

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    public ProductCategoryPreviewController(final AdminServiceFacade adminServiceFacade) {
        this.adminServiceFacade = adminServiceFacade;
    }

    protected ProductCategoryPreviewController() {
        // empty constructor for spring autowiring
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showPopulatedForm() {
        return new ModelAndView(REDIRECT_TO_ADMIN_SCREEN);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = EDIT_PARAM, required = false) String edit,
            @RequestParam(value = CANCEL_PARAM, required = false) String cancel,
            @ModelAttribute("formBean") ProductCategoryFormBean formBean,
            BindingResult result) {

        String viewName = REDIRECT_TO_ADMIN_SCREEN;
        ModelAndView modelAndView = new ModelAndView();

        if (StringUtils.isNotBlank(edit)) {
            viewName = "editCategoryInformation";
            modelAndView.setViewName(viewName);
            modelAndView.addObject("formBean", formBean);
            modelAndView.addObject("breadcrumbs", new AdminBreadcrumbBuilder().withLink("admin.viewproductcategories",
                    "viewProductCategories.ftl").withLink(formBean.getProductCategoryName(), "").build());
        } else if (StringUtils.isNotBlank(cancel)) {
            viewName = REDIRECT_TO_ADMIN_SCREEN;
            modelAndView.setViewName(viewName);
        } else if (result.hasErrors()) {
            viewName = "categoryPreview";
            modelAndView.setViewName(viewName);
            modelAndView.addObject("formBean", formBean);
            modelAndView.addObject("breadcrumbs", new AdminBreadcrumbBuilder().withLink("admin.viewproductcategories", "viewProductCategories.ftl").withLink(formBean.getProductCategoryName(),"").build());
        } else {
            Integer productStatusId = Integer.parseInt(formBean.getProductCategoryStatusId());
            Integer productTypeId = Integer.parseInt(formBean.getProductTypeId());

            CreateOrUpdateProductCategory productCategory = new CreateOrUpdateProductCategory(productTypeId
                    .shortValue(), formBean.getProductCategoryName(), formBean.getProductCategoryDesc(),
                    productStatusId.shortValue(), formBean.getGlobalPrdCategoryNum());

            try {
                this.adminServiceFacade.updateProductCategory(productCategory);
                modelAndView.setViewName(REDIRECT_TO_ADMIN_SCREEN);
            } catch (BusinessRuleException e) {
                ObjectError error = new ObjectError("formBean", new String[] {e.getMessageKey()}, new Object[] {}, "default: ");
                result.addError(error);
                modelAndView.setViewName("categoryPreview");
                modelAndView.addObject("formBean", formBean);
                modelAndView.addObject("breadcrumbs", new AdminBreadcrumbBuilder().withLink("admin.viewproductcategories", "viewProductCategories.ftl").withLink(formBean.getProductCategoryName(),"").build());
            }
        }
        return modelAndView;
    }
}
