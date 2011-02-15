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

import java.util.List;

import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.dto.screen.ProductCategoryDetailsDto;
import org.mifos.dto.screen.ProductCategoryTypeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/viewProductCategoryDetails")
public class ViewProductCategoryDetailsController {

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    protected ViewProductCategoryDetailsController() {
        // empty constructor for spring wiring
    }

    public ViewProductCategoryDetailsController(final AdminServiceFacade adminServiceFacade){
        this.adminServiceFacade=adminServiceFacade;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView handleRequestInternal(@RequestParam(value = "globalPrdCategoryNum", required = true) String globalPrdCategoryNum) {

        ModelAndView modelAndView = new ModelAndView("viewProductCategoryDetails");
        List<ProductCategoryTypeDto> typeDto = adminServiceFacade.retrieveProductCategoryTypes();
        ProductCategoryDetailsDto detailsDto =  adminServiceFacade.retrieveProductCateogry(globalPrdCategoryNum);
        modelAndView.addObject("typeDto", typeDto);
        modelAndView.addObject("detailsDto", detailsDto);
        modelAndView.addObject("breadcrumbs", new AdminBreadcrumbBuilder().withLink("viewProductCategoryDetails", "viewProductCategoryDetails.ftl").build());
        modelAndView.addObject("globalPrdCategoryNum", globalPrdCategoryNum);
        return modelAndView;
    }
}