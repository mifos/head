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

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.dto.screen.ProductCategoryDisplayDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/viewProductCategories")
public class ViewProductCategoriesController {

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    protected ViewProductCategoriesController() {
        // empty constructor for spring wiring
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="NP_UNWRITTEN_FIELD", justification="request is not null")
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView handleRequestInternal(HttpServletRequest request) {

        ModelAndView modelAndView = new ModelAndView("viewProductCategories");
        ProductCategoryDisplayDto dto = adminServiceFacade.retrieveAllProductCategories();
        modelAndView.addObject("dto", dto);
        modelAndView.addObject("breadcrumbs", new AdminBreadcrumbBuilder().withLink("admin.viewproductcategories", "viewProductCategories.ftl").build());

        return modelAndView;
    }

    public String getPageToDisplay(HttpServletRequest request) {
        return request.getRequestURI().replace("mifos/", "").replace("/", "").replace(".ftl", "");
    }
}