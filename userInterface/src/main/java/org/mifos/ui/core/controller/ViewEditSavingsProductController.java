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

import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.config.servicefacade.ConfigurationServiceFacade;
import org.mifos.config.servicefacade.dto.AccountingConfigurationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping("/viewEditSavingsProduct")
@Controller
public class ViewEditSavingsProductController {

    @Autowired
    private AdminServiceFacade adminServiceFacade;
    
    @Autowired
    private ConfigurationServiceFacade configurationServiceFacade;
    

    protected ViewEditSavingsProductController() {
        // default contructor for spring autowiring
    }

    public ViewEditSavingsProductController(final AdminServiceFacade adminServiceFacade) {
        this.adminServiceFacade = adminServiceFacade;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showSavingProductDetails(@RequestParam("productId") Integer productId) {
    	ModelAndView modelAndView = new ModelAndView("viewEditSavingsProduct");
    	
    	AccountingConfigurationDto configurationDto = this.configurationServiceFacade.getAccountingConfiguration();
    	modelAndView.addObject("GLCodeMode", configurationDto.getGlCodeMode());
    	modelAndView.addObject("savingsProductDetails", adminServiceFacade.retrieveSavingsProductDetails(productId));
        return modelAndView;
    }
}