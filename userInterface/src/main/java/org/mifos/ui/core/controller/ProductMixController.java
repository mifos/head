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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/viewProductMix")
public class ProductMixController {

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    protected ProductMixController(){
        //spring autowiring
    }

    public ProductMixController(final AdminServiceFacade adminServiceFacade){
        this.adminServiceFacade=adminServiceFacade;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink("viewProductMix", "viewProductMix.ftl").build();
    }

    @ModelAttribute("mixList")
    public Map<String, Object> getList() {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("mix", adminServiceFacade.retrieveAllProductMix().getProductMixList());
        return model;
    }

}